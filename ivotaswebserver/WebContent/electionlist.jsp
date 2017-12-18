<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<!-- CSS -->
	<link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css">
	<link rel="stylesheet" href="assets/css/notification.css">
	<link rel="stylesheet" href="assets/css/details.css">
	<!-- JS  -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
	<script src="assets/js/notifications.js"></script>
	<script src="assets/js/details.js"></script>


<title>IVotas - Elections</title>
</head>
<body>


		
	<div class="container">
      <div class="row"  >
              <div class="col-md-offset-2 col-lg-offset-2 col-xs-10 col-sm-10 col-md-6 col-lg-6  toppad" >
      
	        <div class="panel panel-info">
            <div class="panel-heading">
              <h3 class="panel-title"><c:out value = "Current Elections: "/></h3>
            </div>
            <div class="panel-body">
              <div class="row">

                <div class=" col-md-12 col-lg-12 "> 
                  <table class="table table-user-information">
                    <tbody>
                    
						<c:choose>
							<c:when test="${sessionBean.elections.size() > 0}">
			                    	<c:forEach items="${sessionBean.elections}" var="value">
			                   			<tr>
			                    			<td><c:out value = "${value.value}"/></td>
											<td>
												<form action="electionlist">
													<input type='hidden' name=electionId id=electionId value="${value.key}"  />
													<input type='submit' value="Vote" class="btn btn-primary"/>
												</form>	
												
											</td>
									    </tr>	
									</c:forEach>
										<td>teste</td>
										<td>election</td>
							</c:when>
							<c:otherwise>
								<tr><td>
									No elections available at the moment...
								</td></tr>
							</c:otherwise>
						</c:choose>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
			</div>
          </div>	
		</div>
	</div>

	<s:if test="hasActionErrors()">
  <div class="error">
	   </div>		<script>
		    displayNotification('error', 'User already voted !!', 2000);	
		</script> 
	</s:if>
	
	<s:if test="hasActionMessages()">
		<div class="success"></div>
		<script>
		    displayNotification('success', 'Vote submited with success !!', 2000);	
		</script> 
	</s:if>


</body>
</html>