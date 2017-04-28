/**
 * Created by XR on 2017/1/6.
 */
define(['zepto', 'base', 'template'], function (require, exports, module) {
    'use strict'
    var base = require('base'),
        path = environment.globalPath;

    new Vue({
        el: '#j-gainPopularity',
        data: {
            token: $('#j-token').attr('htmlToken'),
            appData: '',
            sixGiftTitle: window.encodeURI('新手六重礼'),
            inviteTitle: window.encodeURI('邀请有礼'),
            marathonTitle: window.encodeURI('友情岁月'),
            quadrupleGiftTitle: window.encodeURI('抢标五重礼'),
            popRedPackageTitle: window.encodeURI('投资分享红包')
        },
        created: function () {
            var self = this
            console.log(window.location.href)
            // this.getArguments('encryptionId', window.location.href)
            console.log(self.token)
            window.tokenCallback = function (data) {
                console.log(JSON.parse(data))
                self.appData = JSON.parse(data)
                self.token = JSON.parse(data).result
                console.log('token:', self.token)
                // return appData
            }
        },
        methods: {
            //获取参数
            getArguments: function (name, url) {
                var self = this
                var localUrl = url,
                    reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"),
                    argv = localUrl.substr(1).match(reg);
                self.token = argv ? decodeURI(argv[2]) : '';
                console.log('token:', self.token)
                // return argv ? decodeURI(argv[2]) : ''
            },
            MemberBehavior: function (anchor) {
                var self = this
                // alert('token:'+self.token)
                base.memberBehavior(anchor, self.token)
            },
            gotoLogin: function (event) {
                if (hook.logined) {
                    // alert('logined:'+true)
                } else {
                    hook.login($(event.currentTarget))
                }
               
            }

        }
    })
})