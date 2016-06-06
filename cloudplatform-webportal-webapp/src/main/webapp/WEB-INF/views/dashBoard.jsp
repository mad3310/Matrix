<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
	<meta charset="utf-8"/>
	<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1"/>
	<meta name="viewport" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no"/>
	<!-- bootstrap css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css"/>
	<!-- fontawesome css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css"/>
	<!-- ui-css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css"/>
	<title>Le云控制台首页</title>
	<script>
	 var browserInfo = function(){			
			var bagent = navigator.userAgent.toLowerCase();
			var regStr_ie = /msie [\d.]+;/gi ;
			var regStr_ff = /firefox\/[\d.]+/gi
			var regStr_chrome = /chrome\/[\d.]+/gi ;
			var regStr_saf = /safari\/[\d.]+/gi ;
			//IE
			if(bagent.indexOf("msie") > 0){
				return bagent.match(regStr_ie) ;
			}

			//firefox
			if(bagent.indexOf("firefox") > 0){
				return bagent.match(regStr_ff) ;
			}

			//Chrome
			if(bagent.indexOf("chrome") > 0){
				return bagent.match(regStr_chrome) ;
			}

			//Safari
			if(bagent.indexOf("safari") > 0 && bagent.indexOf("chrome") < 0){
				return bagent.match(regStr_saf) ;
			}
		}
	    var browserVersion = function(){
	    	  var _browser = browserInfo().toString().toLowerCase();
	    	  var verinfo = (_browser+"").replace(/[^0-9.]/ig,"");    	 
	    	  if(_browser.indexOf("msie") >=0 && (verinfo < 9.0)){
	    		  window.location.replace="/browserError";
	    	  }else if(_browser.indexOf("firefox") >=0 && verinfo < 5.0){
	    		  window.location.replace="/browserError";
	    	  }else if(_browser.indexOf("chrome") >=0 && verinfo < 7.0){
	    		  window.location.replace="/browserError";
	    	  }else if(_browser.indexOf("safari") >=0 && verinfo < 4.0){
	    		  window.location.replace="/browserError";
	    	  }
	    }
	    
	    browserVersion(); //浏览器检测初始化
	</script>
