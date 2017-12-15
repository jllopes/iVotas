<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css">
	<link rel="stylesheet" href="assets/css/admin.css">
	<!--           -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
</head>
<body>
	<div class="container">
	    <div class="row">
	        <div class="col-md-3">
	            <ul class="nav nav-pills nav-stacked admin-menu">
	                <li><a href="#" id="home" action="home">Home</a></li>
	                <li class="active"><a href="http://www.jquery2dotnet.com" id="register" action="register" class="active">Register User</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="departments" action="departments">Manage Departments</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="faculties" action="faculties">Manage Faculties</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="newElections" action="newElection">Create Election</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="elections" action="elections">Manage Election</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="electionResults" action="electionResults">Past Election Results</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="userVote" action="userVote">User Vote Info</a></li>
	                <li><a href="http://www.jquery2dotnet.com" id="tables" action="tables">Online Tables</a></li>
	            </ul>
	        </div>
	        <div class="col-md-9 well admin-content" id="register">
	            <div id="signupbox" style="margin-top:50px" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="panel-title">Sign Up</div>
                        </div>  
                        <div class="panel-body" >
                            <c:out value="${}"/>
                         </div>
                    </div>
                </div>
         	</div> 
	    </div>
	</div>
</body>
</html>