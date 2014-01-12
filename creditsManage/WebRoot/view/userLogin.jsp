<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>登录页面</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<%=basePath%>ui/css/jquery.mobile-1.4.0.min.css">
	<script type="text/javascript" src="<%=basePath%>ui/jquery-1.10.2.min.js"></script>
	<script type="text/javascript">
			$(document).bind("mobileinit", function(){
   				 $.mobile.ajaxEnabled = false;
			});
	</script>
	<script type="text/javascript" src="<%=basePath%>ui/jquery.mobile-1.4.0.min.js"></script>
  </head>
  
  <body>
    	<div data-role="page">
    		<div data-role="header">
    			<h1>用户登录</h1>
    		</div>
    	
    		<div data-role="content">
    			<form id="loginForm" data-transition="pop">
   					<div data-role="fieldcontainer">
   						<label for="name">用户名：</label>
   						<input type="text" id="name" name="name" value=""/>
   						<input type="hidden" name="pluginId" value="${pluginId}"/>
   					</div>
   					<div data-role="fieldcontainer">
   						<label for="phone">密码：</label>
   						<input type="text" id="phone" name="phone" value=""/>
   					</div>
   					<div data-role="fieldcontainer">
   						<label for="submitLogin"></label>
   						<button id="submitLogin" type="submit">登录</button>
   					</div>
   					
   				</form>
				
    		</div>
			    	
    		<div data-role="footer" data-position="fixed" align="center" data-theme="b">
    			<h1>赚积分</h1>
    		</div>
    	</div>
  </body>
</html>
