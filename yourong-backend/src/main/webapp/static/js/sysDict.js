jQuery(function($) {
	$('#sysDict_form').validate({
		rules : {},
		messages : {}
	});
	var oTable1 = $('#sysDict-table-2').dataTable({
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
			'mDataProp' : 'groupName',
			'bSortable' : true
		}, {
			'mDataProp' : 'label',
			'bSortable' : true
		}, {
			'mDataProp' : 'key',
			'bSortable' : true
		}, {
			'mDataProp' : 'value',
			'bSortable' : true
		}, {
			'mDataProp' : 'description',
			'bSortable' : true
		}, {
			'mDataProp' : 'sort',
			'bSortable' : true
		}, {
			'mDataProp' : 'statusStr',
			'bSortable' : true
		}, {
			'mDataProp' : 'createTimeStr',
			'bSortable' : true
		}, {
			'mDataProp' : 'updateTimeStr',
			'bSortable' : true
		}, {
			'mDataProp' : 'remarks',
			'bSortable' : true
		} ]
	});
	$('table th input:checkbox').on(
			'click',
			function() {
				var that = this;
				$(this).closest('table').find(
						'tr > td:first-child input:checkbox').each(function() {
					this.checked = that.checked;
					$(this).closest('tr').toggleClass('selected');
				});
			});
	$('#query_sysDict').on('click', function() {
		oTable1.fnDraw();
	});
	$('#new_sysDict').on('click', function() {
		$(".modalFormTitle").text("添加字典");
		$('#modal-table').modal({
			'show' : true
		});
	});
	$('#edit_sysDict').on('click', function() {
		$(".modalFormTitle").text("编辑字典");
        var id = 0,
            checked = $('table tr > td input:checked');
        if (checked.length) {
            id = checked.first().val();
            $("#sysDict_form").xform("load", (config.urlMap.show + id));
            $('#modal-table').modal('show');
        } else {
            bootbox.alert("请选择数据！");
        }
	});
	$('#delete_sysDict').on('click', function() {
		var ids = [];
		$('table tr > td input:checked').each(function() {
			ids.push($(this).val());
		});
		if (ids.length == 0) {
			bootbox.alert("请选择数据！");
			return;
		}
		//console.log(ids);
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (result) {
				$.post(config.urlMap.delet, {
					"id" : ids
				}, function(data) {
					console.log(data);
					oTable1.fnDraw();
				});
			}
		});
	});
	function checkform(formData, jqForm, options) {
		return $("#sysDict_form").valid();
	}
	$("#save_sysDict").on('click', function() {
		var options = {
			beforeSubmit : checkform,
			url : config.urlMap.save,
			type : "post",
			resetForm : true,
			success : function(data) {
				$('#modal-table').modal('toggle');
				oTable1.fnDraw();
			}
		};
		$('#sysDict_form').ajaxSubmit(options);
	});
});