</head>
<body class="bodycolor"> 
	<%@ include file="../layouts/header.jsp"%>
	<div class="container-fluid bodycolor"><!-- main-content begin-->
		<div class="home">
			<div class="home-content">
				<div class="user-profile row"><!-- begin user-profile -->
					<div class="col-xs-12 col-sm-12 col-md-6"><!-- begin userinfo left-->
						<div class="info clearfix hidden-xs">
							<div class="user-info pull-left">
								<p class="text-muted user-name ellipsis">Hi,<span target="_self" class="home-orange" text-length="8">${sessionScope.userSession.userName}</span></p>
								<p class="user-email ellipsis">${sessionScope.userSession.email}</p>
							</div>
							<div class="account pull-left">
								<div class="mlt-4 pull-left">
									<p class="balance-title">账户余额:</p>
									<p class="balance-num ellipsis" >
										<span>100</span>
										<span class="balance-after-point">.00</span>
										<span class="balance-unit">元</span>
									</p>
								</div>
								<div class="account-opt pull-left hidden-xs hidden-sm">
									<div class="account-opt-row">
										<a href="javascript:void(0)" target="_self" class="btn btn-default btn-sm disabled disabled">充值</a>
										<a href="javascript:void(0)" target="_self" class="btn btn-default btn-sm disabled">提现</a>
										<a href="javascript:void(0)" target="_self" class="btn btn-default btn-sm disabled">索取发票</a>
									</div>
									<div>
										<a href="javascript:void(0)" target="_self" class="btn btn-default btn-sm disabled">订单管理</a>
									</div>
								</div>
							</div> 
						</div>
						<div class="m-info hidden-sm hidden-md hidden-lg">
							<div class="text-center">
								<div class='m-userimg'><img src="${ctx}/static/img/help/header.png"></div>
								<p class="text-muted user-name ellipsis">Hi,<span target="_self" class="home-orange" text-length="8">${sessionScope.userSession.userName}</span></p>
								<p class="user-email ellipsis">${sessionScope.userSession.email}</p>
							</div>
							<!-- <div class="account pull-left">
								<div class="mlt-4 pull-left">
									<p class="balance-title">账户余额:</p>
									<p class="balance-num ellipsis" >
										<span>100</span>
										<span class="balance-after-point">.00</span>
										<span class="balance-unit">元</span>
									</p>
								</div>
							</div> --> 
						</div>
					</div><!-- end userinfo left-->
					<div class="hidden-xs hidden-sm col-md-6 yundun-wrap"><!-- begin cloud right-->
						<div class="yundun-bg  yundun-bg-notopen"></div>
						<div class="yundun">
							<div class="yundun-inner clearfix">
								<div class="pull-left">
									<a class="product-icons-48 product-icons-yundun-grey" href="javascript:void(0)"></a>
								</div>
								<div class="yundun-content pull-left">
									<div class="yundun-title">
										<a href="javascript:void(0)">云盾</a>
										<div class="yundun-title-tips">
											<span class="glyphicon glyphicon-info-sign text-warning"></span>
											<span class="text-danger">您尚未购买云主机或负载均衡,在您购买后会自动开启云盾服务</span>
										</div>
									</div>
									<div>
										<div class="yundun-actions clearfix">
											<div class="clearfix">
												<div class="yundun-action yundun-action-status">
													<a href="javascript:void(0)"><span class="glyphicon glyphicon-cloud text-muted"></span> <span class="text-muted">未开通</span>
													</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div><!-- end cloud right-->
				</div><!-- end user-profile -->
				<div class="product-list product-list-opened"><!-- 已开通产品 begin -->
					<div class="list-title">
						<span class="list-title-em">已开通</span>
						的产品与服务:
						<span class='hidden-sm hidden-md hidden-lg pull-right pd-r8'><span class='glyphicon glyphicon-chevron-up'></span></span>
					</div>
					<ul class="row">
						<li id="rds-opened" class="hide product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="${ctx}/list/db" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-rds"></span>
								</a>
								<p class="item-title">
									<a href="/rds">关系型数据库&nbsp;<span class="item-title-short">RDS</span>
									</a>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<a class="item-record-num-count" href="${ctx}/list/db"><span id="dbCount">0</span></a>
								</span>
								<span class="item-record-unit">个</span>
								<a data-toggle="tooltip" data-placement="top" title="数据库数">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a><!-- 已开通产品 begin -->
							</div>
						</li>
						<li id="slb-opened" class="hide product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="${ctx}/list/slb" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-slb"></span>
								</a>
								<p class="item-title">
									<a href="${ctx}/list/slb">负载均衡&nbsp;<span class="item-title-short">SLB</span>
									</a>
									<span class="beta-font">Beta</span>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<a class="item-record-num-count" href="${ctx}/list/slb"><span id="slbCount">0</span></a>
								</span>
								<span class="item-record-unit">个</span>
								<a data-toggle="tooltip" data-placement="top" title="负载均衡数">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a>
							</div>
						</li>
						<li id="gce-opened" class="hide product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="${ctx}/list/gce" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-ace"></span>
								</a>
								<p class="item-title">
									<a href="${ctx}/list/gce">云引擎&nbsp;<span class="item-title-short">GCE</span>
									</a>
									<span class="beta-font">Beta</span>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<a class="item-record-num-count" href="${ctx}/list/gce"><span id="gceCount"></span></a>
								</span>
								<span class="item-record-unit">个</span>
								<a data-toggle="tooltip" data-placement="top" title="云引擎数">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a>
							</div>
						</li>
						<li id="ocs-opened" class="hide product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="${ctx}/list/cache" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-ocs"></span>
								</a>
								<p class="item-title">
									<a href="${ctx}/list/cache">开放缓存服务&nbsp;<span class="item-title-short">OCS</span>
									</a>
									<span class="beta-font">Beta</span>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<a class="item-record-num-count" href="${ctx}/list/cache"><span id="ocsCount"></span></a>
								</span>
								<span class="item-record-unit">个</span>
								<a data-toggle="tooltip" data-placement="top" title="缓存数">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a>
							</div>
						</li>
						<li id="oss-opened" class="hide product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="${ctx}/list/oss" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-oss"></span>
								</a>
								<p class="item-title">
									<a href="${ctx}/list/oss">开放存储服务&nbsp;<span class="item-title-short">OSS</span>
									</a>
									<span class="beta-font">Beta</span>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<a class="item-record-num-count" href="${ctx}/list/oss"><span id="ossCount"></span></a>
								</span>
								<span class="item-record-unit">个</span>
								<a data-toggle="tooltip" data-placement="top" title="oss数">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a>
							</div>
						</li>
						<li id="vm-opened" class="hide product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="${ctx}/list/vm" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-yunzhuji"></span>
								</a>
								<p class="item-title">
									<a href="${ctx}/list/vm">云主机&nbsp;<span class="item-title-short">ECS</span>
									</a>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<a class="item-record-num-count" href="${ctx}/list/vm"><span id="vmCount">0</span></a>
								</span>
								<span class="item-record-unit">个</span>
								<a data-toggle="tooltip" data-placement="top" title="云主机数">
									<span class="glyphicon glyphicon-question-sign text-muted" ></span>
								</a><!-- 已开通产品 begin -->
							</div>
						</li>
						<li class="product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="javascript:void(0)" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-jiankong"></span>
								</a>
								<p class="item-title">
									<a href="javascript:void(0)">云监控&nbsp;<span class="item-title-short"></span></a>
								</p>
							</div>
							<div class="item-record">
								<span class="item-record-num">
									<span class="font-disabled">0</span>
								</span> 
								<span class="item-record-unit">个</span> 
								<span class="glyphicon glyphicon-question-sign text-muted" data-toggle="tooltip" data-placement="top" title="监控点"></span>
							</div>
						</li>
						<li class="product-item col-xs-12 col-sm-12 col-md-3 col-lg-3">
							<div class="item-profile clearfix">
								<a href="javascript:void(0)" class="pull-left">
									<span class="item-icon product-icons-48 product-icons-toolsimage"></span>
								</a>
								<p class="item-title">
									<a href="javascript:void(0)">工具与镜像&nbsp;<span class="item-title-short"></span></a>
								</p>
							</div>
						</li>
					</ul>
				</div><!-- 已开通产品 end -->
				<div class="product-list product-list-notopen"><!-- 未开通产品 begin -->
					<div class="list-title">
						<span class="list-title-em">未开通</span>的产品与服务:
						<span class="hidden-sm hidden-md hidden-lg pull-right pd-r8"><span class="glyphicon glyphicon-chevron-up"></span></span>
					</div>
					<ul class="row">
						<li class="col-xs-12 col-sm-12 col-md-3 col-lg-3 product-col">
							<div class="product-category">
								<p class="category-title ng-binding">弹性计算</p>
								<ul>
									<li id="vm-not-opened">
										<div class="pull-left clearfix">
											<span class="glyphicon glyphicon-th-list text-muted"></span> 
											<span>云主机</span>
											<span>ECS</span>
											<span class="beta-font">Beta</span>
										</div>
										<div class="product-opts pull-right clearfix">
											<div class="pull-left product-opt-wrap">
												<a href="/detail/vmCreate" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="立即购买">
													<span id="rds-purchase" class="glyphicon glyphicon-shopping-cart product-opt-icon"></span>
												</a>
											</div>
											<div class="pull-left product-opt-wrap">
												<a href="/helpCenter/helpCenter.jsp" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="产品详情">
													<span id="rds-purchase" class="glyphicon glyphicon-question-sign product-opt-icon"></span>
												</a>
											</div>
										</div>
									</li>
									<li  id="slb-not-opened">
										<div class="pull-left clearfix">
											<span class="glyphicon glyphicon-tasks text-muted"></span>
											<span>负载均衡</span>
											<span>SLB</span>
											<span class="beta-font">Beta</span>
										</div>
										<div class="pull-right clearfix">
											<div class="pull-left product-opt-wrap">
												<a href="/detail/slbCreate" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="立即购买">
													<span id="rds-purchase" class="glyphicon glyphicon-shopping-cart product-opt-icon"></span>
												</a>
											</div>
											<div class="pull-left product-opt-wrap">
												<a href="/helpCenter/helpCenter.jsp?container=product-SLBIntro" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="产品详情">
													<span id="rds-purchase" class="glyphicon glyphicon-question-sign product-opt-icon"></span>
												</a>
											</div>
										</div>
										<!-- <div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div> -->
									</li>
									<li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-cloud-upload text-muted"></span> 
											<span>弹性伸缩服务</span>
											<span>ESS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									
										<div class="pull-left">
											<!-- <span class="glyphicon glyphicon-random text-muted"></span> 
											<span>弹性伸缩服务</span>
											<span>ESS</span>  -->
										</div>
										<div class="pull-right clearfix">
											<!-- <span class="home-orange">敬请期待...</span> -->
										</div>
									</li>
								</ul>
							</div>
							<div class="product-category">
								<p class="category-title ng-binding">数据库</p>
								<ul>
									<li id="rds-not-opened" class="hide clearfix">
										<div class="pull-left">
											<span class="fa fa-database text-muted"></span>
											<span>关系型数据库</span>
											<span>RDS</span>
										</div>
										<div class="pull-right clearfix">
											<div class="pull-left product-opt-wrap">
												<a href="/rds" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="立即购买">
													<span id="rds-purchase" class="glyphicon glyphicon-shopping-cart product-opt-icon"></span>
												</a>
											</div>
											<div class="pull-left product-opt-wrap">
												<a href="/helpCenter/helpCenter.jsp?container=help-RDSIntro" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="产品详情">
													<span id="rds-purchase" class="glyphicon glyphicon-question-sign product-opt-icon"></span>
												</a>
											</div>
										</div>
									</li>
									<!-- <li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-tower text-muted"></span> 
											<span>开放结构化数据服务</span>
											<span>OTS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li> -->
									<li id="ocs-not-opened" class="clearfix">
										<div class="pull-left" data-type='tip' data-order='1' data-content='开发缓存服务<br>' data-position='top'>
											<span class="glyphicon glyphicon-leaf text-muted"></span> 
											<span>开放缓存服务</span>
											<span>OCS</span>
											<span class="beta-font">Beta</span>
										</div>
										 <div class="pull-right clearfix" data-type='tip' data-order='2' data-content='开放缓存服务<br>购买开放缓存服务' data-position='right'> 
											<div class="pull-left product-opt-wrap">
												<a href="/detail/cacheCreate" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="立即购买">
													<span id="ocs-purchase" class="glyphicon glyphicon-shopping-cart product-opt-icon"></span>
												</a>
											</div>
											<div class="pull-left product-opt-wrap">
												<a href="/helpCenter/helpCenter.jsp?container=help-OCSIntro" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="产品详情">
													<span id="rds-purchase" class="glyphicon glyphicon-question-sign product-opt-icon"></span>
												</a>
											</div>
										</div>
										<%--<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div> --%>
									</li>
									<li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-fullscreen text-muted"></span> 
											<span>分布式关系型数据库</span>
											<span>DRDS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li>
								</ul>
							</div>
						</li>
						<li class="col-xs-12 col-sm-12 col-md-3 col-lg-3 product-col">
							<div id="store-and-CDN" class="product-category">
								<p class="category-title ng-binding">存储与CDN</p>
								<ul>
									<li id="oss-not-opened" class="clearfix">
										<div class="pull-left" data-type='tip' data-order='3' data-content='开放存储服务<br>' data-position='top'>
											<span class="glyphicon glyphicon-qrcode text-muted"></span> 
											<span>开放存储服务</span>
											<span>OSS</span>
											<span class="beta-font">Beta</span>
										</div>
										<div class="pull-right clearfix"    data-type='tip' data-order='4' data-content='开放存储服务<br>购买开放存储服务' data-position='right'>
											<div class="pull-left product-opt-wrap">
												<a href="/buy/oss" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="立即购买">
													<span id="oss-purchase" class="glyphicon glyphicon-shopping-cart product-opt-icon"></span>
												</a>
											</div>
											<div class="pull-left product-opt-wrap">
												<a href="/helpCenter/helpCenter.jsp?container=help-OSSIntro" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="产品详情">
													<span id="rds-purchase" class="glyphicon glyphicon-question-sign product-opt-icon"></span>
												</a>
											</div>
										</div>
									</li>
									<!-- <li class="clearfix">
										 <div class="pull-left">
											<span class="glyphicon glyphicon-retweet text-muted"></span> 
											<span>开放归档服务</span>
											<span>OAS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li> -->
								</ul>
							</div>
							<div class="product-category">
								<p class="category-title ng-binding">大规模计算</p>
								<ul>
									<!-- <li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-road text-muted"></span> 
											<span>开放数据处理服务</span>
											<span>ODPS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li> -->
									<!-- <li class="clearfix">
										 <div class="pull-left">
											<span class="glyphicon glyphicon-cloud-download text-muted"></span> 
											<span>采云间</span>
											<span>DPC</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div> 
									</li> -->
									<li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-compressed text-muted"></span> 
											<span>分析数据库服务</span>
											<span>ADS</span>
										</div>
										<div class="pull-right clearfix">
												<span class="home-orange">敬请期待...</span>
										</div>
									</li>
								</ul>
							</div>
						</li>
						<li class="col-xs-12 col-sm-12 col-md-3 col-lg-3 product-col">
							<div class="product-category">
								<p class="category-title ng-binding">应用服务</p>
								<ul>
									<li id="gce-not-opened" class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-gift text-muted"></span> 
											<span>云引擎</span>
											<span>GCE</span>
											<span class="beta-font">Beta</span>
										</div>
										<div class="pull-right clearfix">
											<div class="pull-left product-opt-wrap">
												<a href="/detail/gceCreate" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="立即购买">
													<span id="gce-purchase" class="glyphicon glyphicon-shopping-cart product-opt-icon"></span>
												</a>
											</div>
											<div class="pull-left product-opt-wrap">
												<a href="/helpCenter/helpCenter.jsp?container=product-GCEIntro" target="_blank" class="product-opt" data-toggle="tooltip" data-placement="top" title="产品详情">
													<span id="rds-purchase" class="glyphicon glyphicon-question-sign product-opt-icon"></span>
												</a>
											</div>
										</div>
										<!-- <div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div> -->
									</li>
									<li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-inbox text-muted"></span> 
											<span>简单日志服务</span>
											<span>SLS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li>
									<li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-import text-muted"></span> 
											<span>消息队列服务</span>
											<span>MQS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li>
									<!-- <li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-search text-muted"></span> 
											<span>开放搜索服务</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li> -->
									<!-- <li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-sound-dolby text-muted"></span> 
											<span>性能测试服务 PTS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li> -->
									<!-- <li class="clearfix">
										<div class="pull-left">
											<span class="glyphicon glyphicon-list-alt text-muted"></span> 
											<span>开放消息服务 ONS</span>
										</div>
										<div class="pull-right clearfix">
											<span class="home-orange">敬请期待...</span>
										</div>
									</li> -->
								</ul>
							</div>
						</li>
					</ul>
				</div>
			</div>
		</div>
	</div><!-- main-content end-->
	<!-- tip 提示container，渲染容器是shade-->
	<div class="shade-container hidden-xs"></div>
	<%@ include file="../layouts/rToolbar.jsp"%>
</body>
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">
// Set configuration
seajs.config({
base: "${ctx}/static/modules/",
alias: {
"jquery": "jquery/2.0.3/jquery.min.js",
"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js"
}
});

seajs.use("${ctx}/static/page-js/clouddb/dashboard/main");
</script>
</html>
