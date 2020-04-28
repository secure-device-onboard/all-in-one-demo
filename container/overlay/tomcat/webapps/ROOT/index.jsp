<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>AIO File Upload</title>
</head>
<body>
<a>AIO File Upload:</a>
Select file: <br />
<form action="do-upload.jsp" method="post"
                        enctype="multipart/form-data">
<input type="file" name="file" size="50" />
<input type="hidden" name="db" value="v1" />
<input type="hidden" name="table" value="defaults" />
<input type="hidden" name="target" value="svi.json" />
<br />
<input type="submit" value="Upload File" />
</form>
</body>
</html>