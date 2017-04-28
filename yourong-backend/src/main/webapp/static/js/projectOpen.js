var projectOpenTable;
jQuery(function($) {
    projectOpenList();
    searchProjectOpenList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var check = "<button class='btn btn-xs btn-info projectopen-check permission-" + config.permission.detail + "' data-name='"+row.outBizNo+"' data-url='"+config.urlMap.detail+"' data-id='" + row.id + "' ><i class='icon-edit bigger-130'>查看订单信息</i></button>";
    var create = "<button class='btn btn-xs btn-danger projectopen-create permission-" + config.permission.build + "' data-name='"+row.outBizNo+"' data-url='"+config.urlMap.openCreate+"' data-id='" + row.id + "' ><i class='bigger-130'>生成项目</i></button>";
    var auditing  = "<button class='btn btn-xs btn-danger projectopen-auditing permission-" + config.permission.auditing + "' data-id='" + row.id + "' data-openPlatformKey='" + row.openPlatformKey + "' ><i class='bigger-130'>审核</i></button>";
    var auditingdetail = "<button class='btn btn-xs btn-danger projectopen-auditingdetail permission-" + config.permission.auditinginfo + "' data-id='" + row.id + "' ><i class='bigger-130'>查看审核信息</i></button>";
    var remark = "<button class='btn btn-xs btn-danger projectopen-remark permission-" + config.permission.remark + "' data-id='" + row.id + "' data-remark='"+row.remark+"' ><i class='bigger-130'>添加备注</i></button>";
    var button="";
    if (status==3) {
        button=check+"   "+auditing+"    "+remark;
    }
    if (status==4) {
        button=check+"     "+remark;
    }
    if (status==5) {
        button=check+"    "+auditingdetail+"    "+create+"     "+remark;
    }
    if (status==6){
        button=check+"    "+auditingdetail+"     "+remark;
    }
    return button;
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
function projectOpenList(){
    projectOpenTable = $('#projectOpen-table').dataTable({
        //"tableTools": {//excel导出
        //    "aButtons": exportButton,
        //    "sSwfPath": config.swfUrl
        //},
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
            'mDataProp' : 'createTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data){
                    return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }
                return "";
            }
        },{
            'mDataProp' : 'openPlatformKey',
            'bSortable' : false,
            'mRender': function(data, type, row) {
				switch (data) {
					case "shandai":
						return "闪贷";
						break;
					case "jimistore":
						return "机蜜";
						break;

				}
			}
        }, {
            'mDataProp': 'outBizNo',
            'bSortable': false
        },  {
            'mDataProp' : 'totalAmount',
            'bSortable' : false
        }, {
            'mDataProp' : 'annualizedRate',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                return data+"%";
            }
        }, {
            'mDataProp' : 'borrowPeriod',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                var periodType='';
                if (row.borrowPeriodType==1){
                    periodType="天";
                }
                if (row.borrowPeriodType==2){
                    periodType="个月";
                }
                if (row.borrowPeriodType==3){
                    periodType="年";
                }
                if (row.borrowPeriodType==4){
                    periodType="周";
                }
                return data+periodType;
            }
        }, {
            'mDataProp' : 'borrowerName',
            'bSortable' : false
        }, {
            'mDataProp' : 'sku',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (!data||data.length!=10){
                    return "-";
                }
                return getPhoneModel(data.substring(2,4),data.substring(4,6));
            }
        }, {
            'mDataProp' : 'status',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data==3){
                    return "待审核";
                }else if (data==4){
                    return "已生成";
                }else if (data==5){
                    return "待生成";
                }else if (data==6){
                    return "拒绝上线";
                }
            }
        }, {
            'mDataProp': 'operation',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return getAllOperation(row);
            }
        }, {
            'mDataProp': 'remark',
            'bSortable': false
        }
        ]
    });
}

function getPhoneModel(phone,color){
    var resultphone='';
    var resultcolor='';
    switch (phone){
        case "11":resultphone='iphone SE';break;
        case "12":resultphone='iphone 6 PLUS';break;
        case "13":resultphone='iphone 6S';break;
        case "14":resultphone='iphone 7';break;
        case "15":resultphone='iphone 7 PLUS';break;
    }
    switch (color){
        case "11":resultcolor="亮银";break;
        case "12":resultcolor="玫瑰金";break;
        case "13":resultcolor="土豪金";break;
        case "14":resultcolor="黑色";break;
        case "15":resultcolor="亮黑";break;
        case "16":resultcolor="深空灰";break;
    }
    return resultphone+"/"+resultcolor;
}

