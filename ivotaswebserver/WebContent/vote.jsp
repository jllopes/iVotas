<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IVotas - Lists</title>
	<!-- CSS -->
	<link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css">
	<link rel="stylesheet" href="assets/css/notification.css">
	
	<!-- JS  -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script src="assets/js/notifications.js"></script>
</head>
<body>

	<c:choose>
		<c:when test="${electionLists.size() > 0}">
			<p>Election Lists: </p>
		</c:when>
		<c:otherwise>
			<p>No lists in this election ...</p>
		</c:otherwise>
	</c:choose>
	
	
	<form action="vote" >
		<input type='hidden' name=electionId id=electionId value="${electionId}" />
	
		<c:forEach items="${electionLists}" var="value">
	    		<c:out value="${value.value}" /> 
    			<input type="checkbox" name="listId" value="${value.key}" />
    			<!--  input type='hidden' name=listId id=listId value="${value.key}" />	-->
 			 	<!-- input type="submit" value="${value.value}"/> --><br>	
		</c:forEach>
		<input type="submit" value="Vote"/>
	</form>
	
	
	
	
	
	
	
	
	
	<button id="showError">Show Error Notification</button>
	<button id="showSuccess">Show Success Notification</button>
		
	<script>
	$('#showError').on('click', function(e) {
	    displayNotification('error', 'User already voted !!', 2000);
	    e.preventDefault();
	});

	$('#showSuccess').on('click', function(e) {
	    displayNotification('success', 'Vote submited with success !!', 2000);
	    e.preventDefault();
	});

	</script> 

</body>
</html>