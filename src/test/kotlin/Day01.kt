import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.collections.unzip

class Day01 {
    private val sample = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
    """.trimIndent().lines()

    private fun parse(input: List<String>): Pair<List<Int>, List<Int>> = input.map { it.ints().let { it[0] to it[1]} }.unzip()

    private fun one(input: List<String>): Int {
        val (left, right) = parse(input)
        return left.sorted().zip(right.sorted()).sumOf { (l, r) -> l delta r }
    }

    private fun two(input: List<String>): Int {
        val (left, right) = parse(input)
        return left.sumOf { l -> l * right.count { it == l } }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 11
        one(input) shouldBe 1189304
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 31
        two(input) shouldBe 24349736
    }
}

/*
Welcome to AoC 2024!!!

As usual day 1 is very simple. Nothing special.

After implementing in TS, I changed the code from `val data = parse(input)` to `val (left, right) = parse(input)`
which made the code a bit prettier.

Yeah, learned something new from the other Kotlin developers: Kotlin stdlib has an `unzip` function! made the code
even a bit more elegant.

Added `delta` function to Utils.
 */
