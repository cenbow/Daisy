jQuery(function($) {
	

	//表单验证初始化
	var bscBankFrom = $("#bscBank_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	var bscBankTable = $('#bscBank-table-2').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'order': [
		            [0, "desc"]
		        ],
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
				'mDataProp': 'id',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return "<input type='checkbox' name='bankSel' value=" + row.id + ">";
				}
			}
			, {
				'mDataProp': 'id',
				'bSortable': true
			}, {
				'mDataProp': 'simpleName',
				'bSortable': true
			}, {
				'mDataProp': 'fullName',
				'bSortable': true
			}, {
				'mDataProp': 'code',
				'bSortable': true
			}, {
				'mDataProp': 'status',
				'bSortable': true,
				'mRender': function(data) {
					if(data == 1) {return "使用";}
					else if(data == -1) {return "不使用";}
					else {return "";}
				}
			}, {
				'mDataProp': 'createTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == "") {
						return "";
					}
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'updateTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == "") {
						return "";
					}
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var buttons = '';
					buttons = buttons + "<button class='btn btn-xs btn-info btn-primary editBank permission-"+config.permission.edit+"' data-id='"+row.id+"' data-simpleName='"+row.simpleName+"' data-fullName='"+row.fullName+"' data-code='"+row.code+"' data-status='"+row.status+"' >修改</button> ";
					return buttons;
				}
			}
		]
	});
	
	$('table th input:checkbox').on('click', function() {
		var that = this;
		$(this).closest('table').find(
				'tr > td:first-child input:checkbox').each(function() {
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});

	//查询
	$('#query_bscBank').on('click', function() {
		bscBankTable.fnDraw();
	});
	
	//新增银行
	$('#new_bscBank').on('click', function() {
		$(".modalFormTitle").text("新增银行");
		$("input[name='bankInfo']").each(function () {
            $(this).val('');
        });
		$("#status").find("option[value='1']").attr("selected",true);
		$('#modal-table').modal({
			'show' : true,
			backdrop : true
		});
	});
	
	//新增保存
	$('#save_bscBank').on('click', function() {
		var notice = '';
		$("input[name='bankInfo']").each(function () {
            if(trimStr($(this).val()) == '' && $(this).attr("placeholder") != undefined) {
            	if(notice == '') {
            		notice += $(this).attr("placeholder");
            	} else {
            		notice += "、" + $(this).attr("placeholder");
            	}
            };
        });
		if(notice != '') {
			bootbox.alert(notice + "不能为空！");
		} else {
			var id = $('#id').val();
			var simpleName = $('#simpleName').val();
			var fullName = $('#fullName').val();
			var code = $('#code').val();
			var status = $('#status').val();
			$(this).addClass("disabled");
			var that = $(this);
			$.post(
				config.urlMap.save,{id:id, simpleName:simpleName, fullName:fullName, code:code, status:status},function(data){
					that.removeClass("disabled");
					if(data.success){
						$('#modal-table').modal('hide');
						bscBankTable.fnDraw();
					}else{
						var remarks = '';
						if(data.result.remarks != undefined)
							remarks = data.result.remarks;
						bootbox.alert("操作失败！" + remarks);
					}
				}
			);
		}
		
	});
	
	//修改
	$("#bscBank-table-2").on("click", '.editBank', function() {
		$(".modalFormTitle").text("编辑银行");
		var id = $(this).attr("data-id");
		var simpleName = $(this).attr("data-simpleName");
		var fullName = $(this).attr("data-fullName");
		var code = $(this).attr("data-code");
		var status = $(this).attr("data-status");
		$("#status").find("option[value='"+status+"']").attr("selected",true);
		$("#id").val(id);
		$("#simpleName").val(simpleName);
		$("#fullName").val(fullName);
		$("#code").val(code);
		$("#status").val(status);
		$('#modal-table').modal({
			'show' : true,
			backdrop : true
		});
	});

	//删除银行
	$('#delete_bscBank').on('click', function() {
		var ids = new Array();
		$("input[name='bankSel']").each(function () {
            if($(this).context.checked) {
            	ids.push(parseInt($(this).context.value));
            }
        });
		if(ids.length > 0) {
			bootbox.confirm("你确定要删除吗?", function(result) {
				if(!result)return;
				$.post(config.urlMap.del, {ids:ids}, function(data) {
					if (data.success) {
						bootbox.alert("删除成功！",function(){
							bscBankTable.fnDraw();
						});
					} else {
						boobbox.alert("删除失败！");
					}
				});
			});
		}
	});

});

