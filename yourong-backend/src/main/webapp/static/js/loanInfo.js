var loanTable;
jQuery(function($) {
	loanList();
});
	
	//获取操作按钮
	function getAllOperation(row) {
		var status = row.status,project_status = row.projectStatus;
		var detail = "<button class='btn btn-xs btn-danger p2p-loan permission-" + config.permission.loan + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "'><i class='icon-edit bigger-130'>放款</i></button>"; //详情
		if (status==51) { //待放款
				return detail;
		}else{
			return "";
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
            "sFileName": "放款管理列表.xls"
    };
    if(config.permission.loanExcel){
    	exportButton.push(excelButton);
    }
    function loanList(){
    	loanTable = $('#loan-table').dataTable({
    		"tableTools": {//excel导出
                "aButtons": exportButton,
                "sSwfPath": config.swfUrl
            },
            'bAutoWidth' : false,
    		'bFilter': false,
    		'bProcessing': true,
    		'bSort': true,
    		'bServerSide': true,
    		'fnServerParams': function(aoData) {
    			getAllSearchValue(aoData);
    		},
    		'sAjaxSource': config.urlMap.ajax,
    		'aoColumns': [{
    				'mDataProp': 'id',
    				'bSortable': false,
    				'mRender': function(data, type, row) {
    					return "<input type='checkbox' value=" + row.id + ">";
    				}
    			}, {
    	            'mDataProp' : 'id',
    	            'bSortable' : false
    	        }, {
    	            'mDataProp' : 'name',
    	            'bSortable' : false
    	        }, {
    				'mDataProp': 'securityType',
    				'bSortable': false,
    				'mRender': function(data, type, row) {
    					return getDictLabel(config.securityType, data);
    				}
    			}, {
    	            'mDataProp' : 'onlineTime',
    	            'bSortable' : true,
    	            'mRender'   : function(data, type, row) {
    	            	return formatDate(data,"yyyy-mm-dd HH:mm:ss");
    	            }
    	        }, {
    				'mDataProp' : 'salePeriod',
    				'bSortable' : false,
    				'mRender':function(data,type,row){
    					var salePeriod = "";
    					if(!!row.onlineTime){
    						salePeriod = formatDate(row.onlineTime,"yyyy-mm-dd HH:mm:ss");
    					}
    					if(!!row.saleEndTime){
    						salePeriod = salePeriod +"~"+ formatDate(row.saleEndTime,"yyyy-mm-dd HH:mm:ss");
    					}
    					return salePeriod;
    				}
    			}, {
    	            'mDataProp' : 'saleComplatedTime',
    	            'bSortable' : true,
    	            'mRender'   : function(data, type, row) {
    	            	var allTime="";
    	            	if(!!row.saleComplatedTime){
    	            		allTime=formatDate(row.saleComplatedTime,"yyyy-mm-dd HH:mm:ss");
    	            	}
    	            	return allTime;
    	            }
    	        }, {
    	            'mDataProp' : 'mobile',
    	            'bSortable' : false
    	        }, {
    	            'mDataProp' : 'borrowerName',
    	            'bSortable' : false,
    	            'mRender': function(data, type, row) {
    					if (row.borrowerName == null) {
    						return "";
    					}
    					if(!!row.openPlatformKey){
    						return row.borrowerName == null ? "" : row.borrowerName+showOpen(row);
    					}else{
    						return row.borrowerName == null ? "" : row.borrowerName;
    					}
    					
    				}
    	        }, {
    	            'mDataProp' : 'borrowerId',
    	            'bSortable' : false,'mRender':function(data,type,row){
    					var borrowValue = "";
    					if(!!row.borrowerId){
    						borrowValue ="YRUC"+row.borrowerId;
    					}
    					return borrowValue;
    				}
    	        }, {
    				'mDataProp' : 'totalAmount',
    				'bSortable' : false,
    				'mRender':function(data,type,row){
    					var amount = "";
    					if(!!row.totalAmount){
    						amount = row.formatTotalAmount;
    					}
    					return amount;
    				}
    			}, {
    				'mDataProp' : 'ableLoanAmount',
    				'bSortable' : false,
    				'mRender':function(data,type,row){
    					var amount = "";
    					if(!!row.ableLoanAmountStr){
    						amount =  row.ableLoanAmountStr;
    					}
    					return amount;
    				}
    			}, {
    				'mDataProp' : 'projectFeeStr',
    				'bSortable' : false,
    				'mRender':function(data,type,row){
    					var amount = "";
    					if(!!row.projectFeeStr){
    						amount =  row.projectFeeStr;
    					}
    					return amount+getAllDetail(row) ;
    				}
    			}, {
    				'mDataProp' : 'invesAmountStr',
    				'bSortable' : false,
    				'mRender':function(data,type,row){
    					var amount = "";
    					if(!!row.investAmount){
    						amount = row.invesAmountStr;
    					}else{
    						amount="￥0.00";
    					}
    					return amount;
    				}
    			}, {
    				'mDataProp' : 'totalCouponAmountStr',
    				'bSortable' : false,
    				'mRender':function(data,type,row){
    					var amount = "";
    					if(!!row.totalCouponAmount){
    						amount =  row.totalCouponAmountStr;
    					}else{
    						amount =  "￥0.00";
    					}
    					return amount;
    				}
    			}, {
    	            'mDataProp' : 'operateName',
    	            'bSortable' : false
    	        }, {
    	            'mDataProp' : 'loanDate',
    	            'bSortable' : true,
    	            'mRender'   : function(data, type, row) {
    	            	var Time="";
    	            	if(!!row.loanDate){
    	            		Time=formatDate(row.loanDate,'yyyy-mm-dd HH:mm');
    	            	}
    	            	return Time;
    	            }
    	        }, {
    	            'mDataProp' : 'status',
    	            'bSortable' : false,
    	            'mRender':function(data,type,row){
    	            	if(!!row.status){
    	            		if(row.status==50){
    		            		return "待审核";
    		            	}else if(row.status==51){
    		            		return "待放款";
    		            	}else if(row.status==52){
    		            		return "已放款";
    		            	}else{
    		            		return "已还款";
    		            	}
    	            	}
    				}
    	        }/*, {
    	            'mDataProp' : 'remarks',
    	            'bSortable' : false
    	        }*/,{
    				'mDataProp': 'operation',
    				'bSortable': false,
    				'mRender': function(data, type, row) {
    					return getAllOperation(row);
    				}
    			}
    		]
    	});
    }
    
    function showOpen(row) {
		var detail = "<button class='btn btn-xs btn-primary openPlatformKey permission-" + config.permission.loan + "' data-openPlatformKey='" + row.openPlatformKey + "' data-serialNumber ='" + row.serialNumber + "'>渠道商</button>"; //详情
		return detail;
	}
    
    $("#loan-table").on("click", '.openPlatformKey', function() {
		var openPlatformKey = $(this).attr("data-openPlatformKey");
		bootbox.alert(getDictLabel(config.channelKey, openPlatformKey));
	});
	
	function getAllDetail(row) {
		var detail = "<button class='btn btn-xs  p2p-detail permission-" + config.permission.loan + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "'><i class=' bigger-130'>明细</i></button>"; //详情
		return detail;
	}
	
	$("#loan-table").on("click", '.p2p-detail', function() {
		var id = $(this).attr("data-id");
		$("#detail_form").xform("load", config.urlMap.getLoanInfo+id,function(data){
			if(!!data){
				if(!!data.guaranteeFeeRate){
					$("#detail_guaranteeFee_c").show();
					$('#detail_guaranteeFee').html(data.guaranteeFeeRate+"%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+data.guaranteeFee);
				}else{
					$("#detail_guaranteeFee_c").hide();
					$('#detail_guaranteeFee').html("0%&nbsp;&nbsp;"+data.guaranteeFee);
				}
				$('#detail_formatManageFee').html(data.manageFeeRate+"%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+data.formatManageFee);
				if(!!data.riskFeeRate){
					$("#detail_riskFee_c").show();
					$('#detail_riskFee').html(data.riskFeeRate+"%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+data.riskFee);
				}else{
					$("#detail_riskFee_c").hide();
					$('#detail_riskFee').html("0%&nbsp;&nbsp;"+data.riskFee);
				}
				if(!!data.introducerFeeRate){
					$("#detail_introducerFee_c").show();
					$('#detail_introducerFee').html(data.introducerFeeRate+"%&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+data.introducerFee);
				}else{
					$("#detail_introducerFee_c").hide();
					$('#detail_introducerFee').html("0%&nbsp;&nbsp;"+data.introducerFee);
				}
				if(!!data.quickReward){
					$('#detail_quickReward').html("￥"+data.quickReward);
				}else{
					$('#detail_quickReward').html("￥0");
				}
				$('#detail_projectFeeStr').html(data.projectFeeStr);
			}
		});

		$('#modal-table-detail').modal('show');
	});
	//查询
	$('#query_loan').on('click', function() {
		loanTable.fnSettings()._iDisplayStart=0;
		loanTable.fnDraw({
			"fnServerParams": function(aoData) {
				getAllSearchValue(aoData);
			}
		});
		return false;
	});

	//重置
	$('#reset_loan').on('click', function() {
		$('#loan_form')[0].reset();
	});
	//获取放款基本信息
	$("#loan-table").on("click", '.p2p-loan', function() {
		var id = $(this).attr("data-id");
		$("#loan_info_form").xform("load", config.urlMap.getLoanInfo+id,function(data){
			if(data.regeditDate!=null){
			}
		});

		$('#modal-table_loan_form').modal('show');
	});
	




//放款
$("#btn_save_loan").on('click', function () {
	$('#modal-table_loan_form').modal('toggle');
		$.ajax({
			url:config.urlMap.loan,
			data:$("#loan_info_form").serialize(),
			type:"post",
			dataType:"json",
			success:function(data){
				if(data.success){
					bootbox.alert("放款成功！");
						loanTable.fnDraw();
						//$('#modal-table_loan_form').modal('toggle');
					//$('#modal-table_loan_form').modal('toggle');
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							loanTable.fnDraw();
							return;
						});
					}else{
						return bootbox.alert("放款失败！");
					}
				}
			}
		});
});

