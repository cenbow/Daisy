/**
 * @entry 有融网首页
 */

import 'module/jquery.extend'
import 'lib/js/jq/jquery.circliful.min.js'
import {Checkin} from 'module/cube'
import {Countdown} from 'module/countdown'
import {Util} from 'common/util'

//新闻
$('.j-scroll-news').fadeShow({
    time: 5000,
    type: 'news'
})

//Banner
let $focusShow = $('.j-focus-show'),
    $focusLink = $focusShow.find('li'),
    focusCount = $focusShow.data('size')

if (focusCount > 1) {
    $focusShow.fadeShow({
        time: 10000,
        type: 'banner'
    })
    //Banner按钮位置矫正
    let $focusBtn = $('.u-focus-btn')
    $focusBtn.css('margin-left', '-' + $focusBtn.outerWidth() / 2 + 'px')

    //Banner延迟加载
    $focusBtn.on('mouseenter', 'li', function () {
        loadBannerImg($focusLink, $(this).index())
    }).on('click', 'li', function () {
        var i = $(this).index()

        if (i < $focusLink.length - 1) {
            setTimeout(function () {
                loadBannerImg($focusLink, i + 1)
            }, 7500)
        }
    })
    let firstIndex = 1,
        bannerLoadTimer = setInterval(function () {

            if (firstIndex < $focusLink.length) {
                loadBannerImg($focusLink, firstIndex)
                ++firstIndex
            } else {
                clearInterval(bannerLoadTimer)
            }
        }, 7500)
}

function loadBannerImg($target, index) {
    let $imgLink = $target.eq(index).find('a')

    if ($imgLink.css('background-image') === 'none') {
        $imgLink.css('background-image', 'url(' + $imgLink.data('imgurl') + ')')
    }
}


//项目预告倒计时
let $pnt = $('#j-project-notice-time')
if ($pnt.length > 0) {
    Countdown.notice($pnt.attr('sencond'))
}

//项目圆环
let $pstat = $('.j-pl-stat')
if (!Util.lteIE9()&&$pstat.length > 0) {
    $('body').append('<canvas id="j-canvas-demo"></canvas>')
    try {
        $('canvas')[0].getContext('2d')
        $pstat.circliful()
    } catch (err) {

    }
    $('#j-canvas-demo').remove()
}

//Banner区域签到引导
Checkin.guide('.j-focus-show')

//获取要定位元素距离浏览器顶部的距离
var floatingWin = $('#j-floating-window')
var sideDecorate = $('.u-sideDecorate')

//页面初始化时,侧导航和春节元素定位
scrollPosition()

//滚动条事件
$(window).scroll(function () {
    scrollPosition()
})

//侧导航和春节元素定位
function scrollPosition() {
    //获取滚动条的滑动距离
    var scroH = $(window).scrollTop()
    if (scroH < 689) {
        floatingWin.css({'position': 'absolute', 'top': 770})
        sideDecorate.css({'position': 'absolute', 'top': 265})
    } else if (scroH > 689) {
        floatingWin.css({'position': 'fixed', 'top': '9%'})
        sideDecorate.css({'position': 'fixed', 'top': '32%'})
        if (scroH > 1169) {
            sideDecorate.css({'position': 'absolute', 'top': 758})
        }
        if (scroH > 1455) {
            floatingWin.css({'position': 'absolute', 'top': 1550})
        }
    }
}
//置顶
$('#j-icon-gotop').on('click', function () {
    $("html,body").animate({
        'scrollTop': '0px'
    }, 500)
});

//鼠标悬浮显示二维码

floatingWin.on('mouseenter', 'a', function () {
    $(this).find('span').removeClass('f-dn')
}).on('mouseleave', 'a', function () {
    $(this).find('span').addClass('f-dn')
})

//项目列表切换
$('#j-category-title').switchList('.j-partner-list').on('click', 'h2', function () {
    $('.j-product-list').find('canvas').remove()
    if(!Util.lteIE9()&&$pstat.length > 0){
        $pstat.circliful()
    }
})

//合作伙伴切换
$('#j-partner-title').switchList('.j-product-list')