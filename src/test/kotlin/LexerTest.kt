package com.swordglowsblue.fukuro

import com.swordglowsblue.fukuro.core.Lexer
import com.swordglowsblue.fukuro.core.Lexer.Token.*
import com.swordglowsblue.fukuro.core.Lexer.Pos
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
      Pos(1, 0),
      Pos(2, 0),
      Pos(3, 0), Pos(3, 8), Pos(3, 9),

      Pos(6, 0),
      Pos(7, 0),
      Pos(8, 0),
      Pos(9, 0),
      Pos(10, 0),
      Pos(11, 0),

      Pos(14, 0),
      Pos(15, 0),
      Pos(16, 0),
      Pos(17, 0),
      Pos(18, 0),
      Pos(19, 0),

      Pos(22, 0), Pos(22, 2), Pos(22, 4), Pos(22, 6), Pos(22, 8), Pos(22, 10),
        Pos(22, 12), Pos(22, 14), Pos(22, 16), Pos(22, 18), Pos(22, 20),
      Pos(23, 0), Pos(23, 3), Pos(23, 6), Pos(23, 9), Pos(23, 12), Pos(23, 15),

      Pos(26, 0), Pos(26, 1), Pos(26, 3), Pos(26, 4), Pos(26, 6), Pos(26, 7)
    )
  }
})
