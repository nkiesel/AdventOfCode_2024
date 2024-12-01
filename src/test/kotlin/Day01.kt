import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue

class Day01 {
    private val sample = """
        3   4
        4   3
        2   5
        1   3
        3   9
        3   3
    """.trimIndent().lines()

    private fun parse(input: List<String>): Pair<List<Int>, List<Int>> {
        val left = input.map { it.ints()[0] }
        val right = input.map { it.ints()[1] }
        return left to right
    }

    private fun one(input: List<String>): Int {
        val (left, right) = parse(input)
        return left.sorted().zip(right.sorted()).sumOf { (l, r) -> (l - r).absoluteValue }
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
 */
