package ua.mai.zyme.kotlin.firststeps

import java.io.File

class Player() {
    constructor(saveFileBytes: ByteArray) :
            this()
    companion object {
        private const val SAVE_FILE_NAME = "player.dat"
        fun fromSaveFile() =
            Player(File(SAVE_FILE_NAME).readBytes())
    }
}

fun main() {
    var player = Player.fromSaveFile();
}