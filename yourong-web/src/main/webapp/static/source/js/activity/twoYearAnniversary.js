(function () {

    var path = environment.globalPath,
        logined = $('#j-top').data('logined')

    Vue.config.devtools = true

    window.Anniversary = new Vue({
        el: '#j-twoYearAnniversaray',
        data: {
            // 后端接口地址
            urls: {
                init: path + '/activity/anniversary/init',
                receiveCoupon: path + '/activity/anniversary/receive',
                receiveReward: path + '/activity/anniversary/receiveReward'
            },
            logined: logined,
            initData: {},
            showStatusDialog: false,           //普通弹出框
            showGiftPacksDialog: false,        //累投领礼包弹出框
            showSelectPopDialog: false,        //人气值选择弹出框
            showTenPrizesDialog: false,        //10点人气值奖品框
            showThirtyPrizesDialog: false,      //30点人气值奖品框

            giftBtnStatus: 0,
            cash88ActivityStatus: false,         //抢88元现金券活动状态
            isTheFirstDay: false,                //是否为活动第一天

            startGrabCash: false,
            startCountDown: false,
            hours: '',
            minutes: '',
            seconds: '',

            //提示框
            dialogTips: '',                    //提示信息
            targetUrls: '',                    //跳转路径
            dialogOther: '',                   //链接文字
            open: false,                       //规则弹窗

            rewardType: 0,                     //奖品类型：1.现金券 2.收益券 3.爱奇艺
            rewardValue: '',                   //奖品值
            clickCardPosition: 0,              //点击卡片的位置
            selectPopularity: 0,                //选择要消耗的人气值

            springCashList: [188, 100, 50, 188, 100, 50, 188, 100, 50],
            tenMinuteStatus: false

        },
        created: function() {
            var self = this;

            if('IE9' == self.IEVersion()){
                $('body').addClass('IE9')
            }else if (navigator.userAgent.indexOf('QQBrowser') !== -1) {
                $('body').addClass('QQBrowser')
            }

            $.xPost({
                url: self.urls.init,
                callback: function(data){
                    if(data.success){
                        init(data.result)
                    }else {
                        console.log('页面初始化错误',data)
                    }
                }
            })

            function init(result){

                self.initData = {
                    startTime: result.startTime || '',   //活动开始时间
                    endTime: result.endTime || '',       //活动结束时间
                    activityStatus: result.activityStatus || 2,  //活动状态

                    // 88元红包
                    totalCoupon88: result.totalCoupon88 || 0,
                    coupon88Remind: result.coupon88Remind || 0,
                    eightCouponStartTime: result.eightCouponStartTime || 0,

                    // 夏--累投领礼包
                    summerGift: {
                        popularity19: result.popularity19 || false,
                        popularity199: result.popularity199 || false,
                        popularity659: result.popularity659 || false,
                        popularity1119: result.popularity1119 || false,
                        popularity2016: result.popularity2016 || false,
                        iphone7: result.iphone7 || false
                    },

                    totalMyInvestAmount: result.totalMyInvestAmount || 0,  //我的累计投资总额
                    myNumber: result.myNumber || 0,  //我的领取次数
                    myPopularity: result.myPopularity ||0,  //我的人气值

                    // 秋--翻牌
                    position: result.position || 0,
                    rewardResult: result.rewardResult || ''

                };

                self.rewardType = self.getRewardType(result.rewardResult);

                // 判断168元抢红包活动的状态
                var curTime = env.serverDate,
                    startTime = self.initData.eightCouponStartTime;

                self.isTheFirstDay = moment(curTime).isSame(startTime, 'day');

                if (curTime - startTime >= 0) {
                    self.cash88ActivityStatus = true
                }

                console.log("大活动开始时间: "+new Date(self.initData.startTime))
                console.log("168元抢红包开始时间: "+new Date(self.initData.eightCouponStartTime))
                console.log("判断168元抢红包活动的状态: "+self.cash88ActivityStatus)
                console.log("是否为活动第一天: "+self.isTheFirstDay)
            }
        },
        computed: {
            // 抢88元红包是否置灰
            getCash88BtnStatus: function(){
                var self = this,
                    count = self.initData.coupon88Remind;
                console.log("判断168元抢红包活动的状态: "+self.cash88ActivityStatus)

                if (self.cash88ActivityStatus) {
                    if(self.isTheFirstDay){
                        if (count>0) {
                            return false
                        }else {
                            return true
                        }
                    }else {
                        return true
                    }
                }else {
                    return true
                }

            },
            // 抢88元红包倒计时
            cash88TimeCount: function() {
                var self = this;
                var seconds = 0;
                var flag = false;
                var date = env.serverDate;
                var startDate = self.initData.eightCouponStartTime;
                var curTime = self.getSeconds(new Date(date).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
                var startTime = self.getSeconds(new Date(startDate).toJSON().split(/[TZ]/)[1]) + 3600 * 8;

                /*活动开始第一天*/
                if (!self.cash88ActivityStatus && self.isTheFirstDay ) {
                    if (curTime < startTime && startTime - curTime > 600) {
                        var extraTime = 10 * 60;
                        var extraSeconds = startTime - curTime - extraTime;
                        flag = false

                        var exTimer = setInterval(function () {
                            if (extraSeconds > 0) {
                                extraSeconds--
                            } else {
                                clearInterval(exTimer);
                                seconds = 600;
                                self.tenMinuteStatus = true;
                                self.countdown(parseInt(seconds))
                            }
                        }, 1000)

                    } else if (curTime < startTime && startTime - curTime <= 600) {
                        // 最后10分钟开始倒计时
                        seconds = startTime - curTime;
                        self.startCountDown = true;
                        flag = true;
                    }else if (curTime > startTime) {
                        self.startGrabCash = true;
                        self.startCountDown = false;
                        flag = false
                    }
                }else if(self.cash88ActivityStatus) {
                    self.startGrabCash = true;
                }

                if (seconds > 0) {
                    self.countdown(parseInt(seconds))
                }

                return flag

            }

        },
        methods: {
            IEVersion: function(){
                var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
                var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE浏览器
                var isEdge = userAgent.indexOf("Windows NT 6.1; Trident/7.0;") > -1 && !isIE; //判断是否IE的Edge浏览器
                if(isIE)
                {
                    var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
                    reIE.test(userAgent);
                    var fIEVersion = parseFloat(RegExp["$1"]);
                    if(fIEVersion == 7)
                    { return "IE7";}
                    else if(fIEVersion == 8)
                    { return "IE8";}
                    else if(fIEVersion == 9)
                    { return "IE9";}
                    else if(fIEVersion == 10)
                    { return "IE10";}
                    else if(fIEVersion == 11)
                    { return "IE11";}
                    else
                    { return "0"}//IE版本过低
                }
                else if(isEdge)
                {
                    return "Edge";
                }
                else
                {
                    return "-1";//非IE
                }
            },
            getSeconds: function (timeString) {
                var list = timeString.split(':')
                return list[0] * 3600 + list[1] * 60 + Math.ceil(list[2] * 1)
            },
            fixZero: function (num) {
                return +num < 10 ? '0' + num : num
            },
            countdown: function (seconds) {
                var sec = seconds - 1,
                    self = this

                count()

                var timer = setInterval(function () {

                    if (sec > 0) {
                        count()
                    } else {
                        clearInterval(timer)
                        self.startGrabCash = true;
                        self.startCountDown = false;
                        self.cash88ActivityStatus = true;
                    }
                }, 1000)

                function count() {
                    var hours = parseInt(sec / 3600),
                        minutes = parseInt((sec - hours * 3600) / 60)
                    self.$set('hours', self.fixZero(hours))
                    self.minutes = self.fixZero(minutes)
                    self.seconds = self.fixZero(sec - hours * 3600 - minutes * 60)
                    sec--
                }
            },
            // 抢88元红包按钮文案
            cash88Text: function(){
                var text = '';
                var count = this.initData.coupon88Remind;

                if (this.cash88ActivityStatus) {
                    if (this.isTheFirstDay) {
                        if (count < 1) {
                            text = '已抢完'
                        }else {
                            text = '立即领取'
                        }
                    }else {
                        text = '已抢完'
                    }
                }else {
                    text = '即将开抢'
                }

                return text
            },
            // 抢88元红包按钮
            receive88Cash: function($event){
                var self = this;
                var count = self.initData.coupon88Remind;

                //请求数据
                var status = self.cash88ActivityStatus;
                if (status && self.isTheFirstDay) {
                    if(!self.logined){
                        self.goLoginPage();
                    }else {
                        if (count > 0) {
                            $.xPost({
                                url: self.urls.receiveCoupon,
                                data: {
                                    type: 1,
                                    couponAmount: 88
                                },
                                callback: function(data) {
                                    if(data.success) {
                                        self.dialogTips = '恭喜您获得一张168元现金券，快去使用吧~';
                                        self.targetUrls = path + '/products/list-all-all-investing-1-createTimeDesc-1.html';
                                        self.dialogOther = '立即使用 >>';
                                        self.initData.coupon88Remind = data.result.coupon88Remind;
                                    }else {
                                        if (data.resultCodeEum[0].code == '91052') {
                                            self.dialogTips = '存钱罐可用余额≥2000元就可以抢红包啦~';
                                            self.targetUrls = path+'/memberBalance/rechargePage';
                                            self.dialogOther = '立即充值 >>';
                                        }else {
                                            self.dialogTips = data.resultCodeEum[0].msg;
                                            self.targetUrls = '';
                                            self.dialogOther = '';
                                        }
                                    }
                                    self.activityDialog('showGiftPacksDialog','show');
                                }
                            })
                        }else {
                            return false
                        }
                    }
                }else {
                    $event.preventDefault();
                    return  false
                }

            },
            // 领取现金券
            receiveCashCoupon: function(val) {
                var self = this;

                if(!self.logined){
                    self.goLoginPage()
                }else {
                    if (self.initData.activityStatus === 2) {
                        self.dialogTips = '活动即将开始';
                        self.activityDialog('showStatusDialog','show');
                    }else if (self.initData.activityStatus === 4) {
                        $.xPost({
                            url: self.urls.receiveCoupon,
                            data: {
                                type: 1,
                                couponAmount: val
                            },
                            callback: function(data) {
                                if(data.success){
                                    self.dialogTips = '恭喜您获得一张'+self.springCashList[val-1]+'元现金券，快去使用吧~';
                                    self.targetUrls = '/products/list-all-all-investing-1-createTimeDesc-1.html';
                                    self.dialogOther = '立即使用 >>';
                                    self.initData.myNumber = self.initData.myNumber - 1;
                                }else {
                                    self.dialogTips = '不满足领取条件，';
                                    self.targetUrls = '/products/list-all-all-investing-1-createTimeDesc-1.html';
                                    self.dialogOther = '立即投资 >>';
                                }
                                self.activityDialog('showGiftPacksDialog','show');
                            }
                        })
                    }else if (self.initData.activityStatus === 6) {
                        self.dialogTips = '活动已结束';
                        self.activityDialog('showStatusDialog','show');
                    }
                }

            },
            // 人气值收益券领取按钮文案
            giftPacksBtnText: function(val) {
                var text = '';
                var self = this;
                var popularityName = '';
                var status = self.initData.activityStatus;

                if('iphone7' === val) {
                    popularityName = 'iphone7';
                }else {
                    popularityName = 'popularity' + val;
                }

                if (status === 2) {
                    text = '领取';
                } else if (status === 4 || status === 6) {
                    if (self.initData.summerGift[popularityName]) {
                        text = '已领取'
                    } else {
                        text = '领取'
                    }
                }

                return text
            },
            // 领取礼包
            getGiftPacks: function(val,str) {
                var self = this;
                var popularityName = '';

                if('iphone7' === str) {
                    popularityName = 'iphone7';
                }else {
                    popularityName = 'popularity' + str;
                }

                if(!self.logined){
                    self.goLoginPage();
                }else {
                    if (!self.initData.summerGift[popularityName]) {
                        if (self.initData.activityStatus === 2) {
                            self.dialogTips = '活动即将开始';
                            self.activityDialog('showStatusDialog', 'show');
                        } else if (self.initData.activityStatus === 4) {
                            $.xPost({
                                url: self.urls.receiveCoupon,
                                data: {
                                    type: 2,
                                    couponAmount: val,
                                },
                                callback: function(data) {
                                    if(data.success){
                                        self.dialogTips = '恭喜您获得专属礼包，快去使用吧~';
                                        self.targetUrls = '/coupon/reputation';
                                        self.dialogOther = '立即使用 >>';
                                        self.initData.summerGift[popularityName] = true;
                                    }else {
                                        self.dialogTips = '不满足领取条件，';
                                        self.targetUrls = '/products/list-all-all-investing-1-createTimeDesc-1.html';
                                        self.dialogOther = '立即投资 >>';
                                    }
                                    self.activityDialog('showGiftPacksDialog','show');
                                }
                            })
                        } else if (self.initData.activityStatus === 6) {
                            self.dialogTips = '活动已结束';
                            self.activityDialog('showStatusDialog', 'show');
                        }
                    } else {
                        return false
                    }
                }
            },
            // 翻牌  (当日已翻牌点击无效)
            drawCards: function(val) {
                var self = this;

                if(!self.logined){
                    self.goLoginPage();
                }else {
                    if(self.initData.activityStatus === 4 ) {
                        if(self.initData.position && self.initData.position > 0){
                            self.dialogTips = '今日已超限，明天再来哦~';
                            self.targetUrls = '';
                            self.dialogOther = '';
                            self.activityDialog('showGiftPacksDialog','show');
                        }else {
                            self.clickCardPosition = val;
                            self.activityDialog('showSelectPopDialog','show');
                        }
                    }else if (self.initData.activityStatus === 2) {
                        self.dialogTips = '活动即将开始';
                        self.targetUrls = '';
                        self.dialogOther = '';
                        self.activityDialog('showGiftPacksDialog','show');
                    }else if (self.initData.activityStatus === 6) {
                        self.dialogTips = '活动已结束';
                        self.targetUrls = '';
                        self.dialogOther = '';
                        self.activityDialog('showGiftPacksDialog','show');
                    }
                }

            },
            // 打开奖品列表
            openPrizesModal: function(val) {
                var self = this;
                self.giftBtnStatus = val;
                if("10" == val){
                    self.activityDialog('showTenPrizesDialog','show');
                }else {
                    self.activityDialog('showThirtyPrizesDialog','show');
                }
            },
            // 选择消耗10/30点人气值
            selectPopValue: function(val) {
                this.selectPopularity = val;
            },
            //奖品类型: 1.现金券  2.收益券  3.爱奇艺
            getRewardType: function(result) {
                var self = this;
                var type = 0;

                if(result){
                    if (result.indexOf('For') === -1) {
                        type = 3;
                        self.rewardValue = 'iQiYi';
                    }else {
                        if (result.split('For')[0] === 'coupon') {
                            type = 1;
                        }else {
                            type = 2;
                        }
                        self.rewardValue = result.split('For')[1];
                    }
                }

                return type
            },
            // 确定翻牌
            goPrizeDraw: function() {
                var self = this;

                if (self.selectPopularity === 0) {
                    return false;
                }else {
                    $.xPost({
                        url: self.urls.receiveReward,
                        data: {
                            chip: self.clickCardPosition,
                            popularValue: self.selectPopularity
                        },
                        callback: function(data) {
                            if(data.success){
                                var result = data.result;
                                self.initData.position = result.position;
                                self.rewardType = self.getRewardType(result.rewardResult);
                                self.initData.myPopularity = self.initData.myPopularity - self.selectPopularity
                            }else {
                                self.dialogTips = '人气值不足，';
                                self.targetUrls = '/banner/activityList';
                                self.dialogOther = '立即赚人气值 >>';
                                self.activityDialog('showGiftPacksDialog','show');
                            }
                            self.activityDialog('showSelectPopDialog','hide');
                        }
                    })
                }
            },
            // 活动弹出框
            activityDialog: function(name,status) {
                if("undefined" == typeof status || "show" === status) {
                    this[name] = true;
                }else {
                    this[name] = false;
                }

                if (this.selectPopularity !== 0) {
                    this.selectPopularity = 0
                }

                $.shade(status);
            },
            // 去登陆
            goLoginPage: function () {
                $.xPost({url: this.urls.receiveCoupon})
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

    $('.j-gold-animation').snowfall('clear');
    $('.j-gold-animation').snowfall({
        image: "/static/img/activity/twoYearAnniversary/jb1.png",
        flakeCount:15,
        minSpeed: 20,
        minSize: 10,
        maxSize: 22
    });

    var isCodeControl = false;
    var menuTitles =  $("#j-stickUp-nav .u-menuItem");
    var topNav = $("#j-stickUp-nav");

    /*导航选中高亮*/
    var rightSlideActive = function rightSlideActive(index){
        menuTitles.removeClass('active').eq(index).addClass('active');
    }

    /* 右侧导航置顶 */
    $('#j-nav-toTop').on('click',function(){
        $("html,body").animate({
            'scrollTop': '0px'
        })
    })

    /*顶部悬浮导航*/
    $('#j-stickUp-nav li').on('click',function(){
        var $index = $(this).index();

        rightSlideActive($index);

        if ($index === 0) {
            isCodeControl = true;
            $("html,body").animate({
                'scrollTop': '0px'
            }, 500,function(){
                isCodeControl = false;
                rightSlideActive(0);
                updateNavItem();
            })
        }else {
            isCodeControl = true;
            $("html,body").animate({
                scrollTop: $('#j-twoYearAnniversaray .m-module-wrap').eq($index - 1).offset().top - 88
            },500,function(){
                isCodeControl = false;
                updateNavItem();
            });
        }
    })

    var updateNavItem = function updateNavItem(){
        //获取滚动条的滑动距离
        var top = $(document).scrollTop();          //定义变量，获取滚动条的高度
        var menu = $("#j-main-wrapper");            //定义变量，抓取#menu
        var items = menu.find(".m-module-wrap");          //定义变量，查找.item
        var curId = "";                              //定义变量，当前所在的楼层item #id

        items.each(function(index){
            var item  =  $(this);
            var itemTop = item.offset().top - 118;        //定义变量，获取当前的top偏移量
            $(this).attr("data-id",index+1);
            if (top >= itemTop) {
                curId =  item.attr("data-id");
            }else{
                return false;
            }
        });

        //根据取得的id设置相应属性
        if(curId && !menuTitles.eq(curId).hasClass("active")){
            rightSlideActive(curId);
        }
    }

    /*滚动到相应模块，右侧导航响应*/
    $(window).scroll(function(){
        var navTop = $(this).scrollTop();

        if (navTop < 725) {
            topNav.fadeOut();
        }else {
            topNav.fadeIn();
            topNav.css({
                position: "fixed",
                top: "10px",
            })
        }

        var len = $('#j-twoYearAnniversaray').find('.running').length;
        if (navTop > 2263 && len <= 0) {
            $('.j-decoration').addClass('running');
        }

        if (navTop < 700) {
            if(!$('#j-nav-toTop').hasClass('f-dn')){
                $('#j-nav-toTop').addClass('f-dn');
            }
        }else {
            $('#j-nav-toTop').removeClass('f-dn');
        }

        if(isCodeControl) return;
        updateNavItem();
    });

})()
