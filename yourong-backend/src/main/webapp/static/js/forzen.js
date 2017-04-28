var forzenTable;
jQuery(function($) {
	initForzen();
});
	
	//获取操作方法
	function getAllOperation(row) {
		var unForzen =  "<button class='btn btn-info btn-xs btn-primary unforzen permission-"+config.permission.unforzen+"' data-id='"+row.id+"'>解冻</button>",
			forzenDetail = "<button class='btn btn-xs btn-success forzenList permission-"+config.permission.unforzenDetail+"' data-forzenNo='"+row.forzenNo+"' data-processRecord='"+row.processRecord+"' >解冻明细</button>",
			syncForzen = "<button class='btn btn-xs btn-success syncForzen permission-"+config.permission.syncFrozen+"' data-forzenNo='"+row.forzenNo+"' >同步状态</button>",
			operation = "";
		/*if(Number(row.loanableUserCapital + row.loanablePlatCapital)>0 && Number(row.loanedUserCapital + row.loanedPlatCapital)>0){
			operation = loan + "  "+ detail;
		}else if(Number(row.loanedUserCapital + row.loanedPlatCapital)>0){
			operation = detail;
		}else if(Number(row.loanableUserCapital + row.loanablePlatCapital)>0){
			operation = loan;
		}*/
		if(row.forzenStatus=="SUCCESS"){
			if(Number(row.balance)>0){
				operation = unForzen;
			}
			if(row.unforzenRecord==1){
				if(Number(row.balance)>0){
					operation =unForzen + "  "+ forzenDetail;
				}else{
					operation = forzenDetail;
				}
				
			}
		}else if(row.forzenStatus=="PROCESSING"){
			operation = syncForzen;
		}else{
			operation ="";
		}
		return operation;
	}
	
	 /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel" ,
            "sFileName": "托管放款管理.xls",
            "mColumns":[0,1,2,3,4,5,6,7,8,9,10,11]
    };
    if(config.permission.indexExcel){
    	exportButton.push(excelButton);
    }
    
	//列表数据
    function initForzen(){
    	 forzenTable = $('#forzen-table').dataTable({
    		"tableTools": {//excel导出
               // "aButtons": exportButton,
              //  "sSwfPath": config.swfUrl
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
                'mDataProp': 'id',
                'bSortable':true,
                'mRender':function(data,type,row){
                	return row.id;
                }
            }, {
                'mDataProp': 'forzenNo',
                'bSortable': false,
                'mRender': function(data, type, row) {
                    return row.forzenNo;
                }
            }, {
                'mDataProp': 'memberId',
                'bSortable': false
            }, {
                'mDataProp': 'trueName',
                'bSortable': false
            }, {
                'mDataProp': 'mobile',
                'bSortable': false
            }
            , {
                'mDataProp': 'forzenAmountStr',
                'bSortable': false
            }
            , {
                'mDataProp': 'forzenBalanceStr',
                'bSortable': false
            }, {
                'mDataProp': 'forzenTime',
                'bSortable': false,
                'mRender': function(data, type, row) {
                    return formatDate(row.forzenTime,"yyyy-mm-dd HH:mm:ss");
                }
            }, {
                'mDataProp': 'forzenReason',
                'bSortable': false
            }, {
                'mDataProp': 'type',
                'bSortable': false,
                'mRender'   : function(data, type, row) {
    				return getDictLabel(config.forzenType, row.type);
    			}
            }
            , {
                'mDataProp': 'forzenStatus',
                'bSortable': false,
                'mRender'   : function(data, type, row) {
    				return getDictLabel(config.forzenStatus, row.forzenStatus);
    			}
            }, {
                'mDataProp': 'operate',
                'bSortable': false,
                'mRender':function(data, type, row){
                	return getAllOperation(row);
                }
            }, {
                'mDataProp': 'remarks',
                'bSortable': false
            }]
    	});
    }
	
	
	
	$('#query_forzen').on('click', function() {
		forzenTable.fnSettings()._iDisplayStart=0; 
		forzenTable.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
    });
	
	
//解冻明细
$("#forzen-table").on("click", '.forzenList', function() {
	var forzenNo = $(this).attr("data-forzenNo");
	var processRecord = $(this).attr("data-processRecord");
	if(processRecord==1){
		$('#btn_sync_unforzen').show();
	}else{
		$('#btn_sync_unforzen').hide();
	}
	$('#forzenId').val(forzenNo);
	forzenList(forzenNo);
});


