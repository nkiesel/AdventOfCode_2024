import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day10 {
    private val sample1 = """
        0123
        1234
        8765
        9876
    """.trimIndent().lines()

    private val sample2 = """
        ...0...
        ...1...
        ...2...
        6543456
        7.....7
        8.....8
        9.....9
    """.trimIndent().lines()

    private val sample3 = """
        ..90..9
        ...1.98
        ...2..7
        6543456
        765.987
        876....
        987....
    """.trimIndent().lines()

    private val sample4 = """
        10..9..
        2...8..
        3...7..
        4567654
        ...8..3
        ...9..2
        .....01
    """.trimIndent().lines()

    private val sample5 = """
        89010123
        78121874
        87430965
        96549874
        45678903
        32019012
        01329801
        10456732
    """.trimIndent().lines()

    private val sample6 = """
        .....0.
        ..4321.
        ..5..2.
        ..6543.
        ..7..4.
        ..8765.
        ..9....
    """.trimIndent().lines()

    private fun parse(input: List<String>) = CharArea(input)

    private fun one(input: List<String>): Int {
        val area = parse(input)
        return area.tiles { it == '0' }.sumOf { t ->
            buildSet {
                dfs(t) { n ->
                    area.neighbors4(n).filter { area[it] != '.' && area[it].digitToInt() == area[n].digitToInt() + 1 }
                }.filter { it.index == 9 }.forEach {
                    add(it.value)
                }
            }.size
        }
    }

    private fun two(input: List<String>): Int {
        val area = parse(input)
        return area.tiles { it == '0' }.sumOf { t ->
            walk(t) { n ->
                area.neighbors4(n).filter { area[it] != '.' && area[it].digitToInt() == area[n].digitToInt() + 1 }
            }.count { it.index == 9 }
        }
    }

    fun <T> walk(start: T, next: (T) -> Iterable<T>): Sequence<IndexedValue<T>> = sequence {
        val queue = ArrayDeque(listOf(IndexedValue(0, start)))
        while (queue.isNotEmpty()) {
            val a = queue.removeFirst()
            yield(a)
            for (b in next(a.value)) {
                queue.addFirst(IndexedValue(a.index + 1, b))
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 1
        one(sample2) shouldBe 2
        one(sample3) shouldBe 4
        one(sample4) shouldBe 3
        one(sample5) shouldBe 36
        one(input) shouldBe 489
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample6) shouldBe 3
        two(input) shouldBe 1086
    }
}

/*
As expected, a puzzle using tree walks.  I first for the part 1 wrong because I thought trailheads are only starting
at the edges.
 */