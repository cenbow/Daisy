jQuery(function($) {
	/**
	 * 优惠券解锁脚本
	 */
	var couponUnlockedForm = $("#coupon_unlocked_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	$("#coupon_unlocked").on('click',function(){
		if (couponUnlockedForm.check(false)) {
			var couponNo = $("#couponId").val();
			$.ajax({
				url:config.urlMap.unlockedCoupon+"?couponNo="+couponNo,
				type:"post", 
				dataType:'json',
				success:function(data) {
					if(data.success) {
						bootbox.alert("优惠券解锁成功！");
					} else {
						var status = data.result;
						switch(status) {
							case 0:
								bootbox.alert("优惠券解锁失败！当前优惠券状态为：未领取");
								break;
							case 1:
								bootbox.alert("优惠券解锁失败！当前优惠券状态为：已领取，未使用");
								break;
							case 2:
								bootbox.alert("优惠券解锁失败！当前优惠券状态为：已使用");
								break;
							case 3:
								bootbox.alert("优惠券解锁失败！当前优惠券状态为：未领取，已过期");
								break;
							case 4:
								bootbox.alert("优惠券解锁失败！当前优惠券状态为：已领取，已过期");
								break;
							case 5:
								bootbox.alert("优惠券解锁失败！当前优惠券状态为：使用中");
								break;
						}
					}
				}
			});
		}
	});
	
	
	
	/**
	 * 清除项目缓存
	 */
	var delProjectForm = $("#delete_project_redis_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	$("#delete_project_id").on("click",function(){
		if (delProjectForm.check(false)) {
			var projectId = $("#project_id").val();
			$.ajax({
				url:config.urlMap.delProjectRedis+"?projectId="+projectId,
				type:"post", 
				dataType:'json',
				success:function(data) {
					if(data.success){
						bootbox.alert("清除项目缓存成功！");
					}else{
						bootbox.alert("清除项目缓存失败！");
					}
				}
			});
		}
	});
	
	/*
	 *删除用户绑定银行卡 
	 */
	var delBankCardForm = $("#delete_bankcard_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	$("#delete_bankcard_id").on("click",function(){
		if (delBankCardForm.check(false)) {
			var mobile = $("#bankcard_mobile").val();
			$.ajax({
				url:config.urlMap.delBankCard+"?mobile="+mobile,
				type:"post", 
				dataType:'json',
				success:function(data) {
					if(data.success){
						bootbox.alert("删除银行卡成功！");
					}else{
						if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
							bootbox.alert(data.resultCodeEum[0].msg);
						}else{
							bootbox.alert("删除银行卡失败！");
						}
					}
				}
			});
		}
	});
/*
 *根据代收交易号处理还本付息代付
 */
var processPayTradeNoForm = $("#pay_tradeno_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#pay_tradeno_id").on("click",function(){
	if (processPayTradeNoForm.check(false)) {
		var tradeNo = $("#pay_tradeNo").val();
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
			}
		});
	}
});
/*
 *同步单笔代收交易状态
 */
var synHostingForm = $("#syn_hosting_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#syn_hosting_id").on("click",function(){
	if (synHostingForm.check(false)) {
		var tradeNo = $("#syn_tradeNo").val();
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
	}
});

var createPrevervation = $("#create_prevervation").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#create_prevervation_id").on("click",function(){
	if (createPrevervation.check(false)) {
		var transactionId = $('#preservation_tradeNo').val();
		$(this).addClass("disabled");
		$.post(
			config.urlMap.createPreservation,{id:transactionId},function(data){
				if(data.success) {
					bootbox.alert("交易号：" + transactionId + "，创建保全成功！");
				} else {
					bootbox.alert("交易号：" + transactionId + "，创建保全失败！");
				}
			}
		);
		$(this).removeClass("disabled");
	}
});

var cancleMobile = $("#cancle_mobile").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#cancle_mobile_id").on("click",function(){
	if (cancleMobile.check(false)) {
		var uid = $('#cancle_mobile_member_id').val();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.cansoleMobile,
			data: {uid:uid},
			dataType: "json",
			success: function(data){
				bootbox.alert("清除认证");
			}
		});
		$(this).removeClass("disabled");
	}
});


//绑定新浪认证信息
var cancellationMemberVerify = $("#cancellation_member").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

//冻结用户不使用的账户
$("#cancellation_member_btn").on("click",function(){
	if (cancellationMemberVerify.check(false)) {
		var data =  $("#cancellation_member").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.cancellationMember,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert(data.errorCode);
				} else {
					bootbox.alert("操作成功");
				}
				$("#cancellation_member_btn").removeClass("disabled");
			}
		});
	}
});

