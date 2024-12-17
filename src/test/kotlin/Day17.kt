import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import kotlin.math.pow

class Day17 {
    private val sample1 = """
        Register A: 729
        Register B: 0
        Register C: 0

        Program: 0,1,5,4,3,0
    """.trimIndent().lines()

    private val sample2 = """
        Register A: 2024
        Register B: 0
        Register C: 0

        Program: 0,3,5,4,3,0
    """.trimIndent().lines()

    class Computer(val da: Long, val db: Long, var dc: Long, val program: List<Int>) {
        var a = da
        var b = db
        var c = dc
        val output = mutableListOf<Int>()
        var instruction: Int = 0

        fun reset(na: Long) {
            a = na
            b = db
            c = dc
            instruction = 0
            output.clear()
        }

        fun combo(operand: Int) = when (operand) {
            4 -> a
            5 -> b
            6 -> c
            else -> operand.toLong()
        }

        fun div(operand: Int): Long {
            val denominator = combo(operand)
            require(denominator < 64) { "$denominator must be less than 64" }
            return (a shr denominator.toInt()).toLong()
        }

        fun execute(na: Long = da): String {
            reset(na)
            require(a >= 0L) { "a must be >= 0" }
            while (instruction < program.size) {
                val opCode = program[instruction]
                val operand = program[instruction + 1]
                when (opCode) {
                    0 -> a = div(operand)
                    1 -> b = b xor operand.toLong()
                    2 -> b = combo(operand) % 8L
                    3 -> if (a != 0L) instruction = operand - 2
                    4 -> b = b xor c
                    5 -> {
                        val next = (combo(operand) % 8L).toInt()
                        output.add(next)
                    }

                    6 -> b = div(operand)
                    7 -> c = div(operand)
                }
                instruction += 2
            }
            return output.joinToString(",")
        }
    }

    private fun parse(input: List<String>): Computer {
        return Computer(input[0].longs()[0], input[1].longs()[0], input[2].longs()[0], input[4].ints())
    }

    private fun one(input: List<String>): String {
        val computer = parse(input)
        return computer.execute()
    }

    private fun two(input: List<String>): Long {
        val computer = parse(input)
        val ps = computer.program.joinToString(",")
        var pi = ps.length - 1
        var a = 0L
        while (pi >= 0) {
            while (true) {
                val r = computer.execute(a)
                if (r == ps) {
                    return a
                }
                if (r == ps.substring(pi)) {
                    pi -= 2
                    break
                }
                a++
            }
            a *= 8
        }
        error("no result")
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe "4,6,3,5,6,3,5,2,1,0"
        one(input) shouldBe "4,0,4,7,1,2,7,1,6"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 117440L
        two(input) shouldBe 202322348616234L
    }
}

/*
Oh man, I effectively gave up on part 2 last night, but then under the shower this morning I realized that the solution
requires A to end up as 0, because the program always loops to the first instruction otherwise. Thus, the last output
must be the last program instruction, and the one before that must be the second-to-last instruction, and so on. And
while observing the program execution last night, I also realized that the previous value of A was always 8 times the
current one, and thus when finding the right value for A to produce the correct last program instruction, the value
producing the correct second-to-last instruction is 8 times that, and so on.

Part 1 was nice and simple.
 */
