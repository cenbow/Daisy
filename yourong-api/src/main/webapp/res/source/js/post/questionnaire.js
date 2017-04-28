define(['zepto', 'base', 'template'], function(require, exports, module) {
    var template = require('template'),
        base = require('base'),
        data = $("#j-questionnaire-result").data("result"),
        os = $("#j-questionnaire-result").data("os"),
        logined = $("#j-questionnaire-result").data("logined"),
        $questionInit = $('#j-question-init'),
        $beginTest =  $('#j-begin-test'),
        $questionLi = $('#j-question-list li'),
        encryptionId=getUrlParam("encryptionId") || '',
        mKey = $('#j-result-mKey').attr('data-mkey')

    if (os === 1 || os === 2) {
        $(".g-ft").addClass("f-dn")
    }

    // console.log(JSON.stringify(data))
    // alert(JSON.stringify(data))

    // 展示用户头像和用户名
    if(data.result){
        var avatar = data.result.avatar
        var username = data.result.userName || data.result.mobile || ''
        if(avatar){
            $('#j-result-avatar').attr('src', 'http://oss.aliyuncs.com' + avatar)
        }
        if(username){
            $('#j-result-username').text(username)
        }
    }

    //m站登录跳转
    function setLoginGoBack() {
        var thisUrl = window.location.href,
            $login = $('#j-login'),
            $loginHref = $login.attr('href'),
            $register = $('#j-register'),
            $registerHref = $register.attr('href')

        $login.attr('href', $loginHref + '?from=' + thisUrl)
        $register.attr('href', $registerHref + '?from=' + thisUrl)
    }
    setLoginGoBack()

    //APP调用
    window.getDynamicCallBack = function(data) {
        window.appReturnData = data;
    }

    var urls = {
        saveEvaluation: environment.globalPath + '/activity/evalua/saveEvaluation'
    }

    var $resultH3 = $('#j-result-h3'),
        $resultText = $('#j-result-text'),
        type = '',
        suggest = ''

    // 开始测试，安卓和 ios中现在是定的一定要登陆
    if(os===1){
        if(encryptionId){
            // 安卓登陆并且已经测评过了
            if(data.success && data.result && data.result.evaluationScore  && data.result.evaluationScore > 0){
                var count = data.result.evaluationScore || 0
                renderResult(count)
            } else {
                loginedNotTest()
            }
        } else {
            $questionInit.removeClass('f-dn')
            $beginTest.on('click', function () {
                Android.GetEvent('login', '0', '');
            })
        }
    } else if(os === 2){
        if(encryptionId ){
            // IOS登陆并且已经测评过了
            if(data.success && data.result && data.result.evaluationScore  && data.result.evaluationScore > 0){
                var count = data.result.evaluationScore || 0
                renderResult(count)
            } else {
                loginedNotTest()
            }
        } else { // IOS 没登陆
            $questionInit.removeClass('f-dn')
            $beginTest.attr("href", 'yrw://invokeMethod=loginIn')
        }
    } else {
        if(logined){
            if(data.success && data.result && data.result.evaluationScore  && data.result.evaluationScore > 0){ // 已经做过测评了
                var count = data.result.evaluationScore || 0
                renderResult(count)
            } else{
                loginedNotTest()
            }
        } else {
            $questionInit.removeClass('f-dn')
            $beginTest.attr("href", $('#j-login').attr('href'))
        }
    }

    // 登陸但是沒測評過
    function loginedNotTest() {
        $questionInit.removeClass('f-dn')
        $beginTest.on('click', function () {
            $questionInit.addClass('f-dn')
            $('#j-question-process').removeClass('').addClass('u-question-process z-step1')
            $('#j-current-page').text(1)
            $('#j-question-process-wrap,#j-question-list').removeClass('f-dn')
        })
    }

    // 正式选题逻辑
    $('.u-question-btn').on('click', function () {
        var index = $(this).closest('li').index() - 0
        if(index > ($questionLi.length - 2)){ // 到达最后一个了
            $(this).addClass('z-active')
            $questionLi.removeClass('z-active')
            $('#j-questionnaire-result').removeClass('f-dn')
            countAndLook()

        } else {
            $('#j-question-process').removeClass().addClass('u-question-process z-step' + (index+2))
            $('#j-current-page').text(index+2)

            $(this).addClass('z-active')
                .closest('li').removeClass('z-active')
                .next('li').addClass('z-active')
        }
    })

    function renderResult(count) {
        var $resultH3 = $('#j-result-h3'),
            $resultText = $('#j-result-text'),
            type = '',
            suggest = ''

        if (count > 7 && count < 13) {
            type = '保守型'
            suggest = '宁走十步远，不走一步险'
        } else if (count > 12 && count < 18) {

            type = '稳健型'
            suggest = '谨慎能捕千秋蝉，小心驶得万年船'
        } else if (count > 17 && count < 25) {

            type = '平衡型'
            suggest = '任尔东西南北风，平衡全局理性行'
        } else if (count > 24 && count < 29) {

            type = '进取型'
            suggest = '长风破浪会有时，循序渐进打基石'
        } else if (count > 28 && count < 34) {

            type = '积极型'
            suggest = '不担三分险，难练一身胆'
        }

        if(type&&suggest){
            $resultH3.text(type)
            $resultText.text(suggest)
        }

        $questionInit.addClass('f-dn')
        $('#j-questionnaire-result').removeClass('f-dn')
    }

    //评测结果
    var countAndLook = function(){
        var count = 0

        for(var i = 0; i < $questionLi.length ; i++){
            var val = $questionLi.eq(i).find('.m-btns-group .z-active').attr('data-val') - 0 || 0
            if(val){count += val}
        }

        renderResult(count)

        $('#j-question-process-wrap').addClass('f-dn')

        if(os==1){ // 安卓 ok
            Android.GetEvent('saveEvaluation', 0, 'args_evaluationScore_1_string_'+count);
        } else if(os==2){
            // IOS 初始化数据 ok ，网后台传数据返回用户不存在
            base.getAPI({
                url: environment.globalPath + '/activity/dynamicInvoke',
                data: {
                    loginSource: 2,
                    mKey: mKey, // TODO
                    invokeMethod: 'saveEvaluation',
                    invokeParameters: 'args_evaluationScore_1_string_'+count
                },
                callback: function (data) {
                    // alert('IOS返回的数据' + JSON.stringify(data))
                }
            })

        } else {
            console.log('M站提交数据')
            base.getAPI({
                url: environment.globalPath + '/activity/dynamicInvoke',
                data: {
                    invokeMethod: 'saveEvaluation',
                    invokeParameters: 'args_evaluationScore_1_string_'+count
                },
                callback: function (data) {
                    if(data.success){
                        $('#j-question-process').removeClass().addClass('u-question-process z-step1')
                    } else {
                        console.log('测评失败之后', data)
                    }

                    $('#j-current-page').text(1)
                }
            })
        }
    }

    // 重新测试
    $('#j-reTest').on('click', function () {
        $('.u-question-btn').removeClass('z-active')
        $questionInit.removeClass('f-dn')
        $questionLi.eq(0).addClass('z-active')
        $('#j-question-process').removeClass().addClass('u-question-process z-step1')
        $('#j-current-page').text(1)
        $('#j-question-process-wrap,#j-question-list,#j-questionnaire-result').addClass('f-dn')
    })

    $beginTest.on('click', function () {
        if(encryptionId||logined){
            $questionInit.addClass('f-dn')
            $('#j-question-process').removeClass().addClass('u-question-process z-step1')
            $('#j-current-page').text(1)
            $('#j-question-process-wrap,#j-question-list').removeClass('f-dn')
        }
    })

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
