package com.vf.pig

import com.vf.wql.parser.WqlParser
import parser.{Wql2Pig, PigPrinter}

/**
 * User: valeryf
 * Date: 12/12/12 11:25 PM
 */
class WixPigEmitter(wql: String) extends PigPrinter with WqlParser with Wql2Pig {

  def emit(): String = {
    val wqls = this.parse(wql)
    val pigs = this.pigify(wqls)
    this.exprsToString(pigs)
  }
}
