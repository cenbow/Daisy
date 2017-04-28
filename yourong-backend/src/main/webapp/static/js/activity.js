jQuery(function($) {
	
	//表单验证初始化
	var activityForm = $("#activity_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	//表单验证初始化
	var editForm = $("#edit_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
    
	var activityTable = $('#activity-table').dataTable({
		'bAutoWidth' : false,
		'bFilter': false,
		'bProcessing': false,
		'bSort': true,
		'aaSorting':[[1,"desc"]],
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
				'mDataProp': 'id',
				'bSortable': true
			}, {
				'mDataProp': 'name',
				'bSortable': false
			}, {
				'mDataProp': 'startTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return formatDate(data,"yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'endTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return formatDate(data,"yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'ruleParameterJson',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(typeof(data) == 'undefined') {
						return;
					}
					var len = JSON.stringify(data).length;
					if(len > 100) {
						return data.substring(0, 100) + "...";
					}
					return data;
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var buttons = '';				
					buttons = buttons + "<button class='btn btn-xs btn-inverse btn-primary editRule permission-"+config.permission.edit+"' data-id='"+row.id+"' data-startTime='"+formatDate(row.startTime, "yyyy-mm-dd HH:mm:ss")+"' data-endTime='"+formatDate(row.endTime, "yyyy-mm-dd HH:mm:ss")+"' data-ruleParameter='"+row.ruleParameterJson+"' >修改规则</button> ";
					return buttons;
				}
			}
		]
	});
	
	//查询
	$('#query_customActivity').on('click', function() {
		activityTable.fnDraw();
	});
	
	//修改规则
	$("#activity-table").on("click", '.editRule', function() {
		$("#edit_form")[0].reset();
		var id = $(this).attr("data-id");
		var startTime = $(this).attr("data-startTime");
		var endTime = $(this).attr("data-endTime");
		var ruleParameter = $(this).attr("data-ruleParameter");
		$("#id").val(id);
		$("#startTime").val(startTime);
		$("#endTime").val(endTime);
		if(ruleParameter != "null")
			$("#ruleParameter").val(ruleParameter);
		$('#modal-table').modal({
			'show' : true,
			backdrop : true
		});
	});
	
	//保存规则
	$("#saveRule").on("click", function() {
		if(!editForm.check(false)){
			return false;
		}
		var id = $("#id").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var rule = $("#ruleParameter").val();
		$.post(
			config.urlMap.saveRule,{id:id,startTime:startTime,endTime:endTime,rule:rule},function(data){
				$('#modal-table').modal('toggle');
				activityTable.fnDraw();
			}
		);
	});
});