//解冻详情
$("#forzen-table").on("click", '.unforzen', function() {
	var id = $(this).attr("data-id");
	$("#unforzen_form").xform("load", config.urlMap.detail+id,function(data){
		if(data!=null){
			$("#forzen_trueName").html(data.trueName);
			$("#forzen_mobile").html(data.mobile);
			if(!!data.balance){
				$("#forzen_balance").val(data.balance);
				$("#forzen_balance_h").html(data.balance);
			}else{
				$("#forzen_balance_h").html("0");
			}
		}
	});

	$('#modal-unforzen-table').modal('show');
});

var unforzen_form = $("#unforzen_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#btn_cancel").on('click', function () {
	$('#unforzen_form')[0].reset();
	forzenTable.fnDraw();
});

//解冻余额
$("#btn_save_unforzen").on('click', function () {
	$(this).addClass("disabled");
	var that = $(this);
	if(Number($("#forzen_balance").val())==0){
		 bootbox.alert("剩余冻结金额不足！"); 
		 return;
	}
	 if (unforzen_form.check(false)) {
		 if(Number($("#forzen_balance").val())<Number($("#form-field-amount").val())){
			 bootbox.alert("解冻金额不能大于剩余冻结金额！"); 
			 return;
		}
		$.ajax({
			url:config.urlMap.unforzen,
			data:$("#unforzen_form").serialize(),
			type:"post",
			dataType:"json",
			success:function(data){
				if(data.success){
					$('#modal-unforzen-table').modal('hide');
					bootbox.alert("解冻存钱罐余额成功！");
					forzenTable.fnDraw();
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							return;
						});
					}else{
						if(!!data.result){
							return bootbox.alert(data.result.remarks);
						}else{
							return bootbox.alert("解冻存钱罐余额失败！");
						}
						
					}
				}
			}
		});
	 }
	that.removeClass("disabled");
});






function forzenList(forzenNo) {
	var interestTable = $('#unforzenDetail-table').dataTable({
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
		'sAjaxSource' : config.urlMap.unforzenDetail+"?forzenNo="+forzenNo,
		'aoColumns' : [
			{
				'mDataProp': 'id',
				'bSortable': false
			}, {
				'mDataProp': 'unforzenNo',
				'bSortable': false
				
			}, {
                'mDataProp': 'unforzenTime',
                'bSortable': false,
                'mRender': function(data, type, row) {
                    return formatDate(row.unforzenTime,"yyyy-mm-dd HH:mm:ss");
                }
            }, {
				'mDataProp': 'amount',
				'bSortable': false
				
			}, {
				'mDataProp': 'unforzenStatus',
				'bSortable': false,
				'mRender'   : function(data, type, row) {
    				return getDictLabel(config.unforzenStatus, row.unforzenStatus);
    			}
				
			}
			, {
				'mDataProp': 'unforzenReason',
				'bSortable': false
				
			}
		]
	});
	interestTable.fnSettings().sAjaxSource=config.urlMap.unforzenDetail+"?forzenNo="+forzenNo;
	interestTable.fnDraw();
	$('#unforzen-modal-table').modal('show');
	
}

//同步解冻
$("#btn_sync_unforzen").on("click",function(){
		var forzenNo = $('#forzenId').val();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.syncFrozen,
			data: {forzenNo:forzenNo,type:1},
			dataType: "json",
			success: function(data){
				if(data.success){
					bootbox.alert("同步解冻成功！");
					forzenTable.fnDraw();
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							return;
						});
					}else{
						if(!!data.result){
							return bootbox.alert(data.result.remarks);
						}else{
							return bootbox.alert("同步解冻失败！");
						}
						
					}
				}
			}
		});
		$(this).removeClass("disabled");
});
//同步冻结
$("#forzen-table").on("click", '.syncForzen', function() {
	var forzenNo = $(this).attr("data-forzenNo");
	$(this).addClass("disabled");
	$.ajax({
		type: "GET",
		url:  config.urlMap.syncFrozen,
		data: {forzenNo:forzenNo,type:2},
		dataType: "json",
		success: function(data){
			if(data.success){
				bootbox.alert("同步冻结成功！");
				forzenTable.fnDraw();
			}else{
				if(!!data.resultCodeEum){
					return bootbox.alert(data.resultCodeEum[0].msg,function(){
						return;
					});
				}else{
					if(!!data.result){
						return bootbox.alert(data.result.remarks);
					}else{
						return bootbox.alert("同步冻结失败！");
					}
					
				}
			}
		}
	});
	$(this).removeClass("disabled");
});

$("#form-field-amount").change(function(){
	var rate = $(this).val();
	if(Number($("#forzen_balance").val())<Number(rate)){
		 bootbox.alert("解冻金额不能大于剩余冻结金额余额！"); 
		 return;
	}
});

