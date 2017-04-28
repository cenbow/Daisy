/**
 * @entry 里程拉新
 */

import 'module/jquery.extend'
import {template} from 'module/template'
import {Util} from 'common/util'
import {Dialog} from 'module/cube'

let domain = env.globalPath,
    urls = {
        init: environment.globalPath + '/activity/marathon/init',
        receive: environment.globalPath + '/activity/marathon/receive',
        list: environment.globalPath + '/activity/marathon/list'
    },
    logined = $('#j-main').data('logined')

// 加载数据
Util.post({
    url: urls.init,
    callback: function(data){

        if(data.success){
            init(data.result)
        } else{
            // Dialog.show({content: '后台加载数据出错：'})
            log('后台加载数据异常：', data)
        }
    }
})

function goLoginPage() {
    Util.post({ url: urls.receive });
}

/*
*  document: http://192.168.0.197:8090/pages/viewpage.action?pageId=31981710
*/
let init = (result)=>{
    var activityStatus = result.activityStatus || '',
        startTime = result.startTime || '', // 活动开始时间
        totalKm = result.totalKm || 0, // 登录用户的总里程
        packages = result.packages || 0, // 登录用户待领取的背包数
        boxes = result.boxes || 0, // 登录用户待领取的宝箱数
        topList = result.topList || [], // 排行榜 memberName avatar Remark
        avatar = result.avatar || ''

    if(!logined){
        // 点击领取人气值 , #j-btn-boxes, #j-checkAssist, #j-btn-invite
        $('#j-btn-packages, #j-btn-boxes, #j-checkAssist, #j-btn-invite').on('click', function(){
            goLoginPage()
        })

    } else {
        if(result.avatar){
            $('#j-this-avatar').attr('src', 'http://oss.aliyuncs.com' + avatar).addClass('z-square')
        }
        $('#j-mapIcon').removeClass('z-hide')
        
        $('#j-totalPop').text(result.totalPop || 0)
        $('#j-totalCoupon').text(result.totalCoupon || 0)
        $('#j-result-content').removeClass('z-hide')
        
        // 点击 '查看好友助力记录' 按钮，页面滚动到对应部分
        var $htmlBody = $('html, body'),
            assistOffset = $('#j-assist').offset().top - 80

        $('#j-checkAssist').on('click', function(){
            $htmlBody.animate({
                scrollTop: assistOffset
            }, 500)
        })

        // 邀请好友按钮，点击展示浮层
        $('#j-btn-invite').on('click', function () {
            $('#j-qrcode-wrap').removeClass('z-hide')
            $.shade('show')
        })

        // 邀请链接复制
        var clipboard = new Clipboard('#j-copy-invite')
        clipboard.on('success', function() {
            Dialog.show({
                type: 'success',
                content:'复制成功'
            })
        })

        $('body').on('click', '.u-shade, #j-qrcode-close', function () {
            $('#j-qrcode-wrap').addClass('z-hide')
            $.shade('hide')
        })
    }

    // 里程拉辛没有活动开始或者结束这一说
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

    // 渲染用户友情榜
    if(topList.length == 0){
        $('#j-gainList').addClass('z-noData').text('快来和好友一起参加活动吧')
    } else {
        let tempStr = template('j-gainList-tpl', result)
        $('#j-gainList').html(tempStr).scrollList({
            size: 5,
            height: 50,
            length: 1,
            time: 5000,
            stoppable: true
        })
    }
}

let beforeActive = (result) => {
    // log('活动未开始')
    $('#j-gainList').addClass('z-noData').html('暂无数据')

    var shadeHeight = $('#j-assist-table-wrap').height()
    $('#j-assist-shade').text('快来和好友一起参加活动吧').css('height', shadeHeight).removeClass('z-hide')

    if(logined){

        $('#j-btn-packages, #j-btn-boxes').on('click', function () {
            Dialog.show({
                content: '活动未开始'
            })
        })
    }
}

