import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day13 {
    private val sample = """
        Button A: X+94, Y+34
        Button B: X+22, Y+67
        Prize: X=8400, Y=5400

        Button A: X+26, Y+66
        Button B: X+67, Y+21
        Prize: X=12748, Y=12176

        Button A: X+17, Y+86
        Button B: X+84, Y+37
        Prize: X=7870, Y=6450

        Button A: X+69, Y+23
        Button B: X+27, Y+71
        Prize: X=18641, Y=10279
    """.trimIndent().lines()

    class Machine(val ax: Int, val ay: Int, val bx: Int, val by: Int, val px: Int, val py: Int) {
        constructor(l: List<Int>) : this(l[0], l[1], l[2], l[3], l[4], l[5])

        fun bruteForce(): Int = buildSet {
            for (na in 0..100) {
                val rx = px - ax * na
                if (rx % bx != 0) continue
                val nb = if (rx == 0) 0 else rx / bx
                if (nb > 100) continue
                if (ay * na + by * nb != py) continue
                add(na * 3 + nb)
            }
        }.let { if (it.isEmpty()) 0 else it.min() }

        fun tokens(d: Long): Long {
            val pxd = px + d
            val pyd = py + d
            val na = (pyd * bx - pxd * by) / (ay * bx - ax * by)
            val nb = (pxd - ax * na) / bx
            return if (na * ax + nb * bx == pxd && na * ay + nb * by == pyd) na * 3 + nb else 0
        }
    }

    private fun parse(input: List<String>) =
        input.chunkedBy(String::isEmpty).map { Machine(it.joinToString("").ints()) }

    private fun one(input: List<String>): Long {
        return parse(input).sumOf { m -> m.tokens(0L) }
    }

    private fun two(input: List<String>): Long {
        return parse(input).sumOf { m -> m.tokens(10000000000000L) }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 480L
        one(input) shouldBe 29522L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 875318608908L
        two(input) shouldBe 101214869433312L
    }
}

/*
For part1 , I first did a simple brute-force approach, and that worked as expected. The text of course suggested
that for every machine, there could be more than one possible na,nb combination. But then for part 2 I knew
that following the part1 approach would take forever. I then started with the 2 equations:
ax * na + bx * nb = px
ay * na + by * nb = py

and transformed these to
nb = (px - ax * na) / bx
nb = (py - ay * na) / by

which then lead to a formula for na and nb. And thus, there is actually only a single or no na,nb pair for every
machine.
*/
