import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner

class MovePlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream) : BasePlayer(stdin, stdout, stderr) {
  init {
    var destinationCell = Cell(1, 3, false)
    while (true) {
      val grid = Grid(ArrayList<Cell>())

      val cells = scanner.nextLine().toInt()
      for (i in 0 until cells) {
        val coords = scanner.nextLine().split(" ")
        if (coords[0].toInt() >= 0) {
          grid.cells += Cell(coords[0].toInt(), coords[1].toInt(), coords[2].toInt() == 1)
        }
      }

      System.err.println("${destinationCell.x} ${destinationCell.y}")
      destinationCell = grid.cells.filter { it.x <= destinationCell.x + 1 && it.x >= destinationCell.x - 1 && it.y <= destinationCell.y + 1 && it.y >= destinationCell.y - 1 && !it.isTable }.shuffled().take(1)[0]
      System.err.println("${destinationCell.x} ${destinationCell.y}")

      System.err.println(grid.toString(destinationCell))

      stdout.println("MOVE ${destinationCell.x} ${destinationCell.y}")
    }
  }
}

class Cell(val x: Int, val y: Int, val isTable: Boolean)
class Grid(val cells: ArrayList<Cell>) {
  fun toString(cell: Cell): String {
    var returnString = "\n"
    for (i in 0 until cells.size) {
      returnString += if (cells[i].x == cell.x && cells[i].y == cell.y) " * " else if (cells[i].isTable) " X " else " O "
      if (i < cells.size - 1 && cells[i].y != cells[i + 1].y) returnString += "\n"
    }
    return returnString
  }
}