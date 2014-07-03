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
    
    <title>用户下线页面</title>
    
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

		function withDraw(account, credit, id) {
		
			if("null" == credit || "" == credit || "0" == credit) {
				alert("该用户暂无可以提现的积分");
				return;
			}
		
			var r=confirm("确定完成此笔体现?");
			if (r==true){
			  	$.ajax({ 
			            type : "POST", 
			            url  : "<%=basePath%>credit/withDraw.apk",  
			            cache : false, 
			            data : {
			            	'account':account,
			            	'id':id,
			            	'credit':credit
			            }, 
			            success : onSuccess, 
			            error : onError 
		        }); 
			} else {			  
			  	//alert("You pressed Cancel!");
			}
		}
		
		function onSuccess(data,status){ 
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
    			<h1>用户下线信息</h1>
    		</div>
    	
    		<div data-role="content">
    			<div data-role="content">
    			
	    			<table data-role="table" id="movie-table-custom" data-mode="reflow" class="movie-list ui-responsive">
						  <thead>
						    <tr>
						      <th data-priority="1">账户</th>
						      <th style="width:25%">总积分</th>
						      <th data-priority="2">待提现积分</th>
						      <th data-priority="3">上线账号</th>
						      <th data-priority="4">IMEI</th>
						      <th data-priority="5">IMSI</th>
						      <th data-priority="6">状态</th>
						      <th data-priority="7">时间</th>
						    </tr>
						  </thead>
						  <tbody>
						  	<c:forEach items="${userList}" var="user">
						  		<tr>
							      <th>${user.account}</th>
							      <th>${user.totalCredit}</th>
							      <th>${user.pendCredit}</th>
							      <th>${user.linkId}</th>
							      <th>${user.imei}</th>
							      <th>${user.imsi}</th>
							      <c:if test="${user.state eq '1'}">
							      		<td>正常</td>
							      </c:if>
							      <c:if test="${user.state eq '2'}">
							      		<td>异常</td>
							      </c:if>
							      <td>${user.createTime}</td>
							    </tr>
						  	</c:forEach>
						  </tbody>
						</table>
	    		</div>
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
			<a href="#userCredit" data-role="button" data-rel="back" data-theme="b">关闭</a>
		</div>
		<!-- /content -->
		<div data-role="footer" align="center" data-position="fixed" data-theme="b">
			<h4>
			</h4>
		</div>
  </body>
</html>