//对外项目创建直投项目
function findOpenDetail(id) {
    $("#direct_project_form").xform('load', config.urlMap.findDetail + "openid=" + id,
        function(data) {
            if (data.code>0){
                bindOpenInfo(data.open);
                //附件信息的显示
                renderAttachmentDetail(data.bscAttachments);
                disableDropzone();//图片操作禁止
            }
        });
}

function searchProjectOpenList(){
    $("#query_projectOpen").on("click",function(){
    	var openPlatformKey = $("#openPlatformKey").val();
    	if(openPlatformKey == "闪贷"){
    		$("#openPlatformKey").val("shandai");
    	}else if(openPlatformKey == "机蜜"){
    		$("#openPlatformKey").val("jimistore");
    	}else if(openPlatformKey != null && openPlatformKey != ""){
    		bootbox.alert("请输入正确渠道商");
    	}
        projectOpenTable.fnDraw();
        openPlatformKey = $("#openPlatformKey").val();
        if(openPlatformKey == "shandai"){
    		$("#openPlatformKey").val("闪贷");
    	}else if(openPlatformKey == "jimistore"){
    		$("#openPlatformKey").val("机蜜");
    	}
    });
}

//查看
$("#projectOpen-table").on("click", '.projectopen-check', function() {
    var id = $(this).attr("data-id");
    var url = $(this).attr("data-url");
    var name = $(this).attr("data-name");
    window.top.setIframeBox("check"+id, url+"openid=" + id, "订单"+id);
});

//审核
$("#projectOpen-table").on("click", '.projectopen-auditing', function() {
    var id = $(this).attr("data-id");
    $('#openAuditingId').val(id);
    $('#auditing_result').val('5');
    $('#refuse_tr').hide();
    $('#shortDesc_tr').show();
    $('#borrowDetail_tr').show();
    $('#refuse').val('');
    $('#shortDesc').val('');
    $('#borrowDetail').val('');
    var openPlatformKey = $(this).attr("data-openPlatformKey");
/*    if(openPlatformKey == "shandai"){
    	bootbox.alert("闪贷项目当前不能审核！");
    	return;
    }*/
    
    $('#modal-openAuditing').modal('show');
});

$("#auditing_result").on("change",function(){
    var changevalue=$(this).val();
    if (changevalue==5){
        $('#refuse_tr').hide();
        $('#shortDesc_tr').show();
        $('#borrowDetail_tr').show();
    }
    if (changevalue==6){
        $('#refuse_tr').show();
        $('#shortDesc_tr').hide();
        $('#borrowDetail_tr').hide();
    }
})

//查看审核
$("#projectOpen-table").on("click", '.projectopen-auditingdetail', function() {
    var id = $(this).attr("data-id");
    var url = $(this).attr("data-url");
    var name = $(this).attr("data-name");
    var shortDesc = $(this).attr("data-shortDesc");
    var borrowDetail = $(this).attr("data-borrowDetail");
    $.post(
        config.urlMap.auditinginfo,{openid:id},function(data){
            if(data.success){
                if (data.result){
                    $('#checkrefuse').val(data.result.refuseCause);
                    $('#checkShortDesc').val(data.result.shortDesc);
                    $('#checkBorrowDetail').val(data.result.borrowDetail);
                    if (data.result.status==5){
                        $('#checkrefuse_tr').hide();
                        $('#checkShortDesc_tr').show();
                        $('#checkBorrowDetail_tr').show();
                    }else if (data.result.status==6){
                        $('#checkrefuse_tr').show();
                        $('#checkShortDesc_tr').hide();
                        $('#checkBorrowDetail_tr').hide();
                    }
                }else {
                    $('#checkShortDesc').val("");
                    $('#checkBorrowDetail').val("");
                }
                $('#modal-openAuditingDetail').modal('show');
            }else{
                $('#checkShortDesc').val("");
                $('#checkBorrowDetail').val("");
                $('#modal-openAuditingDetail').modal('show');
            }
        }
    );
});

