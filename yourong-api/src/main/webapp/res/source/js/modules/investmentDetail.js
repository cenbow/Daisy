/**
 * Created by lyl on 2016/6/23.
 */
define(['base', 'vue','zepto'], function (require, exports, module) {
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
var transactionId=$('.j-transactionId').attr('transactionId')
    //债权协议
    // var $preview=$('#j-previewForm'),
    //     $previewBtn=$('.j-contract-btn');
    // $previewBtn.on('click', function () {
    //    
    // });
    var investment=new Vue({
        el:'#j-investmentDetail',
        data:{
            investmentDetailData:{},
            myAnnualizedRate:"",
            tipsUrl:environment.globalPath+'/mstation/tips?from='+window.location.href
        },
        created:function(){
            var self=this;
            base.getAPI({
                url:environment.globalPath+'/security/transaction/queryTransactionDetail',
                data:{transactionId:transactionId},
                version:'1.0.0',
                callback:function(data){
                    if(data.success){
                        self.investmentDetailData=data.result;
                        var myAnnualizedRate=((data.result.annualizedRate-0)+(data.result.extraAnnualizedRate-0)).toFixed(2);
                        self.myAnnualizedRate=myAnnualizedRate;
                    }
                }
            })
        },
        methods:{
            requestProtocol:function(){
                var self=this,
                    url=$('.j-pviewUrl').data('url'),
                    xToken=$('#xToken').val();
                $.ajax({
                    url:url,
                    type:'POST',
                    data:{
                        orderId:self.investmentDetailData.orderId,
                        xToken:xToken},
                    success: function (data) {
                        $('#j-contract').html(data).append('<div class="u-contract-back" id="j-goback">返回</div>').show();
                        $('#j-investmentDetail').hide()
                        $('#j-goback').on('click', function () {
                            // $('#j-contract').hide();
                            // $('#j-investmentDetail').show()
                            location.reload()
                        });
                    }
                });
            }
            // agreementText:function () {
            //     var data=''
            //     $('#j-contract').html(data).append('<div class="u-contract-back" id="j-goback">返回</div>').show();
            //     $('#j-investmentDetail').hide()
            //     $('#j-goback').on('click', function () {
            //         // $('#j-contract').hide();
            //         // $('#j-investmentDetail').show()
            //         location.reload()
            //     });
            // }
        }
    })
})