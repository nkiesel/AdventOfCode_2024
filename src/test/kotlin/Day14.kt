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
            robots.forEach { r ->
                r.p = Point((r.p.x + r.v.x + wx) % wx, (r.p.y + r.v.y + wy) % wy)
            }
        }
        val count = CountingMap<Int>()
        val (bx, by) = listOf(wx, wy).map { it / 2 }
        robots.forEach { r ->
            val (x, y) = r.p
            when {
                x < bx && y < by -> count.inc(1)
                x > bx && y < by -> count.inc(2)
                x < bx && y > by -> count.inc(3)
                x > bx && y > by -> count.inc(4)
            }
        }
        return count.values().reduce(Long::times)
    }

    private fun two(input: List<String>, wx: Int, wy: Int): Int {
        val robots = parse(input)
        var seconds = 0
        while (true) {
            seconds++
            val pos = mutableSetOf<Point>()
            robots.forEach { r ->
                r.p = Point((r.p.x + r.v.x + wx) % wx, (r.p.y + r.v.y + wy) % wy)
                pos += r.p
            }
            if (robots.any { filledRegion(it.p, pos) }) {
                val area = CharArea(wx, wy, ' ')
                robots.forEach { robot ->
                    area[robot.p] = '#'
                }
                area.png(Tiles.BASE)
                return seconds
            }
        }
    }

    private fun filledRegion(p: Point, pos: Set<Point>): Boolean {
        for (dx in 0..3) {
            for (dy in 0..3) {
                if (p.move(dx, dy) !in pos) return false
            }
        }
        return true
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

Update: I changed the terminating criteria for part 2 to "picture must contain a dense 3x3 area or robots". That is
actually a solution that I should have found and that while a bit slower makes much more sense than the "no overlap".
*/
