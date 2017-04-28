/**
 * @entry 五月活动
 */

import 'module/jquery.extend'
import {Dialog} from 'module/cube'
import {Util} from 'common/util'

if(Util.lteIE9()){
    $('#j-rain-area-IE').removeClass('z-hide')
} else {
    $('#j-rain-area').removeClass('z-hide')
}

const reload=()=>{
    location.reload()
}

const logined = $('#j-main').data('logined'),
    contents = {
        text1: '活动未开始，五一再来哟！',
        text2: '活动已结束！',
        text3: '红包欲来风满楼！',
        text4: '抢红包雨人太多，稍后再来吧！',
        text5: '亲，登录后才可参与抢现金券哦！',
        text6: '亲，登录后才可领取奖励哦！'
    },
    domain = env.globalPath,
    urls = {
        init: domain + '/activity/mayDay4Gifts/init',
        receive: domain + '/activity/mayDay4Gifts/receive'
    }

/* 获取随机数，主要用来定位红包的初始化位置 */
const random = (min, max) => {
    return Math.round(min + Math.random() * (max - min))
}

/* 给红包定位 */
$('.u-redbag-flakes').each(function() {
    let randomWidth = random(70, 100)

    $(this).css({
        'left': random(100, 1200) + 'px', // 这里其实取得是 rain area的宽度加一些
        'width': randomWidth,
        'height': randomWidth
    })
})

Util.post({
    url: urls.init,
    callback: function(data){
        if(data && data.success){
            let result = data.result
            if(result){
                init(result)
            }
        } else {
            log('后端数据有问题')
        }
    }
})

let init = (result) => {
    let activityStatus = result.activityStatus,
        couponForDayFlag = result.couponForDayFlag
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

    // 等所有元素都渲染完了，把两个模块展现出来
    $('#j-three-gifts').removeClass('z-hide')
    $('#j-redbag-wrapper').removeClass('z-hide')
}

let showMask = (str) => {
    $('#j-mask-title').text(str)
    $('#j-rain-area-mask').removeClass('z-hide')
}

/* 未登录时访问receive接口就可以实现登录跳回了 */
// 这个是红包的提示文案
let goLoginPage1 = () => {
    Dialog.show({
        'content': contents.text5,
        'okValue': '去登录',
        close:reload,
        callback: function () { Util.post({ url: urls.receive }) }
    })
}

// 这个是‘投满拿奖励’ 的提示文案
let goLoginPage2 = () => {
    Dialog.show({
        'content': contents.text6,
        'okValue': '去登录',
        close:reload,
        callback: function () { Util.post({ url: urls.receive }) }
    })
}

let beforeActivity = (result) => {
    //log('活动未开始')
    showMask(contents.text1)
    $('.j-gift-btn').text('活动未开始').addClass('z-disable')
    $('#j-gifts-wrapper').removeClass('z-hide')
}

