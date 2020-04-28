// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

public interface RelayListener {
  void beforeAccess(String url);

  void afterAccess(String url);
}
