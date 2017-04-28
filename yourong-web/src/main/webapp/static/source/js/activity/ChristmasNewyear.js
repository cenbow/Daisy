/**
 * 双旦迎新，提钱跨年
 * @require ['jquery','common']
 * @author Adeweb/adewebx@gmail.com
 * @date 2015-12-01
 */
/*global $,env,console*/
//环境变量


(function() {
    'use strict';
    var domain = env.globalPath,
    // jcl 是 christmas-lottery 的简写，后面会用到很多次
        $jcl = $('#j-Christmas-lottery'),
        $logined = $jcl.data('logined'),
        _this,

    // 按钮点击时间，防止短时间内多次点击,这里直接设了10秒了
        thisClickTime,
        clickGutter = 10000,

    // 各个接口的domain + '/activity/doubleDan/receive/christmas地址
        initUrl = domain + '/activity/doubleDan/init',
        christmasUrl = domain + '/activity/doubleDan/receive/christmas',
        newYearUrl = domain + '/activity/doubleDan/receive/newYear',
        secretUrl = domain + '/activity/doubleDan/receive/scretGift';

    // 这里的初始化为了从后台了解活动还没开始或者活动已经结束 init
    $.xPost({
        url: initUrl,
        callback: function(data) {
            if(data.success) {
                initChristmasActivity(data.result);
                initNewYearActivity(data.result);
            } else {
                $.xDialog({
                    type: 'error',
                    content: '太挤了，伸个懒腰再来领'
                })
            }
        }
    });

    // 删除数组中的某个值
    Array.prototype.removeItem = function(val) {
        var index = this.indexOf(val);
        if(index > -1) {
            this.splice(index, 1);
        } else {
            return this;
        }
    };

    // 圣诞活动初始化
    function initChristmasActivity(data) {
        var christmasStatus = data.christmasStatus, // 圣诞节状态
            christmasGiftList = data.christmasGiftList || 0, // 选中的list
            christmasGiftListLen = christmasGiftList.length || 0, // 选中的list的个数

        // 圣诞节的图标,共6个
            $jclI = $jcl.find('i'),
            $jclI0 = $jclI.eq(0),
            $jclI1 = $jclI.eq(1),
            $jclI2 = $jclI.eq(2),
            $jclI3 = $jclI.eq(3),
            $jclI4 = $jclI.eq(4),
            $jclI5 = $jclI.eq(5),

        // 圣诞节按钮
            $jclSpan = $jcl.find('span'),

        // em字体string
            emBegin = '<em class="f-fs18">',
            emEnd = '%</em><br>收益券',

        // 圣诞快乐图标字体的
            marryChrismasStr = '<em class="f-fs14" style="position:relative; top: 8px;">圣诞快乐</em>',
        // 圣诞老爷爷走了
            fatherChrismasGoneStr = '很抱歉，圣诞老爷爷走远了，明年再约吧';

        // 圣诞的整体逻辑处理
        if(christmasStatus == 2) {
            setChristmasText('领取礼物', '圣诞老爷爷还没来，请耐心等待');
        } else if(christmasStatus == 4) {
            if($logined === 0) {
                duringChrisNotLogined();
            } else if($logined == 1) {
                duringChrisLogined();
            }
        } else if(christmasStatus == 6) {
            if($logined === 0) {
                afterChrisNotChoose();
            } else if($logined == 1) {
                if(christmasGiftListLen === null || christmasGiftListLen === 0) {
                    afterChrisNotChoose();
                } else if(christmasGiftListLen == 1) {
                    afterChrisChoosedOne();
                } else if(christmasGiftListLen == 2) {
                    afterChrisChoosedTwo();
                }
            }
        }

        // 获取选中的百分比，并且不要小数点，1.0%优惠券 > 1
        function getPercent(str) {
            if($.type(str) == 'string') {
                if(str == '谢谢惠顾') {
                    return '';
                } else {
                    var tempStr = str.substring(0, str.indexOf('%'));
                    if(tempStr == '1.0') {
                        tempStr = '1';
                    } else if(tempStr == '2.0') {
                        tempStr = '2';
                    }

                    return tempStr;
                }
            } else {
                return '';
            }
        }

        // 圣诞活动未开始或者已经结束，总体设置一下christmas的按钮的文字和事件
        function setChristmasText(str, content) {
            $jclSpan.text(str).on('click', function() {
                $.xDialog({
                    content: content
                });
            });

        }

        // 圣诞期间，没有登陆
        function duringChrisNotLogined() {
            $jclSpan.on('click', function() {
                $.xDialog({
                    okValue: '登录',
                    content: '登录后再去选择圣诞礼物哟',
                    callback: function() {
                        $.xPost({
                            url: christmasUrl
                        })
                    }
                });
            });
        }

        // 圣诞期间或结束，选择了两个礼物，那么图标都要翻出来，且设置按钮的颜色
        // 参数是 获取回来的 christmasGiftList
        function ChrisChoosedTwo(christmasGiftList) {
            // 如果返回的是谢谢惠顾就会变成一个空的字符串
            var numA = getPercent(christmasGiftList[0]) || '',
                numB = getPercent(christmasGiftList[1]) || '',
                tempArr = ['0.5', '0.8', '1', '1.5', '2', ''],
                tempStr;
            // 把已经获取的两个移到数组最前面
            tempArr.removeItem(numA);
            tempArr.removeItem(numB);
            tempArr.unshift(numB);
            tempArr.unshift(numA);

            $jclI.removeClass('z-initial').each(function(index) {
                tempStr = tempArr[index];

                if(tempStr === '') {
                    tempStr = marryChrismasStr;
                } else {
                    tempStr = emBegin + tempStr + emEnd;
                }

                $(this).html(tempStr);
            });
        }

        // 用于处理在点击选择第二个礼物时，需要翻牌，但是已经选的两个位置是不变的
        function chrisShowLeftBtns(tempChristmasGift, thisChristmasGift) {
            $jclSpan.not('.z-current').text('明年见').addClass('z-current').unbind('click');
            var numA = getPercent(tempChristmasGift[0]) || '',
                numB = getPercent(thisChristmasGift) || '',
                tempArr = ['0.5', '0.8', '1', '1.5', '2', ''];

            tempArr.removeItem(numA);
            tempArr.removeItem(numB);
            // 找到剩下的4个
            $jcl.find('i.z-initial').removeClass('z-initial').each(function(index) {
                _this = $(this);
                if(tempArr[index] === '') {
                    _this.html(marryChrismasStr);
                } else {
                    _this.html(emBegin + tempArr[index] + emEnd);
                }
            });
        }

        // 圣诞按钮点击，如果返回已经领取了两个礼物的错误，即同时在两处登录的情况
        function chrisReturnHasTwoGift() {
            $.xDialog({
                content: '您的两份礼物已都领取',
                callback: function() {
                    $.xPost({
                        url: initUrl,
                        callback: function(data) {
                            if(data.success) {
                                ChrisChoosedTwo(data.result.christmasGiftList);
                                $jclSpan.addClass('z-current').unbind('click');
                                $jcl.find('span:lt(2)').text('已领取')
                                    .end().find('span:gt(1)').text('明年见');
                            } else {
                                $.xDialog({
                                    type: 'error',
                                    content: '圣诞老爷爷爬烟囱卡住了，耐心等待下吧'
                                });
                            }
                        }
                    });
                }
            });
        }

        // 圣诞期间，已经登陆
        function duringChrisLogined() {
            // 如果已经选过了，就给按钮添加 .z-current，并修改图标
            if(christmasGiftListLen > 0) {
                var str = '';
                $jcl.find('span:lt(' + christmasGiftListLen + ')').each(function(index) {
                    if(christmasGiftList[index] == '谢谢惠顾') {
                        str = marryChrismasStr;
                    } else {
                        str = emBegin + getPercent(christmasGiftList[index]) + emEnd;
                    }
                    $(this).addClass('z-current').text('已领取')
                        .prev('i').removeClass('z-initial').html(str);
                });
            }

            var zCurrentLen = $jcl.find('span.z-current').length || 0;
            // 如果已经选择了2个礼物，就给按钮解除所有事件，且把以获取的两个给显示出来
            if(zCurrentLen == 2) {
                ChrisChoosedTwo(christmasGiftList);
                $jcl.find('span:gt(1)').text('明年见').addClass('z-current')
                    .end().find('span:lt(2)').text('已领取');
            } else {
                // 已经选过的就不要添加事件了
                $jclSpan.not('.z-current').on('click', function() {
                    _this = $(this);
                    // 防止多次点击
                    thisClickTime = (new Date).getTime();
                    if(thisClickTime - (+_this.attr('data-clickBegin') || 0) > clickGutter) {
                        _this.attr('data-clickBegin', thisClickTime)

                        // 这里获取一下当前有 z-current 的类的按钮个数，如果 >= 2 个就不再往后台发数据了
                        zCurrentLen = $jcl.find('span.z-current').length || 0;
                        _this = $(this);
                        // 如果没有选择过礼物
                        if(zCurrentLen === 0) {
                            $.xPost({
                                url: christmasUrl,
                                callback: function(data) {
                                    if(data.success) {
                                        var thisChristmasGift = data.result.thisChristmasGift || '';
                                        if(thisChristmasGift == '谢谢惠顾') {
                                            $.xDialog({
                                                content: '圣诞老爷爷忘记放礼物了，再去领一份吧',
                                                callback: function() {
                                                    _this.addClass('z-current').text('已领取').unbind('click')
                                                        .prev('i').removeClass('z-initial').html(marryChrismasStr);
                                                }
                                            });
                                        } else {
                                            $.xDialog({
                                                type: 'success',
                                                content: '恭喜您获得' + getPercent(thisChristmasGift) + '%收益券,还可以领一份礼物哦',
                                                callback: function() {
                                                    _this.addClass('z-current').text('已领取').unbind('click')
                                                        .prev('i').removeClass('z-initial').html(emBegin + getPercent(thisChristmasGift) + emEnd);
                                                }
                                            });
                                        }
                                    } else {
                                        chrisReturnHasTwoGift();
                                    }
                                }
                            });

                            // 如果已经选择过了一个礼物
                        } else if(zCurrentLen == 1) {
                            // 现在唯一的问题就是如果已经选择了一个礼物，剩下的礼物里再按一个 之后要翻牌所有的卡
                            $.xPost({
                                url: christmasUrl,
                                callback: function(data) {
                                    if(data.success) {
                                        var tempChristmasGift = data.result.christmasGiftList,
                                            thisChristmasGift = data.result.thisChristmasGift || '';

                                        if(thisChristmasGift == '谢谢惠顾') {
                                            $.xDialog({
                                                content: '圣诞老爷爷忘记放礼物了，明年再约吧',
                                                callback: function() {
                                                    _this.addClass('z-current').text('已领取').unbind('click')
                                                        .prev('i').removeClass('z-initial').html(marryChrismasStr);
                                                    chrisShowLeftBtns(tempChristmasGift, thisChristmasGift);
                                                }
                                            });

                                        } else {
                                            $.xDialog({
                                                type: 'success',
                                                content: '恭喜您获得' + getPercent(thisChristmasGift) + '%收益券,快去使用吧',
                                                callback: function() {
                                                    _this.addClass('z-current').text('已领取').unbind('click')
                                                        .prev('i').removeClass('z-initial').html(emBegin + getPercent(thisChristmasGift) + emEnd);
                                                    chrisShowLeftBtns(tempChristmasGift, thisChristmasGift);
                                                }
                                            });
                                        }
                                    } else {
                                        chrisReturnHasTwoGift();
                                    }
                                }
                            });
                        }
                    } else {
                        return false
                    }
                });
            }
        }

        // 圣诞结束后，没登陆 或者 登录没选择
        function afterChrisNotChoose() {
            // 设置按钮文字和事件
            setChristmasText('明年见', fatherChrismasGoneStr);
        }

        // 圣诞结束后，如果登陆了，但是只选择了1个礼物
        function afterChrisChoosedOne() {
            // 后面5个牌子的按钮都做这样的处理
            $jcl.find('span:gt(0)').text('明年见')
                .unbind('click').on('click', function() {
                    $.xDialog({
                        content: fatherChrismasGoneStr
                    });
                });
            // 把第一个给赋值
            var tempPercent = christmasGiftList[0];
            // 如果只获取一个，且那个为空，第一个按钮直接给圣诞快乐，且不能按
            if(tempPercent == '谢谢惠顾') {
                $jcl.find('span:first').addClass('z-current').text('已领取').unbind('click')
                    .prev('i').removeClass('z-initial').html(marryChrismasStr);

                // 后面5个牌子都显示概率
                $jclI.removeClass('z-initial');
                $jclI1.html('<em class="f-fs18">0.5%</em><br>收益券');
                $jclI2.html('<em class="f-fs18">0.8%</em><br>收益券');
                $jclI3.html('<em class="f-fs18">1.0%</em><br>收益券');
                $jclI4.html('<em class="f-fs18">1.5%</em><br>收益券');
                $jclI5.html('<em class="f-fs18">2%</em><br>收益券');

                // 获取的那个不为空
            } else {
                $jcl.find('span:first').addClass('z-current').text('已领取').unbind('click')
                    .prev('i').removeClass('z-initial').html(emBegin + getPercent(tempPercent) + emEnd);

                // 把已经取到的那个去掉，然后给后面四个赋值最后一个就填圣诞快乐
                var tempArr = ['0.5', '0.8', '1', '1.5', '2'];
                tempArr.removeItem(getPercent((tempPercent)));

                $jclI.removeClass('z-initial');
                $jclI1.html(emBegin + tempArr[0] + emEnd);
                $jclI2.html(emBegin + tempArr[1] + emEnd);
                $jclI3.html(emBegin + tempArr[2] + emEnd);
                $jclI4.html(emBegin + tempArr[3] + emEnd);
                $jclI5.html(marryChrismasStr);
            }
        }

        // 圣诞结束后，如果登陆了且选择了2个礼物
        function afterChrisChoosedTwo() {
            // 结束后的按钮处理
            $jcl.find('span:lt(2)').addClass('z-current').text('已领取').unbind('click')
                .end().find('span:gt(1)').removeClass('z-current').text('明年见').on('click', function() {
                    $.xDialog({
                        content: fatherChrismasGoneStr
                    });
                });
            // 选择了2个礼物之后，剩下的按钮和图标处理
            ChrisChoosedTwo(christmasGiftList);
        }
    }


    // 元旦活动初始化
    function initNewYearActivity(data) {
        var $jNewYearLottery = $('#j-NewYear-lottery'),
        // 元旦按钮各个状态
        // 1、等待领取 2、已领取， 未过期 3、已领取， 已过期 4、已使用 5、点击领取 6、热情等候中 7、登录查看 8、活动已结束
            missionArray = ['', '等待领取', '已领取', '已过期', '已领用', '点击领取', '热情等候中', '登录查看', '活动已结束'],

        // 获取元旦初始化的状态和神秘礼状态
            newYearStatus = data.newYearStatus,
            newYearMissionList = data.newYearMissionList,
            secretStatus = data.secretStatus,
            secretMission = data.secretMission,
            secretRealIndex = data.secretRealIndex,

        // 元旦前5个按钮
            $newYearBtns = $jNewYearLottery.find('.u-newyear-btn:lt(5)'),
        // 元旦每个按钮的mission
            thisBtnMission,
            prevBtnMission,
            nextBtnMission,

        // 神秘礼按钮
            $secretBtn = $jNewYearLottery.find('.u-newyear-btn:last');

        // 元旦活动整体逻辑, 由于后台的元旦前5个按钮, 神秘礼是分开的，所以整体逻辑也要分开做

        // 这里处理元旦的前5个按钮
        if(newYearStatus == 2) {
            $newYearBtns.addClass('z-current').text('热情等候中');
        } else if(newYearStatus == 4) {
            if($logined == 0) {
                duringNewYearNotLogined();
            } else if($logined == 1) {
                duringNewYearLogined(); // not done
            }
        } else if(newYearStatus == 6) {
            $newYearBtns.addClass('z-current').text('活动结束');
        }

        // 元旦活动进行中未登录,根据后台的数据修改按钮文本,前五个按钮
        function duringNewYearNotLogined() {
            $newYearBtns.each(function(index) {
                _this = $(this);
                thisBtnMission = newYearMissionList[index];
                if(thisBtnMission == 6) {
                    _this.addClass('z-current').text('热情等候中');
                }
                if(thisBtnMission == 7) {
                    _this.removeClass('z-current').text('登录查看')
                        .on('click', function() {
                            $.xPost({
                                url: christmasUrl
                            })
                        });
                }
            });
        }

        // 元旦活动进行中已登录，这个最复杂了
        function duringNewYearLogined() {
            // 先根据后台的数据修改按钮文本,前五个按钮
            $newYearBtns.each(function(index) {
                var _thisBtn = $(this);
                prevBtnMission = newYearMissionList[index - 1] || '';
                thisBtnMission = newYearMissionList[index] || '';
                nextBtnMission = newYearMissionList[index + 1] || '';

                // 给每个按钮的data-thisBtnMission属性赋值
                _thisBtn.addClass('z-current').text(missionArray[thisBtnMission])
                    .attr({
                        'data-prevBtnMission': prevBtnMission,
                        'data-thisBtnMission': thisBtnMission,
                        'data-nextBtnMission': nextBtnMission
                    });

                // 已过期的文本也显示已领取
                if(thisBtnMission == 3) {
                    _thisBtn.text('已领取');
                }
                if(thisBtnMission == 2) {
                    _thisBtn.attr('data-notUsed', 2);
                }

                // 如果是已领用, 已领用，已过期就把图片翻开
                if(thisBtnMission == 2 || thisBtnMission == 3 || thisBtnMission == 4) {
                    _thisBtn.prev('.u-newyear-gift').addClass('z-opened-' + (index + 1));
                }

                // 除了点击领取，即 thisBtnMission = 5的按钮是红的，其他的都是灰的
                if(thisBtnMission == 5) {
                    _thisBtn.removeClass('z-current')
                        .on('click', function() {
                            _this = $(this);
                            // 防止多次点击
                            thisClickTime = (new Date).getTime();
                            if(thisClickTime - (+_this.attr('data-clickBegin') || 0) > clickGutter) {
                                _this.attr('data-clickBegin', thisClickTime);
                                // giftIndex 需要这个参数，告诉后台点击的是第几关
                                $.xPost({
                                    url: newYearUrl,
                                    data: {
                                        'giftIndex': index
                                    },
                                    callback: function(data) {
                                        if(data.success) {
                                            // 点击领取之后有两种情况,而且这里只需要返回的数组，这个按钮的状态以及下一个按钮的状态即可
                                            // 后一个按钮是等待领取，字还是不变的，因为后面的按钮要能按，必须前一个是已领用才行啊
                                            newYearMissionList = data.result.newYearMissionList || [];
                                            thisBtnMission = newYearMissionList[index] || '';
                                            nextBtnMission = newYearMissionList[index + 1] || '';

                                            var tempStr = '';
                                            if(_thisBtn.attr('data-nextBtnMission') == '1' && _thisBtn.attr('data-nextValue') != '') {
                                                tempStr = '用完这张，今天还有一张' + _thisBtn.attr('data-nextValue') + '元现金券可以领取哟';
                                            } else {
                                                tempStr = '恭喜您，' + _thisBtn.attr('data-giftValue') + '元现金券领取成功！领取后一天有效，记得使用';
                                            }

                                            $.xDialog({
                                                type: 'success',
                                                content: tempStr,
                                                callback: function() {
                                                    // 图片翻开
                                                    _thisBtn.prev('.u-newyear-gift').addClass('z-opened-' + (index + 1));
                                                    _thisBtn.text('已领取').addClass('z-current').unbind('click')
                                                        .attr({
                                                            'data-prevBtnMission': prevBtnMission,
                                                            'data-thisBtnMission': thisBtnMission,
                                                            'data-nextBtnMission': nextBtnMission,
                                                            'data-notUsed': 2
                                                        });
                                                    $jNewYearLottery.find('span.u-newyear-btn:eq(' + (index + 1) + ')').attr('data-prevBtnMission', 2);
                                                }
                                            });

                                            // 这里就是同一用户同时登录两个地方，后台会报错的，所以只要再init一下所有按钮的状态即可
                                        } else {
                                            $.xDialog({
                                                content: '您已领取过今天的现金券',
                                                callback: function() {
                                                    window.location.reload();
                                                }
                                            });
                                        }
                                    }
                                });

                            } else {
                                return false;
                            }
                        });

                    // 等待领取
                } else if(thisBtnMission == 1) {
                    _thisBtn.on('click', function() {
                        // 分三种情况 即前面的按钮状态有
                        // prevBtnMission = 1（ 等待领取） || 5（ 点击领取） ||  = 2(已领取， 未使用) || = 3（ 已过期
                        // 同时要考虑前面所有的按钮中有没有已领取未使用的 ==2 的
                        // 处理前面有已过期，等待领取的文字提示
                        var notUsed = '';
                        var outOfDate = '';
                        $jNewYearLottery.find('.u-newyear-btn').each(function(index, el) {
                            if($(this).attr('data-notUsed') == 2) {
                                notUsed = 2
                            }
                            if($(this).attr('data-thisBtnMission') == 3) {
                                outOfDate = 1;
                            }
                        });

                        var tempStr = '';
                        prevBtnMission = _thisBtn.attr('data-prevBtnMission');

                        if(outOfDate == 1) {
                            tempStr = '您有券在有效期内未使用，遗憾止步于此';
                        } else {
                            if((prevBtnMission == 1 || prevBtnMission == 5) && notUsed != 2) {
                                tempStr = '要记得前面还有未领取的券哟';
                            } else if(prevBtnMission == 2 || notUsed == 2) {
                                tempStr = '要记得前面还有未使用的券哦';
                            }
                        }
                        $.xDialog({
                            content: tempStr
                        });
                    });
                }
            });
        }

        // 这里处理神秘礼
        if(secretStatus == 2) {
            $secretBtn.addClass('z-current').text('热情等候中');
        } else if(secretStatus == 4) {
            if($logined == 0) {
                duringSecretNotLogined();
            } else if($logined == 1) {
                duringSecretLogined(); // not done
            }
        } else if(secretStatus == 6) {
            $secretBtn.addClass('z-current').text('活动结束');
        }

        // 神秘礼进行中未登录
        function duringSecretNotLogined() {
            if(secretMission == 6) {
                $secretBtn.addClass('z-current').text('热情等候中');
            } else if(secretMission == 7) {
                $secretBtn.removeClass('z-current').text('登录查看')
                    .on('click', function() {
                        $.xPost({
                            url: christmasUrl
                        })
                    });
            }
        }

        // 神秘礼进行中已登录, 这个先放一边出现
        function duringSecretLogined() {
            var secretMission = data.secretMission,
                secretRealIndex = data.secretRealIndex,
                secretStatus = data.secretStatus,
                secretPrevMission = $jNewYearLottery.find('.u-newyear-btn:eq(4)').attr('data-thisBtnMission');

            // 如果是已领用, 已领用，已过期就把图片翻开
            if(secretMission == 2 || secretMission == 3 || secretMission == 4) {
                $secretBtn.prev('.u-newyear-gift').addClass('z-opened-6');
            }

            // 这是神秘礼初始化的时候就已经领取了,只有等于5的时候才可以点击
            if(secretMission == 1) {
                // 前一天是等待领取，未领取,未使用
                if(secretPrevMission == 1 || secretPrevMission == 2 || secretPrevMission == 5) {
                    $secretBtn.text('等待领取').addClass('z-current').on('click', function() {
                        secretPrevMission = $jNewYearLottery.find('.u-newyear-btn:eq(4)').attr('data-thisBtnMission');

                        var notUsed = '';
                        var outOfDate = '';
                        $jNewYearLottery.find('.u-newyear-btn').each(function(index, el) {
                            if($(this).attr('data-notUsed') == 2) {
                                notUsed = 2
                            }
                            if($(this).attr('data-thisBtnMission') == 3) {
                                outOfDate = 1;
                            }
                        });

                        var tempStr = '';
                        if(outOfDate == 1) {
                            tempStr = '您有券在有效期内未使用，遗憾止步于此';
                        } else {
                            if((secretPrevMission == 1 || secretPrevMission == 5) && notUsed != 2) {
                                tempStr = '要记得前面还有未领取的券哟';
                            } else if(secretPrevMission == 2 || notUsed == 2) {
                                tempStr = '要记得前面还有未使用的券哦';
                            }
                        }
                        $.xDialog({
                            content: tempStr
                        })
                    })
                }
                // 1、等待领取 2、已领取， 未过期 3、已领取， 已过期 4、已使用 5、点击领取 6、热情等候中 7、登录查看 8、活动已结束
                //if(secretRealIndex != 0) {
                //    $secretBtn.text('擦肩而过').on('click', function() {
                //        $.xDialog({
                //            content: '好遗憾，您未每天准时领取现金券，与神秘新年礼擦肩而过~'
                //        });
                //    });
                //}

            } else if(secretMission == 5) {
                if(secretRealIndex == 0) {
                    $secretBtn.text('擦肩而过').on('click', function() {
                        $.xDialog({
                            content: '好遗憾，您未每天准时领取现金券，与神秘新年礼擦肩而过~'
                        });
                    });
                } else {
                    $secretBtn.text('点击领取')
                        .one('click', function() {
                            _this = $(this)

                            thisClickTime = (new Date).getTime();
                            if(thisClickTime - (+_this.attr('data-clickBegin') || 0) > clickGutter) {
                                _this.attr('data-clickBegin', thisClickTime)
                                $.xPost({
                                    url: secretUrl,
                                    callback: function(data) {
                                        if(data.success) {
                                            var secretMission = data.result.secretMission,
                                                secretRealIndex = data.result.secretRealIndex,
                                                secretStatus = data.result.secretStatus,
                                                thisSecretGift = data.result.thisSecretGift;

                                            if(secretRealIndex == 0) {
                                                $.xDialog({
                                                    content: '好遗憾，您未每天准时领取现金券，与神秘新年礼擦肩而过~',
                                                    callback: function() {
                                                        $secretBtn.text('擦肩而过');
                                                    }
                                                });
                                            } else {
                                                $.xDialog({
                                                    type: 'success',
                                                    content: '恭喜您获得' + secretRealIndex * 60 + '元现金券，快去使用吧',
                                                    callback: function() {
                                                        $secretBtn.prev('.u-newyear-gift').addClass('z-opened-6');
                                                        $secretBtn.text('已领取').addClass('z-current');
                                                    }
                                                });
                                            }
                                        } else {
                                            $.xDialog({
                                                content: '您已领取过今天的现金券',
                                                callback: function() {
                                                    window.location.reload();
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                return false
                            }
                        });
                }
                // 神秘礼等待领取按钮也要有提示
            } else {
                $secretBtn.text(missionArray[secretMission]).addClass('z-current');
            }
        }
    }

    // 提示框toggle
    var examplearrows = $('#j-example-arrows'),
        doubleDanexample = $('#j-doubleDan-example'),
        exampleclose = $('#j-example-close');

    examplearrows.on('click', function() {
        doubleDanexample.toggle();
    });
    exampleclose.on('click', function() {
        doubleDanexample.hide();
    });
})();
