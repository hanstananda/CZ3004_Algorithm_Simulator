/*
package utils.map

import simulator.SimulatorTest
import java.awt.Image
import javax.swing.ImageIcon
import javax.swing.JOptionPane

private var realRun = false
private var simulate = false

fun main(args: Array<String>) {
//    val icon = ImageIcon(ImageIcon(Constant.DIALOGICONIMAGEPATH).getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT))
    var result = JOptionPane.CLOSED_OPTION
    var debug = JOptionPane.CLOSED_OPTION
    var simulator = JOptionPane.CLOSED_OPTION
    while (result == JOptionPane.CLOSED_OPTION) {
        result = JOptionPane.showConfirmDialog(null, "Is this the real run?", "Real Run", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)
        if (result == JOptionPane.YES_OPTION) {
            realRun = true
            debug = JOptionPane.showConfirmDialog(null, "Print debug?", "Debug", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)
            while (debug == JOptionPane.CLOSED_OPTION) {
                debug = JOptionPane.showConfirmDialog(null, "Print debug?", "Debug", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)
            }
            simulator = JOptionPane.showConfirmDialog(null, "Show Simulator?", "Simulator", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)
            while (simulator == JOptionPane.CLOSED_OPTION) {
                simulator = JOptionPane.showConfirmDialog(null, "Show Simulator?", "Simulator", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)
            }
            if (simulator == JOptionPane.YES_OPTION) {
                simulate = true
            }
        }
        if (result == JOptionPane.NO_OPTION) {
            realRun = false
        }
    }
    if (realRun) {

    } else {
        val s = SimulatorTest()
    }
}
*/