let duringActive = (result) => {
    // 设置里程位置
    var totalKm = result.totalKm - 0 || 0
    var friendListCount = result.friendListCount || 0

    if(logined){
        $('#j-packages').text(result.packages || 0)
        $('#j-boxes').text(result.boxes || 0)
    }

    setMapSituation(result.totalKm || 0)
    renderBoxText(result) // 渲染宝箱的提示内容

    // 渲染宝箱的提示内容
    function renderBoxText(result) {
        var totalKm = result.totalKm || 0,
            boxTextObj = {
                totalKm1: {coupon30: 1, coupon50: 1, coupon100: 1},
                totalKm2: {coupon30: 2, coupon50: 1, coupon100: 1},
                totalKm3: {coupon30: 2, coupon50: 2, coupon100: 1},
                totalKm4: {coupon30: 1, coupon50: 1, coupon100: 2},
                totalKm5: {coupon30: 2, coupon50: 1, coupon100: 2},
                totalKm6: {coupon30: 3, coupon50: 1, coupon100: 2},
                totalKm7: {coupon30: 2, coupon50: 2, coupon100: 2}
            },
            boxTextNum = Math.ceil(totalKm / 100)

        log(boxTextNum)
        if(boxTextNum > 6){
            boxTextNum = 7
        }
        if(boxTextNum == 0){
            boxTextNum = 1
        }

        var tempBoxStr = template('j-box-detail-tpl', boxTextObj['totalKm' + boxTextNum])

        $('#j-box-detail').html(tempBoxStr)
    }


    // 设置两个按钮上的图标数量
    if(!logined){
        $('#j-btn-packages').on('click', function () {
            goLoginPage()
        })
    } else {
        if(result.packages > 0 && $('#j-packages').text() - 0 > 0) {
            $('#j-packages').text(result.packages).parent().removeClass('z-hide')
            $('#j-btn-packages').on('click', function () {
                var $this = $(this)
                if($('#j-packages').text() - 0 == 0){
                    Dialog.show({
                        content: '没有奖励可领取哦'
                    })
                } else {
                    Util.post({
                        url: urls.receive,
                        data: {'rewardType': 1},
                        callback: function (data) {
                            if (data.success) {
                                Dialog.show({
                                    type: 'success',
                                    content: '恭喜你获得幸运背包一个<br>奖励' + data.result.receiveReturnStr
                                })
                                var tempNum = $('#j-packages').text() - 0
                                if(tempNum > 0){
                                    $('#j-packages').text(tempNum-1)
                                }
                            } else {
                                Dialog.show({
                                    content: '没有奖励可领取哦'
                                })
                            }
                        }
                    })
                }
            })
        } else {
            $('#j-btn-packages').on('click', function () {
                Dialog.show({
                    content: '没有奖励可领取哦'
                })
            })
        }
    }

    if(!logined){
        $('#j-btn-boxes').on('click', function () {
            goLoginPage()
        })
    } else {
        if(result.boxes > 0 && $('#j-boxes').text() - 0 > 0){
            $('#j-boxes').text(result.boxes).parent().removeClass('z-hide')
            $('#j-btn-boxes').on('click', function () {
                var $this = $(this)
                if($('#j-boxes').text() - 0 == 0){
                    Dialog.show({
                        content: '没有奖励可领取哦'
                    })
                } else {
                    Util.post({
                        url: urls.receive,
                        data: {'rewardType': 2},
                        callback: function (data) {
                            if(data.success){
                                var receiveReturnStr = data.result.receiveReturnStr,
                                    tempArr = receiveReturnStr.split(','),
                                    tempStr = ''
                                for (var i = 0; i < tempArr.length; i++) {
                                    var tempArr2 = tempArr[i].split('-')
                                    console.log(tempArr2)
                                    tempStr += '奖励' + tempArr2[0] + '元现金券' + tempArr2[1] + '张 <br>'
                                }
                                tempStr = '<span style="position:relative; top: -40px">恭喜你获得幸运宝箱一个<br>' + tempStr + '</span>'
                                Dialog.show({
                                    type: 'success',
                                    content: tempStr
                                })
                                var tempNum = $('#j-boxes').text() - 0
                                if(tempNum > 0){
                                    $('#j-boxes').removeClass('z-hide').text(tempNum-1)
                                }
                            } else {
                                Dialog.show({
                                    content: '没有奖励可领取哦'
                                })
                            }
                        }
                    })
                }
            })
        } else {
            $('#j-btn-boxes').on('click', function () {
                Dialog.show({
                    content: '没有奖励可领取哦'
                })
            })
        }
    }

    // 好友助力表
    if(logined){
        Util.post({
            url: urls.list,
            callback: function (data) {
                if(data.success){
                    renderFriendList(data) // 渲染好友助力
                    renderPagination(data) // 渲染分页
                } else {
                    // log('没有好友助力记录哦')
                    var shadeHeight = $('#j-assist-table-wrap').height()
                    $('#j-assist-shade').css('height', shadeHeight).removeClass('z-hide')
                }
            }
        })
        // 3399ff
        const renderFriendList = (data)=>{
            // 渲染表格
            var result = data.result || {}
            var friendList = result.friendList || []
            if(friendList.length > 0){
                // 遍历循环出每个好友的完成情况
                var tempList = [],
                    friendListObj = {}
                $.each(friendList, function (i, val) {
                    tempList = []
                    friendListObj = {}

                    var remark = val.remark || ''
                    tempList = remark.split(',')
                    $.each(tempList, function (j, val2) {
                        if(val2){
                            friendListObj[val2] = val2
                        }
                    })

                    val.friendListObj = friendListObj
                })

                let tempStr = template('j-friendList-tpl', data.result)
                $('#j-friendList').html(tempStr)
            } else {
                var shadeHeight = $('#j-assist-table-wrap').height()
                $('#j-assist-shade').css('height', shadeHeight).removeClass('z-hide')
            }
        }

        // 姗姗测试账号 18258222255
        // 渲染分页，TODO 这里暂时先不考虑大于10几页的情况，需要50人以上才会大于10页
        function renderPagination(data) {
            var result = data.result
            var friendListCount = result.friendListCount // 获取分页数
            if(friendListCount > 5){
                var pages = Math.ceil(friendListCount / 5)
                var paginations = ''
                for(var i = 0; i < pages; i++){
                    var j = i + 1
                    if(i==0){
                        paginations += '<li class="u-page z-current" data-startNo="'+ i * 5 +'">'+ j +'</li>'
                    } else {
                        paginations += '<li class="u-page" data-startNo="'+ i * 5 +'">'+ j +'</li>'
                    }
                }
                paginations = '<li class="u-prev" id="j-prev">上一页</li>'
                    + paginations
                    + '<li class="u-next" id="j-next">下一页</li>'
                $('#j-pagination').html(paginations).removeClass('z-hide')

                // 分页按钮点击事件
                function goGetDataAndRenderList(startNo) {
                    Util.post({
                        url: urls.list,
                        data: { startNo: startNo },
                        callback: function (data) {
                            if(data.success && data.result.friendList && data.result.friendList.length > 0){
                                renderFriendList(data)
                            }
                        }
                    })
                }

                var $pagination = $('#j-pagination')
                $pagination.on('click', '.u-page', function(){
                    var $this = $(this),
                        startNo = $this.attr('data-startNo')
                    if(!$this.hasClass('z-current')){
                        $this.addClass('z-current').siblings('.u-page').removeClass('z-current')
                        goGetDataAndRenderList(startNo)
                    }
                }).on('click', '#j-prev', function () {
                    // log('上一页呗点击了')
                    var pageIndex = $pagination.find('.z-current').index() + 1
                    if(pageIndex == 2){
                        return false
                    } else {
                        $pagination.find('li.z-current').removeClass('z-current')
                            .prev('li').addClass('z-current')
                        var startNo = $pagination.find('.z-current').attr('data-startNo')
                        goGetDataAndRenderList(startNo)
                    }
                }).on('click', '#j-next', function () {
                    // log('下一页呗点击了')
                    if($pagination.find('.z-current').index() == $pagination.find('li').length-2){
                        return false
                    } else {
                        $pagination.find('li.z-current').removeClass('z-current')
                            .next('li').addClass('z-current')
                        var startNo = $pagination.find('.z-current').attr('data-startNo')
                        goGetDataAndRenderList(startNo)
                    }
                })
            }
        }
        // 渲染分页结束

    }
}


