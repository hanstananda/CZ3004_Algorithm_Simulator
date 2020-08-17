package constants

const val GOAL_ROW = 18
const val GOAL_COL = 13
const val START_ROW = 1
const val START_COL = 1

enum class DIRECTION {
    // Note: The current implementation is tedious, yet is the most efficient. More discussion needed
    // Alternative: https://stackoverflow.com/questions/609860/convert-from-enum-ordinal-to-enum-type/609879
    NORTH {
        override fun getNext(cur: DIRECTION): DIRECTION = EAST
        override fun getPrev(cur: DIRECTION): DIRECTION = WEST
    },
    SOUTH {
        override fun getNext(cur: DIRECTION): DIRECTION = WEST
        override fun getPrev(cur: DIRECTION): DIRECTION = EAST
    },
    WEST {
        override fun getNext(cur: DIRECTION): DIRECTION = NORTH
        override fun getPrev(cur: DIRECTION): DIRECTION = SOUTH
    },
    EAST {
        override fun getNext(cur: DIRECTION): DIRECTION = SOUTH
        override fun getPrev(cur: DIRECTION): DIRECTION = NORTH
    };

    abstract fun getNext(cur: DIRECTION): DIRECTION
    abstract fun getPrev(cur: DIRECTION): DIRECTION
}

enum class MOVEMENT {
    FORWARD, BACKWARD, RIGHT, LEFT
}