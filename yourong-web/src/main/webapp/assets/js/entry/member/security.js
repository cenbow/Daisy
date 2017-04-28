import 'common/member'
import { Util } from 'common/util'
import { Dialog } from 'module/cube'

/*****************
 * 我的资料页面
 *****************/

//邮箱开通绑定
$("#j-email-verify").on("click", function() {
    //var sendEmailIsShow = $(".u-email-send").css("display");
    if (!$(".u-email-send:visible").length) {
        $(".u-security-email").fadeIn();
    }
    return false;
})
$("#j-email-btn").on("click", function() {
    var val = $("#j-email-input").val(),
        reg = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
    var bindedEmail = $("#j-member-email").html();
    if (typeof val !== "undefined" && val.length !== 0) {
        if (reg.test(val)) {
            if (bindedEmail == val) {
                Dialog.show({
                    content: "该邮箱已绑定！"
                });
                return;
            }
            $("#j-email-btn").addClass("z-disabled").attr("disabled", true).html("发送中");
            $("#email").html(val);
            var aIndex = val.indexOf("@");
            var mailUrl = "http://mail." + val.substring(aIndex + 1);
            $("#j-goto-email").attr("href", mailUrl);
            var xToken = $("#xToken").attr("value"),
                data = {
                    'email': val,
                    'xToken': xToken
                };
            Util.post({
                url: environment.globalPath + "/member/to/bind/email",
                data: data,
                callback: function(data) {
                    $("#j-email-btn").removeClass("z-disabled").removeAttr("disabled", true).html("确定");
                    var result = data.success;
                    if (!result) {
                        Dialog.show({
                            content: data.resultCodeEum[0].msg
                        });

                    } else {
                        $(".u-security-email").hide();
                        $(".u-email-send").fadeIn();
                    }
                }
            });
        } else {
            Dialog.show({
                content: "请输入正确的邮箱地址"
            });
        }
    } else {
        Dialog.show({
            content: "请输入邮箱"
        });
    }
})
$("#j-email-cancel").on("click", function() {
    $(".u-security-email").hide()
});

//邮件再次发送
$("#j-resend-email").on("click", function() {
    var resendStatus = $(this).html();
    if (resendStatus == "发送中") {
        return false;
    }
    $(this).html("发送中");
    var val = $("#j-email-input").val();
    var xToken = $("#xToken").attr("value"),
        data = {
            'email': val,
            'xToken': xToken
        };
    Util.post({
        url: environment.globalPath + "/member/to/bind/email",
        data: data,
        callback: function(data) {
            //邮箱已绑定，再次发送邮件时提示邮箱已绑定
            var result = data.success;
            if (!result) {
                Dialog.show({
                    content: data.resultCodeEum[0].msg
                });

            } else {
                Dialog.show({
                    type: 'success',
                    title: '提示',
                    content: '发送成功！',
                    callback: function() {
                        $("#j-resend-email").html("没收到 再次发送");
                    }
                });
            }
        }
    });
    return false;
})