//添加审核，发送后台
$("#btn_add_auditing").on("click",function(){
    $(this).addClass("disabled");
    var that = $(this);
    var auditing_result=$('#auditing_result').val();
    var id=$('#openAuditingId').val();
    var shortDesc= $("#shortDesc").val();
    var borrowDetail= $("#borrowDetail").val();
    var refuse= $("#refuse").val();
    if (!id){
        bootbox.alert("参数错误");
        that.removeClass("disabled");
        return;
    }
    if (auditing_result==5){
        if (!shortDesc){
            bootbox.alert("请填写项目介绍");
            that.removeClass("disabled");
            return;
        }
        if (!borrowDetail){
            bootbox.alert("请填写借款用途");
            that.removeClass("disabled");
            return;
        }
    }
    if (auditing_result==6){
        if (!refuse){
            bootbox.alert("请填写拒绝原因");
            that.removeClass("disabled");
            return;
        }
    }

    $.post(
        config.urlMap.auditing,{openid:id,status:auditing_result,refuse:refuse,shortDesc:shortDesc,borrowDetail:borrowDetail},function(data){
            that.removeClass("disabled");
            if(data.success){
                $('#modal-openAuditing').modal('hide');
                projectOpenTable.fnDraw();
            }else{
                bootbox.alert("审核失败！");
            }
        }
    );

});

//备注
$("#projectOpen-table").on("click", '.projectopen-remark', function() {
    var id = $(this).attr("data-id");
    $('#openRemarkId').val(id);

    $.post(
        config.urlMap.remark,{openid:id},function(data){
            if(data.success){
                if (data.result){
                    $('#remark').val(data.result.remark);
                }else {
                    $('#remark').val("");
                }
                $('#modal-openRemarks').modal('show');
            }else{
                $('#remark').val("");
                $('#modal-openRemarks').modal('show');
            }
        }
    );
});

//添加备注，发送后台
$("#btn_add_remarks").on("click",function(){
    $(this).addClass("disabled");
    var that = $(this);
    var id=$('#openRemarkId').val();
    var remark= $("#remark").val();
    if (!id){
        bootbox.alert("参数错误");
        that.removeClass("disabled");
        return;
    }
    $.post(
        config.urlMap.addremark,{openid:id,remark:remark},function(data){
            that.removeClass("disabled");
            if(data.success){
                $('#modal-openRemarks').modal('hide');
                projectOpenTable.fnDraw();
            }else{
                bootbox.alert("备注添加失败！");
            }
        }
    );
});

//生成项目
$("#projectOpen-table").on("click", '.projectopen-create', function() {
    var id = $(this).attr("data-id");
    var url = $(this).attr("data-url");
    var name = $(this).attr("data-name");
    window.top.setIframeBox("create"+id, url+"openid=" + id, "商家编号"+name);
});

function renderAttachmentDetail(bscAttachments) {
    if (bscAttachments != "" && bscAttachments != null) {
        var dropID = "";
        var customDropzone;
        $.each(bscAttachments, function(n, v) {
            var module = v.module;
            if (v.module=="direct_project_borrower"||v.module=="direct_project_collateral"||
                v.module=="direct_project_contract"||v.module=="direct_project_legal"||
                v.module=="direct_project_credit"||v.module=="direct_project_base"||v.module=="thumbnail"){
                if (dropID != module) {
                    customDropzone = Dropzone.forElement("#dropzone_" + module);
                    dropID = module;
                }
                addImageToDropzone(customDropzone, v);
                customColorBox(module);
            }
        });
    }
}

function bindOpenInfo(openinfo){
    if (openinfo){
        $('#outBizNo').text(openinfo.outBizNo);
        $('#borrowerName').text(openinfo.borrowerName);
        $('#totalAmount').text(openinfo.totalAmount+"元");
        var periodType='';
        if (openinfo.borrowPeriodType==1){
            periodType="天";
        }
        if (openinfo.borrowPeriodType==2){
            periodType="个月";
        }
        if (openinfo.borrowPeriodType==3){
            periodType="年";
        }
        if (openinfo.borrowPeriodType==4){
            periodType="周";
        }
        $('#borrowPeriod').text(openinfo.borrowPeriod);
        $('#borrowPeriodType').text(periodType);
        $('#annualizedRate').text(openinfo.annualizedRate+'%');
        $('#job').text(openinfo.job);
        var income="";
        switch (openinfo.income){
            case 1:income="1000元以下";break;
            case 2:income="1000-2000元";break;
            case 3:income="2001-4000元";break;
            case 4:income="4001-6000元";break;
            case 5:income="6001-8000元";break;
            case 6:income="8001-10000元";break;
            case 7:income="10001-20000元";break;
            case 8:income="20001-40000元";break;
            case 9:income="40001-60000元";break;
            case 10:income="60000元以上";break;
        }
        $('#income').text(income);
        $('#personalInfo').text(openinfo.personalInfo);
    }
}




