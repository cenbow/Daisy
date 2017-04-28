jQuery(function($) {
	$('#cmsComment_form').validate({
		rules : {},
		messages : {}
	});
	var oTable1 = $('#cmsComment-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
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
			'mDataProp' : 'categoryId',
			'bSortable' : true
		}, {
			'mDataProp' : 'articleId',
			'bSortable' : true
		}, {
			'mDataProp' : 'title',
			'bSortable' : true
		}, {
			'mDataProp' : 'content',
			'bSortable' : true
		}, {
			'mDataProp' : 'name',
			'bSortable' : true
		}, {
			'mDataProp' : 'ip',
			'bSortable' : true
		}, {
			'mDataProp' : 'auditUserId',
			'bSortable' : true
		}, {
			'mDataProp' : 'auditTime',
			'bSortable' : true,
			'mRender' : function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp' : 'createTime',
			'bSortable' : true,
			'mRender' : function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		}]
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
	$('#query_cmsComment').on('click', function() {
		oTable1.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	$('#new_cmsComment').on('click', function() {
		$('#modal-table').modal({
			'show' : true
		});
	});
	$('#edit_cmsComment').on('click', function() {
		var id = $('table tr > td input:checked').first().val();
		$.post(config.urlMap.show + id, {}, function(data) {
			// console.log(data);
			$('#cmsComment_form').form("load", data);
			$('#modal-table').modal('show');
		});
	});
	$('#delete_cmsComment').on('click', function() {
		var ids = [];
		$('table tr > td input:checked').each(function() {
			ids.push($(this).val());
		});
		if (ids.length == 0) {
			bootbox.alert("请选择数据！");
			return;
		}
		// console.log(ids);
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
		return $("#cmsComment_form").valid();
	}
	$("#save_cmsComment").on('click', function() {
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
		$('#cmsComment_form').ajaxSubmit(options);
	});
});
