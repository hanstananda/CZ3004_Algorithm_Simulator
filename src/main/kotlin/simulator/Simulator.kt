package simulator

import constants.MapConstants
import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
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
import kotlin.system.exitProcess


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
    private var with_time = false
    private var with_coverage = false
    private var speed_chosen = 1
    private var checked = false
    private var displayed = false

    var real_run = false

    @JvmStatic
    fun main(args: Array<String>) {
        val map = MazeMap()
        map.initExploredAreas()
        val bot = Robot(1, 1)
        sim = SimulatorMap(map, bot)
        displayFrame()
    }

    fun displayFrame() {
        while (!checked) {
            checked = checkRealRun()
        }
        if(!displayed) {
            if (real_run) {
                displayMainRealRunFrame()
            }
            else {
                displayMainSimulatorFrame()
            }
            displayed = true
        }
    }

    private fun checkRealRun(): Boolean {
        var result = JOptionPane.showConfirmDialog(null, "Is this the real run?", "MDP Group 28", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
        if (result == JOptionPane.YES_OPTION) {
            real_run = true
            return true
        }
        if (result == JOptionPane.NO_OPTION) {
            real_run = false
            return true
        }
        if (result == JOptionPane.CLOSED_OPTION){
            exitProcess(1)
        }
        return false
    }

    fun updateSimulatorMap() {
        if(this::m.isInitialized) {
            m.repaint()
            logger.debug { "Map repainted!" }
        }
    }

    fun displayMainSimulatorFrame() {
        //initialise main frame
        f = JFrame("MDP Group 28 Simulator")
        f.size = Dimension(1000, 700)
        f.isResizable = false
        f.isFocusable = true
        f.focusTraversalKeysEnabled = false;

        //center main frame
        val dim = Toolkit.getDefaultToolkit().screenSize
        f.setLocation(dim.width / 2 - f.size.width / 2, dim.height / 2 - f.size.height / 2)

        //create CardLayout for storing different maps and robot
        m = JPanel(CardLayout())
        m.background = Color.WHITE

        //create JPanel for buttons
        b = JPanel()
//        b.background = Color.WHITE

        //add m & b to the main frame's content pane
        val contentPane = f.contentPane
        contentPane.add(m, BorderLayout.CENTER)
        contentPane.add(b, BorderLayout.EAST)
        initMain()
        initButtons()

        //load blank map
        loadMap(MapConstants.DEFAULT_MAP)

        f.isVisible = true
        f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    }

    fun displayMainRealRunFrame() {
        //initialise main frame
        f = JFrame("MDP Group 28 Real Run")
        f.size = Dimension(700, 700)
        f.isResizable = false
        f.isFocusable = true
        f.focusTraversalKeysEnabled = false
        f.background = Color.WHITE

        //center main frame
        val dim = Toolkit.getDefaultToolkit().screenSize
        f.setLocation(dim.width / 2 - f.size.width / 2, dim.height / 2 - f.size.height / 2)

        //create CardLayout for storing different maps and robot
        m = JPanel(CardLayout())

        //add m to the main frame's content pane
        val contentPane = f.contentPane
        contentPane.add(m, BorderLayout.CENTER)
        initMain()

        //load realtime map from server
        sim.map = SimulatorServer.realTimeMap

        f.isVisible = true
        f.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    }

    private fun initMain() {
        sim.background = Color.BLACK
        m.add(sim, "MAP")
        val cl = m.layout as CardLayout
        cl.show(m, "MAP")
    }

    private fun initButtons() {
        // movement buttons to move robot
        initMovementButtons()

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
//        loadMapPanel.background = Color.WHITE
        val loadMapBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Load Map ")
        loadMapBorder.titleJustification = TitledBorder.CENTER
        loadMapPanel.border = loadMapBorder
        val arr: Array<String?> = getMapFileNames()
        val loadMapButton = JComboBox<String>(arr)
        loadMapButton.isFocusable = false
        loadMapButton.actionCommand = "Load Map"
        loadMapButton.addActionListener(this)
        loadMapPanel.add(loadMapButton)
        addIndivButton(loadMapPanel,b,buttonLayout,gbc,0,0,2,1,Insets(30,40,10,40))

        // Show Map Button
        val showMapPanel = JPanel()
//        showMapPanel.background = Color.WHITE
        val showMapBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Display Map ")
        showMapBorder.titleJustification = TitledBorder.CENTER
        showMapPanel.border = showMapBorder
        val types = arrayOf ("True Map", "Explored Map", "Real-Time Map")
        val showMapButton = JComboBox<String>(types)
        showMapButton.isFocusable = false
        showMapButton.actionCommand = "Display Map"
        showMapButton.addActionListener(this)
        showMapPanel.add(showMapButton)
        addIndivButton(showMapPanel,b,buttonLayout,gbc,0,1,2,1,Insets(30,40,10,40))

        // Exploration Button
        val explorationPanel = JPanel()
//        explorationPanel.background = Color.WHITE
        val explorationBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Exploration ")
        explorationBorder.titleJustification = TitledBorder.CENTER
        explorationPanel.border = explorationBorder

        val startButton = JButton("Start")
        startButton.isFocusable = false
        startButton.actionCommand = "Start"

        val stopButton = JButton("Stop")
        stopButton.isFocusable = false
        stopButton.actionCommand = "Stop"

        val withTime = JCheckBox("Time Limit ")
        val withCoverage = JCheckBox("Coverage Limit ")

        val explorationLayout = GridBagLayout()
        explorationPanel.layout = explorationLayout

        addIndivButton(startButton,explorationPanel,explorationLayout,gbc,0,0,1,1,Insets(5,15,5,0))
        addIndivButton(stopButton,explorationPanel,explorationLayout,gbc,1,0,1,1,Insets(5,25,5,15))
        addIndivButton(withTime,explorationPanel,explorationLayout,gbc,0,1,1,1,Insets(0,0,0,0))
        addIndivButton(withCoverage,explorationPanel,explorationLayout,gbc,1,1,1,1,Insets(0,5,0,0))

        startButton.addActionListener(this)
        stopButton.addActionListener(this)
        withTime.addItemListener { e ->
            with_time = e.stateChange == 1
        }
        withCoverage.addItemListener { e ->
            with_coverage = e.stateChange == 1
        }
        addIndivButton(explorationPanel,b,buttonLayout,gbc,0,2,2,1,Insets(30,40,10,40))

        // Fastest Path Button
        val fastestPathButton = JButton("Fastest Path")
        fastestPathButton.isFocusable = false
        fastestPathButton.actionCommand = "Fastest Path"
        fastestPathButton.addActionListener(this)
        addIndivButton(fastestPathButton,b,buttonLayout,gbc,0,3,2,1,Insets(10,40,10,40))

        // Reset Robot Button
        val resetButton = JButton("Reset Robot")
        resetButton.isFocusable = false
        resetButton.actionCommand = "Reset Robot"
        resetButton.addActionListener(this)
        addIndivButton(resetButton,b,buttonLayout,gbc,0,4,2,1,Insets(10,40,10,40))

        // Set Speed Slider
        val speedPanel = JPanel()
//        speedPanel.background = Color.WHITE
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
        addIndivButton(speedPanel,b,buttonLayout,gbc,0,5,2,1,Insets(10,40,10,40))

        // Set Time Limit Button
        val timePanel = JPanel()
//        timePanel.background = Color.WHITE
        val timeBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"Time limit (s) ")
        timeBorder.titleJustification = TitledBorder.CENTER
        timePanel.border = timeBorder
        val timeArr: Array<String?> = createSeqArray(0, 121)
        val timeButton = JComboBox<String>(timeArr)
        timeButton.isFocusable = false
        timeButton.actionCommand = "Set time limit"
        timeButton.addActionListener(this)
        timePanel.add(timeButton)
        addIndivButton(timePanel,b,buttonLayout,gbc,0,6,1,1,Insets(10,40,10,0))

        // Set Coverage Limit Button
        val coveragePanel = JPanel()
//        coveragePanel.background = Color.WHITE
        val coverageBorder: TitledBorder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(),"% limit  ")
        coverageBorder.titleJustification = TitledBorder.CENTER
        coveragePanel.border = coverageBorder
        val coverageArr: Array<String?> = createSeqArray(0, 101)
        val coverageButton = JComboBox<String>(coverageArr)
        coverageButton.isFocusable = false
        coverageButton.actionCommand = "Set % limit"
        coverageButton.addActionListener(this)
        coveragePanel.add(coverageButton)
        addIndivButton(coveragePanel,b,buttonLayout,gbc,1,6,1,1,Insets(10,5,10,40))

        // Set Waypoint Button
        val waypointPanel = JPanel()
