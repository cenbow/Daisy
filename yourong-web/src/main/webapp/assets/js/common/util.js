/**
 * @module Util
 */
export const Util = class {
    /**
     * less than IE9
     * @return {Boolean}
     */
    static ltIE9() {
        return !Object.hasOwnProperty('keys')
    }

    /**
     * less than equal IE9
     * @return {Boolean}
     */
    static lteIE9() {
        return !('placeholder' in document.createElement('input'))
    }

    /**
     * 命名空间
     * @param {string} namespace
     */
    static ns(namespace) {
        var _ns = namespace.split('.'),
            cur = window[_ns[0]]

        if (typeof(cur) === 'undefined') {
            cur = window[_ns[0]] = {}
        }

        var len = _ns.length

        for (var i = 1; i < len; i++) {
            cur = cur[_ns[i]] = cur[_ns[i]] || {}
        }
        return cur
    }

    /**
     * setCookie
     * @param {string} name
     * @param {string} value
     * @param {date} exp
     */
    static setCookie(name, value, exp) {
        var Days = 30; //此 cookie 将被保存 30 天
        // var exp  = new Date();    //new Date("December 31, 9998");
        exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
        document.cookie = name + "=" + escape(value) + ";path=/" + ";expires=" + exp.toGMTString();
    }

    /**
     * 获取URL参数
     * @param {string} name
     * @param {string} url //如果不是浏览器环境,此项必填
     * @return {string}
     *
     */
    static getUrlParam(name, url) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"),
            localUrl = typeof(window) !== 'undefined' ? window.location.search : url.substr(url.indexOf('?')),
            params = localUrl.substr(1).match(reg) // 匹配目标参数
        return params ? decodeURI(params[2]) : '' // 返回参数值
    }

    /**
     * 本地存储封装
     * 所有键值对存在localStorage.xStorage下
     * @param name {string} key name
     * @param value {any} key value
     * @return get => {string|number}, set|del => {object},没有参数 => undefined
     */
    static storage(name, value) {
        var sessionData = {},
            xStorageValue = localStorage.getItem('xStorage')

        if (xStorageValue) {
            sessionData = $.parseJSON(xStorageValue)
        } else {
            localStorage.setItem('xStorage', '{}')
        }

        var action = typeof(value) === 'undefined' ? 'get' : (value !== null ? 'set' : 'del')
        switch (action) {
            case 'get':
                return $.type(name) === 'string' ? sessionData[name] : undefined
                break

            case 'set':
                sessionData[name] = value
                localStorage.setItem('xStorage', JSON.stringify(sessionData))
                break

            case 'del':
                delete sessionData[name]
                name ? localStorage.setItem('xStorage', JSON.stringify(sessionData)) : throwError('name')
                break

            default:
                return sessionData
        }

        function throwError(name) {
            throw new ReferenceError('xStorage: ' + name + ' is not defined')
        }

        return sessionData
    }

    /**
     * Ajax Post Method
     * @param  {Object} config
     *      config:{
     *          url:    ''/post/url',   //{String}   request url'
     *          type:  ''POST', //{String}  POST or GET
     *          datatype:   ''json',    //{String}    json
     *          async   :true,  //{Boolean},
     *          xToken  :''df34dgk4tdfm3d34t56129dfda',     {String}    请求安全所需的Token
     *          data    :{},    //{Object} or {Array}   键值对或者序列化form数组
     *          callback    :function(){}   //{Function}
     *      }
     */
    static post(config) {
        var type = "POST",
            datatype = "json",
            async = true

        if (typeof config === "object") {
            type = config.type ? config.type : type
            datatype = config.datatype ? config.datatype : datatype
            async = config.async ? config.async : async
            var xToken = $("#xToken").val(),
                xData = config.data

            if (typeof xToken !== "undefined" && xToken !== "") {
                if (config.hasOwnProperty("data")) {
                    //拼接的JSON(Object)
                    if ($.isPlainObject(xData)) {
                        xData.xToken = xToken
                    }
                    //序列化的表单数据(Array)
                    else if ($.isArray(xData)) {
                        //add xToken
                        var hasToken = false
                        $.each(xData, function (index, item) {
                            if (item.name === 'xToken') {
                                hasToken = true
                            }
                        })
                        !hasToken ? xData.push({name: 'xToken', value: xToken}) : null
                    }

                }
                else {
                    xData = {
                        xToken: xToken
                    }
                }
            }
        }
        else {
            throw new Error("post: config参数为空或不是对象")
        }
        $.ajax({
            type: type,
            url: config.url,
            data: xData,
            dataType: datatype,
            async: async,
            success: config.callback,
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        })
    }

    /**
     * 格式化 e.g. format(15000)=>15,000  format(1475130117675,'yyyy/MM/dd')=>2016/09/29
     * @param num {number} 金额数字或时间戳
     * @param tag 格式化格式
     * @returns {string}
     */
    static format(num, tag) {
        //格式化金额
        if ((Math.round(num) + '').length < 13) {
            var parts = num.toString().split(".")
            parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",")
            if (tag && !parts[1]) {
                parts[1] = '00'
            }
            return parts.join(".")
        }
        //格式化时间
        else {
            var date = new Date(num),
                format = tag

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
                        v = '0' + v
                        v = v.substr(v.length - 2)
                    }
                    return v
                }
                else if (t === 'y') {
                    return (date.getFullYear() + '').substr(4 - all.length);
                }
                return all
            })

            return format
        }
    }
}

/**
 * 替代原生toFixed
 * @param {Number} size
 */
Number.prototype.toFixed = function (size) {
    var s = this + "",
        d = size ? size : 0

    if (s.indexOf(".") == -1) {
        s += "."
    }

    s += new Array(d + 1).join("0")

    if (new RegExp("^(-|\\+)?(\\d+(\\.\\d{0," + (d + 1) + "})?)\\d*$").test(s)) {

        s = "0" + RegExp.$2
        var pm = RegExp.$1, a = RegExp.$3.length, b = true

        if (a == d + 2) {
            a = s.match(/\d/g)

            if (parseInt(a[a.length - 1]) > 4) {

                for (var i = a.length - 2; i >= 0; i--) {

                    a[i] = parseInt(a[i]) + 1

                    if (a[i] == 10) {
                        a[i] = 0;
                        b = i != 1

                    } else {
                        break
                    }
                }

            }

            s = a.join("").replace(new RegExp("(\\d+)(\\d{" + d + "})\\d$"), "$1.$2")

        }
        if (b)s = s.substr(1);
        return (pm + s).replace(/\.$/, "")

    }
    return this + ""
}