define([ 'base', 'vue'], function (require, exports, module) {
    'use strict'
    var base = require('base'),
        $rechargeBtn = $('#j-recharge-btn');

            //限制只能输入金额
            // var $rechargeInput = $('#j-recharge-num');
            // $rechargeInput.on("blur", function () {
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
            //
            // //充值操作
            // $rechargeBtn.on('click', function () {
            //
            //     if (!$rechargeInput.data('validate')) {
            //         base.xTips({content: '请输入有效金额'});
            //         return false;
            //     }
            //
            //     if ($(this).hasClass('z-disabled')) {
            //         return false;
            //     }
            //
            //     $(this).attr('disabled', 'disabled').addClass('z-disabled');
            //
            //     console.log($('#j-recharge-num').val())
            //     var amount=$('#j-recharge-num').val()
            //     base.getAPI({
            //         url:environment.globalPath +'/security/banlance/recharge',
            //         version:'1.7.0',
            //         data:{
            //             amount:amount,
            //             rechargeType:'1'
            //         },
            //         callback:function (data) {
            //             if(data.success){
            //                 $('#j-submitAmount').html(data.result.result)
            //             }
            //         }
            //     })
            //
            // });
    
    new Vue({
        el:'#recharge',
        data:{
            amount:''
        },
        created:function () {
            
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
            limitSubmit:function (type,orderId) {
                var _this=this
                 _this.amount=$('#j-recharge-num').val()
                
                _this.limitInput('#j-recharge-num')
                if (!$('#j-recharge-num').data('validate')) {
                    base.xTips({content: '请输入有效金额'});
                    return false;
                }
                if ($(this).hasClass('z-disabled')) {
                    return false;
                }

                $(this).attr('disabled', 'disabled').addClass('z-disabled');

                base.getAPI({
                    url:environment.globalPath +'/security/banlance/recharge',
                    version:'1.7.0',
                    data:{
                        amount:_this.amount,
                        rechargeType:type,
                        outAdvanceNo:orderId
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
                $('#j-recharge-num').val('')
            }
        }
    })
});