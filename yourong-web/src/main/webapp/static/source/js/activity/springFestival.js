(function(){
    "use strict";
    // base 页面效果
    // 飘雪
    $(document).snowfall({
        image: '/static/img/activity/springFestival/flake.png',
        flakeCount : 180,
        minSize: 10,
        maxSize: 40
    });

    // 右边的导航菜单悬浮滚动等效果
    var $jSideMenu = $("#j-toggle-sideMenu");

    $('#j-toggle-sideMenu a').on('click', function(){
        $(this).removeClass('z-current');
        $('html, body').animate({
            scrollTop: $($(this).attr('data-href')).offset().top - 120
        }, 500);
    });

    var scrollTop,
        jSideMenuA = $jSideMenu.find('a'),
        $window = $(window);

    function highlightNav(i){
        jSideMenuA.removeClass('z-current').eq(i).addClass('z-current');
    }

    $window.on('scroll', function() {
        scrollTop = $window.scrollTop();
        scrollTop >600 ? $jSideMenu.fadeIn(): $jSideMenu.fadeOut();
        if(scrollTop > 600 && scrollTop < 1300 ) {
            highlightNav(0);
        } else if(scrollTop > 1301 && scrollTop < 1900) {
            highlightNav(1);
        } else if(scrollTop > 1901 && scrollTop < 2500){
            highlightNav(2);
        } else if(scrollTop > 2501){
            highlightNav(3);
        }
    });

    var $shareDiv = $('.u-share>div');
    $('.j-popup-trigger').hover(function(){
        $shareDiv.hide();
        $(this).next().toggle();
    }, function(){
        $shareDiv.hide();
    });

    // 需要与后台接轨了
    var $jChoose = $('#j-act1-choose'), // 选择愿望
        $openDialog1 = $('#j-openDialog1'), // 许愿留言按钮
        $openDialog2 = $('#j-openDialog2'), // 领压岁钱按钮
        $dialog1 = $('#j-dialog1'),
        $dialog2 = $('#j-dialog2'),
        $closeDialog = $('.j-closeDialog'),

    // 渲染数据
        domain = env.globalPath,
        urls = {
            init: domain + '/activity/springFestival/init',
            makeWish: domain + '/activity/springFestival/makeWish',
            receiveCoupon: domain + '/activity/springFestival/receive'
        },
        content = {
            text1: '<li class="u-act-noData" style="margin-top: 50px;padding-left: 110px">快来许愿吧</li>',
            text2: '<li class="u-act-noData">新年新气象，快来辞旧迎新哦</li>',
            text3: '<li class="u-act-noData u-act4-noData">猴年大吉，万事如意！</li>',
            text4: '<li class="u-act-noData">活动期间兑换并使用现金券，<br/>享受“辞旧迎新”返利</li>'
        },
        initTime = new Date().getTime(); // 页面加载的时候有个时间。需要在后面 duringActiveLogined 里面使用;

    $.xPost({
        url: urls.init,
        callback: function(data){
            if(data.success) {
                init(data.result);
            } else {
                $('#j-act1-list').html(content.text1);
                $('#j-act2-list').html(content.text2);
                $('#j-act4-list').html(content.text3);
            }
        }
    });

    // 用于给数字添加千分号
    function commafy(num) {
        num = num + "";
        var re = /(-?\d+)(\d{3})/;
        while (re.test(num)) {
            num = num.replace(re, "$1,$2");
        }
        return num;
    }
    // 渲染列表
    function renderData(result){
        if(result){
            // act1 许愿墙渲染, 如果没有活动开始，但是之前还没人许愿
            if(result.wishList && result.wishList.length>0) {
                $('#j-act1-list').html(template('j-act1-list-tpl', result));
                $('#j-act1-choose').show();
            } else {
                $('#j-act1-list').html(content.text1);
            }

            // act1-right ?? cc 这里需要记录一下， env.serverDate要大于 receiveTime才行，之前少了这个条件
            var receiveCouponNum = result.receiveCouponNum,
                receiveStartTime = result.receiveStartTime,
                receiveEndTime = result.receiveEndTime;
            if(env.serverDate > receiveStartTime
                && result.activityStatus != 2
                && receiveCouponNum > 0) {
                $('#j-act1-text').html('已有<span class="f-bold-red">'+ receiveCouponNum +'人</span>成功领取压岁钱');
            } else {
                $('#j-act1-text').html('许个愿，除夕来领券哟~')
            }

            if(env.serverDate > receiveEndTime) {
                $('#j-dialog1-title').hide(); // 对话框1取消那句 ‘除夕来领压岁钱哦’
                // 把活动1模块移到最后，也要处理一下右边的菜单栏
                $('#act1').insertAfter('#act4');
                $('#j-toggle-sideMenu').find('a:eq(0)').insertAfter($('#j-toggle-sideMenu').find('a:eq(3)'));
                $(window).on('scroll', function() {
                    scrollTop = $window.scrollTop();
                    scrollTop >600 ? $jSideMenu.fadeIn(): $jSideMenu.fadeOut();
                    if(scrollTop > 600 && scrollTop < 1100 ) {
                        highlightNav(1);
                    } else if(scrollTop > 1101 && scrollTop < 1900) {
                        highlightNav(2);
                    } else if(scrollTop > 1901 && scrollTop < 2500){
                        highlightNav(3);
                    } else if(scrollTop > 2501){
                        highlightNav(0);
                    }
                });

                $('#j-openDialog2').text('猴年大吉');
            }

            // act2
            if(result.rechargePopularityList && result.rechargePopularityList.length > 0) {
                $('#j-act2-list-title').show();
                $('#j-act2-list').html(template('j-act2-list-tpl', result));
            } else {
                if(result.activityStatus == 2){
                    //$('#j-act2-list').html(content.text4)
                    $('#j-act2-list').hide();
                    $('#j-act2-noData').removeClass('hide');
                } else if(result.activityStatus == 4) {
                    $('#j-act2-list').html(content.text2)
                }
            }

            // act3 title
            if(result.activityStatus != 2) {
                var claimNum = result.claimNum || 0,
                    totalNum = result.totalNum || 0;
                $('#j-act3-text').html('已发出<span class="f-bold-red">'+ totalNum + '</span>个红包，已有<span class="f-bold-red">'+ claimNum +'人</span>领取红包').show();
            }

            // act4 left
            if(result.activityStatus == 4 && result.recommendProject!==null) {
                $('#j-act4-left').html(template('j-act4-left-tpl', result));
                var onlineTime = result.recommendProject.onlineTime;
                if(onlineTime > env.serverDate) {
                    $('#j-act4-btn').addClass('z-current');
                    $('#j-timer2').attr('data-time', onlineTime);

                    $('#j-timer2').timeCountdown($(this).attr('data-time'), function(){
                        $('#j-act4-btn').text('我要万事如意').removeClass('z-current').attr('href',  '/products/detail-' + result.recommendProject.id + '.html');
                    })
                } else {
                    $('#j-act4-btn').text('我要万事如意').removeClass('z-current').attr('href',  '/products/detail-' + result.recommendProject.id + '.html')
                }
            } else {
                if(result.activityStatus == 2) {
                    $('#j-act4-btn').text('活动未开始').addClass('z-current')
                } else {
                    $('#j-act4-btn').text('正在赶来的路上').addClass('z-current')
                }
            }

            // act4,金钱数字需要添加千分号
            if(result.sprProInvestList && result.sprProInvestList.length > 0) {
                $.each(result.sprProInvestList, function(index, val){
                    val.chip = commafy(val.chip);
                });
                $('#j-act4-list-title').show();
                $('#j-act4-list').html(template('j-act4-list-tpl', result));
            } else {
                $('#j-act4-list').html(content.text3)
            }

            // 让数据滚动
            $('#j-act1-list').scrollList({
                size: 3,
                height: 50,
                length: 1,
                time: 5000,
                stoppable: true
            });
            $('#j-act2-list, #j-act4-list').scrollList({
                size: 5,
                height: 50,
                length: 1,
                time: 5000,
                stoppable: true
            });

            // 圆环处理
            var $pstat = $('.j-pl-stat');
            if ($pstat.length > 0) {
                $("body").append("<canvas id='j-canvas-demo'></canvas>");
                try {
                    $("canvas")[0].getContext("2d");
                    $pstat.circliful();
                } catch (err) {}
                $("#j-canvas-demo").remove();
            }
        } else {
            $('#j-act1-list').html(content.text1);
            $('#j-act2-list').html(content.text2);
            $('#j-act4-list').html(content.text3);
        }
    }

    function init(result){
        // 处理整体的逻辑了
        var logined = $('#top').data('logined'),
            activityStatus = result.activityStatus, // [2, 4, 6]
            receiveStartTime = result.receiveStartTime, // 118 领取开始时间
            hasReceiveCoupon = result.hasReceiveCoupon, // 118 是否领取
            $act4Btn = $('#j-act4-btn');

        // 渲染数据
        renderData(result);
        if(logined === false) {
            switch (activityStatus) {
                case 2:
                    beforeActiveNotLogined(result);
                    break;
                case 4:
                    duringActiveNotLogined(result);
                    break;
                case 6:
                    afterActiveNotLogined(result);
                    break;
                default : (function(){})();
            }
        }else if(logined === true) {
            switch (activityStatus){
                case 2:
                    beforeActiveLogined(result);
                    break;
                case 4:
                    duringActiveLogined(result);
                    break;
                case 6:
                    afterActiveLogined(result);
                    break;
                default : (function(){})();
            }
        }

        //截取act4字符串
        $('#j-act4-list li').each(function(){
            var oldText = $(this).find('a').text(),
                newText = oldText.substring('0', oldText.indexOf('期') + 1);
            $(this).find('a').text(newText);
        })
    }

    // 未登录的处理 活动前
    function beforeActiveNotLogined(result){
        //console.log('beforeActiveNotLogined');
        // act1
        $('#j-act1-wishWall').hide();
        $('#j-wishWall').show();
        //$('#j-act1-text').html('许个愿，除夕来领券哟~');
        $openDialog1.addClass('z-current');
        $openDialog2.text('领取未开始').addClass('z-current');

        // act2
        $('#j-exchange-cashCoupon').text('活动未开始').addClass('z-current')
            .css('margin-top', '60px').removeAttr('target');
        $('#j-act2-list-title').hide();
        //$('#j-act2-list').html(content.text4);
        $('#j-act2-list').hide();
        $('#j-act2-noData').removeClass('hide');
        $('#j-act2-count').hide();

        // act3
        $('#j-act3-text').hide();

        // act4
        $('#j-act4-btn').text('活动未开始').addClass('z-current').removeAttr('href target');
        $('#j-act4-list-title').hide();
        $('#j-act4-list').html(content.text3);
    }

    // 活动中
    function goLoginPage() {
        $.xPost({ url: urls.makeWish });
    }

    function duringActiveNotLogined(result){
        //console.log('duringActiveNotLogined');
        $jChoose.show().find('span').css('cursor', 'pointer')
            .on('click',function(){
                $(this).addClass('z-choosed').siblings().removeClass('z-choosed');
            });
        $openDialog1.on('click', goLoginPage);

        var receiveStartTime = result.receiveStartTime;

        if(receiveStartTime > env.serverDate) {
            $openDialog2.addClass('z-current');
            $('#j-timer1').attr('data-time', receiveStartTime);
            $('#j-timer1').timeCountdown($(this).attr('data-time'), function(){
                $openDialog2.removeClass('z-current').html('领压岁钱').on('click', goLoginPage);
            })
        } else if(env.serverDate > result.receiveEndTime) {
            $('#j-openDialog2').text('猴年大吉').addClass('z-current');
        } else {
            $openDialog2.removeClass('z-current').html('领压岁钱').on('click', goLoginPage);
        }

        $('#j-act2-count').hide();
        $('#j-exchange-cashCoupon').text('登录查看人气值')
            .css('margin-top', '60px').removeAttr('href target')
            .on('click', goLoginPage);
        // act4, 判断车子推荐活动的状态码 30-50直接，我要万事如意，50以上 履约中 cc
        if(result && !!result.recommendProject && !!result.recommendProject.status){
            if((result.recommendProject.status - 0 > 29) && (result.recommendProject.status - 0 < 49) ){
                $('#j-act4-btn').text('我要万事如意').removeClass('z-current').attr('href',  '/products/detail-' + result.recommendProject.id + '.html');
            }
            if(result.recommendProject.status - 0 > 49 && (result.recommendProject.status - 0 < 70)){
                $('#j-act4-btn').text('履约中').addClass('z-current').removeAttr('href target');
            }
            if((result.recommendProject.status - 0) == 70){
                $('#j-act4-btn').text('已还款').addClass('z-current').removeAttr('href target');
            }
        }
    }

    // 活动结束
    function afterActiveNotLogined(result){
        //console.log('afterActiveNotLogined');
        $('#j-act1-choose').show();
        $openDialog1.text('猴年大吉').addClass('z-current');
        $openDialog2.text('猴年大吉').addClass('z-current');

        $('#j-act2-count').hide();
        $('#j-exchange-cashCoupon').text('活动已结束').addClass('z-current')
            .css('margin-top', '60px').unbind('click');

        $('#j-act4-btn').text('活动已结束').addClass('z-current');
    }

    // 登录以后的处理
    // 活动前
    function beforeActiveLogined(result){
        //console.log('beforeActiveLogined');
        // act1
        $('#j-act1-wishWall').hide();
        $('#j-wishWall').show();
        $openDialog1.addClass('z-current');
        $openDialog2.text('领取未开始').addClass('z-current');
        $('#j-act1-text').html('许个愿，除夕来领券哟~');
        // act2
        $('#j-bar-red').addClass('z-current');

        $('#j-act2-count').hide();
        $('#j-exchange-cashCoupon').text('活动未开始').addClass('z-current')
            .css('margin-top', '60px').removeAttr('target').unbind('click');
        $('#j-act2-list-title').hide();
        $('#j-act2-list').hide();
        $('#j-act2-noData').removeClass('hide');

        // act3
        $('#j-act3-text').hide();

        // act4
        $('#j-act4-btn').text('活动未开始').addClass('z-current').removeAttr('href target');
        $('#j-act4-list-title').hide();
        $('#j-act4-list').html(content.text3);
    }

    // 活动中
    function duringActiveLogined(result){
        //console.log('duringActiveLogined');
        // act1-left
        $jChoose.show().find('span').css('cursor', 'pointer')
            .on('click',function(){
                $(this).addClass('z-choosed').siblings().removeClass('z-choosed');
            });

        var $makeWishBtn = $('#j-makeWishBtn');
        if(result.hasMakeWish) {
            $makeWishBtn.attr('data-hasMakeWish', 1);
        }

        var msgId;
        $openDialog1.on('click', function(){
            var $this = $(this),
                clickGutter = 1000,
            // 防止多次点击
                thisClickTime = (new Date).getTime();
            if(thisClickTime - (+$this.attr('data-clickBegin') || 0) > clickGutter) {
                $this.attr('data-clickBegin', thisClickTime);
                if ($jChoose.find('.z-choosed').length > 0) {
                    msgId = $jChoose.find('span.z-choosed').attr('data-msgId');
                    $.xPost({
                        url: urls.makeWish,
                        data: {'msgId': msgId},
                        callback: function (data) {
                            if (data.success) {
                                $makeWishBtn.attr('data-hasMakeWish', 1);
                                $dialog1.fadeIn();
                                xShade('show');
                            }
                        }
                    });
                } else {
                    $.xDialog({content: '请先选择愿望哦~'})
                }
            } else {
                return false;
            }
        });

        var receiveStartTime = result.receiveStartTime,
            receiveEndTime = result.receiveEndTime,
            hasReceiveCoupon = result.hasReceiveCoupon;
        $openDialog2.addClass('z-current').attr('data-hasReceiveCoupon', result.hasReceiveCoupon);
        // 这里表示118元活动开始了
        function receiveStart(){
            $openDialog2.removeClass('z-current').html('领压岁钱').on('click', function(){
                var clickTime = new Date().getTime(),
                    gutter = clickTime - initTime + env.serverDate,
                    $this = $(this),
                    clickGutter = 1000,
                    // 防止多次点击
                    thisClickTime = (new Date).getTime();
                if(thisClickTime - (+$this.attr('data-clickBegin') || 0) > clickGutter) {
                    $this.attr('data-clickBegin', thisClickTime);

                    console.log('result是 ', result);
                    if (gutter > receiveEndTime) {
                        $.xDialog({content: '领取已结束'});
                    } else {
                        if (result.hasReceiveCoupon == true || $('#j-openDialog2').attr('data-hasReceiveCoupon') == "true") {
                            $.xDialog({content: '一年只能领一份压岁钱哦~快去用掉你的压岁钱吧！'});
                        } else {
                            if (($jChoose.find('.z-choosed').length > 0
                                && $('#j-makeWishBtn').attr('data-hasMakeWish') == 1)
                                || result.hasMakeWish) {
                                $.xPost({
                                    url: urls.receiveCoupon,
                                    callback: function (data) {
                                        if (data.success) {
                                            $dialog2.fadeIn();
                                            xShade('show');
                                            $('#j-openDialog2').attr('data-hasReceiveCoupon', true);
                                        } else {
                                            // 这里多一项判断
                                            if ($.isArray(data.resultCodeEum) && (data.resultCodeEum[0].type - 0) === 2) {
                                                $.xDialog({content: '一年只能领一份压岁钱哦~快去用掉你的压岁钱吧！'});
                                            } else {
                                                $.xDialog({content: '领压岁钱的人太多啦！稍后再来吧！'})
                                            }
                                        }
                                    }
                                });
                            } else {
                                $.xDialog({content: '先去隔壁许下新年愿望再来领压岁钱~'})
                            }
                        }
                    }
                } else {
                    return false;
                }
            });
        }
        if(receiveStartTime > env.serverDate) {
            // 活动未开始
            $('#j-timer1').attr('data-time', receiveStartTime);
            $('#j-timer1').timeCountdown($(this).attr('data-time'), receiveStart);
        } else if(env.serverDate > result.receiveEndTime){
            $('#j-openDialog2').text('猴年大吉').addClass('z-current');
        } else {
            receiveStart();
        }

        $closeDialog.on('click', function(){
            $(this).parent('div').fadeOut();
            xShade("hide");
        });

        $('body').on("click", ".u-shade", function () {
            xShade("hide");
            $dialog1.fadeOut();
            $dialog2.fadeOut();
        });

        // act2圆环
        $('#j-act2-count').show();
        var couponNum = Math.ceil(($('#j-coupon-num').text() - 0) / 1000);
        if(couponNum == 0) { couponNum = '000' }
        $('#j-coupon').attr('src', '/static/img/member/coupon/'+ couponNum +'.png');
        // 跳转到我的优惠页面
        $('#j-exchange-cashCoupon').text('兑换现金券');

        // act4, 判断车子推荐活动的状态码 30-50直接，我要万事如意，50以上 履约中
        if(result.activityStatus == 4) {
            if(result && !!result.recommendProject && !!result.recommendProject.status) {
                if((result.recommendProject.status - 0 > 29) && (result.recommendProject.status - 0 < 49) ){
                    $('#j-act4-btn').text('我要万事如意').removeClass('z-current').attr('href',  '/products/detail-' + result.recommendProject.id + '.html');
                }
                if(result.recommendProject.status - 0 > 49 && result.recommendProject.status - 0 < 70){
                    $('#j-act4-btn').text('履约中').addClass('z-current').removeAttr('href target');
                }else if((result.recommendProject.status - 0) == 70){
                    $('#j-act4-btn').text('已还款').addClass('z-current').removeAttr('href target');
                }
                var onlineTime = result.recommendProject.onlineTime;
                if(onlineTime > env.serverDate) {
                    $('#j-act4-btn').addClass('z-current');
                    $('#j-timer2').attr('data-time', onlineTime);

                    $('#j-timer2').timeCountdown($(this).attr('data-time'), function(){
                        $('#j-act4-btn').text('我要万事如意').removeClass('z-current').attr('href',  '/products/detail-' + result.recommendProject.id + '.html');
                    })
                } else {
                    $('#j-act4-btn').text('我要万事如意').removeClass('z-current').attr('href',  '/products/detail-' + result.recommendProject.id + '.html')
                }
            } else {
                $('#j-act4-btn').text('正在赶来的路上');
            }
        }

        if(result && !!result.recommendProject && !!result.recommendProject.status){
            if(result.recommendProject.status - 0 > 49 && result.recommendProject.status - 0 < 70){
                $('#j-act4-btn').text('履约中').addClass('z-current').removeAttr('href target');
            } else if((result.recommendProject.status - 0) == 70){
                $('#j-act4-btn').text('已还款').addClass('z-current').removeAttr('href target');
            }
        }
    }

    // 活动结束
    function afterActiveLogined(){
        //console.log('afterActiveLogined');
        $openDialog1.text('猴年大吉').addClass('z-current');
        $openDialog2.text('猴年大吉').addClass('z-current');
        $('#j-act2-count').hide();
        $('#j-bar-red').addClass('z-current');
        $('#j-exchange-cashCoupon').text('活动已结束').addClass('z-current').css('margin-top', '60px').removeAttr('target').unbind('click');
        $('#j-act4-btn').text('活动已结束').addClass('z-current').removeAttr('href target');
    }

    //倒计时扩展
    $.fn.extend({
        timeCountdown: function (timestamp, callback) {
            this.each(function () {
                var _this = $(this),
                    time = ((_this.data('time') || timestamp) - env.serverDate) / 1000; // cc change a little

                if (time > 0) {
                    _this.text(getCountTime(time));
                    var timer = setInterval(function () {
                        _this.text(getCountTime(time - 1));
                        time = time - 1;

                        if (time <= 0) {
                            clearInterval(timer);
                            callback();
                        }
                    }, 1000);
                }
            });
            function getCountTime(seconds) {
                if (seconds < 0) {
                    return 0;
                }
                var timeArray = [
                    Math.floor(seconds / 86400) + '' || '00',
                    seconds >= 86400 ? Math.floor(seconds % 86400 / 3600) :
                    Math.floor(seconds / 3600) || '00',
                    Math.floor((seconds / 3600) % 1 * 60) || '00',
                    Math.floor(seconds % 60) || '00'
                ];
                var timeString;
                if(timeArray[0] > 0){
                    timeString = timeArray[0] + '天';
                } else {
                    for (var i = 0; i < timeArray.length; i++) {
                        if (timeArray[i] < 10 && timeArray[i] !== '00') {
                            timeArray[i] = '0' + timeArray[i];
                        }
                    }
                    timeArray.shift();
                    timeString = timeArray.join(":");
                }

                return timeString;
            }
        }
    });
//现金券兑换
    function exchangeCouponByTemplateId(templateId) {
        var xToken = $("#xToken").attr("value");
        $.xPost({
            url: environment.globalPath + "/coupon/exchange",
            data: {
                'couponTemplateId': templateId,
                'num': 1,
                'xToken': xToken
            },
            callback: function (data) {
                if (data.success) {
                    var couponName = "查看我的现金券!",url="/coupon/couponList";
                    if(data.result!=null && data.result.couponType!=null){
                        if(data.result.couponType==2){
                            couponName = "查看我的收益券";
                            url = "/coupon/profitCouponList";
                        }
                    }
                    $('body').find('.u-shade').click();
                    $.xDialog({
                        content: "优惠券兑换成功，" + "<a class='u-dialog-link' href='" + environment.globalPath + url +"' >"+couponName+"</a>",
                        type: "success", //success,warn,error,info
                        callback: function () {
                            //$(this).close();
                            window.location.href = environment.globalPath + "/activity/springFestival/index";
                        } //确认按钮回调
                    });
                } else {
                    var $repButton=$('#j-cash-button');
                    if(data.result.couponType==2){
                        $repButton=$('#j-profit-button');
                    }
                    $repButton.removeClass('z-disabled').removeProp('disabled');
                    $.xDialog({
                        content: "领用失败！",
                        type: "error"
                    });
                }
            }
        });
    }
    // 兑换优惠券
    var exchangeList = $(".j-reputation-exchange"),
        exchangeCoupon=$(".j-exchage-coupon");
    if (exchangeList!=null && exchangeList.length>0 ) {
        var repList= $(".j-rep-list");

        //兑换选择
        repList.each(function () {
            var _this=$(this),
                repButton = _this.next('div').find('.j-exc-button'),
                curPoint = _this.next('div').find('.j-exc-curPoint');
            _this.parent().appendTo('body');
            _this.on('click', 'li.z-usabled', function (e) {
                e.stopPropagation();
                $(this).addClass('z-selected').
                    siblings().removeClass('z-selected');
                var index = $(this).index();
                repButton.css('margin-left', 239 * index).show();
                curPoint.val($(this).data('templateid'));
            });

            //人气值卡片显示
            var repPoint = $('#j-cash-list').data('point');

            $('.u-rep-card').each(function () {
                var point = $(this).parent().data('point');
                if (point <= repPoint) {
                    $(this).addClass('z-card-usabled').parent().addClass('z-usabled');
                }
            });
            //$('#j-profit-list').find('.z-usabled').eq(-1).click();
            $('#j-cash-list').find('.z-usabled').eq(-1).click();
            //人气值兑换
            repButton.on('click', function () {

                if ($(this).hasClass('z-disabled')) {
                    return false;
                }

                $(this).addClass('z-disabled').prop('disabled', 'disabled');

                exchangeCouponByTemplateId(curPoint.val());

                //兑换回调
                function callback() {
                    $('body').find('.u-shade').click();
                }
            });


            $('body').on('click', '.u-shade', function () {
                xShade('hide');
                _this.parent().hide();
            });

            //隐藏全都不存在的条件
            var $disabledScopes = _this.find('.j-couponScope-disabled');
            if ($disabledScopes.length === 4) {
                $disabledScopes.remove();
            }
        });
        exchangeCoupon.on('click', function () {
            $('.j-reputation-exchange').show();
            xShade();
        });
        //关闭按钮
        $('.j-exc-close').on('click', function () {
            $('.u-shade').click();
        });
    }
})();