let splitAmount = (amount) => {
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

let duringActivityNotLogined = (result) => {
    let receiveStartTime = result.receiveStartTime || '',
        receiveEndTime = result.receiveEndTime || '',
        receiveStartHour = receiveStartTime.substr(0, receiveStartTime.indexOf(':')),
        receiveStartMin = receiveStartTime.substr(receiveStartTime.indexOf(':') + 1,2),
        receiveEndHour = receiveEndTime.substr(0, receiveEndTime.indexOf(':')),
        receiveEndMin = receiveEndTime.substr(receiveEndTime.indexOf(':') + 1, 2),
        serverDate = env.serverDate,
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

    //log(serverDateStr, receiveStartTimeStr,receiveEndTimeStr )

    if(serverYear == year && serverMonth == month && serverDay == day){
        isLastDay = true
    }

    //log(typeof serverDate, serverHours, receiveStartHour, receiveEndHour)
    /* 这里是活动开始了，但是没到领取时间，红包雨上也会有蒙层 */
    if((serverDateStr > receiveStartTimeStr
        || serverDateStr == receiveStartTimeStr)
        && serverDateStr < receiveEndTimeStr){
        //log('活动进行中，未登录，在14点-15点之间')
        $('#j-redbag-rain').on('click', '.u-redbag-flakes, .u-redbag-flakes-IE', goLoginPage1)
    } else {
        if(isLastDay && (serverDateStr > receiveEndTimeStr || serverDateStr == receiveEndTimeStr)){
            //log('最后一天，15点以后了')
            $('#j-three-gifts').addClass('z-onTop')
            $('#j-redbag-wrapper').addClass('z-onBottom').appendTo('#j-wrap')
            showMask('活动已结束！')
        } else {
            showMask(contents.text3)
        }
    }

    $('.j-gift-btn').text('等待领取').on('click',goLoginPage2)
    $('#j-gifts-wrapper').removeClass('z-hide')
}

let duringActivityLogined = (result) => {
    //log('活动进行中, 登录')
    let receiveStartTime = result.receiveStartTime || '',
        receiveEndTime = result.receiveEndTime || '',
        receiveStartHour = receiveStartTime.substr(0, receiveStartTime.indexOf(':')),
        receiveStartMin = receiveStartTime.substr(receiveStartTime.indexOf(':') + 1,2),
        receiveEndHour = receiveEndTime.substr(0, receiveEndTime.indexOf(':')),
        receiveEndMin = receiveEndTime.substr(receiveEndTime.indexOf(':') + 1, 2),
        serverDate = env.serverDate,
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

    //log(numberAppendZero(new Date(serverDate).getMinutes()))
    //log(serverDateStr, receiveStartTimeStr,receiveEndTimeStr )

    if(serverYear == year && serverMonth == month && serverDay == day){
        isLastDay = true
    }

    if((serverDateStr > receiveStartTimeStr
        || serverDateStr == receiveStartTimeStr)
        && serverDateStr < receiveEndTimeStr){
        //log('活动进行中，登录，在14点-15点之间')
        /* 设置点击红包效果 */
        $('#j-redbag-rain').on('click', '.u-redbag-flakes, .u-redbag-flakes-IE', function(){
            let $this = $(this)

            serverDateStr = ( ('' + new Date(serverDate).getHours()) + ('' + numberAppendZero(new Date(serverDate).getMinutes())) ) - 0

            /* 判断时间是否在 14-15点之间,所以只要小时是在 14即可 */
            if((serverDateStr > receiveStartTimeStr
                || serverDateStr == receiveStartTimeStr)
                && serverDateStr < receiveEndTimeStr){
                //log('可以抢红包了，服务器当前时间点', serverHours )
                /* 判断今天的有没有领过 */
                if($('#j-redbag-rain').attr('data-couponForDayFlag') == 'true'){
                    Dialog.show({
                        'content': '恭喜您领取成功！如未及时到账，请耐心等待',
                        close:reload,
                        callback:reload
                    })
                } else {
                    Util.post({
                        url: urls.receive,
                        data: {couponAmount: 30},
                        callback: function(data){
                            //log('第一次点击返回的数据：', data)
                            if(data.success){
                                $.shade('show')
                                $this.addClass('z-hide')
                                $('#j-redbag-open').removeClass('z-hide')
                                $('#j-redbag-open-btn').on('click', function(){
                                    window.location.href = env.globalPath + '/coupon/couponList'
                                })
                                $('.u-shade, #j-redbag-close').on('click', function(){
                                    $('#j-redbag-open').addClass('z-hide')
                                    $.shade('hide')
                                    $('#j-redbag-rain').attr('data-couponForDayFlag', true)
                                })
                            } else{
                                Dialog.show({ content: contents.text4,close:reload,callback:reload })
                            }
                        }
                    })
                }
            } else {
                //log('还不能抢红包，服务器当前时间点', serverHours )
                // 这里需要蒙层显示的
                showMask(contents.text3)
            }
        })

    // 活动期间，但是没到时间
    } else {
        if(isLastDay && (serverDateStr > receiveEndTimeStr || serverDateStr == receiveEndTimeStr)){
            //log('最后一天，15点以后了')
            $('#j-three-gifts').addClass('z-onTop')
            $('#j-redbag-wrapper').addClass('z-onBottom').appendTo('#j-wrap')
            showMask('活动已结束！')
        } else {
            //log('活动进行中，未登录，但不在14点-15点之间')
            showMask(contents.text3)
        }
    }

    /* 投满拿奖励 */
    let investAmount = result.investAmount || 0,
        couponForLv1 = result.couponForLv1 || '',
        couponForLv2 = result.couponForLv2 || '',
        couponForLv3 = result.couponForLv3,
        totalInvestLv1 = result.totalInvestLv1 || 0,
        totalInvestLv2 = result.totalInvestLv2 || 0,
        totalInvestLv3 = result.totalInvestLv3 || 0

    /* 当天累计投资多少钱 */
    $('#j-investAmout').text(splitAmount(investAmount))
    $('.u-total-count').removeClass('z-hide')

    /* 三重礼红包状态和按钮状态 */
    if(couponForLv1 == true){
        $('#j-redbag-88').addClass('z-active')
        $('#j-gift-btn-88').text('您已领取').addClass('z-disable')
    } else {
        $('#j-gift-btn-88').text('等待领取').on('click', function(){
            getGift(investAmount, 88, totalInvestLv1)
        })
    }

    if(couponForLv2 == true){
        $('#j-redbag-188').addClass('z-active')
        $('#j-gift-btn-188').text('您已领取').addClass('z-disable')
    } else {
        $('#j-gift-btn-188').text('等待领取').on('click', function(){
            getGift(investAmount, 188, totalInvestLv2)
        })
    }

    if(couponForLv3 == true){
        $('#j-redbag-500').addClass('z-active')
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
    let getGift = (investAmount, couponAmount, limit) => {
        if(!$('#j-gift-btn-' + couponAmount).hasClass('z-disable')){
            // 有资格领取
            if(investAmount > limit || investAmount == limit){
                Util.post({
                    url: urls.receive,
                    data: { couponAmount: couponAmount },
                    callback: function(data){
                        let success = data.success || false
                        if(data && success ){
                            Dialog.show({
                                content: '恭喜获得1张'+ couponAmount +
                                '元现金券，继续投资，获取更多奖励吧！',close:reload,callback:reload
                            })
                            $('#j-redbag-' + couponAmount).addClass('z-active')
                            $('#j-gift-btn-' + couponAmount).text('您已领取').addClass('z-disable')
                        } else {
                            Dialog.show({ content: '领取失败，请稍后再次领取！',close:reload,callback:reload })
                        }
                    }
                })
            } else {
                Dialog.show({
                    content: '快去投资获取奖励吧！',
                    okValue: '立即投资',
                    callback: function(){
                        window.location.href = env.globalPath + '/products/list-all-all-investing-1-createTimeAsc-1.html'
                    },close:reload
                })
            }
        }
    }

    /* 设置完三个按钮的状态之后，在展示 */
    $('#j-gifts-wrapper').removeClass('z-hide')
    let couponForDayFlag = $('#j-redbag-rain').attr('data-couponForDayFlag')
    // 如果当天领过 30元券，把30元券整块移到下面
    if(couponForDayFlag == 'true'){
        //log('hello cc')
        $('#j-three-gifts').addClass('z-onTop')
        $('#j-redbag-wrapper').addClass('z-onBottom').appendTo('#j-wrap')
    }

}

let afterActivity = (result) => {
    //log('活动结束')
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
