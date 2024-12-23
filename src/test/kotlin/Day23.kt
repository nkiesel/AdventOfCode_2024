import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

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

    private fun parse(input: List<String>): Pair<List<Set<String>>, Map<String, Set<String>>> {
        val connections = input.map { it.split("-").toSet() }
        return connections to connections.flatten().distinct().associateWith { name ->
            connections.filter { name in it }.flatten().toSet() - name
        }
    }

    private fun one(input: List<String>): Int {
        val (_, peers) = parse(input)

        return peers.filter { it.key.startsWith("t") }.map { (t, tPeers) ->
            tPeers.mapNotNull { o ->
                val oPeers = peers[o]!!
                if (t in oPeers) {
                    val thirds = oPeers.intersect(tPeers)
                    if (thirds.isNotEmpty()) thirds.map { setOf(t, o, it) } else null
                } else null
            }.flatten().distinct()
        }.flatten().distinct().size
    }

    private fun twoSlow(input: List<String>): String {
        val (connections, peers) = parse(input)

        var parties = connections.toSet()

        while (true) {
            val next = buildSet {
                for (c in peers.keys) {
                    for (party in parties) {
                        if (c !in party && party.all { c in peers[it]!! }) {
                            add(party + c)
                        }
                    }
                }
            }
            if (next.isEmpty()) {
                return parties.maxBy { it.size }.sorted().joinToString(",")
            }
            parties = next
        }
    }

    private fun two(input: List<String>): String {
        val (connections, peers) = parse(input)

        var parties = connections.map { it.toMutableSet() }

        for (party in parties) {
            for (c in peers.keys) {
                if (c !in party && party.all { c in peers[it]!! }) {
                    party += c
                }
            }
        }

        return parties.maxBy { it.size }.sorted().joinToString(",")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 7
        one(input) shouldBe 1485
    }

    @Test
    fun testTwo(input: List<String>) {
//        twoSlow(sample) shouldBe "co,de,ka,ta"
        two(sample) shouldBe "co,de,ka,ta"
//        twoSlow(input) shouldBe "cc,dz,ea,hj,if,it,kf,qo,sk,ug,ut,uv,wh"
        two(input) shouldBe "cc,dz,ea,hj,if,it,kf,qo,sk,ug,ut,uv,wh"
    }
}

/*
Pretty sure there must be faster solutions for part 2 (takes about 15 secs on my laptop), but good enough for now.

Update: after thinking about it a bit mre, I came up with a faster solution for part 2.  I'm still not 100% convinced
that this solution is correct for all inputs though, so also keeping the much slower one.
*/
