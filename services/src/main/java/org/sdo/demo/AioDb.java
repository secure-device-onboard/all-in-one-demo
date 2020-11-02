// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import org.apache.tomcat.util.IntrospectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AioDb Provides database access and configuration logic.
 */
public class AioDb implements AutoCloseable {

  private Connection conn = null;
  private static Logger logger = LoggerFactory.getLogger(AioDb.class);

  /**
   * Extract the property value based on key name.
   */
  public String getProperty(String name) {
    String result = System.getProperty(name);
    if (result != null) {
      result = IntrospectionUtils.replaceProperties(result, System.getProperties(), null);
    }
    return result;
  }

  /**
   * Set RendezvousInfo in Manufacturer instance.
   */
  public void setRvInfo() throws SQLException {
    String redirectFile = getProperty("org.sdo.to0.ownersign.to1d.bo");

    URI redirectUri = null;
    try {
      redirectFile = redirectFile.replace('\\', '/');
      URI pwd = Paths.get(".").toUri();
      URI resolved = pwd.resolve(new URI(redirectFile));
      redirectUri = resolved.normalize();
    } catch (URISyntaxException e) {
      logger.error("Unable to resolve RV URI");
    }

    Properties properties = new Properties();
    try (InputStream in = new FileInputStream(new File(redirectUri))) {
      properties.load(in);
    } catch (FileNotFoundException e) {
      logger.error("redirect.properties file not found");
      throw new SQLException(e);
    } catch (IOException e) {
      logger.error("Error reading the redirect.properties file.");
      throw new SQLException(e);
    }

    String port = properties.getProperty("port");
    String dns = properties.getProperty("dns");
    if (dns == null) {
      dns = properties.getProperty("ip");
    }
    if (dns != null) {
      StringBuilder builder = new StringBuilder();
      builder.append("http://");
      builder.append(dns);
      if (port != null) {
        builder.append(":");
        builder.append(port);
      }
      logger.info("setting rv " + builder.toString());
      setRvInfo(builder.toString());
    }
  }

  /**
   * Setup Rendezvous Info in Manufacturer.
   */
  public void setRvInfo(String value) throws SQLException {
    String sqlQuery = "UPDATE MT_SERVER_SETTINGS SET RENDEZVOUS_INFO = ?";
    try {
      PreparedStatement stmt = conn.prepareStatement(sqlQuery);
      stmt.setString(1, value);
      int affected = stmt.executeUpdate();
      if (affected > 0) {
        logger.info(affected + " rows affected");
      }
      logger.info("\n");
    } catch (SQLException e) {
      logger.info("Unable to update the DB with RV details");
    }
  }

  private String combinePath(String path1, String path2) {
    String result;
    if (path1.endsWith("/") && path2.startsWith("/")) {
      result = path1 + path2.substring(1);
    } else if (!path1.endsWith("/") && !path2.endsWith("/")) {
      result = path1 + "/" + path2;
    } else {
      result = path1 + path2;
    }
    return result;
  }

  protected String getUuidFromVoucher(String voucherString) {

    String uuidString = "";

    String query = "\"g\":\"";
    int pos = voucherString.indexOf(query);
    if (pos > 0) {
      int pos2 = voucherString.indexOf("\"", pos + query.length());
      if (pos2 > pos) {
        String guid = voucherString.substring(pos + query.length(), pos2);
        byte[] guidData = Base64.getDecoder().decode(guid);
        UUID uuid = getGuidFromByteArray(guidData);
        uuidString = uuid.toString();
      }
    }
    return uuidString;
  }

  private UUID getGuidFromByteArray(byte[] bytes) {
    ByteBuffer bb = ByteBuffer.wrap(bytes);
    long high = bb.getLong();//read 8 bytes
    long low = bb.getLong(); // read 8 bytes
    UUID uuid = new UUID(high, low);
    return uuid;
  }

  @Override
  public void close() throws SQLException {
    if (conn != null) {
      conn.close();
    }
  }

  /**
   * Setup DB.
   */
  public void connect() throws SQLException {
    String jdbcDriver = getProperty("spring.datasource.driverClassName");
    String dbUser = getProperty("spring.datasource.username");
    String dbPass = getProperty("spring.datasource.password");
    String dbUrl = getProperty("spring.datasource.url");

    try {
      // STEP 1: Register JDBC driver
      Class.forName(jdbcDriver);
    } catch (ClassNotFoundException e) {
      logger.error("Unable to connect with H2 database.");
      throw new SQLException(e);
    }

    conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
  }

