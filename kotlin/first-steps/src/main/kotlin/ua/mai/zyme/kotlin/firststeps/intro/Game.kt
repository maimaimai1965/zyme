package ua.mai.zyme.kotlin.firststeps.intro

object Game {
    init {
        println("Welcome!")
    }

    private class GameInput(arg: String?) {
        private val input = arg ?: ""
        val command = input.split(" ")[0]
        val argument = input.split(" ").getOrElse(1) { "" }

        fun processCommand() = when (command.lowercase()) {
                else -> println("I'm not sure what you're trying to do")
            }
    }

    fun play() {
        while (true) {
            println("${player.name} of ${player.hometown} is in")
            print("> Enter your command: ")
            GameInput(readLine()).processCommand()
        }
    }}

fun main() {
//    Game.play()

    println(Coordinate(1, 5).toString())
    val (myExperience, myLevel) = PlayerScore(1250,5)
    println(myExperience + myLevel)
    val (myX, myY) = Coordinate(1, 0)
    println(myX + myY)
    Direction.East
    Direction.North
    Direction.South
    Direction.West

    var currentPosition = Coordinate(5, 2)
    currentPosition = Direction.East.updateCoordinate(currentPosition)
    println(currentPosition)
    Direction.West.name
}

class PlayerScore(val experience: Int, val level: Int) {
    operator fun component1() = experience
    operator fun component2() = level
}


data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(other: Coordinate) =
        Coordinate(x + other.x, y + other.y)
}


enum class Direction(
    private val directionCoordinate: Coordinate
) {
    North(Coordinate(0, -1)),
    East(Coordinate(1, 0)),
    South(Coordinate(0, 1)),
    West(Coordinate(-1, 0));

    fun updateCoordinate(coordinate: Coordinate) =
        coordinate + directionCoordinate
}

sealed class StudentStatus {
    object NotEnrolled : StudentStatus()
    data class Active(val courseId: String) : StudentStatus()
    object Graduated : StudentStatus()
}

@JvmInline
value class Kilometers(private val kilometers: Double) {
    operator fun plus(other: Kilometers) =
        Kilometers(kilometers + other.kilometers)
    fun toMiles() = kilometers / 1.609
}
@JvmInline
value class Miles(private val miles: Double) {
    operator fun plus(other: Miles) =
        Miles(miles + other.miles)
    fun toKilometers() = miles * 1.609
}
