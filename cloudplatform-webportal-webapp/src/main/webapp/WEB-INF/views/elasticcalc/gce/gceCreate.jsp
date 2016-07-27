<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="utf-8" />
<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1" />
<meta name="viewpoint" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no" />
<!-- bootstrap css -->
<link type="text/css" rel="stylesheet"
	href="${ctx}/static/css/bootstrap.min.css" />
<!-- fontawesome css -->
<link type="text/css" rel="stylesheet"
	href="${ctx}/static/css/font-awesome.min.css" />
<!-- ui-css -->
<link type="text/css" rel="stylesheet"
	href="${ctx}/static/css/ui-css/common.css" />
<title>Le云控制台首页</title>
</head>
<body>
	<!-- top bar begin -->
	<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation"
		style="min-height: 40px; height: 40px;">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand color" href="${ctx}/dashboard"
					style="padding-top: 2px; height: 40px !important;"><img
					src="${ctx}/static/img/logo.png" /></a> 
					<%-- <a class="navbar-brand color"
					href="${ctx}/dashboard"
					style="margin-left: 10px; height: 40px !important;"><i
					class="fa fa-home text-20"></i></a> --%>
			</div>
			<div id="navbar" class="navbar-collapse collapse pull-right">
				<ul class="nav navbar-nav">
					<li><a href="javascript:void(0)" class="hlight"><span
							class="glyphicon glyphicon-bell"></span></a></li>
					<li class="dropdown"><a href="javascript:void(0)"
						class="dropdown-toggle hlight" data-toggle="dropdown">${sessionScope.userSession.userName}<span
							class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="javascript:void(0)">用户中心</a></li>
							<li><a href="javascript:void(0)">我的订单</a></li>
							<li><a href="javascript:void(0)">账户管理</a></li>
							<li class="divider"></li>
							<li><a href="${ctx}/account/logout">退出</a></li>
						</ul></li>
					<li><a href="javascript:void(0)" class="hlight"><span
							class="glyphicon glyphicon-lock"></span></a></li>
					<li><a href="javascript:void(0)" class="hlight"><span
							class="glyphicon glyphicon-pencil"></span></a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>
	<!-- top bar end -->

	<!-- navbar begin -->
	<div class="navbar navbar-default mt40"
		style="margin-bottom: 0px !important;">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="${ctx}/dashboard">Le云控制台首页</a>
			</div>
		</div>
	</div>
	<!-- navbar end -->
	<div class="container-fluid">
		<!-- main-content begin-->
		<div class="row main-header">
			<!-- main-content-header begin -->
			<div class="col-sm-12 col-md-6">
				<div class="pull-left">
					<h4>
						<span>云引擎GCE</span>
					</h4>
				</div>
			</div>
			<div class="col-sm-12 col-md-6">
				<div class="pull-right help-buy">
						<a href="/helpCenter/helpCenter.jsp?container=help-createGce">购买帮助</a>
				</div>
			</div>
		</div>
		<!-- main-content-header end-->

		<div class="row">
			<!-- main-content-center-begin -->
			<div class="col-sm-12 col-md-12">
				<ul class="nav nav-tabs RDSCreateUl" role="tablist" id="RDSCreateTab">
					<li role="presentation" class="active"><a href="#year" role="tab" data-toggle="tab" style="">包年包月</a></li>
					<!--<li role="presentation"><a href="#dosage" role="tab" data-toggle="tab">按量付费</a></li>-->
				</ul>
				<div class="tab-content mt20">
						<div role="tabpanel" class="tab-pane fade active in" id="year">
							<div class="col-sm-12 col-md-9">
								<dl class="bk-group">
									<dt class="bk-group-title">基本配置</dt>
									<dd class="bk-group-detail">
										<div class="bk-group-control"></div>
										<div>
										<form id="monthPurchaseForm">
											<div class="form-group bk-form-row col-sm-12">
												<label class="bk-form-row-name col-sm-2" style="padding-left: 0px;">应用名称：</label>
												<div class="col-sm-4 row">
													<input id="gceName" class="form-control" name="gceName" type="text">
												</div>
											</div>
											<div class="form-group bk-form-row col-sm-12">
												<label class="bk-form-row-name col-sm-2" style="padding-left: 0px;">应用描述：</label>
												<div class="col-sm-4 row">
													<textarea rows="3" cols="20" id="descn" class="form-control" name="descn"></textarea>
												</div>
											</div>
										</form>
										<div class="bk-form-row">
												<label class="bk-form-row-name">地域：</label>
												<div class="bk-form-row-cell">
													<div class="bk-form-row-li">
														<div class="bk-buttontab">
															<button class=" bk-button bk-button-primary bk-button-current">
																<div>
																	<span>北京</span>
																</div>
															</button>
															<button class="bk-button bk-button-primary disabled">
																<div>
																	<span>成都</span>
																</div>
															</button>
															<button class="bk-button bk-button-primary disabled">
																<div>
																	<span>青岛</span>
																</div>
															</button>
															<button class="bk-button bk-button-primary disabled">
																<div>
																	<span>香港</span>
																</div>
															</button>
															<button class="bk-button bk-button-primary disabled">
																<div>
																	<span>深圳</span>
																</div>
															</button>
														</div>
														<span class="bk-lnk" style="cursor: text;">暂时只有北京可用,其他地域陆续开通</span>
													</div>
													<div class="bk-form-row-txt">不同地域之间的产品内网不互通；订购后不支持更换地域，请谨慎选择</div>
												</div>
											</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">可用区：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li clearfix">
													<div class="pull-left">
														<span class="sleBG"> <span class="sleHid">
																<div class="divselect">
																	<span></span>
																	<ul>
																	</ul>
																	<input name="hclusterId" type="hidden" value="" />
																</div>
														</span>
														</span> <span class="bk-select-arrow"></span>
													</div>
												</div>
											</div>
										</div>
										
										<div class="bk-form-row">
											<label class="bk-form-row-name">服务类型：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li clearfix">
													<div class="pull-left">
														<span class="sleBG"> <span class="sleHid">
																<div class="divselect">
																	<span></span>
																	<ul>
																	<!-- <li class="bk-select-option"><a href="javascript:;" selectid="PYTHON">python</a></li>
																	<li class="bk-select-option"><a href="javascript:;" selectid="PHP">php</a></li> -->
																	<li class="bk-select-option"><a href="javascript:;" selectid="JETTY">java</a></li>
																	<li class="bk-select-option"><a href="javascript:;" selectid="TOMCAT">tomcat</a></li>
																	<li class="bk-select-option"><a href="javascript:;" selectid="RESIN">resin</a></li>
																	</ul>
																	<input name="type" type="hidden" value="JETTY" />
																</div>
														</span>
														</span> <span class="bk-select-arrow"></span>
													</div>
												</div>
											</div>
										</div>
										</div>
									</dd>
								</dl>
								<dl class="bk-group">
									<dt class="bk-group-title">关联服务</dt>
									<dd class="bk-group-detail">
										<div class="bk-group-control"></div>
											<div class="bk-form-row">
												<label class="bk-form-row-name">RDS服务：</label>
												<div class="bk-form-row-cell">
													<div class="bk-form-row-li clearfix">
														<div class="pull-left">
															<span class="sleBG"> <span class="sleHid">
																	<div class="divselect">
																		<span></span>
																		<ul>
																		<li class="bk-select-option"><a href="javascript:;" selectid="">不进行关联</a></li>
																		</ul>
																		<input name="rdsId" type="hidden" value="" />
																	</div>
															</span>
															</span> <span class="bk-select-arrow"></span>
														</div>
													</div>
												</div>
											</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">OCS服务：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li clearfix">
													<div class="pull-left">
														<span class="sleBG"> <span class="sleHid">
																<div class="divselect">
																	<span></span>
																	<ul>
																	<li class="bk-select-option"><a href="javascript:;" selectid="">不进行关联</a></li>
																	</ul>
																	<input name="ocsId" type="hidden" value="" />
																</div>
														</span>
														</span> <span class="bk-select-arrow"></span>
													</div>
												</div>
											</div>
										</div>
									</dd>
								</dl>
								<dl class="bk-group" >
									<dt class="bk-group-title">购买量</dt>
									<dd class="bk-group-detail">
										<div class="bk-group-control"></div>
										<div class="disabled">
											<div class="bk-form-row">
												<label class="bk-form-row-name">内存大小：</label>
												<div class="bk-form-row-cell">
													<div class="bk-form-row-li clearfix">
														<div class="pull-left">
															<span class="sleBG"> <span class="sleHid">
																	<div class="divselect">
																		<span></span>
																		<ul>
																		<li class="bk-select-option"><a href="javascript:;" selectid="1073741824">1G</a></li>
																		<li class="bk-select-option"><a href="javascript:;" selectid="2147483648">2G</a></li>
																		<li class="bk-select-option"><a href="javascript:;" selectid="4294967296">4G</a></li>
																		</ul>
																		<input name="memorySize" type="hidden" value="1073741824" />
																	</div>
															</span>
															</span> <span class="bk-select-arrow"></span>
														</div>
													</div>
												</div>
											</div>
											<div class="bk-form-row">
												<label class="bk-form-row-name">购买时长：</label>
												<div class="bk-form-row-cell">
													<div class="bk-form-row-li clearfix">
														<div class="pull-left" style="height: 36px;">
															<span class="sleBG">
															<span class="sleHid">
																<div class="divselect">
																	<span>一年</span>
																	<ul style="display: none;">
																		<li class="bk-select-option"><a href="javascript:;" selectid="1">一年</a></li>
																	</ul>
																	<input type="hidden" value="" />
																</div>
															</span>
															</span>
															<span class="bk-select-arrow"></span>
														</div>
													</div>
													<div class="bk-form-row-txt">此功能暂时不开放</div>
												</div>
											</div>
										</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">数量：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li">
													<!-- <span class="bk-number"> 
														<input type="text" onfocus="this.blur()" class="bk-number-input" name="buyNum" value="2"> 
														<span class="bk-number-unit">台</span> 
														<span class="bk-number-control"> 
															<span class="bk-number-up bk-number-disabled hide"> <i class="bk-number-arrow"></i>
														</span> 
														<span class="bk-number-up"> <i class="bk-number-arrow"></i>
														</span> 
														<span class="bk-number-down"> <i class="bk-number-arrow"></i>
														</span> 
													</span>
													</span> -->
													<div class="bk-buttontab">
														<input name="buyNum" type="text" class="hide" value="2">
														<button class=" bk-button bk-button-primary " value="1">
															<div>
																<span>1台</span>
															</div>
														</button>
														<button class="bk-button bk-button-primary bk-button-current" value="2">
															<div>
																<span>2台</span>
															</div>
														</button>
													</div>
												</div>
												<div class="bk-form-row-txt">目前只支持1台或2台</div>
											</div>
										</div>
									</dd>
								</dl>
							</div>
						</div>
						<div class="col-sm-12 col-md-3">
							<div class="bk-orders-menu  bk-mb4">
								<span class="bk-orders-menu-name">购买清单</span> <span class="bk-orders-menu-quantity bk-pale">1台</span>
							</div>
							<div class="bk-scope bk-items bk-mb4">
								<div class="bk-items-title">当前配置</div>
								<div>
									<div class="bk-items-list">
										<ul>
											<li><span class="bk-items-item-name">地域：</span> <span class="bk-items-item-value">北京</span></li>
											<li><span class="bk-items-item-name">购买量：</span> <span class="bk-items-item-value">1年</span></li>
										</ul>
									</div>
								</div>
								<div class="bk-items-price" style="padding-top: 0">
									<div class="bk-items-price-title bk-pale">配置费用：</div>
									<div class="bk-items-price-settle">
										<div>
											<span class="bk-cny">¥</span> <span class="bk-items-price-money">0.00</span> <span class="bk-items-price-unit"></span>
										</div>
									</div>
								</div>
								<div class="bk-items-price">
									<div class="bk-items-price-title bk-pale">公网流量费用：</div>
									<div class="bk-items-price-settle">
										<span class="bk-cny">¥</span> <span class="bk-items-price-money">0.00</span> <span class="bk-items-price-unit">/GB</span>
									</div>
								</div>
								<div class="bk-items-price-control">
									<button form="monthPurchaseForm" type="submit" id="monthPurchaseBotton" class="bk-button">
										<div>
											<span class="ng-scope">立即购买</span>
										</div>
									</button>
									<button class=" hide bk-button bk-button-default">
										<div>
											<span class="ng-scope">加入清单</span>
										</div>
									</button>
								</div>
								<div class="bk-pb4"></div>
							</div>
							<div class="bk-scope bk-items">
								<div class="bk-items-title">服务专员信息</div>
							</div>
							<div class="bk-scope bk-items">
									<div class="bk-items-list">
											<ul>
												<li><span class="bk-items-item-name">姓名：</span> <span class="bk-items-item-value">周秉政</span></li>
												<li><span class="bk-items-item-name">邮箱：</span> <span class="bk-items-item-value">zhoubingzheng@letv.com</span></li>
												<li><span class="bk-items-item-name">电话：</span> <span class="bk-items-item-value">15901422865</span></li>
												<li><span class="bk-items-item-name">Q  Q：</span> <span class="bk-items-item-value">473877850</span></li>
											</ul>
								</div>
							</div>
						</div>
				</div>
				<div role="tabpanel" class="tab-pane fade mc-hide" id="dosage">
						<div class="col-sm-12 col-md-9">
							<dl class="bk-group">
								<dt class="bk-group-title">基本配置</dt>
								<dd class="bk-group-detail">
									<div class="bk-group-control"></div>
									<div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">地域：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li">
													<div class="bk-buttontab">
														<button
															class=" bk-button bk-button-primary bk-button-current">
															<div>
																<span>青岛</span>
															</div>
														</button>
														<button class="bk-button bk-button-primary">
															<div>
																<span>杭州</span>
															</div>
														</button>
														<button class="bk-button bk-button-primary">
															<div>
																<span>北京</span>
															</div>
														</button>
														<button class="bk-button bk-button-primary">
															<div>
																<span>香港</span>
															</div>
														</button>
														<button class="bk-button bk-button-primary">
															<div>
																<span>深圳</span>
															</div>
														</button>
													</div>
													<span><a class="bk-lnk bk-ml2">查看我的产品地域</a></span>
												</div>
												<div class="bk-form-row-txt">
													不同地域之间的产品内网不互通；订购后不支持更换地域，请谨慎选择 <a href="#" target="_blank"
														class="bk-lnk">教我选择&gt;&gt;</a>
												</div>
											</div>
										</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">可用区：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li clearfix">
													<div class="dropdown pull-left">
														<button
															class="btn btn-default dropdown-toggle w217 wcolor mgl-r"
															type="button" id="singleUseable" data-toggle="dropdown">
															单可用区<span class="caret"></span>
														</button>
														<ul class="dropdown-menu w217" role="menu"
															aria-labelledby="singleUseable">
															<li role="presentation"><a role="menuitem"
																tabindex="-1" href="#">单可用区</a></li>
														</ul>
													</div>
													<div class="dropdown pull-left">
														<button
															class="btn btn-default dropdown-toggle w326 wcolor"
															type="button" id="UseableB" data-toggle="dropdown">
															可用区B<span class="caret"></span>
														</button>
														<ul class="dropdown-menu w326" role="menu"
															aria-labelledby="UseableB">
															<li role="presentation"><a role="menuitem"
																tabindex="-1" href="#">可用区B <span
																	class="text-muted text-xs">0台ECS</span></a></li>
														</ul>
													</div>
													<span
														class="glyphicon glyphicon-question-sign margin-left-5"
														style="margin-top: 8px;"></span>
												</div>
											</div>
										</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">数据库类型：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li clearfix">
													<div class="pull-left">
														<span class="sleBG">   
                                                        <span class="sleHid">
                                                            <select class="form-control w217 wcolor">
																<option >MySQL</option>
																<option >MySQLServer</option>
																<option >mcluster</option>
																<option >fortest</option>
														    </select>
                                                        </span>   
                                                        </span>  
                                                        <span class="bk-select-arrow"></span>														
													</div>
												</div>
											</div>
										</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">版本：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li">
													<div class="bk-buttontab">
														<button
															class=" bk-button bk-button-primary bk-button-current">
															<div>
																<span>5.5</span>
															</div>
														</button>
														<button class="bk-button bk-button-primary">
															<div>
																<span>5.6</span>
															</div>
														</button>
													</div>
												</div>
											</div>
										</div>
									</div>
								</dd>
							</dl>
							<dl class="bk-group">
								<dt class="bk-group-title">存储</dt>
								<dd class="bk-group-detail">
									<div class="bk-group-control"></div>
									<div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">存储空间：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li">
													<div class="bk-slider">
														<div class="bk-slider-range" id="bar2">
															<div id="flag"></div>
															<span class="bk-slider-block bk-slider-l2"> <span
																class="bk-slider-block-box"> <span
																	class="bk-slider-txt">250GB</span>
															</span>
															</span> <span class="bk-slider-block bk-slider-l1"> <span
																class="bk-slider-block-box"> <span
																	class="bk-slider-txt">500GB</span>
															</span>
															</span> <span class="bk-slider-block bk-slider-l1"> <span
																class="bk-slider-block-box bk-slider-block-box-last bk-select-action">
																	<span class="bk-slider-txt">1000GB</span>
															</span>
															</span> <span class="bk-slider-container bk-slider-transition"
																id="layer2" style="width: 11.88px;"> <span
																class="bk-slider-current"> <span
																	class="bk-slider-unit bk-slider-l2"> <span
																		class="bk-slider-unit-box"> <span
																			class="bk-slider-txt">250GB</span></span>
																</span> <span class="bk-slider-unit bk-slider-l1"> <span
																		class="bk-slider-unit-box"> <span
																			class="bk-slider-txt">500GB</span>
																	</span>
																</span> <span class="bk-slider-unit bk-slider-l1"> <span
																		class="bk-slider-unit-box bk-slider-unit-box-last">
																			<span class="bk-slider-txt">1000GB</span>
																	</span>
																</span>
															</span>
															</span>
															<!-- <span>15</span> -->
															<!-- <button id="btn2"></button> -->
															<span class="bk-slider-drag" id="btn2"> <i></i> <i></i>
																<span class="bk-tip-arrow"></span>
															</span>
														</div>
													</div>
													<span class="bk-number bk-ml2"> <input type="text"
														class="bk-number-input" id="value2" value="5"> <span
														class="bk-number-unit">GB</span><span
														class="bk-number-control"> <span
															class="bk-number-up bk-number-disabled hide"> <i
																class="bk-number-arrow"></i>
														</span> <span class="bk-number-up"> <i
																class="bk-number-arrow"></i>
														</span> <span class="bk-number-down bk-number-disabled"> <i
																class="bk-number-arrow"></i>
														</span> <span class="bk-number-down hide"> <i
																class="bk-number-arrow"></i>
														</span>
													</span>
													</span>
												</div>
												<div class="bk-form-row-txt">步长为5GB</div>
											</div>
										</div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">内存：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li">
													<div class="dropdown">
														<button
															class="btn btn-default dropdown-toggle w217 wcolor"
															type="button" id="UseableB" data-toggle="dropdown">
															240MB<span class="caret"></span>
														</button>
														<ul class="dropdown-menu w217 h216" role="menu"
															aria-labelledby="UseableB">
															<li role="presentation"><a role="menuitem"
																tabindex="-1" href="#">240MB</a></li>
															<li role="presentation"><a role="menuitem"
																tabindex="-1" href="#">600MB</a></li>
															<li role="presentation"><a role="menuitem"
																tabindex="-1" href="#">1200MB</a></li>
														</ul>
													</div>
												</div>
												<div class="bk-form-row-txt">最大连接数:60 IOPS:150</div>
											</div>
										</div>
									</div>
								</dd>
							</dl>
							<dl class="bk-group">
								<dt class="bk-group-title">购买量</dt>
								<dd class="bk-group-detail">
									<div class="bk-group-control"></div>
									<div>
										<div class="bk-form-row">
											<label class="bk-form-row-name">数量：</label>
											<div class="bk-form-row-cell">
												<div class="bk-form-row-li">
													<span class="bk-number"> <input type="text"
														class="bk-number-input" value="1"> <span
														class="bk-number-unit">台</span><span
														class="bk-number-control"> <span
															class="bk-number-up bk-number-disabled hide"> <i
																class="bk-number-arrow"></i>
														</span> <span class="bk-number-up"> <i
																class="bk-number-arrow"></i>
														</span> <span class="bk-number-down bk-number-disabled"> <i
																class="bk-number-arrow"></i>
														</span> <span class="bk-number-down hide"> <i
																class="bk-number-arrow"></i>
														</span>
													</span>
													</span>
												</div>
											</div>
										</div>
									</div>
								</dd>
							</dl>
						</div>
						<div class="col-sm-12 col-md-3">
							<div class="bk-orders-menu  bk-mb4">
								<span class="bk-orders-menu-name">购买清单</span> <span
									class="bk-orders-menu-quantity bk-pale">0台</span>
							</div>
							<div class="bk-scope bk-items">
								<div class="bk-items-title">当前配置</div>
								<div>
									<div class="bk-items-list">
										<ul>
											<li><span class="bk-items-item-name">地域：</span> <span
												class="bk-items-item-value">青岛（可用区B）</span></li>
											<li><span class="bk-items-item-name">配置：</span> <span
												class="bk-items-item-value">5GB存储空间、240MB内存、MySQL 5.5</span>
											</li>
											<li><span class="bk-items-item-name">购买量：</span> <span
												class="bk-items-item-value">1个月 x 1台</span></li>
										</ul>
									</div>
								</div>
								<div class="bk-items-price" style="padding-top: 0">
									<div class="bk-items-price-title bk-pale">配置费用：</div>
									<div class="bk-items-price-settle">
										<div>
											<span class="bk-cny">¥</span> <span
												class="bk-items-price-money">40.80</span> <span
												class="bk-items-price-unit"></span>
										</div>
									</div>
								</div>
								<div class="bk-items-price">
									<div class="bk-items-price-title bk-pale">公网流量费用：</div>
									<div class="bk-items-price-settle">
										<span class="bk-cny">¥</span> <span
											class="bk-items-price-money">1.00</span> <span
											class="bk-items-price-unit">/GB</span>
									</div>
								</div>
								<div class="bk-items-price-control">
									<button class="bk-button bk-button-primary">
										<div>
											<span class="ng-scope">立即购买</span>
										</div>
									</button>
									<button class="bk-button bk-button-default">
										<div ng-transclude="">
											<span class="ng-scope">加入清单</span>
										</div>
									</button>
								</div>
								<div class="bk-pb4"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- main-content-center-end -->
	</div>
	<!-- main-content end-->
</body>
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">

// Set configuration
seajs.config({
	base: "${ctx}/static/modules/",
	alias: {
		"jquery": "jquery/2.0.3/jquery.min.js",
		"bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js",
		"bootstrapValidator": "bootstrap/bootstrapValidator/0.5.3/bootstrapValidator.js"
	}
});
seajs.use("${ctx}/static/page-js/elasticcalc/gce/gceCreate/main");
</script>
</html>