//直接代付
var directHostPay = $("#direct_host_pay").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#direct_host_pay_btn").on("click",function(){
	if (directHostPay.check(false)) {
		var amount = $("#direct_host_pay").find("input[name='amount']").val();
		if(amount>=10000){
			bootbox.confirm("你确定直接代付，金额"+amount,function(result) {
				if(result){
					directHostPayFun();
				}
			});
		}else{
			directHostPayFun();
		}
	}
});

function directHostPayFun(buttonObj){
	var data =  $("#direct_host_pay").serialize();
	$("#direct_host_pay_btn").addClass("disabled");
	$.ajax({
		type: "GET",
		url:  config.urlMap.directHostPay,
		data: data,
		dataType: "json",
		success: function(data){
			if(data.success){
				bootbox.alert("直接代付执行成功！");
			}else{
				if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
					bootbox.alert(data.resultCodeEum[0].msg);
				}else{
					bootbox.alert("直接代付执行失败！");
				}
			}
			$("#direct_host_pay_btn").removeClass("disabled");
		}
	});
}

//直接代收
var directHostCollect = $("#direct_host_collect").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#direct_host_collect_btn").on("click",function(){
	if (directHostCollect.check(false)) {
		var data =  $("#direct_host_collect").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.directHostCollect,
			data: data,
			dataType: "json",
			success: function(data){
				bootbox.alert("直接代收执行成功！");
				$("#direct_host_collect_btn").removeClass("disabled");
			}
		});
	}
});

//保存代付到新浪
var addSinaHostPay = $("#add_sina_host_pay").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#add_sina_host_pay_btn").on("click",function(){
	if (addSinaHostPay.check(false)) {
		var data =  $("#add_sina_host_pay").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.addSinaHostPay,
			data: data,
			dataType: "json",
			success: function(data){
				bootbox.alert("保存代付到新浪成功！");
				$("#add_sina_host_pay_btn").removeClass("disabled");
			}
		});
	}
});


//同步项目代收交易状态
var synProjectHostCollect = $("#syn_project_host_collect").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#syn_project_host_collect_btn").on("click",function(){
	if (synProjectHostCollect.check(false)) {
		var data =  $("#syn_project_host_collect").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.synProjectHostCollect,
			data: data,
			dataType: "json",
			success: function(data){
				bootbox.alert("同步项目代收交易状态执行成功！");
				$("#syn_project_host_collect_btn").removeClass("disabled");
			}
		});
	}
});
//同步存钱罐
var synBalance = $("#syn_balance").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#syn_balance_btn").on("click",function(){
	if (synBalance.check(false)) {
		var data =  $("#syn_balance").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.synBalance,
			data: data,
			dataType: "json",
			success: function(data){
			
			}
		});
		bootbox.alert("同步存钱罐余额执行成功！");
		$("#syn_balance_btn").removeClass("disabled");
	}
});
//绑定新浪认证信息
var bindingSinaVerify = $("#binding_sina_verify").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#binding_sina_verify_btn").on("click",function(){
	if (bindingSinaVerify.check(false)) {
		var data =  $("#binding_sina_verify").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.bindingVerify,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert(data.errorMsg);
				} else {
					bootbox.alert("绑定新浪认证信息成功！");
				}
				$("#binding_sina_verify_btn").removeClass("disabled");
			}
		});
	}
});

//人工生成红包
var createRedPackageVerify = $("#create_redPackage_verify").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#create_redPackage_btn").on("click",function(){
	if (createRedPackageVerify.check(false)) {
		var data =  $("#create_redPackage_verify").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.createRedPackage,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert(data.errorMsg);
				} else {
					bootbox.alert("生成红包提交成功，正在异步处理！");
				}
				$("#create_redPackage_btn").removeClass("disabled");
			}
		});
	}
});


//根据批付号同步代付
var synBatchHostPay = $("#syn_batch_host_pay").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#syn_batch_host_pay_btn").on("click",function(){
	if (synBatchHostPay.check(false)) {
		var data =  $("#syn_batch_host_pay").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.synBatchHostPay,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert("同步失败");
				} else {
					bootbox.alert("同步成功！");
				}
				$("#syn_batch_host_pay_btn").removeClass("disabled");
			}
		});
	}
});

//直投项目发起代收完成/撤销
var authDirectPorjectCollectTrade = $("#auth_directPorject_collectTrade").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#auth_directPorject_collectTrade_btn").on("click",function(){
	if (authDirectPorjectCollectTrade.check(false)) {
		var data =  $("#auth_directPorject_collectTrade").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.handleDirectPorjectCollect,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert("直投项目发起代收完成/撤销失败：" + data.resultCodeEum[0].msg);
				} else {
					bootbox.alert("直投项目发起代收完成/撤销成功！");
				}
				$("#auth_directPorject_collectTrade_btn").removeClass("disabled");
			}
		});
	}
});

