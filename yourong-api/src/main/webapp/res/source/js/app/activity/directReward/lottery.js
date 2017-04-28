(function () {
    $vm = new Vue({
        el: '#j-lottery',
        data: {
            lotteryTimes: 3,
            current: {},
            drawStep: 0,
            showDialog: 0, //1=>抽奖,2=>一键抽奖,3=>抽中奖励,4=>未抽中奖励
            rewardList: {},
            projectLottery: {},
            init: {},
            unSuccessText: '未中奖，再接再厉',
            pl: {},
            os: env.os,
            rewardCode: '',
            rewardAmount: ''
        },
        created: function () {
            var initData = $('#j-data').attr('data') || '{}'
            var data = JSON.parse(initData),
                self = this
            console.log('allData:', data)
            console.log('resule:', data.result)
            this.init = data.result
            var lotteryJson = data.result.listProjectLottery
            self.projectLottery = lotteryJson.length ? lotteryJson : []
            console.log(self.projectLottery)
            self.pl = self.projectLottery
            if (self.pl.length) {
                self.current = self.pl[0]
            }
            console.log(self.os)
            window.hookCallback = function (data, method) {

                if (method == 12) {
                    // self.rewardList = data.result.data
                    self.callback(data, self)


                } else if (method == 13) {
                    self.init = data.result
                    lotteryJson = data.result.listProjectLottery
                    self.projectLottery = lotteryJson.length ? lotteryJson : []
                    self.pl = self.projectLottery
                    if (self.pl.length) {
                        self.current = self.pl[0]
                    }
                }
            }
        },
        //滑动可以抽奖的项目--代码很复杂，有空来整理
        ready: function () {
            var stop = 0,
                self = this;
            if (this.projectLottery.length > 1) {
                var animating = false,
                    cardsCounter = 0,
                    numOfCards = this.projectLottery.length,
                    decisionVal = 10,
                    pullDeltaX = 0,
                    deg = 0,
                    $card, $cardReject, $cardLike,
                    $jAwardWrap = $('.j-award-wrap');

                function pullChange() {
                    document.addEventListener("touchmove", function (e) {
                        if (stop == 0) {
                            e.preventDefault();
                            e.stopPropagation();
                        }
                    }, false);
                    animating = true;
                    deg = pullDeltaX / 10;
                    $card.css('transform', 'translateX(' + pullDeltaX + 'px) rotate(' + deg + 'deg)');
                    var opacity = pullDeltaX / 100;
                    var rejectOpacity = opacity >= 0 ? 0 : Math.abs(opacity);
                    var likeOpacity = opacity <= 0 ? 0 : opacity;
                    $cardReject.css('opacity', rejectOpacity);
                    $cardLike.css('opacity', likeOpacity);
                }

                function release() {
                    if (pullDeltaX >= decisionVal) {
                        $card.addClass('to-right');
                    } else if (pullDeltaX <= -decisionVal) {
                        $card.addClass('to-left');
                    }
                    if (Math.abs(pullDeltaX) >= decisionVal) {
                        $card.addClass('inactive');
                        setTimeout(function () {
                            $card.addClass('below').removeClass('inactive to-left to-right');
                            cardsCounter++;
                            if (cardsCounter === numOfCards) {
                                cardsCounter = 0;
                                $('.m-award-head').removeClass('below');
                            }
                        }, 30);
                    }
                    if (Math.abs(pullDeltaX) < decisionVal) {
                        $card.addClass('reset');
                    }
                    setTimeout(function () {
                        $card.attr('style', '').removeClass('reset').find('.u-award-choice').attr('style', '');
                        pullDeltaX = 0;
                        animating = false;
                    }, 30);
                }

                $jAwardWrap.on('mousedown touchstart', '.m-award-head:not(.inactive)', function (e) {
                    if (animating)
                        return;
                    $card = $(this);
                    $cardReject = $('.u-award-choice.m--reject', $card);
                    $cardLike = $('.u-award-choice.m--like', $card);
                    self.current = self.projectLottery[$(this).attr('index')]
                    var startX = e.pageX || e.originalEvent.touches[0].pageX;
                    $jAwardWrap.on('mousemove touchmove', function (e) {
                        stop = 0
                        var x = e.pageX || e.originalEvent.touches[0].pageX;
                        pullDeltaX = x - startX;
                        if (!pullDeltaX)
                            return;
                        pullChange();

                    });
                    $jAwardWrap.on('mouseup touchend', function () {
                        stop = 1
                        $jAwardWrap.off('mousemove touchmove mouseup touchend');
                        if (!pullDeltaX)
                            return;
                        release();
                    });
                });
            }

        },
        watch: {
            showDialog: function (n) {
                if (n === 1 || n === 2) {
                    var $processBar = $('#j-process-bar')
                    setTimeout(function () {
                        $processBar.addClass('z-finished')
                    }, 100)
                    setTimeout(function () {
                        $processBar.removeClass('z-finished')
                    }, 3200)
                    if (n === 2) {
                        var self = this,
                            size = self.current.nummber
                        var timer = setInterval(function () {
                            self.drawStep++

                            if (self.drawStep === size) {
                                clearInterval(timer)
                            }
                        }, 3E3 / size)
                    }
                }
            }
        },
        methods: {
            getRewardName: function (val) {
                var zhNum = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
                return zhNum[val - 1] + '等奖'
            },
            getAwardList: function (list, type) {
                //type=1--页面显示中奖情况；type=2--弹框显示的中奖情况
                var arr = [],
                    self = this,
                    awardsList = list.slice(0, 10)

                if (type == 1) {
                    awardsList.forEach(function (item) {
                        var size = item.number > 1 ? 'x' + item.number : ''
                        arr.push(self.getRewardName(item.level) + size)

                    })
                    var awards = arr.join("、")
                    return awards
                } else if (type == 2) {
                    awardsList.forEach(function (item) {
                        var size = item.num > 1 ? 'x' + item.num : ''
                        arr.push(self.getRewardName(item.level) + size)
                    })
                    var awards = arr.join("、")
                    return awards
                }


            },
            luckyDraw: function (type, e, projectId) {
                var current = this.current
                if (!current || !current.nummber) {
                    return false
                }

                this.showDialog = type
                console.log('showDialog2:', this.showDialog)
                hook.getEvent(12 + '&isNeedRealName=0&args_projectId_1_long_' + projectId +
                    '&args_type_1_integer_' + type, $(e.currentTarget), 1)
            },
            reload: function (e) {
                //不知道返回的数据是怎么样的。。。。。
                // hook.getEvent(13 + '&isNeedRealName=0&' + '', $(e.currentTarget), 1)
                var self = this
                if (self.os == 1) {
                    hook.getEvent(13 + '&isNeedRealName=0&' + '', $(e.currentTarget), 1)
                }
                this.showDialog = 0

            },
            callback: function (data, target) {
                var self = target
                self.drawStep = 0
                console.log('data:', data)
                if (data.success) {
                    self.rewardCode = data.result.popularity
                    self.rewardAmount = data.result.rewardAmount
                    console.log('length:', data.result.prizList.length)
                    if (data.result.prizList.length) {
                        setTimeout(function () {
                            self.showDialog = 3
                        }, 3200)

                    } else {
                        setTimeout(function () {
                            self.showDialog = 4
                        }, 3200)
                    }
                    self.rewardList = data.result.prizList

                } else {
                    self.unSuccessText = data.resultCodes[0].msg
                    setTimeout(function () {
                        self.showDialog = 4
                    }, 3200)
                }
                console.log('showDialog:', self.showDialog)
            },
            toAndroidProjectList: function () {
                Android.ToActivity('projectList', null)
            }
        },
        filters: {
            rewardName: {
                read: function (val) {
                    var zhNum = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
                    return zhNum[val - 1] + '等奖'
                }
            }
        }
    })
})()