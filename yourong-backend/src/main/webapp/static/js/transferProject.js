var transferProjectTable;
jQuery(function($) {
    transferProjectList();
    searchTransferProjectList();
});
/***
 * 导出按钮的权限设置
 */
var exportButton = [];
var excelButton = {
    "sExtends": "xls",
    "sButtonText": "导出Excel",
    "mColumns": [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13,14, 15,16,17 ],
    "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
        sValue = nTr.childNodes[iColumn].innerText;
        if (sValue != "") {
            return "\"" + sValue + "\"";
        } else {
            return sValue;
        }
    },
    "sFileName": "转让项目列表.xls"
};
if(config.permission.transferExcel){
    exportButton.push(excelButton);
}
function transferProjectList(){
    transferProjectTable = $('#transfer-project-table').dataTable({
        "tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'bAutoWidth' : false,
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [{
            'mDataProp': 'id',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + ">";
            }
        }, {
            'mDataProp' : 'id',
            'bSortable' : false
        }, {
            'mDataProp' : 'transferName',
            'bSortable' : false
        }, {
            'mDataProp' : 'transferMember',
            'bSortable' : false
        }, {
            'mDataProp': 'status',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if (data==52||data==70||data==90){
                    return "转让结束";
                }else if (data==30){
                    return "转让中";
                }
                return "未知";
            }
        }, {
			'mDataProp': 'transferEndDate',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if (row.status==52||row.status==70||row.status==90){
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }else{
                	return "-";
                }
			}
		},{
            'mDataProp' : 'projectName',
            'bSortable' : false
        }, {
            'mDataProp' : 'borrowName',
            'bSortable' : false
        }, {
            'mDataProp' : 'transferAmount',
            'bSortable' : false
        }, {
            'mDataProp' : 'income',
            'bSortable' : false
        }, {
            'mDataProp' : 'transferRate',
            'bSortable' : false
        }, {
            'mDataProp' : 'serviceFee',
            'bSortable' : false
        }
        ]
    });
}

function searchTransferProjectList(){
    $("#query_transfer_project").on("click",function(){
        transferProjectTable.fnDraw();
    });
}