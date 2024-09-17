package ua.mai.zyme.kotlin.firststeps.intro;

import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
//    val name = "Kotlin"
//    println("Hello, $name!")
//
//    var playerLevel: Int = 4
//
//    var quest: String? = "Rescue the princess"
//    quest = null
//    quest = quest!!.replace("princess", "prince")
//    ffa()
//    ffb()
//    ffc()
//    ffd()
//    ffe()
//    fff()
//    f010()
//    f011()
//    f012()
//    f014()
//    f016()
//    f017()
//    f019()
//    f020()
//    f021()
//    f023()
//    f024()
//    f027()
    f028()
}

private fun obtainQuest(playerLevel: Int): String? = when (playerLevel) {
    1 -> "Meet Mr. Bubbles in the land of soft things."
    2 -> "Defeat Nogartse, bringer of death and eater of worlds."
    else -> null
}

private fun readBountyBoard() {
}

private fun ff() {
    val str: String? = "ssss"
    if (str != null) {
        str.replace("password", "xxxxxxxx")
    }
}

private fun ffa() {
    val quest: String? = "password Yes"
    val censoredQuest: String? =
        quest?.replace("password", "xxxxxxxx")
    println("quest = $censoredQuest")
}

private fun ffb() {
//    val quest: String? =  "password Yes"
    val quest: String? =  null
    val message: String? = quest?.let {
                str -> str.replace("password", "xxxxxxxx")
            } ?: "Empty"
    println("message = '$message'")
}

private fun ffc() {
//    val quest: String? =  "password Yes"
    val quest: String? =  null
    val message: String? = quest ?: "Empty"
    println("message = '$message'")
}

val HERO_NAME: String  = "mai"

private fun ffd() {
    println("$HERO_NAME announces herpresence to the world.")
    println("What level is $HERO_NAME?")
    val playerLevel: Int = readLine()!!.replace("[^0-9]".toRegex(), "").toInt()
    println("$HERO_NAME's level is $playerLevel.")
}

private fun ffe() {
    println("$HERO_NAME announces herpresence to the world.")
    println("What level is $HERO_NAME?")
    try {
        val playerLevel = readLine()?.toIntOrNull() ?: 1
        if (playerLevel <= 0) {
            throw IllegalArgumentException("The player's level must be at least 1.")
        }
        println("$HERO_NAME's level is $playerLevel.")
    } catch (e: IllegalArgumentException) {
        println("$HERO_NAME can't read what's on the bounty board.")
    }
}

private fun fff() {
    println("$HERO_NAME announces herpresence to the world.")
    println("What level is $HERO_NAME?")
    val message: String = try {
            val playerLevel = readLine()?.toIntOrNull() ?: 1
            if (playerLevel <= 0) {
                throw IllegalArgumentException("The player's level must be at least 1.")
            }
            "$HERO_NAME's level is $playerLevel."
        } catch (e: IllegalArgumentException) {
            "$HERO_NAME can't read what's on the bounty board."
        }
    println(message)
}

private fun f010() {
    println("$HERO_NAME announces herpresence to the world.")
    println("What level is $HERO_NAME?")
    val message: String = try {
        val playerLevel = readLine()?.toIntOrNull() ?: 1
        require(playerLevel > 0) {
            "The player's level must be at least 1."
        }
        "$HERO_NAME's level is $playerLevel."
    } catch (e: IllegalArgumentException) {
        "$HERO_NAME can't read what's on the bounty board."
    }
    println(message)

    val input: String = checkNotNull(readLine()) {
                           // Выдает IllegalStateException ссообщением
                           "No input was provided No input was provided"
                        }

}

class InvalidPlayerLevelException(): IllegalArgumentException("Invalid player level (must be at least 1).")

private fun f011() {
    var count: Int =  "Mississippi".count({ letter -> letter == 's' })
    println(count)
}

private fun f012() {
    var message: String = "Hello";
    println(
        {
            val numExclamationPoints = 3
            message.uppercase() + "!".repeat(numExclamationPoints)
        }())
}

private fun f014() {
    var message: String = "Hello";
    var neMessage: String =
        {   val numExclamationPoints = 3
            message.uppercase() + "!".repeat(numExclamationPoints)
        }();

    var i: Int = { 7 }();
}

private fun f015() {
    var message: String = "Hello";
    val narrationModifier: () -> String = {
            val numExclamationPoints = 3
            message.uppercase() +
                "!".repeat(numExclamationPoints)
        }
    println(narrationModifier()) // Вызов лямбда-функции
}

