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
import kotlin.collections.HashMap


object Simulator: ActionListener {

    lateinit var f: JFrame
    lateinit var m: JPanel
    lateinit var b: JPanel
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

    fun updateSimulatorMap(simulatorMap: SimulatorMap = sim) {
        sim = simulatorMap
        if(this::m.isInitialized) {
            m.repaint()
            logger.debug { "Map repainted!" }
        }
    }

    fun displayMainFrame() {
        //initialise main frame
        f = JFrame("MDP Simulator")
        f.size = Dimension(700, 820)
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
        contentPane.add(b, BorderLayout.PAGE_END)
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
        b.layout = GridLayout(4,3)
        addButtons()
    }

    private fun addButtons() {
        // Load Map Button
        val loadMapPanel = JPanel()
        val loadMapLabel = JLabel("Load Map:")
        val arr: Array<String?> = getMapFileNames()
        val loadMapButton = JComboBox<String>(arr)
        loadMapButton.isFocusable = false
        loadMapButton.actionCommand = "Load Map"
        loadMapButton.addActionListener(this)
        loadMapPanel.add(loadMapLabel)
        loadMapPanel.add(loadMapButton)
        b.add(loadMapPanel)

        // Show True Map Button
        val showTrueMapButton = JButton("Show True Map")
        showTrueMapButton.isFocusable = false
        showTrueMapButton.actionCommand = "Show True Map"
        showTrueMapButton.addActionListener(this)
        b.add(showTrueMapButton)

        // Show Explored Map Button
        val showExploredMapButton = JButton("Show Explored Map")
        showExploredMapButton.isFocusable = false
        showExploredMapButton.actionCommand = "Show Explored Map"
        showExploredMapButton.addActionListener(this)
        b.add(showExploredMapButton)

        // Exploration Button
        val exploreButton = JButton("Exploration")
        exploreButton.isFocusable = false
        exploreButton.actionCommand = "Exploration"
        exploreButton.addActionListener(this)
        b.add(exploreButton)

        // Fastest Path Button
        val fastestPathButton = JButton("Fastest Path")
        fastestPathButton.isFocusable = false
        fastestPathButton.actionCommand = "Fastest Path"
        fastestPathButton.addActionListener(this)
        b.add(fastestPathButton)

        // Reset Robot Button
        val resetButton = JButton("Reset Robot")
        resetButton.isFocusable = false
        resetButton.actionCommand = "Reset Robot"
        resetButton.addActionListener(this)
        b.add(resetButton)

        // Set Time Limit Button
        val timePanel = JPanel()
        val timeLabel = JLabel("Set time limit (s):")
        val timeArr: Array<String?> = createSeqArray(0, 121)
        val timeButton = JComboBox<String>(timeArr)
        timeButton.isFocusable = false
        timeButton.actionCommand = "Set time limit"
        timeButton.addActionListener(this)
        timePanel.add(timeLabel)
        timePanel.add(timeButton)
        b.add(timePanel)

        // Set Coverage Limit Button
        val coveragePanel = JPanel()
        val coverageLabel = JLabel("Set coverage limit (%):")
        val coverageArr: Array<String?> = createSeqArray(0, 101)
        val coverageButton = JComboBox<String>(coverageArr)
        coverageButton.isFocusable = false
        coverageButton.actionCommand = "Set % limit"
        coverageButton.addActionListener(this)
        coveragePanel.add(coverageLabel)
        coveragePanel.add(coverageButton)
        b.add(coveragePanel)

        // Set Speed Button
        val speedPanel = JPanel()
        val speedLabel = JLabel("Set speed (s/step):")
        val speedArr: Array<String?> = createSeqArray(1,6)
        val speedButton = JComboBox<String>(speedArr)
        speedButton.isFocusable = false
        speedButton.actionCommand = "Set speed"
        speedButton.addActionListener(this)
        speedPanel.add(speedLabel)
        speedPanel.add(speedButton)
        b.add(speedPanel)

        // Set Waypoint Button
        val waypointPanel = JPanel()
        val waypointLabel = JLabel("Set waypoint:")
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
        waypointPanel.add(waypointLabel)
        waypointPanel.add(waypointRowButton)
        waypointPanel.add(waypointColButton)
        b.add(waypointPanel)

    }

    private fun initMovementButtons() {
        f.addKeyListener(object : KeyListener {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_RIGHT) {
                    logger.debug{ "right button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.RIGHT)
                    sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.mazeMap)
                    m.repaint()
                } else if (e.keyCode == KeyEvent.VK_LEFT) {
                    logger.debug{ "left button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.LEFT)
                    sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.mazeMap)
                    m.repaint()
                } else if (e.keyCode == KeyEvent.VK_UP) {
                    logger.debug{ "up button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.FORWARD)
                    sim.bot.simulateSensors(SimulatorServer.exploredMap, SimulatorServer.mazeMap)
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
                SimulatorServer.mazeMap = newMap
                sim.map= SimulatorServer.mazeMap
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
            sim.map = SimulatorServer.mazeMap
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
            sim.bot.resetRobot()
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

