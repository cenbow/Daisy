/**
 * @module Common
 */

import 'module/jquery.extend'
import {Util} from 'common/util'
import {Checkin,Message,Stats} from 'module/cube'

/**
 * 替代原生toFixed,修复JS缺陷问题
 * @param {number} size
 */
Number.prototype.toFixed = function (size) {
    const POW = Math.pow(10, size)
    return Math.round(this * POW) / POW
}

//每日签到
Checkin.init()

//推送用户未读消息
Message.pushUnreadMessage(300)

//ajax全局初始配置
$.ajaxSetup({
    //不缓存请求
    cache: false,
    //异常状态处理
    statusCode: {
        403: function () {
            alert('请刷新页面')
        },
        500: function () {
            window.location.href = environment.globalPath + "/500"
        },
        //session timeout
        901: function () {
            window.location.href = environment.globalPath + "/security/login/"
        }
    }
})

//设置注册跟踪Cookies TODO 可能要移到具体入口
Stats.registerTrace()

//全局二维码预览
$('.j-app-qrcode').qrcodePreview()

//兼容修正
if (Util.lteIE9()) {
    $('input[type="text"]:visible,input[type="password"]').placeholder({
        isUseSpan: true,
        onInput: true
    })
}

//输入框验证提示//TODO 只在有表单验证的entry里使用
//Validform.init()

//banner浮动层显示
//$('.j-layer-close').toggleLayerDisplay()

//浮动层定位 //TODO 移到具体位置
//$('.j-floatlayer').floatLayer()

// 模拟选择框 //TODO 移到具体位置
//$(".j-selector").xSelector()

//模拟多选框 //TODO 移到具体位置
//$(".j-ckbox").xCheckBox()

// 用户提示 //TODO 移到具体位置
//$(".j-user-tips").hoverTips()

// 箭头的定位（用户中心tab也切换时）//TODO 移到具体位置
//$('.j-arrow-locator').arrowLocator() //old arrowLocator('.u-menu')

//阻止Enter ['实名认证','个人资料','添加银行卡','验证码输入框']
//$('.j-stop-enterKey').stopEnterKey()

// 弹出提示 一般ajax调用之后显示错误信息
//window.showErrorMessage=YR.showErrorMessage;

//充值，支付，调用
//window.showErrorResultMessage=YR.showErrorResultMessage;