var goodsTable;
jQuery(function($) {
    goodsList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var edit = "<button class='btn btn-xs btn-info goods-edit permission-" + config.permission.loan + "' data-id='" + row.id + "' ><i class='icon-edit bigger-130'>编辑</i></button>"; //编辑
    var upper = "<button class='btn btn-xs btn-danger goods-upper permission-" + config.permission.loan + "' data-id='" + row.id + "' ><i class='bigger-130'>上架</i></button>"; //上架
    var lower = "<button class='btn btn-xs btn-warning goods-lower permission-" + config.permission.loan + "' data-id='" + row.id + "' ><i class='bigger-130'>下架</i></button>"; //下架
    if (status==1) { //下架
        return edit+upper;
    }else if (status==2){
        return lower;
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
            'bSortable' : true
        }, {
            'mDataProp' : 'status',
            'bSortable' : false
        }, {
            'mDataProp' : 'tags',
            'bSortable' : true
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
            'mDataProp' : 'inventor',
            'bSortable' : false
        }, {
            'mDataProp' : 'inventor',
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
    $('#goodsForm').xform('post', config.urlMap.save, function(data) {
        if (!data.success) {
            bootbox.alert("保存商品失败!",function(){
                $('#submit_goods').removeAttr("disabled");
            });
        } else {
            bootbox.alert("保存商品成功!", function() {
                $('#add-modal-table').modal('hide');
                $('#submit_goods').removeAttr("disabled");
                goodsTable.fnDraw();
            });
        }
    });
});

// 添加商品
$("#add_goods").click(function(){
    $('#id').val(0);
    $('#goodsName').val("");
    $('#goodsDes').text("");
    $('#goodsType').find("option[value='1']").attr("selected",true);
    $('#sourceId').val("");
    $('#tags').find("option[value='1']").attr("selected",true);
    $('#inventor').val("");
    $('#price').val("");
    $('#discount').val("");
    $('#add-modal-table').modal('show');
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
    $.post(
        config.urlMap.edit,{goodsid:id},function(data){
            if(data.success) {
                $('#id').val(data.result.id);
                $('#goodsName').val(data.result.goodsName);
                $('#goodsDes').text(data.result.goodsDes);
                $('#goodsType').find("option[value='"+data.result.goodsType+"']").attr("selected",true);
                if (data.result.goodsType==1){
                    $('#source_group').show();
                }else {
                    $('#source_group').hide();
                }
                $('#sourceId').val(data.result.sourceId);
                $('#sourceId').attr("readonly",true);
                $('#tags').find("option[value='"+data.result.tags+"']").attr("selected",true);
                $('#inventor').val(data.result.inventor);
                $('#price').val(data.result.price);
                $('#discount').val(data.result.discount);
                $('#add-modal-table').modal('show');
            }
        }
    );
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

//投资专享
$("#invest").click(function(){
    $('#goodstype').val(1);
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnDraw();
});

//虚拟卡券
$("#dummy").click(function(){
    $('#goodstype').val(2)
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnDraw();
});
//超值实物
$("#entity").click(function(){
    $('#goodstype').val(3);
    goodsTable.fnSettings()._iDisplayStart=0;
    goodsTable.fnDraw();
});

$('#goodsType').on("change",function(){
    if($(this).val()==1){
        $('#source_group').show();
    }else {
        $('#source_group').hide();
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
    if(dropzoneID.indexOf("dropzone_borrow_photo")>=0){
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




