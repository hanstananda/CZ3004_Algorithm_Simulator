package simulator

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
import javax.swing.*

object Simulator {

    lateinit var f: JFrame
    lateinit var m: JPanel
    lateinit var b: JPanel
    val logger = KotlinLogging.logger {}

    lateinit var sim: SimulatorMap

    @JvmStatic
    fun main(args: Array<String>) {
        val map = MazeMap()
        map.initExploredAreas()
        val bot = Robot(1, 1)
        sim = SimulatorMap(map, bot)
        displayMainFrame()
    }

    fun updateSimulatorMap(simulatorMap: SimulatorMap) {
        sim = simulatorMap
        if(this::m.isInitialized) {
            m.repaint()
        }
    }

    fun displayMainFrame() {

        //initialise main frame
        f = JFrame("MDP Simulator")
        f.size = Dimension(690, 700)
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
        b.layout = GridLayout()
        addButtons()
    }

    private fun formatButton(btn: JButton) {
        btn.font = Font("Arial", Font.BOLD, 13)
        btn.isFocusPainted = false
    }

    private fun addButtons() {
        // Load Map Button
        val loadMapButton = JButton("Load Map")
        loadMapButton.isFocusable = false
        formatButton(loadMapButton)
        loadMapButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val loadMapDialog = JDialog(f, "Load Map", true)
                loadMapDialog.setSize(400, 80)
                loadMapDialog.layout = FlowLayout()
                val loadTF = JTextField(15)
                val loadButton = JButton("Load")
                loadButton.isFocusable = false
                loadButton.addMouseListener(object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent) {
                        val newMap = MazeMap()
                        loadMapDialog.isVisible = false
                        loadMapFromDisk(newMap, loadTF.text)
                        sim = SimulatorMap(newMap, sim.bot)
                        val cl = m.layout as CardLayout
                        cl.show(m, "REAL_MAP")
                        sim.repaint()
                    }
                })
                loadMapDialog.add(JLabel("File Name: "))
                loadMapDialog.add(loadTF)
                loadMapDialog.add(loadButton)
                loadMapDialog.isVisible = true
            }
        })
        b.add(loadMapButton)



        // Exploration Button
        val exploreButton = JButton("Exploration")
        formatButton(exploreButton)
        exploreButton.isFocusable = false
        exploreButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val cl = m.layout as CardLayout
                cl.show(m, "EXPLORATION")
            }
        })
        b.add(exploreButton)

        // Fastest Path Button
        val fastestPathButton = JButton("Fastest Path")
        formatButton(fastestPathButton)
        fastestPathButton.isFocusable = false
        fastestPathButton.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val cl = m.layout as CardLayout
                cl.show(m, "FASTEST PATH")
            }
        })
        b.add(fastestPathButton)
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
//            } else if (e.keyCode == KeyEvent.VK_DOWN) {
//                logger.debug{ "down button pressed" }
//                sim.bot.move(RobotConstants.MOVEMENT.BACKWARD)
//                m.repaint()
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
}

