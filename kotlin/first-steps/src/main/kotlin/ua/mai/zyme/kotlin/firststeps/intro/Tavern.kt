package ua.mai.zyme.kotlin.firststeps.intro

import java.io.File

private const val TAVERN_MASTER = "Taernyl"
private const val TAVERN_NAME = "$TAVERN_MASTER's Folly"

fun main() {
//    f01()
//    f02()
//    f03()
//    f04()
//    f05()
//    f06()
//    f07_setOf()
//    f08_MutableSey_add()
//    f09_MutableSey_remove()
//    f10_IntArray()
//    f11_List()
//    f12_return()
//    f13_return_label()
//    f15_map()
//    f19_map_element()
//    f24_map_putAll()
//    f29_List_to_Map()
//    f30_map_forEach()
    f38()
}

fun f01() {
    val heroName: String = "Duke"
    var modifier = { message: String -> "Wow $message"}

    fun narrate(message: String) {
        println(modifier(message))
    }

    fun promptHeroName(): String {
        narrate("A hero enters the town of Kronstadt. What is their name?")
                 { message -> "Wow $message" }
        println("Madrigal")
        return "Madrigal"
    }

    fun visitTavern() {
        narrate("$heroName enters $TAVERN_NAME")
        val patrons: List<String> = listOf("Eli", "Mordoc", "Sophie")
        println("Patrons: $patrons")
    }

    promptHeroName()
    visitTavern()

    fun visitTavern2() {
        narrate("$heroName enters $TAVERN_NAME")
        val patrons = listOf("Eli", "Mordoc", "Sophie")
        println("Patron: " + patrons[2])
    }
    visitTavern2()
}

fun f02() {
    val patrons = listOf("Eli", "Mordoc", "Sophie")
    println(patrons.getOrElse(4){ "Empty"})
    println(patrons.getOrNull(4)?: "Empty")
}

fun f03() {
    val patrons = listOf("Eli", "Mordoc", "Sophie")
    for (patron in patrons) {
        println("Good evening, $patron")
    }

    // Выводит имя каждого посетителя
    for (i in 0 until patrons.size) {
        println(patrons[i])
    }
    // Выводит имена посетителей через одного в обратном порядке
    for (i in patrons.size - 1 downTo 0 step 2) {
        println(patrons[i])
    }
}

fun f04() {
    val patrons = listOf("Eli", "Mordoc", "Sophie")

    patrons.forEach { patron ->
        println("Good evening, $patron")
    }

    patrons.forEachIndexed { index, patron ->
        println("Good evening, $patron - you're #${index + 1} in line")
    }
}

fun f05() {
    val patrons = listOf("Eli", "Mordoc", "Sophie")

    val data = File("C:/work/idea_home/zyme/kotlin/first-steps/src/main/resources/data.txt")
        .readText()
        .split("\n")

    data.forEachIndexed { index, row ->
        println("$index ROW: $row")
    }
}

fun f06() {
    val (id0, id1, id2) = listOf(22, 25, 28)
    println("$id0 $id1 $id2")

    val (_, idСurr, _) = listOf(22,25, 28)
    println(idСurr)

    val itemData = "окунь, рыба, река"
    val (name, type, area) = itemData.split(",")
    println("'$name' '$type' '$area'")
}

fun f07() {
    val itemData = listOf("окунь, рыба, река",
                          "пингвин, птица, льды",
                          "олень, млекопитающее, лес")

    val areaList = List(itemData.size) { index ->
        val (name, type, area) = itemData[index].split(",")
            area
    }
    println(areaList)
}

fun f07_MutableSey_setOf() {
    val planets = setOf("Mercury", "Venus", "Earth")
    println(planets)
}

fun f08_MutableSey_add() {
    val planets = mutableSetOf("Mercury")
    planets.add("Venus")
    planets += "Earth"

    println(planets)
}

fun f09_MutableSey_remove() {
    val planets = mutableSetOf("Mercury", "Venus", "Earth")
    planets.remove("Venus")
    planets -= "Earth"

    println(planets)
}

