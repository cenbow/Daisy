//问候时间
$("#j-regards").greetingTime();
jQuery(function ($) {
    fnOnGoToPageActivedCashCoupon();
    fnOnGoToPageUsedCashCoupon();
    fnOnGoToPageExpiredCashCoupon();
    fnOnGoToPageActivedProfitCoupon();
    fnOnGoToPageUsedProfitCoupon();
    fnOnGoToPageExpiredProfitCoupon();
    //fnOnGoToPageInvite();//邀请好友
    if(location.href.indexOf('friendList#inviteFriends')===-1){
        fnOnGoToPageGain();// 获取记录
    }

    // 菜单下的箭头定位
    arrowLocator(".u-uc-menu");
});

// 加载优惠券
function loadCoupons(divId, callback) {
    var data = $(divId + " :input").serialize();
    $(divId + " .results").loading().load(environment.globalPath + "/coupon/couponListData", data,
        function () {
            $(divId + " .results li:odd").addClass("odd");
            /* 现金券数字显示 */
            $(divId + " input.j-cash-amount-num").each(function (i) {
                    var num = parseInt($(this).val())
                        + "";
                    var amountSpanObj = $(divId
                        + " span.u-coupon-amount:eq("
                        + i + ")");
                    for (var i = 0; i < num.length; i++) {
                        var n = num.substring(i,
                            i + 1);
                        var emObj = $("<em class='u-cnum-a u-cnum-s"
                            + n + "'></em>");
                        amountSpanObj.append(emObj);
                    }
                    amountSpanObj.append(emObj);
                });
            /* 收益券数字显示 */
            $(divId + " input.j-profit-amount-num").each(
                function (i) {
                    var num = $(this).val() + "";
                    var amountSpanObj = $(divId
                        + " span.u-coupon-amount:eq("
                        + i + ")");
                    if (num.indexOf(".")) {
                        var numArray = num.split(".");
                        for (var i = 0; i < numArray[0].length; i++) {
                            var n = numArray[0]
                                .substring(i, i + 1);
                            var emObj = $("<em class='u-cnum-a u-cnum-s"
                                + n + "'></em>");
                            amountSpanObj.append(emObj);
                        }
                        var poinEmObj = $("<em class='u-cnum-b u-cnum-mp'></em>");
                        amountSpanObj.append(poinEmObj);
                        for (var i = 0; i < numArray[1].length; i++) {
                            var n = numArray[1]
                                .substring(i, i + 1);
                            var emObj = $("<em class='u-cnum-b u-cnum-m"
                                + n + "'></em>");
                            amountSpanObj.append(emObj);
                        }
                    } else {
                        for (var i = 0; i < num.length; i++) {
                            var n = num.substring(i,
                                i + 1);
                            var emObj = $("<em class='u-cnum-a u-cnum-s"
                                + n + "'></em>");
                            amountSpanObj.append(emObj);
                        }
                    }
                    var bEmObj = $("<em class='u-cnum-b u-cnum-mb'></em>");
                    amountSpanObj.append(bEmObj);
                });
            callback ? callback() : null;
        });

}

/* 可使用现金优惠券 */
function fnOnGoToPageActivedCashCoupon() {
    var divId = "#activedCashCoupon";
    loadCoupons(divId);
}

/*已使用现金优惠券*/
function fnOnGoToPageUsedCashCoupon() {
    var divId = "#usedCashCoupon";
    loadCoupons(divId);

}

/* 已过期现金优惠券 */
function fnOnGoToPageExpiredCashCoupon() {
    var divId = "#expiredCashCoupon";
    loadCoupons(divId,goAnchor);
    function goAnchor() {
        setTimeout(function () {
            if (location.href.indexOf('#usedCashCoupon') !== -1) {
                location.href = '#usedCashCoupon';
            }
        },500);
    }
}

/* 可使用收益优惠券 */
function fnOnGoToPageActivedProfitCoupon() {
	var divId = "#activedProfitCoupon";
	loadCoupons(divId);
}

/* 已使用收益优惠券 */
function fnOnGoToPageUsedProfitCoupon() {
    var divId = "#usedProfitCoupon";
    loadCoupons(divId);
}

/* 已过期收益优惠券 */
function fnOnGoToPageExpiredProfitCoupon() {
    var divId = "#expiredProfitCoupon";
    loadCoupons(divId);
}

/* 获取记录load */
function fnOnGoToPageGain() {
    var data = $("#j-popularity-form").serialize();
    $("#j-popularity-list").loading().load(
        environment.globalPath + "/coupon/popularity/gain/list", data);
}

/* 兑换记录load */
function fnOnGoToPageExchange() {
    var data = $("#j-popularity-form").serialize();
    $("#j-popularity-list").loading().load(
        environment.globalPath + "/coupon/popularity/exchange/list", data);
}

