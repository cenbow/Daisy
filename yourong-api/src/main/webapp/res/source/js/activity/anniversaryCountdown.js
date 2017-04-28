/*global receiveData,environment,os*/
define(['base'], function (require, exports, module) {
    'use strict'
    var path = environment.globalPath,
        base = require('base'),
        hook = new AppHook(os)

    Vue.config.devtools = true

    window.vm = new Vue({
        el: '#j-countdown',
        data: {
            urls: {
                init: path + '/activity/anniversaryLast/index',
                receive: path + '/activity/dynamicInvoke/'
            },
            init: {},
            status: 2,
            startTime: 0,
            endTime: 0,
            receiveStartTime: 0,
            receiveEndTime: 0,
            startReceive: [],
            open: false,
            close: false,
            hours: '',
            minutes: '',
            seconds: '',
            countdownStatusText: '开始',
            logined: os > 2 ? logined : hook.logined,
            loginUrl: path + '/mstation/login?from=' + location.href
        },
        created: function () {
            var self = this
            window.hookCallback = this.hookCallback

            if (receiveData.success) {
                var init = self.init = receiveData.result
                self.status = init.activityStatus
                self.startTime = self.getSeconds(init.receiveStartTime)
                self.endTime = self.getSeconds(init.receiveEndTime)
            }

            var date = +environment.serverDate,
                curDate = new Date(date).toJSON().split('T')[0].replace(/-/g, '/'),
                curTime = this.getSeconds(new Date(date).toJSON().split(/[TZ]/)[1]) + 3600 * 8,
                zeroTime = new Date(curDate + ' 23:59:59').getTime(),
                startTime = this.startTime,
                endTime = this.endTime

            var seconds = 0

            switch (this.status) {
                case 2:
                    this.countdownStatusText = '开始'
                    seconds = startTime + Math.ceil((this.init.startTime - date)/1E3)
                    break
                case 4:
                    if (curTime < startTime) {
                        this.countdownStatusText = '开始'
                        seconds = startTime - curTime
                    } else if (curTime >= startTime && curTime < endTime) {
                        this.countdownStatusText = '结束'
                        seconds = endTime - curTime
                    } else {
                        this.countdownStatusText = '开始'
                        seconds = startTime + (zeroTime - date) / 1E3 + 1
                    }

                    break
                case 6:
                    break
            }

            if (seconds > 0) {
                this.countdown(parseInt(seconds))
            }

        },
        computed: {
            logined: function () {
                return os > 2 ? logined : hook.logined
            },
            loginUrl:function () {
                  var url = path + '/mstation/login?from=' + location.href,nullUrl='javascript:void(0)'

                    if(this.logined){
                      url=nullUrl
                    }else{
                        if(os===2){
                            url='yrw://invokeMethod=loginIn'
                        }else if(os===1){
                            url = nullUrl
                        }
                    }

                return url
            },
            remainDay: function () {
                var now = +environment.serverDate,
                    remain = (this.init.weakStartTime - now) / (3600 * 24 * 1000)
                if (remain < 0) {
                    remain = 0
                }
                return Math.ceil(remain)
            },
            receiveStartTime: function () {
                var timeArray = this.init.receiveStartTime.split(':')
                return timeArray[0] + ':' + timeArray[1]
            },
            receiveEndTime: function () {
                var timeArray = this.init.receiveEndTime.split(':')
                return timeArray[0] + ':' + timeArray[1]
            }
        },
        methods: {
            // 领取红包按钮文案
            couponText: function (amount) {
                var text = ''
                var count = this.$get('init.coupon' + amount)
                switch (this.status) {
                    case 2:
                        text = '活动未开始'
                        break
                    case 4:
                        text = '我要领取'
                        if (count < 1) {
                            text = '今日已领完'
                        }
                        break
                    case 6:
                        text = '活动已结束'
                        break
                }


                return text
            },
            getSeconds: function (timeString) {
                var list = timeString.split(':')
                return list[0] * 3600 + list[1] * 60 + Math.ceil(list[2] * 1)
            },
            openCheck: function () {
                this.open = true
            },
            closeCheck: function () {
                this.open = false
            },
            fixZero: function (num) {
                return +num < 10 ? '0' + num : num
            },

            countdown: function (seconds, callback) {
                var sec = seconds - 1,
                    self = this

                count()

                var timer = setInterval(function () {

                    if (sec > 0) {
                        count()
                    } else {
                        clearInterval(timer)
                        callback()
                    }
                }, 1000)

                function count() {
                    var hours = parseInt(sec / 3600),
                        minutes = parseInt((sec - hours * 3600) / 60)
                    self.$set('hours', self.fixZero(hours))
                    self.minutes = self.fixZero(minutes)
                    self.seconds = self.fixZero(sec - hours * 3600 - minutes * 60)
                    sec--
                }
            },

            getCoupon: function (val, $event) {

                //登录拦截
                if(!this.logined){
                    if(this.status===4){
                        if(os>2){
                            self.goLoginPage()
                        }else{
                            hook.login($event.currentTarget)
                        }
                    }else if(this.status===2){
                        $event.preventDefault()
                        return  false
                    }else if(this.status===6){
                        $event.preventDefault()
                        return  false
                    }
                }

                if(this.status!==4){
                    $event.preventDefault()
                    return false
                }
                // 请求数据
                var self = this,
                    status = self.status
                if (os !== 3) {
                    hook.getEvent('receiveAnniversaryCoupon' + '&isNeedRealName=0&args_couponAmount_1_integer_' + val, $($event.currentTarget))
                }
                else {
                    if (status == 4) {
                        base.getAPI({
                            url: self.urls.receive,
                            data: {
                                invokeMethod: 'receiveAnniversaryCoupon',
                                invokeParameters: 'args_couponAmount_1_integer_' + val
                            },

                            callback: self.hookCallback
                        })
                    }
                    else if (status == 2) {
                        return  false
                    }
                    else if (status == 6) {
                        return false
                    }
                }

            },

            hookCallback: function (data) {
                if (data.success) {
                    var result = data.result
                    this.init.coupon10 = result.coupon10
                    this.init.coupon30 = result.coupon30
                    this.init.coupon100 = result.coupon100
                    this.init.coupon200 = result.coupon200

                    base.xTips({
                        time: 2000,
                        content: '领取成功'
                    })
                } else {
                    var resultCodes = data.resultCodes[0],
                        errorMsg=resultCodes.msg

                    if(resultCodes.code=='93017'){
                        errorMsg='每日10:00~15:59开抢！'
                    }
                    base.xTips({
                        time: 2000,
                        content: errorMsg
                    })
                }
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            }
        }
    })
})