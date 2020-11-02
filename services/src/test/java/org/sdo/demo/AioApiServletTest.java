package org.sdo.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class AioApiServletTest {


  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private AioApiServlet obj;

  @BeforeEach
  public void setup() {
    obj = new AioApiServlet();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    System.setProperty("rest.api.username","aio");
    System.setProperty("rest.api.password","Sm9@wojk");
  }

  @AfterEach
  public void teardown() {
    request.close();
    obj = null;
  }

  @Test
  void checkCredentials() {
    String auth = Base64.getEncoder().encodeToString(("aio:Sm9@wojk").getBytes());
    request.addHeader("Authorization", "Basic " + auth);
    boolean val = obj.checkCredentials(request,response);
    assertEquals(true, val);
  }

  @Test
  void checkCredentialsFail() {
    String auth = Base64.getEncoder().encodeToString(("aio2:aio").getBytes());
    request.addHeader("Authorization", "Basic " + auth);
    boolean val = obj.checkCredentials(request,response);
    assertEquals(false, val);
  }

  @Test
  void getPathElements() {
    List<String> res = obj.getPathElements("mp/v1/api/deviceInfo");
    List<String> expected = Arrays.asList("v1","api","deviceInfo");
    assertEquals(res,expected);
  }

  @Test
  void getPathElementsFail() {
    List<String> res = obj.getPathElements("mp/v1/api/deviceInfo");
    List<String> expected = Arrays.asList("v2","api","deviceInfo");
    assertNotEquals(res,expected);
  }

  @Test
  void getPathName() {
    String expected = "testfolder";
    String actual = obj.getPathName("/home/test/testfolder");
    System.out.println(actual);
    assertEquals(expected, actual);
  }

  @Test
  void copyFile() {
    assertDoesNotThrow( ()->
        obj.copyFile(request, response, Paths.get("/home")));
  }


  @Test
  void isMethodAllow() {
    String method ="GET";
    boolean expected = true;
    boolean actual = obj.isMethodAllow(method);
    assertEquals(expected, actual);
  }


}