<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="renderer" content="webkit">
	<title>乐视云计算-最专业的VaaS云平台</title>
	<meta name="Keywords" content="乐视云计算，云计算，VaaS，视频存储，免费空间，企业视频，云主机，开放平台">
	<link rel="shortcut icon" href="/static/staticPage/img/favicon.ico">
	<link rel="stylesheet" href="/static/staticPage/css/common.css">
	<link rel="stylesheet" href="/static/staticPage/css/style.css">
</head>
<body>
	<input id="userId" type="text" class="hide" value="${sessionScope.userSession.userId}">
	<div class="main-body">
		<div class="order">
			<div class="order-title">订单支付</div>
			<div class="order-pay">
				<div class="pay-item">账号名称：<span class="item-desc">letvcloud@letv.com</span></div>
				<div class="pay-item">
					<span>可用余额：</span><span class="item-desc">¥100</span>
					<button class="btn btn-le-red item-recharge">充值</buttom>
				</div>
				<div class="pay-item">本次需支付：<span class="text-red item-desc">¥1000</span></div>
				<div class="pay-item">
					<span>支付方式：</span>
					<button class="payoption active"><img src="/static/staticPage/img/zhifubao.png"></button>
					<!-- <button class="payoption"><img src="/static/staticPage/img/wechat.png"></button> -->
				</div>
				<div class="pay-item">
					<button class="btn btn-le-blue item-pay">确认支付</button>
				</div>
			</div>
		</div>
		<div class="order">
			<div class="order-title">
				<span>订单详情</span>
				<span class="title-rollup">
					<span class="rollup-text">收起</span>
					<span class="iconfont icon-arrow01"></span>
					<span class="clearfix"></span>
				</span>
				<div class="clearfix"></div>
			</div>
			<div class="price-table ordertable opacity">
				<table class="col-md-12">
					<thead>
						<tr>
							<th width="12.5%">订单号</th>
							<th width="37.5%">配置</th>
							<th width="12.5%">数量</th>
							<th width="12.5%">单价</th>
							<th width="12.5%">使用时长</th>
							<th width="12.5%">支付费用</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>0100002010010</td>
							<td><div style="width:50%;text-align:right;">购买服务器</div></td>
							<td>1</td>
							<td class="price">105.00元/月</td>
							<td>1个月</td>
							<td class="price">¥105</td>
						</tr>
						<tr>
							<td></td>
							<td>
								<div class="payitems">
									<div class="payitem clearfix">
										<div class="text-right">配置&nbsp;:</div><div class="payitem-desc">&nbsp;1核，2G内存，无数据盘</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">带宽&nbsp;:</div><div class="payitem-desc">&nbsp;1Mbps</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">操作系统&nbsp;:</div><div class="payitem-desc">&nbsp;CentOS 6.6 32位</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">安全组件&nbsp;:</div><div class="payitem-desc">&nbsp;安全加固组件</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">地域&nbsp;:</div><div class="payitem-desc">&nbsp;华东区-上海</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">所属网络&nbsp;:</div><div class="payitem-desc">&nbsp;基础网络</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">可用区&nbsp;:</div><div class="payitem-desc">&nbsp;上海一区</div>
									</div>
									<div class="payitem clearfix">
										<div class="text-right">作为网关&nbsp;:</div><div class="payitem-desc">&nbsp;否</div>
									</div>
								</div>
							</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<!-- <div class="main-body" style="position:absolute;z-index:200;background:rgba(0,0,0,.3)">
		
	</div> -->
</body>
<script src="/static/javascripts/jquery-1.11.3.js"></script>
<script>
	$('.title-rollup').unbind('click').click(function(){
		var _target=$('.ordertable');
		var _targetI=$('.icon-arrow01');
		var _targetTxt=$('.rollup-text');
		if(_target.hasClass('opacity')){
			_target.removeClass('opacity')
			_target.css({
				opacity: '0',
				transition: 'opacity .2s ease-in'
			});
			_targetI.css({
				transform:'rotate(0deg)',
				transition:'transform .2s ease-in'
			});
			_targetTxt.text('展开');
		}else{
			_target.css({
				opacity: '1',
				transition: 'opacity .2s ease-in'
			});
			_target.addClass('opacity');
			_targetI.css({
				transform:'rotate(180deg)',
				transition:'transform .2s ease-in'
			});
			_targetTxt.text('收起');
		}
	});
</script>
</html>