/**
 * Created by lyl on 2016/6/24.
 */
define(['base', 'vue','zepto'], function (require, exports, module) {
    'use strict'
    var base = require('base');
    Vue.filter('formatName', function (value) {
        return value.split('期')[0]
    });
    Vue.filter('dateFormat', function (date, format) {

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
    });
    Vue.filter('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return commafy(amount);
                break;
        }

        // 用于给数字添加千分号
        function commafy(num) {
            num = num + "";
            var re = /(-?\d+)(\d{3})/;
            while (re.test(num)) {
                num = num.replace(re, "$1,$2");
            }
            return num;
        }
    });
    var investment=new Vue({
        el:'#j-memberHome',
        data:{
            memberData:{},
            unableBalance:'',
            totalReceivedInterest:'',
            investTotal:'',
            evaluationName: '',
            showSpecial: false,
            showTips: true,
            token: $('#xToken').val(),
            OverduePopularity: {
                year: '',
                month: ''
            }
        },
        created:function(){
            var self=this;
            base.getAPI({
                url:environment.globalPath+'/security/banlance/queryMemberBalance',
                version:'1.3.0',
                callback:function(data){
                    if(data.success){
                        self.memberData=data.result;
                        self.unableBalance=((data.result.balance-0)-(data.result.availableBalance-0)).toFixed(2)
                        self.investTotal=(data.result.investTotal).toFixed(2)
                        self.totalReceivedInterest=((data.result.receivableInterest-0)+(data.result.receivedInterest-0)).toFixed(2)
                    }else{
                        base.xTips(data.resultCodes[0].msg)
                    }
                }
            })
            base.getAPI({
                url:environment.globalPath+'/security/evalua/getEvalua',
                version:'1.5.0',
                callback:function(data){
                    if(data.success){
                        self.evaluationName=data.result.evaluationName
                    }else{
                        base.xTips(data.resultCodes[0].msg)
                    }
                }
            })

            base.getAPI({
                url: environment.globalPath + '/security/coupon/queryMemberOverduePopularity',
                version: '1.0.0',
                callback: function (data) {
                    if (data.success) {
                        self.OverduePopularity = data.result
                        self.OverduePopularity.year = data.result.year
                        self.OverduePopularity.month = data.result.month
                    } else {
                        base.xTips(data.resultCodes[0].msg)
                    }
                }
            })
            if (this.getCookie('isMShowed_memberId_01_' + self.token)) {
                this.showTips = false
            }


        },
        ready: function () {
            var self = this

        },
        methods:{
            showTipBlock:function (id) {
                $(id).show()
                base.cover.show(null)
            },
            closeTipBlock:function (id) {
                $(id).hide()
                base.cover.hide()
            },
            getCookie: function (name) {
                var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
                if (arr = document.cookie.match(reg)) return unescape(arr[2]);
                else return null;
            },
            setCookie: function (name, value, time) {
                var self = this;
                var strsec = self.getsec(time);
                var exp = new Date();
                exp.setTime(exp.getTime() + strsec * 1);
                document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
            },
            getsec: function (str) {
                var str1 = str.substring(1, str.length) * 1;
                var str2 = str.substring(0, 1);
                if (str2 == "s") {
                    return str1 * 1000;
                } else if (str2 == "h") {
                    return str1 * 60 * 60 * 1000;
                } else if (str2 == "d") {
                    return str1 * 24 * 60 * 60 * 1000;
                }
            },
            tipBtnClick: function (type) {
                var self = this
                this.setCookie("isMShowed_memberId_01_" + self.token, self.token, "d31");
                if (type == 1) {
                    window.location.href = environment.globalPath + '/mCenter/reputationPage'
                } else {
                    self.showTips = false
                }
            }
        }
    })
})