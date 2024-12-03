import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day03 {
    private val sample1 = """
                xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
    """.trimIndent().lines()

    private val sample2 = """
                xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))don't()
                xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
    """.trimIndent().lines()

    private fun one(input: List<String>): Int {
        return Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
            .findAll(input.joinToString(""))
            .sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
    }

    private fun two(input: List<String>): Int {
        var enabled = true
        return Regex("""mul\((\d{1,3}),(\d{1,3})\)|do(n't)?\(\)""")
            .findAll(input.joinToString(""))
            .sumOf { m ->
                if (enabled && m.value.startsWith("mul")) {
                    m.groupValues[1].toInt() * m.groupValues[2].toInt()
                } else {
                    enabled = m.value == "do()"
                    0
                }
            }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 161
        one(input) shouldBe 173785482
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 88
        two(input) shouldBe 83158140
    }
}

/**
 * Oh, this was a bit more tricky than I thought. I actually got the code nearly correct within a few minutes, but my
 * answer for part 2 was wrong because I wrote the code to handle every input line independently. Thus, I started with
 * enabled at the beginning of every line, which returned a value which was too high. I finally - after reading the
 * instructions multiple times - realized that it never talked about "sum of lines".
 */
