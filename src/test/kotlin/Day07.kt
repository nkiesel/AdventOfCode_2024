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

    private fun one(input: List<String>): Long {
        return parse(input).filter { line -> valid1(line.first(), line.drop(1)) }.sumOf { line -> line.first() }
    }

    private fun list(a: Long, b: List<Long>) = b.toMutableList().apply { add(0, a) }

    private fun valid1(r: Long, n: List<Long>): Boolean {
        if (n.size == 1) return r == n.first()
        val (a, b) = n
        val tail = n.drop(2)
        return valid1(r, list(a + b, tail)) || valid1(r, list(a * b, tail))
    }

    private fun two(input: List<String>): Long {
        return parse(input).filter { line -> valid2(line.first(), line.drop(1)) }.sumOf { line -> line.first() }
    }

    private fun valid2(r: Long, n: List<Long>): Boolean {
        if (n.size == 1) return r == n.first()
        val (a, b) = n
        val tail = n.drop(2)
        return valid2(r, list(a + b, tail)) || valid2(r, list(a * b, tail)) || valid2(r, list("$a$b".toLong(), tail))
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
 */
