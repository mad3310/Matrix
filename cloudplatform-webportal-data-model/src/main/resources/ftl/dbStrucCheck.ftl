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

<h3>${userName}您好：</h3>
<p>我们后台检测到您的数据库集群 ${dbName}中有违反mcluster规则的元素，请尽快处理。否则可能引起数据不一致，乃至集群宕机。
</p>

<p>查询具体反例表请咨询DBA（zhangchengqiang@le.com; gaoqiang3@le.com）。</p>

<p style="color:red;">处理方法：请将表修改为innodb引擎，没有主键（NOPK）的表请加入默认主键（或业务无关主键），并建议使用自增值。
  注意：MyISAM如果已经存在数据，请将数据导出后重建表，不要直接以alter命令更改表引擎，直接修改可能集群间不同步。</p>

<p>如果有问题，请回复此封邮件，DBA会指导您如何操作。</p>
<hr/>
<p>乐视云计算公司<br/>
PaaS团队</p>
</body>
</html>