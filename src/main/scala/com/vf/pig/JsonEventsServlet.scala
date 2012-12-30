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
    val wql = req.getParameter("wql")

    val emitter = new WixPigEmitter(wql)

    val response = JSONObject(
      Map("pig" -> emitter.emit)
    )

    Some(response.toString())
  }
}
