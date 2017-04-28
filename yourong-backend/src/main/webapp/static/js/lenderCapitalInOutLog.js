jQuery(function($) {
	$('#capitalInOutLog_form').validate({
		rules : {},
		messages : {}
	});
	
	var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel" ,
            "sFileName": "出借人资金管理.xls",
            "mColumns": [1, 2,3,4,5,6,7,8,9,10 ]
    };
    exportButton.push(excelButton);
	$("select[name='search_payAccountType']").find("option[value='1']").attr("selected",true);
	
	var oTable1 = $('#lenderCapitalInOutLog-table-2').dataTable({
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
			'mDataProp' : 'trueName',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
            	if(row.payAccountType == 2) {
            		return "有融网";
            	} else {
            		return row.trueName;
            	}
            }
		}, {
			'mDataProp' : 'memberId',
			'bSortable' : false
		},{
			'mDataProp' : 'mobile',
			'bSortable' : false
		}, {
			'mDataProp' : 'payAccountType',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.payAccountType, row.payAccountType)
            }
		}, {
			'mDataProp' : 'balance',
			'bSortable' : true
		}, {
			'mDataProp' : 'type',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return getDictLabel(config.type, row.type)
            }
		}, {
			'mDataProp' : 'income',
			'bSortable' : true
		}, {
			'mDataProp' : 'outlay',
			'bSortable' : true
		}, {
			'mDataProp' : 'remark',
			'bSortable' : true
		}, {
			'mDataProp' : 'happenTime',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
				if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
			}
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
	
	$('#query_lenderCapitalInOutLog').on('click', function() {
		oTable1.fnDraw();
	});
	
});
