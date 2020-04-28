<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*,java.util.*, javax.servlet.*, java.nio.file.Paths " %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="org.apache.commons.io.*" %>
<%@ page import="org.sdo.demo.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>AIO File Upload</title>
  </head>
  <body>
    <%
      String fsRootPath = new AioDb().getProperty("fs.root.dir");
      String filePath =Paths.get(fsRootPath, "v1", "values", "package.sh").toString();
      String contentType = request.getContentType();

      if ((contentType.indexOf("multipart/form-data") >= 0)) {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(servletContext);

        factory.setFileCleaningTracker(fileCleaningTracker);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        List fileItems = upload.parseRequest(request);

        Iterator items = fileItems.iterator();

        while (items.hasNext()) {
          FileItem item = (FileItem)items.next();
          if (!item.isFormField()) {
            String fieldName = item.getFieldName();
            String fileName = item.getName();
            boolean isInMemory = item.isInMemory();
            long sizeInBytes = item.getSize();
            File file = new File( filePath) ;
            item.write( file ) ;
            out.println("Uploaded File: " + fileName + "<br>");
          }
        }

        out.println("</body>");
        out.println("</html>");
      } else {
        out.println("<html>");
        out.println("<body>");
        out.println("<p>No file to upload</p>");
        out.println("</body>");
        out.println("</html>");
      }
    %>
  </body>
</html>
