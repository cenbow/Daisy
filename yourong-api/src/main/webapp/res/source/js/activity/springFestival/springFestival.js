define(['base'],function (require, exports, module) {
    var base=require('base'),
        initTime = new Date().getTime();
    // tab
    $('#j-tab-nav li').on('click', function() {
        $(this).siblings('li').removeClass('z-active');
        $(this).addClass('z-active');

        $('#j-tab-content>div').removeClass('z-active').eq($(this).index()).addClass('z-active');
    });

    var thisUrl = window.location.href;
    // 做一下注册登录的回跳
    var $login = $('#j-login'),
        $loginHref = $login.attr('href'),
        $register = $('#j-register'),
        $registerHref = $register.attr('href');

    $login.attr('href', $loginHref + '?from=' + thisUrl);
    $register.attr('href', $registerHref + '?from=' + thisUrl);

    $('#j-wish-list li').on('click', function(){
        $(this).siblings('li').removeClass('z-active');
        $(this).addClass('z-active');
    });

    var urls = {
        init: environment.globalPath + '/activity/springFestival/init',
        makeWish: environment.globalPath + '/security/activity/springFestival/makeWish',
        receiveCoupon: environment.globalPath + '/security/activity/springFestival/receive'
    };

    var content = {
        text1: '许愿完成！除夕来领压岁钱哦！',
        text2: '请选择新年愿望~',
        text3: '先许愿，再领券，除夕开领哦~',
        text4: '先去许下新年愿望再来领压岁钱~',
        text5: '领取成功！祝您猴年大发！',
        text6: '一年只能领一份压岁钱哦~',
        text7: '领压岁钱的人太多啦！稍后再来吧！'
    };

    base.getAPI({
        url: urls.init,
        callback: function(data){
            if(data.success) {
                //console.log('init result', data.result);
                init(data.result);
            }
        }
    });

    // 弹出框
    var $mask = $('#j-mask'),
        $msgWindow = $('#j-msgWindow'),
        docHeight = $(document).height();
    $mask.css('height', docHeight);

    function showDialog(msg){
        $mask.show();
        $msgWindow.text(msg).show();
        var timer = setTimeout(function(){
            $mask.hide();
            $msgWindow.hide();
        },2000);
        $mask.click(function(){
            $mask.hide();
            $msgWindow.hide();
            clearTimeout(timer);
        });
    }

    function init(result){
        var activityStatus = result.activityStatus,
            $btn1 = $('#j-btn1'),
            $btn2 = $('#j-btn2'),
            $exchangeBtn = $('.j-exchange-btn'); // 马上兑换按钮

        var receiveStartTime = result.receiveStartTime,
            hasMakeWish = result.hasMakeWish,
            receiveEndTime = result.receiveEndTime,
            envTime = environment.serverDate - 0;

        // 已有多少人领取压岁钱
        if(activityStatus > 2) {
            if(result.receiveCouponNum !== null
                && !(result.receiveCouponNum < 0)
                && envTime > receiveStartTime){
                $('#j-count2').text(result.receiveCouponNum);
                $('#j-pink-title').addClass('z-visible');
            }
        }

        if(activityStatus == 2) {
            //console.log('活动状态 2');
            $btn1.addClass('z-current');
            $btn2.on('click', function(){showDialog(content.text3);});
            $exchangeBtn.addClass('z-current').removeAttr('href');
        } else if(activityStatus == 4) {
            var logined = $('#j-tab-nav').attr('data-logined');

            if(logined == true){
                $btn1.on('click', function() {
                    if($('#j-wish-list li.z-active').length == 0){
                        showDialog(content.text2);
                    } else {
                        var $this = $(this),
                            clickGutter = 1000,
                            // 防止多次点击
                            thisClickTime = (new Date).getTime();
                        if(thisClickTime - (+$this.attr('data-clickBegin') || 0) > clickGutter) {
                            $this.attr('data-clickBegin', thisClickTime)
                            $btn1.attr('data-hasMakeWish', 1);
                            var msgId = $('#j-wish-list li.z-active').attr('data-msgId');
                            base.getAPI({
                                url: urls.makeWish+'?msgId='+msgId,
                                callback: function(data){
                                    if(data.success){
                                        showDialog(content.text1);
                                    }
                                }
                            })
                        } else {
                            return false;
                        }
                    }
                });

                if(result.hasReceiveCoupon) {$btn2.attr('data-hasReceiveCoupon', true);}

                $btn2.on('click', function() {
                    var $this = $(this),
                        clickTime = new Date().getTime(),
                        gutter = clickTime - initTime + envTime,
                        clickGutter = 1000,
                        // 防止多次点击
                        thisClickTime = (new Date).getTime();
                    if(thisClickTime - (+$this.attr('data-clickBegin') || 0) > clickGutter) {
                        $this.attr('data-clickBegin', thisClickTime);

                        if (gutter < receiveStartTime) {
                            showDialog(content.text3); // text3: '先许愿，再领券，除夕开领哦~',
                        } else if (gutter > receiveEndTime) {
                            showDialog('领取已结束');
                        } else {
                            if ($this.attr('data-hasReceiveCoupon') == 'true' || result.hasReceiveCoupon) {
                                showDialog(content.text6); // 一年只能领一份压岁钱哦
                            } else {
                                if ($('#j-wish-list li.z-active').length == 0 && !result.hasMakeWish) {
                                    showDialog(content.text4); // 先去许下新年愿望再来领压岁钱~
                                } else {
                                    // 之前许过愿望 或者这个页面进来的时候许过愿望
                                    if (result.hasMakeWish || $btn1.attr('data-hasMakeWish') == 1) {
                                        base.getAPI({
                                            url: urls.receiveCoupon,
                                            callback: function (data) {
                                                if (data.success) {
                                                    showDialog(content.text5); // 领取成功！祝您猴年大发！
                                                    $btn2.attr('data-hasReceiveCoupon', true);
                                                } else {
                                                    // 这里也要加一层判断，判断压岁钱券是否被领完
                                                    if (data.resultCodes
                                                        && data.resultCodes.length > 0
                                                        && (data.resultCodes[0].type - 0) == 2) {
                                                        showDialog(content.text6); // 一年只能领一份压岁钱哦
                                                    } else {
                                                        showDialog(content.text7); // 领压岁钱的人太多啦！稍后再来吧！
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        showDialog(content.text4); // 先去许下新年愿望再来领压岁钱~
                                    }
                                }
                            }
                        }
                    } else {
                        return false;
                    }
                });
            } else {
                function goLogin(){
                    var loginHref = $login.attr('href');
                    window.location.href = loginHref;
                }
                $btn1.on('click',goLogin);
                $btn2.on('click', goLogin);
            }

            if(envTime > receiveEndTime){
                $btn2.text('猴年大吉').addClass('z-current').unbind('click');
            }

            // 已发出XX个红包，已有XX人领取红包
            var totalNum = result.totalNum,
                claimNum = result.claimNum;
            if(!totalNum) {totalNum = 0}
            if(!claimNum) {claimNum = 0}
            if((totalNum > 0 || totalNum == 0) && (claimNum > 0 || claimNum == 0)){
                $('#j-totalNum').text(totalNum);
                $('#j-claimNum').text(claimNum);
                $('#j-nums').show();
            }
        } else {
            //console.log('活动状态 6');
            $btn1.text('猴年大吉').addClass('z-current');
            $btn2.text('猴年大吉').addClass('z-current');
            $exchangeBtn.text('猴年大吉').addClass('z-current').removeAttr('href');

            // 已发出XX个红包，已有XX人领取红包
            var totalNum = result.totalNum,
                claimNum = result.claimNum;
            if(!totalNum) {totalNum = 0}
            if(!claimNum) {claimNum = 0}
            if((totalNum > 0 || totalNum == 0) && (claimNum > 0 || claimNum == 0)){
                $('#j-totalNum').text(totalNum);
                $('#j-claimNum').text(claimNum);
                $('#j-nums').show();
            }
        }
    }
});
