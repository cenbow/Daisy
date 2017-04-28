var directProjectTable;
var directProjectForm;
var directProjectPackageTable;
jQuery(function($) {
	//表单验证初始化
	directProjectForm = $("#direct_project_form").Validform({
		tiptype: 4,
		ignoreHidden: true,
		showAllError: true,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	//	直投项目列表数据
	getDirectProjectTable();
	//选择借款人类型
	selectBorrowerType();
	//自动补全企业信息
	autoCompletedCompanyInfo();
	//自动补全用户信息
	autoCompletedMemberInfo();
	//自动补全介绍人信息
	autoCompletedIntroducerInfo();
	//项目是否可以转让
	selectTransferFlag();
	//项目是否新客项目
	selectIsNovice();
	//项目是否满标悬赏
	selectCatalyzerFlag();
	//自动补全其他组织信息
	autoCompletedOrgInfo();
	//项目是否加息
	selectAddRateFlag();
    //直投项目打包列表
	getDirectProjectPackageTable();
	$(".j-modify-img").click(function() {
		$("#upload-project-thumbnail").show(1000);
		$("#project-thumbnail-pane").hide();
		$("#upload-thumbnail-btn").show();
	});
	//上传项目图片
	updateProjectThumbnail();
	
	//填写起投金额自动填充递增金额
	autoFullIncreAmount();
	
	searchDirectProjectList();
	
	//借款金额 ，同步所有费率
	$('#totalAmount').on('keyup', function() {
		var totalAmount = $('#totalAmount').val();
		
		var introducerFeeRate = $('#introducerFeeRate').val();
		if(introducerFeeRate){
			introducerFee = introducerFeeRate * totalAmount /100;
			$('#introducerFee').html("("+introducerFee+"元)");
		}
		
		/*var overdueFeeRate = $('#overdueFeeRate').val();
		if(overdueFeeRate){
			overdueFee = overdueFeeRate * totalAmount /100;
			$('#overdueFee').html("("+overdueFee+"元)");
		}
		
		var lateFeeRate = $('#lateFeeRate').val();
		if(lateFeeRate){
			lateFee = lateFeeRate * totalAmount /100;
			$('#lateFee').html("("+lateFee+"元)");
		}*/
		
		var guaranteeFeeRate = $('#guaranteeFeeRate').val();
		if(guaranteeFeeRate){
			guaranteeFee = guaranteeFeeRate * totalAmount /100;
			$('#guaranteeFee').html("("+guaranteeFee+"元)");
		}
		
		var riskFeeRate = $('#riskFeeRate').val();
		if(riskFeeRate){
			riskFee = riskFeeRate * totalAmount /100;
			$('#riskFee').html("("+riskFee+"元)");
		}
		
		var manageFeeRate = $('#manageFeeRate').val();
		if(manageFeeRate){
			manageFee = manageFeeRate * totalAmount /100;
			$('#manageFee').html("("+manageFee+"元)");
		}
		
	});
	
	
	//介绍费率
	$('#introducerFeeRate').on('keyup', function() {
		var introducerFeeRate = $('#introducerFeeRate').val();
			var totalAmount = $('#totalAmount').val();
			if(totalAmount){
				introducerFee = introducerFeeRate * totalAmount /100;
				$('#introducerFee').html("("+introducerFee+"元)");
			}
	});
	
	$('#introducerFeeRate').blur(function(){
		var introducerFeeRate = $('#introducerFeeRate').val();
		if(introducerFeeRate){
			var reg = /^(?!0+(?:\.0+)?$)(?:[1-9]\d{0,2}|0)(?:\.\d{1,2})?$/; 
		    //console.log (reg.test (introducerFeeRate));
			if(!reg.test(introducerFeeRate)){
				bootbox.alert("介绍费率，请输入最多两位小数！");
			}
		}
	});
	

	/*//垫资罚息率
	$('#overdueFeeRate').on('keyup', function() {
		var overdueFeeRate = $('#overdueFeeRate').val();
		var totalAmount = $('#totalAmount').val();
		if(totalAmount){
			overdueFee = overdueFeeRate * totalAmount /100;
			$('#overdueFee').html("("+overdueFee+"元)");
		}
	});
	
	//逾期罚息率
	$('#lateFeeRate').on('keyup', function() {
		var lateFeeRate = $('#lateFeeRate').val();
		var totalAmount = $('#totalAmount').val();
		if(totalAmount){
			lateFee = lateFeeRate * totalAmount /100;
			$('#lateFee').html("("+lateFee+"元)");
		}
	});*/
	
	//保证金费率
	$('#guaranteeFeeRate').on('keyup', function() {
		var guaranteeFeeRate = $('#guaranteeFeeRate').val();
		var totalAmount = $('#totalAmount').val();
		if(totalAmount){
			guaranteeFee = guaranteeFeeRate * totalAmount /100;
			$('#guaranteeFee').html("("+guaranteeFee+"元)");
		}
	});
	
	//风险金费率
	$('#riskFeeRate').on('keyup', function() {
		var riskFeeRate = $('#riskFeeRate').val();
		var totalAmount = $('#totalAmount').val();
		if(totalAmount){
			riskFee = riskFeeRate * totalAmount /100;
			$('#riskFee').html("("+riskFee+"元)");
		}
	});
	
	//管理费率
	$('#manageFeeRate').on('keyup', function() {
		var manageFeeRate = $('#manageFeeRate').val();
		var totalAmount = $('#totalAmount').val();
		if(totalAmount){
			manageFee = manageFeeRate * totalAmount /100;
			$('#manageFee').html("("+manageFee+"元)");
		}
	});
	
});

function getOperatorBtn(row) {
	
	var totalBtn = '',
		status = row.status,//项目状态
		editBtn = getBtn("dir-prj-edit", "btn-primary", config.permission.edit, getParamJson({"id":row.id,"name":row.name,"url":config.urlMap.edit}), "编辑"),
		emergencyAllBtn = getBtn("dir-prj-emgall", "btn-danger", config.permission.emergencyAll, getParamJson({"id":row.id,"name":row.name,"url":config.urlMap.emergencyAll}), "紧急修改（全部）"),
		emergencyPartBtn = getBtn("dir-prj-emgpart", "btn-danger", config.permission.emergencyPart, getParamJson({"id":row.id,"name":row.name, "url":config.urlMap.emergencyPart}), "紧急修改"),
		detailBtn = getBtn("dir-prj-detail", "btn-info", config.permission.detail, getParamJson({"id":row.id,"name":row.name, "url":config.urlMap.detail}), "查看" ),
		delBtn = getBtn("dir-prj-del", "btn-danger", config.permission.del, getParamJson({"id":row.id,"url": config.urlMap.del}), "删除" ),
		startBtn = getBtn("dir-prj-start", "btn-success",config.permission.start,getParamJson({"id":row.id,"url": config.urlMap.start}), "开启"),
		stopBtn = getBtn("dir-prj-stop", "btn-yellow",config.permission.stop,getParamJson({"id":row.id,"url":  config.urlMap.stop}), "暂停"),
		reviewBtn = getBtn("dir-prj-review", "btn-warning",config.permission.review,getParamJson({"id":row.id,"url": config.urlMap.review}), "上线审核"),
		riskReviewBtn = getBtn("dir-prj-riskReview", "btn-purple",config.permission.riskReview,getParamJson({"id":row.id,"url": config.urlMap.riskReview}), "风控审核"),
		waitReviewBtn = getBtn("dir-prj-waitReview", "btn-primary",config.permission.waitReview,getParamJson({"id":row.id, "url": config.urlMap.waitReview}), "提交待审"),
		payRecordBtn = getBtn("dir-prj-payRecord", "btn-primary",config.permission.payRecord,getParamJson({"id":row.id, "url": config.urlMap.getPayRecord,"totalAmount":row.formatTotalAmount,
			"borrowerMemberBaseBiz":row.borrowerMemberBaseBiz,"originalProjectNumber":row.originalProjectNumber,"annualizedRate":row.annualizedRate,"startDate":row.startDate,"endDate":row.endDate,"profitType":row.profitType}), "还款记录"),
		uotTimeBtn = getBtn("dir-prj-uotTime", "btn-primary",config.permission.uotTime,getParamJson({"id":row.id,"onlineTime":row.onlineTime,"saleEndTime":row.saleEndTime,"startDate":row.startDate,"url": config.urlMap.uotTime}), "修改募集时间"),
		uEndTimeBtn = getBtn("dir-prj-uEndTime", "btn-primary",config.permission.uEndTime,getParamJson({"id":row.id,"saleEndTime":row.saleEndTime,"url":config.urlMap.uEndTime}), "修改募集时间"),
		loseBtn = getBtn("dir-prj-lose", "btn-primary",config.permission.lose,getParamJson({"id":row.id,"url":config.urlMap.lose}), "流标"),
		delProjectRedis=getBtn("dir-prj-delProjectRedis", "btn-primary",config.permission.delProjectRedis,getParamJson({"id":row.id,"url":config.urlMap.delProjectRedis}), "清除项目缓存"),
		remarkBtn=getBtn("dir-prj-addremark", "btn-primary",config.permission.addRemark,getParamJson({"id":row.id,"url":config.urlMap.addRemarks,"remark":row.remarks}), "添加备注")

		totalBtn = editBtn + " " + emergencyAllBtn + " " + emergencyPartBtn + " " + detailBtn + " "+ startBtn + " " + stopBtn + " " + reviewBtn + " " +waitReviewBtn + " "+ riskReviewBtn + " " + delBtn +" "+ uotTimeBtn + " " + uEndTimeBtn +" " +payRecordBtn+" "+delProjectRedis + " " + remarkBtn;
	if(status == 1){
		totalBtn = waitReviewBtn + " " + editBtn +" " +  delBtn + " " + detailBtn +" " + delProjectRedis + " " + remarkBtn;
	}else if(status == 10){
		totalBtn = reviewBtn + " " + editBtn + " " + uotTimeBtn + " " + detailBtn +" " + delProjectRedis + " " + remarkBtn;
	}else if(status == 20){
		totalBtn = emergencyAllBtn + " " + emergencyPartBtn + " " + uotTimeBtn + " " + detailBtn +" " + delProjectRedis + " " + remarkBtn;
	}else if(status == 30){
		totalBtn = stopBtn + " " + emergencyPartBtn + " " + uEndTimeBtn + " " + detailBtn + " " + delProjectRedis + " " + remarkBtn;
	}else if(status == 40){
		totalBtn = startBtn + " " + emergencyPartBtn + " " + uEndTimeBtn + " " + detailBtn + " " + delProjectRedis + " " + remarkBtn;
	}else if(status == 50){
		totalBtn = riskReviewBtn + " " + emergencyPartBtn + " " + detailBtn + " " + delProjectRedis + " " + remarkBtn;
	}else if(status == 51 || status == 80 || status == 90){
		totalBtn = emergencyPartBtn + " " + detailBtn + " " + delProjectRedis + " " + remarkBtn;
	}else if(status == 52 || status == 70){//已放款、已还款  可以查看还本付息
		totalBtn = payRecordBtn + " "+emergencyPartBtn + " " + detailBtn + " " + delProjectRedis + " " + remarkBtn;
	}else if(status==60){//已截止  可以操作“流标”
		totalBtn = loseBtn +" " + emergencyPartBtn + " " + detailBtn + " " + delProjectRedis + " " + remarkBtn;
	}else if(status==81){//审核通过  可以操作“查看” 和 “紧急修改”
		totalBtn = detailBtn +" " + emergencyPartBtn + " " + remarkBtn;
	}
	return totalBtn;
}

function getBtn(btnClass, iconClass, permission, paramJson, btnName, url) {
	return "<button class='btn " + btnClass +" "+ iconClass +" permission-" + permission + "' data-url='" + url + "' data-class='" + btnClass + "' data-param='"+paramJson+"' >" + btnName + "</button>";
}

function getParamJson(paramObj){
	return JSON.stringify(paramObj);
}
/***
 * 导出按钮的权限设置
 */
var exportButton = [];
var excelButton = { 
        "sExtends": "xls", 
        "sButtonText": "导出Excel",
        "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13],
        "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
        	sValue = nTr.childNodes[iColumn].innerText;
        	 if (sValue != "") {
                 return "=\"" + sValue + "\"";
             } else {
            	 return sValue;
             }
        },
        "sFileName": "直投项目列表.xls"
};
if(config.permission.directExcel){
	exportButton.push(excelButton);
}
/**
 *直投项目列表数据
 */
function getDirectProjectTable() {
	directProjectTable = $('#direct-project-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'bAutoWidth' : false,
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'aaSorting':[[0,"desc"]],
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
			'mDataProp': 'id',
			'bSortable': false
		}, {
			'mDataProp': 'originalProjectNumber',
			'bSortable': false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'securityType',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getDictLabel(config.securityType, data);
			}
		}, {
			'mDataProp': 'totalAmount',
			'bSortable': false
		}, {
			'mDataProp': 'borrowerName',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if (row.borrowerMemberBaseBiz == null) {
					return "";
				}
				if (row.borrowerMemberBaseBiz.member == null) {
					return "";
				}
				return row.borrowerMemberBaseBiz.member.trueName == null ? "" : row.borrowerMemberBaseBiz.member.trueName;
			}
		}, {
			'mDataProp': 'annualizedRate',
			'bSortable': false,
			'mRender': function(data) {
				return data == null ? "" : data + "%";
			}
		}, {
			'mDataProp': 'interestFrom',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getDictLabel(config.interestFrom, row.interestFrom);
			}
		}, {
			'mDataProp': 'borrowPeriod',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var periodValue = "";
				if (!!row.borrowPeriodType) {
					periodValue = data + getDictLabel(config.borrowPeriodType, row.borrowPeriodType);
				}
				return periodValue;
			}
		}, {
			'mDataProp': 'createTime',
			'bSortable': true,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp': 'onlineTime',
			'bSortable': true,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		},{
			'mDataProp' : 'onlineFlag',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				var onlineFlag = "";
				if (data == 1) {
					onlineFlag = "正常上线";
				} else if (data == 2){
					onlineFlag = "暂停上线";
				} else if (data == 3){
					onlineFlag = "正常上线(超出授信额)";
				} else if (data == 4){
					onlineFlag = "暂停上线(超出授信额)";
				}
				
				return onlineFlag;
			}
		}, {
			'mDataProp': 'salePeriod',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var salePeriod = "";
				if (!!row.onlineTime) {
					salePeriod = formatDate(row.onlineTime, "yyyy-mm-dd HH:mm:ss");
				}
				if (!!row.saleEndTime) {
					salePeriod = salePeriod + "~" + formatDate(row.saleEndTime, "yyyy-mm-dd HH:mm:ss");
				}
				return salePeriod;
			}
		}, {
			'mDataProp': 'progress',
			'bSortable': false,
			'mRender': function(data) {
				return data + "%"
			}
		}, {
			'mDataProp': 'saleComplatedTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(!!data){
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
				}else{
					return "-";
				}
			}
		}, {
			'mDataProp': 'publishName',
			'bSortable': false
		}, {
			'mDataProp': 'operateName',
			'bSortable': false
		}, {
			'mDataProp': 'status',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getDictLabel(config.projectStatus, row.status);
			}
		}, {
			'mDataProp': 'operator',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getOperatorBtn(row);
			}
		}, {
			'mDataProp': 'remarks',
			'bSortable': false
		}]
	});
}
/**
 * 直投项目打包列表
 */
