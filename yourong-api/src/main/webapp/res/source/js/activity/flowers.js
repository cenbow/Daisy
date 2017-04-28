/**
 * Created by hushuna on 2017/3/22.
 */
define(['base'], function (require, exports, module) {
    'use strict'
    /* 除掉移动端默认的点击图片出现全图浏览 */
    // $('img').on('click', function (e) {e.preventDefault()})

    var path = environment.globalPath,
        base = require('base'),
        currentTime = +environment.serverDate

    window.vm = new Vue({
        el: '#j-send-flowers',
        data: {
            // 后端接口地址
            urls: {
                receiveBag: path + '/activity/dynamicInvoke'
            },
            logined: logined,   //登录状态
            loginUrl: path + '/mstation/login?from=' + location.href,  //m站登录地址
            currentTime: currentTime,
            init: {},
            hasReceived: false,
            buttonText: '登录领取红包',
            shade: false,
            todayNo: 1,
            womenWord: '',
            womenName: '',
            womenWords: ['是我让融仔有了喜怒哀乐，你看我们是不是一样萌？',
                '在公司缺啥少啥就和我说，在有融要学会生活',
                '希望破60亿的时候，你可以接到我的来电',
                '我的工作就是每天和融粉谈恋爱，约么？',
                '为创意插上翅膀，让所想转为所见',
                '程序猿可不一定只有男生，也可以是头发浓密的女孩纸',
                '你来公司第一眼见到的是前台，第二眼可能就是我了',
                '可能你就是通过我认识有融的哦',
                '你看我，可不可以代表公司有颜有实力的形象',
                '专注传道受业解惑，你想被调教么？'],
            womenNames: ['设计部-格格', '行政部-雪芹', '客服部-华妹', '运营部-楚焱', '设计部-林子', '技术部-雨琴',
                '人事部-艳林', '推广部-玲玲', '品牌部-小鹿', '培训-洋洋'],
            imgNames: ['gege', 'xueqin', 'gaohuamei', 'chuyan', 'zhonglinzi', 'yuqin', 'yanlin', 'huanglin', 'zhanglu', 'zhouyang'],
            imgName: 'gege',
            hasSent: false,
            abnormalTips: false,
            isNotIng: false,
            tipsText: ''
        },
        created: function () {
            var self = this,
                result = receiveData.result
            self.init.startTime = result.startTime
            self.init.endTime = result.endTime
            self.init.receive = result.receive
            self.todayNo = Math.floor((self.currentTime - self.init.startTime) / 8.64E7)
            if (self.todayNo > 9) {
                self.todayNo = 9
            } else if (self.todayNo < 0) {
                self.todayNo = 0
            }
            if (self.logined) {
                if (self.init.receive) {
                    base.getAPI({
                        url: self.urls.receiveBag,
                        data: {
                            invokeMethod: 'receiveFlowers'
                        },
                        callback: function (data) {
                            if (data.success) {
                                self.hasReceived = true
                                self.buttonText = '领取成功'
                                self.init.receive = false
                            } else {
                                if (data.resultCodes[0].code = '10001') {
                                    self.abnormalTips = true
                                    setTimeout(function () {
                                        self.abnormalTips = false
                                    }, 2000)
                                }
                            }
                        }
                    })
                }
            }
        },
        computed: {
            womenWord: function () {
                return this.womenWords[this.todayNo]
            },
            womenName: function () {
                return this.womenNames[this.todayNo]
            },
            imgName: function () {
                return this.imgNames[this.todayNo]
            }
        },
        methods: {
            sendFlowers: function () {
                var self = this
                if (self.currentTime > self.init.startTime && self.currentTime < self.init.endTime) {
                    self.hasSent = true
                    if (self.logined) {
                        if (!self.init.receive) {
                            self.buttonText = '一天只能领取一次哦'
                        }
                    }
                } else {
                    self.isNotIng = true
                    if (self.currentTime < self.init.startTime) {
                        self.tipsText = '活动未开始'
                    } else {
                        self.tipsText = '活动已结束'
                    }
                    setTimeout(function () {
                        self.isNotIng = false
                    }, 2000)
                }
            },
            getBag: function () {
                var self = this
                if (self.currentTime < self.init.startDate) {
                    return false
                } else if (self.currentTime > self.init.startTime && self.currentTime < self.init.endTime) {
                    if (!self.logined) {
                        self.goLoginPage();
                        self.isReceived = true
                    }
                } else {
                    return false
                }
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            },
        }
    })


})
