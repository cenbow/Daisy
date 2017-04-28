jQuery(function($) {
	$('#sysMenu_form').validate({
		rules : {},
		messages : {}
	});
	var oTable1 = $('#sysMenu-table-2').dataTable({
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
			'mDataProp' : 'parentId',
			'bSortable' : true
		}, {
			'mDataProp' : 'type',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.menuType, row.type)
            }
		}, {
			'mDataProp' : 'name',
			'bSortable' : false
		}, {
			'mDataProp' : 'href',
			'bSortable' : false
		}, {
			'mDataProp' : 'target',
			'bSortable' : false,
			"bVisible" : false
		}, {
			'mDataProp' : 'icon',
			'bSortable' : false
		}, {
			'mDataProp' : 'sort',
			'bSortable' : true
		}, {
			'mDataProp' : 'statusStr',
			'bSortable' : false
			
		}, {
			'mDataProp' : 'permission',
			'bSortable' : false
		}, {
			'mDataProp' : 'createTimeStr',
			'bSortable' : false
		}, {
			'mDataProp' : 'updateTimeStr',
			'bSortable' : false
		}, {
			'mDataProp' : 'remarks',
			'bSortable' : false
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
	$('#query_sysMenu').on('click', function() {
		oTable1.fnDraw();
	});
	$('#new_sysMenu').on('click', function() {
		$(".modalFormTitle").text("添加菜单");
		$('#modal-table').modal({
			'show' : true
		});
	});
	$('#edit_sysMenu').on('click', function() {
		$(".modalFormTitle").text("编辑菜单");
		var id = $('table tr > td input:checked').first().val();
		$.post(config.urlMap.show + id, {}, function(data) {
			// console.log(data);
			$('#sysMenu_form').form("load", data);
			$('#modal-table').modal('show');
		});
	});
	$('#delete_sysMenu').on('click', function() {
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
		return $("#sysMenu_form").valid();
	}
	$("#save_sysMenu").on('click', function() {
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
		$('#sysMenu_form').ajaxSubmit(options);
	});
	
	$("#clean_sysMenu_1").on("click",function(){
		clearCacheByKey("1");
	});
	$("#clean_sysMenu_2").on("click",function(){
		clearCacheByKey("2");
	});
	$("#clean_sysMenu_3").on("click",function(){
		clearCacheByKey("5");
	});

});
