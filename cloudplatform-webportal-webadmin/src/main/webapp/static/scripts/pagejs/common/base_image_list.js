var currentPage = 1; //第几页
var recordsPerPage = 15; //每页显示条数

$(function(){
  //初始化
  page_init();
  //chosen-select 组件兼容 2015-06-16
  if(!IsPC()){
    $('.chosen-select').removeClass('chosen-select');
  }
  $(document).on('click', 'th input:checkbox' , function(){
    var that = this;
    $(this).closest('table').find('tr > td:first-child input:checkbox')
      .each(function(){
        this.checked = that.checked;
        $(this).closest('tr').toggleClass('selected');
      });
  });
  
 $("#tby").click(function(e){
		e = e? e:window.event;
		var _target = e.target || e.srcElement;
		var aTarget = $(_target).parent()[0];
		console.log($(aTarget).attr('data-click-type'));
		if($(aTarget).attr('data-click-type') == "modify"){
			$('#modify-image-modal').modal('show');
			initModify(aTarget);
		}else if($(aTarget).attr('data-click-type') == "delete"){
			delImage(aTarget)
		}else if($(aTarget).attr('data-click-type') == "default"){
			setDefault(aTarget);
		}
	});
  
  $("#imgSearch").click(function(){
		var iw=document.body.clientWidth;
		if(iw>767){//md&&lg
		}else{
			$('.queryOption').addClass('collapsed').find('.widget-body').attr('style', 'dispaly:none;');
			$('.queryOption').find('.widget-header').find('i').attr('class', 'ace-icon fa fa-chevron-down');
			var qryStr='';
			var qryStr1=$('#seImageName').val();var qryStr2=$('#seImageType').val();var qryStr3=$('#seImageUsedTo').val();var qryStr4=$('#seImageStatus').val();var qryStr5;
			if($('#dbStatus').val()){
				qryStr5=translateStatus($('#dbStatus').val());
			}
			if(qryStr1){
				qryStr+='<span class="label label-success arrowed">'+qryStr1+'<span class="queryBadge" data-rely-id="dbName"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr2){
				qryStr+='<span class="label label-warning arrowed">'+qryStr2+'<span class="queryBadge" data-rely-id="dbMcluster"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr3){
				qryStr+='<span class="label label-purple arrowed">'+qryStr3+'<span class="queryBadge" data-rely-id="dbPhyMcluster"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr4){
				qryStr+='<span class="label label-yellow arrowed">'+qryStr4+'<span class="queryBadge" data-rely-id="dbuser"><i class="ace-icon fa fa-times-circle"></i></span></span>&nbsp;'
			}
			if(qryStr){
				$('.queryOption').find('.widget-title').html(qryStr);
				$('.queryBadge').click(function(event) {
					var id=$(this).attr('data-rely-id');
					$('#'+id).val('');
					$(this).parent().remove();
					queryByPage();
					if($('.queryBadge').length<=0){
						$('.queryOption').find('.widget-title').html('数据库查询条件');
					}
					return;
				});
			}else{
				$('.queryOption').find('.widget-title').html('数据库查询条件');
			}
		}
		queryByPage();
	});
	$("#imgClearSearch").click(function(){
		var clearList = ["seImageName","seImageType","seImageUsedTo","seImageStatus"];
		clearSearch(clearList);
	});
	
	enterKeydown($(".page-header > .input-group input"),queryByPage);
});
function queryByPage() {
	var dictionaryId=$('#seImageType').val();
	var isUsed = $('#seImageStatus').val();
	var purpose=$('#seImageUsedTo').val();
	var name=$('#seImageName').val();
  var queryCondition = {
    'currentPage':currentPage,
    'recordsPerPage':recordsPerPage,
    isUsed:isUsed?isUsed:'',
    purpose:purpose?purpose:'',
    dictionaryId:dictionaryId?dictionaryId:'',
    name:name?name:''
    // 'image': $('#seImageName')
  }
  $("#tby tr").remove();
  getLoading();
  $.ajax({
    cache:false,
    type : "get",
    url : queryUrlBuilder("/image",queryCondition),
    dataType : "json", /*这句可用可不用，没有影响*/
    success : function(data) {
      removeLoading();
      if(error(data)) return;
      var array = data.data.data;
      var tby = $("#tby");
      var totalPages = data.data.totalPages;

      for (var i = 0, len = array.length; i < len; i++) {
        var td1 = $("<td class=\"center\">"
                    +"<label class=\"position-relative\">"
                    +"<input name=\"gce_image_id\" value= \""+array[i].id+"\" type=\"checkbox\" class=\"ace\"/>"
                    +"<span class=\"lbl\"></span>"
                    +"</label>"
                    +"</td>");
        var td2 = $("<td class=\"td-name\" >"
                    +array[i].name
                    + "</td>");
        var td3 = $("<td class=\"td-type hidden-480\" dict-id='"+array[i].dictionaryId+"'>"
                    + array[i].dictionary.name
                    + "</td>");
        var td4 = $("<td class=\"td-purpose\" >"
                    + array[i].purpose
                    + "</td> ");
        var td5 = $("<td class=\"td-url hidden-480\" >"
                    + array[i].url
                    + "</td>");
        var td6 = $("<td class=\"td-tag hidden-480\" >"
                    + array[i].tag
                    + "</td>");
        var td7 = $("<td class=\"td-used\" img-used=\""+array[i].isUsed+"\">"
                    + (array[i].isUsed?'default':'')
                    + "</td>");
        var td8= $("<td class=\"td-descn hidden-480\">"+array[i].descn+"</td>");
        
        if(array[i].purpose=="default"){
        	var btnIcons=['del','modify'];
        }else{
        	var btnIcons=['del','modify','default'];
        }
		var td9=$(transStateHtml(btnIcons));	
        var tr = $("<tr></tr>");

        tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td7).append(td8).append(td9);
        tr.appendTo(tby);
      }//循环json中的数据

      /*初始化tooltip*/
      $('[data-toggle = "tooltip"]').tooltip();

      if (totalPages <= 1) {
        $("#pageControlBar").hide();
      } else {
        $("#pageControlBar").show();
        $("#totalPage_input").val(totalPages);
        $("#currentPage").html(currentPage);
        $("#totalRows").html(data.data.totalRecords);
        $("#totalPage").html(totalPages);
      }
    },
    error : function(XMLHttpRequest,textStatus, errorThrown) {
      error(XMLHttpRequest);
      return false;
    }
  });
}

