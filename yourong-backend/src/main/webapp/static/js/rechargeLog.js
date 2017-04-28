jQuery(function($) {
	$('#rechargeLog_form').validate({
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
            "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16],
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            	sValue = nTr.childNodes[iColumn].innerText;
            	 if (sValue != "") {
                     return "=\"" + sValue + "\"";
                 } else {
                	 return sValue;
                 }
            },
            "sFileName": "充值.xls"
    };
    if(config.permission.exportExcel){
    	exportButton.push(excelButton);
    }
	
	
	var oTable1 = $('#rechargeLog-table-2').dataTable({
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
			'mDataProp' : 'rechargeNo',
			'bSortable' : false
		}, {
			'mDataProp' : 'type',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.type, row.type)
            }
		}, {
			'mDataProp' : 'orderNo',
			'bSortable' : true
		}, {
			'mDataProp' : 'amount',
			'bSortable' : true
		}, {
			'mDataProp' : 'fee',
			'bSortable' : true
		}, {
			'mDataProp' : 'rechargeTime',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
				if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp' : 'trueName',
			'bSortable' : false
		}, {
			'mDataProp' : 'mobile',
			'bSortable' : false
		}, {
			'mDataProp' : 'payMethod',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.payMethod, row.payMethod)
            }
		}, {
			'mDataProp' : 'remarks',
			'bSortable' : true
		}, {
			'mDataProp' : 'bankCode',
			'bSortable' : true
		}, {
			'mDataProp' : 'status',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.status, row.status)
            }
		}, {
			'mDataProp' : 'updateTime',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
				if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp' : 'bankCardId',
			'bSortable' : true
		}, {
			'mDataProp' : 'rechargeSource',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
				var value = data;
				if(data == null) {
					value = 0;
				}
            	return getDictLabel(config.rechargeSource, value)
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
	
	$('#query_rechargeLog').on('click', function() {
		oTable1.fnDraw();
	});
	
});
