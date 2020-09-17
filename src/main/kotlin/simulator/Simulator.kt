package simulator

import constants.RobotConstants
import data.map.MazeMap
import data.robot.Robot
import utils.map.loadMapFromDisk
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


private var f: JFrame? = null
var m: JPanel? = null
private var b: JPanel? = null

var sim: SimulatorMap? = null

private val realRun = false

fun main(args: Array<String>) {
    val map = MazeMap()
    map!!.setAllUnexplored()
    val bot = Robot(1,1)
    sim = SimulatorMap(map,bot)
    displayMainFrame()
}

private fun displayMainFrame() {

    //initialise main frame
    f = JFrame("Group 28 MDP Simulator")
    f!!.size = Dimension(1500, 1000)
    f!!.isResizable = false
    f!!.isFocusable = true
    f!!.focusTraversalKeysEnabled = false;

    //center main frame
    val dim = Toolkit.getDefaultToolkit().screenSize
    f!!.setLocation(dim.width / 2 - f!!.size.width / 2, dim.height / 2 - f!!.size.height / 2)

    //create CardLayout for storing different maps and robot
    m = JPanel(CardLayout())

    //create JPanel for buttons
    b = JPanel()

    initButtons()

    //add m & b to the main frame's content pane
    val contentPane = f!!.contentPane
    contentPane.add(m, BorderLayout.CENTER)
//    contentPane.add(b, BorderLayout.PAGE_END)
    initMain()

    // movement buttons to move robot
    initMovementButtons()

    f!!.isVisible = true
    f!!.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
}

private fun initMain() {
    m?.add(sim, "MAP")
    val cl = m!!.layout as CardLayout
    cl.show(m, "MAP")
    m?.setLocation(500,500)

}

private fun initButtons() {
//    b!!.layout = GridLayout()
//    addButtons()
    // add the buttons onto the frame
    var buttonListener = f?.let { AddJButtonActionListener(it) }
}

private fun formatButton(btn: JButton) {
    btn.font = Font("Arial", Font.BOLD, 13)
    btn.isFocusPainted = false
}

private fun addButtons() {
    if (!realRun) {
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
                        loadMapFromDisk(newMap!!, loadTF.text)
                        sim?.map = newMap
                        val cl = m!!.layout as CardLayout
                        cl.show(m, "REAL_MAP")
                        sim!!.repaint()
                    }
                })
                loadMapDialog.add(JLabel("File Name: "))
                loadMapDialog.add(loadTF)
                loadMapDialog.add(loadButton)
                loadMapDialog.isVisible = true
            }
        })
        b!!.add(loadMapButton)
    }


    // Exploration Button
    val exploreButton = JButton("Exploration")
    formatButton(exploreButton)
    exploreButton.isFocusable = false
    exploreButton.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            val cl = m!!.layout as CardLayout
            cl.show(m, "EXPLORATION")
        }
    })
    b!!.add(exploreButton)

    // Fastest Path Button
    val fastestPathButton = JButton("Fastest Path")
    formatButton(fastestPathButton)
    fastestPathButton.isFocusable = false
    fastestPathButton.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            val cl = m!!.layout as CardLayout
            cl.show(m, "FASTEST PATH")
        }
    })
    b!!.add(fastestPathButton)
}

private fun initMovementButtons() {
    f!!.addKeyListener(object : KeyListener {
        override fun keyPressed(e: KeyEvent) {
            if (e.keyCode == KeyEvent.VK_RIGHT) {
                println("right button pressed")
                sim?.bot!!.move(RobotConstants.MOVEMENT.RIGHT)
                sim!!.repaint()
            } else if (e.keyCode == KeyEvent.VK_LEFT) {
                println("left button pressed")
                sim?.bot!!.move(RobotConstants.MOVEMENT.LEFT)
                sim!!.repaint()
//            } else if (e.keyCode == KeyEvent.VK_DOWN) {
//                println("down button pressed")
//                sim?.bot!!.move(RobotConstants.MOVEMENT.BACKWARD)
//                sim!!.repaint()
            } else if (e.keyCode == KeyEvent.VK_UP) {
                println("up button pressed")
                sim?.bot!!.move(RobotConstants.MOVEMENT.FORWARD)
                sim!!.repaint()
            }
        }

        override fun keyTyped(e: KeyEvent) {
        }

        override fun keyReleased(e: KeyEvent) {
        }
    })
}

