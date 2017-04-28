(function () {
    Array.prototype.last = function () {
        return this[this.length - 1]
    }

    var hook = new AppHook(env.platform)
    var vm = new Vue({
        el: '#app',
        data: {
            status: -1,
            init: {},
            giftData: {},
            curTime: env.time,
            showContractTips: false,
            index: 0,
            title: window.encodeURI('快投有奖规则'),
            isShow: true
        },
        computed: {
            pid: function () {
                return location.href.match(/[0-9]{6,15}/)[0]
            },
            url: function () {
                return 'yrw-skip://invokeMethod=chooseMoney&projectID=' + this.pid
            },
            // 是否显示黄条
            showYellowBar: function () {
                var self = this
                var flag = true
                if (self.curTime - self.init.quickRewardConfig.endDate > 0 || self.init.quickRewardConfig.popularity < 0 || !self.init.quickRewardConfig.flag) {
                    flag = false
                }
                return flag
            }
        },
        created: function () {
            var initData = $('#j-data').attr('data') || '{}'
            var data = JSON.parse(initData)
            this.status = data.flag
            this.init = data
        },
        ready: function () {

        },
        methods: {
            closeTip: function () {
                this.isShow = false
            },
            getDate: function (val) {
                var date = new Date(+val).toJSON().split(/[-TZ]/)
                return date[1] + '月' + date[2] + '日'
            },
            toggleContractTips: function (shown) {
                this.showContractTips = shown
            },
            getRewardName: function (val) {
                var zhNum = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
                return zhNum[val - 1] + '等奖'
            },
            getAwardList: function (list) {
                var arr = [],
                    self = this,
                    awardsList = list.slice(0, 2)

                awardsList.forEach(function (item) {
                    var size = item.number > 1 ? 'x' + item.number : ''
                    arr.push(self.getRewardName(item.level) + size)
                })

                var awards = arr.join('、')

                if (list.length > 2) {
                    awards += '等'
                }
                return awards
            },
            toAndroidProjectList: function () {
                var self = this
                Android.ToActivity('chooseMoney', self.pid)
            },
            showGiftDetail: function () {
                this.showDetail = false
            },
            closeDetailBlick: function () {
                this.showDetail = true
            }
        },
        filters: {
            rewardName: {
                read: function (val) {
                    var zhNum = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
                    return zhNum[val - 1] + '等奖'
                }
            }
        }
    })

    if (env.debug) {
        window.$vm = vm
    }
})()