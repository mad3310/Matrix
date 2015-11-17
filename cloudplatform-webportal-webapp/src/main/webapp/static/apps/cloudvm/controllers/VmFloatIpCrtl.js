/**
 * Created by jiangfei on 2015/8/12.
 */
define(['controllers/app.controller'], function (controllerModule) {
  controllerModule.controller('VmFloatIpCrtl', ['$scope','$interval','Config','$modal','HttpService','WidgetService','CurrentContext',
    function ($scope,$interval, Config,$modal,HttpService,WidgetService,CurrentContext) {
      $scope.searchIpName='';
      $scope.floatIpList = [];
      $scope.currentPage = 1;
      $scope.totalItems = 0;
      $scope.pageSize = 10;
      $scope.onPageChange = function () {
        refreshFloatIpList();
      };
      $scope.doSearch = function () {
        refreshFloatIpList();
      };
      $scope.isAllFipChecked=function(){
        var unCheckedFips=$scope.floatIpList.filter(function(fIp){
          return fIp.checked===false || fIp.checked===undefined;
        });
        return unCheckedFips.length==0;
      };
      $scope.checkAllFip=function(){
        if($scope.isAllFipChecked()){
          $scope.floatIpList.forEach(function(fIp){
            fIp.checked=false;
          });
        }
        else{
          $scope.floatIpList.forEach(function(fIp){
            fIp.checked=true;
          });
        }
      };
      $scope.checkFip = function (fIp) {
        fIp.checked = fIp.checked === true ? false : true;
      };
      $scope.openIPcreateModal=function(size){
        var modalInstance = $modal.open({
          animation: $scope.animationsEnabled,
          templateUrl: '/static/apps/cloudvm/templates/vm-floatip-create-modal.html',
          controller: 'VmIPcreateModalCtrl',
          size: size,
          backdrop: 'static',
          keyboard: false,
          resolve: {
            region: function () {
              return CurrentContext.regionId;
            }
          }
        });
        modalInstance.result.then(function (resultData) {
          if(resultData &&resultData.result===1){
            refreshFloatIpList();
          }
        }, function () {
        });
      };
      $scope.bindVm=function(size){
        var checkedIps=getCheckedIp();
        if(checkedIps.length !==1){
          WidgetService.notifyWarning('请选中一个公网Ip');
          return;
        }else{
          if(checkedIps[0].bindResource){
            WidgetService.notifyError('已绑定云主机');
            return;
          }else{
            //是否有可用的云主机
            HttpService.doGet('/ecs/vm/unbindedfloatingip/list',{'region':CurrentContext.regionId}).success(function(data){
              if(data.result==1){//success
                if(data.data.length>0){
                  var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'VmIPbindVmMOdalTpl',
                    controller: 'VmIpBindVmModalCtrl',
                    size: size,
                    backdrop: 'static',
                    keyboard: false,
                    resolve: {
                      region: function () {
                        return CurrentContext.regionId;
                      },
                      bindfloatIp:function(){
                        return checkedIps[0];
                      },
                      avalibleVm:function(){
                        return data.data;
                      }
                    }
                  });
                  modalInstance.result.then(function (resultData) {
                    if(resultData &&resultData.result===1){
                      refreshFloatIpList();
                    }
                  }, function () {
                  });
                }else{//has no yunzhuji
                  WidgetService.notifyWarning('无可用云主机');
                }
              }else{
                WidgetService.notifyError(data.msgs[0]||'请求出错');
              }
            });
          }
        } 
      }
      $scope.editIp=function(size){
        var checkedIps=getCheckedIp();
        if(checkedIps.length !==1){
          WidgetService.notifyWarning('请选中一个公网Ip');
          return;
        }else{
          var modalInstance = $modal.open({
            animation: $scope.animationsEnabled,
            templateUrl: 'VmIpEditMOdalTpl',
            controller: 'VmIpEditModalCtrl',
            size: size,
            backdrop: 'static',
            keyboard: false,
            resolve: {
              region: function () {
                return CurrentContext.regionId;
              },
              floatIp:function(){
                return checkedIps[0];
              }
            }
          });
          modalInstance.result.then(function (resultData) {
            if(resultData &&resultData.result===1){
              refreshFloatIpList();
            }
          }, function () {
          });
        }
      }
      $scope.unbindVm=function(size){
        var checkedIps=getCheckedIp();
        if(checkedIps.length !==1){
          WidgetService.notifyWarning('请选中一个公网Ip');
          return;
        }else{
          if(checkedIps[0].bindResource){//解绑
            var data={
              region:checkedIps[0].region,
              vmId:checkedIps[0].bindResource.id,
              floatingIpId: checkedIps[0].id
            };
            var modalInstance = WidgetService.openConfirmModal('解绑云主机','确定要（'+checkedIps[0].name+'）解绑云主机吗？');
            modalInstance.result.then(function (resultData) {
              if(!resultData) return resultData;
              WidgetService.notifyInfo('解绑云主机执行中...');
              checkedIps[0].status='AVAILABLE';
              HttpService.doPost(Config.urls.floatIp_unbindVm,data).success(function (data, status, headers, config) {
                if(data.result===1){
                  checkedIps[0].status='AVAILABLE';
                  modalInstance.close(data);
                  WidgetService.notifySuccess('解绑云主机成功');
                  refreshFloatIpList();
                }
                else{
                  WidgetService.notifyError(data.msgs[0]||'解绑云主机失败');
                }
              });
            }, function () {
            });
          }else{//未绑定，不需解绑
            WidgetService.notifyWarning('未绑定云主机');
            return;
          }
        } 
      }
      $scope.deleteIp=function(size){
        var checkedIps=getCheckedIp();
        if(checkedIps.length !==1){
          WidgetService.notifyWarning('请选中一个公网Ip');
          return;
        }
        var data={
          region:checkedIps[0].region,
          floatingIpId: checkedIps[0].id
        };
        var modalInstance = WidgetService.openConfirmModal('删除公网IP','确定要删除公网IP（'+checkedIps[0].name+'）吗？');
        modalInstance.result.then(function (resultData) {
          if(!resultData) return resultData;
          WidgetService.notifyInfo('公网Ip删除执行中...');
          checkedIps[0].status='DELETEING';
          HttpService.doPost(Config.urls.floatIp_delete,data).success(function (data, status, headers, config) {
            if(data.result===1){
              checkedIps[0].status='DELETED';
              modalInstance.close(data);
              WidgetService.notifySuccess('删除公网Ip成功');
              refreshFloatIpList();
            }
            else{
              WidgetService.notifyError(data.msgs[0]||'删除公网Ip失败');
            }
          });
        }, function () {
        });
      };
      var refreshFloatIpList = function () {
          var queryParams = {
            region:CurrentContext.regionId,
            name:$scope.searchIpName,
            currentPage: $scope.currentPage,
            recordsPerPage: $scope.pageSize
          };
        $scope.isListLoading=true;
          HttpService.doGet(Config.urls.floatIP_list, queryParams).success(function (data, status, headers, config) {
            $scope.isListLoading=false;
            $scope.floatIpList = data.data.data;
            $scope.totalItems = data.data.totalRecords;
          });
      };
      getCheckedIp=function(){
        return $scope.floatIpList.filter(function(item){
          return item.checked===true;
        });
      };
      refreshFloatIpList();
    }
  ]);

  controllerModule.controller('VmIpBindVmModalCtrl', function (Config, HttpService,WidgetService,ModelService,Utility,CurrentContext, $scope, $modalInstance,$timeout,$window, region,bindfloatIp,avalibleVm) {
    $scope.floatIpName=bindfloatIp.name;
    $scope.floatIp=bindfloatIp.ipAddress;
    $scope.vmListSelectorData=avalibleVm.map(function(vm){
      return new ModelService.SelectModel(vm.name,vm.id);
    });
    $scope.selectedVm=$scope.vmListSelectorData[0];
    $scope.closeModal=function(){
      $modalInstance.dismiss('cancel');
    };
    $scope.IpbingVm=function(){
      var data={
        'region':region,
        vmId:$scope.selectedVm.value,
        floatingIpId:bindfloatIp.id
      };
      HttpService.doPost(Config.urls.floatIp_bindVm,data).success(function(data){
        if(data.result==0){//error
          WidgetService.notifyError(data.msgs[0]||'绑定云主机出错');
        }else{
          $modalInstance.close(data);
          WidgetService.notifySuccess('绑定云主机成功');
        }

      });
    }
  });

  controllerModule.controller('VmIpEditModalCtrl', function (Config, HttpService,WidgetService,Utility,CurrentContext, $scope, $modalInstance,$timeout,$window, region,floatIp) {
    Utility.getRzSliderHack($scope)();
    $scope.ipName=floatIp.name;
    $scope.closeModal=function(){
      $modalInstance.dismiss('cancel');
    };
    $scope.editIP=function () {
      if (!$scope.vm_ip_edit_form.$valid) return;
      var data = {
        'region':region,
        floatingIpId:floatIp.id,
        name: $scope.ipName
      };
      $scope.isFormSubmiting=true;
      HttpService.doPost(Config.urls.floatIP_edit,data).success(function (data, status, headers, config) {
        if(data.result===1){
          $modalInstance.close(data);
          WidgetService.notifySuccess('修改公网IP成功');
          // $window.location.href = '/payment/'+data.data;
        }
        else{
          $scope.isFormSubmiting=flase;
          WidgetService.notifyError(data.msgs[0]||'修改公网IP失败');
        }
      });
    };
  });
});
