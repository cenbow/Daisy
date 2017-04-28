

/* 这个页面渲染领取红包的结果 */
define(['zepto','base'],function (require, exports, module) {
    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function(e){
        e.preventDefault()
    })

    var base=require('base'),
        logined = $('#j-main').data('logined'),
        contents = {
            text1: '活动未开始，五一再来哟！',
            text2: '活动已结束！',
            text3: '红包欲来风满楼！',
            text4: '抢红包雨人太多，稍后再来吧！',
            text5: '亲，登录后才可参与抢现金券哦！',
            text6: '亲，登录后才可领取奖励哦！'
        },
        domain = environment.globalPath,
        urls = {
            receive: domain + '/activity/dynamicInvoke'
        }

    /* 获取随机数，主要用来定位红包的初始化位置 */
    function random(min, max){
        return Math.round(min + Math.random() * (max - min))
    }

    /* 给红包定位 */
    var width = $('#j-redbag-rain').width()
    $('.u-redbag-flakes').each(function() {
        var randomWidth = random(50, 80)
        $(this).css({
            'left': random(40, width + 50) + 'px', // 这里其实取得是 rain area的宽度加一些
            'width': randomWidth,
            'height': randomWidth
        })
    })

    /* app 和 M站有两套逻辑 */
    var os=$("#j-container").data("os");

    // M站的逻辑
    base.getAPI({
        url: urls.receive,
        data: { invokeMethod: 'mayDay4GiftsInit' },
        callback: function(data){
            log(data.result)

            if(data && data.success){
                var result = data.result
                if(result){
                    init(result)

                }
            } else {
                alert('后端数据有问题')
            }
        }
    });

    function init(result) {
        log('开始初始化')
        var activityStatus = result.activityStatus,
            couponForDayFlag = result.couponForDayFlag;

        // 标记今天的30元券是否领取
        $('#j-redbag-rain').attr('data-couponForDayFlag', couponForDayFlag)
        $('#j-receiveStartTime').text(result.receiveStartTime.substr(0, 5))
        $('#j-receiveEndTime').text(result.receiveEndTime.substr(0, 5))

        //activityStatus = 2
        switch (activityStatus){
            case 2:
                beforeActivity(result)
                break
            case 4:
                if(logined == true){
                    duringActivityLogined(result)
                } else {
                    duringActivityNotLogined(result)
                }
                break
            case 6:
                afterActivity(result)
                break
        }

        // 等所有元素都渲染完了，把两个模块展现出来,
        // 这里和 PC端不同，showMask需要获取红包雨高度所以不能加 z-hide
        $('#j-main').removeClass('z-fadeOut')

        setLoginGoBack()
    }

    function showMask(str){
        $('#j-mask-title').text(str)
        var height = $('#j-redbag-wrapper').height()
        $('#j-rain-area-mask').css('height', height).removeClass('z-hide')
    }

    // 做一下注册登录的回跳
    function  setLoginGoBack(){
        var thisUrl = window.location.href,
            $login = $('#j-login'),
            $loginHref = $login.attr('href'),
            $register = $('#j-register'),
            $registerHref = $register.attr('href')

        $login.attr('href', $loginHref + '?from=' + thisUrl)
        $register.attr('href', $registerHref + '?from=' + thisUrl)
    }

    // 移动端弹框如果没有按钮的话，就设置个3秒延时自动关闭
    var tempTimeout
    function showDialog(content, btnText, callback){
        clearTimeout(tempTimeout)
        $('#j-dialog-title').html(content).removeClass('z-hide')

        if(btnText && $('#j-dialog-btn')){
            $('#j-dialog-btn').text(btnText).removeClass('z-hide')
        }
        $('#j-shade, #j-dialog').removeClass('z-fadeOut')
        $('#j-shade').on('click',function(){
            $('#j-shade, #j-dialog').addClass('z-fadeOut')
            $('#j-dialog-btn').addClass('z-hide')
        })

        if(callback && os !== 1 && os !== 2){
            $('#j-dialog-btn').on('click', function(){
                callback()
                $('#j-shade, #j-dialog').addClass('z-fadeOut')
                $('#j-dialog-btn').addClass('z-hide')
            })
        } else {
            tempTimeout = setTimeout(function(){
                $('#j-shade, #j-dialog').addClass('z-fadeOut')
                $('#j-dialog-btn').addClass('z-hide')
            }, 5000)
        }
    }

    /* 未登录时访问receive接口就可以实现登录跳回了 */
    function goLoginPage1(){
        showDialog(contents.text5, '去登录', function(){
            base.getAPI({
                url: urls.receive,
                callback: function(){
                    window.location.href = $('#j-login').attr('href') + '?from=' + window.location.href
                }
            })
        })
    }

    function goLoginPage2(){
        showDialog(contents.text6, '去登录', function(){
            base.getAPI({
                url: urls.receive,
                callback: function(){
                    window.location.href = $('#j-login').attr('href') + '?from=' + window.location.href
                }
            })
        })
    }

    function beforeActivity(result){
        log('活动未开始')
        showMask(contents.text1)
        $('.j-gift-btn').text('活动未开始').addClass('z-disable')
        $('#j-gifts-wrapper').removeClass('z-hide')
    }

    function splitAmount(amount){
        if (amount >= 1000) {

            var amountStr = amount.toString(),
                size = parseInt(amountStr.length / 3),
                amountArray = amountStr.split('').reverse();

            for (var i = 1; i <= size; i++) {
                var j = i * 3 - 1;
                if (j !== amountArray.length - 1) {
                    amountArray[j] = ',' + amountArray[j];
                }
            }

            return amountArray.reverse().join('');

        } else {
            return amount;
        }
    }

    function duringActivityNotLogined(result){
        var receiveStartTime = result.receiveStartTime || '',
            receiveEndTime = result.receiveEndTime || '',
            receiveStartHour = receiveStartTime.substr(0, receiveStartTime.indexOf(':')),
            receiveStartMin = receiveStartTime.substr(receiveStartTime.indexOf(':') + 1,2),
            receiveEndHour = receiveEndTime.substr(0, receiveEndTime.indexOf(':')),
            receiveEndMin = receiveEndTime.substr(receiveEndTime.indexOf(':') + 1, 2),
            serverDate = environment.serverDate - 0,
            lastReceiveEndTime = result.lastReceiveEndTime,
            year = new Date(lastReceiveEndTime).getFullYear(),
            month = new Date(lastReceiveEndTime).getMonth() + 1,
            day = new Date(lastReceiveEndTime).getDate(),
            serverYear = new Date(serverDate).getFullYear(),
            serverMonth = new Date(serverDate).getMonth() + 1,
            serverDay = new Date(serverDate).getDate(),
            isLastDay = false, // 这个用来判断是否是活动最后一天
        // 这里直接用字符串相加之后对比
            serverDateStr = ( ('' + new Date(serverDate).getHours()) + ('' + numberAppendZero(new Date(serverDate).getMinutes())) ) - 0,
            receiveStartTimeStr =(  ('' + receiveStartHour) + ('' + numberAppendZero(receiveStartMin)) ) - 0,
            receiveEndTimeStr =(  ('' + receiveEndHour) + ('' + numberAppendZero(receiveEndMin)) ) - 0

        log(serverDateStr, receiveStartTimeStr,receiveEndTimeStr)

        if(serverYear == year && serverMonth == month && serverDay == day){
            isLastDay = true
        }

        /* 这里是活动开始了，但是没到领取时间，红包雨上也会有蒙层 */
        if((serverDateStr > receiveStartTimeStr
            || serverDateStr == receiveStartTimeStr)
            && serverDateStr < receiveEndTimeStr){
            log('活动进行中，未登录，在14点-15点之间')
            $('#j-redbag-rain').on('click', '.u-redbag-flakes', goLoginPage1)
        } else {
            if(isLastDay && (serverDateStr > receiveEndTimeStr || serverDateStr == receiveEndTimeStr)){
                log('最后一天，15点以后了')
                $('#j-three-gifts').addClass('z-onTop')
                $('#j-redbag-wrapper').addClass('z-onBottom').appendTo('#j-main')
                showMask('活动已结束！')
            } else {
                showMask(contents.text3)
            }
        }

        $('.j-gift-btn').text('等待领取').on('click',goLoginPage2)
        $('#j-gifts-wrapper').removeClass('z-hide')
    }

    function duringActivityLogined(result){
        log('活动进行中, 登录')
        var receiveStartTime = result.receiveStartTime || '',
            receiveEndTime = result.receiveEndTime || '',
            receiveStartHour = receiveStartTime.substr(0, receiveStartTime.indexOf(':')),
            receiveStartMin = receiveStartTime.substr(receiveStartTime.indexOf(':') + 1,2),
            receiveEndHour = receiveEndTime.substr(0, receiveEndTime.indexOf(':')),
            receiveEndMin = receiveEndTime.substr(receiveEndTime.indexOf(':') + 1, 2),
            serverDate = environment.serverDate - 0,
            lastReceiveEndTime = result.lastReceiveEndTime,
            year = new Date(lastReceiveEndTime).getFullYear(),
            month = new Date(lastReceiveEndTime).getMonth() + 1,
            day = new Date(lastReceiveEndTime).getDate(),
            serverYear = new Date(serverDate).getFullYear(),
            serverMonth = new Date(serverDate).getMonth() + 1,
            serverDay = new Date(serverDate).getDate(),
            isLastDay = false, // 这个用来判断是否是活动最后一天
        // 这里直接用字符串相加之后对比
            serverDateStr = ( ('' + new Date(serverDate).getHours()) + ('' + numberAppendZero(new Date(serverDate).getMinutes())) ) - 0,
            receiveStartTimeStr =(  ('' + receiveStartHour) + ('' + numberAppendZero(receiveStartMin)) ) - 0,
            receiveEndTimeStr =(  ('' + receiveEndHour) + ('' + numberAppendZero(receiveEndMin)) ) - 0

        log(serverDateStr, receiveStartTimeStr, receiveEndTimeStr)

        if(serverYear == year && serverMonth == month && serverDay == day){
            isLastDay = true
        }

        if((serverDateStr > receiveStartTimeStr
            || serverDateStr == receiveStartTimeStr)
            && serverDateStr < receiveEndTimeStr){
            /* 设置点击红包效果 */
            $('#j-redbag-rain').on('click', '.u-redbag-flakes', function(){
                var $this = $(this)
                serverDateStr = ( ('' + new Date(serverDate).getHours()) + ('' + numberAppendZero(new Date(serverDate).getMinutes())) ) - 0

                /* 判断时间是否在 14-15点之间,所以只要小时是在 14即可 */
                if((serverDateStr > receiveStartTimeStr
                    || serverDateStr == receiveStartTimeStr)
                    && serverDateStr < receiveEndTimeStr){
                    /* 判断今天的有没有领过 */
                    if($('#j-redbag-rain').attr('data-couponForDayFlag') == 'true'){
                        showDialog('恭喜您领取成功！<br>如未及时到账，请耐心等待。')
                    } else {
                        base.getAPI({
                            url: urls.receive,
                            data: {
                                invokeMethod: 'mayDay4GiftsReceive',
                                invokeParameters: 'args_couponAmount_1_integer_30'
                            },
                            callback: function(data){
                                log('第一次点击返回的数据：', data)
                                $this.addClass('z-hide')

                                if(data.success){
                                    $('#j-shade,#j-redbag-open').removeClass('z-fadeOut')
                                    $('#j-redbag-open-btn').on('click', function(){
                                        window.location.href = environment.globalPath + '/mCenter/coupon#cash'
                                    })
                                    $('#j-shade, #j-redbag-close').on('click', function(){
                                        $('#j-shade,#j-redbag-open').addClass('z-fadeOut')
                                        $('#j-redbag-rain').attr('data-couponForDayFlag', true)
                                    })
                                } else{
                                    showDialog( contents.text4 )
                                }
                            }
                        })
                    }
                } else {
                    log('还不能抢红包，服务器当前时间点')
                    // 这里需要蒙层显示的
                    showMask(contents.text3)
                }
            })

            // 活动期间，但是没到时间
        } else {
            if(isLastDay && (serverDateStr > receiveEndTimeStr || serverDateStr == receiveEndTimeStr)){
                log('最后一天，15点以后了')
                $('#j-three-gifts').addClass('z-onTop')
                $('#j-redbag-wrapper').addClass('z-onBottom').appendTo('#j-main')
                showMask('活动已结束！')
            } else {
                log('活动进行中，未登录，但不在14点-15点之间')
                showMask(contents.text3)
            }
        }

        /* 投满拿奖励 */
        var investAmount = result.investAmount - 0 || 0,
            couponForLv1 = result.couponForLv1 || '',
            couponForLv2 = result.couponForLv2 || '',
            couponForLv3 = result.couponForLv3,
            totalInvestLv1 = result.totalInvestLv1 || 0,
            totalInvestLv2 = result.totalInvestLv2 || 0,
            totalInvestLv3 = result.totalInvestLv3 || 0

        /* 当天累计投资多少钱 */
        if($('#j-investAmout')){
            $('#j-investAmout').text(splitAmount(investAmount))
            $('.u-total-count').removeClass('z-hide')
        }

        /* 三重礼红包状态和按钮状态 */
        if(couponForLv1 == true){
            $('#j-redbag-88').addClass('z-hide')
            $('#j-redbag88-open').removeClass('z-hide')
            $('#j-gift-btn-88').text('您已领取').addClass('z-disable')
        } else {
            $('#j-gift-btn-88').text('等待领取').on('click', function(){
                getGift(investAmount, 88, totalInvestLv1)
            })
        }

        if(couponForLv2 == true){
            $('#j-redbag-188').addClass('z-hide')
            $('#j-redbag188-open').removeClass('z-hide')
            $('#j-gift-btn-188').text('您已领取').addClass('z-disable')
        } else {
            $('#j-gift-btn-188').text('等待领取').on('click', function(){
                getGift(investAmount, 188, totalInvestLv2)
            })
        }

        if(couponForLv3 == true){
            $('#j-redbag-500').addClass('z-hide')
            $('#j-redbag500-open').removeClass('z-hide')
            $('#j-gift-btn-500').text('您已领取').addClass('z-disable')
        } else {
            $('#j-gift-btn-500').text('等待领取').on('click', function(){
                getGift(investAmount, 500, totalInvestLv3)
            })
        }

        /**
         *  在有资格领券的时候的执行的函数
         *  @param investAmount 累计投资总额
         *  @param couponAmount 券的类型
         *  @param limit 满足条件投资限额
         */
        function getGift(investAmount, couponAmount, limit) {
            if(!$('#j-gift-btn-' + couponAmount).hasClass('z-disable')){
                // 有资格领取
                if(investAmount > limit || investAmount == limit){
                    base.getAPI({
                        url: urls.receive,
                        data: {
                            invokeMethod: 'mayDay4GiftsReceive',
                            invokeParameters: 'args_couponAmount_1_integer_' + couponAmount
                        },
                        callback: function(data){
                            var success = data.success || false
                            if(data && success ){
                                showDialog('恭喜获得1张'+ couponAmount +'元现金券，继续投资，获取更多奖励吧！')

                                $('#j-gift-btn-' + couponAmount).text('您已领取').addClass('z-disable')
                                    .parent().find('img').toggleClass('z-hide')
                            } else {
                                showDialog( '领取失败，请稍后再次领取！' )
                            }
                        }
                    })
                } else {
                    showDialog('快去投资获取奖励吧！', '立即投资', function(){
                        window.location.href = environment.globalPath + '/products/m/list-all-all-1.html'
                    })
                }
            }
        }

        /* 设置完三个按钮的状态之后，在展示 */
        $('#j-gifts-wrapper').removeClass('z-hide')
        var couponForDayFlag = $('#j-redbag-rain').attr('data-couponForDayFlag')
        // 如果当天领过 30元券，把30元券整块移到下面
        if(couponForDayFlag == 'true'){
            $('#j-three-gifts').addClass('z-onTop')
            $('#j-redbag-wrapper').addClass('z-onBottom').appendTo('#j-main')
        }
    }

    function afterActivity(result){
        log('活动结束')
        showMask(contents.text2)
        $('.j-gift-btn').text('活动已结束').addClass('z-disable')
        $('#j-gifts-wrapper').removeClass('z-hide')
    }

    /**
     * 小于10在数字前追加0
     * @param num
     * @returns {String}
     */
    function numberAppendZero(num) {
        num = num - 0
        if (num < 10) {
            num = "0" + num;
        }
        return num;
    }

})
