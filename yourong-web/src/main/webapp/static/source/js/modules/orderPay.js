;(function () {
    Vue.config.devtools = true
    var path = environment.globalPath

    window.vm = new Vue({
        el: '#j-orderPay',
        data: {
            detailExtend: false,
            init: window.ORDER_INFO || {},
            useBalance: true,
            useCoupon: true,
            needPay: 0,
            selectedCoupon: {
                code: 0,
                amount: 0
            },
            tips: {
                selectedCoupon: '请选择现金券'
            },
            countdown: 5,
            layer: 0,
            relayUrl: 'javascript:void(0);',
            url: {
                p2p: '/transaction/p2pContract/view?orderId=',
                debt: '/transaction/contract/view?orderId='
            },
            cache: {},
            transactionId: 0
        },
        watch: {
            useCoupon: function (n, o) {

                if (!n) {
                    this.tips.selectedCoupon = this.tips.selectCoupon
                    this.selectedCoupon.code = 0
                    this.selectedCoupon.amount = 0
                }
            },
            useBalance: function (n, o) {

                if (!n) {
                    this.usedBalance = 0
                }
            },
            usedBalance: function (n) {
                var init = this.init,
                    usedBalance = n,
                    couponAmount = this.selectedCoupon.amount

                if (typeof(n) === 'undefined') {
                    usedBalance = this.usedBalance = 0
                } else {
                    this.usedBalance = usedBalance = _.toFixed(n - 0)
                }

                if (usedBalance <= init.amount - couponAmount) {
                    this.needPay = (init.amount - usedBalance - couponAmount).toFixed(2)
                } else {
                    this.usedBalance = init.amount - couponAmount
                }

                //余额不足
                if(usedBalance>init.balance){
                    this.usedBalance = init.balance
                }
            },
            'selectedCoupon.amount': function (n, o) {
                var init = this.init,
                    usedBalance = this.usedBalance,
                    couponAmount = this.selectedCoupon.amount

                if (n > o) {
                    if (usedBalance >= init.amount - couponAmount) {
                        this.usedBalance = init.amount - couponAmount
                    }
                }

                this.needPay = (init.amount - this.usedBalance - this.selectedCoupon.amount).toFixed(2)
            }
        },
        computed: {
            status: function () {
                /**
                 * 10=>余额不足未开通委托 20=>余额不足已开通委托
                 * 30=>余额充足未开通委托 40=>余额充足已开通委托
                 */
                var init = this.init,
                    usedBalance = this.usedBalance ? this.usedBalance - 0 : 0,
                    couponAmount = init.usedCashAmount ? init.usedCashAmount - 0 : this.selectedCoupon.amount,
                    status = 0,
                    balanceEnough = usedBalance + couponAmount >= init.amount,
                    couponPay = init.amount === couponAmount
                switch (true) {
                    //开通委托时提交充值支付,关闭委托再去支付页面
                    case !balanceEnough && !init.delegated && !init.firstOrder:
                        status = 70
                        break
                    case !balanceEnough && !init.delegated:
                        status = 10
                        break
                    case balanceEnough&&init.delegated && !init.firstOrder:
                        status = 60
                        break
                    case !balanceEnough && init.delegated:
                        status = 20
                        break
                    case (balanceEnough && init.delegated) || couponPay:
                        status = 40
                        break
                    case balanceEnough && !init.delegated:
                        status = 30
                        break
                }
                return status
            },
            projectName: function () {
                return this.init.projectName.split(/\s/)[0]
            },
            contractUrl: function () {
                var id = this.init.orderId
                return this.isDirect ? this.url.p2p + id : this.url.debt + id
            },
            transitUrl: function () {
                var id = this.transactionId
                return id ? '/member/transit?transactionId=' + id : '#'
            }
        },
        methods: {
            extendDetail: function () {
                this.detailExtend = !this.detailExtend
            },
            switchCheckbox: function (name, val) {
                var switchVal = val ? false : true
                this.$set(name, switchVal)

                if (name === 'useBalance') {
                    this.usedBalance = '0.00'
                }
            },
            /**
             * 选择现金券
             * @param code {String} 编号
             * @param amount {Number} 面额
             * @param limit 使用限制
             * @param bigAmountLimit {Number} 面额超过投资金额的限制
             * @param event {Event} 选择事件
             */
            selectCoupon: function (code, amount, limit, bigAmountLimit, event) {

                if (!limit && !bigAmountLimit) {

                    this.selectedCoupon = code ?
                    {code: code, amount: amount} : {code: 0, amount: 0}

                    var tips = this.tips

                    if (limit !== 0) {
                        this.tips.selectedCoupon = event.currentTarget.innerHTML
                    } else {
                        this.tips.selectedCoupon = tips.selectCoupon
                    }
                }
            },
            limitAmountInput: function (e) {
                var val = e.currentTarget.value,
                    key = e.key,
                    code = e.keyCode

                if ((code === 8 || code === 37 || code === 39)
                    || (val.indexOf('.') === -1 && code === 110)
                    || (code >= 48 && code <= 57) || (code >= 96 && code <= 105)) {
                    return true
                } else {
                    e.preventDefault()
                    if(window.event){
                        window.event.returnValue = false
                    }
                    return false
                }
            },
            limitMaxBalance: function (e) {
                var usedBalance = e.currentTarget.value,
                    balance = this.init.balance,
                    neededAmount = this.init.amount - this.selectCoupon.amount
                if (usedBalance > balance) {
                    if (usedBalance > neededAmount) {
                        this.usedBalance = balance
                    }
                } else {
                    if (usedBalance > neededAmount) {
                        this.usedBalance = neededAmount
                    }
                }
            },
            gotoPay: function (type, updateUrl) {
                var layer = type
                if (type!=10&&!this.submitAvailabled) {
                    return false
                }

                var self = this,
                    coupon = self.selectedCoupon,
                    code = coupon.code,
                    url = '/member/payRelay?',
                    data = {orderNo: self.init.orderNo, usedCapital: self.usedBalance}

                if (code && code.length > 15) {
                    data.cashCouponNo = coupon.code
                    data.usedCouponAmount = coupon.amount
                }

                if (+this.needPay > 0) {
                    data.payAmount = this.needPay
                }

                var balancePay = false
                switch (type) {
                    case 10:
                    case 20:
                    case 70:
                        data.payType = 'recharge'
                        data.isFirstCreaterOrder = self.init.firstOrder
                        break
                    case 30:
                    case 60:
                        data.payType = 'balance'
                        break
                    case 40:
                        data.payType = 'balance'
                        if (self.init.firstOrder && !updateUrl) {
                            balancePay = true
                            self.timeCountdown('countdown', 5)
                            self.balancePay(data)
                        }
                        break
                }

                if (layer && !updateUrl) {
                    self.layer = layer
                }

                if (!balancePay) {
                    if (type === 10) {
                        self.relayUrl = '/memberBalance/rechargePage?payAmount=' + self.needPay
                    } else {
                        self.relayUrl = url + self.encodeUrl(data)
                    }
                }
            },
            getOrderStatus: function () {
                var url = environment.globalPath + '/transaction/getByOrderId',
                    self = this

                $.xPost({
                    url: url,
                    data: {orderId: self.init.orderId},
                    callback: function (data) {
                        if (data.success) {
                            var status = data.result.order.status

                            switch (status) {
                                case 1:
                                case 0:
                                case 2:
                                case 4:
                                    status = 1
                                    break
                                case 8:
                                    status = 18
                                    break
                                case 9:
                                    window.location.reload()
                                    break
                            }
                            self.init.orderStatus = status

                            var transaction = data.result.transactionDto
                            if (transaction && transaction.id) {
                                self.transactionId = transaction.id
                            }
                        } else {
                            self.relayCallback(data.resultCodeEum[0])
                        }
                    }
                })
            },
            balancePay: function (data) {
                var self = this
                $.xPost({
                    url: '/transaction/pay/order/cashDesk',
                    data: data,
                    callback: function (data) {
                        if (!data.success) {
                            self.relayCallback(data.resultCodeEum[0])
                        }
                    }
                })
            },
            encodeUrl: function (data) {
                var props = Object.keys(data),
                    propsList = []

                props.forEach(function (item, index) {
                    var suffix = (index !== props.length - 1) ? '&' : ''
                    propsList.push(item + '=' + data[item] + suffix)
                })
                return propsList.join('')
            },
            //前往开通
            gotoDelegate: function () {
                this.layer = 50
                this.relayUrl = '/member/pageRelay?from=delegatePay&type=1'
            },
            //重新支付
            rePay: function (e) {
                e.preventDefault()
                if(window.event){
                    window.event.returnValue = false
                }
                window.location.reload()
                return false
            },
            getWithholdAuthorityStatus: function () {
                var self = this,
                    curTime = new Date().getTime()

                if (self.cache.hoverTime && curTime - self.cache.hoverTime < 5000) {
                    return false
                }
                $.xPost({
                    url: '/member/queryWithholdAuthority',
                    callback: function (data) {
                        if (data.success) {
                            if (data.result !== self.init.delegated) {
                                self.layer = 100
                                $.xDialog({
                                    content: '委托支付状态已改变,请刷新当前页面',
                                    callback: function () {
                                        window.location.reload()
                                    }
                                })
                            }
                            self.cache.hoverTime = new Date().getTime()
                        }
                    }
                })
            },
            flushUsedBalance: function (e) {
                $('#j-usedBalance').blur()
            },
            /**
             * 倒计时
             * @param name
             * @param seconds
             */
            timeCountdown: function (name, seconds) {
                var self = this,
                    sec = seconds - 1

                var timer = setInterval(function () {
                    if (sec > 0) {
                        self.$set(name, sec)
                        sec--
                    } else {
                        clearInterval(timer)
                        if(self.layer!==100){
                            self.getOrderStatus()
                        }
                    }
                }, 1000)
            },
            relayCallback: function (error) {
                var msg = error.msg,
                    code = error.code
                if (code === '90406') {
                    msg = '亲，该项目不可投，换个项目试试吧'
                }
                var self = this

                this.layer = 100
                $.xDialog({
                    content: msg,
                    callback: function () {

                        switch (error.code) {
                            case '90406':
                            case '90407':
                            case '90384':
                                location.href = '/products/list-all-all-investing-1-createTimeAsc-1.html'
                                break
                            case '90409':
                                location.href = '/products/detail-'+self.init.projectId+'.html'
                                break
                            default:
                                location.reload()
                        }

                    }
                })
            },
            updateLink: function (status) {
                var self = this

                if (status === 31) {
                    self.relayUrl = '/member/pageRelay?from=delegatePay&type=1'
                } else {
                    self.flushUsedBalance()
                    self.gotoPay(status, true)
                }
            }
        }
    })
    //处理IE下mouseenter事件不触发的问题
    $('body').on('mouseenter', '.j-updateLink', function () {
        var status = $(this).data('status'),
            isIE = !!window.ActiveXObject || "ActiveXObject" in window

        if (isIE) {
            if (status === 32) {
                vm.getWithholdAuthorityStatus()
            } else {
                vm.updateLink(+status)
            }
        }
    })
})()