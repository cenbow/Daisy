/*
 *登录注册页面-验证与交互JS
 */
/*global window,$,environment,setInterval,clearInterval,formValid*/
//no cache for all ajax request
$.ajaxSetup({
    cache: false
});
//common method script
$.extend({
    // Ajax Post Method
    xPost: function (config) {
        var type = "POST",
            datatype = "json",
            async = true;
        if (typeof config === "object") {
            type = config.type ? config.type : type;
            datatype = config.datatype ? config.datatype : datatype;
            async = config.async ? config.async : async;
            var xToken = $("#xToken").val();
            if (typeof xToken !== "undefined" && xToken === "") {
                if (config.hasOwnProperty("data")) {
                    config.data.xToken = xToken;
                } else {
                    config.data = {
                        xToken: xToken
                    };
                }
            }
        } else {
            throw new Error("xPost: config参数为空或不是对象");
        }
        $.ajax({
            type: type,
            url: config.url,
            data: config.data,
            datatype: datatype,
            async: async,
            success: config.callback,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                // console.log(XMLHttpRequest);
                // console.log(textStatus);
                // console.log(errorThrown);
            },
            statusCode: {
                403: function () {
                    alert('请刷新页面');
                },
                500: function () {
                    window.location.href = environment.globalPath + "/500";
                }
            }

        });
    }
});

