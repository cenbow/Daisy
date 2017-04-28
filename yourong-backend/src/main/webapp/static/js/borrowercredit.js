var index_borrowercreditForm;
var borrowercreditForm;

jQuery(function($) {
	
	//检索表单验证初始化
	index_borrowercreditForm = $("#borrowCredit_form").Validform({
		tiptype: 4,
		ignoreHidden: true,
		showAllError: true,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	//表单验证初始化
	borrowercreditForm = $("#editCreditamount_form").Validform({
		tiptype: 4,
		ignoreHidden: true,
		showAllError: true,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	var borrowercreditTable = $('#borrowercredit-table').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : false,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.queryBorrowerCreditList,
		'aoColumns' : [{
			'mDataProp' : 'borrowerId',
			'mRender': function(data, type, row) {
				if (row.openPlatformKey != null && row.openPlatformKey != "") {
					return "-";
				}
				return data;
			}
		}, {
			'mDataProp' : 'borrowerType',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				if (row.openPlatformKey != null && row.openPlatformKey != "") {
					return "渠道商用户";
				}
				if (row.borrowerType == 1) {
					return "个人用户";
				}
				if (row.borrowerType == 2) {
					return "企业用户";
				}
				if (row.borrowerType == 4) {
					return "其他组织用户";
				}
				return "";
			}
		}, {
			'mDataProp' : 'borrowerTrueName',
			'bSortable' : false
		}, {
			'mDataProp' : 'borrowerMobile',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				if (row.openPlatformKey != null && row.openPlatformKey != "") {
					return "-";
				}
				return data;
			}
		}, {
			'mDataProp' : 'formatPayablePrincipal',
			'bSortable' : false
		}, {
			'mDataProp' : 'formatCreditAmount',
			'bSortable' : false
		},{
			'mDataProp' : 'status',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				var status=""
					if(row.status == 1){
						status = "正常";
					}else if(row.status == 0){
						status = "超出授信额";
					}
				return status;
			}
		},{
			'mDataProp' : 'onlineFlag',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				var onlineFlag=""
					if(row.onlineFlag == 0){
						onlineFlag = "暂停上线";
					} else {
						onlineFlag = "正常上线";
					}
				return onlineFlag;
			}
		},{
			'mDataProp' : 'remark',
			'bSortable' : false
		},{
			'mDataProp' : 'operation',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		}]
	});
	
	//获取操作方法
	function getAllOperation(row) {
		var pauseOnline = "<button class='btn btn-xs btn-info pauseOnline permission-" + config.permission.pauseOnline + "' data-trueName='"+row.borrowerTrueName+"' data-id="+row.id+">暂停上线</button>";
		var recoveryOnline = "<button class='btn btn-xs btn-info recoveryOnline permission-" + config.permission.recoveryOnline + "' data-trueName='"+row.borrowerTrueName+"' data-id="+row.id+">恢复上线</button>";
		var addRemark = "<button class='btn btn-xs btn-info addremark permission-" + config.permission.addRemark + "' data-remark='"+row.remark+"'' data-id="+row.id+">添加备注</button>";
		var updateCreditAmount = "<button class='btn btn-xs btn-info editCreditamount permission-" + config.permission.updateCreditAmount + "' data-creditamount='"+row.creditAmount+"'' data-trueName='"+row.borrowerTrueName+"' data-id="+row.id+">修改授信额</button>";
		var operation = "";
		
		if(row.onlineFlag==0){
			operation = recoveryOnline;
		} else {
			operation = pauseOnline;
		}
		operation = operation + "    " + addRemark + "  " + updateCreditAmount;
		return operation;
	}
	
	//查询
	$('#query_borrowerCredit').on('click', function() {
		borrowercreditTable.fnDraw();
	});
	
	//重置
	$('#reset_borrowerCredit').on('click', function() {
		$('#borrowCredit_form')[0].reset();
	});
	
	// 恢复上线
	$("#borrowercredit-table").on("click", '.recoveryOnline', function() {
		var id = $(this).attr("data-id");
		var trueName = $(this).attr("data-trueName");
		opertionOnline("恢复", trueName, config.urlMap.recoveryOnline, id);
	});
	// 暂停上线
	$("#borrowercredit-table").on("click", '.pauseOnline', function() {
		var id = $(this).attr("data-id");
		var trueName = $(this).attr("data-trueName");
		opertionOnline("暂停", trueName, config.urlMap.pauseOnline, id);
	});
	function opertionOnline(opertionBtn,trueName,url,id){
		bootbox.confirm("确定要"+ opertionBtn +"借款人"+ trueName +"的项目上线吗？",function(result){
			if(result){
				$.post(url,{
						id:id
					},function(data){
					if(data.success){
//						bootbox.alert(opertionBtn + "成功",function(){
							borrowercreditTable.fnDraw();
//						})
					}else{
						bootbox.alert(opertionBtn + "失败，请稍后重试！");
					}
				})
			}
		})
	}
	
	// 添加备注
	$("#borrowercredit-table").on("click",".addremark",function(){
		$("#addremark_form").resetForm();
		var id = $(this).attr("data-id");
		var remark = $(this).attr("data-remark");
		if (remark == "null") {
			remark = null;
		}
		$('#borrowercredit_remark').val(remark);
		$('#id').val(id);
		
		$('#modal-addremark').modal('show');
	});
	// 添加备注操作
	$("#btn_addremark").on('click', function () {
		var id = $('#id').val();
		var remark = $('#borrowercredit_remark').val();
		
		if(remark == ""){
			bootbox.alert("数据不能为空");
			return;
		}
		$.post(config.urlMap.addRemark, {
			id:id, 
			remark:remark
		}, function(data) {
//			$("#addremark_form").resetForm();
			$('#modal-addremark').modal('toggle');
			borrowercreditTable.fnDraw();
		});
		 
	});
	
	// 修改授信额
	$("#borrowercredit-table").on("click",".editCreditamount",function(){
		$("#editCreditamount_form").resetForm();
		var id = $(this).attr("data-id");
		var creditAmount = $(this).attr("data-creditamount");
		var trueName = $(this).attr("data-trueName");
		
		$('#id').val(id);
		$('#borrowercredit_creditAmount').val(creditAmount);
		$('.borrower_info').html(trueName);
		
		$('#modal-editCreditamount').modal('show');
	});
	// 修改授信额操作
	$("#btn_editCreditamount").on('click', function () {
		var id = $('#id').val();
		var creditAmount = $('#borrowercredit_creditAmount').val();
		
		if (borrowercreditForm.check(false)) {
			$.post(config.urlMap.updateCreditAmount, {
				id:id,
				creditAmount:creditAmount
			}, function(data) {
//				$("#editCreditamount_form").resetForm();
				$('#modal-editCreditamount').modal('toggle');
				borrowercreditTable.fnDraw();
			});
		}
		 
	});
	
});
