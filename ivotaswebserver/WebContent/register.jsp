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
                                		var select = document.getElementById("departmentSelect");
                                		var length = select.options.length;
                                		for(i = length-1 ; i>=0 ; i--){
                                			select.remove(i);
                                		}
                                		<c:forEach items="${sessionBean.getFaculties()}" var="faculty">
                                			<c:if test="${faculty.value != null}">
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
<jsp:include page="logoutheader.jsp" />
<body>
	<div class="container">
	    <div class="row">
	        <div class="col-md-3">
	            <ul class="nav nav-pills nav-stacked admin-menu">
	                <li><a href="<s:url action="homePage"/>" id="home" action="adminPage">Home</a></li>
	                <li class="active"><a href="<s:url action="registerPage"/>" id="register" >Register User</a></li>
	                <li><a href="<s:url action="newElectionPage"/>" id="newElections" >Create Election</a></li>
	                <li><a href="<s:url action="changeElectionPage"/>" id="elections" >Change Election</a></li>
	                <li><a href="<s:url action="chooseListTypePage"/>"id="tables" >Create Election List</a></li>
	                <li><a href="<s:url action="electionResultsPage"/>" id="electionResults" >Past Election Results</a></li>
	                <li><a href="<s:url action="electionDetailsPage"/>" id="electionInfo">Election Info</a></li>
	                <li><a href="<s:url action="userVotePage"/>" id="userVote">User Vote Info</a></li>
	                <li><a href="<s:url action="manageTable"/>" id="manageTable">Manage Tables</a></li>  
	                <li><a href="<s:url action="tablesPage"/>"id="tables" >Online Tables</a></li>
	            </ul>
	        </div>
	        <div class="col-md-9 well admin-content" id="register">
	            <div id="signupbox" style="margin-top:50px" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="panel-title">New User</div>
                        </div>  
                        <div class="panel-body" >
                            <form id="signupform" class="form-horizontal" role="form" action="register" method="POST">
                                
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
                                		<label for="fac" class="col-sm-3 control-label">Faculty</label>
				                    <div class="col-sm-4">
                                    <select onchange="departmentSelection()" id="facultySelect" name="faculty" class="form-control">
                                    <option value="" disabled selected>Faculty</option>
									  <c:forEach items="${sessionBean.getFaculties()}" var="faculty">
									    <option value="${faculty.key.value}">
									    		${faculty.key.key}
    									    </option>
									  </c:forEach>
									</select>
									</div>
                                </div>
                                <div class="form-group">
                               	<label for="dep" class="col-sm-3 control-label">Department</label>
				                    <div class="col-sm-4">
                                    <select id="departmentSelect" name="department" class="form-control">
                                    <option value="" disabled selected>Department</option>
									</select>
									</div>
                                </div>
                                <div class="form-group">
                                    <label for="id" class="col-md-3 control-label">Id Number</label>
                                    <div class="col-md-9">
                                        <input type="number" class="form-control" name="id" placeholder="Id">
                                    </div>
                                </div>
                                    
                                <div class="form-group">
                                    <label for="month" class="col-md-3 control-label">Validity Month</label>
                                    <div class="col-md-9">
                                        <input type="number" class="form-control" name="month" placeholder="12" min=0 max=12>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="year" class="col-md-3 control-label">Validity Year</label>
                                    <div class="col-md-9">
                                        <input type="number" class="form-control" name="year" placeholder="2017" min=1970 max=2050>
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="phone" class="col-md-3 control-label">Phone Number</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="phone" placeholder="123123123">
                                    </div>
                                </div>
                                
                                <div class="form-group">
                                    <label for="address" class="col-md-3 control-label">Address</label>
                                    <div class="col-md-9">
                                        <input type="text" class="form-control" name="address" placeholder="Address">
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