jQuery(function($) {

    //表单验证初始化
    var repayForm = $("#repay_interest_info_form").Validform({
        tiptype: 4,
        btnReset: ".btnReset",
        ajaxPost: false
    });

    $("#thirdMemberId").change(
        function() {
            var memberId = $(this).find("option:selected").val();
            getThirdAccountMoney(memberId);
        });
    //获取操作按钮
    function getAllOperation(row) {
        var allStatus=row.allStatus,status=row.status;
        var record = "<button class='btn btn-xs btn-info showInverstsDetail permission-" + config.permission.showDirectProjectInverst + "' data-borrowerId='" + row.borrowerId  + "'data-projectIds='"+row.projectIds+"' data-enddate='" + formatDate(row.endDate,'yyyy-mm-dd')  + "'>查看明细</button>";
        return record;
    }
    $("#repayInterest-table").on("click", '.showProjectInversts', function() {
        var id = $(this).attr("data-projectId");
        window.top.setIframeBox("show-template-"+id,config.urlMap.queryInversts+id, "项目投资还本付息详情");
    });
    /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = {
        "sExtends": "xls",
        "sButtonText": "导出Excel",
        "mColumns": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13,14],
        "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            sValue = nTr.childNodes[iColumn].innerText;
            //if (iColumn === 3) {
            //    if (sValue != "") {
            //        return "=\"" + sValue.substring(0,sValue.length-4) + "\"";
            //    }
            //}else if (iColumn === 6) {
            //    if (sValue != "") {
            //        return "=\"" + sValue + "\"";
            //    }
            //}
            return sValue;
        },
        "sFileName": "借款人还本付息列表.xls"
    };
    if(config.permission.repayExcel){
        exportButton.push(excelButton);
    }
    var interestTable = $('#repayInterest-table').dataTable({
        "tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'order':[[1,"desc"]],
        'bAutoWidth' : false,
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajaxBorrowRepayment,
        'aoColumns': [{
            'mDataProp': 'borrowerId',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + ">";
            }
        }, {
            'mDataProp' : 'borrowerId',
            'bSortable' : false
        }, {
            'mDataProp' : 'borrowerName',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
            	var borrowerPlatformKey="";
            	if(row.borrowerPlatformKey!=null){
            		borrowerPlatformKey ="<em class='div-borrowerPlatformKey' style='display:none'>("+row.borrowerPlatformKey+")</em>";
            	}
                return row.borrowerName + borrowerPlatformKey;
            }
        }, {
            'mDataProp': 'borrowerMobile',
            'bSortable': false,
            'mRender'   : function(data, type, row) {
                return ''+data+'';
            }
        }, {
            'mDataProp' : 'endDate',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
                return formatDate(data,'yyyy-mm-dd');
            }
        }, {
            'mDataProp' : 'accountBalance',
            'bSortable' : true,
            'mRender':function(data, type, row){
                if(row.borrowerName!=null  && row.borrowerMobile!=null){
                    return "<button class='btn btn-xs btn-success' style='float: right' onclick='showMemberBalance("+row.borrowerMobile+")' >显示余额</a>";
                }else{
                    return "";
                }
            }
        }, {
            'mDataProp': 'waitRepayNum',
            'bSortable': false
        }, {
            'mDataProp' : 'endRepayNum',
            'bSortable' : true
        }, {
            'mDataProp': 'payabletotal',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
            }
        }, {
            'mDataProp': 'payablePrincipal',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
            }
        },{
            'mDataProp': 'realPayableInterest',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
            }
        },{
            'mDataProp': 'realPayableExtraInterest',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
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

    findcollect();

    //查询
    $('#query_interest').on('click', function() {
        if (!$('#endDate').val()){
            alert('请填写还款日期');
            $('#endDate').focus();
            return false;
        }
        interestTable.fnSettings()._iDisplayStart=0;
        interestTable.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
        findcollect();
        flag = false;
        return false;
    });
    
	var flag =false;

    //渠道
    $('#query_borrowerPlatformKey').on('click', function() {
        
    	if(!flag){
        	$(".div-borrowerPlatformKey").css("display","inline");
        	flag = true;
    	}else{
    		$(".div-borrowerPlatformKey").css("display","none");
        	flag = false;
    	}
    });

    //重置
    $('#reset_interest').on('click', function() {
        $('#interest_form')[0].reset();
    });


    $("#cancel_interest").on('click', function () {
        $('#repay_interest_info_form')[0].reset();
    });

});

