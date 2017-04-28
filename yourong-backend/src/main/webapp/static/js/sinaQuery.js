jQuery(function($) {
	
	var pageSize = 30;
	//查询
	$('#query_accountDetails').on('click', function() {
		if(!validCheck()) {
			return;
		}
		var memberId = $('#memberId').val();
		var svpTradeType = $('#svpTradeType').val();
		$('#pageNo').html("1");
		var startDateStr = $('#startDateStr').val();
		var endDateStr = $('#endDateStr').val();
		var mobile = $('#mobile').val();
		$.ajax({
			url:config.urlMap.queryAccountDetailsAjax,
			data:{
				'memberId':memberId,
				'mobile':mobile,
				'svpTradeType':svpTradeType,
				'pageNo':1,
				'pageSize':pageSize,
				'startDateStr':startDateStr,
				'endDateStr':endDateStr
			},
			type:'post',
			dataType:'json',
			success:function(data){
				if(data.success) {
					$('#totalCount').html(data.totalCount);
					if(memberId != "") {
						$('#memberIdResult').html(memberId);
					} else {
						$('#memberIdResult').html(mobile);
					}
					$('#totalIncome').html(data.module.totalIncome);
					$('#totalPayout').html(data.module.totalPayout);
					$('#memberInfo').show();
					$("#queryAccountDetails-table-2 tr:not(:first)").remove();
					$.each(data.module.detailList, function (i, value) { 
						$("<tr><td>" + (parseInt(i)+1) + 
								"</td><td>" + value.summary + 
								"</td><td>" + value.recordedTime + 
								"</td><td>" + value.direction + value.amount.amount + 
								"</td><td>" + value.balance.amount +
								"</td><td>" + value.transactionType +
								"</td></tr>").insertAfter($("#queryAccountDetails-table-2 tr:eq("+i+")"));
						i++;
					});
				} else {
					bootbox.alert(data.errorMsg);
					$("#queryAccountDetails-table-2 tr:not(:first)").remove();
					$('#pageNo').html("1");
					$('#totalCount').html("0");
					$('#memberInfo').hide();
				}
			}
		});
	});
	
	//上一页
	$('#previousA').on('click', function() {
		if(!validCheck()) {
			return;
		}
		var memberId = $('#memberId').val();
		var svpTradeType = $('#svpTradeType').val();
		var pageNo = $('#pageNo').html();
		if(parseInt(pageNo) > 1) {
			pageNo = parseInt(pageNo) - 1;
		} else {
			return;
		}
		var startDateStr = $('#startDateStr').val();
		var endDateStr = $('#endDateStr').val();
		var mobile = $('#mobile').val();
		$.ajax({
			url:config.urlMap.queryAccountDetailsAjax,
			data:{
				'memberId':memberId,
				'mobile':mobile,
				'svpTradeType':svpTradeType,
				'pageNo':pageNo,
				'pageSize':pageSize,
				'startDateStr':startDateStr,
				'endDateStr':endDateStr
			},
			type:'post',
			dataType:'json',
			success:function(data){
				if(data.success) {
					$('#pageNo').html(pageNo);
					$('#totalCount').html(data.totalCount);
					if(memberId != "") {
						$('#memberIdResult').html(memberId);
					} else {
						$('#memberIdResult').html(mobile);
					}
					$('#totalIncome').html(data.module.totalIncome);
					$('#totalPayout').html(data.module.totalPayout);
					$('#memberInfo').show();
					$("#queryAccountDetails-table-2 tr:not(:first)").remove();
					var index = 0;
					if(pageNo > 1) {
						index = pageSize*(pageNo-1);
					}
					$.each(data.module.detailList, function (i, value) { 
						$("<tr><td>" + (parseInt(i)+1 + index) + 
								"</td><td>" + value.summary + 
								"</td><td>" + value.recordedTime + 
								"</td><td>" + value.direction + value.amount.amount + 
								"</td><td>" + value.balance.amount +
								"</td><td>" + value.transactionType +
								"</td></tr>").insertAfter($("#queryAccountDetails-table-2 tr:eq("+i+")"));
						i++;
					});
				} else {
					bootbox.alert(data.errorMsg);
					$("#queryAccountDetails-table-2 tr:not(:first)").remove();
					$('#pageNo').html("1");
					$('#totalCount').html("0");
					$('#memberInfo').hide();
				}
			}
		});
	});
	
	//下一页
	$('#nextA').on('click', function() {
		if(!validCheck()) {
			return;
		}
		var memberId = $('#memberId').val();
		var svpTradeType = $('#svpTradeType').val();
		var pageNo = $('#pageNo').html();
		var totalCount = $('#totalCount').html();
		var totalPage = Math.ceil(parseInt(totalCount) / pageSize);
		if(parseInt(pageNo) < totalPage) {
			pageNo = parseInt(pageNo) + 1;
		} else {
			return;
		}
		var startDateStr = $('#startDateStr').val();
		var endDateStr = $('#endDateStr').val();
		var mobile = $('#mobile').val();
		$.ajax({
			url:config.urlMap.queryAccountDetailsAjax,
			data:{
				'memberId':memberId,
				'mobile':mobile,
				'svpTradeType':svpTradeType,
				'pageNo':pageNo,
				'pageSize':pageSize,
				'startDateStr':startDateStr,
				'endDateStr':endDateStr
			},
			type:'post',
			dataType:'json',
			success:function(data){
				if(data.success) {
					$('#pageNo').html(pageNo);
					$('#totalCount').html(data.totalCount);
					if(memberId != "") {
						$('#memberIdResult').html(memberId);
					} else {
						$('#memberIdResult').html(mobile);
					}
					$('#totalIncome').html(data.module.totalIncome);
					$('#totalPayout').html(data.module.totalPayout);
					$('#memberInfo').show();
					$("#queryAccountDetails-table-2 tr:not(:first)").remove();
					var index = 0;
					if(pageNo > 1) {
						index = pageSize*(pageNo-1);
					}
					$.each(data.module.detailList, function (i, value) { 
						$("<tr><td>" + (parseInt(i)+1+index) + 
								"</td><td>" + value.summary + 
								"</td><td>" + value.recordedTime + 
								"</td><td>" + value.direction + value.amount.amount + 
								"</td><td>" + value.balance.amount +
								"</td><td>" + value.transactionType +
								"</td></tr>").insertAfter($("#queryAccountDetails-table-2 tr:eq("+i+")"));
						i++;
					});
				} else {
					bootbox.alert(data.errorMsg);
					$("#queryAccountDetails-table-2 tr:not(:first)").remove();
					$('#pageNo').html("1");
					$('#totalCount').html("0");
					$('#memberInfo').hide();
				}
			}
		});
	});
});

function validCheck() {
	var memberId = $('#memberId').val();
	var mobile = $('#mobile').val();
	if(memberId == "" && mobile == "") {
		bootbox.alert("请出入会员ID或者手机号！");
		return false;
	}
	return true;
}