function fnOnGoToPageInvite() {
    var data = $("#j-popularity-form").serialize();
    $("#j-popularity-list").loading().load(
        environment.globalPath + "/coupon/referral/list", data);
}
/* 按钮的选中样式转化 */
$(".j-type-select").on("click", function () {
    $(".m-activity-list").find(".z-current").removeClass("z-current");
    $(this).addClass("z-current");
    var index = $(this).index();
    // 箭头定位
    arrowLocator(".m-activity-list");
    $("#u-jiantou-up").attr("class", "u-arrow-up u-arrow-i" + index);
});

/* 获取人气值记录click */
$("#j-popularity-gain").on(
    "click",
    function () {
        var type = $(this).attr("value");
        if ($("#j-popularity-form input[name='type']").length > 0) {
            $("#j-popularity-form input[name='type']").val(type);
        } else {
            $("#j-popularity-form").append(
                $("<input type='hidden' name='type' value='" + type
                    + "'>"))
        }
        if ($("#j-popularity-form input[name='currentPage']").length > 0) {
            $("#j-popularity-form input[name='currentPage']").val(1);
        }
        $("#j-popularity-form input[name='type']").val(type);
        fnOnGoToPageGain();
    });
/* 兑换人气值记录click */
$("#j-popularity-exchange").on(
    "click",
    function () {
        var type = $(this).attr("value");
        if ($("#j-popularity-form input[name='type']").length > 0) {
            $("#j-popularity-form input[name='type']").val(type);
        } else {
            $("#j-popularity-form").append(
                $("<input type='hidden' name='type' value='" + type
                    + "'>"))
        }
        if ($("#j-popularity-form input[name='currentPage']").length > 0) {
            $("#j-popularity-form input[name='currentPage']").val(1);
        }
        fnOnGoToPageExchange();
    });

// 兑换优惠券
$(".j-exchange-cashCoupon").on("click", function () {
    var tempId = $(this).data("tempid");
    var xToken = $("#xToken").attr("value");
    $.xPost({
        url: environment.globalPath + "/coupon/exchange",
        data: {
            'value': tempId,
            'xToken': xToken
        },
        callback: function (data) {
            if (data.success) {
                $.xDialog({
                    content: "领用成功！",
                    type: "success", // success,warn,error,info
                    callback: function () {
                        window.location.href = environment.globalPath
                            + "/coupon/reputation";
                    } // 确认按钮回调
                });
            } else {
                $.xDialog({
                    content: "领用失败！",
                    type: "error"
                });
            }
        }
    });
});

