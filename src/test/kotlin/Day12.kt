import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day12 {
    private val sample1 = """
        AAAA
        BBCD
        BBCC
        EEEC
    """.trimIndent().lines()

    private val sample2 = """
        OOOOO
        OXOXO
        OOOOO
        OXOXO
        OOOOO
    """.trimIndent().lines()

    private val sample3 = """
        RRRRIICCFF
        RRRRIICCCF
        VVRRRCCFFF
        VVRCCCJFFF
        VVVVCJJCFE
        VVIVCCJJEE
        VVIIICJJEE
        MIIIIIJJEE
        MIIISIJEEE
        MMMISSJEEE
    """.trimIndent().lines()

    private val sample4 = """
        EEEEE
        EXXXX
        EEEEE
        EXXXX
        EEEEE
    """.trimIndent().lines()

    private val sample5 = """
        AAAAAA
        AAABBA
        AAABBA
        ABBAAA
        ABBAAA
        AAAAAA
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>) = three(input, ::sides1)

    private fun two(input: List<String>) = three(input, ::sides2)

    private fun three(input: List<String>, sides: (CharArea, Char, Set<Point>) -> Int): Int {
        val area = parse(input)
        val seen = mutableSetOf<Point>()
        return area.tiles().sumOf { p ->
            val c = area[p]
            val region = mutableSetOf<Point>()
            val queue = ArrayDeque(listOf(p))
            while (queue.isNotEmpty()) {
                val next = queue.removeFirst()
                if (seen.add(next)) {
                    region.add(next)
                    queue.addAll(area.neighbors4(next).filter { it !in seen && area[it] == c })
                }
            }
            region.size * sides(area, c, region)
        }
    }

    private fun sides1(area: CharArea, c: Char, region: Set<Point>): Int {
        return region.sumOf { p -> 4 - area.neighbors4(p).count { area[it] == c } }
    }

    private fun sides2(area: CharArea, c: Char, region: Set<Point>): Int {
        fun outside(p: Point, d: Direction) = p.move(d) !in region
        fun corner(a: Boolean, ab: Boolean, b: Boolean) = if (a && ab && b || !a && ab && !b || a && !ab && b) 1 else 0
        return region.sumOf { p ->
            val (n, e, s, w) = listOf(N, E, S, W).map { outside(p, it) }
            val (ne, se, sw, nw) = listOf(NE, SE, SW, NW).map { outside(p, it) }
            corner(n, ne, e) + corner(n, nw, w) + corner(s, se, e) + corner(s, sw, w)
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 140
        one(sample2) shouldBe 772
        one(sample3) shouldBe 1930
        one(input) shouldBe 1437300
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample1) shouldBe 80
        two(sample2) shouldBe 436
        two(sample3) shouldBe 1206
        two(sample4) shouldBe 236
        two(sample5) shouldBe 368
        two(input) shouldBe 849332
    }
}

/*
Oh man, part 1 was pretty simple but part 2 was complicated. My solution looks convoluted, but it's fast and works.

Updated code after finally finding a better "count sides of region" approach for part 2.
 */
