/**
 * @entry 注册
 */

import 'module/jquery.extend'

//CONFIG
let CONFIG = {
    url: {
        //核对手机号是否已注册
        telUrl2: environment.globalPath + "/security/checkMember?type=1",
        //图形验证码验证
        imgUrl: environment.globalPath + "/security/checkCode",
        //短信语音验证请求
        telUrl: environment.globalPath + "/security/sendMobileCode",
        //核对手机验证码
        checkUrl: environment.globalPath + "/security/checkMobileCode"
    },
    mobileCount: 6,
    moblieTime: 60,
    tips: {
        imgCodeError: "请输入正确的图形验证码",
        requestLimitError: "抱歉，您今天的验证码发送次数已超过限制！请明日再试。",
        cpnNotCorrect: "请输入正确的手机号码",
        cpnExistedError: "手机号码已被注册",
        cpnNotExistedError: "手机号码未注册",
        argeeError: "请先阅读并同意《有融网服务协议》。",
        checkCodeError: "请输入正确的手机验证码"
    }
}

//Get xToken
let xToken = $("#xToken").val()

//注册协议的初始化
$("#j-register-argreement-link").xArgreement()

//右侧Banner
$("#j-register-banner").fadeShow({
    time: 5000,
    type: "adv"
})

let $cpn=$("#j-cpn")
//1.手机号码验证
$cpn.on("keypress", function (e) {
    $(this).data("prevValue", $(this).val());

}).on("keyup", function (e) {
    var val = $(this).val(),
        prevValue = $(this).data("prevValue");
    if (val.indexOf(" ") !== -1) {
        val = val.replace(" ", "");
        $(this).val(val);
        return false;
    }
    if (val !== prevValue) {
        $(this).checkMobile({
            "handler": 1,
            "refer": 0,
            "event": e.type
        });
    }

}).on("blur", function () {
    var tipsText = "请输入正确的手机号码",
        len = $(this).val().length;
    if (len < 11) {
        if (len === 0) {
            tipsText = "请输入手机号码";
        }
        $(this).data("isCorrect", false).next(".m-val-tips").validTips("error", tipsText);
    }
})

let $refercode=$("#j-refercode")
///推荐人手机号码验证
$refercode.on("keyup postpaste", function (e) {
    var _this = $(this);
    if (_this.val() === "") {
        _this.next(".m-val-tips").empty();
    } else {
        _this.checkMobile({
            "handler": 1,
            "refer": 1,
            "event": e.type
        });
    }

}).on("blur", function () {
    if ($(this).val() !== "" && $(this).val().length < 11) {
        $(this).next(".m-val-tips").validTips("error", "请输入正确的手机号码");
    }
})

//推荐人粘贴触发校验
$refercode.on("postpaste", function () {}).pasteEvents()

let $vercode=$("#j-vercode")
///图形码输入框聚焦时再次验证手机号码是否存在
$vercode.on("focus", function () {
    var cpn = $("#j-cpn"),
        isCorrect = cpn.data("isCorrect");
    if (cpn.val().length === 0 || !isCorrect) {
        cpn.focus();
        cpn.data({
            "isCorrect": false
        });
    }
})

let $inputVercode=$('#j-input-vercode'),
    $regForm=$("#j-reg-form")
//2.验证图形码并发送手机验证码
$vercode.on("keyup", function () {
    var _this = $(this),
        val = $(this).val();
    if (val.length === 4) {
        var cpn = $("#j-cpn").val();
        $(this).parent().find(".m-val-tips").empty();

        if (cpn.length === 11 && $("#j-cpn").data("isCorrect") === true) {
            $.xPost({
                url: CONFIG.url.imgUrl,
                data: {
                    "code": val,
                    "mobile": cpn,
                    "xToken": xToken
                },
                callback: function (data) {
                    var status = Number(data.resultCodeEum[0].code),
                        count = Number(data.result);
                    if (status === 1) {
                        //图形码验证通过
                        _this.parents(".m-input-vercode").find(".m-val-tips").html("");
                        $("#j-pngcode").val(val);
                        switch (count) {
                            case 0:
                                //第一次发短信
                                getVerification("1", cpn);
                                $("#j-cpn").addClass("z-disabled").attr("readonly", "readonly");
                                break;
                            case CONFIG.mobileCount:
                                //超过六次限制发送
                                limitCodeRequest();
                                break;
                            default:
                                //默认出现语音和短信选择
                                chooseVoice();
                        }
                    } else {
                        //图形码验证失败
                        _this.parents(".m-input-vercode").find(".m-val-tips").validTips("error", CONFIG.tips.imgCodeError);
                    }
                }
            });
        }
    } else if (val.length > 4) {
        $(this).val(val.substr(0, 4));
    }
})

