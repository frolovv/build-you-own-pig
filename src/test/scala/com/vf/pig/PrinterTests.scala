package com.vf.pig

import org.junit.Test
import org.hamcrest.CoreMatchers.is
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers


/**
 * User: valeryf
 * Date: 12/10/12 11:43 PM
 */
class PrinterTests {


  def assertExpr(expr: Expr, expected: String) {
    val actual = Printer.exprToString(expr)

    assertThat(actual, Matchers.equalToIgnoringWhiteSpace(expected))
  }

  val dumpSamples = List(
    (DumpExpr(VarExpr("events")), "dump events;")
  )

  val simpleConditionSamples = List(
    (SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103)), "evid == 103")
  )


  val andSamples = List(
    (AndExpr(List(SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103)), SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103)))),
      "(evid == 103) and (evid == 103)")
  )

  val orSamples = List(
    (OrExpr(List(SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103)), SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103)))),
      "(evid == 103) or (evid == 103)")
  )

  val filterSamples = List(
    (FilterExpr(VarExpr("events"), AndExpr(List(SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103)), SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103))))),
      "filter events by (evid == 103) and (evid == 103)")
  )

  val loadSamples = List(
    (LoadExpr(SymbolicExpr("wix-bi"), UdfExpr("TableLoader", List(SymbolicExpr("users_by_src"))), SchemaExpr(List("uuid"), List("chararray"))),
      "load 'wix-bi' using TableLoader('users_by_src') as (uuid:chararray)")
  )

  val groupSamples = List(
    (GroupExpr(VarExpr("events"), List(FieldExpr("evid")), ParallelExpr(6)), "group events by evid Parallel 6")
  )

  val assignSamples = List(
    (AssignExpr(VarExpr("events"), LoadExpr(SymbolicExpr("wix-bi"), UdfExpr("TableLoader", List(SymbolicExpr("users_by_src"))), SchemaExpr(List("uuid"), List("chararray")))),
      "events = load 'wix-bi' using TableLoader('users_by_src') as (uuid:chararray);")
  )

  val limitSamples = List(
    (LimitExpr(VarExpr("events"), 10), "limit events 10")
  )

  val orderSamples = List(
    (OrderExpr(VarExpr("events"), List(OrderPair(FieldExpr("evid"), OrderDesc())), ParallelExpr(3)),
      "order events by evid desc parallel 3")
  )

  val unionSamples = List(
    (UnionExpr(VarExpr("events_user"), VarExpr("events_anon"), Nil),
      "union events_user, events_anon")
  )

  val describeSamples = List(
    (DescribeExpr(VarExpr("events")), "describe events;")
  )

  val storeSamples = List(
    (StoreExpr(VarExpr("events"), SymbolicExpr("wix-bi"), UdfExpr("PigStorage", List(SymbolicExpr("\t")))),
      "store events into 'wix-bi' using PigStorage('\t');")
  )

  val keyFilterExpr = List(
    (KeyFilterExpr("2012-12-12 00:00", "2012-12-12 01:00", 42), "date_created between (\"2012-12-12 00:00\", \"2012-12-12 01:00\") and src = 42")
  )

  val columnFilterExprSamples = List(
    (ColumnFilterExpr(SimpleCondition(ColumnFilterEquality(), FieldExpr("event:evid"), NumberExpr(100))),
      "event:evid = 100")
  )

  val wixTableLoaderSamples = List(
    (WixTableLoaderExpr("users_by_src",
        KeyFilterExpr("2012-12-12 00:00", "2012-12-12 01:00", 42),
        ColumnFilterExpr(SimpleCondition(ColumnFilterEquality(), FieldExpr("event:evid"), NumberExpr(100))),
        List("uuid", "evid")), "TableLoader('users_by_src',\n'date_created between (\"2012-12-12 00:00\", \"2012-12-12 01:00\") and src = 42',\n" +
          "'event:evid = 100',\n" + "'event:uuid event:evid')"
      )
  )

  val udfSamples = List(
    (UdfExpr("PigLoader", List(SymbolicExpr("\t"))), "PigLoader('\t')")
  )

  @Test def runUdfSamples() {
    runSamples(udfSamples)
  }

  @Test def runTableLoaders(){
    runSamples(wixTableLoaderSamples)
  }

  @Test def runColumnFilter() {
    runSamples(columnFilterExprSamples)
  }



  @Test def runKeyFilters(){
    runSamples(keyFilterExpr)
  }

  @Test def runStores(){
    runSamples(storeSamples)
  }

  @Test def runDescribe() {
    runSamples(describeSamples)
  }

  @Test def runUnion(){
    runSamples(unionSamples)
  }

  @Test def runOrder() {
    runSamples(orderSamples)
  }


  @Test def runLimits() {
    runSamples(limitSamples)
  }

  @Test def runAssigns() {
    runSamples(assignSamples)
  }

  @Test def runGroups() {
    runSamples(groupSamples)
  }


  @Test def runLoads() {
    runSamples(loadSamples)
  }


  @Test def runDump() {
    runSamples(dumpSamples)
  }

  @Test def runSimpleCondition() {
    runSamples(simpleConditionSamples)
  }

  @Test def runAndSamples() {
    runSamples(andSamples)
  }

  @Test def runOrSamples() {
    runSamples(orSamples)
  }

  @Test def runFilterSamples() {
    runSamples(filterSamples)
  }

  @Test def multiExprs() {
    val exprs = List(FilterExpr(VarExpr("events"), SimpleCondition(Equality(), FieldExpr("evid"), NumberExpr(103))), DumpExpr(VarExpr("events")))
    val expected = "filter events by evid == 103" + "\n" + "dump events;"

    assertThat(Printer.exprsToString(exprs), is(expected))
  }

  def runSamples(samples: List[(Expr, String)]) {
    for ((expr, str) <- samples) {
      assertExpr(expr, str)
    }
  }
}
