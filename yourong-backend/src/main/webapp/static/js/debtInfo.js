jQuery(function($) {
	
	//表单验证初始化
	var debtForm = $("#debtInfo_form").Validform({
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
            "sButtonText": "导出Excel",
            "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12  ],
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            	sValue = nTr.childNodes[iColumn].innerText;
            	 if (sValue != "") {
                     return "=\"" + sValue + "\"";
                 } else {
                	 return sValue;
                 }
            },
            "sFileName": "债权信息查询.xls"
    };
    if(config.permission.infoExcel){
    	exportButton.push(excelButton);
    }
    
	var debtInfoTable = $('#debtInfo-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
		'bAutoWidth' : false,
		'bFilter': false,
		'bProcessing': false,
		'bSort': true,
		'aaSorting':[[1,"desc"]],
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.infoAjax,
		'aoColumns': [{
				'mDataProp': 'projectId',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return "<input type='checkbox' value=" + row.projectId + ">";
				}
			}, {
				'mDataProp': 'projectId',
				'bSortable': true,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'projectName',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + row.projectName + "</font>";
					else
						return row.projectName;
				}
			}, {
				'mDataProp': 'debtType',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + getDictLabel(config.debtType, row.debtType) + "</font>";
					else
						return getDictLabel(config.debtType, row.debtType);
				}
			}, {
				'mDataProp': 'guarantyType',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + getDictLabel(config.guarantyType, row.guarantyType) + "</font>";
					else
						return getDictLabel(config.guarantyType, row.guarantyType);
				}
			}, {
				'mDataProp': 'serialNumber',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'originalDebtNumber',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			},  {
				'mDataProp': 'lenderName',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'borrowerName',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'amountStr',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'onlineTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
	            	if(data == null) {
	            		return '';
	            	}
	            	if(row.endFlag > 0)
						return "<font color='red'>" + formatDate(data,"yyyy-mm-dd") + "</font>";
					else
						return formatDate(data,"yyyy-mm-dd");
	            }
				
			}, {
				'mDataProp': 'endDate',
				'bSortable': true,
				'mRender': function(data, type, row) {
	            	if(data == null) {
	            		return '';
	            	}
	            	if(row.endFlag > 0)
						return "<font color='red'>" + formatDate(data,"yyyy-mm-dd") + "</font>";
					else
						return formatDate(data,"yyyy-mm-dd");
	            }
			}, {
				'mDataProp': 'repayment',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'controlRemarks',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == null) 
						return "";
					if(row.endFlag > 0)
						return "<font color='red'>" + data + "</font>";
					else
						return data;
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var buttons = '';
					buttons = buttons + "<button class='btn btn-xs btn-info btn-primary interestRecord' data-value='"+row.id+"'>付息记录</button> ";
					buttons = buttons + "<button class='btn btn-xs btn-warn btn-primary addRemarks' data-value='"+row.id+"' data-controlRemarks='" + row.controlRemarks +"'>添加备注</button> ";
					return buttons;
				}
			}
		]
	});

	//付息记录
	$("#debtInfo-table").on("click", '.interestRecord', function() {
		var id = $(this).attr("data-value");
		$("#interest-table").dataTable().fnDestroy(); 
		showInterestRecord(id);
	});
	
	//添加备注
	$("#debtInfo-table").on("click", '.addRemarks', function() {
		var id = $(this).attr("data-value");
		var controlRemarks =  $(this).attr("data-controlRemarks");
		$('#controlRemarksId').val(id);
		if(controlRemarks == 'null') {
			$('#newControlRemarks').val('');
		} else {
			$('#newControlRemarks').val(controlRemarks);
		}
		$('#modal-controlRemarks').modal('show');
	});
	
	//查询
	$('#query_debtInfo').on('click', function() {
		debtInfoTable.fnDraw();
	});
	
	//添加备注，发送后台
	$("#btn_add_remarks").on("click",function(){
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
});

function showInterestRecord(id) {
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
				'mDataProp': 'debtId',
				'bSortable': false
			}, {
				'mDataProp': 'debtId',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.payablePrincipal == 0) {
						return ((row.endDate != null)?formatDate(row.endDate,"yyyy-mm-dd") + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' : ' ') + '利息';
					} else {
						return ((row.endDate != null)?formatDate(row.endDate,"yyyy-mm-dd") + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' : ' ') + '利息 + 本金';
					}
				}
			}, {
				'mDataProp': 'debtId',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.status == 1) {
						return '¥' + formatCurrency(row.realPayPrincipal + row.realPayInterest);
					} else {
						return '¥' + formatCurrency(row.payablePrincipal + row.payableInterest);
					}
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

function dayDiff(day1) {
	var s1 = new Date(day1.replace(/-/g, "/"));
	var s2 = new Date();

	var days = s1.getTime() - s2.getTime();
	var time = parseInt(days / (1000 * 60 * 60 * 24));
	return time;
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
		default:
	}
});
//重置时时间查询条件隐藏
$(".resetButton").on("click",function(){
	$("#end_date_search").css("display","none");
	$("#end_date_start_search").css("display","none");
	$("#due_date_search").css("display","none");
});
