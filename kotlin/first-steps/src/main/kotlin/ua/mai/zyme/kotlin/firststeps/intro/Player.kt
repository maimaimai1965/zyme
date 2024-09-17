package ua.mai.zyme.kotlin.firststeps.intro

class Player(
    initialName: String,
    val hometown: String = "Neversummer",
    var healthPoints: Int,
    val isImmortal: Boolean)
{
    var name =  initialName
        get() = field.replaceFirstChar {
            it.uppercaseChar()
        }
        private set(value) {
            field = value.trim()
        }
//    var title: String
//        get() = ...

    val prophecy by lazy {
        Thread.sleep(3000)
        "The fortune teller bestows a prophecy upon $name"
    }
    init {
        require(healthPoints > 0) { "healthPoints must be greater than zero" }
        require(name.isNotBlank()) { "Player must have a name" }
    }

    constructor(name: String):
            this(
                initialName = name,
                hometown = "",
                healthPoints = 100,
                isImmortal = false
            ){
                if (name.equals("Jason", ignoreCase = true)) {
                    healthPoints = 500
                }
             }
//    ...
}

fun f(): Unit {
    val player = Player(
        initialName = "Madrigal",
        hometown = "Neversummer",
        healthPoints = 40,
        isImmortal = true
    )
    print(player)
}

lateinit var player: Player

fun main() {
//    if (::player.isInitialized) {
//    }
    val player = Player(
        name = " Madrigal "
    )
    var men = Any();

    men is Player

    println(player.prophecy)
//    val villager = Villager(
//        name = "Madrigal",
//        hometown = "London"
//    )

}

class Villager(val name: String,
               val hometown: String) {
    val personality: String
    val race = "Dwarf"
    var age = 50
        private set
    init {
        println("initializing villager")
        personality = "Outgoing"
    }
    constructor(name: String) : this(
        name,
        "Bavaria") {
        age = 99
    }
}

class Arena {
    var isArenaOpen = false
    lateinit var opponentName: String
    fun prepareArena() {
        isArenaOpen = true
        opponentName = getWillingCombatants().random()
    }
    private fun getWillingCombatants() =
        listOf("Cornelius", "Cheryl", "Ralph", "Deborah")
}