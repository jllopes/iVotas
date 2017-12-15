<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IVotas - Elections</title>
</head>
<body>

	<c:choose>
		<c:when test="${sessionBean.elections.size() > 0}">
			<p>Available elections: </p>
		</c:when>
		<c:otherwise>
			<p>No elections available at the moment...</p>
		</c:otherwise>
	</c:choose>
		
		<c:forEach items="${sessionBean.elections}" var="value">
		   		<c:out value="${value.key}" /> 	
				<form action="electionlist">
					<input type='hidden' name=electionId id=electionId value="${value.key}" />
					<input type='submit' value="${value.value}" />
				</form>				
		</c:forEach>	


</body>
</html>