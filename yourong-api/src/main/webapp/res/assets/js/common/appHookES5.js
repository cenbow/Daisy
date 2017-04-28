/*
 * AppHook
 */
(function () {
    var AppHook = function (os) {
        //设备平台
        this.platform = {
            is: {
                android: os === 1,
                ios: os === 2,
                mweb: os === 3 || (os !== 1 && os !== 2)
            }
        }
        //登录状态
        var encryptionId = this.getArguments('encryptionId')
        this.logined = encryptionId.length > 40

        //全局回调
        window.getDynamicCallBack = this.callback

        //初始化当前事件名
        AppHook.cache('currentAppHookEventName', null)
    }

    AppHook.cache = function (name, value) {
        if (typeof(value) === 'undefined') {
            return window[name]
        } else {
            window[name] = value
            return value
        }
    }

    //获取参数
    AppHook.prototype.getArguments = function (name, url) {

        var localUrl = url ? url.substr(url.indexOf('?')) : location.search,
            reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"),
            argv = localUrl.substr(1).match(reg)

        return argv ? decodeURI(argv[2]) : ''
    }

    //获取字符化的键值对属性值
    AppHook.prototype.getAttr = function (attr, separator) {
        var sep = separator ? separator : '=',
            list = attr.split(sep)
        return attr.split(sep)[1]
    }

    AppHook.prototype.callback = function (data) {
        //NOTICE 全局回调里this指向的是window,故不能使用实例方法
        console.log(AppHook.cache('currentAppHookEventName'))

        var appData = JSON.parse(data)
        window.hookCallback(appData, AppHook.cache('currentAppHookEventName'))

        AppHook.cache('currentAppHookEventName', null)
    }

    //分享 无事件回调,与普通事件区分开
    AppHook.prototype.share = function ($target) {

        //传递当前事件名
        AppHook.cache('currentAppHookEventName', params[0])

        if (this.platform.is.android) {
            Android.ToActivity("inviteFriend", null)
            return false
        }
        else if (this.platform.is.ios) {
            $target.attr('href', this.getMethodUrl('inviteFriend'))
            return true
        }
    }

    //事件方法触发
    AppHook.prototype.getEvent = function (method, $target, type) {

        var params = method.split('&'),
            methodName = params[0],
            isNeedRealName = params.length > 1 ? this.getAttr(params[1]) : '0',
            args = params.length > 2 ? params.slice(2).join('&') : null,
            isUserID = params.length > 3 ? this.getAttr(params[3]) : 1

        //传递当前事件名
        AppHook.cache('currentAppHookEventName', params[0])
        console.log(methodName, isNeedRealName, args, isUserID)
        if (this.platform.is.android) {
            if (type == 1) {
                if (isUserID) {
                    Android.FindGetEvent(methodName, isNeedRealName, args, isUserID)
                } else {
                    Android.FindGetEvent(methodName, isNeedRealName, args, 1)
                }


                return false
            } else if (type == 0) {
                Android.ToActivity(methodName, null)
                return false
            } else {
                Android.GetEvent(methodName, isNeedRealName, args)
                return false
            }

        }
        else if (this.platform.is.ios) {
            $target.attr('href', this.getMethodUrl(method, type))
            console.log('postUrl:', $target.attr('href'))
            return true
        }
    }

    //登陆状态
    AppHook.prototype.login = function ($target) {
        var status

        switch (true) {
            case this.platform.is.android:
                status = this.androidLogin()
                break
            case this.platform.is.ios:
                status = this.iosLogin($target)
                break
            default:
        }
        return status
    }

    AppHook.prototype.androidLogin = function () {
        Android.ToActivity("login", null)
        return false
    }

    AppHook.prototype.iosLogin = function ($target) {
        $target.attr('href', this.getMethodUrl('loginIn'))
        return true
    }

    //获取iOS方法路径
    AppHook.prototype.getMethodUrl = function (method, type, title) {
        // 0--活动；1--发现；undefined--原先的
        // var pageTitle = '&title=' + encodeURI(title)
        switch (type) {
            case 0:
                return 'yrw-skip://invokeMethod=' + method
                break;
            case 1:
                return 'yrw-find://invokeMethod=' + method
                break;
            default:
                return 'yrw://invokeMethod=' + method
                break
        }
    }

    window.AppHook = AppHook
})()