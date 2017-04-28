//实名认证
$(function() {
    //禁止输入空格
    $("#j-trueName-blank").on('keydown',function(){
        if(event.keyCode===32){
            return false;
        }
    });
    $("#memberIdentity_form_button").on("click", function() {
        if (formValid.check(false)) {
            $(this).addClass("z-disabled").attr("disabled", true).val("开通中");
            var data = $("#memberIdentity_form").serializeArray();
            $.xPost({
                url: environment.globalPath + "/member/authIdentity",
                data: data,
                callback: function(data) {
                    var result = data.result;
                    $("#memberIdentity_form_button").removeClass("z-disabled").removeAttr("disabled").val("确认开通");
                    if (data.success) {
                        window.location.href = environment.globalPath + "/member/registerSucess";
                    } else {
                        showErrorMessage(data);
                    }
                }
            });
        } else {
            return false;
        }
    });
    //服务协议阻止提交
    $("#j-pay-agree").change(function() {
        var checked = $(this).is(":checked"),
            btn = $("#memberIdentity_form_button");
        if (!checked) {
            btn.addClass("z-disabled").attr("disabled", "disabled");
        } else {
            btn.removeClass("z-disabled").removeAttr("disabled");
        }
    });

    //新浪支付服务使用协议的初始化
    $("#j-sinapay-argreement-link").xArgreement();

});
