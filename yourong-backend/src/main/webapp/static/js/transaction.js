jQuery(function($) {
	if(!config.permission.showMobile) {
		$("[name = 'mobileTd']").each(function () {
			$(this).hide();
        });
		$("#memberIdTd").attr("colspan", "3");
	}
    /* form handler */
     //表单验证初始化
     var transactionForm = $("#transaction_form").Validform({
         tiptype : 4,
         btnReset : ".btnReset",
         ajaxPost : true
     });
     
     /***
      * 导出按钮的权限设置
      */
     var exportButton = [];
     var excelButton = {
         "sExtends": "xls",
         "sButtonText": "导出Excel",
         "mColumns": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13,14,15],
         "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
             sValue = nTr.childNodes[iColumn].innerText;
             if (sValue != "") {
                 return "\"" + sValue + "\"";
             } else {
                 return sValue;
             }
         },
         "sFileName": "投资列表.xls"
     };
     if(config.permission.transactionExcel){
         exportButton.push(excelButton);
     }
     
    // dateTable init
    var transactionTable = $('#transaction-table-2').dataTable({
    	"tableTools": {//excel导出
    		"aButtons": exportButton,
    		"sSwfPath": config.swfUrl
    	},
        'bFilter' : false,
        'bProcessing' : true,
        'bSort' : false,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ {
            'mDataProp': 'id',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + ">";
            }
        },{
            'mDataProp' : 'id',
            'bSortable' : false
        }, {
            'mDataProp' : 'orderNo',
            'bSortable' : false
        }, {
            'mDataProp' : 'projectName',
            'bSortable' : false
        }, {
            'mDataProp' : 'investAmount',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
            	if(data == null) {
            		return "";
            	}
            	return formatCurrency(row.investAmount);
            }
        }, {
            'mDataProp' : 'payMethod',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
            	var retStr = '';
            	if(row.usedCapital > 0) {
            		retStr += "新浪存钱罐 <br/>";
            	}
            	if(row.payAmount > 0) {
            		retStr += "银行卡支付 <br/>";
            	}
            	if(row.usedCouponAmount > 0) {
            		retStr += "现金券 <br/>";
            	}
            	return retStr;
			}
        }, {
            'mDataProp' : 'payAmount',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
            	var retStr = '';
            	if(row.usedCapital > 0) {
            		retStr += formatCurrency(row.usedCapital) + "<br/>";
            	}
            	if(row.payAmount > 0) {
            		retStr += formatCurrency(row.payAmount) + "<br/>";
            	}
            	if(row.usedCouponAmount > 0) {
            		retStr += formatCurrency(row.usedCouponAmount) + "<br/>";
            	}
            	return retStr;
            }
        }, {
            'mDataProp' : 'annualizedRate',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	if(row.extraAnnualizedRate != null) {
            		return row.annualizedRate + "% + " + row.extraAnnualizedRate + "%";
            	} else {
            		return row.annualizedRate + "%";
            	}
            }
        }, {
            'mDataProp' : 'totalInterest',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	return "<a href='#' onclick='interestRecord(" + row.id + ")' >" + data + "</a>";
            }
        }, {
            'mDataProp' : 'installmentNum',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	if(data==null){
            		return "";
            	}else{
            		return row.payedNum + "/" + data;
            	}
            	
            }
        }, {
            'mDataProp' : 'receivedInterest',
            'bSortable' : false
        }, {
            'mDataProp' : 'leaseBonusAmounts',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return "";
            	}
            	return "<a href='#' onclick='leaseBonusRecord(" + row.id + ", "+ formatCurrency(data) + ")' >" + formatCurrency(data) + "</a>";
            }
        }, {
            'mDataProp' : 'memberId',
            'bSortable' : false
        }, {
            'mDataProp' : 'status',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
				return getDictLabel(config.status, row.status);
			}
        }, {
            'mDataProp' : 'transactionTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
            'mDataProp' : 'remarks',
            'bSortable' : false
        },{
        	'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var buttons = '';
				buttons = buttons + "<button class='btn btn-xs btn-info orderPersonInfo permission-" + config.permission.transPersonInfo + "' data-value='"+row.orderNo+"'>投资人信息</button> "+
				"<button class='btn btn-xs btn-primary downContract permission-" + config.permission.downContract + "' data-value='"+row.id+"'>下载合同</button> <br />";;
				return buttons;
			}
		}]
    });
    
	$('#query_transaction').on('click', function() {
		if($("#investAmountStart").val() == '' || $("#investAmountEnd").val() == '') {
			if($("#investAmountStart").val() != '') {
				$("#investAmountEnd").val($("#investAmountStart").val());
			}
			if($("#investAmountEnd").val() != '') {
				$("#investAmountStart").val($("#investAmountEnd").val());
			}
		}
		if($("#transactionTimeStart").val() == '' || $("#transactionTimeEnd").val() == '') {
			if($("#transactionTimeStart").val() != '') {
				$("#transactionTimeEnd").val($("#transactionTimeStart").val());
			}
			if($("#transactionTimeEnd").val() != '') {
				$("#transactionTimeStart").val($("#transactionTimeEnd").val());
			}
		}
		if($("#couponStart").val() == '' || $("#couponEnd").val() == '') {
			if($("#couponStart").val() != '') {
				$("#couponEnd").val($("#couponStart").val());
			}
			if($("#couponEnd").val() != '') {
				$("#couponStart").val($("#couponEnd").val());
			}
		}
		if($("#installmentNumStart").val() == '' || $("#installmentNumEnd").val() == '') {
			if($("#installmentNumStart").val() != '') {
				$("#installmentNumEnd").val($("#installmentNumStart").val());
			}
			if($("#installmentNumEnd").val() != '') {
				$("#installmentNumStart").val($("#installmentNumEnd").val());
			}
		}
		if (!$("#checkAll").prop("checked")
				&& !$("#checkCapital").prop("checked")
				&& !$("#checkCoupon").prop("checked")
				&& !$("#checkEBank").prop("checked")
				&& !$("#checkFastPayment").prop("checked")) {
			$("#checkAll").prop("checked", 'true');
		} 
		if($("#checkAll").prop("checked")) {
			$("#checkAll").val('1');
		} else {
			$("#checkAll").val('');
		}
		if($("#checkCapital").prop("checked")) {
			$("#checkCapital").val('1');
		} else {
			$("#checkCapital").val('');
		}
		if($("#checkCoupon").prop("checked")) {
			$("#checkCoupon").val('1');
		} else {
			$("#checkCoupon").val('');
		}
		if($("#checkEBank").prop("checked")) {
			$("#checkEBank").val('1');
		} else {
			$("#checkEBank").val('');
		}
		if($("#checkFastPayment").prop("checked")) {
			$("#checkFastPayment").val('1');
		} else {
			$("#checkFastPayment").val('');
		}
		transactionTable.fnDraw();
	});
	
	//查看投资人信息
	$("#transaction-table-2").on("click", '.orderPersonInfo', function() {
		var orderNo = $(this).attr("data-value");
		showOrderPersonInfo(orderNo);
	});
	
	//下载合同
	$("#transaction-table-2").on("click", '.downContract', function() {
		var transactionId = $(this).attr("data-value");
		$.ajax({
			type: "POST",
			url:  config.urlMap.getContractDownUrl,
			data:{
				transactionId: transactionId
            },
			dataType: "json",
			success: function(data){
				if(data.success) {
					window.location.href=data.result
				} else {
					bootbox.alert("合同下载失败");
				}
			}
		});
	});

});

