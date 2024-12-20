import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day20 {
    private val sample = """
        ###############
        #...#...#.....#
        #.#.#.#.#.###.#
        #S#...#.#.#...#
        #######.#.#.###
        #######.#.#...#
        #######.#.###.#
        ###..E#...#...#
        ###.#######.###
        #...###...#...#
        #.#####.#.###.#
        #.#...#.#.#...#
        #.#.#.#.#.#.###
        #...#...#...###
        ###############
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    class Step(val c: Int, val next: Point)

    private fun one(input: List<String>, threshold: Int) = three(input, threshold, 2)

    private fun two(input: List<String>, threshold: Int) = three(input, threshold, 20)

    private fun firstOne(input: List<String>, threshold: Int): Int {
        val area = parse(input)
        val start = area.first('S')
        val end = area.first('E')
        area[end] = '.'
        val steps = steps(area, start, end)

        var p = start
        var cheats = 0
        do {
            val s = steps[p]!!
            p.neighbors4().filter { area[it] == '#' }.forEach { w ->
                val d = p.direction(w)
                val n = w.move(d)
                if (area.valid(n) && area[n] == '.') {
                    val step = steps[n]!!
                    val saved = step.c - s.c - 2
                    if (saved >= threshold) {
                        cheats++
                    }
                }
            }
            p = s.next
        } while (p != end)
        return cheats
    }

    private fun three(input: List<String>, threshold: Int, picoseconds: Int): Int {
        val area = parse(input)
        val start = area.first('S')
        val end = area.first('E')
        area[end] = '.'
        val steps = steps(area, start, end)

        var cheats = mutableSetOf<Pair<Point, Point>>()
        for ((p, s) in steps.entries) {
            area.manhattan(p, picoseconds).filter { area[it] == '.' }.forEach { t ->
                if (steps[t]!!.c - s.c - manhattanDistance(t, p) >= threshold) {
                    cheats.add(p to t)
                }
            }
        }
        return cheats.size
    }

    private fun steps(area: CharArea, start: Point, end: Point): Map<Point, Step> {
        val steps = mutableMapOf<Point, Step>()
        var p = start
        var i = 0
        do {
            val n = area.neighbors4(p).first { area[it] == '.' && it !in steps }
            steps[p] = Step(i++, n)
            p = n
        } while (p != end)
        steps[end] = Step(i, end)
        return steps
    }

    @Test
    fun testOne(input: List<String>) {
        firstOne(sample, 20) shouldBe 5
        one(sample, 20) shouldBe 5
        one(sample, 10) shouldBe 10
        firstOne(input, 100) shouldBe 1438
        one(input, 100) shouldBe 1438
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 76) shouldBe 3
        two(sample, 74) shouldBe 7
        two(sample, 72) shouldBe 29
        two(input, 100) shouldBe 1026446
    }
}

/*
Yeah, solved day 20!!!
The part 1 I solved pretty fast, but as usual it took a bit to solve part 2. One initial misread was the assumption
that during the cheating, we could only step on walls.  Thus, I first computed the connected walls from the first
wall up to a distance of 20, and then the distance of stepping into the path from all of these walls to the starting
point. But that failed, and when I looked again at the first example of part 2 I saw that one of the cheats also
stepped on the path.  This then lead to the actually much simpler solution to find all the points with manhattan
distance of 20 or less.
 */
