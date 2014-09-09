<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html> 
<head>
<title>Letv CloudPlateform WebPortal<sitemesh:title/></title>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta http-equiv="Cache-Control" content="no-store" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />

<!-- bootstrap & fontawesome -->
<link rel="stylesheet" href="${ctx}/static/ace/css/bootstrap.min.css" />
<link rel="stylesheet" href="${ctx}/static/ace/css/font-awesome.min.css" />

<!-- page specific plugin styles -->

<!-- text fonts -->
<link rel="stylesheet" href="${ctx}/static/ace/css/ace-fonts.css" />

<!-- ace styles -->
<link rel="stylesheet" href="${ctx}/static/ace/css/ace.min.css" id="main-ace-style" />

<!--[if lte IE 9]>
	<link rel="stylesheet" href="${ctx}/static/ace/css/ace-part2.min.css" />
<![endif]-->
<link rel="stylesheet" href="${ctx}/static/ace/css/ace-skins.min.css" />
<link rel="stylesheet" href="${ctx}/static/ace/css/ace-rtl.min.css" />

<!--[if lte IE 9]>
	<link rel="stylesheet" href="${ctx}/static/ace/css/ace-ie.min.css" />
<![endif]-->

<!-- inline styles related to this page -->

<!-- ace settings handler -->
<script src="${ctx}/static/ace/js/jquery.min.js"></script>
<script src="${ctx}/static/ace/js/ace-extra.min.js"></script>
<script src="${ctx}/static/ace/js/bootstrap.min.js"></script>

<!-- HTML5shiv and Respond.js for IE8 to support HTML5 elements and media queries -->

<!--[if lte IE 8]>
	<script src="${ctx}/static/ace/js/html5shiv.min.js"></script>
	<script src="${ctx}/static/ace/js/respond.min.js"></script>
<![endif]-->

<sitemesh:head/>
</head>

<body class="no-skin">
<<<<<<< HEAD
	<%@ include file="/WEB-INF/layouts/header.jsp"%>
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
	<%@ include file="/WEB-INF/layouts/sidebar.jsp"%>
	
	<!--#内容显示 -->
	<div class="main-content">
		<!-- #section:basics/content.breadcrumbs -->
		<div class="breadcrumbs" id="breadcrumbs">
			<script type="text/javascript">
				try {
					ace.settings.check('breadcrumbs', 'fixed')
				} catch (e) {
				}
			</script>

			<ul class="breadcrumb">
				<li><i class="ace-icon fa fa-home home-icon"></i> <a href="#">集群管理</a>
				</li>
				<li class="active">集群列表</li>
			</ul>
			<!-- /.breadcrumb -->

			<!-- #section:basics/content.searchbox -->
			<div class="nav-search" id="nav-search">
				<form class="form-search">
					<span class="input-icon"> 
						<input type="text" placeholder="Search ..." value="" class="nav-search-input" id="nav-search-input" autocomplete="off" /> 
						<i class="ace-icon fa fa-search nav-search-icon"></i>
					</span>
				</form>
			</div>
			<!-- /.nav-search -->

			<!-- /section:basics/content.searchbox -->
		</div>
		
		<!-- /section:basics/content.breadcrumbs -->
		<div class="page-content">
			<!-- #section:settings.box -->
			<div class="ace-settings-container" id="ace-settings-container">
				<div class="btn btn-app btn-xs btn-warning ace-settings-btn" id="ace-settings-btn">
					<i class="ace-icon fa fa-cog bigger-150"></i>
				</div>

				<div class="ace-settings-box clearfix" id="ace-settings-box">
					<div class="pull-left width-50">
						<!-- #section:settings.skins -->
						<div class="ace-settings-item">
							<div class="pull-left">
								<select id="skin-colorpicker" class="hide">
									<option data-skin="no-skin" value="#438EB9">#438EB9</option>
									<option data-skin="skin-1" value="#222A2D">#222A2D</option>
									<option data-skin="skin-2" value="#C6487E">#C6487E</option>
									<option data-skin="skin-3" value="#D0D0D0">#D0D0D0</option>
								</select>
							</div>
							<span>&nbsp; Choose Skin</span>
						</div>

						<!-- /section:settings.skins -->

						<!-- #section:settings.navbar -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-navbar" /> <label class="lbl"
								for="ace-settings-navbar"> Fixed Navbar</label>
						</div>

						<!-- /section:settings.navbar -->

						<!-- #section:settings.sidebar -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-sidebar" /> <label class="lbl"
								for="ace-settings-sidebar"> Fixed Sidebar</label>
						</div>

						<!-- /section:settings.sidebar -->

						<!-- #section:settings.breadcrumbs -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-breadcrumbs" /> <label class="lbl"
								for="ace-settings-breadcrumbs"> Fixed Breadcrumbs</label>
						</div>

						<!-- /section:settings.breadcrumbs -->

						<!-- #section:settings.rtl -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-rtl" /> <label class="lbl"
								for="ace-settings-rtl"> Right To Left (rtl)</label>
						</div>

						<!-- /section:settings.rtl -->

						<!-- #section:settings.container -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-add-container" /> <label class="lbl"
								for="ace-settings-add-container"> Inside <b>.container</b>
							</label>
						</div>

						<!-- /section:settings.container -->
					</div>
					<!-- /.pull-left -->

					<div class="pull-left width-50">
						<!-- #section:basics/sidebar.options -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-hover" /> <label class="lbl"
								for="ace-settings-hover"> Submenu on Hover</label>
						</div>

						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-compact" /> <label class="lbl"
								for="ace-settings-compact"> Compact Sidebar</label>
						</div>

						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-highlight" /> <label class="lbl"
								for="ace-settings-highlight"> Alt. Active Item</label>
						</div>

						<!-- /section:basics/sidebar.options -->
					</div>
					<!-- /.pull-left -->
				</div>
				<!-- /.ace-settings-box -->
			</div>
			<!-- /.ace-settings-container -->
		
			<!-- /section:settings.box -->
			<div class="page-content-area">
				<div class="page-header">
					<h1> 
						集群列表
						<!-- <small> 
							<i class="ace-icon fa fa-angle-double-right"></i> 
							overview &amp; stats
						</small> -->
					</h1>
				</div>
				<!-- /.page-header -->
			</div>
			<!-- /.page-content-area -->
			<div class="row">
				<div class="col-xs-12">
					<sitemesh:body/>
				</div>
			</div>
			
			
		</div>
