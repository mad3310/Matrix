<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${ctx}/static/styles/ui-css/style.css"/>
<div class="page-content-area">
	<div class="row">
		<div class="col-xs-12">
			<div class="row">
			
			<div class="col-sm-12 infobox-container">
					<div class="col-md-12 col-xs-12" >
						<div class=" logo-label mt10">
							<div class="width-100  left padding-tb-5">
								<a class="dash-a"><h4><b>RDS资源概览</b></h4></a>
							</div>
						</div>
					</div>
					<div id="statistics" class="col-md-12 col-xs-12 mt10">
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" onclick="document.location='${ctx}/list/hcluster';">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cubes"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="db_hclusterSum">0</span>
								<div class="infobox-content">物理机集群</div>
							</div>
						</div>
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cube"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="db_hostSum">0</span>
								<div class="infobox-content">物理机节点</div>
							</div>
						</div>
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" onclick="document.location='${ctx}/list/mcluster';">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cubes"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="db_clusterSum">0</span>
								<div class="infobox-content">RDS集群</div>
							</div>
						</div>

						<div class="infobox infobox-blue" onmouseover="this.style.cursor='pointer'" onclick="document.location='${ctx}/list/db';">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-database"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="db_dbSum">0</span>
								<div class="infobox-content">数据库</div>
							</div>
						</div>

						<div class="infobox infobox-red"  onmouseover="this.style.cursor='pointer'" onclick="document.location='${ctx}/list/db';">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-database"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="db_unauditeDbSum">0</span>
								<div class="infobox-content">待审核数据库</div>
							</div>
						</div>
				</div>

		</div>
				<div class="col-sm-12 infobox-container">
					<div class="col-md-12 col-xs-12">
						<div class=" logo-label mt10">
							<div class="width-100  left padding-tb-5">
								<a class="dash-a"><h4><b>GCE资源概览</b></h4></a>
							</div>
						</div>
					</div>
					<div id="statistics" class="col-md-12 col-xs-12 mt10">
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cubes"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="gce_hclusterSum">0</span>
								<div class="infobox-content">物理机集群</div>
							</div>
						</div>
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cube"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="gce_hostSum">0</span>
								<div class="infobox-content">物理机节点</div>
							</div>
						</div>
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cubes"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="gce_clusterSum">0</span>
								<div class="infobox-content">container集群</div>
							</div>
						</div>

						<div class="infobox infobox-blue" onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-database"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="gce_gceSum">0</span>
								<div class="infobox-content">GCE服务</div>
							</div>
						</div>

						<div class="infobox infobox-red"  onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-database"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="gce_unauditeGceSum">0</span>
								<div class="infobox-content">待审核GCE</div>
							</div>
						</div>
					</div>

				</div>
				<div class="col-sm-12 infobox-container">
					<div class="col-md-12 col-xs-12">
						<div class=" logo-label mt10">
							<div class="width-100  left padding-tb-5">
								<a class="dash-a"><h4><b>SLB资源概览</b></h4></a>
							</div>
						</div>
					</div>
					<div id="statistics" class="col-md-12 col-xs-12 mt10">
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cubes"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="slb_hclusterSum">0</span>
								<div class="infobox-content">物理机集群</div>
							</div>
						</div>
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'">
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cube"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="slb_hostSum">0</span>
								<div class="infobox-content">物理机节点</div>
							</div>
						</div>
						<div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-cubes"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="slb_clusterSum">0</span>
								<div class="infobox-content">container集群</div>
							</div>
						</div>

						<div class="infobox infobox-blue" onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-database"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="slb_slbSum">0</span>
								<div class="infobox-content">SLB服务</div>
							</div>
						</div>

						<div class="infobox infobox-red"  onmouseover="this.style.cursor='pointer'" >
							<div class="infobox-icon">
								<i class="ace-icon fa fa-database"></i>
							</div>
							<div class="infobox-data">
								<span class="infobox-data-number" id="slb_unauditeSlbSum">0</span>
								<div class="infobox-content">待审核SLB</div>
							</div>
						</div>
					</div>

				</div>
                <div class="col-sm-12 infobox-container">
                    <div class="col-md-12 col-xs-12">
                        <div class=" logo-label mt10">
                            <div class="width-100  left padding-tb-5">
                                <a class="dash-a"><h4><b>OCS资源概览</b></h4></a>
                            </div>
                        </div>
                    </div>
                    <div id="statistics" class="col-md-12 col-xs-12 mt10">
                        <div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
                            <div class="infobox-icon">
                                <i class="ace-icon fa fa-cubes"></i>
                            </div>
                            <div class="infobox-data">
                                <span class="infobox-data-number" id="ocs_hclusterSum">0</span>
                                <div class="infobox-content">物理机集群</div>
                            </div>
                        </div>
                        <div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'">
                            <div class="infobox-icon">
                                <i class="ace-icon fa fa-cube"></i>
                            </div>
                            <div class="infobox-data">
                                <span class="infobox-data-number" id="ocs_hostSum">0</span>
                                <div class="infobox-content">物理机节点</div>
                            </div>
                        </div>
                        <div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
                            <div class="infobox-icon">
                                <i class="ace-icon fa fa-cubes"></i>
                            </div>
                            <div class="infobox-data">
                                <span class="infobox-data-number" id="ocs_clusterSum">0</span>
                                <div class="infobox-content">container集群</div>
                            </div>
                        </div>

                        <div class="infobox infobox-blue" onmouseover="this.style.cursor='pointer'" >
                            <div class="infobox-icon">
                                <i class="ace-icon fa fa-database"></i>
                            </div>
                            <div class="infobox-data">
                                <span class="infobox-data-number" id="ocs_ocsSum">0</span>
                                <div class="infobox-content">OCS服务</div>
                            </div>
                        </div>

                        <div class="infobox infobox-red"  onmouseover="this.style.cursor='pointer'" >
                            <div class="infobox-icon">
                                <i class="ace-icon fa fa-database"></i>
                            </div>
                            <div class="infobox-data">
                                <span class="infobox-data-number" id="ocs_unauditeOcsSum">0</span>
                                <div class="infobox-content">待审核OCS</div>
                            </div>
                        </div>
                    </div>
            </div>
            <div class="col-sm-12 infobox-container">
                <div class="col-md-12 col-xs-12">
                    <div class=" logo-label mt10">
                        <div class="width-100  left padding-tb-5">
                            <a class="dash-a"><h4><b>OSS资源概览</b></h4></a>
                        </div>
                    </div>
                </div>
                <div id="statistics" class="col-md-12 col-xs-12 mt10">
                    <div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'" >
                        <div class="infobox-icon">
                            <i class="ace-icon fa fa-cubes"></i>
                        </div>
                        <div class="infobox-data">
                            <span class="infobox-data-number" id="oss_hclusterSum">0</span>
                            <div class="infobox-content">物理机集群</div>
                        </div>
                    </div>
                    <div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'">
                        <div class="infobox-icon">
                            <i class="ace-icon fa fa-cube"></i>
                        </div>
                        <div class="infobox-data">
                            <span class="infobox-data-number" id="oss_hostSum">0</span>
                            <div class="infobox-content">物理机节点</div>
                        </div>
                    </div>
                    <div class="infobox infobox-blue"  onmouseover="this.style.cursor='pointer'">
                        <div class="infobox-icon">
                            <i class="ace-icon fa fa-cubes"></i>
                        </div>
                        <div class="infobox-data">
                            <span class="infobox-data-number" id="oss_clusterSum">0</span>
                            <div class="infobox-content">container集群</div>
                        </div>
                    </div>
                    <div class="infobox infobox-blue" onmouseover="this.style.cursor='pointer'">
                        <div class="infobox-icon">
                            <i class="ace-icon fa fa-database"></i>
                        </div>
                        <div class="infobox-data">
                            <span class="infobox-data-number" id="oss_ossSum">0</span>
                            <div class="infobox-content">OSS服务</div>
                        </div>
                    </div>

                    <div class="infobox infobox-red"  onmouseover="this.style.cursor='pointer'" >
                        <div class="infobox-icon">
                            <i class="ace-icon fa fa-database"></i>
                        </div>
                        <div class="infobox-data">
                            <span class="infobox-data-number" id="oss_unauditeOssSum">0</span>
                            <div class="infobox-content">待审核OSS</div>
                        </div>
                    </div>
                </div>
            </div>
	</div>
</div>
<script src="${ctx}/static/scripts/pagejs/dashboard.js"></script>
