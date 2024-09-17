package ua.mai.zyme.kotlin.firststeps.intro

open class Room(val name: String) {
    protected open val status = "Calm"
    fun description() = name
    open fun enterRoom() {
        println("There is nothing to do here")
    }
}

open class TownSquare : Room("The Town Square") {
    override val status = "Bustling"
    override fun enterRoom() {
        println("The villagers rally and cheer as the hero enters")
    }
}

val abandonedTownSquare = object : TownSquare() {
    override fun enterRoom() {
        println("The hero anticipated applause, but no one is here...")
    }
}


fun printInfo(item: Any) {
    val isPlaced: Boolean =
          if (item is Player) {
              item.hometown == "Monaco"
          } else if (item is Room) {
              item.name == "Calm"
          } else {
              false
          }
    println(isPlaced)
}
//class T : TownSquare("The Town Square")
fun main() {
    var townSquare = TownSquare();
    var className: String = when (townSquare) {
        is TownSquare -> "TownSquare"
        is Room -> "Room"
        else -> throw IllegalArgumentException()
    }
    println(className)
    townSquare.apply {  }

}