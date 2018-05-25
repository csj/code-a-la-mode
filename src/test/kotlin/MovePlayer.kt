import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner

class MovePlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BasePlayer(stdin, stdout, stderr) {
    init {
        while (true) {
            val inputs = readInputs()
            stdout.println("MOVE B${Math.round(Math.random()*2) % 3 + 1}")
        }
    }
}