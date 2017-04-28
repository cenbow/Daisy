jQuery(function($) {
	
	var settlmentForm = $("#directSettlement_form").Validform({
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
            "sFileName": "直投项目平台营收结算.xls"
    };
    if(config.permission.settlmentExcel){
    	exportButton.push(excelButton);
    }
    	
    var interestTable = $('#directSettlement-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'order': [
            [0, "desc"]
        ],
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [{
        	'mDataProp': 'projectId',
        	'bSortable': false,
        	'mRender': function(data, type, row) {
        		return "<input type='checkbox' value=" + row.projectId + ">";
        	}
        }, {
            'mDataProp': 'projectId',
            'bSortable': false
        },{
            'mDataProp': 'projectName',
            'bSortable': false
        }, {
            'mDataProp': 'borrowerName',
            'bSortable': false
        }, {
            'mDataProp': 'totalAmountStr',
            'bSortable': false
        }, {
            'mDataProp': 'endDate',
            'bSortable': true,
            'mRender':function(data,type,row){
				var dateTime = "";
				if(!!row.startDate){
					dateTime = formatDate(row.startDate,"yyyy-mm-dd");
				}
				if(!!row.endDate){
					dateTime = dateTime +"<br/>至"+ formatDate(row.endDate,"yyyy-mm-dd");
				}
				return dateTime;
			}
        }, {
            'mDataProp': 'loanTime',
            'bSortable': false,
            'mRender'   : function(data, type, row) {
            	if(!!row.loanTime){
            		return formatDate(data,"yyyy-mm-dd");
            	}else{
            		return "";
            	}
            	
            	
            }
        }, {
            'mDataProp': 'projectFeeStr',
            'bSortable': false,
            'mRender':function(data,type,row){
				return data + getAllDetail(row);
			}
           
        }, {
            'mDataProp': 'usedCouponAmountStr',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'extraInterestStr',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'grossProfitStr',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }]
    });
    function getAllDetail(row) {
		var detail = "<button class='btn btn-xs  p2p-detail permission-" + config.permission.settlmentExcel + "' data-id='" + row.projectId + "'><i class=' bigger-130'>明细</i></button>"; //详情
		return detail;
	}
    
    $("#directSettlement-table").on("click", '.p2p-detail', function() {
		var id = $(this).attr("data-id");
		$("#detail_settle_form").xform("load", config.urlMap.getLoanInfo+id,function(data){
			if(!!data){
				$('#detail_formatManageFee').html(data.manageFeeRate+"%&nbsp;&nbsp;"+data.formatManageFee);
				if(!!data.guaranteeFeeRate){
					$("#detail_guaranteeFee_c").show();
					$('#detail_guaranteeFee').html(data.guaranteeFeeRate+"%&nbsp;&nbsp;"+data.guaranteeFee);
				}else{
					$("#detail_guaranteeFee_c").hide();
				}
				if(!!data.riskFeeRate){
					$("#detail_riskFee_c").show();
					$('#detail_riskFee').html(data.riskFeeRate+"%&nbsp;&nbsp;"+data.riskFee);
				}else{
					$("#detail_riskFee_c").hide();
				}
				if(!!data.introducerFeeRate){
					$("#detail_introducerFee_c").show();
					$('#detail_introducerFee').html(data.introducerFeeRate+"%&nbsp;&nbsp;"+data.introducerFee);
				}else{
					$("#detail_introducerFee_c").hide();
				}
				$('#detail_projectFeeStr').html(data.projectFeeStr);
			}
		});

		$('#modal-table-settle-detail').modal('show');
	});
    /**
     * 查询事件
     */
    $("#query_settlement").on("click", function() {
    	if(settlmentForm.check(false)){
    		  interestTable.fnSettings()._iDisplayStart=0;
    		  interestTable.fnDraw({
    	            "fnServerParams": function(aoData) {
    	                getAllSearchValue(aoData);
    	            }
    	        });
    	}
        return false;
    });



});
