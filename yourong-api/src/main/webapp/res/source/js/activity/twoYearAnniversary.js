/*global receiveData,environment,os*/
define(['base'], function (require, exports, module) {
    'use strict'
    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function (e) {e.preventDefault()})

    var path = environment.globalPath,
        base = require('base'),
        hook = new AppHook(os),
        logined = $('#j-twoYearAnniversary').data('logined')

    Vue.config.devtools = true

    window.vm = new Vue({
        el: '#j-twoYearAnniversary',
        data: {
            // 后端接口地址
            urls: {
                init: path + 'activity/twoYearAnniversary/init',
                receiveCoupon: path + '/activity/dynamicInvoke/',
                receiveReward: path + '/activity/dynamicInvoke/'
            },
            initData: {},
            showStatusDialog: false,           //普通弹出框
            showGiftPacksDialog: false,        //累投领礼包弹出框
            showSelectPopDialog: false,        //人气值选择弹出框
            showTenPrizesDialog: false,        //10点人气值奖品框
            showThirtyPrizesDialog: false,     //30点人气值奖品框

            selectTab: 1,                 //切换4个tab 春:1 夏:2 秋:3 冬:4

            goPageBtnMethod: '',
            giftBtnStatus: 0,
            cash88ActivityStatus: false,         //抢88元现金券活动状态
            isTheFirstDay: false,                //是否为活动第一天

            startGrabCash: false,
            startCountDown: false,
            hours: '',
            minutes: '',
            seconds: '',

            //提示框
            dialogTips: '',                    //提示信息
            targetUrls: '',                    //跳转路径
            dialogOther: '',                   //链接文字
            open: false,                       //规则弹窗

            rewardType: 0,                     //奖品类型：1.现金券 2.收益券 3.爱奇艺
            rewardValue: '',                   //奖品值
            clickCardPosition: 0,              //点击卡片的位置
            selectPopularity: 0,               //选择要消耗的人气值

            springCashList: [188,100,50,188,100,50,188,100,50],
            tenMinuteStatus: false,
            clickSpringCashIndex: 0,
            summerPopularityName: '',

            logined: os > 2 ? logined : hook.logined,
            loginUrl: path + '/mstation/login?from=' + location.href
        },
        created: function () {
            var self = this;

            if (receiveData.success) {
                var result = receiveData.result;

                self.initData = {
                    startTime: result.startTime || '',  //活动开始时间
                    endTime: result.endTime || '',      //活动结束时间
                    activityStatus: result.activityStatus || 2,  //活动状态

                    // 88元红包
                    totalCoupon88: result.totalCoupon88 || 0,
                    coupon88Remind: result.coupon88Remind || 0,
                    eightCouponStartTime: result.eightCouponStartTime || 0,

                    // 夏--累投领礼包
                    summerGift: {
                        popularity19: result.popularity19 || false,
                        popularity199: result.popularity199 || false,
                        popularity659: result.popularity659 || false,
                        popularity1119: result.popularity1119 || false,
                        popularity2016: result.popularity2016 || false,
                        iphone7: result.iphone7 || false
                    },

                    totalMyInvestAmount: result.totalMyInvestAmount || 0,  //我的累计投资总额
                    myNumber: result.myNumber || 0,  //我的领取次数
                    myPopularity: result.myPopularity ||0,  //我的人气值

                    // 秋--翻牌
                    position: result.position || 0,
                    rewardResult: result.rewardResult || ''

                };

                self.rewardType = self.getRewardType(result.rewardResult);

                // 判断168元抢红包活动的状态
                var curTime = +environment.serverDate,
                    startTime = self.initData.eightCouponStartTime;

                self.isTheFirstDay = moment(curTime).isSame(startTime, 'day');

                if (curTime - startTime >= 0) {
                    self.cash88ActivityStatus = true
                }

                console.log("大活动开始时间: "+new Date(self.initData.startTime))
                console.log("168元抢红包开始时间: "+new Date(self.initData.eightCouponStartTime))
                console.log("判断168元抢红包活动的状态: "+self.cash88ActivityStatus)
                console.log("是否为活动第一天: "+self.isTheFirstDay)
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
            // 抢88元红包是否置灰
            getCash88BtnStatus: function(){
                var self = this,
                    count = self.initData.coupon88Remind;

                if (self.cash88ActivityStatus) {
                    if(self.isTheFirstDay){
                        if (count>0) {
                            return false
                        }else {
                            return true
                        }
                    }else {
                        return true
                    }
                }else {
                    return true
                }

            },
            // 抢88元红包倒计时
            cash88TimeCount: function() {
                var self = this;
                var seconds = 0;
                var flag = false;
                var date = +environment.serverDate;
                var startDate = self.initData.eightCouponStartTime;
                var curTime = self.getSeconds(new Date(date).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
                var startTime = self.getSeconds(new Date(startDate).toJSON().split(/[TZ]/)[1]) + 3600 * 8;

                /*活动开始第一天*/
                if (!self.cash88ActivityStatus && self.isTheFirstDay ) {
                    if (curTime < startTime && startTime - curTime > 600) {
                        var extraTime = 10 * 60;
                        var extraSeconds = startTime - curTime - extraTime;
                        flag = false

                        var exTimer = setInterval(function () {
                            if (extraSeconds > 0) {
                                extraSeconds--
                            } else {
                                clearInterval(exTimer);
                                seconds = 600;
                                self.tenMinuteStatus = true;
                                self.countdown(parseInt(seconds))
                            }
                        }, 1000)

                    } else if (curTime < startTime && startTime - curTime <= 600) {
                        // 最后10分钟开始倒计时
                        seconds = startTime - curTime;
                        self.startCountDown = true;
                        flag = true;
                    }else if (curTime > startTime) {
                        self.startGrabCash = true;
                        self.startCountDown = false;
                        flag = false
                    }
                }else if(self.cash88ActivityStatus) {
                    self.startGrabCash = true;
                }

                if (seconds > 0) {
                    self.countdown(parseInt(seconds))
                }

                return flag

            }

        },
        methods: {
            andGoPage: function () {
                var self = this;
                var pageName = self.goPageBtnMethod;
                Android.ToActivity(pageName,null);
            },
            // tab切换
            goSelectTab: function (tabNum) {
                var body = $('body');
                var self = this;
                self.selectTab = tabNum;
                switch (tabNum) {
                    case 1 :
                        body.css('background-color','#cce54c');
                        break
                    case 2:
                        body.css('background-color','#aada8c');
                        break
                    case 3:
                        body.css('background-color','#ffda74');
                        break
                    case 4:
                        body.css('background-color','#ffbc6b');
                        break
                }
            },
            getSeconds: function (timeString) {
                var list = timeString.split(':')
                return list[0] * 3600 + list[1] * 60 + Math.ceil(list[2] * 1)
            },
            fixZero: function (num) {
                return +num < 10 ? '0' + num : num
            },
            countdown: function (seconds) {
                var sec = seconds - 1,
                    self = this

                count()

                var timer = setInterval(function () {

                    if (sec > 0) {
                        count()
                    } else {
                        clearInterval(timer);
                        self.startGrabCash = true;
                        self.startCountDown = false;
                        self.cash88ActivityStatus = true;
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
            openRuleDialog: function() {
                this.open = true;
                $.shade('show');
            },
            closeRuleDialog: function() {
                this.open = false;
                $.shade('hide');
            },
            // 抢88元红包按钮文案
            cash88Text: function(){
                var text = '';
                var count = this.initData.coupon88Remind;

                if (this.cash88ActivityStatus) {
                    if (this.isTheFirstDay) {
                        if (count < 1) {
                            text = '已抢完'
                        }else {
                            text = '立即领取'
                        }
                    }else {
                        text = '已抢完'
                    }
                }else {
                    text = '即将开抢'
                }

                return text
            },
            // 人气值收益券领取按钮文案
            giftPacksBtnText: function(val) {
                var text = '';
                var self = this;
                var popularityName = '';
                var status = self.initData.activityStatus;

                if('iphone7' === val) {
                    popularityName = 'iphone7';
                }else {
                    popularityName = 'popularity' + val;
                }

                if (status === 2) {
                    text = '领取';
                } else if (status === 4 || status === 6) {
                    if (self.initData.summerGift[popularityName]) {
                        text = '已领取'
                    } else {
                        text = '领取'
                    }
                }

                return text
            },
            // 翻牌  (当日已翻牌点击无效)
            drawCards: function (val, $event) {
                var self = this;

                if(!self.logined){
                    if (os > 2) {
                        self.goLoginPage();
                    } else {
                        hook.login($($event.currentTarget));
                    }
                }else {
                    if(self.initData.activityStatus === 4 ) {
                        if(self.initData.position && self.initData.position > 0){
                            self.dialogTips = '今日已超限，明天再来哦~';
                            self.targetUrls = '';
                            self.dialogOther = '';
                            self.activityDialog('showGiftPacksDialog','show');
                        }else {
                            self.clickCardPosition = val;
                            self.activityDialog('showSelectPopDialog','show');
                        }
                    }else if (self.initData.activityStatus === 2) {
                        self.dialogTips = '活动即将开始';
                        self.targetUrls = '';
                        self.dialogOther = '';
                        self.activityDialog('showGiftPacksDialog','show');
                    }else if (self.initData.activityStatus === 6) {
                        self.dialogTips = '活动已结束';
                        self.targetUrls = '';
                        self.dialogOther = '';
                        self.activityDialog('showGiftPacksDialog','show');
                    }
                }

            },
            // 打开奖品列表
            openPrizesModal: function(val) {
                var self = this;
                self.giftBtnStatus = val;
                if("10" == val){
                    self.activityDialog('showTenPrizesDialog','show');
                }else {
                    self.activityDialog('showThirtyPrizesDialog','show');
                }
            },
            // 选择消耗10/30点人气值
            selectPopValue: function(val) {
                this.selectPopularity = val;
            },
            //奖品类型: 1.现金券  2.收益券  3.爱奇艺
            getRewardType: function(result) {
                var self = this;
                var type = 0;

                if(result){
                    if (result.indexOf('For') === -1) {
                        type = 3;
                        self.rewardValue = 'iQiYi';
                    }else {
                        if (result.split('For')[0] === 'coupon') {
                            type = 1;
                        }else {
                            type = 2;
                        }
                        self.rewardValue = result.split('For')[1];
                    }
                }

                return type
            },
            // 抢88元红包按钮
            receive88Cash: function($event){
                //请求数据
                var self = this;
                var status = self.cash88ActivityStatus;
                var count = self.initData.coupon88Remind;

                if (status && self.isTheFirstDay) {
                    if (!self.logined) {
                        if (os > 2) {
                            self.goLoginPage();
                        } else {
                            hook.login($($event.currentTarget));
                        }
                    }else {
                        if (count > 0) {
                            if(os !== 3) {
                                hook.getEvent('receiveCouponAnniversary'
                                    +'&isNeedRealName=0&args_type_1_integer_1'
                                    +'&args_couponAmount_2_integer_88',$($event.currentTarget));
                                window.eventName = 'receiveCouponAnniversary&cash'
                            }else {
                                base.getAPI({
                                    url: self.urls.receiveCoupon,
                                    data: {
                                        invokeMethod: 'receiveCouponAnniversary',
                                        invokeParameters:'args_type_1_integer_1' + '&args_couponAmount_2_integer_88'
                                    },
                                    callback: function (data) {
                                        if(data.success) {
                                            self.dialogTips = '恭喜您获得一张168元现金券，快去使用吧~';
                                            self.targetUrls = path + '/products/m/list-all-all-1.html';
                                            self.dialogOther = '立即使用 >>';
                                            self.initData.coupon88Remind = data.result.coupon88Remind;
                                        }else {
                                            if (data.resultCodes[0].code == '91052') {
                                                self.dialogTips = '存钱罐可用余额≥2000元就可以抢红包啦~';
                                                self.targetUrls = path+'/memberBalance/rechargePage';
                                                self.dialogOther = '立即充值 >>';
                                            }else {
                                                self.dialogTips = data.resultCodes[0].msg;
                                                self.targetUrls = '';
                                                self.dialogOther = '';
                                            }
                                        }
                                        self.activityDialog('showGiftPacksDialog','show');
                                    }
                                })
                            }
                        }else {
                            return false
                        }
                    }
                }else {
                    return  false
                }

            },
            // 领取现金券
            receiveCashCoupon: function(val, $event) {
                var self = this;

                if(!self.logined){
                    if (os > 2) {
                        self.goLoginPage();
                    } else {
                        hook.login($($event.currentTarget));
                    }
                }else {
                    if (self.initData.activityStatus === 2) {
                        self.dialogTips = '活动即将开始';
                        self.activityDialog('showStatusDialog','show');
                    }else if (self.initData.activityStatus === 4) {
                        self.clickSpringCashIndex = val;
                        if (os !== 3) {
                            hook.getEvent('receiveCouponAnniversary'
                                + '&isNeedRealName=0&args_type_1_integer_1'
                                + '&args_couponAmount_2_integer_' + val, $($event.currentTarget))
                            window.eventName = 'receiveCouponAnniversary&cashCoupon'
                        }else {
                            base.getAPI({
                                url: self.urls.receiveCoupon,
                                data: {
                                    invokeMethod: 'receiveCouponAnniversary',
                                    invokeParameters:'args_type_1_integer_1' + '&args_couponAmount_2_integer_'+val
                                },
                                callback: function(data) {
                                    if(data.success){
                                        self.dialogTips = '恭喜您获得一张'+self.springCashList[val-1]+'元现金券，快去使用吧~';
                                        self.targetUrls = path+'/products/m/list-all-all-1.html';
                                        self.dialogOther = '立即使用 >>';
                                        self.initData.myNumber = self.initData.myNumber - 1;
                                    }else {
                                        self.dialogTips = '不满足领取条件，';
                                        self.targetUrls = path+'/products/m/list-all-all-1.html';
                                        self.dialogOther = '立即投资 >>';
                                    }
                                    self.activityDialog('showGiftPacksDialog','show');
                                }
                            })
                        }
                    }else if (self.initData.activityStatus === 6) {
                        self.dialogTips = '活动已结束';
                        self.activityDialog('showStatusDialog','show');
                    }
                }
            },
            // 领取礼包
            getGiftPacks: function(val, str, $event) {
                var self = this;
                var popularityName = '';

                if('iphone7' === str) {
                    popularityName = 'iphone7';
                }else {
                    popularityName = 'popularity' + str;
                }

                self.summerPopularityName = popularityName;

                if(!self.logined){
                    if (os > 2) {
                        self.goLoginPage();
                    } else {
                        hook.login($($event.currentTarget));
                    }
                }else {
                    if (!self.initData.summerGift[popularityName]) {
                        if (self.initData.activityStatus === 2) {
                            self.dialogTips = '活动即将开始';
                            self.activityDialog('showStatusDialog', 'show');
                        } else if (self.initData.activityStatus === 4) {
                            if (os !== 3) {
                                hook.getEvent('receiveCouponAnniversary'
                                    + '&isNeedRealName=0&args_type_1_integer_2'
                                    + '&args_couponAmount_2_integer_' + val, $($event.currentTarget))
                                window.eventName = 'receiveCouponAnniversary&gift'
                            }else {
                                base.getAPI({
                                    url: self.urls.receiveCoupon,
                                    data: {
                                        invokeMethod: 'receiveCouponAnniversary',
                                        invokeParameters:'args_type_1_integer_2' + '&args_couponAmount_2_integer_'+val
                                    },
                                    callback: function(data) {
                                        if(data.success){
                                            self.dialogTips = '恭喜您获得专属礼包，快去使用吧~';
                                            self.targetUrls = path+'/mCenter/reputationPage';
                                            self.dialogOther = '立即使用 >>';
                                            self.initData.summerGift[popularityName] = true;
                                        }else {
                                            self.dialogTips = '不满足领取条件，';
                                            self.targetUrls = path+'/products/m/list-all-all-1.html';
                                            self.dialogOther = '立即投资 >>';
                                        }
                                        self.activityDialog('showGiftPacksDialog','show');
                                    }
                                })
                            }
                        } else if (self.initData.activityStatus === 6) {
                            self.dialogTips = '活动已结束';
                            self.activityDialog('showStatusDialog', 'show');
                        }
                    } else {
                        return false
                    }
                }
            },
            // 确定翻牌
            goPrizeDraw: function($event) {
                var self = this;

                if (self.selectPopularity === 0) {
                    return false;
                }else {
                    if(os !== 3){
                        hook.getEvent('receiveRewardAnniversary'
                            + '&isNeedRealName=0&args_chip_1_integer_' + self.clickCardPosition
                            + '&args_popularValue_2_integer_' + self.selectPopularity, $($event.currentTarget))
                    }else {
                        base.getAPI({
                            url: self.urls.receiveReward,
                            data: {
                                invokeMethod: 'receiveRewardAnniversary',
                                invokeParameters: 'args_chip_1_integer_' + self.clickCardPosition + '&args_popularValue_2_integer_' + self.selectPopularity
                            },
                            callback: function(data) {
                                if(data.success){
                                    var result = data.result;
                                    self.initData.position = result.position;
                                    self.rewardType = self.getRewardType(result.rewardResult);
                                    self.initData.myPopularity = self.initData.myPopularity - self.selectPopularity
                                }else {
                                    self.dialogTips = '人气值不足，';
                                    self.targetUrls = path+'/yourong-api/mIndex';
                                    self.dialogOther = '立即赚人气值 >>';
                                    self.activityDialog('showGiftPacksDialog','show');
                                }
                                self.activityDialog('showSelectPopDialog','hide');
                            }
                        })
                    }
                }
            },
            // 活动弹出框
            activityDialog: function(name,status) {
                if("undefined" == typeof status || "show" === status) {
                    if (this.selectPopularity !== 0) {
                        this.selectPopularity = 0
                    }
                    this[name] = true;
                }else {
                    this[name] = false;
                }

                $.shade(status);
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            }
        }
    })

    // 显示、隐藏遮罩层
    $.extend({
        shade: function e(t) {
            var e = $(".u-shade");
            if ("undefined" == typeof t || "show" === t) if (e.length) e.show();
            else {
                var n = $("body"),
                    i = n.height(),
                    s = "<div class='u-shade' style='height:" + i + "px'></div>";
                n.append(s)
            } else "hide" === t && e.remove()
        }
    })

    // App数据处理
    window.hookCallback = function (data, eventName) {
        switch (eventName) {
            case 'receiveCouponAnniversary':
                var method = window.eventName.split('&')[1];
                if ('cash' === method) {
                    dealGetCashPack(data);
                }else if ('cashCoupon' === method) {
                    dealReceiveCashCoupon(data);
                }else if ('gift' === method) {
                    dealReceiveGiftPacks(data);
                }
                break
            case 'receiveRewardAnniversary':
                dealReceiveReward(data);
                break
        }

        // 抢88元红包回调处理
        function dealGetCashPack(data) {
            if(data.success) {
                vm.dialogTips = '恭喜您获得一张168元现金券，快去使用吧~';
                if(os==2) {
                    vm.targetUrls = 'yrw-skip://invokeMethod=projectList';
                }else if (os==1) {
                    vm.goPageBtnMethod = 'projectList';
                    vm.targetUrls = 'javascript:;';
                }
                vm.dialogOther = '立即使用 >>';
                vm.initData.coupon88Remind = data.result.coupon88Remind;
            }else {
                if (data.resultCodes[0].code == '91052') {
                    vm.dialogTips = '存钱罐可用余额≥2000元就可以抢红包啦~';
                    if(os==2) {
                        vm.targetUrls = 'yrw-skip://invokeMethod=recharge';
                    }else if (os==1) {
                        vm.goPageBtnMethod = 'recharge';
                        vm.targetUrls = 'javascript:;';
                    }
                    vm.dialogOther = '立即充值 >>';
                }else {
                    vm.dialogTips = data.resultCodes[0].msg;
                    vm.targetUrls = '';
                    vm.dialogOther = '';
                    vm.goPageBtnMethod = '';
                }
            }
            vm.activityDialog('showGiftPacksDialog','show');
        }
        // 领取现金券回调处理
        function dealReceiveCashCoupon(data) {
            if(data.success){
                vm.dialogTips = '恭喜您获得一张'+vm.springCashList[vm.clickSpringCashIndex-1]+'元现金券，快去使用吧~';
                if(os==2) {
                    vm.targetUrls = 'yrw-skip://invokeMethod=projectList';
                }else if (os==1) {
                    vm.goPageBtnMethod = 'projectList';
                    vm.targetUrls = 'javascript:;';
                }
                vm.dialogOther = '立即使用 >>';
                vm.initData.myNumber = vm.initData.myNumber - 1;
            }else {
                vm.dialogTips = '不满足领取条件，';
                if(os==2) {
                    vm.targetUrls = 'yrw-skip://invokeMethod=projectList';
                }else if (os==1) {
                    vm.goPageBtnMethod = 'projectList';
                    vm.targetUrls = 'javascript:;';
                }
                vm.dialogOther = '立即投资 >>';
            }
            vm.activityDialog('showGiftPacksDialog','show');
        }
        // 领取礼包回调处理
        function dealReceiveGiftPacks(data) {
            if(data.success){
                vm.dialogTips = '恭喜您获得专属礼包，快去使用吧~';
                if(os==2) {
                    vm.goPageBtnMethod = '';
                    vm.targetUrls = 'yrw-skip://invokeMethod=discount';
                }else if (os==1) {
                    vm.goPageBtnMethod = 'discount';
                    vm.targetUrls = '';
                }
                vm.dialogOther = '立即使用 >>';
                vm.initData.summerGift[vm.summerPopularityName] = true;
            }else {
                vm.dialogTips = '不满足领取条件，';
                if(os==2) {
                    vm.goPageBtnMethod = '';
                    vm.targetUrls = 'yrw-skip://invokeMethod=projectList';
                }else if (os==1) {
                    vm.goPageBtnMethod = 'projectList';
                    vm.targetUrls = '';
                }
                vm.dialogOther = '立即投资 >>';
            }
            vm.activityDialog('showGiftPacksDialog','show');
        }
        // 确定翻牌回调处理
        function dealReceiveReward(data) {
            if(data.success){
                var result = data.result;
                vm.initData.position = result.position;
                vm.rewardType = vm.getRewardType(result.rewardResult);
                vm.initData.myPopularity = vm.initData.myPopularity - vm.selectPopularity
            }else {
                vm.dialogTips = '人气值不足，';
                if(os==2) {
                    vm.goPageBtnMethod = '';
                    vm.targetUrls = 'yrw-skip://invokeMethod=activityList';
                }else if (os==1) {
                    vm.goPageBtnMethod = 'activityList';
                    vm.targetUrls = '';
                }
                vm.dialogOther = '立即赚人气值 >>';
                vm.activityDialog('showGiftPacksDialog','show');
            }
            vm.activityDialog('showSelectPopDialog','hide');
        }
    }
})