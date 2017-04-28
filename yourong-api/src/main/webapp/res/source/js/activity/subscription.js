/**
 * Created by XR on 2017/2/14.
 */

define(['base'], function (require, exports, module) {
    'use strict'
    var path = environment.globalPath,
        logined = $('#j-subscription').data('logined'),
        base = require('base')
    Vue.config.devtools = true

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

    window.vm = new Vue({
        el: '#j-subscription',
        data: {
            // 后端接口地址
            urls: {
                receiveReward: path + '/activity/dynamicInvoke/'
            },
            loginUrl: path + '/mstation/login?from=' + location.href,
            logined: mLogined,
            date: new Date().getTime(),
            initData: initData,
            gameStart: false,
            gameEnd: false,
            unStart: false,
            isGet: false
        },
        ready: function () {

        },
        created: function () {
            var self = this;
            if (self.date < self.initData.result.startTime) {
                self.unStart = true
                self.gameEnd = false
                self.gameStart = false
            } else if (self.date > self.initData.result.endTime) {
                self.unStart = false
                self.gameEnd = true
                self.gameStart = false
            } else {
                self.unStart = false
                self.gameEnd = false
                self.gameStart = true
            }

            if (self.logined && self.initData.result.receive && self.initData.result.status == 4) {
                base.getAPI({
                    url: path + '/activity/dynamicInvoke',
                    data: {
                        invokeMethod: 'subscription',
                        xToken: $('#xToken').val()
                    },
                    callback: function (data) {
                        if (data.success) {
                            location.reload()
                        } else {
                            // base.xTips({content: data.resultCodes[0].msg})
                            // setTimeout(function () {
                                location.reload()
                            // }, 1500)

                        }

                    }
                })
            }

            if ((self.logined && self.initData.result.receive) || !self.logined || self.gameEnd) {
                self.isGet = true
            }
            // $('meta[name="description"]').content='试一试'
            // 修改页面的描述
            //  document.querySelector('meta[name="description"]').setAttribute('content','试一试')
        },
        computed: {},
        methods: {

            // 去登陆
            goLoginPage: function () {
                location.href = this.loginUrl
                return false
            },
            showRules: function (id) {
                base.cover.show()
                $(id).show()
            },
            closeRules: function (id) {
                base.cover.hide()
                $(id).hide()
            }
        }
    })
})