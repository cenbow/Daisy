/* 破二十亿活动 */

define(['zepto','base','template'],function (require, exports, module) {
    var template = require('template'),
        base = require('base'),
        data=$("#j-result-data").data("result"),
        os=$("#j-result-os").data("os"),
        mKey = $('#j-result-mKey').data('mkey'),
        logined=$("#j-checkNum").data("login"),
        encryptionId=getUrlParam("encryptionId") || ''

    if(os===1||os===2){
        $(".g-ft").addClass("f-dn")
    }

    var debug = getUrlParam('debug') || '' // cc add to debug
    if(debug){
        console.log(data)
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
        // alert(data)
        // alert(JSON.stringify(data))
        // 点击抽奖
        var ROTATE_OFFSET=20,
        // 这里前面的参数是 360度除以每个等分这里是 8等分
            giftAngleList = {
                'popularityFor8': 135-ROTATE_OFFSET,
                'popularityFor18': 90-ROTATE_OFFSET,
                'popularityFor58': 45-ROTATE_OFFSET,
                'popularityFor118': -ROTATE_OFFSET,
                'annualizedFor0.005': -45-ROTATE_OFFSET,
                'annualizedFor0.01': -90-ROTATE_OFFSET,
                'couponFor100': -135-ROTATE_OFFSET,
                'couponFor200': 180-ROTATE_OFFSET
            },
            num = Math.random(),
            factor = Math.round(num) ? 1 : -1,
            point = num * factor * 12

        window.appReturnData = JSON.parse(data);
        var data = window.appReturnData
        var activityPart = data.result.activityPart

        if(activityPart == 1){
            var lotteryRewardCode =  data.result.lotteryRewardCode || '',
                lotteryRewardName =  data.result.lotteryRewardName || ''

            // alert(lotteryRewardCode)

            if( data.success){
                $('#j-lottery-rotate').rotate({
                    angle: 0,
                    animateTo: 360 * 5 + giftAngleList[lotteryRewardCode] + point, // 至少转四圈
                    duration: 4000,
                    callback: function () {
                        $('#j-lottery-arrow').data('status', 1)
                        alert( '恭喜你！获得'+ lotteryRewardName  +'，稍后可在账户中查看')
                    }
                });
            } else {
                if ( data.resultCodes[0].code == '90724') {
                    alert('还差一点，快去投资获得机会~')
                } else {
                    alert('抱歉，当前抽奖人数过多，请稍后再试')

                    if(debug){
                        alert(data.resultCodes[0].msg)
                        console.log('系统出错',data)
                    }
                }
            }

        } else if(activityPart == 2) {
            if(data && data.result && data.result.giftIndex > -1){
                var giftIndex =  data.result.giftIndex
                var tempText = ''
                switch (data.result.giftIndex) {
                    case 0:
                        tempText = '恭喜您！获得100元现金券，0.5%收益券'
                        break;
                    case 1:
                        tempText = '恭喜您！获得200元现金券，1%收益券'
                        break
                    case 2:
                        tempText = '恭喜您！获得200元现金券，1.5%收益券'
                        break
                    case 3:
                        tempText = '恭喜您！获得500元现金券，2%收益券'
                        break
                    default:
                        break
                }
                alert(tempText)
                $('#j-section2-btn').text('已领取').addClass('z-disabled')
                    .attr('data-hasReceiveGift', 1)
            }
        } else {
            alert('抱歉，领取现金券人数太多，请稍后再试')
            console.log(data.resultCodes[0].msg)
        }
    }

    // 切换三个tab
    $('#j-tab-menu .u-tab').on('click', function () {
        var $this = $(this),
            skin = $this.data('skin')

        $(this).addClass('z-active')
        $('#j-main').removeClass().addClass('g-main').addClass('z-' + skin)
    })

    var domain = environment.globalPath,
        urls = {
            init: environment.globalPath + '/activity/break2Billion',
            receive: environment.globalPath + '/activity/dynamicInvoke'
        }

    // 进度条
    var $progressInner = $('#j-progress-inner'),
        $progressTip = $('#j-progress-tip'),
        $progressTotal = $('#j-progress-total'),
        $progressRemain = $('#j-progress-remain'),
        $progressArrow = $('#j-progress-arrow')

    /* 登录查看我的投资额 */
    // alert(JSON.stringify(data))
    if(data.success){
        init(data.result)
    }
    function init(result){
        var activityStatus=result.activityStatus||''
        // activityStatus = 2

        /* 如果突破20亿，投资总额变成 20亿突破时间 */
        if(result.hasBreak) {
            $('#j-total').html(formatTime( result.break2BillionTime, 'yyyy年MM月dd日hh时mm分') + ',<br>交易额突破20亿')
        }

        // 登录查看我的投资额
        if(logined || encryptionId){
            if(result.activityStatus == 6){
                var text = '我的累计投资额：￥' + (amountSplit(result.memberTotalAmount  || 0)  || 0)
                $('#j-checkNum').html(text)
            } else {
                var text = '我的今日投资额：￥' + (amountSplit(result.memberDayAmount || 0) || 0) + '<br>我的累计投资额：￥' + (amountSplit(result.memberTotalAmount  || 0)  || 0)
                $('#j-checkNum').html(text).addClass('z-active')
            }
        }

        // 渲染第一块用户中奖列表
        function renderlotteryList(result){
            var activityStatus = result.activityStatus

            var lotteryList = result.lotteryList || []
            if(activityStatus == 2){
                $('#j-luckly-list').addClass('z-noData').text('活动未开始')

            } else if(activityStatus == 4 || activityStatus == 6) {
                if(lotteryList.length == 0){
                    $('#j-luckly-list').addClass('z-noData').text('—— 快来试试手气吧 ——')
                } else {
                    var tempStr = template('j-luckly-list-tpl', result)

                    $('#j-luckly-list').html(tempStr)
                }
            }
        }

        renderlotteryList(result)

        // 展示进度条和我的投资额
        $('#j-num, #j-progress-wrap,#j-checkNum').removeClass('z-fadeOut')

        switch (activityStatus){
            case 2:
                beforeActive();
                break;
            case 4:
                duringActive(result);
                break;
            case 6:
                afterActive(result);
                break;
            default:
                break;
        }

        // 渲染第三块列表
        if(activityStatus == 2){
            $('#j-ranking-wrap').html('— 敬请期待 — ').addClass('z-noData')
        } else {
            var rankList = result.rankList ||[],
                listLength=rankList.length,
                html;

            $("#j-ranking-list").removeClass("f-dn")

            if(listLength<10){
                for (var i=0;i<10-listLength;i++) {
                    rankList.push({
                        avatar: '',
                        lastUsername: "虚位以待",
                        lastTotalInvest: ""
                    })
                }
            }
            result.rankList = rankList
            html = template('j-ranking-list-tpl', result);
            $('#j-ranking-list').html(html);
        }
    }

    //活动开始之前
    function beforeActive(){
        $progressTip.css('left','55%')
        $progressArrow.css('left', '90%')

        $('#j-lottery-arrow').on('click', function () {alert('来早啦~活动未开始')})

        function beforeActiveLogin() {
            $('#j-checkNum').html('我的今日投资额：￥0<br>我的累计投资额：￥0').addClass('z-active')
        }

        if(os===1){
            if(encryptionId){
                beforeActiveLogin()
            }else {
                $('#j-checkNum, #j-lottery-arrow, #j-section2-btn').on("click", function () {
                    Android.GetEvent('break2BillionReceive', '0', '');
                })
            }

        }else if(os===2){
            if (encryptionId) {
                beforeActiveLogin()
            }else{
                $('#j-checkNum, #j-lottery-arrow, #j-section2-btn').attr("href","yrw-skip://invokeMethod=loginIn")
            }
        }else{
            if(logined){
                beforeActiveLogin()
            }else{
                $('#j-checkNum, #j-lottery-arrow, #j-section2-btn').attr("href", $('#j-login').attr('href'))
            }
        }

        // section2
        $('#j-section2-btn').text('即将开启').addClass('z-disabled')
    }

    // 活动进行中
    function duringActive(result) {

        // 渲染进度条
        var tempVal = result.fund
        if(tempVal > 1000000 ){tempVal = 1000000}
        if(tempVal < 0){tempVal = 0}
        var rate = ((tempVal / 1000000).toFixed(2)) * 100 - 100
        // 定位 tooltip
        if(rate > -78){
            if(rate < -17){

            $progressTip.css('left', rate + 77 + '%')
            } else {
                $progressTip.css('left', '60%')
            }
        }
        // 定位箭头
        if(rate > -95){
            if(rate > -3){
                $progressArrow.css('left', '96%')
            } else {
                $progressArrow.css('left', (rate + 98) +'%')
            }
        } else {
            $progressArrow.css('left', '3%')
        }
        $progressRemain.text(amountSplit(tempVal) || 0)

        if(tempVal> 0 && (tempVal < 10000 || tempVal == 10000)){
            $progressInner.css('left', '-99%')
        } else {
            $progressInner.css('left', rate + '%')
        }

        // 点击抽奖
        var ROTATE_OFFSET=20,
        // 这里前面的参数是 360度除以每个等分这里是 8等分
            giftAngleList = {
                'popularityFor8': 135-ROTATE_OFFSET,
                'popularityFor18': 90-ROTATE_OFFSET,
                'popularityFor58': 45-ROTATE_OFFSET,
                'popularityFor118': -ROTATE_OFFSET,
                'annualizedFor0.005': -45-ROTATE_OFFSET,
                'annualizedFor0.01': -90-ROTATE_OFFSET,
                'couponFor100': -135-ROTATE_OFFSET,
                'couponFor200': 180-ROTATE_OFFSET
            },
            num = Math.random(),
            factor = Math.round(num) ? 1 : -1,
            point = num * factor * 12

        // 点击箭头去登录 或者抽奖
        // 安卓
        if(os===1){
            if(encryptionId){
                // duringActiveLogin()
                $('#j-checkNum').html('我的今日投资额：￥' + (amountSplit(result.memberDayAmount || 0) || 0) + '<br>我的累计投资额：￥' + (amountSplit(result.memberTotalAmount  || 0)  || 0)).addClass('z-active')

                $('#j-lottery-arrow').on('click', function () {
                    var thisClickTime = new Date().getTime(),
                        $this = $(this)
                    if(thisClickTime - ($this.attr('data-clickbegin') - 0 || 0) > 1000) {
                        $this.attr('data-clickbegin', thisClickTime);
                        if(result.hasLottery || $this.data('status') == 1) {
                            alert( '今日已抽奖，明天再来吧！')
                        } else {
                            Android.GetEvent('break2BillionReceive', '0', 'args_activityPart_1_integer_1');
                        }
                    } else {
                        $this.attr('data-clickbegin', thisClickTime);
                        return false;
                    }
                })

            }else {
                $('#j-checkNum, #j-lottery-arrow, #j-section2-btn').on("click", function () {
                    Android.GetEvent('break2BillionReceive', '0', '');
                })
            }

        // IOS
        }
        else{
            // IOS
            if(os == 2){
                if(encryptionId){
                    // alert('登录')
                    $('#j-checkNum').html('我的今日投资额：￥' + (amountSplit(result.memberDayAmount || 0) || 0) + '<br>我的累计投资额：￥' + (amountSplit(result.memberTotalAmount  || 0)  || 0)).addClass('z-active')

                    $('#j-lottery-arrow').on('click', function () {
                        var thisClickTime = new Date().getTime(),
                            $this = $(this)
                        if(thisClickTime - ($this.attr('data-clickbegin') - 0 || 0) > 1000) {
                            $this.attr('data-clickbegin', thisClickTime);
                            if(result.hasLottery || $this.data('status') == 1) {
                                alert( '今日已抽奖，明天再来吧！')
                            } else {
                                // alert('今天还没抽奖')
                                base.getAPI({
                                    // url: urls.receive,
                                    url: environment.globalPath + '/activity/dynamicInvoke',
                                    data: {
                                        loginSource: 2,
                                        mKey: mKey, // TODO
                                        invokeMethod: 'break2BillionReceive',
                                        invokeParameters: 'args_activityPart_1_integer_1'
                                    },
                                    callback: function (data) {
                                        // alert(JSON.stringify(data))
                                        var result = data.result || {}
                                        var lotteryRewardCode = result.lotteryRewardCode || '',
                                            lotteryRewardName = result.lotteryRewardName || ''

                                        if(data.success && lotteryRewardCode){
                                            $('#j-lottery-rotate').rotate({
                                                angle: 0,
                                                animateTo: 360 * 5 + giftAngleList[lotteryRewardCode] + point, // 至少转四圈
                                                duration: 4000,
                                                callback: function () {
                                                    $('#j-lottery-arrow').data('status', 1)
                                                    alert( '恭喜你！获得'+ lotteryRewardName  +'，稍后可在账户中查看')
                                                }
                                            });
                                        } else {
                                            if(data.resultCodes[0].code === '90724'){
                                                alert('还差一点，快去投资获得机会~')
                                            } else {
                                                alert('抱歉，当前抽奖人数过多，请稍后再试')

                                                if(debug){
                                                    alert(data.resultCodes[0].msg)
                                                    console.log('系统出错',data)
                                                }
                                            }
                                        }
                                    }
                                })

                            }
                        } else {
                            $this.attr('data-clickbegin', thisClickTime);
                            return false;
                        }
                    })
                } else {
                    $('#j-checkNum, #j-lottery-arrow').attr("href","yrw-skip://invokeMethod=loginIn")
                    $('#j-section2-btn').text('立刻领取').removeClass('z-disabled').attr("href","yrw-skip://invokeMethod=loginIn")
                }

            // M站
            } else{
                if(logined){
                    $('#j-checkNum').html('我的今日投资额：￥' + (amountSplit(result.memberDayAmount || 0) || 0) + '<br>我的累计投资额：￥' + (amountSplit(result.memberTotalAmount  || 0)  || 0)).addClass('z-active')

                    $('#j-lottery-arrow').on('click', function () {
                        var thisClickTime = new Date().getTime(),
                            $this = $(this)
                        if(thisClickTime - ($this.attr('data-clickbegin') - 0 || 0) > 1000) {
                            $this.attr('data-clickbegin', thisClickTime);
                            if(result.hasLottery || $this.data('status') == 1) {
                                alert( '今日已抽奖，明天再来吧！')
                            } else {
                                // alert('今天还没抽奖')
                                base.getAPI({
                                    url: urls.receive,
                                    data: {
                                        invokeMethod: 'break2BillionReceive',
                                        invokeParameters: 'args_activityPart_1_integer_1'
                                    },
                                    callback: function (data) {
                                        console.log(data)
                                        var result = data.result || {}
                                        var lotteryRewardCode = result.lotteryRewardCode || '',
                                            lotteryRewardName = result.lotteryRewardName || ''
                                        if(data.success && lotteryRewardCode){
                                            $('#j-lottery-rotate').rotate({
                                                angle: 0,
                                                animateTo: 360 * 5 + giftAngleList[lotteryRewardCode] + point, // 至少转四圈
                                                duration: 4000,
                                                callback: function () {
                                                    $('#j-lottery-arrow').data('status', 1)
                                                    alert( '恭喜你！获得'+ lotteryRewardName  +'，稍后可在账户中查看')
                                                }
                                            });
                                        } else {

                                            if(data.resultCodes[0].code === '90724'){
                                                alert('还差一点，快去投资获得机会~')
                                            } else {
                                                alert('抱歉，当前抽奖人数过多，请稍后再试')
                                            }

                                            if(debug){
                                                alert(data.resultCodes[0].msg)
                                                console.log('系统出错',data)
                                            }

                                        }
                                    }
                                })

                            }
                        } else {
                            $this.attr('data-clickbegin', thisClickTime);
                            return false;
                        }
                    })
                }else{
                    $('#j-checkNum, #j-lottery-arrow, #j-section2-btn').attr("href", $('#j-login').attr('href'))
                }
            }
        }

        // section2
        $('#j-section2-btn').text('即将开启').addClass('z-disabled')
    }

    // 活动结束后
    function afterActive(result) {
        // alert(JSON.stringify(result, 4, 4))
        // 点击箭头去登录 或者抽奖
        if(os===1){
            if(encryptionId){
            }else {
                $('#j-checkNum, #j-section2-btn').on("click", function () {
                    Android.GetEvent('break2BillionReceive', '0', '');
                })
            }

        }else if(os===2){
            if (encryptionId) {
            }else{
                $('#j-checkNum, #j-section2-btn').attr("href","yrw-skip://invokeMethod=loginIn")
            }
        }else{
            if(logined){
            }else{
                $('#j-checkNum, #j-section2-btn').attr("href", $('#j-login').attr('href'))
            }
        }

        // 设置进度条
        $progressRemain.text(0)
        $progressArrow.css('left', '5%')
        $progressInner.css('left', '-100%')
        $progressTip.css('left', 0)

        $('#j-lottery-arrow').on('click', function () {
            alert('活动已结束')
        })

        // section2
        var limitTime = result.break2BillionTime + result.giftOutTime * 60 *60 *1000

        if(limitTime < environment.serverDate - 0) {
            $('#j-section2-btn').text('活动已结束').addClass('z-disabled')
        } else {
            if(result.hasReceiveGift || $('#j-section2-btn').attr('data-hasReceiveGift') === 1){
                $('#j-section2-btn').text('已领取').addClass('z-disabled')
            } else {
                // 点击箭头去登录 或者抽奖
                if(os===1){
                    if(encryptionId){ // 安卓登录
                        // afterActiveLogin(result)
                        // alert('安卓登录了')
                        if((result.memberTotalAmount || 0) < result.giftLevel[0]){
                            // alert('没有达到领取资格')
                            $('#j-section2-btn').text('立刻领取').addClass('z-disabled')
                        } else {
                            // alert('达到领取资格')
                            $('#j-section2-btn').removeClass('z-disabled').on('click', function () {
                                if($(this).attr('data-hasReceiveGift') == '1'){
                                    return false
                                } else {
                                    Android.GetEvent('break2BillionReceive', '0', 'args_activityPart_1_integer_2');
                                }
                            })
                        }
                    }else {
                        // alert('安卓没登录')
                        $('#j-section2-btn').text('立刻领取').removeClass('z-disabled').on("click", function () {
                            Android.GetEvent('break2BillionReceive', '0', '');
                        })
                    }
                }
                // IOS 和M站这次一起
                else{
                    if(os == 2){
                        // IOS 这里按照M站的逻辑走
                        if(encryptionId){
                            // 登录之后未满足条件
                            if((result.memberTotalAmount || 0) < result.giftLevel[0]){
                                $('#j-section2-btn').text('立刻领取').addClass('z-disabled')
                                // alert('section2投资额未满足条件')
                            } else {
                                // alert('section2满足条件')
                                $('#j-section2-btn').removeClass('z-disabled').on('click', function () {
                                    if($(this).attr('data-hasReceiveGift') === '1'){
                                        return false
                                    } else {
                                        // alert('可以去领了')
                                        base.getAPI({
                                            url: urls.receive,
                                            data:  {
                                                loginSource: 2,
                                                mKey: mKey, // TODO
                                                invokeMethod: 'break2BillionReceive',
                                                invokeParameters: 'args_activityPart_1_integer_2'
                                            },
                                            callback: function (data) {
                                                var result = data.result || {}
                                                if(data.success && result.giftIndex){
                                                    var tempText = ''
                                                    switch (result.giftIndex) {
                                                        case 0:
                                                            tempText = '恭喜您！获得100元现金券，0.5%收益券'
                                                            break;
                                                        case 1:
                                                            tempText = '恭喜您！获得200元现金券，1%收益券'
                                                            break
                                                        case 2:
                                                            tempText = '恭喜您！获得200元现金券，1.5%收益券'
                                                            break
                                                        case 3:
                                                            tempText = '恭喜您！获得500元现金券，2%收益券'
                                                            break
                                                        default:
                                                            break
                                                    }

                                                    alert(tempText)

                                                    $('#j-section2-btn').text('已领取').addClass('z-disabled')
                                                        .attr('data-hasReceiveGift', 1)
                                                } else {
                                                    alert(data.resultCodes[0].msg)
                                                    // alert('领取第二部分红包后端返回数据有错误: ' + JSON.stringify(data))
                                                }
                                            }
                                        })
                                    }
                                })
                            }
                        } else {
                            $('#j-section2-btn').text('立刻领取').removeClass('z-disabled').attr("href","yrw-skip://invokeMethod=loginIn")
                        }
                    } else {
                        if(logined){
                            afterActiveLogin(result)
                        } else {
                            $('#j-section2-btn').text('立刻领取').removeClass('z-disabled').attr("href", $('#j-login').attr('href'))
                        }
                    }
                }

                // 活动结束后登录
                function afterActiveLogin(result) {
                    // 登录之后未满足条件
                    if((result.memberTotalAmount || 0) < result.giftLevel[0]){
                        $('#j-section2-btn').text('立刻领取').addClass('z-disabled')
                    } else {
                        $('#j-section2-btn').removeClass('z-disabled').on('click', function () {
                            if($(this).attr('data-hasReceiveGift') === '1'){
                                return false
                            } else {
                                base.getAPI({
                                    url: urls.receive,
                                    data:  {
                                        invokeMethod: 'break2BillionReceive',
                                        invokeParameters: 'args_activityPart_1_integer_2'
                                    },
                                    callback: function (data) {
                                        if(data.success && data.result.giftIndex){
                                            var tempText = ''
                                            switch (data.result.giftIndex) {
                                                case 0:
                                                    tempText = '恭喜您！获得100元现金券，0.5%收益券'
                                                    break;
                                                case 1:
                                                    tempText = '恭喜您！获得200元现金券，1%收益券'
                                                    break
                                                case 2:
                                                    tempText = '恭喜您！获得200元现金券，1.5%收益券'
                                                    break
                                                case 3:
                                                    tempText = '恭喜您！获得500元现金券，2%收益券'
                                                    break
                                                default:
                                                    break
                                            }

                                            alert(tempText)

                                            $('#j-section2-btn').text('已领取').addClass('z-disabled')
                                                .attr('data-hasReceiveGift', 1)
                                        } else {
                                            alert(data.resultCodes[0].msg)
                                            // alert('领取第二部分红包后端返回数据有错误: ' + JSON.stringify(data))
                                        }
                                    }
                                })
                            }
                        })
                    }
                }

            }
        }

        // section3
        //巅峰榜表单
        if(result.activityStatus == 2){
            $('#j-list-beforeActive').removeClass('f-dn')
        } else{
            var rankList = result.rankList ||[],
                listLength=rankList.length,
                html;
            // 填充前3位
            var $cardList = $('#j-card-list')
            if(listLength > 0){
                rankList = result.rankList || []

                for(var i = 0; i < 3; i++){
                    if(rankList[i]){
                        var tempAvatar = rankList[i].avatar || '',
                            tempLastUsername = rankList[i].lastUsername || ''

                        if(!tempAvatar){
                            $cardList.find('.m-card').eq(i).find('.u-user-header img').attr('src', 'https://www.yrw.com/static/img/member/avatar_35x35.png')
                                .end().find('.u-user-name').text(tempLastUsername)
                        } else {

                            $cardList.find('.m-card').eq(i).find('.u-user-header img').attr('src', 'https://oss-cn-hangzhou.aliyuncs.com/' + tempAvatar)
                                .end().find('.u-user-name').text(tempLastUsername)
                        }

                    }
                }
            }
        }

    }

    function formatTime (date, format){
        date = new Date(date);

        var map = {
            "M": date.getMonth() + 1, //月份
            "d": date.getDate(), //日
            "h": date.getHours(), //小时
            "m": date.getMinutes(), //分
            "s": date.getSeconds(), //秒
            "q": Math.floor((date.getMonth() + 3) / 3), //季度
            "S": date.getMilliseconds() //毫秒
        };
        format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
            var v = map[t];
            if (v !== undefined) {
                if (all.length > 1) {
                    v = '0' + v;
                    v = v.substr(v.length - 2);
                }
                return v;
            }
            else if (t === 'y') {
                return (date.getFullYear() + '').substr(4 - all.length);
            }
            return all;
        });
        return format;
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

    //数字转换成千分号方法
    function amountSplit(num){
        if (!num) {
            return ''
        }
        var n = num + "",
            reg = /(-?\d+)(\d{3})/
        return n.replace(reg, "$1,$2")
    }
})
