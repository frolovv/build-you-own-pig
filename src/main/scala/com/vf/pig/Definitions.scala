package com.vf.pig

;

/**
 * User: valeryf
 * Date: 12/10/12 11:20 PM
 */
sealed abstract class Expr

case class AssignExpr(name: VarExpr, value: Expr) extends Expr

case class FilterExpr(what: VarExpr, condition: Expr) extends Expr

case class LoadExpr(what: SymbolicExpr, using: AbstractUdf, as: Expr) extends Expr

sealed abstract class AbstractUdf extends Expr

case class UdfExpr(name: String, exprs: List[Expr]) extends AbstractUdf

case class SymbolicExpr(value: String) extends Expr

case class SchemaExpr(names: List[String], types: List[String]) extends Expr

case class VarExpr(name: String) extends Expr

case class GroupExpr(what: VarExpr, exprs: List[FieldExpr], parallel: Expr) extends Expr

case class ParallelExpr(cnt: Int) extends Expr

case class DumpExpr(what: VarExpr) extends Expr

sealed class ConditionExpr extends Expr

case class AndExpr(exprs : List[Expr]) extends ConditionExpr

case class OrExpr(exprs : List[Expr]) extends ConditionExpr

case class SimpleCondition(operator: Expr, field: Expr, value: Expr) extends ConditionExpr

case class EmptyCondition() extends ConditionExpr

case class Equality() extends Expr
case class ColumnFilterEquality() extends Expr

case class FieldExpr(name: String) extends Expr

case class NumberExpr(value: Int) extends Expr

case class DistinctExpr(what: VarExpr, par: ParallelExpr) extends Expr

case class LimitExpr(what: VarExpr, cnt: Int) extends Expr

sealed abstract class Order extends Expr

case class OrderAsc() extends Order

case class OrderDesc() extends Order

case class OrderPair(field: FieldExpr, order: Order) extends Expr

case class OrderExpr(what: VarExpr, orders: List[OrderPair], par: ParallelExpr) extends Expr

case class UnionExpr(first: VarExpr, second: VarExpr, rest: List[VarExpr]) extends Expr

case class DescribeExpr(what: VarExpr) extends Expr

case class StoreExpr(what: VarExpr, where: SymbolicExpr, udf: AbstractUdf) extends Expr

case class WixTableLoaderExpr(table: String, keyFilter: KeyFilterExpr,
                              columnFilter: ColumnFilterExpr,
                              columns: List[String]) extends AbstractUdf

case class KeyFilterExpr(start: String, stop: String, src: Int) extends Expr

case class ColumnFilterExpr(conditions : ConditionExpr) extends Expr