fun f10_IntArray() {
    val ar = intArrayOf(22, 44, 11)
    val i = ar[0]

    println(ar.asList())
}

fun f11_List() {
    val x = listOf(mutableListOf(1, 2, 3))
    val y = listOf(mutableListOf(1, 2, 3))
    x[0].add(4)
    println(x == y)

    var myList: List<Int> = listOf(1, 2, 3)
    (myList as MutableList)[2] = 1000
    println(myList)
}

fun f12_return() {
    fun printConsonants() {
        ('a'..'z').forEach { letter ->
            if ("aeiou".contains(letter)) {
                return
            }
            print(letter)
        }}
    printConsonants()
}

fun f13_return_label() {
    fun printConsonants() {
        ('a'..'z').forEach letters@{ letter ->
            if ("aeiou".contains(letter)) {
                return@letters
            }
            print(letter)
        }}
    printConsonants()
}

fun f14_loop_label() {
    prefixLoop@for(prefix in listOf("alpha", "beta")) {
        var number = 0
        numbersLoop@while (number < 10) {
            val identifier = "$prefix $number"
            if (identifier == "beta 3") {
                break@prefixLoop
            }
            number++
        }
    }
}

fun f15_map() {
    val patronGold = mapOf(
        "Mike" to 86.00,
        "Frida" to 4.50
    )
    print(patronGold)
}
fun f16_map() {
    val patronGold: Map<String, Double>  = mapOf(
        "Mike" to 86.00,
        "Frida" to 4.50
    )
    print(patronGold)
}
fun f17_map() {
    val patronGold: Map<String, Double>  = mapOf(
        "Mike".to(86.00),
        "Frida".to(4.50)
    )
    print(patronGold)
}
fun f18_map() {
    val patronGold = mapOf(
        Pair("Mike", 86.00),
        Pair("Frida", 4.50)
    )
    print(patronGold)
}

fun f19_map_element() {
    val patronGold = mapOf(
        "Mike" to 86.00,
        "Frida" to 4.50
    )
    println(patronGold["Mike"])
    println(patronGold["Eli"])
}

fun f20_map_set() {
    val patronGold = mutableMapOf("Mordoc" to 6.0)
    patronGold["Mordoc"] = 5.0
}

fun f21_map_add() {
    val patronGold = mutableMapOf("Mordoc" to 6.0)
    patronGold += "Eli" to 5.0
}

fun f22_map_add() {
    val patronGold = mutableMapOf("Mordoc" to 6.0)
    patronGold += mapOf("Eli" to 7.0,
                        "Mordoc" to 1.0,
                        "Sophie" to 4.5
                  )
//    { Mordoc=1.0, Eli=7.0, Sophie=4.5}
}

fun f23_map_put() {
    val patronGold = mutableMapOf("Mordoc" to 6.0)
    patronGold.put("Mordoc", 5.0)
//    {Mordoc=5.0}
}

fun f24_map_putAll() {
    val patronGold = mutableMapOf("Mordoc" to 6.0)
    patronGold.putAll(listOf("Jebediah" to 5.0,
                             "Sahara" to 6.0)
    )
    println(patronGold)
// {Mordoc=6.0, Jebediah=5.0, Sahara=6.0}
}

fun f25_map_getOrPut() {
    val patronGold = mutableMapOf<String, Double>()
    patronGold.getOrPut("Randy"){5.0}
//  return 5.0

    patronGold.getOrPut("Randy"){10.0}
//  return 5.0
}

fun f26_map_remove() {
    val patronGold = mutableMapOf("Mordoc" to 5.0)
    val mordocBalance = patronGold.remove("Mordoc")
    print(mordocBalance)
//    5.0
}

fun f27_map_remove() {
    val patronGold = mutableMapOf("Mordoc" to 6.0,
                                  "Jebediah" to 1.0,
                                  "Sophie" to 8.0,
                                  "Tariq" to 4.0)
    patronGold -= listOf("Mordoc", "Sophie")
// {Jebediah=1.0, Tariq=4.0}
}

