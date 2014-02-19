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
    
    <title>提现页面</title>
    
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
    			<h1>详细提现信息</h1>
    		</div>
    	
    		<div data-role="content">
    			<div data-role="content">
    			
	    			<table data-role="table" id="movie-table-custom" data-mode="reflow" class="movie-list ui-responsive">
						  <thead>
						    <tr>
						      <th data-priority="1">用户</th>
						      <th style="width:25%">提现类别</th>
						      <th data-priority="2">账户</th>
						      <th data-priority="3">积分</th>
						      <th data-priority="4">操作</th>
						    </tr>
						  </thead>
						  <tbody>
						  	<c:forEach items="${wdCreditsList}" var="wdCredit">
						  		<tr>
							      <th>${wdCredit.account}</th>
							      <c:if test="${wdCredit.cashType eq '1'}">
							      		<td>支付宝</td>
							      </c:if>
							      <c:if test="${wdCredit.cashType eq '2'}">
							      		<td>qq币</td>
							      </c:if>
							      <c:if test="${wdCredit.cashType eq '3'}">
							      		<td>话费</td>
							      </c:if>
							      <td>${wdCredit.cashAccount}</td>
							      <td>${wdCredit.credit}元</td>
							      <td><a href="#"  onclick="withDraw('${wdCredit.account}','${wdCredit.credit}','${wdCredit.id}')">提现</a></td>
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
