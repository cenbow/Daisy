(function () {

    var lotteryArrow = $('#j-lottery-arrow'),
        lotteryRotate = $('#j-lottery-rotate');

    //点击抽奖
    var ROTATE_OFFSET = 22.5,
        giftAngleList = {
            'rewardFor1': 0 - ROTATE_OFFSET,
            'rewardFor6': -45 - ROTATE_OFFSET,
            'rewardFor2': -90 - ROTATE_OFFSET,
            'rewardFor4': 180 - ROTATE_OFFSET,
            'rewardFor5': 135 - ROTATE_OFFSET,
            'rewardFor3': 90 - ROTATE_OFFSET,
            'rewardForNull1': -135 - ROTATE_OFFSET,
            'rewardForNull2': 45 - ROTATE_OFFSET
        };

    var path = environment.globalPath,
        logined = $('#j-directLottery').data('logined');

    Vue.config.devtools = true

    Vue.filter('formatNumToDay', function (val) {
        var day = '';
        switch (val) {
            case 1:
                day = '一';
                break
            case 2:
                day = '二';
                break
            case 3:
                day = '三';
                break
            case 4:
                day = '四';
                break
            case 5:
                day = '五';
                break
            case 6:
                day = '六';
                break
            case 7:
                day = '七';
                break
        }
        return day
    })

    Vue.filter('splitNumber', function (val, isDecimals) {
        var numArr = [];
        if (val.indexOf('.') !== -1) {
            numArr = val.split('.')
        } else {
            numArr.push(val)
        }

        if (isDecimals) {
            return numArr[1]
        } else {
            return numArr[0]
        }
    })

    Vue.filter('compareTime', function (val) {
        var timeText = '';
        var minute = moment().diff(val, 'minute');
        if (minute >= 1440) {
            var days = moment().diff(val, 'days');
            timeText = days + '天'
        } else if (minute >= 60) {
            var hour = moment().diff(val, 'hours');
            timeText = hour + '小时'
        } else {
            if (minute > 0) {
                timeText = minute + '分钟'
            } else {
                timeText = '1分钟'
            }
        }
        return timeText
    })

    window.vm = new Vue({
        el: '#j-directLottery',
        data: {
            urls: {
                init: path + '/post/quickLottery/init',
                directLottery: path + '/activity/lottery'
            },
            logined: logined,

            initData: {},

            lotteryBtnStatus: false,    //抽奖按钮状态

            open: false,         //弹出框是否显示

            lotteryNumber: 0,   //抽奖次数

            lotteryType: 0,      //抽奖类型  1:一次抽奖  2:一键抽奖
            lotteryProject: 0,   //抽奖项目
            isLottery: false,        //是否中奖
            lotteryContent: [],       //中奖列表

            prizeRateText: '',        //奖项等级与比例文本
            rewardNum: 3,              //设置的奖项个数
            popularity: 0              //未中奖补贴的人气值
        },
        created: function () {
            var self = this;

            $.xPost({
                url: self.urls.init,
                callback: function (data) {
                    if (data.success) {
                        init(data.result)
                    } else {
                        console.log('页面初始化错误', data)
                    }
                }
            })

            function init(result) {
                self.initData = {
                    prizePoolList: result.prizePoolList || [],              //具体奖项设置
                    projectForReward: result.projectForReward || [],       //中奖纪录

                    projectFrontList: result.projectFrontList || [],        //快投有奖项目列表
                    listProjectLottery: result.listProjectLottery || [],    //当前抽奖项目

                    ruleAmountList: result.ruleAmountList || [],            //投资区间和次数
                    prizeInPoolList: result.prizeInPoolList || [],          //具体奖项分布

                    listReward: result.listReward || [],                    //未登录时的项目活跃度
                    listTemplate: result.listTemplate || [],                 //现金券使用规则
                    rewardHour:result.rewardHour || 0                        //奖励时间
                }

                if (result.prizeInPoolList && result.prizeInPoolList.length > 0) {
                    var prizeList = result.prizeInPoolList;
                    var len = prizeList.length;
                    var text = '';

                    self.rewardNum = prizeList.length;

                    for (var i = 0; i < len; i++) {
                        if (i == 0) {
                            text += '一等奖奖励金额为当天奖池总额的' + prizeList[i].proportion + '%；';
                        } else {
                            if (i == len - 1) {
                                text += self.toUppercase(prizeList[i].level) + '等奖为' + prizeList[i].proportion + '%。'
                            } else {
                                text += self.toUppercase(prizeList[i].level) + '等奖为' + prizeList[i].proportion + '%；'
                            }
                        }
                    }
                    self.prizeRateText = text;
                } else {
                    //没设置奖项时抽奖转盘显示默认的图片
                    self.rewardNum = 6
                }

                if (result.listProjectLottery && result.listProjectLottery.length > 0) {
                    self.lotteryNumber = result.listProjectLottery[0].nummber;
                    self.lotteryProject = result.listProjectLottery[0].projectId;
                }
            }

        },
        watch: {
            'initData.listProjectLottery': function () {
                var self = this;
                $('.default').dropkick({
                    change: function (value, label) {
                        var lotteryInfo = value.split('&');
                        self.lotteryProject = lotteryInfo[0];
                        self.lotteryNumber = lotteryInfo[1];
                    }
                });
            },
            'initData.listReward': function () {
                var self = this;
                $("#j-directLotteryList").scrollUserList({
                    size: 5,
                    height: -70,
                    length: 1
                });
            },
            'initData.projectFrontList': function () {
                //圆环
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
            }
        },
        methods: {
            goLottery: function (val) {
                var self = this;

                if (!self.logined) {
                    window.location.href = '/security/login?from=/post/directActivator';
                    return false;
                }

                self.lotteryType = val;

                //0次不触发
                if (self.lotteryNumber == 0) {
                    return false;
                }

                //阻止反复点击
                if (self.lotteryBtnStatus) {
                    return false;
                }

                self.lotteryBtnStatus = true;

                //初始转动
                lotteryRotate.rotate({
                    angle: 0,
                    animateTo: 21580,
                    duration: 10000,
                    callback: function () {
                        self.lotteryBtnStatus = false;
                        $.xDialog({content: '网络不给力，请刷新页面。'})
                    }
                });
                //抽奖
                self.drawLottery(function (data) {
                    //init
                    self.lotteryBtnStatus = true;
                    lotteryRotate.stopRotate();
                    if (typeof(data) !== 'object') {
                        $.xDialog({content: '网络不给力，请刷新页面。'});
                        return false;
                    }
                    if (data.success === false) {
                        $.xDialog({
                            content: data.resultCodeEum[0].msg,
                            callback: function () {
                                window.location.reload();
                            }
                        });
                        self.lotteryBtnStatus = false;
                        return false;
                    }

                    //随机位置系数
                    var num = Math.random(),
                        factor = Math.round(num) ? 1 : -1,
                        point = parseInt(num * factor * 6);

                    var result = data.result;

                    //剩余抽奖次数
                    if (val == 1) {
                        self.lotteryNumber = self.lotteryNumber - 1;
                    } else if (val == 2) {
                        self.lotteryNumber = 0;
                    }

                    //中奖系数
                    var rewardCode = '';
                    if (result.lottery && result.rewardInfo) {
                        var reward = result.rewardInfo;
                        var rewardArr = [];
                        var rewardStr = '';

                        // 只有一个奖项时
                        if (self.isSingleReward(reward)) {
                            rewardStr = parseInt(reward)
                        } else {
                            rewardArr = reward.split(',');
                            // 统计个奖项中奖次数
                            var rewardList = [];
                            var prizeArr = self.countLotteryNum(rewardArr);
                            for (var key in prizeArr) {
                                var rewardObj = {}
                                switch (key) {
                                    case '1':
                                        rewardObj.level = 1;
                                        rewardObj.number = prizeArr[key];
                                        break
                                    case '2':
                                        rewardObj.level = 2;
                                        rewardObj.number = prizeArr[key];
                                        break
                                    case '3':
                                        rewardObj.level = 3;
                                        rewardObj.number = prizeArr[key];
                                        break
                                    case '4':
                                        rewardObj.level = 4;
                                        rewardObj.number = prizeArr[key];
                                        break
                                    case '5':
                                        rewardObj.level = 5;
                                        rewardObj.number = prizeArr[key];
                                        break
                                    case '6':
                                        rewardObj.level = 6;
                                        rewardObj.number = prizeArr[key];
                                        break
                                }
                                rewardList.push(rewardObj);
                            }
                            rewardStr = rewardList[0].level
                        }

                        //多个奖项时转盘停在最大的奖项上面
                        switch (rewardStr) {
                            case 1:
                                rewardCode = 'rewardFor1';
                                break
                            case 2:
                                rewardCode = 'rewardFor2';
                                break
                            case 3:
                                rewardCode = 'rewardFor3';
                                break
                            case 4:
                                rewardCode = 'rewardFor4';
                                break
                            case 5:
                                rewardCode = 'rewardFor5';
                                break
                            case 6:
                                rewardCode = 'rewardFor6';
                                break
                            default:
                                rewardCode = 'rewardForNull1'
                        }
                    } else {
                        //生成2个数的随机数
                        var random = Math.floor(Math.random() * 2 + 1);
                        rewardCode = 'rewardForNull' + random;
                    }

                    //转动抽奖
                    lotteryRotate.rotate({
                        angle: 0,
                        animateTo: 1440 + giftAngleList[rewardCode] + point,
                        duration: 5000,
                        callback: function () {
                            if (result.lottery) {
                                var rewardMsg = result.rewardInfo;
                                var rewardMsgArr = [];
                                var rewardMsgList = [];
                                if (rewardMsg) {
                                    // 只有一个奖项时
                                    if (self.isSingleReward(rewardMsg)) {
                                        rewardMsgList.push(parseInt(rewardMsg));
                                    } else {
                                        rewardMsgArr = rewardMsg.split(',');
                                        // 统计个奖项中奖次数
                                        var prizeArr = self.countLotteryNum(rewardMsgArr);

                                        for (var key in prizeArr) {
                                            var rewardObj = {}
                                            switch (key) {
                                                case '1':
                                                    rewardObj.level = '一等奖';
                                                    rewardObj.number = prizeArr[key];
                                                    break
                                                case '2':
                                                    rewardObj.level = '二等奖';
                                                    rewardObj.number = prizeArr[key];
                                                    break
                                                case '3':
                                                    rewardObj.level = '三等奖';
                                                    rewardObj.number = prizeArr[key];
                                                    break
                                                case '4':
                                                    rewardObj.level = '四等奖';
                                                    rewardObj.number = prizeArr[key];
                                                    break
                                                case '5':
                                                    rewardObj.level = '五等奖';
                                                    rewardObj.number = prizeArr[key];
                                                    break
                                                case '6':
                                                    rewardObj.level = '六等奖';
                                                    rewardObj.number = prizeArr[key];
                                                    break
                                            }
                                            rewardMsgList.push(rewardObj);
                                        }
                                    }
                                }

                                self.$set('lotteryContent', rewardMsgList);
                                self.isLottery = true;
                                self.openLotteryDialog()
                            } else {
                                self.isLottery = false;
                                self.popularity = result.popularity;
                                self.openLotteryDialog()
                            }

                            self.lotteryBtnStatus = false;

                        }
                    });
                });
            },
            drawLottery: function (callback) {
                $.xPost({
                    url: this.urls.directLottery,
                    data: {
                        projectId: this.lotteryProject,
                        type: this.lotteryType
                    },
                    callback: callback
                })
            },
            getPlaceholderCount: function (str) {
                var thisCount = 0;
                str.replace(/\{[.]+\}|\{\}/g, function (m, i) {
                    //m为找到的{xx}元素、i为索引
                    thisCount++;
                });
                return thisCount;
            },
            //转换成大写的数字
            toUppercase: function (val) {
                var num = '';
                switch (val) {
                    case 1:
                        num = '一';
                        break
                    case 2:
                        num = '二';
                        break
                    case 3:
                        num = '三';
                        break
                    case 4:
                        num = '四';
                        break
                    case 5:
                        num = '五';
                        break
                    case 6:
                        num = '六';
                        break
                }
                return num
            },
            //去掉最后一个,
            removeLastStr: function (str) {
                var index = str.lastIndexOf(',');
                var result = str.substring(0, index);
                return result
            },
            //判断获得的是一个奖还是多个奖 true:一个奖
            isSingleReward: function (result) {
                if (result.indexOf(',') === -1) {
                    return true
                } else {
                    return false
                }
            },
            // 统计所有奖项和各奖项次数
            countLotteryNum: function (arr) {
                var res = [];
                arr.sort();

                for (var i = 0; i < arr.length; i++) {
                    var key = "" + arr[i];
                    if (res.hasOwnProperty(key)) {
                        res[key]++;
                    } else {
                        res[key] = 1;
                    }
                }

                return res
            },
            // 打开抽奖弹出框
            openLotteryDialog: function () {
                this.open = true;
                $.shade('show');
            },
            // 关闭抽奖弹出框
            closeLotteryDialog: function () {
                this.open = false;
                $.shade('hide');
                window.location.reload();
            }
        }
    })

    // 显示、隐藏遮罩层
    $.extend({
        shade: function e(t) {
            var e = $(".u-shade");
            if ("undefined" == typeof t || "show" === t) if (e.length) e.show();
            else {
                var n = $("body"),
                    i = n.height(),
                    s = "<div class='u-shade' style='height:" + i + "px'></div>";
                n.append(s)
            } else "hide" === t && e.remove()
        }
    })

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
                }, 3000);
            }
        })
    };

})()
