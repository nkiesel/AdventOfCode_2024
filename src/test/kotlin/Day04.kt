import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day04 {
    private val sample = """
        MMMSXXMASM
        MSAMXMSMSA
        AMXSXMAAMM
        MSAMASMSMX
        XMASAMXAMM
        XXAMMXXAMA
        SMSMSASXSS
        SAXAMASAAA
        MAMMMXMMMM
        MXMXAXMASX
    """.trimIndent().lines()

    private fun parse(input: List<String>): List<Set<Point>> {
        val area = CharArea(input)
        val allX = mutableSetOf<Point>()
        val allM = mutableSetOf<Point>()
        val allA = mutableSetOf<Point>()
        val allS = mutableSetOf<Point>()
        area.tiles().forEach {
            when (area[it]) {
                'X' -> allX.add(it)
                'M' -> allM.add(it)
                'A' -> allA.add(it)
                'S' -> allS.add(it)
            }
        }
        return listOf(allX, allM, allA, allS)
    }

    private fun one(input: List<String>): Int {
        val (allX, allM, allA, allS) = parse(input)
        var count = 0
        for (x in allX) {
            for (d in listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)) {
                if (
                    x.move(d.first * 1, d.second * 1) in allM &&
                    x.move(d.first * 2, d.second * 2) in allA &&
                    x.move(d.first * 3, d.second * 3) in allS
                ) count++
            }
        }
        return count
    }

    private fun two(input: List<String>): Int {
        val (_, allM, allA, allS) = parse(input)
        var count = 0
        for (a in allA) {
            val tl = a.move(-1, -1)
            val tr = a.move(1, -1)
            val bl = a.move(-1, 1)
            val br = a.move(1, 1)
            if (
                (tl in allM && br in allS || tl in allS && br in allM) &&
                (tr in allM && bl in allS || tr in allS && bl in allM)
            ) {
                count++
            }
        }
        return count
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 18
        one(input) shouldBe 2554
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 9
        two(input) shouldBe 1916
    }
}

/**
 * This was pretty simple because of my CharArea class.  After writing in TypeScript, I rewrote the code a bit
 * to use explicitly named sets.
 */
