var table;
var form
jQuery(function($) {
    //表单验证初始化
    form = $("#goodsForm").Validform({
        tiptype: 4,
        btnReset: ".btnReset",
        ajaxPost: false
    });
    tableList();
    searchBorrowerCreditGradeList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var query = "<button class='btn btn-xs btn-info baidu-query permission-" + config.permission.edit + "' data-id='" + row.borrowerId + "' ><i class='icon-edit bigger-130'>百度查询</i></button>"; //编辑
    return query;
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
function tableList(){
    table = $('#borrowerCreditGrade-table').dataTable({
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
            'mDataProp' : 'borrowerId',
            'bSortable' : false
        }, {
            'mDataProp': 'borrowerTrueName',
            'bSortable': false
        }, {
            'mDataProp' : 'creditLevel',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                var selectedAA;
                var selectedA;
                var selectedB;
                var selectedC;
                var selectedD;
                switch (data){
                    case "A+":selectedAA="selected";break;
                    case "A":selectedA="selected";break;
                    case "B":selectedB="selected";break;
                    case "C":selectedC="selected";break;
                    case "D":selectedD="selected";break;
                }
                return "<select disabled='disabled'>" +
                    "<option>无</option>" +
                    "<option "+selectedAA+">A+</option>" +
                    "<option "+selectedA+">A</option>" +
                    "<option "+selectedB+">B</option>" +
                    "<option "+selectedC+">C</option>" +
                    "<option "+selectedD+">D</option></select>" +
                    "<button type=\"button\" class=\"btn btn-edit-credit btn-sm btn-primary\">修改</button>" +
                    "<button type=\"button\" style=\"display: none;\" data-borrowerId='"+row.borrowerId+"' class=\"btn btn-save-credit btn-sm btn-primary\">保存</button>";
            }
        }, {
            'mDataProp' : 'blackLevel',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data==1){
                    return "黑名单:("+row.blackReason+")";
                }else if (data==0) {
                    return "白名单"
                }
                return "";
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

function searchBorrowerCreditGradeList(){
    $("#query_borrowerCreditGrade").on("click",function(){
        table.fnDraw();
    });
}

//百度查询
$("#borrowerCreditGrade-table").on("click", '.baidu-query', function() {
    var id = $(this).attr("data-id");
    $.post(
        config.urlMap.queryBorrowerCredit,{memberid:id},function(data){
            if(data.success) {
                $('#borrowerTrueName').text(data.result.borrowerTrueName);
                $('#borrowerMobile').text(data.result.borrowerMobile);
                $('#identityNumber').text(data.result.identityNumber);
                $('#blackReason').text(data.result.blackReason);
                $('#blackQueryTime').text(data.result.blackQueryTimeStr);
                if (data.result.blackLevel==1){
                    $('#blackLevel').text('黑名单');
                }else {
                    $('#blackLevel').text('白名单');
                }
                $('#modal-creditGradeInfo').modal('show');
            }else {
                $('#modal-creditGrade-Error').modal('show');
            }
        }
    );
});

//修改综合评级
$("#borrowerCreditGrade-table").on("click", '.btn-edit-credit', function() {
    $(this).hide();
    $(this).next().show();
    $(this).parent().find("select").attr("disabled",false);
});

//保存综合评级
$("#borrowerCreditGrade-table").on("click", '.btn-save-credit', function() {
    var cl= $(this).parent().find("select").val();
    var id = $(this).attr("data-borrowerId");
    $.post(
        config.urlMap.updateCreditLevel,{borrowerId:id,creditLevel:cl},function(data){
            if(data.success) {
                bootbox.alert("保存成功！");
                return
            }
            bootbox.alert("修改异常！");
            return;
        }
    );
    $(this).hide();
    $(this).prev().show();
    $(this).parent().find("select").attr("disabled",true);
});

$("#submit_creditGradeInfo").click(function(){
    var id = $('#borrowerId').val();
    $.post(
        config.urlMap.saveCreditLevel,{borrowerId:id},function(data){
            if(data.success) {
                bootbox.alert("保存成功！");
                $('#modal-creditGradeInfo_create').modal('hide');
                $("#creditGradeInfoForm").resetForm();
                table.fnDraw();
                return
            }
            bootbox.alert(data.result);
            $('#modal-creditGradeInfo_create').modal('hide');
            return;
        }
    );
});

$("#create_borrowerCreditGrade").click(function(){
    $('#modal-creditGradeInfo_create').modal('show');
});

$("#close_creditGradeInfo").click(function(){
    $('#modal-creditGradeInfo').modal('hide');
    table.fnDraw();
});

$("#close_creditGradeInfoError").click(function(){
    $('#modal-creditGrade-Error').modal('hide');
    table.fnDraw();
});



