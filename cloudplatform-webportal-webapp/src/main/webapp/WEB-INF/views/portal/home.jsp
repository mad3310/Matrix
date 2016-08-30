<%@ page language="java" pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="utf-8"/>
<meta name="keyword" content="oauth,letv"/>
<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1"/>
<meta name="viewpoint" content="width=device-width,initial-scale=1"/>

<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css"/>
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css"/>
<link rel="shortcut icon" href=" ${ctx}/static/img/favicon2.ico">
<script type="text/javascript" src="${ctx}/static/modules/jquery/2.0.3/jquery.min.js"></script>
<script type="text/javascript" src="${ctx}/static/modules/bootstrap/bootstrap/3.3.0/bootstrap.min.js"></script>
<title>乐视云计算首页</title>
</head>
<body>
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
<style>
.hidden{display: none;}
/*video*/
.page-background {
  background-image: url(${ctx}/static/img/userbg.jpg);
}
.page-background {
  background-attachment: fixed;
  background-repeat: no-repeat;
  background-position: center;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
  -webkit-transition: background-image .25s;
}
.last.page-background {
  background-image: url(${ctx}/static/img/ftb.jpg);
}
.page-background-image {
  background: rgba(0,0,0,.5);
}
#background-video, .page-background-image {
  width: 100%;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 0;
 /* background: #000;*/
}
.description-wrapper {
  display: table;
  width: 100%;
  height: 100%;
  position: relative;
  top: 0;
  left: 0;
}
#video-description {
  width: 100%;
  height: 100%;
  position: relative;
  z-index: 1;
  color: #FFF;
  text-align: center;
  display: table-cell;
  vertical-align: middle;
}
#video-description .words {
  position: absolute;
  width: 100%;
}
#video-description h1 {
  font-size: 72px;
  font-weight: 700;
  margin: 35px auto;
  position: absolute;
  width: 100%;
  top: -60px;
}
#video-description h2 {
  font-size: 24px;
  position: absolute;
  width: 100%;
  top: 85px;
}
#video-description .play {
  display: inline-block;
  line-height: 59px;
  cursor: pointer;
  width: 110px;
  height: 110px;
  -webkit-border-radius: 100%;
  -moz-border-radius: 100%;
  border-radius: 100%;
  border: 2px solid #fff;
  background-color: transparent;
  outline: 0;
  -webkit-transition: .4s;
  -moz-transition: .4s;
  -ms-transition: .4s;
  -o-transition: .4s;
  transition: .4s;
  position: relative;
  margin-top: 180px;
}
#video-description .has-no-video {
  margin-top: 180px;
}
#video-description .play-triangle {
  width: 0;
  height: 0;
  border-top: 16px solid transparent;
  border-bottom: 16px solid transparent;
  border-left: 25px solid #FFF;
  position: absolute;
  left: 50%;
  top: 50%;
  margin-left: -9px;
  margin-top: -15px;
}
#video-description .play {
  display: inline-block;
  line-height: 59px;
  cursor: pointer;
  width: 110px;
  height: 110px;
  -webkit-border-radius: 100%;
  -moz-border-radius: 100%;
  border-radius: 100%;
  border: 2px solid #fff;
  background-color: transparent;
  outline: 0;
  -webkit-transition: .4s;
  -moz-transition: .4s;
  -ms-transition: .4s;
  -o-transition: .4s;
  transition: .4s;
  position: relative;
  margin-top: 180px;
}
/*end video*/
.section {
position: relative;
-webkit-box-sizing: border-box;
-moz-box-sizing: border-box;
box-sizing: border-box;
}
.section.table, .slide.table {
display: table;
width: 100%;
}
#fullpage>.section>.tableCell {
min-width: 470px;
}
.tableCell {
display: table-cell;
vertical-align: middle;
width: 100%;
height: 100%;
}
.section-inner {
display: table;
width: 100%;
height: 100%;
}
.left-section, .right-section {
display: table-cell;
height: 100%;
width: 50%;
text-align: center;
vertical-align: middle;
}
.left-section .article-wrapper {
margin: 0 5% 0 15%;
}
/*.right-section .article-wrapper {
margin: 0 15% 0 5%;
}*/
.left-section, .right-section {
display: table-cell;
height: 100%;
width: 50%;
text-align: center;
vertical-align: middle;
}
.article-wrapper {
text-align: left;
}
.article-wrapper h1 {
margin-bottom: 40px;
font-size: 60px;
font-weight: 700;
}
/*footer*/
.footer.section .tableCell>.wrapper {
  display: table-row;
  position: relative;
  width: 100%;
}
#fullpage>.section>.tableCell {
  min-width: 470px;
}
.footer.section .tableCell>.wrapper {
  display: table-row;
  position: relative;
  width: 100%;
}
.footer.section .tableCell>.wrapper>.inner-wrapper {
  position: relative;
  display: table-cell;
  width: 100%;
  height: 100%;
  vertical-align: middle;
}
.footer.section h1 {
  font-size: 60px;
  font-weight: 700;
  color: #FFF;
  text-align: center;
  margin-bottom: 60px;
  position: relative;
  z-index: 2;
}
#footer {
  border-top: 1px solid rgba(0,0,0,.1);
  padding: 30px 0;
  background: #FFF;
  position: relative;
  display: table-row;
  bottom: 0;
  left: 0;
  height: 200px;
  width: 100%;
}
@media only screen and (max-width: 1366px)
.article-wrapper h1 {
  margin-bottom: 40px;
  font-size: 45px;
  font-weight: 700;
}
.slideLeftRetourn {
-webkit-animation-name: slideLeftRetourn;
-moz-animation-name: slideLeftRetourn;
-ms-animation-name: slideLeftRetourn;
-o-animation-name: slideLeftRetourn;
animation-name: slideLeftRetourn;
}
.magictime {
-webkit-animation-duration: 1s;
-moz-animation-duration: 1s;
-ms-animation-duration: 1s;
-o-animation-duration: 1s;
animation-duration: 1s;
-webkit-animation-fill-mode: both;
-moz-animation-fill-mode: both;
-ms-animation-fill-mode: both;
-o-animation-fill-mode: both;
animation-fill-mode: both;
}
.article-wrapper p.show {
  opacity: 1;
  word-break: break-all;
}
@media only screen and (max-width: 1366px)
.article-wrapper p {
  font-size: 22px;
  line-height: 40px;
}
.article-wrapper p {
font-size: 30px;
line-height: 50px;
opacity: 0;
-webkit-transition: all 1s ease-in;
-moz-transition: all 1s ease-in;
-ms-transition: all 1s ease-in;
-o-transition: all 1s ease-in;
transition: all 1s ease-in;
}
.article-wrapper ul.screenshot {
padding-top: 20px;
}
.article-wrapper ul.screenshot li {
float: left;
margin: 10px 10px 0 0;
padding: 3px;
border: 1px solid #CCC;
border-radius: 3px;
/*background: #FFF;*/
background-color:transparent;
}
/*自定义*/
.screenshot img{width:120px;height:68px;position:relative;z-index:101;cursor:pointer}
.down{position:fixed;z-index:100;left:50%;bottom:15px;color:rgba(0,0,0,.3);}
.up{position:fixed;z-index:100;left:50%;top:15px;color:rgba(0,0,0,.3);}
.down span,.up span{font-size:40px;}
.down span:hover,.up span:hover{color:rgba(0,0,0,.6);cursor:pointer;}
/*end 自定义*/
.circle-wrapper {
width: 680px;
height: 680px;
margin: auto;
position: relative;
}
.red.doted-circle {border-color: #f07373;}
.blue.doted-circle {border-color: #6dabd6;}
.green.doted-circle{border-color:#3AB052;}
.pink.doted-circle{border-color:#DB5789;}
.right-section .doted-circle, .right-section .filled-circle {
margin-left: -10%;
}
.doted-circle.circling {
-webkit-animation: rotate-infinity 16s linear infinite;
-moz-animation: rotate-infinity 16s linear infinite;
-ms-animation: rotate-infinity 16s linear infinite;
animation: rotate-infinity 16s linear infinite;
}
.doted-circle {
width: 100%;
height: 100%;
border: 2px dashed #CCC;
}
.doted-circle, .filled-circle {
position: absolute;
border-radius: 50%;
-webkit-background-clip: padding-box;
-moz-background-clip: padding;
background-clip: padding-box;
}
.red.filled-circle {background: #f07373;}
.blue.filled-circle{background: #6dabd6;}
.green.filled-circle{background:#3AB052;}
.pink.filled-circle{background:#DB5789;}
.filled-circle {
top: 3%;
left: 3%;
width: 95%;
height: 95%;
overflow: hidden;
}
.frame-3.part-1 {
top: 15%;
left: 50%;
margin-left: -87px;
}
.frame-3[class*=part-] {
position: absolute;
}
.twisterInDown {
-webkit-animation-name: twisterInDown;
-moz-animation-name: twisterInDown;
-ms-animation-name: twisterInDown;
-o-animation-name: twisterInDown;
animation-name: twisterInDown;
}
.frame-3.part-2 {
bottom: 25%;
right: 13%;
}
.twisterInUp {
-webkit-animation-name: twisterInUp;
-moz-animation-name: twisterInUp;
-ms-animation-name: twisterInUp;
-o-animation-name: twisterInUp;
animation-name: twisterInUp;
}
.frame-3.part-3 {
bottom: 25%;
left: 13%;
}
.twisterInDown {
-webkit-animation-name: twisterInDown;
-moz-animation-name: twisterInDown;
-ms-animation-name: twisterInDown;
-o-animation-name: twisterInDown;
animation-name: twisterInDown;
}
[class*='.frame-2-']>.part-1{margin-bottom:30px}.frame-2-2[class*=part-]{width:250px}
.frame-2-2 img.part-1,.frame-2-3 img.part-1,.frame-2-4 img.part-1,.frame-2-5 img.part-1{width:58%}.frame-3[class*=part-]{width:30%!important}
.frame-2-6 [class*=part-]{position:absolute;width:100px;opacity:1;-webkit-transition:all .8s ease-in;-moz-transition:all .8s ease-in;-ms-transition:all .8s ease-in;-o-transition:all .8s ease-in;transition:all .8s ease-in}
.frame-2-6 .part-0{top:50%;left:50%;margin-left:-50px;margin-top:-50px}
.frame-2-6 .part-1{top:5%;left:50%;margin-left:-50px}
.frame-2-6 .part-2{top:32%;right:10%}
.frame-2-6 .part-3{bottom:12%;right:22%}
.frame-2-6 .part-4{bottom:12%;left:22%}
.frame-2-6 .part-5{top:32%;left:10%}
.frame-2-6.together [class*=part-]{opacity:0}
.frame-2-6.together .part-1{top:40%;left:52%}
.frame-2-6.together .part-2{top:40%;right:40%}
.frame-2-6.together .part-3{bottom:40%;right:41%}
.frame-2-6.together .part-4{bottom:40%;left:41%}
.frame-2-6.together .part-5{top:40%;left:41%}
.frame-2-6-2{display:none}
.frame-2-6-1{display:none;position:absolute;left:50%;top:20%;opacity:0;margin-left:-77px;margin-top:-56px}
.table-wrapper.hidden {
display: none;
}
.table-wrapper {
display: table;
width: 100%;
height: 100%;
}
.word-wrapper {
display: table-cell;
text-align: center;
vertical-align: middle;
color: #FFF;
}
@media only screen and (max-width:980px){form.register-form .input-wrapper{display:block;float:none;margin-right:0;margin-bottom:20px}form.register-form .button-wrapper{display:block}form.register-form .register.button{float:none;width:100%!important}.frame-2-2 img.part-1,.frame-2-3 img.part-1,.frame-2-4 img.part-1,.frame-2-5 img.part-1{width:58%}.frame-3[class*=part-]{width:30%!important}.frame-3.part-1{top:15%!important;left:58%!important;margin-left:-87px!important}.frame-3.part-2{bottom:19%!important;right:11%!important}.frame-3.part-3{bottom:19%!important;left:16%!important}img.frame-4,img.frame-5{width:66%}img.frame-5.part-2{width:auto}.circle-wrapper{width:400px!important;height:400px!important}.footer.section h1{font-size:40px!important}}
@media only screen and (max-width:1366px){
  /*#top-menu{height:60px}#top-menu .logo{padding:10px 0}#top-menu .logo img{height:40px}#top-menu form .wrapper{top:16px;left:155px}#top-menu 
  .public-pages{line-height:60px;margin-left:190px}#top-menu .login,#top-menu .register{margin:13px 20px auto auto}#current-user{margin:10px 0 auto auto;padding:0 20px}#current-user>img{width:40px;border-radius:40px}*/
  .circle-wrapper{width:580px;height:580px}.article-wrapper{text-align:left;margin:0 10% 0 15%}.article-wrapper h1{margin-bottom:40px;font-size:45px;font-weight:700}.article-wrapper p{font-size:22px;line-height:40px}
  /*.footer.section h1{font-size:50px;margin-bottom:40px}*/
  .frame-3[class*=part-]{width:150px}.frame-3.part-1{top:15%;left:53%;margin-left:-87px}.frame-3.part-2{bottom:25%;right:13%}.frame-3.part-3{bottom:25%;left:13%}.footer.section .partners .partner img{height:40px}}
@media all and (max-width:900px){.mfp-arrow{-webkit-transform:scale(0.75);transform:scale(0.75)}.mfp-arrow-left{-webkit-transform-origin:0;transform-origin:0}.mfp-arrow-right{-webkit-transform-origin:100%;transform-origin:100%}.mfp-container{padding-left:6px;padding-right:6px}}
.imgScale{
  -webkit-transform-origin:0 80%;
  -moz-transform-origin:0 80%;
  -ms-transform-origin:0 80%;
  -o-transform-origin:0 80%;
  transform-origin: 0 80%;
  -webkit-transform:scale(10,10);
  -moz-transform:scale(10,10);
  -ms-transform:scale(10,10);
  -o-transform:scale(10,10);
  transform:scale(10,10);
}
.holeOut {
-webkit-animation-name: holeOut;
-moz-animation-name: holeOut;
-ms-animation-name: holeOut;
-o-animation-name: holeOut;
animation-name: holeOut;
}
.swashIn {
-webkit-animation-name: swashIn;
-moz-animation-name: swashIn;
-ms-animation-name: swashIn;
-o-animation-name: swashIn;
animation-name: swashIn;
}
.openDownRightOut {
-webkit-animation-name: openDownRightOut;
-moz-animation-name: openDownRightOut;
-ms-animation-name: openDownRightOut;
-o-animation-name: openDownRightOut;
animation-name: openDownRightOut;
}
.openUpLeftOut {
-webkit-animation-name: openUpLeftOut;
-moz-animation-name: openUpLeftOut;
-ms-animation-name: openUpLeftOut;
-o-animation-name: openUpLeftOut;
animation-name: openUpLeftOut;
}
.swashOut {
-webkit-animation-name: swashOut;
-moz-animation-name: swashOut;
-ms-animation-name: swashOut;
-o-animation-name: swashOut;
animation-name: swashOut;
}
/*右侧点点*/
#fullPage-nav.right {
  right: 17px;
}
#fullPage-nav {
  position: fixed;
  z-index: 100;
  margin-top: -32px;
  top: 50%;
  opacity: 1;
}
#fullPage-nav ul, .fullPage-slidesNav ul {
  margin: 0;
  padding: 0;
}
#fullPage-nav li, .fullPage-slidesNav li {
  display: block;
  width: 14px;
  height: 13px;
  margin: 7px;
  position: relative;
}
#fullPage-nav li a, .fullPage-slidesNav li a {
  display: block;
  position: relative;
  z-index: 1;
  width: 100%;
  height: 100%;
  cursor: pointer;
  text-decoration: none;
}
#fullPage-nav li .activel span {
  background: #f75288!important;
  border-color: #f75288!important;
}
#fullPage-nav.light-nav li span {
  background: #FFF;
  border-color: #FFF;
}
#fullPage-nav li .activel span, .fullPage-slidesNav .activel span {
  background: #333;
}
#fullPage-nav li span {
  background-color: #000;
  -webkit-transition: all .2s ease-in;
  -moz-transition: all .2s ease-in;
  -ms-transition: all .2s ease-in;
  -o-transition: all .2s ease-in;
  transition: all .2s ease-in;
}
#fullPage-nav span, .fullPage-slidesNav span {
  top: 2px;
  left: 2px;
  width: 8px;
  height: 8px;
  border: 1px solid #000;
  background: 0 0;
  -webkit-border-radius: 50%;
  -moz-border-radius: 50%;
  border-radius: 50%;
  position: absolute;
  z-index: 1;
}
/*end dot*/
@keyframes rotate-infinity{
	from{-webkit-transform:rotate(0deg);-moz-transform:rotate(0deg);-ms-transform:rotate(0deg);transform:rotate(0deg)}
	to{-webkit-transform:rotate(360deg);-moz-transform:rotate(360deg);-ms-transform:rotate(360deg);transform:rotate(360deg)}
}
@-moz-keyframes rotate-infinity{from{-moz-transform:rotate(0deg);transform:rotate(0deg)}to{-moz-transform:rotate(360deg);transform:rotate(360deg)}}@-webkit-keyframes rotate-infinity{from{-webkit-transform:rotate(0deg);transform:rotate(0deg)}to{-webkit-transform:rotate(360deg);transform:rotate(360deg)}}@-ms-keyframes rotate-infinity{from{-ms-transform:rotate(0deg);transform:rotate(0deg)}to{-ms-transform:rotate(360deg);transform:rotate(360deg)}}
@-moz-keyframes slideLeftRetourn{0%{-moz-transform-origin:0 0;-moz-transform:translateX(-100%)}100%{-moz-transform-origin:0 0;-moz-transform:translateX(0%)}}@-webkit-keyframes slideLeftRetourn{0%{-webkit-transform-origin:0 0;-webkit-transform:translateX(-100%)}100%{-webkit-transform-origin:0 0;-webkit-transform:translateX(0%)}}@-o-keyframes slideLeftRetourn{0%{-o-transform-origin:0 0;-o-transform:translateX(-100%)}100%{-o-transform-origin:0 0;-o-transform:translateX(0%)}}@-ms-keyframes slideLeftRetourn{0%{-ms-transform-origin:0 0;-ms-transform:translateX(-100%)}100%{-ms-transform-origin:0 0;-ms-transform:translateX(0%)}}@keyframes slideLeftRetourn{0%{transform-origin:0 0;transform:translateX(-100%)}100%{transform-origin:0 0;transform:translateX(0%)}}
@-moz-keyframes twisterInDown{0%{opacity:0;-moz-transform-origin:0 100%;-moz-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-moz-transform-origin:0 100%;-moz-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;-moz-transform-origin:100% 100%;-moz-transform:scale(1,1) rotate(0deg) translateY(0%)}}@-webkit-keyframes twisterInDown{0%{opacity:0;-webkit-transform-origin:0 100%;-webkit-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-webkit-transform-origin:0 100%;-webkit-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;-webkit-transform-origin:100% 100%;-webkit-transform:scale(1,1) rotate(0deg) translateY(0%)}}@-o-keyframes twisterInDown{0%{opacity:0;-o-transform-origin:0 100%;-o-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-o-transform-origin:0 100%;-o-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;-o-transform-origin:100% 100%;-o-transform:scale(1,1) rotate(0deg) translateY(0%)}}@-ms-keyframes twisterInDown{0%{opacity:0;filter:alpha(opacity=0);-ms-transform-origin:0 100%;-ms-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-ms-transform-origin:0 100%;-ms-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:100% 100%;-ms-transform:scale(1,1) rotate(0deg) translateY(0%)}}@keyframes twisterInDown{0%{opacity:0;transform-origin:0 100%;transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{transform-origin:0 100%;transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;transform-origin:100% 100%;transform:scale(1,1) rotate(0deg) translateY(0%)}}
@-moz-keyframes twisterInUp{0%{opacity:0;-moz-transform-origin:100% 0;-moz-transform:scale(0,0) rotate(360deg) translateY(100%)}30%{-moz-transform-origin:100% 0;-moz-transform:scale(0,0) rotate(360deg) translateY(100%)}100%{opacity:1;-moz-transform-origin:0 0;-moz-transform:scale(1,1) rotate(0deg) translateY(0)}}@-webkit-keyframes twisterInUp{0%{opacity:0;-webkit-transform-origin:100% 0;-webkit-transform:scale(0,0) rotate(360deg) translateY(100%)}30%{-webkit-transform-origin:100% 0;-webkit-transform:scale(0,0) rotate(360deg) translateY(100%)}100%{opacity:1;-webkit-transform-origin:0 0;-webkit-transform:scale(1,1) rotate(0deg) translateY(0)}}@-o-keyframes twisterInUp{0%{opacity:0;-o-transform-origin:100% 0;-o-transform:scale(0,0) rotate(360deg) translateY(100%)}30%{-o-transform-origin:100% 0;-o-transform:scale(0,0) rotate(360deg) translateY(100%)}100%{opacity:1;-o-transform-origin:0 0;-o-transform:scale(1,1) rotate(0deg) translateY(0)}}@-ms-keyframes twisterInUp{0%{opacity:0;filter:alpha(opacity=0);-ms-transform-origin:100% 0;-ms-transform:scale(0,0) rotate(360deg) translateY(100%)}30%{-ms-transform-origin:100% 0;-ms-transform:scale(0,0) rotate(360deg) translateY(100%)}100%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:0 0;-ms-transform:scale(1,1) rotate(0deg) translateY(0)}}@keyframes twisterInUp{0%{opacity:0;transform-origin:100% 0;transform:scale(0,0) rotate(360deg) translateY(100%)}30%{transform-origin:100% 0;transform:scale(0,0) rotate(360deg) translateY(100%)}100%{opacity:1;transform-origin:0 0;transform:scale(1,1) rotate(0deg) translateY(0)}}
@-moz-keyframes twisterInDown{0%{opacity:0;-moz-transform-origin:0 100%;-moz-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-moz-transform-origin:0 100%;-moz-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;-moz-transform-origin:100% 100%;-moz-transform:scale(1,1) rotate(0deg) translateY(0%)}}@-webkit-keyframes twisterInDown{0%{opacity:0;-webkit-transform-origin:0 100%;-webkit-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-webkit-transform-origin:0 100%;-webkit-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;-webkit-transform-origin:100% 100%;-webkit-transform:scale(1,1) rotate(0deg) translateY(0%)}}@-o-keyframes twisterInDown{0%{opacity:0;-o-transform-origin:0 100%;-o-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-o-transform-origin:0 100%;-o-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;-o-transform-origin:100% 100%;-o-transform:scale(1,1) rotate(0deg) translateY(0%)}}@-ms-keyframes twisterInDown{0%{opacity:0;filter:alpha(opacity=0);-ms-transform-origin:0 100%;-ms-transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{-ms-transform-origin:0 100%;-ms-transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:100% 100%;-ms-transform:scale(1,1) rotate(0deg) translateY(0%)}}@keyframes twisterInDown{0%{opacity:0;transform-origin:0 100%;transform:scale(0,0) rotate(360deg) translateY(-100%)}30%{transform-origin:0 100%;transform:scale(0,0) rotate(360deg) translateY(-100%)}100%{opacity:1;transform-origin:100% 100%;transform:scale(1,1) rotate(0deg) translateY(0%)}}

@-moz-keyframes holeOut{
	0%{opacity:1;-moz-transform-origin:50% 50%;-moz-transform:scale(1,1) rotateY(0deg)}
	100%{opacity:0;-moz-transform-origin:50% 50%;-moz-transform:scale(0,0) rotateY(180deg)}}@-webkit-keyframes holeOut{0%{opacity:1;-webkit-transform-origin:50% 50%;-webkit-transform:scale(1,1) rotateY(0deg)}100%{opacity:0;-webkit-transform-origin:50% 50%;-webkit-transform:scale(0,0) rotateY(180deg)}}@-o-keyframes holeOut{0%{opacity:1;-o-transform-origin:50% 50%;-o-transform:scale(1,1) rotateY(0deg)}100%{opacity:0;-o-transform-origin:50% 50%;-o-transform:scale(0,0) rotateY(180deg)}}@-ms-keyframes holeOut{0%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:50% 50%;-ms-transform:scale(1,1) rotateY(0deg)}100%{opacity:0;filter:alpha(opacity=0);-ms-transform-origin:50% 50%;-ms-transform:scale(0,0) rotateY(180deg)}}@keyframes holeOut{0%{opacity:1;transform-origin:50% 50%;transform:scale(1,1) rotateY(0deg)}100%{opacity:0;transform-origin:50% 50%;transform:scale(0,0) rotateY(180deg)}}
