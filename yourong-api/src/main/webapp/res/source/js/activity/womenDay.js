define(['base'], function (require, exports, module) {
    'use strict'
    /* 除掉移动端默认的点击图片出现全图浏览 */
    // $('img').on('click', function (e) {e.preventDefault()})

    var path = environment.globalPath,
        currentTime = +environment.serverDate,
        base = require('base'),
        hook = new AppHook(os)

    window.vm = new Vue({
        el: '#j-womenDay',
        data: {
            // 后端接口地址
            urls: {
                receiveBag: path + '/activity/dynamicInvoke'
            },
            pageNo: 1,
            acquireBag: '',
            abnormalTips: false,
            activityRegular: false,
            currentTime: currentTime,
            init: {},                 //初始化数据
            logined: os > 2 ? logined : hook.logined,    //登录状态
            loginUrl: path + '/mstation/login?from=' + location.href,  //m站登录地址
            cantGetText: ['', '活动即将开始', '活动已结束', '网络拥挤，过会再来~',
                '女神，记得3月8号来领取哦', '女神，你已错过领取哦', '快带女神来领吧~'],
            tipsNo: 0,
            buttonText: ['领取我的专属礼包', '已领取', '活动已结束'],
            ableGetStart: '',  //用来处理38日子比对,
            ableGetEnd: '',
        },
        created: function () {
            var self = this
            var result = receiveData
            window.hookCallback = this.hookCallback
            self.init = {
                startDate: result.startDate,
                endDate: result.endDate,
                bag: result.bag,
                investment: result.investment || 0,
                sex: result.sex,
                womensDate: result.womensDate
            };
            self.init.womensDate = self.init.womensDate.replace(/\-/g, '/')
            self.ableGetStart = new Date(self.init.womensDate + ' 00:00:00').getTime()
            self.ableGetEnd = new Date(self.init.womensDate + ' 23:59:59').getTime()
            window.onhashchange = function () {
                if (location.hash == '') {
                    window.scrollTo(0, 0)
                    self.activityRegular = false
                }
            }
        },
        computed: {
            loginUrl: function () {
                var url = path + '/mstation/login?from=' + location.href, nullUrl = 'javascript:void(0)'

                if (this.logined) {
                    url = nullUrl
                } else {
                    if (os === 2) {
                        url = 'yrw://invokeMethod=loginIn'
                    } else if (os === 1) {
                        url = nullUrl
                    }
                }
                return url
            },
            buttonTextNo: function () {
                var self = this

                if (self.currentTime > self.init.endDate) {
                    return 2
                } else {
                    if (!self.init.bag && self.init.sex === 0) {
                        return 1
                    } else {
                        return 0
                    }

                }
            },

        },
        methods: {
            abnormalTip: function (num) {
                var self = this
                self.tipsNo = num
                self.abnormalTips = true
                setTimeout(function () {
                    self.abnormalTips = false
                }, 2000)
            },
            getBag: function ($event) {
                var self = this
                self.abnormalTips = true
                if (self.currentTime > self.init.startDate && self.currentTime < self.init.endDate) {
                    if (!self.logined && os > 2) {
                            self.goLoginPage();
                    } else {
                        if (!self.init.bag && self.init.sex === 0) {
                            return false
                        } else {
                            if (self.currentTime > self.ableGetStart && self.currentTime < self.ableGetEnd) {
                                if (os !== 3) {
                                    hook.getEvent('womensDayBag' + '&isNeedRealName=0', $($event.currentTarget));
                                } else {
                                    base.getAPI({
                                        url: self.urls.receiveBag,
                                        data: {
                                            invokeMethod: 'womensDayBag'
                                        },
                                        callback: self.hookCallback
                                    })
                                }
                            } else if (self.currentTime < self.ableGetStart) {
                                if (self.init.sex === 0) {
                                    self.abnormalTip(4)
                                } else if (self.init.sex === 1) {
                                    self.abnormalTip(6)
                                } else {
                                    self.acquireBag = 'goShow'
                                }
                            } else {
                                self.abnormalTip(5)
                            }
                        }
                    }
                } else if (self.currentTime < self.init.startDate) {
                    self.abnormalTip(1)
                } else {
                    return false
                }
            },
            // 按钮状态转换
            closeTips: function () {
                var self = this
                self.acquireBag = ''
                self.init.bag = false
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            },
            hookCallback: function (data) {
                var self = this
                self.abnormalTips = false
                if (data.success) {
                    self.acquireBag = 'succeed'
                } else {
                    if (data.resultCodes[0].code == 94012) {
                        self.abnormalTip(6)
                    } else if (data.resultCodes[0].code == 94013) {
                        self.acquireBag = 'goShow'
                    } else {
                        self.abnormalTip(3)
                    }
                }
            }
        }
    })
})