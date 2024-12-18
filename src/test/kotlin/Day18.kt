import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day18 {
    private val sample = """
        5,4
        4,2
        4,5
        3,0
        2,1
        6,3
        2,4
        1,5
        0,6
        3,3
        2,6
        5,1
        1,2
        5,5
        2,5
        6,5
        1,4
        0,4
        6,4
        1,1
        6,1
        1,0
        0,5
        1,6
        2,0
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.ints().let { Point(it[0], it[1]) } }

    private fun one(input: List<String>, x: Int, y: Int, c: Int): Int {
        val bytes = parse(input)
        val area = CharArea(x + 1, y + 1, '.')
        val start = Point(0, 0)
        val exit = Point(x, y)
        val next: (Point) -> List<Point> = { area.neighbors4(it).filter { area[it] != '#' } }
        bytes.take(c).forEach { area[it] = '#' }
        return bfs(start, next).filter { it.value == exit }.minOf { it.index }
    }

    private fun two(input: List<String>, x: Int, y: Int, c: Int): String {
        val bytes = parse(input)
        val area = CharArea(x + 1, y + 1, '.')
        val start = Point(0, 0)
        val exit = Point(x, y)
        val next: (Point) -> List<Point> = { area.neighbors4(it).filter { area[it] != '#' } }
        bytes.take(c).forEach { area[it] = '#' }
        for (b in bytes.drop(c)) {
            area[b] = '#'
            if (dfs(start, next).any { it.value == exit }) continue
            return "${b.x},${b.y}"
        }
        error("no block")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, 6, 6, 12) shouldBe 22
        one(input, 70, 70, 1024) shouldBe 354
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 6, 6, 12) shouldBe "6,1"
        two(input, 70, 70, 1024) shouldBe "36,17"
    }
}

/*
Puh, a very simple puzzle today compared to yesterday. I'm pretty sure that part 2 could be further optimized. One
simple idea is to collect all the tiles for all the paths towards the exit and then remove all the paths containing
the next byte. That way, I guess we would not have to rediscover all the paths again after adding the next byte.
However, this runs in under 5 seconds and thus brute force is good enough for today.

Minor update: we know from part 1 that the exit is reachable from the start after 12/1024 bytes dropped. Thus, we do
not have to check again for the first 12/1024 bytes, which cuts down the cost.  Of course, even better speedup would
be to then use binary search to find the first byte that blocks the exit, but that's not necessary for this input size.
*/
