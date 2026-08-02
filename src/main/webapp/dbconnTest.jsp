<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Connection"%>
<%@page import="connection.ConnectionProvider"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	try (Connection conn = ConnectionProvider.getConnection()) {
		out.println("connection success");
	} catch (SQLException e) {
		out.println("connection failed : " + e.getMessage());
		application.log("connection failed", e);
	}
%>
</body>
</html>