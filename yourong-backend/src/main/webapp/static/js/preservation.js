jQuery(function($) {

	//表单验证初始化
	var debtForm = $("#preservation_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	//创建保全
	$('#create_preservation').on('click', function() {
		var transactionId = $('#search_transactionId').val();
		if(transactionId == '') {
			bootbox.alert("请输入交易号！");
			$('#create_preservation').focus();
			return;
		}
		$(this).addClass("disabled");
		$.post(
			config.urlMap.create,{id:transactionId},function(data){
				if(data.success) {
					bootbox.alert("交易号：" + transactionId + "，创建保全成功！");
				} else {
					bootbox.alert("交易号：" + transactionId + "，创建保全失败！");
				}
			}
		);
		$(this).removeClass("disabled");
	});

});


