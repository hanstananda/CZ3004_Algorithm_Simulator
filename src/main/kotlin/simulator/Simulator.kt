package simulator

import constants.MapConstants
import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import utils.map.debugMap
import utils.map.loadMapFromDisk
import java.awt.*
import java.awt.event.*
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.event.ChangeEvent
import kotlin.collections.HashMap


object Simulator: ActionListener {

    lateinit var f: JFrame
    lateinit var m: JPanel
    lateinit var b: JPanel
    lateinit var buttonLayout: GridBagLayout
    lateinit var gbc: GridBagConstraints
    val logger = KotlinLogging.logger {}

    lateinit var sim: SimulatorMap

    private var waypoint_chosen = Pair(-1, -1) // In form of (x,y)
    private var time_chosen = -1
    private var percentage_chosen = 100
    private var speed_chosen = 1

    @JvmStatic
    fun main(args: Array<String>) {
        val map = MazeMap()
        map.initExploredAreas()
        val bot = Robot(1, 1)
        sim = SimulatorMap(map, bot)
        displayMainFrame()
    }

    fun updateSimulatorMap() {
        if(this::m.isInitialized) {
            m.repaint()
            logger.debug { "Map repainted!" }
        }
    }

    fun displayMainFrame() {
        //initialise main frame
        f = JFrame("MDP Group 28 Simulator")
        f.size = Dimension(950, 700)
        f.isResizable = false
        f.isFocusable = true
        f.focusTraversalKeysEnabled = false;

        //center main frame
        val dim = Toolkit.getDefaultToolkit().screenSize
        f.setLocation(dim.width / 2 - f.size.width / 2, dim.height / 2 - f.size.height / 2)

        //create CardLayout for storing different maps and robot
        m = JPanel(CardLayout())

        //create JPanel for buttons
        b = JPanel()

        //add m & b to the main frame's content pane
        val contentPane = f.contentPane
        contentPane.add(m, BorderLayout.CENTER)
        contentPane.add(b, BorderLayout.EAST)
        initMain()
        initButtons()

        // movement buttons to move robot
        initMovementButtons()

        f.isVisible = true
        f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    }

    private fun initMain() {
        m.add(sim, "MAP")
        val cl = m.layout as CardLayout
        cl.show(m, "MAP")
    }

    private fun initButtons() {
        buttonLayout = GridBagLayout()
        gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.BOTH
        gbc.weightx = 0.5
        gbc.weighty = 0.5
        b.layout = buttonLayout
        addButtons()
    }

    private fun addIndivButton(component: Component, container: Container, layout: GridBagLayout, gbc: GridBagConstraints, gridx: Int, gridy: Int, gridwidth: Int, gridheight: Int, inset: Insets) {
        gbc.gridx = gridx
        gbc.gridy = gridy
        gbc.gridwidth = gridwidth
        gbc.gridheight = gridheight
        gbc.insets = inset
        layout.setConstraints(component, gbc)
        container.add(component)
    }

