package ua.mai.zyme.kotlin.firststeps.intro2

abstract class Loot {
    abstract val name: String
}

interface Sellable {
    val value: Int
}

class Fedora(
        override val name: String,
        override val value: Int,
        val discount: Int
) : Loot(), Sellable

class Gemstones(
        override val value: Int
) : Loot(), Sellable {
    override val name = "sack of gemstones worth $value gold"
}

class Key(
        override val name: String
) : Loot()

class LootBox<out T: Loot>(val contents: T)

//val lootBox: LootBox<Loot> =
//        LootBox(Fedora("a dazzling fuchsia fedora", 15))
//val fedora: Fedora = lootBox.contents  // Несоответствие типов.
//                                       // Требуется Fedora, обнаружен Loot

class DropOffBox<in T> where T : Loot, T : Sellable {
    fun sellLoot(sellableLoot: T): Int {
        return (sellableLoot.value * 0.7).toInt()
    }
}

fun sell() {
    val hatDropOffBox = DropOffBox<Fedora>()
    hatDropOffBox.sellLoot(Fedora("a sequin-covered fedora", 20, 10))
//    hatDropOffBox.sellLoot(Gemstones(100))
}

fun f01() {
    var fedoraBox: LootBox<Fedora> =
            LootBox(Fedora("a generic-looking fedora", 15, 30))
    var lootBox: LootBox<Loot> = LootBox(Gemstones(150))
    lootBox = fedoraBox
    fedoraBox.contents.discount;
    lootBox.contents;
}
