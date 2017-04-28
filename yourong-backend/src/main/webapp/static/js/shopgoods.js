var goodsTable;
var goodsForm
jQuery(function($) {
    //表单验证初始化
     goodsForm = $("#goodsForm").Validform({
        tiptype: 4,
        btnReset: ".btnReset",
        ajaxPost: false
    });
    goodsList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    if (status==1) { //下架
    	//编辑
        var edit = "<button class='btn btn-xs btn-info goods-edit permission-" + config.permission.edit + "' data-id='" + row.id + "' ><i class='icon-edit bigger-130'>编辑</i></button>"; 
        //上架
        var upper = "<button class='btn btn-xs btn-danger goods-upper permission-" + config.permission.upper + "' data-id='" + row.id + "' ><i class='bigger-130'>上架</i></button>"; 
        var deleted = "<button class='btn btn-xs btn-danger goods-deleted permission-" + config.permission.deleted + "' data-id='" + row.id + "' ><i class='bigger-130'>删除</i></button>"; 
        return upper+edit+deleted;
    }else if (status==2){
    	//下架
        var lower = "<button class='btn btn-xs btn-warning goods-lower permission-" + config.permission.lower + "' data-id='" + row.id + "' ><i class='bigger-130'>下架</i></button>"; 
        //查看
        var checked = "<button class='btn btn-xs btn-info goods-edit' data-id='" + row.id + "' data-status='" + status + "'><i class='bigger-130'>查看</i></button>"; 
        return checked+lower;
    }
}
/***
 * 导出按钮的权限设置
 */
var exportButton = [];
var excelButton = {
    "sExtends": "xls",
    "sButtonText": "导出Excel",
    "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13,14, 15,16,17 ],
    "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
        sValue = nTr.childNodes[iColumn].innerText;
        if (sValue != "") {
            return "\"" + sValue + "\"";
        } else {
            return sValue;
        }
    },
    "sFileName": "商品列表.xls"
};
if(config.permission.goodsExcel){
    exportButton.push(excelButton);
}
function goodsList(){
    goodsTable = $('#goods-table').dataTable({
        "tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'bAutoWidth' : false,
        'bFilter': false,
        "bLengthChange": false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [ {
            'mDataProp' : 'id',
            'bSortable' : false
        }, {
            'mDataProp' : 'sort',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
                return "<input type='text' style='width: 60px' name='sort' data-id='"+row.id+"' readonly='readonly' value=" + row.sort + ">";
            }
        }, {
            'mDataProp': 'goodsName',
            'bSortable': false
        }, {
            'mDataProp' : 'goodsType',
            'bSortable' : true,
            'mRender': function(data, type, row) {
                if (data==1){
                    return "投资专享";
                }else if (data==2){
                    return "虚拟卡券";
                }else if (data==3){
                    return "超值实物";
                }else if (data==4){
                    return "双节特惠";
                }

            }
        }, {
            'mDataProp' : 'status',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data==1){
                    return "下架";
                }else if (data==2){
                    return "上架";
                }
            }
        }, {
            'mDataProp' : 'tags',
            'bSortable' : true,
            'mRender': function(data, type, row) {
                if (data==0){
                    return "无";
                }else if (data==1){
                    return "新品";
                }else if (data==2){
                    return "特价";
                }else if (data==3){
                    return "推荐";
                }else if (data==4){
                    return "限时抢购";
                }else if (data==5){
                    return "元宵限时4折";
                }else if (data==6){
                    return "元宵限时5折";
                }else if (data==7){
                    return "限时3折";
                }
            }
        }, {
            'mDataProp' : 'price',
            'bSortable' : false
        }, {
            'mDataProp' : 'sourceId',
            'bSortable' : false
        }, {
            'mDataProp' : 'createTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data){
                    return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }
                return "";
            }
        }, {
            'mDataProp' : 'statusTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data){
                    return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }
                return "";
            }
        }, {
            'mDataProp' : 'exchange',
            'bSortable' : false
        }, {
            'mDataProp' : 'surplusInventor',
            'bSortable' : false
        }, {
            'mDataProp': 'operation',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return getAllOperation(row);
            }
        }
        ]
    });
}

