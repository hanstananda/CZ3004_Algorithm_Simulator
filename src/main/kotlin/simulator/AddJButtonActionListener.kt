package simulator

import constants.MapConstants
import constants.RobotConstants
import data.robot.Robot
import data.map.MazeMap
import utils.map.loadMapFromDisk
import java.awt.*
import java.awt.event.*
import java.io.*
import java.util.*
import java.util.Timer
import java.util.concurrent.TimeUnit
import javax.swing.*
import kotlin.collections.HashMap

class AddJButtonActionListener(private val frame: JFrame) : ActionListener {
    private val x = 1000
    private val y = 200
    private val r: Robot? = null
    private val buttons = ArrayList<JComponent>()
    private val labels = HashMap<String, JLabel>()
    private var filePath = HashMap<String, String>()
    private val t = Timer()
    private val delay = 15
    private val gridWidth = 40
    private val gridHeight = 40
    private val step = 1
    private var loadedMap: MazeMap? = null
    private val waypointChosen = intArrayOf(-1, -1)
    private var timeChosen = -1
    private var percentageChosen = 100
    private var MDFString = arrayOf<String?>(null, null)
    private var speedChosen = 1
    private var imageRecognitionChosen = false
    private var message: JTextArea? = null
    private var scrollPane: JScrollPane? = null
    private fun displayMessage(s: String, mode: Int) {
        // Mode 0 only print the message, Mode 1 display onto the console, Mode 2 does both
        val vbar = scrollPane!!.verticalScrollBar
        when (mode) {
            0 -> println(s)
            1 -> {
                message!!.append(s.trimIndent())
                try {
                    val messageText = message!!.document.getText(0, message!!.document.length)
                    if (messageText[0] == '\n') {
                        message!!.text = message!!.document.getText(0, message!!.document.length).replaceFirst("\n".toRegex(), "")
                    }
                    val arr = messageText.split("\n").toTypedArray()
                    if (arr.size > 100) {
                        message!!.text = message!!.document.getText(0, message!!.document.length).replaceFirst(arr[0] + "\n".toRegex(), "")
                    }
                } catch (e: Exception) {
                    println("Error in displayMessage")
                }
                vbar.addAdjustmentListener(object : AdjustmentListener {
                    override fun adjustmentValueChanged(e: AdjustmentEvent) {
                        val adjustable = e.adjustable
                        adjustable.value = adjustable.maximum
                        // This is so that the user can scroll down afterwards
                        vbar.removeAdjustmentListener(this)
                    }
                })
            }
            2 -> {
                println(s)
                message!!.append(s.trimIndent())
                try {
                    val messageText = message!!.document.getText(0, message!!.document.length)
                    if (messageText[0] == '\n') {
                        message!!.text = message!!.document.getText(0, message!!.document.length).replaceFirst("\n".toRegex(), "")
                    }
                    val arr = messageText.split("\n").toTypedArray()
                    if (arr.size > 100) {
                        message!!.text = message!!.document.getText(0, message!!.document.length).replaceFirst(arr[0] + "\n".toRegex(), "")
                    }
                } catch (e: Exception) {
                    println("Error in displayMessage")
                }
                vbar.addAdjustmentListener(object : AdjustmentListener {
                    override fun adjustmentValueChanged(e: AdjustmentEvent) {
                        val adjustable = e.adjustable
                        adjustable.value = adjustable.maximum
                        // This is so that the user can scroll down afterwards
                        vbar.removeAdjustmentListener(this)
                    }
                })
            }
        }
    }

    private fun createSeqArray(min: Int, max: Int): Array<String?> {
        val arr = arrayOfNulls<String>(max - min + 1)
        var count = 1
        for (i in min until max) {
            arr[count] = Integer.toString(i)
            count++
        }
        return arr
    }

