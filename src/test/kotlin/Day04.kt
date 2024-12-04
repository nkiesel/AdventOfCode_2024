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
        return allX.sumOf { x ->
            Point(0, 0).neighbors8().count { d ->
                x.move(d, 1) in allM && x.move(d, 2) in allA && x.move(d, 3) in allS
            }
        }
    }

    private fun two(input: List<String>): Int {
        val (_, allM, allA, allS) = parse(input)
        return allA.count { a ->
            val (tl, bl, tr, br) = a.neighbors8().filter { it.x != a.x && it.y != a.y }
            (tl in allM && br in allS || tl in allS && br in allM) &&
                    (tr in allM && bl in allS || tr in allS && bl in allM)
        }
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
