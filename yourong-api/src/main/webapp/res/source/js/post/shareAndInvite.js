/**
 * Created by Administrator on 2016/5/18.
 */
define(['zepto', 'base', 'template'], function (require, exports, module) {
    var template = require('template'),
        loginSource = $('#j-login-btn').attr('data-loginSource'),
        logined=$('#j-login-btn').attr('data-login'),
        loginUrl=environment.globalPath+ '/mstation/login?from=' + location.href,
        result = $("#j-share-data").data("result");
    
    $('#j-login').attr('href',loginUrl)
    //判断红包是否出现
    var encryptionId = getUrlParam("encryptionId") || '';
    var html = template('j-share-amount-tpl', result),
        $shareAmount=$('#j-share-amount');
    if (loginSource == 3) {
        if(logined=='true'){
            $shareAmount.html(html);
        }
        else{
            $shareAmount.html(html);
            $('#j-share-myAmount').hide()
        }
    } else {
        if (encryptionId) {
            $shareAmount.html(html);
        } else {
            $shareAmount.html(html);
            $('#j-share-myAmount').hide()
        }
    }
    // 获取URL参数
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg); // 匹配目标参数
        if (r != null) {
            return unescape(r[2]);
        }
        return null; // 返回参数值
    }
})