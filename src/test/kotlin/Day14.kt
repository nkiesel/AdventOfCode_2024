import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day14 {
    private val sample = """
        p=0,4 v=3,-3
        p=6,3 v=-1,-3
        p=10,3 v=-1,2
        p=2,0 v=2,-1
        p=0,0 v=1,3
        p=3,0 v=-2,-2
        p=7,6 v=-1,-3
        p=3,0 v=-1,-2
        p=9,3 v=2,3
        p=7,3 v=-1,2
        p=2,4 v=2,-3
        p=9,5 v=-3,-3
    """.trimIndent().lines()

    class Robot(var p: Point, val v: Point)

    private fun parse(input: List<String>) = input.map { line ->
        val (px, py, vx, vy) = line.ints()
        Robot(Point(px, py), Point(vx, vy))
    }

    private fun one(input: List<String>, wx: Int, wy: Int): Long {
        val robots = parse(input)
        repeat(100) {
            robots.forEach {
                it.p = Point((it.p.x + it.v.x + wx) % wx, (it.p.y + it.v.y + wy) % wy)
            }
        }
        val count = CountingMap<Int>()
        robots.forEach { robot ->
            val (x, y) = robot.p
            val (bx, by) = listOf(wx / 2, wy / 2)
            when {
                x < bx && y < by -> count.inc(1)
                x > bx && y < by -> count.inc(2)
                x < bx && y > by -> count.inc(3)
                x > bx && y > by -> count.inc(4)
            }
        }
        return count.values().reduce { acc, c -> acc * c }
    }

    private fun two(input: List<String>, wx: Int, wy: Int): Int {
        val robots = parse(input)
        var seconds = 0
        while (true) {
            seconds++
            val pos = mutableSetOf<Point>()
            robots.forEach {
                it.p = Point((it.p.x + it.v.x + wx) % wx, (it.p.y + it.v.y + wy) % wy)
                pos += it.p
            }
            if (robots.size == pos.size) {
                val area = CharArea(wx, wy, ' ')
                robots.forEach { robot ->
                    area[robot.p] = '#'
                }
                area.png(TILES.BASE)
                return seconds
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample, 11, 7) shouldBe 12L
        one(input, 101, 103) shouldBe 215476074L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input, 101, 103) shouldBe 6285
    }
}

/*
My first cheating of this year: I could not figure out how to decide that a picture contained a Christmas tree, so
I showed all picture where the top left and top right area was mostly empty (assuming a traditional image). But that
never showed the correct picture.  I then saw that someone suggested to use "no overlap" as criteria, and that then
very quickly gave the correct result.  The PNG picture is added as `Day14.png`.
 */