@-moz-keyframes swashIn{0%{opacity:0;-moz-transform-origin:50% 50%;-moz-transform:scale(0,0)}90%{opacity:1;-moz-transform-origin:50% 50%;-moz-transform:scale(0.9,.9)}100%{opacity:1;-moz-transform-origin:50% 50%;-moz-transform:scale(1,1)}}@-webkit-keyframes swashIn{0%{opacity:0;-webkit-transform-origin:50% 50%;-webkit-transform:scale(0,0)}90%{opacity:1;-webkit-transform-origin:50% 50%;-webkit-transform:scale(0.9,.9)}100%{opacity:1;-webkit-transform-origin:50% 50%;-webkit-transform:scale(1,1)}}@-o-keyframes swashIn{0%{opacity:0;-o-transform-origin:50% 50%;-o-transform:scale(0,0)}90%{opacity:1;-o-transform-origin:50% 50%;-o-transform:scale(0.9,.9)}100%{opacity:1;-o-transform-origin:50% 50%;-o-transform:scale(1,1)}}@-ms-keyframes swashIn{0%{opacity:0;filter:alpha(opacity=0);-ms-transform-origin:50% 50%;-ms-transform:scale(0,0)}90%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:50% 50%;-ms-transform:scale(0.9,.9)}100%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:50% 50%;-ms-transform:scale(1,1)}}@keyframes swashIn{0%{opacity:0;transform-origin:50% 50%;transform:scale(0,0)}90%{opacity:1;transform-origin:50% 50%;transform:scale(0.9,.9)}100%{opacity:1;transform-origin:50% 50%;transform:scale(1,1)}}
@-moz-keyframes openDownRightOut{0%{opacity:1;-moz-transform-origin:bottom right;-moz-transform:rotate(0deg);-moz-animation-timing-function:ease-out}100%{opacity:0;-moz-transform-origin:bottom right;-moz-transform:rotate(110deg);-moz-animation-timing-function:ease-in-out}}@-webkit-keyframes openDownRightOut{0%{opacity:1;-webkit-transform-origin:bottom right;-webkit-transform:rotate(0deg);-webkit-animation-timing-function:ease-out}100%{opacity:0;-webkit-transform-origin:bottom right;-webkit-transform:rotate(110deg);-webkit-animation-timing-function:ease-in-out}}@-o-keyframes openDownRightOut{0%{opacity:1;-o-transform-origin:bottom right;-o-transform:rotate(0deg);-o-animation-timing-function:ease-out}100%{opacity:0;-o-transform-origin:bottom right;-o-transform:rotate(110deg);-o-animation-timing-function:ease-in-out}}@-ms-keyframes openDownRightOut{0%{opacity:1;-ms-transform-origin:bottom right;-ms-transform:rotate(0deg);-ms-animation-timing-function:ease-out}100%{opacity:0;-ms-transform-origin:bottom right;-ms-transform:rotate(110deg);-ms-animation-timing-function:ease-in-out}}@keyframes openDownRightOut{0%{opacity:1;transform-origin:bottom right;transform:rotate(0deg);animation-timing-function:ease-out}100%{opacity:0;transform-origin:bottom right;transform:rotate(110deg);animation-timing-function:ease-in-out}}
@-moz-keyframes openUpLeftOut{0%{opacity:1;-moz-transform-origin:top left;-moz-transform:rotate(0deg);-moz-animation-timing-function:ease-out}100%{opacity:0;-moz-transform-origin:top left;-moz-transform:rotate(110deg);-moz-animation-timing-function:ease-in-out}}@-webkit-keyframes openUpLeftOut{0%{opacity:1;-webkit-transform-origin:top left;-webkit-transform:rotate(0deg);-webkit-animation-timing-function:ease-out}100%{opacity:0;-webkit-transform-origin:top left;-webkit-transform:rotate(110deg);-webkit-animation-timing-function:ease-in-out}}@-o-keyframes openUpLeftOut{0%{opacity:1;-o-transform-origin:top left;-o-transform:rotate(0deg);-o-animation-timing-function:ease-out}100%{opacity:0;-o-transform-origin:top left;-o-transform:rotate(110deg);-o-animation-timing-function:ease-in-out}}@-ms-keyframes openUpLeftOut{0%{opacity:1;-ms-transform-origin:top left;-ms-transform:rotate(0deg);-ms-animation-timing-function:ease-out}100%{opacity:0;-ms-transform-origin:top left;-ms-transform:rotate(110deg);-ms-animation-timing-function:ease-in-out}}@keyframes openUpLeftOut{0%{opacity:1;transform-origin:top left;transform:rotate(0deg);animation-timing-function:ease-out}100%{opacity:0;transform-origin:top left;transform:rotate(110deg);animation-timing-function:ease-in-out}}
@-moz-keyframes swashOut{0%{opacity:1;-moz-transform-origin:50% 50%;-moz-transform:scale(1,1)}80%{opacity:1;-moz-transform-origin:50% 50%;-moz-transform:scale(0.9,.9)}100%{opacity:0;-moz-transform-origin:50% 50%;-moz-transform:scale(0,0)}}@-webkit-keyframes swashOut{0%{opacity:1;-webkit-transform-origin:50% 50%;-webkit-transform:scale(1,1);transform:scale(1,1)}80%{opacity:1;-webkit-transform-origin:50% 50%;-webkit-transform:scale(0.9,.9)}100%{opacity:0;-webkit-transform-origin:50% 50%;-webkit-transform:scale(0,0)}}@-o-keyframes swashOut{0%{opacity:1;-o-transform-origin:50% 50%;-o-transform:scale(1,1)}80%{opacity:1;-o-transform-origin:50% 50%;-o-transform:scale(0.9,.9)}100%{opacity:0;-o-transform-origin:50% 50%;-o-transform:scale(0,0)}}@-ms-keyframes swashOut{0%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:50% 50%;-ms-transform:scale(1,1)}80%{opacity:1;filter:alpha(opacity=100);-ms-transform-origin:50% 50%;-ms-transform:scale(0.9,.9)}100%{opacity:0;filter:alpha(opacity=0);-ms-transform-origin:50% 50%;-ms-transform:scale(0,0)}}@keyframes swashOut{0%{opacity:1;transform-origin:50% 50%;transform:scale(1,1)}80%{opacity:1;transform-origin:50% 50%;transform:scale(0.9,.9)}100%{opacity:0;transform-origin:50% 50%;transform:scale(0,0)}}

