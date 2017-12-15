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
	<script>
	                                	function departmentSelection(){
	                                		console.log("cona cona")
	                                		var select = document.getElementById("departmentSelect");
	                                		var length = select.options.length;
	                                		for(i = length-1 ; i>=0 ; i--){
	                                			select.remove(i);
	                                		}
	                                		<c:forEach items="${sessionBean.getFaculties()}" var="faculty">
	                                			console.log("cona cona cona")
	                                			<c:if test="${faculty.value != null}">
	                                				console.log("cona cona cona cona")
		                                			var id = "<c:out value="${faculty.key.value}"/>";
		                                			if(id == document.getElementById("facultySelect").value){
		                                				<c:forEach items="${faculty.value}" var="department">
														var option = document.createElement('option');
			                                				option.text = "<c:out value="${department.key}"/>"
			                                				option.value = "<c:out value="${department.value}"/>"
			                                				departmentSelect.add(option,0)
		    									    		</c:forEach>
		                                			}
	                                			</c:if>
									  	</c:forEach>
									  
	                                	}
                                </script>
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
                            <form id="signupform" class="form-horizontal" role="form">
                                
                                <div id="signupalert" style="display:none" class="alert alert-danger">
                                    <p>Error:</p>
                                    <span></span>
                                </div>
                                    
                                <div class="form-group">
                                    <label for="username" class="col-md-3 control-label">Username</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="username" placeholder="Username">
                                    </div>
                                </div>
                                    
                                <div class="form-group">
                                    <label for="password" class="col-md-3 control-label">Password</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="password" placeholder="Password">
                                    </div>
                                </div>
                                <div class="form-group">
				                    <label for="type" class="col-sm-3 control-label">Type</label>
				                    <div class="col-sm-4">
				                        <select name="type" id="Select" class="form-control">
				                            <option>User</option>
				                            <option>Professor</option>
				                            <option>Employee</option>
				                            <option>Administrator</option>
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
                                    <select onchange="departmentSelection()" id="facultySelect" name="faculties">
                                    <option value="" disabled selected>Faculty</option>
									  <c:forEach items="${sessionBean.getFaculties()}" var="faculty">
									    <option value="${faculty.key.value}">
									    		${faculty.key.key}
    									    </option>
									  </c:forEach>
									</select>
                                </div>
                                <div class="form-group">
                                    <select id="departmentSelect" name="departments">
                                    <option value="" disabled selected>Department</option>
									</select>
                                </div>
                                <div class="form-group">
                                    <label for="department" class="col-md-3 control-label">Department</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="department" placeholder="Department">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label for="id" class="col-md-3 control-label">Id Number</label>
                                    <div class="col-md-9">
                                        <input type="password" class="form-control" name="id" placeholder="Id">
                                    </div>
                                </div>
                                    
                                <div class="form-group">
                                    <label for="month" class="col-md-3 control-label">Validity Month</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="month" placeholder="12">
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="year" class="col-md-3 control-label">Validity Year</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="year" placeholder="2017">
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="phone" class="col-md-3 control-label">Phone Number</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="phone" placeholder="915151515">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <!-- Button -->                                        
                                    <div class="col-md-offset-3 col-md-9">
                                        <button id="btn-signup" type="button" class="btn btn-primary" onclick="document.getElementById('signupform').submit()"><i class="icon-hand-right"></i> &nbsp Register</button>
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