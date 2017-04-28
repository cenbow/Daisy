jQuery(function($) {
	
	 /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel" ,
            "sFileName": "托管放款管理.xls",
            "mColumns":[0,1,2,3,4,5,6,7,8,9,10,11]
    };
    if(config.permission.indexExcel){
    	exportButton.push(excelButton);
    }
	
	//列表数据
	var loanDetailTable = $('#loan-detail-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
		'order':[[0,"desc"]],
		'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.query,
        'aoColumns': [
                    	{
                          'mDataProp': 'projectId',
                          'bSortable':true
                      }, {
                          'mDataProp': 'projectName',
                          'bSortable': false
                      }, {
                          'mDataProp': 'projectStatus',
                          'bSortable': false,
                          'mRender': function(data, type, row) {
                        	  return getDictLabel(config.projectStatus, data);
                          }
                      }, {
                          'mDataProp': 'onlineTime',
                          'bSortable': false,
                          'mRender': function(data, type, row) {
                              return formatDate(data,"yyyy-mm-dd HH:mm:ss");
                          }
                      }, {
                          'mDataProp': 'saleEndTime',
                          'bSortable': false,
                          'mRender': function(data, type, row) {
                              return formatDate(data,"yyyy-mm-dd HH:mm:ss");
                          }
                      }, {
			            'mDataProp': 'lenderName',
			            'bSortable': false
			        }, {
			            'mDataProp': 'totalAmount',
			            'bSortable': false
			        }, {
			            'mDataProp': 'loanAmount',
			            'bSortable': false
			        }, {
			            'mDataProp': 'userPay',
			            'bSortable': false
			        }, {
			            'mDataProp': 'platformPay',
			            'bSortable': false
			        }, {
			            'mDataProp': 'loanStatus',
			            'bSortable': false,
                        'mRender': function(data, type, row) {
                        	  return getDictLabel(config.loanStatus, data);
                          }
			        }, {
			            'mDataProp': 'loanTime',
			            'bSortable': false,
                        'mRender': function(data, type, row) {
                            return formatDate(data,"yyyy-mm-dd HH:mm:ss");
                        }
			        }]
	});
	
	
	$('#query_loan').on('click', function() {
		loanDetailTable.fnSettings()._iDisplayStart=0; 
		loanDetailTable.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
    });
	
		
});