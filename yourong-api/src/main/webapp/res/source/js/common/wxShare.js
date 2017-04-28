/**
 * 微信分享组件
 * @author Adeweb
 */
/*global define,console,$*/
define(['zepto', 'wxSDK'], function(require, exports, module) {

    'use strict';

    var wx = require('wxSDK'),
        config = {},
        noop = function() {},
        log = noop;

    exports.init = function(data) {
        if (typeof(data) === 'object') {
            if (data.debug) {
                log = console.log.bind(console);
            }
            config = data;
        }

        log('config:', data);

        $.ajax({
            url: config.reqUrl,
            type: 'get',
            dataType: 'jsonp',
            data: {
                "shareUrl": config.shareUrl
            },
            'jsonpCallback': 'wxCallback'
        });
    };
    /**
     *接收jsonp回调
     *@param {Object} data json数据
     */
    window.wxCallback = function(data) {
        if (typeof(data) === 'object' && data.signature) {
            wxShareInit({
                timestamp: data.timestamp,
                nonceStr: data.nonceStr,
                signature: data.signature
            });
        } else {
            log('签名获取失败!');
        }
    };

    /**
     *微信分享初始化
     *@param {Object} data 配置数据
     */
    function wxShareInit(data) {
        if (!Object.keys(data).length) {
            log('wxShareInit初始化失败');
            return false;
        }
        wx.config({
            debug: config.debug,
            appId: config.appId, // 必填，公众号的唯一标识
            timestamp: data.timestamp, // 必填，生成签名的时间戳
            nonceStr: data.nonceStr, // 必填，生成签名的随机串
            signature: data.signature, // 必填，签名
            //jsApiList: ['onMenuShareAppMessage', 'onMenuShareTimeline', 'onMenuShareQQ', 'onMenuShareQZone'] //要使用的接口列表
            jsApiList: config.jsApiList || ['onMenuShareAppMessage', 'onMenuShareTimeline', 'onMenuShareQQ', 'hideOptionMenu', 'showOptionMenu', 'hideMenuItems', 'showMenuItems'] //要使用的接口列表
            // cc change
        });
        wx.ready(function() {
            log('wxShareInit Success!');

            //alert('wxShareInit成功')

            //分享参数
            var wxConfig = {
                title: config.title,
                desc: config.desc || '',
                link: config.link || location.href,
                imgUrl: config.imgUrl || '',
                trigger: function(res) {
                    log('用户点击了分享\n' + res);
                },
                success: function(res) {
                    // 用户确认分享后执行的回调函数
                    config.success ? config.success(res) : loop;
                    log('恭喜您分享成功！');
                },
                cancel: function(res) {
                    // 用户取消分享后执行的回调函数
                    config.cancel ? config.cancel(res) : loop;
                    log('为什么会失败!\n' + res);
                }
            };

            //分享给朋友
            wx.onMenuShareAppMessage(wxConfig);

            //分享到朋友圈
            wx.onMenuShareTimeline(wxConfig);

            //分享到QQ
            //wx.onMenuShareQQ(wxConfig);

            // 分享到QQ空间
            //wx.onMenuShareQZone(wxConfig);

            var hideMenuItemsList = config.hideMenuItemsList || [];

            // 隐藏右上角的按钮
            //wx.hideOptionMenu();
            wx.hideMenuItems({
                menuList: hideMenuItemsList // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
            });

            //异常处理
            wx.error(function(res) {
                log(res.errMsg); //打印错误消息
                //alert('wxShareInit失败')
            });
        });
    }
});
