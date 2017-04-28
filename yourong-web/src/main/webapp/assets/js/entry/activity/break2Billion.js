/**
 * @entry 破二十亿活动
 * @url   /activity/break2Billion/index
 */
import {template} from 'module/template'
import {Util} from 'common/util'
import {Dialog} from 'module/cube'

let $progressInner = $('#j-progress-inner'),
    $progressTip = $('#j-progress-tip'),
    $progressTotal = $('#j-progress-total'),
    $progressRemain = $('#j-progress-remain')

// 切换三个tab
$('#j-tab-menu').on('click', 'li.u-tab', function () {
    var $this = $(this),
        skin = $this.data('skin')
    $('#j-main').removeClass().addClass('g-main').addClass('z-' + skin)
})

let domain = env.globalPath,
    urls = {
        init: environment.globalPath + '/activity/break2Billion/init',
        receive: environment.globalPath + '/activity/break2Billion/receive'
    },
    logined = $('#j-main').data('logined')

/* 登录查看我的投资额 */
$('#j-checkNum').on('click', function () {
    if(!logined){
        Util.post({
            url: urls.receive,
            callback: function (data) {
                // console.log(data)
            }
        })
    }else {
        return false
    }
})

function goLoginPage() {
    Util.post({ url: urls.receive })
}

// 加载数据
Util.post({
    url: urls.init,
    callback: function(data){
        if(data.success){
            // log(data)
            init(data.result)
        } else{
            log('后台加载数据出错')
        }
    }
})

let init = (result)=>{
    let activityStatus = result.activityStatus || ''
    /* 如果突破20亿，投资总额变成 20亿突破时间 */
    if(result.hasBreak) {
        $('#j-total').text(Util.format( result.break2BillionTime, 'yyyy年的MM月dd日hh时mm分') + ',交易额突破20亿')
    }
    // 登录查看我的投资额
    if(logined === 1){
        if(result.activityStatus == 6){
            var text = '我的累计投资额：¥' + (Util.format(result.memberTotalAmount  || 0)  || 0)
        } else {
            var text = '我的今日投资额：¥' + (Util.format(result.memberDayAmount || 0) || 0) + '，我的累计投资额：¥' + (Util.format(result.memberTotalAmount  || 0)  || 0)
        }

        $('#j-checkNum').text(text).css('cursor', 'default')
    }

    $('#j-num').removeClass('z-fadeOut')

    switch (activityStatus) {
        case 2:
            beforeActive(result)
            break;
        case 4:
            duringActive(result)
            break
        case 6:
            afterActive(result)
            break
        default:
            break
    }

    // 展示进度条
    $('#j-progress-wrap').addClass('z-show')

    // 渲染第一块用户中奖列表
    let renderlotteryList = (result)=>{
        let lotteryList = result.lotteryList || []
        if(activityStatus == 2){
            $('#j-luckly-list').addClass('z-noData2').text('活动未开始')

        } else if(activityStatus == 4 || activityStatus == 6) {
            if(lotteryList.length == 0){
                $('#j-luckly-list').addClass('z-noData').text('—— 快来试试手气吧 ——')
            } else {
                let tempStr = template('j-luckly-list-tpl', result)

                $('#j-luckly-list').html(tempStr).scrollList({
                    size: 5,
                    height: 50,
                    length: 1,
                    time: 5000,
                    stoppable: true
                })
            }
        }
    }

    renderlotteryList(result)

    // section3
    //巅峰榜表单
    if(activityStatus == 2){
        $('#j-list-beforeActive').removeClass('f-dn')
    } else{
        let rankList = result.rankList ||[],
            listLength=rankList.length,
            html;

        $("#j-ranking-list").removeClass("f-dn")

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
        html = template('j-ranking-list-tpl', result);
        $('#j-ranking-list').html(html);
    }
}

// 活动前
const beforeActive = (result)=>{
    // log('活动前', result)


    $('#j-lottery-arrow').on('click', function () {
        Dialog.show({ content: '来早啦~活动未开始' })
    })

    // section2
    $('#j-section2-btn').text('即将开启').addClass('z-disabled')
}


