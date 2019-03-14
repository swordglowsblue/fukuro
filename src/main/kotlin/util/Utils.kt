package com.swordglowsblue.fukuro.util

internal fun String.indent(spaces:Int=2) =
  split("\n").map { " ".repeat(spaces)+it }.joinToString("\n")

internal fun String.escape() = fold("") { acc, c ->
    acc + when (c) {
      '\u0007' -> "\\a"
      '\b' -> "\\b"
      '\u000C' -> """\f"""
      '\n' -> "\\n"
      '\r' -> "\\r"
      '\t' -> "\\t"
      '\"' -> "\\\""
      '\'' -> "\\\'"
      '\\' -> "\\\\"
      else -> c
    }
  }

internal fun String.unescape() = this
  .replace(Regex("""\\([^u])""")) {
    when (it.groupValues[1]) {
      "a" -> "\u0007"
      "b" -> "\b"
      "f" -> "\u000c"
      "n" -> "\n"
      "r" -> "\r"
      "t" -> "\t"
      else -> it.groupValues[1]
    }
  }
  .replace(Regex("""\\u([0-9A-Fa-f]{4})""")) {
    it.groupValues[1].toInt(16).toChar().toString()
  }

internal val String.head get() = getOrNull(0)
internal val String.tail get() = drop(1)
internal val <T, C : Collection<T>> C.head get():T? = elementAtOrNull(0)
internal val <T, C : Collection<T>> C.tail get():List<T> = drop(1)
internal operator fun Regex.contains(text:Char):Boolean = contains(text.toString())
internal operator fun Regex.contains(text:CharSequence):Boolean = this.matches(text)
