/**
 * 520活动
 */

define(['zepto','base','template'],function (require, exports, module) {
    var template = require('template'),
        base = require('base');
    var data=$("#j-fellInLove-data").data("result"),
        os=$("#j-fellInLove-os").data("os"),
     islogin=$("#j-fellInLove-btn").data("login")

    if(os===1||os===2){
        $(".g-ft").addClass("f-dn")
    }
//数字转换成千分号方法
    function amountSplit(num){
        if (!num) {
            return ''
        }
        var n = num + "",
            reg = /(-?\d+)(\d{3})/
        return n.replace(reg, "$1,$2")
    }

    //m站登录跳转
    function  setLoginGoBack(){
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

//加载数据

    if(data.success){
        init(data.result)
    }
     function init(result){
        var actvityStatus=result.activityStatus||''

        switch (actvityStatus){
            case 2:
                beforeActive();
                //duringActive(result);
                break;
            case 4:
                duringActive(result);
                //beforeActive();
                break;
            case 6:
                afterActive(result);
                break;
            default:
                break;
        }
    }

    //活动开始之前
    function beforeActive(){
        //进度条
        $("#j-progressBar-text").css("left","74%")
        $("#j-progress-point").css("left","92%")
        $("#j-progressBar-yel").css("left",0)
        $("#j-coupon-val").text("5200")
        $("#j-progress").css("opacity",1)
        //巅峰壕礼
        $("#j-countdown-content").removeClass("f-dn")
        //按钮
        $("#j-fellInLove-btn").text("活动未开始").addClass("z-unStartBtn")
        //列表
        $("#j-list-beforeActive").removeClass("f-dn")
    }
    //活动进行中
    function duringActive(result){
        //进度条
        var count=result.couponNumber,
            pre=((count/5200).toFixed(2))*100
        if(pre>90){
            $("#j-progressBar-text").css("left","73%")

        }else{
            if(pre<20){
                $("#j-progressBar-text").css("left","1%")
            }else{
                $("#j-progressBar-text").css("left",(pre-18)+"%")
            }

        }
        if(pre<8){
            $("#j-progress-point").css("left","0%")
        }else{
            $("#j-progress-point").css("left",(pre-9)+"%")
        }

        $("#j-progressBar-yel").css("left",(pre-102)+"%")
        $("#j-coupon-val").text(count)
        $("#j-progress").css("opacity",1)
        //巅峰豪礼
        $("#j-countdown-countdown").removeClass("f-dn")
        //按钮
        var encryptionId=getUrlParam("encryptionId") || '',
            memberTotalAmount=amountSplit(result.memberTotalAmount||'');
        //APP上登录逻辑  1.安卓 2.ios
        if(os===1){
                if(encryptionId){
                    if(memberTotalAmount){
                        $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元")
                    }else{
                        $("#j-fellInLove-btn").text("我的投资额:0元")
                    }
                }else {
                    $("#j-fellInLove-btn").text("登录查看我的投资额")
                   .on("click", function () {
                        Android.GetEvent('fellInLoveFor520Init', '0', '');
                    })
                }

        }else if(os===2){
                if (encryptionId) {
                    if(memberTotalAmount){
                        $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元")
                    }else{
                        $("#j-fellInLove-btn").text("我的投资额:0元")
                    }
                }else{
                    $("#j-fellInLove-btn").text("登录查看我的投资额")
                    .attr("href","yrw://invokeMethod=loginIn")
                }
        }else{

            if(islogin){
                if(memberTotalAmount){
                    $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元")
                }else{
                    $("#j-fellInLove-btn").text("我的投资额:0元")
                }
            }else{
                $("#j-fellInLove-btn").text("登录查看我的投资额")
                    .attr("href", $('#j-login').attr('href'))
            }


        }
        //表单
        $("#j-fellInLove-list").removeClass("f-dn")
        var rankList = result.rankList ||[],
            listLength=rankList.length,
            html;
        if(listLength<10){
            for (var i=0;i<10-listLength;i++) {
                rankList.push({
                    avatar: "/static/img/member/avatar_35x35.png",
                    lastUsername: "虚位以待",
                    lastTotalInvest: ""
                })
            }
        }
        result.rankList = rankList
        html = template('j-fellInLove-list-tpl', result);
        $('#j-fellInLove-list').html(html);
        //倒计时部分
        $("#j-countdown-countdown").removeClass("f-dn");
        var $actCountdown=$("#j-activity-countdown"),
            startTime=result.startTime,
            endTime=result.endTime,
            tNow=environment.serverDate,
            //startTime=new Date('2016/05/12 18:31:00').getTime(),
            //endTime=new Date('2016/05/12 18:31:10').getTime(),
            actCountTime=Math.floor((endTime - tNow) / 1000),
            countClock = $actCountdown.FlipClock({
                countdown: true,
                autoStart: false,
                clockFace: 'HourlyCounter',
                callbacks: {
                    stop: function () {
                        var html = template('j-countdown-ranking-tpl', result);
                        $('#j-countdown-ranking').html(html);
                    }
                }
            });
        //当前时间大于等于活动开始时间活动倒计时开始
        if(tNow>=startTime){
            countClock.setTime(actCountTime);
            countClock.start();
        }

    }




    //活动结束后
    function afterActive(result){
        //进度条
        $("#j-progressBar-text").css("left","-1%")
        $("#j-progress-point").css("left","0%")
        $("#j-progressBar-yel").css("left","-100%")
        $("#j-coupon-val").text("0")
        $("#j-progress").css("opacity",1)
        //巅峰壕礼
        var html1 = template('j-countdown-ranking-tpl', result);
        $('#j-countdown-ranking').html(html1)
            .removeClass("f-dn");
        //按钮
        //表单
        $("#j-fellInLove-list").removeClass("f-dn")
        var rankList = result.rankList ||[],
            listLength=rankList.length,
            html;
        if(listLength<10){
            for (var i=0;i<10-listLength;i++) {
                rankList.push({
                    avatar: "/static/img/member/avatar_35x35.png",
                    lastUsername: "虚位以待",
                    lastTotalInvest: ""
                })
            }
        }
        result.rankList = rankList
        html = template('j-fellInLove-list-tpl', result);
        $('#j-fellInLove-list').html(html);

        //按钮
        var encryptionId=getUrlParam("encryptionId") || '',
            memberTotalAmount=amountSplit(result.memberTotalAmount||'');
        //APP上登录逻辑  1.安卓 2.ios

        if(os===1){
            if(encryptionId){
                if(memberTotalAmount){
                    $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元").addClass("z-unStartBtn")
                }else{
                    $("#j-fellInLove-btn").text("我的投资额:0元").addClass("z-unStartBtn")
                }
            }else {
                $("#j-fellInLove-btn").text("登录查看我的投资额")
                .on("click", function () {
                    Android.GetEvent('fellInLoveFor520Init', '0', '');
                })
            }

        }else if(os===2){
            if (encryptionId) {
                if(memberTotalAmount){
                    $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元").addClass("z-unStartBtn")
                }else{
                    $("#j-fellInLove-btn").text("我的投资额:0元").addClass("z-unStartBtn")
                }
            }else{
                $("#j-fellInLove-btn").text("登录查看我的投资额")
                .attr("href","yrw://invokeMethod=loginIn")
            }
        }else{
            if(islogin){
                if(memberTotalAmount){
                    $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元").addClass("z-unStartBtn")
                }else{
                    $("#j-fellInLove-btn").text("我的投资额:0元").addClass("z-unStartBtn")
                }
            }else{
                $("#j-fellInLove-btn").text("登录查看我的投资额")
                    .attr("href", $('#j-login').attr('href'))
            }

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
});

