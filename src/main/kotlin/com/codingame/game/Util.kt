package com.codingame.game

fun <T> Boolean.then(thenExpr: T) = if (this) thenExpr else null

fun <T> Iterator<T>.tryNext() = if (hasNext()) next() else null

fun <T> Iterator<T>.findNext(pred: (T) -> Boolean): T? {
  while (hasNext()) {
    next().also { if (pred(it)) return it }
  }
  return null
}

inline fun <reified T> Array<Array<T>>.transpose(default: () -> T): Array<Array<T>> {
  val rows = this.size
  val cols = this[0].size
  val trans = Array(cols) { Array(rows) { default() } }
  for (i in 0 until cols) {
    for (j in 0 until rows) trans[i][j] = this[j][i]
  }
  return trans
}

fun Sequence<String>.splitAccumulate(length: Int) = sequence {
  var currentLine = ""

  forEach { tok ->
    if (currentLine.length + tok.length > length) {
      yield(currentLine.substring(1))
      currentLine = ""
    }
    currentLine += " $tok"
  }

  yield(currentLine.substring(1))
}

fun <E> List<E>.loopingIterator(): Iterator<E> =
    sequence { while (true) yieldAll(this@loopingIterator) }.iterator()

fun <T> T.nullIf(nullVal: T): T? = if (this == nullVal) null else this
