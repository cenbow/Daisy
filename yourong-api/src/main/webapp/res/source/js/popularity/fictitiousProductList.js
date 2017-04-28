/**
 * Created by XR on 2017/3/15.
 */
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
var vm = new Vue({
    el: '#j-productList',
    data: {
        token: '',
        productData: '',
        productDetailTitle: window.encodeURI('商品详情'),
        coverShow: false,
        loginTimeOut: false
    },
    created: function () {
        var self = this
        if (os == 1) {
            Android.UpdateTitle('虚拟卡券')
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
            console.log('token:', self.token)
        }
        console.log(self.token)
    },
    methods: {
        getData: function () {
            var self = this
            $.ajax({
                url: environment.globalPath + '/yrwHtml/getAllProductList',
                data: {
                    htmlToken: self.token,
                    goodsType: '2'
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
                        self.productData = data.result
                        console.log('productData:', self.productData)
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