$("#repayInterest-table").on("click", '.showInverstsDetail', function() {
    var borrowerid = $(this).data("borrowerid");
    var projectids = $(this).data("projectids");
    var enddate = $(this).data("enddate");
    //var id = param.id;
    //var controlRemarks =  param.remark;
    //$('#controlRemarksId').val(id);
    //if(controlRemarks == 'null') {
    //    $('#newControlRemarks').val('');
    //} else {
    //    $('#newControlRemarks').val(controlRemarks);
    //}

    var interestDetailTable = $('#repayInterest-detail-table').dataTable({
        //"tableTools": {//excel导出
        //    "aButtons": exportButton,
        //    "sSwfPath": config.swfUrl
        //},
        'order':[[1,"desc"]],
        'bDestroy':true,
        'bAutoWidth' : false,
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            aoData.push({
                "name": "borrowid",
                "value": borrowerid
            });
            aoData.push({
                "name": "enddate",
                "value": enddate
            });
            aoData.push({
                "name": "projectids",
                "value": projectids
            });
            getAllSearchValue(aoData);
            //aoData.push({"borrowid":"11111"})
        },
        'sAjaxSource': config.urlMap.ajaxBorrowRepaymentDetail,
        'aoColumns': [{
            'mDataProp': 'projectName',
            'bSortable': false
        }, {
            'mDataProp' : 'periods',
            'bSortable' : false
        }, {
            'mDataProp' : 'endDate',
            'bSortable': false,
            'mRender'   : function(data, type, row) {
                return formatDate(data,'yyyy-mm-dd');
            }
        }, {
            'mDataProp': 'payablePrincipal',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
            }
        }, {
            'mDataProp': 'realPayableInterest',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
            }
        }, {
            'mDataProp': 'realPayableExtraInterest',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if(data == null) {
                    return "";
                }
                return formatCurrency(data);
            }
        }, {
            'mDataProp': 'status',
            'bSortable': false,
            'mRender':function(data,type,row){
                if(row.status!=null && row.status==0){
                    return "已还款";
                }else {
                    return "<font color=\"red\">待还款</font>";
                }
            }
        }
        ]
    });

    $('#modal-table_interest_detail').modal('show');
});

//重置时时间查询条件隐藏
$(".resetButton").on("click",function(){
    $("#end_date_search").css("display","none");
    $("#end_date_start_search").css("display","none");
    $("#due_date_search").css("display","none");
});
function autoEndDate(){
    var sDate = $('#search_endDateStart').val(),//开始时间
        eDate = $("#search_endDateEnd").val();//结束时间
    //判断结束时间是否大于开始时间
    var num = compareTwoDate(sDate,eDate);
    if (num <= 0) {
        bootbox.alert("结束时间必须大于开始时间！",function(){
            $("#search_endDateEnd").val("");
        });
        return false;
    }
}

function findcollect(){
    $.ajax({
        url:config.urlMap.ajaxCollect,
        data:{
            'endDate':$('#endDate').val()
        },
        type:'post',
        dataType:'json',
        success:function(data){
            if (data.data){
                $('#waitRepayNum').html(data.data.waitRepayNum);
                $('#payablePrincipal').html(formatCurrency(data.data.payablePrincipal));
                $('#payableInterest').html(formatCurrency(data.data.payableInterest));
                $('#extraInterest').html(formatCurrency(data.data.extraInterest));
                $('#payabletotal').html(formatCurrency(data.data.payabletotal));
            }else {
                $('#waitRepayNum').html(0);
                $('#payablePrincipal').html(0);
                $('#payableInterest').html(0);
                $('#extraInterest').html(0);
                $('#payabletotal').html(0);
            }
        }
    });
}

function compareTwoDate(start,end){
    if(start==""){
        return 1;
    }else if(end==""){
        return -1;
    }else if(start=="" && end ==""){
        return 0;
    }else{
        var dayNum = new Date(end) - new Date(start);
        if(dayNum>0){
            return 1;
        }else if(dayNum<0){
            return -1;
        }else{
            return 0;
        }
    }
}

//数字转金额
function formatCurrency(num) {
    num = num.toString().replace(/\$|\,/g,'');
    if(isNaN(num))
        num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num*100+0.50000000001);
    cents = num%100;
    num = Math.floor(num/100).toString();
    if(cents<10)
        cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
        num = num.substring(0,num.length-(4*i+3))+','+
            num.substring(num.length-(4*i+3));
    return "￥"+(((sign)?'':'-') + num + '.' + cents);
}

//显示余额
function showMemberBalance(mobile){
    $.ajax({
        url:config.urlMap.queryBalance,
        data:{
            'memberId':mobile
        },
        type:"post",
        dataType:'json',
        success:function(data) {
            if(data.success){
                bootbox.alert("余额:" + data.module.balance + ",可用余额:"
                    + data.module.availableBalance);
            }else{
                bootbox.alert("查询余额失败！");
            }
        }
    });
}