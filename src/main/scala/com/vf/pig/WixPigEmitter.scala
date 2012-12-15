package com.vf.pig

/**
 * User: valeryf
 * Date: 12/12/12 11:25 PM
 */
class WixPigEmitter(src: Int, evids: List[Int], start: String, end: String, fields: List[String]) {

  def emitColumnFilter(evids: List[Int]): ConditionExpr = {
    evids match {
      case Nil => EmptyCondition()
      case _ => {
        val mapped = evids map {
          evid => SimpleCondition(ColumnFilterEquality(), FieldExpr("event:evid"), NumberExpr(evid))
        }
        OrExpr(mapped)
      }
    }
  }

  def emitTblLoader(src: Int, evids: List[Int], start: String, stop: String, fields: List[String]): AbstractUdf = {
    val columnFilter = emitColumnFilter(evids)
    WixTableLoaderExpr("users_by_src", KeyFilterExpr(start, stop, src), ColumnFilterExpr(columnFilter), fields)
  }

  def emitSchemaOf(names: List[String]): SchemaExpr = {
    val types = names map {
      case x if List("evid", "date_created", "src").contains(x) => "long"
      case _ => "chararray"
    }
    SchemaExpr(names, types)
  }

  def emit(): String = {

    val tblLoader = emitTblLoader(src, evids, start, end, fields)
    val schema = emitSchemaOf(fields)

    val expr1 = AssignExpr(VarExpr("events"), LoadExpr(SymbolicExpr("wix-bi"), tblLoader, schema))
    val expr2 = DumpExpr(VarExpr("events"))
    val exprs = List(expr1, expr2)

    Printer.exprsToString(exprs)
  }
}
