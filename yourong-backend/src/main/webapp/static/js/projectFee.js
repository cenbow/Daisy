jQuery(function($) {
	 /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel",
            "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11],
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            	sValue = nTr.childNodes[iColumn].innerText;
                	 return sValue;
            },
            "sFileName": "直投项目各费用收取记录.xls"
    };
    if(config.permission.manageExcel){
    	exportButton.push(excelButton);
    }
	var manageTable = $('#management-info-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'bAutoWidth' : false,
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'aaSorting':[[1,"desc"]],
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajaxManage,
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
	            'bSortable' : false
	        }, {
	            'mDataProp' : 'borrowerName',
	            'bSortable' : false
	            
	        }, {
	            'mDataProp' : 'totalAmount',
	            'bSortable' : false,
	            'mRender':function(data,type,row){
	            	return formatCurrency(row.totalAmount);
				}
	        }, {
	            'mDataProp' : 'feeType',
	            'bSortable' : false,
	            'mRender'   : function(data, type, row) {
	            	return getDictLabel(config.feeType, row.feeType);
	            }
	            
	        }, {
				'mDataProp' : 'manageFeeRate',
				'bSortable' : false,
				'mRender':function(data, type, row) {
					if(!!data){
						if(row.feeType==1){
							return row.manageFeeRate+"%";
						}else if(row.feeType==2){
							return row.riskFeeRate+"%";
						}else if(row.feeType==3){
							return row.guaranteeFeeRate+"%";
						}else{
							return row.introducerFeeRate+"%";
						}
					}else{
						return "";
					}
				}
			}, {
	            'mDataProp' : 'amount',
	            'bSortable' : false,
	            'mRender':function(data){
					return data==null?"":formatCurrency(data);
				}
	        }, {
	            'mDataProp' : 'chargeMemberName',
	            'bSortable' : false,
	            'mRender':function(data,type,row){
					if(row.memberId=="110800000000"){
						return "平台";
					}else{
						return data;
					}
				}
	        }, {
	            'mDataProp' : 'feeStatus',
	            'bSortable' : false,
	            'mRender':function(data,type,row){
	            	if(!!data){
	            		if(row.feeStatus==1||row.feeStatus==2){
	            			return "待收取";
	            		}else if(row.feeStatus==3||row.feeStatus==4){
	            			return "已收取";
	            		}else if(row.feeStatus==5){
	            			return "已归还";
	            		}
	            	}else{
	            		return "";
	            	}
				}
	        }, {
	            'mDataProp' : 'gatherTime',
	            'bSortable' : false,
	            'mRender':function(data){
					return data==null?"_":formatDate(data,"yyyy-mm-dd HH:mm:ss");
				}
	        }, {
	            'mDataProp' : 'returnTime',
	            'bSortable' : false,
	            'mRender':function(data){
					return data==null?"_":formatDate(data,"yyyy-mm-dd HH:mm:ss");
				}
	        }, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return getAllOperation(row);
				}
			}
		]
	});
	
	//查询
	$('#query_manage').on('click', function() {
		manageTable.fnSettings()._iDisplayStart=0;
		manageTable.fnDraw({
			"fnServerParams": function(aoData) {
				getAllSearchValue(aoData);
			}
		});
		return false;
	});

	//重置
	$('#reset_manage').on('click', function() {
		$('#manage_form')[0].reset();
	});
	
	//获取操作按钮
	function getAllOperation(row) {
		var status = row.feeStatus,feeType=row.feeType;
		var voucher = "<button class='btn btn-xs btn-warn btn-primary voucher permission-" + config.permission.voucher + "' data-value='"+row.projectId+"' data-totalAmount='"+row.totalAmount+"' " +
				"data-manageFeeRate='"+row.manageFeeRate+"' data-amount='"+row.amount+"' >借款协议凭证</button> ";
		if (status==3&&feeType==1) { //存盘
				return voucher;
		}else{
				return "-";
		}
	}
	
	//借款协议凭证
	$("#management-info-table").on("click", '.voucher', function() {
		var projectId = $(this).attr("data-value"); 
		
		$("#b-voucher-table").find("span[name='totalAmount']").html(formatCurrency($(this).attr("data-totalAmount")));
		$("#b-voucher-table").find("span[name='manageFeeRate']").html($(this).attr("data-manageFeeRate")+"%");
		$("#b-voucher-table").find("span[name='amount']").html(formatCurrency($(this).attr("data-amount")));
		
		$("#agreementUrl").attr('data-id',projectId);
		//$('#agreementUrlA').attr('href','agreement?projectId='+projectId); 
		$.ajax({
			url:config.urlMap.borrowerInformation,
			data:{
				'projectId':projectId
				},
			type:'post',
			dataType:'json',
			success:function(data){
				if(data.borrowerType==1){
					$("#b-voucher-table").find("span[name='borrowerType']").html("借款人姓名：");
					$("#b-voucher-table").find("span[name='codeType']").html("身份证号码：");
					$("#b-voucher-table").find("span[name='borrowerName']").html(data.borrowerName);
					$("#b-voucher-table").find("span[name='code']").html(data.identityNumber);
				}else{
					$("#b-voucher-table").find("span[name='borrowerType']").html("借款企业名称：");
					$("#b-voucher-table").find("span[name='codeType']").html("社会统一代码：");
					$("#b-voucher-table").find("span[name='borrowerName']").html(data.borrowerName);
					$("#b-voucher-table").find("span[name='code']").html(data.organizationCode);
				}
				$('#modal-table-voucher').modal({
					'show': true
				});
			}
		});
		
	});
	
	//借款协议链接
	$('#agreementUrl').on('click', function() {
		var projectId = $(this).attr("data-id");
		window.top.setIframeBox( "agreement-" + projectId, config.urlMap.agreement +"projectId="+ projectId, "借款协议");
	});
	

})

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
    return (((sign)?'':'-') + num );
}

function autoEndDate(){
	var sDate = $('#form-field-startDate').val(),//开始时间
		eDate = $("#form-field-endDate").val();//结束时间
	//判断结束时间是否大于开始时间
	var num = compareTwoDate(sDate,eDate);
	if (num <= 0) {
		bootbox.alert("结束时间必须大于开始时间！",function(){
			$("#form-field-endDate").val("");
		});
		return false;
	}
}

function autoStartDate(){
	var sDate = $('#form-startDate').val(),//开始时间
		eDate = $("#form-endDate").val();//结束时间
	//判断结束时间是否大于开始时间
	var num = compareTwoDate(sDate,eDate);
	if (num <= 0) {
		bootbox.alert("结束时间必须大于开始时间！",function(){
			$("#form-endDate").val("");
		});
		return false;
	}
}
/*比较两个时间的大小
 * 1:end>start
 * 0:end==start
 * -1:start>end
 * */
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
