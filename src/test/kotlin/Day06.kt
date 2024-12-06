import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day06 {
    private val sample = """
        ....#.....
        .........#
        ..........
        ..#.......
        .......#..
        ..........
        .#..^.....
        ........#.
        #.........
        ......#...
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>): Int {
        val area = parse(input)
        val start = area.first('^')
        return path(area, start).size
    }

    private fun two(input: List<String>): Int {
        val area = parse(input)
        val start = area.first('^')
        return (path(area, start) - start).count { c ->
            area[c] = '#'
            loop(area, start).also {
                area[start] = '^'
                area[c] = '.'
            }
        }
    }

    private fun path(area: CharArea, start: Point): Set<Point> {
        var pos = start
        val visited = mutableSetOf(pos)
        var dir = Direction.N
        while (true) {
            val next = pos.move(dir)
            if (!area.valid(next)) return visited
            if (area[next] == '#') {
                dir = dir.right()
            } else {
                pos = next
                visited += pos
            }
        }
    }

    private fun loop(area: CharArea, start: Point): Boolean {
        var pos = start
        var dir = Direction.N
        val visited = mutableSetOf(pos to dir)
        while (true) {
            val next = pos.move(dir)
            if (!area.valid(next)) return false
            if (area[next] == '#') {
                dir = dir.right()
            } else {
                pos = next
                if (!visited.add(pos to dir)) return true
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 41
        one(input) shouldBe 4374
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 6
        two(input) shouldBe 1705
    }
}

/**
 * CharArea to rescue!!!
 * This was pretty fast because my CharArea allowed me to 100% concentrate on the algorithm and I did not have to
 * worry about the implementation.
 * The only mistake I made was that for part 2, I initially used the `if` instead of the `while` to handle running
 * into obstacles.  I actually thought about using `while` when doing part 1, but then was not required and I forgot
 * about it.
 * The other point that I got wrong first for part 2 was to assume I would have to test all empty cells as candidates.
 * But then I quickly realized that I only had to consider the cells from the path.  I thus refactored the part 1
 * solution to return the path instead of just the path length so that I could use that for part 2.
 *
 * After seeing some other solutions, some more cleanup to my code.
 */
