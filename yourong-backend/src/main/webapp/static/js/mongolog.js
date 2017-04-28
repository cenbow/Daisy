var loadLogTable;
var inputLogTable;
jQuery(function($) {
    loadlogList();
    inputlogList();
    searchDirectProjectList();
});
function loadlogList(){
    loadLogTable = $('#loadlog-table').dataTable({
        //"tableTools": {//excel导出
        //    "aButtons": exportButton,
        //    "sSwfPath": config.swfUrl
        //},
        'bAutoWidth' : false,
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [ {
            'mDataProp' : 'openId',
            'bSortable' : false
        }, {
            'mDataProp' : 'loadUrl',
            'bSortable' : false
        }, {
            'mDataProp' : 'msg',
            'bSortable' : false
        }, {
            'mDataProp': 'createTime',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if (data){
                    return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }
                return "";
            }
        }
        ]
    });
}

function inputlogList(){
    inputLogTable = $('#serviceInput-table').dataTable({
        //"tableTools": {//excel导出
        //    "aButtons": exportButton,
        //    "sSwfPath": config.swfUrl
        //},
        'bAutoWidth' : false,
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.inputajax,
        'aoColumns': [ {
            'mDataProp' : 'outBizNo',
            'bSortable' : false
        }, {
            'mDataProp' : 'channelKey',
            'bSortable' : false
        }, {
            'mDataProp' : 'serviceName',
            'bSortable' : false
        }, {
            'mDataProp' : 'inputJson',
            'bSortable' : false
        }, {
            'mDataProp': 'createTime',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if (data){
                    return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }
                return "";
            }
        }
        ]
    });
}

function searchDirectProjectList(){
    $("#query_loadlog").on("click",function(){
        loadLogTable.fnDraw();
    });
    $("#query_serviceinputlog").on("click",function(){
        inputLogTable.fnDraw();
    });
}