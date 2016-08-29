    <%@ page language="java" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <!DOCTYPE html>
        <html>
        <head>
        <meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1"/>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>控制台-乐视云平台</title>
        <link rel="shortcut icon" href="http://i3.letvimg.com/lc05_lecloud/201601/12/10/21/favicon.ico">
		<link rel="stylesheet" href="/stylesheets/dist/vendor.css">
		<link rel="stylesheet" href="/stylesheets/dist/common-1468484220168.css">
		<link rel="stylesheet" href="/stylesheets/dist/style-rds-1468484220168.css">
        </head>
        <style>
        .main .side-bar {
        height: 100%;
        margin-top: 0;
        padding-top: 10px;
        z-index: 200;
        background: #ebebeb;
        }

        .main .content-wrapper {
        width: 1349px;
        width: 100%;
        min-width: 1349px;
        }
        </style> 
        <body>
        <div ng-include="'/apps/rds/views/headerRds.html'"></div>
        <div class="main">
        <div ng-include="'/apps/rds/views/side-menu.html'"></div>
        <div class="content-wrapper">
        <div ng-view="ng-view" class="content"></div>
        </div>
        <div class="clearfix"></div>
        </div>

        <script type="text/javascript" src="/javascripts/dist/require.min.js" data-main="/javascripts/dist/rds-${lang}-main-build-1468484220168.js"></script>

        </body>
        </html>