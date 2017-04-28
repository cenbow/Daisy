//订单
(function() {
    //是否同意转让协议
    $("#j-agree").on("click", function() {
        var isChecked = $(this).is(":checked");
        if (!isChecked) {
            $("input[type='submit']").addClass("z-disabled")
                .attr("disabled", "disabled");
        } else {
            $("input[type='submit']").removeClass("z-disabled")
                .removeAttr("disabled");
        }
    });
    //转让协议查看
    //    $("#j-argreement-link").xArgreement();
    //选中切换
    $(".j-pay-checkbox").on("click", function() {
        if ($(this).hasClass("z-disabled")) {
            return;
        }
        var paytype = $(this).parent().find(".j-paytype");
        $(this).toggleClass("z-checked");
        $(this).next(".j-paytype").click();
    });
    //银行选择
    $("#j-bank-selector").on("click", "li", function() {
        var spanCheckObj = $(".u-bank-checkbox");
        if (spanCheckObj.length > 0) {
            spanCheckObj.remove();
        }
        $("#submit_pay_order").removeAttr("disabled");
        var siblings = $(this).siblings();
        $(this).addClass("z-selected").find(".u-bankicon").removeClass("z-disabled");
        siblings.removeClass("z-selected")
            .find("span").css("border-color", "#dddbd6");
        siblings.find(".u-bankicon").addClass("z-disabled");
        var color = $(this).find(".u-bankicon").css("color");
        $(this).find("span").css("border-color", color);
        $("#j-bankid").val($(this).attr("value"));
        $("#j-bankid-recharge").val($(this).attr("value"));
        $(".j-select-other").removeClass("z-selected").find("button").text("选择其他银行");
        //显示银行额度限制
        var bankCode = $(this).attr("value");
        var bankLimit = showBankLimit(bankCode, 1);
        bankCode ? bankLimit : null;
    });

    $(".j-select-other li").on("click", function() {
        $("#j-bankid").val($(this).attr("value"));
        $("#j-bank-selector li").removeClass("z-selected")
            .find("span").css("border-color", "#dddbd6");
        $("#j-bankid").val($(this).attr("value"));
        $("#j-bankid-recharge").val($(this).attr("value"));
        $("#j-bank-selector .u-bankicon").addClass("z-disabled");
        $(this).parents(".j-select-other").addClass("z-selected");
    });
    //收益计划
    $("#j-btn-plan").on("click", function() {
        var position = $(this).position(),
            left = position.left,
            top = position.top,
            boxWidth = $("#j-plan-box").outerWidth();
        $("#j-plan-box").css({
            "left": left - boxWidth / 2 + 45,
            "top": top + 30
        }).toggleClass("z-open");
    }).on("mouseleave", function() {
        $("#j-plan-box").removeClass("z-open");
    });
    $("#j-plan-box").on("mouseenter", function() {
        $(this).addClass("z-open");
    }).on("mouseleave", function() {
        $(this).removeClass("z-open");
    });

    /**
     * 选择收益券下拉框事件
     */
    $(".j-selector[type='new'] li").on("click", function() {
    	if($(this).hasClass('z-disabled')){
    		return false;
    	}
    	
        var value = $(this).attr("value");
        var investAmount = $("#investAmount").attr("value");
        var projectId = $("#projectId").attr("value");
        var memberId = $("#memberId").attr("value");
        var xToken = $("#xToken").attr("value");
        data = {
            'profitCouponNo': value,
            'investAmount': investAmount,
            'projectId': projectId,
            'memberId': memberId,
            'xToken': xToken
        };
        $.xPost({
            url: environment.globalPath + "/order/newAjax",
            data: data,
            callback: function(data) {
                var result = data.result;
                $("#totalAnnualizedRate").html("年化收益：" + result.totalAnnualizedRate + "%");
                $("#expectAmount").html("￥" + result.expectAmount);
                $("#expectAmount2").html(result.expectAmount);
                //如果不是直投项目，不用初始化收益计划
                if(!result.directProject){
                    for (var index = 1; index <= result.transactionInterestForOrders.length; index++) {
                        $("#payableInterest_" + index).html(result.transactionInterestForOrders[index - 1].payableInterest);
                    }
                }
                $("#j-argreement-link").attr("href", environment.globalPath + "/transaction/contract/preview?projectId=" + projectId + "&annualizedRate=" + result.totalAnnualizedRate + "&investAmount=" + investAmount);
            }
        });

    });


    /**
     * 提交保存订单信息
     */
    $("#submit_save_order").on("click", function() {
        $(this).addClass("z-disabled").attr('disabled', "disabled").val("提交中");
        var profitCouponNo = $("#profitCouponNo-h").attr("value");
        var investAmount = $("#investAmount").attr("value");
        var projectId = $("#projectId").attr("value");
        var memberId = $("#memberId").attr("value");
        var xToken = $("#xToken").attr("value");
        var onceToken = $("#onceToken").attr("value");
        data = {
            'profitCouponNo': profitCouponNo,
            'investAmount': investAmount,
            'projectId': projectId,
            'memberId': memberId,
            'onceToken': onceToken,
            'xToken': xToken
        };
        $.xPost({
            url: environment.globalPath + "/order/save",
            data: data,
            callback: function(data) {
                var result = data.result;
                if (data.success) {
                    var orderId = result.id;
                    window.location.href = environment.globalPath + "/order/to/pay?orderId=" + orderId;
                } else {
                    if (typeof data.resultCodeEum[0].msg !== 'undefined') {
                        $.xDialog({
                            content: data.resultCodeEum[0].msg
                        });
                    } else {
                        $.xDialog({
                            content: data.result
                        });
                    }
                    $("#submit_save_order").removeClass("z-disabled").removeAttr('disabled').val("提交订单");
                }

            }
        });

    });
    //已经完成充值
    $(".j-already-recharge").on("click", function() {
        showAlreadInvert();
    });

    //是否使用优惠劵 
    $("#j-use-coupon").on("click", function() {
        if ($(this).hasClass("z-disabled")) {
            return;
        }
        var isUse = $(this).hasClass("z-checked");
        if (!isUse) {
            $("#j-use-coupon-button").data("value", 0).data("number", "").html("请选择现金券").attr("disabled", true);
            changeCoupon("", 0)
        } else {
            $("#j-use-coupon-button").html("请选择现金券").removeAttr("disabled");
        }
    });

    //是否使用新浪存钱罐
    $(".j-pay-checkbox").on("click", function() {
        if ($(this).hasClass("z-disabled")) {
            return;
        }
        var isUse = $("#j-sinapay-check").hasClass("z-checked");
        investAmount = $("#investAmount").val(),
            couponAmount = $("#usedCouponAmount").val();
        if (!isUse) {
            $("#sinapay").val("").attr("disabled", true).next(".Validform_wrong").remove();
            $("#usedCapital").val(0); //form提交的存钱罐值
            //不适用新浪存钱罐，即新浪存钱罐使用值为0
            userSinaPay(0);
        } else {
            $("#sinapay").removeAttr("disabled");
        }
    });

    // 存钱罐支付金额验证
    $("#sinapay").on("blur", function() {
        var amount = Number($(this).val()),
            amountVal = $(this).val(),
            isError = false,
            errorTipsArea = $(this).next(".Validform_checktip"),
            errorTips = "小数不超过2位",
            point = -1;
        if (amountVal.indexOf(".") !== -1 && amountVal.indexOf(".") !== 0) {
            point = amountVal.length - amountVal.indexOf(".") - 1;
        }
        if (!isNaN(amount)) {
            $(this).val(Number(amount));
            var amountVal2 = $(this).val(),
                valIndex = amountVal2.indexOf(".");
            if (valIndex !== -1 && valIndex !== 0) {
                point = amountVal2.length - amountVal2.indexOf(".") - 1;
            } else {
                point = -1;
            }
            if (Number(amountVal2) === 0) {
                isError = false;
                errorTipsArea.remove();
            } else if (amount < 0.01 || point > 2) {
                isError = true;
            } else {
                errorTipsArea.remove();
            }

        } else {
            isError = true;
            errorTips = "请输入有效金额";
        }
        if (isError) {

            if (errorTipsArea.length !== 1) {
                $(this).after("<span class='Validform_checktip Validform_wrong'>" + errorTips + "</span>");
            } else {
                errorTipsArea.text(errorTips);
            }
        }
    });
    $("#sinapay").on("keydown", function(e) {
        var amount = $(this).val();
        return checkKeyForFloat(amount, e);
    });
    /**
     * 支付页面 选择现金券券下拉框事件
     */
    $(".j-selector[type='pay']").on("click", "li", function() {
        if($(this).hasClass('z-disabled')){
            return false;
        }
        //优惠券
        var couponNumber = $(this).data("number"),
        couponAmount = Number($(this).data("value"));
        if (isNaN(couponAmount)) {
            couponAmount = 0;
        }
        //优惠券值改变时触发的事件
        changeCoupon(couponNumber, couponAmount);

    });

    /**
     * 使用优惠券值改变时
     */
    function changeCoupon(couponNo, couponAmount) {
        //使用新浪存钱罐值
        var sinapay = Number($("#sinapay").val());
        if (isNaN(sinapay)) {
            sinapay = 0;
        }
        //投资本金
        var investAmount = Number($("#investAmount").val());
        if (isNaN(investAmount)) {
            investAmount = 0;
        }
        //还需支付
        var sinapayAmount = _(investAmount - couponAmount - sinapay).toFixed(2);
        $("#cashCouponNo").val(couponNo);
        $("#usedCouponAmount").val(couponAmount);
        $("#usedCapital").val(sinapay);
        $("#payAmount_html").text(sinapayAmount);
        $("#j-qbank-amount").text(sinapayAmount);
        $("#payAmount").val(sinapayAmount);
        if (sinapayAmount > 0) {
            showRechage();
            $("#j-recharge-mount").text(sinapayAmount);
        } else if (sinapayAmount == 0) {
            hiddenRechage();
        } else if (sinapayAmount < 0) {
            userSinaPay(sinapay);
        }
    }



    /**
     * 使用新浪存钱罐
     */
    $("#sinapay").on("keyup", function() {
        if (Number($(this).val()) < 0) {
            var num = -Number($(this).val());
            $(this).val(_(num).toFixed(2));
        }
        var newsinapay = Number($(this).val());
        userSinaPay(newsinapay);
    });


    /**
     * 使用新浪存钱罐值改变时
     */
    function userSinaPay(newsinapay) {
            //1.使用存钱罐的值不能大于存钱罐余额；2.不能大于所需支付的总额
            //1.使用存钱罐的值不能大于存钱罐余额
            //新浪存钱罐可用余额
            var availableBalance = Number($("#j-available-balance").html());
            if (isNaN(availableBalance)) {
                availableBalance = 0;
            }
            if (newsinapay > availableBalance) {
                $("#sinapay").val(availableBalance);
                newsinapay = availableBalance;
            }
            //2.不能大于所需支付的总额
            var investAmount = Number($("#investAmount").val()),
                couponAmount = Number($("#usedCouponAmount").val());
            if (isNaN(couponAmount)) {
                couponAmount = 0;
            }
            if (isNaN(investAmount)) {
                investAmount = 0;
            }
            //剩余所需使用存钱罐值
            var residue = investAmount - couponAmount;
            if (newsinapay > residue) {
                $("#sinapay").val(_(residue).toFixed(2));
                newsinapay = residue;
            }
            var sinapayAmount = _(investAmount - couponAmount - newsinapay - 0).toFixed(2);
            $("#payAmount_html").text(sinapayAmount);
            $("#j-qbank-amount").text(sinapayAmount);
            $("#payAmount").val(sinapayAmount);
            $("#usedCapital").val(newsinapay);
            if (sinapayAmount > 0) {
                $("#j-recharge-mount").text(sinapayAmount);
                showRechage();
            } else {
                hiddenRechage();
            }
        }
        //显示充值
    function showRechage() {
            $("#j-need-pay").show();
            $("#j-blank-select").show();
            $("#j-select-other-blank-code").show();
            $("#j-just-recharge").show();
            $("#j-paybank-list").show();
            $("#submit_pay_balance").hide().addClass("z-disabled").attr("disabled",true);
        }
        //隐藏充值
    function hiddenRechage() {
        $("#j-need-pay").hide();
        $("#j-blank-select").hide();
        $("#j-select-other-blank-code").hide();
        $("#j-just-recharge").hide();
        $("#j-paybank-list").hide();
        $("#submit_pay_balance").show().removeClass("z-disabled").removeAttr("disabled");
    }


    //点击直接充值    
    $("#j-link-to-rechargePage").on("click", function() {
        var couponNumber = $(this).data("number");
        var herf = $(this).data("url");
        var payamount = $("#payAmount").val();
        window.location.href = herf + "?payAmount=" + payamount;
    });


    /**
     * 提交支付订单信息
     *
     */
    $("#submit_pay_order,#submit_pay_balance").on("click", function() {
        var bankId = $("#j-bankid").val(),
            selectObj = $("#j-blank-select"),
            bankSelObj = $(".u-bank-checkbox"),
            sinapayVal = $("#sinapay").next(".Validform_wrong");
        if (sinapayVal.length > 0) {
            return false;
        }
        var data = $("#payOrderForm").serializeArray();
        var href = environment.globalPath + "/transaction/pay/order";
        var sinapayAmount = $("#payAmount").val();
        if (sinapayAmount == null) {
            sinapayAmount = 0;
        }
        $(this).attr('disabled', "true");
        var _this = $(this);
                
        if(_this.data("type")==="bankPay"&&bankId.length===0){
    		$.xDialog({content:"请先选择银行卡"});
    		_this.removeAttr("disabled");
    		return false;
    	}
        if (sinapayAmount > 0) {
            if (bankId === "" && bankSelObj.length === 0) {
                selectObj.append("<span class='u-bank-checkbox'>请选择银行!</span>");
                return false;
            }
            if (!formValid.check() || $(".u-pay-wrap .Validform_wrong").length > 0) {
                return false;
            }
            $(this).addClass("z-disabled").attr("disabled", true).val("支付中");
            href = environment.globalPath + "/transaction/pay/order/thirdPay";
            $("#payOrderForm").attr("action", href);
            $("#payOrderForm").attr("target", "_blank");
            $("#payOrderForm").attr("method", "POST");
            $("#payOrderForm").submit();
            xShade();
            $("#submit_pay_order").removeClass("z-disabled").removeAttr("disabled").val("确认支付");
            $("#pay_order_bank").show();
            //showAlreadInvert();               

        } else {
            $(this).addClass("z-disabled").attr("disabled", true).val("支付中");
            xShade();
            $("#pay_order_recharge_directly_pay").show();
            $.xPost({
                url: href,
                data: data,
                callback: function(data) {
                    //                    $("#submit_pay_order").removeClass("z-disabled").removeAttr("disabled").val("确认支付");
                    //                    if (data.success) {
                    //                        showAlreadInvert();
                    //                    } else {
                    //                        xShade();
                    //                        var message = data.resultCodeEum[0] != null ? data.resultCodeEum[0].msg : "";
                    //                        $("#error-message").html(message);
                    //                        $("#error-pay-investment").show();
                    //                    }
                }
            });
            flushTime();
        }
    });
    //余额支付倒计时
    var wait_time = 5;

    function flushTime() {
            if (wait_time == 0) {
                showAlreadInvert();
            } else {
                wait_time--;
                $("#message_time").html(wait_time);
                setTimeout(function() {
                        flushTime()
                    },
                    1000)
            }
        }
        //短信倒计时
    function smsCountdown(obj) {
        if (typeof obj !== "string") {
            return false;
        }
        var countObj = $(obj);
        if (countObj.length > 0) {var cid = "count_" + (new Date().getTime());
            countObj.find("em").attr("id", cid);
            var countNum = $("#" + cid),
                timer = setInterval(function() {
                var count = Number(countNum.text());
                if (count !== 0) {
                    count -= 1;
                    countNum.text(count);
                } else {
                    clearInterval(timer);
                    countObj.text("重新获取").addClass("u-recode-btn");
                }
            }, 1000);
        }
    }
    $("#j-recode-tips").on("click", function() {
        if ($(this).hasClass("u-recode-btn")) {
            location.reload();
        }
    });
   // 绑卡支付按钮事件
    $("#order_pay_rechargeOnBank_submit").on("click", function() {
    	$("#j-payMethod").val(2);
        var data = $("#payOrderForm").serializeArray();
        var href = environment.globalPath + "/transaction/pay/order/onthirdPayCarID";
        var sinapayAmount = $("#payAmount").val();
        if (sinapayAmount <= 0) {
            return;
        }        
        var _this = $(this);
        $(this).addClass("z-disabled").attr("disabled", true).val("支付中");
        $.xPost({
            url: href,
            data: data,
            callback: function(data) {
                if (data.success) {
                    $("#outAdvanceNo").val(data.result.rechargeNo);
                    $("#ticket").val(data.result.ticket);
                    _this.siblings("label").remove();
                    _this.remove();
                    $("#order_pay_rechargeOnBank_advance").show();
                    $("#j-pay-smscode").show().children().removeAttr("disabled");
                    //禁用支付输入和银行卡选择
                    $("#coupon_ul").remove();
                    $(".j-pay-checkbox").addClass("z-disabled");
                    $(".u-amount-wrap .u-ipt-amount,#j-use-coupon-button").attr("disabled",true);
                    $("#j-qbank-list .u-qlist-wrap").show();
                    smsCountdown("#j-recode-tips");
                    
                } else {
                	showErrorResultMessage(data);
                	setTimeout(function(){
                		_this.removeAttr("disabled").removeClass("z-disabled").val("提交支付");
                	},700);
                }
            }
        });

    });
    //限制只能输入数字
    $("#j-bank-checkcode").on("keyup", function() {
        var val = $(this).val();
        if(val.length>6){
        	val =val.substr(0,6);
        }
        $(this).val(val.replace(/[^\d]/g, ""));
    });
    $("#j-bank-checkcode").on("blur",function(){
    	var val=$(this).val();
    	if(val===""){
    		if($(this).siblings(".Validform_wrong").length===0){
    			$("#j-pay-smscode").append("<span class='Validform_checktip Validform_wrong'>请输入验证码</span>");
    		}
    	}else{
    		$("#j-pay-smscode .Validform_wrong").remove();
    	}
    });
    //绑卡支付推进
    $("#order_pay_rechargeOnBank_advance").on("click", function() {
    	if($("#j-bank-checkcode").val()===""){
    		$("#j-bank-checkcode").blur();
    		return;
        }
        $("#checkCode").val($("#j-bank-checkcode").val());
        var data = $("#payOrderFormAdvace").serializeArray(),_this=$(this);
        //data.checkCode = $("#j-bank-checkcode").val();
        var href = environment.globalPath + "/memberBalance/rechargeOnBankCardCheck";
        $(this).addClass("z-disabled").attr("disabled", true).val("支付中");
        var _this=$(this);       
        $.xPost({
            url: href,
            data: data,
            callback: function(data) {
                if (data.success) {
                	xShade();
                    $("#j-payorder-by-bankcard").text("快捷支付");
                    $("#pay_order_recharge_directly_pay").show();
                    flushTime();
                } else {
                	_this.removeAttr("disabled").removeClass("z-disabled").val("确认支付");
                	var errorMsg="验证码错误",
                        errType="normal";
                	if(data.result != null || data.result == ""){
                		if("短信验证失败"==data.result ||"ticket不存在或已失效"==data.result){
                			errorMsg = "您输入的验证码有误，请重新支付";
                            errType = "smsError";
                            _this.addClass("z-disabled").attr("disabled",true);
                		}else{
                			errorMsg = data.result;
                		}	
                	}
                    $.xDialog({
                        content: errorMsg,
                        callback:function(){
                        	//重新触发获取验证码
                        	if (errType === "smsError") {
                                $("#j-recode-tips").addClass("u-recode-btn").click();
                            }
                        }
                    });
                }
            }
        });        
       
    });





var transactionId;
    //显示充值div
    function showAlreadInvert() {
        $(".j-floatlayer").hide();
        xShade();
        $.xPost({
            url: environment.globalPath + '/transaction/getByOrderId',
            data: {
                'orderId': $("#orderId").val()
            },
            type: 'GET',
            callback: function(data) {
                if (data.success) {
                    if (data.result != null) {
                        transactionId=data.result.id
                        if(data.result.directProject){
                            $.each(data.result, function(n, v) {
                                $("#success-pay-investment-directProject").find("i[name='" + n + "'],td[name='" + n + "'],strong[name='" + n + "']").html(v);
                            });
                            $(".j-contract-auto").attr("href","/transaction/p2pContract/view?orderId="+data.result.orderId)
                            $('#success-pay-investment-directProject').show();
                        }else{
                            $.each(data.result, function(n, v) {
                                $("#success-pay-investment").find("i[name='" + n + "'],td[name='" + n + "'],strong[name='" + n + "']").html(v);
                            });
                            $(".j-contract-auto").attr("href","/transaction/contract/view?orderId="+data.result.orderId);

                            $("#success-pay-investment").show();
                        }
                        $(".j-contract-hand").attr("href","/member/transit?transactionId="+data.result.id)
                        $("#j-again-sign").attr("href","/member/transit?transactionId="+data.result.id)
                    } else {
                        $("#pay_order_bank").hide();
                        $("#process-investment").show();
                    }
                } else {
                    if(data.resultCodeEum != null){
                        xShade();
                        var message = "";
                        message  = data.resultCodeEum[0] != null ? data.resultCodeEum[0].msg : "";
                        $("#error-message").html(message);
                        $("#error-pay-investment").show();
                    }else{
                        $("#pay_order_bank").hide();
                        $("#process-investment").show();
                    }

                }
            }
        });
    }
    $(".j-contract-hand").on("click",function(){
        $("#success-pay-investment-directProject").hide()
        $("#j-settings-auto").show()
    })

    //银行卡 id 和手机号码赋值
    $(".j-pay-qbank").on("click", function() {
        var cardId = $(this).data("cardid"),
            $smscode=$("#j-pay-smscode");
        $(this).addClass("z-selected").siblings().removeClass("z-selected");
        $("#j-cardId").val(cardId);
        $smscode.find('strong').text($(this).data("bankmobile"));
        $("#j-bankid").val($(this).data("code"));
        showBankLimit($(this).data("code"), 2, 1);
    });
    //快捷支付协议
    $("#j-qbank-agree").on("click", function() {
        if (!$(this).is(":checked")) {
            $("#order_pay_rechargeOnBank_submit").addClass("z-disabled").attr("disabled", true);
        } else {
            $("#order_pay_rechargeOnBank_submit").removeClass("z-disabled").removeAttr("disabled");
        }
    });
    //快捷支付手机号码初次赋值
    $("#j-pay-smscode").find('strong').text($(".j-pay-qbank:eq(0)").data("bankmobile"));
    
    $("#j-qbank-argreement-link").xArgreement();
    //firefox bug 修复
    $("#order_pay_rechargeOnBank_submit").removeAttr("disabled");
    //阻止协议事件冒泡
    $("#j-argreement-label a").on("click",function(event){
    	event.stopPropagation();
    });

    var $disabledCoupon = $('#j-use-copon-div').find('.z-disabled');
    $disabledCoupon.eq(0).addClass('f-bd-t');
    $disabledCoupon.each(function(){
        var conditions=$(this).data('conditions');

    });
    
    $("#j-isSafecard").each(function() {
		showBankLimit($(this).attr("data-code"), 2, 1);
	});
    
  
})();