// 添加商品
$("#submit_goods").click(function(){
    var thumbnail = $("#j-json-dropzone_goods").val();
    if (thumbnail == "" || thumbnail.length < 1) {
        bootbox.alert("请上传商品图片");
        return false;
    }
    if ($('#goodsType').val()==1||$('#goodsType').val()==4){
        if (!$('#sourceId').val()){
            bootbox.alert("请填写优惠券id");
            return false;
        }
    }
    if ($('#discount').val()){
        var reg=/^(?!(?:\.)?$)(?:[0-9]\d{0,2}|0)(?:\.\d{1,2})?$/;
        if (!reg.test($('#discount').val())){
            bootbox.alert("请填写正确的折扣");
            return false;
        }
    }
    if ($('#goodsType').val()==1){
        $('#levelNeed').val(0);
    }
    if (!($('#goodsType').val()==2&&$('#rechargeType').val()==1)){
        $('#rechargeAmount').val(0);
    }
    if ($('#autoInventory').is(':checked')){
        var ireg=/^\d+$/;
        if (!ireg.test($('#autoInventoryCount').val())){
            bootbox.alert("请填写正确库存补充数量");
            return false;
        }
    }
    //检查折扣信息是否填写正确
    if(checkDiscountInfo()){
    	return false;
    }
    if(checkShelvesTime()){
    	return false;
    }
    if (goodsForm.check(false)){
        $('#goodsForm').xform('post', config.urlMap.save, function(data) {
            if (!data.success) {
                bootbox.alert("保存商品失败!",function(){
                    $('#submit_goods').removeAttr("disabled");
                });
            } else {
                bootbox.alert("保存商品成功!", function() {
                    $('#add-modal-table').modal('hide');
                    $('#submit_goods').removeAttr("disabled");
                    goodsForm.resetForm();
                    goodsTable.fnDraw();
                });
            }
        });
    }
});

// 添加商品
$("#add_goods").click(function(){
    $('#id').val(0);
    $('#goodsName').val("");
    $('#goodsDes').text("");
    //$('#goodsType').find("option[value='1']").attr("selected",true);
    $('#goodsType').val(1);
    $('#source_group').show();
    $('#sourceId').attr("readonly",false);
    $('#sourceId').val("");
    //$('#tags').find("option[value='1']").attr("selected",true);
    $('#tags').val(1);
    //$('#rechargeType').find("option[value='1']").attr("selected",true);
    $('#rechargeType_group').hide();
    $('#rechargeAmount_group').hide();
    $('#levelNeed_group').hide();
    $('#rechargeType').val(1);
    $('#inventor').val("");
    $('#price').val("");
    $('#levelNeed').val("");
    $('#discount').val("");
    $('#autoInventory').attr('checked',false);
    $('#autoInventoryCount').val("");
    $('#autoInventoryCount').attr('readonly',true);
    $('#shelvesTime').val("");
    $('#offShelvesTime').val("");
    //删除多余的折扣信息input
    removeDiscountInfoInput();
    deleteDropzoneAllimage('dropzone_goods');
    checkId('del-dropzone_goods');
    $('#add-modal-table').modal('show');
    $('#submit_goods').show();//显示保存按钮
     goodsForm.resetForm();
});

// 排序
$("#goods_sort").click(function(){
    $("input[name='sort']").each(function(e){
        $(this).attr("readonly",false);
    })
    $(this).hide();
    $('#submit_sort').show();
});

