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
}
