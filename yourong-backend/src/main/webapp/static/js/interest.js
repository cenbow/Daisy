jQuery(function($) {

	/***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel",
            "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14 ],
            "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            	sValue = nTr.childNodes[iColumn].innerText;
            	 if (sValue != "") {
                     return "=\"" + sValue + "\"";
                 } else {
                	 return sValue;
                 }
            },
            "sFileName": "本息查询.xls"
    };
    if(config.permission.infoExcel){
    	exportButton.push(excelButton);
    }
	
    var interestTable = $('#interest-table').dataTable({
    		"tableTools": {//excel导出
                "aButtons": exportButton,
                "sSwfPath": config.swfUrl
            },
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
    				'bSortable': false,
    				'mRender': function(data, type, row) {
    					return  "<input type='checkbox' value=" + row.id + ">";;
    				}
    			},{
    				'mDataProp': 'id',
    				'bSortable': false
    				
    			}, {
    				'mDataProp': 'transactionId',
    				'bSortable': false
    				
    			},{
    				'mDataProp': 'memberId',
    				'bSortable': false
    				
    			}, {
    				'mDataProp': 'startDate',
    				'bSortable': true,
    				'mRender': function(data, type, row) {
    					return formatDate(data,"yyyy-mm-dd");
    				
    				}
    			}, {
    				'mDataProp': 'endDate',
    				'bSortable': true,
    				'mRender': function(data, type, row) {
    				return formatDate(data,"yyyy-mm-dd");
    				}
    			}, {
    				'mDataProp': 'status',
    				'bSortable': false,
    				'mRender': function(data, type, row) {
    						return getDictLabel(config.status, row.status);
    				}
    			}, {
    				'mDataProp': 'payableInterest',
    				'bSortable': false
    			}, {
    				'mDataProp': 'payablePrincipal',
    				'bSortable': false
    			},  {
    				'mDataProp': 'extraInterest',
    				'bSortable': false
    			}, {
    				'mDataProp': 'realPayPrincipal',
    				'bSortable': false
    			}, {
    				'mDataProp': 'realPayInterest',
    				'bSortable': false
    			}, {
    				'mDataProp': 'payTime',
    				'bSortable': true,
    				'mRender': function(data, type, row) {
    					if(data!=null){
    						return formatDate(data,"yyyy-mm-dd HH:mm:ss");
    					}else{
    						return null;
    					}
    	            }
    				
    			}, {
    				'mDataProp': 'createTime',
    				'bSortable': true,
    				'mRender': function(data, type, row) {
    					if(data!=null){
    						return formatDate(data,"yyyy-mm-dd HH:mm:ss");
    					}else{
    						return null;
    					}
    	            }
    			}, {
    				'mDataProp': 'updateTime',
    				'bSortable': true,
    				'mRender': function(data, type, row) {
    					if(data!=null){
    						return formatDate(data,"yyyy-mm-dd HH:mm:ss");
    					}else{
    						return null;
    					}
    				}
    			}
    		]
    	});
  //query
	$('#query_interest').on('click', function() {
		interestTable.fnDraw();
	});
    
    
    

});
