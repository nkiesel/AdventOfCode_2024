import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.io.path.createTempFile

private const val tileSize = 11

private val baseTiles = mapOf(
    '*' to """
        ...........
        .*...*...*.
        ..*..*..*..
        ...*.*.*...
        ....***....
        .*********.
        ....***....
        ...*.*.*...
        ..*..*..*..
        .*...*...*.
        ...........
        """,
    '#' to """
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        """,
    'g' to """
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        ***********
        """,
    ' ' to """
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        """,
    '.' to """
        ...........
        ...........
        ...........
        ...........
        ...........
        .....*.....
        ...........
        ...........
        ...........
        ...........
        ...........
        """,
    'd' to """
        ...........
        ...........
        .....*.....
        ....***....
        ...*****...
        ..*******..
        ...*****...
        ....***....
        .....*.....
        ...........
        ...........
        """,
    '-' to """
        ...........
        ...........
        ...........
        ...........
        ***********
        ***********
        ***********
        ...........
        ...........
        ...........
        ...........
        """,
    '|' to """
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    '+' to """
        ....***....
        ....***....
        ....***....
        ....***....
        ***********
        ***********
        ***********
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    ' ' to """
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        ...........
        """,
    '/' to """
        ..........*
        .........*.
        ........*..
        .......*...
        ......*....
        .....*.....
        ....*......
        ...*.......
        ..*........
        .*.........
        *..........
        """,
    '\\' to """
        *..........
        .*.........
        ..*........
        ...*.......
        ....*......
        .....*.....
        ......*....
        .......*...
        ........*..
        .........*.
        ..........*
        """,
).mapValues { toBufferedImage(it.key, it.value) }

private val pathTiles = mapOf(
    'L' to """
        ....***....
        ....***....
        ....***....
        ....***....
        ....*******
        ....*******
        ....*******
        ...........
        ...........
        ...........
        ...........
        """,
    '7' to """
        ...........
        ...........
        ...........
        ...........
        *******....
        *******....
        *******....
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    'S' to """
        ...........
        ...........
        ...........
        ...........
        ....*******
        ....*******
        ....*******
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    'F' to """
        ...........
        ...........
        ...........
        ...........
        ....*******
        ....*******
        ....*******
        ....***....
        ....***....
        ....***....
        ....***....
        """,
    'J' to """
        ....***....
        ....***....
        ....***....
        ....***....
        *******....
        *******....
        *******....
        ...........
        ...........
        ...........
        ...........
        """,
).mapValues { toBufferedImage(it.key, it.value) }

private val digitTiles = mapOf(
    '1' to """
        ...........
        ...........
        ....**.....
        ...*.*.....
        ..*..*.....
        .....*.....
        .....*.....
        .....*.....
        .....*.....
        ...*****...
        ...........
        """,
    '2' to """
        ...........
        ...........
        ....****...
        ...*....*..
        ..*....*...
        ......*....
        .....*.....
        ....*......
        ...*.......
        ..*******..
        ...........
        """,
    '3' to """
        ...........
        ...........
        ....***....
        ...*...*...
        ........*..
        ........*..
        ....****...
        ........*..
        ...*....*..
        ....****...
        ...........
        """,
    '4' to """
        ...........
        ...........
        .......*...
        ......*....
        .....*.....
        ....*......
        ...*..*....
        ..*******..
        ......*....
        ......*....
        ...........
        """,
    '5' to """
        ...........
        ...........
        ...******..
        ...*.......
        ...*.......
        ...****....
        .......*...
        ........*..
        ..*....*...
        ...****....
        ...........
        """,
    '6' to """
        ...........
        ...........
        ...*****...
        ..*.....*..
        ..*.....*..
        ..*........
        ..******...
        ..*.....*..
        ..*.....*..
        ...*****...
        ...........
        """,
    '7' to """
        ...........
        ...........
        ..*******..
        ........*..
        .......*...
        ......*....
        .....*.....
        ....*......
        ...*.......
        ..*........
        ...........
        """,
    '8' to """
        ...........
        ...........
        ...*****...
        ..*.....*..
        ..*.....*..
        ...*****...
        ..*.....*..
        ..*.....*..
        ..*.....*..
        ...*****...
        ...........
        """,
    '9' to """
        ...........
        ...........
        ...*****...
        ..*....**..
        ..*....**..
        ...****.*..
        ........*..
        ........*..
        ..*....*...
        ...****....
        ...........
        """,
    '0' to """
        ...........
        ...........
        ....***....
        ...*...*...
        ..*.....*..
        ..*.....*..
        ..*.....*..
        ..*.....*..
        ...*...*...
        ....***....
        ...........
        """,
).mapValues { toBufferedImage(it.key, it.value) }

enum class TILES {
    BASE, PATH, DIGIT
}

private fun toBufferedImage(char: Char, data: String): BufferedImage {
    val charImage = BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB)
    val dot = Color.YELLOW.rgb
    val star = when (char) {
        'b' -> Color.BLACK.rgb
        'w' -> Color.WHITE.rgb
        'g' -> Color.GREEN.rgb
        else -> Color.RED.rgb
    }
    data.trimIndent().lines().forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            when (c) {
                '.' -> charImage.setRGB(x, y, dot)
                '*' -> charImage.setRGB(x, y, star)
            }
        }
    }
    return charImage
}

fun showPng(area: CharArea, tiles: TILES) {
    val out = createTempFile(suffix = ".png").toFile()
    toPng(area, tiles, out)
    val pngViewer = System.getenv("PNG_VIEWER") ?: with(System.getProperty("os.name")) {
        when {
            startsWith("Mac") -> "open"
            startsWith("Windows") -> "explorer"
            else -> "xdg-open"
        }
    }
    ProcessBuilder(pngViewer, out.path).start().waitFor()
}

private fun toPng(area: CharArea, tiles: TILES, output: File) {
    val image = BufferedImage(
        (area.xRange.endInclusive + 1) * tileSize,
        (area.yRange.endInclusive + 1) * tileSize,
        BufferedImage.TYPE_INT_ARGB
    )
    val graphics = image.graphics

    val t = when (tiles) {
        TILES.BASE -> baseTiles
        TILES.PATH -> baseTiles + pathTiles
        TILES.DIGIT -> baseTiles + digitTiles
    }
    area.tiles().forEach { p ->
        t[area[p]]?.let { graphics.drawImage(it, p.x * tileSize, p.y * tileSize, null) }
    }

    ImageIO.write(image, "png", output)
}
