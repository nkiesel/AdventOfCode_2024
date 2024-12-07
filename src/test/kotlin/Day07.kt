import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day07 {
    private val sample = """
        190: 10 19
        3267: 81 40 27
        83: 17 5
        156: 15 6
        7290: 6 8 6 15
        161011: 16 10 13
        192: 17 8 14
        21037: 9 7 18 13
        292: 11 6 16 20
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.longs() }

    private fun one(input: List<String>): Long = three(input, true)

    private fun two(input: List<String>): Long = three(input, false)

    private fun three(input: List<String>, part1: Boolean): Long =
        parse(input).filter { valid(it.first(), it.drop(1), part1) }.sumOf { it.first() }

    private fun valid(r: Long, n: List<Long>, part1: Boolean): Boolean {
        val a = n[0]
        if (n.size == 1) return r == a
        if (a > r) return false
        val b = n[1]
        val heads = if (part1) listOf(a + b, a * b) else listOf(a + b, a * b, "$a$b".toLong())
        val tail = n.drop(1).toMutableList()
        return heads.any { valid(r, tail.apply { tail[0] = it }, part1) }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 3749L
        one(input) shouldBe 2664460013123L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 11387L
        two(input) shouldBe 426214131924213L
    }
}

/*
This was pretty simple. I first thought I would have to use a tree walk, but the simple recursive solution was
working properly. Only other issue was that I first used Int instead of Long.

This could be further optimized by passing the index of the list of values from where to start working on instead of
creating copies of the list, but code runs in a second so no need to make it more complicated.
*/
