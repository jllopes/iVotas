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
<title>IVotas - VOTE</title>
	<!-- CSS -->
	<link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css">
	<link rel="stylesheet" href="assets/css/vote.css">
	<link rel="stylesheet" href="assets/css/details.css">
	
	<!--           -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script src="assets/js/vote.js"></script>
	<script src="assets/js/details.js"></script>



</head>
<jsp:include page="logoutheader.jsp" />
<body>
	<div class="container" style="float:left" >
      <div class="row"  >

        <div class="col-md-offset-2 col-lg-offset-2 col-xs-10 col-sm-10 col-md-6 col-lg-6   toppad" >
   
          <c:set var="election" value="${sessionBean.getElectionInfo(electionId)}"/>
   
          <div class="panel panel-info">
            <div class="panel-heading">
              <h3 class="panel-title"><c:out value = "${election.getName()}"/></h3>
            </div>
            <div class="panel-body">
              <div class="row">

                <div class=" col-md-12 col-lg-12 "> 
                  <table class="table table-user-information">
                    <tbody>
                      <tr>
                        <td>Description:</td>
                        <td><c:out value = "${election.getDescription()}"/></td>
                      </tr>
                      <tr>
                        <td>Department</td>
                        <td><c:out value = "${election.getDepartment().getName()}"/></td>
                      </tr>
                   	  <tr>
                        <td>Start Date:</td>
                        <td><c:out value = "${election.getPrettyStartDate()}"/></td>
                      </tr>
                      <tr>
                        <td>End Date:</td>
                        <td><c:out value = "${election.getPrettyEndDate()}"/></td>
                      </tr>
                      <tr>
						<td><a id="btn-fblogin" class="btn btn-primary">Share to Facebook</a>                      <td>
                      </tr>
						<tr>
                      <td>Election Lists:</td>
                      <td>
						<form action="vote" method="POST">
							<input type='hidden' name=electionId id=electionId value="${electionId}" />
						
							<c:forEach items="${sessionBean.getListsElections(electionId)}" var="value">

							        <div class="form-group">
							        	<input type="checkbox" name="listId" value="${value.key}" id="${value.key}" autocomplete="off" />
							            
							            <div class="btn-group">
							                <label for="${value.key}" class="btn btn-default">
							                    <span class="glyphicon glyphicon-ok"></span>
							                    <span> </span>
							                </label>
							  				   	<select  class="btn btn-default active">
								      				<option>${value.value}</option>
									 			 	<c:forEach items="${sessionBean.getPeopleList(value.key)}" var="name">
												 		<option>${name}</option>
												 	</c:forEach>					 	
												</select>
							            </div>
							        </div>
						 	</c:forEach>	
						
						 	<tr>
								<td></td>							
								<td style="float:right">
									<input type="submit" value="&nbsp &nbsp &nbsp Vote &nbsp &nbsp &nbsp" class="btn btn-primary"/>
								</td>
							</tr>
						</form>
						<s:if test="sessionBean.hasFacebook()">
							<form>
								<input type='hidden' name=electionId id=electionId value="${electionId}" />
								<button type="submit" class="btn btn-primary" value="${sessionBean.shareToFacebook(electionId,session.accessToken,session.service)}">Share To Facebook</button>
							</form>
						</s:if>
						</tr>
                    </tbody>
                  </table>

                </div>
              </div>
            </div>

            
          </div>
        </div>

      

        <div class="col-md-offset-05 col-lg-offset-05 col-xs-10 col-sm-10 col-md-4 col-lg-4  toppad" >
   
          <c:set var="election" value="${sessionBean.getElectionInfo(electionId)}"/>
   
          <div class="panel panel-info">
            <div class="panel-heading">
              <h3 class="panel-title"><c:out value = "Votes per Table"/></h3>
            </div>
            <div class="panel-body">
              <div class="row">

                <div class=" col-md-12 col-lg-12 "> 
                  <table class="table table-user-information">
                    <tbody>
                  		<c:forEach items="${sessionBean.getElectionVotes(electionId)}" var="value">
                        <tr>
                        	<td><c:out value = "${value.key}"/></td>
                        	<td><c:out value = "${value.value}"/></td>
                     	</tr>
						</c:forEach>
                    </tbody>
                  </table>

                </div>
              </div>
            </div>

            
          </div>
        </div>
	 	</div>
	
    </div>




</body>
</html>