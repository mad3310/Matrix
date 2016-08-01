/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require){
	var $ = require('jquery');
    var common = require('../../../common');
    var cn = new common();

/*初始化工具提示*/
    cn.Tooltip('#serviceName');

/*手风琴收放效果箭头变化*/
    cn.Collapse();

    /*加载数据*/
    var dataHandler = require('./dataHandler');
    var gceInfoHandler = new dataHandler();

    cn.GetData("/ecgce/"+$("#gceId").val(),gceInfoHandler.GceInfoHandler);
    cn.GetData("/ecgce/ext/"+$("#gceId").val(),gceInfoHandler.GceExtHandler);
});
