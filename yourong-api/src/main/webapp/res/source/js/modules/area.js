//地区加载
define(['zepto', 'base'], function (require, exports, module) {

    var base=require('base');
    //FIXME 待重构，逻辑存在问题，AJAX请求过于频繁。
    if(typeof(regionArray)!=="undefined"){
        for(var i=0;i<regionArray.length;i++){
            loadAreaByParentId(regionArray[i].array.shift(),regionArray[i].callBack);
        }
    }

    $(".date-select #dateMonthList").on("click","li",function(){
        var dateSelect = $(this).closest(".date-select").next();
        dateDay($(this).attr("value"));
        dateSelect.find("button").html("请选择");
        dateSelect.find(".date").val("");
    });

    $(".date-select #dateYearList").on("click","li",function(){
        var dateSelect = $(this).closest(".date-select").nextAll();
        dateMonth($(this).attr("value"));
        dateSelect.find("button").html("请选择");
        dateSelect.find(".date").val("");
    });

    function loadAreaByParentId(code, areaCallback){
        //加载数据，返回数组。让回调函数处理
        $.ajax({
            type:'GET',
            url:environment.globalPath+"/area/getAreaList",
            data:{code:code},
            dataType:'json',
            success:areaCallback
        })
    }





    //选择地区
    function selectRegion(type,areaId,areaCallback){
        //var areaText = $('#'+areaId+'area_data_'+type+" .areaText").html();
        var areaValue = $('#'+areaId+'area_data_'+type+" .areaValue").val();
        clearArea(type,areaId);
        if(areaValue != ""){
            loadAreaByParentId(areaValue,areaCallback);
        }else{
        }
        getChooseAreaData(areaId);
    }

    /**
     * 获得选中的区域值
     */
    function getChooseAreaData(areaId){
        var area = "";
        $("#"+areaId+" .u-selector").each(function(){
            var city = $(this).find("button").text();
            if(city.indexOf("选择")==-1){
                area += city;
            }
        });
        $("#"+areaId+"-address").text(area);
    }

    $('.j-area-selector').on('change', function () {
        var _this=$(this),
            value=_this.prop('value'),
            areaName=_this.data('areaname'),
            selectName='';


        _this.find('option').each(function(){
            var val=$(this).prop('value');
            if(value===val){
                selectName=$(this).text();
            }
        });

        if(areaName!=='area-city'){
            _this.next('.j-selected-ipt').val(selectName).attr('name','bankProvince');
            loadAreaByParentId(value,regionArray[0].callBack);
        }else{
            _this.next('.j-selected-ipt').val(selectName).attr('name','bankCity');
        }
        _this.next('.j-selected-ipt').trigger('change');
    });

    window.loadAreaByParentIdCallback=function (data,areaId,areaCallback){
        var loadRegionId=-1;  //要加载的下个地区id
        var selectedIndex=-1; //要设置为已选的option的index
        var dataList = data.resultList;
        if(data.resultList.length > 0){
            var parentRegionId = dataList[0].parentId;
            var type = dataList[0].type;
            var areaDataId = '#'+areaId+'area_data_'+type;
            var areaUiListId = '#'+areaId+'area_select_list_'+type;
            resetData(type,areaId);
            var regionArray = eval(areaId+"loadRegionArray");
            if(regionArray.length != 0){
                loadRegionId = regionArray.shift();
            }
            if(dataList.length > 0){
                var name=getAreaTitle(type);
                $(areaUiListId).html("<option value=''>"+name+"</option>");
            }
            for(var i=0;i<dataList.length;i++){
                $(areaUiListId).append("<option value="+dataList[i].code+">"+dataList[i].name+"</option>");
                if(loadRegionId == dataList[i].code){
                    selectedIndex = i+1;
                    $(areaDataId).find("button").html(dataList[i].name);
                    $(areaDataId+" .areaValue").val(dataList[i].code);
                }
            }



            if(selectedIndex != -1){
                //加载下一个地区
                selectRegion(type,areaId,areaCallback);
            }
        }
    };


    /**
     * 根据类型清空值
     * @param type
     */
    function clearArea(type,areaId){
        switch(type){
            case "2":
                resetData(3,areaId);
                resetData(4,areaId);
                break;
            case "3":
                resetData(4,areaId);
                break;
            case "4":
                break;
        }
    }

    /**
     * 重设值
     * @param type
     */
    function resetData(type,areaId){
        var id = '#'+areaId+'area_data_'+type;
        $(id).find(".itemData li").remove();
        $(id).find(".areaValue").val("");
        $(id).find("button").html(getAreaTitle(type));
    }

    /*区域默认显示值*/
    function getAreaTitle(type){
        var name= "请选择";
        switch(type){
            case 2:
                name="选择省";
                break;
            case 3:
                name="选择市";
                break;
            case 4:
                name="选择区";
                break;
        }
        return name;
    }

    function datePlugin(){
        var date = new Date(environment.serverDate);
        $("#dateYearList").html("");
        var year = date.getFullYear();
        for(var i=1900; i<=year; i++){
            $("#dateYearList").append("<li value="+i+">"+i+"</li>");
        }
        dateMonth();
    }


    function dateMonth(){
        $("#dateMonthList").html("");
        for(var i=1; i<=12; i++){
            var v = appendZero(i);
            $("#dateMonthList").append("<li value="+v+">"+v+"</li>");
        }
        $("#dateDayList").html("");
        for(var j=1; j<=31; j++){
            var u = appendZero(j);
            $("#dateDayList").append("<li value="+u+">"+u+"</li>");
        }
    }

    function dateDay(m){
        var year = parseInt($("#dateYear").val());//转为整形
        var day = 31;
        var month = parseInt(m);
        console.log(month);
        switch(parseInt(m)){//做一下月份的判断，为日期复制
            case 1,3,5,7,8,10,12:
                day = 31;
                break;
            case 2:
                day = 28;
                break;
            case 4,6,9,11:
                day = 30;
                break;
        }
        if(day == 28){
            if ((year%4==0 && year%100!=0) || (year%400==0)){
                day = 29;
            }
        }
        $("#dateDayList").html("");
        for(var i=1; i<=day; i++){
            var v = appendZero(i);
            $("#dateDayList").append("<li value="+v+">"+v+"</li>");
        }
    }


    function appendZero(num){
        if(num < 10){
            return "0"+num;
        }
        return num;
    }

    function checkDateSelect(){
        var y = $("#dateYear").val();
        var m = $("#dateMonth").val()
        var d = $("#dateDay").val();
        if(y==""){
            $("#dateErrorMsg").html("请选择年份");
            return false;;
        }
        if(m==""){
            $("#dateErrorMsg").html("请选择月份");
            return false;;
        }
        if(d==""){
            $("#dateErrorMsg").html("请选择天");
            return false;;
        }
        var date = y+"年"+appendZero(m)+"月"+appendZero(d)+"日";
        $("#birthday").val(date);
        $("#birthdayText").text(date);
        return true;
    }
});