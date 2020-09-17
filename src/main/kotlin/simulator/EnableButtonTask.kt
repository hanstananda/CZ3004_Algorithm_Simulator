package simulator

import java.util.*
import java.util.concurrent.TimeUnit


class EnableButtonTask // This class enables all the button after the movement
(private val AL: AddJButtonActionListener) : TimerTask() {
    override fun run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: Exception) {
                println("Exception in EnableButtonTask.java")
            }
        }
        AL.enableButtons()
    }

}