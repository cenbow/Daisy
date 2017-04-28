common.js重构遗留问题记录

/*global escape,unescape,dialog*/

//项目预告倒计时 //TODO 除了首页还有其他页面没替换

//开启验证提示

//每日签到&&签到引导
//import {Checkin} from 'module/checkin'
//Checkin.signinGuide('body');//TODO move to member.js

// 设置注册跟踪Cookies

// 限制金额输入,只允许输入数字与小数点 require('util')
_.isFloat('value','e');//需要在具体页面替换 isFloatNumber()

//全局二维码预览

//banner浮动层显示

//浮动层定位

//模拟多选框,需要在具体文件里替换 util.js

// 模拟选择框

// 用户提示

// 箭头的定位（用户中心tab也切换时）

// 弹出提示 一般ajax调用之后显示错误信息

//充值，支付，调用

//阻止Enter ['实名认证','个人资料','添加银行卡','验证码输入框']
//$("#memberIdentity_form input[type='text'], " +
//    "#memberInfoForm input[type='text'], " +
//    "#bankAdd_form,#j-bank-checkcode").stopEnterKey();
//已改写