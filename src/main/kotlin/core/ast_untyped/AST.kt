package com.swordglowsblue.fukuro.core.ast_untyped

import com.swordglowsblue.fukuro.util.SourcePos

sealed class SyntaxTree(val sourcePos: SourcePos)
sealed class Expression(sourcePos: SourcePos) : SyntaxTree(sourcePos)

class Identifier(val name: String, sourcePos: SourcePos) : Expression(sourcePos)
class Block(val body: List<Expression>, sourcePos: SourcePos) : Expression(sourcePos)

sealed class Literal<T>(val value: T, sourcePos: SourcePos) : Expression(sourcePos) {
  class Integer (value: Int, sourcePos: SourcePos)     : Literal<Int>(value, sourcePos)
  class Float   (value: Float, sourcePos: SourcePos)   : Literal<Float>(value, sourcePos)
  class String  (value: String, sourcePos: SourcePos)  : Literal<String>(value, sourcePos)
  class Boolean (value: Boolean, sourcePos: SourcePos) : Literal<Boolean>(value, sourcePos)
}

sealed class TypeAnnotation(val typeName: Identifier, val params: List<TypeAnnotation>, sourcePos: SourcePos) : SyntaxTree(sourcePos) {
  constructor(typeName: Identifier, sourcePos: SourcePos) : this(typeName, emptyList(), sourcePos)

  class Integer (sourcePos: SourcePos) : TypeAnnotation(Identifier("Int", sourcePos), sourcePos)
  class Boolean (sourcePos: SourcePos) : TypeAnnotation(Identifier("Bool", sourcePos), sourcePos)
  class Float   (sourcePos: SourcePos) : TypeAnnotation(Identifier("Float", sourcePos), sourcePos)
  class String  (sourcePos: SourcePos) : TypeAnnotation(Identifier("String", sourcePos), sourcePos)

  class UserDef(typeName: Identifier, params: List<TypeAnnotation>, sourcePos: SourcePos) : TypeAnnotation(typeName, params, sourcePos) {
    constructor(typeName: Identifier, sourcePos: SourcePos) : this(typeName, emptyList(), sourcePos)
  }
}

sealed class NameDecl(val name: Identifier, val type: TypeAnnotation?, sourcePos: SourcePos) : SyntaxTree(sourcePos) {
  class Typed(name: Identifier, type: TypeAnnotation, sourcePos: SourcePos) : NameDecl(name, type, sourcePos)
  class Untyped(name: Identifier, sourcePos: SourcePos) : NameDecl(name, null, sourcePos)
}

sealed class VarDecl(val name: NameDecl, val value: Expression, sourcePos: SourcePos) : Expression(sourcePos) {
  class Let(name: NameDecl, value: Expression, sourcePos: SourcePos) : VarDecl(name, value, sourcePos)
  class Mut(name: NameDecl, value: Expression, sourcePos: SourcePos) : VarDecl(name, value, sourcePos)
  class Upd(name: NameDecl.Untyped, value: Expression, sourcePos: SourcePos) : VarDecl(name, value, sourcePos)
}

class FunDecl(
  val name: Identifier,
  val params: List<NameDecl.Typed>,
  val returnType: TypeAnnotation,
  val body: Block,
  val extType: Identifier?,
  sourcePos: SourcePos) : SyntaxTree(sourcePos)
class Lambda(
  val params: List<NameDecl>,
  val body: Block,
  val usesIt: Boolean,
  sourcePos: SourcePos) : Expression(sourcePos)
class FunCall(
  val name: Identifier,
  val params: List<Expression>,
  val block: Block?,
  sourcePos: SourcePos) : Expression(sourcePos)

class StructDecl(
  val name: Identifier,
  val fields: List<NameDecl.Typed>,
  val implInterface: Identifier,
  sourcePos: SourcePos) : SyntaxTree(sourcePos)
class InterfaceDecl(
  val name: Identifier,
  val fields: List<NameDecl.Typed>,
  val implInterface: Identifier,
  sourcePos: SourcePos) : SyntaxTree(sourcePos)

sealed class UnaryOp(val operand: Expression, sourcePos: SourcePos) : Expression(sourcePos) {
  class Neg(operand: Expression, sourcePos: SourcePos) : UnaryOp(operand, sourcePos)
  class Not(operand: Expression, sourcePos: SourcePos) : UnaryOp(operand, sourcePos)
}

sealed class BinaryOp(val left: Expression, val right: Expression, sourcePos: SourcePos) : Expression(sourcePos) {
  class Dot(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Add(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Sub(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Mul(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Div(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Mod(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Lt (left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Gt (left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Leq(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Geq(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class And(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Or (left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Eql(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
  class Neq(left: Expression, right: Expression, sourcePos: SourcePos) : BinaryOp(left, right, sourcePos)
}
