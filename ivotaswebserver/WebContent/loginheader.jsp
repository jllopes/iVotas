<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<nav class="navbar navbar-default">
  <div class="container-fluid">
   
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">iVotas</a>
    </div>

   
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <form id="signin" class="navbar-form navbar-right" role="form" action="login" method="post">
                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-user"></i></span>
                            <input id="email" type="text" class="form-control" name="username" value="" placeholder="Email Address">                                        
                        </div>

                        <div class="input-group">
                            <span class="input-group-addon"><i class="glyphicon glyphicon-lock"></i></span>
                            <input id="password" type="password" class="form-control" name="password" value="" placeholder="Password">                                        
                        </div>

                        <button type="submit" class="btn btn-success" >Login</button>
                        <a id="btn-fblogin" href="https://www.facebook.com/v2.2/dialog/oauth?client_id=176392666280433&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fivotaswebserver%2Ffacebook&scope=publish_actions" class="btn btn-primary">Login with Facebook</a>
                   </form>
     
    </div>
  </div>
</nav>
<body>

</body>
</html>