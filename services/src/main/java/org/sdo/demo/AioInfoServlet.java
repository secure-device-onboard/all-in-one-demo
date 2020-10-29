// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AioInfoServlet extends AioApiServlet {

  @Override
  protected boolean isMethodAllow(String method) {
    if (method.equals("GET")) {
      return true;
    }
    return false;
  }

  /**
   * getAsync Handles HTTP GET for DeviceInfo.
   *
   * @param asyncCtx Context
   */
  public void getAsync(AsyncContext asyncCtx) {

    try (AioDb db = new AioDb()) {
      HttpServletRequest req = (HttpServletRequest) asyncCtx.getRequest();
      HttpServletResponse res = (HttpServletResponse) asyncCtx.getResponse();

      List<String> list = getPathElements(req.getRequestURI());
      System.out.println(list.toString());
      if (list.size() > 3) {
        try {
          int pollTime = Integer.parseInt(list.get(3));
          if (pollTime < 0) {
            pollTime = 20;
          }
          res.setContentType("application/json");
          res.setCharacterEncoding("utf-8");
          PrintWriter out = res.getWriter();
          db.connect();
          out.print(db.getDevicesInfoWithTime(pollTime));
          db.close();
        } catch (NumberFormatException e) {
          System.out.println("Invalid poll time. Couldn't fetch device information.");
        } catch (Exception e) {
          System.out.println("Unable to retrieve Device Information.");
        }
      } else {
        try {
          res.setContentType("application/json");
          res.setCharacterEncoding("utf-8");
          PrintWriter out = res.getWriter();
          db.connect();
          out.print(db.getDevicesInfo());
          db.close();
        } catch (Exception e) {
          System.out.println("Unable to retrieve Device Information.");
        }
      }
      asyncCtx.complete();
    } catch (SQLException e) {
      System.out.println("Invalid SQL statement.");
    }
  }
}