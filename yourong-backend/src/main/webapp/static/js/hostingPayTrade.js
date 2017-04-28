jQuery(function($) {
    // dateTable init
    var hostingPayTradeTable = $('#hostingPayTrade-table-2').dataTable({
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
				'mDataProp': 'payeeId',
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
				'mDataProp': 'summary',
				'bSortable': false
			}, {
				'mDataProp': 'tradeStatus',
				'bSortable': true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.status, row.tradeStatus);
				}
			}, {
				'mDataProp': 'createTime',
				'bSortable': true,
				'mRender'   : function(data, type, row) {
					if(data == "") {
						return "";
					}
					return formatDate(data,'yyyy-mm-dd HH:mm:ss');
				}
			}
		]
    });
    
	$('#query_hostingPayTrade').on('click', function() {
		hostingPayTradeTable.fnDraw();
	});

	//choose
	$('table th input:checkbox').on('click',function() {
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox').each(function() {
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	
});

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

