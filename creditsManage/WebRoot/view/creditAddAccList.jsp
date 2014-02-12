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
    			<h1>account列表</h1>
    		</div>
    	
    		<div data-role="content">
    			<div data-role="content">
    			
	    			<ul data-role="listview">
	    				<c:forEach items="${creditsCountList}" var="acc">
		   					<li>
		   						
		   						<c:choose>
		   							<c:when test="${acc.credit > 30.0}">
		   								<a href="<%=basePath%>credit/showAccAddCreditList.do?account=${acc.account}" ><p style="color:red;">用户:${acc.account}, 积分:${acc.credit}</p></a>
		   							</c:when>
		   							<c:otherwise>
		   								<a href="<%=basePath%>credit/showAccAddCreditList.do?account=${acc.account}">用户:${acc.account}, 积分:${acc.credit} </a>
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
