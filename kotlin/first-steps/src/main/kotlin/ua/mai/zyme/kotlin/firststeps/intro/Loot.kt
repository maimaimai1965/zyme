package ua.mai.zyme.kotlin.firststeps.intro

class Loot {
}

class LootBox<T>(var contents: T)

class Fedora(
    val name: String,
    val value: Int
)

class Gemstones(
    val value: Int
)

class Key(
    val name: String
)

fun sub() {
    val lootBoxOne: LootBox<Fedora> =
            LootBox(Fedora("a generic-looking fedora", 15))
    val lootBoxTwo: LootBox<Gemstones> =
            LootBox(Gemstones(150))
    println(lootBoxOne.toString() + lootBoxTwo.toString())

    val lootBoxOne2 = LootBox(Fedora("a genericlooking fedora", 15))
    val lootBoxTwo2 = LootBox(Gemstones(150))

    println(lootBoxOne2.toString() + lootBoxTwo2.toString())
}

