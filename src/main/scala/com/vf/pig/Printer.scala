package com.vf.pig
/**
 * User: valeryf
 * Date: 12/10/12 11:27 PM
 */
object Printer {
  val semicolumn = ";"

  def exprToString(expr: Expr): String = {
    expr match {
      case DumpExpr(VarExpr(name)) => "dump " + name + semicolumn
      case DescribeExpr(VarExpr(name)) => "describe " + name + semicolumn
      case FilterExpr(VarExpr(name), condition: Expr) => {
        "filter " + name + " by " + exprToString(condition)
      }
      case Equality() => "=="
      case ColumnFilterEquality() => "="
      case FieldExpr(name) => name
      case NumberExpr(value) => value.toString
      case SymbolicExpr(value) => "'" + value + "'"
      case VarExpr(name) => name
      case EmptyCondition() => ""


      case AssignExpr(VarExpr(name), value) => name + " = " + exprToString(value) + semicolumn

      case AndExpr(exprs : List[ConditionExpr]) => {
        exprs match {
          case Nil => ""
          case e :: Nil => exprToString(e)
          case _ => {
            val mapped = exprs map exprToString
            val joined = mapped.mkString(") and (")
            "(" + joined + ")"
          }
        }
      }
      case OrExpr(exprs : List[ConditionExpr]) => {
        exprs match {
          case Nil => ""
          case e :: Nil => exprToString(e)
          case _ => {
            val mapped = exprs map exprToString
            val joined = mapped.mkString(") or (")
            "(" + joined + ")"
          }
        }
      }
      case SimpleCondition(operator, field, value) =>
        exprToString(field) + " " + exprToString(operator) + " " + exprToString(value)

      case GroupExpr(VarExpr(name), exprs, par) => exprs match {
        case FieldExpr(field) :: Nil => "group " + name + " by " + field + " " + exprToString(par)
        case exprs2 => {
          val mapped = exprs2 map exprToString
          val joined = mapped.mkString(", ")
          "(" + joined + ")"
        }
      }

      case LoadExpr(from: SymbolicExpr, udf: AbstractUdf, schema: SchemaExpr) => {
        "load " + exprToString(from) + " using " + exprToString(udf) + " as " + exprToString(schema)
      }
      case StoreExpr(VarExpr(name), dir, udf) => {
        "store " + name + " into " + exprToString(dir) + " using " + exprToString(udf) + ";"
      }

      case DistinctExpr(VarExpr(name), par) => "distinct " + name + exprToString(par)
      case LimitExpr(VarExpr(name), n) => "limit " + name + " " + n
      case OrderExpr(VarExpr(name), orders, par) => {
        val mapped = orders map exprToString
        val joined = mapped.mkString(", ")

        "order " + name + " by " + joined + " " + exprToString(par)
      }

      case OrderPair(FieldExpr(name), OrderDesc()) => name + " " + "desc"
      case OrderPair(FieldExpr(name), OrderAsc()) => name + " " + "asc"

      case UnionExpr(first, second, rest) => {
        val mapped = (first :: second :: rest) map exprToString
        val joined = mapped.mkString(", ")
        "union " + joined
      }

      case ParallelExpr(1) => ""
      case ParallelExpr(cnt) => "parallel " + cnt

      case UdfExpr(name, exprs) => {
        val values = exprs map exprToString
        val quoted = values map quote
        name + "(" + quoted.mkString(",\n\t") + ")"
      }

      case KeyFilterExpr(start, stop, src) => {
        "date_created between (\"" + start + "\", \"" + stop + "\") and src = " + src
      }

      case ColumnFilterExpr(conditions) => {
        exprToString(conditions)
      }

      case WixTableLoaderExpr(table, keyFilter, columnFilter, columns) => {
        val mapped = columns map {case x : String => "event:" + x}
        val joined = mapped.mkString(" ")

        exprToString(UdfExpr("TableLoader", List(SymbolicExpr(table), keyFilter, columnFilter, SymbolicExpr(joined))))
      }

      case SchemaExpr(names, types) =>
        val zip = names zip types

        val concat = zip map {
          case (n, t) => n + ":" + t
        }

        "(" + concat.mkString(",") + ")"
    }
  }

  def exprsToString(exprs: List[Expr]): String = {
    val values = exprs map exprToString
    values.mkString("\n")
  }

  def quote(str : String ) : String = {
    if(str.startsWith("'") && str.endsWith("'"))
      str
    else
      "'" + str + "'"
  }
}
