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

</head>
<body>
<style>
.dropbtn {
    background-color: #4CAF50;
    color: white;
    padding: 16px;
    font-size: 16px;
    border: none;
    cursor: pointer;
}

.dropbtn:hover, .dropbtn:focus {
    background-color: #3e8e41;
}

.dropdown {
    position: relative;
    display: inline-block;
}

.dropdown-content {
    display: none;
    position: absolute;
    background-color: #f9f9f9;
    min-width: 160px;
    overflow: auto;
    box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
    z-index: 1;
}

.dropdown-content a {
    color: black;
    padding: 12px 16px;
    text-decoration: none;
    display: block;
}

.dropdown a:hover {background-color: #f1f1f1}

.show {display:block;}
</style>


    <c:set var="election" value="${sessionBean.getElectionInfo(electionId)}"/>
      <br><c:out value = "${election.getName()}"/>
      <br><c:out value = "${election.getDescription()}"/>
      <br><c:out value = "${election.getDepartment().getName()}"/>
      <br><c:out value = "${election.getPrettyStartDate()}"/>
     <br> <c:out value = "${election.getPrettyEndDate()}"/>
      <br><c:out value = "${election.getBlankVotes()}"/>
      <br><c:out value = "${election.getNullVotes()}"/>

	<c:choose>
		<c:when test="${electionLists.size() > 0}">
			<p>Election Lists: </p>
		</c:when>
		<c:otherwise>
			<p>No lists in this election ...</p>
		</c:otherwise>
	</c:choose>
	
	
	<form action="vote" method="POST">
		<input type='hidden' name=electionId id=electionId value="${electionId}" />
	
		<c:forEach items="${electionLists}" var="value">
	    		<c:out value="${value.value}" /> 
    			<input type="checkbox" name="listId" value="${value.key}" />
    			<!--  input type='hidden' name=listId id=listId value="${value.key}" />	-->
 			 	<!-- input type="submit" value="${value.value}"/> --><br>	
				<div class="dropdown" >
				<button type="button" onclick="myFunction(${value.key})" class="dropbtn">
					<img src="https://cedcn.org/wp-content/themes/cedcn/images/icon-arrow_dropdown.svg" width=25 height=25>
				</button>
				  <div id="${value.key}" class="dropdown-content">
	 			 	<c:forEach items="${sessionBean.getPeopleList(value.key)}" var="name">
				 		<a>${name}</a>
				 	</c:forEach>
				  </div>
				</div>
				<br></br>

		 	</c:forEach>
		<input type="submit" value="Vote"/>
	</form>
	
	


	<script> 
	/* When the user clicks on the button, 
	toggle between hiding and showing the dropdown content */
	function myFunction( x) {
	    document.getElementById(x).classList.toggle("show");
	}
	
	// Close the dropdown if the user clicks outside of it
	window.onclick = function(event) {
	  if (!event.target.matches('.dropbtn')) {
	
	    var dropdowns = document.getElementsByClassName("dropdown-content");
	    var i;
	    for (i = 0; i < dropdowns.length; i++) {
	      var openDropdown = dropdowns[i];
	      if (openDropdown.classList.contains('show')) {
	        openDropdown.classList.remove('show');
	      }
	    }
	  }
	}
	</script>


</body>
</html>