var  exportButton_package = [];
function getDirectProjectPackageTable() {
	    directProjectPackageTable = $('#direct-project-package-table').dataTable({
        'bAutoWidth' : false,
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'aaSorting':[[0,"desc"]],
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
//		'fnRowCallback':function(nRow, aData,iDataIndex){
//		        	var startIndex = iDataIndex;
//					if(typeof oTable1 !== "undefined"){
//						var dts = oTable1.dataTableSettings[0];
//						startIndex = dts._iDisplayStart+iDataIndex;
//					}
//		        	$('td:eq(1)', nRow).html(startIndex+1);
//		 },
		'sAjaxSource': config.urlMap.packageAjax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value='" + row.id + "' status='"+row.status+"'>";
			}
		},
		{
			'mDataProp': 'id',
			'bSortable': false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		},{
			'mDataProp':'totalAmount',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return (data / 10000)+"万元";
			}
		},{
			'mDataProp': 'totalCount',
			'bSortable': false
		}, {
			'mDataProp': 'minAnnualizedRate',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(row.minAnnualizedRate == row.maxAnnualizedRate){
					 return row.maxAnnualizedRate+"%";
				}else{
					return row.minAnnualizedRate+" - "+row.maxAnnualizedRate+"%"
				}
			}
		},{
			'mDataProp': 'maxBorrowPeriod',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var periodValue= row.minBorrowPeriod+" - "+row.maxBorrowPeriod;
				if(row.minBorrowPeriod == row.maxBorrowPeriod){
					periodValue = row.maxBorrowPeriod;
				}
				if (row.maxBorrowPeriodType) {
					periodValue = periodValue + getDictLabel(config.borrowPeriodType, row.maxBorrowPeriodType);
				}
				return periodValue;
			}
		},{
			'mDataProp': 'saleEndTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(!!data){
					return formatDate(row.saleStartTime, "yyyy-mm-dd HH:mm:ss")+" ~ "+formatDate(row.saleEndTime, "yyyy-mm-dd HH:mm:ss");
				}else{
					return "-";
				}
				
			}
		},{
			'mDataProp': 'progress',
			'bSortable': false,
			'mRender': function(data) {
				return data + "%"
			}
		},{
			'mDataProp': 'status',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(data ==0){
					return "待审核"
				}else if(data == 1){
					return "募集中"
				}else if(data == 2){
					return "已售罄"
				}
			}
		},
		{
			'mDataProp': 'status',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var auditButton =  "<button  type='button' projectPackageId='"+row.id+"' class='btn btn-info btn-sm btn-primary auditProjectPackage'>审核</button>";
				var queryButton = "<button style ='margin-left:10px'  projectPackageId='"+row.id+"' type='button' class='btn btn-info btn-sm btn-primary query_direct_package_detail'>查看子项目</button>";
			    if(row.status ==0){
			    	return auditButton+queryButton;
			    }else{
			    	return queryButton;
			    }
			}
		}]
	});
};
$(document).on("click","#query_direct_project",function(){
	directProjectTable.fnDraw();
})

$("#query_direct_project_package").on("click",function(){
	directProjectPackageTable.fnDraw();
})
var initModal = function(){
	 $("#controlRemarks_form").find("input[type='text']").val("");
	 var len = $(".addProjectTr").length;
	 $(".addProjectTr").not($(".addProjectTr").eq(len-1)).remove();
	 $(".addProjectTr").eq(0).find(".deleteProject").hide();
	 $(".addProjectTr").eq(0).find("td").eq(0).html("添加项目：");
	 
	 $("#thumbnail-pane").find("img").attr("src",$("#defaultImg").val());
	 $("#thumbnail").val("");
}
//显示弹出层
$("#add_direct_project_package").on("click",function() {
	 initModal();
	 $("#projectPackageId").val("");
	 $("#projectPackage-add-remark").modal('show');

	 
});
//修改
$("#update_direct_project_package").on("click",function(){
	initModal();
	var project_arrray_element = $("input[type='checkbox']:checked");
	if(project_arrray_element.length ==0){
			bootbox.alert("您选择数据");
			return false;
	}; 		
	var projectPackageId = $("input[type='checkbox']:checked").val();
	$.post(
	    config.urlMap.getPackage,
	    {'projectPackageId':projectPackageId},
	    function(data){
	    	$("#projectPackage-add-remark").modal('show');
	    	$("#projectPackage-update-remark").find(".modalFormTitle").html("修改集合项目");
	    	$("#projectPackageName").val(data.name);
	    	var projectArray = data.projectList;
	    	for(var i=0;i<projectArray.length;i++){
	    		 $(".addProjectTr").eq(i).find("input[name='originalProjectNumber']").val(projectArray[i].projectId);
	    		 if(i < projectArray.length-1){
	    			var _this = $(".addProjectTr").eq(i).find(".addProject");
	    			addProjectOption(_this);
	    		 }
	    	}
	    	$("#thumbnail-pane").find("img").attr("src",data.pathImg);
	    	$("#projectPackageId").val(data.id);
	    	$("#projectPackagestatus").val(data.status);
	    	$("#thumbnail").val(data.imgurl);
	    }
	);
});

var addProjectOption = function(_this){
	var tr_element = $(_this).parent().parent().clone();
	tr_element.find("td").eq(0).html("");
	tr_element.find("input").val("");
	var tbody_element=$(_this).parents("tbody");
	//tbody_element.append(tr_element);
	$(_this).parent().parent().after(tr_element);
	tr_element.find(".deleteProject").show();//显示当前删除按钮
	$(_this).hide();
	$(_this).next().show();
}
//继续添加
$(document).on("click",".addProject",function(){
	var projectPackageId =$("#projectPackageId").val();
	var projectPackagestatus = $("#projectPackagestatus").val();
	if(projectPackageId!='' && projectPackagestatus !=0){
		  bootbox.alert("【募集中】和【已售罄】不能添加子项目");
		  return false;
	}
	addProjectOption(this);
})
//删除
$(document).on("click",".deleteProject",function(){
	var projectPackageId =$("#projectPackageId").val();
	var projectPackagestatus = $("#projectPackagestatus").val();
	if(projectPackageId!='' && projectPackagestatus !=0){
		  bootbox.alert("【募集中】和【已售罄】不能删除子项目");
		  return false;
	}
	$(this).parent().parent().remove();
	$(".addProjectTr").find(".addProject").hide();
	$(".addProjectTr").find(".addProject").last().show();
	if($(".addProjectTr").length == 1){
		$(".addProjectTr").eq(0).find(".deleteProject").hide();
	}
	$(".addProjectTr").eq(0).find("td").eq(0).html("添加项目：");
})
//审核
$(document).on("click",".auditProjectPackage",function(){
	var projectPackageId =$(this).attr("projectPackageId");
	bootbox.confirm("请审核该项目是否上线？",function(result){
		if(result){
			$.post(config.urlMap.auditProjectFromPackage, {
				"projectPackageId":projectPackageId
			}, function(data) {
		        if(data < 0){
		    	    bootbox.alert("审核失败");
					return false;
				}else{
		          $("#projectPackage-add-remark").modal('hide');
		          directProjectPackageTable.fnDraw();
				}
			});	
		}
	})
})
//查看资产包项目详细
$(document).on("click",".query_direct_package_detail",function(){
	$.post(
		config.urlMap.packageProjectAjax,
		{"projectPackageId":$(this).attr("projectPackageId")},
		function(data){
			if(data ==undefined ||data==null) return ;
			var trHtml =""; 
			for(var i=0;i<data.length;i++){
				var row = data[i];
				var borrowPeriod =row.borrowPeriod+getDictLabel(config.borrowPeriodType, row.borrowPeriodType);
				var profitType = getDictLabel(config.returnType, row.profitType);
				var salePeriod ="";
				if (!!row.onlineTime) {
					salePeriod = formatDate(row.onlineTime, "yyyy-mm-dd HH:mm:ss");
				}
				if (!!row.saleEndTime) {
					salePeriod = salePeriod + "~" + formatDate(row.saleEndTime, "yyyy-mm-dd HH:mm:ss");
				}
				var status = getDictLabel(config.projectStatus, row.status);
				trHtml += "<tr role='row' class='odd'><td>"+row.name+"</td><td>"+row.totalAmount+"</td>"+
				         "<td>"+row.annualizedRate+"%</td><td>"+borrowPeriod+"</td>"+
				         "<td>"+profitType+"</td><td>"+salePeriod+"</td>"+
				         "<td>"+row.progress+"%</td><td>"+status+"</td></tr>";
			}
			$("#projectDetailTable").html(trHtml);
			console.log(data);
			$("#projectPackage-show-remark").modal('show');
		}
	);
});
//确定添加
$("#btn_add_projectPackage").on("click",function(){
	var projectPackageName =$.trim($("#projectPackageName").val());
	if(projectPackageName == ''){
		bootbox.alert("请填入集合项目名称!");
		return false;
	}
	var originalProjectNumber_array = [];
	$("input[name='originalProjectNumber']").each(function(){
		var originalProjectNumber = $.trim($(this).val());
		if(originalProjectNumber == ''){
			   bootbox.alert("请输入项目编号");
			   originalProjectNumber_array = [];
			   return false;
		}
		originalProjectNumber_array.push(originalProjectNumber);
	});
	var id = $("#projectPackageId").val();
	var thumbnail =$("#thumbnail").val();
	if(thumbnail == '' && id==''){
		bootbox.alert("请上传项目缩列图!");
		return false;
	}
	if(originalProjectNumber_array.length ==0){
		return false;
	}
	
	$.post(config.urlMap.addProjectNumber, {
		"name":projectPackageName,
		"id":id,
		"projectIdJson" : JSON.stringify(originalProjectNumber_array),
		"imgurl":thumbnail
	}, function(data) {
        if(data == -1){
    	    bootbox.alert("您填入的项目编号!请核对输入");
			return false;
		}
        if(data == -2){
        	 bootbox.alert("您填入的项目编号不存在!请核对输入");
 			return false;
        }
        if(data == -3){
       	 bootbox.alert("您填入的项目编号已经存在!请勿重复添加");
			return false;
        }
        if(data == -4){
          	 bootbox.alert("存盘、待审核和不能添加集合中");
   			return false;
        }
        if(data == -5){
         	 bootbox.alert("待发布项目(未预告)不能添加集合中");
  			return false;
       }
        $("#projectPackage-add-remark").modal('hide');
        directProjectPackageTable.fnDraw();
	});		
});
$("#delete_direct_project_package").on("click",function(){
	var project_arrray_element = $("input[type='checkbox']:checked");
	if(project_arrray_element.length ==0){
		 bootbox.alert("您选择要删除的项目");
		return false;
	};
	var project_array=[];
	project_arrray_element.each(function(){
		project_array.push($(this).val());
	});
	
	bootbox.confirm("确定要删除项目资产包吗？",function(result){
		
		if(result){
			$.post(config.urlMap.removeProjectPackageAjax,{projectIds: JSON.stringify(project_array)},function(data){
				if(data == -1){
					bootbox.alert("项目删除失败，请稍后重试！");
				}else{
					bootbox.alert("成功删除项目",function(){
						directProjectPackageTable.fnDraw();
					})
				}
			})
		}
	});	
})
/**
 * 借款人类型选择
 * 显示不同的输入框（个人：姓名；企业：企业名称）
 */
