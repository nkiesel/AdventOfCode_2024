import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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

    lateinit var orders: List<Pair<Int, Int>>
    lateinit var updates: List<List<Int>>

    private fun parse(input: List<String>) {
        input.map { it.ints() }.chunkedBy(List<Int>::isEmpty).let { (o, u) ->
            orders = o.map { Pair(it[0], it[1]) }
            updates = u
        }
    }

    private fun one(input: List<String>): Int {
        parse(input)
        return updates.filter { isValid(it) }.sumOf { it[it.size / 2] }
    }

    private fun two(input: List<String>): Int {
        parse(input)
        return updates.filterNot { isValid(it) }.map { fixed(it) }.sumOf { it[it.size / 2] }
    }

    private fun isValid(update: List<Int>): Boolean {
        update.forEachIndexed { i, page ->
            val mustBeBefore = orders.filter { it.second == page }.map { it.first }.toSet()
            if (!update.subList(0, i).all { it in mustBeBefore }) return false
        }
        return true
    }

    private fun fixed(update: List<Int>): List<Int> {
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