  /**
   * Get a particular voucher.
   *
   * @param serialNumber  Serial Number of the voucher
   */
  public String getAssignedVoucher(String serialNumber) throws SQLException {

    String restUrl = combinePath(getProperty("rest.api.server"), "/api/v1/vouchers/")
        + serialNumber;

    HttpRequest.Builder reqBuilder = HttpRequest.newBuilder().uri(URI.create(restUrl)).GET();
    try {

      HttpClient hc = HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_1_1)
          .followRedirects(HttpClient.Redirect.NEVER)
          .sslContext(SSLContext.getInstance("TLS"))
          .sslParameters(new SSLParameters())
          .build();

      HttpResponse<String> response =
          hc.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());

      return response.body();

    } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
      throw new SQLException(e);
    }
  }

  /**
   * Get list of Owner public keys registered with Manufacturer.
   */
  public String getOwnerKeyDescription() throws SQLException {
    String result = null;
    try (Statement stmt = conn.createStatement()) {
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT CUSTOMER_DESCRIPTOR ");
      builder.append("FROM RT_CUSTOMER_PUBLIC_KEY");

      try (ResultSet rs = stmt.executeQuery(builder.toString())) {
        while (rs.next()) {
          result = rs.getString("CUSTOMER_DESCRIPTOR");

        }
      }
    }
    if (result == null) {
      throw new SQLException("No customer key defined");
    }
    return result;
  }

  /**
   * Perform operations on Voucher till TO0 operation.
   */
  public void processDevices() throws SQLException {
    String keyDescription = getOwnerKeyDescription();
    try (Statement stmt = conn.createStatement()) {
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT DEVICE_SERIAL_NO, ");
      builder.append("CUSTOMER_PUBLIC_KEY_ID ");
      builder.append("FROM RT_OWNERSHIP_VOUCHER");

      try (ResultSet rs = stmt.executeQuery(builder.toString())) {
        while (rs.next()) {
          String serialNumber = rs.getString("DEVICE_SERIAL_NO");
          rs.getInt("CUSTOMER_PUBLIC_KEY_ID");
          if (rs.wasNull()) {
            assignOwner(serialNumber, keyDescription);
            String assignedVoucher = getAssignedVoucher(serialNumber);
            createOwnerDevice(assignedVoucher);
            performTo0(getUuidFromVoucher(assignedVoucher));
          }
        }
      }
    }
  }

  /**
   * Extend the voucher for specific Owner.
   */
  public void assignOwner(String serialNo, String customerDescriptor) throws SQLException {

    String sqlQuery = "UPDATE RT_OWNERSHIP_VOUCHER SET CUSTOMER_PUBLIC_KEY_ID = "
        + "(SELECT CUSTOMER_PUBLIC_KEY_ID FROM RT_CUSTOMER_PUBLIC_KEY "
        + "WHERE CUSTOMER_DESCRIPTOR = ?) WHERE DEVICE_SERIAL_NO = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {
      stmt.setString(1, customerDescriptor);
      stmt.setString(2, serialNo);
      int affected = stmt.executeUpdate();
      if (affected > 0) {
        logger.info(affected + " rows affected");
      }
      logger.info("\n");
    }
  }

  /**
   * Get information about all devices in the database.
   *
   * <p>This method is used in AioInfoServlet.
   */
  public String getDevicesInfo() throws SQLException {
    logger.info("Inside getDevicesInfo()");

    JSONArray list = new JSONArray();
    try (Statement stmt = conn.createStatement()) {
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT e.DEVICE_SERIAL_NO, e.DI_END_DATETIME, s.UUID ");
      builder.append("FROM MT_DEVICE_STATE e ");
      builder.append("JOIN RT_OWNERSHIP_VOUCHER s on e.DEVICE_SERIAL_NO = s.DEVICE_SERIAL_NO");

      try (ResultSet rs = stmt.executeQuery(builder.toString())) {
        while (rs.next()) {
          JSONObject obj = new JSONObject();
          String serialNumber = rs.getString("DEVICE_SERIAL_NO");
          Timestamp timestamp = rs.getTimestamp("DI_END_DATETIME");
          obj.put("serial_no", serialNumber);
          obj.put("timestamp", timestamp.toString());
          obj.put("uuid", getUuidFromVoucher(getAssignedVoucher(serialNumber)));
          list.put(obj);
        }
      }
    }

    return list.toString();
  }

  /**
   * Get information about devices those were registered within a given period of time.
   *
   * <p>This method is used in AioInfoServlet.
   */
  public String getDevicesInfoWithTime(int seconds) throws SQLException {

    JSONArray list = new JSONArray();
    try (Statement stmt = conn.createStatement()) {
      StringBuilder builder = new StringBuilder();
      builder.append("SELECT e.DEVICE_SERIAL_NO, e.DI_END_DATETIME, s.UUID ");
      builder.append("FROM MT_DEVICE_STATE e ");
      builder.append("JOIN RT_OWNERSHIP_VOUCHER s on e.DEVICE_SERIAL_NO = s.DEVICE_SERIAL_NO");

      try (ResultSet rs = stmt.executeQuery(builder.toString())) {
        while (rs.next()) {

          String serialNumber = rs.getString("DEVICE_SERIAL_NO");
          Timestamp timestamp = rs.getTimestamp("DI_END_DATETIME");
          Timestamp cur = new Timestamp(System.currentTimeMillis());
          long diff = cur.getTime() - timestamp.getTime();
          long diffSeconds = diff / 1000;
          if (diffSeconds < seconds) {
            JSONObject obj = new JSONObject();
            obj.put("serial_no", serialNumber);
            obj.put("timestamp", timestamp.toString());
            obj.put("uuid", getUuidFromVoucher(getAssignedVoucher(serialNumber)));
            list.put(obj);
          }
        }
      }
    }
    return list.toString();
  }

  /**
   * Create the Device specific state files in Owner.
   */
  public void createOwnerDevice(String voucher) throws SQLException {

    String rootDir = getProperty("fs.root.dir");
    String voucherId = getUuidFromVoucher(voucher);
    String devicesDir = combinePath(rootDir,
        getProperty("fs.devices.dir"));
    String deviceDir = combinePath(devicesDir, voucherId);

    File file = new File(deviceDir);
    file.mkdir();
    byte[] voucherBytes = voucher.getBytes(StandardCharsets.US_ASCII);
    try {
      Files.write(Paths.get(combinePath(deviceDir, "voucher.json")), voucherBytes);
      String[] defaults = {"psi.json", "svi.json"};
      for (String fileName : defaults) {
        String devicePath = combinePath(deviceDir, fileName);
        String defaultPath = combinePath(rootDir, "v1");
        defaultPath = combinePath(defaultPath, "defaults");
        String defaultFile = combinePath(defaultPath, fileName);
        Files.copy(new File(defaultFile).toPath(), new File(devicePath).toPath(),
            StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new SQLException(e);
    }

  }

  /**
   * Perform TO0 Scheduling.
   */
  public void performTo0(String voucherId) throws SQLException {

    String t0ws = getProperty("to0.waitseconds");

    StringBuilder builder = new StringBuilder();
    builder.append("{\"guids\":[\"");
    builder.append(voucherId);
    builder.append("\"],");
    builder.append("\"waitSeconds\":\"");

    builder.append(Duration.ofSeconds(Long.parseLong(t0ws)).toString());
    builder.append("\"}");

    logger.info(builder.toString());

    HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
        .uri(URI.create(getProperty("to0.rest.api")))
        .POST(HttpRequest.BodyPublishers.ofString(builder.toString()))
        .setHeader("Content-Type", "application/json");

    try {

      HttpClient hc = HttpClient.newBuilder()
           .version(HttpClient.Version.HTTP_1_1)
           .followRedirects(HttpClient.Redirect.NEVER)
           .sslContext(SSLContext.getInstance("TLS"))
           .sslParameters(new SSLParameters())
           .build();

      HttpResponse<String> response =
          hc.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());

      logger.info(String.valueOf(response));

    } catch (IOException | InterruptedException | NoSuchAlgorithmException e) {
      logger.error("Error Performing TO0");
      throw new SQLException(e);
    }

  }

}
