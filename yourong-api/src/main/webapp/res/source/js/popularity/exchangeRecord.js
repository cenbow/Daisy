/**
 * Created by XR on 2017/3/15.
 */
'use strict'
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
Vue.filter('avatarUrl', {
    read: function (url) {
        return url ? environment.aliyunPath + url :
        environment.globalPath + '/res/img/member/avatar.png'
    }
})
var vm = new Vue({
    el: '#j-exchangeRecord',
    data: {
        token: '',
        pageNo: 1,
        isLoad: false,
        exchangeRecord: '',
        totalPageCount: '',
        exchangeInfoTitle: window.encodeURI('订单详情'),
        coverShow: false,
        loginTimeOut: false
    },
    created: function () {
        var self = this
        if (os == 1) {
            Android.UpdateTitle('兑换记录')
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
            // console.log('token:', self.token)
            // return appData
        }
        console.log('url:', window.location.href)
        console.log('token:', self.getUrlParam('encryptionId'))
        console.log('token01:', self.token)
    },
    methods: {
        getData: function () {
            var self = this
            console.log('htmlToken:', self.token)
            self.getAPI({
                url: environment.globalPath + '/yrwHtml/getExchangeRecord',
                data: {
                    htmlToken: self.token,
                    pageNo: self.pageNo
                },
                version: '2.0.0',
                callback: function (data) {
                    if (data.success) {
                        self.exchangeRecord = data.result.data
                        self.pageNo = data.result.pageNo
                        self.totalPageCount = data.result.totalPageCount
                        console.log(data)
                    } else {
                        self.coverShow = true
                        self.loginTimeOut = true
                    }

                }
            })
        },
        getNewToken: function (event) {
            if (os == 1) {
                Android.GetToken('test1', 'test2')
            } else if (os == 2) {
                $(event.currentTarget).attr('href', 'yrw-skip://invokeMethod=getToken')
            }
        },
        loadMoreData: function (invokeMethod, event) {
            var self = this,
                pageNo = self.pageNo + 1
            console.log('pageNo:', pageNo)
            self.getAPI({
                url: environment.globalPath + '/yrwHtml/getExchangeRecord',
                data: {
                    htmlToken: self.token,
                    pageNo: pageNo
                },
                version: '2.0.0',
                callback: function (data) {
                    console.log('nextData:', data)
                    self.exchangeRecord = self.exchangeRecord.concat(data.result.data)
                    self.pageNo = data.result.pageNo
                    self.isLoad = false
                }
            })
            self.isLoad = true
        },
        getAPI: function (options) {
            console.log(typeof (options))
            if (typeof(options) === 'object') {
                var data = {};
                if (options.data) {
                    if ($.isArray(options.data)) {
                        $.each(options.data, function (index, item) {
                            data[item.name] = item.value;
                        });
                    } else {
                        data = options.data;
                    }
                }
                $.ajax({
                    url: options.url,
                    data: data,
                    type: options.type || 'POST',
                    headers: {
                        'X-Requested-Accept': 'json',
                        'Accept-Version': options.version || '1.0.0'
                    },
                    dataType: options.dataType || 'json',
                    success: function (data) {
                        if (typeof(options.ids) === 'object') {
                            options.callback(data, options.ids);
                        } else {
                            options.callback(data);
                        }
                    }
                });
            } else {
                throw new Error('options must be an object');
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