function pageControl() {
  // 首页
  $("#firstPage").bind("click", function() {
    currentPage = 1;
    queryByPage();
  });

  // 上一页
  $("#prevPage").click(function() {
    if (currentPage == 1) {
      $.gritter.add({
        title: '警告',
        text: '已到达首页',
        sticky: false,
        time: '5',
        class_name: 'gritter-warning'
      });

      return false;

    } else {
      currentPage--;
      queryByPage();
    }
  });

  // 下一页
  $("#nextPage").click(function() {
    if (currentPage == $("#totalPage_input").val()) {
      $.gritter.add({
        title: '警告',
        text: '已到达末页',
        sticky: false,
        time: '5',
        class_name: 'gritter-warning'
      });

      return false;

    } else {
      currentPage++;
      queryByPage();
    }
  });

  // 末页
  $("#lastPage").bind("click", function() {
    currentPage = $("#totalPage_input").val();
    queryByPage();
  });
}

function searchAction(){
  $('#nav-search-input').bind('keypress',function(event){
    if(event.keyCode == "13")
    {
      queryByPage();
    }
  });
}

function formValidate() {
  $("#add-gce-image-form").bootstrapValidator({
    message: '无效的输入',
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
    }
  }).on('success.form.bv', function(e) {
    e.preventDefault();
    addImage();
  });
}
function updateFormValidate() {
  $("#modify-gce-image-form").bootstrapValidator({
    message: '无效的输入',
    feedbackIcons: {
      valid: 'glyphicon glyphicon-ok',
      invalid: 'glyphicon glyphicon-remove',
      validating: 'glyphicon glyphicon-refresh'
    },
    fields: {
    }
  }).on('success.form.bv', function(e) {
    e.preventDefault();
    updateImage();
  });
}
function initModify(obj){
	var tr = $(obj).closest("tr");
    var trData={
      id : tr.find("input").val(),
      name : tr.find(".td-name").html(),
      url : tr.find(".td-url").html(),
      tag : tr.find(".td-tag").html(),
      isUsed : tr.find(".td-used").attr('img-used'),
      descn : tr.find(".td-descn").html(),
      dictionaryId:tr.find(".td-type").attr('dict-id'),
      purpose:tr.find(".td-purpose").html()
    }
    console.log(tr.find(".td-used"));
    $('#modify-imageId').val(trData.id);
    $('#modify-name').val(trData.name);
     $('#modify-imageType').val(trData.dictionaryId);
     $('#modify-purpose').val(trData.purpose);
     $('#modify-descn').val(trData.descn);
     $('#modify-tag').val(trData.tag);
     $('#modify-url').val(trData.url);
     $('#modify-isUsed').val(trData.isUsed);
}
function addImage(){
  $.ajax({
    cache:false,
    type : "post",
    url : "/image",
    data: {
      name:$('#name').val(),
      dictionaryId:$('#imageType').val(),
      purpose:$('#purpose').val(),
      descn:$('#descn').val(),
      tag:$('#tag').val(),
      url:$('#url').val(),
      isUsed:$('#isUsed').val()
    },
    success : function(data) {
      location.href = "/list/baseImages";
    }
  })
}
function updateImage(status){
	getLoading();
	var isUsed ='';
	if(status == '1' ){
		isUsed='1';
	}else{
		isUsed = $('#modify-isUsed').val();
	}
  $.ajax({
    cache:false,
    type : "post",
    url : "/image/"+$('#modify-imageId').val(),
    data: {
      id : $('#modify-imageId').val(),
   	  name :$('#modify-name').val(),
     dictionaryId:$('#modify-imageType').val(),
     purpose:$('#modify-purpose').val(),
     descn : $('#modify-descn').val(),
     tag : $('#modify-tag').val(),
     url : $('#modify-url').val(),
     isUsed :isUsed
    },
    success : function(data) {
      location.href = "/list/baseImages";
    }
  })
}
function setDefault(obj){
	initModify(obj);
	function setDefaultFunction(){
		updateImage('1');
	}
	confirmframe("设置默认镜像","您正在设置（"+$(obj).closest("tr").find("td:eq(1)").html()+"）为默认镜像","您确定要执行此操作?",setDefaultFunction);
}

function delImage(obj){
  var imageId = $(obj).closest("tr").find("input").val();
  function delCmd(){
    $.ajax({
      cache:false,
      type : "delete",
      url : "/image/"+imageId,
      success : function(){
        location.href = "/list/baseImages";
      }
    })
  }
  confirmframe("删除镜像","删除"+$(obj).closest("tr").find("td:eq(1)").html()+"后可重新添加","您确定要删除?",delCmd);
}
function getImageType(){
	$.ajax({
      cache:false,
      type : "get",
      url : "/dictionary?type=0",
      success : function(data){
      	if(error(data)) return;
        var options='';
        var array = data.data.data;
        for(var i=0,len=array.length;i<len;i++){
        	options=options+"<option value='"+array[i].id+"'>"+array[i].name+"</option>";
        }
        $('#seImageType').append(options);
        $('#imageType').append(options);
        $('#modify-imageType').append(options);
      }
    })
}
function page_init(){
  $('[name = "popoverHelp"]').popover();
  getImageType();
  queryByPage();
  formValidate();
  updateFormValidate();
  pageControl();
}
