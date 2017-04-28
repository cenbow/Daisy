(function () {
    //01.焦点图按钮位置矫正
    var focusBtn = $(".u-focus-btn");
    focusBtn.css("margin-left", "-" + focusBtn.outerWidth() / 2 + "px");

    //02.Banner区域签到引导
    signinGuide(".j-focus-show");

    //03.Banner延迟加载
    var $focusBtn=$('#j-focus-btn'),
        $focusLink=$('#j-focus-show').find('li');

    $focusBtn.on('mouseenter','li', function () {
        loadBannerImg($focusLink,$(this).index());
    }).on('click','li', function () {
        var i=$(this).index();

        if(i<$focusLink.length-1){
            setTimeout(function () {
                loadBannerImg($focusLink,i+1);
            },7500);
        }
    });
    //首页项目类型切换
    var $categorytitle = $('#j-category-title'),
        $productlist = $('.j-product-list');

    $categorytitle.on('click', 'h2', function () {
        var i = $(this).index();
        $(this).addClass('z-current').siblings().removeClass('z-current');
        $productlist.eq(i).removeClass('f-dn').siblings('ul').addClass('f-dn');
    });
    var firstIndex= 1,
        bannerLoadTimer=setInterval(function () {

            if(firstIndex<$focusLink.length){
                loadBannerImg($focusLink,firstIndex);
                ++firstIndex;
            }else{
                clearInterval(bannerLoadTimer);
            }
        },7500);

    //! Banner加载
    function loadBannerImg($target,index){
        var $imgLink=$target.eq(index).find('a');

        if($imgLink.css('background-image')==='none'){
            $imgLink.css('background-image','url('+$imgLink.data('imgurl')+')');
        }
    }

    //04.首页圆环
    var $pstat = $('.j-pl-stat');
    if ($pstat.length > 0) {
        $("body").append("<canvas id='j-canvas-demo'></canvas>");
        try {
            $("canvas")[0].getContext("2d");
            $pstat.circliful();
        } catch (err) {

        }
        $("#j-canvas-demo").remove();
    }

    //05.首页底部合作伙伴和媒体报道
    var $partnertitle = $('#j-partner-title'),
        $partnerlist = $('.j-partner-list');
    $partnertitle.on('click', 'span', function () {
        var i = $(this).index();
        $(this).addClass('z-current').siblings().removeClass('z-current');
        $partnerlist.eq(i).removeClass('f-dn').siblings('ul').addClass('f-dn');
    });

    //$.extend({
    //    /*
    //     * banner倒计时
    //     * @endDate 结束时间，格式：'2015/07/31 23:59:59'
    //     * @effectId banner的唯一标示(在后台指定)
    //     */
    //    bannerCountdown: function (endDate, effectId) {
    //        var curDate = new Date(environment.serverDate),
    //            endTime = new Date(endDate),
    //            days = endTime.getDate() - curDate.getDate() + 1;
    //        if (curDate > endTime) {
    //            days = 0;
    //        }
    //        if (days < 10) {
    //            if (days < 0) {
    //                days = 0;
    //            }
    //            days = "0" + days;
    //        }
    //        $('.j-focus-show ul').find('[data-effect="' + effectId + '"]')
    //            .append('<div class="u-countdown-wrap"><p class="u-' + effectId + '-countdown">' + days + '</p></div>');
    //    }
    //});
    //$.bannerCountdown('2015/07/31 23:59:59', 'heavyInvest');
})();

