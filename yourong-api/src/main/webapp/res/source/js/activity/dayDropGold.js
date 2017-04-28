define(['base'], function (require, exports, module) {
    'use strict'
    /* 除掉移动端默认的点击图片出现全图浏览 */
// $('img').on('click', function (e) {e.preventDefault()})


    var path = environment.globalPath,
        currentTime = +environment.serverDate,
        base = require('base'),
        hook = new AppHook(os)

    Vue.config.devtools = true

    window.vm = new Vue({
        el: '#j-dayDropGold',
        data: {
            // 后端接口地址
            urls: {
                init: path + '/activity/dayDropGold/index',
                receiveCoupon: path + '/activity/dynamicInvoke'
            },
            logined: os > 2 ? logined : hook.logined,
            loginUrl: path + '/mstation/login?from=' + location.href,
            initData: {},
            selectTab: 1,
            gettrue: false,
            getfalse: false,
            door: false,
            tipsList: ['','活动即将开始','网络拥挤，过会再来~'],
            rewardInfoList: ['黄金10g+人气值588点','黄金10g+人气值188点','黄金10g','人气值588点','人气值388点',
                '人气值288点','人气值188点','人气值188点','人气值88点','人气值88点'],

            //提示框
            dialogTips: '',                    //提示信息
            isClick: true,
            rule: false,
            tipsIndex: 0,
            isShowTips: false
        },
        created: function () {
            var self = this;
            self.initData = {
                status: receiveData.result.status,
                newDate: +environment.serverDate,
                startTime: receiveData.result.startTime,
                endTime: receiveData.result.endTime,
                investFirstTen: receiveData.result.investFirstTen || [],  //英雄榜
                myInvestAmount: receiveData.result.myInvestAmount || 0,  //今日投资额
                myTotalInvestAmount: receiveData.result.myTotalInvestAmount || 0,  //累计投资额
                totalRed: receiveData.result.totalRed || 0,  //红包数量
            };
            //是否为活动最后一天
            self.isLastDay = self.testLastDay();
            //夺金英雄榜列表小于10条时用虚位以待填充
            self.initData.investFirstTen = self.fillLuckyMemberList(self.initData.investFirstTen)
            //红包被抢完可能出现-1，归为0
            if (self.initData.totalRed < 0){
                self.initData.totalRed = 0
            }

            window.hookCallback = function (data,eventName) {
                self.Callback(data)
            }
        },
        computed:{
            //红包按钮文案
            redBagText: function () {
                var self = this
                var text = ''

                if (2===self.initData.status) {
                    text = '领取'
                }else if(4===self.initData.status) {
                    text = '领取'
                    if (0===self.initData.totalRed) {
                        text = '明天早点来'
                        if(self.isLastDay){
                            text = '今日已领完'
                        }
                    }
                }else {
                    text = '已结束'
                }

                return text
            }
        },
        methods:{
            // 非模态提示框
            openTipsFrame: function(num) {
                var self = this
                self.tipsIndex = num
                self.isShowTips = true
                setTimeout(function(){
                    self.isShowTips = false
                },2000)
            },
            // goSelectTab: function (tabNum) {
            //     var body = $('body');
            //     var icon = $('.m-nav-icon1');
            //     var self = this;
            //     self.selectTab = tabNum;
            // },
            //格式化时间
            formatTime: function(date, format){
                date = new Date(date);

                var map = {
                    "M": date.getMonth() + 1, //月份
                    "d": date.getDate(), //日
                    "h": date.getHours(), //小时
                    "m": date.getMinutes(), //分
                    "s": date.getSeconds(), //秒
                    "q": Math.floor((date.getMonth() + 3) / 3), //季度
                    "S": date.getMilliseconds() //毫秒
                };
                format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
                    var v = map[t];
                    if (v !== undefined) {
                        if (all.length > 1) {
                            v = '0' + v;
                            v = v.substr(v.length - 2);
                        }
                        return v;
                    }
                    else if (t === 'y') {
                        return (date.getFullYear() + '').substr(4 - all.length);
                    }
                    return all;
                });
                return format;
            },
            // 用虚位以待填充
            fillLuckyMemberList: function (arr) {
                if(arr.length > 0 && arr.length < 10){
                    var arrLength = arr.length
                    for (var i = 0; i < 10 - arrLength; i++) {
                        arr.push({
                            avatars : '',
                            username : "虚位以待",
                            rewardInfo: '',
                            totalInvestAmount: ''
                        })
                    }
                }
                return arr
            },
            // 判断是否为活动最后一天
            testLastDay: function () {
                var self = this
                var flag = false
                var current = +environment.serverDate  //当前时间（时间戳）
                var endTime = self.initData.endTime  //活动结束时间（时间戳）
                var endDay = self.formatTime(endTime,'yyyy/MM/dd')  //活动结束日期
                var endTimeStart = new Date(endDay + ' 00:00:00').getTime() //最后一天的凌晨00点（时间戳）

                if (current >= endTimeStart && current < endTime) {
                    flag =  true
                }

                return flag
            },
            close: function () {
                var self = this;
                self.door = false;
                self.getfalse = false;
                self.gettrue = false;
                self.rule = false
            },
            openrule: function () {
                var self = this;
                self.door = true;
                self.rule = true
            },
            getbag: function (val,$event) {
                var self = this;
                if (self.initData.newDate < self.initData.startTime) {
                    self.openTipsFrame(1)
                } else if (self.initData.newDate > self.initData.endTime) {
                    return false;
                } else {
                    if (self.initData.totalRed > 0){

                        if (!self.isClick) {
                            return false
                        }
                        self.isClick = false

                        if (!self.logined && os > 2) {
                                self.goLoginPage();
                                self.isClick = true;
                        } else {
                            if (os !== 3) {
                                hook.getEvent('receiveCouponGold'
                                  + '&isNeedRealName=0'
                                  + '&args_couponAmount_1_string_' + val, $($event.currentTarget))
                                // window.eventName = 'newYearLuckyMoney&money'
                                self.isClick = true;
                            }else {
                                base.getAPI({
                                    url: self.urls.receiveCoupon,
                                    data: {
                                        invokeMethod: 'receiveCouponGold',
                                        invokeParameters: 'args_couponAmount_1_string_' + val
                                    },
                                    callback: function (data) {
                                        self.Callback(data)
                                    }
                                })
                            }
                        }
                    } else {
                        return false;
                    }

                }
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl;
                return false
            },
            Callback: function (data) {
                var self = this;
                if (data.success) {
                    self.gettrue = true;
                    self.door = true;
                    self.dialogTips = data.result.templateIdName + '元现金券已到手';
                    self.initData.totalRed = data.result.totalRed;
                } else {
                    if (data.resultCodes[0].code == 94015){
                        self.getfalse = true;
                        self.door = true;
                        self.dialogTips = '单笔投资≥2000才可以领取'
                    }else if (data.resultCodes[0].code == 91322){
                        self.getfalse = true;
                        self.door = true;
                        if (self.isLastDay){
                            self.dialogTips = '下手太晚，今天已经没啦'
                        }else {
                            self.dialogTips = '下手太晚，明天早点来哦~'
                        }
                    }else {
                        self.openTipsFrame(2)
                    }
                }
                self.isClick = true;
            }
        }
    })

    var btn = $(".m-nav-btn");

    var rightSlideActive = function rightSlideActive($index) {
        btn.removeClass('active').eq($index).addClass('active');
        $(".m-nav-btn").removeClass('active').eq($index).addClass('active');
    }

    /*悬浮导航点击*/
    $(".m-nav-btn").on('click', function () {
        var $index = $(this).index();
        $("html,body").animate({
            scrollTop: $('.m-fu-title').eq($index).offset().top
        }, 500, function () {
            rightSlideActive($index);
            // updateNavItem();
        });
    })

    var updateNavItem = function updateNavItem() {
        //获取滚动条的滑动距离
        var top = $(document).scrollTop();          //定义变量，获取滚动条的高度
        var menu = $("#j-dayDropGold");            //定义变量，抓取#menu
        var items = menu.find(".m-fu-title");          //定义变量，查找.item
        var curId = "";                              //定义变量，当前所在的楼层item #id

        items.each(function (index) {
            var item = $(this);
            var itemTop = item.offset().top - 10;        //定义变量，获取当前的top偏移量
            $(this).attr("data-id", index);
            if (top >= itemTop) {
                curId = item.attr("data-id");
            } else {
                return false;
            }
        });

        //根据取得的id设置相应属性
        if (curId && !btn.eq(curId).hasClass("active")) {
            rightSlideActive(curId);
        }
    }

    $(window).scroll(function () {
        updateNavItem();
    });
})