fun f28_map_remove() {
    mutableMapOf("Mordoc" to 6.0,
                 "Jebediah" to 1.0
    ).clear()
//    {}
}

fun f29_List_to_Map() {
    val itemData = listOf("окунь, рыба, река",
                          "пингвин, птица, льды",
                          "олень, млекопитающее, лес")
    val menuItem: Map<String, String> =
        List(itemData.size) { index ->
            val (_, type, area) = itemData[index].split(",")
            type to area
        }.toMap()
    println(menuItem)
}

fun f30_map_forEach() {
    val patronGold = mutableMapOf("Mordoc" to 6.0,
                                  "Jebediah" to 1.0,
                                  "Tariq" to 4.0)
    patronGold.forEach{ (name, sum) ->
        println("$name has ${"%.2f".format(sum)} euro")
    }
// Mordoc has 6,00 euro
// Jebediah has 1,00 euro
// Tariq has 4,00 euro
}

fun f30_apply() {
    val patrons: MutableList<String> = mutableListOf()
    patrons.add("Sidney")
    patrons.add("Janet")
    if (patrons.contains("Janet")) {
        patrons.add("Hal")
    }
    val guestList: List<String> = patrons.toList()

    val guestList2: List<String> =
            mutableListOf<String>().apply{
                add("Sidney")
                add("Janet")
                if (contains("Janet")) {
                    add("Hal")
                }
            }.toList()
}

fun f31_let() {
    val patrons: List<String> = listOf("Sidney", "Janet", "Hal")

    val friendlyPatron = patrons.first()
    val greeting = "$friendlyPatron walks overto Madrigal and says, Hi! I'm $friendlyPatron. Welcome!"

    val greeting2 = patrons.first().let {
        "$it walks over to Madrigal and says, Hi! I'm $it. Welcome!"
    }
}

fun f32_let() {
    val patrons: List<String> = listOf()

    val friendlyPatron = patrons.firstOrNull()
    val greeting = if (friendlyPatron != null) {
        "$friendlyPatron walks over to Madrigal and says, Hi! I'm $friendlyPatron. Welcome!"
    } else {
        "Nobody greets Madrigal because the tavern is empty"
    }

    val greeting2 = patrons.firstOrNull()?.let {
        "$it walks over to Madrigal and says, Hi! I'm $it. Welcome!"
    } ?: "Nobody greets Madrigal because the tavern is empty"
}

fun f33_run() {
    val healthPoints = 90
    val healthStatus = run {
        if (healthPoints == 100) "perfect health"
        else "has injuries"
    }
}

fun f34_with() {
    val nameTooLong = with("Polarcubis, Supreme Master of NyetHack") {
            length >= 20
    }
}

fun f35_also() {
    var fileContents: List<String>
    File("file.txt")
        .also { print(it.name) }
        .readLines()
        .also { fileContents = it }
}

fun f36_takeIf() {
    val file = File("myfile.txt")
    val fileContents = if (file.exists()) {
        file.readText()
    } else {
        null
    }

    val fileContents2 = File("myfile.txt")
        .takeIf { it.exists() }
        ?.readText()
}

fun f37_takeUnless() {
    val file = File("myfile.txt")
    val fileContents = if (file.exists()) {
        file.readText()
    } else {
        null
    }

    val fileContents2 = File("myfile.txt")
        .takeUnless { it.isHidden}
        ?.readText()
}

fun f38() {
    val patrons: MutableList<String> = mutableListOf("Sidney", "Janet", "Hal", "Jeb")
    val patronGold = mutableMapOf("Janet" to 6.0,
                                  "Jeb" to 1.0,
                                  "Tariq" to 4.0)

    val departingPatrons: List<String> = patrons.filter { patron ->
            patronGold.getOrDefault(patron, 0.0) < 4.0
        }
    patrons -= departingPatrons
    patronGold -= departingPatrons

    departingPatrons.forEach { patron ->
        println("$patron departing the tavern")
    }
    patrons.forEach { patron ->
        println("$patron staying in the tavern")
    }

}
