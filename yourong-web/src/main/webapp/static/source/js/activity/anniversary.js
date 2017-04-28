/*
 * 周年庆活动 2015.11
 */
//ID&状态
var Award = {
    $btn: $('#j-award-btn'),
    $name: $('#j-award-name')
};

//环境变量
if(window.env){
    env.path=environment.globalPath;
    env.date=environment.serverDate;
    env.logined=Award.$btn.data('logined') ;
}
else{
    var env = {
        path: environment.globalPath,
        date: environment.serverDate,
        logined: Award.$btn.data('logined')
    };
}

//活动时间
var tNow = env.date,
    t17 = new Date('2015/11/17 00:00').getTime(),
    t18 = t17 + 86400 * 1000,
    t19 = t17 + 86400 * 2000,
    t20 = t17 + 86400 * 3000,
    t21 = t17 + 86400 * 4000,
    t22 = t17 + 86400 * 5000,
    t23 = t17 + 86400 * 6000,
    t24 = t17 + 86400 * 7000;

//倒计时扩展
$.fn.extend({
    timeCountdown: function (timestamp, callback) {
        this.each(function () {
            var _this = $(this),
                time = $(this).data('time') || timestamp;

            if (time > 0) {
                _this.text(getCountTime(time));
                var timer = setInterval(function () {
                    _this.text(getCountTime(time - 1));
                    time = time - 1;

                    if (time <= 0) {
                        clearInterval(timer);
                        callback();
                    }
                }, 1000);
            }
        });
        function getCountTime(seconds) {

            if (seconds < 0) {
                return 0;
            }
            var timeArray = [
                Math.floor(seconds / 86400) + '' || '00',
                seconds >= 86400 ? Math.floor(seconds % 86400 / 3600) :
                Math.floor(seconds / 3600) || '00',
                Math.floor((seconds / 3600) % 1 * 60) || '00',
                Math.floor(seconds % 60) || '00'
            ];

            for (var i = 0; i < timeArray.length; i++) {

                if (timeArray[i] < 10 && timeArray[i] !== '00') {
                    timeArray[i] = '0' + timeArray[i];
                }
            }
            var timeString = timeArray.join(":");
            if (timeString === '00:00:00:00') {
                return '00:00:00:00';
            }
            return timeString;
        }
    }
});

/*
 * Activity:活动倒计时
 */
(function () {
    var $actCountdown = $('#j-activity-countdown'),
        actCountTime = Math.floor((t17 - tNow) / 1000),//活动未开始到开始的倒计时
        actEndingCountTime = Math.floor((t24 - tNow) / 1000),//活动进行中到结束的倒计时
        actLastTime = Math.floor((t24 - t17) / 1000);//活动结束倒计时

    var countClock = $actCountdown.FlipClock({
        countdown: true,
        autoStart: false,
        clockFace: 'DailyCounter',
        callbacks: {
            stop: function () {
                if (countClock.status === 1) {
                    turntoEndingCountdown();
                }
                else if (countClock.status === 2) {
                    $actCountdown.parent().remove();
                }
            }
        }
    });
    //countClock.status 1=>未开始的倒计时 2=>进行中的倒计时
    if (tNow < t24) {
        var totalCountTime;
        //活动未开始
        if (tNow < t17) {
            totalCountTime = actCountTime;
            countClock.status = 1;
        }
        //活动进行中
        else {
            $actCountdown.next('span').text('距离活动结束仅剩');
            totalCountTime = actEndingCountTime;
            countClock.status = 2;
        }
        countClock.setTime(totalCountTime);
        countClock.start();
    }
    //活动结束
    else {
        $actCountdown.parent().remove();
    }

    function turntoEndingCountdown() {
        $actCountdown.next('span').text('距离活动结束仅剩');
        setTimeout(function () {
            countClock.setTime(actLastTime);
            countClock.setCountdown(true);
            countClock.start();
            countClock.status = 2;
        }, 1000);
    }
})();

/*
 * Activity:奖励大放送
 */