=======
	<div class="navbar-container" id="navbar-container">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
>>>>>>> branch 'develop' of ssh://git@git.letv.cn/liuhao1/letv_mcluster_webportal.git
	</div>
<<<<<<< HEAD
=======
	<div class="main-container" id="main-container">
		<script type="text/javascript">
			try {
				ace.settings.check('main-container', 'fixed')
			} catch (e) {
			}
		</script>
	<%@ include file="/WEB-INF/layouts/sidebar.jsp"%>
	
	<!--#内容显示 -->
	<div class="main-content">
		<!-- #section:basics/content.breadcrumbs -->
		<div class="breadcrumbs" id="breadcrumbs">
			<script type="text/javascript">
				try {
					ace.settings.check('breadcrumbs', 'fixed')
				} catch (e) {
				}
			</script>

			<ul class="breadcrumb">
				<li><i class="ace-icon fa fa-home home-icon"></i> <a href="#">集群管理</a>
				</li>
				<li class="active">集群列表</li>
			</ul>
			<!-- /.breadcrumb -->

			<!-- #section:basics/content.searchbox -->
			<div class="nav-search" id="nav-search">
				<form class="form-search">
					<span class="input-icon"> 
						<input type="text" placeholder="Search ..." value="" class="nav-search-input" id="nav-search-input" autocomplete="off" /> 
						<i class="ace-icon fa fa-search nav-search-icon"></i>
					</span>
				</form>
			</div>
			<!-- /.nav-search -->

			<!-- /section:basics/content.searchbox -->
		</div>
		
		<!-- /section:basics/content.breadcrumbs -->
		<div class="page-content">
			<!-- #section:settings.box -->
			<div class="ace-settings-container" id="ace-settings-container">
				<div class="btn btn-app btn-xs btn-warning ace-settings-btn" id="ace-settings-btn">
					<i class="ace-icon fa fa-cog bigger-150"></i>
				</div>

				<div class="ace-settings-box clearfix" id="ace-settings-box">
					<div class="pull-left width-50">
						<!-- #section:settings.skins -->
						<div class="ace-settings-item">
							<div class="pull-left">
								<select id="skin-colorpicker" class="hide">
									<option data-skin="no-skin" value="#438EB9">#438EB9</option>
									<option data-skin="skin-1" value="#222A2D">#222A2D</option>
									<option data-skin="skin-2" value="#C6487E">#C6487E</option>
									<option data-skin="skin-3" value="#D0D0D0">#D0D0D0</option>
								</select>
							</div>
							<span>&nbsp; Choose Skin</span>
						</div>

						<!-- /section:settings.skins -->

						<!-- #section:settings.navbar -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-navbar" /> <label class="lbl"
								for="ace-settings-navbar"> Fixed Navbar</label>
						</div>

						<!-- /section:settings.navbar -->

						<!-- #section:settings.sidebar -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-sidebar" /> <label class="lbl"
								for="ace-settings-sidebar"> Fixed Sidebar</label>
						</div>

						<!-- /section:settings.sidebar -->

						<!-- #section:settings.breadcrumbs -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-breadcrumbs" /> <label class="lbl"
								for="ace-settings-breadcrumbs"> Fixed Breadcrumbs</label>
						</div>

						<!-- /section:settings.breadcrumbs -->

						<!-- #section:settings.rtl -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-rtl" /> <label class="lbl"
								for="ace-settings-rtl"> Right To Left (rtl)</label>
						</div>

						<!-- /section:settings.rtl -->

						<!-- #section:settings.container -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-add-container" /> <label class="lbl"
								for="ace-settings-add-container"> Inside <b>.container</b>
							</label>
						</div>

						<!-- /section:settings.container -->
					</div>
					<!-- /.pull-left -->

					<div class="pull-left width-50">
						<!-- #section:basics/sidebar.options -->
						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-hover" /> <label class="lbl"
								for="ace-settings-hover"> Submenu on Hover</label>
						</div>

						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-compact" /> <label class="lbl"
								for="ace-settings-compact"> Compact Sidebar</label>
						</div>

						<div class="ace-settings-item">
							<input type="checkbox" class="ace ace-checkbox-2"
								id="ace-settings-highlight" /> <label class="lbl"
								for="ace-settings-highlight"> Alt. Active Item</label>
						</div>

						<!-- /section:basics/sidebar.options -->
					</div>
					<!-- /.pull-left -->
				</div>
				<!-- /.ace-settings-box -->
			</div>
			<!-- /.ace-settings-container -->
		
			<!-- /section:settings.box -->
			<div class="page-content-area">
				<div class="page-header">
					<h1> 
						集群列表
						<!-- <small> 
							<i class="ace-icon fa fa-angle-double-right"></i> 
							overview &amp; stats
						</small> -->
					</h1>
				</div>
				<!-- /.page-header -->
			</div>
			<!-- /.page-content-area -->
			<div class="row">
				<div class="col-xs-12">
					<sitemesh:body/>
				</div>
			</div>
			
			
		</div>
	</div>
>>>>>>> branch 'develop' of ssh://git@git.letv.cn/liuhao1/letv_mcluster_webportal.git
	<!-- /.内容显示  -->
	<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	</div>
	

	
<!-- basic scripts -->

<!--[if !IE]> -->
<script type="text/javascript">
	window.jQuery || document.write("<script src='${ctx}/static/ace/js/jquery.min.js'>" + "<"+"/script>");
</script>
<!-- <![endif]-->

<!--[if IE]>
	<script type="text/javascript">  window.jQuery || document.write("<script src='${ctx}/static/ace/js/jquery1x.min.js'>"+"<"+"/script>");
	</script>
<![endif]-->
<script type="text/javascript">
	if ('ontouchstart' in document.documentElement)
		document.write("<script src='${ctx}/static/ace/js/jquery.mobile.custom.min.js'>" + "<"+"/script>");
</script>

<!-- ace scripts -->
<script src="${ctx}/static/ace/js/ace-elements.min.js"></script>
<script src="${ctx}/static/ace/js/ace.min.js"></script>
</body>
</html>
