/*还本付息*/
jQuery(function($) {
	
	//表单验证初始化
	var repayForm = $("#repay_interest_info_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	 /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel" ,
            "sFileName": "托管还本付息管理.xls",
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
        		sValue = nTr.childNodes[iColumn].outerText;
            	if (iColumn === 5) {  
            		if (sValue != "") {
            			return "=\"" + sValue.substring(0,sValue.length-4) + "\"";
            		}
    	   		}else if (iColumn === 6) {  
            		if (sValue != "") {
            			return "=\"" + sValue + "\"";
            		}
    	   		}else if (iColumn === 7) {  
            		if (sValue != "") {
            			return "=\"" + sValue + "\"";
            		}
    	   		}
            	
            	return sValue;
            }
    };
    if(config.permission.indexExcel){
    	exportButton.push(excelButton);
    }
    
	//列表数据
	var payTable = $('#payPrincipalInterest-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
		'order':[[0,"desc"]],
		'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [
      	{
            'mDataProp': 'projectId',
            'bSortable':true
            /*'mRender':function(data, type, row){
            	return "<span class='showProjectInversts' data-value='"+data+"'><font color='#2a6496'>"+data+"</font></span>"
            }*/
        }, {
            'mDataProp': 'projectName',
            'bSortable': false,
            'mRender':function(data, type, row){
            	return "<a target='_blanck' href='"+config.webRootUrl+"/products/detail-"+row.projectId+".html'>"+data+"</a>"
            }
        },{
            'mDataProp' : 'projectType',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.project_type, data);
            }
        }, {
            'mDataProp': 'borrowerName',
            'bSortable': false,
            'mRender':function(data, type, row){
            	if(row.borrowerMember!=null && row.borrowerMember.trueName!=null){
            		return row.borrowerMember.trueName;
            	}else{
            		return "";
            	}
            }
        }, {
            'mDataProp': 'lenderMobile',
            'bSortable': false,
            'mRender':function(data, type, row){
            	if(row.lenderMember!=null && row.lenderMember.mobile!=null){
            		return row.lenderMember.mobile;
            	}else{
            		return "";
            	}
            }
        }, {
            'mDataProp': 'lenderName',
            'bSortable': false,
            'mRender':function(data, type, row){
            	if(row.lenderMember!=null && row.lenderMember.trueName!=null && row.lenderMember.mobile!=null){
            		return row.lenderMember.trueName + "<button class='btn btn-xs btn-success permission-" + config.permission.queryBalance + "' style='float: right' onclick='showMemberBalance("+row.lenderMember.mobile+")' >显示余额</a>";
            	}else{
            		return "";
            	}
            }
        }, {
            'mDataProp': 'onlineTime',
            'bSortable': false,
            'mRender':function(data,type,row){
            	return formatDate(data);
            }
        }, {
            'mDataProp': 'totalPeriods',
            'bSortable': false,
            'mRender':function(data,type,row){
            	return row.currentPeriods+"/"+row.totalPeriods;
            }
        }, {
            'mDataProp': 'endDate',
            'bSortable': true,
            'mRender':function(data){
            	return formatDate(data,"yyyy-mm-dd");
            }
        }, {
            'mDataProp': 'expireDays',
            'bSortable': false,
            'mRender':function(data,type,row){
            	if(row.expireDays!=null){
            		return data+"天";
            	}else if(row.expireHours!=null && row.status==0){
            		return "<font color='red'>"+row.expireHours+"小时</font>";
            	}else{
            		return "-";
            	}
            }
        }, {
            'mDataProp': 'payablePrincipalStr',
            'bSortable': false,
            'mRender':function(data,type,row){
            	if(row.status==0 && row.expireHours!=null){
            		return "<font color='red'>￥"+row.payablePrincipalStr+"</font>";
            	}else{
            		return "￥"+data;
            	}
            }
        }, {
            'mDataProp': 'payableInterestStr',
            'bSortable': false,
            'mRender':function(data,type,row){
            	if(row.status==0 && row.expireHours!=null){
            		return "<font color='red'>￥"+row.payableInterestStr+"</font>";
            	}else{
            		return "￥"+data;
            	}
            }
        }, {
            'mDataProp': 'userInterestStr',
            'bSortable': false,
            'mRender':function(data,type,row){
            	if(row.status==0 && row.expireHours!=null){
            		return "<font color='red'>￥"+row.userInterestStr+"</font>";
            	}else{
            		return "￥"+data;
            	}
            }
        }, {
            'mDataProp': 'extraInterestStr',
            'bSortable': false,
            'mRender':function(data,type,row){
            	if(row.status==0 && row.expireHours!=null){
            		return "<font color='red'>￥"+row.extraInterestStr+"</font>";
            	}else{
            		return "￥"+data;
            	}
            }
        }, {
            'mDataProp': 'status',
            'bSortable': false,
            'mRender':function(data,type,row){
            	return getDictLabel(config.status, data);
            }
        }, {
            'mDataProp' : 'thirdPayName',
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
	
	function getAllOperation(row) {
		var record = "<button class='btn btn-xs btn-info  showProjectInversts permission-" + config.permission.showProjectInverst + "' data-projectId='" + row.projectId  + "'>查看还本付息明细</button>"; 
		var underwriter = "<button class='btn btn-xs btn-danger select-underwriter permission-true" +
				"' data-interestId='" + row.interestId + "'" +
				"' data-projectName='" + row.projectName + "'" +
				"' data-currentPeriods='" + row.currentPeriods + "'" +
				"' data-totalPeriods='" + row.totalPeriods + "'" +
				"' data-endDate='" + row.endDate + "'" +
				"' data-payablePrincipal='" + row.payablePrincipal + "'" +
				"' data-payableInterest='" + row.userInterest + "'" +
				"' data-totalPayAmount='" + row.userTotalPayAmount + "'" +
				"' data-member='" + row.lenderMember.trueName + "'" +
				"' data-mobile='" + row.lenderMember.mobile + "'" +
						"><i class=' bigger-130'>选择垫资人</i></button>"; 
		if (row.status == 0 && row.thirdPayName == null) {
			return record + underwriter;
		} else {
			return record;
		}
	}
	
	
	$("#query_pay").on('click', function() {
		payTable.fnSettings()._iDisplayStart=0; 
		payTable.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
    });
	
	$("#payPrincipalInterest-table").on("click", '.showProjectInversts', function() {
		//var id = $(this).attr("data-value");
		var id = $(this).attr("data-projectId");
		window.top.setIframeBox("show-template-"+id,config.urlMap.queryInversts+id, "项目投资还本付息详情");
	});

	//选择垫资人  save
	$("#btn_save_underwriter").on('click', function () {
		var btnSave = this;
		if (repayForm.check(false)) {
			$("#btn_save_underwriter").attr('disabled',"true");
			$.ajax({
				url:config.urlMap.saveRepayment,
				data:$("#repay_interest_info_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						bootbox.alert("设置垫资人成功！")
						payTable.fnDraw();
						$("#btn_save_underwriter").removeAttr('disabled');
						$('#modal-table_interest_form').modal('hide');
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								payTable.fnDraw();
								$("#btn_save_underwriter").removeAttr('disabled');
								$('#modal-table_interest_form').modal('hide');
								return;
							});
						}else{
							$("#btn_save_underwriter").removeAttr('disabled');
							return bootbox.alert("设置垫资人失败！");
						}
					}
				}
			});
		}
			
	});
});
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

