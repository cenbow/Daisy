/**
 *  平台资金同步
 */
jQuery(function($) {
	
	$("#platform_balance_sysnc").on('click', function() {
		bootbox.confirm("你确定要同步吗?", function(result) {
			if (result) {
				$.post(config.urlMap.sys, {	}, function(data) {
					if(data != null ){
						bootbox.alert("余额:"+data.balance+"元,可用余额:"+data.availableBalance+"元");
					}					
				});
			}
		});
	});
	
	
});