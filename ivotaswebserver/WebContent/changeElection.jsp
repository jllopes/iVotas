<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib prefix="s" uri="/struts-tags" %>
    <%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
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
	<script src="assets/js/admin.js"></script>
</head>
<body>
	<div class="container">
	    <div class="row">
	        <div class="col-md-3">
	            <ul class="nav nav-pills nav-stacked admin-menu">
	                <li><a href="#" id="home" action="adminPage">Home</a></li>
	                <li><a href="#" id="register" action="registerPage" class="active">Register User</a></li>
	                <li><a href="#" id="newElections" action="newElectionPage">Create Election</a></li>
	                <li class="active"><a href="#" id="elections" action="electionsPage">Manage Election</a></li>
	                <li><a href="#" id="electionResults" action="electionResultsPage">Past Election Results</a></li>
	                <li><a href="#" id="electionInfo" action="electionDetailsPage">Election Info</a></li>
	                <li><a href="#" id="userVote" action="userVotePage">User Vote Info</a></li>
	                <li><a href="#" id="tables" action="tablesPage">Online Tables</a></li>
	            </ul>
	        </div>
	        <div class="col-md-9 well admin-content" id="register">
	            <div id="signupbox" style="margin-top:50px" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="panel-title">Change Election</div>
                        </div>  
                        <div class="panel-body" >
                            <form id="signupform" class="form-horizontal" role="form" action="changeelection" method="POST">
                                
                                <div id="signupalert" style="display:none" class="alert alert-danger">
                                    <p>Error:</p>
                                    <span></span>
                                </div>
                                    
                                <div class="form-group">
                                		<label for="fac" class="col-sm-3 control-label">Election</label>
				                    <div class="col-sm-4">
                                    <select id="electionSelect" name="election" class="form-control">
                                    <option value="" disabled selected>Election</option>
									  <c:forEach items="${sessionBean.getAllElections()}" var="election">
									    <option value="${election.getId()}">
									    		${election.getName()}
    									    </option>
									  </c:forEach>
									</select>
									</div>
                                </div>
                                <div class="form-group">
                                    <label for="name" class="col-md-3 control-label">Name</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="name" placeholder="Name">
                                    </div>
                                </div>
                                    
                                <div class="form-group">
                                    <label for="description" class="col-md-3 control-label">Description</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="description" placeholder="Description">
                                    </div>
                                </div>
                                <div class="form-group">
				                    <label for="type" class="col-sm-3 control-label">Election Start</label>
				                    <div class="col-sm-4">
				                    		<input type="date" name="startDate">
									  	<input type="time" name="startTime">
				                    </div>
				                </div>
                                <div class="form-group">
				                    <label for="type" class="col-sm-3 control-label">Election Ending</label>
				                    <div class="col-sm-4">
				                    		<input type="date" name="endDate">
									  	<input type="time" name="endTime">
				                    </div>
				                </div>
                                <div class="form-group">
                                    <!-- Button -->                                        
                                    <div class="col-md-offset-3 col-md-9">
                                        <button id="btn-signup" type="submit" class="btn btn-primary"onclick="document.getElementById('signupform').submit()"><i class="icon-hand-right"></i> &nbsp Register</button>
                                    </div>
                                </div>
                                
                              </form>
                         </div>
                    </div>
                </div>
         	</div> 
	    </div>
	</div>
</body>
</html>