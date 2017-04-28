(function () {


    //上榜候选人
   /* $("#j-heavyInvestment-list1").scrollList({
        size: 3,
        length: 3,
        height: 55,
        time: 3000
    });

    //满80万
    $("#j-heavyInvestment-list2").scrollList({
        size: 3,
        length: 1,
        height: 55,
        time: 3000
    });
    //满40万
    $(".j-heavyInvestment-list3").scrollList({
        size: 3,
        length: 1,
        height: 55,
        time: 3000
    });*/
    //土豪排行
    $.xPost({
        url: '/activity/monthlyRank/list',
        callback: function (data) {
            if (data.length) {
                var dataList = {'list1': data[0], 'list2': data[1], 'list3': data[2], 'list4': data[3]};

                //处理候选人为空的情况
                var rankingList4 = dataList.list4.rankingList || [];
                if (rankingList4.length < 9) {
                    for (var i = rankingList4.length; i <= 8; i++) {
                        rankingList4.push({'username': null});
                    }
                    dataList.list4.rankingList = rankingList4;
                }

                var html = template('j-rankList-tpl', dataList);
                $('#j-rankList').html(html);

                $("#j-heavyInvestment-list0").scrollList({
                    size: 3,
                    length: 1,
                    height: 55,
                    time: 3000
                });
                $("#j-heavyInvestment-list4").scrollList({
                    size: 3,
                    length: 1,
                    height: 55,
                    time: 3000
                });
                $("#j-heavyInvestment-list2").scrollList({
                    size: 3,
                    length: 1,
                    height: 55,
                    time: 3000
                });
            }
        }
    });

    //奇偶
    $.xPost({
        url: '/activity/monthlyRank/weeklyList',
        callback: function (data) {
            if (typeof(data) !== 'undefined') {

                //奇偶列表
                data.weeklyList = getWeeklyListArray(data);

                //倒计时
                showWeeklyCountdown(data.startDay, data.endDay);

                //每轮TOP1
                var topperList = data.topperList || [];
                if (topperList.length < 4) {
                    for (var k = topperList.length; k <= 3; k++) {
                        topperList[k] = {'lastUsername': null};
                    }
                } else {//哪一轮没数据时补空
                    for (var p = 0; p < 4; p++) {
                        if (!topperList[p]) {
                            topperList[p] = {'lastUsername': null};
                        }
                    }
                }
                data.topperList = topperList;

                var html = template('j-rankList-tpl1', data);
                $('#j-rankList1').html(html);
            }
        }
    });

    //获取奇偶列表数据
    function getWeeklyListArray(data) {
        var thisList = data.thisList || [],
            lastList = data.lastList || [],
            weeklyList = [],
            pointList = [208, 158, 118, 68, 88, 38, 58, 28, 48, 18];
        if (thisList.length || lastList.length) {
            var nullText = null;
            /*if(data.startDay){
             nullText='0';
             }*/
            if (!data.startDay) {//活动结束
                nullText = '1';
            } else if (environment.serverDate < data.startDay) {//活动未开始
                nullText = '2';
            }

            if (thisList.length >= lastList.length) {
                for (var i = 0; i < 10; i++) {

                    if (i < lastList.length) {

                        weeklyList[i] = {
                            point: pointList[i],
                            curUsername: thisList[i].lastUsername,
                            curTotalInvest: thisList[i].lastTotalInvest,
                            curAvatar: thisList[i].avatar,
                            prevUsername: lastList[i].lastUsername,
                            prevAvatar: lastList[i].avatar,
                            prevTotalInvest: lastList[i].lastTotalInvest
                        };

                    } else if (i >= lastList.length && i < thisList.length) {
                        weeklyList[i] = {
                            point: pointList[i],
                            curUsername: thisList[i].lastUsername,
                            curTotalInvest: thisList[i].lastTotalInvest,
                            curAvatar: thisList[i].avatar,
                            prevUsername: nullText,
                            prevAvatar: null,
                            prevTotalInvest: null
                        };
                    } else {
                        weeklyList[i] = {
                            point: pointList[i],
                            curUsername: nullText,
                            curTotalInvest: null,
                            curAvatar: null,
                            prevUsername: nullText,
                            prevAvatar: null,
                            prevTotalInvest: null
                        };
                    }
                }
            } else {
                for (var j = 0; j < lastList.length; j++) {

                    if (j < thisList.length) {
                        weeklyList[j] = {
                            point: pointList[j],
                            prevUsername: lastList[j].lastUsername,
                            prevTotalInvest: lastList[j].lastTotalInvest,
                            prevAvatar: lastList[j].avatar,
                            curUsername: thisList[j].lastUsername,
                            curAvatar: thisList[j].avatar,
                            curTotalInvest: thisList[j].lastTotalInvest
                        }
                    } else if (j >= thisList.length && j < lastList.length) {
                        weeklyList[j] = {
                            point: pointList[j],
                            prevUsername: lastList[j].lastUsername,
                            prevTotalInvest: lastList[j].lastTotalInvest,
                            prevAvatar: lastList[j].avatar,
                            curUsername: nullText,
                            curAvatar: null,
                            curTotalInvest: null
                        }
                    } else {
                        weeklyList[i] = {
                            point: pointList[j],
                            curUsername: nullText,
                            curTotalInvest: null,
                            curAvatar: null,
                            prevUsername: nullText,
                            prevAvatar: null,
                            prevTotalInvest: null
                        };
                    }
                }
            }
        }

        if (weeklyList.length < 10) {
            var lastCurTime = environment.serverDate,
                lastEndTime = new Date('2015/08/28 17:00').getTime(), nullText2 = null;
            if (lastCurTime > lastEndTime) {
                nullText2 = '1';
            }
            for (var n = weeklyList.length; n <= 9; n++) {
                weeklyList.push({'curUsername': nullText2, 'prevUsername': null, 'point': pointList[n]});
            }
        }
        return weeklyList;
    }

    //本轮下轮倒计时
    function showWeeklyCountdown(startTime, endTime) {
        var turnText = '',
            trunTime = '',
            trunCount = '',
            $countdown = $('#j-weekly-countdown'),
            countSeconds = 0,
            $countTime = $countdown.find('em'),
            $countText = $countdown.find('strong');

        if (startTime || endTime) {
            if (endTime) {//已经开始

                var curTime = new Date(startTime),
                    curDate = curTime.getDate(),
                    curDay = curTime.getDay(),
                    countStarttime = endTime - environment.serverDate;
                turnText = '本轮';
                trunCount = '还剩 ';
                if ($countdown.data('isCountdown')) {
                    countStarttime = 3600 * 24 * 1000 - 1000;
                }
                countSeconds = Math.ceil(countStarttime / 1000);
                console.log(countSeconds);
                if (curDay === 4 || curDay === 1) {
                    trunTime = '8月' + curDate + '日17:00—8月' + (curDate + 1) + '日17:00，';
                } else {
                    trunTime = '8月' + (curDate - 1) + '日17:00—8月' + curDate + '日17:00，';
                }
            } else {//还未开始

                var startDate = new Date(startTime).getDate(),
                    nextStartDate = startDate, nextStartTime = startTime,
                    countStarttime2 = startTime - environment.serverDate;
                trunTime = '8月' + nextStartDate + '日17:00—8月' + (nextStartDate + 1) + '日17:00，';
                turnText = '下轮';
                trunCount = "距离下轮开始还有 ";
                if ($countdown.data('isCountdown')) {
                    countStarttime2 = 3600 * 24 * 1000 * 6 - 1000;
                }
                countSeconds = Math.ceil(countStarttime2 / 1000);
            }

            if (startTime) {
                var startDate2 = new Date(startTime).getDate();
                if (startDate2 <= 6 && environment.serverDate < startTime) {
                    $countText.text('奇偶排行8月6日准时开始，敬请期待！');
                    return false;
                }
            }
            $countTime.data('time', countSeconds);
            $countText.text(turnText + '奇偶排行时间为' + trunTime + trunCount);
        } else {
            var lastActivityDate = new Date('2015/08/28 17:00');
            if (environment.serverDate > lastActivityDate) {
                $countText.text('本轮奇偶排行已经结束，快去冲击月度排行拿冠军吧！');
            } else {
                $countText.text('奇偶排行活动暂未开始');
            }
            return false;
        }

        $countTime.hoursCountdown(function () {
            var lastDate = new Date(startTime).getDate(),
                newEndTime = null;
            if (lastDate >= 27) {
                $countdown.text('奇偶排行活动已结束');
            } else {
                if (startTime < environment.serverDate) {
                    lastDate = lastDate + 7;
                } else {
                    newEndTime = new Date('2015/8/' + (lastDate + 1) + ' 17:00').getTime();
                }
                var newStartTime = new Date('2015/8/' + lastDate + ' 17:00').getTime();

                $countdown.data('isCountdown', 1);
                showWeeklyCountdown(newStartTime, newEndTime);

            }
        });
    }

    //时分秒倒计时
    $.fn.hoursCountdown = function (callback) {
        $(this).each(function () {
            var _this = $(this),
                time = $(this).data('time');
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
        //获取格式化的倒计时
        function getCountTime(seconds) {
            if (seconds < 0) {
                return 0;
            }
            var timeArray = [Math.floor(seconds / 3600) || '00', Math.floor((seconds / 3600) % 1 * 60) || '00', seconds % 60];
            for (var i = 0; i < timeArray.length; i++) {
                if (timeArray[i] < 10 && timeArray[i] !== '00') {
                    timeArray[i] = '0' + timeArray[i];
                }
            }
            //增加天数
            if (timeArray[0] / 24 >= 1) {
                var leftHours = timeArray[0] % 24;
                timeArray[0] = parseInt(timeArray[0] / 24) + '天' + (leftHours < 10 ? '0' + leftHours : leftHours);
            }

            var timeString = timeArray.join(":");
            if (timeString === '00:00:00') {
                return 0;
            }
            return timeString;
        }
    };
    //查看我的排名
    var $ranking = $("#j-ranking"),
        isLogined = $ranking.data("logined");
    $ranking.on('click', function () {
        if (isLogined) {
            var rankingData = $ranking.data('ranking');
            if (typeof(rankingData) !== 'undefined') {
                showRankDialog(rankingData);
            } else {
                $.xPost({
                    url: '/activity/monthlyRank/mine',
                    callback: function (data) {
                        showRankDialog(data);
                        $ranking.data('ranking', data);
                    }
                });
            }

            function showRankDialog(data) {
                var dialogData = {};
                if (data.result) {
                    if (!data.rank) {
                        data.rank = {formatSums: 0, no: 0};
                    }
                    dialogData = {
                        type: 'success', content: '您本月当前累计投资额为' + data.rank.formatSums +
                        '元<br/>在“榜上有名”中排名第' + data.rank.no
                    };
                } else {
                    dialogData = {type: 'info', content: '亲，活动还没开始哟~'};
                }
                $.xDialog(dialogData);

            }
        }
        else {
            window.location.href = '/security/login/';
        }
    });
    //中文数字映射
    template.helper('HansNumber', function (number, format) {
        var numArray = [
            {'no': '零', 'dateArea': ''},
            {'no': '一', 'dateArea': '8月6日17:00—8月7日17:00'},
            {'no': '二', 'dateArea': '8月13日17:00—8月14日17:00'},
            {'no': '三', 'dateArea': '8月20日17:00—8月21日17:00'},
            {'no': '四', 'dateArea': '8月27日17:00—8月28日17:00'}];
        if (format === 'no') {
            return numArray[number].no;
        } else if (format === 'dateArea') {
            return numArray[number].dateArea;
        }
    });
})();