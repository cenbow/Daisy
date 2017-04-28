var jobTable;
var jobForm;
//加载umeditor编辑器
var postEditor = {};
jQuery(function($) {
    //表单验证初始化
    jobForm = $("#jobForm").Validform({
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
            postEditor =UE.getEditor('jobContent', {
                initialFrameWidth: 765,
                initialFrameHeight: 350,
                initialContent:content
            });
        });
    }

    // 添加岗位
    $("#submit_job").click(function(){
        $("textarea[name='jobContent']").val(postEditor.getContent());
        if (jobForm.check(false)){
            $('#jobForm').xform('post', config.urlMap.savejob, function(data) {
                if (!data.success) {
                    bootbox.alert("保存岗位失败!",function(){
                        $('#submit_job').removeAttr("disabled");
                    });
                } else {
                    bootbox.alert("保存岗位成功!", function() {
                        $('#add-job-modal-table').modal('hide');
                        $('#submit_job').removeAttr("disabled");
                        jobForm.resetForm();
                        jobTable.fnDraw();
                    });
                }
            });
        }
    });

    // 添加岗位
    $("#add_job").click(function(){
        if (typeof postEditor.id === 'undefined') {
            loadUeditor();
        } else {
            postEditor.execCommand('cleardoc');
        };
        $('#id').val('');
        jobForm.resetForm();
        $('#jobTitle').text('添加岗位');
        setCategory('categoryId','-1');
        $('#add-job-modal-table').modal('show');
    });

    //编辑
    $("#job-table").on("click", '.job-edit', function() {
        var id = $(this).attr("data-id");
        $('#jobTitle').text('编辑岗位');
        setCategory('categoryId','-1');
        $.post(
            config.urlMap.editjob,{jobid:id},function(data){
                if(data.success) {
                    jobForm.resetForm();
                    $('#id').val(data.result.id);
                    $('#jobName').val(data.result.jobName);
                    $('#categoryId').val(data.result.categoryId);
                    $('#hiringCount').val(data.result.hiringCount);
                    if (typeof postEditor.id === 'undefined') {
                        loadUeditor(data.result.jobContent);
                    }else{
                        postEditor.setContent(data.result.jobContent);
                    }
                    $('#add-job-modal-table').modal('show');
                }
            }
        );
    });

    jobList();
    searchjobList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var edit = "<button class='btn btn-xs btn-info job-edit' data-id='" + row.id + "' ><i class='bigger-130'>编辑</i></button>"; //编辑
    var jobdelete = "<button class='btn btn-xs btn-danger job-delete' data-id='" + row.id + "' ><i class='bigger-130'>删除</i></button>"; //上架
    var jobrelease = "<button class='btn btn-xs btn-warning job-release' data-id='" + row.id + "' ><i class='bigger-130'>发布</i></button>"; //发布
    var jobupdate = "<button class='btn btn-xs btn-success job-update' data-id='" + row.id + "' ><i class='bigger-130'>申请更新</i></button>"; //申请更新
    if (status==1){
        return edit + jobdelete + jobrelease;
    }else {
        return jobupdate;
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
function jobList(){
    jobTable = $('#job-table').dataTable({
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
        'sAjaxSource': config.urlMap.jobajax,
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
            'mDataProp': 'jobName',
            'bSortable': false
        }, {
            'mDataProp' : 'hiringCount',
            'bSortable' : false
        }, {
            'mDataProp' : 'jobContent',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
                return "<button class='btn btn-xs btn-info job-info' data-id='" + row.id + "' ><i class='bigger-130'>查看详情</i></button>";
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

function searchjobList(){
    $("#query_job").on("click",function(){
        jobTable.fnDraw();
    });
}

//加载分类
function setCategory(id,i){
    $('#'+id+'').html('');
    if (i>0){
        $('#'+id+'').append('<option value="">请选择</option>');
    }
    $.post(
        config.urlMap.queryCompanyJobCategory,function(data){
            $.each(data.result,function(i,v){
                $('#'+id+'').append('<option value="'+v.id+'">'+ v.categoryName +'</option>');
            });
        }
    );
}



// 排序
$("#job_sort").click(function(){
    $("input[name='sort']").each(function(e){
        $(this).attr("readonly",false);
    })
    $(this).hide();
    $('#submit_job_sort').show();
});

// 保存排序
$("#submit_job_sort").click(function(){
    var sorts="[";
    $("input[name='sort']").each(function(){
        sorts+="{\"id\":\""+$(this).data("id")+"\",\"sort\":\""+$(this).val()+"\"}";
        $(this).attr("readonly",true);
    })
    sorts+="]";

    $.ajax({
        url:config.urlMap.savejobsort,
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
    $('#job_sort').show();
});

//一键发布
$("#job_all_release").click(function(){
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
                        jobTable.fnDraw();
                    }else {
                        bootbox.alert("一键发布异常");
                    }
                }
            );
        }
    })
});

//删除
$("#job-table").on("click", '.job-delete', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要删除吗?",function(result){
        if (result){
            $.post(
                config.urlMap.deletejob,{jobid:id},function(data){
                    if(data.success) {
                        bootbox.alert("删除成功");
                        jobTable.fnDraw();
                    }else {
                        bootbox.alert("删除失败");
                    }
                }
            );
        }
    })
});

//发布
$("#job-table").on("click", '.job-release', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要发布吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'2'},function(data){
                    if(data.success) {
                        bootbox.alert("发布成功");
                        jobTable.fnDraw();
                    }else {
                        bootbox.alert("发布失败");
                    }
                }
            );
        }
    })
});

//申请更新
$("#job-table").on("click", '.job-update', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要申请更新吗?",function(result){
        if (result){
            $.post(
                config.urlMap.updatestatus,{id:id,status:'1'},function(data){
                    if(data.success) {
                        jobTable.fnDraw();
                    }else {
                        bootbox.alert("申请异常");
                    }
                }
            );
        }
    })
});

//查看详情
$("#job-table").on("click", '.job-info', function() {
    var id = $(this).attr("data-id");
    $('#jobinfo').html('');
    $.post(
        config.urlMap.editjob,{jobid:id},function(data){
            if(data.success) {
                $('#jobinfo').html(data.result.jobContent);
                $('#add-job-info-table').modal('show');
            }
        }
    );
});




