<%@ page language="java" pageEncoding="UTF-8"%>  
<%@page import="com.letv.common.util.ConfigUtil"%>
<script>
	$(function(){
		if($(".user span").text()){
			$(".user").show();
			$(".login").hide();
		}else{
			$(".user").hide();
			$(".login").show();
		}
	});
</script>
<style>
  body{font-family: "Helvetica Neue", "Luxi Sans", "DejaVu Sans", Tahoma, "Hiragino Sans GB", STHeiti, "Microsoft YaHei";}
  .oauth_header{background:rgba(254,254,254,1);border-top:1px solid #ddd;border-bottom:1px solid #ddd;overflow:hidden;width:100%;max-width:1920px;height:85px;padding:12px 12.9% 0;border-top:0;}
  .oauth_container{overflow:hidden;width:100%;height:auto;margin:0 auto;}
  .oauth_container i{font-size:1.8rem;}
  .ul_horizon{list-style: none;display: inline-block;padding:0px;}
  .ul_horizon li{display:inline-block;_zoom:1;_display:inline;overflow:hidden;margin-right:5px;}
  .ul_horizon li a{padding:5px 10px;/*color:#97a6b6;*/color:#4A4F54;}
  .letv-logo>img{padding:5px;}
</style>
<div class="oauth_header">
  <div class="oauth_container">
    <ul class="ul_horizon pull-left">
      <li><a class="letv-logo" href="http://www.letvcloud.com/" target="_blank"><img src="${ctx}/static/img/letv-logo5.png" style="height:66px;width:232px;"></a></li>
    </ul>
    <ul class="ul_horizon pull-right" style="margin-top:30px;font-size:14px;">
      <li class="index_a"><a href="" ><i class="fa fa-home"></i> 首页</a></li>
      <li class="console"><a href='<%=ConfigUtil.getString("oauth.auth.http") + "/index?redirect_uri=" + ConfigUtil.getString("webportal.local.http") +"/oauth/callback"  %>' ><i class="fa fa-desktop"></i> 控制台</a></li>
      <li class="user"><span href="" ><i class="fa fa-user"></i> ${sessionScope.userSession.userName}</span></li>   
      <li class="login"><a href='<%=ConfigUtil.getString("oauth.auth.http") + "/index?redirect_uri=" + ConfigUtil.getString("webportal.local.http") +"/oauth/callback"  %>' ><i class="fa fa-key"></i> 登录</a></li>
      <li class="reg_a"><a href='<%=ConfigUtil.getString("oauth.auth.http") + "/reg.jsp?redirect_uri=" + ConfigUtil.getString("webportal.local.http") +"/oauth/callback"  %>'><i class="fa fa-tag"></i> 注册</a></li>
      <li class="index_a"><a href="${ctx}/helpCenter/helpCenter.jsp" ><i class="fa fa-magic"></i> 帮助中心</a></li>
    </ul>
    
  </div>
</div>
