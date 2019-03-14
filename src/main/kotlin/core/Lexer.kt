package com.swordglowsblue.fukuro.core

import com.swordglowsblue.fukuro.util.SourcePos
import com.swordglowsblue.fukuro.util.*

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

  private val Operators = Regex("[.+\\-*/%!&|=:<>]")
  private val Whitespace = Regex("[ \t\b\r]")
  private val Numeric = Regex("\\d")
  private val Alphabetic = Regex("[a-zA-Z_]")
  private val Alphanumeric = Regex("\\w")

  fun lex(source: String) = lex(source, emptyList(), SourcePos(0,0))
  private tailrec fun lex(source: String, tokens: List<Pair<Token, SourcePos>>, sourcePos: SourcePos) : List<Pair<Token, SourcePos>> = when(val c = source.head) {
    null -> tokens

    // Whitespace and comments
    '\n' -> lex(source.tail, tokens, sourcePos.newline())
    '#' -> lex(source.dropWhile { it != '\n' }.tail, tokens, sourcePos.newline())
    in Whitespace ->
      lex(source.dropWhile { it in Whitespace }, tokens, sourcePos.next(source.takeWhile { it in Whitespace }.length))

    // Brackets
    '[' -> lex(source.tail, tokens + (Token.OpenList to sourcePos), sourcePos.next())
    ']' -> lex(source.tail, tokens + (Token.CloseList to sourcePos), sourcePos.next())
    '(' -> lex(source.tail, tokens + (Token.OpenParen to sourcePos), sourcePos.next())
    ')' -> lex(source.tail, tokens + (Token.CloseParen to sourcePos), sourcePos.next())
    '{' -> lex(source.tail, tokens + (Token.OpenBlock to sourcePos), sourcePos.next())
    '}' -> lex(source.tail, tokens + (Token.CloseBlock to sourcePos), sourcePos.next())

    // Operators
    in Operators -> when(val opr = source.takeWhile { it in Operators }) {
      "." -> lex(source.tail, tokens + (Token.OpDot to sourcePos), sourcePos.next())
      "+" -> lex(source.tail, tokens + (Token.OpAdd to sourcePos), sourcePos.next())
      "-" -> lex(source.tail, tokens + (Token.OpSub to sourcePos), sourcePos.next())
      "*" -> lex(source.tail, tokens + (Token.OpMul to sourcePos), sourcePos.next())
      "/" -> lex(source.tail, tokens + (Token.OpDiv to sourcePos), sourcePos.next())
      "%" -> lex(source.tail, tokens + (Token.OpMod to sourcePos), sourcePos.next())
      "!" -> lex(source.tail, tokens + (Token.OpNot to sourcePos), sourcePos.next())
      "<" -> lex(source.tail, tokens + (Token.OpLt  to sourcePos), sourcePos.next())
      ">" -> lex(source.tail, tokens + (Token.OpGt  to sourcePos), sourcePos.next())
      "=" -> lex(source.tail, tokens + (Token.OpAsn to sourcePos), sourcePos.next())
      ":" -> lex(source.tail, tokens + (Token.OpTyp to sourcePos), sourcePos.next())
      "|" -> lex(source.tail, tokens + (Token.OpBar to sourcePos), sourcePos.next())
      "&&" -> lex(source.drop(2), tokens + (Token.OpAnd to sourcePos), sourcePos.next(2))
      "||" -> lex(source.drop(2), tokens + (Token.OpOr  to sourcePos), sourcePos.next(2))
      "==" -> lex(source.drop(2), tokens + (Token.OpEql to sourcePos), sourcePos.next(2))
      "!=" -> lex(source.drop(2), tokens + (Token.OpNeq to sourcePos), sourcePos.next(2))
      "<=" -> lex(source.drop(2), tokens + (Token.OpLeq to sourcePos), sourcePos.next(2))
      ">=" -> lex(source.drop(2), tokens + (Token.OpGeq to sourcePos), sourcePos.next(2))
      else -> throw ParseException("Encountered unexpected operator $opr", sourcePos)
    }

    // Numbers
    in Numeric -> {
      val num = source.takeWhile { it.isDigit() || it == '.' }.split('.', limit=3).take(2).joinToString(".")
      lex(source.drop(num.length), tokens + (Token.NumberLiteral(num) to sourcePos), sourcePos.next(num.length))
    }

    // Identifiers
    in Alphabetic -> {
      val name = source.takeWhile { it in Alphanumeric }
      lex(source.drop(name.length), tokens + (Token.Identifier(name) to sourcePos), sourcePos.next(name.length))
    }

    // Strings
    '"', '\'' -> {
      val str = run loop@{ source.tail.fold("") { acc, ch ->
        if(ch == c && (!acc.endsWith('\\') || acc.endsWith("\\\\"))) return@loop acc
        acc + ch
      }}

      if(!source.drop(str.length+1).startsWith(c)) throw ParseException(
        "Unexpected EOF while parsing string literal",
        sourcePos
      )
      lex(source.drop(str.length+2), tokens + (Token.StringLiteral(str.unescape()) to sourcePos), sourcePos.next(str.length+2))
    }

    else -> throw ParseException("Encountered unexpected character $c", sourcePos)
  }
}
