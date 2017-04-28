
define(['zepto','base','template'],function (require, exports, module) {
    var template = require('template'),
        base = require('base'),
        os=$(".j-contract-transactionId").data("os"),
        i=$(".j-contract-transactionId").data("transactionid");
    if(os!==3) {
        var hook = new AppHook(os);
    }

//    直投项目


    $(".j-sign").on('click',function(){
        signContract($(this))
    })
    var signContract=function ($event) {

        //if (self.os - 0 !== 3) {
        if(os!==3){
            //安卓
            hook.getEvent('signContract&isNeedRealName=0&args_transactionId_1_string_'+i+'&args_source_2_integer_'+os,$event)

        } else {
            base.getAPI({
                url:environment.globalPath+'/security/transaction/signContract',
                version:'1.6.0',
                data:{transactionId:i},
                callback: function(data){
                    //成功-跳转链接
                    if(data.success) {
                        window.location.href=data.result
                        //$(".j-sign").attr("href",data.result)
                    }else{
                        $(".j-error-tips p").text(data.resultCodes[0].msg)
                        $(".j-error-tips").removeClass("f-dn")
                        $(".u-shade").removeClass("f-dn")
                    }
                }
            });
        }

    }

//接收交互回调的方法，eventName指的是触发的事件名
    window.hookCallback = function (data, eventName) {

        if(data.success) {

            window.location.href=data.result
            //$(".j-sign").attr("href",data.result)
        }else{
            //失败-提示文案
            $(".j-error-tips p").text(data.resultCodes[0].msg)
            $(".j-error-tips").removeClass("f-dn")
            $(".u-shade").removeClass("f-dn")

        }
    }
})

$(".j-download-btn").on('click',function(){
    $(".j-contract-tips").removeClass('f-dn')
})
$(".j-contract-tips").on('click','em',function(){
    $(".j-contract-tips").addClass('f-dn')
})
$(".j-error-tips i").on('click',function(){
    $(".j-error-tips").addClass("f-dn")
    $(".u-shade").addClass("f-dn")
})
