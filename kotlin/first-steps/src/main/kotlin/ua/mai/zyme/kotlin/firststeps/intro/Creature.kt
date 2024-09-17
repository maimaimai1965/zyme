package ua.mai.zyme.kotlin.firststeps.intro

import java.io.File
import kotlin.random.Random

fun main() {
//    var f : Fightable = TODO()
//    f.healthPoints
//    f.diceCount
//    f.diceSides
//    f.takeDamage(5)
//
//    var b : Monster = TODO()
//    f.attack()
//    visitTavern()
//    visit()

   val s = generateSequence(0) { it + 1 }
        .onEach { println("The Count says: $it, ah ah ah!") }
    println(s.first())
}

interface Fightable {
    val name: String
    var healthPoints: Int
    val diceCount: Int
    val diceSides: Int
    fun takeDamage(damage: Int)
    fun attack(opponent: Fightable) {
        val damageRoll = (0 until diceCount).sumOf {
            Random.nextInt(diceSides + 1)
        }
        println("$name deals $damageRoll to ${opponent.name}")
        opponent.takeDamage(damageRoll)
    }
}

abstract class Monster(
    override val name: String,
    val description: String,
    override var healthPoints: Int,
) : Fightable {
    override fun takeDamage(damage: Int) {
        healthPoints -= damage
    }
    abstract fun fromWhere()
}

class Player2(
    initialName: String,
    val hometown: String = "Neversummer",
    override var healthPoints: Int,
    val isImmortal: Boolean
) : Fightable {
    override var name = initialName
        get() = field.replaceFirstChar {
            it.uppercaseChar()
        }
        private set(value) {
            field = value.trim()
        }
    override val diceCount = 3
    override val diceSides = 4
    override fun takeDamage(damage: Int) {
        if (!isImmortal) {
            healthPoints -= damage
        }
    }
//    ...
}

fun s01() {
    val menuData = File("data.txt")
            .readText()
            .split("\n")
    val menuItems: List<String> = menuData.map {
            menuEntry: String ->
            val (_, name, _) = menuEntry.split(",")
            name
        }
    println(menuItems)

    val numbers: List<String> = listOf("1.0", "2.0", "3.0")
    val numbersAsDoubles: List<Double> = numbers.map { it.toDouble() }
    println(numbersAsDoubles)
    val numbersAsDoubles2: List<Double> = numbers.map {
        number: String ->
        number.toDouble()
    }

    val menuItems2: List<String> = menuData.map {
            menuEntry: String ->
            val (_, name, _) = menuEntry.split(",")
            name
        }
}

fun s02() {
    val menuData = File("data.txt")
            .readText()
            .split("\n")
    val menuItemPrices = menuData.map {
            menuEntry ->
            val (_, name, price) = menuEntry.split(",")
            name to price.toDouble()
        }.toMap()
    println(menuItemPrices)

    val menuItemPrices2 = menuData.associate {
            menuEntry ->
            val (_, name, price) = menuEntry.split(",")
            name to price.toDouble()
        }
    println(menuItemPrices2)
}

fun s03() {
    val menuData:List<List<String>> = File("data.txt")
            .readText()
            .split("\n")
            .map { it.split(",") }
    val menuItems = menuData.map { (_, name, _) -> name }
    val menuItemPrices = menuData.associate {
            (_, name, price) ->
            name to price.toDouble()
        }
    println(menuItems + menuItemPrices)

}

fun visitTavern() {
    var firstNames = listOf("Elli", "Doroty", "Hamela")
    var lastNames = listOf("Peppy", "Spark")

    val patrons: MutableSet<String> =
        firstNames
            .zip(lastNames) {
                    firstName, lastName -> "$firstName $lastName"

             }
            .toMutableSet()
    println(patrons)

    val patronGold = mutableMapOf(
        "Serg" to 86.00,
        "Mike" to 4.50,
        *patrons.map { it to 6.00}.toTypedArray()
    )
    println(patronGold)

    val patrons2 : Map<String, String> =
        firstNames
            .zip(lastNames) {
                    firstName, lastName -> firstName to lastName

            }
            .toMap()
    println(patrons2)
}

fun visit() {
    val patrons: MutableSet<String> = mutableSetOf("Elli", "Doroty")
    val patronGold = mutableMapOf(
        "Serg" to 86.00,
        "Mike" to 4.50,
        *patrons.map { it to 6.00}.toTypedArray()
    )
    println(patronGold)
}




































