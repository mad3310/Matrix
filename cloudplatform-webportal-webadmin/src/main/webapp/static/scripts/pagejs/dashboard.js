$(function () {
	getOverview();
	browserVersion(); //浏览器检测初始化
});

function browserInfo(){			
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
};

function browserVersion(){
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
};

function getOverview(){
	$.ajax({
		cache:false,
		type : "get",
		url : "/dashboard/statistics",
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			if(error(data)) return;
			var view = data.data;
			$('#db_hclusterSum').html(view.db_hclusterSum);
			$('#db_hostSum').html(view.db_hostSum);
			$('#db_clusterSum').html(view.db_clusterSum);
			$('#db_dbSum').html(view.db_dbSum);
			$('#db_unauditeDbSum').html(view.db_unauditeDbSum);

			$('#gce_hclusterSum').html(view.gce_hclusterSum);
			$('#gce_hostSum').html(view.gce_hostSum);
			$('#gce_clusterSum').html(view.gce_clusterSum);
			$('#gce_gceSum').html(view.gce_gceSum);
			$('#gce_unauditeGceSum').html(view.gce_unauditeGceSum);
			
			$('#es_clusterSum').html(view.es_clusterSum);
			$('#es_esSum').html(view.es_esSum);
			$('#es_hclusterSum').html(view.es_hclusterSum);
			$('#es_hostSum').html(view.es_hostSum);
			$('#es_unauditeEsSum').html(view.es_unauditeEsSum);

            $('#slb_hclusterSum').html(view.slb_hclusterSum);
            $('#slb_hostSum').html(view.slb_hostSum);
            $('#slb_clusterSum').html(view.slb_clusterSum);
            $('#slb_slbSum').html(view.slb_slbSum);
            $('#slb_unauditeSlbSum').html(view.slb_unauditeSlbSum);

            $('#ocs_hclusterSum').html(view.ocs_hclusterSum);
            $('#ocs_hostSum').html(view.ocs_hostSum);
            $('#ocs_clusterSum').html(view.ocs_clusterSum);
            $('#ocs_ocsSum').html(view.ocs_ocsSum);
            $('#ocs_unauditeOcsSum').html(view.ocs_unauditeOcsSum);

            $('#oss_hclusterSum').html(view.oss_hclusterSum);
            $('#oss_hostSum').html(view.oss_hostSum);
            $('#oss_clusterSum').html(view.oss_clusterSum);
            $('#oss_ossSum').html(view.oss_ossSum);
            $('#oss_unauditeOssSum').html(view.oss_unauditeOssSum);
		},
});
}


