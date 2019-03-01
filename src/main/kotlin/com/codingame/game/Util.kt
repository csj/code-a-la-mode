package com.codingame.game

fun <T> Boolean.then(thenExpr: T) = if (this) thenExpr else null

fun <T> Iterator<T>.tryNext() = if (hasNext()) next() else null

fun <T> Iterator<T>.findNext(pred: (T) -> Boolean): T? {
  while (hasNext()) {
    next().also { if (pred(it)) return it }
  }
  return null
}
