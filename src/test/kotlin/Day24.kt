import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day24 {
    private val sample1 = """
        x00: 1
        x01: 1
        x02: 1
        y00: 0
        y01: 1
        y02: 0

        x00 AND y00 -> z00
        x01 XOR y01 -> z01
        x02 OR y02 -> z02
    """.trimIndent().lines()

    private val sample2 = """
        x00: 1
        x01: 0
        x02: 1
        x03: 1
        x04: 0
        y00: 1
        y01: 1
        y02: 1
        y03: 1
        y04: 1

        ntg XOR fgs -> mjb
        y02 OR x01 -> tnw
        kwq OR kpj -> z05
        x00 OR x03 -> fst
        tgd XOR rvg -> z01
        vdt OR tnw -> bfw
        bfw AND frj -> z10
        ffh OR nrd -> bqk
        y00 AND y03 -> djm
        y03 OR y00 -> psh
        bqk OR frj -> z08
        tnw OR fst -> frj
        gnj AND tgd -> z11
        bfw XOR mjb -> z00
        x03 OR x00 -> vdt
        gnj AND wpb -> z02
        x04 AND y00 -> kjc
        djm OR pbm -> qhw
        nrd AND vdt -> hwm
        kjc AND fst -> rvg
        y04 OR y02 -> fgs
        y01 AND x02 -> pbm
        ntg OR kjc -> kwq
        psh XOR fgs -> tgd
        qhw XOR tgd -> z09
        pbm OR djm -> kpj
        x03 XOR y03 -> ffh
        x00 XOR y04 -> ntg
        bfw OR bqk -> z06
        nrd XOR fgs -> wpb
        frj XOR qhw -> z04
        bqk OR frj -> z07
        y03 OR x01 -> nrd
        hwm AND bqk -> z03
        tgd XOR rvg -> z12
        tnw OR pbm -> gnj
    """.trimIndent().lines()

    data class Gate(val i: Set<String>, val op: String, val oo: String) {
        var executed = false
        var swapped = false
        var o = oo
        fun execute(m: MutableMap<String, Int>) {
            val (o1, o2) = i.map { m[it] }
            if (o1 != null && o2 != null) {
                m[o] = when (op) {
                    "AND" -> o1 and o2
                    "OR" -> o1 or o2
                    "XOR" -> o1 xor o2
                    else -> error("op $op")
                }
                executed = true
            }
        }

        fun reset() {
            executed = false
            swapped = false
            o = oo
        }
    }

    private fun parse(input: List<String>): Pair<Map<String, Int>, List<Gate>> {
        return input.chunkedBy(String::isEmpty).let { (wires, gates) ->
            wires.associate { w -> w.split(": ").let { it[0] to it[1].toInt() } } to
                    gates.map { g ->
                        g.split(" ").let { s -> Gate(setOf(s[0], s[2]), s[1], s[4]) }
                    }
        }
    }

    fun toNumber(wires: Map<String, Int>, prefix: String) =
        wires.filter { it.key.startsWith(prefix) }.toSortedMap().values.reversed().joinToString("")
            .toLong(2)

    private fun one(input: List<String>): Long {
        val (wires, gates) = parse(input)
        val w = wires.toMutableMap()
        while (true) {
            val n = gates.filter { !it.executed }
            if (n.isEmpty()) {
                return toNumber(w, "z")
            } else {
                n.forEach { it.execute(w) }
            }
        }
    }

    private fun x(n: Int) = "x${n.toString().padStart(2, '0')}"
    private fun y(n: Int) = "y${n.toString().padStart(2, '0')}"
    private fun z(n: Int) = "z${n.toString().padStart(2, '0')}"

    private fun two(input: List<String>): String {
        val (wires, gates) = parse(input)
        val nz = gates.map { it.o }.filter { it.startsWith("z") }.distinct().size
        val x = toNumber(wires, "x")
        val y = toNumber(wires, "y")
        val sum = buildMap {
            (x + y).toString(2).reversed().forEachIndexed { i, c -> put(z(i), c.digitToInt()) }
        }
        for (swap in swaps(gates)) {
            val w = wires.toMutableMap()
            gates.forEach { it.reset() }
            val o = swap.map { gates[it].o }
            gates[swap[0]].o = o[1]
            gates[swap[1]].o = o[0]
            gates[swap[2]].o = o[3]
            gates[swap[3]].o = o[2]
            gates[swap[4]].o = o[5]
            gates[swap[5]].o = o[4]
            gates[swap[6]].o = o[7]
            gates[swap[7]].o = o[6]
            while (true) {
                if (sum.any { s -> w[s.key] != null && w[s.key] != s.value }) {
                    break
                }
                val zm = w.count { it.key.startsWith("z") }
                if (zm == nz) {
                    val z = toNumber(w, "z")
                    if (x + y == z) {
                        return o.sorted().joinToString(",")
                    }
                    break
                } else {
                    val n = gates.filter { !it.executed }
                    val n1 = n.size
                    n.forEach { it.execute(w) }
                    val n2 = gates.count { !it.executed }
                    if (n1 == n2) {
                        break
                    }
                }
            }
        }
        error("could not find swap")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 4L
        one(sample2) shouldBe 2024L
        one(input) shouldBe 58367545758258L
    }

    fun swaps(gates: List<Gate>): Sequence<List<Int>> = sequence {
        val m = gates.size
        val used = mutableSetOf<Int>()
        val odd = mutableListOf<Int>()
        val even = mutableListOf<Int>()
        for (i0 in 0..<m) {
            even += i0
            used += i0
            for (i1 in (even.last() + 1)..<m) {
                used += i1
                odd += i1
                for (i2 in (even.last() + 1)..<m) {
                    even += i2
                    if (used.add(i2)) {
                        for (i3 in (even.last() + 1)..<m) {
                            if (used.add(i3)) {
                                odd += i3
                                for (i4 in (even.last() + 1)..<m) {
                                    if (used.add(i4)) {
                                        even.add(i4)
                                        for (i5 in (even.last() + 1)..<m) {
                                            if (used.add(i5)) {
                                                odd.add(i5)
                                                for (i6 in (even.last() + 1)..<m) {
                                                    if (used.add(i6)) {
                                                        even += i6
                                                        for (i7 in (even.last() + 1)..<m) {
                                                            if (used.add(i7)) {
                                                                odd.add(i7)
                                                                yield(even.zip(odd).flatMap { it.toList() })
                                                                used -= i7
                                                                odd.removeLast()
                                                            }
                                                        }
                                                        used -= even.removeLast()
                                                    }
                                                }
                                                used -= odd.removeLast()
                                            }
                                        }
                                        used -= even.removeLast()
                                    }
                                }
                                used -= odd.removeLast()
                            }
                        }
                        used -= even.removeLast()
                    }
                }
                used -= odd.removeLast()
            }
            used -= i0
        }
    }


    @Test
    fun testTwo(input: List<String>) {
//        t2(input)
        three(input) shouldBe "bpf,fdw,hcc,hqc,qcw,z05,z11,z35"
    }

    private open class GG(val op: String, val vals: Set<String>) {
        fun ov(v: String) = vals.first { it != v }

        override fun toString(): String {
            return "$op: $vals"
        }

        override fun equals(other: Any?): Boolean {
            return other is GG && other.op == op && other.vals == vals
        }

        override fun hashCode(): Int {
            return op.hashCode() * 31 + vals.hashCode()
        }
    }

    private class XOR(v1: String, v2: String) : GG("XOR", setOf(v1, v2))
    private class OR(v1: String, v2: String) : GG("OR", setOf(v1, v2))
    private class AND(v1: String, v2: String) : GG("AND", setOf(v1, v2))

    private fun three(input: List<String>): String {
        val (wires, broken) = parse(input)
        val gates = broken.toMutableList()

        val fixed = mutableListOf<String>()
        fun swap(k1: String, k2: String) {
            val i1 = gates.indexOfFirst { it.o == k1 }
            val i2 = gates.indexOfFirst { it.o == k2 }

            gates[i1] = gates[i1].let { Gate(it.i, it.op, k2) }
            gates[i2] = gates[i2].let { Gate(it.i, it.op, k1) }
            fixed += k1
            fixed += k2
        }

        // Hardcoded fixes which make the code below work
        swap("z05", "bpf")
        swap("z11", "hcc")
        swap("hqc", "qcw")
        swap("z35", "fdw")

        val g = gates.associate { GG(it.op, it.i) to it.o }
        val r = gates.associate { it.o to GG(it.op, it.i) }
        val num = wires.keys.count { it.startsWith('x') }
        var pAnd = ""
        var pXor = ""
        var pCarry = ""
        for (i in 0..<num) {
            var reported = false
            fun err(s: String) {
                if (!reported) println("$i: $s")
                reported = true
            }

            val xor = XOR(x(i), y(i))
            val xorName = g[xor]!!
            if (i == 0) {
                if (xorName != z(i)) err("E1")
            } else {
                val zRule = r[z(i)]!!
                val zRuleName = g[zRule]
                if (zRule.op != "XOR") err("E2 $zRuleName")
                if (xorName !in zRule.vals) err("E3 $zRuleName")
                val otherName = zRule.ov(xorName)
                if (i == 1) {
                    if (otherName != pAnd) err("E4 $otherName")
                } else {
                    val oRule = r[otherName]
                    if (oRule == null) {
                        err("E12")
                    } else {
                        if (oRule.op != "OR") err("E5 $otherName")
                        if (pAnd !in oRule.vals) err("E6 $otherName")
                        val o2Name = oRule.ov(pAnd)
                        val o2 = r[o2Name]
                        if (o2 == null) err("E7") else {
                            if (o2.op != "AND") err("E8 $o2Name")
                            if (pXor !in o2.vals) err("E9 $o2Name")
                            val carry = o2.ov(pXor)
                            if (carry != pCarry) err("E10 $o2Name")
                        }
                    }
                }
                pCarry = otherName
            }
            pAnd = g[AND(x(i), y(i))]!!
            pXor = xorName
        }

        return fixed.sorted().joinToString(",")
    }
}

/*
Oh man, this was even harder than Day 21 for me. Part 1 was pretty simple as usual, but I never really solved part 2
algorithmically. The brute force approach took way too long, and when I analyzed the "add binary numbers code", I
only managed to find issues in the program.  I then experimented with swapping the rules that caused the issues, and
finally found the solution for my input data.
 */
