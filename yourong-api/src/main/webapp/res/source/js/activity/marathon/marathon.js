define(['base'], function (require, exports, module) {
    'use strict'
    var base = require('base')

    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function (e) {
        e.preventDefault()
    })

    var logined = $('#j-main').data('logined'),
        domain = environment.globalPath,
        invoke= domain + '/activity/dynamicInvoke',
        urls = {
            list: invoke,
            rewards: invoke,
            init: invoke
        }

    Vue.filter('milestone', function (value, scale) {
        //里程碑
        var reVal = scale * 1 >= 0 ? value - value % 100 + scale * 1 : value,
            centum = value > 0 && !(value % 100)

        //具体样式位置 1-10
        if (scale === '-1') {
            reVal = Math.floor((value % 100) / 10)

            if (centum) {
                reVal = 10
            }
        } else {
            if (centum) {
                if (scale === '-2') {
                    reVal = value
                } else {
                    reVal = reVal - 100
                }
            }
        }

        return reVal
    })

    var Marathon = new Vue({
        el: '#j-marathon',
        data: {
            initData: {},
            totalPop: 0,
            totalCoupon: 0,
            totalKm: 0,
            loginUrl: domain + '/mstation/login?from=' + location.href,
            friendList: [],
            friendListLoaded: false,
            friendListPageNum: 0,
            showMissionModal: false,
            taskProcess:{},
            initTaskProcess: {
                firstInvest: false,
                firstBindApp: false,
                firstBindWeChat: false,
                firstImproveInfo: false,
                firstBindEmail: false
            },
            rewardsList: [],
            avatar: {
                default: domain + '/res/img/member/avatar.png',
                prefix: 'https://oss-cn-hangzhou.aliyuncs.com'
            },
            tips: {
                nomore: '暂无更多记录',
                package: '恭喜你获得幸运背包1个',
                box: '恭喜您获得终极宝箱1个',
                noReward: '没有奖励可领取哦'
            }
        },
        props: ['os', 'logined'],
        created: function () {
            var self = this
            base.getAPI({
                url:urls.init,
                data:{invokeMethod: 'inviteFriendInit'},
                callback: function (data) {
                    if(data.success){
                        self.initData = data.result
                        self.totalKm = data.result.totalKm
                    }else{
                        base.xTips({content:'活动页面初始化失败'})
                    }
                }
            })
        },
        computed: {
            boxRewards: function (type) {
                var index = parseInt((this.totalKm - 1) / 100)
                if(index > 6){
                    index = 6
                }

                var rewards = [
                    [
                        {value: 100, size: 1},
                        {value: 50, size: 1},
                        {value: 30, size: 1}
                    ],
                    [
                        {value: 100, size: 1},
                        {value: 50, size: 1},
                        {value: 30, size: 2}
                    ],
                    [
                        {value: 100, size: 1},
                        {value: 50, size: 2},
                        {value: 30, size: 2}
                    ],
                    [
                        {value: 100, size: 2},
                        {value: 50, size: 1},
                        {value: 30, size: 1}
                    ],
                    [
                        {value: 100, size: 2},
                        {value: 50, size: 1},
                        {value: 30, size: 2}
                    ],
                    [
                        {value: 100, size: 2},
                        {value: 50, size: 1},
                        {value: 30, size: 3}
                    ],
                    [
                        {value: 100, size: 2},
                        {value: 50, size: 2},
                        {value: 30, size: 2}
                    ]
                ]

                return rewards[index]
            }
        },
        methods: {
            readMore: function () {

                if (this.friendList.length % 5) {
                    base.xTips({content: this.tips.nomore})
                } else {
                    this.getTasklist()
                }
            },
            getTasklist: function (type) {
                var self = this

                if (type && this.friendList.length) {
                    return false
                }

                if (self.friendListPageNum === -1) {
                    base.xTips({content: self.tips.nomore})
                    return false
                }

                base.getAPI({
                    url: urls.list,
                    data: {
                        invokeMethod: 'inviteFriendList',
                        invokeParameters: 'args_startNo_1_integer_' + self.friendListPageNum * 5
                    },
                    callback: function (data) {

                        if (data.success) {
                            var list = data.result.friendList || []
                            if (list.length) {
                                if (!self.friendList.length) {
                                    self.friendList = data.result.friendList
                                } else {
                                    self.friendList = self.friendList.concat(list)
                                }
                                ++self.friendListPageNum
                            } else {
                                if (self.friendListLoaded) {
                                    base.xTips({content: self.tips.nomore})
                                }
                            }
                            if (!self.friendListLoaded) {
                                self.friendListLoaded = true
                            }
                        }
                        else {

                            if (data.resultCodes) {
                                log(data.resultCodes[0].msg)
                            } else {
                                self.friendListPageNum = -1
                                base.xTips({content: self.tips.nomore})
                            }
                        }
                    }
                })
            },
            getRewards: function (type, count, logined) {
                var self = this
                if (!logined) {
                    location.href = self.loginUrl
                    return false
                }
                if (count) {
                    //领取终极宝箱
                    //领取幸运背包
                    base.getAPI({
                        url: urls.rewards,
                        data: {
                            invokeMethod: 'inviteFriendReceive',
                            invokeParameters: 'args_rewardType_1_integer_' + type
                        },
                        callback: function (data) {

                            if (data.success) {
                                var tips = type % 2 ? self.tips.package : self.tips.box

                                var risePoint = 0, receive = data.result.receiveReturnStr
                                if (data.result.rewardType === 1) {
                                    risePoint = receive.match(/\d{1}/g)[0] - 0
                                } else {
                                    var receiveList = receive.split(','), totalReceive = 0
                                    receiveList.forEach(function (item) {
                                        var list = item.split('-'),
                                            value = list[0],
                                            size = list[1]
                                        totalReceive = totalReceive + value * size
                                    })
                                }

                                if (type % 2) {
                                    --self.initData.packages
                                    self.totalPop = self.totalPop ? self.totalPop + risePoint : self.initData.totalPop - 0 + risePoint
                                } else {
                                    --self.initData.boxes
                                    self.totalCoupon = self.totalCoupon ? self.totalCoupon + totalReceive : self.initData.totalCoupon - 0 + totalReceive
                                }

                                var rewards = data.result.receiveReturnStr

                                if (rewards.split(',').length > 1) {
                                    self.rewardsList = self.getRewardsList(rewards)
                                } else {
                                    self.rewardsList = [{value: rewards, size: 1}]
                                }
                            } else {
                                log(data.resultCodes[0].msg)
                            }
                        }
                    })
                } else {
                    base.xTips({content: self.tips.noReward})
                }
            },
            getRewardsList: function (listString) {
                var list = listString.split(','), rtList = []
                list.forEach(function (item) {
                    var itemList = item.split('-'),
                        value = itemList[0],
                        size = itemList[1]
                    rtList.push({value: value, size: size})
                })
                return rtList
            },
            getMissionProcess: function (remark) {
                var list = [
                        'firstInvest',
                        'firstBindApp',
                        'firstBindWeChat',
                        'firstImproveInfo',
                        'firstBindEmail'
                    ],
                    process = {}

                if (remark && remark.length) {

                    list.forEach(function (item) {
                        process[item] = remark.indexOf(item) > -1
                    })
                }
                else {
                    process = this.initTaskProcess
                }
                return process
            },
            checkMission: function (index) {
                this.showMissionModal = true
                base.cover.show('#j-check-modal', true)
                this.taskProcess = this.getMissionProcess(this.friendList?this.friendList[index].remark:'')
            },
            closeMissionModal: function () {
                this.showMissionModal = false
                base.cover.hide()
            }
        }
    })
    if (window.debug) {
        window.m = Marathon
    }
    /* 左右滑块切换 */
    var $btnSlide = $('.j-btn-slide'),
        isLateralNavAnimating = false,
        $slidePage = $('#j-sidePage')

    $btnSlide.on('click', function (event) {
        event.preventDefault()

        var target = $(this).attr('data-target')

        $('#j-slide-threeGifts, #j-slide-method').addClass('z-hidden')
        $slidePage.addClass('z-checkUser').removeClass('z-checkUser')

        if (target === 'threeGifts') {

            $('.m-sidePage-wrapper').removeClass('z-hidden')
            $('#j-slide-threeGifts').removeClass('z-hidden')
            $('#j-check-invite').addClass('z-hidden')

        }
        else if (target === 'howTo') {

            $('.m-sidePage-wrapper').removeClass('z-hidden')
            $('#j-slide-method').removeClass('z-hidden')
            $('#j-check-invite').addClass('z-hidden')
        }
        // 查看好友记录
        else if (target === 'checkInvite') {

            var uid = $(this).attr('data-id')
            if (!uid) {
                location.href = Marathon.loginUrl
                return false
            } else {
                if (initData.result.activityStatus === 2) {
                    //活动暂未开始
                }
            }

            $('.m-sidePage-wrapper').addClass('z-hidden')
            $('#j-slide-method').addClass('z-hidden')
            $('.m-check-invite').removeClass('z-hidden')
            $slidePage.addClass('z-checkUser')
            Marathon.getTasklist(1)
        }

        //stop if nav animation is running
        if (!isLateralNavAnimating) {
            if ($(this).parents('.csstransitions').length > 0) isLateralNavAnimating = true

            $('body').addClass('navigation-is-open')
            $('.m-sidePage-wrapper').one('webkitTransitionEnd otransitionend oTransitionEnd msTransitionEnd transitionend', function () {
                //animation is over
                isLateralNavAnimating = false
            })
        }
    })

    $('.j-goBack').on('click', function () {
        $('body').removeClass('navigation-is-open')
    })

    $('.g-sidePage').on('touch', function (e) {
        event.preventDefault()
    })

    // 四个背包鼠标移上去的时候需要有具体奖励提示
    $('.j-vr-bag').click(function () {
        $(this).siblings('.j-vr-bag').children('div').removeClass('z-fadeIn')
        $(this).children('div').toggleClass('z-fadeIn')
    })
})