    private fun addButtons() {
        // Load Map Button
        val loadMapPanel = JPanel()
        val loadMapBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Load Map ")
        loadMapBorder.titleJustification = TitledBorder.CENTER
        loadMapPanel.border = loadMapBorder
        val arr: Array<String?> = getMapFileNames()
        val loadMapButton = JComboBox<String>(arr)
        loadMapButton.isFocusable = false
        loadMapButton.actionCommand = "Load Map"
        loadMapButton.addActionListener(this)
        loadMapPanel.add(loadMapButton)
        addIndivButton(loadMapPanel,b,buttonLayout,gbc,0,0,2,1,Insets(30,30,10,30))

        // Show True Map Button
        val showTrueMapButton = JButton("Show True Map")
        showTrueMapButton.isFocusable = false
        showTrueMapButton.actionCommand = "Show True Map"
        showTrueMapButton.addActionListener(this)
        addIndivButton(showTrueMapButton,b,buttonLayout,gbc,0,1,1,1, Insets(10,30,10,0))

        // Show Explored Map Button
        val showExploredMapButton = JButton("Show Explored Map")
        showExploredMapButton.isFocusable = false
        showExploredMapButton.actionCommand = "Show Explored Map"
        showExploredMapButton.addActionListener(this)
        addIndivButton(showExploredMapButton,b,buttonLayout,gbc,1,1,1,1,Insets(10,10,10,30))

        // Exploration Button
        val exploreButton = JButton("Exploration")
        exploreButton.isFocusable = false
        exploreButton.actionCommand = "Exploration"
        exploreButton.addActionListener(this)
        addIndivButton(exploreButton,b,buttonLayout,gbc,0,2,2,1,Insets(10,30,10,30))

        // Fastest Path Button
        val fastestPathButton = JButton("Fastest Path")
        fastestPathButton.isFocusable = false
        fastestPathButton.actionCommand = "Fastest Path"
        fastestPathButton.addActionListener(this)
        addIndivButton(fastestPathButton,b,buttonLayout,gbc,0,3,2,1,Insets(10,30,10,30))

        // Reset Robot Button
        val resetButton = JButton("Reset Robot")
        resetButton.isFocusable = false
        resetButton.actionCommand = "Reset Robot"
        resetButton.addActionListener(this)
        addIndivButton(resetButton,b,buttonLayout,gbc,0,4,2,1,Insets(10,30,10,30))

        // Set Speed Slider
        val speedPanel = JPanel()
        val speedBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Robot Speed (step/s) ")
        speedBorder.titleJustification = TitledBorder.CENTER
        speedPanel.border = speedBorder
        val speedSlider = JSlider(JSlider.HORIZONTAL,RobotConstants.MIN_SPEED,RobotConstants.MAX_SPEED,RobotConstants.DEF_SPEED)
        speedSlider.majorTickSpacing = 1
        speedSlider.minorTickSpacing = 0
        speedSlider.paintTicks = true
        speedSlider.paintLabels = true
        speedSlider.autoscrolls = true
        speedSlider.paintTrack = true
        speedSlider.addChangeListener(
                fun(e: ChangeEvent) {
                    val s = e.source as JSlider
                    if (!s.valueIsAdjusting) {
                        val sp = s.value
                        speed_chosen = sp
                        sim.bot.speed = speed_chosen
                        logger.debug{"Robot speed set as $speed_chosen step/s"}
                    }
                })
        speedPanel.add(speedSlider)
        addIndivButton(speedPanel,b,buttonLayout,gbc,0,5,2,1,Insets(10,30,10,30))

        // Set Time Limit Button
        val timePanel = JPanel()
        val timeBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Time limit (s) ")
        timeBorder.titleJustification = TitledBorder.CENTER
        timePanel.border = timeBorder
        val timeArr: Array<String?> = createSeqArray(0, 121)
        val timeButton = JComboBox<String>(timeArr)
        timeButton.isFocusable = false
        timeButton.actionCommand = "Set time limit"
        timeButton.addActionListener(this)
        timePanel.add(timeButton)
        addIndivButton(timePanel,b,buttonLayout,gbc,0,6,1,1,Insets(10,30,10,0))

        // Set Coverage Limit Button
        val coveragePanel = JPanel()
        val coverageBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Coverage limit (%)  ")
        coverageBorder.titleJustification = TitledBorder.CENTER
        coveragePanel.border = coverageBorder
        val coverageArr: Array<String?> = createSeqArray(0, 101)
        val coverageButton = JComboBox<String>(coverageArr)
        coverageButton.isFocusable = false
        coverageButton.actionCommand = "Set % limit"
        coverageButton.addActionListener(this)
        coveragePanel.add(coverageButton)
        addIndivButton(coveragePanel,b,buttonLayout,gbc,1,6,1,1,Insets(10,10,10,30))

        // Set Waypoint Button
        val waypointPanel = JPanel()
        val waypointBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Waypoint ")
        waypointBorder.titleJustification = TitledBorder.CENTER
        waypointPanel.border = waypointBorder
        val waypointArrRow: Array<String?> = createSeqArray(1, MapConstants.DEFAULT_ROW_SIZE)
        val waypointArrCol: Array<String?> = createSeqArray(1, MapConstants.DEFAULT_COL_SIZE)
        val waypointRowButton = JComboBox<String>(waypointArrRow)
        val waypointColButton = JComboBox<String>(waypointArrCol)
        waypointRowButton.isFocusable = false
        waypointColButton.isFocusable = false
        waypointRowButton.actionCommand = "Set Waypoint Row"
        waypointColButton.actionCommand = "Set Waypoint Col"
        waypointRowButton.addActionListener(this)
        waypointColButton.addActionListener(this)
        val rowLabel = JLabel("Row: ")
        val colLabel = JLabel("Col: ")
        waypointPanel.add(rowLabel)
        waypointPanel.add(waypointRowButton)
        waypointPanel.add(colLabel)
        waypointPanel.add(waypointColButton)
        addIndivButton(waypointPanel,b,buttonLayout,gbc,0,7,2,1,Insets(10,30,30,30))

    }

