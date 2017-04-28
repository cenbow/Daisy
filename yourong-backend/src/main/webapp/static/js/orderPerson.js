function showOrderPersonInfo(orderNo) {
	$('#modal-orderPerson').modal('show');
	$.post(
		config.urlMap.transPersonInfo,{id:orderNo},function(data){
			if(data.success){
				if(typeof(data.result.true_name) != "undefined") {
					$('#orderPersonTrueName').html(data.result.true_name);
				} else {
					$('#orderPersonTrueName').html("");
				}
				if(typeof(data.result.username) != "undefined") {
					$('#orderPersonUsername').html(data.result.username);
				} else {
					$('#orderPersonUsername').html("");
				}
				if(typeof(data.result.mobile) != "undefined") {
					$('#orderPersonMobile').html(data.result.mobile);
				} else {
					$('#orderPersonMobile').html("");
				}
				if(typeof(data.result.identity_number) != "undefined") {
					$('#orderPersonIdentityNumber').html(data.result.identity_number);
				} else {
					$('#orderPersonIdentityNumber').html("");
				}
				if(typeof(data.result.member_id) != "undefined") {
					$('#orderPersonSinaAccount').html("YRUC" + data.result.member_id);
				} else {					
					$('#orderPersonSinaAccount').html("");
				}
				if(typeof(data.result.pay_method) != "undefined") {
					if(data.result.pay_method == 1 && data.result.pay_amount > 0) {
						$('#orderPersonPayMethod').html("网银支付");
					} else if(data.result.pay_method == 2 && data.result.pay_amount > 0) {
						$('#orderPersonPayMethod').html("快捷支付");
					} else {
						$('#orderPersonPayMethod').html("");
					}
				}
				if(typeof(data.result.bank_code) != "undefined") {
					$('#orderPersonBankName').html(data.result.bank_code);
				} else {
					$('#orderPersonBankName').html("");
				}
				if(typeof(data.result.card_number) != "undefined") {
					$('#orderPersonCardNumber').html(data.result.card_number);
				} else {
					$('#orderPersonCardNumber').html("");
				}
			}else{
				bootbox.alert("刷新失败！");
			}
		}
	);
}