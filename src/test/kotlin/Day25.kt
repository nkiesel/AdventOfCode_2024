import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day25 {
    private val sample = """
        #####
        .####
        .####
        .####
        .#.#.
        .#...
        .....

        #####
        ##.##
        .#.##
        ...##
        ...#.
        ...#.
        .....

        .....
        #....
        #....
        #...#
        #.#.#
        #.###
        #####

        .....
        .....
        #.#..
        ###..
        ###.#
        ###.#
        #####

        .....
        .....
        .....
        #....
        #.#..
        #.#.#
        #####
    """.trimIndent().lines()

    private fun parse(input: List<String>): Pair<List<List<Int>>, List<List<Int>>> {
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()
        for (block in input.chunkedBy(String::isEmpty)) {
            val isLock = block[0] == "#####"
            val nums = MutableList(5) { 0 }
            for (line in if (isLock) block.drop(1) else block.dropLast(1)) {
                line.forEachIndexed { idx, c -> if (c == '#') nums[idx]++ }
            }
            (if (isLock) locks else keys) += nums
        }
        return locks to keys
    }

    private fun one(input: List<String>): Int {
        val (locks, keys) = parse(input)
        return locks.sumOf { lock -> keys.count { key -> lock.zip(key).all { (l, k) -> l + k < 6 } } }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 3
        one(input) shouldBe 3107
    }
}

/*
As expected, Day 25 was among the simplest of all days.
*/
