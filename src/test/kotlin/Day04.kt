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

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>): Int {
        val area = parse(input)
        var count = 0
        val xmas = mapOf(
            'X' to mutableSetOf<Point>(),
            'M' to mutableSetOf<Point>(),
            'A' to mutableSetOf<Point>(),
            'S' to mutableSetOf<Point>(),
        )
        val keys = xmas.keys
        area.tiles().filter { area[it] in keys }.forEach { xmas[area[it]]!!.add(it) }
        for (x in xmas['X']!!) {
            for (d in listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)) {
                if (
                    x.move(d.first * 1, d.second * 1) in xmas['M']!! &&
                    x.move(d.first * 2, d.second * 2) in xmas['A']!! &&
                    x.move(d.first * 3, d.second * 3) in xmas['S']!!
                    ) count++
            }
        }
        return count
    }

    private fun two(input: List<String>): Int {
        val area = parse(input)
        var count = 0
        val allA = area.tiles().filter { area[it] == 'A'}
        for (a in allA) {
            val tl = a.move(-1, -1)
            val tr = a.move(1, -1)
            val bl = a.move(-1, 1)
            val br = a.move(1, 1)
            if (area.valid(tl) && area.valid(tr) && area.valid(bl) && area.valid(br) &&
                (area[tl] == 'M' && area[br] == 'S' || area[tl] == 'S' && area[br] == 'M') &&
                (area[tr] == 'M' && area[bl] == 'S' || area[tr] == 'S' && area[bl] == 'M')
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
 * This was pretty simple because of my CharArea class.
 */
