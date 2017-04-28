/**
 * Created by lyl on 2016/6/22.
 */
/*global define,require,$,environment,alert,confirm,console*/

define(['base', 'vue'], function (require, exports, module) {
    'use strict'
    var base = require('base');
    Vue.filter('formatName', function (value) {
        return value.split('期')[0]
    });
    Vue.filter('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return splitAmount(amount);
                break;
        }
        // //金额格式化
        // function splitAmount(amount) {
        //
        //     if (amount >= 1000) {
        //
        //         var amountStr = amount.toString(),
        //             size = parseInt(amountStr.length / 3),
        //             amountArray = amountStr.split('').reverse();
        //
        //         for (var i = 1; i <= size; i++) {
        //             var j = i * 3 - 1;
        //             if (j !== amountArray.length - 1) {
        //                 amountArray[j] = ',' + amountArray[j];
        //             }
        //         }
        //
        //         return amountArray.reverse().join('');
        //
        //     } else {
        //         return amount;
        //     }
        // }
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
    var orderId = $('#j-orderId').attr('orderId');
    var investList=new Vue({
        el: '#j-investDetailStatus',
        data: {
            investDetailData:{},
            myAmount:'',
            myAnnualizedRate:'',
            paySucessCount: 3,
            paySuccess:true
        },
        created:function(){
            var n = 2, self = this
            // base.cover.show(null)
            // var timer = setInterval(function () {
            //     if (n > 0) {
            //         self.paySucessCount = n
            //         n--
            //     } else {
            //         clearInterval(timer)
            //         self.paySucessCount = n
            //         self.paySuccess=false
            //         base.cover.hide()
            //     }
            // }, 1000)
            base.getAPI({
                url:environment.globalPath+'/security/order/afterInvest',
                data:{orderId:orderId},
                version:'1.0.0',
                callback:function(data){
                    if(data.success){
                        self.investDetailData=data.result;
                        var  myAmount=(data.result.order.investAmount-0)-(data.result.order.usedCouponAmount-0),
                            myAnnualizedRate=((data.result.order.annualizedRate-0)+(data.result.order.extraAnnualizedRate-0)).toFixed(2);
                        self.myAnnualizedRate=myAnnualizedRate
                        self.myAmount=myAmount
                    }
                }
            })
        },
        methods:{
            refresh:function(){
                location.reload()
            }
        }
    })
})