@-moz-keyframes slideDownRetourn{0%{-moz-transform-origin:0 0;-moz-transform:translateY(100%)}100%{-moz-transform-origin:0 0;-moz-transform:translateY(0%)}}@-webkit-keyframes slideDownRetourn{0%{-webkit-transform-origin:0 0;-webkit-transform:translateY(100%)}100%{-webkit-transform-origin:0 0;-webkit-transform:translateY(0%)}}@-o-keyframes slideDownRetourn{0%{-o-transform-origin:0 0;-o-transform:translateY(100%)}100%{-o-transform-origin:0 0;-o-transform:translateY(0%)}}@-ms-keyframes slideDownRetourn{0%{-ms-transform-origin:0 0;-ms-transform:translateY(100%)}100%{-ms-transform-origin:0 0;-ms-transform:translateY(0%)}}@keyframes slideDownRetourn{0%{transform-origin:0 0;transform:translateY(100%)}100%{transform-origin:0 0;transform:translateY(0%)}}@-moz-keyframes slideLeftRetourn{0%{-moz-transform-origin:0 0;-moz-transform:translateX(-100%)}100%{-moz-transform-origin:0 0;-moz-transform:translateX(0%)}}
@-moz-keyframes slideLeftRetourn{0%{-moz-transform-origin:0 0;-moz-transform:translateX(-100%)}100%{-moz-transform-origin:0 0;-moz-transform:translateX(0%)}}@-webkit-keyframes slideLeftRetourn{0%{-webkit-transform-origin:0 0;-webkit-transform:translateX(-100%)}100%{-webkit-transform-origin:0 0;-webkit-transform:translateX(0%)}}@-o-keyframes slideLeftRetourn{0%{-o-transform-origin:0 0;-o-transform:translateX(-100%)}100%{-o-transform-origin:0 0;-o-transform:translateX(0%)}}@-ms-keyframes slideLeftRetourn{0%{-ms-transform-origin:0 0;-ms-transform:translateX(-100%)}100%{-ms-transform-origin:0 0;-ms-transform:translateX(0%)}}@keyframes slideLeftRetourn{0%{transform-origin:0 0;transform:translateX(-100%)}100%{transform-origin:0 0;transform:translateX(0%)}}
@-moz-keyframes slideRightRetourn{0%{-moz-transform-origin:0 0;-moz-transform:translateX(100%)}100%{-moz-transform-origin:0 0;-moz-transform:translateX(0%)}}@-webkit-keyframes slideRightRetourn{0%{-webkit-transform-origin:0 0;-webkit-transform:translateX(100%)}100%{-webkit-transform-origin:0 0;-webkit-transform:translateX(0%)}}@-o-keyframes slideRightRetourn{0%{-o-transform-origin:0 0;-o-transform:translateX(100%)}100%{-o-transform-origin:0 0;-o-transform:translateX(0%)}}@-ms-keyframes slideRightRetourn{0%{-ms-transform-origin:0 0;-ms-transform:translateX(100%)}100%{-ms-transform-origin:0 0;-ms-transform:translateX(0%)}}@keyframes slideRightRetourn{0%{transform-origin:0 0;transform:translateX(100%)}100%{transform-origin:0 0;transform:translateX(0%)}}
@-moz-keyframes slideUpRetourn{0%{-moz-transform-origin:0 0;-moz-transform:translateY(-100%)}100%{-moz-transform-origin:0 0;-moz-transform:translateY(0%)}}@-webkit-keyframes slideUpRetourn{0%{-webkit-transform-origin:0 0;-webkit-transform:translateY(-100%)}100%{-webkit-transform-origin:0 0;-webkit-transform:translateY(0%)}}@-o-keyframes slideUpRetourn{0%{-o-transform-origin:0 0;-o-transform:translateY(-100%)}100%{-o-transform-origin:0 0;-o-transform:translateY(0%)}}@-ms-keyframes slideUpRetourn{0%{-ms-transform-origin:0 0;-ms-transform:translateY(-100%)}100%{-ms-transform-origin:0 0;-ms-transform:translateY(0%)}}@keyframes slideUpRetourn{0%{transform-origin:0 0;transform:translateY(-100%)}100%{transform-origin:0 0;transform:translateY(0%)}}
</style>
<style>
  .circle{display:inline-block;width:48px;height:48px;border-radius:24px;border:1px solid #323A45;text-align:center;line-height:48px;
    transition:all ease-in-out .3s;color:#323A45;float:left}
  .circle:hover{color:#fff;background-color:#323A45;text-decoration:none;}
  .circle i{line-height:1;}
  p.description{font-size:13px;opacity:.7;line-height:1.4em;text-align:justify;color:rgba(50,58,69,.7);}
  ul.half{padding:0;background-color:transparent;}
  ul.half li.noborder{width:50%;border:0;margin:0;}
  li.noborder h4{font-size:17px;margin:5px 0;font-weight:700;}
  article{opacity:.7;line-height: 24px;margin: 25px 0;text-align: justify;}
  .sub-div{width:80%;float:left;padding-left:10px;word-break:break-all; word-wrap:break-word;}
  .screen-img{width:100%;height:100%;position: relative;display: inline-block;margin:0 auto;text-align:center;}
  img.lazy,.updown-tip {
    -webkit-transition: all ease-in-out .4s;
    -moz-transition: all ease-in-out .4s;
    -ms-transition: all ease-in-out .4s;
    -o-transition: all ease-in-out .4s;
    transition: all ease-in-out .4s;
    opacity: 0;
  }
  img.screen {position:absolute;left:0;top:50%;bottom:0;right:0;opacity:0;margin-top:-175px;
  transform: translateX(-50px);
  -webkit-transition: all ease-in-out .3s;
  -moz-transition: all ease-in-out .3s;
  -ms-transition: all ease-in-out .3s;
  -o-transition: all ease-in-out .3s;
  transition: all ease-in-out .3s;
}
img.screen.active,.updown-tip.active{opacity: 1;transform: translateX(0);}

</style>
<!-- <div class="zw" style="height:5px;width:100%"></div> -->
<div class="oauth_container">
	<div class="fullpage" style="height: 100%; position: relative;top:0;">
		
    <div class="section firstSn page-background table activeSn" style="height:100%;background-color: rgb(255, 255, 255);margin-bottom:0;">
      <div class="tableCell" style="height:100%;">
        <%@ include file="header.jsp"%>
              <div id="background-video" style="height:100%;top:0;left:0;">
                <div id="carousel-example-generic" class="carousel slide" data-ride="carousel">
                  <!-- Wrapper for slides -->
                  <div class="carousel-inner" role="listbox">
                    <div class="item active">
                      <img src="${ctx}/static/img/userbg.jpg" alt="...">
                      <div class="carousel-caption" >
                    <!--  文字说明 -->
                      </div>
                    </div>
                    <div class="item">
                      <img src="${ctx}/static/img/userbg2.jpg" alt="...">
                      <div class="carousel-caption">
                      </div>
                    </div>
                    <div class="item">
                      <img src="${ctx}/static/img/userbg3.jpg" alt="...">
                      <div class="carousel-caption">
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="page-background-image"></div>
              <div class="description-wrapper">
                  <div id="video-description">
                      <div class="words">
                          <h1>VaaS模式<span style="opacity: 0">，</span>开启未来</h1>
                          <h2>让梦想到可见更简单 · 一站式按需构建部署 · 高可扩展服务稳定</h2>
                           <!-- Controls -->
                          <a class="left carousel-control" href="#carousel-example-generic" role="button" data-slide="prev">
                            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                            <span class="sr-only">Previous</span>
                          </a>
                          <a class="right carousel-control" href="#carousel-example-generic" role="button" data-slide="next">
                            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                            <span class="sr-only">Next</span>
                          </a>
                      </div>
                  </div>
              </div>
          </div>
      </div>
		<div class="section table hidden sec-sect" style="background-color: rgb(255, 255, 255);">
			<div class="tableCell">
	            <div class="section-inner">
	                <div class="left-section">
	                    <div class="article-wrapper frame-3">
	                        <h1 class="magictime slideLeftRetourn" style="opacity: 1;">关系型数据库RDS</h1>
	                        <p class="show">
	                            即开即用、稳定可靠、可弹性在线伸缩、完善的性能监控体系、并提供专业的数据库备份、恢复及优化方案
<br>
	                        </p>
	                        <a href="/helpCenter/helpCenter.jsp?container=help-createDb" target="_black"><span class="glyphicon glyphicon-hand-right"></span>了解更多</a>
	                        <ul class="screenshot">
	                            <li data-position="right middle">
	                                <a  title="数据库RDS">
	                                    <img src="${ctx}/static/img/img-aL.jpg" alt="">
	                                </a>
	                            </li>
	                            <li data-position="right middle">
	                                <a  title="缓存服务">
	                                    <img src="${ctx}/static/img/img-bL.jpg" alt="">
	                                </a>
	                            </li>
	                            <li data-position="right middle">
	                                <a  title="分布式RDS">
	                                    <img src="${ctx}/static/img/img-cL.jpg" alt="">
	                                </a>
	                            </li>
	                        </ul>
	                    </div>
	                </div>
	                <div class="right-section">
	                    <div class="circle-wrapper">
	                        <div class="blue doted-circle circling">
	                        </div>
	                        <div class="blue filled-circle">
	                            <div class="table-wrapper frame-2-2 hidden first-children">
	                                <div class="word-wrapper swashIn magictime holeOut">
	                                    <img src="${ctx}/static/img/c6e91970cd4ed6d3d75f1ba027a51480.png" alt="" class="part-1">
	                                    <h2></h2>
	                                </div>
	                            </div>
	                            <div class="table-wrapper frame-2-3 hidden">
	                                <div class="word-wrapper openDownRightOut magictime slideLeftRetourn">
	                                    <img src="${ctx}/static/img/87f1baf73a3130d273c36cd2f1e7b452.png" alt="" class="part-1">
	                                    <h2>代码托管</h2>
	                                </div>
	                            </div>
	                            <div class="table-wrapper frame-2-4 hidden">
	                                <div class="word-wrapper openUpLeftOut magictime slideRightRetourn">
	                                    <img src="${ctx}/static/img/eb783668c50d9fb69f1559470264c0e3.png" alt="" class="part-1">
	                                    <h2>运行空间<br>(Paas)</h2>

	                                </div>
	                            </div>
	                            <div class="table-wrapper frame-2-5 hidden">
	                                <div class="word-wrapper swashOut magictime slideDownRetourn">
	                                    <img src="${ctx}/static/img/15fc474671ffd6f88773924b97610634.png" alt="" class="part-1">
	                                    <h2 style="height: auto; margin-top: 40px;">质量控制</h2>
	                                </div>
	                            </div>
	                            <!-- <div class="table-wrapper frame-2-6 hidden last-children">
	                                <img src="img/4579875e7d66b84fa4e96119356cb034.png" alt="" class="part-1 magictime swashIn">
	                                <img src="img/b0e596561669088619b6adfb4a20e1dd.png" alt="" class="part-2 magictime swashIn">
	                                <img src="img/08b1573ac84744faa5e963059b1d18d1.png" alt="" class="part-3 magictime swashIn">
	                                <img src="img/d657cd3da45603b60b2c578a8095031f.png" alt="" class="part-4 magictime swashIn">
	                                <img src="img/7d2ba4136e2368c09ea0879107f89189.png" alt="" class="part-5 magictime swashIn">
	                                <div class="word-wrapper">
	                                    <img src="img/eb783668c50d9fb69f1559470264c0e3.png" class="frame-2-6-2 magictime swashIn" style="display: inline;">
	                                    <img src="img/9383ecf417be779e10905fe3521118af.png" class="frame-2-6-1" style="display: block; top: 54%; opacity: 0;">
	                                </div>
	                            </div> -->
	                        </div>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
	    <div class="section table hidden thi-sect" style=" background-color: rgb(257, 256, 242);">
	    	<div class="tableCell">
	            <div class="section-inner">
	                <div class="left-section">
	                	<div class="circle-wrapper">
	                        <div class="red doted-circle circling">
	                        </div>
	                        <div class="red filled-circle">
	                            <img src="${ctx}/static/img/4282e17f31acb10d9cd8257a7043d93f.png" alt="" class="frame-3 part-1">
	                            <img src="${ctx}/static/img/a8d7303b7bb154a491e16a53c542e03f.png" alt="" class="frame-3 part-2">
	                            <img src="${ctx}/static/img/67bf9b845b9d31526fab1c62ca20bd3d.png" alt="" class="frame-3 part-3">
	                        </div>
	                        
	                    </div>
	                </div>
	                <div class="right-section">
	                    <div class="article-wrapper frame-3">
	                        <h1 class="magictime slideLeftRetourn" style="opacity: 1;">云引擎 GCE</h1>
	                        <p class="show">
	                           云引擎，基于弹性扩展的网络应用托管平台<br>
                             安全可靠、快捷开发、自动弹性、扩展服务<br>
	                        </p>
	                        <a href="/helpCenter/helpCenter.jsp?container=product-GCEIntro" target="_black"><span class="glyphicon glyphicon-hand-right"></span>了解更多</a>
	                        <ul class="screenshot">
	                            <li data-position="right middle">
	                                <a title="数据库RDS">
	                                    <img src="${ctx}/static/img/gce1-lg.jpg" alt="">
	                                </a>
	                            </li>
	                            <li data-position="right middle">
	                                <a title="缓存服务">
	                                    <img src="${ctx}/static/img/gce2-lg.jpg" alt="">
	                                </a>
	                            </li>
	                            <li data-position="right middle">
	                                <a title="分布式RDS">
	                                    <img src="${ctx}/static/img/gce3-lg.jpg" alt="">
	                                </a>
	                            </li>
	                        </ul>
	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>
      
      <div class="section table hidden for-sect" style="background-color: rgb(255, 255, 255);">
        <div class="tableCell">
            <div class="section-inner">
              <div class="left-section">
                  <div class="article-wrapper frame-3">
                      <h2 class="magictime slideLeftRetourn" style="opacity: 1;">负载均衡 SLB</h2>
                      <ul class="screenshot half clearfix">
                        <li class="noborder" data-screen="screen_fuzai1">
                          <a href="javascript:void(0);" class="circle">特性一</a>
                          <div class="sub-div">
                            <h4>负载均衡</h4>
                            <p class="show description">流量分发 / 消除单点故障，提升应用系统的可用性</p>
                          </div>
                        </li>
                        <li class="noborder" data-screen="screen_fuzai2">
                          <a href="javascript:void(0);" class="circle">特性二</a>
                          <div class="sub-div">
                            <h4>虚拟服务IP</h4>
                            <p class="show description">虚拟服务地址，提供高性能、高可用的应用服务池</p>
                          </div>
                        </li>
                        <li class="noborder" data-screen="screen_fuzai3">
                          <a href="javascript:void(0);" class="circle">特性三</a>
                          <div class="sub-div">
                            <h4>配套服务</h4>
                            <p class="show description">SLB服务是面向多机方案的一个配套服务</p>
                          </div>
                        </li>
                      </ul>
                      <article>
                        SLB是Server Load Balance（负载均衡）的简称，是对多台云服务器进行流量分发的负载均衡服务。SLB可以通过流量分发扩展应用系统对外的服务能力，通过消除单点故障提升应用系统的可用性。<a href="/helpCenter/helpCenter.jsp?container=product-SLBIntro" target="_black"><span class="glyphicon glyphicon-hand-right"></span>了解更多</a>
                      </article>
                  </div>
              </div>
              <div class="right-section">
                  <div class="circle-wrapper">
                      <div class="screen-img">
                        <img class="screen lazy active" data-image-src="screen_fuzai1" src="${ctx}/static/img/fuzai1.png">
                        <img class="screen lazy" data-image-src="screen_fuzai2" src="${ctx}/static/img/fuzai2.png">
                        <img class="screen lazy" data-image-src="screen_fuzai3" src="${ctx}/static/img/fuzai3.png">
                      </div>
                  </div>
              </div>
            </div>
          </div>
      </div>
      <div class="section table hidden fiv-sect" style=" background-color: rgb(257, 256, 242);">
        <div class="tableCell">
              <div class="section-inner">
                  <div class="left-section">
                    <div class="circle-wrapper">
                          <div class="screen-img">
                            <img class="screen lazy active" data-image-src="screen_oss1" src="${ctx}/static/img/oss1.png">
                            <img class="screen lazy" data-image-src="screen_oss2" src="${ctx}/static/img/oss2.png">
                          </div>
                      </div>
                  </div>
                  <div class="right-section">
                      <div class="article-wrapper frame-3">
                        <h2 class="magictime swashIn" style="opacity: 1;">开放存储服务 OSS</h2>
                        <ul class="screenshot half magictime swashIn clearfix">
                          <li class="noborder" data-screen="screen_oss1">
                            <a href="javascript:void(0);" class="circle">特性一</a>
                            <div class="sub-div">
                              <h4>实时监控</h4>
                              <p class="show description">实时监控，及时掌握流量动向；分布式存储，保障存储数据安全</p>
                            </div>
                          </li>
                          <li class="noborder" data-screen="screen_oss2">
                            <a href="javascript:void(0);" class="circle">特性二</a>
                            <div class="sub-div">
                              <h4>动态按需扩容</h4>
                              <p class="show description">基于云计算平台动态扩展，存储容量与流量按实际用量自由伸缩；</p>
                            </div>
                          </li>
                        </ul>
                        <article>
                          OSS为open storage service（开放存储服务），用户使用http请求即可使用存储服务，通过matrix、swiftclient及cyberduck客户端工具使用。<br>
                          安全稳定，数据可靠&nbsp;海量存储&nbsp;性能卓越&nbsp;弹性计费<a href="/helpCenter/helpCenter.jsp?container=help-OSSuse" target="_black"><span class="glyphicon glyphicon-hand-right"></span>了解更多</a>
                        </article>
                    </div>
                  </div>
              </div>
          </div>
      </div>
      <!-- <div class="section table hidden six-sect" style="background-color: rgb(255, 255, 255);">
        <div class="tableCell">
            <div class="section-inner">
              <div class="left-section">
                  <div class="article-wrapper frame-3">
                      <h2 class="magictime twisterInDown" style="opacity: 1;">开放缓存服务 OCS</h2>
                      <ul class="screenshot half clearfix">
                        <li class="noborder" data-screen="screen_ocs1">
                          <a href="javascript:void(0);" class="circle">特性一</a>
                          <div class="sub-div">
                            <h4>兼容性</h4>
                            <p class="show description">memcache binary protocol，符合该协议的客户端(binary SASL)都可使用OCS</p>
                          </div>
                        </li>
                        <li class="noborder" data-screen="screen_ocs2">
                          <a href="javascript:void(0);" class="circle">特性二</a>
                          <div class="sub-div">
                            <h4>弹性伸缩</h4>
                            <p class="show description">随时根据需要修改OCS实例的配置，并且在配置变更过程中，OCS实例不会停止服务</p>
                          </div>
                        </li>
                        <li class="noborder" data-screen="screen_ocs3">
                          <a href="javascript:void(0);" class="circle">特性三</a>
                          <div class="sub-div">
                            <h4>性能优越</h4>
                            <p class="show description">缓存数据存储在内存中，数据访问在数毫秒内返回</p>
                          </div>
                        </li>
                        <li class="noborder" data-screen="screen_ocs4">
                          <a href="javascript:void(0);" class="circle">特性四</a>
                          <div class="sub-div">
                            <h4>安全保障</h4>
                            <p class="show description">OCS仅支持ECS访问，并可以限制源服务器的IP地址，避免外部攻击</p>
                          </div>
                        </li>
                      </ul>
                      <article class="magictime twisterInDown">
                        Coding 的代码托管功能除了基本的 Git 仓库以外，还有保护分支，分屏对比， Code Review 等高级功能。并且整合了 CodeInsight，质量管理，演示平台等开发工具，提升研发效率。
                      </article>
                  </div>
              </div>
              <div class="right-section">
                  <div class="circle-wrapper">
                      <div class="screen-img">
                        <img class="screen lazy active" data-image-src="screen_ocs1" src="${ctx}/static/img/oss1.png">
                        <img class="screen lazy" data-image-src="screen_ocs2" src="${ctx}/static/img/oss2.png">
                        <img class="screen lazy" data-image-src="screen_ocs3" src="${ctx}/static/img/oss3.png">
                        <img class="screen lazy" data-image-src="screen_ocs4" src="${ctx}/static/img/oss4.png">
                      </div>
                  </div>
              </div>
            </div>
          </div>
      </div> -->
      <div class="section table footer hidden lastSn">
        <div class="tableCell">
            <div class="wrapper">
                <div class="inner-wrapper last page-background">
                    <div class="page-background-image"></div>
                    <h1>即刻体验云端开发之美<span style="opacity: 0">；</span>做拥抱云时代的开发者</h1>
                </div>
            </div>
            <div id="footer">
              <%@ include file="footer.jsp"%>
            </div>
        </div>
      </div>
    </div>
    <div class="down">
      <div class="updown-tip">下一页</div>
      <span class="glyphicon glyphicon-chevron-down"></span>
    </div>
    <div class="up hide">
    <div class="updown-tip">上一页</div>
    <span class="glyphicon glyphicon-chevron-up"></span>
    </div>

    <div id="fullPage-nav" class="right light-nav" style="color: rgb(0, 0, 0); margin-top: -63.5px;">
    	<ul>
    		<li>
    			<a href="#" class="fir-a activel"><span></span></a></li>
    		<li>
    			<a href="#" class="sec-a"><span></span></a></li>
    		<li>
    			<a href="#" class="thi-a"><span></span></a></li>
          <li>
          <a href="#" class="for-a"><span></span></a></li>
          <li>
          <a href="#" class="fiv-a"><span></span></a></li>
          <!-- <li>
          <a href="#" class="six-a"><span></span></a></li> -->
    		<li>
    			<a href="#" class="ft-a"><span></span></a></li>
    		<!-- <li>
    			<a href="#"><span></span></a></li>
    		<li>
    		<a href="#"><span></span></a></li> -->
    	</ul>
    </div>
</div>
<style>
.img-mfy {width: 100%;height: 100%;position: absolute;left: 0;top:0;z-index: 102;background: rgba(0,0,0,.6);}
.img-mfy i{font-size: 30px;position:relative;top:0;left:-5px;}
.img-mfy i:hover{cursor:pointer;}
</style>
<div class="img-mfy hidden">
  <img src="" />
  <i class="fa fa-times-circle"></i>
</div>
<script>
$('.carousel').carousel({
  interval: 3000
});
var _fw=$('.footer').find('.last');
var o=0,p=0;
var _fp=$('.fullpage');
var _fpnav=$('#fullPage-nav');
var _sn=$('.section');
var _tableC=$('.tableCell');
var _this=$('.activeSn');
var _down=$('.down');
var _up=$('.up');
var _tarImg=$('.img-mfy');
$('.oauth-footer').addClass('');
$('.oauth_header').css({
	position: 'absolute',
	top: '0',
	left:'0',
	"z-index":'999',
});
$(window).load(function() {
	// _fp.css('top', '0');
	$('.first').addClass('active')
				.siblings().removeClass('active');
	setH();
});
$(window).resize(function(event) {
	setH();
});
function setH(){
	var ih=window.innerHeight?window.innerHeight:document.body.clientHeight;
	var iw=window.innerWidth?window.innerWidth:document.body.clientWidth;
	_sn.css({
		height: ih,
		width: iw
	});
	_tableC.css({
		height: ih,
    width:iw
	});
  _fw.css({
    height: ih-273,
    width:iw
  });
	$('#video_background').css({
		height: ih,
		width: iw
	});
  $('.carousel').find('img').css({
    height: ih,
    width: iw
  });
  _tarImg.children('img').css({
                          width:iw*.8,
                          height:ih*.8,
                          'margin-top': ih*.1,
                          'margin-left':iw*.1
                        });
  _tarImg.children('i').css('top',-ih*.35);
}
document.body.onmousewheel = function(event) {
    event = event || window.event;
    p=event.wheelDelta;
    var $this = $(this),
    timeoutId = $this.data('timeoutId');
    if (timeoutId) {
        clearTimeout(timeoutId);
    }
    $this.data('timeoutId', setTimeout(function() {
        //do something
        switchSec(p);
        $this.removeData('timeoutId');
        $this = null
    }, 100));
    return false;	
};
document.body.addEventListener("DOMMouseScroll", function(event) {
    p=event.detail;
    var $this = $(this),
    timeoutId = $this.data('timeoutId');
    if (timeoutId) {
        clearTimeout(timeoutId);
    }
    $this.data('timeoutId', setTimeout(function() {
        switchSec(p);
        $this.removeData('timeoutId');
        $this = null
    }, 100));
    return false;	
});
function switchSec(p){
	if((p==120)||(p==-3)||(p==240)||(p==-6)||(p==360)||(p==-9)){//up		
		if($('.activeSn').hasClass('firstSn')){
			_fp.css('top', '0');
			_fpnav.addClass('light-nav');
      _down.removeClass('hide');
      _up.addClass('hide');
      switchAni();
		}else{
      _down.removeClass('hide');
      _up.removeClass('hide');
			$('.activeSn').prev().removeClass('hidden')
								.addClass('activeSn')
							.next().removeClass('activeSn');
			_fpnav.removeClass('light-nav');
      switchAni();
			//改变dot 的颜色、状态
			$('.activel').parent().prev().find('a').addClass('activel')
						.parent().next().find('a').removeClass('activel');
			//改变 top 值,不变 直观上可以
			// var h=$('.activeSn').offset().top;
			if($('.activeSn').hasClass('firstSn')){
        _up.addClass('hide');
				_fpnav.addClass('light-nav');
			}
		}
	}
	if((p==-120)||(p==3)||(p==-240)||(p==6)||(p==-360)||(p==9)){//down
		_fpnav.removeClass('light-nav');
		if($('.activeSn').hasClass('lastSn')){
      _fpnav.addClass('light-nav');
      _down.addClass('hide');
      _up.removeClass('hide');
      switchAni();
		}else{
       _down.removeClass('hide');
      _up.removeClass('hide');
			$('.activeSn').next().removeClass('hidden')
								.addClass('activeSn')
							.prev().removeClass('activeSn')
									.addClass('hidden');
      if($('.activeSn').hasClass('lastSn')){
        _fpnav.addClass('light-nav');
         _down.addClass('hide');
      }
      switchAni();
			//改变dot 的颜色、状态
			$('.activel').parent().next().find('a').addClass('activel')
						.parent().prev().find('a').removeClass('activel');
			//改变top值,不变 直观上可以							
		}
	}
}
$('.fir-a').click(function(event) {
  _fp.css('top', '0');
  _down.removeClass('hide');
  _up.addClass('hide');  
  $('.firstSn').addClass('activeSn').fadeIn('slow', function() {
    $('.firstSn').removeClass('hidden');
  });
  $('.firstSn').siblings().removeClass('activeSn').addClass('hidden');
  _fpnav.addClass('light-nav');
  $('.fir-a').addClass('activel')
          .parent().siblings().find('a').removeClass('activel');
});
$('.sec-a').click(function(event) {
  _down.removeClass('hide');
  _up.removeClass('hide');  
  $('.sec-sect').addClass('activeSn').removeClass('hidden')
                .siblings().removeClass('activeSn').addClass('hidden');
  _fpnav.removeClass('light-nav');
  $('.sec-a').addClass('activel')
          .parent().siblings().find('a').removeClass('activel');
  switchAni();
});
$('.thi-a').click(function(event) {
  _down.removeClass('hide');
  _up.removeClass('hide');  
  $('.thi-sect').addClass('activeSn').removeClass('hidden')
              .siblings().removeClass('activeSn').addClass('hidden');
  _fpnav.removeClass('light-nav');
  $('.thi-a').addClass('activel')
          .parent().siblings().find('a').removeClass('activel');
  switchAni();
});
$('.for-a').click(function(event) {
  _down.removeClass('hide');
  _up.removeClass('hide');  
  $('.for-sect').addClass('activeSn').removeClass('hidden')
              .siblings().removeClass('activeSn').addClass('hidden');
  _fpnav.removeClass('light-nav');
  $('.for-a').addClass('activel')
          .parent().siblings().find('a').removeClass('activel');
  switchAni();
});
$('.fiv-a').click(function(event) {
  _down.removeClass('hide');
  _up.removeClass('hide');  
  $('.fiv-sect').addClass('activeSn').removeClass('hidden')
              .siblings().removeClass('activeSn').addClass('hidden');
  _fpnav.removeClass('light-nav');
  $('.fiv-a').addClass('activel')
          .parent().siblings().find('a').removeClass('activel');
  switchAni();
});
// $('.six-a').click(function(event) {
//   _down.removeClass('hide');
//   _up.removeClass('hide');  
//   $('.six-sect').addClass('activeSn').removeClass('hidden')
//               .siblings().removeClass('activeSn').addClass('hidden');
//   _fpnav.removeClass('light-nav');
//   $('.six-a').addClass('activel')
//           .parent().siblings().find('a').removeClass('activel');
//   switchAni();
// });
$('.ft-a').click(function(event) {
  _down.addClass('hide');
    _up.removeClass('hide');  
  $('.lastSn').addClass('activeSn').fadeIn('400', function() {
    $('.lastSn').removeClass('hidden')
  });
  $('.lastSn').siblings().removeClass('activeSn').addClass('hidden');
  _fpnav.addClass('light-nav');
  $('.ft-a').addClass('activel')
          .parent().siblings().find('a').removeClass('activel');
});
function switchAni(){
  var s2=$('.frame-2-2');
  var s3=$('.frame-3');
  if(s3.parent().parent().parent().parent().parent().parent().hasClass('activeSn')){
    if(s3.hasClass('part-1')){
    s3.addClass('magictime twisterInDown')
      .next().addClass('magictime twisterInUp')
      .next().addClass('magictime twisterInUp')
    }
  }else{
    if(s3.hasClass('part-1')){
    s3.removeClass('magictime twisterInDown')
      .next().removeClass('magictime twisterInUp')
      .next().removeClass('magictime twisterInUp')
    }
  }
  if(s2.parents('.section').hasClass('activeSn')){
    s2.removeClass('hidden')
      .find('.word-wrapper').addClass('magictime holeOut');
    // setTimeout(function(){
    //   s2.find('.word-wrapper').addClass('holeOut');
    //   s2.addClass('hidden');
    // },1000);
    s2.next().removeClass('hidden')
              .children().addClass('magictime slideLeftRetourn');
    // setTimeout(function(){
    //   s2.next().children().addClass('slideLeftRetourn');
    //   s2.next().addClass('hidden');
    // },2000);
    s2.next().next().removeClass('hidden')
                    .children().addClass('magictime slideRightRetourn');
    // setTimeout(function(){
    //   s2.next().next().addClass('hidden');
    // },3000);
    s2.next().next().next().removeClass('hidden')
                    .children().addClass('magictime slideDownRetourn');
    // setTimeout(function(){
    //   s2.next().next().next().addClass('hidden');
    // },4000);
    // s2.next().next().next().next().removeClass('hidden').addClass('together')
    //                               .children('img').addClass('magictime swashIn')
    //                               .next().children('.frame-2-6-1').css({
    //                                 display:'block',
    //                                 top: '54%',
    //                                 opacity: '1'
    //                               });

  }else{
    s2.addClass('hidden')
      .find('.word-wrapper').removeClass('magictime holeOut');
    s2.next().addClass('hidden')
              .children().removeClass('magictime slideLeftRetourn');
    s2.next().next().addClass('hidden')
                    .children().removeClass('magictime slideRightRetourn');
    s2.next().next().next().addClass('hidden')
                    .children().removeClass('magictime slideDownRetourn');
    // s2.next().next().next().next().removeClass('together').addClass('hidden')
    //                               .children('img').removeClass('magictime swashIn')
    //                               .next().children('.frame-2-6-1').css({
    //                                 display:'none',
    //                                 top: '20%',
    //                                 opacity: '0'
    //                               });
  }
}
udTrans();
function udTrans(){
  var _down=$('.down');
  var _up=$('.up');
  _down.click(function(event) {
    //下一个section
    _fpnav.removeClass('light-nav');
    if($('.activeSn').hasClass('lastSn')){
      _fpnav.addClass('light-nav');
      _down.addClass('hide');
      _up.removeClass('hide');
      switchAni();
    }else{
       _down.removeClass('hide');
      _up.removeClass('hide');
      $('.activeSn').next().removeClass('hidden')
                .addClass('activeSn')
              .prev().removeClass('activeSn')
                  .addClass('hidden');
      if($('.activeSn').hasClass('lastSn')){
        _fpnav.addClass('light-nav');
         _down.addClass('hide');
      }
      switchAni();
      //改变dot 的颜色、状态
      $('.activel').parent().next().find('a').addClass('activel')
            .parent().prev().find('a').removeClass('activel');
      //改变top值,不变 直观上可以             
    }
  });
  _up.click(function(event) {
    if($('.activeSn').hasClass('firstSn')){
      _fp.css('top', '0');
      _fpnav.addClass('light-nav');
      _down.removeClass('hide');
      _up.addClass('hide');
      switchAni();
    }else{
      _down.removeClass('hide');
      _up.removeClass('hide');
      $('.activeSn').prev().removeClass('hidden')
                .addClass('activeSn')
              .next().removeClass('activeSn');
      _fpnav.removeClass('light-nav');
      switchAni();
      //改变dot 的颜色、状态
      $('.activel').parent().prev().find('a').addClass('activel')
            .parent().next().find('a').removeClass('activel');
      //改变 top 值,不变 直观上可以
      // var h=$('.activeSn').offset().top;
      if($('.activeSn').hasClass('firstSn')){
        _up.addClass('hide');
        _fpnav.addClass('light-nav');
      }
    }
  });
}
function imgClick(){
  var _thisImg=$('.screenshot').find('img');
  var _tg=$('.img-mfy');
  _thisImg.click(function(event) {
    var src=$(this).attr('src');
    _tg.children('img').attr('src',src);
    _tg.removeClass('hidden');
  });
  _tg.click(function(event) {
    _tg.addClass('hidden');
  });
}
imgClick();//无法自适应
function imgHover(){
 var _target=$('.circle').parent();
 _target.hover(function() {
   var temp=$(this).attr('data-screen');
   var imgarry=$('.screen-img').find('img');
   imgarry.each(function(index) {
    var imgtarget=$(this).attr('data-image-src');
    if(index==0){      
    }else{
      if(imgtarget==temp){
        $(this).addClass('active').siblings().removeClass('active');
      }
    }
   });
 }, function() {
   $('.screen-img').find('img:first').addClass('active').siblings().removeClass('active');
 });
}
imgHover();
function updowntip(){
 var _target=$('.updown-tip').parent();
 _target.hover(function() {
   $(this).children('.updown-tip').addClass('active');
 }, function() {
  $(this).children('.updown-tip').removeClass('active');
 });
}
updowntip();
</script>
</body>