(function(){
    /*global environment*/
    var couponCount = $('#j-coupon-countdown'),
        countDown1 = $('.u-redFriday-countdown'),
        currentTime = environment.serverDate,
        currentDate = new Date(currentTime).getDate(),
        endTime = getTime('2015/06/' + currentDate + ' 18:00');

    var countDown2 = $('.u-redFriday-countdown2'),
        countClock = $('#j-coupon-countdown2').FlipClock({
            autoStart: false,
            clockFace: 'DailyCounter',
            countdown: true,
            callbacks: {
                stop: function () {
                    countDown2.addClass('f-dn');
                    countDown1.removeClass('f-dn');
                    couponCountdown();
                }
            }
        });
//活动时间
    var startTimeArray = [getTime('2015/06/12 09:00'), getTime('2015/06/19 09:00'), getTime('2015/06/26 09:00')],
        endTimeArray = [getTime('2015/06/12 18:00'), getTime('2015/06/19 18:00'), getTime('2015/06/26 18:00')];
//初始化
    if (inActivityTime()) {
        //剩余时间倒计时
        countDown1.removeClass('f-dn');
        getCountTime(endTime) ? couponCount.text(getCountTime(endTime)) : false;
        couponCountdown();
    } else {
        //开始时间倒计时
        countDown2.removeClass('f-dn');
        if (getNextCountTime()) {
            countClock.setTime(getNextCountTime());
            countClock.setCountdown(true);
            countClock.start();
        } else {
            countDown1.removeClass('f-dn');
            countDown2.addClass('f-dn');
        }
    }

//剩余时间倒计时
    function couponCountdown() {
        var countTimer = setInterval(function () {
            if (getCountTime(endTime)) {
                couponCount.text(getCountTime(endTime));
            } else {
                clearInterval(countTimer);
                countDown1.addClass('f-dn');
                countDown2.removeClass('f-dn');
                if (getNextCountTime()) {
                    countClock.setTime(getNextCountTime());
                    countClock.setCountdown(true);
                    countClock.start();
                } else {
                    countDown1.removeClass('f-dn');
                    countDown2.addClass('f-dn');
                }

            }

        }, 1000);
    }

//是否活动时间
    function inActivityTime() {
        var value = false;
        for (var i = 0; i < startTimeArray.length; i++) {
            if (currentTime >= startTimeArray[i] && currentTime <= endTimeArray[i]) {
                value = true;
                break;
            }
        }
        return value;
    }

    function getTime(dateString) {
        return new Date(dateString).getTime();
    }
//获取用于显示的倒计时时间
    function getCountTime(endTime) {
        var currentTime = new Date().getTime(),
            seconds = Math.round((endTime - currentTime) / 1000);
        if (endTime - currentTime < 0) {
            return false;
        }
        var timeArray = [Math.floor(seconds / 3600) || '00', Math.floor((seconds / 3600) % 1 * 60) || '00', seconds % 60];
        for (var i = 0; i < timeArray.length; i++) {
            if (timeArray[i] < 10 && timeArray[i] !== '00') {
                timeArray[i] = '0' + timeArray[i];
            }
        }
        var timeString = timeArray.join(":");
        if (timeString === '00:00:00') {
            return false;
        }
        return timeString;
    }
//获取距离下次的倒计时时间
    function getNextCountTime() {
        var currentTime = environment.serverDate;
        for (var i = 0; i < endTimeArray.length; i++) {
            if (currentTime < startTimeArray[i]) {
                return Math.round((startTimeArray[i] - currentTime) / 1000);
                break;
            }
        }
        return 0;

    }

//领取券
    $('#j-getCoupon').on('click', function () {
        var _this=$(this),
            logined = _this.data('logined');
        if (typeof(logined) === "undefined" || !logined) {
            window.location.href = environment.globalPath+'/security/login';
            return false;
        }
        if(_this.hasClass('z-disabled')){
            $.xDialog({content:'本期活动你已经参加了，请等待下一期吧！'});
            return false;
        }
        $.ajax({
            url: environment.globalPath+'/activity/receive/redFriday',
            type: 'POST',
            data: {"xToken": $('#xToken').val()},
            success: function (data) {
                if (data.success) {
                    $.xDialog({
                        content: '领取成功<div class="u-dialog-text"></div>',
                        type: 'success'
                    });
                    $('.u-dialog-btn').hide().parents('.ui-dialog-content').height(160);
                    $('.u-dialog-text').append($('#j-bdshare-box').show());
                    _this.addClass('z-disabled');
                    grayscale(_this);
                } else {
                    var result = data.resultCodeEum[0];
                    switch (result.code) {
                        case '90705':
                            $.xDialog({
                                content: result.msg, type: 'info', callback: function () {
                                    window.location.reload();
                                }
                            });

                    }
                }
            }
        });
    });


    //获取并展示用户列表
    $('#j-user-list').xLoading(function(target){
        $.getJSON(environment.globalPath+'/activity/queryReceivedCouponMember',function(data){
            var html=[];
            if(data.success){
                $.each(data.result,function(i,item){
                    var avatars='<img src="'+environment.globalPath+'/static/img/member/avatar_35x35.png" alt="用户头像">';
                    if(item.avatars){
                        avatars='<img src="'+environment.aliyunPath+'/'+item.avatars+'" alt="用户头像">';
                    }
                    html.push('<li><div class="u-user-wrap"><span class="u-user-head">'+
                    avatars+'<i>&nbsp;</i></span> '+item.username+'</div></li>');
                });
                $('#j-redFriday-list').append(html.join(''));
                scrollList();
            }else{
                $.xDialog({
                    content:'领取失败，请重试',
                    callback:function(){
                        window.location.reload();
                    }
                })
            }
        });
        target.loadingDone();
    },'center');

//滚动列表
function scrollList(){
    var scrollList = $("#j-redFriday-list"), scrollSize = scrollList.find("li").length;
    if (scrollSize > 32) {
        setInterval(function () {
            var scrollItems = scrollList.find("li:visible");
            scrollList.animate({marginTop: -59}, 700, function () {
                scrollItems.eq(0).appendTo(scrollList);
                scrollItems.eq(1).appendTo(scrollList);
                scrollItems.eq(2).appendTo(scrollList);
                scrollItems.eq(3).appendTo(scrollList);
                $(this).css("margin-top", 0);
            });
        }, 1500);
    }
}

//禁止时钟链接点击
    $('.flip-clock-wrapper ul li a').attr('href', 'javascript:void(0)');
})();
