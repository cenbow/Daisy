//用户报名登记
(function(){
    var $meetingapply=$('#j-meeting-apply'),
        isLogined=$meetingapply.data("logined"),
        istruename=$meetingapply.data("truename"),
        apply=$meetingapply.data("apply"),
        $memberReportform=$('#j-memberReport-form'),
        $mReportclose=$('#j-mReport-close');
    var that = $(this);
    $meetingapply.on("click", function(){
        var dialogData = {};
            if(isLogined){
            	if(istruename=="0"){
            		 dialogData = {
                             type: 'info', content: '亲爱的会员，实名认证后才能报名哟~', okValue: '请先实名认证', callback: function () {
                                 window.location.href = '/member/sinapay/';
                             }
                         };
                         $.xDialog(dialogData);
            	}else{
            		if(apply){
                        $.xDialog({content:'您已报名，请静候佳音！'});
                        return false;
                    }else{
                    	$memberReportform.show();
                    }
            	}
            }else{
                dialogData = {
                    type: 'info', content: '亲爱的用户，请先去登录', okValue: '登录', callback: function () {
                        window.location.href = '/security/login/';
                    }
                };
                $.xDialog(dialogData);
            }
    });
    $mReportclose.on('click',function(){
        $memberReportform.hide();
    });
	$("#j-checkmobile").attr("readonly", "readonly").addClass("z-disabled");
	$("#j-checkname").attr("readonly", "readonly").addClass("z-disabled");
    $("#j-submit-qreport").on("click", function () {
       // var province = $("#reportregion-area-province").val();
        var province = $("#reportregionarea_data_2").find("button").text();
        $("#province").val(province);
        var data = $("#memberReport_form").serializeArray();
        var href = environment.globalPath + "/memberReport/save";
        if ($("#j-checkname").val() === "") {
            $.xDialog({
                content: "请输入姓名"
            });
            return false;
        }
        if ($("#j-checkmobile").val() === "") {
            $.xDialog({
                content: "请输入手机号"
            });
            return false;
        }
        if (province == "选择省") {
            $.xDialog({
                content: "亲~您的省份还没有填写哟~"
            });
            return false;
        }
        if ($("#travelMode").val() === "") {
            $.xDialog({
                content: "亲~您的交通方式还没有填写哟~"
            });
            return false;
        }
        $.xPost({
            url: href,
            data: data,
            callback: function (data) {
                if (data.success) {
                	// dialogData = {type: 'info', content: '报名成功：感谢您报名参加“共融未来  行以致远——有融网一周年庆典暨投资人答谢会”'};
                     //$.xDialog(dialogData);
                     
                     $.xDialog({
                    	content: '感谢您报名参加“共融未来  行以致远——有融网一周年庆典暨投资人答谢会”!',
     					type:"success", 
     					callback:function(){
     						 $memberReportform.hide();
     						window.location.reload();
     					} //确认按钮回调
     				});
                     
                } else {
                	var dataCodeEum0 = data.resultCodeEum[0],
                    code=Number(dataCodeEum0.code);
                	dialogData = {type: 'info', content: dataCodeEum0.msg};
                         $.xDialog(dialogData);
                }

            }
        });
    });
}());
//地图

//创建和初始化地图函数：
function initMap(){
    createMap();//创建地图
    setMapEvent();//设置地图事件
    addMapControl();//向地图添加控件
    addMapOverlay();//向地图添加覆盖物
}
function createMap(){
    map = new BMap.Map("map");
    map.centerAndZoom(new BMap.Point(120.165991,30.30012),19);
}
function setMapEvent(){
    map.enableScrollWheelZoom();
    map.enableKeyboard();
    map.enableDragging();
    map.enableDoubleClickZoom()
}
function addClickHandler(target,window){
    target.addEventListener("click",function(){
        target.openInfoWindow(window);
    });
}
function addMapOverlay(){
    var markers = [
        {content:"杭州市拱墅区上塘路333号",title:"杭州海外海皇冠假日酒店",imageOffset: {width:-46,height:-21},position:{lat:30.299843,lng:120.166233}}
    ];
    for(var index = 0; index < markers.length; index++ ){
        var point = new BMap.Point(markers[index].position.lng,markers[index].position.lat);
        var marker = new BMap.Marker(point,{icon:new BMap.Icon("http://api.map.baidu.com/lbsapi/createmap/images/icon.png",new BMap.Size(20,25),{
            imageOffset: new BMap.Size(markers[index].imageOffset.width,markers[index].imageOffset.height)
        })});
        var label = new BMap.Label(markers[index].title,{offset: new BMap.Size(25,5)});
        var opts = {
            width: 200,
            title: markers[index].title,
            enableMessage: false
        };
        var infoWindow = new BMap.InfoWindow(markers[index].content,opts);
        marker.setLabel(label);
        addClickHandler(marker,infoWindow);
        map.addOverlay(marker);
    };
}
//向地图添加控件
function addMapControl(){
    var scaleControl = new BMap.ScaleControl({anchor:BMAP_ANCHOR_BOTTOM_LEFT});
    scaleControl.setUnit(BMAP_UNIT_IMPERIAL);
    map.addControl(scaleControl);
    var navControl = new BMap.NavigationControl({anchor:BMAP_ANCHOR_TOP_LEFT,type:BMAP_NAVIGATION_CONTROL_LARGE});
    map.addControl(navControl);
    var overviewControl = new BMap.OverviewMapControl({anchor:BMAP_ANCHOR_BOTTOM_RIGHT,isOpen:true});
    map.addControl(overviewControl);
}
var map;
initMap();


