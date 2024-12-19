import Part.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.sign

class Day19 {

    private val sample = """
        r, wr, b, g, bwu, rb, gb, br

        brwrr
        bggr
        gbbr
        rrbgbr
        ubwu
        bwurrg
        brgr
        bbrgwb
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.chunkedBy(String::isEmpty).let { (patterns, designs) ->
        patterns[0].split(", ") to designs
    }

    private fun one(input: List<String>): Int = three(input, ONE).toInt()

    private fun two(input: List<String>): Long = three(input, TWO)

    private fun three(input: List<String>, part: Part): Long {
        val (patterns, designs) = parse(input)
        return designs.sumOf { design ->
            val l = design.length
            val counts = MutableList<Long>(l + 1) { 0 }
            counts[0] = 1
            for (i in design.indices) {
                val ci = counts[i]
                if (ci == 0L) continue
                for (p in patterns) {
                    val pi = i + p.length
                    if (pi <= l && design.substring(i, pi) == p) counts[pi] += ci
                }
            }
            counts[l].let { if (part == TWO) it else it.sign.toLong() }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 6
        one(input) shouldBe 300
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 16L
        two(input) shouldBe 624802218898092L
    }
}

/*
Hah, I first struggled with part 1 and solved it with some minor optimizations: eliminate all designs that only consist
of the single-letter towels (because we know that these are possible), and for the rest ignore towels with 3 letters
that are equal to a combined pair of 1-letter and 2-letter towels.  Then I used a recursive function with the resulting
data which ran for about 8 seconds.

I knew that this would not work for part 2, and then I came up with the "count how many combinations reach an index of
a design" idea.  That worked and was much faster than the part 1. I thus then used the same approach for part 1 which
drastically simplified the code.
*/