//是否语音选择
$inputVercode.on("click", ".j-choose-voice", {}, function () {
    chooseVoice();
    $(".j-phonecode").empty();
})

$inputVercode.on("click", ".u-tips-vercode", {}, function () {
    var cpn = Number($("#j-cpn").val()),
        type = "1";
    if ($(this).hasClass("j-voice-yes")) {
        type = "2";
        $("#j-cpn").checkMobile({
            "handler": 2,
            "type": type,
            "event": "click"
        });
    } else if ($(this).hasClass("j-voice-no")) {
        type = "1";
        $("#j-cpn").checkMobile({
            "handler": 2,
            "type": type,
            "event": "click"
        });
    } else {
        return false;
    }
    $("#j-phonecode").data("checkType", type);
    $("#j-checkType").val(type);
})

//只验证图形码
$regForm.on("keyup", "#j-vercode2", function () {
    var _this = $(this),
        val = $(this).val(),
        cpn = $("#j-cpn").val();
    if (val.length === 4) {
        $.xPost({
            url: CONFIG.url.imgUrl,
            data: {
                "code": val,
                "mobile": cpn,
                "xToken": xToken
            },
            callback: function (data) {
                var status = Number(data.resultCodeEum[0].code);
                if (status === 1) {
                    //图形码验证通过
                    _this.parent().siblings(".m-val-tips").validTips("success");
                    $("#j-pngcode").val(val);
                } else {
                    //图形码验证失败
                    _this.parent().siblings(".m-val-tips").validTips("error", CONFIG.tips.imgCodeError);
                    $("#j-pngcode").val(val);
                }
            }
        });
    } else if (val.length > 4) {
        $(this).val(val.substr(0, 4));
    }
})

//验证码点击刷新
$inputVercode.on("click", ".u-img-vercode", function () {
    var timestamp = new Date().getTime();
    $(this).attr("src", environment.globalPath + "/security/validateCode" + "?t=" + timestamp);
    $("#j-vercode2").empty();
})

//3.核查手机验证码
$regForm.on("keyup", "#j-phonecode", {}, function () {
    var val = $(this).val(),
        cpn = $("#j-cpn").val(),
        _this = $(this),
        type = "1",
        tips = $(this).parent().find(".m-val-tips");
    if (tips.length === 0) {
        $(this).parent().append('<span class="m-val-tips">&nbsp;</span>');
    }
    var checkType = $(this).data("checkType");
    if (typeof checkType !== "undefined") {
        type = checkType;
    }
    if (val.length === 4) {

        if (!isNaN(Number(val))) {
            $.xPost({
                url: CONFIG.url.checkUrl,
                data: {
                    "type": type,
                    "code": val,
                    "mobile": cpn,
                    "xToken": xToken
                },
                callback: function (data) {
                    var status = Number(data.resultCodeEum[0].code);
                    if (status === 1) {
                        tips.validTips("success");
                        $("#j-mobile").show();
                        _this.data("isCorrect", true);
                    } else {
                        tips.validTips("error", CONFIG.tips.checkCodeError);
                        _this.data("isCorrect", false);
                    }
                }
            });
        } else {
            tips.validTips("error", "验证码为数字");
        }
    } else if (val.length > 4) {
        $(this).val(val.substr(0, 4));
    }
})

