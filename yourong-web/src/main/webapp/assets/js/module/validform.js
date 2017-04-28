//@Module 表单校验

import 'lib/js/validform'

export const Validform = class {
    constructor(){
        this.init()
    }
    init() {
        //validator 表单验证模块
        var validform = $(".j-validform").Validform({
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
        validform.addRule([
            {
                ele: ".v-sex", //性别
                datatype: "*",
                nullmsg: "请选择性别",
                errormsg: "请选择性别"
            }, {
                ele: ".v-edu", //学历
                datatype: "*",
                nullmsg: "请选择学历",
                errormsg: "请选择学历"
            },
            {
                ele: ".v-mob", //手机
                datatype: "mobile",
                nullmsg: "手机号码不能为空",
                errormsg: "请输入正确的手机号码"
            },
            {
                ele: ".v-pwd", //密码
                datatype: "pwd",
                nullmsg: "请输入密码",
                errormsg: "长度6-16位至少包含数字和字母"
            },
            {
                ele: ".v-pwd-reck", //密码确认(注意添加这个class的时候，需要添加recheck属性,例如：recheck="password"，password为密码input的name值)
                datatype: "pwd",
                nullmsg: "请再输入一次密码",
                errormsg: "您两次输入的密码不一致"
            }, {
                ele: ".v-oldPwd", //密码
                datatype: "pwd",
                nullmsg: "请输入旧密码",
                errormsg: "长度6-16位至少包含数字和字母"
            },
            {
                ele: ".v-newPwd", //密码
                datatype: "pwd",
                nullmsg: "请输入新密码",
                errormsg: "长度6-16位至少包含数字和字母"
            },
            {
                ele: ".v-rname", //真实姓名 2-4中文
                datatype: "zhs,*2-25",
                nullmsg: "请填写姓名",
                errormsg: "请填写中文姓名"
            },
            {
                ele: ".v-select-req",
                datatype: "*",
                nullmsg: "此项为必选",
                errormsg: "此项为必选"
            },
            {
                ele: ".v-input-req",
                datatype: "*",
                nullmsg: "此项为必填",
                errormsg: "此项为必填"
            }, {
                ele: ".v-loginid", //昵称 nickname
                datatype: "mobile|z3-14",
                nullmsg: "请填写手机或昵称",
                errormsg: "请填写正确的手机号码或昵称"
            },
            {
                ele: ".v-job", //职业
                datatype: "*",
                nullmsg: "请选择职业",
                errormsg: "请选择职业"
            },
            {
                ele: ".v-n1-10", //金额 没有小数
                datatype: "n1-10",
                nullmsg: "请填写信息",
                errormsg: "请填写1到10位数字"
            },
            {
                ele: ".v-money", //金额
                datatype: "money",
                nullmsg: "请填写金额",
                errormsg: "金额不能小于0.01元"
            },
            {
                ele: ".v-bankcard", //银行卡
                datatype: "n16-19",
                nullmsg: "请填写银行卡",
                errormsg: "请填写正确的银行卡号"
            },
            {
                ele: ".v-bank-reck", //银行卡确认 (注意添加这个class的时候，需要添加recheck属性)
                datatype: "n16-19",
                nullmsg: "请再输入一次银行卡",
                errormsg: "您两次输入的银行卡号不一致"
            },
            {
                ele: ".v-idcard", //身份证
                datatype: "idcard",
                nullmsg: "请填写身份证",
                errormsg: "请填写正确的身份证"
            }
        ]);

        //验证表单填写正确统一不提示信息
        validform.tipmsg.r = " ";

        return validform;
    }
}