    // reads all txt files from the resources directory. Returns the array of the file name in the directory.
    val arenaMapFileNames: Array<String?>
        get() {
            val folder = File("./src/main/resources")
            filePath = HashMap()
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

    // Convert the sample arena text file into map and return the map
    @Throws(Exception::class, FileNotFoundException::class, IOException::class)
    fun getGridfromFile(path: String?, fileName: String, grid: Array<Array<String?>>): MazeMap? {
//        val fr = FileReader(path)
//        val br = BufferedReader(fr)
//        var line = br.readLine()
//        var heightCount = 0
//        while (line != null) {
//            line = line.strip().toUpperCase()
//
//            // Check for invalid map
//            if (line.length != MapConstants.DEFAULT_COL_SIZE) {
//                displayMessage("The format of the $fileName does not match the board format.", 1)
//                throw Exception("The format of the $fileName does not match the board format.")
//            }
//            for (i in line.indices) {
//                when (line[i]) {
////                    'S' -> grid[i][heightCount] = MapConstants.POSSIBLEGRIDLABELS[4]
////                    'U' ->                        // Here, we set to explored instead of Unexplored
////                        grid[i][heightCount] = MapConstants.POSSIBLEGRIDLABELS[1]
//                    '0' -> grid[i][heightCount] = MapConstants.POSSIBLEGRIDLABELS[0]
//                    '1' -> grid[i][heightCount] = MapConstants.POSSIBLEGRIDLABELS[2]
//                    else -> {
//                        displayMessage("""There is unrecognised character symbol in $fileName.$fileName failed to load into the program.""", 1)
//                        throw Exception("""There is unrecognised character symbol in $fileName.$fileName failed to load into the program.""")
//                    }
//                }
//            }
//            heightCount++
//            line = br.readLine()
//        }
//        if (heightCount != MapConstants.DEFAULT_COL_SIZE) {
//            throw Exception("The format of the $fileName does not match the board format.")
//        }
//        br.close()

        loadedMap = MazeMap()
        loadMapFromDisk(loadedMap!!,fileName)
        displayMessage("$fileName has loaded successfully.", 2)
        return loadedMap
    }

    private fun disableButtons() {
        for (i in buttons.indices) {
            buttons[i].isEnabled = false
        }
    }

    fun enableButtons() {
        for (i in buttons.indices) {
            buttons[i].isEnabled = true
        }
    }

    fun disableLabel(label: String?) {
        labels[label]!!.isVisible = false
    }

    fun enableLabel(label: String?) {
        labels[label]!!.isVisible = true
    }

    // defines button actions
    override fun actionPerformed(e: ActionEvent) {
        val action = e.actionCommand
        if (action == "Right") {
            displayMessage("Rotate right button clicked", 2)
//            disableButtons()
//            if (!labels["robotView"]!!.isVisible) {
//                enableLabel("robotView")
//                disableLabel("simulatedMap")
//            }
            sim?.bot!!.move(RobotConstants.MOVEMENT.RIGHT)
            m!!.repaint()
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action == "Left") {
            displayMessage("Rotate left button clicked", 2)
//            disableButtons()
//            if (!labels["robotView"]!!.isVisible) {
//                enableLabel("robotView")
//                disableLabel("simulatedMap")
//            }
            sim?.bot!!.move(RobotConstants.MOVEMENT.LEFT)
            m!!.repaint()
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action == "Up") {
            displayMessage("Forward button clicked", 2)
//            disableButtons()
//            if (!labels["robotView"]!!.isVisible) {
//                enableLabel("robotView")
//                disableLabel("simulatedMap")
//                //r.toggleMap()
//            }
            sim?.bot!!.move(RobotConstants.MOVEMENT.FORWARD)
            m!!.repaint()
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action.contentEquals("Update")) {
//            disableButtons()
//            if (!labels["robotView"]!!.isVisible) {
//                enableLabel("robotView")
//                disableLabel("simulatedMap")
//            }
//            val isObstacle: IntArray = r.updateMap()
//            for (i in isObstacle.indices) {
//                print(isObstacle[i].toString() + " ")
//            }
//            println()
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action.contentEquals("Check Map")) {
//            disableButtons()
//            JOptionPane.showMessageDialog(null, r.checkMap(), "Result of checking map", JOptionPane.INFORMATION_MESSAGE)
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action.contentEquals("Toggle Map")) {
//            disableButtons()
//            if (r.toggleMap().compareTo("robot") === 0) {
//                if (r.checkMap().equals("The maps are the same!") && labels["robotView"]!!.isVisible) {
//                    disableLabel("robotView")
//                    enableLabel("simulatedMap")
//                } else {
//                    enableLabel("robotView")
//                    disableLabel("simulatedMap")
//                }
//            } else {
//                disableLabel("robotView")
//                enableLabel("simulatedMap")
//            }
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action.contentEquals("Restart")) {
//            disableButtons()
//            enableLabel("robotView")
//            disableLabel("simulatedMap")
//            ExplorationThread.stopThread()
//            FastestPathThread.stopThread()
//            r.restartRobotUI()
//            try {
//                TimeUnit.MILLISECONDS.sleep(500)
//            } catch (z: Exception) {
//            }
//            r.restartRobot()
//            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
//            displayMessage("Restarted the Robot", 2)
        }
        if (action.contentEquals("Load Map")) {
            val arenaMap = e.source as JComboBox<String>
            val selectedFile = arenaMap.selectedItem ?: return
            val selectedFileString = arenaMap.selectedItem as String
            if (selectedFileString.compareTo("Choose a map to load") == 0) {
                return
            }
//            val grid = Array(MapConstants.DEFAULT_ROW_SIZE) { arrayOfNulls<String>(MapConstants.DEFAULT_COL_SIZE) }
//            disableButtons()
            try {
//                val newMap: MazeMap? = getGridfromFile(filePath[selectedFile], selectedFileString, grid)
                val newMap = MazeMap()
                loadMapFromDisk(newMap,selectedFileString)
                displayMessage("$selectedFileString has loaded successfully.", 2)
//                r.setTrueMap(map)
                if (newMap != null) {
                    sim?.map = newMap
                }
                val cl = m!!.layout as CardLayout
                cl.show(m, "map")
                m!!.repaint()
//                if (labels["simulatedMap"]!!.isVisible) {
//                    enableLabel("robotView")
//                    disableLabel("simulatedMap")
//                    /*r.toggleMap()
//                    r.toggleMap()*/
//                }
            } catch (f: FileNotFoundException) {
                println("File not found")
            } catch (IO: IOException) {
                println("IOException when reading$selectedFile")
            } catch (eX: Exception) {
                println(eX.message)
            }
            t.schedule(EnableButtonTask(this), delay * (step * gridWidth.toLong() + 1))
        }
        if (action.contentEquals("Fastest Path")) {

        }
        if (action.contentEquals("Exploration")) {

        }
        if (action.contentEquals("Set x coordinate")) {
//            val astar = AStarPathFinder()
//            val waypoint_x = e.source as JComboBox<String>
//            val selected_waypoint_x = waypoint_x.selectedItem as String
//            if (selected_waypoint_x == null) {
//                waypoint_chosen[0] = -1
//            } else {
//                waypoint_chosen[0] = selected_waypoint_x.toInt()
//            }
//            val old_waypoint: IntArray = r.getWaypoint()
//            r.setWaypoint(waypoint_chosen[0], waypoint_chosen[1])
//            if (!Arrays.equals(old_waypoint, r.getWaypoint())) {
//                enableLabel("robotView")
//                disableLabel("simulatedMap")
//            }
        }
        if (action.contentEquals("Set y coordinate")) {
//            val astar = AStarPathFinder()
//            val waypoint_y = e.source as JComboBox<String>
//            val selected_waypoint_y = waypoint_y.selectedItem as String
//            if (selected_waypoint_y == null) {
//                waypoint_chosen[1] = -1
//            } else {
//                waypoint_chosen[1] = selected_waypoint_y.toInt()
//            }
//            val old_waypoint: IntArray = r.getWaypoint()
//            r.setWaypoint(waypoint_chosen[0], waypoint_chosen[1])
//            if (!Arrays.equals(old_waypoint, r.getWaypoint())) {
//                enableLabel("robotView")
//                disableLabel("simulatedMap")
//            }
        }
        if (action.contentEquals("Set time limit")) {
            val secs = e.source as JComboBox<String>
            val secs_chosen = secs.selectedItem
            if (secs_chosen!=null){
                val secs_chosen_string = secs_chosen as String
                timeChosen = secs_chosen_string?.toInt()
            }
        }
        if (action.contentEquals("Set % limit")) {
            val percentage = e.source as JComboBox<String>
            val perc_chosen = percentage.selectedItem as String
            percentageChosen = (perc_chosen?.toInt() ?: return)
        }
        if (action.contentEquals("Return")) {
            sim?.bot!!.resetRobot()
            m!!.repaint()

        }
        if (action.contentEquals("MDF String")) {
//            MDFString = r.getMDFString()
//            displayMessage("The MDF String is: ", 2)
//            displayMessage("Part 1: " + MDFString[0], 2)
//            displayMessage("Part 2: " + MDFString[2], 2)
        }
        if (action.contentEquals("Set speed")) {
            val s = e.source as JComboBox<String>
            val sp = s.selectedItem
            if (sp != null) {
                val spString = sp as String
                speedChosen = spString.toInt()
            }
        }
        if (action.contentEquals("Set image recognition")) {
//            val ir = e.source as JComboBox<String>
//            val irc = ir.selectedItem as String
//            if (irc != null) {
//                imageRecognitionChosen = irc == "With Image Recognition"
//                println("Image Rec: $imageRecognitionChosen")
//            }
        }
    }

    // This constructor initialises all the buttons
    init {
        val arr = arenaMapFileNames
        val waypoint_x_pos = createSeqArray(1, MapConstants.DEFAULT_ROW_SIZE - 1)
        val waypoint_y_pos = createSeqArray(1, MapConstants.DEFAULT_COL_SIZE - 1)
        val time_arr = createSeqArray(0, 121)
        val percentage_arr = createSeqArray(0, 101)
        val speed_arr = createSeqArray(1, 6)
        val image_rec_arr = arrayOf("No Image Recognition", "With Image Recognition")

        // Create the UI Component
        val mcLabel = JLabel("Manual Control:")
        val loadMapLabel = JLabel("Select the map you wish to load:")
        val right = JButton()
        val left = JButton()
        val up = JButton()
        val update = JButton()
        val checkMap = JButton()
        val toggleMap = JButton()
        val resetRobot = JButton()
//        val robotView = JLabel("Robot's View")
        val simulatedMap = JLabel("Simulated Map")
        val exploration = JButton()
        val fastestPath = JButton()
        val arenaMap = JComboBox(arr)
        val waypoint_x = JComboBox(waypoint_x_pos)
        val waypoint_y = JComboBox(waypoint_y_pos)
        val set_waypoint_label = JLabel("Set Waypoint: ")
        val invalid_waypoint = JLabel("Invalid Waypoint!!")
        val time = JComboBox(time_arr)
        val time_label = JLabel("Set exploration time limit (secs): ")
        val percentage = JComboBox(percentage_arr)
        val percentage_label = JLabel("Set exploration coverage limit (%): ")
        val returnToStart = JButton()
        val printMDF = JButton()
        val speed_label = JLabel("Set speed (secs/step): ")
        val speed = JComboBox(speed_arr)
        val image_rec = JComboBox(image_rec_arr)
        val status = JLabel("Status")
        message = JTextArea("\n".repeat(7) + "Initialising the User Interface...")
        scrollPane = JScrollPane(message)

//		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//	        public void adjustmentValueChanged(AdjustmentEvent e) {
//	            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
//
//	        }
//	    });
//
        scrollPane!!.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS)

        // Set Icon or Image to the UI Component
//        right.icon = ImageIcon(ImageIcon(".\\images\\right.png").image.getScaledInstance(gridWidth,gridHeight, Image.SCALE_DEFAULT))
//        left.icon = ImageIcon(ImageIcon(".\\images\\left.png").image.getScaledInstance(gridWidth,gridHeight, Image.SCALE_DEFAULT))
//        up.icon = ImageIcon(ImageIcon(".\\images\\up.png").image.getScaledInstance(gridWidth,gridHeight, Image.SCALE_DEFAULT))
        update.text = "Update"
        checkMap.text = "Check Map"
        toggleMap.text = "Toggle Map"
        resetRobot.text = "Restart"
        exploration.text = "Exploration"
        fastestPath.text = "Fastest Path"
        printMDF.text = "MDF String"
        returnToStart.text = "Return To Start"

        // For the Button to do something, you need to add the button to this Action Listener and set the command for the ActionListener to receive
        right.addActionListener(this)
        right.actionCommand = "Right"
        left.addActionListener(this)
        left.actionCommand = "Left"
        up.addActionListener(this)
        up.actionCommand = "Up"
        update.addActionListener(this)
        update.actionCommand = "Update"
        checkMap.addActionListener(this)
        checkMap.actionCommand = "Check Map"
        toggleMap.addActionListener(this)
        toggleMap.actionCommand = "Toggle Map"
        resetRobot.addActionListener(this)
        resetRobot.actionCommand = "Restart"
        exploration.addActionListener(this)
        exploration.actionCommand = "Exploration"
        fastestPath.addActionListener(this)
        fastestPath.actionCommand = "Fastest Path"
        arenaMap.addActionListener(this)
        arenaMap.actionCommand = "Load Map"
        arenaMap.selectedIndex = -1 //  This line will print null in console
        waypoint_x.addActionListener(this)
        waypoint_x.actionCommand = "Set x coordinate"
        waypoint_x.selectedIndex = -1 //  This line will print null in console
        waypoint_y.addActionListener(this)
        waypoint_y.actionCommand = "Set y coordinate"
        waypoint_y.selectedIndex = -1 //  This line will print null in console
        time.addActionListener(this)
        time.actionCommand = "Set time limit"
        time.selectedIndex = -1 //  This line will print null in console
        percentage.addActionListener(this)
        percentage.actionCommand = "Set % limit"
        percentage.selectedIndex = 101
        returnToStart.addActionListener(this)
        returnToStart.actionCommand = "Return"
        printMDF.addActionListener(this)
        printMDF.actionCommand = "MDF String"
        speed.addActionListener(this)
        speed.actionCommand = "Set speed"
        speed.selectedIndex = 0
        image_rec.addActionListener(this)
        image_rec.actionCommand = "Set image recognition"
        image_rec.selectedIndex = 0


        // Set the size (x, y, width, height) of the UI label
        mcLabel.setBounds(x, y - 100, 100, 50)
        loadMapLabel.setBounds(x, y + 165, 300, 50)
        left.setBounds(x + 100, y - 100, 50, 50)
        right.setBounds(x + 200, y - 100, 50, 50)
        up.setBounds(x + 150, y - 100, 50, 50)
        update.setBounds(x + 275, y - 100, 100, 50)
        checkMap.setBounds(x, y - 25, 100, 50)
        toggleMap.setBounds(x + 150, y - 25, 110, 50)
        resetRobot.setBounds(x + 300, y - 25, 100, 50)
//        robotView.setBounds(x - 600, y - 185, 200, 50)
        simulatedMap.setBounds(x + 100, y - 100, 300, 50)
        exploration.setBounds(x, y + 50, 110, 50)
        fastestPath.setBounds(x + 150, y + 50, 110, 50)
        arenaMap.setBounds(x + 200, y + 175, 120, 30)
        waypoint_x.setBounds(x + 85, y + 125, 50, 30)
        waypoint_y.setBounds(x + 150, y + 125, 50, 30)
        set_waypoint_label.setBounds(x, y + 125, 300, 30)
        invalid_waypoint.setBounds(x + 150, y + 125, 300, 30)
        time.setBounds(x + 200, y + 225, 50, 30)
        time_label.setBounds(x, y + 225, 300, 30)
        percentage.setBounds(x + 200, y + 275, 50, 30)
        percentage_label.setBounds(x, y + 275, 300, 30)
        speed.setBounds(x + 150, y + 325, 50, 30)
        speed_label.setBounds(x, y + 325, 200, 30)
        returnToStart.setBounds(x + 300, y + 50, 140, 50)
        printMDF.setBounds(x + 275, y + 305, 100, 50)
        image_rec.setBounds(x + 275, y + 225, 175, 30)
        status.setBounds(x, y + 360, 50, 30)
        scrollPane!!.setBounds(x, y + 390, 500, 163)


        // Set fonts for the labels
        mcLabel.font = Font(mcLabel.font.name, Font.ITALIC, 13)
//        robotView.font = Font(robotView.font.name, Font.BOLD, 30)
        simulatedMap.font = Font(simulatedMap.font.name, Font.BOLD, 30)
        status.font = Font(simulatedMap.font.name, Font.BOLD, 15)
        message!!.setFont(Font(simulatedMap.font.name, Font.ITALIC, 15))

        // Set background colour of components
        message!!.setBackground(Color.WHITE)

        // Set edittable of components
        message!!.setEditable(false)

        // Set max row of message
        message!!.setLineWrap(true)
        message!!.setWrapStyleWord(true)

        // Set location of the UI component
        mcLabel.setLocation(x, y - 100)
        loadMapLabel.setLocation(x, y + 165)
        left.setLocation(x + 100, y - 100)
        right.setLocation(x + 200, y - 100)
        up.setLocation(x + 150, y - 100)
        update.setLocation(x + 275, y - 100)
        checkMap.setLocation(x, y - 25)
        toggleMap.setLocation(x + 150, y - 25)
        resetRobot.setLocation(x + 300, y - 25)
//        robotView.setLocation(x - 600, y - 185)
        simulatedMap.setLocation(x - 600, y - 185)
        exploration.setLocation(x, y + 50)
        fastestPath.setLocation(x + 150, y + 50)
        arenaMap.setLocation(x + 200, y + 175)
        waypoint_x.setLocation(x + 85, y + 125)
        waypoint_y.setLocation(x + 150, y + 125)
        set_waypoint_label.setLocation(x, y + 125)
        invalid_waypoint.setLocation(x + 250, y + 125)
        time.setLocation(x + 200, y + 225)
        time_label.setLocation(x, y + 225)
        percentage.setLocation(x + 200, y + 275)
        percentage_label.setLocation(x, y + 275)
        speed.setLocation(x + 150, y + 325)
        speed_label.setLocation(x, y + 325)
        returnToStart.setLocation(x + 300, y + 50)
        printMDF.setLocation(x + 275, y + 305)
        image_rec.setLocation(x + 275, y + 225)
        status.setLocation(x, y + 360)
        scrollPane!!.setLocation(x, y + 390)

        // Add the UI component to the frame
        frame.add(mcLabel)
        frame.add(loadMapLabel)
        frame.add(right)
        frame.add(left)
        frame.add(up)
        frame.add(update)
        frame.add(checkMap)
        frame.add(toggleMap)
        frame.add(resetRobot)
//        frame.add(robotView)
        frame.add(simulatedMap)
        frame.add(exploration)
        frame.add(fastestPath)
        frame.add(arenaMap)
        frame.add(waypoint_x)
        frame.add(waypoint_y)
        frame.add(set_waypoint_label)
        frame.add(invalid_waypoint)
        frame.add(time)
        frame.add(time_label)
        frame.add(percentage)
        frame.add(percentage_label)
        frame.add(returnToStart)
        frame.add(printMDF)
        frame.add(speed)
        frame.add(speed_label)
        frame.add(image_rec)
        frame.add(status)
        frame.add(scrollPane)

        // Set Visibility of UI Component
        mcLabel.isVisible = true
        loadMapLabel.isVisible = true
        right.isVisible = true
        left.isVisible = true
        up.isVisible = true
        update.isVisible = true
        checkMap.isVisible = true
        toggleMap.isVisible = true
        resetRobot.isVisible = true
//        robotView.isVisible = true
        simulatedMap.isVisible = false
        exploration.isVisible = true
        fastestPath.isVisible = true
        arenaMap.isVisible = true
        waypoint_x.isVisible = true
        waypoint_y.isVisible = true
        set_waypoint_label.isVisible = true
        invalid_waypoint.isVisible = false
        time.isVisible = true
        time_label.isVisible = true
        percentage.isVisible = true
        percentage_label.isVisible = true
        returnToStart.isVisible = true
        printMDF.isVisible = true
        speed.isVisible = true
        speed_label.isVisible = true
        image_rec.isVisible = true
        status.isVisible = true
        message!!.setVisible(true)
        scrollPane!!.setVisible(true)

        // Add button to the list of buttons
        buttons.add(right)
        buttons.add(left)
        buttons.add(up)
        buttons.add(update)
        buttons.add(checkMap)
        buttons.add(toggleMap)
        buttons.add(resetRobot)
        buttons.add(exploration)
        buttons.add(fastestPath)
        buttons.add(arenaMap)
        buttons.add(waypoint_x)
        buttons.add(waypoint_y)
        buttons.add(time)
        buttons.add(percentage)
        buttons.add(returnToStart)
        buttons.add(printMDF)
        buttons.add(speed)
        buttons.add(image_rec)

        // Add label to the hashmap
        labels["mcLabel"] = mcLabel
        labels["loadMapLabel"] = loadMapLabel
//        labels["robotView"] = robotView
        labels["simulatedMap"] = simulatedMap
        labels["set_waypoint_label"] = set_waypoint_label
        labels["invalid_waypoint"] = invalid_waypoint
        labels["time_label"] = time_label
        labels["percentage_label"] = percentage_label
        labels["speed_label"] = speed_label
        //		Labels.put("image_cap", image_cap);
//		Labels.put("calibrating", calibrating);

        // Disable all button during the real runs as they are not meant to work for the Real Run
        /*if (ConnectionSocket.checkConnection()) {
            disableButtons()
        }*/
    }
}