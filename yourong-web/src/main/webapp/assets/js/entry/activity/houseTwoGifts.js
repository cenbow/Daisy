/**
 * @entry 紧急活动 房有融二重礼
 */
import 'module/jquery.extend'
import {template} from 'module/template'
import {Util} from 'common/util'

let domain = env.globalPath,
    urls = {
        init: environment.globalPath + '/activity/house2Gifts/init'
    }

// 加载数据
Util.post({
    url: urls.init,
    callback: function(data){
        if(data.success){
            init(data.result)
        } else{
            log('后台加载数据出错')
        }
    }
})

let init = (result)=>{
    let activityStatus = result.activityStatus || '',
        fund1 = result.fund1 || 0,
        fund2 = result.fund2 || 0,
        startTime = result.startTime || '',
        investList = result.investList || []

    switch (activityStatus) {
        case 2:
            beforeActive()
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

    // 渲染用户列表
    rederUserList(result)

    // 设置活动开始时间
    $('#j-startDate').text(formatTime(startTime, 'yyyy年MM月dd日 hh:mm'))
}


// 活动开始前
let beforeActive = () => {
    $('.j-step-icon').addClass('z-before')
    $('#j-left-val1').text('300,000')
    $('#j-left-val2').text('500,000')
    $('.j-step').addClass('z-notActive').removeClass('z-hidden');

    // 设置列表和按钮文案样式
    $('.m-userList').addClass('z-noData').text('丰厚现金券奖励 等你来领')
    $('#j-activity-btn').text('未开启').addClass('z-disabled').removeClass('z-hidden').removeAttr('href')

    // 设置箭头位置
    $('.j-arrow-down').css('right', 0)
}


// 活动进行中
let duringActive = (result) => {
    if(result.fund1 == 0 && result.fund2 == 0){
        afterActive(result)
    } else {
        renderProcess(result)
        $('#j-activity-btn').text('立即投资').removeClass('z-disabled z-hidden').removeClass('z-hidden').attr('target', '_blank')
    }
}


// 活动结束
let afterActive = (result) => {
    renderProcess(result)
    $('#j-activity-btn').text('活动已结束').addClass('z-disabled').removeClass('z-hidden').removeAttr('href')
}


// 渲染进度条和两块活动样式，非常麻烦
let renderProcess = (result) => {
    let fund1 = result.fund1 || 0,
        fund2 = result.fund2 || 0
    if(fund1 < 0) {
        fund1 = 0
    }
    if(fund2 < 0) {
        fund2 = 0
    }

    // 用于给数字添加千分号
    function commafy(num) {
        num = num + "";
        var re = /(-?\d+)(\d{3})/;
        while (re.test(num)) {
            num = num.replace(re, "$1,$2");
        }
        return num;
    }

    let tempFund1 = commafy(fund1),
        tempFund2 = commafy(fund2)

    // 这里需要做千分号处理
    $('#j-left-val1').text(tempFund1)
    $('#j-left-val2').text(tempFund2)

    // 设置第一轮第二轮的状态和小旗子按钮,以及展示整个进度情况
    if(fund1 > 0){ // 正在抢
        $('#j-step1').find('.j-step-icon').addClass('z-during').end().removeClass('z-hidden')
        $('#j-step2').addClass('z-notActive').find('.j-step-icon').addClass('z-before').end().removeClass('z-hidden')
    } else if(fund1 == 0 && fund2 > 0) { // 活动2正在抢
        $('#j-step1').addClass('z-notActive').find('.j-step-icon').addClass('z-after').end().removeClass('z-hidden')
        $('#j-step2').removeClass('z-notActive').find('.j-step-icon').addClass('z-during').end().removeClass('z-hidden')
    } else if(fund2 == 0){ // 活动2已经抢完了
        $('#j-step1').addClass('z-notActive')
            .find('.j-step-icon').removeClass('z-before  z-during').addClass('z-after')
            .end().removeClass('z-hidden')

        $('#j-step2').addClass('z-notActive')
            .find('.j-step-icon').removeClass('z-before  z-during').addClass('z-after')
            .end().removeClass('z-hidden')
    }

    function setProcessBarPostion(elem, totalAmount, leftAmount){
        if(leftAmount > totalAmount){
            leftAmount = totalAmount
        }
        // 设置进度条的位置
        let processBarWidth = elem.find('.u-processBar-outer').width(),
            leftVal = ((totalAmount - leftAmount) / totalAmount ) * processBarWidth // 进度条左偏移

        // 主要设置3个元素的位置，1.fund 2.arrowdown 3.processBarInner
        let outerWidth = elem.find('.u-arrow-down-outer').innerWidth(),
            arrowWidth = elem.find('.j-arrow-down').innerWidth() / 2,
            $fund = elem.find('.j-processBar-fund'),
            $arrowdown = elem.find('.j-arrow-down'),
            $processBarInner = elem.find('.j-processBar-inner'),
            leftBorderRadius = 20, // tooltip的圆角，用于调整三角箭头的位置
            leftMinAmount = totalAmount * (leftBorderRadius / processBarWidth),// 用于设置左边极限最小位置
            leftEnd = outerWidth / 2 + 20 + 5.5, // 左边极限位置
            leftEndAmount = totalAmount * (leftEnd / processBarWidth), // 左边极限的值金额大小
            rightEnd = processBarWidth - outerWidth / 2,
            rightEndAmount = totalAmount * (rightEnd / processBarWidth)

        // 分6种情况 1.0, 2.leftMin 3.left 4.mid 5.right 6.max

        let tempWidth = elem.find('.j-processBar-fund').innerWidth(), // 获取tooltip的宽度
            $processsBarInner = elem.find('.j-processBar-inner'),
            $processBarFund = elem.find('.j-processBar-fund'),
            $whiteBar = elem.find('.j-white-bar'),
            whiteBarOldWidth = $whiteBar.width()

        let innerPos, // 内部进度条的位置
            proceessBarInnerPos,
            processBarFundPos,
            arrowDownPos,
            whiteBarNewWidth // 设置白色高亮条的宽度

        if(leftAmount == 0 || leftAmount < 0){
            leftAmount = 0  // ok
            proceessBarInnerPos = processBarWidth
            processBarFundPos = processBarWidth - tempWidth + leftBorderRadius / 2
            arrowDownPos = tempWidth - leftBorderRadius * 2 - 4
            // 这里减4是因为三角和圆角贴变有缝隙
            whiteBarNewWidth = 0

        } else if( leftAmount > 0 && (leftAmount < leftMinAmount || leftAmount == leftMinAmount)){
            // 小于左极限的时候，进度条位置保证最小位置不动
            proceessBarInnerPos = processBarWidth - leftBorderRadius
            processBarFundPos = processBarWidth - tempWidth + leftBorderRadius / 2 - 4
            arrowDownPos = tempWidth - leftBorderRadius * 2 - 4
            whiteBarNewWidth = 0
        } else if(leftAmount > leftMinAmount && (leftAmount < leftEndAmount || leftAmount == leftEndAmount)){
            innerPos = processBarWidth - (leftAmount / totalAmount) * processBarWidth
            proceessBarInnerPos = innerPos
            processBarFundPos = processBarWidth - tempWidth + leftBorderRadius / 2
            arrowDownPos = tempWidth - (processBarWidth - innerPos) - leftBorderRadius - 4
            whiteBarNewWidth = processBarWidth - proceessBarInnerPos - 20
        } else if(leftAmount > rightEndAmount && leftAmount < totalAmount ){
            innerPos = processBarWidth - (leftAmount / totalAmount) * processBarWidth
            proceessBarInnerPos = innerPos
            processBarFundPos = 0
            arrowDownPos = innerPos
            whiteBarNewWidth = processBarWidth - proceessBarInnerPos - 20
        } else if(leftAmount == totalAmount) {
            proceessBarInnerPos = 0
            processBarFundPos = 0
            arrowDownPos = 0
            whiteBarNewWidth = processBarWidth - proceessBarInnerPos - 20
        } else {
            innerPos = processBarWidth - (leftAmount / totalAmount) * processBarWidth
            proceessBarInnerPos = innerPos
            processBarFundPos = innerPos - tempWidth / 2 + leftBorderRadius
            arrowDownPos = tempWidth / 2 - leftBorderRadius
            whiteBarNewWidth = processBarWidth - proceessBarInnerPos - 20
        }

        $processsBarInner.css('right', proceessBarInnerPos + 'px')
        $processBarFund.css('right', processBarFundPos + 'px')
        $arrowdown.css('right', arrowDownPos + 'px')
        $whiteBar.css('width', whiteBarNewWidth + 'px')
    }

    let $processBar1 = $('#j-processBar-1'),
        $processBar2 = $('#j-processBar-2')

    setProcessBarPostion($processBar1, 300000, fund1)
    setProcessBarPostion($processBar2, 500000, fund2)
}


// 模拟后端渲染用户列表
let rederUserList = (result)=> {
    let activityStatus = result.activityStatus,
        investList = result.investList || []

    if(activityStatus == 2){
        $('.m-userList').addClass('z-noData').text('丰厚现金券奖励 等你来领')

    } else if(activityStatus == 4 || activityStatus == 6) {
        if(investList.length == 0){
            $('.m-userList').addClass('z-noData').text('丰厚现金券奖励 等你来领')
        } else {
            let tempStr = template('j-gainList-tpl', result)

            $('#j-gainList').html(tempStr).scrollList({
                size: 6,
                height: 50,
                length: 1,
                time: 5000,
                stoppable: true
            })
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