// 保存排序
$("#submit_sort").click(function(){
    var sorts="[";
    $("input[name='sort']").each(function(){
        sorts+="{\"id\":\""+$(this).data("id")+"\",\"sort\":\""+$(this).val()+"\"}";
        $(this).attr("readonly",true);
    })
    sorts+="]";

    $.ajax({
        url:config.urlMap.savesort,
        data:{
            'sortstr':sorts
        },
        type:'post',
        dataType:'json',
        success:function(data){
            if(data.success){
                goodsTable.fnDraw();
                bootbox.alert("保存成功");
            }else{
                bootbox.alert("保存失败");
            }
        }
    });
    $(this).hide();
    $('#goods_sort').show();
});

//编辑
$("#goods-table").on("click", '.goods-edit', function() {
    var id = $(this).attr("data-id");
    var status = $(this).attr("data-status");
    $.post(
        config.urlMap.edit,{goodsid:id},function(data){
            if(data.success) {
                goodsForm.resetForm();
                $('#id').val(data.result.id);
                $('#goodsName').val(data.result.goodsName);
                $('#goodsDes').text(data.result.goodsDes);
                //$('#goodsType').find("option[value='"+data.result.goodsType+"']").attr("selected",true);
                $('#goodsType').val(data.result.goodsType);
                $('#source_group').hide();
                $('#rechargeType_group').hide();
                $('#rechargeAmount_group').hide();
                $('#sourceId').attr("readonly",false);
                if (data.result.goodsType==1){
                    $('#sourceId').attr("readonly",true);
                    $('#source_group').show();
                    $('#levelNeed_group').hide();
                }
                if (data.result.goodsType==2){
                    $('#rechargeType_group').show();
                    $('#levelNeed_group').show();
                }
                if (data.result.goodsType==3){
                    $('#levelNeed_group').show();
                }
                if (data.result.goodsType==4){
                    $('#sourceId').attr("readonly",true);
                    $('#source_group').show();
                    $('#levelNeed_group').show();
                }

                if (data.result.goodsType==2&&data.result.rechargeType==1){
                    $('#rechargeAmount').val(data.result.rechargeAmount);
                    $('#rechargeAmount_group').show();
                }
                //$('#rechargeType').find("option[value='"+data.result.rechargeType+"']").attr("selected",true);
                $('#rechargeType').val(data.result.rechargeType);

                $('#sourceId').val(data.result.sourceId);
                //$('#tags').find("option[value='"+data.result.tags+"']").attr("selected",true);
                if(data.result.tags==0){
                    $('#tags').val("");
                }else {
                    $('#tags').val(data.result.tags);
                }
                $('#inventor').val(data.result.inventor);
                $('#price').val(data.result.price);
                $('#levelNeed').val(data.result.levelNeed);
                $('#discount').val(data.result.discount);
                if (data.result.autoInventory==1){
                    $('#autoInventory').attr('checked',true);
                    $('#autoInventoryCount').attr('readonly',false);
                }else {
                    $('#autoInventory').attr('checked',false);
                    $('#autoInventoryCount').attr('readonly',true);
                }
                $('#autoInventoryCount').val(data.result.autoInventoryCount)
                deleteDropzoneAllimage('dropzone_goods');
                checkId('del-dropzone_goods');
                if (!!data.result.bscAttachments) {
                    eachAttachements(data.result.bscAttachments, "dropzone_goods");
                }
                //显示会员打折信息
                showdiscountInfo(data.result.discountInfo);
                if(data.result.shelvesTime != null && data.result.shelvesTime != ''){
                	$('#shelvesTime').val(formatDate(data.result.shelvesTime, "yyyy-mm-dd HH:mm:ss"));
                }
                if(data.result.offShelvesTime != null && data.result.offShelvesTime != ''){
                	$('#offShelvesTime').val(formatDate(data.result.offShelvesTime, "yyyy-mm-dd HH:mm:ss"));
                }
                if (status==2) {
                	$('#submit_goods').hide();
                }else{
                	$('#submit_goods').show();
                }
                $('#add-modal-table').modal('show');
               
            }
        }
    );
});