function selectBorrowerType() {
	$("#borrower_member_type").on("change", function() {
		var type = $(this).val();
		showBorrowerType(type);
	});
}

function showBorrowerType(type) {
	switch (type) {
		case "1":
			$("#borrower_member_info").show();
			$("#borrower_company_info").hide();
			$("#borrower_org_info").hide();
			hiddenBorCompanyDetailHtml();
			hiddenBorOrgDetailHtml();
			showBorMemberDetailHtml();
			break;
		case "2":
			$("#borrower_company_info").show();
			$("#borrower_member_info").hide();
			$("#borrower_org_info").hide();
			hiddenBorMemberDetailHtml();
			hiddenBorOrgDetailHtml();
			showBorCompanyDetailHtml();
			break;
		case "4":
			$("#borrower_org_info").show();
			$("#borrower_member_info").hide();
			$("#borrower_company_info").hide();
			hiddenBorMemberDetailHtml();
			hiddenBorCompanyDetailHtml();
			showBorOrgDetailHtml();
			break;
		default:
			$("#borrower_company_info").hide();
			$("#borrower_member_info").hide();
			$("#borrower_org_info").hide();
			removeBorCompanyDetailHtml();
			removeBorCompanyDetailHtml();
			removeBorOrgDetailHtml();
	}
}

//===autoCompletedCompanyInfo=== 
/**
 * 自动补全企业信息
 */
function autoCompletedCompanyInfo() {
	$("#borrower_company_name").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchEnterprise,
				dataType: "json",
				type: 'post',
				data: {
					enterpriseName: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						console.info(item);
						return {
							regeditDate: formatDate(item.regeditDate,"yyyy-mm-dd"), // 注册时间
							address: item.address, // 公司所在地
							income: item.income, //法人身份证
							organizationCode: item.organizationCode, //社会统一代码
							legalName: item.legalName, //法人
							yearSales: item.yearSales, //年度销售额
							id: item.legalId,
							enterpriseId: item.id,
							value: item.name + " " + item.legalName
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			removeBorCompanyDetailHtml(); //移除动态添加的借款用户信息html
			addBorCompanyDetailHtml(); //动态添加显示的借款用户信息html
			fullValForBorCompanyDetail(ui); //填充借款用户数据
			checkCompanyBorrowerCredit(ui); //检验企业用户借款人授信额度信息
			return;
		}
	});
}

//自动补全其他组织信息
function autoCompletedOrgInfo() {
	$("#borrower_regis_no").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchRegisNo,
				dataType: "json",
				type: 'post',
				data: {
					regisNo: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						console.info(item);
						return {
							regeditDate: formatDate(item.regeditDate,"yyyy-mm-dd"), // 注册时间
							address: item.address, // 公司所在地
							organizationCode: item.organizationCode, //社会统一代码
							legalName: item.legalName, //法人
							telephone: item.telephone,
							id: item.legalId,
							enterpriseId: item.id,
							value: item.regisNo 
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			removeBorOrgDetailHtml();
			addBorOrgDetailHtml(); //动态添加显示的借款用户信息html
			fullValForBorOrgDetail(ui); //填充其他组织数据
			checkOrgBorrowerCredit(ui); //检验其他组织用户借款人授信额度信息
			return;
		}
	});
}

function fullValForBorOrgDetail(ui) {
	$("#borrower_memberId").val(ui.item.id);
	$("#borrower_enterpriseId").val(ui.item.enterpriseId);
	$('#org_borrower_regeditDate').html(ui.item.regeditDate); //注册时间
	$('#org_borrower_organizationCode').html(ui.item.organizationCode); //社会统一代码
	$('#org_borrower_legalName').html(ui.item.legalName); //法人身份证
	$('#org_borrower_telephone').html(ui.item.telephone); //电话
	$('#org_borrower_address').html(ui.item.address); //地址
}
function addBorOrgDetailHtml() {
	/**
	 *  注册时间：2015年05月
	 *	社会统一代码：1111****11111
	 *	负责人：吴**
	 * 	联系电话：135888834213
	 *	公司所在地：嘉兴市桐乡市
	 */
	var detailBase =
		$(
			"<div class='form-group clearfix borrowerOrgDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>注册时间：</span>" +
			"<span class='borrowerInfo' id='org_borrower_regeditDate' oname='regeditDate'></span>" +
			"</div>" +
			"<div class='col-sm-3'>" +
			"<span>社会统一代码：</span>" +
			"<span class='borrowerInfo' id='org_borrower_organizationCode' oname='organizationCode'></span>" +
			"</div>" +
			"<div class='col-sm-3'>" +
			"<span>负责人：</span>" +
			"<span class='borrowerInfo' id='org_borrower_legalName' oname='legalName'></span>" +
			"</div>" +
			"</div>" +
			"<div class='form-group clearfix borrowerOrgDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>联系电话：</span>" +
			"<span class='borrowerInfo' id='org_borrower_telephone' oname='telephone'>" +
			"</span>" +
			"</div>" +
			"<div class='col-sm-6'>" +
			"<span>公司所在地：</span>" +
			"<span class='borrowerInfo' id='org_borrower_address' oname='address'></span>" +
			"</div>" +
			"</div>"
		)
	loadHtmlAfterDirectHtml(detailBase, 'borrower_detail');
}
/**
 * 移除动态添加html company
 */
function removeBorCompanyDetailHtml() {
	$('.borrowerCompanyDetail').remove();
}

function removeBorOrgDetailHtml() {
	$('.borrowerOrgDetail').remove();
}

/**
 * 隐藏
 */
function hiddenBorCompanyDetailHtml() {
	$('.borrowerCompanyDetail').hide();
}
/**
 * 显示
 */
function showBorCompanyDetailHtml() {
	$('.borrowerCompanyDetail').show();
}

function showBorOrgDetailHtml() {
	$('.borrowerOrgDetail').show();
}
/**
 * 动态添加html company
 */
function addBorCompanyDetailHtml() {
	/**
	 *  注册时间：2015年05月
	 *	社会统一代码：1111****11111
	 *	法人：吴**
	 * 	注册资本：300万
	 *	公司所在地：嘉兴市桐乡市
	 *	年轻销售额：2千万
	 */
	var detailBase =
		$(
			"<div class='form-group clearfix borrowerCompanyDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>注册时间：</span>" +
			"<span class='borrowerInfo' id='borrower_regeditDate' iname='regeditDate'></span>" +
			"</div>" +
			"<div class='col-sm-3'>" +
			"<span>社会统一代码：</span>" +
			"<span class='borrowerInfo' id='borrower_organizationCode' iname='organizationCode'></span>" +
			"</div>" +
			"<div class='col-sm-3'>" +
			"<span>法人：</span>" +
			"<span class='borrowerInfo' id='borrower_legalName' iname='legalName'></span>" +
			"</div>" +
			"</div>" +
			"<div class='form-group clearfix borrowerCompanyDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>注册资本：</span>" +
			"<span class='borrowerInfo' id='borrower_income' iname='income'>" +
			"</span>" +
			"</div>" +
			"<div class='col-sm-6'>" +
			"<span>公司所在地：</span>" +
			"<span class='borrowerInfo' id='borrower_address' iname='address'></span>" +
			"</div>" +
			"<div class='col-sm-3'>" +
			"<span>年度销售额：</span>" +
			"<span class='borrowerInfo' id='borrower_yearSales' iname='yearSales'></span>" +
			"</div>" +
			"</div>"
		)
	loadHtmlAfterDirectHtml(detailBase, 'borrower_detail');
}

/**
 * 填充借款企业信息
 * @param ui
 */
function fullValForBorCompanyDetail(ui) {
	$("#borrower_memberId").val(ui.item.id);
	$("#borrower_enterpriseId").val(ui.item.enterpriseId);
	$('#borrower_regeditDate').html(ui.item.regeditDate); //注册时间
	$('#borrower_organizationCode').html(ui.item.organizationCode); //社会统一代码
	$('#borrower_legalName').html(ui.item.legalName); //法人身份证
	$('#borrower_income').html(ui.item.income); //注册资本
	$('#borrower_address').html(ui.item.address); //地址
	$('#borrower_yearSales').html(ui.item.yearSales); //年度销售额
}

/**
 * 检验企业用户借款人授信额度信息
 */
function checkCompanyBorrowerCredit(ui) {
	$.post(config.urlMap.queryMemberOrEnterpriseCredit, {
		"borrowerId" : ui.item.id,
		"borrowerType" : 2
	}, function(data) {
		$(".companyBorrowerCreditCueInfo").removeClass("Validform_wrong");
		$(".companyBorrowerCreditCueInfo").html("");
		var html="";
		if (data.result != null) {
			if (data.result.onlineFlag != null && data.result.onlineFlag == 0) {
				html="该借款企业当前项目，暂停上线，请知晓！";
			} else {
				if (data.result.creditAmount != null && data.result.payablePrincipal != null) {
					if (data.result.payablePrincipal > data.result.creditAmount) {
						html="该借款企业当前存续量已经大于授信额，请知晓！";
					}
				}
			}
			if (data.result.creditLevel){
				html+="【该借款企业综合评级："+data.result.creditLevel+"："+data.result.creditLevelDes+"】";
			}else {
				html+="【该借款企业未给予综合评级，建议在完成综合评级后再添加项目】";
			}
			if (html){
				$(".companyBorrowerCreditCueInfo").addClass("borrowerCreditCueInfo Validform_wrong");
				$(".companyBorrowerCreditCueInfo").html(html);
			}
		}
	});
}


function checkOrgBorrowerCredit(ui) {
	$.post(config.urlMap.queryMemberOrEnterpriseCredit, {
		"borrowerId" : ui.item.id,
		"borrowerType" : 4
	}, function(data) {
		$(".borrowerCreditCueInfo").removeClass("Validform_wrong");
		$(".borrowerCreditCueInfo").html("");
		var html="";
		if (data.result != null) {
			if (data.result.onlineFlag != null && data.result.onlineFlag == 0) {
				html="该借款组织当前项目，暂停上线，请知晓！";
			} else {
				if (data.result.creditAmount != null && data.result.payablePrincipal != null) {
					if (data.result.payablePrincipal > data.result.creditAmount) {
						html="该借款组织当前存续量已经大于授信额，请知晓！";
					}
				}
			}
			if (data.result.creditLevel){
				html+="【该借款组织综合评级："+data.result.creditLevel+"："+data.result.creditLevelDes+"】";
			}else {
				html+="【该借款组织未给予综合评级，建议在完成综合评级后再添加项目】";
			}
			if (html){
				$(".borrowerCreditCueInfo").addClass("companyBorrowerCreditCueInfo Validform_wrong");
				$(".borrowerCreditCueInfo").html(html);
			}
		}
	});
}
//===/autoCompletedCompanyInfo=== 
 


