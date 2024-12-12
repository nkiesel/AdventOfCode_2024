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

    private fun one(input: List<String>): Int {
        val area = parse(input)
        val seen = mutableSetOf<Point>()
        return area.tiles().sumOf { p ->
            if (seen.add(p)) {
                val c = area[p]
                val n = area.neighbors4(p).filter { area[it] == c }
                var sides = 4 - n.size
                var count = 1
                val queue = ArrayDeque(n)
                while (queue.isNotEmpty()) {
                    val next = queue.removeFirst()
                    if (seen.add(next)) {
                        count++
                        val nn = area.neighbors4(next).filter { area[it] == c }
                        sides += 4 - nn.size
                        queue.addAll(nn.filter { it !in seen })
                    }
                }
                count * sides
            } else {
                0
            }
        }
    }

    private fun two(input: List<String>): Int {
        val area = parse(input)
        val seen = mutableSetOf<Point>()
        return area.tiles().sumOf { p ->
            if (seen.add(p)) {
                val c = area[p]
                val n = area.neighbors4(p).filter { area[it] == c }
                val region = mutableListOf(p)
                val queue = ArrayDeque(n)
                while (queue.isNotEmpty()) {
                    val next = queue.removeFirst()
                    if (seen.add(next)) {
                        region.add(next)
                        val nn = area.neighbors4(next).filter { area[it] == c }
                        queue.addAll(nn.filter { it !in seen })
                    }
                }
                val count = region.size
                val sides = sides(region)
                count * sides
            } else {
                0
            }
        }
    }

    private fun sides(region: List<Point>): Int {
        val (xs, xe) = region.map { it.x }.minMax()
        val (ys, ye) = region.map { it.y }.minMax()
        val area = CharArea(xe - xs + 3, ye - ys + 3, '.')
        region.forEach { area[it.move(1 - xs, 1 - ys)] = '#' }
        var count = 0
        for (y in area.yRange) {
            var upper = false
            var lower = false
            for (x in area.xRange) {
                if (area[x, y] == '#') {
                    if (area[x, y - 1] == '.') {
                        if (!upper) {
                            upper = true
                            count++
                        }
                    } else {
                        upper = false
                    }
                    if (area[x, y + 1] == '.') {
                        if (!lower) {
                            lower = true
                            count++
                        }
                    } else {
                        lower = false
                    }
                } else {
                    upper = false
                    lower = false
                }
            }
        }
        for (x in area.xRange) {
            var left = false
            var right = false
            for (y in area.yRange) {
                if (area[x, y] == '#') {
                    if (area[x - 1, y] == '.') {
                        if (!left) {
                            left = true
                            count++
                        }
                    } else {
                        left = false
                    }
                    if (area[x + 1, y] == '.') {
                        if (!right) {
                            right = true
                            count++
                        }
                    } else {
                        right = false
                    }
                } else {
                    left = false
                    right = false
                }
            }
        }
        return count
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
        two(sample4) shouldBe 236
        two(sample5) shouldBe 368
        two(sample3) shouldBe 1206
        two(input) shouldBe 849332
    }
}

/*
Oh man, part 1 was pretty simple but part 2 was complicated. My solution looks convoluted, but it's fast and works.
 */