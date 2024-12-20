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

    private fun one(input: List<String>, threshold: Int): Int {
        val area = parse(input)
        val start = area.first('S')
        val end = area.first('E')
        area[end] = '.'
        val steps = mutableMapOf<Point, Step>()
        var p = start
        var i = 0
        do {
            val n = p.neighbors4().first { area[it] == '.' && !steps.containsKey(it) }
            steps[p] = Step(i++, n)
            p = n
        } while (p != end)
        steps[end] = Step(i++, end)

        p = start
        var cheats = 0
        val cc = CountingMap<Int>()
        do {
            val s = steps[p]!!
            p.neighbors4().filter { area[it] == '#' }.forEach { w ->
                val d = p.direction(w)
                val n = w.move(d)
                if (area.valid(n) && area[n] == '.') {
                    val step = steps[n]!!
                    val saved = step.c - s.c - 2
                    if (saved > 0) cc.inc(saved)
                    if (saved >= threshold) {
//                        println(saved)
//                        println("w: $w n: $n")
                        cheats++
                    }
                }
            }
            p = s.next
        } while (p != end)
        return cheats
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, 20) shouldBe 5
        one(sample, 10) shouldBe 10
        one(input, 100) shouldBe 1438
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
