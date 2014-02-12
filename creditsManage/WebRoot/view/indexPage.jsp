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
	<script type="text/javascript">
		var bastPath = '<%=basePath%>';
	
		$(document).ready(function(){
			$('#mac').click(function() { 
				window.location.href = bastPath + "credit/macCredit.do";	
			});
			
			$('#accman').bind('vclick',function() { 
				window.location.href = bastPath + "credit/accCreditCount.do";	
			});
			
			$('#accinfo').bind('vclick',function() { 
				window.location.href = bastPath + "credit/showCreditWdList.do";	
			});
			
			$('#freezeUser').bind('vclick',function() { 
				freezeUser();
			});
		});
	
		function freezeUser() {
			var account = $.trim($("#account").val());
			if(account.length == 0){
				alert("请输入账号");
				return;
			}
			var r=confirm("确定拉黑此用户?");
			if (r==true){
			  	$.ajax({ 
			            type : "POST", 
			            url  : "<%=basePath%>user/freezeUser.do",  
			            cache : false, 
			            data : {
			            	'account':account
			            }, 
			            success : onSuccess, 
			            error : onError 
		        }); 
			} else {			  
			  	//alert("You pressed Cancel!");
			}
		}
		
		function onSuccess(data,status){ 
			if(data == "0") {
				alert("拉黑成功");
			}
			window.location.reload();		
		} 
		
		function onError(data,status){ 
    		//进行错误处理 
		} 
	
	</script>
  </head>
  
  <body>
    	<div id="userCredit" data-role="page">
    		<div data-role="header">
    			<h1>管理首页</h1>
    		</div>
    	
    		<div data-role="content">
    		
    				<input id="account" type="text" placeholder="输入账号"/>
    				<a id="freezeUser" data-role="button">拉黑</a>
    				<br>
    				<br>
    				<a id="mac" data-role="button">mac管理</a>
    				<a id="accman" data-role="button">account管理</a>
    				<a id="accinfo" data-role="button">account信息</a>
    		</div>
			    	
    		<div data-role="footer" data-position="fixed">
    			<h1>赚积分</h1>
    		</div>
    	</div>
  </body>
</html>
