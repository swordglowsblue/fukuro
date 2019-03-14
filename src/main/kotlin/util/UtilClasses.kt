package com.swordglowsblue.fukuro.util

import java.lang.Exception

class ParseException(message: String, sourcePos: SourcePos)
  : Exception("$message (line ${sourcePos.line}, col ${sourcePos.col})")

data class SourcePos(val line: Int, val col: Int) {
  fun offset(l: Int, c: Int) = SourcePos(line+l,col+c)
  fun next(by: Int = 1) = offset(0, by)
  fun newline() = offset(1, -col)
  override fun toString() = "($line, $col)"
}
