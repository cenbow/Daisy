// 日期 filter
Vue.filter('formatTime', function (date, format) {
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
})

//数字转换成千分号方法
Vue.filter('amountSplit', function (num, units, needZero) {
    var unit = units || ''
    if (!num) {
        if(needZero){
            return unit + 0
        } else {
            return ''
        }
    }
    var parts = num.toString().split(".")
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",")
    return unit + parts.join(".")
})

//头像路径
Vue.filter('avatarUrl',{
    read: function (url) {
        return url?environment.aliyunPath+url:
        environment.globalPath+'/res/img/member/avatar.png'
    }
})

//正整数 positive integer
Vue.filter('pInt',{
    write: function (val) {
        var value = +val.replace(/\D/g, '')
        return value ? value : ''
    },
    read: function (val) {
        if (typeof(val) !== 'undefined') {
            return (val + '').replace(/\D/g, '')
        } else {
            return ''
        }
    }
})

//正整数和零
Vue.filter('int',{
    write: function (val) {
        var value = val.replace(/\D/g, '')
        if (value) {
            if (value === '0') {
                return 0
            } else {
                return +value
            }
        } else {
            return ''
        }
    },
    read: function (val) {
        if (typeof(val) !== 'undefined') {
            return (val + '').replace(/\D/g, '')
        } else {
            return ''
        }
    }
})

//倒计时
Vue.component('countdown', {
    template: '<span>{{timeText}}</span>',
    props: ['time', 'text', 'circulate', 'callback'],
    data: function () {
        return {timeText: ''}
    },
    created: function () {
        this.countdown()
    },
    methods: {
        countdown: function () {
            var time = +this.time,
                self = this
            console.log(time)
            self.timeText = self.getFromatTime(time)
            if (self.circulate) {
                var timer = setInterval(function () {
                    if (time > 0) {
                        time--
                        self.timeText = self.getFromatTime(time)
                    } else {
                        window.location.reload()
                    }
                }, 1E3)
            } else {
                var timer = setInterval(function () {
                    if (time > 1) {
                        time--
                        self.timeText = self.getFromatTime(time)
                    } else {
                        clearInterval(timer)
                        self.timeText = self.text
                    }
                }, 1E3)
            }
        },
        getFromatTime: function (time) {
            var text = '',
                hour = this.zeroFill(parseInt(time / 3600)),
                min = this.zeroFill(parseInt(time % 3600 / 60)),
                sec = this.zeroFill(parseInt(time % 60))
            switch (true) {
                case time / 3600 >= 1:
                    text = hour + ':' + min + ':' + sec
                    break
                case time / 3600 < 1 && time / 60 >= 1:
                    text = '00:' + min + ':' + sec
                    break
                case time / 60 < 1:
                    text = '00:00:' + sec
                    break
            }
            return text
        },
        zeroFill: function (num) {
            return +num < 10 ? '0' + num : num
        }
    }
})