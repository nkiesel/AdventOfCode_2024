import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Day15 {
    private val sample = """
        ########
        #..O.O.#
        ##@.O..#
        #...O..#
        #.#.O..#
        #...O..#
        #......#
        ########

        <^^>>>vv<v>>v<<
    """.trimIndent().lines()

    private val sample2 = """
        #######
        #...#.#
        #.....#
        #..OO@#
        #..O..#
        #.....#
        #######

        <vv<<^^<<^^
    """.trimIndent().lines()

    private val sample3 = """
        ##########
        #..O..O.O#
        #......O.#
        #.OO..O.O#
        #..O@..O.#
        #O#..O...#
        #O..O..O.#
        #.OO.O.OO#
        #....O...#
        ##########

        <vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
        vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
        ><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
        <<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
        ^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
        ^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
        >^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
        <><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
        ^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
        v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trimIndent().lines()

    val showMoves = setOf('i', 'f')

    private fun parse(input: List<String>): Pair<CharArea, String> {
        val split = input.chunkedBy { it.isEmpty() }
        return Pair(CharArea(split[0]), split[1].joinToString(""))
    }

    private fun one(input: List<String>): Int {
        val (area, moves) = parse(input)
        var robot = area.first('@')
        area[robot] = '.'
        show('i', area, robot)
        moves.forEach { move ->
            val d = Direction.from(move)
            val n = robot.move(d)
            if (area[n] == '.') {
                robot = n
            } else if (area[n] == 'O') {
                var b = n
                while (area[b] == 'O') b = b.move(d)
                if (area[b] == '.') {
                    area[n] = '.'
                    area[b] = 'O'
                    robot = n
                }
            }
            show(move, area, robot)
        }
        show('f', area, robot)
        return area.tiles { it == 'O' }.sumOf { it.y * 100 + it.x }
    }

    private fun two(input: List<String>, expand: Boolean = true): Int {
        val (orig, moves) = parse(input)
        val area: CharArea
        if (expand) {
            area = CharArea((orig.xRange.endInclusive + 1) * 2, orig.yRange.endInclusive + 1, '.')
            orig.tiles { it != '.' }.forEach { t ->
                when (orig[t]) {
                    '#' -> {
                        area[t.x * 2, t.y] = '#'
                        area[t.x * 2 + 1, t.y] = '#'
                    }

                    'O' -> {
                        area[t.x * 2, t.y] = '['
                        area[t.x * 2 + 1, t.y] = ']'
                    }

                    '@' -> {
                        area[t.x * 2, t.y] = '@'
                    }
                }
            }
        } else {
            area = orig
        }
        var robot = area.first('@')
        area[robot] = '.'
        show('i', area, robot)
        moves.forEach { move ->
            val d = Direction.from(move)
            val n = robot.move(d)
            if (area[n] == '.') {
                robot = n
            } else if (area[n] != '#') {
                when (d) {
                    Direction.W -> {
                        var b = n
                        while (area[b] == ']') b = b.move(-2, 0)
                        if (area[b] == '.') {
                            area[n] = '.'
                            for (x in (b.x)..(n.x - 1) step 2) {
                                area[x, n.y] = '['
                                area[x + 1, n.y] = ']'
                            }
                            robot = n
                        }
                    }

                    Direction.E -> {
                        var b = n
                        while (area[b] == '[') b = b.move(2, 0)
                        if (area[b] == '.') {
                            area[n] = '.'
                            for (x in (n.x + 1)..(b.x - 1) step 2) {
                                area[x, n.y] = '['
                                area[x + 1, n.y] = ']'
                            }
                            robot = n
                        }
                    }

                    else -> {
                        if (push(area, n, d, false)) {
                            push(area, n, d, true)
                            robot = n
                        }
                    }
                }
            }
            show(move, area, robot)
        }
        show('f', area, robot)
        return area.tiles { it == '[' }.sumOf { it.y * 100 + it.x }
    }

    private fun push(area: CharArea, box: Point, d: Direction, update: Boolean): Boolean {
        val l = if (area[box] == ']') box.move(-1, 0) else box
        val r = l.move(1, 0)
        val nl = l.move(d)
        val nr = r.move(d)
        if (area[nl] == '#' || area[nr] == '#') return false
        if ((area[nl] == '.' || push(area, nl, d, update)) && (area[nr] == '.' || push(area, nr, d, update))) {
            if (update) {
                area[nl] = '['
                area[nr] = ']'
                area[l] = '.'
                area[r] = '.'
            }
            return true
        }
        return false
    }

    private fun show(move: Char, area: CharArea, robot: Point) {
        if (move in showMoves) {
            println(
                when (move) {
                    'i' -> "Initial state:"
                    'f' -> "Final state:"
                    else -> "Move $move:"
                }
            )
            area[robot] = '@'
            area.show()
            println()
            area[robot] = '.'
        }
    }

    @Test
    fun testOne(input: List<String>) {
        one(sample) shouldBe 2028
        one(sample3) shouldBe 10092
        one(input) shouldBe 1495147
    }

    @Test
    fun testTwo(input: List<String>) {
        two(sample2) shouldBe 618
        two(sample3) shouldBe 9021
        two(input) shouldBe 1524905
    }
}

/*
Another nice one. As usual, part 1 was much simpler than part 2. However, once I came up with the recursive "push"
idea, the up and down pushes were pretty simple. I struggled for a while because my "push left" code was wrong because
it used the wrong offsets. But once I found that issue, fixing it was again pretty simple.
*/
