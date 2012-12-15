package com.vf.pig

import javax.servlet.http.{HttpServlet, HttpServletResponse, HttpServletRequest}
import util.parsing.json.JSONObject
/**
 * User: valeryf
 * Date: 12/12/12 7:05 PM
 */
class JsonEventsServlet extends HttpServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    resp.setContentType("application/json")
    resp.getWriter.print(generateResponse(req).getOrElse(""))
  }

  private def generateResponse(req: HttpServletRequest): Option[String] = {
    val src = req.getParameter("src").toInt
    val evids = (req.getParameter("evids").split(",").toList) map {case x : String => x.trim().toInt}
    val startDate = req.getParameter("startDate")
    val endDate = req.getParameter("endDate")
    val fields = (req.getParameter("fields").split(",").toList) map {case x : String => x.trim()}

    val emitter = new WixPigEmitter(src, evids, startDate, endDate, fields)

    val response = JSONObject(
      Map("pig" -> emitter.emit)
    )

    Some(response.toString())
  }
}