// 活动中
const duringActive = (result)=>{
    // log('活动中', result)

    // 渲染进度条
    // result.fund = 10
    // log(result)

    let tempVal = result.fund
    if(tempVal > 1000000 ){tempVal = 1000000}
    if(tempVal < 0){tempVal = 0}
    let rate = ((tempVal / 1000000).toFixed(2)) * 100 -100
    $progressRemain.text(Util.format(tempVal) || 0)
    if(tempVal> 0 && (tempVal < 10000 || tempVal == 10000)){
        $progressInner.css('left', '-99%')
    } else {
        $progressInner.css('left', rate + '%')
    }
    $progressTip.css('left', (rate+93) + '%')

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

    $('#j-lottery-arrow').on('click', function () {
        if(!logined){
            goLoginPage()
        } else {
            let thisClickTime = new Date().getTime(),
                $this = $(this)

            if(thisClickTime - ($this.attr('data-clickbegin') - 0 || 0) > 1000) {
                $this.attr('data-clickbegin', thisClickTime);

                if(result.hasLottery || $this.data('status') == 1) {
                    Dialog.show({'content': '今日已抽奖，明天再来吧！'})
                } else {
                    Util.post({
                        url: urls.receive,
                        data: {'activityPart': 1},
                        callback: function (data) {
                            let lotteryRewardCode = data.result.lotteryRewardCode || '',
                                lotteryRewardName = data.result.lotteryRewardName || ''
                            data.success = true

                            if(data.success && lotteryRewardCode){
                                $('#j-lottery-rotate').rotate({
                                    angle: 0,
                                    animateTo: 360 * 5 + giftAngleList[lotteryRewardCode] + point, // 至少转四圈
                                    duration: 4000,
                                    callback: function () {
                                        $('#j-lottery-arrow').data('status', 1)
                                        Dialog.show({
                                            type: 'success',
                                            content: '恭喜你！获得'+ lotteryRewardName  +'，稍后可在账户中查看'
                                        })
                                    }
                                });
                            } else {
                                if(data.resultCodeEum[0].code === '90724'){
                                    Dialog.show({content: '还差一点，快去投资获得机会~'})
                                }
                            }
                        }
                    })

                }
            } else {
                $this.attr('data-clickbegin', thisClickTime);
                return false;
            }
        }
    })

    // section2
    $('#j-section2-btn').text('即将开启')
}


// 活动后
const afterActive = (result)=>{
    // log('活动后', result)

    // 设置进度条
    let tempVal = $(this).val() * 10000
    if(tempVal > 1000000 ){
        tempVal = 1000000
    }
    if(tempVal < 0){
        tempVal = 0
    }

    $progressRemain.text(0)
    $progressInner.css('left', '-100%')
    $progressTip.css('left', '-6%')

    $('#j-lottery-arrow').on('click', function () {
        Dialog.show({'content': '活动已结束'})
    })

    // section2
    var limitTime = result.break2BillionTime + result.giftOutTime * 60 *60 *1000
    if(limitTime < environment.serverDate) {
        // log('活动已结束')
        $('#j-section2-btn').text('活动已结束').addClass('z-disabled')
    } else {
        if(result.hasReceiveGift || $('#j-section2-btn').attr('data-hasReceiveGift') === 1){
            // log('已领取')
            $('#j-section2-btn').text('已领取').addClass('z-disabled')
        } else {
            // 未登录
            if(!logined){
                $('#j-section2-btn').text('立刻领取').removeClass('z-disabled').on('click', function () {
                    goLoginPage()
                })
            } else {
                // 登录之后未满足条件
                if((result.memberTotalAmount || 0 ) < result.giftLevel[0]){
                    $('#j-section2-btn').text('立刻领取').addClass('z-disabled')
                    // log('投资额未满足条件')
                } else {
                    $('#j-section2-btn').removeClass('z-disabled').on('click', function () {
                        if($(this).attr('data-hasReceiveGift') === '1'){
                            return false
                        } else {
                            // log('可以去领了')
                            Util.post({
                                url: urls.receive,
                                data: {'activityPart': 2},
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

                                        Dialog.show({
                                            type: 'success',
                                            content: tempText
                                        })

                                        $('#j-section2-btn').text('已领取').addClass('z-disabled')
                                            .attr('data-hasReceiveGift', 1)
                                    } else {
                                        log('领取第二部分红包后端返回数据有错误: ', data)
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
        let rankList = result.rankList ||[],
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
                        $cardList.find('.m-card').eq(i).find('.u-user-header img').attr('src', '/static/img/member/avatar_35x35.png')
                            .end().find('.u-user-name').text(tempLastUsername)
                    } else {

                        $cardList.find('.m-card').eq(i).find('.u-user-header img').attr('src', 'https://oss-cn-hangzhou.aliyuncs.com' + tempAvatar)
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