//取消 cancel
$('#cancel_shopgoods').on('click', function() {
	$('#add-modal-table').modal('hide');
});

//上架
$("#goods-table").on("click", '.goods-upper', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要上架吗?", function(result) {
        if (result) {
            $.post(
                config.urlMap.upper,{goodsid:id},function(data){
                    if(data.success) {
                        bootbox.alert("上架成功！");
                        goodsTable.fnDraw();
                    }
                }
            );
        }
    });
});

//下架
$("#goods-table").on("click", '.goods-lower', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要下架吗?", function(result) {
        if (result) {
            $.post(
                config.urlMap.lower,{goodsid:id},function(data){
                    if(data.success) {
                        bootbox.alert("下架成功！");
                        goodsTable.fnDraw();
                    }
                }
            );
        }
    });
});

//删除
$("#goods-table").on("click", '.goods-deleted', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要删除商品吗?", function(result) {
        if (result) {
            $.post(
                config.urlMap.deleted,{goodsid:id},function(data){
                    if(data.success) {
                        bootbox.alert("删除成功！");
                        goodsTable.fnDraw();
                    }
                }
            );
        }
    });
});

//投资专享
$("#invest").click(function(){
    $('#goodstype').val(1);
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnSettings().aoColumns[7].bVisible=true;
    $('#thsource_id').show();
    goodsTable.fnDraw();
});

//虚拟卡券
$("#dummy").click(function(){
    $('#goodstype').val(2)
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnSettings().aoColumns[7].bVisible=false;
    $('#thsource_id').hide()
    goodsTable.fnDraw();
});
//超值实物
$("#entity").click(function(){
    $('#goodstype').val(3);
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnSettings().aoColumns[7].bVisible=false;
    $('#thsource_id').hide();
    goodsTable.fnDraw();
});

//超值实物
$("#double").click(function(){
    $('#goodstype').val(4);
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnSettings().aoColumns[7].bVisible=false;
    $('#thsource_id').hide();
    goodsTable.fnDraw();
});

$('#goodsType').on("change",function(){
    $('#source_group').hide();
    $('#rechargeType_group').hide();
    if($(this).val()==1){
        $('#source_group').show();
        $('#levelNeed_group').hide();
        $('#rechargeAmount_group').hide();
    }
    if($(this).val()==2){
        $('#rechargeType_group').show();
        $('#levelNeed_group').show();
        if ($('#rechargeType').val()==1){
            $('#rechargeAmount_group').show();
        }
    }
    if($(this).val()==3){
        $('#levelNeed_group').show();
        $('#rechargeAmount_group').hide();
    }
    if($(this).val()==4){
        $('#source_group').show();
        $('#levelNeed_group').show();
        $('#rechargeAmount_group').hide();
    }
})

$('#rechargeType').on("change",function(){
    if($(this).val()==1){
        $('#rechargeAmount_group').show();
    }
    if($(this).val()==2){
        $('#rechargeAmount_group').hide();
    }
})

$('#autoInventory').on("change",function(){
    if($(this).is(':checked')){
        $('#autoInventoryCount').attr('readonly',false);
    }else {
        $('#autoInventoryCount').val('');
        $('#autoInventoryCount').attr('readonly',true);
    }
})

//一键删除
$('.delImage').on('click', function() {
    var dropzoneID =$(this).attr("id");
    var imgID="";
    if(dropzoneID.indexOf("del-")!=-1){
        imgID=dropzoneID.substring(4,dropzoneID.length-1);
    }
    bootbox.confirm("你确定要删除吗?", function(result) {
        if (result) {
            deleteDropzoneAllimage(imgID);
            checkId(dropzoneID);
        }
    });
});

function checkId(dropzoneID){
    if(dropzoneID.indexOf("dropzone_goods")>=0){
        $("#j-json-dropzone_goods").val("");
    }
}

