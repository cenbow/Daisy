
define(function (require, exports, module) {
    'use strict'
    var $helpHot = $("#j-help-hot"),
        $helpmore = $('.j-help-more'),
        $questionType = $("#j-question-type"),
        siteurl = $(".j-siteurl-link").data("siteurl"),
        $answer = $("#j-help-answer"),
        xToken = $('.j-siteurl-link').data('encryptionId');

    $helpHot.on("click","tr",urlskip);
    ////问题类型页跳转到对应的问题
    $questionType.on("click","li tr",urlskip);
    ////对应的问题找到答案
    $answer.find("li").each(searchNO);
    $questionType.find("li").each(searchType);
    //页面跳转 公告方法
    function urlskip() {
        var urlNO = $(this).data("answerno"),
            osNum = $helpmore.data("os");
        helpCenterBehavior(urlNO, xToken);

        window.location.href = siteurl + "/mstation/post/helpAnswer?urlnum=" + urlNO + "&osnum=" + osNum;
    }
    if(getUrlParam("osnum")){
        $helpmore.attr("data-os",getUrlParam("osnum"));
    }

    //查找出对应的答案并显示
    function searchNO(){
        var url=getUrlParam("urlnum");
        var answerNO=$(this).data("answerno");
        if(answerNO==url){
            $(this).addClass("z-current")
        }
    }

    //查找出对应的类型并显示
    function searchType() {
        var url = getUrlParam("type");
        var type = $(this).data("type");
        if (type == url) {
            $(this).addClass("z-current")
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


    $questionType.find('li').find('div').find('h4').click(function () {
        if ($(this).parent().find('.u-help-hot').find('tr').length) {
            console.log($(this).parent().find('.u-help-hot').find('tr').length)
            if ($(this).parent().find('.u-help-hot').hasClass('f-dn')) {
                $(this).parent().find('.u-help-hot').removeClass('f-dn')
                $(this).parent().siblings().find('.u-help-hot').addClass('f-dn')
                $(this).find("i").addClass('z-unfold')
                $(this).parent().siblings().find('h4').find("i").removeClass('z-unfold')
            } else {
                $(this).parent().find('.u-help-hot').addClass('f-dn')
                $(this).find("i").removeClass('z-unfold')
            }
        }
    })
    function helpCenterBehavior(urlNO, xToken) {
        $.ajax({
            url: environment.globalPath + '/yrwHtml/helpCenterBehavior',
            data: {
                anchor: 'J' + urlNO,
                htmlToken: xToken
            },
            type: 'POST',
            headers: {
                'X-Requested-Accept': 'json',
                'Accept-Version': '1.0.0'
            },
            dataType: 'json'
        })
    }
});
