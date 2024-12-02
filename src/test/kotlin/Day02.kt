import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.math.sign

class Day02 {
    private val sample = """
        7 6 4 2 1
        1 2 7 8 9
        9 7 6 2 1
        1 3 2 4 5
        8 6 4 4 1
        1 3 6 7 9
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.ints() }

    private fun isValid(nums: List<Int>): Boolean {
        val s = (nums[0] - nums[1]).sign
        return nums.zipWithNext { a, b -> a - b }.all { it.sign == s && it.absoluteValue in 1..3 }
    }

    private fun isValid2(nums: List<Int>): Boolean {
        return isValid(nums) || nums.indices.any { isValid(nums.toMutableList().apply { removeAt(it) }) }
    }

    private fun one(input: List<String>) = parse(input).count { isValid(it) }

    private fun two(input: List<String>) = parse(input).count { isValid2(it) }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 2
        one(input) shouldBe 598
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 4
        two(input) shouldBe 634
    }
}

/**
 * Not too bad. For part2, I first tried to optimize by processing lists step by step and allowing one mistake. But
 * this was initially wrong because I did not recompute the descending when the mistake was in the 2nd item. I then
 * thought to first simply try removing a list item.  That worked and was fast enough, and thus I decided to not
 * further optimize.
 */
