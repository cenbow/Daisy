var categoryTable;
var categoryForm;
jQuery(function($) {
    //表单验证初始化
    categoryForm = $("#categoryForm").Validform({
        tiptype: 4,
        btnReset: ".btnReset",
        ajaxPost: false
    });
    categoryList();
    searchcategoryList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var edit = "<button class='btn btn-xs btn-info category-edit' data-id='" + row.id + "' ><i class='bigger-130'>编辑</i></button>"; //编辑
    var del = "<button class='btn btn-xs btn-danger category-delete' data-id='" + row.id + "' ><i class='bigger-130'>删除</i></button>"; //删除
    var release = "<button class='btn btn-xs btn-warning category-release' data-id='" + row.id + "' ><i class='bigger-130'>发布</i></button>"; //发布
    var update = "<button class='btn btn-xs btn-success category-update' data-id='" + row.id + "' ><i class='bigger-130'>申请更新</i></button>"; //申请更新
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
function categoryList(){
    categoryTable = $('#category-table').dataTable({
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
        'sAjaxSource': config.urlMap.categoryajax,
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
            'mDataProp': 'categoryName',
            'bSortable': false
        }, {
            'mDataProp' : 'hiringCount',
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

function searchcategoryList(){
    $("#query_category").on("click",function(){
        categoryTable.fnDraw();
    });
}

// 添加分类
$("#submit_category").click(function(){
    if (categoryForm.check(false)){
        $('#categoryForm').xform('post', config.urlMap.savecategory, function(data) {
            if (!data.success) {
                bootbox.alert("保存分类失败!",function(){
                    $('#submit_goods').removeAttr("disabled");
                });
            } else {
                bootbox.alert("保存分类成功!", function() {
                    $('#add-categoryForm-modal-table').modal('hide');
                    $('#submit_category').removeAttr("disabled");
                    categoryForm.resetForm();
                    categoryTable.fnDraw();
                });
            }
        });
    }
});

// 添加分类
$("#add_job_category").click(function(){
    $('#id').val('');
    categoryForm.resetForm();
    $('#categoryTitle').text('添加岗位类别');
    $('#add-categoryForm-modal-table').modal('show');
});

// 排序
$("#category_sort").click(function(){
    $("input[name='sort']").each(function(e){
        $(this).attr("readonly",false);
    })
    $(this).hide();
    $('#submit_category_sort').show();
});

// 保存排序
$("#submit_category_sort").click(function(){
    var sorts="[";
    $("input[name='sort']").each(function(){
        sorts+="{\"id\":\""+$(this).data("id")+"\",\"sort\":\""+$(this).val()+"\"}";
        $(this).attr("readonly",true);
    })
    sorts+="]";

    $.ajax({
        url:config.urlMap.savecategorysort,
        data:{
            'sortstr':sorts
        },
        type:'post',
        dataType:'json',
        success:function(data){
            if(data.success){
                categoryTable.fnDraw();
                bootbox.alert("保存成功");
            }else{
                bootbox.alert("保存失败");
            }
        }
    });
    $(this).hide();
    $('#category_sort').show();
});

//一键发布
$("#category_all_release").click(function(){
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
                        categoryTable.fnDraw();
                    }else {
                        bootbox.alert("一键发布异常");
                    }
                }
            );
        }
    })
});

//编辑
$("#category-table").on("click", '.category-edit', function() {
    var id = $(this).attr("data-id");
    $('#categoryTitle').text('编辑岗位类别');
    $.post(
        config.urlMap.editcategory,{categoryid:id},function(data){
            if(data.success) {
                $('#id').val(data.result.id);
                $('#categoryName').val(data.result.categoryName);
                $('#add-categoryForm-modal-table').modal('show');
            }
        }
    );
});

//删除
$("#category-table").on("click", '.category-delete', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要删除吗?",function(result){
        if (result){
            $.post(
                config.urlMap.deletecategory,{categoryid:id},function(data){
                    if(data.success) {
                        bootbox.alert("删除成功");
                        categoryTable.fnDraw();
                    }else {
                        bootbox.alert("删除失败");
                    }
                }
            );
        }
    })
});

//发布
$("#category-table").on("click", '.category-release', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要发布吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'2'},function(data){
                    if(data.success) {
                        bootbox.alert("发布成功");
                        categoryTable.fnDraw();
                    }else {
                        bootbox.alert("发布失败");
                    }
                }
            );
        }
    })
});

//申请更新
$("#category-table").on("click", '.category-update', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要申请更新吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'1'},function(data){
                    if(data.success) {
                        categoryTable.fnDraw();
                    }else {
                        bootbox.alert("申请异常");
                    }
                }
            );
        }
    })
});




