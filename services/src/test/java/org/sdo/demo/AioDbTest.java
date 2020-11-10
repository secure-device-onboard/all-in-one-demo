// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AioDbTest {

  private AioDb db;
  private AioDb mockDb;

  @BeforeEach
  public void setUp() throws SQLException {
    db = new AioDb();
    mockDb = mock(AioDb.class);
    when(mockDb.getAssignedVoucher(anyString())).thenReturn("{sz':1,'oh':{'pv':112}");
    when(mockDb.getOwnerKeyDescription()).thenReturn("owner");
    System.setProperty("rest.aio.url","http://localhost:8080");
  }

  @Test
  void getProperty() {
    final String expected = "http://localhost:8080";
    final String actual = db.getProperty("rest.aio.url");
    assertEquals(expected,actual);
  }

  @Test
  void getOwnerKeyDescription() throws SQLException {
    String actual = mockDb.getOwnerKeyDescription();
    assertEquals("owner", actual);
  }

  @Test
  void getAssignedVoucher() throws SQLException {
    final String expected = "{sz':1,'oh':{'pv':112}";
    String actual = mockDb.getAssignedVoucher("Intel123");
    assertEquals(expected, actual);
  }

  @Test
  void getUuidFromVoucher() throws SQLException {
    final String expected = "60015d54-99df-4aa6-ba5c-5ebdc7e99c0b";
    String actual = db.getUuidFromVoucher(",\"pow\":8040,\"pr\":\"http\"}]],\"g\":\"YAFdVJnfSqa6XF69x+mcCw==\",");
    assertEquals(expected,actual);
  }

}
