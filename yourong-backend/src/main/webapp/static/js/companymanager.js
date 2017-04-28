var managerTable;
var managerForm
//加载umeditor编辑器
var postEditor = {};
jQuery(function($) {
    //表单验证初始化
    managerForm = $("#managerForm").Validform({
        tiptype: 4,
        btnReset: ".btnReset",
        ajaxPost: false
    });

    function loadUeditor(content) {
        var url = config.urlMap.ueditor;
        if(typeof content==='undefined'){
            content="";
        };
        $.getScript(url + 'ueditor.config.js');
        $.getScript(url + 'ueditor.all.min.js', function() {
            //实例化编辑器
            postEditor =UE.getEditor('manageContent', {
                initialFrameWidth: 765,
                initialFrameHeight: 350,
                initialContent:content
            });
        });
    }

    // 添加管理层
    $("#submit_manager").click(function(){
        $("textarea[name='manageContent']").val(postEditor.getContent());
        if (managerForm.check(false)){
            $('#managerForm').xform('post', config.urlMap.save, function(data) {
                if (!data.success) {
                    bootbox.alert("保存管理层失败!",function(){
                        $('#submit_manager').removeAttr("disabled");
                    });
                } else {
                    bootbox.alert("保存管理层成功!", function() {
                        $('#add-manager-modal-table').modal('hide');
                        $('#submit_manager').removeAttr("disabled");
                        managerForm.resetForm();
                        managerTable.fnDraw();
                    });
                }
            });
        }
    });

    // 添加管理层
    $("#add_manager").click(function(){
        $('#id').val('');
        if (typeof postEditor.id === 'undefined') {
            loadUeditor();
        } else {
            postEditor.execCommand('cleardoc');
        };
        managerForm.resetForm();
        $('#managerTitle').text('添加管理层');
        deleteDropzoneAllimage('');
        $('#add-manager-modal-table').modal('show');
    });

    //编辑
    $("#manager-table").on("click", '.manager-edit', function() {
        var id = $(this).attr("data-id");
        $('#managerTitle').text('编辑管理层');
        $.post(
            config.urlMap.edit,{id:id},function(data){
                if(data.success) {
                    managerForm.resetForm();
                    $('#id').val(data.result.id);
                    $('#manageName').val(data.result.manageName);
                    $('#manageJob').val(data.result.manageJob);
                    deleteDropzoneAllimage('');
                    eachAttachements(data.result.attachments,"dropzone_manager_href");
                    if (typeof postEditor.id === 'undefined') {
                        loadUeditor(data.result.manageContent);
                    }else{
                        postEditor.setContent(data.result.manageContent);
                    }
                    $('#add-manager-modal-table').modal('show');
                }
            }
        );
    });

    managerList();
    searchManagerList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var edit = "<button class='btn btn-xs btn-info manager-edit' data-id='" + row.id + "' ><i class='bigger-130'>编辑</i></button>"; //编辑
    var del = "<button class='btn btn-xs btn-danger manager-delete' data-id='" + row.id + "' ><i class='bigger-130'>删除</i></button>"; //删除
    var release = "<button class='btn btn-xs btn-warning manager-release' data-id='" + row.id + "' ><i class='bigger-130'>发布</i></button>"; //发布
    var update = "<button class='btn btn-xs btn-success manager-update' data-id='" + row.id + "' ><i class='bigger-130'>申请更新</i></button>"; //申请更新
    if (status==1){
        return edit + del + release;
    }else {
        return update;
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
function managerList(){
    managerTable = $('#manager-table').dataTable({
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
            'mDataProp': 'manageName',
            'bSortable': false
        }, {
            'mDataProp' : 'manageJob',
            'bSortable' : false
        }, {
            'mDataProp' : 'manageContent',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
                return "<button class='btn btn-xs btn-info manager-info' data-id='" + row.id + "' ><i class='bigger-130'>查看详情</i></button>";
            }
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

function searchManagerList(){
    $("#query_manage").on("click",function(){
        managerTable.fnDraw();
    });
}


// 排序
$("#manage_sort").click(function(){
    $("input[name='sort']").each(function(e){
        $(this).attr("readonly",false);
    })
    $(this).hide();
    $('#submit_manage_sort').show();
});

// 保存排序
$("#submit_manage_sort").click(function(){
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
                jobTable.fnDraw();
                bootbox.alert("保存成功");
            }else{
                bootbox.alert("保存失败");
            }
        }
    });
    $(this).hide();
    $('#manage_sort').show();
});

//一键发布
$("#manage_all_release").click(function(){
    bootbox.confirm("你确定要一键发布吗?",function(result){
        if (result){
            $.post(
                config.urlMap.releaseall,{},function(data){
                    if(data.success) {
                        if (data.result){
                            bootbox.alert(data.result);
                        }else {
                            bootbox.alert("一键发布成功");
                        }
                        managerTable.fnDraw();
                    }else {
                        bootbox.alert("一键发布异常");
                    }
                }
            );
        }
    })
});

//删除
$("#manager-table").on("click", '.manager-delete', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要删除吗?",function(result){
        if (result){
            $.post(
                config.urlMap.delete,{id:id},function(data){
                    if(data.success) {
                        bootbox.alert("删除成功");
                        managerTable.fnDraw();
                    }else {
                        bootbox.alert("删除失败");
                    }
                }
            );
        }
    })
});

//发布
$("#manager-table").on("click", '.manager-release', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要发布吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'2'},function(data){
                    if(data.success) {
                        bootbox.alert("发布成功");
                        managerTable.fnDraw();
                    }else {
                        bootbox.alert("发布失败");
                    }
                }
            );
        }
    })
});

//申请更新
$("#manager-table").on("click", '.manager-update', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要申请更新吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'1'},function(data){
                    if(data.success) {
                        managerTable.fnDraw();
                    }else {
                        bootbox.alert("申请异常");
                    }
                }
            );
        }
    })
});

//查看详情
$("#manager-table").on("click", '.manager-info', function() {
    var id = $(this).attr("data-id");
    $('#manageinfo').html('');
    $.post(
        config.urlMap.edit,{id:id},function(data){
            if(data.success) {
                $('#manageinfo').html(data.result.manageContent);
                $('#add-manager-info-table').modal('show');
            }
        }
    );
});

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
    if(dropzoneID.indexOf("dropzone_manager")>=0){
        $("#j-json-dropzone_manager_href").val("");
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

//edit:附件
function eachAttachements(attachments, fileId) {
    var customDropzone = Dropzone.forElement("#"+fileId);
    if (attachments){
        $.each(attachments, function(n, v) {
            addImageToDropzone(customDropzone, v);
            customColorBox(v.module);
        });
    }
}