(function () {
    var initAward = {},
        awradTextList = [
            {text: '30点人气值，限时抢', btn: '点击抢人气值'},
            {text: '1%收益券，限时抢', btn: '点击抢收益券'},
            {text: '50元现金券，限时抢', btn: '点击抢现金券'},
            {text: '30点人气值，限时抢', btn: '点击抢人气值'},
            {text: '1%收益券，限时抢', btn: '点击抢收益券'},
            {text: '50元现金券，限时抢', btn: '点击抢现金券'},
            {text: '30点人气值，限时抢', btn: '点击抢人气值'}
        ];
    //获取初始化数据
    //$.xPost({
    //    url: env.path + '/activity/anniversary/initPrize',
    //    callback: function (data) {
    //        if (data.success) {
    //            var result = data.result;
    //            initAward = {
    //                activityId: result.activityId,
    //                received: result.participate,
    //                status: result.status,
    //                document: result.document,
    //                startTime: result.startTime,
    //                endTime: result.endTime,
    //                text: awradTextList[result.activityIndex].text,
    //                btnText: awradTextList[result.activityIndex].btn
    //            };
    //            //showAwardCountdown(result);
    //        }
    //    }
    //});
    Award.$name.text('奖励大放送已结束');
    Award.$btn.addClass('z-disabled').text('已结束');
    //奖励大放送倒计时
    //function showAwardCountdown(initData) {
    //    "use strict";
    //    var status = initData.status,
    //        act = awradTextList[initData.activityIndex];
    //    Award.$name.text(initData.document);
    //
    //    if (status === 6) {
    //        Award.$btn.addClass('z-disabled').text('已结束');
    //    } else {
    //        //未开始时倒计时
    //        if (status === 2) {
    //            //(initData.startTime-env.date)/1000
    //            Award.$btn.addClass('z-disabled').timeCountdown(
    //                (initData.startTime - env.date) / 1000,
    //                function () {
    //                    //倒计时结束后设置
    //                    Award.$btn.text(act.btn).removeClass('z-disabled');
    //                    Award.$name.text(act.text);
    //                    initAward.status=4;
    //                    //活动结束后准备开始下轮倒计时
    //                    setNextCountdown(3600*1000);
    //                }
    //            );
    //        }else if(status === 4){
    //            Award.$btn.text(initData.btnStr).removeClass('z-disabled');
    //        }
    //        //开始后显示按钮
    //        else {
    //            Award.$btn.text(initData.btnStr).removeClass('z-disabled');
    //            //活动结束后准备开始下轮倒计时
    //            setNextCountdown(initData.endTime-env.date);
    //        }
    //    }
    //
    //    //活动结束后准备开始下轮倒计时
    //    function setNextCountdown(time){
    //        var date=new Date(env.date).getDate();
    //        if(date===23){
    //            Award.$name.text('奖励大放送已结束');
    //            Award.$btn.addClass('z-disabled').text('已结束');
    //        }else{
    //            setTimeout(function () {
    //                Award.$name.text('敬请期待');
    //                initAward.status=2;
    //                Award.$btn.addClass('z-disabled').timeCountdown(23 * 3600,
    //                    function () {
    //                        //23个小时还不刷新页面就强制刷新
    //                        location.reload();
    //                    }
    //                );
    //            }, time);
    //        }
    //
    //    }
    //}

    //领取奖励大放送
    Award.$btn.on('click', function () {
        var activityId = initAward ? initAward.activityId : 0,
            received = initAward ? initAward.received : 0;
        if ($(this).hasClass('z-disabled')) {
            return;
        }
        return false;
        //是否活动时间
        //if (initAward.status === 2) {
        //    $.xDialog({content: '活动暂未开始，敬请期待。'});
        //} else {
        //    //是否登录
        //    if (env.logined) {
        //
        //        //是否已领取
        //        if (received) {
        //            $.xDialog({content: '每场限领一次，您已成功领取，快去用掉它吧~'});
        //        } else {
        //            //initAward初始数据是否已加载
        //            if (activityId) {
        //                if(!initAward.loading){
        //                    $.xPost({
        //                        url: env.path + '/activity/anniversary/receivePrize',
        //                        data: {activityId: activityId},
        //                        callback: awardCallback
        //                    });
        //                    initAward.loading=true;
        //                }else{
        //                    $.xDialog({content:'加载中，请稍后再试。'});
        //                    return false;
        //                }
        //
        //            } else {
        //                $.xDialog({content: '加载中，请稍后再试。'});
        //            }
        //        }
        //    } else {
        //        $.xDialog({
        //            content: '请先登录', callback: function () {
        //                location.href = '/security/login';
        //            }
        //        });
        //    }
        //}
        //function awardCallback(data) {
        //    if (data.success) {
        //        $.xDialog({content: '恭喜您获得' + data.result.rewardName, type: 'success'});
        //        initAward.received = true;
        //    } else {
        //        $.xDialog({content: '太热情，挤爆啦，喝口水再回来吧~'});
        //    }
        //    initAward.loading = false;
        //}
    });
})();