(function () {
    /* 邀请好友列表click */
    var $inviteList=$("#j-invite-list");
    $inviteList.on("click", function () {
        if ($("#j-popularity-form input[name='type']").length > 0) {
            $("#j-popularity-form input[name='type']").remove();
        }
        if ($("#j-popularity-form input[name='currentPage']").length > 0) {
            $("#j-popularity-form input[name='currentPage']").val(1);
        }
        fnOnGoToPageInvite();
    });
    //点击邀请好友数跳转到邀请好友列表
    var currentUrl=location.href,
        tabIndex=currentUrl.indexOf('?type='),
        tabName=currentUrl.slice(tabIndex+6);
    if(tabIndex!==-1&&tabName==='friendList#inviteFriends'){
        $inviteList.trigger('click');
    }
    if (!$('#j-exchange-cashCoupon').length) {
        var uc = new XRW.UC;
        // 头部Banner
        uc.slideBanner('#j-uc-banner', 3000);
    }
    // 新手任务切换显示
    var directionBtn = $('.j-direction-btn');
    var isClick = true;

    directionBtn.on('click', function () {
        var direction = $(this).data('direction'),
            list = $(this).parent().find('ul'),
            listSize = list.find('li').length, step = 0,
            offset = Number(list.css('margin-left').replace('px', ''));

        // 防止频繁点击
        if (!isClick) {
            return false
        }
        isClick = false

        //滑动方向
        if (direction === 'left') {
            step = -1;
            if (Math.abs(offset) / 235 >= listSize - 4) {
                isClick = true
                return false;
            }
        } else {
            step = 1;
            if (offset === 0) {
                isClick = true
                return false;
            }
        }

        list.animate({
            marginLeft: offset + step * 235
        }, 500, function () {
            isClick = true
        });
    });

    // 人气值计数提示
    var $investCount=$('.j-invest-count');
    $investCount.on('mouseover', ".j-rep-count", function () {
        $(this).next('em').show();
    });
    $investCount.on('mouseout', ".j-rep-total", function () {
        $(this).hide();
    });
    // 人气值提示
    $('.j-rep-tips').on('mouseenter', function () {
        var content = $(this).parent().find('p'),
            index = $(this).index() - 2;
        content.eq(index).removeClass('f-dn').siblings('p').addClass('f-dn');

    }).on('mouseleave', function () {
        var content = $(this).parent().find('p'),
            index = $(this).index() - 2;
        content.eq(index).addClass('f-dn').siblings('p').addClass('f-dn');
    });

    var repCount = $('#j-rep-count');
    if(repCount.length){
        //人气值圆环
        showPointRing('#j-rep-round');

        //四重礼统计
        getQuadrupleGiftCount();

        //四重礼下拉滑动
        $('.j-list-slidedown').on('mouseenter', function () {
           $(this).animate({
               height:350
           });
        }).on('mouseleave', function () {
            $(this).animate({
                height:279
            });
        });
    }

    //人气值圆环
    function showPointRing(target) {
        var repRing=$(target),
            gtIE8 = $.support.leadingWhitespace;
        if(gtIE8){
            repRing.radialIndicator({
                barColor: '#D74148',
                radius: 154,
                barWidth: 6,
                initValue: 0,
                percentage: true,
                displayNumber: false
            });
            var countData = repRing.data(),
                percent =countData.point / 10;
            if(percent>=0.1&&percent<1){
                percent=1;
            }else if(percent<0.1){
                percent=0;
            }

            percent=parseInt(percent);
            countData.radialIndicator.animate(percent > 100 ? 100 : percent);
        }
    }
    //四重礼统计
    function getQuadrupleGiftCount() {
        var xToken = $("#xToken").attr("value");
        $.xPost({
            url: environment.globalPath + "/coupon/quadrupleGift/count",
            data: {
                'xToken': xToken
            },
            callback: function (data) {
                //if (typeof data !== "undefined" && data != "" && data != null)
                    var type = "", total = 0, emHtml = "";
                if (data.mostInvestCount > 0) {
                    type = "一鸣惊人";
                    emHtml = type + "<em class='u-count-small j-rep-count '>" + data.mostInvestCount +
                        "</em>" +
                        "<em class='u-count-big f-dn j-rep-total'>你已获得" +
                        "<i class='f-fs18 j-most-invest-count'>" + data.mostInvestCount +
                        "</i>次" + type + "<br>奖励" + data.mostInvestTotal + "点人气值</em>"
                    $(".j-most").html(emHtml);
                }
                if (data.lastInvestCount > 0) {
                    type = "一锤定音";
                    emHtml = type + "<em class='u-count-small j-rep-count '>" + data.lastInvestCount +
                        "</em>" +
                        "<em class='u-count-big f-dn j-rep-total'>你已获得" +
                        "<i class='f-fs18 j-most-invest-count'>" + data.lastInvestCount +
                        "</i>次" + type + "<br>奖励" + data.lastInvestTotal + "点人气值</em>"
                    $(".j-last").html(emHtml);
                }
                if (data.luckInvestCount > 0) {
                    type = "幸运女神";
                    emHtml = type + "<em class='u-count-small j-rep-count '>" + data.luckInvestCount +
                        "</em>" +
                        "<em class='u-count-big f-dn j-rep-total'>你已获得" +
                        "<i class='f-fs18 j-most-invest-count'>" + data.luckInvestCount +
                        "</i>次" + type + "<br>奖励" + data.luckInvestTotal + "点人气值</em>"
                    $(".j-luck").html(emHtml);
                }
                if (data.firstInvestCount > 0) {
                    type = "一羊领头";
                    emHtml = type + "<em class='u-count-small j-rep-count '>" + data.firstInvestCount +
                        "</em>" +
                        "<em class='u-count-big f-dn j-rep-total'>你已获得" +
                        "<i class='f-fs18 j-most-invest-count'>" + data.firstInvestCount +
                        "</i>次" + type + "<br>奖励" + data.firstInvestTotal + "点人气值</em>"
                    $(".j-first").html(emHtml);
                }
                if (data.mostAndLastInvestCount > 0) {
                    type = "一掷千金";
                    emHtml = type + "<em class='u-count-small j-rep-count '>" + data.mostAndLastInvestCount +
                        "</em>" +
                        "<em class='u-count-big f-dn j-rep-total'>你已获得" +
                        "<i class='f-fs18 j-most-invest-count'>" + data.mostAndLastInvestCount +
                        "</i>次" + type + "<br>奖励" + data.mostAndLastInvestTotal + "点人气值</em>"
                    $(".j-mostAndLast").html(emHtml);
                }
            }
        });
    }
    //我的优惠-我的人气值(清空人气值无过期人气值提示叉掉本地本年消失)
    var currentDate = formatDate(+environment.serverDate).substring(0, 7),
        $disappearTip = $('#j-valueTips-disappear'),
        userId = $disappearTip.data('userid') + '',
        userIdLatter = userId.substr(7, 11),
        resetTip = $.xStorage('resetTips_' + userIdLatter),
        checkDate = ''

    if ($disappearTip.length) {
        if (resetTip) {
            checkDate = formatDate(resetTip).substring(0, 7)
            if (checkDate !== currentDate) {
                $disappearTip.show()
            }
        } else {
            $disappearTip.show()
        }
        $disappearTip.on("click", "em", function () {
            $.xStorage('resetTips_' + userIdLatter, environment.serverDate)
            $disappearTip.remove()
        })
    }
})();


