var ifdata;
jQuery(function($) {
    // dateTable init
    var hostingCollectTrade = $('#hostingCollectTrade-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'fnInitComplete':function(){
			
		},
		'sAjaxSource' : config.urlMap.ajax,
		'aoColumns' : [
   			{
   				'mDataProp' : 'id',
   				'bSortable' : false,
   				'mRender' : function(data, type, row) {
   					return "<input type='checkbox' value=" + row.id + ">";
   				}
		   	}, {
				'mDataProp': 'id',
				'bSortable': true
			}, {
				'mDataProp': 'tradeNo',
				'bSortable': true
			}, {
				'mDataProp': 'outTradeNo',
				'bSortable': true
			}, {
				'mDataProp': 'sourceId',
				'bSortable': true
			}, {
				'mDataProp': 'payerId',
				'bSortable': true
			}, {
				'mDataProp': 'type',
				'bSortable': true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.type, row.type);
				}
			}, {
				'mDataProp': 'remarks',
				'bSortable': false
			}, {
				'mDataProp': 'amount',
				'bSortable': true,
				'mRender'   : function(data, type, row) {
					if(data == "") {
						return "";
					}
					return formatCurrency(data);
				}
			}, {
				'mDataProp': 'payStatus',
				'bSortable': true
			}, {
				'mDataProp': 'summary',
				'bSortable': false
			}, {
				'mDataProp': 'tradeStatus',
				'bSortable': true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.status, row.tradeStatus);
				}
			}, {
				'mDataProp': 'tradeTime',
				'bSortable': true,
				'mRender'   : function(data, type, row) {
					if(!data) {
						return "";
					}
					return formatDate(data,'yyyy-mm-dd HH:mm:ss');
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return getAllOperation(row);
				}
			}
		]
    });
    
	$('#query_hostingCollectTrade').on('click', function() {
		hostingCollectTrade.fnDraw();
	});

	//choose
	$('table th input:checkbox').on('click',function() {
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox').each(function() {
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	//获取操作按钮
	function getAllOperation(row) {
		var queryTrade = "<button class='btn btn-xs btn-success host-queryTrade permission-" + config.permission.queryTrade + "' data-tradeNo='" + row.tradeNo + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "' data-type=" + row.type + "><i class='icon-zoom-in bigger-130'>查询新浪代收</i></button>"; //详情
		var synHostingCollectTrade = "<button class='btn btn-xs btn-info host-sync permission-" + config.permission.synTrade + "' data-id='" + row.id + "' data-tradeNo ='" + row.tradeNo + "'><i class=' bigger-130'>同步新浪状态</i></button>"; //详情
	
		if(row.tradeStatus=="WAIT_PAY"){
			return queryTrade+" "+synHostingCollectTrade;
		}else{
			return queryTrade;
		}
	}
//创建代收	
$('#create_collect').on('click',function() {
		var tradeNo = $("#trade_id").val();
		$.ajax({
			url:config.urlMap.hostingCollectTrade+"?tradeNo="+tradeNo,
			type:"post", 
			dataType:'json',
			success:function(data) {
				if(data.success){
					bootbox.alert("创建代收成功！");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("创建代收失败！");
					}
				}
			}
		});
});	

$('#hostingCollectTrade-table-2').on('click', '.host-sync', function() {
		var tradeNo = $(this).attr("data-tradeNo");
		$.ajax({
			url:config.urlMap.synHosting+"?tradeNo="+tradeNo,
			type:"post", 
			dataType:'json',
			success:function(data) {
				if(data.success){
					bootbox.alert("同步单笔代收交易状态成功！");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("同步单笔代收交易状态失败！");
					}
				}
			}
		});
});

});

$('#hostingCollectTrade-table-2').on('click', '.host-queryTrade', function() {
	var id = $(this).attr("data-id");
	var tradeNo = $(this).attr("data-tradeNo");
	$("#trade_id").val(tradeNo);
	showTradeDetail(id, $(this).attr("data-type"));
});

//查询代收交易
var tradeDetailTable;
function showTradeDetail(id, type){
    tradeDetailTable = $('#tradeDetail-table').dataTable({
    	"bPaginate": false,
		"bInfo": false,
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : false,
		"bRetrieve": true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'fnInitComplete':function(settings, json){
			ifdata=json.data.length;
			if(ifdata==0 && (type == 1 || type == 2 || type == 4)){
				$("#create_collect").show();
			}else{
				$("#create_collect").hide();
			}
		},
	'sAjaxSource' : config.urlMap.showTradeRecord+id,
	'aoColumns' : [
		{
			'mDataProp': 'tradeNo',
			'bSortable': true
			
		}, {
			'mDataProp': 'remark',
			'bSortable': false
		}, {
			'mDataProp': 'amount',
			'bSortable': false,
			'mRender'   : function(data, type, row) {
				return row.amount.amount;
			}
			
		}, {
			'mDataProp': 'processStatus',
			'bSortable': false,
			'mRender'   : function(data, type, row) {
				return getDictLabel(config.status, row.processStatus);
			}
		},{
			'mDataProp': 'gmtCreate',
			'bSortable': false,
			'mRender': function(data) {
				return formattime(data);
			}
		}, {
			'mDataProp': 'gmtModified',
			'bSortable': false,
			'mRender': function(data) {
				return formattime(data);
			}
			
		}
	]
});
//hostingCollectTrade.fnDraw();
$('#tradeDetail-table').dataTable().fnDestroy();
$('#modal-table').modal('show');
}
//数字转金额
function formatCurrency(num) {
    num = num.toString().replace(/\$|\,/g,'');
    if(isNaN(num))
    num = "0";
    sign = (num == (num = Math.abs(num)));
    num = Math.floor(num*100+0.50000000001);
    cents = num%100;
    num = Math.floor(num/100).toString();
    if(cents<10)
    cents = "0" + cents;
    for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)
    num = num.substring(0,num.length-(4*i+3))+','+
    num.substring(num.length-(4*i+3));
    return (((sign)?'':'-') + num + '.' + cents);
}
function formattime(time){
	if(time==""){
		return "";
	}
	var year=time.substring(0,4);
	var month=time.substring(4,6);
	var day=time.substring(6,8);
	var h=time.substring(8,10);
	var s=time.substring(10,12);
	var m=time.substring(12,14);
	return year+"-"+month+"-"+day+" "+h+":"+s+":"+m;
}

