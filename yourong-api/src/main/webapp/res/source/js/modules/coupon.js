define(['zepto', 'base', 'template'], function (require, exports, module) {
    var base = require('base'),
        template = require('template');
    //人气值获取记录、人气值兑换记录
    var $recordList = $('#j-record-list'),
        $concordList = $('#j-concord-list');
    if ($recordList.length) {
        //人气值获取记录
        getRecordList(1);
        //事件监听
        $recordList.on('click', '.m-pagenav a', function () {
            var pageNo = $(this).data('pageno');
            getRecordList(pageNo);
        });

        function getRecordList(pageNo) {
            base.getAPI({
                url:environment.globalPath + '/security/coupon/queryMemberPopularityLogList?type=1&pageNo=' + pageNo,
                version:'1.0.2',
                callback: function (data) {
                    if (data.success) {
                        if (data.result.totalElements > 0) {
                            var count = data.result.totalPageCount,
                                countArray = [];
                            if (count > 7) {
                                if (pageNo <= count - 3) {
                                    if (pageNo <= 4) {
                                        for (var i = 1; i <= 7; i++) {
                                            //利用push方法将页码一个个放入countArray数组
                                            countArray.push({'index': i});
                                        }
                                    } else {
                                        for (var l = pageNo - 3; l <= pageNo + 3; l++) {
                                            //利用push方法将页码一个个放入countArray数组
                                            countArray.push({'index': l});
                                        }
                                    }
                                } else {
                                    for (var k = pageNo - 3; k <= count; k++) {
                                        countArray.push({'index': k});
                                    }
                                }
                            }
                            else {
                                for (var j = 1; j <= count; j++) {
                                    countArray.push({'index': j});
                                }
                            }
                            data.result.countArray = countArray;

                            var html = template('j-record-tpl', data.result);
                            $recordList.html(html);
                        }
                        else {
                            base.showEmptyData("#j-record-list")
                        }
                    }
                }
            });
        }
        //人气值兑换记录
        getConcordList(1);
        //事件监听
        $concordList.on('click', '.m-pagenav a', function () {
            var pageNo = $(this).data('pageno');
            getConcordList(pageNo);
        });

        function getConcordList(pageNo) {
            base.getAPI({
                url:environment.globalPath + '/security/coupon/queryMemberPopularityLogList?type=2&pageNo=' + pageNo,
                version:'1.0.2',
                callback: function (data) {
                    if (data.success) {
                        if (data.result.totalElements > 0) {
                            //将取出来的页数打散放入数组
                            var count = data.result.totalPageCount,
                                countArray = [];
                            if (count > 7) {
                                if (pageNo <= count - 3) {
                                    //当前页小于4的时候
                                    if (pageNo <= 4) {
                                        for (var i = 1; i <= 7; i++) {
                                            countArray.push({'index': i});
                                        }
                                    } else {
                                        for (var l = pageNo - 3; l <= pageNo + 3; l++) {
                                            //利用push方法将页码一个个放入countArray数组
                                            countArray.push({'index': l});
                                        }
                                    }
                                } else {
                                    for (var k = pageNo - 3; k <= count; k++) {
                                        countArray.push({'index': k});
                                    }
                                }
                            }
                            else {
                                for (var j = 1; j <= count; j++) {
                                    countArray.push({'index': j});
                                }
                            }
                            data.result.countArray = countArray;

                            var html = template('j-concord-tpl', data.result);
                            $concordList.html(html);
                        }
                        else {
                            base.showEmptyData("#j-concord-list")
                        }
                    }
                }
            });
        }
    }
    //阻止UC/QQ滑动翻页
    (function(){
        var control = navigator.control || {};
        if (control.gesture) {
            control.gesture(false);
        }
    })();
    //我的优惠券-现金券、收益券
    var $couponCash = $('#j-cashCoupon'),
        $jCashCount=$('#j-cash-count'),
        $couponProfit = $('#j-profitCoupon'),
        $jProfitCount =$('#j-profit-count');
        //判断couponCash是否存在
    if ($couponCash.length) {
        //现金券
            //数据加载
        base.getAPI({
            url: environment.globalPath +'/security/coupon/queryCouponList?couponType=1',
            callback: function (data) {
                if (data.success) {
                    if (data.result.totalElements > 0) {
                        var html = template('j-cashCoupon-tpl', data.result);
                        $couponCash.html(html);
                        //现金券张数
                        $jCashCount.text(data.result.totalElements);
                        //券宽度
                        var $cashWrap = $('#j-cashCoupon-wrap'),
                            screenWidth=$('body').width();
                        $cashWrap.width((screenWidth + 4) * $cashWrap.data('total'))
                            .find('.u-coupon-box').width(screenWidth);
                       // alert($couponCash.find('.u-coupon-inner').eq(0).left);

                        //券划动效果
                        $couponCash.on('swipeLeft swipeRight', '.u-coupon-box', function (event) {

                            var index = $(this).index(),
                                pointIndex = index,
                                moveLength = index ? index : 1,
                                direction='left',
                                length=$(this).next().length;

                            if(event.type==='swipeRight'){
                                direction='right';
                                length = $(this).prev().length;
                            }

                            if (length) {
                                pointIndex = index + 1;
                                if(direction==='right'){
                                    pointIndex = index-1;
                                }
                                $cashWrap.animate({
                                    marginLeft: -1*screenWidth * pointIndex
                                }, 300, 'ease-out');

                            } else {
                                pointIndex = 0;
                                $cashWrap.animate({
                                    marginLeft: 0
                                }, 600, 'ease-out');
                            }
                            $('#j-cashCoupon-point').find('li').eq(pointIndex)
                                .addClass('z-current')
                                .siblings().removeClass('z-current');
                        });
                    }
                    else {
                        base.showEmptyData("#j-cashCoupon")
                    }
                }
                if(data.result.totalElements==0){
                    $jCashCount.hide();
                }
            }
        });
        //收益券
        base.getAPI({
            url:environment.globalPath + '/security/coupon/queryCouponList?couponType=2',
            callback: function (data) {
                if (data.success) {
                    if (data.result.totalElements > 0) {
                        var html = template('j-profitCoupon-tpl', data.result);
                        $couponProfit.html(html);
                        //优惠券张数
                        $jProfitCount.text(data.result.totalElements);

                        //券宽度
                        var $profitWrap = $('#j-profitCoupon-wrap'),
                            screenWidth=$('body').width();
                        $profitWrap.width((screenWidth + 4) * $profitWrap.data('total'))
                            .find('.u-coupon-box').width(screenWidth);

                        //券划动效果
                        $couponProfit.on('swipeLeft swipeRight', '.u-coupon-box', function (event) {

                            var index = $(this).index(),
                                pointIndex = index,
                                moveLength = index ? index : 1,
                                direction='left',
                                length=$(this).next().length;

                            if(event.type==='swipeRight'){
                                direction='right';
                                length = $(this).prev().length;
                            }

                            if (length) {
                                pointIndex = index + 1;
                                if(direction==='right'){
                                    pointIndex = index-1;
                                }
                                $profitWrap.animate({
                                    marginLeft: -1*screenWidth * pointIndex
                                }, 300, 'ease-out');

                            } else {
                                pointIndex = 0;
                                $profitWrap.animate({
                                    marginLeft: 0
                                }, 600, 'ease-out');
                            }
                            $('#j-profitCoupon-point').find('li').eq(pointIndex)
                                .addClass('z-current')
                                .siblings().removeClass('z-current');
                        });
                    }
                    else {
                        base.showEmptyData("#j-profitCoupon");
                    }
                }
                if(data.result.totalElements==0){
                    $jProfitCount.hide();
                }
            }
        });
        //左右滑动
        $('.u-cashCoupon-tab').on('click', 'span', function () {
            var _this = $(this),
                index = $(this).index();
            if (index > 0) {
                _this.addClass('z-current').
                    siblings().removeClass('z-current');
                $('.m-Coupon').eq(index - 1).addClass('z-current')
                    .siblings().removeClass('z-current');
            }
        });
        var jTarget = $(':target');
        if (jTarget.length) {
            var targetTab = $('#' + jTarget.data('target'));
            targetTab.click();
        }
    }



    //人气值显示
    var $repCount = $('#j-reputation-count');
    if ($repCount.length) {

        base.getAPI({
            url:environment.globalPath + '/security/coupon/queryMemberPopularity',
            callback: function (data) {
                if (data.success) {
                    var point = data.result,
                        percent = point / 10,
                        $count = $('#j-reputation-count');

                    //显示人气值
                    $count.text(parseInt(data.result)).parent().css('visibility', 'visible');

                    //if (point < 50) {
                    //    $count.next('a').addClass('z-disabled').prop('href', 'javascript:void(0)');
                    //}

                    //渲染环形(动画)
                    if (percent > 100) {
                        percent = 100;
                    } else if (percent < 1 && percent > 0) {
                        percent = 1;
                    }
                    $('#j-reputation-percent').text(percent);
                    processCircle('.u-reputation-circle');
                }
            }
        });
    }

    //人气值兑换
    var $repExchange = $('#j-repExchange-list');
    if ($repExchange.length) {

        //过滤兑换券列表

        base.getAPI({
            url:environment.globalPath + '/security/coupon/exchangeList',
            version:'1.0.5',
            callback: function (data) {
                if (data.success) {
                    if (data.result.totalElements > 0) {
                        base.getAPI({
                            url: environment.globalPath +'/security/coupon/queryMemberPopularity',
                            version:'1.0.5',
                            callback: function (data) {
                                if (data.success) {

                                    var point = data.result.availableBalance,
                                        range = 0;
                                    if (point >= 1000) {
                                        range = 7;
                                    } else if (point >= 500 && point < 1000) {
                                        range = 6;
                                    }else if(point >= 200 && point < 500){
                                        range=5
                                    }
                                    else if (point >= 100 && point < 200) {
                                        range = 4;
                                    } else if (point >= 50 && point < 100) {
                                        range = 3;
                                    }else if (point >= 30 &&point < 50) {
                                        range =2;
                                    } else if (point >= 10 &&point < 30) {
                                        range =1;
                                    }else if (point < 10) {
                                        range =0;
                                    }

                                    $repExchange.find('span').slice(0, range).removeClass('z-disabled');
                                 
                                }
                            }
                        });
                        var actionsheetExchange = data.result;
                        var html = template('j-actionsheetExchange-tpl', actionsheetExchange);
                        $repExchange.html(html);
                    }
                }
            }
        });

        //兑换
        $repExchange.on('click', 'span', function () {

            if ($(this).hasClass('z-disabled')) {
                return false;
            }

            var amount = $(this).data('amount');
               // dialog = $.loading({content: '兑换中...'});

            base.getAPI({
                url:environment.globalPath + '/security/coupon/exchange?value=' + amount,
                version:'1.0.1',
                callback: function (data) {
                    if (data.success) {
                       // dialog.loading('hide');
                        if (window.confirm('恭喜您，兑换成功！')) {
                            window.location.href=environment.globalPath +'/mCenter/reputationPage';
                        } else {
                            window.location.reload();
                        }
                    }else{
                        base.xTips({content:data.resultCodes[0].msg})
                    }
                }
            })
        });
    }


    //好友邀请记录
    var $inviteFriend = $('#j-inviteFriend');
    var jInviteRules = $('#j-invite-rules');
    if ($inviteFriend.length) {
        getInviteList(1);
        //事件监听
        $inviteFriend.on('click', '.m-pagenav a', function () {
            var pageNo = $(this).data('pageno');
            getInviteList(pageNo);
        });
        function getInviteList(pageNo) {
            //数据加载
            base.getAPI({
                url: environment.globalPath + '/security/coupon/queryMemberReferraList?type=1&pageNo=' + pageNo,
                callback: function (data) {
                    if (data.success) {
                        if (data.result.totalElements > 0) {


                            var count = data.result.totalPageCount,
                                countArray = [];
                            if (count > 7) {
                                if (pageNo <= count - 3) {
                                    if (pageNo <= 4) {
                                        for (var i = 1; i <= 7; i++) {
                                            //利用push方法将页码一个个放入countArray数组
                                            countArray.push({'index': i});
                                        }
                                    } else {
                                        for (var l = pageNo - 3; l <= pageNo + 3; l++) {
                                            //利用push方法将页码一个个放入countArray数组
                                            countArray.push({'index': l});
                                        }
                                    }
                                } else {
                                    for (var k = pageNo - 3; k <= count; k++) {
                                        countArray.push({'index': k});
                                    }
                                }
                            }
                            else {
                                for (var j = 1; j <= count; j++) {
                                    countArray.push({'index': j});
                                }
                            }
                            data.result.countArray = countArray;

                            var html = template('j-inviteFriend-tpl', data.result);
                            $inviteFriend.html(html);
                        }
                        else {
                            base.showEmptyData("#j-inviteFriend");
                        }
                    }
                }
            });
        }

        //人气值兑换规则弹出
        $('#j-inviteFriend-rule').on('click', function () {
            jInviteRules.addClass('show');
        });

        // 人气值兑换规则隐藏
        jInviteRules.on('click', 'button', function () {
            jInviteRules.removeClass('show');
        });
    }

    //金额取整
    template.helper('amountFormat', function (amount) {
        return parseInt(amount);
    });

    //人气值列表行为格式化
    template.helper('remarkFormat', function (remark) {
        var values = {
            '签到送人气值': '签到'
        };
        if (values[remark]) {
            return values[remark];
        } else {
            return remark;
        }
    });

    //格式化获取人气值行为
    template.helper('typeFormat', function (type) {
        var  typeList={'1':'推荐好友','2':'平台活动','3':'平台派送','4':'兑换优惠券','5':'补发人气值','6':'签到','7':'提现手续费','8':'兑换收益券'};
        return typeList[type];
    });
    //格式化收益券显示
    template.helper('profitFormat', function (amount,type) {
        var amountArray=(amount+'').split('.'),value='';
        switch(type){
            case 'int':
                value=amountArray[0];
                break;
            case 'float':
                value=amountArray[1];
                break;
        }
        return value;
    });

    //环形图
    var processCircle = function (target) {
        /*circle*/
        var $target = $(target);
        $target.each(function (index, el) {

            var _this = $(this),
                num = _this.find('span').text() * 3.6,
                rightCircle = _this.find('.z-right'),
                leftCircle = _this.find('.z-left');

            //环形动画
            num = parseInt(num);
            if (num <= 180) {
                rightCircle.animate({
                    rotate: num + 'deg'
                }, 1000, 'ease-out');
            } else {
                rightCircle.css({'transform': 'rotate(180deg)', '-webkit-transform': 'rotate(180deg)'});
                leftCircle.animate({
                    rotate: (num - 180) + 'deg'
                }, 1000, 'ease-out');
            }

        });
    };

    Vue.filter('dateFormat', function (date, format) {

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
    var investment = new Vue({
        el: '#j-topTips',
        data: {
            showRules: false,
            showSpecial: false,
            showTips: true,
            token: $('#xToken').val(),
            OverduePopularity: {
                year: '',
                month: ''
            }
        },
        created: function () {
            var self = this;
            base.getAPI({
                url: environment.globalPath + '/security/coupon/queryMemberOverduePopularity',
                version: '1.0.0',
                callback: function (data) {
                    if (data.success) {
                        self.OverduePopularity = data.result
                        self.OverduePopularity.year = data.result.year
                        self.OverduePopularity.month = data.result.month
                    } else {
                        base.xTips(data.resultCodes[0].msg)
                    }
                }
            })
            if (this.getCookie('isMShowed_memberId_02_' + self.token)) {
                this.showTips = false
            }


        },
        ready: function () {
            var self = this

        },
        methods: {
            showTipBlock: function (id) {
                this.showRules = true
                base.cover.show(null)
            },
            closeTipBlock: function (id) {
                this.showRules = false
                base.cover.hide()
            },
            getCookie: function (name) {
                var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
                if (arr = document.cookie.match(reg)) return unescape(arr[2]);
                else return null;
            },
            setCookie: function (name, value, time) {
                var self = this;
                var strsec = self.getsec(time);
                var exp = new Date();
                exp.setTime(exp.getTime() + strsec * 1);
                document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
            },
            getsec: function (str) {
                var str1 = str.substring(1, str.length) * 1;
                var str2 = str.substring(0, 1);
                if (str2 == "s") {
                    return str1 * 1000;
                } else if (str2 == "h") {
                    return str1 * 60 * 60 * 1000;
                } else if (str2 == "d") {
                    return str1 * 24 * 60 * 60 * 1000;
                }
            },
            showOnce: function () {
                var self = this
                self.showTips = false
                this.setCookie("isMShowed_memberId_02_" + self.token, self.token, "d31");
            }
        }
    })
});
