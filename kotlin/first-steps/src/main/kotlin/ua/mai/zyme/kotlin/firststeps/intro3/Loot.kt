package ua.mai.zyme.kotlin.firststeps.intro3

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

class LootBox<out T : Loot>(val contents: T) {
    var isOpen = false
        private set
    fun takeLoot(): T? {
        return contents.takeIf { !isOpen }
                       .also { isOpen = true }
    }
    inline fun <reified U> takeLootOfType(): U? {
        return if (contents is U) {
            takeLoot() as U
        } else {
            null
        }
    }
}

var lootBox: LootBox<Loot> = LootBox(Gemstones(150))
val loot = lootBox.takeLootOfType<Fedora>()

class DropOffBox<in T> where T : Loot, T : Sellable {
    fun sellLoot(sellableLoot: T): Int {
        return (sellableLoot.value * 0.7).toInt()
    }
}

fun sell() {
    loot
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
