import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.collections.flatten

class Day23 {
    private val sample = """
        kh-tc
        qp-kh
        de-cg
        ka-co
        yn-aq
        qp-ub
        cg-tb
        vc-aq
        tb-ka
        wh-tc
        yn-cg
        kh-ub
        ta-co
        de-co
        tc-td
        tb-wq
        wh-td
        ta-ka
        td-qp
        aq-cg
        wq-ub
        ub-vc
        de-ta
        wq-aq
        wq-vc
        wh-yn
        ka-de
        kh-ta
        co-tc
        wh-qp
        tb-vc
        td-yn
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.split("-").toSet() }

    private fun one(input: List<String>): Int {
        val con = parse(input)
        val g = con.flatten().distinct().associate { a ->
            a to (con.filter { a in it }.flatten()).toSet() - a
        }

        return g.filter { it.key.startsWith("t") }.map { (t, u) ->
            u.mapNotNull { o ->
                val nk1 = g[o]!!
                if (t in nk1) {
                    val nk3 = nk1.intersect(u)
                    if (nk3.isNotEmpty()) nk3.map { setOf(t, o, it) } else null
                } else null
            }.flatten().distinct()
        }.flatten().distinct().size
    }

    private fun two(input: List<String>): Int {
        return 0
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 7
        one(input) shouldBe 1485
    }

    @Test
    fun testTwo(input: List<String>) {
//        two(sample) shouldBe 0
//        two(input) shouldBe 0
    }
}
