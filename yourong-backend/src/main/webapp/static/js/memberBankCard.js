jQuery(function($) {
	
	//表单验证初始化
	var memberBankCardForm = $("#memberBankCard_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
    
	var memberBankCardTable = $('#memberBankCard-table-2').dataTable({
		'bAutoWidth' : false,
		'bFilter': false,
		'bProcessing': false,
		'bSort': false,
		'aaSorting':[[1,"desc"]],
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
				'mDataProp': 'trueName',
				'bSortable': false
			}, {
				'mDataProp': 'memberId',
				'bSortable': false
			}, {
				'mDataProp': 'simpleName',
				'bSortable': false
			}, {
				'mDataProp': 'cardNumberFormat',
				'bSortable': false
			}, {
				'mDataProp': 'bankMobile',
				'bSortable': false
			}, {
				'mDataProp': 'cardType',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == 1)
						return "一般卡";
					if(data == 2)
						return "快捷支付卡";
					return data;
				}
			}, {
				'mDataProp': 'isSecurity',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == 1)
						return "是";
					if(data == 0)
						return "否";
					return data;
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var buttons = '';
					if(config.permission.deleteSafeCard && row.isSecurity == 1)
						buttons = buttons + "<button class='btn btn-xs btn-info btn-primary deleteSafeCard' data-memberId='"+row.memberId+"'>删除安全卡</button> ";
					return buttons;
				}
			}
		]
	});
	
	//删除安全卡
	$("#memberBankCard-table-2").on("click", '.deleteSafeCard', function() {
		var memberId = $(this).attr("data-memberId");
		bootbox.confirm("你确定要删除吗?", function(result) {
			if(!result)return;
			$.post(config.urlMap.deleteSafeCard, {memberId:memberId}, function(data) {
				if (data.success) {
					bootbox.alert("删除成功！",function(){
						memberBankCardTable.fnDraw();
					});
				} else {
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("删除失败！");
					}
				}
			});
		});
	});
	
	//查询
	$('#query_memberCard').on('click', function() {
		memberBankCardTable.fnDraw();
	});
	
});