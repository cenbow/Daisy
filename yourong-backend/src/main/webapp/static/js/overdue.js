var payableAmount=0;
var overdueTable;
var repayForm;
var flagForm;
var debtOverdueForm;
var overdueInfoSearchForm;
jQuery(function($) {
	overdueTableList();
	//表单验证初始化
	repayForm = $("#repay_interest_info_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	flagForm = $("#repay_flag_info_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	$("#thirdAmount").change(
			function() {
				var memberId = $(this).find("option:selected").val();
				getThirdAccountMoney(memberId);
	});
	
	debtOverdueForm = $("#debt_overdue_repay_info_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	overdueInfoSearchForm = $("#overdueInfoSearchForm").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
});	
	//获取操作按钮
	function getAllOperation(row) {
		var interestStatus = overdueStatus=row.overdueStatus,allStatus=row.allStatus,underwriteState=row.underwriteState;
		var record = "<button class='btn btn-xs btn-info  interest-record permission-" + config.permission.getPayRecord + "' data-projectId='" + row.projectId  + "'>查看本息记录</button>"; //
		var overdueRecord = "<button class='btn btn-xs btn-primary overdue-record permission-" + config.permission.getOverdueRecord + "' data-projectId='" + row.projectId  + "'>垫资还款记录</button>"; 
		var overdueMark = "<button class='btn btn-xs btn-danger   overdue-mark permission-" + config.permission.saveRepayFlagInterest + "' data-projectId='" + row.projectId  + "'>垫资还款标记</button>"; 
		if(allStatus==1){
			if(overdueStatus==1){
				return 	record+" "+overdueRecord+" "+overdueMark;
			}else{
				return 	record+" "+overdueMark;
			}
			
		}else if(allStatus==2){
			return 	record+" "+overdueRecord;
		}
	}
	
	 /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel",
            "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13,14],
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            	sValue = nTr.childNodes[iColumn].innerText;
                	 return sValue;
            },
            "sFileName": "垫资管理列表.xls"
    };
    if(config.permission.overdueExcel){
    	exportButton.push(excelButton);
    }
	function overdueTableList(){
		overdueTable = $('#overdue-table').dataTable({
			"tableTools": {//excel导出
	            "aButtons": exportButton,
	            "sSwfPath": config.swfUrl
	        },
	        'bAutoWidth' : false,
			'bFilter' : false,
	        'bProcessing' : true,
	        'bSort' : true,
	        'aaSorting':[[1,"desc"]],
	        'bServerSide' : true,
			'fnServerParams': function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource': config.urlMap.ajaxOverdue,
			'aoColumns': [{
					'mDataProp': 'projectId',
					'bSortable': false,
					'mRender': function(data, type, row) {
						return "<input type='checkbox' value=" + row.projectId + ">";
					}
				}, {
		            'mDataProp' : 'projectId',
		            'bSortable' : false
		        }, {
		            'mDataProp' : 'projectName',
		            'bSortable' : false
		        }, {
					'mDataProp': 'securityType',
					'bSortable': false,
					'mRender': function(data, type, row) {
						return getDictLabel(config.securityType, data);
					}
				}, {
		            'mDataProp' : 'totalAmount',
		            'bSortable' : false
		        }
				, {
		            'mDataProp' : 'annualizedRate',
		            'bSortable' : false,
		            'mRender': function(data, type, row) {
						return row.annualizedRate+"%";
					}
		        }, {
		            'mDataProp' : 'mobile',
		            'bSortable' : false
		        }, {
		            'mDataProp' : 'borrowerName',
		            'bSortable' : false
		        }, {
		            'mDataProp' : 'endDate',
		            'bSortable' : true,
		            'mRender': function(data, type, row) {
						var days = "";
						if (!!row.startDate) {
							days = formatDate(row.startDate, "yyyy-mm-dd");
						}
						if (!!row.endDate) {
							days = days + "~" + formatDate(row.endDate, "yyyy-mm-dd");
						}
						return days;
					}
		        }, {
		            'mDataProp' : 'refundTime',
		            'bSortable' : true,
		            'mRender'   : function(data, type, row) {
		            	if(row.allStatus==2){
		            		return "-";
		            	}else{
		            		return formatDate(data,'yyyy-mm-dd');
		            	}
		            	
		            }
		        }, {
		            'mDataProp': 'overdueDays',
		            'bSortable': false,
		            'mRender'   : function(data, type, row) {
		            	if(row.allStatus==2){
		            		return "-";
		            	}else{
		            		return row.overdueDays;
		            	}
		            	
		            }
		        }, {
		            'mDataProp': 'formatOverdueAmount',
		            'bSortable': false,
		            'mRender':function(data,type,row){
		            	if(row.allStatus==2){
		            		return "-";
		            	}else{
		            		if(!!row.formatOverdueAmount){
			            		return "<font color='red'>"+row.formatOverdueAmount+"</font>";
			            	}
		            	}
		            	
		            		
		            }
		        }, {
		            'mDataProp': 'lateFees',
		            'bSortable': false,
		            'mRender':function(data,type,row){
		            	if(row.allStatus==2){
		            		return "-";
		            	}else{
		            		if(!!row.formatLateFees){
			            		return "<font color='red'>"+row.formatLateFees+"</font>";
			            	}
		            	}
		            	
		            }
		        }, {
		            'mDataProp': 'totalPayAmount',
		            'bSortable': false,
		            'mRender':function(data,type,row){
		            	if(row.allStatus==2){
		            		return "-";
		            	}else{
		            		if(!!row.formatTotalPayAmount){
			            		return "<font color='red'>"+row.formatTotalPayAmount+"</font>";
			            	}
		            	}
		            }
		        }, {
		            'mDataProp': 'overdueStatus',
		            'bSortable': false,
		            'mRender':function(data,type,row){
		            	if(row.allStatus==1){
		            		return "有";
		            	}else{
		            		return "无";
		            	}
		            	
		            }
		        },{
					'mDataProp': 'operation',
					'bSortable': false,
					'mRender': function(data, type, row) {
						return getAllOperation(row);
					}
				}
			]
		});
	}
	
	//查询
	$('#query_overdue').on('click', function() {
		overdueTable.fnSettings()._iDisplayStart=0;
		overdueTable.fnDraw({
			"fnServerParams": function(aoData) {
				getAllSearchValue(aoData);
			}
		});
		return false;
	});
	$("#cancel_overdue").on('click', function () {
		$('#repay_flag_info_form')[0].reset();
	});
	//重置
	/*$('#reset_overdue').on('click', function() {
		$('#overdue_form')[0].reset();
	});*/
	
	//查看本息记录
	$("#overdue-table").on("click", '.interest-record', function() {
		var projectId = $(this).attr("data-projectId");
		showPayRecord(projectId);
	});
	//逾期还款记录
	$("#overdue-table").on("click", '.overdue-record', function() {
		var projectId = $(this).attr("data-projectId");
		showOverdueRecord(projectId);
	});
	//逾期还款标记
	$("#overdue-table").on("click", '.overdue-mark', function() {
		flagForm.resetForm();
		var projectId = $(this).attr("data-projectId");
		showOverdueMark(projectId);
	});
	//逾期还款标记
	function showOverdueMark(projectId) {
		
		var overdueRemarkTable = $('#overdueRemark-table').dataTable({
			"bPaginate": false,
			"bInfo": false,
			'bFilter' : false,
			'bProcessing' : true,
			'bSort' : false,
			"bRetrieve": true,
			'aaSorting':[[1,"desc"]],
			'bServerSide' : true,
			'fnServerParams' : function(aoData) {
				getAllSearchValue(aoData);
			},
			'fnRowCallback':function(nRow, aData,iDataIndex){
	        	$('td:eq(0)', nRow).html(iDataIndex+1);
	        },
			'sAjaxSource' : config.urlMap.getPayRecord+"?id=" + projectId + "&isMark=1",
			'aoColumns' : [
				{
					'mDataProp': 'payablePrincipal',
					'bSortable': false
				}, {
					'mDataProp': 'endDate',
					'bSortable': false,
					'mRender': function(data, type, row) {
						if(row.endDate != null) {
							return formatDate(row.endDate,"yyyy-mm-dd");
						}
					}
				}, {
					'mDataProp': 'payTypeStr',
					'bSortable': false
					
				}, {
					'mDataProp': 'formatTotalPayAmount',
					'bSortable': false
					
				}, {
					'mDataProp': 'status',
					'bSortable': false,
					'mRender'   : function(data, type, row) {
		            	return getDictLabel(config.interestStatus, row.status);
		            }
				}, {
					'mDataProp': 'payUnderStatusStr',
					'bSortable': false
					
				}
			]
		});
		overdueRemarkTable.fnSettings().sAjaxSource=config.urlMap.getPayRecord+"?id=" + projectId + "&isMark=1";
		overdueRemarkTable.fnDraw();
		$('#projectId').val(projectId);
		$('#overdueRemark-modal-table').modal('show');
		
	}
	//逾期还款记录
	function showOverdueRecord(projectId) {
		var overdueRecordTable = $('#overduerecord-table').dataTable({
			"bPaginate": false,
			"bInfo": false,
			'bFilter' : false,
			'bProcessing' : true,
			'bSort' : false,
			"bRetrieve": true,
			'aaSorting':[[1,"desc"]],
			'bServerSide' : true,
			'fnServerParams' : function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource' : config.urlMap.getOverdueRecord+"?id="+projectId,
			'aoColumns' : [
			      {
					'mDataProp': 'id',
					'bSortable': false
								
				}, {
					'mDataProp': 'repayDate',
					'bSortable': false,
					'mRender': function(data, type, row) {
						if(row.repayDate != null){
							return formatDate(row.repayDate,"yyyy-mm-dd");
						}
					}
				},{
					'mDataProp': 'overduePrincipal',
					'bSortable': false
				},{
					'mDataProp': 'overdueInterest',
					'bSortable': false
				},{
					'mDataProp': 'overdueFine',
					'bSortable': false
				},{
					'mDataProp': 'breachAmount',
					'bSortable': false
				},{
					'mDataProp': 'payableAmount',
					'bSortable': false
				},{
					'mDataProp': 'realpayAmount',
					'bSortable': false
				},{
					'mDataProp': 'repayType',
					'bSortable': false,
					'mRender'   : function(data, type, row) {
						if(row.repayType==1){
							return "线上";
						}else{
							return "线下";
						}
		            }
				},{
					'mDataProp': 'oparateName',
					'bSortable': false
				}
			]
		});
		overdueRecordTable.fnSettings().sAjaxSource=config.urlMap.getOverdueRecord+"?id="+projectId;
		overdueRecordTable.fnDraw();
		$('#overdue-modal-table').modal('show');
		
	}
	function showPayRecord(projectId) {
		var interestTable = $('#record-table').dataTable({
			"bPaginate": false,
			"bInfo": false,
			'bFilter' : false,
			'bProcessing' : true,
			'bSort' : false,
			"bRetrieve": true,
			'aaSorting':[[1,"desc"]],
			'bServerSide' : true,
			'fnServerParams' : function(aoData) {
				getAllSearchValue(aoData);
			},
			'fnRowCallback':function(nRow, aData,iDataIndex){
	        	$('td:eq(0)', nRow).html(iDataIndex+1);
	        },
			'sAjaxSource' : config.urlMap.getPayRecord+"?id="+projectId,
			'aoColumns' : [
				{
					'mDataProp': 'payablePrincipal',
					'bSortable': false
				}, {
					'mDataProp': 'endDate',
					'bSortable': false,
					'mRender': function(data, type, row) {
						if(row.endDate != null) {
							return formatDate(row.endDate,"yyyy-mm-dd");
						}
					}
				}, {
					'mDataProp': 'payTypeStr',
					'bSortable': false
					
				}, {
					'mDataProp': 'formatTotalPayAmount',
					'bSortable': false
					
				}, {
					'mDataProp': 'status',
					'bSortable': false,
					'mRender'   : function(data, type, row) {
		            	return getDictLabel(config.interestStatus, row.status);
		            }
				}
			]
		});
		interestTable.fnSettings().sAjaxSource=config.urlMap.getPayRecord+"?id="+projectId;
		interestTable.fnDraw();
		$('#interest-modal-table').modal('show');
		
	}
	
	
	$('#overdueRemark-table-1 tr').on("click",".i_e_date,.i_s_date",function(){
		if ($('#overdueRemark-table tbody tr td').length <= 1) {
			flagForm.resetForm();
			return;
		}
		WdatePicker({
			isShowClear:true,
			readOnly:true,
			dateFmt:'yyyy-MM-dd',
			onpicked:vailOverdueRepayDate
		});
	});
	//根据还款日期获取滞纳金、
	function vailOverdueRepayDate(){
		if ($('#overdueRemark-table tbody tr td').length <= 1) {
			flagForm.resetForm();
			return;
		}
		
		var edateObj = $(this),
		repayDate = edateObj.val();
		var projectId=$('#projectId').val();
		$.ajax({
			url:config.urlMap.getOverdueAmount,
			data:{'projectId':projectId,'repayDate':repayDate},
			dataType:"json",
			success:function(data){
				if(data!=null){
					$('#form-field-overduePrincipal').val(data.payablePrincipal);
					$('#form-field-overdueInterest').val(data.payableInterest);
					$('#form-field-lateFees').val(data.lateFees);
					$('#form-field-payableAmount').val(data.payableAmount);
					$('#form-field-unreturnPrincipal').val(data.unreturnPrincipal);
					$('#form-field-overdueDays').val(data.overdueDays);
					payableAmount=data.payableAmount;
				}else{
					bootbox.alert("");
				}
				
			}
		});
		
	}
	//逾期还款记录
	$("#repayInterest-table").on("click", '.repay-detail', function() {
		var id = $(this).attr("data-interestId");
		$('#modal-table_overdue_form').modal('show');
		$.post(
			config.urlMap.getOverdueInfo,{id:id},function(data){
				if(data!=null){
					if(typeof(data.borrowerName) != "undefined") {
						$('#o_borrowerName').html(data.borrowerName);
					} else {
						$('#o_borrowerName').html("");
					}
					if(typeof(data.mobile) != "undefined") {
						$('#o_mobile').html(data.mobile);
					} else {
						$('#o_mobile').html("");
					}
					if(typeof(data.thirdPayName) != "undefined") {
						$('#o_thirdPayName').html(data.thirdPayName);
					} else {
						$('#o_thirdPayName').html("");
					}
					//本金
					if(typeof(data.overduePrincipal) != "undefined") {
						$('#o_overduePrincipal').html(data.overduePrincipal);
					} else {
						$('#o_overduePrincipal').html("");
					}
					//利息
					if(typeof(data.overdueInterest) != "undefined") {
						$('#o_overdueInterest').html(data.overdueInterest);
					} else {
						$('#o_overdueInterest').html("");
					}
					//滞纳金
					if(typeof(data.overdueFine) != "undefined") {
						$('#o_overdueFine').html(data.overdueFine);
					} else {
						$('#o_overdueFine').html("");
					}
					//违约金
					if(typeof(data.breachAmount) != "undefined") {
						$('#o_breachAmount').html(data.breachAmount);
					} else {
						$('#o_breachAmount').html("");
					}
					//共需支付金额
					if(typeof(data.payableAmount) != "undefined") {
						$('#o_payableAmount').html(data.payableAmount);
					} else {
						$('#o_payableAmount').html("");
					}
					//实际支付金额
					if(typeof(data.realPayAmount) != "undefined") {
						$('#o_realPayAmount').html(data.realPayAmount);
					} else {
						$('#o_realPayAmount').html("");
					}
					if(typeof(data.refundType) != "undefined") {
						if(data.refundType==2){
							$('#return_type').html("线上");
						}else{
							$('#return_type').html("线下");
						}
					} else {
						$('#return_type').html("");
					}
					//逾期还款日期
					if(typeof(data.refundTime) != "undefined") {
						$('#o_refundTime').html(formatDate(data.refundTime,'yyyy-mm-dd HH:mm:ss'));
					} else {
						$('#o_refundTime').html("");
					}
					//垫付时间
					if(typeof(data.repayTime) != "undefined") {
						$('#o_repayTime').html(formatDate(data.repayTime,'yyyy-mm-dd HH:mm:ss'));
					} else {					
						$('#o_repayTime').html("");
					}
					
				}else{
					bootbox.alert("刷新失败！");
				}
			}
		);
		
	});
	//添加备注
	$("#repayInterest-table").on("click", '.addRemarks', function() {
		var id = $(this).attr("data-value");
		var controlRemarks =  $(this).attr("data-controlRemarks");
		$('#controlRemarksId').val(id);
		if(controlRemarks == 'null') {
			$('#newControlRemarks').val('');
		} else {
			$('#newControlRemarks').val(controlRemarks);
		}
		$('#modal-interestRemarks').modal('show');
	});
	
	//添加备注，发送后台
	$("#btn_add_interest_remarks").on("click",function(){
		$(this).addClass("disabled");
		var that = $(this);
		var id = $("#controlRemarksId").val();
		var newControlRemarks= $("#newControlRemarks").val();
		$.post(
			config.urlMap.addControlRemarks,{id:id,newControlRemarks:newControlRemarks},function(data){
				that.removeClass("disabled");
				if(data.success){
					$('#modal-controlRemarks').modal('hide');
					debtInfoTable.fnDraw('bStateSave', true);
				}else{
					showErrorMessage(data);
				}
			}
		);
	});
	//垫资代付
	$("#btn_save_pay_interest").on('click', function () {
		if (repayForm.check(false)) {
			$.ajax({
				url:config.urlMap.payInterest,
				data:$("#repay_interest_info_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						bootbox.alert("垫资代还提交成功！")
						interestTable.fnDraw();
						$('#modal-table_interest_form').modal('toggle');
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								return;
							});
						}else{
							return bootbox.alert("垫付失败！");
						}
					}
				}
			});
		}
			
	});
	//逾期还款标记
	$("#btn_save_flag_interest").on('click', function () {
		if(flagForm.check(false)){
			$.ajax({
				url:config.urlMap.saveRepayFlagInterest,
				data:$("#repay_flag_info_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						bootbox.alert("还款成功！")
						overdueTable.fnDraw();
						$('#overdueRemark-modal-table').modal('toggle');
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								return;
							});
						}else{
							return bootbox.alert("逾期还款标记失败！");
						}
					}
				}
			});
		}
});
	
	//编辑show
	$("#debt-table").on("click", '.debt-edit', function() {
		var target = $(this);
		var id = $(this).attr("data-id");
		$.ajax({
			url:config.urlMap.debtStatus,
			data:{'id':id},
			dataType:"json",
			success:function(data){
				if(data!=null){
					if(data.status==0){
						var serial = target.attr('data-serialNumber');
						window.top.setIframeBox("debt-edit-" + id, config.urlMap.show + id, "修改债权" + serial);
					}else {
						bootbox.alert("当前债权非存盘状态，不能编辑！");
					}
				}
				
			}
		});
	});
	

	$("#form-field-breachAmount").on("change",function(){
		var overdueFine = $(this).val();
		if(overdueFine>0&&payableAmount>0){
			$.ajax({
				url:config.urlMap.getPayableAmount,
				data:{'overdueFine':overdueFine,'payableAmount':payableAmount},
				dataType:"json",
				success:function(data){
					if(data!=null){
						$('#form-field-payableAmount').val(data);
					}else{
						bootbox.alert("");
					}
					
				}
			});
		}else{
			$('#form-field-payableAmount').val(payableAmount);
		}
		if(payableAmount==0){
			$('#form-field-payableAmount').val(overdueFine);
		}
	});



	/**
	 * 新增保存
	 */
	$('#save_debt').on('click', function() {
		getGuarantyType();
		getCollateralDetail();
		getPledgeDetail();
		var creditType = $('#credit_type_sel').find("option:selected").val();
		if(creditType=='houseRecord' || creditType=='carPayIn'  || creditType=='carBusiness' || creditType=='runCompany'){
			getCreditDetail();//信用详细信息
		}
		removeHiddenVaildation();
		if (!checkInterestEndDate()) {
			return false;
		};
		if (debtForm.check(false)) {
			$(this).attr("disabled","disabled");
			$('#debt_form').xform('post', config.urlMap.save, function(data) {
				if (!data.success) {
					if (!!data.resultCodeEum) {
						bootbox.alert(data.resultCodeEum[0].msg,function(){
							$('#save_debt').removeAttr("disabled");
						});
					} else {
						bootbox.alert("添加债权失败!",function(){
							$('#save_debt').removeAttr("disabled");
						});
					}
					
				} else {
					bootbox.alert("添加债权成功!", function() {
						debtTable.fnDraw();
						window.top.closeActiveIframe();
						$('#save_debt').removeAttr("disabled");
					});
				}
			});
		}
	});