function deleteDropzoneAllimage(imgID){
    $(".dropzoneImage").each(function(){
        var dropzoneID =$(this).attr("id");
        var customDropzone = Dropzone.forElement("#"+dropzoneID);
        var dropZoneFiles = customDropzone.files;
        if(dropzoneID.indexOf(imgID)==0){
            if((dropzoneID.indexOf("mosaic")>=0)==(imgID.indexOf("mosaic")>=0)){
                if(dropZoneFiles.length > 0){
                    for(i=0; i<dropZoneFiles.length;i++){
                        customDropzone.removeFile(dropZoneFiles[i]);
                    }
                }
            }
        }

    });
}

//edit Contract
function findContractById(id) {
    $.post(config.urlMap.findattachment +"?orderid="+ id, function(data) {
        if (!!data.attachments) {
            eachAttachements(data.attachments, "dropzone_goods");
        }
    });
}

//edit:附件
function eachAttachements(attachments, fileId) {
    var customDropzone = Dropzone.forElement("#"+fileId);
    $.each(attachments, function(n, v) {
        addImageToDropzone(customDropzone, v);
        customColorBox(v.module);
    });
}

//增加折扣信息input
$("#addDiscount").click(function(){
	var input = document.createElement('input');  
	input.setAttribute('type', 'text');
	input.setAttribute('name', 'discountInfo');
	input.style.marginLeft = '5px';
	var img = document.getElementById('addDiscount');
	document.getElementById('discountlist').insertBefore(input,img);
});

//根据打折信息动态生成input去显示
function showdiscountInfo(discountInfo){
	if (discountInfo == null || discountInfo == undefined || discountInfo == '') { 
		removeDiscountInfoInput();
		return;
	}
	$('input[name="discountInfo"]').remove();
	var discountInfoArray = discountInfo.split(',');
	$.each(discountInfoArray,function(index,value){
		var input = document.createElement('input');  
		input.setAttribute('type', 'text');
		input.setAttribute('name', 'discountInfo');
		input.style.marginLeft = '5px';
		input.value = value;
		var img = document.getElementById('addDiscount');
		document.getElementById('discountlist').insertBefore(input,img);
		
	});

}
//删除多余的折扣信息input,保留一个
function removeDiscountInfoInput(){
	var inputArray = $('input[name="discountInfo"]');
	$.each(inputArray,function(index,value){
		if(index != 0){
			value.remove();
		}
	});
}

//检查折扣信息是否填写正确，有问题就返回true
function checkDiscountInfo(){
	var result = false;
	var reginfo=/^\d{1}-\d{1}-\d{1}\.{0,1}\d{0,2}$/;
	$("input[name='discountInfo']").each(function(){
		var info = $(this).val();
		if( info == ''){
			$(this).remove()
		}else{
			if(!reginfo.test(info)){
				bootbox.alert("请填写正确折扣信息："+info);
				result = true;
			}
		}
	});
	return result;
}

//检查上下架时间
function checkShelvesTime(){
	var shelvesTime=$("#shelvesTime").val();  
	var offShelvesTime=$("#offShelvesTime").val();  
	var shelvesDate,offShelvesDate;
	var now = new Date();
	if(shelvesTime != ''){
		shelvesDate = new Date(shelvesTime.replace(/\-/g, "\/"));  
		if(shelvesDate < now){
			bootbox.alert("请填写正确选择上架时间");
			return true;
		}
		
	}
	if(offShelvesTime != ''){
		offShelvesDate = new Date(offShelvesTime.replace(/\-/g, "\/"));
		if(offShelvesDate < now){
			bootbox.alert("请填写正确选择下架时间");
			return true;
		}
	}
	//当上架、下架时间都存在时，上架时间不能大于下架时间
	if(shelvesTime != '' && offShelvesTime != ''){
		if(shelvesDate > offShelvesDate){
			bootbox.alert("上架时间不能大于下架时间");
			return true;
		}
	}
	return false;
}


