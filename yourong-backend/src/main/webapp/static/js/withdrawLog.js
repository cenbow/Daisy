jQuery(function($) {
	$('#withdrawLog_form').validate({
		rules : {},
		messages : {}
	});
	
	/***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel",
            "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            	sValue = nTr.childNodes[iColumn].innerText;
            	 if (sValue != "") {
                     return "=\"" + sValue + "\"";
                 } else {
                	 return sValue;
                 }
            },
            "sFileName": "托管提现.xls"
    };
    if(config.permission.exportExcel){
    	exportButton.push(excelButton);
    }
	var oTable1 = $('#withdrawLog-table-2').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
		'bFilter' : false,
		'bProcessing' : true,
		'aaSorting':[[1,"desc"]],
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			aoData.push({
				"name": "search_notExistFlag",
				"value": "1"
			});
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
			'mDataProp' : 'withdrawNo',
			'bSortable' : false
		}, {
			'mDataProp' : 'withdrawAmount',
			'bSortable' : true
		}, {
			'mDataProp' : 'withdrawTime',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				if(data==null){
					return "";
				}else{
					return formatDate(data,'yyyy-mm-dd HH:mm:ss');
				}
			}
		}, {
			'mDataProp' : 'arrivedAmount',
			'bSortable' : true
		}, {
			'mDataProp' : 'processTime',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				if(data==null){
					return "";
				}else{
					return formatDate(data,'yyyy-mm-dd HH:mm:ss');
				}
			}
		}, {
			'mDataProp' : 'updateTime',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				if(data==null){
					return "";
				}else{
					return formatDate(data,'yyyy-mm-dd HH:mm:ss');
				}
			}
		}/*, {
			'mDataProp' : 'bankCardId',
			'bSortable' : false
		}*/, {
			'mDataProp' : 'trueName',
			'bSortable' : false
		}, {
			'mDataProp' : 'mobile',
			'bSortable' : false
		}/*, {
			'mDataProp' : 'fee',
			'bSortable' : false
		}*/, {
			'mDataProp' : 'notice',
			'bSortable' : false
		}, {
			'mDataProp' : 'status',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.state, row.status)
            }
		}, {
			'mDataProp' : 'operateName',
			'bSortable' : false
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
	
	$('#query_withdrawLog').on('click', function() {
		oTable1.fnDraw();
	});
	$('#new_withdrawLog').on('click', function() {
		$('#modal-table').modal({
			'show' : true
		});
	});
	$('#edit_withdrawLog').on('click', function() {
		var id = $('table tr > td input:checked').first().val();
		$.post(config.urlMap.show + id, {}, function(data) {
			//console.log(data);
			$('#withdrawLog_form').form("load", data);
			$('#modal-table').modal('show');
		});
	});
	
	$('#delete_withdrawLog').on('click', function() {
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
		return $("#withdrawLog_form").valid();
	}
	$("#save_withdrawLog").on('click', function() {
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
		$('#withdrawLog_form').ajaxSubmit(options);
	});
});