/*
 * Activity:巅峰1小时
 */
(function () {
    $.xPost({
        url: env.path + '/activity/anniversary/oneHour',
        callback: function (data) {
            if (data.success) {
                showHourCountdown(data.result.startDay, data.result.endDay);
                renderHourList(data);
            }
        }
    });
    function showHourCountdown(start, end) {
        var date = env.date,
            event = {};
        //活動時間
        if (date >= start && date <= end) {
            event = {
                interval: end - date,
                tips: '活动结束',
                status: 1
            }
        }
        //活動未開始
        else if (date < start) {
            event = {
                interval: start - date,
                tips: '活动开始',
                status: 0
            }
        }
        //活動已結束
        else {
            event = {
                interval: -1,
                tips: '活动已结束',
                status: -1
            }
        }

        //倒计时
        var $countTips = $('#j-hour-tips'),
            $countdown = $('#j-hour-countdown'),
            hourStartTime = new Date('2015/11/19 14:11:19'),
            hourEndTime = new Date('2015/11/19 15:11:19');
        if (event.status >= 0) {
            $countTips.text(event.tips);

            var splitTime=event.interval;
            //开始之前倒计时
            if (tNow >= t19 && tNow < hourStartTime) {
                $countdown.parent().show();
                event.status = 0;
            }
            //开始之后结束倒计时
            else if (tNow > t19 && tNow >= hourStartTime && tNow < hourEndTime) {
                $countTips.text('活动结束');
                $countdown.parent().show();
                event.status = 1;
            }
            var hoursClock = $countdown.FlipClock(Math.floor(splitTime / 1000),{
                autoStart: true,
                countdown: true,
                callbacks: {
                    stop: function () {
                        setTimeout(function () {
                            if (event.status === 0) {
                                $countTips.text('活动结束');
                                hoursClock.setTime(3600);
                                hoursClock.start();
                                event.status = 1;
                            } else if (event.status === 1) {
                                $countdown.parent().text('活动已结束');
                            }
                        }, 1000);
                    }
                }
            });

        } else {
            $countTips.parent().text('活动已结束');
        }
    }

    function renderHourList(data) {
        var html = template('j-anniversary-cat2listtpl', data);
        $('#j-anniversary-cat2list').html(html);
    }
})();

/*
 * Activity:幸运二十五宫格
 */