//4.表单提交
$regForm.submit(function () {
    var _this = $(this),
        url = _this.data("action"),
        data = _this.serializeArray(),
        page = _this.data("page"),
        isXpost = true;
    if (typeof page !== "undefined") {
        switch (page) {
            case "login":
                if (formValid.check(false)) {
                    $("#j-login-submit").val("登录中").addClass("z-disabled").attr("disabled", true);
                } else {
                    return false;
                }
                break;
            case "register":
                var returnVal = validMobile();
                if (returnVal) {
                    if (formValid.check(false)) {
                        $(".j-reg-btn").val("注册中").addClass("z-disabled").attr("disabled", true);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
                break;
            case "lostPwd":
                var returnVal2 = validMobile();
                if (returnVal2) {
                    if (!formValid.check(false)) {
                        return false;
                    } else {
                        isXpost = false;
                        return true;
                    }
                } else {
                    return false;
                }
                break;
            case "updatePwd":
                if (formValid.check(false)) {
                    $(".j-updatepwd-btn").val("修改中").addClass("z-disabled").attr("disabled", true);
                } else {
                    return false;
                }
                break;
            case "changePwd":
                if (formValid.check(false)) {
                    $(".j-changepwd-btn").val("修改中").addClass("z-disabled").attr("disabled", true);
                } else {
                    return false;
                }
                break;
            default:
        }
        if (isXpost) {
            $.xPost({
                url: url,
                data: data,
                callback: function (data) {
                    if (data.success) {
                        var url = environment.globalPath;
                        switch (page) {
                            case "login":
                                $("#j-reg-error").empty().hide();
                                $("#j-login-submit").val("登录").removeClass("z-disabled").removeAttr("disabled");
                                if(data.result.indexOf("http")>=0){
                                    window.location.href = data.result;
                                }else{
                                    window.location.href = url + data.result;
                                }
                                break;
                            case "register":
                                $(".j-reg-btn").val("注册").removeClass("z-disabled").removeAttr("disabled");
                                window.location.href = url + "/member/sinapay?register=true";
                                break;
                            case "updatePwd":
                                $(".j-updatepwd-btn").val("修改密码").removeClass("z-disabled").removeAttr("disabled");
                                var tips = $("#j-repwd-tips");
                                _this.remove();
                                tips.show();
                                var timer = setInterval(function () {
                                    var count = Number($("#j-time-count").text());

                                    if (count !== 0) {
                                        count -= 1;
                                        $("#j-time-count").text(count);
                                    } else {
                                        clearInterval(timer);
                                        window.location.href = url + "/security/login/";
                                    }
                                }, 1000);
                                break;
                            case "changePwd":
                                $(".j-changepwd-btn").val("修改密码").removeClass("z-disabled").removeAttr("disabled");
                                var tips2 = $("#j-repwd-tips");
                                _this.remove();
                                tips2.show();
                                var timer2 = setInterval(function () {
                                    var count = Number($("#j-time-count").text());

                                    if (count !== 0) {
                                        count -= 1;
                                        $("#j-time-count").text(count);
                                    } else {
                                        clearInterval(timer2);
                                        window.location.href = url + "/security/login/";
                                    }
                                }, 1000);
                                break;
                            default:
                                return false;
                        }

                    } else {
                        switch (page) {
                            case "login":
                                $("#j-login-submit").val("登录").removeClass("z-disabled").removeAttr("disabled");

                                var reCode = Number(data.resultCodeEum[0].code),
                                    loginErrorTips = "";
                                if (reCode === 90019) {
                                    $("#j-login-error").fadeToggle().html('您输入的图形验证码有误').fadeIn();
                                } else {
                                    $("#j-login-error").fadeToggle().html('您输入的帐号或密码有误').fadeIn();
                                }
                                switch (reCode) {
                                    case 90019:
                                        loginErrorTips = "您输入的图形验证码有误";
                                        break;
                                    case 90003:
                                        loginErrorTips = "您的账号已注销，如需帮助，请联系客服";
                                        break;
                                    default:
                                        loginErrorTips = "您输入的帐号或密码有误";
                                }

                                $("#j-login-error").fadeToggle().html(loginErrorTips).fadeIn();
                                var imgCode = $("#j-img-vercode2"),
                                    imgUrl = environment.globalPath + "/security/validateCode";
                                if (data.result > 3) {
                                    $("#j-img-vercode2").attr("src", imgUrl);
                                    $("#j-login-submit").before($(".m-input-vercode"));
                                    $(".m-input-vercode").show().find("input").show();
                                }
                                break;
                            case "register":
                                $(".j-reg-btn").val("注册").removeClass("z-disabled").removeAttr("disabled");
                                var reCode2 = Number(data.resultCodeEum[0].code);
                                switch (reCode2) {
                                    case 90009:
                                        $.xDialog({
                                            content: "请输入正确的手机验证码。",
                                            type: "error"
                                        });
                                        break;
                                    case 10001:
                                        $.xDialog({
                                            content: "网络异常，请重新提交。",
                                            type: "warn"
                                        });
                                        break;
                                    case 4:
                                        $.xDialog({
                                            content:"网络异常，请刷新页面。",
                                            type: "warn"
                                        });
                                        break;
                                    default:
                                        $.xDialog({
                                            content: "网络异常，请刷新页面。",
                                            type: "warn"
                                        });
                                }
                                break;
                            case "updatePwd":
                                $(".j-updatepwd-btn").val("修改密码")
                                    .removeClass("z-disabled").removeAttr("disabled");
                                if (data.resultCodeEum[0] !== null) {
                                    $.xDialog({
                                        content: data.resultCodeEum[0].msg,
                                        type: "error"
                                    });
                                }
                                break;
                            case "changePwd":
                                $(".j-changepwd-btn").val("修改密码")
                                    .removeClass("z-disabled").removeAttr("disabled");
                                if (data.resultCodeEum[0] !== null) {
                                    $.xDialog({
                                        content: data.resultCodeEum[0].msg,
                                        type: "error"
                                    });
                                }
                                break;
                            default:
                                return false;
                        }
                    }
                }
            });
        }
        return false;
    }
})

//服务协议未勾选阻止提交
$("#j-agree").change(function () {
    var checked = $(this).is(":checked"),
        btn = $("input[type=submit]");
    if (!checked) {
        btn.addClass("z-disabled").attr("disabled", "disabled");
    } else {
        btn.removeClass("z-disabled").removeAttr("disabled");
    }
})

//ie7~9 模拟的 placeholder 提示
$("#j-phonecode, #j-vercode").on("keyup", function () {
    var tips = $(this).siblings("em");
    if (typeof (tips) !== "undefined" && $(this).val().length > 0) {
        tips.addClass("z-hide");
    } else {
        tips.removeClass("z-hide");
    }
}).on("blur", function () {
    var tips = $(this).siblings("em");
    if (typeof (tips) !== "undefined" && $(this).val() === "") {
        tips.removeClass("z-hide");
    }
})

$(".u-xtips-wrap").on("click", "em", function () {
    $(this).siblings("input").focus();
})

//firefox清除历史输入
if(window.mozPaintCount){
    $("#j-pngcode").val("")
    $cpn.val("")
}

//验证提示
$.fn.validTips = function (type, message) {
    var style = "error";
    if (type === "success") {
        style = "success";
        message = "";
    }
    var html = "<span class='m-val-tips'><i class='u-icon u-icon-" +
        style + " f-icon-s'>&nbsp;</i>" + message + "&nbsp;</span>";
    $(this).replaceWith(html);
}

//校验手机号
$.fn.checkMobile = function (config) {
    var _this = $(this),
        val = _this.val(),
        isExist = false,
        getTrueName = "",
        cpn = "",
        reg = /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/;

    //config init
    if (typeof config !== "undefined") {
        var handler = (typeof (config.handler) !== "undefined") ? config.handler : 1; //默认鼠标事件 2.其他行为
        var type = (typeof (config.type) !== "undefined") ? config.type : 0; //1.短信，2.语音
        var refer = (typeof (config.refer) !== "undefined") ? config.refer : 0; //0.非推荐验证,1.推荐验证
        cpn = (typeof (config.cpn) !== "undefined") ? config.cpn : ""; //手机号
    } else {
        throw new Error("config is not defined");
    }

    if (typeof $("#j-reg-mobile").val() === "undefined" || config.refer === 1) {
        isExist = true;
        getTrueName = "1";
    }

    if (val.length === 11) {
        var tips = _this.next(".m-val-tips");
        if (isNaN(val)) {
            _this.next(".m-val-tips").validTips("error", CONFIG.tips.cpnNotCorrect);
            _this.data({
                "isCorrect": false
            });
            return false;
        }
        if (reg.test(val)) {
            $.xPost({
                url: CONFIG.url.telUrl2,
                data: {
                    "code": val,
                    "type": 1, //1.手机，2.用户名
                    "mobile": cpn,
                    "getTrueName": getTrueName,
                    "xToken": xToken
                },
                callback: function (data) {
                    var code = Number(data.resultCodeEum[0].code),
                        page = $("#j-reg-form").data("page");
                    if (code !== 0) {
                        switch (code) {
                            case 90002:
                                //用户不存在
                                if (!isExist) {
                                    _this.next(".m-val-tips").validTips("success");
                                    _this.data({
                                        "isCorrect": true
                                    });
                                    if (handler === 2) {
                                        getVerification(type, val);
                                    }
                                } else {
                                    var cpnErrorTips = CONFIG.tips.cpnNotExistedError;
                                    if (page !== "lostPwd") {
                                        cpnErrorTips = "推荐人不存在";
                                        $("#j-refercode-val").val("");
                                    }
                                    _this.next(".m-val-tips").validTips("error", cpnErrorTips);
                                    _this.data({
                                        "isCorrect": false
                                    });
                                }
                                break;
                            case 90003:
                                //已注销(账号异常)
                                if (!isExist) {
                                    _this.next(".m-val-tips").validTips("error", "该账号异常，请联系客服");
                                } else {
                                    if (page !== "lostPwd") {
                                        _this.next(".m-val-tips").text("您的推荐人已注销");
                                        $("#j-refercode-val").val("");
                                    } else {
                                        _this.next(".m-val-tips").validTips("error", "该账号异常，请联系客服");
                                    }
                                }
                                _this.data({
                                    "isCorrect": false,
                                    "isCancel": true
                                });
                                break;
                            case 90018:
                                //用户存在
                                if (!isExist) {
                                    _this.next(".m-val-tips").validTips("error", CONFIG.tips.cpnExistedError);
                                    _this.data({
                                        "isCorrect": false
                                    });
                                } else {
                                    _this.data({
                                        "isCorrect": true
                                    });
                                    if (refer !== 1) {
                                        _this.next(".m-val-tips").validTips("success");

                                    } else {
                                        //推荐人验证
                                        var referText = data.result[0],
                                            referCode = data.result[1];

                                        if (referText !== null && referText.length > 1) {
                                            _this.next(".m-val-tips").text("您的推荐人:" + referText);
                                        } else {
                                            _this.next(".m-val-tips").validTips("success", "");
                                        }
                                        $("#j-refercode-val").val(referCode);
                                    }
                                    if (handler === 2) {
                                        getVerification(type, val);
                                    }
                                }
                                break;
                            default:
                                return false;
                        }
                    }
                }
            });
        } else {
            _this.next(".m-val-tips").validTips("error", CONFIG.tips.cpnNotCorrect);
            _this.data({
                "isCorrect": false
            });
        }
    } else if (val.length > 11) {
        $(this).val(val.substr(0, 11));
    } else if (!Number(val)) {
        $(this).val(val.replace(/[^\d]/g, ""));
    }
}

//请求手机验证
function getVerification(type, cpn) {
    var isCheckMobile = 2;
    if (typeof $("#j-reg-mobile").val() !== "undefined") {
        isCheckMobile = 1;
    }
    $.xPost({
        url: CONFIG.url.telUrl,
        data: {
            "type": type,
            "mobile": cpn,
            "isCheckMobile": isCheckMobile,
            "xToken": xToken
        },
        callback: function (data) {
            var status = Number(data.result);
            $(".j-voice-yes, .j-voice-no").removeAttr("disabled");
            if (data.success) {
                //限制六次
                if (status < 6) {
                    timerTips(type);
                    //超过三次提示拨打热线
                    if (status > 2) {
                        var valTips = $(".m-input-vercode").find(".m-val-tips");
                        if (valTips.length === 0) {
                            $(".m-input-vercode").append("<span class='m-val-tips u-hotline-tips'>短信、语音均无法收到？请拨打4000256688</span>");
                        } else {
                            valTips.addClass("u-hotline-tips").html("短信、语音均无法收到？请拨打4000256688");
                        }
                    }
                } else {
                    limitCodeRequest();
                }
            } else if (typeof (status) != "undefined" && status >= 6) {
                limitCodeRequest();
            } else {
                $.xDialog({
                    content: "网络异常，请刷新页面。"
                });
            }
        }
    });
}

//限制手机验证码请求
function limitCodeRequest() {
    $("#j-input-vercode").html("<div class='u-tips-waring f-round f-w430'>" +
        CONFIG.tips.requestLimitError + "</div>");
}

//已发送计时
function timerTips(type) {
    var tipsObj = $(".u-tips-vercode"),
        tipsPre = "",
        status = "";
    if (type === "1") {
        tipsPre = "短信已发送";
        status = "f-tips-green";
    } else if (type === "2") {
        tipsPre = "已呼叫你手机";
        status = "f-tips-blue";
    }
    if ($(".u-tips-voice").length > 0) {
        $(".u-tips-voice").remove();
    } else {
        $("#j-vercode, #j-img-vercode").remove();
    }
    tipsObj.remove();
    $("#j-phonecode").val("").parent().removeClass("z-hide")
        .before('<span class="f-round u-tips-vercode ' + status +
            '">' + tipsPre + ' <i id="j-time-count">' + CONFIG.moblieTime + '</i>秒后可重获</span>');
    $("#j-phonecode").siblings("em").removeClass("z-hide");
    //短信倒计时
    var timer = setInterval(function () {
        var count = Number($("#j-time-count").text());

        if (count !== 0) {
            count -= 1;
            $("#j-time-count").text(count);
        } else {
            clearInterval(timer);
            $(".u-tips-vercode").text("重新获取手机验证码").addClass("f-tips-red j-choose-voice f-cur-point u-btn-recode").removeClass("f-tips-blue f-tips-green");
        }
    }, 1000);
}

//语音选择
function chooseVoice() {
    var phonecode = $("#j-phonecode").parent().addClass("z-hide").clone();
    var html = '<div class="m-voice-box"><div class="u-tips-voice u-tips-voice2 f-round f-fs12 f-fl">' +
        '长时间没收到短信？<br>可使用手机语音验证码，' +
        '请注意接听来电</div>' +
        '<span class="f-round u-tips-vercode f-tips-blue f-cur-point j-voice-yes">使用语音验证</span><br/>' +
        '<span class= "f-round u-tips-vercode f-tips-green f-cur-point j-voice-no">获取短信验证 </span></div>';
    $(".m-input-vercode").html(html);
    $(".m-voice-box").after(phonecode);
}

//提交时手机验证
function validMobile() {
    var isCorrect = $("#j-cpn").data("isCorrect"),
        isCancel = $("#j-cpn").data("isCancel"),
        pngCode = $("#j-pngcode"),
        phoneCode = $("#j-phonecode");
    //检验手机号
    if (typeof (isCorrect) === "undefined" || isCorrect === false) {
        var errorMobileTips = "请输入正确的手机号码";
        if (typeof (isCancel) !== "undefined" && isCancel) {
            errorMobileTips = "该账号异常，请联系客服";
        }
        $("#j-cpn").next(".m-val-tips").validTips("error", errorMobileTips);
        return false;
    }
    //检验图形码
    if (pngCode.val() === "") {
        $("#j-vercode").parent().siblings(".m-val-tips").validTips("error", "请输入图形验证码");
        return false;
    }
    //检验验证码
    var phoneIsCorrect = phoneCode.data("isCorrect"),
        phoneTips = phoneCode.next(".m-val-tips");
    if (phoneCode.val() === "" || typeof (phoneIsCorrect) === "undefined" || phoneIsCorrect === false) {
        if (phoneTips.length > 0) {
            phoneTips.validTips("error", "请输入正确的手机验证码");
        } else {
            phoneCode.after('<span class="m-val-tips"><i class="u-icon u-icon-error f-icon-s">&nbsp;</i>请输入正确的手机验证码</span>');
        }
        return false;
    }
    return true;
}