<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html  lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>IVotas</title>
	<!-- CSS -->
	<link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css">
	<link rel="stylesheet" href="assets/css/admin.css">
	<!--           -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<!-- <script src="assets/js/admin.js"></script> -->
</head>
<jsp:include page="logoutheader.jsp" />
<body>
	<div class="container">
	    <div class="row">
	        <div class="col-md-3">
	            <ul class="nav nav-pills nav-stacked admin-menu">
	                <li class="active"><a href="<s:url action="homePage"/>" id="home" action="adminPage">Home</a></li>
	                <li><a href="<s:url action="registerPage"/>" id="register" >Register User</a></li>
	                <li><a href="<s:url action="newElectionPage"/>" id="newElections" >Create Election</a></li>
	                <li><a href="<s:url action="changeElectionPage"/>" id="elections" >Change Election</a></li>
	                <li><a href="<s:url action="chooseListTypePage"/>"id="tables" >Create Election List</a></li>
	                <li><a href="<s:url action="electionResultsPage"/>" id="electionResults" >Past Election Results</a></li>
	                <li><a href="<s:url action="electionDetailsPage"/>" id="electionInfo">Election Info</a></li>
	                <li><a href="<s:url action="userVotePage"/>" id="userVote">User Vote Info</a></li>
	                <li><a href="<s:url action="addTable"/>" id="addTable">Add Table</a></li> 
	                <li><a href="<s:url action="tablesPage"/>"id="tables" >Online Tables</a></li>
	            </ul>
	        </div>
	        <div class="col-md-9 well admin-content" id="home">
	            <h2>Welcome to the admin page!</h2>
	            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vehicula blandit erat, nec tristique felis molestie a. Sed dictum facilisis eros a fermentum. Donec est est, aliquam sit amet ante non, scelerisque luctus orci. Ut vel eleifend ligula, eget blandit neque. In odio ex, porta eget arcu eu, blandit fermentum odio. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque aliquet magna sit amet justo feugiat imperdiet. Donec quis varius elit. Donec sit amet lectus a quam porta pellentesque eget in lorem. Praesent et interdum dui, ut porttitor dui. Cras in ligula auctor, ornare est sit amet, vehicula nisl. Maecenas et bibendum erat. Sed varius sapien vitae nibh semper, in facilisis ante dictum. Fusce sed nisi nec arcu tempor placerat. Praesent arcu tellus, elementum vel purus semper, tincidunt accumsan ante.</p>
	        </div>
	    </div>
	</div>
</body>
</html>