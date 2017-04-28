var payableAmount=0;
var overdueTable;
var repayForm;
var flagForm;
var collectForm;
var detailForm;
jQuery(function($) {
	overdueTableList();
	//表单验证初始化
	collectForm = $("#collect_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	//取消 cancel
	$('#cancel_collection').on('click', function() {
		collectForm.resetForm(); //验证重置
	});
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
	detailForm = $("#detail_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
});	
	//获取操作按钮
	function getAllOperation(row) {
		var allStatus=row.allStatus,repayStatus=row.repayStatus,collect=row.collect;
		var record = "<button class='btn btn-xs btn-info  interest-record permission-" + config.permission.getPayRecord + "' data-projectId='" + row.projectId  + "'>查看本息记录</button>"; //
		var overdueRecord = "<button class='btn btn-xs btn-primary overdue-record permission-" + config.permission.getOverdueRecord + "' data-projectId='" + row.projectId  + "'>垫资还款记录</button>"; 
		var overdueMark = "<button class='btn btn-xs btn-danger   overdue-mark permission-" + config.permission.saveRepayFlagInterest + "' data-projectId='" + row.projectId  + "'>垫资还款标记</button>"; 
		var findCollection = "<button class='btn btn-xs btn-success   overdue-find permission-" + config.permission.find + "' data-overdueRepayId='" + row.overdueRepayId  + "'>查看催收历程</button>";
		var addCollection = "<button class='btn btn-xs btn-primary   overdue-add permission-" + config.permission.add + "' data-overdueRepayId='" + row.overdueRepayId  + "'>添加</button>"; 
		var editCollection = "<button class='btn btn-xs btn-info   overdue-edit permission-" + config.permission.saveRepayFlagInterest + "' data-overdueRepayId='" + row.overdueRepayId  + "'>编辑</button>"; 
		//垫资还款
			if(collect==1){
				return findCollection+" "+addCollection;
			}else{
				return addCollection;
			}
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
                	 return sValue;
            },
            "sFileName": "逾期管理列表.xls"
    };
    if(config.permission.normalExcel){
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
					'mDataProp': 'borrowerName',
					'bSortable': false
				}, {
		            'mDataProp' : 'formatTotalAmount',
		            'bSortable' : false
		        }, {
		            'mDataProp' : 'interestPeriods',
		            'bSortable' : false
		        }, {
		            'mDataProp' : 'overdueStartDate',
		            'bSortable' : false,
		            'mRender': function(data, type, row) {
		            	if(!!row.overdueStartDate){
		            		return formatDate(data,'yyyy-mm-dd');
		            	}else{
		            		return "-";
		            	}
		            	
		            }
		        }, {
		            'mDataProp': 'overdueDays',
		            'bSortable': false,
		            'mRender'   : function(data, type, row) {
		            	if(row.allStatus==2){
		            		return "-";
		            	}else{
		            		return row.overdueDays+"天";
		            	}
		            	
		            }
		        }, {
		            'mDataProp': 'overdueInterestStr',
		            'bSortable': false
		        }, {
		            'mDataProp': 'overduePrincipalStr',
		            'bSortable': false
		        }, {
		            'mDataProp': 'overdueFineStr',
		            'bSortable': false
		        }, {
		            'mDataProp': 'totalAmountStr',
		            'bSortable': false
		        }, {
		            'mDataProp' : 'repayTime',
		            'bSortable' : false,
		            'mRender': function(data, type, row) {
		            	if(!!row.repayTime){
		            		return formatDate(data,'yyyy-mm-dd HH:mm:ss');
		            	}else{
		            		return "-";
		            	}
		            }
		        },{
		            'mDataProp' : 'repayStatus',
		            'bSortable' : false,
		            'mRender'   : function(data, type, row) {
		            	if(!!row.repayStatus){
		            		if(row.repayStatus==3){
		            			return "已还款"
		            		}else{
		            			return "未还款"
		            		}
		            	}else{
		            		return "";
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
	//添加
	$("#overdue-table").on("click", '.overdue-add', function() {
		$("#collectStatus").removeAttr('disabled');
		$("#collectDetail").removeAttr('disabled');
		$('#collect_form')[0].reset();
		$("#collectDetail").empty();
		$("#dropzone-collect-image").empty();
		//$('#collect_form').empty();
		$("#dropzone-collect-image").append("");
		//deleteDropzoneAll();
		var overdueRepayId = $(this).attr("data-overdueRepayId");
		$('#overdueRepayId').val(overdueRepayId);
		showCollect(overdueRepayId);
		$('#next_collect').hide();
		$("#edit1").show();
		$(".modalFormTitle").text("催收历程添加");
		$('#modal-table').modal({
			'show' : true
		});
	});
	//编辑
	$("#overdue-table").on("click", '.overdue-edit', function() {
		var overdueRepayId = $(this).attr("data-overdueRepayId");
		$("#collect").empty();
		$("#dropzone_collect_image").empty();
		$("#dropzone-collect-image").append("");
		$('#overdueRepayId').val(overdueRepayId);
		detailById(overdueRepayId,1);
		$('#next_collect').hide();
		$(".modalFormTitle").text("催收历程编辑");
		$('#collect-modal-table').modal({
			'show' : true
		});
	});
	//查看
	$("#overdue-table").on("click", '.overdue-find', function() {
		var overdueRepayId = $(this).attr("data-overdueRepayId");
		findCollectRecord(overdueRepayId);
	});
	$("#collectStatus").change(function(){
		var collectStatus=$('#collectStatus').val();
		if(collectStatus==2){
			$('#next_collect').show();
		}else{
			$('#next_collect').hide();
		}
	});
	function removeHiddenVaildation() {
		$('#collect_form div:hidden').each(function() {
			$(this).find('select,input,textarea').removeAttr("datatype");
		});
	}

	function detailById(id){
		$.post(config.urlMap.find + overdueRepayId, function(data) {
			//eachCollectList(data.collectList,type);
		});
	}
	function queryCollect(id){
		window.top.setIframeBox("collect-find-" + id, config.urlMap.queryCollect + id, "催收历程详情");
		window.top.closeActiveIframe();
		/*$.post(config.urlMap.findImage + collectId, function(data) {
			if(!!data){
				eachAttachements(data.bscAttachments, "dropzone_collect_image");
			}
		});*/
	}
	function findCollectRecord(overdueRepayId) {
		var collectTable = $('#collectRecord-table').dataTable({
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
			'sAjaxSource' : config.urlMap.showCollectRecord+"?overdueRepayId="+overdueRepayId,
			'aoColumns' : [
				 {
					'mDataProp': 'id',
					'bSortable': false
				},{
					'mDataProp': 'collectTime',
					'bSortable': false,
					'mRender': function(data, type, row) {
						if(row.collectTime != null) {
							return formatDate(row.collectTime,"yyyy-mm-dd");
						}
					}
				}, {
					'mDataProp': 'collectForm',
					'bSortable': false
					
				}, {
					'mDataProp': 'collectStatus',
					'bSortable': false,
					'mRender': function(data, type, row) {
						if(!!row.collectStatus) {
							if(row.collectStatus==1){
								return "成功";
							}else{
								return "失败";
							}
						}
					}
					
				}, {
					'mDataProp': 'collectDetail',
					'bSortable': false
				},{
					'mDataProp': 'nextCollectTime',
					'bSortable': false,
					'mRender': function(data, type, row) {
						if(row.nextCollectTime != null) {
							return formatDate(row.nextCollectTime,"yyyy-mm-dd");
						}else{
							return "-";
						}
					}
				}, {
					'mDataProp': 'nextCollectForm',
					'bSortable': false
					
				},{
					'mDataProp': 'operation',
					'bSortable': false,
					'mRender': function(data, type, row) {
						return getAllOperationCollect(row);
					}
				}
				
			]
		});
		collectTable.fnSettings().sAjaxSource=config.urlMap.showCollectRecord+"?overdueRepayId="+overdueRepayId;
		collectTable.fnDraw();
		$('#process-modal-table').modal('show');
		
	}
	function getAllOperationCollect(row) {
		var findCollection = "<button class='btn btn-xs btn-success   collect-find permission-" + config.permission.find + "' data-id='" + row.id  + "'>查看图片</button>";
		var addCollection = "<button class='btn btn-xs btn-primary   overdue-add permission-" + config.permission.saveRepayFlagInterest + "' data-overdueRepayId='" + row.overdueRepayId  + "'>添加</button>"; 
		var editCollection = "<button class='btn btn-xs btn-info   collect-edit permission-" + config.permission.edit + "' data-id='" + row.id  + "'>编辑</button>"; 
		return findCollection+" "+editCollection;
	}
	$("#collectRecord-table").on("click", '.collect-find', function() {
		$('#collect-modal-table').modal({
			'show' : true
		});
		var id = $(this).attr("data-id");
		$("#dropzone_collect_image").empty();
		$("#j-json-dropzone_collect_image").val("");
		showProcessFind(id);
	});
	$("#collectRecord-table").on("click", '.collect-edit', function() {
		$('#modal-table').modal({
			'show' : true
		});
		$("#dropzone-collect-image").empty();
		$("#j-json-dropzone-collect-image").val("");
		$("#edit1").hide();
		$("#next_collect").hide();
		var id = $(this).attr("data-id");
		showProcess(id);
	});
	function showProcessFind(id){
		$("#detail_form").xform("load", config.urlMap.findCollectProcess+id,function(data){
			if(!!data){
					$("#collect_time").val(formatDate(data.collectTime,'yyyy-mm-dd'));
					$("#collect_form").val(data.collectForm);
					//$("#collect_status").val(data.collectForm);
					$("#collect_Detail").attr('disabled',"true");
					$("#next_CollectTime").val(formatDate(data.nextCollectTime,'yyyy-mm-dd'));
					$("#update_Collection").hide();
				}
				eachAttachementsFind(data.bscAttachments,"dropzone_collect_image");
		});
	}
	function showProcess(id){
		$("#collect_form").xform("load", config.urlMap.findCollectProcess+id,function(data){
			if(!!data){
				if(!!data.collectTime){
					$("#collectTime").val(formatDate(data.collectTime,'yyyy-mm-dd'));
					//$("#collectTime").attr('disabled',"true");
					//$("#collectForm").attr('disabled',"true");
					//$("#collectStatus").attr('disabled',"true");
					//$("#collectStatus").val("");
					$("#nextCollectTime").val(formatDate(data.nextCollectTime,'yyyy-mm-dd'));
					$("#nextCollectTime").attr('disabled',"true");
					$("#nextCollectForm").attr('disabled',"true");
				}
				eachAttachements(data.bscAttachments,"dropzone-collect-image");
			}
		});
	}
	function deleteDropzoneAll(){
			var dropzoneID ="dropzone_collect_image";
			var customDropzone = Dropzone.forElement("#"+dropzoneID);
			var dropZoneFiles = customDropzone.files;
			if(dropZoneFiles.length > 0){
				for(i=0; i<dropZoneFiles.length;i++){
					customDropzone.removeFile(dropZoneFiles[i]);
				}
			}
	}
	function eachCollectList(collectList,type) {
		if (!!collectList) {
			for(var i=0;i<collectList.length;i++){
				var groupCollectTime = $("<div  class='form-group clearfix'><label for='form-field-1' class='col-sm-3 control-label no-padding-right'> 催收日期: </label>" +
				"<div class='col-sm-9'><input type='text' id='collectTime' disabled='disabled' name='collectList[" + i + "].collectTime' class='col-xs-10 col-sm-5'  ><input type='hidden' id='id' name='collectList[" + i + "].id'></div></div>"+
				"<div  class='form-group clearfix'><label for='form-field-1' class='col-sm-3 control-label no-padding-right'> 催收形式: </label>" +
				"<div class='col-sm-9'><input type='text' id='collectForm' disabled='disabled' name='collectList[" + i + "].collectForm' class='col-xs-10 col-sm-5' ></div></div>"+
				"<div  class='form-group clearfix'><label for='form-field-1' class='col-sm-3 control-label no-padding-right'> 催收结果: </label>" +
				"<div class='col-sm-9'><input type='text' id='collectStatus' disabled='disabled' name='collectList[" + i + "].collectStatus' class='col-xs-10 col-sm-5'  ></div></div>"+
				"<div  class='form-group clearfix'><label for='form-field-1' class='col-sm-3 control-label no-padding-right'> 催收结果: </label>" +
				"<div class='col-sm-9'> <textarea id='collectDetail' name='collectList[" + i + "].collectDetail']  datatype='*' rows='5' cols='60' maxlength=1000 placeholder='此处添加文字(必须填)'></textarea>" +
				//"<span class='btn btn-success fileinput-button dropzone_collect_image dz-upload-btn' style='display:none'><i class='glyphicon glyphicon-plus'></i><span>添加图片</span></span>"+
				//"<div class='dropzoneImage dropzone dropzoneSort dropzone_collect_image' id='dropzone_collect_image' data-category='collect_image'></div><div class='fallback'></div>" +
				"<div class='col-sm-9'><button onclick ='return false;' class='btn btn-sm' id='look_image' data-collectId='" +collectList[i].id +"'>查看图片</button></div>"+
				"</div></div>");
				$(groupCollectTime).find("input[name='collectList[" + i + "].collectTime']").val(formatDate(collectList[i].collectTime,'yyyy-mm-dd')); 
				$(groupCollectTime).find("input[name='collectList[" + i + "].collectForm']").val(collectList[i].collectForm);
				var resultCollect="";
				if(collectList [i].collectStatus==1){
					resultCollect="成功";
				}else{
					resultCollect="失败";
				}
				$(groupCollectTime).find("input[name='collectList[" + i + "].collectStatus']").val(resultCollect);
				$(groupCollectTime).find("input[name='collectList[" + i + "].id']").val(collectList[i].id);
				$(groupCollectTime).find("textarea[ name='collectList[" + i + "].collectDetail']").val(collectList[i].collectDetail);
				
				$(groupCollectTime).find("button[ id='look_image']").on('click', function() {
					var collectId = $(this).attr("data-collectId");
					$("#dropzone_collect_image").empty();
					findImage(collectId);
					//$(groupCollectTime).find("div[ id='aa']").appendTo("#temp");
				});
				$("#collect").append(groupCollectTime);
				for(var j=0;j<i;j++){
					if(i>1){
						$("#collect").after(groupCollectTime);
					}
				}
				if (!!collectList[i].bscAttachments) {
					//eachAttachements(collectList[i].bscAttachments, "dropzone_collect_image");
				}
				if(type==2){
					$(groupCollectTime).find("textarea[id='collectDetail']").attr('disabled',"disabled");
					$('#update_Collection').hide();
				}else{
					$(groupCollectTime).find("textarea[id='collectDetail']").removeAttr('disabled');
					$('#update_Collection').show();
				}
			}
		}else{
			//alert();
		}
	}
	/*$('#look_image').on('click', function() {
		alert();
	});*/ 
	function eachAttachements(attachments, fileId) {
		var customDropzone = Dropzone.forElement("#"+fileId);
		$.each(attachments, function(n, v) {
			addImageToDropzone(customDropzone, v);
			//customColorBox(v.module);
		});
	}
	function eachAttachementsFind(attachments, fileId) {
		var customDropzone = Dropzone.forElement("#"+fileId);
		$.each(attachments, function(n, v) {
			addImageToDropzone(customDropzone, v);
			disableDropzone();
			//customColorBox(v.module);
		});
	}
	function showCollect(overdueRepayId){
		$("#collect_form").xform("load", config.urlMap.showCollectInfo+overdueRepayId,function(data){
			if(!!data.collectTime){
				$("#collectTime").val(formatDate(data.collectTime,'yyyy-mm-dd'));
				//$("#collectTime").attr('disabled',"true");
				//$("#collectForm").attr('disabled',"true");
			}else{
				$("#collectTime").removeAttr('disabled');
				$("#collectForm").removeAttr('disabled');
			}
		});
	}
	//保存
	$('#save_Collection').on('click', function() {
		removeHiddenVaildation();
		if (collectForm.check(false)) {
			$('#collect_form').xform('post', config.urlMap.save, function(data) {
				if (!data.success) {
					if (!!data.resultCodeEum) {
						bootbox.alert(data.resultCodeEum[0].msg,function(){
							$('#save_Collection').removeAttr("disabled");
						});
					} else {
						bootbox.alert("保存失败!",function(){
							$('#save_Collection').removeAttr("disabled");
						});
					}
				} else {
					bootbox.alert("保存成功!", function() {
						overdueTable.fnDraw();
						window.top.closeActiveIframe("直投项目逾期管理");
						$('#save_Collection').removeAttr("disabled");
					});
				}
			});
		}
	});
	//更新
	$('#update_Collection').on('click', function() {
		if (detailForm.check(false)) {
			$('#detail_form').xform('post', config.urlMap.update, function(data) {
				if (!data.success) {
					if (!!data.resultCodeEum) {
						bootbox.alert(data.resultCodeEum[0].msg,function(){
							$('#save_Collection').removeAttr("disabled");
						});
					} else {
						bootbox.alert("更新催收历程失败!",function(){
							$('#save_Collection').removeAttr("disabled");
						});
					}
				} else {
					bootbox.alert("更新催收历程成功!", function() {
						overdueTable.fnDraw();
						window.top.closeActiveIframe("直投项目逾期管理");
						$('#save_Collection').removeAttr("disabled");
					});
				}
			});
		}
	});
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
				}, {
					'mDataProp': 'payUnderStatusStr',
					'bSortable': false
					
				}
			]
		});
		overdueRemarkTable.fnSettings().sAjaxSource=config.urlMap.getPayRecord+"?id="+projectId;
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
	function showCollectRecord(overdueRepayId) {
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
			'sAjaxSource' : config.urlMap.getPayRecord+"?id="+overdueRepayId,
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
		interestTable.fnSettings().sAjaxSource=config.urlMap.getPayRecord+"?id="+overdueRepayId;
		interestTable.fnDraw();
		$('#interest-modal-table').modal('show');
		
	}
	
	
	$('#overdueRemark-table-1 tr').on("click",".i_e_date,.i_s_date",function(){
		WdatePicker({
			isShowClear:true,
			readOnly:true,
			dateFmt:'yyyy-MM-dd',
			onpicked:vailOverdueRepayDate
		});
	});
	//根据还款日期获取滞纳金、
	function vailOverdueRepayDate(){
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




