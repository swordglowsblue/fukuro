package com.swordglowsblue.fukuro.core

object Lexer {
  sealed class Token(open val text: String) {
    data class Identifier    (override val text: String) : Token(text)
    data class NumberLiteral (override val text: String) : Token(text)
    data class StringLiteral (override val text: String) : Token(text)

    object OpDot : Token(".")
    object OpAdd : Token("+")
    object OpSub : Token("-")
    object OpMul : Token("*")
    object OpDiv : Token("/")
    object OpMod : Token("%")
    object OpNot : Token("!")
    object OpLt  : Token("<")
    object OpGt  : Token(">")
    object OpAsn : Token("=")
    object OpTyp : Token(":")
    object OpBar : Token("|")
    object OpAnd : Token("&&")
    object OpOr  : Token("||")
    object OpEql : Token("==")
    object OpNeq : Token("!=")
    object OpLeq : Token("<=")
    object OpGeq : Token(">=")

    object OpenList   : Token("[")
    object CloseList  : Token("]")
    object OpenParen  : Token("(")
    object CloseParen : Token(")")
    object OpenBlock  : Token("{")
    object CloseBlock : Token("}")
  }

  data class Pos(val line: Int, val col: Int) {
    fun offset(l: Int, c: Int) = Pos(line+l,col+c)
    fun next(by: Int = 1) = offset(0, by)
    fun newline() = offset(1, -col)
    override fun toString() = "($line, $col)"
  }

  private val Operators = Regex("[.+\\-*/%!&|=:<>]")
  private val Whitespace = Regex("[ \t\b\r]")
  private val Numeric = Regex("\\d")
  private val Alphabetic = Regex("[a-zA-Z_]")
  private val Alphanumeric = Regex("\\w")
  private val Escapes = Regex("[\\u0007\b\\u000C\n\r\t\'\"\\\\]")

  fun lex(source: String) = lex(source, emptyList(), Pos(0,0))
  private tailrec fun lex(source: String, tokens: List<Pair<Token, Pos>>, pos: Pos) : List<Pair<Token, Pos>> = when(val c = source.head) {
    null -> tokens

    // Whitespace and comments
    '\n' -> lex(source.tail, tokens, pos.newline())
    '#' -> lex(source.dropWhile { it != '\n' }.tail, tokens, pos.newline())
    in Whitespace ->
      lex(source.dropWhile { it in Whitespace }, tokens, pos.next(source.takeWhile { it in Whitespace }.length))

    // Brackets
    '[' -> lex(source.tail, tokens + (Token.OpenList to pos), pos.next())
    ']' -> lex(source.tail, tokens + (Token.CloseList to pos), pos.next())
    '(' -> lex(source.tail, tokens + (Token.OpenParen to pos), pos.next())
    ')' -> lex(source.tail, tokens + (Token.CloseParen to pos), pos.next())
    '{' -> lex(source.tail, tokens + (Token.OpenBlock to pos), pos.next())
    '}' -> lex(source.tail, tokens + (Token.CloseBlock to pos), pos.next())

    // Operators
    in Operators -> when(val opr = source.takeWhile { it in Operators }) {
      "." -> lex(source.tail, tokens + (Token.OpDot to pos), pos.next())
      "+" -> lex(source.tail, tokens + (Token.OpAdd to pos), pos.next())
      "-" -> lex(source.tail, tokens + (Token.OpSub to pos), pos.next())
      "*" -> lex(source.tail, tokens + (Token.OpMul to pos), pos.next())
      "/" -> lex(source.tail, tokens + (Token.OpDiv to pos), pos.next())
      "%" -> lex(source.tail, tokens + (Token.OpMod to pos), pos.next())
      "!" -> lex(source.tail, tokens + (Token.OpNot to pos), pos.next())
      "<" -> lex(source.tail, tokens + (Token.OpLt  to pos), pos.next())
      ">" -> lex(source.tail, tokens + (Token.OpGt  to pos), pos.next())
      "=" -> lex(source.tail, tokens + (Token.OpAsn to pos), pos.next())
      ":" -> lex(source.tail, tokens + (Token.OpTyp to pos), pos.next())
      "|" -> lex(source.tail, tokens + (Token.OpBar to pos), pos.next())
      "&&" -> lex(source.drop(2), tokens + (Token.OpAnd to pos), pos.next(2))
      "||" -> lex(source.drop(2), tokens + (Token.OpOr  to pos), pos.next(2))
      "==" -> lex(source.drop(2), tokens + (Token.OpEql to pos), pos.next(2))
      "!=" -> lex(source.drop(2), tokens + (Token.OpNeq to pos), pos.next(2))
      "<=" -> lex(source.drop(2), tokens + (Token.OpLeq to pos), pos.next(2))
      ">=" -> lex(source.drop(2), tokens + (Token.OpGeq to pos), pos.next(2))
      else -> throw ParseException("Encountered unexpected operator $opr", pos)
    }

    // Numbers
    in Numeric -> {
      val num = source.takeWhile { it.isDigit() || it == '.' }.split('.', limit=3).take(2).joinToString(".")
      lex(source.drop(num.length), tokens + (Token.NumberLiteral(num) to pos), pos.next(num.length))
    }

    // Identifiers
    in Alphabetic -> {
      val name = source.takeWhile { it in Alphanumeric }
      lex(source.drop(name.length), tokens + (Token.Identifier(name) to pos), pos.next(name.length))
    }

    // Strings
    '"', '\'' -> {
      val str = run loop@{ source.tail.fold("") { acc, ch ->
        if(ch == c && (!acc.endsWith('\\') || acc.endsWith("\\\\"))) return@loop acc
        acc + ch
      }}

      if(!source.drop(str.length+1).startsWith(c)) throw ParseException("Unexpected EOF while parsing string literal", pos)
      lex(source.drop(str.length+2), tokens + (Token.StringLiteral(str.unescape()) to pos), pos.next(str.length+2))
    }

    else -> throw ParseException("Encountered unexpected character $c", pos)
  }
}