//显示银行限额
function showBankLimit(bankCode, typeLimit) {
    $.xPost({
        url: environment.globalPath + "/payment/platformLimit",
        data: {
            'code': bankCode,
            'typeLimit': typeLimit,
            'platformType': 1
        },
        callback: function (data) {
            var quickRemark=$("#td-quick-remark");
            if (data.success) {
                //查询出所有的限额以及对应的bankId
                if (typeLimit == 2) {
                	$("#t-quick").show();
                    $("#h6-bankName-quick").text(data.result.simpleName + "快捷支付限额如下");
                    $("#td-quick-singleLimit").text(showLimitStr(data.result.singleLimit));
                    $("#td-quick-dailyLimit").text(showLimitStr(data.result.dailyLimit));
                    $("#td-quick-minLimit").text(data.result.minLimit);
                   /* $("quickRemark").text(data.result.remark);*/
                    data.result.remark===null?quickRemark.text(""):quickRemark.text(data.result.remark);
                }
                if (data.result.maintenanceContent != null && data.result.maintenanceContent.length > 0) {
                    if (typeLimit == 2) {
                        $(".u-recharge-amount2").addClass("f-dn");
                        $(".d-quick").addClass("f-dn");
                        $(".d-quick-maint").removeClass("f-dn");
                        $(".d-quick-maint").html(data.result.maintenanceContent);
                    }
                    if (typeLimit == 1) {
                        $(".d-eBank").addClass("f-dn");
                        $(".d-ebank-maint").removeClass("f-dn");
                        $(".d-ebank-maint").html(data.result.maintenanceContent);
                    }
                } else {
                    if (typeLimit == 2) {
                        $(".u-recharge-amount2").removeClass("f-dn");
                        $(".d-quick").removeClass("f-dn");
                        $(".d-quick-maint").addClass("f-dn");
                    }
                    if (typeLimit == 1) {
                        $(".d-eBank").removeClass("f-dn");
                        $(".d-ebank-maint").addClass("f-dn");
                        $(".d-ebank-maint").html(data.result.maintenanceContent);
                    }
                }
            } else {
            	$("#t-quick").hide();
                $("#h6-bankName-quick").text("");
                $("#td-quick-singleLimit").text('');
                $("#td-quick-dailyLimit").text('');
                $("#td-quick-minLimit").text('');
                quickRemark.text('');
                if (typeLimit == 2) {
                    $(".u-recharge-amount2").removeClass("f-dn");
                    $(".d-quick").removeClass("f-dn");
                    $(".d-quick-maint").addClass("f-dn");
                }
                if (typeLimit == 1) {
                    $(".d-eBank").removeClass("f-dn");
                    $(".d-ebank-maint").addClass("f-dn").html(data.result.maintenanceContent);
                }
            }
        }
    });
}

function showLimitStr(num) {
    var intNum = parseInt(num);
    if (intNum > 10000) {
        intNum = intNum / 10000;
        return intNum + "万";
    }
    return intNum;
}

    //阳光保险+新浪支付的小banner
    var sinapaybanner=$('.j-sinapay-banner');
    setTimeout("sinapaybanner.slideUp();",5000);

