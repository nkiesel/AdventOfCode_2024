import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day08 {
    private val sample = """
        ............
        ........0...
        .....0......
        .......0....
        ....0.......
        ......A.....
        ............
        ............
        ........A...
        .........A..
        ............
        ............
    """.trimIndent().lines()

    private val sample2 = """
        T.........
        ...T......
        .T........
        ..........
        ..........
        ..........
        ..........
        ..........
        ..........
        ..........
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>): Int = three(input, ::antidotes1)

    private fun two(input: List<String>): Int = three(input, ::antidotes2)

    private fun three(input: List<String>, antidotes: (CharArea, List<Point>) -> Set<Point>): Int {
        val area = parse(input)
        val antennas = area.tiles { it != '.' }.groupBy { area[it] }
        return antennas.flatMapTo(HashSet()) { antidotes(area, it.value) }.size
    }

    private fun antidotes1(area: CharArea, points: List<Point>): Set<Point> {
        return buildSet {
            points.forEachIndexed { index, a ->
                points.subList(index + 1, points.size).forEach { b ->
                    val dx = a.x - b.x
                    val dy = a.y - b.y
                    a.move(dx, dy).let { if (it in area) add(it) }
                    b.move(-dx, -dy).let { if (it in area) add(it) }
                }
            }
        }
    }

    private fun antidotes2(area: CharArea, points: List<Point>): Set<Point> {
        return buildSet {
            points.forEachIndexed { index, a ->
                points.subList(index + 1, points.size).forEach { b ->
                    val d1 = a - b
                    var p1 = a
                    while (p1 in area) {
                        add(p1)
                        p1 = p1.move(d1)
                    }
                    val d2 = b - a
                    var p2 = b
                    while (p2 in area) {
                        add(p2)
                        p2 = p2.move(d2)
                    }
                }
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 14
        one(input) shouldBe 259
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 9
        two(input) shouldBe 927
    }
}

/*
Again pretty straightforward.  The code could be improved, but it's working and fast enough so leaving it for now.
 */
