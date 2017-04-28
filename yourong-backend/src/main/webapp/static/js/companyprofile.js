var profileTable;
var profileForm;
//加载umeditor编辑器
var postEditor = {};
jQuery(function($) {
    //表单验证初始化
    profileForm = $("#profileForm").Validform({
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
            postEditor =UE.getEditor('eventContent', {
                initialFrameWidth: 765,
                initialFrameHeight: 350,
                initialContent:content
            });
        });
    }

    // 添加简介
    $("#add_profile").click(function(){
        $('#id').val('');
        if (typeof postEditor.id === 'undefined') {
            loadUeditor();
        } else {
            postEditor.execCommand('cleardoc');
        };
        profileForm.resetForm();
        $('#profileTitle').text('添加公司简介');
        $('#add-profile-modal-table').modal('show');
    });

    //编辑
    $("#profile-table").on("click", '.profile-edit', function() {
        var id = $(this).attr("data-id");
        $('#profileTitle').text('编辑公司简介');
        $.post(
            config.urlMap.edit,{id:id},function(data){
                if(data.success) {
                    profileForm.resetForm();
                    $('#id').val(data.result.id);
                    $('#eventYear').val(data.result.eventYear);
                    $('#eventMonth').val(data.result.eventMonth);
                    $('#eventDate').val(data.result.eventDate);
                    $('#eventContent').val(data.result.eventContent);
                    $('#link').val(data.result.link);
                    //var content = utf8to16(base64decode(data.result.eventContent));
                    if (typeof postEditor.id === 'undefined') {
                        loadUeditor(data.result.eventContent);
                    }else{
                        postEditor.setContent(data.result.eventContent);
                    }
                    $('#add-profile-modal-table').modal('show');
                }
            }
        );
    });

    // 添加简介
    $("#submit_profile").click(function(){
        $("textarea[name='eventContent']").val(postEditor.getContent());
        if (profileForm.check(false)){
            $('#profileForm').xform('post', config.urlMap.save, function(data) {
                if (!data.success) {
                    bootbox.alert("保存简介失败!",function(){
                        $('#submit_profile').removeAttr("disabled");
                    });
                } else {
                    bootbox.alert("保存简介成功!", function() {
                        $('#add-profile-modal-table').modal('hide');
                        $('#submit_profile').removeAttr("disabled");
                        profileForm.resetForm();
                        profileTable.fnDraw();
                    });
                }
            });
        }
    });
    profileList();
    searchprofileList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var edit = "<button class='btn btn-xs btn-info profile-edit' data-id='" + row.id + "' ><i class='bigger-130'>编辑</i></button>"; //编辑
    var del = "<button class='btn btn-xs btn-danger profile-delete' data-id='" + row.id + "' ><i class='bigger-130'>删除</i></button>"; //删除
    var release = "<button class='btn btn-xs btn-warning profile-release' data-id='" + row.id + "' ><i class='bigger-130'>发布</i></button>"; //发布
    var update = "<button class='btn btn-xs btn-success profile-update' data-id='" + row.id + "' ><i class='bigger-130'>申请更新</i></button>"; //申请更新
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
    "sFileName": "岗位分类.xls"
};
if(config.permission.goodsExcel){
    exportButton.push(excelButton);
}
function profileList(){
    profileTable = $('#profile-table').dataTable({
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
            'mDataProp': 'eventYear',
            'bSortable': false
        }, {
            'mDataProp' : 'eventMonth',
            'bSortable' : false
        }, {
            'mDataProp' : 'eventDate',
            'bSortable' : false
        }, {
            'mDataProp' : 'eventContent',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
                var html;
                if (data){
                    var p=data.indexOf("</p>");
                    var doc = data.substring(0,p).replace("<p>",'');
                    if (doc){
                        html = doc.substring(0,10);
                    }
                }
                if (html){
                    return ""+html+"<button class='btn btn-xs btn-info profile-info' data-id='" + row.id + "' ><i class='bigger-130'>查看详情</i></button>";
                }
                return "<button class='btn btn-xs btn-info profile-info' data-id='" + row.id + "' ><i class='bigger-130'>查看详情</i></button>";
            }
        }, {
            'mDataProp' : 'link',
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

function searchprofileList(){
    $("#query_profile").on("click",function(){
        profileTable.fnDraw();
    });
}

// 排序
$("#profile_sort").click(function(){
    $("input[name='sort']").each(function(e){
        $(this).attr("readonly",false);
    })
    $(this).hide();
    $('#submit_profile_sort').show();
});

// 保存排序
$("#submit_profile_sort").click(function(){
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
                profileTable.fnDraw();
                bootbox.alert("保存成功");
            }else{
                bootbox.alert("保存失败");
            }
        }
    });
    $(this).hide();
    $('#profile_sort').show();
});

//一键发布
$("#profile_all_release").click(function(){
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
                        profileTable.fnDraw();
                    }else {
                        bootbox.alert("一键发布异常");
                    }
                }
            );
        }
    })
});

//删除
$("#profile-table").on("click", '.profile-delete', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要删除吗?",function(result){
        if (result){
            $.post(
                config.urlMap.delete,{id:id},function(data){
                    if(data.success) {
                        bootbox.alert("删除成功");
                        profileTable.fnDraw();
                    }else {
                        bootbox.alert("删除失败");
                    }
                }
            );
        }
    })
});

//发布
$("#profile-table").on("click", '.profile-release', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要发布吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'2'},function(data){
                    if(data.success) {
                        bootbox.alert("发布成功");
                        profileTable.fnDraw();
                    }else {
                        bootbox.alert("发布失败");
                    }
                }
            );
        }
    })
});

//申请更新
$("#profile-table").on("click", '.profile-update', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要申请更新吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'1'},function(data){
                    if(data.success) {
                        profileTable.fnDraw();
                    }else {
                        bootbox.alert("申请异常");
                    }
                }
            );
        }
    })
});

//查看详情
$("#profile-table").on("click", '.profile-info', function() {
    var id = $(this).attr("data-id");
    $('#profileinfo').html('');
    $.post(
        config.urlMap.edit,{id:id},function(data){
            if(data.success) {
                $('#profileinfo').html(data.result.eventContent);
                $('#add-profile-info-table').modal('show');
            }
        }
    );
});




