package players

import java.io.InputStream
import java.io.PrintStream

class WaitPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {
  init {
    while (true) {
      val inputs = readInputs()
      stdout.println("WAIT")
    }
  }
}