    private fun initMovementButtons() {
        f.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_RIGHT) {
                    logger.debug{ "right button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.RIGHT)
                    sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.trueMap)
                    m.repaint()
                } else if (e.keyCode == KeyEvent.VK_LEFT) {
                    logger.debug{ "left button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.LEFT)
                    sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.trueMap)
                    m.repaint()
                } else if (e.keyCode == KeyEvent.VK_UP) {
                    logger.debug{ "up button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.FORWARD)
                    sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.trueMap)
                    m.repaint()
                }
            }

            override fun keyTyped(e: KeyEvent) {
            }

            override fun keyReleased(e: KeyEvent) {
            }
        })
    }

    // reads all txt files from the resources directory and returns the array of file names
    private fun getMapFileNames(): Array<String?> {
        val folder = File("./src/main/resources/mazemaps")
        val filePath = HashMap<String, String>()
        for (file in folder.listFiles()) {
            if (file.name.endsWith(".txt")) {
                filePath[file.name.substring(0, file.name.lastIndexOf(".txt"))] = file.absolutePath
            }
        }
        val fileName = arrayOfNulls<String>(filePath.size)
        var i = filePath.size - 1
        for (key in filePath.keys) {
            fileName[i] = key
            i--
        }
        Arrays.sort(fileName)
        return fileName
    }

    private fun createSeqArray(min: Int, max: Int): Array<String?> {
        val arr = arrayOfNulls<String>(max - min + 1)
        var count = 1
        for (i in min until max) {
            arr[count] = i.toString()
            count++
        }
        return arr
    }

    override fun actionPerformed(e: ActionEvent?) {
        val action = e!!.actionCommand
        if (action!!.contentEquals("Load Map")) {
            val arenaMap = e.source as JComboBox<*>
            val selectedFile = arenaMap.selectedItem ?: return
            val selectedFileString = arenaMap.selectedItem as String
            try {
                val newMap = MazeMap()
                loadMapFromDisk(newMap,selectedFileString)
                newMap.setAllExplored()
                SimulatorServer.trueMap = newMap
                SimulatorServer.resetExploredMapAndRobot()
                sim.map= SimulatorServer.trueMap
                val cl = m.layout as CardLayout
                cl.show(m, "map")
                updateSimulatorMap()

            } catch (f: FileNotFoundException) {
                logger.debug{"File not found"}
            } catch (IO: IOException) {
                logger.debug{"IOException when reading$selectedFile"}
            } catch (eX: Exception) {
                logger.debug{eX.message}
            }
        }
        if (action.contentEquals("Show True Map")){
            sim.map = SimulatorServer.trueMap
            updateSimulatorMap()
        }
        if (action.contentEquals("Show Explored Map")){
            sim.map = SimulatorServer.exploredMap
            updateSimulatorMap()
        }
        if (action.contentEquals("Exploration")){
            runBlocking {
                SimulatorServer.startExploration()
            }
        }
        if (action.contentEquals("Fastest Path")){
            runBlocking {
                SimulatorServer.startFastestPathWithWaypoint(waypoint_chosen.first, waypoint_chosen.second)
            }
        }
        if (action.contentEquals("Reset Robot")){
            SimulatorServer.resetExploredMapAndRobot()
            sim.map = SimulatorServer.trueMap
            updateSimulatorMap()
        }
        if (action.contentEquals("Set time limit")){
            val secs = e.source as JComboBox<*>
            val secsChosen = secs.selectedItem as String
            time_chosen = secsChosen.toInt()
        }
        if (action.contentEquals("Set % limit")){
            val percentage = e.source as JComboBox<*>
            val percentageChosen = percentage.selectedItem as String
            percentage_chosen = percentageChosen.toInt()
        }
        if (action.contentEquals("Set speed")){
            val s = e.source as JComboBox<*>
            val sp = s.selectedItem as String
            speed_chosen = sp.toInt()
            sim.bot.speed = speed_chosen
        }
        if (action.contentEquals("Set Waypoint Row")){
            val waypointRow = e.source as JComboBox<*>
            val selectedWaypointRow = waypointRow.selectedItem as String
            waypoint_chosen = Pair(waypoint_chosen.first,selectedWaypointRow.toInt())
        }
        if (action.contentEquals("Set Waypoint Col")){
            val waypointCol = e.source as JComboBox<*>
            val selectedWaypointCol = waypointCol.selectedItem as String
            waypoint_chosen = Pair(selectedWaypointCol.toInt(), waypoint_chosen.second)
        }
    }

}

