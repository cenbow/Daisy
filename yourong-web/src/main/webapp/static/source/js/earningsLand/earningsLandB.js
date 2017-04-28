/**
 * Created by lyl on 2015/12/17.
 */
/**
 * Created by lyl on 2015/12/17.
 */
$(function(){
//项目列表
    $.xPost({
        url:'/landing/quintupleGift/lastProjects',
        callback: function (data) {

            renderProjectsList(data);
        }
    });
    function renderProjectsList(data) {
        var html = template('j-project-tpl01', data);
        $('#j-project-01').html(html);
        //var $circle=$('.j-pl-stat2');
        //if ($circle.length > 0) {
        //    $("body").append("<canvas id='j-canvas-demo'></canvas>");
        //    try {
        //        $("canvas")[0].getContext("2d");
        //        $circle.circliful();
        //    } catch (err) {
        //
        //    }
        //    $("#j-canvas-demo").remove();
        //}
    };

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
    //获取五重礼的用户列表
    $.xPost({
        url:'/landing/quintupleGift/gain/list',
        callback:function(data){
                getQuintupleGiftGainList(data)
        }
    });
    function getQuintupleGiftGainList(data){
        var xData={getQuintupleGiftGainList:data};
        var html=template('j-gainList-tpl',xData);
        $('#j-gainList-01').html(html);
        $('#j-gainList-01').scrollUserList({
            size: 6 ,
            height: -47,
            length: 1
        });
    }
    //抢标五重礼
    $.xPost({
        url:'/landing/quadrupleGift/count',
        callback:function(data){
            getQuintupleCount(data)
        }
    });
    function getQuintupleCount(data){
        var html=template('j-quintupleCount-tp',data);
        $('#j-quintupleCount').html(html);

    }

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
    template.helper('parseInt', function(number){
        return Math.ceil(number);
    });

})