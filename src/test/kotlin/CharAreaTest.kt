import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.sequences.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CharAreaTest {
    @Test
    fun `Simple CharArea`() {
        val area = CharArea(10, 10, '.')
        area[1, 1] = '*'
        val p = Point(2, 2)
        area[p] = '#'
        area.show()
        area.png()
    }

    @Test
    fun `CharArea with border`() {
        val area = CharArea(10, 10, ' ')
        area.edges().forEach { area[it] = '#' }
        for (x in area.xRange) {
            val p = Point(x, x)
            if (area.valid(p) && p !in area.edges()) {
                area[p] = '\\'
            }
            val q = Point(x, area.xRange.last - x)
            if (area.valid(q) && q !in area.edges()) {
                area[q] = '/'
            }
        }
        area.show()
        area.png()
    }

    @Test
    fun `Some CharArea ops`() {
        val area = CharArea(10, 10, ' ')

        area.neighbors4(Point(0, 0)) shouldHaveSize 2
        area.neighbors4(Point(0, 1)) shouldHaveSize 3
        area.neighbors4(Point(1, 1)) shouldHaveSize 4
        area.neighbors4(1, 1) shouldHaveSize 4

        area.neighbors8(Point(0, 0)) shouldHaveSize 3
        area.neighbors8(Point(0, 1)) shouldHaveSize 5
        area.neighbors8(Point(1, 1)) shouldHaveSize 8
        area.neighbors8(1, 1) shouldHaveSize 8

        area[1,1] shouldBe ' '
        area[1, 1] = '.'
        area[2, 2] = '.'
        area.tiles().filter { area[it] != ' ' } shouldHaveSize 2
        area.filter { it != ' ' } shouldHaveSize 2
        area.first('.') shouldBe Point(1, 1)

        area.corners() shouldHaveSize 4
        area.corners() shouldContain Point(0, 0)

        area.edges() shouldHaveSize 36
    }

    @Test
    fun `CharArea rotation`() {
        val area = CharArea(2, 3, ' ')
        area.tiles().forEach { area[it] = (it.x + it.y).digitToChar() }
        area.show()
        area.rotated().show()
    }
}
