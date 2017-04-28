/**
 * 提现
 */
define(['base', 'vue'], function (require, exports, module) {
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
            // //限制只能输入金额
            // var $withdrawInput = $('#j-withdraw-num');
            //
            // $withdrawInput.on("blur", function () {
            //     var amount = Number($(this).val()),
            //         amountVal = $(this).val(),
            //         isError = false,
            //         errTips = "金额为整数或小数，小数点不超过2位",
            //         point = -1;
            //     if (amountVal.indexOf(".") !== -1) {
            //         point = amountVal.length - amountVal.indexOf(".") - 1;
            //     }
            //     if (!isNaN(amount)) {
            //         if (amount === 0) {
            //             isError = true;
            //         } else if (amount < 0.01) {
            //             isError = true;
            //         } else if (point > 2 || point === 0) {
            //             isError = true;
            //         } else if (amount > 100000000) {
            //             isError = true;
            //             errTips = "您输入的金额超限";
            //         }
            //     } else {
            //         isError = true;
            //         errTips = "请输入有效金额";
            //     }
            //     if (isError) {
            //         base.xTips({content: errTips});
            //         $(this).data('validate', false);
            //     } else {
            //         $(this).data('validate', true);
            //     }
            // });
            // //提现操作
            // $('#j-withdraw-btn').on('click', function () {
            //
            //     if (!$withdrawInput.data('validate')) {
            //         base.xTips({content: '请输入有效金额'});
            //         return false;
            //     }
            //
            //     $(this).attr('disabled', 'disabled').addClass('z-disabled');
            //     console.log($('#j-withdraw-num').val())
            //     var amount=$('#j-withdraw-num').val()
            //     base.getAPI({
            //         url:environment.globalPath +'/security/banlance/withdraw',
            //         version:'1.7.0',
            //         data:{
            //             withdrawAmount:amount
            //         },
            //         callback:function (data) {
            //             if(data.success){
            //                 console.log(data.result.result)
            //                 //$('#j-submitAmount').html(data.result.result)
            //             }else{
            //                 base.xTips({content:data.resultCodes[0].msg})
            //             }
            //         }
            //     })
            // });

    setTimeout(function(){
        //到帐日期显示
        var curDate = new Date(+environment.serverDate),
                curHours = curDate.getHours(),
                curDay = curDate.getDate(),
                dueDate = curDate,
                splitDay = 1

        if(curHours>=15){
            splitDay = 2
        }
        dueDate = new Date(curDate.setDate(curDay+splitDay))
        log(curDate,curHours)
        $('.j-arrivalTime').text((dueDate.getMonth()+1)+'月'+dueDate.getDate()+'日')
    },300)

    new Vue({
        el:'#withdraw',
        data:{
            balanceAmount:0,
            withdrawAmount:''
        },
        created:function () {
            var _this=this
            base.getAPI({
                url:environment.globalPath+'/security/banlance/queryMemberBalance',
                version:'1.3.0',
                callback:function (data) {
                    if(data.success){
                        _this.balanceAmount=data.result.availableBalance
                    }

                }
            })
        },
        watch:{
            'withdrawAmount':function (n, o) {
                this.withdrawAmount=$('#j-withdraw-num').val()
            }
        },
        methods:{
            limitInput:function (id) {
                var amount = Number($(id).val()),
                    amountVal = $(id).val(),
                    isError = false,
                    errTips = "金额为整数或小数，小数点不超过2位",
                    point = -1;
                if (amountVal.indexOf(".") !== -1) {
                    point = amountVal.length - amountVal.indexOf(".") - 1;
                }
                if (!isNaN(amount)) {
                    if (amount === 0) {
                        isError = true;
                    } else if (amount < 0.01) {
                        isError = true;
                    } else if (point > 2 || point === 0) {
                        isError = true;
                    } else if (amount > 100000000) {
                        isError = true;
                        errTips = "您输入的金额超限";
                    }
                } else {
                    isError = true;
                    errTips = "请输入有效金额";
                }
                if (isError) {
                    base.xTips({content: errTips});
                    $(id).data('validate', false);
                } else {
                    $(id).data('validate', true);
                }
            },
            //全部提现功能和删除金额功能
            limitSubmit:function () {
                var _this=this

                _this.withdrawAmount=$('#j-withdraw-num').val()

               _this.limitInput('#j-withdraw-num')

                if (!$('#j-withdraw-num').data('validate')) {
                    base.xTips({content: '请输入有效金额'});
                    return false;
                }
                $(this).attr('disabled', 'disabled').addClass('z-disabled');
               
                if(_this.withdrawAmount==''){
                    base.xTips({content: '请输入有效金额'});
                    return false;
                }
                base.getAPI({
                    url:environment.globalPath +'/security/banlance/withdraw',
                    version:'1.7.0',
                    data:{
                        withdrawAmount:_this.withdrawAmount
                    },
                    callback:function (data) {
                        if(data.success){
                            
                             window.location.href=data.result.resultUrl
                        }else{
                            base.xTips({content:data.resultCodes[0].msg})
                        }
                    }
                })
            },
            deleteAll:function () {
                $('#j-withdraw-num').val('')

            },
            withdrawAll:function () {
                $('#j-withdraw-num').val(this.balanceAmount)
            }
        }
    })
});