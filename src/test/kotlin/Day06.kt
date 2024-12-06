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
        val candidates = path(area, start) - start
        return candidates.count { c ->
            area[c] = '#'
            loop(area, start).also {
                area[start] = '^'
                area[c] = '.'
            }
        }
    }

    private fun path(area: CharArea, start: Point): Set<Point> {
        val visited = mutableSetOf<Point>()
        var pos = start
        visited += pos
        var dir = Direction.N
        var next = pos.move(dir)
        while (area.valid(next)) {
            if (area[next] == '#') {
                dir = dir.right()
                next = pos.move(dir)
            }
            pos = next
            visited += pos
            next = pos.move(dir)
        }
        return visited
    }

    private fun loop(area: CharArea, start: Point): Boolean {
        val visited = mutableSetOf<Pair<Point, Direction>>()
        var pos = start
        var dir = Direction.N
        visited += pos to dir
        var next = pos.move(dir)
        while (area.valid(next)) {
            while (area[next] == '#') {
                dir = dir.right()
                next = pos.move(dir)
            }
            pos = next
            if (!visited.add(pos to dir)) return true
            next = pos.move(dir)
        }
        return false
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
 */
