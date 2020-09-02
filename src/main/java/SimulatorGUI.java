import data.map.MazeMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static utils.map.MapDescriptorKt.loadMapFromDisk;

public class SimulatorGUI {

    private static JFrame f = null;
    private static JPanel m = null;
    private static JPanel b = null;

    private static Robot bot;

    private static MazeMap realMap = null;              // real map
    private static MazeMap exploredMap = null;          // exploration map

    private static final boolean realRun = true;

    public static void main(String[]args){
        if (!realRun) {
            realMap = new MazeMap();
            realMap.setAllUnexplored();
        }

        exploredMap = new MazeMap();
        exploredMap.setAllUnexplored();
        displayMainFrame();
    }

    private static void displayMainFrame(){

        //initialise main frame
        f = new JFrame("MDP Simulator");
        f.setSize(new Dimension(690, 700));
        f.setResizable(false);

        //center main frame
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(dim.width / 2 - f.getSize().width / 2, dim.height / 2 - f.getSize().height / 2);

        //create CardLayout for storing different maps
        m = new JPanel(new CardLayout());

        //create JPanel for buttons
        b = new JPanel();

        //add m & b to the main frame's content pane
        Container contentPane = f.getContentPane();
        contentPane.add(m, BorderLayout.CENTER);
        contentPane.add(b, BorderLayout.PAGE_END);

        initMain();
        initButtons();

        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void initMain() {
//        if (!realRun) {
//            m.add(realMap, "REAL_MAP");
//        }
//        m.add(exploredMap, "EXPLORATION");

        CardLayout cl = ((CardLayout) m.getLayout());
        if (!realRun) {
            cl.show(m, "REAL_MAP");
        } else {
            cl.show(m, "EXPLORATION");
        }
    }

    private static void initButtons() {
        b.setLayout(new GridLayout());
        addButtons();
    }

    private static void formatButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
    }

    private static void addButtons() {
        if (!realRun) {
            // Load Map Button
            JButton btn_LoadMap = new JButton("Load Map");
            formatButton(btn_LoadMap);
            btn_LoadMap.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    JDialog loadMapDialog = new JDialog(f, "Load Map", true);
                    loadMapDialog.setSize(400, 60);
                    loadMapDialog.setLayout(new FlowLayout());

                    final JTextField loadTF = new JTextField(15);
                    JButton loadMapButton = new JButton("Load");

                    loadMapButton.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            loadMapDialog.setVisible(false);
                            loadMapFromDisk(realMap, loadTF.getText());
                            CardLayout cl = ((CardLayout) m.getLayout());
                            cl.show(m, "REAL_MAP");
                            realMap.reset();
                        }
                    });

                    loadMapDialog.add(new JLabel("File Name: "));
                    loadMapDialog.add(loadTF);
                    loadMapDialog.add(loadMapButton);
                    loadMapDialog.setVisible(true);
                }
            });
            b.add(btn_LoadMap);
        }


        // Exploration Button
        JButton btn_Exploration = new JButton("Exploration");
        formatButton(btn_Exploration);
        btn_Exploration.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) m.getLayout());
                cl.show(m, "EXPLORATION");
            }
        });
        b.add(btn_Exploration);

        // Fastest Path Button
        JButton btn_FastestPath = new JButton("Fastest Path");
        formatButton(btn_FastestPath);
        btn_FastestPath.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                CardLayout cl = ((CardLayout) m.getLayout());
                cl.show(m, "EXPLORATION");
            }
        });
        b.add(btn_FastestPath);
    }

}
