import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner

class WaitPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BasePlayer(stdin, stdout, stderr) {
  init {
    while (true) {
      val inputs = readInputs()
      stdout.println("WAIT")
    }
  }
}


@Suppress("unused")
abstract class BasePlayer(stdin: InputStream, val stdout: PrintStream, val stderr: PrintStream) {
  public val scanner = Scanner(stdin)
  protected fun readInputs() = AllInputs(scanner.nextLine()!!)
    .also { /*System.out.println("Received world state: $it")*/}
}

data class AllInputs(val line: String)