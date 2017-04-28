/*
 *亿路有你活动
 **/
(function () {
    //列表滚动
    $.fn.scrollUserList = function (config) {
        this.each(function () {
            var _this = $(this),
                scrollSize = _this.find("li").length;
            if (scrollSize > config.size) {
                setInterval(function () {
                    var scrollItems = _this.find("li:visible");
                    _this.animate({marginTop: config.height}, 700, function () {
                        scrollItems.eq(0).appendTo(_this);
                        _this.css("margin-top", 0);
                    });
                }, 1500);
            }
        })
    };
})();
(function () {
    var lotteryArrow = $('#j-lottery-arrow'),
        lotteryRotate = $('#j-lottery-rotate');
    //破亿钱数旋转角度
    var totalInvest = $('#j-totalInvest');
    $('#j-tips-text').rotate(2);
    $('#j-tips-amount').rotate(2);
    $('#j-time-end').rotate(3);
    totalInvest.rotate(2);
    totalInvest.parent().removeClass('f-dn');
    //加载时获取可抽奖次数
    if (lotteryArrow.data('logined')) {
        showRealCount(function (data) {
            lotteryArrow.data('count', data).find('strong').text(data);
        });
    }

    //加载转盘获奖列表
    showLotteryList('#j-luckly-list');
    //下载次数
    showDownloadCount('#j-download-count');
    //人气值达人榜&滚动
    showShareList(function (data) {
        var listArray = [], target = $('#j-yl-pValues');
        if ($.isArray(data) && data.length > 0) {
            $.each(data, function (i, item) {
                var avatar=item.avatar,
                    avatarUrl='/static/img/member/avatar_35x35.png';
                if(avatar){
                    avatarUrl=environment.aliyunPath+avatar;
                }
                listArray.push('<li class="u-yl-user"><span class="u-user-head"><img src="'+avatarUrl+'"' +
                ' alt="用户头像"> <i>&nbsp;</i></span><span class="u-user-name u-yl-fix">' +
                '&nbsp;&nbsp;' + item.memberName + '</span><span>' + item.drawInterval + '前获得<em>' +
                item.rewardInfo + '</em></span></li>');
            });
            $(target).html(listArray.join(''));
            //列表滚动
            $(target).scrollUserList({
                size: 5,
                height: -47,
                length: 1
            });
        }
    });

    //夺魁倒计时
    rankCountdown('#j-activity-countdown');
    var rankDate = new Date(environment.serverDate);
    if(rankDate.getDate()>6 || (rankDate.getDate()==6 && rankDate.getHours()>16)){//活动6号结束
        $('#j-activity-countdown').html('<em class="f-fs16 f-fwb" style="color:#FF5C5D;">亿举夺魁已结束</em>')
            .prev('span').remove();
    }else{
        rankCountdown('#j-activity-countdown');
    }

    //点击抽奖
    var ROTATE_OFFSET=52,
        giftAngleList = {
        'PopularityFor10': 144-ROTATE_OFFSET,
        'PopularityFor18': -144-ROTATE_OFFSET,
        'PopularityFor20': 72-ROTATE_OFFSET,
        'PopularityFor25': -72-ROTATE_OFFSET,
        'AnnualizedFor0.005': -36-ROTATE_OFFSET,
        'AnnualizedFor0.008': 36-ROTATE_OFFSET,
        'AnnualizedFor0.01': -108-ROTATE_OFFSET,
        'SelfieStickForMi': 108-ROTATE_OFFSET,
        'MiPhoneNote': 0-ROTATE_OFFSET,
        'Juicer': 180-ROTATE_OFFSET
    };
    lotteryArrow.on('click', function () {
        var arrowData = $(this).data();
        //登录跳转
        if (!arrowData.logined) {
            window.location.href = '/security/login?from=/activity/yiRoad/index';
            return false;
        }
        //0次不触发
        if (!arrowData.count) {
            $.xDialog({content: '很遗憾，您还没有抽奖机会，快去投资获取吧！'});
            return false;
        }

        //阻止反复点击
        if (arrowData.status) {
            return false;
        }
        $(this).data('status', 1).addClass('z-disabled');

        //初始转动
        lotteryRotate.rotate({
            angle: 0,
            animateTo: 21600,
            duration: 10000,
            callback: function () {
                lotteryArrow.data('status', 0).removeClass('z-disabled');
                $.xDialog({content: '网络不给力或者没登录，请刷新页面。'})
            }
        });
        //抽奖
        drawLottery(function (data) {
            //init
            lotteryArrow.data('status', 1).addClass('z-disabled');
            lotteryRotate.stopRotate();
            if(typeof(data)!=='object'){
                $.xDialog({content: '网络不给力或者没登录，请刷新页面。'});
                return false;
            }
            if (data.rewardCode === 'error') {
                $.xDialog({
                    content: '转盘偷了个懒……再来一次吧！',
                    callback: function () {
                        window.location.reload();
                    }
                });
                lotteryArrow.data('status', 0).removeClass('z-disabled');
                return false;
            }

            //随机位置系数
            var num = Math.random(),
                factor = Math.round(num) ? 1 : -1,
                point = num * factor * 12;

            //剩余抽奖次数
            $("#j-lottery-arrow").find('strong').text(data.realCount);

            //转动抽奖
            lotteryRotate.rotate({
                angle: 0,
                animateTo: 1440 + giftAngleList[data.rewardCode] + point,
                duration: 5000,
                callback: function () {
                    $.xDialog({
                        content: '恭喜您，中了' + data.rewardName + ' !',
                        type: 'success',
                        callback: function () {
                            window.location.reload();
                        }
                    });
                    lotteryArrow.data('status', 0).removeClass('z-disabled');

                }
            });
        });
    });

    //百度分享
    (function () {
        var isLogined = $('#j-lottery-arrow').data('logined'), shareBox = $('#j-share-box');

        if (isLogined) {
            todayShareCount(function (data) {
                shareBox.data('count', data);
            });
        }

        shareBox.on('click', 'a', function (e) {
            var count = shareBox.data('count');
            if (isLogined) {
                if(count!==0){
                    $.xPost({
                        url: environment.globalPath + "/activity/yiRoad/share",
                        callback: function (data) {
                            if(data.rewardName){
                                $.xDialog({content:'恭喜您，获得'+data.rewardName,type:'success'});
                                shareBox.data('count', 0);
                            }
                        }
                    })
                }

            } else {
                $.xDialog({content: '请先登录！'});
                return false;
            }
        });

    })();

    //转盘抽奖的获奖列表
    function showLotteryList(target) {
        var xToken = $('#xToken').val();
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/showList",
            data: {'xToken': xToken},
            callback: function (data) {
                var listArray = [];
                if ($.isArray(data) && data.length > 0) {
                    $.each(data, function (i, item) {
                        var avatar=item.avatar,
                            avatarUrl='/static/img/member/avatar_35x35.png';
                        if(avatar){
                            avatarUrl=environment.aliyunPath+avatar;
                        }
                        listArray.push('<li class="u-yl-user"><span class="u-user-head"><img src="'+avatarUrl+'"' +
                        ' alt="用户头像"> <i>&nbsp;</i></span><span class="u-user-name u-yl-fix">' +
                        '&nbsp;&nbsp;' + item.memberName + '</span><span>' + item.drawInterval + '前获得' +
                        item.rewardInfo + '</span></li>');
                    });
                    $(target).html(listArray.join(''));
                    //列表滚动
                    $("#j-luckly-list").scrollUserList({
                        size: 5,
                        height: -47,
                        length: 1
                    });
                }

            }
        })
    }

    showRankList('#j-rank-list');
    function showRankList(target) {
        var xToken = $('#xToken').val(),
            rankList = $('#j-rank-list').find('tr');
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/rankList",
            data: {'xToken': xToken},
            callback: function (data) {
                $.each(data, function (i, item) {
                    var thisInvest = '<span class="f-fl">'+item.thisUsername+'</span>'+
                            '<span class="f-fr">￥'+item.thisTotalInvest+'</span>',
                        lastInvest = '<span class="f-fl">'+item.lastUsername+'</span>'+
                            '<span class="f-fr">￥'+item.lastTotalInvest+'</span>',
                        date = new Date(environment.serverDate);
                    if (!item.lastUsername) {
                        if (i === 0) {
                            lastInvest = "叫我第一名";
                        } else {
                            lastInvest = "我要当第一";
                        }
                    }
                    if (!item.thisUsername) {
                        if (i===0) {
                            thisInvest = "叫我第一名";
                        } else {
                            thisInvest = "我要当第一";
                        }
                    }

                    if(date.getDate()>6){
                        lastInvest='亿路上有你';
                    }
                    if(date.getDate()>6 || (date.getDate()==6 && date.getHours()>16)){
                        thisInvest='亿路上有你';
                    }
                    rankList.eq(i).find('[name="today"]')
                        .html('<div class="f-cf u-yltable-patch">'+thisInvest+'</div>');
                    rankList.eq(i).find('[name="yesday"]')
                        .html('<div class="f-cf u-yltable-patch">'+lastInvest+'</div>');
                });
            }
        })
    }

    //夺魁抽奖
    function rankCountdown(target,seconds){
        var curTime = environment.serverDate,
            hours = new Date(curTime).getHours(),
            curDate=new Date(environment.serverDate),
            endTime = new Date(curDate.getFullYear()+'/'+(curDate.getMonth()+1)+'/'+curDate.getDate()+' 17:00'),
            countTime= 0,
            self=$(target);

        if(hours>=17){
            endTime = endTime.setDate(endTime.getDate()+1);
        }
        countTime=Math.ceil((endTime-curTime)/1000);
        if(typeof(seconds)!=='undefined'){
            countTime=seconds;
        }
        var countClock = self.FlipClock({
            autoStart: false,
            clockFace: 'HourlyCounter',
            countdown: true,
            callbacks:{
                stop:rankClockCallback
            }
        });

        countClock.setTime(countTime);
        countClock.setCountdown(true);
        countClock.start();

        self.find('a').prop('href','javascript:void(0)');
    }

    function rankClockCallback(){
        var day = new Date(environment.serverDate).getDate();
        if(day>=6){//活动6号结束
            $('#j-activity-countdown').html('<em class="f-fs16 f-fwb" style="color:#FF5C5D;">亿举夺魁已结束</em>')
                .prev('span').remove();
        }else{
            rankCountdown('#j-activity-countdown',24*3600);
        }

    }
    //抽奖
    function drawLottery(callback) {
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/drawLottery",
            callback: callback
        })
    }

    //当日是否分享
    function todayShareCount(callback) {
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/shareFlag",
            callback: callback
        })
    }

    //剩余抽奖次数
    function showRealCount(callback) {
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/realCount",
            callback: callback
        })
    }

    //人气值达人榜
    function showShareList(callback) {
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/shareList",
            callback: callback
        })
    }

    //APP下载次数
    function showDownloadCount(target) {
        $.xPost({
            url: environment.globalPath + "/activity/yiRoad/appDownCount",
            callback: function (data) {
                $(target).text(data);
            }
        })
    }
})();
