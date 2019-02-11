package com.codingame.game

fun <T> Boolean.then(thenExpr: T) = if (this) thenExpr else null