//转让项目发起代收完成/撤销
var authTransferPorjectCollectTrade = $("#auth_transferPorject_collectTrade").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#auth_transferPorject_collectTrade_btn").on("click",function(){
	if (authTransferPorjectCollectTrade.check(false)) {
		var data =  $("#auth_transferPorject_collectTrade").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.handleTransferPorjectCollect,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert("转让交易创建批付失败：" + data.resultCodeEum[0].msg);
				} else {
					bootbox.alert("转让交易创建批付成功！");
				}
				$("#auth_transferPorject_collectTrade_btn").removeClass("disabled");
			}
		});
	}
});

//项目保证金归还异常处理
var createCollectTradeGuaranteeFee = $("#create_collectTrade_guaranteeFee_btn").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#create_collectTrade_guaranteeFee_btn").on("click",function(){
	if (createCollectTradeGuaranteeFee.check(false)) {
		var data =  $("#create_collectTrade_guaranteeFee").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.collectTradeGuaranteeFee,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.error) {
					bootbox.alert("保证金归还执行失败！");
				} else {
					bootbox.alert("保证金归还执行成功！");
				}
				$("#create_collectTrade_guaranteeFee_btn").removeClass("disabled");
			}
		});
	}
});
/*
 *根据红包加密串查询交易号
 */
var redBagCodeForm = $("#redBagCode_transactionId").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
$("#redBagCode_transactionId_btn").on("click",function(){
	if (redBagCodeForm.check(false)) {
		var redBagCode = $("#redBagCode").val();
		$.ajax({
			url:config.urlMap.findTransactionId+"?redBagCode="+redBagCode,
			type:"post", 
			dataType:'json',
			success:function(data) {
				if(data.success){
					bootbox.alert("交易号为:"+data.result);
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("查询交易号失败！");
					}
				}
			}
		});
	}
});


//根据交易ID重新初始化合同数据
var contractReInit = $("#contract_re_init").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false

});

$("#contract_re_init_btn").on("click",function(){
	if (contractReInit.check(false)) {
		var data =  $("#contract_re_init").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.contractReInit,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					bootbox.alert("合同重新初始化执行成功！");
				} else {
					bootbox.alert("合同重新初始化执行失败！");
				}
				$("#contract_re_init_btn").removeClass("disabled");
			}
		});
	}
});




$("#contract_history_init_btn").on("click",function(){
		var data =  $("#contract_history_init").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.contractHistoryInit,
			data: data,
			dataType: "json",
			success: function(data){
				bootbox.alert(data.result);
				$("#contract_history_init_btn").removeClass("disabled");
			}
		});
});


//根据交易ID同步合同签署信息
var contractSynchro= $("#contract_synchro").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});


$("#contract_synchro_btn").on("click",function(){
	if (contractSynchro.check(false)) {
		var data =  $("#contract_synchro").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.queryContractInfo,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					bootbox.alert("同步合同签署信息执行成功！");
				} else {
					bootbox.alert("同步合同签署信息执行失败！");
				}
				$("#contract_synchro_btn").removeClass("disabled");
			}
		});
	}
});



//根据交易ID同步合同签署信息
var createRemoteBatchPay= $("#create_remote_batch_pay").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});


$("#create_remote_batch_pay_btn").on("click",function(){
	if (createRemoteBatchPay.check(false)) {
		var data =  $("#create_remote_batch_pay").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.createRemoteBatchPay,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					bootbox.alert("根据批付号创建远程批付执行成功！");
				} else {
					bootbox.alert("根据批付号创建远程批付执行失败！");
				}
				$("#create_remote_batch_pay_btn").removeClass("disabled");
			}
		});
	}
});

//根据交易ID同步合同签署信息
var createHostBatchPay= $("#create_host_batch_pay").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});


$("#create_host_batch_pay_btn").on("click",function(){
	if (createHostBatchPay.check(false)) {
		var data =  $("#create_host_batch_pay").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.createHostBatchPay,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					bootbox.alert("根据批次号创建本地批付执行成功！");
				} else {
					bootbox.alert("根据批次号创建本地批付执行失败！");
				}
				$("#create_remote_batch_pay_btn").removeClass("disabled");
			}
		});
	}
});


