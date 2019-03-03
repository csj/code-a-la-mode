package com.codingame.game.sample

import sample.BaseCALMPlayer
import java.io.InputStream
import java.io.PrintStream

class HugPlayer(stdin: InputStream, stdout: PrintStream, stderr: PrintStream): BaseCALMPlayer(stdin, stdout, stderr) {
  init {
    while (true) {
      val inputs = readInputs()
     // stdout.println("MOVE ${inputs.myFriend.x} ${inputs.myFriend.y}; WWWWWWWWWWWWWWWWWWWWWWW")
    }
  }
}
