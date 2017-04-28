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
		var repayUser = "<button class='btn btn-xs btn-danger select-repayUser permission-" + config.permission.repayUser
		+ "' data-interestId='" + row.interestId 
		+ "' data-projectName='" + row.projectName
		+ "' data-currentPeriods='" + row.currentPeriods 
		+ "' data-totalPeriods='" + row.totalPeriods 
		+ "' data-mobile='" + row.mobile 
		+ "' data-payablePrincipal='" + row.payablePrincipal 
		+ "' data-payableInterestStr='" + row.payableInterestStr
		+ "' data-realInterest='" + row.realInterest 
		+ "' data-totalPayAmount='" + row.realPayAmount 
		+ "' data-endDate='" + row.endDate 
		+ "' data-projectId='" + row.projectId 
		+ "' data-borrowerName='" + row.borrowerName + 
		"'><i class=' bigger-130'>选择垫资人</i></button>"; //
		
		var record = "<button class='btn btn-xs btn-info  showProjectInversts permission-" + config.permission.showDirectProjectInverst + "' data-projectId='" + row.projectId  + "'>查看还本付息明细</button>"; 
		
		var cancelUnderWrite = "<button class='btn btn-primary btn-xs  cancelUnderWrite permission-" + config.permission.showDirectProjectInverst + "' data-interestId='" + row.interestId  + "'><i class=' bigger-120'>撤销垫资还款</i></button>"; 
		
		if(row.canelUnderWrite==true){ 
			record=record+cancelUnderWrite;
		}
		//待还款
		if(allStatus==0&&row.thirdPayName==null){
			
			return record +repayUser;
		}else{
			
			return record;
		}
	
	}
	
	//撤销垫资还款
	$("#repayInterest-table").on("click", '.cancelUnderWrite', function() {
		var interestId = $(this).attr("data-interestId");
		bootbox.confirm("确定要取消该笔垫资还款吗？",function(result){
			if(result){
				$.post(config.urlMap.cancelUnderWrite,{interestId:interestId},function(data){
					if(data.success){
						bootbox.alert("操作成功",function(){
							interestTable.fnDraw();
						})
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								interestTable.fnDraw();
								return;
							});
						}else{
							return bootbox.alert("操作失败！");
						}
					}
				})
			}
		})
	});
	
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
            	if (iColumn === 3) {  
            		if (sValue != "") {
            			return "=\"" + sValue.substring(0,sValue.length-4) + "\"";
            		}
    	   		}else if (iColumn === 6) {  
            		if (sValue != "") {
            			return "=\"" + sValue + "\"";
            		}
    	   		}
            	
                	 return sValue;
            },
            "sFileName": "还本付息列表.xls"
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
		'sAjaxSource': config.urlMap.ajaxRepayment,
		'aoColumns': [{
				'mDataProp': 'id',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
	            'mDataProp' : 'projectId',
	            'bSortable' : false
	        }, {
	            'mDataProp' : 'projectName',
	            'bSortable': false,
	            'mRender':function(data, type, row){
					return "<a target='_blanck' href='"+config.webRootUrl+"/products/detail-"+row.projectId+".html'>"+data+"</a>"
	            }
	        }, {
	            'mDataProp': 'borrowerName',
	            'bSortable': false,
	            'mRender':function(data, type, row){
	            	if(row.borrowerName!=null  && row.mobile!=null){
	            		return row.borrowerName + "<button class='btn btn-xs btn-success permission-" + config.permission.queryBalance + "' style='float: right' onclick='showMemberBalance("+row.projectId+")' >显示余额</a>";
	            	}else{
	            		return "";
	            	}
	            }
	        }, {
	            'mDataProp' : 'mobile',
	            'bSortable' : false
	        }, {
	            'mDataProp' : 'onlineTime',
	            'bSortable' : true,
	            'mRender'   : function(data, type, row) {
	            	return formatDate(data,'yyyy-mm-dd HH:mm');
	            }
	        }, {
	            'mDataProp': 'totalPeriods',
	            'bSortable': false,
	            'mRender':function(data,type,row){
	            	return row.currentPeriods+"/"+row.totalPeriods;
	            }
	        }, {
	            'mDataProp' : 'endDate',
	            'bSortable' : true,
	            'mRender'   : function(data, type, row) {
	            	return formatDate(data,'yyyy-mm-dd');
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
	            	if(row.interestStatus==0){
	            		return "<font color='red'>￥"+row.payablePrincipalStr+"</font>";
	            	}else{
	            		return "￥"+data;
	            	}
	            }
	        }, {
	            'mDataProp': 'payableInterestStr',
	            'bSortable': false,
	            'mRender':function(data,type,row){
	            	if(row.interestStatus==0){
	            		return "<font color='red'>￥"+row.payableInterestStr+"</font>";
	            	}else{
	            		return "￥"+data;
	            	}
	            }
	        },{
			'mDataProp': 'realPayInterestStr',  //实际总支付利息
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.allStatus==0){
					return "-";
				}else{
					return data;
				}
			}
			}, {
	            'mDataProp': 'userInterestStr',
	            'bSortable': false,
	            'mRender':function(data,type,row){
	            	if(row.interestStatus==0 ){
	            		return "<font color='red'>￥"+row.userInterestStr+"</font>";
	            	}else{
	            		return "￥"+data;
	            	}
	            }
	        }, {
				'mDataProp': 'borrowerInterestStr',
				'bSortable': false,
				'mRender':function(data,type,row){
					if(row.allStatus==0 ){
						return "-";
					}else{
						return "￥"+data;
					}
				}
			},{
	            'mDataProp': 'extraInterestStr',
	            'bSortable': false,
	            'mRender':function(data,type,row){
	            	if(row.interestStatus==0){
	            		return "<font color='red'>￥"+row.extraInterestStr+"</font>";
	            	}else{
	            		return "￥"+data;
	            	}
	            }
	        },{
				'mDataProp': 'realExtraInterest',
				'bSortable': false,
				'mRender':function(data,type,row){
					if(row.allStatus==0){
						return "-";
					}else{
						return "￥"+data;
					}
				}
			}, {
	            'mDataProp': 'allStatus',
	            'bSortable': false,
	            'mRender':function(data,type,row){
	            	if(row.allStatus!=null && row.allStatus==0){
	            		return "待还款";
	            	}else if(row.allStatus!=null && row.allStatus==1){
	            		return "已还款";
	            	}else{
	            		return "";
	            	}
	            	
	            }
	        }, {
				'mDataProp' : 'guaranteeFeeRate',
				'bSortable' : false,
				'mRender':function(data,type,row){
					if (row.currentPeriods==row.totalPeriods){
						return data+"%";
					}else {
						return "";
					}
				}
			}, {
				'mDataProp' : 'guaranteeFee',
				'bSortable' : false,
				'mRender':function(data,type,row){
					if (row.currentPeriods==row.totalPeriods){
						return data;
					}else {
						return "";
					}
				}
			}, {
				'mDataProp' : 'thirdPayName',
				'bSortable' : false
			},{
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return getAllOperation(row);
				}
			}
		]
	});
	//查询
	$('#query_interest').on('click', function() {
		interestTable.fnSettings()._iDisplayStart=0;
		interestTable.fnDraw({
			"fnServerParams": function(aoData) {
				getAllSearchValue(aoData);
			}
		});
		return false;
	});

	//重置
	$('#reset_interest').on('click', function() {
		$('#interest_form')[0].reset();
	});
	//选择垫资人  获取详情
	$("#repayInterest-table").on("click", '.select-repayUser', function() {
		var id = $(this).attr("data-interestId");
		var projectName = $(this).attr("data-projectName");
		var borrow_name = $(this).attr("data-borrowerName");
		var periods = $(this).attr("data-periods");
		var end_date = $(this).attr("data-endDate");
		var borrow_mobile = $(this).attr("data-mobile");
		var payablePrincipalStr = $(this).attr("data-payablePrincipal");
		var payableInterestStr = $(this).attr("data-realInterest");
		var currentPeriods = $(this).attr("data-currentPeriods");
		var totalPayAmount = $(this).attr("data-totalPayAmount");
		var totalPeriods = $(this).attr("data-totalPeriods");
		var projectId = $(this).attr("data-projectId");
		$('#form-field-projectName').val(projectName);
		if(!!end_date){
			$('#form-field-dataTime').val(formatDate(end_date,'yyyy-mm-dd'));
		}
		if(borrow_name!="null"){
			$('#form-field-borrowerName').val(borrow_name);
		}else{
			$('#form-field-borrowerName').val("");
		}
		if(borrow_mobile!="null"){
			$('#form-field-mobile').val(borrow_mobile);
		}else{
			$('#form-field-mobile').val(" ");
		}
		
		
		$('#form-field-payablePrincipal').val(payablePrincipalStr);
		$('#form-field-payableInterest').val(payableInterestStr);
		$('#form-field-periods').val(currentPeriods+"/"+totalPeriods);
		$('#form-field-totalPayAmount').val(totalPayAmount);
		$('#form-field-interestId').val(id);
		$('#projectId').val(projectId);
		//同步垫资人余额
		//synchronizedBalance();
		$('#modal-table_interest_form').modal('show');
	});
	
	
	//选择垫资人  save
	$("#btn_save_pay_interest").on('click', function () {
		$(this).addClass("disabled");
		var that = $(this);
		if (repayForm.check(false)) {
			$.ajax({
				url:config.urlMap.saveRepayment,
				data:$("#repay_interest_info_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						bootbox.alert("垫资成功！")
						interestTable.fnDraw();
						$('#modal-table_interest_form').modal('toggle');
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								interestTable.fnDraw();
								//$('#modal-table_interest_form').modal('toggle');
								return;
							});
						}else{
							return bootbox.alert("垫资失败！");
						}
					}
				}
			});
		}
		that.removeClass("disabled");	
	});
	$("#cancel_interest").on('click', function () {
		$('#repay_interest_info_form')[0].reset();
	});
	//同步余额
	function synchronizedBalance(){
		$.ajax({
			url : config.urlMap.synchronizedBalance,
			success : function(data) {},
			error : function(e) {
			}
		});
	}

});
//显示余额
function showMemberBalance(projectId){
	var borrowerBalance="";
	var channelBalance="";
	$.ajax({
		url:config.urlMap.queryBorrowerBalance,
		data:{
			'projectId':projectId
		},
		type:"post", 
		dataType:'json',
		success:function(data) {
			if(data.success){
				borrowerBalance="借款人："+data.module.borrowerName+"(余额:" + data.module.balance + ",可用余额:"+ data.module.availableBalance+")";
				if(data.module.flag==true){
					channelBalance="<br/>渠道商："+data.module.channelName+"(余额:" + data.module.channelBalance + ",可用余额:"+ data.module.channelAvailableBalance+")";
					borrowerBalance=borrowerBalance+channelBalance;
				}
				bootbox.alert(borrowerBalance);
			}else{
				bootbox.alert("查询余额失败！");
			}
		}
	});
}
//获取第三方垫资公司
function getThirdAccountMoney(memberId){
	$.ajax({
		url:config.urlMap.getThirdAccountMoney,
		data:{'memberId':memberId},
		dataType:"json",
		success:function(data){
			if(data!=null){
				$("#form-field-creditAmount").val(data);
			}else{
				$("#form-field-creditAmount").val(" ");
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