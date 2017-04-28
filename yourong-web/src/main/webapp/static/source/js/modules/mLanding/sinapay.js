/**
 * Created by adeweb on 15/5/14.
 */
/*global window,$,environment,setInterval,clearInterval,formValid*/
//no cache for all ajax request
$.ajaxSetup({
    cache: false
});
// 阻止重复提交
$.fn.extend({
    preventRepeatedSubmit: function (interval) {
        var $this=$(this);
        $this.on('click', function () {
            var repeatedTime=$this.data('repeatedTime'),
                now=new Date().getTime();

            if(repeatedTime&&now-repeatedTime<=(interval-0||500)){
                return false;
            }else{
                $this.data('repeatedTime',now);
            }
        });
    }
});
//立即认证并开通资金托管账户按钮并发处理
$(".j-reg-btn").preventRepeatedSubmit(1000);
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
/**验证规则*/
formValid.addRule([{
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
},{
    ele: ".v-idcard", //身份证
    datatype: "idcard",
    nullmsg: "请填写身份证",
    errormsg: "请填写正确的身份证"
}


]);
//验证表单填写正确统一不提示信息
formValid.tipmsg.r = " ";
//实名认证
$(function() {
    function showErrorMessage(data) {
        var sinapayErrorObj=$("#j-sinapay-error");
        if (data.resultCodeEum != null && data.resultCodeEum != "") {
            var message = "";
            for (var x in data.resultCodeEum) {
                message = data.resultCodeEum[x].msg + message;
            }
            sinapayErrorObj.text(message);
        } else {

            sinapayErrorObj.text(data.result);
        }
    }
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
                        $("#j-sinapay-error").fadeOut();
                        var pageTag=$("#pageId").val();
                        switch(pageTag){
                            case "landingSinapay":
                                window.location.href = environment.globalPath + "/member/landing/mRegisterSuccess";
                                break;
                            case "landingSinapay_A":
                                window.location.href = environment.globalPath + "/member/landing/mRegisterSuccessA";
                                break;
                            default :
                                break;
                        }

                    } else {
                        $("#j-sinapay-error").fadeIn();
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

});