//        waypointPanel.background = Color.WHITE
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
        addIndivButton(waypointPanel,b,buttonLayout,gbc,0,7,2,1,Insets(10,40,30,40))

    }

    private fun initMovementButtons() {
        f.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_RIGHT -> {
                        logger.debug{ "right button pressed" }
                        sim.bot.move(RobotConstants.MOVEMENT.RIGHT)
                        sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.trueMap)
                        m.repaint()
                    }
                    KeyEvent.VK_LEFT -> {
                        logger.debug{ "left button pressed" }
                        sim.bot.move(RobotConstants.MOVEMENT.LEFT)
                        sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.trueMap)
                        m.repaint()
                    }
                    KeyEvent.VK_UP -> {
                        logger.debug{ "up button pressed" }
                        sim.bot.move(RobotConstants.MOVEMENT.FORWARD)
                        sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.trueMap)
                        m.repaint()
                    }
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

    private fun loadMap(selectedFileString: String) {
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
            logger.debug{"IOException when reading$selectedFileString"}
        } catch (eX: Exception) {
            logger.debug{eX.message}
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        val action = e!!.actionCommand
        if (action!!.contentEquals("Load Map")) {
            val arenaMap = e.source as JComboBox<*>
            val selectedFileString = arenaMap.selectedItem as String
            loadMap(selectedFileString)
        }
        if (action!!.contentEquals("Display Map")) {
            val mapType = e.source as JComboBox<*>
            when (mapType.selectedItem as String){
                "True Map" -> {
                    sim.map = SimulatorServer.trueMap
                    updateSimulatorMap()
                }
                "Explored Map" -> {
                    sim.map = SimulatorServer.exploredMap
                    updateSimulatorMap()
                }
                "Real-Time Map" -> {
                    sim.map = SimulatorServer.realTimeMap
                    updateSimulatorMap()
                }
                else -> {
                    sim.map = SimulatorServer.trueMap
                    updateSimulatorMap()
                }
            }
        }
        if (action.contentEquals("Start")){
            SimulatorServer.handleStartExploration(
                timeout = if (with_time) time_chosen else -1,
                coverageLimit = if (with_coverage) percentage_chosen else 100,
            )
        }
        if (action.contentEquals("Stop")){
            runBlocking {
                SimulatorServer.stopExploration("Force stop from UI")
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