function boxcheck(obj) {
	if(obj == 'checkAll' && $("#checkAll").prop("checked")) {
		$("#checkCapital").attr("checked", false);
		$("#checkCoupon").attr("checked", false);
		$("#checkAnnualizedRate").attr("checked", false);
		$("#checkEBank").attr("checked", false);
		$("#checkFastPayment").attr("checked", false);
	}
	if(obj != 'checkAll' && $("#" + obj).prop("checked")) {
		$("#checkAll").attr("checked", false);
	}
	if(obj == 'checkEBank' && $("#checkEBank").prop("checked")) {
		$("#checkFastPayment").attr("checked", false);
	}
	if(obj == 'checkFastPayment' && $("#checkFastPayment").prop("checked")) {
		$("#checkEBank").attr("checked", false);
	}
}

//交易利息信息
function interestRecord(id) {
	$("#interest-table").dataTable().fnDestroy(); 
	var interestTable = $('#interest-table').dataTable({
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
		'sAjaxSource' : config.urlMap.showInterest+id,
		'aoColumns' : [
			{
				'mDataProp': 'id',
				'bSortable': false
			}, {
				'mDataProp': 'startDate',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return DateDiff(row.endDate, row.startDate);
	            }
			}, {
				'mDataProp': 'payTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
	            		return '';
	            	}
	                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
	            }
			}, {
				'mDataProp': 'payablePrincipal',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
						return "";
					}
					return formatCurrency(data);	
				}
			}, {
				'mDataProp': 'realPayPrincipal',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
						return "";
					}
					return formatCurrency(data);	
				}
			}, {
				'mDataProp': 'payableInterest',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
						return "";
					}
					return formatCurrency(data);	
				}
			}, {
				'mDataProp': 'realPayInterest',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
						return "";
					}
					return formatCurrency(data);	
				}
			}, {
				'mDataProp': 'status',
				'bSortable': false,
				'mRender'   : function(data, type, row) {
	            	return getDictLabel(config.interestStatus, row.status);
	            }
			}
		]
	});
	interestTable.fnSettings().sAjaxSource=config.urlMap.showInterest+id;
	interestTable.fnDraw();
	$('#modal-table').modal('show');
}

//查看租赁分红
function leaseBonusRecord(id, title) {
	$("#leaseBonusDetailByTrans-table").dataTable().fnDestroy(); 
	$(".modalFormTitle").html("租赁分红总额：" + title);
	var leaseBonusDetailByTransTable = $('#leaseBonusDetailByTrans-table').dataTable({
		"bPaginate": true,
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
		'sAjaxSource' : config.urlMap.leaseBonusRecord+id,
		'aoColumns' : [
			{
				'mDataProp': 'id',
				'bSortable': false
			}, {
				'mDataProp': 'createTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
	            		return '';
	            	}
	                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
	            }
			}, {
				'mDataProp': 'bonusAmount',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) {
						return "";
					}
					return formatCurrency(data);	
				}
			}
		]
	});
	leaseBonusDetailByTransTable.fnSettings().sAjaxSource=config.urlMap.leaseBonusRecord+id;
	leaseBonusDetailByTransTable.fnDraw();
	$('#modal-leaseBonusDetailByTrans').modal('show');
}
//数字转金额
function formatCurrency(num) {
    num = num.toString().replace(/\$|\,/g,'');
    if(isNaN(num))
    num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num*100+0.50000000001);
    cents = num%100;
    num = Math.floor(num/100).toString();
    if(cents<10)
    cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
    num = num.substring(0,num.length-(4*i+3))+','+
    num.substring(num.length-(4*i+3));
    return (((sign)?'':'-') + num + '.' + cents);
}

//时间天数
function DateDiff(endDate,startDate) { 
	intDays=parseInt(Math.abs(endDate-startDate)/1000/60/60/24) + 1; 
	return intDays; 
} 
