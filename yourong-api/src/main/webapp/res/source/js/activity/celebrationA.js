define(['base'], function (require, exports, module) {

    var path = environment.globalPath,
        base = require('base'),
        initResult = initData.result,
        hook = new AppHook(os)

    var startTime = initResult.startTime || 0,
        endTime = initResult.endTime || 0

    Vue.config.devtools = true

    window.hookCallback = function (data, eventName) {

        if (eventName === Celebration.invokeName) {

            if (data.success) {

                Celebration.rewards = data.result
                Celebration.received = true
            } else {
                base.xTips({content: data.resultCodes[0].msg})
            }
        }
    }

    var Celebration = new Vue({
        el: '#j-celebration',
        data: {
            loginUrl: path + '/mstation/login?from=' + location.href,
            redPacketsUrl: path + '/activity/dynamicInvoke',
            rewards: '',
            os: os,
            status: initResult.activityStatus,
            received: initResult.isReceived,
            invokeName: 'receiveCelebrationA',
            startTime: formatTime(startTime, 'M月d日'),
            endTime: formatTime(endTime, 'M月d日'),
            fullStartTime:  formatTime(startTime, 'yyyy年M月d日hh:mm'),
            fullEndTime: formatTime(endTime, 'M月d日hh:mm'),
        },
        props: ['platform'],
        computed: {
            rewardsBtnText: function () {
                var text = ''
                switch (this.status) {
                    case 2:
                        text = '未开始'
                        break
                    case 4:
                        text = this.received ? '已领取' : '领取红包'
                        break
                    case 6:
                        text = '已结束'
                        break
                    default:
                }
                return text
            },
            logined: function () {
                return this.os === 1 ? initResult.isLogined : window.mLogined
            },
            startDate: function () {

                // return this.getDate(initResult.startTime)
            },
            endDate: function () {

                // return this.getDate(initResult.endTime)
            }
        },
        methods: {
            getRewards: function () {

                if (this.status === 2) {

                    //base.xTips({content: '活动暂未开始,敬请期待'})
                    return false

                } else if (this.status === 6) {

                    base.xTips({content: '活动已结束'})
                    return false
                }

                if (this.logined) {

                    if (!this.received) {
                        this.getRewardsData()
                    }
                    else {
                        //base.xTips({content: '您今日已领取过红包'})
                    }
                }
                else {
                    if (this.os === 1) {
                        this.getRewardsData()
                    } else {
                        location.href = this.loginUrl
                    }
                }
            },
            getRewardsData: function () {

                if (this.platform < 3) {
                    console.log('getEvent', this.invokeName)
                    hook.getEvent(this.invokeName + '&isNeedRealName=0')
                } else {
                    console.log('mweb', this.invokeName)
                    var self = this
                    base.getAPI({
                        url: self.redPacketsUrl,
                        data: {
                            invokeMethod: self.invokeName
                        },
                        callback: function (data) {
                            hookCallback(data, self.invokeName)
                        }
                    })
                }
            }
            // ,
            // getDate: function (timestamp) {
            //
            //     const date = new Date(timestamp),
            //         month = date.getMonth() + 1,
            //         days = date.getDate(),
            //         hours = date.getHours(),
            //         minutes = date.getMinutes()
            //     return month + '月' + days + '日'
            // }
        }
    })



    function formatTime (date, format){
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
})