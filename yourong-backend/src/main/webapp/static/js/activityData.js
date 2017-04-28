jQuery(function($) {
	var dataForm = $("#data_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	var oTable1 = $('#activityData-table').dataTable({
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
			'mDataProp' : 'createTime',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				return formatDate(data,"yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp' : 'dataGole',
			'bSortable' : false
		}, {
			'mDataProp' : 'dataSilver',
			'bSortable' : false
		}, {
			'mDataProp' : 'dataCopper',
			'bSortable' : false
		}, {
			'mDataProp' : 'activityId',
			'bSortable' : false
		}  ]
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
	//添加
	$('#new_activityData').on('click', function() {
		$(".modalFormTitle").text("添加数据");
		$("select[name='activityId']").find("option[value='']").attr("selected", true);
		$('#modal-table').modal({
			'show' : true
		});
	});
	//保存
	$("#saveActivityData").on('click', function() {
		if (dataForm.check(false)) {
			var options = {
					url : config.urlMap.save,
					type : "post",
					resetForm : true,
					success : function(data) {
						if (!data.success) {
							if (!!data.resultCodeEum) {
								bootbox.alert(data.resultCodeEum[0].msg,function(){
								});
							} else {
								bootbox.alert("保存失败!",function(){
									$('#modal-table').modal('toggle');
									oTable1.fnDraw();
								});
							}
						} else {
							bootbox.alert("添加成功!", function() {
								$('#modal-table').modal('toggle');
								oTable1.fnDraw();
							});
						}
					}
				};
				$('#data_form').ajaxSubmit(options);
		}
		
	});
	
	$('#edit_activityData').on('click', function() {
		  var id = 0, checked = $('table tr > td input:checked');
	        if (checked.length) {
	            id = checked.first().val();
	            $("#data_form").xform("load", (config.urlMap.show + id));
	            $('#modal-table').modal('show');
	        } else {
	            bootbox.alert("请选择数据！");
	        }
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
		return $("#data_form").valid();
	}
	
	
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
