jQuery(function($) {
	
	//获取操作方法
	function getAllOperation(row) {
		var detail =  "<button class='btn btn-xs btn-info j-loan-detail' data-id='"+row.project.id+"'>记录</button>",
			loan = "<button class='btn btn-xs btn-success j-loan-loan permission-"+config.permission.loan+"' data-id='"+row.project.id+"' >放款</button>",
			operation = "";
		if(Number(row.loanableUserCapital + row.loanablePlatCapital)>0 && Number(row.loanedUserCapital + row.loanedPlatCapital)>0){
			operation = loan + "  "+ detail;
		}else if(Number(row.loanedUserCapital + row.loanedPlatCapital)>0){
			operation = detail;
		}else if(Number(row.loanableUserCapital + row.loanablePlatCapital)>0){
			operation = loan;
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
	var loanTable = $('#loan-table').dataTable({
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
            'mDataProp': 'id',
            'bSortable':true,
            'mRender':function(data,type,row){
            	return row.project.id;
            }
        }, {
            'mDataProp': 'projectName',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return row.project.name;
            }
        }, {
            'mDataProp': 'projectStatus',
            'bSortable': false,
            'mRender': function(data, type, row) {
            	return getDictLabel(config.projectStatus, row.project.status);
            }
        }, {
            'mDataProp': 'onlineTime',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return formatDate(row.project.onlineTime,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
            'mDataProp': 'saleEndTime',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return formatDate(row.project.saleEndTime,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
            'mDataProp': 'progressStr',
            'bSortable': false,
            'mRender':function(data){
            	return data+"%";
            }
        }, {
            'mDataProp': 'lastTransDate',
            'bSortable': false,
            'mRender': function(data, type, row) {
            	if(data==null){
            		return "";
            	}
                return formatDate(row.lastTransDate,"yyyy-mm-dd HH:mm:ss");
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
            	if(row.lenderMember!=null && row.lenderMember.trueName!=null){
            		return row.lenderMember.trueName;
            	}else{
            		return "";
            	}
            }
        }, {
            'mDataProp': 'totalAmount',
            'bSortable': false,
            'mRender':function(data, type, row){
            	return "￥"+Number(row.project.totalAmount).toFixed(2);
            }
        }, {
            'mDataProp': 'loanable',
            'bSortable': false,
            'mRender':function(data,type,row){
            	return "<span style='text-align:'center''>￥"+ row.loanableCapitalStr +"</span></br>￥" + row.loanableUserCapitalStr+"+￥"+row.loanablePlatCapitalStr;
            }
        }, {
            'mDataProp': 'loaned',
            'bSortable': false,
            'mRender':function(data,type,row){
            	return "<span style='text-align:'center''>￥"+ row.loanedCapitalStr +"</span></br>￥" + row.loanedUserCapitalStr+"+￥"+row.loanedPlatCapitalStr;;
            }
        }, {
            'mDataProp': 'status',
            'bSortable': false,
            'mRender':function(data,type,row){
            	return getDictLabel(config.status,row.status);
            }
        }, {
            'mDataProp': 'operate',
            'bSortable': false,
            'mRender':function(data, type, row){
            	return getAllOperation(row);
            }
        }]
	});
	
	
	$('#query_loan').on('click', function() {
		loanTable.fnSettings()._iDisplayStart=0; 
		loanTable.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
    });
	
	//放款详情
	//detail
	var loanDetailTable = $('#loanDetail-table').dataTable({
		'order':[[0,"desc"]],
		'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
        	getSearchValue("#loanDetail-form", aoData);
        },
        'sAjaxSource': config.urlMap.detail,
        'aoColumns': [
      	{
            'mDataProp': 'loanTime',
            'bSortable':true,
            'mRender':function(data){
            	return formatDate(data,"yyyy-mm-dd HH:mm:ss")
            }
        }, {
            'mDataProp': 'loanAmountStr',
            'bSortable': false
        }, {
            'mDataProp': 'userPayStr',
            'bSortable': false
        }, {
            'mDataProp': 'platformPayStr',
            'bSortable': false
        }]
	});
	

	
	//放款
	//loan
	$("#loan-table").on("click",".j-loan-loan",function(){
		var projectId = $(this).data('id');
		$.ajax({
			url:config.urlMap.showLoan,
			data:{
				'projectId':projectId
			},
			type:'post',
			dataType:'json',
			success:function(data){
				if(data.loanBiz!=null){
					eachLoanBiz(data.loanBiz);
				}
				$("#save_toloan").removeClass("disabled");
				if(data.balance!=null && data.loanBiz !=null && data.loanBiz.loanablePlatCapital != null){
					if(Number(data.balance) -  Number(data.loanBiz.loanablePlatCapital) <=0 && Number(data.loanBiz.loanablePlatCapital)!=0){
						$("span[name='balance']").html("(*平台运营资金余额不足)");
						$("#save_toloan").addClass("disabled");
					}
				}
			}
		});
		$('#loan-loan-table').modal('show');
	});
	
	//放款记录
	//loan-detail
	$("#loan-table").on("click",".j-loan-detail",function(){
		var projectId = $(this).data('id');
		$("#loan-detail-projectId").val(projectId);
		loanDetailTable.fnDraw({
			"fnServerParams": function(aoData) {
				getSearchValue("#loanDetail-form", aoData);
			}
		});
		$('#loan-detail-table').modal('show');
	});
	
	$("#save_toloan").on("click",function(){
		var projectId = $("#projectId").val();
		$(this).addClass("disabled");
		$.ajax({
			url:config.urlMap.toLoan,
			data:{
				'projectId':projectId
			},
			type:'post',
			dataType:'json',
			success:function(data){
				if(data.success){
					bootbox.alert("放款执行成功!",function(){
						$("#save_toloan").removeClass("disabled");
						$("#loan-loan-table").modal('hide');
						loanTable.fnDraw();
					});
				}else{
					$("#save_toloan").removeClass("disabled");
					if(data.resultCodeEum[0]!=null){
						bootbox.alert("放款失败，"+data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("放款失败!");
					}
				}
			}
		});
	});
});

function eachLoanBiz(data){
	$("span[name],td[name]").html("");
	$("input[name]").val("");
	$.each(data,function(n,v){
		if(n=="project" || n=="lenderMember"){
			$.each(v,function(k,m){
				if(k=="totalAmount"){
					$("span[name='project.totalAmount']").html(Number(m).toFixed(2));
				}else{
					$("span[name='"+n+"."+k+"'],td[name='"+n+"."+k+"']").html(m);
					$("input[name='"+n+"."+k+"']").val(m);
				}
			});
		}
		$("span[name='"+n+"'],td[name='"+n+"']").html(v);
	});
}