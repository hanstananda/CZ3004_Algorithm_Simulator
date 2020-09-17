package simulator

import constants.MapConstants
import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import mu.KotlinLogging
import utils.map.loadMapFromDisk
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import javax.swing.*
import kotlin.collections.HashMap

object Simulator {

    lateinit var f: JFrame
    lateinit var m: JPanel
    lateinit var b: JPanel
    val logger = KotlinLogging.logger {}

    lateinit var sim: SimulatorMap

    private val waypoint_chosen = intArrayOf(-1, -1)
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
        f.size = Dimension(700, 800)
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
        b.layout = GridLayout(3,3)
        addButtons()
    }

    private fun formatButton(btn: JButton) {
        btn.font = Font("Arial", Font.BOLD, 13)
        btn.isFocusPainted = false
    }

    private fun addButtons() {
        // Exploration Button
        val exploreButton = JButton("Exploration")
        exploreButton.isFocusable = false
        exploreButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
            }
        })
        b!!.add(exploreButton)

        // Fastest Path Button
        val fastestPathButton = JButton("Fastest Path")
        fastestPathButton.isFocusable = false
        fastestPathButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
            }
        })
        b!!.add(fastestPathButton)

        // Reset Robot Button
        val resetButton = JButton("Reset Robot")
        resetButton.isFocusable = false
        resetButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                sim.bot.resetRobot()
                updateSimulatorMap()
            }
        })
        b!!.add(resetButton)

        // Load Map Button
        val loadMapPanel = JPanel()
        val loadMapLabel = JLabel("Load Map:")
        val arr: Array<String?> = getMapFileNames()
        val loadMapButton = JComboBox<String>(arr)
        loadMapButton.isFocusable = false
        loadMapButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val arenaMap = e.source as JComboBox<String>
                val selectedFile = arenaMap.selectedItem ?: return
                val selectedFileString = arenaMap.selectedItem as String
                try {
                    val newMap = MazeMap()
                    loadMapFromDisk(newMap,selectedFileString)
                    if (newMap != null) {
                        sim.map = newMap
                    }
                    val cl = m!!.layout as CardLayout
                    cl.show(m, "map")
                    updateSimulatorMap()

                } catch (f: FileNotFoundException) {
                    println("File not found")
                } catch (IO: IOException) {
                    println("IOException when reading$selectedFile")
                } catch (eX: Exception) {
                    println(eX.message)
                }
            }
        })
        loadMapPanel.add(loadMapLabel)
        loadMapPanel.add(loadMapButton)
        b.add(loadMapPanel)

        // Set Time Limit Button
        val timePanel = JPanel()
        val timeLabel = JLabel("Set time limit (s):")
        val timeArr: Array<String?> = createSeqArray(0, 121)
        val timeButton = JComboBox<String>(timeArr)
        timeButton.isFocusable = false
        timeButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val secs = e.source as JComboBox<String>
                val secsChosen = secs.selectedItem as String
                if (secsChosen == null) {
                    return
                } else {
                    time_chosen = secsChosen.toInt()
                }
            }
        })
        timePanel.add(timeLabel)
        timePanel.add(timeButton)
        b.add(timePanel)

        // Set Coverage Limit Button
        val coveragePanel = JPanel()
        val coverageLabel = JLabel("Set coverage limit (%):")
        val coverageArr: Array<String?> = createSeqArray(0, 101)
        val coverageButton = JComboBox<String>(coverageArr)
        coverageButton.isFocusable = false
        coverageButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val percentage = e.source as JComboBox<String>
                if (percentage == null) {
                    return
                } else {
                    val percentageChosen = percentage.selectedItem as String
                    percentage_chosen = percentageChosen?.toInt()
                }
            }
        })
        coveragePanel.add(coverageLabel)
        coveragePanel.add(coverageButton)
        b.add(coveragePanel)

        // Set Speed Button
        val speedPanel = JPanel()
        val speedLabel = JLabel("Set speed (s/step):")
        val speedArr: Array<String?> = createSeqArray(1,6)
        val speedButton = JComboBox<String>(speedArr)
        speedButton.isFocusable = false
        speedButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val s = e.source as JComboBox<String>
                val sp = s.selectedItem as String
                if (sp != null) {
                    speed_chosen = sp.toInt()
                }
            }
        })
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
        waypointRowButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val waypointRow = e.source as JComboBox<String>
                val selectedWaypointRow = waypointRow.selectedItem as String
                if (selectedWaypointRow == null) {
                    waypoint_chosen[0] = -1
                } else {
                    waypoint_chosen[0] = selectedWaypointRow.toInt()
                }
            }
        })
        waypointColButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val waypointCol = e.source as JComboBox<String>
                val selectedWaypointCol = waypointCol.selectedItem as String
                if (selectedWaypointCol == null) {
                    waypoint_chosen[1] = -1
                } else {
                    waypoint_chosen[1] = selectedWaypointCol.toInt()
                }
            }
        })
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
                    m.repaint()
                } else if (e.keyCode == KeyEvent.VK_LEFT) {
                    logger.debug{ "left button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.LEFT)
                    m.repaint()
                } else if (e.keyCode == KeyEvent.VK_UP) {
                    logger.debug{ "up button pressed" }
                    sim.bot.move(RobotConstants.MOVEMENT.FORWARD)
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
        var filePath = HashMap<String, String>()
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

}

