(function () {
    var path = environment.globalPath,
        // base = require('base'),
        // initResult = initData.result
        initResult = {}

    $.xPost({
        url: path + '/activity/celebrationA/init',
        callback: function (data) {
            console.log('Pc初始化数据', data)
            initResult = data.result

            var startTime = initResult.startTime || 0,
                endTime = initResult.endTime || 0

            Vue.config.devtools = true

            var Celebration = new Vue({
                el: '#j-celebration',
                data: {
                    // loginUrl: path + '/activity/celebrationA/receive',
                    // redPacketsUrl: path + '/activity/dynamicInvoke',
                    redPacketsUrl: path + '/activity/celebrationA/receive',
                    // rewards: '',
                    startTime: formatTime(startTime, 'M月d日'),
                    endTime: formatTime(endTime, 'M月d日'),
                    fullStartTime:  formatTime(startTime, 'yyyy年M月d日hh:mm'),
                    fullEndTime: formatTime(endTime, 'M月d日hh:mm'),
                    logined: initResult.logined,
                    received: initResult.received,
                    notDuringActive: initResult.activityStatus!=4,
                    // isEnd: initResult.activityStatus==6,
                    status: initResult.activityStatus,
                    invokeName:'receiveCelebrationA'
                },
                computed: {
                    rewardsBtnText: function () {
                        var text = ''
                        switch (this.status) {
                            case 2:
                                text = '未开始'
                                break
                            case 4:
                                text = this.received?'已领取':'领取红包'
                                break
                            case 6:
                                text = '已结束'
                                break
                            default:
                        }
                        return text
                    }
                },
                methods: {
                    getRewards: function () {

                        if (this.status === 2) {

                            // $.xDialog({content: '活动暂未开始,敬请期待'})
                            return false
                        } else if (this.status === 6) {

                            // $.xDialog({content: '活动已结束'})
                            return false
                        }

                        if (this.logined) {

                            if (!this.received) {
                                this.getRewardsData()
                            }
                            else {
                                // $.xDialog({content: '您今日已领取过红包'})
                                return false
                            }
                        }
                        else {
                            // location.href = this.loginUrl
                            // 此处访问后端接口前往登陆
                            $.xPost({
                                url: path + '/activity/celebrationA/receive'
                            })
                        }
                    },
                    getRewardsData: function () {
                        var self = this
                        $.xPost({
                            url: self.redPacketsUrl,
                            // data: {
                            //     invokeMethod: self.invokeName
                            // },
                            callback: function (data) {
                                self.dataHandle(data, self.invokeName)
                            }
                        })
                    },
                    dataHandle: function (data, eventName) {

                        if (eventName === this.invokeName) {

                            if (data.success && data.result) {

                                // Celebration.rewards = data.result.rewards || ''

                                $.xDialog({
                                    type: 'success',
                                    content: '恭喜您，获得'+ data.result.popularityVaule +'元现金券一张'
                                })
                                this.received = true
                            } else {

                                $.xDialog({content: data.resultCodes[0].msg})
                            }
                        }
                    }
                }
            })
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

})()