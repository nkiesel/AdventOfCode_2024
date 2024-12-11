import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day11 {
    private val sample = """125 17""".trimIndent().lines()

    private fun parse(input: List<String>) = input[0].longs()

    private fun one(input: List<String>): Int {
        var stones = parse(input)
        repeat(25) {
            stones = buildList {
                stones.forEach { s ->
                    val ss = s.toString()
                    when {
                        s == 0L -> add(1L)
                        ss.length % 2L == 0L -> {
                            val (l, r) = ss.chunked(ss.length / 2)
                            add(l.toLong())
                            add(r.toLong())
                        }

                        else -> add(s * 2024L)
                    }
                }
            }
        }
        return stones.size
    }

    private fun two(input: List<String>, rep: Int): Long {
        var countMap = CountingMap<Long>()
        parse(input).groupingBy { it }.eachCount().forEach { countMap.inc(it.key, it.value.toLong()) }
        repeat(rep) {
            val next = CountingMap<Long>()
            countMap.entries.forEach { stone ->
                val s = stone.key
                val count = stone.value.value
                val ss = s.toString()
                when {
                    s == 0L -> next.inc(1L, count)
                    ss.length % 2L == 0L -> {
                        val (l, r) = ss.chunked(ss.length / 2)
                        next.inc(l.toLong(), count)
                        next.inc(r.toLong(), count)
                    }

                    else -> next.inc(s * 2024L, count)
                }
            }
            countMap = next
        }
        return countMap.entries().sumOf { it.value }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 55312
        one(input) shouldBe 184927
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample, 25) shouldBe 55312
        two(input, 25) shouldBe 184927
        two(input, 75) shouldBe 220357186726677L
    }
}

/*
Yeah, first "brute force solution does not work for part 2 anymore" puzzle. I first thought of processing every stone
individually, but even that would exceed the list length with 75 iterations. So I had to come up with a solution that
counts the stones and their occurrences. I used my counting map class for that, which allows to increment map values
without having to create a new map entry. This directly worked and of course also then works for part 1.
 */