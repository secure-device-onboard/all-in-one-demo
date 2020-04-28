// Copyright 2020 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.demo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AioFilesServlet handles unauthenticated download of files.
 */
public class AioFilesServlet extends AioApiServlet {


  @Override
  protected boolean checkCredentials(HttpServletRequest req, HttpServletResponse res) {
    return true;
  }

  @Override
  protected boolean isMethodAllow(String method) {
    if (method.equals("GET")) {
      return true;
    }
    return false;
  }


  /**
   * getAsync Handles HTTP GET of owner files.
   *
   * @param asyncCtx Context
   */
  public void getAsync(AsyncContext asyncCtx) {

    HttpServletRequest req = (HttpServletRequest) asyncCtx.getRequest();
    HttpServletResponse res = (HttpServletResponse) asyncCtx.getResponse();

    String fsRootDir = new AioDb().getProperty("fs.root.dir");
    String fsFilesDir = new AioDb().getProperty("fs.files.dir");

    List<String> list = getPathElements(req.getRequestURI());
    if (list.size() > 2 && list.get(0).equals("api") && list.get(1).equals("v1")) {
      if (list.get(2).equals("files") && list.size() > 3) {
        Path filePath = Paths.get(fsRootDir, fsFilesDir, list.get(3));
        copyFile(filePath, res);

      } else {
        res.setStatus(404);
      }
    } else {
      res.setStatus(404);
    }
    asyncCtx.complete();
  }

}