//根据交易ID自动签署
var contractAutoSignSecond= $("#contract_autoSignSecond").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

    
$("#contract_autoSignSecond_btn").on("click",function(){
	if (contractAutoSignSecond.check(false)) {
		var data =  $("#contract_autoSignSecond").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.autoSignSecond,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					bootbox.alert("根据交易号自动签署乙方成功！");
				} else {
					bootbox.alert("根据交易号自动签署乙方失败！");
				}
				$("#contract_autoSignSecond_btn").removeClass("disabled");
			}
		});
	}
});


 
// 根据代收交易单号退款/解冻
var contractSynchro= $("#collectTrade_refund_unfrozen").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#collectTrade_refund_unfrozen_btn").on("click",function(){
	if (contractSynchro.check(false)) {
		var data =  $("#collectTrade_refund_unfrozen").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.hostingRefundUnfrozen,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					bootbox.alert("代收交易单号退款/解冻成功！");
				} else {
					bootbox.alert("代收交易单号退款/解冻失败！");
				}
				$("#collectTrade_refund_unfrozen_btn").removeClass("disabled");
			}
		});
	}
});

//根据代收交易号发起退款
var refundTradeCollect = $("#refund_trade_collect").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#refund_trade_collect_btn").on("click",function(){
	if (refundTradeCollect.check(false)) {
		var data =  $("#refund_trade_collect").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.refundTradeCollection,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success){
					bootbox.alert("根据代收交易号发起退款成功！");
					$("#refund_trade_collect_btn").removeClass("disabled");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("根据代收交易号发起退款失败！");
					}
				}
			
			}
		});
	}
});
//根据项目id补发五重礼人气值
var sendFiveRites = $("#send_fiveRites_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#send_fiveRites_btn").on("click",function(){
	if (sendFiveRites.check(false)) {
		var data =  $("#send_fiveRites_form").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.sendFiveRites,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success){
					bootbox.alert("根据项目id补发五重礼人气值成功！");
					$("#send_fiveRites_btn").removeClass("disabled");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("根据项目id补发五重礼人气值失败！");
					}
				}
			
			}
		});
	}
});

//同步选择状态的充值记录
var sysRechargeLog = $("#sys_recharge_log").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#sys_recharge_log_btn").on("click",function(){
	if (sysRechargeLog.check(false)) {
		var data =  $("#sys_recharge_log").serialize();
		
		var dataJson = {
				formToJson: function (data) {
					data=data.replace(/&/g,"\",\"");
					data=data.replace(/=/g,"\":\"");
					data="{\""+data+"\"}";
					return data;
				},
		};
		data = decodeURIComponent(data,true);
		var dataObj=dataJson.formToJson(data);
		if (dataObj != null) {
			dataObj = JSON.parse(dataObj);
			if((dataObj.rechargeNo==null || dataObj.rechargeNo=='')
					&& (dataObj.startTime==null || dataObj.startTime=='')
					&& (dataObj.endTime==null || dataObj.endTime=='')) {
				bootbox.alert("交易号或交易时间至少填入一项！");
				return;
			}
			
			if(dataObj.startTime!=null && dataObj.startTime!='') {
				if (dataObj.endTime==null || dataObj.endTime=='') {
					bootbox.alert("请输入交易结束时间！");
					return;
				}
			}
			if(dataObj.endTime!=null && dataObj.endTime!='') {
				if (dataObj.startTime==null || dataObj.startTime=='') {
					bootbox.alert("请输入交易开始时间！");
					return;
				}
			}
		}
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.sysRechargeLog,
			data: data,
			dataType: "json",
			success: function(data){
				if(data){
					bootbox.alert("同步成功！");
					$("#sys_recharge_log_btn").removeClass("disabled");
				}else{
					bootbox.alert("同步失败！");
				}
			
			}
		});
	}
});


//根据交易ID下载合同数据
var contractDown = $("#contract_down").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false

});

$("#contract_down_btn").on("click",function(){
	if (contractDown.check(false)) {
		var data =  $("#contract_down").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.getContractDownUrl,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success) {
					window.location.href=data.result
				} else {
					bootbox.alert("合同下载失败");
				}
				$("#contract_down_btn").removeClass("disabled");
			}
		});
	}
});


$("#member_level_up_btn").on("click",function(){
		var data =  $("#member_level_up_init").serialize();
		//$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.memberLevelUpHandle,
			data: data,
			dataType: "json",
			success: function(data){
			}
		});
});

//根据项目id补发五重礼人气值
var sendQuickLottery = $("#quick_lottery_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});

$("#quick_lottery_btn").on("click",function(){
	if (sendQuickLottery.check(false)) {
		var data =  $("#quick_lottery_form").serialize();
		$(this).addClass("disabled");
		$.ajax({
			type: "GET",
			url:  config.urlMap.sendQuickDirectLottery,
			data: data,
			dataType: "json",
			success: function(data){
				if(data.success){
					bootbox.alert("根据项目id补发快投有奖成功！");
					$("#quick_lottery_btn").removeClass("disabled");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("根据项目id补发快投有奖失败！");
					}
				}
			
			}
		});
	}
});
});