(function () {
    //IE8,IE9不支持sparkline
    //navigator.userAgent.indexOf("MSIE 9.0")
    var IEVersion=navigator.appVersion,
        $heartbeatBox=$('#j-heartbeat-box');
    if(IEVersion.match(/8./i)=="8."||IEVersion.match(/9./i)=="9."){
        $heartbeatBox.addClass('f-dn')
            .siblings('.j-invest-newest').removeClass('f-dn')
            .siblings('span').hide();
        return false;
    }

    //首页头部心跳投资切换
//    function slideInvestnewst(){
//        var $investnewest=$('.j-invest-newest'),
//            $investcut=$('#j-invest-cut');
//
//        setTimeout(once,5000);
//        setTimeout(twice,10000);
//        function once(){
//            $investcut.find('i').eq(0).addClass('z-current').siblings().removeClass('z-current');
//            $investnewest.eq(0).removeClass('f-dn').siblings('div').addClass('f-dn');
//        }
//        function twice(){
//            $investcut.find('i').eq(0).removeClass('z-current').siblings().addClass('z-current');
//            $investnewest.eq(0).addClass('f-dn').siblings('div').removeClass('f-dn');
//        }
//        $investcut.on('click','i',function(){
//            var i = $(this).index();
//            $(this).removeClass('z-current').siblings().addClass('z-current');
//            $investnewest.eq(i).addClass('f-dn').siblings('div').removeClass('f-dn');
//        });
//}

    //socket client
    //var wsUrl='http://ws.yrw.com:8100';
    //
    //if(/dev|test/.test(location.href)){
    //    wsUrl=environment.globalDomain.replace(/[0-9]{4}/i,'8100');
    //}
    //var socket = io.connect(
    //        wsUrl,
    //        {
    //            'transports': ['websocket', 'flashsocket', 'xhr-polling', 'jsonp-polling']
    //        }
    //    ),
    //    pointsSize = 30,//投资额折线点数量
    //    msgArray = [],//投资列表数组
    //    transactionArray=[],//投资记录ID列表
    //    lastPoint = {},
    //    $heartBeat = $('#j-heartbeat'),
    //    pointsArray = [];//投资额数组

    //默认不出现心跳
    $heartbeatBox.addClass('f-dn')
        .siblings('.j-invest-newest').removeClass('f-dn')
        .siblings('span').hide();

    ////心跳消息
    //socket.on('heartbeat', function (msg) {
    //    var data = msg.data;
    //    //消息队列缓存
    //    if (data.length > 1) {
    //        msgArray = data;
    //    } else if(data.length===1) {
    //        msgArray.push(data[0]);
    //    }
    //
    //    if(data.length&&msgArray.length>29){
    //        $heartbeatBox.removeClass('f-dn')
    //            .siblings('.j-invest-newest').addClass('f-dn')
    //            .siblings('span').show();
    //    }
    //
    //    //折线图数组
    //    if (data.length >= pointsSize) {
    //        $.each(data, function (index, item) {
    //            var amount = index % 2 ? item.investAmount : -item.investAmount;
    //            pointsArray.push(amount);
    //            transactionArray.push(item.transactionTime);
    //        });
    //        slideInvestnewst();
    //    } else {
    //        var pointsLength = pointsArray.length;
    //        if (pointsLength) {
    //
    //            //阻止重复数据
    //            // FIXME 有重新心跳数据，暂时先过滤，后期再查redsocks原因
    //            if($.inArray(data[0].transactionTime,transactionArray)!==-1){
    //                return false;
    //            }
    //
    //            //投资额队列
    //            var investAmount = data[0].investAmount;
    //            if (pointsArray[pointsLength - 1] > 0) {
    //                pointsArray.push(-investAmount);
    //            } else {
    //                pointsArray.push(investAmount);
    //            }
    //        }
    //    }
    //    //最新投资记录点
    //    lastPoint = pointsArray[pointsArray.length - 1];
    //    //移除最新的投资记录点(避免右侧重复)
    //    pointsArray = pointsArray.slice(0,-1);
    //    $.merge(pointsArray,[0,0]);
    //
    //    //初始心跳显示
    //    if(data.length >= pointsSize){//只初始时显示
    //        heartBeat(pointsArray);
    //    }
    //
    //    //动态显示效果
    //    var isOnload = (data.length !== 1),
    //        $investBox=$('#j-invest-box'),
    //        lastIndex = msgArray.length > 1 ? msgArray.length - 1 : 0;
    //    if (isOnload) {
    //        $investBox.replaceWith(getUserInvestHtml(msgArray, lastIndex, true));
    //
    //    } else {
    //        //$investBox.fadeOut(500, function () {
    //        //    $investBox.replaceWith(getUserInvestHtml(msgArray, lastIndex, true)).fadeIn(500);
    //        //});
    //    }
    //});
    //
    ////四重礼消息
    //var isOnload=true;
    //socket.on('giftbeat', function (msg) {
    //    if(isOnload){
    //        giftLoopAnimate(msg.data);
    //        isOnload=false;
    //    }
    //    //var giftLoopTime=window.giftLoopTime,
    //    //    splitTime=0;
    //    //if(giftLoopTime){
    //    //    splitTime=giftLoopTime-(new Date().getTime());
    //    //    if(splitTime>=3000){
    //    //        giftLoopAnimate(msg.data);
    //    //    }else{
    //    //        setTimeout(function () {
    //    //            giftLoopAnimate(msg.data)
    //    //        },3000-splitTime);
    //    //    }
    //    //}else{
    //    //    giftLoopAnimate(msg.data);
    //    //}
    //    //window.giftLoopTime = new Date().getTime();
    //});
    //
    ////时间刷新
    ////var nowTime=new Date().getTime();
    ////setInterval(function () {
    ////    refreshInvestTime(msgArray,pointsArray,nowTime,$('#j-invest-box'));
    ////},60*1000);
    //
    ////心跳图实例
    //function heartBeat(points) {
    //    $heartBeat.sparkline(points, {
    //        type: 'line',
    //        width: 520,
    //        tooltipPrefix: '',
    //        //tooltipSuffix: html,
    //        height: '90',
    //        lineColor: '#ffffff',
    //        lineWidth: 2,
    //        minSpotColor: false,
    //        maxSpotColor: false,
    //        spotRadius:2.7,
    //        highlightLineColor: '#d74148',
    //        highlightSpotColor: '#fff',
    //        defaultPixelsPerValue: 0,
    //        tooltipSkipNull: true,
    //        tooltipFormatter: function (sparkline, options, fields) {
    //            console.log(fields.x);
    //            return getUserInvestHtml(msgArray, fields.x, false);
    //        },
    //        fillColor: null
    //    });
    //    //移除数组里的补位零
    //    pointsArray = points.slice(0, -2);
    //    //增加新的数据点
    //    pointsArray.push(lastPoint);
    //
    //}
    //
    ////心跳折线点提示框
    //$heartBeat.bind('sparklineRegionChange', function (ev) {
    //    var sparkline = ev.sparklines[0],
    //        region = sparkline.getCurrentRegionFields(),
    //        value = region.y,
    //        $tooltip = $('#jqstooltip');
    //    $tooltip.css({
    //        'border': 0,
    //        'background': 'none'
    //    });
    //
    //    !value ? $tooltip.hide() : $tooltip.show();
    //});
    //
    ////投资记录HTML
    //function getUserInvestHtml(msgArray, index, onload) {
    //
    //    var item = msgArray[index] || {},
    //        avatars = item.avatars ? "https://oss-cn-hangzhou.aliyuncs.com" +
    //        item.avatars : "/static/img/member/avatar_35x35.png",
    //        fromNow = 0,
    //        unit = '刚刚',
    //        tTime = item.transactionTime,
    //        splitTime=item.splitTime|| 0,
    //        tooltipClassName = onload ? '' : 'm-invest-tooltip',
    //        projectName = item.projectName || '车有融110期',
    //        interval = Math.round(((new Date().getTime()) - tTime + splitTime) / 1000);
    //
    //    if (tTime) {
    //        if (interval >= 60 && interval < 3600) {
    //            fromNow = Math.floor(interval / 60);
    //            unit = '分钟前';
    //        } else if (interval >= 3600 && interval < 86400) {
    //            fromNow = Math.floor(interval / 3600);
    //            unit = '小时前';
    //        } else if (interval >= 86400) {
    //            fromNow = Math.floor(interval / 86400);
    //            unit = '天前';
    //        }else{
    //            fromNow='';
    //            unit='刚刚';
    //        }
    //    }else{
    //        fromNow='';
    //    }
    //
    //    return '<div class="m-invest-box ' + tooltipClassName + '" id="j-invest-box">' +
    //        '<div class="u-invest-user"><span class="u-user-head">' +
    //        '<img src="' + avatars + '" alt="用户头像">' +
    //        '<i>&nbsp;</i></span><span class="u-user-name">' + fromNow + unit +
    //        '&nbsp;&nbsp;' + item.maskUserName + '<br/>'+ '<a href="products/detail-'+item.projectId+'.html" target="_blank">'+projectName +'</a>'+ '&nbsp;&nbsp;&nbsp;投资￥'
    //        + splitAmount(item.investAmount) + '</span></div></div>';
    //
    //}
    //
    ////四重礼记录HTML
    //function getInvestGiftHtml(msgArray, onload) {
    //    var item = msgArray || {},
    //        avatars = item.avatars ? "https://oss-cn-hangzhou.aliyuncs.com" +
    //        item.avatars : "/static/img/member/avatar_35x35.png",
    //        fromNow = 0,
    //        unit = '刚刚',
    //        tTime = item.insertDate,
    //        tooltipClassName = onload ? '' : 'm-invest-tooltip',
    //        projectName = item.projectName || '车有融110期',
    //        interval = Math.round(((new Date().getTime()) - tTime) / 1000);
    //
    //    var giftArray = item.projectGift,
    //        tag='',
    //        giftText = '';
    //    for (var giftType in giftArray) {
    //        if (giftArray.hasOwnProperty(giftType) && giftArray[giftType]) {
    //            switch (giftType) {
    //                case 'first':
    //                    giftText = '一羊领头';
    //                    tag='#j-yylt';
    //                    break;
    //                case 'last':
    //                    giftText = '一锤定音';
    //                    tag='#j-ycdy';
    //                    break;
    //                case 'luck':
    //                    giftText = '幸运女神';
    //                    tag='#j-xyns';
    //                    break;
    //                case 'most':
    //                    giftText = '一鸣惊人';
    //                    tag='#j-ymjr';
    //                    break;
    //                default :
    //                    break;
    //            }
    //            break;
    //        }
    //    }
    //    if (tTime) {
    //        if (interval >= 60 && interval < 3600) {
    //            fromNow = Math.floor(interval / 60);
    //            unit = '分钟前';
    //        } else if (interval >= 3600 && interval < 86400) {
    //            fromNow = Math.floor(interval / 3600);
    //            unit = '小时前';
    //        } else if (interval >= 86400) {
    //            fromNow = Math.floor(interval / 86400);
    //            unit = '天前';
    //        }else{
    //            fromNow='';
    //            unit='刚刚';
    //        }
    //    }else{
    //        fromNow='';
    //    }
    //
    //    return '<div class="u-invest-luck f-dn">' +
    //        '<span class="u-user-head">' +
    //        '<img src="' + avatars + '" alt="用户头像">' +
    //        '<i>&nbsp;</i></span><span class="u-user-name">' + fromNow + unit +
    //        '&nbsp;&nbsp;' + item.maskUserName + '<br />' + '<a href="products/detail-' + item.projectId +'.html" target="_blank">'+projectName +'</a>' +
    //        '&nbsp;&nbsp;&nbsp;<i><a href="/activity/quadrupleGift'+tag+ '" target="_blank">' + giftText + ' </a></i></span></div>';
    //
    //
    //
    //
    //}
    //
    //
    ////四重礼队列动画
    //function giftLoopAnimate(data) {
    //    //队列里永远只有8条数据，每次来新的取最新的8条
    //
    //    var investGift = $('#j-invest-gift'),
    //        n = 0;
    //    if (data.length > 0) {
    //
    //        var giftData=data.reverse();
    //
    //        investGift.html(getInvestGiftHtml(giftData[n], 0, true));
    //        investGift.find('.u-invest-luck').show();
    //        if(data.length>1){
    //            var listTimer = setInterval(function () {
    //                investGift.find('.u-invest-luck').animate({
    //                    top: '90px',
    //                    opacity: 0
    //                }, 500, function () {
    //                    $(this).remove();
    //                    investGift.append(getInvestGiftHtml(giftData[n + 1], 0, true)).
    //                        find('.u-invest-luck').fadeIn(300);
    //                });
    //
    //                if (n < giftData.length - 2) {
    //                    n = n + 1;
    //                } else {
    //                    n = -1;
    //                }
    //            }, 3000);
    //        }
    //    }
    //}
    ////格式化金额
    //function splitAmount(amount) {
    //
    //    if (amount >= 1000) {
    //
    //        var amountStr = amount.toString(),
    //            size = parseInt(amountStr.length / 3),
    //            amountArray = amountStr.split('').reverse();
    //
    //        for (var i = 1; i <= size; i++) {
    //            var j = i * 3 - 1;
    //            if (j !== amountArray.length - 1) {
    //                amountArray[j] = ',' + amountArray[j];
    //            }
    //        }
    //
    //        return amountArray.reverse().join('');
    //
    //    } else {
    //        return amount;
    //    }
    //}
    //
    ////心跳记录时间刷新
    //function refreshInvestTime(msgArray,points,time,id){
    //    //时间差
    //    var splitTime=(new Date().getTime())-time;
    //    $.each(msgArray,function(index,item){
    //        item.splitTime=60*1000;
    //    });
    //    id.replaceWith(getUserInvestHtml(msgArray, msgArray.length-1, true));
    //    points = points.slice(0, -1);
    //    $.merge(points,[0,0]);
    //    heartBeat(points);
    //}
})();

(function(){
    //获取要定位元素距离浏览器顶部的距离
    var floatingWin=$("#j-floating-window")
        ,navH = floatingWin.offset().top;
    //滚动条事件
    floatingWin.css('top',790);
    $(window).scroll(function(){
        //获取滚动条的滑动距离
        var scroH = $(this).scrollTop();

        if(scroH<689){
            floatingWin.css({"position":"absolute","top":790});
        }else if(scroH>689){
            floatingWin.css({"position":"fixed","top":"10%"});
            if(scroH>1643){
                floatingWin.css({"position":"absolute","top":"85%"});
            }
        }
    });

    //鼠标悬浮显示二维码

    floatingWin.on('mouseenter','a',function(){
        $(this).find('span').removeClass('f-dn');
    }).on("mouseleave",'a',function () {
        $(this).find('span').addClass('f-dn');
    });


})();

// 如意图标点击跳转到春节活动页
(function(){
    var isRuyi=false;
    $('.j-icon-ruyi').on('click', function(){
        isRuyi=true;
        window.location.href = '/activity/springFestival/index#act4';
        return false;
    });

    $('.j-icon-ruyi').parent().on('click', function () {
        if(isRuyi){
            return false;
        }
    });
})();