//===autoCompletedMemberInfo=== 
/**
 * 自动补全用户信息
 */
function autoCompletedMemberInfo() {
	$("#borrower_member_name").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchMember,
				dataType: "json",
				type: 'post',
				data: {
					searchDbInforItem: request.term,
					memberType: 1
				},
				success: function(data) {
					response($.map(data, function(item) {
						return {
							mobile: item.mobile, // 手机
							memberInfo: item.memberInfo, // 详细信息
							address: item.address, // 联系地址
							sex: item.sex, // 性别
							marriage: item.marriage, // 婚姻
							highEdu: item.highEdu, // 学历
							identityNumber: item.identityNumber, // 身份证
							memberId: item.id,
							age: item.age,
							value: item.trueName + " " + item.identityNumber
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			removeBorMemberDetailHtml(); //移除动态添加的借款用户信息html
			addBorMemberDetailHtml(); //动态添加显示的借款用户信息html
			fullValForBorMemberDetail(ui); //填充借款用户数据
			checkBorrowerCredit(ui); //检验个人用户借款人授信额度信息
			return;
		}
	});
}

/**
 * 移除动态添加的借款用户信息html
 */
function removeBorMemberDetailHtml() {
	$('.borrowerMemberDetail').remove();
}

/**
 * 隐藏
 */
function hiddenBorMemberDetailHtml() {
	$('.borrowerMemberDetail').hide();
}

function hiddenBorOrgDetailHtml() {
	$('.borrowerOrgDetail').hide();
}

/**
 * 显示
 */
function showBorMemberDetailHtml() {
	$('.borrowerMemberDetail').show();
}
/**
 * 动态显示的借款用户信息html
 */
function addBorMemberDetailHtml() {
	/**
	 *  性别：男 sex
	 *	年龄：36 age
	 *	婚姻：已婚 marriage
	 *	学历：中专 high_edu
	 *	户籍地：浙江省温州市苍南县 census_register_name
	 *	身份证号：3333333****3333   identityNumber
	 *	职业：苍南县某皮具有限公司的老板     detail_info
	 * 	户口性质：农业户口  register_type
	 *	月收入：50000元/月  income
	 */
	var detailBase =
		$(
			"<div class='form-group clearfix borrowerMemberDetail'>" +
			"<div class='col-sm-2'>" +
			"<span>性别：</span>" +
			"<span class='borrowerInfo' id='borrower_sex' iname='sex'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>年龄：</span>" +
			"<span class='borrowerInfo' id='borrower_age' iname='age'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>婚姻：</span>" +
			"<span class='borrowerInfo' id='borrower_marriage' iname='marriage' ></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>学历：</span>" +
			"<span class='borrowerInfo' id='borrower_highEdu' iname='highEdu'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>户籍地：</span>" +
			"<span class='borrowerInfo' id='borrower_censusRegisterName' iname='censusRegisterName'></span>" +
			"</div>" +
			"</div>" +
			"<div class='form-group clearfix borrowerMemberDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>身份证号：</span>" +
			"<span class='borrowerInfo' id='borrower_identityNumber' iname='identityNumber'>" +
			"</span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>职业：</span>" +
			"<span class='borrowerInfo' id='borrower_detailInfo' iname='detailInfo'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>户口性质：</span>" +
			"<span class='borrowerInfo' id='borrower_registerType' iname='registerType'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>月收入：</span>" +
			"<span class='borrowerInfo' id='borrower_income' iname='income'></span>" +
			"</div>" +
			"</div>"
		)
	loadHtmlAfterDirectHtml(detailBase, 'borrower_detail');
}

/**
 * 设置借款用户信息
 */
function fullValForBorMemberDetail(ui) {
	$.post(config.urlMap.findMemberInfo, {
		"memberId": ui.item.memberId
	}, function(data) {
		if (data == null || data == "") {
			bootbox.alert("没有找到借款人数据：" + ui.item.memberId);
		} else {
			$("#borrower_memberId").val(ui.item.memberId);
			ui.item.sex = getDictLabel(config.sex, ui.item.sex); //性别
			$('#borrower_sex').html(ui.item.sex);
			$('#borrower_age').html(ui.item.age); //年龄
			$('#borrower_identityNumber').html(ui.item.identityNumber); //身份证
			if (data.result.memberInfo != "undefined" && data.result.memberInfo != null) {
				var memberInfo = data.result.memberInfo;
				memberInfo.marriage = getDictLabel(config.marriage, memberInfo.marriage);
				$('#borrower_marriage').html(memberInfo.marriage); //婚姻情况
				$('#borrower_censusRegisterName').html(memberInfo.censusRegisterName); //户籍地
				memberInfo.highEdu = getDictLabel(config.highEdu, memberInfo.highEdu);
				$('#borrower_highEdu').html(memberInfo.highEdu); //学历
				$('#borrower_detailInfo').html(memberInfo.detailInfo); //职业
				memberInfo.registerType = getDictLabel(config.registerType, memberInfo.registerType); //户口性质
				$('#borrower_registerType').html(memberInfo.registerType); //户口性质
				memberInfo.income = getDictLabel(config.income, memberInfo.income); //收入
				$('#borrower_income').html(memberInfo.income); //收入
			}
		}
	});
}

/**
 * 检验个人用户借款人授信额度信息
 */
function checkBorrowerCredit(ui) {
	$.post(config.urlMap.queryMemberOrEnterpriseCredit, {
		"borrowerId" : ui.item.memberId,
		"borrowerType" : 1
	}, function(data) {
		$(".borrowerCreditCueInfo").removeClass("Validform_wrong");
		$(".borrowerCreditCueInfo").html("");
		var html="";
		if (data.result != null) {
			if (data.result.onlineFlag != null && data.result.onlineFlag == 0) {
				html="【该借款人当前项目，暂停上线，请知晓！】";
			} else {
				if (data.result.creditAmount != null && data.result.payablePrincipal != null) {
					if (data.result.payablePrincipal > data.result.creditAmount) {
						html="【该借款人当前存续量已经大于授信额，请知晓！】";
					}
				}
			}
			if (data.result.creditLevel){
				html+="【该借款人综合评级："+data.result.creditLevel+"："+data.result.creditLevelDes+"】";
			}else {
				html+="【该借款人未给予综合评级，建议在完成综合评级后再添加项目】";
			}
			if (html){
				$(".borrowerCreditCueInfo").addClass("borrowerCreditCueInfo Validform_wrong");
				$(".borrowerCreditCueInfo").html(html);
			}
		}
	});
}
//===/autoCompletedMemberInfo===
/**
 * 用户百度评级
 */
function checkBorrowerCreditGrade(ui){
	$.post(config.urlMap.queryCreditGradeByBorrowerId, {
		"borrowerId" : ui.item.memberId
	}, function(data) {
		if (data.success) {
			if (data.result.creditLevel){
				var html=$(".borrowerCreditCueInfo").html();
				if (!html){
					$(".borrowerCreditCueInfo").addClass("borrowerCreditCueInfo Validform_wrong");
				}
				html+="【该借款人综合评级："+data.result.creditLevel+"："+data.result.creditLevelDes+"】";
				$(".borrowerCreditCueInfo").html(html);
			}else {
				var html=$(".borrowerCreditCueInfo").html();
				if (!html){
					$(".borrowerCreditCueInfo").addClass("borrowerCreditCueInfo Validform_wrong");
				}
				html+="【该借款人未给予综合评级，建议在完成综合评级后再添加项目】";
				$(".borrowerCreditCueInfo").html(html);
			}
		}
	});
}

//自绘缩略图
function jcropThumbnail() {
	$("#upload-thumbnail").Jcrop({
		onChange: jcropThumbnailCallBack,
		allowSelect: false,
		bgOpacity: 0.6,
		setSelect: [0, 0, 300, 300],
		bgColor: 'white',
		addClass: 'jcrop-light',
		aspectRatio: 1,
		minSize: [120, 120],
		onSelect: jcropThumbnailCallBack
	}, function() {
		jcrop_api = this;
	});

	function jcropThumbnailCallBack(c) {
		if (parseInt(c.w) > 0) {
			var rx = 120 / c.w;
			var ry = 120 / c.h;
			$("#thumbnail-120").css({
				width: Math.round(rx * 300) + 'px',
				height: Math.round(ry * 300) + 'px',
				marginLeft: '-' + Math.round(rx * c.x) + 'px',
				marginTop: '-' + Math.round(ry * c.y) + 'px'
			});
		}
		$('#x').val(c.x);
		$('#y').val(c.y);
		$('#w').val(c.w);
		$('#h').val(c.h);
	}
}

/**
 * 上传项目图片
 */
function updateProjectThumbnail() {
	var previewNode = document.querySelector("#template");
	var previewTemplate;
	if (typeof previewNode !== "undefined" && previewNode != null)
		previewTemplate = previewNode.parentNode.innerHTML;
	$("#upload-thumbnail-btn").dropzone({
		url: config.urlMap.uploadURL,
		thumbnailWidth: null,
		thumbnailHeight: null,
		acceptedFiles: ".jpg,.jpeg,.png",
		params: {
			category: 'project-thumbnail'
		},
		maxFilesize: 1,
		maxFiles: 1,
		previewTemplate: previewTemplate,
		previewsContainer: "#previews",
		clickable: "#upload-thumbnail-btn",
		dictDefaultMessage: "Drop files here to upload",
		dictFallbackMessage: "上传控件暂不支持您使用的浏览器。请使用Firefox4+、Chrome7+、IE10+、Opear12+、Safari6+版本以上的浏览器",
		dictFallbackText: "",
		dictFileTooBig: "101", // 文件大小超限
		dictInvalidFileType: "102", // 不支持文件类型
		dictResponseError: "103", // 服务器异常
		dictCancelUpload: "104", // 取消上传
		dictCancelUploadConfirmation: "确定要取消上传吗？",
		dictRemoveFile: "删除文件",
		dictMaxFilesExceeded: "105", // 文件上传数量超限了，不能再上传更多文件
		init: function() {
			this.on("addedfile", function(file) {
		    	  $("upload-thumbnail-btn .dz-processing").hide();
		      });
			this.on("success", function(file, data) {
				if (data.uploadStatus != 1) {
					alert("文件上传失败");
				} else {
					var previewNode = document.querySelector("#template");
					var previewTemplate;
					if (typeof previewNode !== "undefined" && previewNode != null) {
						previewTemplate = previewNode.parentNode.innerHTML;
						previewNode.parentNode.removeChild(previewNode);
					}
					var dropZoneFiles = this.files;
					this.removeFile(dropZoneFiles[0]);
					var fileInfo = data.fileInfo[0];
					var url = config.baseURL + "/" + fileInfo.filePath;
					var mockFile = {
						name: fileInfo.name,
						size: fileInfo.fileSize,
						type: 'image/jpeg',
						status: Dropzone.SUCCESS,
						url: url
					};
					$("#thumbnail").val(fileInfo.filePath);
					this.emit("addedfile", mockFile);
					this.emit("thumbnail", mockFile, url);
					$("#thumbnail-120").attr("src", url);
					jcropThumbnail();
				}
			});
			this.on("error", function(file, data) {
				dropzoneError("upload-thumbnail-btn", file, data);
			});
			this.on("processing",function(file,data){
		    	$("upload-thumbnail-btn .dz-processing").hide();
		    })
		}
	});
}

/**
 * 设置风控数据
 */
function getRiskControlData() {
	var textAreaObj = {};
	$(".risk_control textarea").each(function(i, e) {
		var inputName = $(this).attr("iname");
		var inputVal = $(this).val();
		textAreaObj[inputName] = inputVal;
	});
	$("#description").val(JSON.stringify(textAreaObj));
}

/**
 * 担保类型选择
 */
$("#direct_type").on("change", function() {
	var type = $(this).find("option:selected").val();
	//担保物类型
	showGuarantyTypeByDebtType(type);
	//查询guarantTypeThing明细
	searchGurantyThingDetail();
	/*是否显示分期radiobox*/
	isShowInstalment();
	/*是否显示租赁分红radiobox*/
	isShowJoinLease();
	
});

$("#guarantee_direct_type,#credit_direct_type,#car_leas_direct_type").on("change", function() {
	var type = $(this).find("option:selected").val();
	//设置担保物类型
	setGurantyThingType(type);
	/*是否显示分期radiobox*/
	isShowInstalment();
	//查询guarantTypeThing明细
	searchGurantyThingDetail();
	/*是否显示租赁分红radiobox*/
	isShowJoinLease();
});

/*
 * 是否委托代收
 */
$("#entrust_collection").on("change", function() {
	entrustcollection();
});

/*
 * 委托代收change
 */
function entrustcollection(){
	var type = $('#entrust_collection').is(':checked');
	if (type){
		$("#honey_machine").show();
	}else {
		$("#honey_machine").hide();
		$("select[name='channelBusiness']").val('');
	}
}

$("#instalment").on("change", function() { 
	//查询guarantTypeThing明细
	searchGurantyThingDetail();
});

//填写起投金额后，需要自动填充递增单位金额为起投金额
function autoFullIncreAmount(){
	$("#minInvestAmount").on("change",function(){
		$("#incrementAmount").val($(this).val());
	});
}
/*
 * 根据担保类型显示不同的担保物
 */
function showGuarantyTypeByDebtType(debtType, projectType) {
	var allGuaranty = ["credit_type", "guarantee_type","car_leas_type"];
	$.each(allGuaranty, function(i, n) {
		if (n == debtType + "_type") {
			$("#" + n).show();
			if (typeof projectType != 'undefined') {
				$("#" + n).find("select").val(projectType);
			}
		} else {
			$("#" + n).hide();
		}
	});
}


/**
 * 设置担保物类型
 * @param type
 */
function setGurantyThingType(type) {
	$("#input_collateralType").val(type);
}
/**
 * 获取项目类型的查询条件
 */
function getProjectTypeObj() {
	var projectType = {};
	projectType.guarantyType = $("#direct_type").val();
	projectType.guarantyThingType = $("#input_collateralType").val();
	projectType.instalment = $("#instalment").val();
	//如果是否分期隐藏，是否分期的值设置为0
	if($("#instalment_type").css("display")=="none"){
		projectType.instalment = 0;
	}
	return projectType;
}

function searchGurantyThingDetail(debtCollateral,action) {
	var projectType = getProjectTypeObj();
	//if(projectType.guarantyThingType=="consumer_inst"){
	//	$("#honey_machine").show();
	//}else{
	//	$("#honey_machine").hide();
	//}
	//$("#honey_machine").show();
	$("#guaranty_thing_detail").empty();
	if (!projectType.guarantyType || !projectType.guarantyThingType || typeof projectType.instalment == "undefined" ) {
		return;
	}
	$.post(config.urlMap.projectTypeSearch, {
			"guarantyType": projectType.guarantyType,
			"guarantyThingType": projectType.guarantyThingType,
			"instalment": projectType.instalment
		},
		function(data) {
			if (!!data && !!data.guarantyInfo) {
				var guarantyInfoJsonObj = JSON.parse(data.guarantyInfo),
					groupDivObj;
				$.each(guarantyInfoJsonObj, function(i, item) {
					if (i % 3 == 0) {
						groupDivObj = $("<div  class='form-group clearfix'></div>");
					}
					var labelName = item.info_name;
					if (item.is_not_null == 1) { //必填，且有正则表达式
						labelName = labelName +"*";
					}
					var cellObj = $("<input>"), //默认为input类型
						labelObj = $("<label class='col-sm-1 control-label no-padding-right' for='form-field-type'>" + labelName + "</label>"),
						spanValidObj = $("<span class='Validform_checktip'></span>"), //js验证框架的提示框
						divObj = $("<div class='col-sm-9'></div>"),
						validObj = {}; //datatype:校验规则 nullmsg:为空校验 errormsg:错误校验	ignore:为空是否校验 type::input的类型 onclick:onclick事件
					if (item.info_name_type == 'textarea') {
						cellObj = $("<textarea style='width:100% ;min-Height:200px'></textarea>");
					} else {
						if (item.info_name_type == 'string' || item.info_name_type == 'date' || item.info_name_type == 'int') {
							validObj.type = "text";
						}
						if (item.info_name_type == 'date') {
							validObj.onclick = "WdatePicker({isShowClear:true,readOnly:true,dateFmt:'yyyy-MM-dd'})";
						}
					}

					cellObj.attr("iname", item.info_name_code); //设置编号
					cellObj.attr("name", item.info_name); //设置名称
					if (item.is_not_null == 1 && !!item.valid_rule) { //必填，且有正则表达式
						validObj.datatype = item.valid_rule;
					} else if (item.is_not_null == 1 && !item.valid_rule) { //必填，无正则表达式
						validObj.datatype = "*";
					} else if (item.is_not_null == 0 && !!item.valid_rule) { //非必填必填，且有正则表达式
						validObj.datatype = item.valid_rule;
						validObj.ignore = "ignore";
					}
					$.each(validObj, function(n, v) { //添加属性到动态添加对象
						if (!!n) {
							cellObj.attr(n, v);
						}
					});
					divObj.append(cellObj).append(spanValidObj);
					groupDivObj.append(labelObj).append(divObj);
					if (i % 3 == 2 || i == guarantyInfoJsonObj.length-1) {
						$("#guaranty_thing_detail").append(groupDivObj);
					}
				});
				$("#guaranty_thing_detail").show();
			}
			if (typeof debtCollateral != "undefined") {
				fullCollateralDetail(debtCollateral);
			}
			
			if(typeof action != "undefined" &&  action=="detail" ){
				$('.security-detail-info').find('input,select,textarea,button,span').attr('disabled', 'disabled');
			}
		}
	);
}

function getGuarantyThingDetail() {
	var inputObj = {};
	$("#guaranty_thing_detail input,#guaranty_thing_detail textarea").each(function(i, ele) {
		var name = $(this).attr("name");
		var val = $(this).val();
		inputObj[name] = val;
	});
	$("#collateralDetails").val(JSON.stringify(inputObj));
}

//是否显示分期radiobox
function isShowInstalment() {
	var projectType = getProjectTypeObj();
	if(projectType.guarantyType== '' || projectType.guarantyType=='guarantee'){
		//$("#honey_machine").hide();
	}
	if (projectType.guarantyType == 'collateral' && projectType.guarantyThingType == 'car') {
		$("#instalment_type").show();
	} else {
		$("#instalment_type").hide();
	}
}

//是否显示分期radiobox
function isShowJoinLease() {
	var projectType = getProjectTypeObj();
	if (projectType.guarantyType == 'pledge' && projectType.guarantyThingType == 'car') {
		$("#join_lease_radio").show();
	} else {
		$("#join_lease_radio").hide();
	}
}

//选择渠道商类型，检验渠道商借款人授信额度
$("#zt_channelBusiness").on("change", function() {
	var channelBusiness = $(this).find("option:selected").val();
	
	$.post(config.urlMap.queryChannelCredit, {
		"openPlatformKey" : channelBusiness,
		"borrowerType" : 3
	}, function(data) {
		$(".channelBorrowerCreditCueInfo").removeClass("Validform_wrong");
		$(".channelBorrowerCreditCueInfo").html("");
		if (data.result != null) {
			if (data.result.onlineFlag != null && data.result.onlineFlag == 0) {
				$(".channelBorrowerCreditCueInfo").addClass("channelBorrowerCreditCueInfo Validform_wrong");
				$(".channelBorrowerCreditCueInfo").html("该渠道商当前项目，暂停上线，请知晓！");
			} else {
				if (data.result.creditAmount != null && data.result.payablePrincipal != null) {
					if (data.result.payablePrincipal > data.result.creditAmount) {
						$(".channelBorrowerCreditCueInfo").addClass("channelBorrowerCreditCueInfo Validform_wrong");
						$(".channelBorrowerCreditCueInfo").html("该渠道商当前存续量已经大于授信额，请知晓！");
					}
				}
			}
		}
	});
});

//保存项目信息
$("#save_project").on("click", function() {
	if (!preCheckForAdd()) {
		return;
	}
	getRiskControlData();
	getGuarantyThingDetail();
	$('#return_type').attr('disabled',false);
	$('#borrow_period_type').attr('disabled',false);
	var saveBtn = $(this);
	if (directProjectForm.check(false)) {
		$(this).attr("disabled", "disabled");
		$('#direct_project_form').xform('post', config.urlMap.save, function(data) {
			if (!data.success) {
				if (!!data.resultCodeEum) {
					bootbox.alert(data.resultCodeEum[0].msg, function() {
						saveBtn.removeAttr("disabled");
					});
				} else {
					bootbox.alert("添加直投项目失败!", function() {
						saveBtn.removeAttr("disabled");
					});
				}

			} else {
				bootbox.alert("添加直投项目成功!", function() {
					directProjectTable.fnDraw();
						window.top.closeActiveIframe();
					saveBtn.removeAttr("disabled");
				});
			}
		});
	}
});

//编辑项目信息
$("#edit_project").on("click", function() {
	if (!preCheckForAdd()) {
		return;
	}
	var introducerMobile = $.trim($('#introducerMobile').val());
	if(!introducerMobile){
		$('#introducerId').val("");
	}
	
	getRiskControlData();
	getGuarantyThingDetail();
	var editBtn = $(this);
	if (directProjectForm.check(false)) {
		$(this).attr("disabled", "disabled");
		$('#direct_project_form').xform('post', config.urlMap.update, function(data) {
			if (!data.success) {
				if (!!data.resultCodeEum) {
					bootbox.alert(data.resultCodeEum[0].msg, function() {
						editBtn.removeAttr("disabled");
					});
				} else {
					bootbox.alert("更新直投项目失败!", function() {
						editBtn.removeAttr("disabled");
					});
				}

			} else {
				bootbox.alert("更新直投项目成功!", function() {
					directProjectTable.fnDraw();
						window.top.closeActiveIframe();
					editBtn.removeAttr("disabled");
				});
			}
		});
	}
});

//编辑,紧急修改（全部），紧急修改，查看
$("#direct-project-table").on("click", ".dir-prj-edit,.dir-prj-emgall,.dir-prj-emgpart,.dir-prj-detail", function() {
	var param = $(this).data("param"),
		className = $(this).data("class"),
		val = $(this).html();
	if (!!param.url) {
		window.top.setIframeBox(className + param.id, param.url + param.id, param.name + "项目_" + val);
	}
});


/**
 * 添加的前置校验
 * @returns {Boolean}
 */
function preCheckForAdd() {
	var thumbnail = $("#thumbnail").val();
	if (thumbnail == "" || thumbnail.length < 1) {
		bootbox.alert("请上传项目缩略图");
		return false;
	}
	var signImage = $("#j-json-dropzone_direct_project_sign").val();
	if (!signImage) {
		bootbox.alert("请上传项目形象图");
		return false;
	}
	var borrowPeriodType = $("#borrow_period_type").val(),
		returnType = $("#return_type").val();
	if (borrowPeriodType == "1" && returnType == "avg_principal") {
		bootbox.alert("借款周期为日不支持等本等息还款方式");
		//bootbox.alert("等本等息项目的借款周期类型不能为天");
		return false;
	}
	
	var minAmount = Number($("#minInvestAmount").val()),
	incrementAmount = Number($("#incrementAmount").val()),
	totalAmount =Number($("#totalAmount").val());
	if(minAmount % incrementAmount!=0){
		bootbox.alert("起投金额必须是递增金额倍数！");
		return false;
	}
	if(minAmount > totalAmount){
		bootbox.alert("起投金额必须小于等于借款总额！");
		return false;
	}
	if(totalAmount % incrementAmount){
		bootbox.alert("借款总额必须为递增金额的倍数！");
		return false;
	}
	var introducerFeeRate = $('#introducerFeeRate').val();
	if(introducerFeeRate){
		var reg = /^(?!0+(?:\.0+)?$)(?:[1-9]\d{0,2}|0)(?:\.\d{1,2})?$/; 
	    //console.log (reg.test (introducerFeeRate));
		if(!reg.test(introducerFeeRate)){
			bootbox.alert("介绍费率，请输入最多两位小数！");
			return false;
		}
	}
	var introducerFeeRate = $('#introducerFeeRate').val();
	if(introducerFeeRate){
		var introducerMobile = $('#introducerMobile').val();
		if(!introducerMobile){
			bootbox.alert("您填写了介绍费率，要记得填写介绍人手机号呦！");
			return false;
		}
	}
	
	var introducerMobile = $('#introducerMobile').val();
	var introducerId = $('#introducerId').val();
	if(introducerMobile){
		if(!introducerId){
			bootbox.alert("请关联有效的介绍人手机号！");
			return false;
		}
	}
	
	return true;
}

//获取直投项目详情
function findDetail(id, action) {
	$("#direct_project_form").xform('load', config.urlMap.find + "id=" + id,
		function(data) {
			//缩略图的显示
			var thumbnail = data.thumbnail;
			if (thumbnail != "" && thumbnail != null) {
				$("#upload-project-thumbnail").hide();
				$("#upload-thumbnail-btn").hide();
				$("#thumbnail-120").attr("src", config.picURLHead + thumbnail);
				$("#project-thumbnail").attr("src", config.picURLHead + thumbnail);
				$("#project-thumbnail-pane").show();
			}
			//借款人详细显示
			renderBorrowerDetail(data);
			//介绍人详细显示
			renderIntroducerDetail(data);
			//担保物信息的显示	
			renderSecurityDetail(data,action);
			//风控信息的显示
			renderRiskControl(data.description);
			//附件信息的显示
			renderAttachmentDetail(data.bscAttachments)
			showTransferFlag(data.transferFlag);
			showCatalyzerFlag(data.catalyzerFlag);
			showAddRateFlag(data.addRateFlag);
			if(action=="detail"){
				$('#direct_project_form').find('input,select,textarea,button,span').attr('disabled', 'disabled');
				disableDropzone();//图片操作禁止
				$(".dz-input-data").attr('disabled', 'disabled');
			}
			if(action=="emergencyPart"){
				$(".delAllImage").removeClass("hidden");
				$('#direct_project_form').find('input,select,textarea,button,span').attr('disabled', 'disabled');//禁用所有操作
				disableDropzoneById("#j-json-dropzone_direct_project_sign");//项目形象图禁止修改
				$('.j-input-dropzone').removeAttr('disabled');
				
				$('#direct_project_form').find('#shortDesc').removeAttr('disabled');//项目介绍可用可编辑
				$('#guaranty_thing_detail').find('input,textarea').removeAttr('disabled');//抵质押物类型和担保方式里的具体信息可编辑
				$('.risk_control').find("textarea").removeAttr('disabled');
				//设置 添加图片按钮存在的 图片控件按钮和输入框可用
				$('.dropzoneImage').each(function(i){
					if($(this).prev('.dz-upload-btn:visible').length>=1){
						setActiveDropzoneBtn($(this));
					}
				});
				//action,id,description,collateralDetails,collateralId 不能设置为disabled
				$('#direct_project_form').find('.emergency-modify').removeAttr("disabled");
				$(".delAllImage").removeAttr("disabled");
				$("#del-dropzone_direct_project_sign").hide();
			}
			
			var selectedTransferFlag = $('#select_transferFlag').val();
			if (selectedTransferFlag == $("#transferFlag").get(0).options[0].value) {
				$("#transferFlag").get(0).options[0].selected = true;
			} else {
				$("#transferFlag").get(0).options[1].selected = true;
			}
			
			var selectedIsNovice = $('#select_isNovice').val();
			if (selectedIsNovice == $(".is_novice").get(0).options[0].value) {
				$(".is_novice").get(0).options[0].selected = true;
			} else {
				$(".is_novice").get(0).options[1].selected = true;
			}
			
			// 可转让，新手禁止
			var transferFlag = $('#transferFlag').children('option:selected').val();
			if (transferFlag == 1) {
				$(".is_novice").attr("disabled","disabled");
			}
			// 新手，转让禁止
			var isNovice = $('.is_novice').children('option:selected').val();
			if (isNovice == 0) {
				$("#transferFlag").attr("disabled","disabled");
			}
			
	});
}

//对外项目创建直投项目
function findOpenDetail(id, action) {
	$("#direct_project_form").xform('load', config.urlMap.findOpen + "openid=" + id,
		function(data) {
			//缩略图的显示
			var thumbnail = data.thumbnail;
			if (thumbnail != "" && thumbnail != null) {
				$("#upload-project-thumbnail").hide();
				$("#upload-thumbnail-btn").hide();
				if (thumbnail.indexOf("open/upload")<0){
					$("#thumbnail-120").attr("src", config.picURLHead + thumbnail);
					$("#project-thumbnail").attr("src", config.picURLHead + thumbnail);
				}else {
					$("#thumbnail-120").attr("src", config.baseURL+"/" + thumbnail);
					$("#project-thumbnail").attr("src", config.baseURL+"/" + thumbnail);
				}
				$("#project-thumbnail-pane").show();
			}
			//借款人详细显示
			renderBorrowerDetail(data);
			//$('#totalAmount').attr('readonly',true);
			//$("input[name='annualizedRate']").attr('readonly',true);
			//$('#return_type').attr('disabled',true);
			//$("input[name='borrowPeriod']").attr('readonly',true);
			//$('#borrow_period_type').attr('disabled',true);
			//介绍人详细显示
			renderIntroducerDetail(data);
			//担保物信息的显示
			renderSecurityDetail(data,action);
			//风控信息的显示
			var desc='{"fk_bzcs":"杭州早**有限公司提供连带担保责任。",'+
				'"fk_hkly":"第一还款来源是借款人还款；第二还款来源是押金返还；第三还款来源是担保公司还款。",'+
				'"fk_fksh":"经过严格的准入条件和全面的实地尽职调查，该借款项目真实可靠，相关证照齐全，相关手续已落实，风险可控，经风控中心审议决定，该笔借款项目审核通过。",'+
				'"fk_jgbz":"该项目用户租赁及服务协议已签署；杭州早**有限公司担保函已签署。",'+
				'"fk_fljg":"专业律师对平台模式及债权形成流程进行严格法律风险把控，并提供法律保障。",'+
				'"fk_zjgl":"投资资金由新浪支付进行资金托管，实行专人专户、专款专用，实现实时监管、资金零挪用。",'+
				'"fk_qtfkxx":""}';
			renderRiskControl(desc);
			//附件信息的显示
			renderAttachmentDetail(data.bscAttachments)
			showTransferFlag(data.transferFlag);
			if(action=="detail"){
				$('#direct_project_form').find('input,select,textarea,button,span').attr('disabled', 'disabled');
				disableDropzone();//图片操作禁止
				$(".dz-input-data").attr('disabled', 'disabled');
			}
			if(action=="emergencyPart"){
				$(".delAllImage").removeClass("hidden");
				$('#direct_project_form').find('input,select,textarea,button,span').attr('disabled', 'disabled');//禁用所有操作
				disableDropzoneById("#j-json-dropzone_direct_project_sign");//项目形象图禁止修改
				$('.j-input-dropzone').removeAttr('disabled');

				$('#direct_project_form').find('#shortDesc').removeAttr('disabled');//项目介绍可用可编辑
				$('#guaranty_thing_detail').find('input,textarea').removeAttr('disabled');//抵质押物类型和担保方式里的具体信息可编辑
				$('.risk_control').find("textarea").removeAttr('disabled');
				//设置 添加图片按钮存在的 图片控件按钮和输入框可用
				$('.dropzoneImage').each(function(i){
					if($(this).prev('.dz-upload-btn:visible').length>=1){
						setActiveDropzoneBtn($(this));
					}
				});
				//action,id,description,collateralDetails,collateralId 不能设置为disabled
				$('#direct_project_form').find('.emergency-modify').removeAttr("disabled");
				$(".delAllImage").removeAttr("disabled");
				$("#del-dropzone_direct_project_sign").hide();
			}
		});
}

//设置图片的按钮和输入框可用
function setActiveDropzoneBtn(obj){
	obj.prev('.dz-upload-btn').removeAttr('disabled');
	obj.find('.dz-input-data').removeAttr('disabled');
}

function renderBorrowerDetail(data) {
	if (!!data.borrowerMemberBaseBiz) {
		var borrower = data.borrowerMemberBaseBiz;
		if (!!data.borrowerType) {
			showBorrowerType(String(data.borrowerType));
			if(!data.borrowerMemberBaseBiz){
				return;
			}
			if (data.borrowerType == 1) {
				addBorMemberDetailHtml();
				var member = data.borrowerMemberBaseBiz.member;
				//uc_member
				if(!!member){
					$.each(member, function(n, v) {
						if (n == 'sex') {
							v = getDictLabel(config.sex, v); //性别
						}
						$("input[iname='" + n + "']:visible").val(v);
						$("span[iname='" + n + "']:visible").html(v);
						if (n == 'id') {
							$("#borrower_memberId").val(v);
						}
						if (n == 'trueName') {
							$("#borrower_member_name").val(v + " " + member.identityNumber);
						}
					});
				}
				//uc_member_info
				var memberInfo = data.borrowerMemberBaseBiz.memberInfo;
				if(!!memberInfo){
					$.each(memberInfo, function(n, v) {
						if (n == 'highEdu') {
							v = getDictLabel(config.highEdu, v); //学历
						} else if (n == 'registerType') {
							v = getDictLabel(config.registerType, v); //户口性质
						} else if (n == 'income') {
							v = getDictLabel(config.income, v); //收入
						} else if (n == 'marriage') {
							v = getDictLabel(config.marriage, v); //收入
						}
						
						$("input[iname='" + n + "']:visible").val(v);
						$("span[iname='" + n + "']:visible").html(v);
						
					});
				}
			} else if (data.borrowerType == 2) {
				addBorCompanyDetailHtml();
				var enterprise = data.enterprise;
				if(!!enterprise){
					$.each(enterprise, function(n, v) {
						if (n == 'regeditDate') {
							v = formatDate(v);
						}
						$("input[iname='" + n + "']:visible").val(v);
						$("span[iname='" + n + "']:visible").html(v);
						if (n == 'name') {
							$("input[iname='name']:visible").val(v + " " + enterprise.legalName);
						}
					});
				}
			}else if(data.borrowerType == 4){
				addBorOrgDetailHtml();
				var enterprise = data.enterprise;
				if(!!enterprise){
					$.each(enterprise, function(n, v) {
						if (n == 'regeditDate') {
							v = formatDate(v);
						}
						$("input[oname='" + n + "']:visible").val(v);
						$("span[oname='" + n + "']:visible").html(v);
						/*if (n == 'regisNo') {
							$("input[iname='regisNo']:visible").val(v + " " + enterprise.regisNo);
						}*/
					});
				}
			}
		}
	}
}

function renderIntroducerDetail(data) {
	if (!!data.introducerMemberBaseBiz) {
		var introducer = data.introducerMemberBaseBiz;
		$("#introducerId").val(introducer.member.id);
		$("#introducerMobile").val(introducer.member.trueName + " " + introducer.member.mobile );
		
	}
}

function renderSecurityDetail(data,action) {
	if(typeof data.debtCollateral != "undefined" && data.debtCollateral !=null){
		$("#collateralId").val(data.debtCollateral.id);
	}
	if (data.channelBusiness){
		$('#entrust_collection').attr('checked',true);
		$("#honey_machine").show();
		$("select[name='channelBusiness']").val(data.channelBusiness);
	}
	showGuarantyTypeByDebtType(data.securityType, data.projectType);
	/*是否显示分期radiobox*/
	isShowInstalment();
	//查询guarantTypeThing明细
	searchGurantyThingDetail(data.debtCollateral,action);
	/*是否显示租赁分红radiobox*/
	isShowJoinLease();

}

function renderRiskControl(riskControlInfo) {
	console.info(riskControlInfo);
	if (!!riskControlInfo) {
		$.each(JSON.parse(riskControlInfo), function(n, v) {
			$("textarea[iname='" + n + "'").html(v);
		});
	}
}

function renderAttachmentDetail(bscAttachments) {
	if (bscAttachments != "" && bscAttachments != null) {
		var dropID = "";
		var customDropzone;
		$.each(bscAttachments, function(n, v) {
			if (v.module!="thumbnail"){
				var module = v.module;
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

function fullCollateralDetail(debtCollateral) {
	if (!!debtCollateral) {
		//console.info(debtCollateral.collateralDetails);
		//console.info(JSON.parse(debtCollateral.collateralDetails));
		$.each(JSON.parse(debtCollateral.collateralDetails), function(n, v) {
			$("#guaranty_thing_detail").find("input[name='" + n + "']").val(v);
			$("textarea[name='" + n + "'").html(v);
		});
	}
}

//关闭页面
$(".closePage").click(function(){
	   window.top.closeActiveIframe("直投项目管理");
})
//恢复项目状态到投资中
$("#direct-project-table").on("click", ".dir-prj-start",function(){
	var param = $(this).data("param");
	startProject(param.id,param.url);
});
//项目审核
$("#direct-project-table").on("click", ".dir-prj-stop",function(){
	var param = $(this).data("param");
	stopProject(param.id,param.url);
});
//项目审核
$("#direct-project-table").on("click", ".dir-prj-review",function(){
	var param = $(this).data("param");
	reviewProject(param.id,param.url);
});
//提交待审
$("#direct-project-table").on("click", ".dir-prj-waitReview",function(){
	var param = $(this).data("param");
	waitProject(param.id,param.url);
});
//风控审核
$("#direct-project-table").on("click", ".dir-prj-riskReview",function(){
	var param = $(this).data("param");
	riskReviewProject(param.id,param.url);
});

//删除项目
$("#direct-project-table").on("click", ".dir-prj-del",function(){
	var param = $(this).data("param");
	deleteProject(param.id,param.url);
});
//修改时间  待审核、待发布，修改上线时间和销售截止时间
$("#direct-project-table").on("click", ".dir-prj-uotTime",function(){
	var param = $(this).data("param");
	modifyOnlineTimeProject(param.id,param.onlineTime,param.saleEndTime,param.startDate,param.url);
});
//修改时间 修改销售截止时间
$("#direct-project-table").on("click", ".dir-prj-uEndTime",function(){
	var param = $(this).data("param");
	modifyEndDateProject(param.id,param.saleEndTime,param.url);
});
//流标
$("#direct-project-table").on("click", ".dir-prj-lose",function(){
	var param = $(this).data("param");
	loseProject(param.id,param.url);
});
$("#direct-project-table").on("click", ".dir-prj-delProjectRedis",function(){
	var param = $(this).data("param");
	$.ajax({
		url:config.urlMap.delProjectRedis+"?projectId="+param.id,
		type:"post", 
		dataType:'json',
		success:function(data) {
			if(data.success){
				bootbox.alert("清除项目缓存成功！");
			}else{
				bootbox.alert("清除项目缓存失败！");
			}
		}
	});
});

$("#direct-project-table").on("click", '.dir-prj-addremark', function() {
	var param = $(this).data("param");
	var id = param.id;
	var controlRemarks =  param.remark;
	$('#controlRemarksId').val(id);
	if(controlRemarks == 'null') {
		$('#newControlRemarks').val('');
	} else {
		$('#newControlRemarks').val(controlRemarks);
	}
	$('#modal-controlRemarks').modal('show');
});
//添加备注，发送后台
$("#btn_add_remarks").on("click",function(){
	$(this).addClass("disabled");
	var that = $(this);
	var id=$('#controlRemarksId').val();
	var newControlRemarks= $("#newControlRemarks").val();
	$.post(
		config.urlMap.addRemarks,{id:id,remarks:newControlRemarks},function(data){
			that.removeClass("disabled");
			if(data.success){
				$('#modal-controlRemarks').modal('hide');
				directProjectTable.fnDraw();
			}else{
				bootbox.alert("备注添加失败！");
			}
		}
	);
});

/**
 * 还款记录
 */
$("#direct-project-table").on("click", '.dir-prj-payRecord', function() {
	var param = $(this).data("param");
	showPayRecord(param);
});
//本息记录
function showPayRecord(param) {
	var interestTable = $('#record-table-1').dataTable({
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
		'sAjaxSource' : param.url+"?id="+param.id,
		'aoColumns' : [
			{
				'mDataProp': 'interestId',
				'bSortable': false
			}, {
				'mDataProp': 'endDate',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var timeD="";
					if(row.startDate != null) {
						timeD= formatDate(row.startDate,"yyyy-mm-dd")+"~";
					}
					if(row.endDate != null){
						timeD=timeD+ formatDate(row.endDate,"yyyy-mm-dd");
					}
					return timeD;
				}
			}, {
				'mDataProp': 'endDate',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endDate != null) {
						return formatDate(row.endDate,"yyyy-mm-dd");
					}
				}
			}, {
				'mDataProp': 'payableInterestFor',
				'bSortable': false
				
				
			}, {
				'mDataProp': 'payablePrincipalFor',
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
	interestTable.fnSettings().sAjaxSource=param.url+"?id="+param.id;
	interestTable.fnDraw();
	$('#pay-modal-table').modal('show');
	showProjectInfo(param);
	
}
//项目基本信息
function showProjectInfo(param){
	if(!!param){
		if(typeof(param.totalAmount) != "undefined") {
			$('#project_amount').html(param.totalAmount);
		} else {
			$('#project_amount').html("");
		}
		if(typeof(param.id) != "undefined") {
			$('#project_number').html(param.id);
		} else {
			$('#project_number').html("");
		}
		if(typeof(param.annualizedRate) != "undefined") {
			$('#project_annualizedRate').html(param.annualizedRate);
		} else {
			$('#project_annualizedRate').html("");
		}
		if(!!param.profitType){
			$('#pay_type').html(getDictLabel(config.returnType, param.profitType));
		}
		if(!!param.borrowerMemberBaseBiz){
			if(param.borrowerMemberBaseBiz.member == null) {
				$('#project_borrow').html("");
			}else{
				$('#project_borrow').html(param.borrowerMemberBaseBiz.member.trueName == null ? "" : param.borrowerMemberBaseBiz.member.trueName);
			}
		}
		if(typeof(param.startDate) != "undefined") {
			var dataD="";
			if(!!param.startDate){
				dataD=formatDate(param.startDate,"yyyy-mm-dd")+"~";
			}
			if(!!param.endDate){
				dataD=dataD+ formatDate(param.endDate,"yyyy-mm-dd");
			}
			$('#project_time').html(dataD);
		} else {
			$('#project_time').html("");
		}
	}
}
/**
 * 修改项目发布和项目到期时间
 * @param id
 */
function modifyOnlineTimeProject(id,onlineTime, saleEndTime,startDate,url){
	boxDialog2("修改时间",formatDate(onlineTime,'yyyy-mm-dd HH:mm'), formatDate(saleEndTime,'yyyy-mm-dd HH:mm'),function(){
		 var message = $('#message').val();
		 var onlineTime = $('#onlineTime').val();
		 var saleEndTime = $('#saleEndTime').val();
		 if(onlineTime == "" || saleEndTime == ""){
			 bootbox.alert("数据不能为空哦");
			 return;
		 }
		 $.post(url,{id:id,onlineTime:onlineTime,saleEndTime:saleEndTime},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						directProjectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}
/**
 * 修改截止时间
 * @param id
 * @param endDate
 */
function modifyEndDateProject(id, saleEndTime,url){
	boxDialog3("修改时间",formatDate(saleEndTime,'yyyy-mm-dd HH:mm'),function(){
		 var message = $('#message').val();		 
		 var saleEndTime = $('#saleEndTime').val();
		 if(saleEndTime == ""){
			 bootbox.alert("数据不能为空哦");
			 return;
		 }
		 $.post(url,{id:id,saleEndTime:saleEndTime},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						directProjectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}

/**
 * 删除项目
 * @param id
 */
function deleteProject(id,url){
	bootbox.confirm("确定要删除项目："+id+"吗？",function(result){
		if(result){
			$.post(url,{id:id},function(data){
				if(data.success){
					bootbox.alert("成功删除项目",function(){
						directProjectTable.fnDraw();
					})
				}else{
					bootbox.alert("项目删除失败，请稍后重试！");
				}
			})
		}
	})
}

/**
 * 提交待审
 * @param id
 */
function waitProject(id,url){
	bootbox.confirm("确定把项目："+id+"提交审核吗？",function(result){
		if(result){
			$.post(url,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						directProjectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
		}
	})
}
/**
 * 项目上线审核
 * @param id
 */
function reviewProject(id,url){
	boxDialog("项目上线审核",true,false,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(url,{id:id,message:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						directProjectTable.fnDraw();
					})
				}else{
					if(data.resultCodeEum != null && data.resultCodeEum.length>0){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("操作失败，请稍后重试！");
					}
				}
			})
	})
}
/**
 * 暂停项目
 * @param id
 */
function stopProject(id,url){
	boxDialog("项目暂停",false,false,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(url,{id:id,msg:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						directProjectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}

/**
 * 恢复项目状态到投资中
 * @param id
 */
function startProject(id,url){
	boxDialog("恢复项目状态到募集中",false,false,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(url,{id:id,msg:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						directProjectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}

/**
 * 风控审核
 * @param id
 */
function riskReviewProject(id,url){
	boxDialog("风控审核",true,true,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 boxDialogForm = $(".form-horizontal").Validform({
			 tiptype: 4,
			 ignoreHidden: true,
			 showAllError: true,
			 btnReset: ".btnReset",
			 ajaxPost: false
		 });
		 if(!boxDialogForm.check(false)){
			 return false;
		 }
		 $.post(url,{id:id,msg:message,radioStatus:radioStatus},function(data){
			 if(data.success){
				 bootbox.alert("操作成功。",function(){
					 directProjectTable.fnDraw();
				 })
			 }else{
				 if(data.resultCodeEum != null && data.resultCodeEum.length>0){
					 bootbox.alert(data.resultCodeEum[0].msg);
				 }else{
					 bootbox.alert("操作失败，请稍后重试！");
				 }
			 }
		 });
	})
}

/**
 * 提交待审
 * @param id
 */
function loseProject(id,url){
	bootbox.confirm("项目："+id+"确定需要流标吗？",function(result){
		if(result){
			$.post(url,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						directProjectTable.fnDraw();
					})
				}else{
					if(data.getResultCodeEum.length>0){
						bootbox.alert(data.getResultCodeEum[0].getMsg());
					}else{
						bootbox.alert("操作失败，请稍后重试！");
					}
				}
			})
		}
	})
}

function boxDialog(title, isRadio,isNotNull, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage(isRadio,isNotNull),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxDialog2(title,onlineTime, saleEndTime, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage2(onlineTime, saleEndTime),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxDialog3(title, saleEndTime, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage3(saleEndTime),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}
function boxMessage(isRadio,isNotNull){
	if(isNotNull){
		var datatype = "*";
	}
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>请填写操作原因：</label>"+
    "<div class='col-md-4'><textarea id='message' name='message' type='text' datatype="+datatype+" placeholder='请填写操作原因' class='form-control input-md' style='width: 341px; height: 89px;'></textarea></div></div>";
    if(typeof isRadio !== "undefined" && isRadio){
    	 html+="<div class='form-group'><label class='col-md-4 control-label' for='radioStatus'>动作</label> ";
    	 html+="<div class='col-md-4'><div class='radio'><label for='radioStatus-1'><input type='radio' name='radioStatus' id='radioStatus-1' value='1' checked='checked'>通过 </label></div>";
    	 html+="<div class='radio'><label for='radioStatus-0'><input type='radio' name='radioStatus' id='radioStatus-0' value='0'> 不通过 </label></div></div></div>";
    }
    html+="</form></div></div>";
	return html;
}

function getPayRecord(id){
	var payRecordTable = $('#record-table-1').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : false,
		"bRetrieve": true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.getPayRecord+"?id="+id,
		'aoColumns' : [
			{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp' : 'id'
			}, {
				'mDataProp' : 'cardNumber',
				'bSortable' : false
			}, {
				'mDataProp' : 'cardHolder',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankCode',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankProvince',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankCity',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankDistrict',
				'bSortable' : false
			}
		]
	});
	payRecordTable.fnDraw();
}

function boxMessage2(onlineTime, saleEndTime){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name' >募集开始时间：</label>"+
    "<div class='col-md-4'><input type='text' value='"+onlineTime+"' id='onlineTime' onchange='onlineTimechange(this)' onclick='getDatePicker()'></div></div>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>募集结束时间：</label>"+
    "<div class='col-md-4'><input type='text' value='"+saleEndTime+"' id='saleEndTime' onclick='getDatePickerMinDate()'/></div></div>";
	 html+="</form></div></div>";
	return html;
}

function onlineTimechange(e){
	var newdate= jQuery(e).val();
	if (newdate){
		var oDate1 = new Date(newdate);
		var  ddd= oDate1.getTime()+7*24*60*60*1000;
		ddd=new Date(ddd);
		var lastmonth=parseInt(ddd.getMonth())+1;
		var lastenddate=ddd.getFullYear()+'-'+lastmonth+'-'+ddd.getDate()+' '+ddd.getHours()+':'+ddd.getMinutes();
		jQuery('#saleEndTime').val(lastenddate);
		return;
	}
	jQuery('#saleEndTime').val('');
}

function boxMessage3(saleEndTime){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>募集结束时间：</label>"+
    "<div class='col-md-4'><input type='text' value='"+saleEndTime+"' id='saleEndTime' onclick='getDatePicker()'/></div></div>";
	 html+="</form></div></div>";
	return html;
}

function getDatePicker(){
	WdatePicker(
		{
			isShowClear:true,
			readOnly:true,
			dateFmt:'yyyy-MM-dd HH:mm'
		}
	);
}

function getDatePickerMinDate(id){
	WdatePicker({isShowClear:true,readOnly:true,minDate:'#F{$dp.$D(\'onlineTime\')}',dateFmt:'yyyy-MM-dd HH:mm'});
}

function searchDirectProjectList(){
	$("#query_direct_project").on("click",function(){
		directProjectTable.fnDraw();
	});
}
$('.delAllImage').on('click', function() {
	var dropzoneID =$(this).attr("id");
	var imgID="";
	if(dropzoneID.indexOf("del-")!=-1){
		imgID=dropzoneID.substring(4,dropzoneID.length);
	}
	bootbox.confirm("你确定要删除吗?", function(result) {
		if (result) {
			deleteDropzoneAllimage(imgID);
			checkId(imgID);
		}
	});
});

function checkId(dropzoneID){
	
	if(dropzoneID.indexOf("mosaic")>=0){
		if(dropzoneID.indexOf("dropzone_direct_project_borrower_mosaic")>=0){
			$("#j-json-dropzone_direct_project_borrower_mosaic").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_collateral_mosaic")>=0){
			$("#j-json-dropzone_direct_project_collateral_mosaic").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_contract_mosaic")>=0){
			$("#j-json-dropzone_direct_project_contract_mosaic").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_legal_mosaic")>=0){
			$("#j-json-dropzone_direct_project_legal_mosaic").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_credit_mosaic")>=0){
			$("#j-json-dropzone_direct_project_credit_mosaic").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_base_mosaic")>=0){
			$("#j-json-dropzone_direct_project_base_mosaic").val("");
		}
	}else{
		if(dropzoneID.indexOf("dropzone_direct_project_borrowe")>=0){
			$("#j-json-dropzone_direct_project_borrower").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_collateral")>=0){
			$("#j-json-dropzone_direct_project_collateral").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_contrac")>=0){
			$("#j-json-dropzone_direct_project_contract").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_legal")>=0){
			$("#j-json-dropzone_direct_project_legal").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_base")>=0){
			$("#j-json-dropzone_direct_project_base").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_sign")>=0){
			$("#j-json-dropzone_direct_project_sign").val("");
		}else if(dropzoneID.indexOf("dropzone_direct_project_credit")>=0){
			$("#j-json-dropzone_direct_project_credit").val("");
		}
	}
	
}

function autoCompletedIntroducerInfo() {
	$("#introducerMobile").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchMemberByMobile,
				dataType: "json",
				type: 'post',
				data: {
					mobile: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						return {
							mobile: item.mobile, // 手机
							memberInfo: item.memberInfo, // 详细信息
							address: item.address, // 联系地址
							sex: item.sex, // 性别
							marriage: item.marriage, // 婚姻
							highEdu: item.highEdu, // 学历
							identityNumber: item.identityNumber, // 身份证
							memberId: item.id,
							age: item.age,
							value: item.trueName + " " + item.mobile
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			removeIntroducerDetailHtml(); //移除动态添加的借款用户信息html
			//addIntroducerDetailHtml(); //动态添加显示的借款用户信息html
			fullValForIntroducerDetail(ui); //填充借款用户数据
			return;
		}
	});
}

/**
 * 移除动态添加的借款用户信息html
 */
function removeIntroducerDetailHtml() {
	$('.introducerDetail').remove();
}

/**
 * 隐藏
 */
function hiddenIntroducerDetailHtml() {
	$('.introducerDetail').hide();
}

/**
 * 显示
 */
function showIntroducerDetailHtml() {
	$('.introducerDetail').show();
}

/**
 * 动态显示的借款用户信息html
 */
function addIntroducerDetailHtml() {
	/**
	 *  性别：男 sex
	 *	年龄：36 age
	 *	婚姻：已婚 marriage
	 *	学历：中专 high_edu
	 *	户籍地：浙江省温州市苍南县 census_register_name
	 *	身份证号：3333333****3333   identityNumber
	 *	职业：苍南县某皮具有限公司的老板     detail_info
	 * 	户口性质：农业户口  register_type
	 *	月收入：50000元/月  income
	 */
	var detailBase =
		$(
			"<div class='form-group clearfix introducerDetail'>" +
			"<div class='col-sm-2'>" +
			"<span>性别：</span>" +
			"<span class='borrowerInfo' id='introducer_sex' iname='sex'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>年龄：</span>" +
			"<span class='borrowerInfo' id='introducer_age' iname='age'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>婚姻：</span>" +
			"<span class='borrowerInfo' id='introducer_marriage' iname='marriage' ></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>学历：</span>" +
			"<span class='borrowerInfo' id='introducer_highEdu' iname='highEdu'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>户籍地：</span>" +
			"<span class='borrowerInfo' id='introducer_censusRegisterName' iname='censusRegisterName'></span>" +
			"</div>" +
			"</div>" +
			"<div class='form-group clearfix introducerDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>身份证号：</span>" +
			"<span class='borrowerInfo' id='introducer_identityNumber' iname='identityNumber'>" +
			"</span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>职业：</span>" +
			"<span class='borrowerInfo' id='introducer_detailInfo' iname='detailInfo'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>户口性质：</span>" +
			"<span class='borrowerInfo' id='introducer_registerType' iname='registerType'></span>" +
			"</div>" +
			"<div class='col-sm-2'>" +
			"<span>月收入：</span>" +
			"<span class='borrowerInfo' id='introducer_income' iname='income'></span>" +
			"</div>" +
			"</div>"
		)
	loadHtmlAfterDirectHtml(detailBase, 'introducer_detail');
}

/**
 * 设置借款用户信息
 */
function fullValForIntroducerDetail(ui) {
	$.post(config.urlMap.findMemberInfo, {
		"memberId": ui.item.memberId
	}, function(data) {
		if (data == null || data == "") {
			bootbox.alert("没有找到借款人数据：" + ui.item.memberId);
		} else {
			$("#introducerId").val(ui.item.memberId);
			/*ui.item.sex = getDictLabel(config.sex, ui.item.sex); //性别
			$('#introducer_sex').html(ui.item.sex);
			$('#introducer_age').html(ui.item.age); //年龄
			$('#introducer_identityNumber').html(ui.item.identityNumber); //身份证
			if (data.result.memberInfo != "undefined" && data.result.memberInfo != null) {
				var memberInfo = data.result.memberInfo;
				memberInfo.marriage = getDictLabel(config.marriage, memberInfo.marriage);
				$('#introducer_marriage').html(memberInfo.marriage); //婚姻情况
				$('#introducer_censusRegisterName').html(memberInfo.censusRegisterName); //户籍地
				memberInfo.highEdu = getDictLabel(config.highEdu, memberInfo.highEdu);
				$('#introducer_highEdu').html(memberInfo.highEdu); //学历
				$('#introducer_detailInfo').html(memberInfo.detailInfo); //职业
				memberInfo.registerType = getDictLabel(config.registerType, memberInfo.registerType); //户口性质
				$('#introducer_registerType').html(memberInfo.registerType); //户口性质
				memberInfo.income = getDictLabel(config.income, memberInfo.income); //收入
				$('#introducer_income').html(memberInfo.income); //收入
			}*/
		}
	});
}

/**
 *项目是否可以转让
 */
function selectTransferFlag() {
	$("#transferFlag").on("change", function() {
		var type = $(this).val();
		$("#select_transferFlag").val(type);
		showTransferFlag(type);
		setIsNovice(type);
	});
}

function showTransferFlag(type) {
		if(type==1){
			$("#transfer_flag_detail").show();
			$("#transferRate_div").show();
		}else{
			$("#transfer_flag_detail").hide();
			$("#transferRate_div").hide();
		}
}

// 设置是否可选新手项目
function setIsNovice(transferFlag) {
	if (transferFlag == 1) {
		$(".is_novice").attr("disabled","disabled");
	} else {
		$(".is_novice").removeAttr("disabled");
	}
}
//还款方式
$('#return_type').change(function(){ 
	var selectReturnType = $(this).children('option:selected').val(); 
    var selectPeriodType = $('#borrow_period_type').children('option:selected').val();
    // 如果选择按周还款
    if (selectPeriodType != "") {
    	if (selectPeriodType == 4) {
    		if (selectReturnType != "" && selectReturnType != 'avg_principal_week') {
    			bootbox.alert("借款周期以周为单位时，还款方式只能选择等本等息按周还款");
    			$(this).children('option:selected').attr("selected",false);
    			return;
    		}
    	} else {
    		if (selectReturnType != "" && selectReturnType == 'avg_principal_week') {
    			bootbox.alert("还款方式选择等本等息按周还款时，借款周期只能选择以周为单位");
    			$(this).children('option:selected').attr("selected",false);
    			return;
    		}
    	}
    }
	if (selectReturnType=='avg_principal'||selectReturnType=='avg_principal_week'){
		$("#transferFlag").attr("disabled","disabled");
		$("#transferFlag").val("0");
		$("#select_transferFlag").val(0);
		showTransferFlag(0);
		setIsNovice(0);
	}else {
		$("#transferFlag").removeAttr("disabled");
	}
});

//设置是否可选转让项目
function selectIsNovice() {
	$(".is_novice").on("change", function() {
		var isNovice = $(this).val();
		$("#select_isNovice").val(isNovice);
		if (isNovice == 0) {
			$("#transferFlag").attr("disabled","disabled");
		} else {
			$("#transferFlag").removeAttr("disabled");
		}
	});
}
/**
 *项目是否满标悬赏
 */
function selectCatalyzerFlag() {
	$("#catalyzerFlag").on("change", function() {
		var type = $(this).val();
		showCatalyzerFlag(type);
	});
}
// 借款周期
$('#borrow_period_type').change(function(){ 
	var selectPeriodType = $(this).children('option:selected').val(); 
    var selectReturnType = $('#return_type').children('option:selected').val();
	// 如果选择周期单位为周
    if (selectReturnType != "") {
    	if (selectReturnType == 'avg_principal_week') {
    		if (selectPeriodType != "" && selectPeriodType != 4) {
    			bootbox.alert("还款方式选择等本等息按周还款时，借款周期只能选择以周为单位");
    			$('#return_type').children('option:selected').attr("selected",false);
    			return;
    		}
    	} else {
    		if (selectPeriodType != "" && selectPeriodType == 4) {
    			bootbox.alert("借款周期以周为单位时，还款方式只能选择等本等息按周还款");
    			$('#return_type').children('option:selected').attr("selected",false);
    			return;
    		}
    	}
    }
});

function showCatalyzerFlag(type) {
		if(type==1){
			$("#catalyzer_extra_amount").show();
		}else{
			$("#catalyzer_extra_amount").hide();
		}
}
/**
 *项目是否加息
 */
function selectAddRateFlag() {
	$("#addRateFlag").on("change", function() {
		var type = $(this).val();
		showAddRateFlag(type);
	});
}

function findTransferRate(){
	$.post(
		config.urlMap.findTransferRate,function(data){
			if(data.success){
				var html="<tr><td style='padding:5px'>持有天数</td><td style='padding:5px'>手续费率</td></tr>";
				$.each(data.result,function(i,d){
					html+="<tr>";
					html+="	<td style='padding:5px'>"+d.desc+"</td>";
					html+="	<td style='padding:5px'>"+d.rate+"%</td>";
					html+="</tr>";
				})
				$('#TransferRate').html(html);
			}
		}
	);
}
function showAddRateFlag(type) {
	if(type==1){
		$("#add_rate").show();
	}else{
		$("#add_rate").hide();
	}
}
