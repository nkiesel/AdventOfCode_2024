import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day22 {
    private val sample = """
        1
        10
        100
        2024
    """.trimIndent().lines()

    private val sample2 = """
        1
        2
        3
        2024
    """.trimIndent().lines()

    private fun parse(input: List<String>) = input.map { it.toLong() }

    private fun one(input: List<String>): Long {
        return parse(input).sumOf { secret ->
            var n = secret
            repeat(2000) {
                n = ((n * 64) xor n) % 16777216L
                n = ((n / 32) xor n) % 16777216L
                n = ((n * 2048) xor n) % 16777216L
            }
            n
        }
    }

    class RingBuffer() {
        val l = MutableList(4) { 0 }
        var i = 0
        var prev = 0

        operator fun plusAssign(n: Int) {
            i = (i + 1) % 4
            l[i] = n - prev
            prev = n
        }

        val key: String
            get() = (1..4).map { l[(it + i) % 4] }.joinToString(",")
    }

    private fun two(input: List<String>): Int {
        val mem = List(input.size) { mutableMapOf<String, Int>() }
        val keys = mutableSetOf<String>()
        parse(input).forEachIndexed { i, secret ->
            val rb = RingBuffer()
            val m = mem[i]
            var n = secret
            rb += (n % 10).toInt()
            repeat(2000) { r ->
                n = ((n * 64) xor n) % 16777216L
                n = ((n / 32) xor n) % 16777216L
                n = ((n * 2048) xor n) % 16777216L
                val p = (n % 10).toInt()
                rb += p
                if (r > 3) {
                    val k = rb.key
                    keys += k
                    m.putIfAbsent(k, p)
                }
            }
        }
        return keys.maxOf { k -> mem.sumOf { m -> m.getOrDefault(k, 0) } }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 37327623L
        one(input) shouldBe 14273043166L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 23
        two(input) shouldBe 1667
    }
}

/*
Much easier than yesterday. Only mistake I made was to memorize the highest instead of the first price per key, which
resulted in a higher number of bananas.
*/
