/*global define,require,$,environment,alert,confirm,console,env*/
define(['zepto', 'base', 'template'], function (require, exports, module) {
    'use strict';
    var base = require('base');
    //服务协议阻止提交
    $("#j-agree").change(function () {
        var checked = $(this).is(":checked"),
            btn = $("button[type=submit]");
        if (!checked) {
            btn.addClass("z-disabled").attr("disabled", "disabled");
        } else {
            btn.removeClass("z-disabled").removeAttr("disabled");
        }
    });

//一些全局变量
    /**
     * selectedBankIndex(选中的快捷卡的index)
     * balanceAmount(投资金额与余额支付的现金差)
     * cashCouponIndex(现金券的index)
     * getOrderId(订单的详细信息数据中的订单id)
     * payAmount(用余额支付的金额数)
     * getPayReqData(发送验证码接口请求时上传的数据)
     * @type {number}
     */
    var selectedBankIndex = -1,
        balanceAmount,
        cashCouponIndex = -1,
        getPayReqData = {},
        couponAmount = 0,
        getOrderId,
        bankReqData,
        payAmount;
    //订单支付页面加载
    var $orderId = $('#j-orderId');
    if ($orderId.length) {

        var orderId = $orderId.attr('orderId'),
            template = require('template'),
            initSuccess = false;
        if (orderId) {
            base.getAPI({
                url: environment.globalPath + '/security/order/queryPayOrderInfo',
                data: {orderId: orderId},
                callback: function (data) {

                    if (data.success) {
                        //max coupon amount
                        var coupons = data.result.coupons,
                            maxCoupon = 0;
                        if (coupons.length) {
                            var couponsNumberList = [];
                            coupons.forEach(function (item) {
                                if (item.isAvailable) {
                                    couponsNumberList.push(item.amount - 0);
                                }
                            });
                            couponsNumberList = couponsNumberList.sort(function (a, b) {
                                return a - b;
                            });

                            maxCoupon = couponsNumberList[couponsNumberList.length - 1] || 0;

                        }
                        //判断是否有银行卡信息
                        var result=data.result;
                        if (result.investAmount>result.savingPotBalance-0+maxCoupon&&!result.bankCardList.length) {
                            $('.j-balancePopup').show();
                            $('.j-cover').show();
                            return false;
                        }
                        //判断是否是第二次支付
                        if(data.result.orderPayAmount){
                            $('.j-balanceAmount').text(data.result.orderPayAmount.toFixed(2))
                        }
                        getOrderId = data.result.orderId;
                        //渲染订单信息模板
                        var html = template('j-orderPay-tpl', {order: data.result});
                        $('#j-orderPay-info').html(html);
                        ////快捷支付
                        var quickPayHtml = template('j-quickPay-tpl', {bankCardMsg: data.result.bankCardList,order:data.result});
                        $('#j-quickPay-info').html(quickPayHtml);

                        var investAmount = data.result.investAmount - 0,
                            $payAmount=$('.j-pay-amount');
                        payAmount =$payAmount.val();
                        balanceAmount = (investAmount - payAmount).toFixed(2);
                        if (balanceAmount > 0) {
                            bankCardShow(data)
                        }
                        //实时监控了input框内的数值变化
                        $payAmount.on('input', function () {
                            var val = $(this).val();
                            var reg=/^[1-9]*[1-9][0-9]*(.[0-9]{1,2})?$|^(0.[0-9]{1,2})$/;
                            if(val-0<0){
                                val = 0;
                                $(this).val(0);
                            }
                            if((val-0===0 || reg.test(val))){
                            if (val > data.result.savingPotBalance) {
                                base.xTips({content:'余额不足'});
                                //alert('余额不足');
                                $(this).val(data.result.savingPotBalance)
                            }
                            if (cashCouponIndex === -1) {
                                if (val < investAmount) {
                                    if(!result.bankCardList.length){
                                        $('.j-balancePopup').show();
                                        $('.j-cover').show();
                                        return false;
                                    }
                                    bankCardShow(data);
                                    $('.j-balanceAmount').text(commafy((investAmount - $(this).val()).toFixed(2)));
                                    payAmount = $(this).val();
                                    balanceAmount = (investAmount - payAmount).toFixed(2);
                                }
                                else if (val == investAmount) {
                                    $('.z-quickPay-show').hide();
                                    selectedBankIndex = -1;
                                    balanceAmount = 0;
                                }
                                else if (val > investAmount) {
                                    base.xTips({content:'请输入正确金额'});
                                    //alert('请输入正确金额');
                                    $(this).val(payAmount);
                                    selectedBankIndex = -1;
                                    balanceAmount = 0;
                                }
                            }
                            else {

                                var inputVal = ($(this).val() - 0) + (base.cache.couponAmount - 0);
                                if (inputVal < investAmount) {
                                    if(!result.bankCardList.length){
                                        $('.j-balancePopup').show();
                                        $('.j-cover').show();
                                        return false;
                                    }
                                    bankCardShow(data);
                                    $('.j-balanceAmount').text(commafy((investAmount - $(this).val() - (base.cache.couponAmount)).toFixed(2)));
                                    payAmount = ($(this).val()-0).toFixed(2);
                                    balanceAmount = (investAmount - payAmount).toFixed(2);
                                    //余额加上现金券足够支付时
                                    if(inputVal>=investAmount){
                                        $('.z-quickPay-show').hide();
                                        selectedBankIndex = -1;
                                        balanceAmount = 0;
                                    }else{
                                        $('.z-quickPay-show').show();
                                        if(selectedBankIndex === -1){
                                            selectedBankIndex = 0;
                                        }
                                        balanceAmount = investAmount - inputVal - couponAmount;
                                    }
                                }
                                else if (inputVal === investAmount) {
                                    $('.z-quickPay-show').hide();
                                    selectedBankIndex = -1;
                                    balanceAmount = 0;
                                }
                                else if (inputVal > investAmount) {
                                    base.xTips({content:'请输入正确金额'});
                                    //alert('请输入正确金额');
                                    payAmount = (investAmount - base.cache.couponAmount).toFixed(2);
                                    $(this).val(payAmount);
                                    $('.z-quickPay-show').hide();
                                    selectedBankIndex = -1;
                                    balanceAmount = 0;
                                }


                            }
                            }else{
                                base.xTips({content:'输入金额错误'});
                            }
                        });

                        //快捷支付
                        var couponHtml = template('j-profitCoupon-tpl', {orderInfo: data.result.coupons});
                        $('#j-profitCoupon-list').html(couponHtml).css('max-height', '360px');
                        if (coupons.length) {
                            $('.j-choice').show();
                        }

                        $('.j-bankCardNow').on('click', function () {
                            $(this).addClass('z-clicked').siblings().removeClass('z-clicked');
                            selectedBankIndex = $(this).data('index');
                        });

                        payOrder(data.result, maxCoupon);

                    } else {
                        var resultCode = data.resultCodes[0];
                        switch (resultCode.code - 0) {
                            case 90384:
                                confirm(resultCode.msg);
                                location.href = environment.globalPath + '/products/m/list-all-all-1.html';
                                break;
                            default:
                                alert(resultCode.msg);
                        }
                    }




                }

            });

        }
    }
//显示银行卡
    function bankCardShow(data) {
        var investAmount = data.result.investAmount - 0,payAmount=$('.j-pay-amount').val()-0;
        $('.z-quickPay-show').show();
        $('.j-balanceAmount').text(commafy(investAmount-payAmount-couponAmount));
        selectedBankIndex = 0;
        var mobileNum = data.result.bankCardList[selectedBankIndex].bankMobile;
        $('.j-mobile').text(mobileNum.substr(7, 4));
    }

    //点击支付按钮后支付功能
    function payOrder(data, maxCoupon) {
        var orderNo = data.orderNo,
            isFirstCreaterOrder = true;
        var referrer=document.referrer.indexOf('submitOrder'),
            referrer2=document.referrer.indexOf('orderPayment');

        if(referrer===-1&&referrer2===-1){
            isFirstCreaterOrder=false;
        }
        var investAmount = data.investAmount - 0;

        var $payOrder = $('#j-payOrder');

        $payOrder.on('click', function () {
            if ($(this).hasClass('z-disabled')) {
                return;
            }

            if($('.j-pay-amount').val()===''){
                $('.j-pay-amount').val(0)
                payAmount=0;
            }
            //var usedCapital='';
            //if((data.investAmount-data.savingPotBalance)>0){
            //    return usedCapital=data.savingPotBalance;
            //}
            //else{
            //    return usedCapital=data.investAmount;
            //}
            //log(usedCapital);

            var couponId = $(this).attr('couponid'),
                $this = $(this),
                couponAmount = $(this).attr('couponAmount') - 0,
                reqData = {
                    orderNo: orderNo,
                    payAmount: 0.00,
                    usedCapital: data.investAmount - couponAmount,
                    isFirstCreaterOrder: isFirstCreaterOrder,
                    usedCouponAmount: couponAmount
                };
            var newBalance = investAmount - payAmount - couponAmount;
            if (newBalance > 0) {
                if(referrer===-1&&referrer2===-1){
                    couponAmount=data.orderUsedCashAmount - 0;
                }

                 bankReqData = {
                    orderNo: orderNo,
                    payAmount:(newBalance).toFixed(2),
                    usedCapital: payAmount,
                    isFirstCreaterOrder: isFirstCreaterOrder,
                    usedCouponAmount: couponAmount,
                    cardId: data.bankCardList[selectedBankIndex].id,
                    bankCode: data.bankCardList[selectedBankIndex].bankCode,
                    bankProvince: data.bankCardList[selectedBankIndex].bankProvince,
                    bankCity: data.bankCardList[selectedBankIndex].bankCity,
                    orderId: data.orderId
                };
                if (couponId) {
                    bankReqData.cashCouponNo = couponId;
                }
            }

            if (couponId) {
                reqData.cashCouponNo = couponId;
            }
            if (selectedBankIndex === -1) {
                //余额支付接口
                base.getAPI({
                    url: environment.globalPath + '/security/transaction/PayOrderOnAmount',
                    data: reqData,
                    callback: function (data) {
                        if (data.success) {
                            showPaySuccessDialog();
                            $('.j-cover').show();
                            $this.addClass('z-disabled');
                        }
                    }
                });
            } else {
                //快捷支付
                sendMessage(bankReqData);
            }
            //重新获取验证码
            $('.j-resent-box').on('click', function () {
                var smsCount = base.cache.smsCount;

                if (smsCount && smsCount < 1) {
                    sendMessage(bankReqData);
                    $('.j-countdown').show();
                    $('.j-resent-box').hide();
                }
            });
        });

        //send sms
        function sendMessage(bankReqData, resent) {
            //阻止异常提交
            if (!bankReqData) {
                return false
            }
            //发送验证码
            base.getAPI({
                url: env.path + '/security/transaction/PayOrderOnQuickPayment',
                data: bankReqData,
                callback: function (data) {
                    if (data.success) {
                        var item = data.result;

                        getPayReqData = {
                            ticket: item.ticket,
                            outAdvanceNo: item.rechargeNo,
                            orderNo: getOrderId
                        };
                        showCodeDialog(resent)
                    } else {
                        var errTip = data.resultCodes[0].msg;
                        base.xTips({content: errTip});
                    }
                }
            });

        }

        //快捷支付推进
        function getQuickPay(getPayReqData) {
            if (!getPayReqData.validCode) {
                return false
            }
            else {
                base.getAPI({
                    url: env.path + '/security/banlance/rechargeOnBankCardCheck',
                    data: getPayReqData,
                    callback: function (data) {
                        if (data.success === true) {
                            $('#j-dialog-next').hide();
                            showPaySuccessDialog()
                        }
                        else {
                            base.xTips({content: '输入验证码错误，请重新获取'});
                        }
                    }
                })
            }
        }
        //显示短信对话框
        var showCodeDialog = function (resent) {
            var $dialog = $('#j-dialog-next'),
                $countBox = $dialog.find('.j-countdown'),
                $resentBox = $dialog.find('.j-resent-box'),
                $num = $dialog.find('.j-count');

            if (!resent) {
                //显示窗口
                $dialog.show();
                $('.j-cover').show()
            } else {
                $resentBox.hide();
                $countBox.show();
                $num.text(60);
            }
            //点击取消按钮时的连接地址
            var cancleBtnUrl = document.referrer;
            $('.j-cancleUrl').attr('href', cancleBtnUrl);
            //倒计时
            $num.text(60 - 1);
            var timer = setInterval(function () {
                var count = Number($num.text());
                if (count > 0) {
                    $num.text(count - 1);
                } else {
                    $countBox.hide();
                    $resentBox.show();
                    clearInterval(timer);
                }
                base.cache.smsCount = count - 1;
            }, 1000);
        };
        //支付成功后的显示框
        var showPaySuccessDialog = function () {
            $('#j-dialog-next').hide();
            $('.j-paySuccess').show();


            var i = 2;
            var orderTimer = setInterval(function () {
                $('.j-showNum').text('(' + i + ')');
                i--;
            }, 1000);

            setTimeout(function () {
                clearInterval(orderTimer);
                $('.j-transactionLink').attr('href', environment.globalPath + '/mCenter/myTransaction');
                $('.j-showNum').hide();
            }, 3000);
        };

        //校验验证码，支付请求
        $('#j-quickPay-btn').on('click', function () {
            var smsCode = $('#j-smscode').val().replace(/ /g, '');
            var reg = /^\d{6,}$/;
            getPayReqData.validCode = smsCode;
            if (!reg.test(smsCode)) {
                base.xTips({content: '请输入正确的验证码'});
            }
            //}
            if (smsCode) {
                getQuickPay(getPayReqData)
            }
            else {
                base.xTips({content: '请输入验证码'});
            }


        });


        //现金券弹窗
        $('.j-choice').on('click', function () {

            $('body').css('overflow', 'hidden');
            $('.u-cashCoupon-popup').show();
            $('.j-cover').show();

            //选择现金券
            $('.u-cashCoupon>li').on('click', function () {

                //判断现金券是否可以使用
                var $payAmount=$('.j-pay-amount');
                payAmount = $payAmount.val();
                if ($(this).hasClass('z-disabled')) {
                    return false;
                }

                $(this).addClass('j-certificate').siblings().removeClass('j-certificate');
                cashCouponIndex = $(this).index();
                //余额支付
                couponAmount = $(this).attr('couponamount');
                var savingPotBalance = data.savingPotBalance - 0;
                //快捷支付
                couponAmount = couponAmount||0;
                var newBalance = investAmount - payAmount - couponAmount;
                $('.j-balanceAmount').text(commafy(newBalance.toFixed(2)));
                if (cashCouponIndex === 0) {
                    $('.j-cover').hide();
                    $('.u-cashCoupon-popup').hide();
                    $('.j-choice>input').attr('value', '不使用现金券');
                    //if (savingPotBalance >= investAmount) {
                    //    $('#j-pay-amount').val(investAmount);
                    //} else {
                    //    $('#j-pay-amount').val(savingPotBalance);
                    //}
                    //当纯用余额支付时
                    balanceAmount = investAmount - payAmount;
                    if(balanceAmount===0){
                        $('.j-pay-amount').val(payAmount);
                    }else if(balanceAmount>0){
                        $('.z-quickPay-show').show();
                    }
                    $payOrder.attr('couponid', '');
                    $payOrder.attr('couponAmount','');
                    couponAmount=0;
                    base.cache.couponAmount = 0

                    $('.j-balanceAmount').text(commafy((balanceAmount).toFixed(2)));
                }
                else {
                    var couponValidityNum = data.coupons[cashCouponIndex - 1].couponValidity,
                        amount = data.coupons[cashCouponIndex - 1].amount;
                    $payOrder.attr({'couponid': $(this).attr('couponcode'), 'couponAmount': amount});
                    base.cache.couponAmount = amount;
                    //余额加券是否够支付
                    //if (selectedBankIndex === -1) {
                    //
                    //}
                    if(newBalance<=0){
                        $('.j-pay-amount').val(investAmount - couponAmount);
                        $payOrder.removeClass('z-disabled');
                        selectedBankIndex = -1;
                        balanceAmount = 0;
                        $('.z-quickPay-show').hide();
                    }else{
                        $('.j-pay-amount').val(payAmount);
                        balanceAmount = newBalance;
                        if(selectedBankIndex===-1){
                            selectedBankIndex = 0;
                        }
                        $payOrder.removeClass('z-disabled');
                        $('.z-quickPay-show').show();
                    }
                    //if (savingPotBalance +(couponAmount-0)  >= investAmount) {
                    //    $('.j-pay-amount').val(investAmount - couponAmount);
                    //    $payOrder.removeClass('z-disabled');
                    //    selectedBankIndex = -1;
                    //    balanceAmount = 0;
                    //    $('.z-quickPay-show').hide();
                    //} else {
                    //    $('.j-pay-amount').val(savingPotBalance);
                    //    $payOrder.addClass('z-disabled');
                    //    $('.z-quickPay-show').show();
                    //}
                    $('.j-cover').hide();
                    $('.j-choice>input').attr('value', '+' + amount + '元' + '     ' + couponValidityNum);
                    $('.u-cashCoupon-popup').hide();
                }

            });

        });
        //关闭按钮
        $('.j-btn-close').click(function () {
            $('.j-cover').hide();
            $('.u-cashCoupon-popup').hide();
        });


    }

    /**
     *金额格式化
     * @param amount{Number} 需要格式化的金额
     * @param format{String} 格式化类型，split=逗号分割的整数
     */
    template.helper('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return commafy(amount);
                break;
        }

        // 用于给数字添加千分号
        function commafy(num) {
            num = num + "";
            var re = /(-?\d+)(\d{3})/;
            while (re.test(num)) {
                num = num.replace(re, "$1,$2");
            }
            return num;
        }
    });
    // 用于给数字添加千分号
    function commafy(num) {
        var num2 = (+num).toFixed(2) + "";
        var re = /(-?\d+)(\d{3})/;
        while (re.test(num2)) {
            num2 = num2.replace(re, "$1,$2");
        }
        return num2;
    }

    /**
     * 银行名字对应格式化
     */
    template.helper('bankNameFormat',function(bankCode){
//银行名字
        var bankNameList = {
            'ICBC': "中国工商银行",
            "ABC": "中国农业银行",
            "CCB": "中国建设银行",
            "BOC": "中国银行",
            "COMM": "交通银行",
            "CMB": "招商银行",
            "CMBC": "民生银行",
            "SZPAB": "平安银行",
            "GDB": "广发银行",
            "CITIC": "中信银行",
            "CEB": "光大银行",
            "HXB": "华夏银行",
            "CIB": "兴业银行",
            "SPDB": "浦发银行",
            "PSBC": "中国邮储银行",
            "BCCB": "北京银行",
            "BOS": "上海银行",
            "CZB": "浙商银行"
        };
        return bankNameList[bankCode];
    });


    /**
     *
     * @param amount
     * @returns {*}
     */
    //金额格式化
    function splitAmount(amount) {

        if (amount >= 1000) {

            var amountStr = amount.toString(),
                size = parseInt(amountStr.length / 3),
                amountArray = amountStr.split('').reverse();

            for (var i = 1; i <= size; i++) {
                var j = i * 3 - 1;
                if (j !== amountArray.length - 1) {
                    amountArray[j] = ',' + amountArray[j];
                }
            }

            return amountArray.reverse().join('');

        } else {
            return amount;
        }
    }

});