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
    <c:set var="bean" value="${sessionBean}"/>

    <c:set var="electionInfo" value="${bean.getElectionInfo(election)}"/>

	<div class="container" style="float:left" >
	
      <div class="row"  >
        <div class="col-md-offset-2 col-lg-offset-2 col-xs-10 col-sm-10 col-md-6 col-lg-6   toppad" >
          <div class="panel panel-info">
            <div class="panel-heading">
              <h3 class="panel-title"><c:out value = "${electionInfo.getName()}"/></h3>
            </div>
            <div class="panel-body">
              <div class="row">

                <div class=" col-md-12 col-lg-12 "> 
                  <table class="table table-user-information">
                    <tbody>
                      <tr>
                        <td>Election:</td>
                        <td><c:out value = "${sessionBean.getVote(vote).getElection().getName()}"/></td>
                        	
                      </tr>
                      <tr>
                        <td>Vote Table:</td>
                        <td><c:out value = "${sessionBean.getVote(vote).getTable().getDepartment().getName()}"/></td>
                        	
                      </tr>
                   	  <tr>
                        <td>Vote Time:</td>
                        <td><c:out value = "${sessionBean.getVote(vote).getDate()	}"/></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
        </div>
        </div>


	<button class="btn btn-primary" onclick="window.history.back();">Go Back</button>
    
</body>