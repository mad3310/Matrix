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

<h3>您好：</h3>
<p>我们后台检测到您的数据库集群 ${dbName}中有违反mcluster规则的元素，请尽快处理。否则可能引起数据不一致，乃至集群宕机。
mcluster反例表如下: </p>

<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">表名</th>
		<th width="100px">engine</th>
		<th width="100px">nopk</th>
	</tr>
	${dbTableInfo}
</table>

<p style="color:red;">处理方法：请将表修改为innodb引擎，没有主键（NOPK）的表请加入默认主键（或业务无关主键），并建议使用自增值。
  注意：MyISAM如果已经存在数据，请将数据导出后重建表，不要直接以alter命令更改表引擎，直接修改可能集群间不同步。</p>

<p>本邮件由系统发出，请勿回复。<br/>如有问题，联系系统管理员。</p>
<hr/>
<p>乐视云计算公司<br/>
PAAS云开发团队</p>
</body>
</html>