private fun f016() {
    val narrationModifier: (String, Int) -> String = {
                            message, numExclamationPoints ->
            message.uppercase() + "!".repeat(numExclamationPoints)
         }
    println(narrationModifier("Hello", 4)) // Вызов лямбда-функции
}

private fun f017() {
    val narrationModifier: (String) -> String = {
        it.uppercase() + "!".repeat(3)
    }
    println(narrationModifier("Hello")) // Вызов лямбда-функции
}

private fun f018() {
    val message = "Hello";
    val narrationModifier: () -> String = {
        message.uppercase() + "!".repeat(3)
    }
    println(narrationModifier()) // Вызов лямбда-функции

    val narrationModifier2 = {
        message.uppercase() + "!".repeat(3)
    }
    println(narrationModifier2()) // Вызов лямбда-функции

    val narrationModifier3 = {
            message: String, numExclamationPoints: Int ->
        message.uppercase() + "!".repeat(numExclamationPoints)
    }
    println(narrationModifier3("Hello", 4)) // Вызов лямбда-функции
}

private fun f019() {
    var narrationModifier: (String) -> String = { it }

    fun narrate(message: String) {
        println(narrationModifier(message))
    }

    fun changeNarratorMood(moodId: Int) {
        val mood: String
        val modifier: (String) -> String
        when (moodId) {
            1 -> {
                mood = "loud"
                modifier = { message ->
                    message.uppercase() + "!".repeat(3)
                }
            }
            2 -> {
                mood = "tired"
                modifier = { message ->
                    message.lowercase().replace(" ","...")
                }
            }
            3 -> {
                mood = "unsure"
                modifier = { message ->
                    "$message?"
                }
            }
            else -> {
                mood = "professional"
                modifier = { message ->
                    "$message."
                }
            }
        }
        narrationModifier = modifier
        narrate("** The narrator begins to feel $mood")
    }

    val begin = "Begin story"
    val end = "End story"

    narrate(begin)
    narrate(end)

    for (id in 1..4) {
        changeNarratorMood(id)
        narrate(begin)
        narrate(end)
    }
}

private fun f020() {
    var narrationModifier: (String) -> String = { it }

    fun narrate(message: String, modifier: (String) -> String = narrationModifier) {
        println(modifier(message))
    }

    val begin = "Begin story"
    val end = "End story"

    narrate(begin)
    narrate(end)

    var myModifier = { message: String ->
        "Wow! $message."
    }
    narrate(begin, myModifier)
    narrate(end, myModifier)
}

private fun f021() {
    fun narrate(message: String, modifier: (String) -> String) {
        println(modifier(message))
    }
    val begin = "Begin story"

    narrate(begin, { message -> "\u001b[33;1m$message\u001b[0m"})
    // То же самое:
    narrate(begin) { message -> "\u001b[33;1m$message\u001b[0m" }

}

private fun f022() {
    "Mississippi".count ({ it == 's' })
    // То же самое:
    "Mississippi".count { it == 's' }
}

inline fun narrate(message: String, modifier: (String) -> String) {
    println(modifier(message))
}
private fun f023() {
    val begin = "Begin story"
    narrate(begin, { message -> "\u001b[33;1m$message\u001b[0m"})
}

private fun f024() {
    val heroName = readLine()
    require(heroName != null && heroName.isNotEmpty())
            { "The hero must have a name." }
    // То же самое:
    require(heroName != null && heroName.isNotEmpty(),
            { -> "The hero must have a name." })
}

private fun f025() {
    fun createTitle(name: String): String {
        return when {
            name.count { it.lowercase() in "aeiou" } > 4
            -> "The Master of Vowel"

            else -> "The Renowned Hero"
        }
    }
}

private fun f026() {
    fun createTitle2(name: String): String {
        return when {
            name.all { it.isDigit() }
                 -> "The Identifiable"
            name.none { it.isLetter() }
                 -> "The Witness Protection Member"
            name.count { it.lowercase() in "aeiou" } > 4
                 -> "The Master of Vowels"
            else -> "The Renowned Hero"
        }
    }
}

private fun makeWow(message: String) = "Wow $message"
private fun f027() {
    fun narrate(message: String, modifier: (String) -> String) {
        println(modifier(message))
    }
    narrate("Mike", ::makeWow)
}

private fun f028() {
    fun narrate(message: String, modifier: (String) -> String) {
        println(modifier(message))
    }

    var narrationsGiven = 0
    val modifier: (String) -> String = { message ->
        narrationsGiven++
        "$message. (I have narrated $narrationsGiven things)"
    }
    narrate("Short story", modifier)
    narrate("Tiny story", modifier)
}












