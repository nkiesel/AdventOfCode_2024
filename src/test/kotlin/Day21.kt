import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.abs

class Day21 {
    private val sample = """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent().lines()

    private fun row1(from: Char): Int = when (from) {
        '0', 'A' -> 1
        '1', '2', '3' -> 2
        '4', '5', '6' -> 3
        else -> 4
    }

    private fun col1(from: Char): Int = when (from) {
        '1', '4', '7' -> 1
        '0', '2', '5', '8' -> 2
        else -> 3
    }

    private fun row2(from: Char): Int = when (from) {
        '<', 'v', '>' -> 1
        else -> 2
    }

    private fun col2(from: Char): Int = when (from) {
        '<' -> 1
        '^', 'v' -> 2
        else -> 3
    }

    private fun robot1(from: Char, to: Char): List<String> {
        return buildList {
            if (from != to) {
                val fr = row1(from)
                val fc = col1(from)
                val tr = row1(to)
                val tc = col1(to)
                val h = (if (fc < tc) ">" else "<").repeat(abs(fc - tc))
                val v = (if (fr < tr) "^" else "v").repeat(abs(fr - tr))
                if (fc == tc) {
                    add(v)
                } else if (fr == tr) {
                    add(h)
                } else {
                    if (fc > 1 || tr > 1) add(v + h)
                    if (fr > 1 || tc > 1) add(h + v)
                }
            }
        }.map { it + "A" }
    }

    val memory2 = mutableMapOf<Pair<Char, Char>, List<String>>()

    private fun robot2(fromTo: Pair<Char, Char>): List<String> {
        return memory2.getOrPut(fromTo) {
            val (from, to) = fromTo
            var fr = row2(from)
            var fc = col2(from)
            val tr = row2(to)
            val tc = col2(to)
            val h = (if (fc < tc) ">" else "<").repeat(abs(fc - tc))
            val v = (if (fr < tr) "^" else "v")
            return buildList {
                if (from == to) {
                    add("")
                } else {
                    if (fc == tc) {
                        add(v)
                    } else if (fr == tr) {
                        add(h)
                    } else if (from == '<') {
                        if (tc == 2) {
                            add(">^")
                        } else {
                            add(">^>")
                            add(">>^")
                        }
                    } else if (to == '<') {
                        if (fc == 2) {
                            add("v<")
                        } else {
                            add("<v<")
                            add("v<<")
                        }
                    } else {
                        add(h + v)
                        add(v + h)
                    }
                }
            }.map { it + "A" }
        }
    }

    fun combi(r: List<List<String>>): List<String> {
        var rs = listOf<String>("")
        r.forEach { l ->
            val nl = l.map { a -> rs.map { it + a } }
            rs = nl.flatten()
        }
        return rs.distinct()
    }

    private fun steps(ft: Pair<Char, Char>): Int =
        abs(row2(ft.first) - row2(ft.second)) + abs(col2(ft.first) - col2(ft.second))

    val cost = mutableMapOf<String, Int>()

    private fun minLength(typing: String, rep: Int): Long {
        return combi(("A$typing").zipWithNext().map { c -> robot1(c.first, c.second) }).minOf { r1 ->
            combi("A$r1".zipWithNext().map { robot2(it) }).minOf { r2 ->
                var cm = CountingMap(("A$r2").zipWithNext())
                repeat(rep - 1) {
                    val n = CountingMap<Pair<Char, Char>>()
                    for ((p, v) in cm.entries()) {
                        val robot2 = robot2(p).minBy { r ->
                            cost.getOrPut(r) {
                                "${r}A".zipWithNext().flatMap {
                                    robot2(it).map {
                                        it.zipWithNext().sumOf { steps(it) }
                                    }
                                }.sum()
                            }
                        }
                        n.inc('A' to robot2.first(), v)
                        robot2.zipWithNext().forEach { n.inc(it, v) }
                    }
                    cm = n
                }
                cm.values().sum()
            }
        }
    }

    private fun one(input: List<String>): Int = three(input, 2).toInt()

    private fun two(input: List<String>): Long = three(input, 25)

    private fun three(input: List<String>, rep: Int): Long {
        return input.sumOf { line -> minLength(line, rep) * line.ints().first() }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 126384
        one(input) shouldBe 278748
    }

    @Test
    fun testTwo(input: List<String>) {
        two(input) shouldBe 337744744231414L
    }
}

/*
As expected, the challenge for the first day of the last weekend was by far the most complicated for me.  It took
me about 10 hours and lots of experimentation to finally solve it.
*/
