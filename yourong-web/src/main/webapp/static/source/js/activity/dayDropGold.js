(function () {

    if (isDev) {
        Vue.config.devtools = true
    }

    var path = environment.globalPath
    var vm = new Vue({
        el: '#j-dayDropGold',
        data: {
            urls: {
                init: path + '/activity/dayDropGold/init',                  //页面初始化接口
                receive: path + '/activity/ dayDropGold/receive'            //领取福袋接口
            },
            init: {},
            logined: logined,
            cashIndex: 0,
            cashList: ['',88,128,238],  //现金券
            tipsIndex: 0,
            tipsList: ['','活动即将开始','下手太晚，明天早点来哦~','下手太晚，今天已经没啦'],
            rewardInfoList: ['黄金10g+人气值588点','黄金10g+人气值188点','黄金10g','人气值588点','人气值388点',
                '人气值288点','人气值188点','人气值188点','人气值88点','人气值88点'],
            isSuccess: false,
            isLastDay: false,  //是否为活动最后一天
            isShow: false,  //是否显示弹框
            isShowTips: false,  //是否显示模态框
            isCover: false,  //是否显示遮罩层
            isWhiteCover: false,  //是否显示透明遮罩层
            isClick: true
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
        },
        computed:{
            //红包按钮文案
            redBagText: function () {
                var self = this
                var text = ''

                if (2===self.init.status) {
                    text = '领取'
                }else if(4===self.init.status) {
                    text = '领取'
                    if (0===self.init.totalRed) {
                        text = '明天早点来~'
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
        methods: {
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
                            usernames : "虚位以待",
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
                var current = env.serverDate  //当前时间（时间戳）
                var endTime = self.init.endTime  //活动结束时间（时间戳）
                var endDay = self.formatTime(endTime,'yyyy/MM/dd')  //活动结束日期
                var endTimeStart = new Date(endDay + ' 00:00:00').getTime() //最后一天的凌晨00点（时间戳）

                if (current >= endTimeStart && current < endTime) {
                    flag =  true
                }

                return flag
            },
            // 初始化数据
            initData: function (result) {
                var self = this
                self.init = {
                    startTime: result.startTime || 0,    //活动开始时间
                    endTime: result.endTime || 0,    //活动结束时间
                    status: result.status || 2,    //活动状态
                    investFirstTen: result.investFirstTen || [] ,    //英雄榜
                    myInvestAmount: result.myInvestAmount || 0,    //今日投资额
                    myTotalInvestAmount: result.myTotalInvestAmount || 0,    //累计投资额
                    totalRed: result.totalRed || 0    //红包数量
                };

                //夺金英雄榜列表小于10条时用虚位以待填充
                self.init.investFirstTen = self.fillLuckyMemberList(self.init.investFirstTen)
                //是否为活动最后一天
                self.isLastDay = self.testLastDay()
                //红包被抢完可能出现-1，归为0
                if (self.init.totalRed < 0){
                    self.init.totalRed = 0
                }

            },
            // 挑红包
            getRedBag: function (val) {
                var self = this
                var status = self.init.status
                if (2 === status) {
                    self.openTipsFrame(1)
                }else if(4 === status) {
                    self.cashIndex = val

                    if (self.init.totalRed > 0) {
                        if (!self.isClick) {
                            return false
                        }
                        self.isClick = false

                        $.xPost({
                            url: self.urls.receive,
                            data: {
                                couponAmount: self.cashIndex
                            },
                            callback: function(data) {
                                if(data.success){
                                    self.isSuccess = true
                                    self.init.totalRed = data.result.totalRed
                                    self.showDialog('show')
                                }else {
                                    switch (data.resultCodeEum[0].code) {
                                        case "91322":
                                            if (self.isLastDay) {
                                                self.openTipsFrame(3)
                                            }else {
                                                self.openTipsFrame(2)
                                            }
                                            break;
                                        case "94015":
                                            self.isSuccess = false
                                            self.showDialog('show')
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                self.isClick = true
                            }
                        })
                    }else {
                        return false
                    }

                }else if(6 === status) {
                    return false
                }
            },
            // 非模态提示框
            openTipsFrame: function(num) {
                var self = this
                self.tipsIndex = num
                self.isShowTips = true
                self.isWhiteCover = true
                setTimeout(function(){
                    self.isShowTips = false
                    self.isWhiteCover = false
                },2000)
            },
            // 弹框的显示、隐藏
            showDialog: function (action) {
                var self = this
                if ('show'==action) {
                    self.isShow = true
                    self.isCover = true
                } else {
                    self.isShow = false
                    self.isCover = false
                }
            }
        }
    })
})()



