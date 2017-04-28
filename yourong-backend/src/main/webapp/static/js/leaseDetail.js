jQuery(function($) {
	$('#leaseDetail_form').validate({
		rules : {},
		messages : {}
	});
	var oTable1 = $('#leaseDetail-table-2').dataTable({
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
			'mDataProp' : 'leaseBonusId',
			'bSortable' : true
		}, {
			'mDataProp' : 'startDate',
			'bSortable' : true
		}, {
			'mDataProp' : 'endDate',
			'bSortable' : true
		}, {
			'mDataProp' : 'rental',
			'bSortable' : true
		}, {
			'mDataProp' : 'createTime',
			'bSortable' : true
		}, {
			'mDataProp' : 'updateTime',
			'bSortable' : true
		}, {
			'mDataProp' : 'remark',
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
	$('#query_leaseDetail').on('click', function() {
		oTable1.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	$('#new_leaseDetail').on('click', function() {
		$('#modal-table').modal({
			'show' : true
		});
	});
	$('#edit_leaseDetail').on('click', function() {
		var id = $('table tr > td input:checked').first().val();
		$.post(config.urlMap.show + id, {}, function(data) {
			//console.log(data);
			$('#leaseDetail_form').form("load", data);
			$('#modal-table').modal('show');
		});
	});
	$('#delete_leaseDetail').on('click', function() {
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
		return $("#leaseDetail_form").valid();
	}
	$("#save_leaseDetail").on('click', function() {
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
		$('#leaseDetail_form').ajaxSubmit(options);
	});
});
