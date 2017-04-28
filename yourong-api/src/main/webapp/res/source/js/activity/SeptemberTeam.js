define(['base'], function (require, exports, module) {
    'use strict'

    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function (e) {e.preventDefault()})

    /*查看规则弹窗*/
    $('#j-rule-win-btn').on('click',function(){$('#j-rule-window').fadeIn()})
    $('#j-rule-window .m-dialog-close-btn').on('click',function(){$('#j-rule-window').fadeOut()})

    var base = require('base'),
        logined = $('#j-SepTeam').data('logined'),
        path = environment.globalPath,
        invoke= path + '/activity/dynamicInvoke',
        hook = new AppHook(os),
        encryptionId = base.getUrlParam('encryptionId') || ''

    Vue.config.devtools = true
    Vue.filter('formatBetReward', function (val) {
        return val == -1 ? '待揭晓' : val
    })

    var SepTeam = new Vue({
        el: '#j-SepTeam',
        data: {
            // 后端接口地址
            urls: {
                init: invoke,
                joinTeam: invoke,
                receiveCoupon: invoke,
                betTeam: invoke
            },

            loginUrl: path + '/mstation/login?from=' + location.href,
            logined: os>2?logined:hook.logined,
            encryptionId: encryptionId,

            avatar: {
                default: path + '/res/img/member/avatar.png',
                prefix: 'https://oss-cn-hangzhou.aliyuncs.com'
            },

            initData: {}, // 初始化接口返回数据

            selectTab: 'tab1', // 切换3个tab

            showPkList: false,

            betGroupType: 0, // 具体竞猜哪个战队 1红 2蓝
            passValid: false,
            validErrorMsg: '', // 用于表单验证提示

            canReceiveCouponThisTime: true,

            team1Count:[],
            team2Count:[],

            selectedCouponName: '',
            selectedCouponVal: '',

            showPinDialog: false, // 显示竞猜框
            showListWin: false, // 显示第一名皇冠

            pkHistoryList: []
        },

        props: ['os'],

        created:function(){
            var self = this
            if(receiveData.success){
                //self.initData = receiveData.result
                init(receiveData.result)

                self.$set('team1Count', self.fillCount(receiveData.result.todayInvestAmountA||0))
                self.$set('team2Count', self.fillCount(receiveData.result.todayInvestAmountB||0))
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

            } else {
                log(receiveData)
            }

            function init(result) {
                if(result.activityStatus == 2 || (!self.logined && !encryptionId) || !result.currentGroupType || result.activityStatus == 6 ){
                    // 未登录或未开始或已结束, 没加入战队都显示默认数值
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

                // Pk记录
                self.pkHistoryList =  self.fillPkList(result.pkHistoryList || [])  // PK记录
            }
        },
        computed: {
            // 两队PK战绩
            // 是否显示口哨下的文案
            showPkTitle: function () {
                var self = this,
                    now = new Date(+environment.serverDate).getHours()
                return !(now > self.initData.countStartTime - 1 && now < self.initData.countEndTime)
            },

            // 领取红包按钮是否置灰
            getCouponBtnStatus: function(){
                var self = this,
                    now = new Date(+environment.serverDate).getHours()
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
            julyTeamContribution: function(){
                return this.splitContributionList(this.initData.julyTeamContribution, 1)
            },

            // 是否显示第一名皇冠
            showListWin: function () {
                var self = this,
                    now = new Date(+environment.serverDate).getHours()
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
            // tab 切换
            goSelectTab: function(tabNum){
                this.selectTab = tabNum
            },

            // 是否显示 PK列表
            goShowPkList: function(){
                this.showPkList = true
            },
            goClosePkList: function(){
                this.showPkList = false
            },

            fillCount: function (count) { // 填充战队的金额
                var str = count || 0,
                    arr = (str + '').split('')

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
                    return arr
                }
            },

            joinTeam: function ($event) {
                var self = this,
                    activityStatus = self.initData.activityStatus,
                    dialogText = ''

                if(activityStatus == 4){
                    if(self.os - 0 !== 3){
                        // 这里ios没有处理‘没有参数’这个情况，所以添加一个虚拟的参数
                        hook.getEvent('joinJulyTeam&isNeedRealName=0&args_abc_1_integer_1', $($event.currentTarget))
                    } else {
                        if(!self.logined){
                            self.goLoginPage()
                        } else {
                            if(self.initData.canJoinTeam){
                                base.getAPI({
                                    url: self.urls.joinTeam,
                                    data: {
                                        invokeMethod: 'joinJulyTeam'
                                    },
                                    callback: function (data) {
                                        var dialogText = ''

                                        if(data.success){
                                            self.initData.joined = true
                                            self.initData.currentGroupType = data.result.currentGroupType
                                            self.initData.remindCoupon = data.result.couponRemind || []
                                            self.initData.julyTeamContribution = data.result.julyTeamContribution || []
                                            var groupType = ['五仁月饼', '冰皮月饼']

                                            base.xTips({
                                                time: 1000,
                                                content: '哇哦~恭喜加入'+ groupType[data.result.currentGroupType - 1] +'战队！<br>每日'+ self.initData.countStartTime +'：00后开始战队比拼哦'
                                            })
                                        } else {
                                            base.xTips({ time: 1000,content: data.resultCodes[0].msg})
                                        }
                                    }
                                })
                            } else {
                                base.xTips({ time: 1000,content: '每日'+ self.initData.joinStartTime +'：00到'+ self.initData.joinEndTime +'：00可加入战队'})
                            }

                        }

                    }

                } else {
                    if(activityStatus == 2){
                        base.xTips({ time: 1000,content: '来早啦，活动未开始~'})
                    } else if(activityStatus == 6){
                        // 活动结束后按钮盒备注消失
                        base.xTips({ time: 1000,content: '活动已结束~'})
                    }
                }
            },

            // 抽取现金券红包
            getCoupon: function (val, $event) {
                // 往后台发送数据
                var self = this,
                    couponName = 'coupon' + val,
                    activityStatus = self.initData.activityStatus

                self.selectedCouponName = couponName
                self.selectedCouponVal = val

                if(self.os - 0 !== 3){
                    hook.getEvent('receiveJulyTeamCoupon' + '&isNeedRealName=0&args_couponAmount_1_integer_'+ val, $($event.currentTarget))
                } else {
                    if(!self.logined){
                        self.goLoginPage()
                    } else {
                        if(activityStatus == 4){
                            if(!self.initData.currentGroupType){
                                base.xTips({ time: 1000,
                                    content: '加入战队才能领取哦~<br>活动期间每日'+ self.initData.joinStartTime +'：00—'+ self.initData.joinEndTime +'：00可加入战队'
                                })
                            } else {
                                if (self.initData.remindCoupon[couponName] > 0) {
                                    if(self.initData.canReceive){
                                        base.getAPI({
                                            url: self.urls.receiveCoupon,
                                            data: {
                                                invokeMethod: 'receiveJulyTeamCoupon',
                                                invokeParameters: 'args_couponAmount_1_integer_' + val
                                            },
                                            callback: function (data) {
                                                log('getCoupon back:', data)

                                                if (data.success) {
                                                    // 这里需要重新渲染剩余的红包量
                                                    self.initData.remindCoupon = data.result.couponRemind || []
                                                    base.xTips({
                                                        time: 1000,
                                                        content: '恭喜你，获得' + val + '元现金券一张'
                                                    })
                                                } else {
                                                    base.xTips({
                                                        time: 1000,
                                                        content: data.resultCodes[0].msg
                                                    })
                                                }
                                            }
                                        })
                                    } else {
                                        return false
                                        // base.xTips({ time: 1000,
                                        //     content: '每日'+ self.initData.receiveStartTime +'点到'+ self.initData.receiveEndTime +'点才可领取'
                                        // })
                                    }

                                } else {
                                    return false
                                }
                            }
                        } else {
                            if(activityStatus==2){
                                base.xTips({ time: 1000,content: '来早啦~活动未开始'})
                            } else if(activityStatus == 6){
                                return false
                            }
                        }
                    }
                }
            },

            goShowPinDialog: function (team) {
                var self = this,
                    dialogText = '',
                    activityStatus = self.initData.activityStatus

                self.betGroupType = team

                if(!self.logined && os==3){
                    self.goLoginPage()
                } else {
                    if(activityStatus==4){
                        if(!self.showPinDialog){
                            if(self.initData.canBet){
                                if(self.initData.beted){
                                    base.xTips({
                                        time: 1000,
                                        content: '每天只能竞猜一次哦'
                                    })
                                } else {
                                    self.supportTeam = team
                                    self.showPinDialog = true
                                }
                            } else {
                                base.xTips({
                                    time: 1000,
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
                        base.xTips({ time: 1000,content: dialogText})
                    }
                }
            },

            goClosePinDialog: function () {
                this.showPinDialog = false
            },

            // 押注
            goConfirmDialog: function (popularityValue, $event) {
                var self = this,
                    supportTeam = this.supportTeam,
                    dialogText = ''

                this.validBetForm(popularityValue)

                if(this.passValid){

                    if(self.os - 0 !== 3){
                        hook.getEvent('betJulyTeam'
                            + '&isNeedRealName=0&args_groupType_1_integer_'+ self.betGroupType
                            + '&args_popularityValue_2_integer_' + popularityValue, $($event.currentTarget))
                    } else {
                        base.getAPI({
                            url: self.urls.betTeam,
                            data: {
                                invokeMethod: 'betJulyTeam',
                                invokeParameters: 'args_groupType_1_integer_' + self.betGroupType + '&args_popularityValue_2_integer_' + popularityValue
                            },
                            callback: function (data) {
                                if (data.success) {
                                    self.initData.beted = true
                                    self.initData.betList = data.result.betList
                                    self.showPinDialog = false
                                    self.initData.currentBetMember = data.result.betTotals
                                    // 重置本轮竞猜人数
                                } else {
                                    if (!data.resultCodes[0].msg) {
                                        base.xTips({time: 1000, content: '当前参与人数过多，稍后再试'})
                                    } else {
                                        if(data.resultCodes[0].code==90707){
                                            self.validErrorMsg = '人气值不足'
                                        } else{
                                            base.xTips({time: 1000, content: data.resultCodes[0].msg})
                                        }
                                    }
                                }
                            }
                        })
                    }
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
                location.href = this.loginUrl
                return false
            }
        }
    })


    // APP 处理数据
    window.hookCallback = function (data, eventName) {
        $('#j-console').html('APP的eventName' + eventName)
        switch (eventName){
            case 'joinJulyTeam':
                dealJoinTeam(data)
                break;

            case 'receiveJulyTeamCoupon':
                dealReceiveJulyTeamCoupon(data)
                break;

            case 'betJulyTeam':
                dealBetJulyTeam(data)
                break;
        }

        // 加入战队
        function dealJoinTeam(data){
            //alert(JSON.stringify(data, '', 4))
            $('#j-console').html('app 加入战队' + JSON.stringify(data, '', 4))

            var self = SepTeam,
                dialogText = ''
            if(data.success){
                self.initData.joined = true
                self.initData.currentGroupType = data.result.currentGroupType
                self.initData.remindCoupon = data.result.couponRemind || []
                self.initData.julyTeamContribution = data.result.julyTeamContribution || []
                var groupType = ['五仁月饼', '冰皮月饼']

                base.xTips({
                    time: 1000,
                    content: '哇哦~恭喜加入'+ groupType[data.result.currentGroupType - 1] +'战队！<br>每日'+  self.initData.countStartTime +'：00后开始战队比拼哦'
                })
            } else {
                base.xTips({ time: 1000,content: data.resultCodes[0].msg})
            }
        }

        function dealReceiveJulyTeamCoupon(data){
            //alert(JSON.stringify(data, '', 4))
            $('#j-console').html('app 领红包' + JSON.stringify(data, '', 4))
            var self = SepTeam,
                activityStatus = self.initData.activityStatus,
                couponName = self.selectedCouponName,
                val = self.selectedCouponVal

            if(activityStatus == 4){
                if(!self.initData.currentGroupType){
                    base.xTips({ time: 1000,
                        content: '加入战队才能领取哦~<br>活动期间每日'+ self.initData.joinStartTime +'：00—'+ self.initData.joinEndTime +'：00可加入战队'
                    })
                } else {
                    if (self.initData.remindCoupon[couponName] > 0) {
                        if (data.success) {
                            // 这里需要重新渲染剩余的红包量
                            self.initData.remindCoupon = data.result.couponRemind || []
                            base.xTips({
                                time: 1000,
                                content: '恭喜你，获得' + val + '元现金券一张'
                            })
                        } else {
                            if(self.initData.canReceive){
                                base.xTips({
                                    time: 1000,
                                    content: data.resultCodes[0].msg
                                })

                            } else {
                                return false
                                // base.xTips({
                                //     time: 1000,
                                //     content: '每日'+ self.initData.receiveStartTime +'点到'+ self.initData.receiveEndTime +'点才可领取'
                                // })
                            }
                        }
                    } else {
                        return false
                    }
                }
            } else {
                if(activityStatus==2){
                    base.xTips({ time: 1000,content: '来早啦~活动未开始'})
                } else if(activityStatus == 6){
                    return false
                }
            }
        }

        function dealBetJulyTeam(data){
            var self= SepTeam
            if (data.success) {
                self.initData.beted = true
                self.initData.betList = data.result.betList
                self.showPinDialog = false
                self.initData.currentBetMember = data.result.betTotals
            } else {
                if (!data.resultCodes[0].msg) {
                    base.xTips({time: 1000, content: '当前参与人数过多，稍后再试'})
                } else {
                    if(data.resultCodes[0].code==90707){
                        self.validErrorMsg = '人气值不足'
                    } else{
                        base.xTips({time: 1000, content: data.resultCodes[0].msg})
                    }
                }
            }
        }
    }

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

    if (window.debug) {
        window.SepTeam = SepTeam
    }
})
