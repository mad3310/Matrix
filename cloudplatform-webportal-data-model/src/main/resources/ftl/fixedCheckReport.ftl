<html>
<head>
<style type="text/css">
table.gridtable {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}

table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}

table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
	text-align: center; 
}
p {
	margin: 20px
}
</style>
</head>
<body>
<!-- Table goes in the document BODY -->

<h3>运维，您好：</h3>
<p>固资系统对账异常结果如下,请及时解决。</p>

<p>cmdb系统中异常的数据：</p>
<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">container名称</th>
		<th width="100px">ip地址</th>
	</tr>
	${cmdbTableInfo}
</table>

<p>rds中未推送的数据：</p>
<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">container名称</th>
		<th width="100px">ip地址</th>
	</tr>
	${rdsTableInfo}
</table>

<p>gce中未推送的数据：</p>
<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">container名称</th>
		<th width="100px">ip地址</th>
	</tr>
	${gceTableInfo}
</table>

<p>slb中未推送的数据：</p>
<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">container名称</th>
		<th width="100px">ip地址</th>
	</tr>
	${slbTableInfo}
</table>
<br/>

<p>本邮件由系统发出，请勿回复。<br/>如有问题，联系系统管理员。</p>
<hr/>
<p>乐视云计算公司<br/>
PAAS云开发团队</p>
</body>
</html>