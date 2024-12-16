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

    private fun one(input: List<String>) = three(input).first().cost

    private fun two(input: List<String>) = three(input).flatMap { it.path }.distinct().size

    private class Step(val p: Point, val d: Direction, val cost: Int, val path: List<Point>)

    private fun three(input: List<String>): List<Step> {
        val area = parse(input)
        val start = area.first('S')
        val end = area.first('E')
        val reachedEnd = mutableListOf<Step>()
        val seen = mutableMapOf<Pair<Point, Direction>, Int>()
        val queue = ArrayDeque(listOf(Step(start, Direction.E, 0, listOf(start))))
        while (queue.isNotEmpty()) {
            val s = queue.removeFirst()
            val p = s.p
            if (p == end) {
                reachedEnd.add(s)
            } else {
                val d = s.d
                listOf(d, d.turnLeft(), d.turnRight()).map { p.move(it) to it }.filter { area[it.first] != '#' }
                    .forEach { (n, nd) ->
                        val cost = s.cost + if (d == nd) 1 else 1001
                        val key = n to d
                        val prev = seen[key]
                        if (prev == null || cost <= prev) {
                            seen[key] = cost
                            queue.add(Step(n, nd, cost, s.path + n))
                        }
                    }
            }
        }
        val bestCost = reachedEnd.minOf { it.cost }
        return reachedEnd.filter { it.cost == bestCost }
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

Update: while converting to TypeScript, I changed the "neighbors" logic to use "turnLeft" and "turnRight" instead
of first finding all possible neighbors and then computing their direction, which simplified the code. I then finally
also used part2 for part1, although that adds a some overhead for part 1 because we would not have to keep track of
the path to the point in part 1, and because we would not care from which direction a point was reached.
*/