let afterActive = (result) => {
    alert('活动已结束')
}


// 设置地图图标位置和路标的数值等
let setMapSituation = (roadTotalCount) => {
    var i = 0,
        $mapIcon = $('#j-mapIcon'),
        $mapIconContentWrapper = $('#j-mapIcon-content-wrapper'),
        $mapIconContent = $('#j-mapIcon-content'),
        $roadSign = $('.j-roadSign'),
        ge, shi, bai, // 个，十，百位上的数字
        roadSignPart1 = 0 // 总数减去十位和个位上的数字

    ge = roadTotalCount - ((Math.floor(roadTotalCount/10))*10) - 0 //个位
    shi = (roadTotalCount - ((Math.floor(roadTotalCount/100))*100) - ge)/10 //十位数
    bai = (roadTotalCount - ((Math.floor(roadTotalCount/1000))*1000)-(shi*10)-ge)/100 - 0 //百位上的数字
    // 如果超过一百，图标上的数字也要对应改变
    if(roadTotalCount > 100 ){
        roadSignPart1 = roadTotalCount - shi * 10 - ge
        // 到满百的时候是停留在100位而不是0位
        if(roadTotalCount % 100 === 0){
            shi = 10
        }
        if(roadTotalCount == roadSignPart1){
            $roadSign.each(function(){
                $(this).text($(this).attr('data-roadNotice') - 0 + roadSignPart1 - 100)
            })
        } else {
            $roadSign.each(function(){
                $(this).text($(this).attr('data-roadNotice') - 0 + roadSignPart1)
            })
        }
    }

    if(roadTotalCount == 100){
        shi = 10
    }

    // 到达五位数的时候，图标的宽度要稍微加一点了，不然太挤了
    if(roadTotalCount > 9999){
        $mapIconContentWrapper.addClass('z-bigger')
        $roadSign.addClass('z-smallFont')
    }
    $mapIcon.removeClass().addClass('u-mapIcon').addClass('z-step' + shi)
    $mapIconContent.text(roadTotalCount)
}

// 四个背包鼠标移上去的时候需要有具体奖励提示
$('.j-vr-bag').hover(function(event){
    event.stopPropagation()
    $(this).find('.u-gift-detail').toggleClass('z-fadeIn')
})

// 微信微博按钮
var $shareDiv = $('#j-share>div')
$('.j-popup-trigger').hover(function(){
    $shareDiv.removeClass('z-fadeIn')
    $(this).next().addClass('z-fadeIn')

}, function(){
    $shareDiv.removeClass('z-fadeIn')
})





