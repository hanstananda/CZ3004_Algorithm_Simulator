package constants

object RobotConstants {

    const val GOAL_ROW = 18
    const val GOAL_COL = 13
    const val START_ROW = 1
    const val START_COL = 1
    const val SENSOR_SHORT_RANGE_L = 1 // range of short range sensor (cells)
    const val SENSOR_SHORT_RANGE_H = 2 // range of short range sensor (cells)
    const val SENSOR_LONG_RANGE_L = 3 // range of long range sensor (cells)
    const val SENSOR_LONG_RANGE_H = 5
    val START_DIR = DIRECTION.NORTH
    const val DELAY = 100
    val FRONT_SENSOR_IDS = setOf("IRS_FL", "IRS_FM", "IRS_FR")

    enum class DIRECTION {
        // Note: The current implementation is tedious, yet is the most efficient. More discussion needed
        // Alternative: https://stackoverflow.com/questions/609860/convert-from-enum-ordinal-to-enum-type/609879
        NORTH {
            override fun print() = 'N'
        },
        EAST {
            override fun print() = 'E'
        },
        SOUTH {
            override fun print() = 'S'
        },
        WEST {
            override fun print() = 'W'
        };

        abstract fun print(): Char
        companion object {
            private val directionValues: Array<DIRECTION> = values()
            fun getNext(cur: DIRECTION): DIRECTION = directionValues[(cur.ordinal + 1) % directionValues.size]
            fun getPrev(cur: DIRECTION): DIRECTION =
                    directionValues[(cur.ordinal + directionValues.size - 1) % directionValues.size]
        }
    }

    enum class MOVEMENT {
        FORWARD {
            override fun print(cur: MOVEMENT): Char = 'F'
        },
        BACKWARD {
            override fun print(cur: MOVEMENT): Char = 'B'
        },
        RIGHT {
            override fun print(cur: MOVEMENT): Char = 'R'
        },
        LEFT {
            override fun print(cur: MOVEMENT): Char = 'L'
        };

        abstract fun print(cur: MOVEMENT): Char
    }

}

