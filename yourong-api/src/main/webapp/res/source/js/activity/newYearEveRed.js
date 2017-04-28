/*global receiveData,environment,os*/
define(['base'], function (require, exports, module) {
    'use strict'
    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function (e) {e.preventDefault()})

    var path = environment.globalPath,
        base = require('base'),
        hook = new AppHook(os),
        logined = $('#j-newYearEveRed').data('logined')

    Vue.config.devtools = true

    window.vm = new Vue({
        el: '#j-newYearEveRed',
        data: {
            // 后端接口地址
            urls: {
                grabRedBag: path + '/activity/dynamicInvoke/'
            },
            init: {},                 //初始化数据
            isShowDialog: false,      //是否显示弹框
            activityStatus: 2,        //抢压岁包状态
            isSuccess: false,         //是否抢到压岁包
            countDownText: '',        //倒计时文本
            isShowCountDown: false,   //是否显示倒计时按钮
            surplusCount: 0,          //剩余红包数量
            startHour: 0,             //红包开始时间
            startHourBtnText: '',     //开抢按钮时间文本
            noRespone: true,         //是否发起请求
            logined: os > 2 ? logined : hook.logined,    //登录状态
            loginUrl: path + '/mstation/login?from=' + location.href  //m站登录地址
        },
        created: function () {
            var self = this;
            window.hookCallback = this.hookCallback

            var result = receiveData;

            self.init = {
                startTime: result.startTime,                 /*活动开始时间*/
                endTime: result.endTime,                     /*活动结束时间*/
                popularity: result.popularity,
                grabed: result.grabed || false,              /*是否已经抢过压岁包*/
                grabCount: result.grabCount || 0,            /*压岁包总数量*/
                grabedCount: result.grabedCount || 0,        /*已抢红包数量*/
                grabResult: result.grabResult || []          /*幸运名单*/
            };

            self.surplusCount = self.init.grabCount - self.init.grabedCount

            //幸运名单小于5条时用虚位以待填充
            self.init.grabResult = self.splitLuckyMemberList(self.init.grabResult)

            var curTime = +environment.serverDate
            var seconds = 0

            //判断抢压岁包活动状态
            if (curTime - self.init.startTime >= 0 && self.init.endTime - curTime >0) {
                if (self.surplusCount>0) {
                    self.activityStatus = 4
                }else {
                    self.activityStatus = 6
                }

            }else if (curTime - self.init.endTime >= 0) {
                self.activityStatus = 6
            }

            // 开抢前10分钟显示倒计时按钮并倒计时；大于开抢10分钟在控制台倒计时,直到开抢10分钟再显示倒计时按钮
            if (self.init.startTime - curTime > 600000) {
                seconds = Math.ceil((self.init.startTime - 600000 - curTime)/1E3)
                self.hiddenCountDown(parseInt(seconds))
            }else if (self.init.startTime - curTime <= 600000 && (self.init.startTime - curTime) > 0) {
                seconds = Math.ceil((self.init.startTime - curTime)/1E3)
                self.isShowCountDown = true
                self.countDown(parseInt(seconds))
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
            // 将24小时制时间转换为12小时制
            changeHourFormat: function () {
                var self = this
                var hour = new Date(self.init.startTime).getHours()
                var startHourText = ''
                if (hour < 13) {
                    startHourText = '早'+ hour
                }else if (hour < 19) {
                    startHourText = '下午'+ (hour - 12)
                }else {
                    startHourText = '晚'+ (hour - 12)
                }
                self.startHourBtnText = startHourText
                return startHourText
            },
            //抢压岁包按钮文本
            grabBagBtnText: function () {
                var self = this
                var text = ''
                switch (self.activityStatus) {
                    case 2:
                        text = '除夕'+self.startHourBtnText+'点整，准时开抢'
                        break
                    case 4:
                        text = '速度！抢 →'
                        break
                    case 6:
                        text = '下手太慢，抢完啦'
                        break
                    default :
                        text = '除夕'+self.startHourBtnText+'点整，准时开抢'
                        break
                }
                return text
            }
        },
        methods: {
            // 幸运名单
            splitLuckyMemberList: function (arr) {
                if(arr.length < 5){
                    var arrLength = arr.length
                    for (var i = 0; i < 5 - arrLength; i++) {
                        arr.push({
                            mobileStr: '',
                            usernames : "虚位以待",
                            avatars : ''
                        })
                    }
                }
                return arr
            },
            fixZero: function (num) {
                return +num < 10 ? '0' + num : num
            },
            // 活动开抢10分钟以前在控制台倒计时
            hiddenCountDown: function (seconds) {
                var sec = seconds - 1,
                    self = this

                var timer = setInterval(function () {
                    if (sec > 0) {
                        sec--
                    } else {
                        clearInterval(timer)
                        self.isShowCountDown = true
                        self.countDown(600)
                    }
                }, 1000)
            },
            // 抢压岁包按钮倒计时
            countDown: function (time) {
                var sec = time - 1,
                    self = this

                getFromatTime()

                var timer = setInterval(function () {
                    if (time > 0) {
                        getFromatTime()
                    } else {
                        clearInterval(timer)
                        self.isShowCountDown = false
                        self.activityStatus = 4
                    }
                }, 1000)

                function getFromatTime() {
                    var text = '',
                        hour = self.fixZero(parseInt(time / 3600)),
                        min = self.fixZero(parseInt(time % 3600 / 60)),
                        sec = self.fixZero(time % 60);

                        text = hour + ':' + min + ':' + sec

                    self.countDownText = text
                    time--
                }
            },
            // 抢压岁包
            grabBag: function ($event) {

                var self = this
                if (!self.logined) {
                    if (os > 2) {
                        self.goLoginPage();
                    } else {
                        hook.login($($event.currentTarget));
                    }
                }else {
                    if (self.surplusCount > 0 && self.init.grabed && self.activityStatus === 4) {
                        if(os !== 3) {

                            hook.getEvent('grab'+'&isNeedRealName=0',$($event.currentTarget));
                            if(os === 1){
                                setTimeout(function(){
                                    
                                    if(self.noRespone){
                                        base.cover.show()
                                        self.isSuccess = false
                                        self.isShowDialog = true
                                        self.noRespone=true
                                    }
                                },3000)
                            }
                        }else {
                            base.getAPI({
                                url: self.urls.grabRedBag,
                                data: {
                                    invokeMethod: 'grab'
                                },
                                callback: self.hookCallback
                            })
                        }
                    }else {
                        return false
                    }
                }
            },
            hookCallback: function (data) {
                var self = this
                self.noRespone = false

                if(os === 1){
                    setTimeout(function(){

                        if(!data){
                            base.cover.show()
                            self.isSuccess = false
                            self.isShowDialog = true
                            self.noRespone=true
                        }
                    },3000)
                }

                if(data.success) {
                    base.cover.show()
                    self.isSuccess = true
                    self.isShowDialog = true
                }else {
                    var code = data.resultCodes?data.resultCodes[0].code:0
                    if (code == '94010') {
                        base.xTips({
                            time: 3000,
                            content: "只能抢一次哦"
                        })
                    }else if (code == '94011') {
                        base.xTips({
                            time: 3000,
                            content: "下手太慢，抢完啦"
                        })
                    }else {
                        base.cover.show()
                        self.isSuccess = false
                        self.isShowDialog = true
                    }
                }
            },
            //关闭弹出框
            closeDialog: function (index) {
                this.isShowDialog = false
                base.cover.hide()
                if (index === 1) {
                    location.reload();
                }
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            }
        }
    })

    //列表滚动
    $.fn.scrollUserList = function (config) {
        this.each(function () {
            var _this = $(this),
                scrollSize = _this.find("li").length;
            if (scrollSize > config.size) {
                setInterval(function () {
                    var scrollItems = _this.find("li:visible");
                    _this.animate({marginTop: config.height}, 700, function () {
                        scrollItems.eq(0).appendTo(_this);
                        _this.css("margin-top", 0);
                    });
                }, 3000);
            }
        })
    };

    $('#j-luckyMemberList').scrollUserList({
        size: 5,
        height: -55,
        length: 1
    })
})