// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.tomcat.util.IntrospectionUtils;
import org.h2.tools.Server;

/**
 * Listen for startup of Web Apps.
 */
public class AioContextListener implements ServletContextListener {
  private static boolean dbLoaded;

  @Override
  public void contextDestroyed(ServletContextEvent event) {
  }

  //Run this before web application is started
  @Override
  public void contextInitialized(ServletContextEvent event) {
    if (!dbLoaded) {
      dbLoaded = true;
      System.out.println("Loading AIO database..");

      String dbUrl = new AioDb().getProperty("spring.datasource.url");
      System.out.println(dbUrl);

      List<String> argList = new ArrayList<String>();
      argList.add("-ifExists");
      String tcpParam = new AioDb().getProperty("db.tcp.port");
      if (tcpParam != null && !tcpParam.isEmpty()) {
        argList.add("-tcp");
        argList.add("-tcpPort");
        argList.add(tcpParam);
      }

      String webParam = new AioDb().getProperty("db.web.port");
      if (webParam != null && !webParam.isEmpty()) {
        argList.add("-web");
        argList.add("-webPort");
        argList.add(webParam);
        System.out.println("adding db web support");
      }

      final String[] args = new String[argList.size()];
      argList.toArray(args);

      new Thread(() -> startDb(args)).start();

      int maxRetries = 5;
      while (maxRetries > 0) {
        try (AioDb aioDb = new AioDb()) {
          aioDb.connect();
          aioDb.setRvInfo();
          maxRetries = 0;
        } catch (SQLException e) {
          maxRetries--;
          try {
            Thread.sleep(1000);
          } catch (InterruptedException ex) {
            maxRetries = 0;
          }
        }
      }
    }
  }

  private void startDb(final String[] args) {
    try {
      Server.main(args);
    } catch (SQLException e) {
      System.out.println(e.getStackTrace());
    }
  }
}
