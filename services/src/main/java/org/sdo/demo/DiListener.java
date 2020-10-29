// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DiListener.
 */
public class DiListener implements RelayListener {

  private static Logger logger = LoggerFactory.getLogger(DiListener.class);

  public DiListener() {

  }

  @Override
  public void beforeAccess(String url) {
  }

  @Override
  public void afterAccess(String url) {

    if (url.endsWith("/msg/12")) {
      try (AioDb db = new AioDb()) {
        db.connect();
        db.processDevices();
      } catch (SQLException e) {
        logger.error("Unable to connect with H2 database.");
      }
    }
  }

}
