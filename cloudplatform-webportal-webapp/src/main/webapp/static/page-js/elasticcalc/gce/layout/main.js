/**
 * Created by yaokuo on 2014/12/12.
 */
define(function(require){
    var common = require('../../../common');
    var cn = new common();
    cn.initNavbarMenu([{
            name : "云引擎GCE",
            herf : "/list/ecgce"
        }]);
    /*加载数据*/
    var dataHandler = require('./dataHandler');
    var basicInfoHandler = new dataHandler();

    /*初始化侧边栏菜单*/
    var index = [1,0];
    cn.Sidebar(index);//index为菜单中的排序(1-12)
    /*
     * 加载gce基础信息
     */
    cn.GetData("/ecgce/"+$("#gceId").val(),basicInfoHandler.resCountHandler);
    
});
