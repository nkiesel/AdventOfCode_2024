import Direction.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day16 {
    private val sample1 = """
        ###############
        #.......#....E#
        #.#.###.#.###.#
        #.....#.#...#.#
        #.###.#####.#.#
        #.#.#.......#.#
        #.#.#####.###.#
        #...........#.#
        ###.#.#####.#.#
        #...#.....#.#.#
        #.#.#.###.#.#.#
        #.....#...#.#.#
        #.###.#.#.#.#.#
        #S..#.....#...#
        ###############
    """.trimIndent().lines()

    private val sample2 = """
        #################
        #...#...#...#..E#
        #.#.#.#.#.#.#.#.#
        #.#.#.#...#...#.#
        #.#.#.#.###.#.#.#
        #...#.#.#.....#.#
        #.#.#.#.#.#####.#
        #.#...#.#.#.....#
        #.#.#####.#.###.#
        #.#.#.......#...#
        #.#.###.#####.###
        #.#.#...#.....#.#
        #.#.#.#####.###.#
        #.#.#.........#.#
        #.#.#.#########.#
        #S#.............#
        #################
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private data class Step1(val p: Point, val d: Direction, val cost: Int)

    private fun one(input: List<String>): Int {
        val area = parse(input)
        val start = area.first('S')
        val end = area.first('E')
        val seen = mutableMapOf<Point, Int>()
        val queue = ArrayDeque(listOf(Step1(start, E, 0)))
        while (queue.isNotEmpty()) {
            val s = queue.removeFirst()
            s.p.neighbors4().filter { area[it] != '#' }.forEach { n ->
                val d = s.p.direction(n)
                val turns = when (s.d) {
                    d -> 0
                    N -> if (d == S) 2 else 1
                    E -> if (d == W) 2 else 1
                    S -> if (d == N) 2 else 1
                    W -> if (d == E) 2 else 1
                    else -> error("Unexpected direction $d")
                }
                if (turns < 2) {
                    val cost = s.cost + 1 + 1000 * turns
                    val prev = seen[n]
                    if (prev == null || cost < prev) {
                        seen[n] = cost
                        if (n != end) queue.add(Step1(n, d, cost))
                    }
                }
            }
        }
        return seen[end]!!
    }

    private data class Step2(val p: Point, val d: Direction, val cost: Int, val path: List<Point>)

    private fun two(input: List<String>): Int {
        val area = parse(input)
        val start = area.first('S')
        val end = area.first('E')
        val results = mutableListOf<Step2>()
        val seen = mutableMapOf<Pair<Point, Direction>, Int>()
        val queue = ArrayDeque(listOf(Step2(start, E, 0, listOf(start))))
        while (queue.isNotEmpty()) {
            val s = queue.removeFirst()
            if (s.p == end) {
                results.add(s)
            } else {
                s.p.neighbors4().filter { area[it] != '#' }.forEach { n ->
                    val d = s.p.direction(n)
                    val turns = when (s.d) {
                        d -> 0
                        N -> if (d == S) 2 else 1
                        E -> if (d == W) 2 else 1
                        S -> if (d == N) 2 else 1
                        W -> if (d == E) 2 else 1
                        else -> error("Unexpected direction $d")
                    }
                    if (turns < 2) {
                        val cost = s.cost + 1 + 1000 * turns
                        val key = n to s.d
                        val prev = seen[key]
                        if (prev == null || cost <= prev) {
                            seen[key] = cost
                            queue.add(Step2(n, d, cost, s.path + n))
                        }
                    }
                }
            }
        }

        val bestCost = results.minOf { it.cost }
        return results.filter { it.cost == bestCost }.flatMap { it.path }.distinct().size
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 7036
        one(sample2) shouldBe 11048
        one(input) shouldBe 72400
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample1) shouldBe 45
        two(sample2) shouldBe 64
        two(input) shouldBe 435
    }
}

/*
The approach was pretty obvious, but I tried to use my `bfs` helper and while that worked for the samples after I
stopped allowing to turn around, it still failed for the real input. I then open-coded that graph walk and use a
map from Point to cost as filter criteria. For part 2, the code was very similar, but I had to use a pair of point
and direction and let paths with equal costs proceed.
*/
