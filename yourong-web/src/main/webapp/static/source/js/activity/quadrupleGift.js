/**
 * Created by adeweb on 15/7/9.
 */
(function () {
    var url = {
        projects: '/activity/leadingSheep/projects',
        gainList: '/activity/leadingSheep/gainList',
        rankList: '/activity/leadingSheep/rankList'
    };
    //请求风云榜
    $.xPost({url: url.rankList, callback: renderRankList});

    //排行榜
    renderGainList('一羊领头送人气值');
    renderGainList('一鸣惊人送人气值');
    renderGainList('一锤定音送人气值');
    renderGainList('一掷千金送人气值');
    renderGainList('幸运女神送人气值');


    //项目列表
    $.xPost({url: url.projects, callback: renderProjectsList});

    //模板渲染
    function renderProjectsList(data) {
        $.each(data, function (i, item) {
            var targetId = 'j-project-0' + (i + 1),
                templateId = 'j-project-tpl0' + (i + 1);
            var html = template(templateId, item);
            $('#' + targetId).html(html);
            if(i===0){
                projectCountdown('.j-project-noticeTime');
            }
        });
        var $circle=$('.j-pl-stat2');
        if ($circle.length > 0) {
            $("body").append("<canvas id='j-canvas-demo'></canvas>");
            try {
                $("canvas")[0].getContext("2d");
                $circle.circliful();
            } catch (err) {

            }
            $("#j-canvas-demo").remove();
        }
    }


    function renderRankList(data) {
        var titleArray = ['一羊领头', '一鸣惊人', '一锤定音','一掷千金','幸运女神'],
            rankData = {};
        $.each(data, function (i, n) {
            n.title = titleArray[i];
        });
        rankData.list = data;
        var html = template('j-rankList-template', rankData);
        $('#j-rankList').html(html);
    }

    function renderGainList(remark) {

        var ids = {
                '幸运女神送人气值': ['j-gainList-05', 'j-gainList-tpl05'],
                '一鸣惊人送人气值': ['j-gainList-02', 'j-gainList-tpl02'],
                '一锤定音送人气值': ['j-gainList-03', 'j-gainList-tpl03'],
                '一掷千金送人气值': ['j-gainList-04', 'j-gainList-tpl04'],
                '一羊领头送人气值': ['j-gainList-01', 'j-gainList-tpl01']
            },
            targetId = ids[remark][0],
            templateId = ids[remark][1];

        $.xPost({
            url: url.gainList,
            data: {'remark': remark},
            callback: renderList
        });

        function renderList(data) {
            $.each(data, function (i, n) {
                n.title = remark.substr(0, 4);
            });

            var gainData = {},
                $target=$('#' + targetId);
            gainData.list = data;

            var html = template(templateId, gainData);
            $target.html(html);

            $target.scrollUserList({
                size: 5,
                height: -47,
                length: 1
            });
        }
    }

    //模板扩展方法
    template.helper('statusText', function (status,text) {
        switch (status) {
            case 30:
                value = text;
            break;
            case 40:
                value = '已暂停';
                break;
            case 50:
                value = '履约中';
                break;
            case 60:
                value = '履约中';
                break;
            case 70:
                value = '已还款';
                break;
            default:
                value= '';
        }
        return value;
    });

    //切换项目列表
    var $switchList=$('.j-list-switch');
    //列表切换
    $switchList.on('click','ol li', function () {
        var index=$(this).index();
        $(this).addClass('z-current').siblings().removeClass('z-current');
        $(this).parent().siblings('ul')
            .find('li').eq(index).addClass('z-current')
            .siblings().removeClass('z-current');
    });
    //头像下方信息切换
    $switchList.on('mouseenter','ul li', function () {
        var $amount=$(this).find('.u-pl-amount');
        if($amount.length){
            $(this).find('.u-pl-process').hide();
            $amount.show();
        }

    }).on('mouseleave','ul li', function () {
        var $amount=$(this).find('.u-pl-amount');
        if($amount.length){
            $(this).find('.u-pl-process').show();
            $amount.hide();
        }
    });
    //用户列表滚动
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
    //预告项目倒计时
    function projectCountdown(target){
        $(target).each(function () {
            var _this = $(this),
                time = _this.data('time');
            time = Math.ceil((time-environment.serverDate)/1000);
            if(time>0){
                _this.text(getCountTime(time));
                var timer = setInterval(function () {
                    _this.text(getCountTime(time-1));
                    time = time - 1;
                    if (time <= 0) {
                        clearInterval(timer);
                        _this.parents('.u-pl-btn').siblings('.u-pl-photo').find('.u-pl-process').text('当前进度0%');
                        _this.parent().removeProp('id').removeClass('z-disabled').text('我要一羊领头');
                    }
                }, 1000);
            }
        })
    }
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
        var timeString = timeArray.join(":");
        if (timeString === '00:00:00') {
            return 0;
        }
        return timeString;
    }
    /**
     * 对日期进行格式化，
     * @param date 要格式化的日期
     * @param format 进行格式化的模式字符串
     *     支持的模式字母有：
     *     y:年,
     *     M:年中的月份(1-12),
     *     d:月份中的天(1-31),
     *     h:小时(0-23),
     *     m:分(0-59),
     *     s:秒(0-59),
     *     S:毫秒(0-999),
     *     q:季度(1-4)
     * @return String
     * @author yanis.wang
     * @see	http://yaniswang.com/frontend/2013/02/16/dateformat-performance/
     */
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
        format = format.replace(/([yMdhmsqS])+/g, function(all, t){
            var v = map[t];
            if(v !== undefined){
                if(all.length > 1){
                    v = '0' + v;
                    v = v.substr(v.length-2);
                }
                return v;
            }
            else if(t === 'y'){
                return (date.getFullYear() + '').substr(4 - all.length);
            }
            return all;
        });
        return format;
    });
    template.helper('parseInt', function(number){
        return Math.ceil(number);
    });


})();
// 五重礼切换显示
(function(){
    var directionBtn = $('.j-direction-btn');
    directionBtn.on('click', function () {
        var direction = $(this).data('direction'),
            list = $(this).parent().find('.j-wrap-li'),
            listSize = list.length,
            step = 0,
            now = new Date().getTime(),
            offset = Number(list.css('margin-left').replace('px', ''));

        // 防止频繁点击
        var difftime = $(this).data('difftime') || 0;
        if (difftime !== 0 && now - difftime < 500) {
            return false;
        }
        $(this).data('difftime', now);

        //滑动方向
        if (direction === 'left') {
            step = -1;
            if (Math.abs(offset) / 250 >= listSize - 4) {
                return false;
            }
        } else {
            step = 1;
            if (offset === 0) {
                return false;
            }
        }

        list.animate({
            marginLeft: offset + step * 250
        }, 500);
    });
})();
(function(){
    // 一大波的hover  tooltip事件
    $('.j-hoverBlock').hover(function () {
        $(this).parent().next().toggleClass('z-show')
    })
})()