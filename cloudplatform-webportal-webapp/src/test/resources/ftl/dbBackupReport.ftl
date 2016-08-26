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

<h3>DBA，您好：</h3>
<p>今日凌晨0:00至08:00，数据库集群备份结果如下,如有失败备份，请及时登录matrix-manage进行解决。</p>
<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">类别</th>
		<th width="100px">总数</th>
		<th width="100px">备份总数</th>
		<th width="100px">备份成功数</th>
		<th width="100px">备份失败数</th>
		<th width="100px">备份异常数</th>
		<th width="100px">备份中数</th>
	</tr>
	<tr>
		<th width="100px">数据库</th>
		<th width="100px">${dbCount}</th>
		<th width="100px">${dbBackupCount}</th>
		<th width="100px">${successDb}</th>
		<th width="100px">${failedDb}</th>
		<th width="100px">${abNormalDb}</th>
		<th width="100px">${buildingDb}</th>
	</tr>
	<tr>
		<th width="100px">数据库集群</th>
		<th width="100px">${clusterCount}</th>
		<th width="100px">${clusterBackupCount}</th>
		<th width="100px">${successCluster}</th>
		<th width="100px">${failedCluster}</th>
		<th width="100px">${abNormalCluster}</th>
		<th width="100px">${buildingCluster}</th>
	</tr>
</table>
<br/>

<p>本邮件由系统发出，请勿回复。<br/>如有问题，联系系统管理员。</p>
<hr/>
<p>乐视云计算公司<br/>
PAAS云开发团队</p>
</body>
</html>