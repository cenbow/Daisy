/**
 * 还本付息管理
 * Created by py on 2015/7/29.
 */
jQuery(function($) {
    var projectId = $('#projectId').val();
    var oTable1 = $('#repayment-hostingCollectTradeList-table').dataTable({
        'bFilter' : false,
        'paging': false,
        'bProcessing' : true,
        'aaSorting':[[1,"desc"]],
        "scrollY": "350px",
        "scrollCollapse": "true",
        'bSort' : false,
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
            getAllSearchValue(aoData);
        },
//		'fnInitComplete':function(){
//				$(".withdrowlog").on("click",function(){
//					var id = $(this).attr("data-value");
//					configOprate(id, 1);
//				});
//				$(".withdrowlogRefuje").on("click",function(){
//					var id = $(this).attr("data-value");
//					configOprate(id, 2);
//				})
//
//			},
        'sAjaxSource' : config.urlMap.ajaxhostingCollectTradeList+projectId,
        'aoColumns' : [
            {
            'mDataProp' : 'id'
        }, {
            'mDataProp' : 'tradeNo'
        }, {
            'mDataProp' : 'outTradeNo'
        },{
            'mDataProp' : 'amount'
        }, {
            'mDataProp' : 'payerId'
        },{
            'mDataProp' : 'payerIp'
        }, {
            'mDataProp' : 'tradeStatus'
        }, {
            'mDataProp' : 'remarks'
        }, {
            'mDataProp' : 'tradeTime',
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        }
        , {
            'mDataProp' : 'updateTime',
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        } , {
            'mDataProp' : 'platformAmount'
        },{
            'mDataProp' : 'operation',
            'mRender':function(data,type,row){
            	var button ="<button id='payTra' class='btn btn-xs btn-danger btn-primary payTradeno permission-"+config.permission.payTradeNo+"' data-tradeno='"+row.tradeNo+"'>处理还本付息代付</button>";
            	if(row.tradeStatus=="TRADE_FINISHED"&&row.type==2){
            		return button;
            	}else{
            		return "";
            	}
            	
            }
        }

        ]
    });

    $('#repayment-hostingCollectTradeList-table-reload').on('click', function() {
        oTable1.fnDraw();
    });

    var oTable2 = $('#repayment-projectInversit-table').dataTable({
        'bFilter' : false,
        'paging': false,
        'bProcessing' : true,
        'bSort' : false,
        'bServerSide' : true,
        "scrollY": "350px",
        "scrollCollapse": "true",
        'fnServerParams' : function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource' : config.urlMap.queryhostingPayTradeList+projectId,
        'aoColumns' : [
            {
                'mDataProp' : 'id'
            }, {
                'mDataProp' : 'status',
                'mRender':function(data,type,row){
                	return getDictLabel(config.status, row.status)
                }
            },{
                'mDataProp' : 'trueName'
            },{
                'mDataProp' : 'mobile'
            }, {
                'mDataProp' : 'payablePrincipal'
            },{
                'mDataProp' : 'payableInterest'
            },{
                'mDataProp' : 'realPayPrincipal'
            },{
                'mDataProp' : 'realPayInterest'
            },{
                'mDataProp' : 'tradeNo'
            } ,{
                'mDataProp' : 'batchPayNo'
            },{
                'mDataProp' : 'outTradeNo'
            },{
                'mDataProp' : 'amount'
            },{
                'mDataProp' : 'tradeStatus'
            },{
                'mDataProp' : 'hostRepayRemarks'
            },{
                'mDataProp' : 'createTime',
                'mRender': function(data, type, row) {
                	if(data == null) {
                		return '';
                	}
                    return formatDate(data,"yyyy-mm-dd HH:mm:ss");
                }
            },{
                'mDataProp' : 'updateTime',
                'mRender': function(data, type, row) {
                	if(data == null) {
                		return '';
                	}
                    return formatDate(data,"yyyy-mm-dd HH:mm:ss");
                }
            }

        ]
    });
    $('#repayment-projectInversit-table-reload').on('click', function() {
        oTable2.fnDraw();
    });

    //同步代收
    $('#repayment-synchronizedHostingCollectTrade').on('click', function() {
    	$("#repayment-synchronizedHostingCollectTrade").attr('disabled',true);
    	$("#repayment-synchronizedHostingCollectTrade").val('执行中,请稍后...');
    	$.post(config.urlMap.sycnCollect, {}, function(data) {
			if (data.success) {
				bootbox.alert("同步代收完成！");
			} else {
				boobbox.alert("同步代收失败！");
			}
			$("#repayment-synchronizedHostingCollectTrade").val('同步代收');
			$("#repayment-synchronizedHostingCollectTrade").attr('disabled',false);
		});
    });
    
    //同步代付
    $('#repayment-synchronizedHostingPayTrade').on('click', function() {
    	$("#repayment-synchronizedHostingPayTrade").attr('disabled',true);
    	$("#repayment-synchronizedHostingPayTrade").val('执行中,请稍后...');
    	$.post(config.urlMap.sycnPay, {}, function(data) {
			if (data.success) {
				bootbox.alert("同步代付完成！");
			} else {
				boobbox.alert("同步代收代付！");
			}
			$("#repayment-synchronizedHostingPayTrade").val('同步代付');
			$("#repayment-synchronizedHostingPayTrade").attr('disabled',false);
		});
    });
    
    /*
     * 根据代收交易号处理还本付息代付
     */
    $('#repayment-hostingCollectTradeList-table').on("click",".payTradeno",function(){
    	var tradeNo = $(this).data("tradeno");
		$.ajax({
			url:config.urlMap.payTradeNo+"?tradeNo="+tradeNo,
			type:"post", 
			dataType:'json',
			success:function(data) {
				if(data.success){
					bootbox.alert("据代收交易号处理还本付息代付成功！");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("据代收交易号处理还本付息代付失败！");
					}
				}
				$("#payTra").attr('disabled',true);
			}
		});
    });
});
