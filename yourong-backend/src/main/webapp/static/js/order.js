jQuery(function($) {
	if(!config.permission.showMobile) {
		$("[name = 'mobileTd']").each(function () {
			$(this).hide();
        });
		$("#memberIdTd").attr("colspan", "3");
	}
    /* form handler */
     //表单验证初始化
     var orderForm = $("#order_form").Validform({
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
         "mColumns": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13,14],
         "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
             sValue = nTr.childNodes[iColumn].innerText;
             if (sValue != "") {
                 return "\"" + sValue + "\"";
             } else {
                 return sValue;
             }
         },
         "sFileName": "订单列表.xls"
     };
     if(config.permission.orderExcel){
         exportButton.push(excelButton);
     }
     
    // dateTable init
    var orderTable = $('#order-table-2').dataTable({
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
        'aoColumns' : [ 
        {
            'mDataProp' : 'orderId',
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
            'mDataProp' : 'memberId',
            'bSortable' : false
        }, {
            'mDataProp' : 'mobile',
            'bSortable' : false,
            'bVisible':config.permission.showMobile?true:false
        }, {
            'mDataProp' : 'orderTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
            'mDataProp' : 'status',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
				return getDictLabel(config.status, row.status);
			}
        }, {
            'mDataProp' : 'updateTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
            	if(row.status < 2) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
			'mDataProp' : 'orderSource',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				return getDictLabel(config.orderSource, data);
            }
        }, {
            'mDataProp' : 'remarks',
            'bSortable' : false
        },{
        	'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var buttons = '';
				if(row.status < 2) {
					if(row.status == 0) {
						buttons = buttons + "<button class='btn btn-xs btn-info cancelOrder permission-" + config.permission.cancelOrder + "' data-value='"+row.orderId+"'>取消订单</button> <br />";
					}
					buttons = buttons + "<button class='btn btn-xs btn-success reflash permission-" + config.permission.reflashOrder + "' >刷新</button> ";		
				} else {
					buttons = buttons + "<button class='btn btn-xs btn-info orderPersonInfo permission-" + config.permission.transPersonInfo + "' data-value='"+row.orderNo+"'>投资人信息</button> <br />";
				}
				if(row.status ==8){
					buttons = buttons + "<button class='btn btn-xs btn-info closeDirectProjectOrder permission-" + config.permission.closeDirectProjectOrder 
					+ "' data-value='"+row.orderId+"'>关闭订单</button> <br />";
				}
				if(row.status==2){
					buttons = buttons + "<button class='btn btn-xs btn-info returnOrderStatus permission-" + config.permission.closeDirectProjectOrder
						+ "' data-value='"+row.orderId+"'>退款状态查询</button> <br />";
				}
				return buttons;
			}
		}]
    });
    
	$('#query_order').on('click', function() {
		if($("#investAmountStart").val() == '' || $("#investAmountEnd").val() == '') {
			if($("#investAmountStart").val() != '') {
				$("#investAmountEnd").val($("#investAmountStart").val());
			}
			if($("#investAmountEnd").val() != '') {
				$("#investAmountStart").val($("#investAmountEnd").val());
			}
		}
		if($("#orderTimeStart").val() == '' || $("#orderTimeEnd").val() == '') {
			if($("#orderTimeStart").val() != '') {
				$("#orderTimeEnd").val($("#orderTimeStart").val());
			}
			if($("#orderTimeEnd").val() != '') {
				$("#orderTimeStart").val($("#orderTimeEnd").val());
			}
		}
		if($("#updateTimeStart").val() == '' || $("#updateTimeEnd").val() == '') {
			if($("#updateTimeStart").val() != '') {
				$("#updateTimeEnd").val($("#updateTimeStart").val());
			}
			if($("#updateTimeEnd").val() != '') {
				$("#updateTimeStart").val($("#updateTimeEnd").val());
			}
		}
		if($("#updateTimeStart").val() != '') {
			if($("select[name='search_status'] option:selected").val() < 2)
				$("select[name='search_status'] option[value='']").attr("selected","selected");
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
		orderTable.fnDraw();
	});
	
	//取消订单
	$("#order-table-2").on("click", '.cancelOrder', function() {
		var id = $(this).attr("data-value");
		bootbox.confirm("你确定要取消订单吗?", function(result) {
			if (result) {				
				$.post(
						config.urlMap.cancelOrder,{id:id},function(data){
							if(data.success){
								bootbox.alert("操作成功！");
							}else{
								bootbox.alert("操作失败！");
							}
							orderTable.fnDraw('bStateSave', true);
						}
				);
			}
		});
	});
	
	//关闭直投项目订单
	$("#order-table-2").on("click", '.closeDirectProjectOrder', function() {
		var id = $(this).attr("data-value");
		bootbox.confirm("你确定要关闭订单吗?", function(result) {
			if (result) {				
				$.post(
						config.urlMap.closeDirectProjectOrder,{id:id},function(data){
							if(data.success){
								bootbox.alert("操作成功！");
							}else{
								bootbox.alert("操作失败！");
							}
							orderTable.fnDraw('bStateSave', true);
						}
				);
			}
		});
	});

	//退款状态查询
	$("#order-table-2").on("click", '.returnOrderStatus', function() {
		var id = $(this).attr("data-value");
		$.post(
			config.urlMap.returnOrderStatus,{id:id},function(data){
				if (data.code>0){
					var str='';
					if (data.status=='WAIT_REFUND'){
						str='当前订单退款状态为退款中';
					}
					if (data.status=='SUCCESS'){
						str='当前订单退款状态为退款成功';
					}
					if (data.status=='FAILED'){
						str='当前订单退款状态为退款失败';
					}
					$('#returnorderstatus').html(str);
					$('#modal-returnOrderStatus').modal('show');
				}else {
					$('#returnorderstatus').html("");
					$('#modal-returnOrderStatus').modal('show');
				}
			}
		);
	});

	//刷新
	$("#order-table-2").on("click", '.reflash', function() {
		var that = $(this);
		var orderId = $('td', that.parents("tr")).eq(0).text();
		var statusStr = $('td', that.parents("tr")).eq(10).text();
		if(statusStr == "待支付" || statusStr == "处理中") {
			$.post(
				config.urlMap.reflashOrder,{id:orderId},function(data){
					if(data.success){
						if(data.result.status > 1) {
							var str = getDictLabel(config.status, data.result.status);
							$('td', that.parents("tr")).eq(10).text(str);
							$('td', that.parents("tr")).eq(11).text(formatDate(data.result.updateTime,"yyyy-mm-dd HH:mm:ss"));
						}
						bootbox.alert("刷新成功！");
					}else{
						bootbox.alert("刷新失败！");
					}
				}
			);
		}
		
	});
	
	//查看投资人信息
	$("#order-table-2").on("click", '.orderPersonInfo', function() {
		var orderNo = $(this).attr("data-value");
		showOrderPersonInfo(orderNo);
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
