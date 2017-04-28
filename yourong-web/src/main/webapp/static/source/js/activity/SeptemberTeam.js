(function () {

    var path = environment.globalPath,
        logined = $('#j-top').data('logined')

    Vue.config.devtools = true

    Vue.filter('formatBetReward', function (val) {
        return val == -1 ? '待揭晓' : val
    })

    var SepTeam = new Vue({
        el: '#j-SepTeam',
        data: {
            // 后端接口地址
            urls: {
                init: path + '/activity/julyTeam/init',
                joinTeam: path + '/activity/julyTeam/joinTeam',
                receiveCoupon: path + '/activity/julyTeam/receiveCoupon',
                betTeam: path + '/activity/julyTeam/betTeam'
            },

            logined: logined,

            initData: {}, // 初始化接口返回数据

            betGroupType: 0, // 具体竞猜哪个战队 1红 2蓝
            passValid: false,
            validErrorMsg: '', // 用于表单验证提示

            canReceiveCouponThisTime: true,

            team1Count:[],
            team2Count:[],

            showPinDialog: false, // 显示竞猜框

            pkHistoryList: []
        },
        created:function(){
            var self = this

            $.xPost({
                url: self.urls.init,
                callback: function (data) {
                    if(data.success){
                        init(data.result)

                        log(data.result)
                        self.$set('team1Count', self.fillCount(data.result.todayInvestAmountA||0))
                        self.$set('team2Count', self.fillCount(data.result.todayInvestAmountB||0))

                        log(self.team1Count)
                        log(self.team2Count)
                        // TODO
                        var str = ''
                        for(var i = 0; i < self.team1Count.length; i++){
                            str += '<li>'+ self.team1Count[i] +'</li>'
                        }
                        $('#j-team1Count').html(str)

                        var str2 = ''
                        for(var j = 0; j < self.team2Count.length; j++){
                            str2 += '<li>'+ self.team2Count[j] +'</li>'
                        }
                        $('#j-team2Count').html(str2)
                    }else{
                        console.log('页面初始化错误',data)
                    }
                }
            })

            function init(result) {
                if(result.activityStatus == 2 || !logined || !result.currentGroupType || result.activityStatus == 6 ){
                    // 未登录或未开始或已结束, 或者没加入战队都显示默认数值
                    result.remindCoupon= {
                        coupon30: 400,
                        coupon50: 300,
                        coupon100: 500,
                        coupon200: 200
                    }
                }
                var remindCoupon = result.remindCoupon || {}

                self.initData = {
                    startTime: result.startTime || '',
                    endTime: result.endTime || '',
                    activityStatus: result.activityStatus,

                    // Pk战队
                    pkHistoryList: result.pkHistoryList || [],  // PK记录
                    countStartTime: result.countStartTime || '',
                    countEndTime: result.countEndTime || '',

                    // 加入战队 PK
                    joinStartTime: result.joinStartTime || '',
                    joinEndTime: result.joinEndTime || '',
                    joined: result.joined || false,
                    todayInvestAmountA:  result.todayInvestAmountA || 0,
                    todayInvestAmountB: result.todayInvestAmountB || 0 ,
                    todayInvestAmountMy: result.todayInvestAmountMy || 0,
                    currentGroupType: result.currentGroupType || '',
                    successFlag: result.successFlag || '',
                    canJoinTeam: result.canJoinTeam || false,

                    // 抽取红包
                    remindCoupon: {
                        coupon30:  remindCoupon.coupon30,
                        coupon50: remindCoupon.coupon50,
                        coupon100: remindCoupon.coupon100,
                        coupon200: remindCoupon.coupon200
                    },
                    receiveStartTime: result.receiveStartTime || '',
                    receiveEndTime: result.receiveEndTime || '',
                    canReceive: result.canReceive || '',

                    // 竞猜战队
                    betStartTime: result.betStartTime || '',
                    betEndTime: result.betEndTime || '',
                    beted: result.beted || false,
                    canBet: result.canBet || false, // 当前时间能否竞猜
                    betList: result.betList || [],
                    currentBetMember: result.currentBetMember || 0,
                    lastBetMember: result.lastBetMember || 0,
                    ifFirstBet: result.ifFirstBet || false,


                    // 排行榜
                    julyTeamContribution: result.julyTeamContribution || [],

                }

                self.pkHistoryList =  self.fillPkList(result.pkHistoryList || [])  // PK记录
            }

        },
        computed: {

            // 两队PK战绩
            // 是否显示口哨下的文案
            showPkTitle: function () {
                var self = this,
                    now = new Date(env.serverDate).getHours()
                return !(now > self.initData.countStartTime - 1 && now < self.initData.countEndTime)
            },

            // 领取红包按钮是否置灰
            getCouponBtnStatus: function(){
                var self = this,
                    now = new Date(env.serverDate).getHours()
                if (self.initData.activityStatus == 2) {
                    return true
                }else {
                    if (self.logined) {
                        return (now > self.initData.receiveStartTime - 1 && now < self.initData.receiveEndTime)
                    }else {
                        return true
                    }
                }
            },

            // 领取红包按钮文案
            getCouponText: function () {
                return this.initData.activityStatus === 6 ? '活动已结束' : '我要领取'
            },

            // PK榜单
            julyTeamContributionLeft: function(){
                return this.splitContributionList(this.initData.julyTeamContribution, 1)
            },
            julyTeamContributionRight: function () {
                return this.splitContributionList(this.initData.julyTeamContribution, 2)
            },
            // 是否显示第一名皇冠 TODO 皇冠在胜利的队伍才显示
            showListWin: function () {
                var self = this,
                    now = new Date(env.serverDate).getHours()
                log('显示皇冠',!(now > self.initData.countStartTime - 1 && now < self.initData.countEndTime)
                    && (self.initData.currentGroupType==self.initData.successFlag))
                if(!(now > self.initData.countStartTime - 1 && now < self.initData.countEndTime)
                    && (self.initData.currentGroupType==self.initData.successFlag)
                ){
                    return true
                } else {
                    return false
                }
            }
        },
        methods: {
            fillCount: function (count) { // 填充战队的金额
                var str = count || 0,
                    arr = (str + '').split('')
                log(count)

                if (arr.length < 8) {
                    var arrLength = arr.length
                    for (var i = 0; i < 8 - arrLength; i++) {
                        arr.unshift('0')
                    }
                }
                arr.unshift('￥')

                return arr
            },

            fillPkList: function (arr) {
                var self = this,
                    activityStatus = self.initData.activityStatus
                if(activityStatus==2){
                    arr = []
                }
                var beginDay = new Date(self.initData.startTime).getDate()

                if(arr.length > 0){
                    var str = arr[0].pkTime,
                        strA = str.substring(str.indexOf('月') + 1),
                        strB = strA.substring(strA)
                    beginDay = strB.substring(0, strB.indexOf('日')) - 0

                    for(var i = 0; i < arr.length; i++){
                        arr[i].pkTime = formatTime(arr[i].happenTime, 'M月d日')
                    }
                }
                if(arr.length < 7){
                    var arrLength = arr.length
                    for (var i = 0; i < 7 - arrLength; i++) {
                        arr.push({
                            todayInvestAmountA: '-',
                            todayInvestAmountB: '-',
                            pkTime: '9月' + (beginDay + arrLength + i ) + '日'
                        })
                    }
                }
                return arr
            },

            // 最后的列表
            splitContributionList: function (arr, pos) {
                if(arr.length==0){
                    return []
                } else {
                    if(arr.length < 10){
                        var arrLength = arr.length
                        for (var i = 0; i < 10 - arrLength; i++) {
                            arr.push({
                                avatars: '',
                                usernames : "虚位以待",
                                totalInvest : ''
                            })
                        }
                    }
                    return (_.chunk(arr, 5))[pos-1]
                }
            },

            joinTeam: function () {
                var self = this,
                    activityStatus = self.initData.activityStatus,
                    dialogText = ''

                if(!self.logined){
                    self.goLoginPage()
                } else {
                    if(activityStatus == 4){
                        if(self.initData.canJoinTeam){
                            $.xPost({
                                url: self.urls.joinTeam,
                                callback: function (data) {
                                    var dialogText = ''

                                    if(data.success){
                                        self.initData.joined = true
                                        self.initData.currentGroupType = data.result.currentGroupType
                                        self.initData.remindCoupon = data.result.couponRemind || []
                                        self.initData.julyTeamContribution = data.result.julyTeamContribution

                                        var groupType = ['五仁月饼', '冰皮月饼']

                                        $.xDialog({
                                            content: '哇哦~恭喜加入'+ groupType[data.result.currentGroupType - 1] +'战队！<br>每日'+ self.initData.countStartTime +'：00后开始战队比拼哦'
                                        })
                                    } else {
                                        $.xDialog({content: data.resultCodeEum[0].msg})
                                    }
                                }
                            })
                        } else {
                            $.xDialog({content: '每日'+ self.initData.joinStartTime +'：00到'+ self.initData.joinEndTime +'：00可加入战队'})
                        }

                    } else {
                        if(activityStatus == 2){
                            $.xDialog({content: '来早啦，活动未开始~'})
                        } else if(activityStatus == 6){
                            // 活动结束后按钮盒备注消失
                            $.xDialog({content: '活动已结束~'})
                        }
                    }
                }
            },

            // 抽取现金券红包
            getCoupon: function (val) {
                // 往后台发送数据
                var self = this,
                    couponName = 'coupon' + val,
                    activityStatus = self.initData.activityStatus
                if(!self.logined){
                    self.goLoginPage()
                } else {
                    if(activityStatus == 4){
                        if(!self.initData.currentGroupType){
                            $.xDialog({
                                content: '加入战队才能领取哦~活动期间每日'+ self.initData.joinStartTime +'：00—'+  self.initData.joinEndTime+'：00可加入战队'
                            })
                        } else {
                            if(self.initData.remindCoupon[couponName] > 0 ){
                                if(self.initData.canReceive){
                                    $.xPost({
                                        url: self.urls.receiveCoupon,
                                        data: { couponAmount: val },
                                        callback: function (data) {
                                            log('getCoupon back:', data)
                                            if(data.success){
                                                // 这里需要重新渲染剩余的红包量
                                                self.initData.remindCoupon = data.result.couponRemind || []
                                                $.xDialog({
                                                    content: '恭喜你，获得'+ val +'元现金券一张'
                                                })
                                            } else {
                                                $.xDialog({
                                                    content: data.resultCodeEum[0].msg
                                                })
                                            }
                                        }
                                    })

                                } else {
                                    return false
                                    // $.xDialog({
                                    //     content: '每日'+ self.initData.receiveStartTime +'点到'+ self.initData.receiveEndTime +'点才可领取'
                                    // })
                                }

                            } else {
                                return false
                            }
                        }
                    } else {
                        if(activityStatus==2){
                            $.xDialog({content: '来早啦~活动未开始'})
                        } else if(activityStatus == 6){
                            return false
                        }
                    }
                }
            },


            goShowPinDialog: function (team) {
                var self = this,
                    dialogText = '',
                    activityStatus = self.initData.activityStatus

                self.betGroupType = team

                if(!self.logined){
                    self.goLoginPage()
                } else {

                    if(activityStatus==4){
                        if(!self.showPinDialog){
                            if(self.initData.canBet){
                                if(self.initData.beted){
                                    $.xDialog({ content: '每天只能竞猜一次哦' })
                                } else {
                                    self.supportTeam = team
                                    self.showPinDialog = true
                                }
                            } else {
                                $.xDialog({
                                    content: '每日'+ self.initData.betStartTime +'点至'+ self.initData.betEndTime +'点才可竞猜'
                                })
                            }
                        }
                    } else {
                        if(activityStatus == 2){
                            dialogText = '活动未开始'
                        } else if(activityStatus == 6){
                            dialogText = '活动已结束'
                        }
                        $.xDialog({content: dialogText})
                    }
                }

            },

            goClosePinDialog: function () {
                this.showPinDialog = false
            },

            // 押注
            goConfirmDialog: function (popularityValue) {
                var self = this,
                    supportTeam = this.supportTeam,
                    dialogText = ''

                this.validBetForm(popularityValue)

                if(this.passValid){
                    $.xPost({
                        url: self.urls.betTeam,
                        data: {
                            groupType: self.betGroupType,
                            popularityValue: popularityValue
                        },
                        callback: function (data) {
                            console.log('betTeam: ',data)

                            if(data.success){
                                self.initData.beted = true
                                //self.initData.canBet = false
                                self.initData.betList = data.result.betList
                                self.showPinDialog = false
                                self.initData.currentBetMember = data.result.betTotals
                                // 重置本轮竞猜人数
                            } else {
                                $.xDialog({
                                    content: data.resultCodeEum[0].msg
                                })
                                //if(!data.resultCodeEum[0].msg){
                                //    $.xDialog({
                                //        content:'当前参与人数过多，稍后再试'
                                //    })
                                //} else {
                                //    if(data.resultCodeEum[0].code='90707') {
                                //        self.validErrorMsg = '人气值不足'
                                //    } else {
                                //        $.xDialog({ content: data.resultCodeEum[0].msg })
                                //    }
                                //}
                            }
                        }
                    })
                }
            },

            // 竞猜表单验证
            validBetForm: function (popularityValue) {
                var self = this
                this.passValid = false

                if(popularityValue){
                    var re = new RegExp(/(^1[0-9]{1}$)|(^[1-9]{1}$)|(^20$)/);
                    if (popularityValue.match(re) == null) {
                        this.validErrorMsg = '请输入1-20之间的整数'
                    } else {
                        this.passValid = true
                        this.validErrorMsg = ''
                    }
                }
            },
            clearErrorMsg: function () {
                this.validErrorMsg = ''
            },


            // 去登陆
            goLoginPage: function () {
                $.xPost({url: this.urls.receiveCoupon})
            }
        }
    })
    window.SepTeam = SepTeam

    function formatTime(date, format) {
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
    }

})()
