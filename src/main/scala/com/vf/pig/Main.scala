package com.vf.pig

import org.mortbay.jetty.Server
import org.mortbay.jetty.handler.ResourceHandler
import org.mortbay.jetty.servlet.Context

/**
 * User: valeryf
 * Date: 12/12/12 1:43 AM
 */
object Main {
  def main(args: Array[String]) {
    val server = new Server(8080)

    val staticContect: Context = new Context(server, "/", 0)
    staticContect.setHandler(new ResourceHandler)
    staticContect.setResourceBase("./src/main/resources/")

    val servletContext: Context = new Context(server, "/", Context.SESSIONS)
    servletContext.addServlet(classOf[JsonEventsServlet], "/pig-factory")

    server.start()
    server.join()
  }
}
