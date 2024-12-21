import io.kotest.matchers.shouldBe
import jdk.internal.org.jline.utils.Colors.h
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

    private fun parse(input: List<String>) = input

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

    private fun robot2(from: Char, to: Char): String {
        var fr = row2(from)
        var fc = col2(from)
        val tr = row2(to)
        val tc = col2(to)
        return buildString {
            if (fc == tc + 1 && (fc != 2)) {
                append("<")
                fc--
            }
            if (fc < tc) append(">".repeat(tc - fc))
            if (fr > tr) append("v")
            if (fc > tc) append("<".repeat(fc - tc))
            if (fr < tr) append("^")
            append("A")
        }
    }

    private fun robot3(from: Char, to: Char): String {
        var fr = row2(from)
        var fc = col2(from)
        val tr = row2(to)
        val tc = col2(to)
        return buildString {
            if (fc > tc && (fc != 2)) {
                append("<")
                fc--
            }
            if (fr > tr) append("v")
            if (fc > tc) append("<".repeat(fc - tc))
            if (fc < tc) append(">".repeat(tc - fc))
            if (fr < tr) append("^")
            append("A")
        }
    }

    private fun log(typing: String, r3: String, r2: String, r1: String): String {
        val m = """
            029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
            980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
            179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
            456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
            379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
        """.trimIndent().lines().map { it.split(": ") }.associate { it[0] to it[1] }
        println("$typing: ${m[typing]}\n      $r3\n      ${m[typing] == r3}\n      $r2\n      $r1")
        return r3
    }

    private fun seq(typing: String): String {
        val e1 = "<A^A>^^AvvvA"
        val r1l = ("A" + typing).zipWithNext().map { c -> robot1(c.first, c.second) }
        val per = r1l.map { it.indices }
        val r1 = r1l.joinToString("") { it.first() }
        val r1s = buildList {
            for (i0 in r1l[0]) {
                for (i1 in r1l[1]) {
                    for (i2 in r1l[2]) {
                        for (i3 in r1l[3]) {
                            add(listOf(i0, i1, i2, i3).joinToString(""))
                        }
                    }
                }
            }
        }
//        val r1 = ("A" + typing).zipWithNext().joinToString("") { c -> robot1(c.first, c.second) }
        val o1 = e1 == r1
        val e2 = "v<<A>>^A<A>AvA<^AA>A<vAAA>^A"
        val r2 = ("A" + r1).zipWithNext().joinToString("") { c -> robot2(c.first, c.second) }
        val r2s = r1s.map { ("A" + it).zipWithNext().joinToString("") { c -> robot2(c.first, c.second) }}

        val o2 = e2 == r2
        val e3 = "<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A"
        val r3 = ("A" + r2).zipWithNext().joinToString("") { c -> robot3(c.first, c.second) }
        val r3s = r2s.map { ("A" + it).zipWithNext().joinToString("") { c -> robot2(c.first, c.second) }}
        val o3 = e3 == r3
        log(typing, r3, r2, r1)
        return r3s.minBy(String::length)
    }

    private fun reverse1(s: String): String {
        return buildString {
            var r = 1
            var c = 3
            s.split("A").forEach { m ->
                m.split("").forEach { i ->
                    when (i) {
                        "<" -> c--
                        ">" -> c++
                        "v" -> r--
                        "^" -> r++
                    }
                }
                when (r to c) {
                    1 to 3 -> append("A")
                    1 to 2 -> append("0")
                    2 to 3 -> append("3")
                    2 to 2 -> append("2")
                    2 to 1 -> append("1")
                    3 to 3 -> append("6")
                    3 to 2 -> append("5")
                    3 to 1 -> append("4")
                    4 to 3 -> append("9")
                    4 to 2 -> append("8")
                    4 to 1 -> append("7")
                    else -> error("unreachable $r to $c")
                }
            }
        }.dropLast(1)
    }

    private fun reverse2(s: String): String {
        return buildString {
            var r = 2
            var c = 3
            s.split("A").forEach { m ->
                m.split("").forEach { i ->
                    when (i) {
                        "<" -> c--
                        ">" -> c++
                        "v" -> r--
                        "^" -> r++
                    }
                }
                when (r to c) {
                    2 to 3 -> append("A")
                    2 to 2 -> append("^")
                    1 to 3 -> append(">")
                    1 to 2 -> append("v")
                    1 to 1 -> append("<")
                    else -> error("unreachable $r to $c")
                }
            }
        }.dropLast(1)
    }

    private fun qes(r3: String): String {
        val r2 = reverse2(r3)
        println(r2)
//        r2 shouldBe "v<<A>>^A<A>AvA<^AA>A<vAAA>^A"
        val r1 = reverse2(r2)
        println(r1)
//        r1 shouldBe "<A^A>^^AvvvA"
        val typing = reverse1(r1)
        println(typing)
//        typing shouldBe "029A"
        return typing
    }

    @Test
    fun learn() {
        val m = """
            029A: <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
            980A: <v<A>>^AAAvA^A<vA<AA>>^AvAA<^A>A<v<A>A>^AAAvA<^A>A<vA>^A<A>A
            179A: <v<A>>^A<vA<A>>^AAvAA<^A>A<v<A>>^AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
            456A: <v<A>>^AA<vA<A>>^AAvAA<^A>A<vA>^A<A>A<vA>^A<A>A<v<A>A>^AAvA<^A>A
            379A: <v<A>>^AvA^A<vA<AA>>^AAvA<^A>AAvA^A<vA>^AA<A>A<v<A>A>^AAAvA<^A>A
        """.trimIndent().lines().map { it.split(": ") }.associate { it[0] to it[1] }
        for ((t, r) in m) {
            qes(r) shouldBe t
            seq(t) shouldBe r
            println("-------")
        }
    }

    private fun one(input: List<String>): Int {
        return input.sumOf { line ->
            val s = seq(line)
            val l = s.length
            val n = line.ints().first()
            val r = l * n
            println("$line -> $s -> $l * $n -> $r")
            r
        }
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 126384
        one(input) shouldBe 0
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}

/*

         <|   A| >| A|   <|   A|A|  v| <|   A|A| >|>|  ^| A|  v|  A|A| ^| A|   <| v|  A|A|A| >|  ^| A|
      <v<A|>>^A|vA|^A|<v<A|>>^A|A|<vA|<A|>>^A|A|vA|A|<^A|>A|<vA|>^A|A|<A|>A|<v<A|>A|>^A|A|A|vA|<^A|>A|

         <|   A| >| A|   <|   A|A|   <| v|   A|A| >|>|  ^| A|  v|  A|A| ^| A|   <| v|  A|A|A| >|  ^| A|
      <v<A|>>^A|vA|^A|<v<A|>>^A|A|<v<A|>A|>^A|A|vA|A|<^A|>A|<vA|>^A|A|<A|>A|<v<A|>A|>^A|A|A|vA|<^A|>A|



 */
