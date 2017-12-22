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
	$(function()
			{
			    $(document).on('click', '.btn-add', function(e)
			    {
			        e.preventDefault();

			        var controlForm = $('.controls form:first'),
			            currentEntry = $(this).parents('.entry:first'),
			            newEntry = $(currentEntry.clone()).appendTo(controlForm);

			        newEntry.find('input').val('');
			        controlForm.find('.entry:not(:last) .btn-add')
			            .removeClass('btn-add').addClass('btn-remove')
			            .removeClass('btn-success').addClass('btn-danger')
			            .html('<span class="glyphicon glyphicon-minus"></span>');
			    }).on('click', '.btn-remove', function(e)
			    {
					$(this).parents('.entry:first').remove();

					e.preventDefault();
					return false;
				});
			});
	</script>
</head>
<jsp:include page="logoutheader.jsp" />
<body>
	<div class="container">
	    <div class="row">
	        <div class="col-md-3">
	            <ul class="nav nav-pills nav-stacked admin-menu">
	                <li><a href="<s:url action="homePage"/>" id="home" action="adminPage">Home</a></li>
	                <li><a href="<s:url action="registerPage"/>" id="register" >Register User</a></li>
	                <li><a href="<s:url action="newElectionPage"/>" id="newElections" >Create Election</a></li>
	                <li><a href="<s:url action="changeElectionPage"/>" id="elections" >Change Election</a></li>
	                <li  class="active"><a href="<s:url action="chooseListTypePage"/>"id="tables" >Create Election List</a></li>
	                <li><a href="<s:url action="electionResultsPage"/>" id="electionResults" >Past Election Results</a></li>
	                <li><a href="<s:url action="electionDetailsPage"/>" id="electionInfo" action="electionDetailsPage">Election Info</a></li>
	                <li><a href="<s:url action="userVotePage"/>" id="userVote">User Vote Info</a></li>
	                <li><a href="<s:url action="manageTable"/>" id="manageTable">Manage Tables</a></li>
	                <li><a href="<s:url action="tablesPage"/>"id="tables" >Online Tables</a></li>
	            </ul>
	        </div>
	        <div class="col-md-9 well admin-content" id="register">
	            <div id="signupbox" style="margin-top:50px" class="mainbox col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <div class="panel-title">New Election List</div>
                        </div>  
                        <div class="panel-body" >
                            <form id="signupform" class="form-horizontal" role="form" action="createList" method="POST">
                 
                                <div id="signupalert" style="display:none" class="alert alert-danger">
                                    <p>Error:</p>
                                    <span></span>
                                </div>
                                    				                
				                <div class="form-group">
				                    <label for="type" class="col-sm-3 control-label">Type</label>
				                    <div class="col-sm-4">
				                        <select name="listType" id="Select" class="form-control">
				                            <option value=1>Student</option>
				                            <option value=2>Professor</option>
				                            <option value=3>Employee</option>
				                          </select>
				                    </div>
				                </div>
				                
				                <div class="form-group">
				                    <label for="type" class="col-sm-3 control-label">Election</label>
				                    <div class="col-sm-4">
				                        <select name="electionId" id="Select" class="form-control">
                                    		<option value="" disabled selected>Election</option>
										  <c:forEach items="${sessionBean.getFutureElections()}" var="election">
										    <option value="${election.getId()}">
										    		${election.getName()}
	    									    </option>
										  </c:forEach>
				                          </select>
				                    </div>
				                </div>
				                
                                <div class="form-group">
                                    <!-- Button -->                                        
                                    <div class="col-md-offset-3 col-md-9">
                                        <button id="btn-signup" type="submit" class="btn btn-primary"onclick="document.getElementById('signupform').submit()"><i class="icon-hand-right"></i> &nbsp Create</button>
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