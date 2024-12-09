import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day09 {
    class Block(val id: Int, var length: Int) {
        var moveable = true
    }

    private val sample = """2333133121414131402""".trimIndent().lines()

    private fun parse(input: List<String>) = blocks(input[0])

    private fun one(input: List<String>): Long {
        val data = parse(input)
        val blocks = data.flatMapTo(mutableListOf<Int>()) { b -> List(b.length) { b.id } }
        while (true) {
            val free = blocks.indexOfFirst { it == -1 }
            val avail = blocks.indexOfLast { it != -1 }
            if (free > avail) break
            blocks[free] = blocks[avail].also { blocks[avail] = blocks[free] }
        }
        return blocks.filter { it != -1 }.foldIndexed(0L) { v, s, i -> s + v * i }
    }

    private fun two(input: List<String>): Long {
        val blocks = parse(input).toMutableList()
        var moveable = blocks.indexOfLast { it.id != -1 }
        while (true) {
            val b = blocks[moveable]
            b.moveable = false
            val free = blocks.indexOfFirst { it.id == -1 && it.length >= b.length }
            if (free != -1 && free < moveable) {
                val fl = blocks[free].length
                blocks[free] = b
                blocks[moveable] = Block(-1, b.length)
                if (b.length != fl) {
                    blocks.add(free + 1, Block(-1, fl - b.length))
                }
            }
            moveable = blocks.indexOfLast { it.id != -1 && it.moveable }
            if (moveable == -1) break
        }
        var sum = 0L
        var i = 0
        blocks.forEach { b ->
            if (b.id == -1) {
                i += b.length
            } else {
                repeat(b.length) { sum += b.id * i++ }
            }
        }
        return sum
    }

    private fun blocks(data: String): List<Block> {
        return buildList {
            data.forEachIndexed { index, c ->
                if (index % 2 == 0) {
                    add(Block(index / 2, c.digitToInt()))
                } else {
                    add(Block(-1, c.digitToInt()))
                }
            }
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 1928L
        one(input) shouldBe 6154342787400L
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample) shouldBe 2858L
        two(input) shouldBe 6183632723350L
    }
}

/*
Oh man, this was blowing up my brain.  I initially struggled with whether every block has a single digit value, but of
course looking at the real input that was not true. Then I ended up with 2 different approaches for the 2 parts,
although I'm quite sure that they should be better alienable. But calling it a day for now.
 */
