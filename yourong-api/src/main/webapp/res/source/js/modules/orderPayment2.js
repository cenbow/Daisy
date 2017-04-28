/*global define,require,$,environment,alert,confirm,console,env,Vue*/
define(['base'], function (require, exports, module) {
    'use strict'
    var base = require('base'),
        path = environment.globalPath
    //开启chrome调试工具
    Vue.config.devtools = true
    // Vue.filter('formatName', function (value) {
    //     return value.split('期')[0]
    // });
    // 给数字添加千分号
    Vue.filter('amountSplit', function (num) {
        if (!num) {
            return 0
        }
        var n = num + "",
            reg = /(-?\d+)(\d{3})/
        return n.replace(reg, "$1,$2")
    })

    /**
     * 银行名字对应格式化
     * @param {String} 银行英文缩写
     */
    Vue.filter('bankNameFormat', function (bankCode) {
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
        }
        return bankNameList[bankCode]
    })

    var payment = new Vue({
        el: '#payment',
        data: {
            path: path,//根路径
            inputAmount: '',//输入的使用余额
            goBackUrl:document.referrer||path+'/mCenter/home',//返回到上一个页面
            inputKey:-1,
            order: {},//订单信息
            couponsPopupOpened: false,//现金券弹窗是否关闭
            detailInfoOpened:false,//项目详情是否展开
            smsDialog: {
                opened: false,
                isResent: false,
                isCountdown: true,
                countNum: 60,
                validCode: '',
                cancelUrl: document.referrer,
                mobileNum: 0
            },
            couponBtnText: '请选择现金券',
            selectedCoupon: null,//已选择的现金券信息
            quickpayAmount: 0,//使用快捷支付的金额
            balanceNotEnough: false,//余额是否足够
            isWithholdAuthority:false,//是否开通委托支付
            isNeedSina:false,//是否需要跳转到新浪
            isFirstCreateOrder: true,//是否第一次创建订单
            apiUrl: {
                orderInfo: path + '/security/order/queryPayOrderInfo',
                balancePay: path + '/security/transaction/pay/order/cashDesk',
                rechargePay: path + '/security/transaction/pay/order/recharge'
            },
            bankcardIndex: 0,
            bankReqData: null,
            transactionUrl: 'javascript:',
            paySucessCount: 3,
            btnDisabled: false,//确认支付按钮的状态
            isagreementChecked:false,
            paySuccess:false
        },
        props: ['orderid','pid','type'],
        created: function () {
            var self = this
            base.getAPI({
                url: self.apiUrl.orderInfo,
                data: {orderId: self.orderid},
                version:'1.7.0',
                callback: function (data) {
                    var orderData = data.result
                    if (data.success) {
                        self.order = orderData
                        self.isWithholdAuthority=orderData.isWithholdAuthority
                        self.quickpayAmount=orderData.orderPayAmount||0
                        //初始化输入框金额
                        if (orderData.orderSavingPotAmount === null) {
                            var inputAmount = ''
                            if (orderData.investAmount > orderData.savingPotBalance) {
                                inputAmount = orderData.savingPotBalance
                                self.btnDisabled=true
                            } else {
                                inputAmount = orderData.investAmount
                                self.balanceNotEnough=true
                            }
                            self.inputAmount = inputAmount - 0
                        } else {
                            self.inputAmount = orderData.orderSavingPotAmount
                            self.isFirstCreateOrder = false
                        }
                        //判断是否出现3.2.1倒计时
                        if(self.type==4||self.type==6){
                           self.Countdown(2)
                        }else{

                        }
                    }
                    else {
                        alert(data.resultCodes[0].msg)
                    }
                }
            })
        },
        watch: {
            'inputAmount': function (n, o) {
                var couponAmount = this.$get('selectedCoupon.amount') - 0||this.order.orderUsedCashAmount || 0 ,
                    investAmount = this.order.investAmount - 0,
                    newInputAmount = n - 0
                var self = this,
                    savingBalance = this.order.savingPotBalance
                if(this.order.orderSavingPotAmount){
                    newInputAmount=this.order.orderSavingPotAmount
                }else{
                    if (newInputAmount > savingBalance) {
                        newInputAmount = savingBalance
                        this.investAmount = savingBalance
                    }
                }

                var quickpayAmount = investAmount - couponAmount - newInputAmount
                //输入金额不能大于投资额与券额的差值
                if (newInputAmount >= investAmount - couponAmount) {

                    if (newInputAmount > investAmount - couponAmount) {
                        base.xTips({content: '请输入正确金额'})
                    }

                    setTimeout(function () {
                        self.inputAmount = investAmount - couponAmount
                    }, 100)

                    if (newInputAmount == investAmount - couponAmount) {
                        this.quickpayAmount = 0
                        self.btnDisabled=false
                        self.isagreementChecked=false
                        self.isNeedSina=false
                    }
                } else {
                    this.quickpayAmount = quickpayAmount.toFixed(2) - 0
                    if(this.isWithholdAuthority){
                        self.btnDisabled=false
                        self.isagreementChecked=false
                        self.isNeedSina=true
                    }else {
                        self.btnDisabled=true
                        self.isagreementChecked=true
                    }
                }
                if (typeof(this.inputAmount) !== 'number') {
                    var amountVal = (n+'').replace(this.inputKey,'')

                    if(amountVal.split('.').length>2){
                        amountVal = o
                    }

                    $('#j-inputAmount').val(amountVal)
                    this.inputAmount = amountVal - 0
                }
            },
            'selectedCoupon.amount': function (n, o) {
                var newInputAmount,
                    self = this,
                    newAmount = n || 0,
                    oldAmount = o || 0,
                    investAmount = this.order.investAmount - 0,
                    savingBalance = this.order.savingPotBalance,
                    newQuickpayAmount = investAmount - this.inputAmount - newAmount
                //当余额足够，且没有快捷卡或安全卡时，切换现金券金额使用余额数变换
                if (savingBalance + newAmount >= investAmount ) {
                // && !self.order.bankCardList.length
                    this.inputAmount = investAmount - newAmount
                    newQuickpayAmount = 0
                }
                if (newAmount === investAmount) {
                    this.inputAmount = 0
                }

                if (newQuickpayAmount > 0) {
                    this.quickpayAmount = newQuickpayAmount.toFixed(2) - 0
                    if(this.isWithholdAuthority){
                        self.btnDisabled=false
                        self.isagreementChecked=false
                        self.isNeedSina=true
                    }else {
                        self.btnDisabled=true
                        self.isagreementChecked=true
                    }

                } else {
                    this.quickpayAmount = 0
                    this.inputAmount = investAmount - newAmount
                    self.btnDisabled=false
                    self.isagreementChecked=false
                    self.isNeedSina=false
                }
            },
            'agreementChecked': function (n, o) {
                if(this.isagreementChecked==true){
                    return false
                }else{
                    this.btnDisabled=!n
                }


            }
        },
        methods: {
            //选择现金券
            selectCoupon: function (event) {
                var $coupon = $(event.currentTarget),
                    investAmount = this.order.investAmount,
                    data = $coupon.data(),
                    text = '',
                    couponData = {}

                if ($coupon.hasClass('z-disabled') || data.amount > investAmount - 0) {
                    return
                }

                this.toggleCouponsPopup(false)

                if (data.index < 0) {
                    text = $coupon.text()
                    couponData = null
                }
                else {
                    text = '+' + data.amount + '元现金券'
                    couponData = {
                        amount: data.amount - 0,
                        code: data.code
                    }
                }
                this.couponBtnText = text
                this.selectedCoupon = couponData
            },

            limitInputAmount: function (event) {
                var $input = $(event.currentTarget),
                    savingBalance = this.order.savingPotBalance,
                    val = $input.val()

                var pointVal = val.split('.')
                if (pointVal.length===2&&pointVal[1].length>2) {
                    $input.val(this.inputAmount.toFixed(2))
                }

                //限制不能小于零或大于存钱罐余额
                val = $input.val() - 0
                if (val > savingBalance) {
                    $input.val(savingBalance)
                } else if (val < 0) {
                    $input.val(0)
                }
            },
            stopIllegalInput: function (event) {
                var key = event.key
                if(!/[0-9.]/.test(key)){
                    this.inputKey =  key
                }
            },
            //切换现金券窗口
            toggleCouponsPopup: function (opened) {
                this.couponsPopupOpened = opened

                if (opened) {
                    base.cover.show('#j-cashCoupons')
                } else {
                    base.cover.hide('#j-cashCoupons')
                }
            },
            //获取最大的券金额
            getMaxCoupon: function (orderData) {
                var coupons = orderData.coupons

                if (orderData.coupons && orderData.coupons.length) {
                    var couponsAmountList = []
                    coupons.forEach(function (item, index) {
                        var amount = item.amount - 0

                        if (item.isAvailable && amount < orderData.investAmount - 0) {
                            couponsAmountList.push(amount)
                        }
                    })
                    couponsAmountList.sort(function (a, b) {
                        return b - a
                    })
                    return couponsAmountList[0]
                } else {
                    return 0
                }
            },
            // 未开通委托充值
            gotoRecharge:function () {
                var rechargeAmount=this.quickpayAmount
                window.location.href=path+'/mCenter/recharge?type=2&investment='+rechargeAmount+'&orderId='+this.order.id
            },
            payOrder: function () {
                var couponAmount = this.selectedCoupon ? this.selectedCoupon.amount : null,
                    couponCode = this.selectedCoupon ? this.selectedCoupon.code : null,
                    order = this.order,
                    self = this
                if (this.btnDisabled) {
                    return false
                }
                if(this.isWithholdAuthority){
                    //开通委托支付
                    if(!self.isNeedSina){
                        var reqData={
                            orderNo:self.order.orderNo,
                            payAmount:0.00,
                            usedCapital:order.investAmount - couponAmount,
                            cashCouponNo:couponCode,
                            usedCouponAmount:couponAmount
                        }
                        //余额足够，余额支付
                        base.getAPI({
                            url:self.apiUrl.balancePay,
                            version:'1.7.0',
                            data:reqData,
                            callback:function (data) {
                                if(data.success){
                                    self.Countdown(2)
                                }else{
                                    alert(data.resultCodes[0].msg)
                                }

                            }
                        })
                    }else{
                        var rechargeData={
                            orderNo:self.order.orderNo,
                            payAmount:self.quickpayAmount,
                            usedCapital:(order.investAmount - couponAmount-self.quickpayAmount).toFixed(2),
                            cashCouponNo:couponCode,
                            isFirstCreaterOrder: this.isFirstCreateOrder,
                            usedCouponAmount:couponAmount
                        }
                        //充值支付
                        base.getAPI({
                            url:self.apiUrl.rechargePay,
                            version:'1.7.0',
                            data:rechargeData,
                            callback:function (data) {
                                if(data.success){
                                    window.location.href=data.result
                                }else{
                                    alert(data.resultCodes[0].msg)
                                }
                            }
                        })
                    }

                }else{
                    //未开通委托支付,余额支付
                    var reqData={
                        orderNo:self.order.orderNo,
                        payAmount:0.00,
                        usedCapital:order.investAmount - couponAmount,
                        cashCouponNo:couponCode,
                        usedCouponAmount:couponAmount
                    }
                    //余额足够，余额支付
                    base.getAPI({
                        url:self.apiUrl.balancePay,
                        version:'1.7.0',
                        data:reqData,
                        callback:function (data) {
                            if(data.success){
                                if(data.result.allCashCouponPay){
                                   self.Countdown(2)
                                }else{
                                    window.location.href=data.result.redirectUrl
                                }
                            }else{
                                alert(data.resultCodes[0].msg)
                            }

                        }
                    })

                }
                this.btnDisabled=true
            },
            spcialOrderPay:function () {
                var couponAmount = this.selectedCoupon ? this.selectedCoupon.amount : null,
                    couponCode = this.selectedCoupon ? this.selectedCoupon.code : null,
                    self=this
                var rechargeData={
                    orderNo:self.order.orderNo,
                    payAmount:self.quickpayAmount,
                    usedCapital:(self.order.investAmount - couponAmount-self.quickpayAmount).toFixed(2),
                    cashCouponNo:couponCode,
                    isFirstCreaterOrder: this.isFirstCreateOrder,
                    usedCouponAmount:couponAmount
                }
                //充值支付
                base.getAPI({
                    url:self.apiUrl.rechargePay,
                    version:'1.7.0',
                    data:rechargeData,
                    callback:function (data) {
                        if(data.success){
                            window.location.href=data.result
                        }else{
                            alert(data.resultCodes[0].msg)
                        }
                    }
                })
            },
            // sendMessage: function () {
            //     var self = this,
            //         order = self.order,
            //         card = order.bankCardList[self.bankcardIndex]
            //
            //     self.bankReqData = {
            //         orderNo: order.orderNo,
            //         payAmount: self.quickpayAmount,
            //         usedCapital: self.inputAmount,
            //         isFirstCreaterOrder: self.isFirstCreateOrder,
            //         usedCouponAmount: self.selectedCoupon ? self.selectedCoupon.amount : 0,
            //         cardId: card.id,
            //         bankCode: card.bankCode,
            //         bankProvince: card.bankProvince,
            //         bankCity: card.bankCity,
            //         orderId: order.orderId
            //     }
            //
            //     if (self.selectedCoupon) {
            //         self.$set('bankReqData.cashCouponNo', self.selectedCoupon.code)
            //     }
            //     if (order.orderUsedCashNo !== null) {
            //         self.$set('bankReqData.cashCouponNo', order.orderUsedCashNo)
            //         self.$set('bankReqData.usedCouponAmount', order.orderUsedCashAmount)
            //     }
            //     if (order.orderPayAmount !== null) {
            //         self.$set('bankReqData.payAmount', order.orderPayAmount)
            //     }
            //     //发送验证码
            //     base.getAPI({
            //         url: self.apiUrl.sendSMS,
            //         data: self.bankReqData,
            //         callback: function (data) {
            //             if (data.success) {
            //                 var item = data.result
            //                 self.$set('smsDialog.ticket', item.ticket)
            //                 self.$set('smsDialog.outAdvanceNo', item.rechargeNo)
            //                 self.showSMSDialog()
            //             } else {
            //                 var errTip = data.resultCodes[0].msg
            //                 base.xTips({content: errTip})
            //             }
            //         }
            //     })
            // },
            // //显示短信验证码窗口
            // showSMSDialog: function () {
            //     //显示窗口
            //     if (!this.smsDialog.opened) {
            //         this.smsDialog.opened = true
            //         base.cover.show(null)
            //     }
            //
            //     //倒计时
            //     var self = this
            //     this.smsDialog.countNum = this.smsDialog.countNum - 1
            //     var timer = setInterval(function () {
            //         var count = self.smsDialog.countNum
            //         if (count > 0) {
            //             self.smsDialog.countNum = count - 1
            //         } else {
            //             self.smsDialog.isCountdown = false
            //             self.smsDialog.isResent = true
            //             clearInterval(timer)
            //             self.smsDialog.countNum = 60
            //         }
            //         base.cache.smsCount = count - 1
            //     }, 1000)
            // },
            // //重发验证码
            // resendSMS: function () {
            //     var smsCount = base.cache.smsCount
            //
            //     if (smsCount && smsCount < 1) {
            //         this.smsDialog.validCode = ''
            //         this.sendMessage()
            //         this.smsDialog.isCountdown = true
            //         this.smsDialog.isResent = false
            //     }
            // },
            // //校验短信验证码
            // checkSMSCode: function () {
            //     var smsCode = this.smsDialog.validCode.replace(/ /g, ''),
            //         reg = /^\d{6,}$/
            //
            //     if (smsCode) {
            //         if (reg.test(smsCode)) {
            //             this.postQuickpay()
            //         } else {
            //             base.xTips({content: '请输入正确的验证码'})
            //         }
            //     }
            //     else {
            //         base.xTips({content: '请输入验证码'})
            //     }
            // },
            // //推进快捷支付
            // postQuickpay: function () {
            //     if (!this.smsDialog.validCode) {
            //         return false
            //     }
            //
            //     var self = this,
            //         sms = self.smsDialog
            //     base.getAPI({
            //         url: self.apiUrl.quickPay,
            //         data: {
            //             validCode: sms.validCode,
            //             ticket: sms.ticket,
            //             outAdvanceNo: sms.outAdvanceNo,
            //             orderNo: self.order.orderId
            //         },
            //         callback: function (data) {
            //             if (data.success) {
            //                 self.smsDialog.opened = false
            //                 self.showSuccessDialog()
            //             }
            //             else {
            //                 base.xTips({content: '输入验证码错误，请重新获取'})
            //             }
            //         }
            //     })
            // },
            // //显示支付成功窗口
            // showSuccessDialog: function (type) {
            //     if (type) {
            //         this.smsDialog.opened = false
            //     }
            //     base.cover.show(null)
            //     this.$set('paySuccess', true)
            //
            //     var n = 2, self = this
            //     var timer = setInterval(function () {
            //         if (n > 0) {
            //             self.paySucessCount = n
            //             n--
            //         } else {
            //             clearInterval(timer)
            //             self.paySucessCount = n
            //             window.location.href=path+'/mCenter/finishInvest?orderId='+self.orderid
            //         }
            //     }, 1000)
            // },
            detailInfoOpen:function () {
                if(this.detailInfoOpened){
                    this.detailInfoOpened=false
                }else{
                    this.detailInfoOpened=true
                }
            },
            getContract: function (){
                //债权协议
                var $preview=$('#j-previewForm'),
                    _this=this,
                    $orderInfo=$('#orderInfo'),
                    $contract=$('#j-contract');
                if($('#j-goback').length){
                    $contract.show();
                    $orderInfo.hide()
                    return false;
                }
                $orderInfo.hide()
                var formArray=$preview.serializeArray(),
                    url=$preview.attr('url'),
                    xToken=$('#xToken').val();
                if(_this.order.investType==2){
                    url=path+'/security/transaction/p2pContract/preview';
                }
                formArray.push({name:'xToken',value:xToken});
                $.ajax({
                    url:url,
                    type:'POST',
                    data:formArray,
                    success: function (data) {
                        $contract.html(data).append('<div class="u-contract-back" id="j-goback">返回</div>');
                        $('#j-goback').on('click', function () {
                            $contract.hide();
                            $orderInfo.show()
                        });
                    }
                });

            },
            gotoSetPaymentCipher:function (type,mType) {
                base.getAPI({
                    url:environment.globalPath+'/security/member/handWithholdAuthority',
                    version:'1.7.0',
                    data:{
                        type:type,
                        mType:mType,
                        orderId:this.order.id
                    },
                    callback:function (data) {
                        window.location.href=data.result
                    }
                })
            },
            Countdown:function (n) {
                var self=this
                base.cover.show(null)
                self.paySuccess=true
                var timer = setInterval(function () {
                    if (n > 0) {
                        self.paySucessCount = n
                        n--
                    } else {
                        clearInterval(timer)
                        self.paySucessCount = n
                        // self.paySuccess=false
                        // base.cover.hide()
                        window.location.href=path+'/mCenter/finishInvest?orderId='+self.orderid
                    }
                }, 1000)
            }
        }
    })
})