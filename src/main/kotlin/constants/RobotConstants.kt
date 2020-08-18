package constants

const val GOAL_ROW = 18
const val GOAL_COL = 13
const val START_ROW = 1
const val START_COL = 1

enum class DIRECTION {
    // Note: The current implementation is tedious, yet is the most efficient. More discussion needed
    // Alternative: https://stackoverflow.com/questions/609860/convert-from-enum-ordinal-to-enum-type/609879
    NORTH {
        override fun print(cur: DIRECTION) = 'N'
    },
    SOUTH {
        override fun print(cur: DIRECTION) = 'S'
    },
    WEST {
        override fun print(cur: DIRECTION) = 'W'
    },
    EAST {
        override fun print(cur: DIRECTION) = 'E'
    };

    private val directionValues: Array<DIRECTION> = values()
    abstract fun print(cur: DIRECTION): Char
    fun getNext(cur: DIRECTION): DIRECTION = directionValues[(cur.ordinal + 1)% directionValues.size]
    fun getPrev(cur: DIRECTION): DIRECTION = directionValues[(cur.ordinal + directionValues.size -1 )% directionValues.size]
}

enum class MOVEMENT {
    FORWARD, BACKWARD, RIGHT, LEFT
}