$("#endDateSelect").on("change",function(){
	var endDateSelect = $(this).val();
	switch(endDateSelect){
		case "0":
			$("#end_date_search").css("display","block");
			$("#end_date_start_search").css("display","none");
			$("#due_date_search").css("display","none");
			return;
		case "1":
			$("#end_date_search").css("display","none");
			$("#end_date_start_search").css("display","block");
			$("#due_date_search").css("display","none");
			return;
		case "2":
			$("#end_date_search").css("display","none");
			$("#end_date_start_search").css("display","none");
			$("#due_date_search").css("display","block");
			return;
		case "":
			$("#end_date_search").css("display","none");
			$("#end_date_start_search").css("display","none");
			$("#due_date_search").css("display","none");
			return;
		default:
	}
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

//选择垫资人对话框
$("#payPrincipalInterest-table").on("click", '.select-underwriter', function() {
	var id = $(this).attr("data-interestId");
	var projectId = $(this).attr("data-projectId");
	var payablePrincipalStr = $(this).attr("data-payablePrincipal");
	var payableInterestStr = $(this).attr("data-payableInterest");
	var projectName = $(this).attr("data-projectName");
	var totalPayAmountStr = $(this).attr("data-totalPayAmount");
	var currentPeriods = $(this).attr("data-currentPeriods");
	var totalPeriods = $(this).attr("data-totalPeriods");
	var endDate = $(this).attr("data-endDate");
	var memberName = $(this).attr("data-member");
	var mobile = $(this).attr("data-mobile");
	
	$('#form-field-projectName').val(projectName);
	$('#form-field-borrowerName').val(memberName);
	$('#form-field-mobile').val(mobile);
	
	$('#form-field-periods').val(currentPeriods+"/"+totalPeriods);
	$('#form-field-payablePrincipal').val(payablePrincipalStr);
	$('#form-field-payableInterest').val(payableInterestStr);
	console.info(payableInterestStr)
	$('#form-field-totalPayAmount').val(totalPayAmountStr);
	$('#form-field-interestId').val(id);
	if(!!endDate){
		$('#form-field-dataTime').val(formatDate(endDate,'yyyy-mm-dd'));
	}
	$('#projectId').val(projectId);
	//同步垫资人余额
	//synchronizedBalance();
	$('#modal-table_interest_form').modal('show');
});



$("#thirdMemberId").change(
	function() {
		var memberId = $(this).find("option:selected").val();
		console.info(memberId);
		getThirdAccountMoney(memberId);
});

//获取第三方垫资公司
function getThirdAccountMoney(memberId){
	$.ajax({
		url:config.urlMap.getThirdAccountMoney,
		data:{'memberId':memberId},
		dataType:"json",
		success:function(data){
			console.info(data);
			if(data!=null){
				$("#form-field-creditAmount").val(data);
			}else{
				$("#form-field-creditAmount").val(" ");
			}
		}
	});
}