function getThirdAccountMoney(memberId){
	$.ajax({
		url:config.urlMap.getThirdAccountMoney,
		data:{'memberId':memberId},
		dataType:"json",
		success:function(data){
			if(data!=null){
				$("#form-field-creditAmount").val(data);
			}
			
		}
	});
}



$('#interest_from').on('change', function() {
	var interestFrom = $(this).val();
	var startData = new Date($('#form-field-startDate').val());
	if($("#form-field-returnType").val()=="avg_principal"){
		$('#interest_s_date_1').val(formatDate(startData.getTime()));
	}else{
		$('#interest_s_date_1').val(formatDate(startData.getTime() + (interestFrom * 24 * 60 * 60 * 1000)));
	}
});


/**
 * 显示逾期垫资还款标记信息
 */
$("#query_overdue_debt").on("click", function() {
	if(overdueInfoSearchForm.check(false)){
		var projectId = $('#debtOverRepay_projectId').val();
		var repayDate = $('#debtOverRepay_debtOverRepayDate').val();
		if(!projectId || $.trim(projectId)=="" || projectId==null){
			bootbox.alert("项目ID不能为空!");
			return;
		}
		if(!repayDate || $.trim(repayDate)=="" || repayDate==null){
			bootbox.alert("还款日期不能为空!");
			return;
		}
		$("#debt_overdue_info").show();
		getDebtOverdueInfo(projectId, repayDate);
	}
	
});
//根据还款日期获取
function getDebtOverdueInfo(projectId, repayDate){
	$.ajax({
		url:config.urlMap.getOverdueAmount,
		data:{'projectId':projectId,'repayDate':repayDate},
		dataType:"json",
		success:function(data){
			if(data != null){
				$('#form-field-debtOverduePrincipal').val(data.payablePrincipal);
				$('#form-field-debtOverdueInterest').val(data.payableInterest);
				$('#form-field-debtLateFees').val(data.lateFees);
				$('#form-field-debtPayableAmount').val(data.payableAmount);
				$('#form-field-debt-overdue-overdueDays').val(data.overdueDays);
				$('#form-field-debt-overdue-projectId').val(projectId);
				$('#form-field-debt-overdue-refundTime').val(repayDate);
				$('#form-field-debt-overdue-unreturnPrincipal').val(data.unreturnPrincipal);
				$('#form-field-debtRealPayAmount').val("");
				payableAmount=data.payableAmount;
			}else{
				bootbox.alert("");
			}
			
		}
	});
	
}
//债权项目逾期还款标记
$("#btn_save_debt_overdue").on('click', function () {
	if(debtOverdueForm.check(false)){
		$.ajax({
			url:config.urlMap.saveDebtOverdueRepay,
			data:$("#debt_overdue_repay_info_form").serialize(),
			type:"post",
			dataType:"json",
			success:function(data){
				if(data.success){
					bootbox.alert("还款成功！")
					$("#debt_overdue_info").hide();
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							$("#debt_overdue_info").hide();
							return;
						});
					}else{
						return bootbox.alert("逾期还款标记失败！");
					}
				}
			}
		});
	}
});

