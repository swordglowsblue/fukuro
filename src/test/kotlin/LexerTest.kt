package com.swordglowsblue.fukuro

import com.swordglowsblue.fukuro.core.Lexer
import com.swordglowsblue.fukuro.core.Lexer.Token.*
import com.swordglowsblue.fukuro.util.SourcePos
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class LexerTest : StringSpec({
  val lexerTest = Lexer.lex("""
    # Number literals
    100
    100.0213
    100.0213.1251

    # String literals
    "Hello, world!"
    'Hello, world!'
    "Escaped \""
    'Escaped \''
    "Escape chars \a\b\f\n\r\t\"\'\\"
    'Escape chars \a\b\f\n\r\t\"\'\\'

    # Identifiers
    test
    test2
    test_name
    _test
    _test2
    _test_name

    # Operators
    + - * / % ! < > = : |
    && || == != <= >=

    # Brackets
    [] () {}
  """.trimIndent())

  "Lexer#lex should properly lex Fukuro source files" {
    lexerTest.map { it.first } shouldBe listOf(
      NumberLiteral("100"),
      NumberLiteral("100.0213"),
      NumberLiteral("100.0213"), OpDot, NumberLiteral("1251"),

      StringLiteral("Hello, world!"),
      StringLiteral("Hello, world!"),
      StringLiteral("Escaped \""),
      StringLiteral("Escaped \'"),
      StringLiteral("Escape chars \u0007\b\u000C\n\r\t\"\'\\"),
      StringLiteral("Escape chars \u0007\b\u000C\n\r\t\"\'\\"),

      Identifier("test"),
      Identifier("test2"),
      Identifier("test_name"),
      Identifier("_test"),
      Identifier("_test2"),
      Identifier("_test_name"),

      OpAdd, OpSub, OpMul, OpDiv, OpMod, OpNot, OpLt, OpGt, OpAsn, OpTyp, OpBar,
      OpAnd, OpOr, OpEql, OpNeq, OpLeq, OpGeq,

      OpenList, CloseList, OpenParen, CloseParen, OpenBlock, CloseBlock
    )
  }

  "Lexer#lex should produce proper token positions" {
    lexerTest.map { it.second } shouldBe listOf(
      SourcePos(1, 0),
      SourcePos(2, 0),
      SourcePos(3, 0), SourcePos(3, 8), SourcePos(3, 9),

      SourcePos(6, 0),
      SourcePos(7, 0),
      SourcePos(8, 0),
      SourcePos(9, 0),
      SourcePos(10, 0),
      SourcePos(11, 0),

      SourcePos(14, 0),
      SourcePos(15, 0),
      SourcePos(16, 0),
      SourcePos(17, 0),
      SourcePos(18, 0),
      SourcePos(19, 0),

      SourcePos(22, 0), SourcePos(22, 2), SourcePos(22, 4), SourcePos(22, 6), SourcePos(22, 8), SourcePos(22, 10),
        SourcePos(22, 12), SourcePos(22, 14), SourcePos(22, 16), SourcePos(22, 18), SourcePos(22, 20),
      SourcePos(23, 0), SourcePos(23, 3), SourcePos(23, 6), SourcePos(23, 9), SourcePos(23, 12), SourcePos(23, 15),

      SourcePos(26, 0), SourcePos(26, 1), SourcePos(26, 3), SourcePos(26, 4), SourcePos(26, 6), SourcePos(26, 7)
    )
  }
})
