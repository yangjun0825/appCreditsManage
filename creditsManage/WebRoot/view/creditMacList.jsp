<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>积分页面</title>
    
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
    	<div id="userCredit" data-role="page">
    		<div data-role="header">
    			<h1>mac列表</h1>
    		</div>
    	
    		<div data-role="content">
    			<div data-role="content">
    			
	    			<ul data-role="listview">
	    				<c:forEach items="${creditsMacList}" var="mac">
		   					<li>
		   						<c:choose>
		   							<c:when test="${mac.credit > 50.0}">
		   								<a href="<%=basePath%>credit/userInfo.do?macAddress=${mac.macAddress}" ><p style="color:red;">mac:${mac.macAddress}, 总积分:${mac.credit}</p> </a>
		   							</c:when>
		   							<c:otherwise>
		   								<a href="<%=basePath%>credit/userInfo.do?macAddress=${mac.macAddress}" >mac:${mac.macAddress}, 总积分:${mac.credit} </a>
		   							</c:otherwise>
		   						</c:choose>
		   						
		   					</li>
	   					</c:forEach>
	   				</ul>
	    		</div>
    		</div>
			    	
    		<div data-role="footer" data-position="fixed">
    			<h1>赚积分</h1>
    		</div>
    	</div>
  </body>
</html>
