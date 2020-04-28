// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import java.sql.SQLException;

/**
 * DiListener.
 */
public class DiListener implements RelayListener {

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
        e.printStackTrace();
      }
    }
  }

}
