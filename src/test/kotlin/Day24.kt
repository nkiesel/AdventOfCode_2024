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

    enum class Operation {
        AND, OR, XOR;

        companion object {
            fun fromString(s: String): Operation = when (s) {
                "AND" -> AND
                "OR" -> OR
                "XOR" -> XOR
                else -> error("op $s")
            }
        }
    }

    data class Gate(val i1: String, val i2: String, val op: Operation, val o: String) {
        var executed = false
        fun execute(m: MutableMap<String, Int>) {
            val o1 = m[i1]
            val o2 = m[i2]
            if (o1 != null && o2 != null) {
                m[o] = when (op) {
                    Operation.AND -> o1 and o2
                    Operation.OR -> o1 or o2
                    Operation.XOR -> o1 xor o2
                }
                executed = true
            }
        }
    }

    private fun parse(input: List<String>): Pair<Map<String, Int>, List<Gate>> {
        return input.chunkedBy(String::isEmpty).let { (wires, gates) ->
            wires.associate { w -> w.split(": ").let { it[0] to it[1].toInt() } } to
                    gates.map { g ->
                        g.split(" ").let { s -> Gate(s[0], s[2], Operation.fromString(s[1]), s[4]) }
                    }
        }
    }

    private fun one(input: List<String>): Long {
        val (wires, gates) = parse(input)
        val w = wires.toMutableMap()
        while (true) {
            val n = gates.filter { !it.executed }
            if (n.isEmpty()) {
                return w.filter { it.key.startsWith("z") }.toSortedMap().values.reversed().joinToString("").toLong(2)
            } else {
                n.forEach { it.execute(w) }
            }
        }
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe 4L
        one(sample2) shouldBe 2024L
        one(input) shouldBe 58367545758258L
    }

    fun swaps(m: Int) = sequence {
        for (i1 in 0..<m) {
            for (i2 in 0..<m) {
                if (i2 == i1) continue
                for (i3 in 0..<m) {
                    if (i3 == i1 || i3 == i2) continue
                    for (i4 in 0..<m) {
                        if (i4 == i1 || i4 == i2 || i4 == i3) continue
                        yield(listOf(i1, i2, i3, i4))
                    }
                }
            }
        }
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
