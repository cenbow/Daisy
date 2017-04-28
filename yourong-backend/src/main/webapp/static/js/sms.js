var smsRemindTable;
var smsForm;
jQuery(function($) {
	initSmsList();
	//表单验证初始化
	smsForm = $("#sms_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
});
	// 获取操作按钮
	function getAllOperation(row) {
		var edit = "<button class='btn btn-xs btn-info sms-edit  permission-" + config.permission.show + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>编辑</i></button>"; // 编辑
		var del = "<button class='btn btn-xs btn-danger sms-del permission-" + config.permission.del + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
		return edit+del;
	}


	/**
	 * 短信预警设置列表
	 */
	function initSmsList(){
		 smsRemindTable = $('#msg-sms-table').dataTable({
			'bFilter' : false,
			'bProcessing' : true,
			'bSort' : true,
			'aaSorting':[[1,"desc"]],
			'bServerSide' : true,
			'fnServerParams' : function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource' : config.urlMap.ajax,
			'aoColumns' : [ {
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp' : 'id'
			}, {
				'mDataProp' : 'label',
				'bSortable' : true
			}, {
				'mDataProp' : 'description',
				'bSortable' : true
			}, {
				'mDataProp' : 'value',//号码
				'bSortable' : true
			}, {
				'mDataProp' : 'remarks',//预警余额
				'bSortable' : false
			}, {
				'mDataProp': 'key',//预警账号
				'bSortable': false
			},{
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return getAllOperation(row);
				}
			}]
		});
		initSmsAcount();
	}
	


	// 新增 dialog
	$('#new_sms').on('click', function() {
		$("#smsTitle").text("新增短信预警");
		$('#modal-table').modal('show');
	});

	// 保存save
	$('#save_sms').on('click', function() {
		$("#sort").val(1);
		$("#groupName").val("sms_remind");
		if (smsForm.check(false)) {
			$('#sms_form').xform('post', config.urlMap.save, function(data) {
				if (!data.success) {
					bootbox.alert("保存失败！");
				} else {
					bootbox.alert("保存成功！", function() {
						smsRemindTable.fnDraw();
						smsForm.resetForm(); //验证重置
					});
				}
			});
		}
	});
	
	//取消 cancel
	$('#cancel_couponTemplate').on('click', function() {
		smsForm.resetForm(); //验证重置
	});

	
	//编辑 edit
	$("#msg-sms-table").on("click", '.sms-edit', function() {
		var id = $(this).attr("data-id");
		$("#smsTitle").text("修改短信预警设置");
		$('#modal-table').modal("show");
		smsForm = $("#sms_form").Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});

		$('#sms_form').xform("load", config.urlMap.show + id, function(data) {
		});
	});
	//删除 delet
	$("#msg-sms-table").on("click", '.sms-del', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.del + id, function(data) {
				if (data.success) {
					bootbox.alert("删除成功！", function() {
						smsRemindTable.fnDraw();
					});
				} else {
					bootbox.alert("删除失败！");
				}
			});
		});
	});

//预警账号显示
function initSmsAcount(){
	$.post(config.urlMap.getSmsCdkeyList,function(data){
		if(data.success) {
			var res = data.resultList;
			$("#sms-table-query tbody tr ").remove();
			if (!!res && res.length > 0) {
				for (var i = 0; i < res.length; i++) {
					var index = i + 1;
					var trObj = $("<tr>" +
							"<td name='value[" + index + "]'>" + res[i].value +"</td>" +
							"<td name='label[" + index + "]'><span>" + res[i].label + "</span></td>" +
							"<td name='description[" + index + "]'>" + res[i].description +"</td>" +
							"<td > <button  class='btn btn-xs btn-info sms-search permission-" + config.permission.del + "' data-id='" + res[i].id  + "'><i class='icon-search bigger-120'>查询</i></button>" + "</td>" +
					"</tr>");
					$("#sms-table-query tbody ").append(trObj);
				}
			}
		}
	});
}
//短信余额查询
$("#sms-table-query").on("click", '.sms-search', function() {
	var id = $(this).attr("data-id");
		$.ajax({
			url:config.urlMap.queryBalance,
			data:{'id':id},
			dataType:"json",
			success:function(data){
				if (data.success) {
					initSmsAcount();
				} else {
					bootbox.alert("余额查询失败！");
				}
			}
		});
});