(function () {

    var $luckyDialog = $('#j-lucky-dialog'),
        $repValue = $('#j-repValue'),
        $luckyGrid = $('#j-luckyGrid'),
        Lucky = {//Lucky=>luckyDialog
            $name: $luckyDialog.find('em'),
            $ipt: $luckyDialog.find('input'),
            $btn: $luckyDialog.find('a'),
            $close: $luckyDialog.find('i'),
            point: $repValue.text() - 0 || 0
        };

    //点击抽奖
    $luckyGrid.on('click', 'span', function () {
        var _this = $(this);

        //未输入|未登录|不在活动期间 时叉掉窗口可以再弹出提示框
        if (env.date < t17 || !Lucky.$ipt.val() || !env.logined) {
            $luckyDialog.show();
            return false;
        }else if(env.date>=t24){
            $.xDialog({content:'活动已结束！'});
            return false;
        }
        if (env.logined && !_this.hasClass('z-actived') && env.date < t24 && env.date >= t17) {//0=>t17

            if (Lucky.cachePoint) {
                $luckyDialog.parent().xLoading(function (target) {
                    getLottery(Lucky.cachePoint, _this, target);
                });
            } else {
                Lucky.$ipt.val('');
                $luckyDialog.show();
                return false;
            }
        }
    });

    //#初始化25宫格
    //活动期间
    if (env.date >= t17 && env.date < t24) {

        Lucky.$btn.removeClass('z-disabled');

        if (!env.logined) {
            Lucky.$btn.prop('href', '/security/login');
        }
        Lucky.$name.text('幸运25宫格，下注试试手气');
        Lucky.$ipt.prop('placeholder', '请输入您要下注的人气值点数');
    }
    //活动已结束
    else if (env.date >= t24) {
        Lucky.$name.text('翻倍不在，真爱还在~');
        Lucky.$ipt.prop({'placeholder': '周年庆活动已结束~ ', 'readonly': 'readonly'});
        Lucky.$btn.text('活动已结束');
    }
    //活动未开始
    else if (env.date < t17) {
        Lucky.$ipt.prop({
            placeholder: '11月17日，翻倍赢人气值',
            readonly: 'readonly'
        }).next('strong').remove();
        Lucky.$name.text('幸运25宫格，等你来翻倍');
        Lucky.$btn.addClass('z-disabled')
            .text('求约').attr('href', 'javascript:void(0)');
    }

    //人气值输入框
    Lucky.$ipt.on('keyup', function () {
        var val = $(this).val();
        $(this).val(val.replace(/[^\d]/g, ""));
    });
    //确定按钮
    Lucky.$btn.on('click', function () {
        var val = Lucky.$ipt.val() - 0 || 0;
        if (val) {
            Lucky.$ipt.removeClass('z-incorrect');
            if (val <= Lucky.point) {
                $luckyDialog.hide();
                Lucky.cachePoint = val;
                Lucky.init = true;
                Lucky.$btn.text('再抽一次');
                setTimeout(function () {
                    lotteryAnimate(0);
                }, 300);
            } else {
                $.xDialog({content: '您当前的人气值余额不足，<br/>快去赚取人气值！', type: 'warn'})
            }
        } else {
            Lucky.$ipt.addClass('z-incorrect').focus();
        }
    });
    //关闭按钮
    Lucky.$close.on('click', function () {
        Lucky.$ipt.val('');
        if(tNow<t24){
            Lucky.$name.text('幸运25宫格，下注试试手气');
        }
        $luckyDialog.hide();

        setTimeout(function () {
            lotteryAnimate(0);
        },2500);
    });

    //抽奖请求
    var lotteryIsLoading=false;
    function getLottery(point, $grid, $target) {
        if(!lotteryIsLoading){
            lotteryIsLoading=true;
            $.xPost({
                url: env.path + '/activity/anniversary/twentyFive',
                data: {chip: point},
                callback: function (data) {
                    if (data.success) {
                        //奖励显示
                        var reward = {ratio: 0};
                        switch (data.result.rewardCode) {
                            case 'noReward':
                                reward.ratio = 0;
                                break;
                            case 'PopularityFor10times':
                                reward.ratio = 10;
                                break;
                            case 'PopularityFor5times':
                                reward.ratio = 5;
                                break;
                            case 'PopularityFor2times':
                                reward.ratio = 2;
                                break;
                            default :
                                break;
                        }
                        if (reward.ratio) {
                            reward.name = '好手气，获得' + reward.ratio * point + '点人气值，再接再厉啊';
                        } else {
                            reward.name = '好遗憾，听说舔屏下注，中奖概率高3倍哦';
                        }
                        Lucky.$name.text(reward.name);

                        //清空输入值
                        Lucky.$ipt.val('');

                        //修正当前用户的人气值
                        $repValue.text(data.result.popularityVaule);

                        //间隔时间再弹出结果
                        setTimeout(function () {
                            //翻转格子
                            //$grid.addClass('z-actived' + (reward.ratio || ''));
                            gridAnimate(reward.ratio, $grid.index());

                            //loading done && show dialog
                            $target.loadingDone();

                            //默认取上次的值
                            Lucky.$ipt.val(Lucky.cachePoint || '');

                            $luckyDialog.show();
                        },1000);
                    } else {
                        $target.loadingDone();
                        $.xDialog({content:data.resultCodeEum?data.resultCodeEum[0].msg:'抽奖失败'});
                    }
                    lotteryIsLoading=false;
                }
            });
        }

        function gridAnimate(reward, gridIndex) {
            var ratio = [2, 5, 10],
                gridArray = [], randomGridArray = [], $gridList = $luckyGrid.find('span');

            for (var n = 0; n < 25; n++) {
                gridArray.push(n);
            }
            gridArray.splice(gridIndex, 1);

            if (reward) {
                var ratioIndex = $.inArray(reward, ratio);
                if (ratioIndex !== -1) {
                    ratio.splice(ratioIndex, 1);
                }
                randomGridArray = getArrayItems(gridArray, 2);
                $gridList.eq(gridIndex).addClass('z-actived' + reward);
                $gridList.eq(randomGridArray[0]).addClass('z-actived' + ratio[0]);
                $gridList.eq(randomGridArray[1]).addClass('z-actived' + ratio[1]);
            } else {
                randomGridArray = getArrayItems(gridArray, 3);
                $gridList.eq(randomGridArray[0]).addClass('z-actived' + ratio[0]);
                $gridList.eq(randomGridArray[1]).addClass('z-actived' + ratio[1]);
                $gridList.eq(randomGridArray[2]).addClass('z-actived' + ratio[2]);
            }
            lotteryAnimate(1);
        }
    }

    //抽奖翻转
    function lotteryAnimate(direction) {
        "use strict";
        var count = direction ? 1 : 5;
        var timer = setInterval(function () {
            var $countList = $luckyGrid.find('span:nth-child(5n+' + count + ')');
            if (direction) {
                if (count <= 5) {
                    $countList.addClass('z-actived');
                    count++;
                } else {
                    clearInterval(timer);
                }
            } else {
                if (count >= 1) {
                    $countList.removeClass();
                    count--;
                } else {
                    clearInterval(timer);
                }
            }
        }, 100);
    }

    //25宫格赢家列表请求
    $.xPost({
        url: '/activity/anniversary/twentyFive/list',
        callback: function (data) {
            if (data.success) {
                renderLuckyList(data);

            }
        }
    });

    //25宫格获奖列表
    function renderLuckyList(data) {
        var html = template('j-anniversary-gametpl', data);
        $('#j-anniversary-game').html(html);
        $('.j-anniversary-ranking').scrollList({
            size: 6,
            length: 1,
            height: 55,
            time: 3000
        });
    }
})();

//从一个给定的数组arr中,随机返回num个不重复项
function getArrayItems(arr, num) {
    //新建一个数组,将传入的数组复制过来,用于运算,而不要直接操作传入的数组;
    var temp_array = [];
    for (var index in arr) {
        temp_array.push(arr[index]);
    }
    //取出的数值项,保存在此数组
    var return_array = [];
    for (var i = 0; i < num; i++) {
        //判断如果数组还有可以取出的元素,以防下标越界
        if (temp_array.length > 0) {
            //在数组中产生一个随机索引
            var arrIndex = Math.floor(Math.random() * temp_array.length);
            //将此随机索引的对应的数组元素值复制出来
            return_array[i] = temp_array[arrIndex];
            //然后删掉此索引的数组元素,这时候temp_array变为新的数组
            temp_array.splice(arrIndex, 1);
        } else {
            //数组中数据项取完后,退出循环,比如数组本来只有10项,但要求取出20项.
            break;
        }
    }
    return return_array;
}

//template日期格式化
template.helper('dateFormat', function (date, format) {

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
});