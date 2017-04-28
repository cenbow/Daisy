(function () {
    var lotteryRotate = $('.j-lottery-rotate');
    if (isDev) {
        Vue.config.devtools = true
    }
    //点击抽奖
    var ROTATE_OFFSET = 22.5,
        luckBothGiftList = {                           //福禄双全奖品列表  --应用于转盘
            'rewardFor5': 0 - ROTATE_OFFSET,           //2%收益券
            'rewardFor6': -45 - ROTATE_OFFSET,         //20元现金券
            'rewardFor7': -90 - ROTATE_OFFSET,         //kindle入门版
            'rewardFor1': 180 - ROTATE_OFFSET,         //16%收益券
            'rewardFor2': 135 - ROTATE_OFFSET,         //58点人气值
            'rewardFor3': 90 - ROTATE_OFFSET,          //10%收益券
            'rewardFor0': -135 - ROTATE_OFFSET,        //150元现金券
            'rewardFor4': 45 - ROTATE_OFFSET           //30元现金券
        },
        luckMonkeyGiftList = {                         //福禄天齐奖品列表  --应用于转盘
            'rewardFor4': 0 - ROTATE_OFFSET,           //4%收益券
            'rewardFor5': -45 - ROTATE_OFFSET,         //200元京东购物卡
            'rewardFor0': -90 - ROTATE_OFFSET,         //iphone7 128G
            'rewardFor2': 180 - ROTATE_OFFSET,         //DW手表
            'rewardFor7': 135 - ROTATE_OFFSET,         //318点人气值
            'rewardFor6': 90 - ROTATE_OFFSET,          //3%收益券
            'rewardFor1': -135 - ROTATE_OFFSET,        //1288元现金券
            'rewardFor3': 45 - ROTATE_OFFSET           //588元现金券
        };

    var path = environment.globalPath
    var vm = new Vue({
        el: '#j-fiveBillion',
        data: {
            urls: {
                init: path + '/activity/fiveBillion/init',                  //页面初始化接口
                receive: path + '/activity/fiveBillion/receive',            //领取福袋接口
                lotteryLuck: path + '/activity/fiveBillion/lotteryLuck',     //抽奖接口
                awardRecord: path + '/activity/fiveBillion/myLotteryRecord'      //中奖纪录接口
            },

            init: {},
            logined: logined,
            lotteryType: 1,       //1为福禄双全  2为福禄天齐
            rewardType: 0,
            tipsShow: false,      //是否显示非模态提示框
            tipsType: 0,          //提示类型
            tipsTextList: [
                '客官别急，活动马上就要开始啦~',
                '抱歉，您来晚了，多多关注其他活动哦~',
                '网络拥挤，过会再来~',
                '活动期间只能领取一个哦~'
            ],
            modalList:[
                {text:'活动期间只能领一个哦，确认领取？',btnText:'确认',href:'',clickMethod:'confirmGetLuckyBag'},
                {text: '哇咔咔，福袋已是您的囊中之物啦，赶紧去使用吧~',btnText:'使用福袋去投资',href:'/products/list-all-all-investing-1-createTimeDesc-1.html',clickMethod:''},
                {text: '客官，您的抽奖券不足',btnText:'投资去获取更多抽奖机会',href:'/products/list-all-all-investing-1-createTimeDesc-1.html',clickMethod:''},
            ],
            modalObj: {},
            coverShow: false,          //黑色蒙层
            whiteCoverShow: false,     //透明蒙层
            ruleShow: false,           //是否显示规则弹框
            showDialog: false,         //是否显示弹框

            luckBothTotalSize: 0,     //福禄双全中奖记录总条数
            luckMonkeyTotalSize: 0,   //福禄天齐中奖记录总条数
            pageSize: 20,             //每一页中奖记录数量

            pages: {                  //我的中奖记录分页
                max: 1,
                min: 1,
                pageNo: 1,
                totalPageCount: 0
            },

            myLotteryList: [],
            lotteryBtnStatus: false, //抽奖按钮是否可点击

            luckBothGiftList: [     //福禄双全奖品列表  --应用于获奖弹框
                {rewardIndex: 0, rewardValue: '158元', rewardText: '158元现金券'},
                {rewardIndex: 1,rewardValue:'16%',rewardText:'16%收益券'},
                {rewardIndex: 2,rewardValue:'58点',rewardText:'58点人气值'},
                {rewardIndex: 3,rewardValue:'10%',rewardText:'10%收益券'},
                {rewardIndex: 4, rewardValue: '80元', rewardText: '80元现金券'},
                {rewardIndex: 5,rewardValue:'2%',rewardText:'2%收益券'},
                {rewardIndex: 6,rewardValue:'20元',rewardText:'20元现金券'},
                {rewardIndex: 7,rewardValue:'kindle',rewardText:'kindle入门版一台'}
            ],
            luckMonkeyList: [      //福禄天齐奖品列表  --应用于获奖弹框
                {rewardIndex: 0,rewardValue:'iPhone7 128G',rewardText:'iPhone7  128G一台'},
                {rewardIndex: 1, rewardValue: '1328元', rewardText: '1328元现金券'},
                {rewardIndex: 2,rewardValue:'DW手表',rewardText:'DW手表一块'},
                {rewardIndex: 3,rewardValue:'588元',rewardText:'588元现金券'},
                {rewardIndex: 4,rewardValue:'4%',rewardText:'4%收益券'},
                {rewardIndex: 5,rewardValue:'200元京东购物卡',rewardText:'200元京东购物卡一张'},
                {rewardIndex: 6,rewardValue:'3%',rewardText:'3%收益券'},
                {rewardIndex: 7,rewardValue:'318点',rewardText:'318点人气值'}
            ],
            myLotteryObj: {},

            clickLuckyBagIndex: 0,    //点击的福袋的坐标
            sign: ''//弹框种类太多加个标志

        },
        created: function () {
            var self = this
            $.xPost({
                url: self.urls.init,
                callback: function(data){
                    if(data.success){
                        self.initData(data.result)
                    }else {
                        console.log('页面初始化错误',data)
                    }
                }
            })
            var height = $(window).height(),
                scrollHeight = height - 100
            console.log(scrollHeight)
            if (scrollHeight < 664) {
                $('.u-scroll-forsmall').css({"height": scrollHeight, "overflow-y": "scroll"})
            }

        },
        watch: {
            init: function () {
                var self = this
                self.scrollUserList({
                    size: 8,
                    height: -60,
                    length: 1
                })
            }
        },
        methods: {
            // 福禄双全与福禄双齐中奖列表滚动
            scrollUserList: function (config) {
                $(".j-usersRankList").each(function () {
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
            },
            // 用虚位以待填充
            fillLuckyMemberList: function (arr) {
                if(arr.length < 8){
                    var arrLength = arr.length
                    for (var i = 0; i < 8 - arrLength; i++) {
                        arr.push({
                            happenTime: '',
                            usernames : "虚位以待",
                            avatars : ''
                        })
                    }
                }
                return arr
            },
            // 初始化数据
            initData: function (result) {
                var self = this
                self.init = {
                    startTime: result.startTime || '',   //活动开始时间
                    endTime: result.endTime || '',       //活动结束时间
                    status: result.status || 2,          //活动状态
                    number: result.number || 0,          //消耗抽奖券的数量

                    myListLuckBoth: result.myListLuckBoth || [],      //福禄双全 我的中奖记录
                    myListLuckMonkey: result.myListLuckMonkey || [],  //福禄天齐 我的中奖记录
                    luckBothRecord: result.luckBothRecord || [],      //福禄双全 获奖名单
                    luckMonkeyRecord: result.luckMonkeyRecord || [],  //福禄天齐 获奖名单

                    myInvestAmount: result.myInvestAmount || 0,       //我的累计投资额
                    countLuckBoth: result.countLuckBoth || 0,         //福禄双全 抽奖次数
                    countLuckMonkey: result.countLuckMonkey || 0,     //福禄天齐 抽奖次数
                    reward: result.reward || 0,                       //领取的福袋的类型
                };

                self.luckBothTotalSize = self.init.myListLuckBoth.length
                self.luckMonkeyTotalSize = self.init.myListLuckMonkey.length
                //福禄双全与福禄双齐中奖列表小于8条时用虚位以待填充
                self.init.luckBothRecord = self.fillLuckyMemberList(self.init.luckBothRecord)
                self.init.luckMonkeyRecord = self.fillLuckyMemberList(self.init.luckMonkeyRecord)
            },
            // 福禄双全与福禄双齐的切换事件
            changeLotteryType: function (val) {
                this.lotteryType = val
            },
            // 福袋的移入事件
            switchRewardType: function (val) {
                this.rewardType = val
            },
            // 领取福袋
            getLuckyBag: function (index) {
                var self = this
                if(self.init.status === 2) {
                    self.openTipsFrame(0)
                }else if(self.init.status === 4) {
                    if(!self.logined){
                        self.goLoginPage();
                    }else {
                        if(self.init.reward === index){
                            return false
                        }else if(self.init.reward && self.init.reward !== index){
                            self.openTipsFrame(3)
                        }else {
                            self.modalObj = self.modalList[0]
                            self.clickLuckyBagIndex = index
                            self.openDialog('ensure')
                        }
                    }
                }else if(self.init.status === 6) {
                    self.openTipsFrame(1)
                }
            },
            // 确定领取福袋
            confirmGetLuckyBag: function () {
                var self = this
                self.closeDialog()
                $.xPost({
                    url: self.urls.receive,
                    data: {
                        couponAmount: self.clickLuckyBagIndex
                    },
                    callback: function(data) {
                        if(data.success){
                            self.init.reward = data.result.reward
                            self.modalObj = self.modalList[1]
                            self.openDialog('ensure')
                        }else {
                            self.openTipsFrame(3)
                        }
                    }
                })
            },
            // 去登陆
            goLoginPage: function () {
                $.xPost({url: this.urls.receive})
            },
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
            // 规则弹框
            switchRuleModal: function(action) {
                var self = this
                if (action=='show') {
                    self.ruleShow = true
                    self.coverShow = true
                }else {
                    self.ruleShow = false
                    self.coverShow = false
                }
            },
            // 打开弹框
            openDialog: function (type) {
                var self = this
                this.sign = type
                this.showDialog = true
                this.coverShow = true
            },
            // 关闭弹框
            closeDialog: function (callback) {
                this.showDialog = false
                this.coverShow = false
                this.pages.pageNo = 1
                this.sign = ''
                if(callback) {
                    window.location.reload()
                }
            },
            // 抽奖
            isSureLottery: function () {
                var self = this
                var lotteryNumber = 0
                if(self.init.status === 2) {
                    self.openTipsFrame(0)
                }else if(self.init.status === 4) {
                    if(!self.logined){
                        self.goLoginPage();
                    }else {
                        //阻止反复点击
                        if (self.lotteryBtnStatus) {
                            return false;
                        }

                        if(self.lotteryType===1){
                            lotteryNumber = self.init.countLuckBoth
                        }else {
                            lotteryNumber = self.init.countLuckMonkey
                        }

                        if (lotteryNumber>0) {
                            self.openDialog('selectUseCount')
                        }else {
                            self.modalObj = self.modalList[2]
                            self.openDialog('ensure')
                        }
                    }
                }else if(self.init.status === 6) {
                    self.openTipsFrame(1)
                }
            },
            // 确认抽奖
            goLottery: function () {
                var self = this;
                self.closeDialog()

                //阻止反复点击
                if (self.lotteryBtnStatus) {
                    return false;
                }

                self.lotteryBtnStatus = true;

                //初始转动
                lotteryRotate.rotate({
                    angle: 0,
                    animateTo: 21580,
                    duration: 10000,
                    callback: function () {
                        self.lotteryBtnStatus = false;
                        self.openTipsFrame(2)
                    }
                });
                //抽奖
                self.drawLottery(function (data) {
                    //init
                    self.lotteryBtnStatus = true;
                    lotteryRotate.stopRotate();
                    if (typeof(data) !== 'object') {
                        self.openTipsFrame(2)
                        return false;
                    }
                    if (data.success === false) {
                        $.xDialog({
                            content: data.resultCodeEum[0].msg,
                            callback: function () {
                                window.location.reload();
                            }
                        });
                        self.lotteryBtnStatus = false;
                        return false;
                    }

                    //随机位置系数
                    var num = Math.random(),
                        factor = Math.round(num) ? 1 : -1,
                        point = parseInt(num * factor * 6);

                    var result = data.result;

                    //剩余抽奖次数
                    if (self.lotteryType === 1) {
                        self.init.countLuckBoth = self.init.countLuckBoth - 1
                    }else {
                        self.init.countLuckBoth = self.init.countLuckBoth - self.init.number
                    }
                    self.init.countLuckMonkey = Math.floor(self.init.countLuckBoth/self.init.number)

                    //中奖系数
                    var rewardCode = '', rotateDeg = 0;
                    rewardCode = 'rewardFor'+result.reward

                    if (self.lotteryType === 1) {
                        rotateDeg = luckBothGiftList[rewardCode]
                    }else {
                        rotateDeg = luckMonkeyGiftList[rewardCode]
                    }

                    //转动抽奖
                    lotteryRotate.rotate({
                        angle: 0,
                        animateTo: 1440 + rotateDeg + point,
                        duration: 5000,
                        callback: function () {
                            if (result.hasOwnProperty('reward') && result.reward != null) {
                                var target = ''
                                if (self.lotteryType===1) {
                                    if (2 === result.reward) {
                                        target = 'popularity'
                                    }else if(7 === result.reward) {
                                        target = 'entity'
                                    }else {
                                        target = 'coupon'
                                    }
                                    self.myLotteryObj = self.luckBothGiftList[result.reward]
                                }else {
                                    if (7 === result.reward) {
                                        target = 'popularity'
                                    }else if(0 === result.reward || 2 === result.reward || 5 === result.reward) {
                                        target = 'entity'
                                    }else {
                                        target = 'coupon'
                                    }
                                    self.myLotteryObj = self.luckMonkeyList[result.reward]
                                }
                                self.openDialog(target)
                            }

                            self.lotteryBtnStatus = false;

                        }
                    });
                });
            },
            drawLottery: function (callback) {
                $.xPost({
                    url: this.urls.lotteryLuck,
                    data: {
                        type: this.lotteryType
                    },
                    callback: callback
                })
            },
            //分页
            goPage: function (pageNo) {
                var pages = this.pages,
                    self = this
                if (pageNo > 0 && pageNo <= pages.totalPageCount) {
                    self.pages.pageNo = pageNo
                    self.awardRecord(self.lotteryType)
                }
            },
            // 中奖纪录
            awardRecord: function () {
                var self = this
                if (!self.logined) {
                    self.goLoginPage();
                } else {
                    $.xPost({
                        url: this.urls.awardRecord,
                        data: {
                            type: self.lotteryType,
                            pageNo: self.pages.pageNo
                        },
                        callback: function (data) {
                            if (data.success) {
                                var awardRecord = data.result,
                                    list = awardRecord.data
                                self.pages.pageNo = awardRecord.pageNo
                                self.pages.max = awardRecord.maxPager - awardRecord.minPager + 1
                                self.pages.min = awardRecord.minPager
                                self.pages.totalPageCount = awardRecord.totalPageCount
                                self.myLotteryList = awardRecord.data
                            }

                        }
                    })
                    this.sign = 'lotteryRecord'
                    this.showDialog = true
                    this.coverShow = true
                }
            }
        }
    })

})()



