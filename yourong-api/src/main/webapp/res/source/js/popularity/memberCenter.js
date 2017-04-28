/**
 * Created by XR on 2017/3/7.
 */
// define(['base'], function (require, exports, module) {
'use strict'
// var base = require('base')
// 定义在安卓端还是IOS端\
var os = 3
var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //android终端
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
if (isAndroid) {
    os = 1
} else if (isiOS) {
    os = 2
}
//头像路径
Vue.filter('avatarUrl', {
    read: function (url) {
        return url ? environment.aliyunPath + url :
        environment.globalPath + '/res/img/member/avatar.png'
    }
})
new Vue({
    el: '#j-memberCenter',
    data: {
        token: '',
        current: '',
        initData: '',
        level: '',
        needSncreaseScore: '',
        popularity: '',
        membershipPrivilegesTitle: window.encodeURI('优惠兑换现金券'),
        upgradePacksTitle: window.encodeURI('升级礼包'),
        birthdayGiftTitle: window.encodeURI('生日礼包'),
        growthRecodeTitle: window.encodeURI('会员成长体系'),
        growthSystemTitle: window.encodeURI('成长记录'),
        coverShow: false,
        loginTimeOut: false
    },
    created: function () {
        var self = this
        //安卓传递title
        if (os == 1) {
            Android.UpdateTitle('会员中心')
            }
        self.token = self.getUrlParam('encryptionId').replace(/%2B/g, '+')
        self.getData()
        window.tokenCallback = function (data) {
            console.log(JSON.parse(data))
            self.token = JSON.parse(data).result
            setTimeout(function () {
                self.getData()
            }, 100)
            self.coverShow = false
            self.loginTimeOut = false
            // return appData
        }
        console.log('token:', self.token)
    },
    ready: function () {
            var self = this
        },
    methods: { 
        getData: function () {
            var self = this
            console.log(self.token)
                $.ajax({
                    url: environment.globalPath + '/yrwHtml/getMemberCenter',
                    data: {
                        htmlToken: self.token
                    },
                    type: 'POST',
                    headers: {
                        'X-Requested-Accept': 'json',
                        'Accept-Version': '2.0.0'
                    },
                    dataType: 'json',
                    success: function (data) {
                        console.log(data)
                        if (data.success) {
                            self.initData = data.result
                            self.current = data.result.vipLevel
                            self.level = data.result.vipLevel
                            self.needSncreaseScore = data.result.needSncreaseScore
                            self.popularity = data.result.score
                            var mySwiper = new Swiper('.swiper-container02', {
                                direction: 'horizontal',
                                freeMode: true,
                                slidesPerView: 'auto',
                                initialSlide: self.level,
                                centeredSlides: true,
                                slidesOffsetBefore: 40

                            })
                        } else {
                            self.coverShow = true
                            self.loginTimeOut = true
                        }

                    }

                })
        },
        memberBehavior: function (anchor, sourceId, remarks) {
            var self = this
            $.ajax({
                url: environment.globalPath + '/yrwHtml/memberBehavior',
                data: {
                    sourceId: sourceId || '',//业务ID
                    deviceParam: '',//设备参数
                    remarks: remarks || '',//备注
                    device: '',//设备
                    anchor: anchor,//锚点
                    htmlToken: self.token
                },
                type: 'POST',
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': '1.0.0'
                },
                dataType: 'json'
            })
        },
        changeCurrent: function (num) {
            this.current = num
        },
        getNewToken: function (event) {
            if (os == 1) {
                Android.GetToken('teat1', 'test2')
            } else if (os == 2) {
                $(event.currentTarget).attr('href', 'yrw-skip://invokeMethod=getToken')
            }
        },
        cancleBtn: function () {
            var self = this
            self.coverShow = false
            self.loginTimeOut = false
        },
        getUrlParam: function (name, url) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"),
                localUrl = typeof(window) !== 'undefined' ? window.location.search : url.substr(url.indexOf('?')),
                params = localUrl.substr(1).match(reg); // 匹配目标参数
            return params ? decodeURI(params[2]) : ''; // 返回参数值
        }
        }

})
// })
