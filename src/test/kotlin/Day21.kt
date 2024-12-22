import io.kotest.matchers.longs.shouldBeInRange
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

    val memory2l = mutableMapOf<Pair<Char, Char>, List<String>>()
    val memory2 = mutableMapOf<Pair<Char, Char>, String>()

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

    private fun robot2(fromTo: Pair<Char, Char>): String {
        return memory2.getOrPut(fromTo) {
            val (from, to) = fromTo
            var fr = row2(from)
            var fc = col2(from)
            val tr = row2(to)
            val tc = col2(to)
            val h = (if (fc < tc) ">" else "<").repeat(abs(fc - tc))
            val v = (if (fr < tr) "^" else "v")
            return buildString {
                if (from != to) {
                    if (fc == tc) {
                        append(v)
                    } else if (fr == tr) {
                        append(h)
                    } else {
                        if (tc == 1) append(v + h) else append(h + v)
                    }
//                if (fc == tc + 1 && (fc != 2)) {
//                    append("<")
//                    fc--
//                }
//                if (fc < tc) append(">".repeat(tc - fc))
//                if (fr > tr) append("v")
//                if (fc > tc) append("<".repeat(fc - tc))
//                if (fr < tr) append("^")
                }
            } + "A"
        }
    }

    private fun robot2l(fromTo: Pair<Char, Char>): List<String> {
        return memory2l.getOrPut(fromTo) {
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
        val r1l = ("A$typing").zipWithNext().map { c -> robot1(c.first, c.second) }
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
        }.distinct()
        val r2s = r1s.map { ("A$it").zipWithNext().joinToString("") { robot2(it) } }.distinct()
        val r3s = r2s.map { ("A$it").zipWithNext().joinToString("") { robot2(it) } }.distinct()
        if (typing == "029A") {
            println("$typing: ${r2s.map(String::length)}")
            println("$typing: ${r3s.map(String::length)}")
            val r4s = r3s.map { ("A$it").zipWithNext().joinToString("") { robot2(it) } }.distinct()
            println("$typing: ${r4s.map(String::length)}")
            val r5s = r4s.map { ("A$it").zipWithNext().joinToString("") { robot2(it) } }.distinct()
            println("$typing: ${r5s.map(String::length)}")
        }
        return r3s.minBy(String::length)
    }

    private fun seq2(typing: String, rep: Int): String {
        val r1l = ("A$typing").zipWithNext().map { c -> robot1(c.first, c.second) }
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
        var r2s = r1s.map { ("A$it").zipWithNext().joinToString("") { robot2(it) } }.distinct()
        repeat(rep - 1) {
            r2s = r2s.map { ("A$it").zipWithNext().joinToString("") { robot2(it) } }.distinct()
        }
        return r2s.minBy(String::length)
    }

    fun combi(r: List<List<String>>): List<String> {
        var rs = listOf<String>("")
        r.forEach { l ->
            val nl = l.map { a -> rs.map { it + a } }
            rs = nl.flatten()
        }
        return rs.distinct()
    }

    @Test
    fun ct() {
        val l = listOf(listOf("A"), listOf("B1", "B2"), listOf("C"), listOf("D1", "D2"))
        val c = combi(l)
        c.size shouldBe 4
    }

    private fun steps(ft: Pair<Char, Char>): Int = steps(ft.first, ft.second)

    private fun steps(f: Char, t: Char): Int {
        return (row2(f) delta row2(t)) + (col2(f) delta col2(t))
    }

    private fun seq2a(typing: String, rep: Int): Long {
        return combi(("A$typing").zipWithNext().map { c -> robot1(c.first, c.second) }).minOf { r1 ->
            combi("A$r1".zipWithNext().map { robot2l(it) }).minOf { nk ->
                var cm = CountingMap(("A$nk").zipWithNext())
                repeat(rep - 1) {
                    val n = CountingMap<Pair<Char, Char>>()
                    for ((p, v) in cm.entries()) {
                        val robot2 = robot2l(p).minBy { r ->
                            "${r}A".zipWithNext().flatMap {
                                robot2l(it).map {
                                    it.zipWithNext().sumOf { steps(it) }
                                }
                            }.sum()
                        }
                        n.inc('A' to robot2.first(), v)
                        robot2.zipWithNext().forEach {
                            n.inc(it, v)
                        }
                    }
                    cm = n
                }
                cm.values().sum()
            }
        }
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
//            println("$line -> $s -> $l * $n -> $r")
            r
        }
    }

    private fun two(input: List<String>, rep: Int): Long {
        return input.sumOf { line ->
            val l = seq2a(line, rep)
            val n = line.ints().first()
            val r = l * n
            println("$line -> $l * $n -> $r")
            r
        }.toLong()
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 126384
        one(input) shouldBe 278748
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 2) shouldBe 126384L
//        two(sample, 4) shouldBe 126384L
        two(input, 2) shouldBe 278748L
        val two = two(input, 25)
        two shouldBeInRange 38963892L..<381421277177524L
        println(two)
    }
}

/*

         <|   A| >| A|   <|   A|A|  v| <|   A|A| >|>|  ^| A|  v|  A|A| ^| A|   <| v|  A|A|A| >|  ^| A|
      <v<A|>>^A|vA|^A|<v<A|>>^A|A|<vA|<A|>>^A|A|vA|A|<^A|>A|<vA|>^A|A|<A|>A|<v<A|>A|>^A|A|A|vA|<^A|>A|

         <|   A| >| A|   <|   A|A|   <| v|   A|A| >|>|  ^| A|  v|  A|A| ^| A|   <| v|  A|A|A| >|  ^| A|
      <v<A|>>^A|vA|^A|<v<A|>>^A|A|<v<A|>A|>^A|A|vA|A|<^A|>A|<vA|>^A|A|<A|>A|<v<A|>A|>^A|A|A|vA|<^A|>A|



 */
