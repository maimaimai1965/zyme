package ua.mai.zyme.kotlin.firststeps.intro

fun String.addEnthusiasm(enthusiasmLevel: Int = 1) =
    this + "!".repeat(enthusiasmLevel)


//fun Any.print(): Any {
//    println(this)
//    return this
//}

fun <T> T.print(): T {
    print(this)
    return this
}

operator fun List<List<Room>>.get(coordinate: Coordinate) =
                 getOrNull(coordinate.y)?.getOrNull(coordinate.x)
infix fun Coordinate.move(direction: Direction) =
                 direction.updateCoordinate(this)

val String.numVowels
    get() = count { it.lowercase() in "aeiou" }


fun Room?.orEmptyRoom(name: String = "the middle of nowhere"): Room =
              this ?: Room(name)

fun main() {
    println("Error".numVowels)
}

