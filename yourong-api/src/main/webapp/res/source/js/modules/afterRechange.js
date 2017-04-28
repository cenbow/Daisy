/**
 * Created by lyl on 2016/8/10.
 */
define([ 'base', 'vue'], function (require, exports, module) {
    'use strict'
    var base = require('base')
    Vue.filter('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return splitAmount(amount);
                break;
        }
        // 用于给数字添加千分号
        function splitAmount(num) {
            num = num + "";
            var re = /(-?\d+)(\d{3})/;
            while (re.test(num)) {
                num = num.replace(re, "$1,$2");
            }
            return num;
        }
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
    new Vue({
        el:'#afterRechange',
        data:{
            tradeNo:$('#j-tradeNo').attr('tradeNo'),
            rechangeData:{}
        },
        created:function () {
            var _this=this
            base.getAPI({
                url:environment.globalPath+'/security/banlance/queryRecharge',
                data:{
                    tradeNo:_this.tradeNo
                },
                version:'1.7.0',
                callback:function (data) {
                    if(data.success){
                       _this.rechangeData=data.result

                    }else{
                        base.xTips({content:data.resultCodes[0].msg})
                    }
                }
            })
        },
        methods:{
            reloadBtn:function () {
                location.reload()
            }
        }
    })
})