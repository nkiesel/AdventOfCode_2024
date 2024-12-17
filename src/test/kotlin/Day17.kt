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

        var checkOutput: Boolean = false

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

        fun execute(na: Long = da): String? {
            reset(na)
            require(a >= 0L) { "a must be >= 0" }
//            println("----- $a ${program.joinToString("")} -----")
            while (instruction < program.size) {
//                println("${a.toString(8)} $output")
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
                        if (checkOutput && (output.size >= program.size || program[output.size] != next)) return null
                        output.add(next)
                        if (output.size > 0) println("${na.toString(8)} ${output.joinToString("")} ${program.joinToString("")}")
                    }
                    6 -> b = div(operand)
                    7 -> c = div(operand)
                }
                instruction += 2
            }
            return if (checkOutput && output.size != program.size) null else output.joinToString(",")
        }
    }

    private fun parse(input: List<String>): Computer {
        return Computer(input[0].longs()[0], input[1].longs()[0], input[2].longs()[0], input[4].ints())
    }

    private fun one(input: List<String>): String {
        val computer = parse(input)
        return computer.execute()!!
    }

    private fun two(input: List<String>): Long {
        val computer = parse(input).apply { checkOutput = true }
        val p = computer.program
        val sp = p.joinToString("")
        var sb = computer.program.reversed().joinToString("").toLong(8) * 8 + 8
        while (computer.execute(sb) == null) {
            sb = sb + 1
        }
        return sb
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample1) shouldBe "4,6,3,5,6,3,5,2,1,0"
        one(input) shouldBe "4,0,4,7,1,2,7,1,6"
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 117440L
//        two(input) shouldBe 0L
    }
}

// 2,4 -> b = a % 8
// 1,1 -> b = b xor 1
// 7,5 -> c = c shr b
// 0,3 -> a = a / 8
// 1,4 -> b = b xor 4
// 4,5 -> b = b xor c
// 5,5 -> output b % 8
// 3,0 -> nothing
