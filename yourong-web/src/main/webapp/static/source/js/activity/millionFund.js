/**
 * Created by lyl on 2016/1/6.
 */
(function () {
    //进度条
    $.xPost({
        url: '/activity//millionFund/init',
        callback: function (data) {
            var xData = data.result.investList;
            var num = data.result.fund;
            if (data.success) {
                var money = splitAmount(num);
                $('.j-fund').text('还剩￥' + money);
                progressBar(num);
                renderMillionFoundList(xData);
                changeBtnStatus(data.result.activityStatus);
            }

        }
    });

    //判断活动状态
    function changeBtnStatus(status){
        var statusText = '';
        switch (status) {
            case 2:
                statusText = '活动未开始';
                break;
            case 6:
                statusText = '活动已结束';
                break;
            default :
                break;

        }

        if (statusText) {
            $('.u-investmentBtn').addClass('z-investmentBtn-disabled')
                .find('a').attr("href", "javascript:void(0)")
                .find('span').text(statusText);
        }

    }
    //进度条计算
    function progressBar(num) {
        var Width = (1000000 - num) / 2000;
        var width2 = 500 - Width;
        var $progressBar = $('.m-progressBar .f-progressBar-bg'),
            $progressBarFund = $('.j-progressBarFund .f-text-ps');
        if (width2 === 500) {
            $progressBar.css({'width': '500px'});
        }
        else if (0 < width2 && width2 < 20) {
            $progressBar.css({'width': '18px'});
            $progressBarFund.css({'right': '422px'});
        }
        else if (width2 >= 20) {
            $progressBar.css({'width': width2 + 'px'});
            $progressBarFund.css({'right': (Width - 68) + 'px'});
        }
        else if (width2 === 0) {
            $progressBar.css({'width': 0});
            $('.m-progressBar .f-progressBar-img img').css({'display': 'none'});
            $progressBarFund.css({'right': '440px'});
            $('.u-investmentBtn').addClass('z-investmentBtn-disabled')
                .find('a').attr("href", "javascript:void(0)")
                .find('span').text('活动已结束');
        }
        else {
        }
    }
//金额格式化
    function splitAmount(amount) {

        if (amount >= 1000) {

            var amountStr = amount.toString(),
                size = parseInt(amountStr.length / 3),
                amountArray = amountStr.split('').reverse();

            for (var i = 1; i <= size; i++) {
                var j = i * 3 - 1;
                if (j !== amountArray.length - 1) {
                    amountArray[j] = ',' + amountArray[j];
                }
            }

            return amountArray.reverse().join('');

        } else {
            return amount;
        }
    };
//实时播报列表


    function renderMillionFoundList(data) {
        if (data === null) {
            $('.f-list-box .f-show').show();
        }
        else {
            var xData = {investList: data};
            var html = template('j-gainList-tp', xData);
            $('#j-gainList').html(html);
            $('#j-gainList').scrollList({
                size: 6,
                height: 47,
                length: 1,
                time: 3000
            });
        }
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
     * @see    http://yaniswang.com/frontend/2013/02/16/dateformat-performance/
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
    template.helper('parseInt', function (number) {
        return Math.ceil(number);
    });
    /**
     *金额格式化
     * @param amount{Number} 需要格式化的金额
     * @param format{String} 格式化类型，split=逗号分割的整数
     */
    template.helper('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return splitAmount(amount);
                break;
        }

        function splitAmount(amount) {

            if (amount >= 1000) {

                var amountStr = amount.toString(),
                    size = parseInt(amountStr.length / 3),
                    amountArray = amountStr.split('').reverse();

                for (var i = 1; i <= size; i++) {
                    var j = i * 3 - 1;
                    if (j !== amountArray.length - 1) {
                        amountArray[j] = ',' + amountArray[j];
                    }
                }

                return amountArray.reverse().join('');

            } else {
                return amount;
            }
        }
    });
})();