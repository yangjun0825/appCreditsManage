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
	
	<script type="text/javascript">
		
		var bastPath = '<%=basePath%>';
	
		$(document).ready(function(){
			
			$('#submitLogin').bind('vclick',function() { 
			
				var result = filterProcess();
				
				if (result) {
					var formData = $('#loginForm').serialize(); 
		        	//.serialize() 方法创建以标准 URL 编码表示的文本字符串 
		      		//alert("formData: " + formData);
		        	$.ajax({ 
			            type : "POST", 
			            url  : "<%=basePath%>user/login.apk",  
			            cache : false, 
			            data : formData, 
			            success : onSuccess, 
			            error : onError 
		        	}); 
				}
	        	
	        	return false; 
    		}); 
    		
			$("#device").css("width","100%");
	    	$("#device").css("height",$(document.body).height()/2);
		});
		
		function onSuccess(data,status){ 
			if(data=="0") {
				//window.location.href = bastPath + "credit/showCreditWdList.do";		
				window.location.href = bastPath + "user/indexPage.do";	
			} else {
				$("#promptInfo").html("用户名或密码不正确");
				$("#openDialog").click();
			}
		} 
 
		function onError(data,status){ 
    		//进行错误处理 
		} 
		
		//提交订单之前的过滤处理
		function filterProcess(){
			var account=$.trim($("#account").val());
				
			if(account.length == 0){
				$("#promptInfo").html("请输入姓名");
				$("#openDialog").click();
				//$("#mainPage").click();
				return false;
			}
			
			var password=$.trim($("#password").val());
			
			if(password.length == 0){
				$("#promptInfo").html("请输入密码");
				$("#openDialog").click();
				return false;
			}
			return true;
		}
	</script>
	
  </head>
  
  <body>
    	<div id="userLogin" data-role="page">
    		<div data-role="header">
    			<h1>用户登录</h1>
    		</div>
    	
    		<div data-role="content">
    			<a id="openDialog" href="#dialogPage" data-rel="dialog" data-transition="pop" style="display:none"  data-overlay-theme="a"></a> 
    			<form id="loginForm" data-transition="pop">
					<div data-role="fieldcontainer">
						<label for="name">用户名：</label>
						<input type="text" id="account" name="account" value=""/>
						<input type="hidden" name="account" value=""/>
					</div>
					<div data-role="fieldcontainer">
						<label for="password">密码：</label>
						<input type="password" id="password" name="password" value=""/>
					</div>
					<div data-role="fieldcontainer">
						<label for="submitLogin"></label>
						<button id="submitLogin" type="submit">登录</button>
					</div>
				</form>
    		</div>
			    	
    		<div data-role="footer" data-position="fixed">
    			<h1>赚积分</h1>
    		</div>
    	</div>
    	
    	<div id="dialogPage" data-role="page" data-theme="c" data-overlay-theme="e">
		<div data-role="header" data-theme="b">
			<h1>提示</h1>
		</div>
		<div data-role="content">
			<p id="promptInfo"></p>
			<!-- <a data-role="button" data-rel="back" data-theme="b" onclick="$('.ui-dialog').dialog('close'); return false;">确定</a>  -->     
			<a href="#userLogin" data-role="button" data-rel="back" data-theme="b">关闭</a>
		</div>
		<!-- /content -->
		<div data-role="footer" align="center" data-position="fixed" data-theme="b">
			<h4>
			</h4>
		</div>
  </body>
</html>
