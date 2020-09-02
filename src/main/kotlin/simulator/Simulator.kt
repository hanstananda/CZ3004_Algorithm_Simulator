package simulator

import data.map.MazeMap
import utils.map.loadMapFromDisk
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


private var f: JFrame? = null
private var m: JPanel? = null
private var b: JPanel? = null

private val bot: Robot? = null

private var realMap: MazeMap? = null // real map

private var exploredMap: MazeMap? = null // exploration map

private val realRun = false

fun main(args: Array<String>) {
    if (!realRun) {
        realMap = MazeMap()
        realMap!!.setAllUnexplored()
    }
    exploredMap = MazeMap()
    exploredMap!!.setAllUnexplored()
    displayMainFrame()
}

private fun displayMainFrame() {

    //initialise main frame
    f = JFrame("MDP Simulator")
    f!!.size = Dimension(690, 700)
    f!!.isResizable = false

    //center main frame
    val dim = Toolkit.getDefaultToolkit().screenSize
    f!!.setLocation(dim.width / 2 - f!!.size.width / 2, dim.height / 2 - f!!.size.height / 2)

    //create CardLayout for storing different maps
    m = JPanel(CardLayout())

    //create JPanel for buttons
    b = JPanel()

    //add m & b to the main frame's content pane
    val contentPane = f!!.contentPane
    contentPane.add(m, BorderLayout.CENTER)
    contentPane.add(b, BorderLayout.PAGE_END)
    initMain()
    initButtons()
    f!!.isVisible = true
    f!!.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
}

private fun initMain() {
//        if (!realRun) {
//            m.add(realMap, "REAL_MAP");
//        }
//        m.add(exploredMap, "EXPLORATION");
    val cl = m!!.layout as CardLayout
    if (!realRun) {
        cl.show(m, "REAL_MAP")
    } else {
        cl.show(m, "EXPLORATION")
    }
}

private fun initButtons() {
    b!!.layout = GridLayout()
    addButtons()
}

private fun formatButton(btn: JButton) {
    btn.font = Font("Arial", Font.BOLD, 13)
    btn.isFocusPainted = false
}

private fun addButtons() {
    if (!realRun) {
        // Load Map Button
        val btn_LoadMap = JButton("Load Map")
        formatButton(btn_LoadMap)
        btn_LoadMap.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val loadMapDialog = JDialog(f, "Load Map", true)
                loadMapDialog.setSize(400, 60)
                loadMapDialog.layout = FlowLayout()
                val loadTF = JTextField(15)
                val loadMapButton = JButton("Load")
                loadMapButton.addMouseListener(object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent) {
                        loadMapDialog.isVisible = false
                        loadMapFromDisk(realMap!!, loadTF.text)
                        val cl = m!!.layout as CardLayout
                        cl.show(m, "REAL_MAP")
                        realMap!!.reset()
                    }
                })
                loadMapDialog.add(JLabel("File Name: "))
                loadMapDialog.add(loadTF)
                loadMapDialog.add(loadMapButton)
                loadMapDialog.isVisible = true
            }
        })
        b!!.add(btn_LoadMap)
    }


    // Exploration Button
    val btn_Exploration = JButton("Exploration")
    formatButton(btn_Exploration)
    btn_Exploration.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            val cl = m!!.layout as CardLayout
            cl.show(m, "EXPLORATION")
        }
    })
    b!!.add(btn_Exploration)

    // Fastest Path Button
    val btn_FastestPath = JButton("Fastest Path")
    formatButton(btn_FastestPath)
    btn_FastestPath.addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
            val cl = m!!.layout as CardLayout
            cl.show(m, "EXPLORATION")
        }
    })
    b!!.add(btn_FastestPath)
}
