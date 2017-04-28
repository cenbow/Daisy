/**
 * @module template
 * @description 在 template.js的基础上添加了helper方法
 */

export const template = (function(){
    let template = require('lib/js/template.js')
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

    /**
     * 向上取整
     * @param {number} number
     * @returns {number}
     */
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

    return template
})()