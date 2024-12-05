import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

typealias Order = Pair<Int, Int>
typealias Update = List<Int>

class Day05 {
    private val sample = """
        47|53
        97|13
        97|61
        97|47
        75|29
        61|13
        75|53
        29|13
        97|29
        53|29
        61|53
        97|53
        61|29
        47|13
        75|47
        97|75
        47|61
        75|61
        47|29
        75|13
        53|13

        75,47,61,53,29
        97,61,53,29,13
        75,29,13
        75,97,47,61,53
        61,13,29
        97,13,75,29,47
    """.trimIndent().lines()


    private fun parse(input: List<String>): Pair<List<Order>, List<Update>> {
        val orders = mutableListOf<Order>()
        val updates = mutableListOf<Update>()
        input.forEach { line ->
            when {
                '|' in line -> orders += line.ints().let { Pair(it[0], it[1]) }
                ',' in line -> updates += line.ints()
            }
        }
        return orders to updates
    }

    private fun one(input: List<String>): Int {
        val (orders, updates) = parse(input)
        return updates.filter { isValid(it, orders) }.sumOf { it[it.size / 2] }
    }

    private fun two(input: List<String>): Int {
        val (orders, updates) = parse(input)
        return updates.filterNot { isValid(it, orders) }.map { fixed(it, orders) }.sumOf { it[it.size / 2] }
    }

    private fun isValid(update: Update, orders: List<Order>): Boolean {
        update.forEachIndexed { i, page ->
            val mustBeBefore = orders.filter { it.second == page }.map { it.first }.toSet()
            if (!update.subList(0, i).all { it in mustBeBefore }) return false
        }
        return true
    }

    private fun fixed(update: Update, orders: List<Order>): List<Int> {
        val fixed = mutableListOf<Int>()
        val lastCandidates = update.toMutableSet()
        while (lastCandidates.isNotEmpty()) {
            lastCandidates.first { orders.none { o -> o.first == it && o.second in lastCandidates } }.let {
                fixed += it
                lastCandidates -= it
            }
        }
        return fixed.reversed()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 143
        one(input) shouldBe 7307
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 123
        two(input) shouldBe 4713
    }
}

/**
 * Hmm, getting a bit more complicated. Still thinking my `isValid` can be written much better, but it's working
 * so leaving it for now.
 */
