define(['base'], function (require, exports, module) {
    'use strict'
    /* 除掉移动端默认的点击图片出现全图浏览 */
    // $('img').on('click', function (e) {
    //     e.preventDefault()
    // })

    if (debug) {
        Vue.config.devtools = true
    }

    var path = environment.globalPath,
        base = require('base'),
        hook = new AppHook(os)

    var vm = new Vue({
        el: '#j-fiveBillion',
        data: {
            selectBagIndex: 0,

            coverShow: false,          //黑色蒙层
            whiteCoverShow: false,     //透明蒙层

            tipsShow: false,      //是否显示非模态提示框
            tipsType: 0,          //提示类型
            tipsTextList: [
                '客官别急，活动马上就要开始啦~',
                '抱歉，您来晚了，多多关注其他活动哦~',
                '网络拥挤，过会再来~',
                '活动期间只能领取一个哦~'
            ],

            lotteryType: 1,

            luckBothGiftList: [     //福禄双全奖品列表  --应用于获奖弹框
                {rewardIndex: 0,rewardValue: '158元', rewardText: '158元现金券'},
                {rewardIndex: 1,rewardValue:'16%',rewardText:'16%收益券'},
                {rewardIndex: 2,rewardValue:'58',rewardText:'58点人气值'},
                {rewardIndex: 3,rewardValue:'10%',rewardText:'10%收益券'},
                {rewardIndex: 4,rewardValue: '80元', rewardText: '80元现金券'},
                {rewardIndex: 5,rewardValue:'2%',rewardText:'2%收益券'},
                {rewardIndex: 6,rewardValue:'20元',rewardText:'20元现金券'},
                {rewardIndex: 7,rewardValue:'kindle',rewardText:'kindle入门版一台'}
            ],
            luckMonkeyList: [      //福禄天齐奖品列表  --应用于获奖弹框
                {rewardIndex: 0,rewardValue:'iPhone7 128G',rewardText:'iPhone7（128G）手机一部'},
                {rewardIndex: 1,rewardValue: '1328元', rewardText: '1328元现金券'},
                {rewardIndex: 2,rewardValue:'DW手表',rewardText:'DW手表一只'},
                {rewardIndex: 3,rewardValue:'588元',rewardText:'588元现金券'},
                {rewardIndex: 4,rewardValue:'4%',rewardText:'4%收益券'},
                {rewardIndex: 5,rewardValue:'200元京东购物卡',rewardText:'200元京东购物卡一张'},
                {rewardIndex: 6,rewardValue:'3%',rewardText:'3%收益券'},
                {rewardIndex: 7,rewardValue:'318',rewardText:'318点人气值'}
            ],
            myLotteryObj: {},
            pages:{
                pageNo: 0,
                totalPageCount: 0
            },
            isLoad: false,
            lotteryRecord: [],

            gamePage: 1,        //显示哪个游戏的标志
            clickTime: 0,      //点击抽奖时间
            lotteryTime: 0,     //抽奖时间
            init: {},            //初始化接口数据
            urls: {
                getLuck: path + '/activity/dynamicInvoke',        //领取福袋接口
                lottery: path + '/activity/dynamicInvoke',        //抽奖接口
                lotteryRecord: path + '/activity/dynamicInvoke'   //中奖纪录分页接口
            },
            sign: '',//规则弹框（除了领取福袋、抽奖的提示弹框）种类太多加个标志
            cannotGet: ['客官别急，活动马上就要开始啦~',
                '抱歉，您来晚了，多多关注其他活动哦~',
                '网络拥挤，过会再来~',
                '活动期间只能领取一个哦~'],
            logined: os > 2 ? logined : hook.logined,    //登录状态
            loginUrl: path + '/mstation/login?from=' + location.href  //m站登录地址
        },
        created: function () {
            var self = this

            window.onhashchange = function () {
                if (location.hash == '') {
                    window.scrollTo(0, 0)
                    self.sign = ''
                }
            }

            if (receiveData.success) {
                var result = receiveData.result;
                self.init = {
                    startTime: result.startTime, /*活动开始时间*/
                    endTime: result.endTime, /*活动结束时间*/
                    status: result.status,   /*活动状态*/
                    reward: result.reward,   /*领取的福袋类型*/
                    number: result.number,   /*天齐每次抽奖需消耗的抽奖券数量*/
                    myInvestAmount: result.myInvestAmount, /*我的累计投资额*/
                    doubleRecord: result.luckBothRecord || [], /*双全的获奖名单*/
                    monkeyRecord: result.luckMonkeyRecord || [], /*天齐的获奖名单*/
                    countLuckBoth: result.countLuckBoth, /*抽奖券数量&&双全抽奖次数*/
                    countLuckMonkey: result.countLuckMonkey, /*天齐抽奖次数*/
                    // doubleHistory: result.myListLuckBoth || [], /*双全我的中奖纪录*/
                    monkeyHistory: result.myListLuckMonkey || [], /*天齐我的中奖纪录*/
                };

                //福禄双全与福禄双齐中奖列表小于8条时用虚位以待填充
                self.init.doubleRecord = self.fillLuckyMemberList(self.init.doubleRecord)
                self.init.monkeyRecord = self.fillLuckyMemberList(self.init.monkeyRecord)
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
            }
        },
        methods: {
            // 非模态提示框
            openTipsFrame: function(num) {
                var self = this
                self.tipsType = num
                self.tipsShow = true
                self.whiteCoverShow = true
                setTimeout(function(){
                    self.tipsShow = false
                    self.whiteCoverShow = false
                },2000)
            },
            // 底部导航切换
            switchPage: function (val) {
                var self = this
                self.gamePage = val
                if (val==2) {
                    self.lotteryType = 1
                }else if(val==3) {
                    self.lotteryType = 2
                }
            },
            // 用虚位以待补充列表
            fillLuckyMemberList: function (arr) {
                if(arr.length < 5){
                    var arrLength = arr.length
                    for (var i = 0; i < 5 - arrLength; i++) {
                        arr.push({
                            happenTime: '',
                            usernames : "虚位以待",
                            avatars : ''
                        })
                    }
                }
                return arr
            },
            // 打开弹框
            openDialog: function (type) {
                this.sign = type
                this.coverShow = true
            },
            // 关闭弹框
            closeDialog: function (callback) {
                this.sign = ''
                this.coverShow = false
                if(callback) {
                    window.location.reload()
                }
            },
            // 中奖纪录弹框增加登陆判断
            awardRecord: function (index, $event) {
                var self = this
                if (!self.logined) {
                    if (os > 2) {
                        self.goLoginPage();
                    } else {
                        hook.login($($event.currentTarget));
                    }
                } else {
                    window.location.hash = '#bothRecord'
                    self.sign = index
                    self.lotteryRecord = []
                    self.pages.pageNo = 0
                    self.loadMoreData($event)
                }
            },
            // 领取福袋
            getLuckyBag: function (index, $event) {
                var self = this
                if(self.init.status === 2) {
                    self.openTipsFrame(0)
                }else if(self.init.status === 4) {
                    if(!self.logined){
                        if (os > 2) {
                            self.goLoginPage();
                        } else {
                            hook.login($($event.currentTarget));
                        }
                    }else {
                        if(self.init.reward === index){
                            return false
                        }else if(self.init.reward && self.init.reward !== index){
                            self.openTipsFrame(3)
                        }else {
                            self.selectBagIndex = index
                            self.openDialog('getBagWin')
                        }
                    }
                }else if(self.init.status === 6) {
                    self.openTipsFrame(1)
                }
            },
            // 确定领取福袋
            sureGetLuckyBag: function ($event) {
                var self = this
                self.closeDialog()

                if (os !== 3) {
                    hook.getEvent('receiveLuckBag'
                        + '&isNeedRealName=0&args_couponAmount_1_string_'+self.selectBagIndex, $($event.currentTarget))
                }else {
                    base.getAPI({
                        url: self.urls.getLuck,
                        data: {
                            invokeMethod: 'receiveLuckBag',
                            invokeParameters: 'args_couponAmount_1_string_'+self.selectBagIndex
                        },
                        callback: function(data) {
                            if(data.success){
                                self.init.reward = data.result.reward
                                self.openDialog('luckyBagWin')
                            }else {
                                self.openTipsFrame(3)
                            }
                        }
                    })
                }
            },
            // 两次点击时间差短不允许请求
            ableTolottery: function (num, $event) {
                var self = this
                var lotteryNumber = 0

                if(self.init.status === 2) {
                    self.openTipsFrame(0)
                }else if(self.init.status === 4) {
                    if(!self.logined){
                        if (os > 2) {
                            self.goLoginPage();
                        } else {
                            hook.login($($event.currentTarget));
                        }
                    }else {
                        if(num===1){
                            lotteryNumber = self.init.countLuckBoth
                        }else {
                            lotteryNumber = self.init.countLuckMonkey
                        }

                        if (lotteryNumber>0) {
                            self.clickTime = new Date().getTime()
                            if (self.clickTime - self.lotteryTime > 3000) {
                                self.lotteryType = num
                                self.openDialog('selectLotteryType')
                            }
                        }else {
                            self.openDialog('noLotteryCount')
                        }
                    }
                }else if(self.init.status === 6) {
                    self.openTipsFrame(1)
                }
            },
            lottery: function (e, $event) {
                var self = this
                var stopStep = 1;    //表示最终奖品位置
                var runT = null;     //转动方法
                var step = -1        //计算转动的步数，控制转速和停止
                var during = 2;      //转速

                self.lotteryTime = new Date().getTime()
                self.closeDialog()
                stopStep = Math.floor(Math.random() * 8 + 1);

                if (self.lotteryType === 1) {
                    self.init.countLuckBoth = self.init.countLuckBoth - 1
                }else {
                    self.init.countLuckBoth = self.init.countLuckBoth - self.init.number
                }
                self.init.countLuckMonkey = Math.floor(self.init.countLuckBoth/self.init.number)

                if (os !== 3) {
                    hook.getEvent('lotteryLuckBoth'
                        + '&isNeedRealName=0&args_type_1_string_' + self.lotteryType, $($event.currentTarget))
                }else {
                    base.getAPI({
                        url: self.urls.lottery,
                        data: {
                            invokeMethod: 'lotteryLuckBoth',
                            invokeParameters:'args_type_1_string_'+self.lotteryType
                        },
                        callback: function(data) {
                            if (data.success) {
                                stopStep = data.result.reward
                                runF()
                            }
                        }
                    })
                }

                function runF(result) {
                    if (step >= (24 + stopStep))//设置转动多少圈
                    {
                        $(e + (step % 8)).css("border-color", "#843aff");
                        self.awardRank = step % 8
                        if (self.lotteryType===1) {
                            if (2 == stopStep) {
                                self.openDialog('popularity')
                            }else if(7 == stopStep) {
                                self.openDialog('entity')
                            }else {
                                self.openDialog('coupon')
                            }
                            self.myLotteryObj = self.luckBothGiftList[stopStep]
                        }else {
                            if (7 == stopStep) {
                                self.openDialog('popularity')
                            }else if(0 == stopStep || 2 == stopStep || 5 == stopStep) {
                                self.openDialog('entity')
                            }else {
                                self.openDialog('coupon')
                            }
                            self.myLotteryObj = self.luckMonkeyList[stopStep]
                        }
                        clearTimeout(runT);
                        return;
                    }

                    if (step >= (16 + stopStep)) { //在即将结束转动前减速
                        during += 1;
                    } else {
                        if (during <= 2) { //控制中间转速
                            during = 2;
                        }
                        during--;
                    }
                    step++;
                    $(e).each(function () {
                        $(this).css("border-color", "#dcc488");
                    });
                    $(e + (step % 8)).css("border-color", "#843aff");
                    runT = setTimeout(runF, during * 50);
                }
            },
            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            },
            // 我的中奖记录
            loadMoreData:function ($event) {
                var self=this
                self.pages.pageNo++
                self.isLoad = true

                if (os !== 3) {
                    hook.getEvent('myLotteryRecord'
                        + '&isNeedRealName=0&args_type_1_integer_'+self.lotteryType
                        + '&args_pageNo_2_integer_'+self.pages.pageNo, $($event.currentTarget))
                }else {
                    base.getAPI({
                        url: self.urls.lotteryRecord,
                        data: {
                            invokeMethod: 'myLotteryRecord',
                            invokeParameters: 'args_type_1_integer_'+self.lotteryType+'&args_pageNo_2_integer_'+self.pages.pageNo
                        },
                        callback: function(data) {
                            if(data.success){
                                var res = data.result
                                self.isLoad = false
                                //列表
                                var resList = res.data

                                resList.forEach(function(obj){
                                    self.lotteryRecord.push(obj)
                                })

                                //分页
                                self.pages = {
                                    pageNo: res.pageNo,
                                    totalPageCount: res.totalPageCount
                                }
                            }
                        }
                    })
                }
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

    $('.j-usersRankList').scrollUserList({
        size: 5,
        height: -40,
        length: 1
    })

// App数据处理
    window.hookCallback = function (data, eventName) {
        switch (eventName) {
            case 'receiveLuckBag':
                dealSureGetLuckyBag(data);
                break
            case 'lotteryLuckBoth':
                dealLottery(data);
                break
            case 'myLotteryRecord':
                dealLoadMoreData(data);
                break
        }

        // 领取福袋回调事件
        function dealSureGetLuckyBag(data) {
            if(data.success){
                vm.init.reward = data.result.reward
                vm.openDialog('luckyBagWin')
            }else {
                vm.openTipsFrame(3)
            }
        }

        // 抽奖回调事件
        function dealLottery(data) {
            var stopStep = 1;    //表示最终奖品位置
            var runT = null;     //转动方法
            var step = -1        //计算转动的步数，控制转速和停止
            var during = 2;      //转速
            var e = ''

            if(vm.lotteryType===1){
                e = '.gift'
            }else {
                e= '.bigGift'
            }
            vm.lotteryTime = new Date().getTime()
            vm.closeDialog()
            stopStep = Math.floor(Math.random() * 8 + 1);

            if (data.success) {
                stopStep = data.result.reward
                runF()
            }

            function runF(result) {
                if (step >= (24 + stopStep))//设置转动多少圈
                {
                    $(e + (step % 8)).css("border-color", "#843aff");
                    vm.awardRank = step % 8
                    if (vm.lotteryType===1) {
                        if (2 == stopStep) {
                            vm.openDialog('popularity')
                        }else if(7 == stopStep) {
                            vm.openDialog('entity')
                        }else {
                            vm.openDialog('coupon')
                        }
                        vm.myLotteryObj = vm.luckBothGiftList[stopStep]
                    }else {
                        if (7 == stopStep) {
                            vm.openDialog('popularity')
                        }else if(0 == stopStep || 2 == stopStep || 5 == stopStep) {
                            vm.openDialog('entity')
                        }else {
                            vm.openDialog('coupon')
                        }
                        vm.myLotteryObj = vm.luckMonkeyList[stopStep]
                    }
                    clearTimeout(runT);
                    return;
                }

                if (step >= (16 + stopStep)) { //在即将结束转动前减速
                    during += 1;
                } else {
                    if (during <= 2) { //控制中间转速
                        during = 2;
                    }
                    during--;
                }
                step++;
                $(e).each(function () {
                    $(this).css("border-color", "#dcc488");
                });
                $(e + (step % 8)).css("border-color", "#843aff");
                runT = setTimeout(runF, during * 50);
            }
        }

        // 我的中奖记录分页回调事件
        function dealLoadMoreData(data) {
            if(data.success){
                var res = data.result
                vm.isLoad = false
                //列表
                var resList = res.data

                resList.forEach(function(obj){
                    vm.lotteryRecord.push(obj)
                })

                //分页
                vm.pages = {
                    pageNo: res.pageNo,
                    totalPageCount: res.totalPageCount
                }
            }
        }
    }
})
