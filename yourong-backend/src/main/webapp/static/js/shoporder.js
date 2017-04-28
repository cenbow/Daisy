var orderTable;
jQuery(function($) {
    orderList();
    searchDirectProjectList();
});

//获取操作按钮
function getAllOperation(row) {
    var status = row.status;
    var type = row.rechargeType;
    var give = "<button class='btn btn-xs btn-warning order-give permission-" + config.permission.sendRemark + "' data-id='" + row.orderId + "' data-sendremark='" + row.sendRemark + "' ><i class='bigger-130'>发放</i></button>";
    var failgive = "<button class='btn btn-xs btn-warning order-failgive permission-" + config.permission.failgive + "' data-id='" + row.orderId + "' ><i class='bigger-130'>发放信息</i></button>";
    var recharge = "<button class='btn btn-xs btn-info order-recharge permission-" + config.permission.sendRemark + "' data-id='" + row.orderId + "' data-sendremark='" + row.sendRemark + "' ><i class='bigger-130'>充值</i></button>";
    var remark = "<button class='btn btn-xs btn-info order-remark permission-" + config.permission.Remark + "' data-id='" + row.orderId + "' ><i class='bigger-130'>备注</i></button>";
    var button=remark;
    if (status==1) {
        if (type==1){
            button+='     '+recharge;
        }else {
            button+='     '+give;
        }
    }
    if (status==5){
        if (!row.sendRemark){
            button+='     '+failgive;
        }
    }
    return button;
}
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
    "sFileName": "商品兑换列表.xls"
};
if(config.permission.orderExcel){
    exportButton.push(excelButton);
}
function orderList(){
    orderTable = $('#order-table').dataTable({
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
        'aoColumns': [ {
            'mDataProp' : 'orderId',
            'bSortable' : false
        }, {
            'mDataProp' : 'trueName',
            'bSortable' : false
        }, {
            'mDataProp' : 'memberMobile',
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
        }, {
            'mDataProp' : 'goodsName',
            'bSortable' : false
        }, {
            'mDataProp' : 'goodsType',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data==1){
                    return "投资专享";
                }else if (data==2){
                    return "虚拟卡券";
                }else if (data==3){
                    return "超值实物";
                }else if (data==4){
                    return "双节特惠";
                }
            }
        }, {
            'mDataProp' : 'receiver',
            'bSortable' : false
        }, {
            'mDataProp' : 'takeMobile',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (row.goodsType==2&&row.rechargeType==1){
                    return row.rechargeCard;
                }else {
                    return row.takeMobile;
                }
            }
        }, {
            'mDataProp' : 'address',
            'bSortable' : false
        }, {
            'mDataProp' : 'status',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data==1){
                    return "待发放";
                }else if (data==2){
                    return "已发放";
                }else if (data==3){
                    return "已取消";
                }else if (data==4){
                    return "处理中";
                }else if (data==5){
                    return "充值失败";
                }
            }
        }, {
            'mDataProp' : 'sendTime',
            'bSortable' : false,
            'mRender': function(data, type, row) {
                if (data){
                    return formatDate(data, "yyyy-mm-dd HH:mm:ss");
                }
                return "";
            }
        }, {
            'mDataProp' : 'sendRemark',
            'bSortable' : false
        }, {
            'mDataProp' : 'remark',
            'bSortable' : false
        }, {
            'mDataProp': 'operation',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return getAllOperation(row);
            }
        }
        ]
    });
}

function searchDirectProjectList(){
    $("#query_order").on("click",function(){
        orderTable.fnDraw();
    });
}

//发放
$("#order-table").on("click", '.order-give', function() {
    var id = $(this).attr("data-id");
    var sendremark=$(this).attr("data-sendremark");
    $('#orderid').val(id);
    if (!sendremark||sendremark=="null"){
        $('#sendremark').val("");
    }else {
        $('#sendremark').val(sendremark);
    }
    $('#modal-take').modal('show');
});

//添加发货信息
$("#btn_add_sendremarks").on("click",function(){
    var that = $(this);
    var orderid=$('#orderid').val();
    var sendremark= $("#sendremark").val();
    $.post(
        config.urlMap.sendremark,{orderid:orderid,remark:sendremark},function(data){
            if(data.success){
                $('#modal-take').modal('hide');
                orderTable.fnDraw();
            }else{
                bootbox.alert("添加发货信息失败！");
            }
        }
    );
});

//查询多贝余额
$("#query_balance").click(function(){
    $('#duobei_balance').text("");
    $.post(
        config.urlMap.balance,function(data){
            if(data.success){
                $('#duobei_balance').text(data.result);
                $('#modal-balance').modal('show');
            }
        }
    );
});

//充值
$("#order-table").on("click", '.order-recharge', function() {
    var id = $(this).attr("data-id");
    bootbox.confirm("你确定要充值吗?", function(result) {
        if (result) {
            $.post(
                config.urlMap.recharge,{orderid:id},function(data){
                    if(data.success) {
                        bootbox.alert("充值处理中！");
                        orderTable.fnDraw();
                    }else {
                        bootbox.alert(data.result);
                        orderTable.fnDraw();
                    }
                }
            );
        }
    });
});

//备注
$("#order-table").on("click", '.order-remark', function() {
    var id = $(this).attr("data-id");
    $('#orderRemarkId').val(id);
    $.post(
        config.urlMap.queryremark,{orderid:id},function(data){
            if(data.success){
                $('#orderRemark').val(data.result);
                $('#modal-orderRemark').modal('show');
            }else{
                $('#remark').val("");
                $('#modal-orderRemark').modal('show');
            }
        }
    );
});

//添加备注
$("#btn_add_orderremarks").on("click",function(){
    var orderid=$('#orderRemarkId').val();
    var remark= $("#orderRemark").val();
    $.post(
        config.urlMap.remark,{orderid:orderid,remark:remark},function(data){
            if(data.success){
                $('#modal-orderRemark').modal('hide');
                orderTable.fnDraw();
            }else{
                bootbox.alert("添加备注失败！");
            }
        }
    );
});

//发放
$("#order-table").on("click", '.order-failgive', function() {
    var id = $(this).attr("data-id");
    $('#failorderid').val(id);
    $('#failsendremark').val('非常抱歉，本次充值失败，人气值已退回您的账户啦~请查收（欢迎重新兑换）。');
    $('#modal-failtake').modal('show');
});

//添加失败的发货信息
$("#btn_add_failsendremarks").on("click",function(){
    var that = $(this);
    var orderid=$('#failorderid').val();
    var sendremark= $("#failsendremark").val();
    $.post(
        config.urlMap.updatesendremark,{orderid:orderid,remark:sendremark},function(data){
            if(data.success){
                $('#modal-failtake').modal('hide');
                orderTable.fnDraw();
            }else{
                bootbox.alert("添加发货信息失败！");
            }
        }
    );
});