(function () {
    //CONFIG
    var CONFIG = {
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
    };
    var referCodeObj=$("#j-refercode"),
        cpnObj=$("#j-cpn"),
        regFormObj=$("#j-reg-form"),
        vercodeWrapObj=$(".m-input-vercode"),
        vercodeObj=$("#j-vercode");
    //Get xToken
    var xToken = $("#xToken").val();
    //firefox清除历史输入
    cpnObj.val("");
  //开启验证提示
    var formValid = $(".j-validform").Validform({
        tiptype: 3,
        postonce: true,
        datatype: {
            //中文
            "zh2-4": /^[\u4E00-\u9FA5]{2,4}$/,
            //少数民族姓名
            "zhs": /^[\u4E00-\u9FA5]{2,10}(?:·[\u4E00-\u9FA5]{2,10})*$/,
            //密码
            "pwd": /^(?![^a-zA-Z]+$)(?!\D+$).{6,16}$/,
            "mobile": /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/,
            //货币
            "money": /^[1-9]*[1-9][0-9]*(.[0-9]{1,2})?$|^(0.[0-9]{1,2})$/,
            //身份证
            "idcard": function (gets, obj, curform, datatype) {
                var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1]; // 加权因子;
                var ValideCode = [1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2]; // 身份证验证位值，10代表X;

                if (gets.length == 15) {
                    return isValidityBrithBy15IdCard(gets);
                } else if (gets.length == 18) {
                    var a_idCard = gets.split(""); // 得到身份证数组   
                    if (isValidityBrithBy18IdCard(gets) && isTrueValidateCodeBy18IdCard(a_idCard)) {
                        return true;
                    }
                    return false;
                }
                return false;

                function isTrueValidateCodeBy18IdCard(a_idCard) {
                    var sum = 0; // 声明加权求和变量   
                    if (a_idCard[17].toLowerCase() == 'x') {
                        a_idCard[17] = 10; // 将最后位为x的验证码替换为10方便后续操作   
                    }
                    for (var i = 0; i < 17; i++) {
                        sum += Wi[i] * a_idCard[i]; // 加权求和   
                    }
                    var valCodePosition = sum % 11; // 得到验证码所位置   
                    if (a_idCard[17] == ValideCode[valCodePosition]) {
                        return true;
                    }
                    return false;
                }

                function isValidityBrithBy18IdCard(idCard18) {
                    var year = idCard18.substring(6, 10);
                    var month = idCard18.substring(10, 12);
                    var day = idCard18.substring(12, 14);
                    var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
                    // 这里用getFullYear()获取年份，避免千年虫问题   
                    if (temp_date.getFullYear() != parseFloat(year) || temp_date.getMonth() != parseFloat(month) - 1 || temp_date.getDate() != parseFloat(day)) {
                        return false;
                    }
                    return true;
                }

                function isValidityBrithBy15IdCard(idCard15) {
                    var year = idCard15.substring(6, 8);
                    var month = idCard15.substring(8, 10);
                    var day = idCard15.substring(10, 12);
                    var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
                    // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法   
                    if (temp_date.getYear() != parseFloat(year) || temp_date.getMonth() != parseFloat(month) - 1 || temp_date.getDate() != parseFloat(day)) {
                        return false;
                    }
                    return true;
                }

            },
            //4-14位中英文字符
            "z3-14": function (gets, obj, curform, datatype) {
                var reg = /^[\u4E00-\u9FA5\uf900-\ufa2dA-Za-z]+$/;
                if (reg.test(gets)) {
                    var strLength = getStrLeng(gets);
                    if (strLength < 3 || strLength > 14) {
                        return false;
                    }
                    return true;
                }
                return false;

                function getStrLeng(str) {
                    var realLength = 0;
                    var len = str.length;
                    var charCode = -1;
                    for (var i = 0; i < len; i++) {
                        charCode = str.charCodeAt(i);
                        if (charCode >= 0 && charCode <= 128) {
                            realLength += 1;
                        } else {
                            // 如果是中文则长度加2
                            realLength += 2;
                        }
                    }
                    return realLength;
                }
            },
            //日期
            "date": /^(?:(?:1[6-9]|[2-9][0-9])[0-9]{2}([-/.]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:(?:1[6-9]|[2-9][0-9])(?:0[48]|[2468][048]|[13579][26])|(?:16|[2468][048]|[3579][26])00)([-/.]?)0?2\2(?:29))(\s+([01][0-9]:|2[0-3]:)?[0-5][0-9]:[0-5][0-9])?$/
        }
    });
    //阻止Enter ['验证码输入框']
    $("#j-bank-checkcode").on("keydown", function (e) {
        var ev = e || event;
        if (ev.keyCode == 13) {
            ev.keyCode = 0;
            return false;
        }
    });
    /**验证规则*/
    formValid.addRule([{
            ele: ".v-sex", //性别
            datatype: "*",
            nullmsg: "请选择性别",
            errormsg: "请选择性别"
        }, {
            ele: ".v-edu", //学历
            datatype: "*",
            nullmsg: "请选择学历",
            errormsg: "请选择学历"
        }, {
            ele: ".v-mob", //手机 
            datatype: "mobile",
            nullmsg: "手机号码不能为空",
            errormsg: "请输入正确的手机号码"
        }, {
            ele: ".v-pwd", //密码
            datatype: "pwd",
            nullmsg: "请输入密码",
            errormsg: "长度6-16位至少包含数字和字母"
        }, {
            ele: ".v-pwd-reck", //密码确认(注意添加这个class的时候，需要添加recheck属性,例如：recheck="password"，password为密码input的name值)
            datatype: "pwd",
            nullmsg: "请再输入一次密码",
            errormsg: "您两次输入的密码不一致"
        }, {
            ele: ".v-oldPwd", //密码
            datatype: "pwd",
            nullmsg: "请输入旧密码",
            errormsg: "长度6-16位至少包含数字和字母"
        }, {
            ele: ".v-newPwd", //密码
            datatype: "pwd",
            nullmsg: "请输入新密码",
            errormsg: "长度6-16位至少包含数字和字母"
        }, {
            ele: ".v-rname", //真实姓名 2-4中文
            datatype: "zhs,*2-25",
            nullmsg: "请填写姓名",
            errormsg: "请填写中文姓名"
        }, {
            ele: ".v-select-req",
            datatype: "*",
            nullmsg: "此项为必选",
            errormsg: "此项为必选"
        }, {
            ele: ".v-input-req",
            datatype: "*",
            nullmsg: "此项为必填",
            errormsg: "此项为必填"
        }, {
            ele: ".v-loginid", //昵称 nickname
            datatype: "mobile|z3-14",
            nullmsg: "请填写手机或昵称",
            errormsg: "请填写正确的手机号码或昵称"
        }, {
            ele: ".v-job", //职业
            datatype: "*",
            nullmsg: "请选择职业",
            errormsg: "请选择职业"
        }, {
            ele: ".v-n1-10", //金额 没有小数
            datatype: "n1-10",
            nullmsg: "请填写信息",
            errormsg: "请填写1到10位数字"
        }, {
            ele: ".v-money", //金额
            datatype: "money",
            nullmsg: "请填写金额",
            errormsg: "金额不能小于0.01元"
        }, {
            ele: ".v-bankcard", //银行卡
            datatype: "n16-19",
            nullmsg: "请填写银行卡",
            errormsg: "请填写正确的银行卡号"
        }, {
            ele: ".v-bank-reck", //银行卡确认 (注意添加这个class的时候，需要添加recheck属性)
            datatype: "n16-19",
            nullmsg: "请再输入一次银行卡",
            errormsg: "您两次输入的银行卡号不一致"
        }, {
            ele: ".v-idcard", //身份证
            datatype: "idcard",
            nullmsg: "请填写身份证",
            errormsg: "请填写正确的身份证"
        }
    ]);
    //验证表单填写正确统一不提示信息
    formValid.tipmsg.r = " ";
    
    /*
     * Method
     */
    //验证提示
    $.fn.validTips = function (type, message) {
        var style = "error";
        if (type === "success") {
            style = "success";
            message = "";
        }
        var html = "<span class='m-val-tips'><i class='u-icon u-icon-" +
            style + " f-icon-s'></i>" + message + "</span>";
        $(this).replaceWith(html);
    };
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
                                                _this.next(".m-val-tips").text("您的推荐人:" + referText).addClass("z-success");
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
    };
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
                        //if (status > 2) {
                        //    var valTips = $(".m-input-vercode").find(".m-val-tips");
                        //    if (valTips.length === 0) {
                        //        $(".m-input-vercode").append("<span class='m-val-tips u-hotline-tips'>短信、语音均无法收到？请拨打4000256688</span>");
                        //    } else {
                        //        valTips.addClass("u-hotline-tips").html("短信、语音均无法收到？请拨打4000256688");
                        //    }
                        //}
                    } else {
                        limitCodeRequest();
                    }
                } else if (typeof (status) != "undefined" && status >= 6) {
                    limitCodeRequest();
                } else {
                    alert("网络异常，请刷新页面。");
                }
            }
        });
    }

    //限制手机验证码请求
    function limitCodeRequest() {
        $(".m-input-vercode").html("<div class='u-tips-waring f-round f-w430'>" +
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
            $("#j-vercode, #j-img-vercode").fadeOut(400,function(){
                $(this).remove();
            });
        }
        tipsObj.remove();
        $("#j-phonecode").val("").parent().removeClass("z-hide")
            .before('<span class="f-round u-tips-vercode ' + status +
            '">' + tipsPre + '<i id="j-time-count">' + CONFIG.moblieTime + '</i>秒后可重获</span>');
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
            '长时间没收到短信？可使用手机语音验证码，' +
            '请注意接听来电</div>' +
            '<span class="f-round u-tips-vercode f-tips-blue f-cur-point j-voice-yes">使用语音验证</span>' +
            '<span class= "f-round u-tips-vercode f-tips-green f-cur-point j-voice-no u-tips-pLeft">获取短信验证 </span></div>';
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

    /*
     * 流程
     */
    //1.手机号码验证
    cpnObj.on("keypress", function (e) {
        $(this).data("prevValue", $(this).val());
    });
    cpnObj.on("keyup", function (e) {
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
    });
    //推荐人手机号码验证
    referCodeObj.on("keyup postpaste", function (e) {
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
    });
    referCodeObj.on("blur", function () {
        if ($(this).val() !== "" && $(this).val().length < 11) {
            $(this).next(".m-val-tips").validTips("error", "请输入正确的手机号码");
        }
    });
    cpnObj.on("blur", function () {
        var tipsText = "请输入正确的手机号码",
            len = $(this).val().length;
        if (len < 11) {
            if (len === 0) {
                tipsText = "请输入手机号码";
            }
            $(this).data("isCorrect", false).next(".m-val-tips").validTips("error", tipsText);
        }
    });
    //图形码输入框聚焦时再次验证手机号码是否存在
    vercodeObj.on("focus", function () {
        var cpn = $("#j-cpn"),
            isCorrect = cpn.data("isCorrect");
        if (cpn.val().length === 0 || !isCorrect) {
            cpn.focus();
            cpn.data({
                "isCorrect": false
            });
        }
    });

    //2.验证图形码并发送手机验证码
    vercodeObj.on("keyup", function () {
        var _this = $(this),
            val = $(this).val();
        if (val.length === 4) {
            var cpn = cpnObj.val();
            $(this).parent().find(".m-val-tips").empty();

            if (cpn.length === 11 && cpnObj.data("isCorrect") === true) {
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
    });
    //是否语音选择
    vercodeWrapObj.on("click", ".j-choose-voice", {}, function () {
        chooseVoice();
        $(".j-phonecode").empty();
    });
    vercodeWrapObj.on("click", ".u-tips-vercode", {}, function () {
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
    });
    //只验证图形码
    regFormObj.on("keyup", "#j-vercode2", function () {
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
    });
    //验证码点击刷新
    vercodeWrapObj.on("click", ".u-img-vercode", function () {
        var timestamp = new Date().getTime();
        $(this).attr("src", environment.globalPath + "/security/validateCode" + "?t=" + timestamp);
        $("#j-vercode2").empty();
    });

    //3.核查手机验证码
    regFormObj.on("keyup", "#j-phonecode", {}, function () {
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
    });

    //4.表单提交
    regFormObj.submit(function () {
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
                default:
                    break;
            }
            if (isXpost) {
                var bindTipsObj=$("#j-bind-success"),
                    formAreaObj=$("#j-reg-form,.u-reg-tips");
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
                                    //微信端登录回调
                                    formAreaObj.remove();
                                    bindTipsObj.show();
                                    alert("恭喜您登录绑定成功！");
                                    if(typeof WeixinJSBridge!=="undefined"){
                                        WeixinJSBridge.call('closeWindow');
                                    }
                                    break;
                                case "register":
                                    $(".j-reg-btn").val("注册").removeClass("z-disabled").removeAttr("disabled");
                                    //微信端注册回调
                                    formAreaObj.remove();
                                    bindTipsObj.show();
                                    alert("恭喜您注册绑定成功！");
                                    if(typeof WeixinJSBridge!=="undefined"){
                                        WeixinJSBridge.call('closeWindow');
                                    }
                                    break;
                                default:
                                    return false;
                                    break;
                            }

                        } else {
                            switch (page) {
                                case "login":
                                    $("#j-login-submit").val("登录").removeClass("z-disabled").removeAttr("disabled");

                                    var reCode = Number(data.resultCodeEum[0].code),
                                        loginErrorTips = "";
                                    switch (reCode) {
                                        case 90019:
                                            loginErrorTips = "您输入的图形验证码有误";
                                            break;
                                        case 90003:
                                            loginErrorTips = "您的账号已注销，如需帮助，请联系客服";
                                            break;
                                        case 90045:
                                        	loginErrorTips = "此账号已经绑定过微信";
                                        	break;
                                        case 90048:
                                        	loginErrorTips = "您已绑定了有融网公众号！请返回操作。";
                                        	break;
                                        default:
                                            loginErrorTips = "您输入的帐号或密码有误";
                                        break;
                                    }

                                    $("#j-login-error").fadeToggle().html(loginErrorTips).fadeIn();
                                    var imgUrl = environment.globalPath + "/security/validateCode";
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
                                            alert("请输入正确的手机验证码。");
                                            break;
                                        case 10001:
                                            alert("网络异常，请重新提交。");
                                            break;
                                        case 90048:
                                        	formAreaObj.remove();
                                            alert("恭喜您成功加入有融网！您的微信当前已绑定了其他有融网账号。");
                                            if(typeof WeixinJSBridge!=="undefined"){
                                                WeixinJSBridge.call('closeWindow');
                                            }
                                        	break;
                                        default:
                                            alert("网络异常，请刷新页面。");
                                            break;
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
    });
    //服务协议阻止提交
    $("#j-agree").change(function () {
        var checked = $(this).is(":checked"),
            btn = $("button[type=submit]");
        if (!checked) {
            btn.addClass("z-disabled").attr("disabled", "disabled");
        } else {
            btn.removeClass("z-disabled").removeAttr("disabled");
        }
    });
//    function isWechat(){
//        var ua = navigator.userAgent.toLowerCase();
//        return ua.indexOf("micromessenger")!==-1?true:false;
//    }
//    if(!isWechat()){
//        //alert("请在微信中打开此页面");
//    }
    //微信openId取值
    var localUrl=window.location.href,
        oidIndex=localUrl.indexOf("?openId=");
    if(oidIndex!==-1){
    	var openId=localUrl.substring(oidIndex+8,localUrl.length),
    		  wxUrl=$(".j-weixin-url").attr("href");
        $("#j-openId").val(openId);
        $(".j-weixin-url").attr("href",wxUrl+"?openId="+openId);
    }
    //firefox bug
    $("#j-pngcode").val("");
    $.fn.extend({
        //粘贴事件
        pasteEvents: function (delay) {
            if (typeof (delay) === "undefined") {
                delay = 20;
            }
            return $(this).each(function () {
                var $el = $(this);
                $el.on("paste", function () {
                    $el.trigger("prepaste");
                    setTimeout(function () {
                        $el.trigger("postpaste");
                    }, delay);
                });
            });
        }
    });
    //推荐人粘贴触发校验
    referCodeObj.on("postpaste", function () {
    }).pasteEvents();
})();