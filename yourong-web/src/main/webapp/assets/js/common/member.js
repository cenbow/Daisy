/**
 * @model 用户中心
 */
import 'module/jquery.extend' // 问候时间
import {Checkin} from 'module/cube' // 签到引导
//问候时间
$("#j-regards").greetingTips()
//签到引导
Checkin.guide("body");


//用户中心Banner  note 这个只在 账户浏览的头部有用到
var ucBanner = $('#j-uc-banner');
if (ucBanner.length) {
    var $profit=$('#j-income-statistics');
    if($profit.length){
        var bannerTimer = setTimeout(bannerTimeoutAction, 3000);
        ucBanner.removeClass('f-dn').data('expand', true);
    }else{
        ucBanner.width(10).removeClass('f-dn');
        ucBanner.data('expand', false);
    }

    var intervalTime=0;
    ucBanner.on('mouseenter', function () {
        if (ucBanner.data('expand')) {
            clearTimeout(bannerTimer);
        } else {
            bannerTimeoutAction('expand');
        }
        intervalTime=new Date().getTime();
    }).on('mouseleave', function () {

        intervalTime=(new Date().getTime())-intervalTime;
        if(intervalTime<350){
            setTimeout(function () {
                bannerTimeoutAction();
            },3000-intervalTime);
        }else if(ucBanner.data('expand')){
            bannerTimeoutAction();
        }
    });
}
function bannerTimeoutAction(action) {
    var width = '10px',
        isExpand = false;
    if (action === 'expand') {
        width = '650px';
        isExpand = true;
    }
    ucBanner.animate({
        width: width
    },350, function () {
        ucBanner.data('expand', isExpand);
    });
}

// 模拟选择框 这个在 我的消息和个人资料等地方有用到
var $selector = $(".j-selector");
$selector.on("click", function (e) {
    $(this).toggleClass("z-actived");
    e.preventDefault();
    return false;
}).on("mouseleave", function () {
    $(this).removeClass("z-actived");
});
$(".j-selector ul").on("mouseenter", function () {
    $(this).parent().addClass("z-actived");
});
$(".u-scroll-selector ul").scroll(function (e) {
    e.stopPropagation();
});
$selector.on("click", "li", function () {
    var text = $(this).html(),
        val = $(this).attr("value"),
        parent = $(this).parent();
    //只限可用状态下选择
    if(!$(this).hasClass("z-disabled")){
        parent.siblings("button").html(text);
        parent.siblings(".j-selected-ipt").val(val);
    }else{
        return false;
    }
});


//个人中心头部余额展开效果
var $couponHeadbalance=$('#j-couponHead-balance');
$couponHeadbalance.on('mouseenter',function(){
    var hoverTime=$(this).data('hoverTime')||null,
        time=(new Date().getTime())-hoverTime;
    if(hoverTime===null||(hoverTime!==null&&time>500)){
        $(this).animate({width:'375px'},500).find('.j-couponHead-btn').animate({opacity:1},500);
    }
});
$couponHeadbalance.on('mouseleave',function(){
    $(this).data('hoverTime',new Date().getTime());
    $(this).animate({width:'215px'},500).find('.j-couponHead-btn').animate({opacity:0},500);
});

// 这段在 header.vm中用到，所有的用户中心页面都有的
$('.j-user-tips').hoverTips()
