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

var hook = new AppHook(os)
Vue.filter('avatarUrl', {
    read: function (url) {
        return url ? environment.aliyunPath + url : ''
    }
})
var vm = new Vue({
    el: '#j-productDetail',
    data: {
        token: '',
        goodId: $('#j-token').attr('goodId'),
        productData: '',
        levelNeed: '',
        btnText: [
            '立即兑换',
            '人气值不足',
            '投资后才可以兑换哦',
            '已兑完'
        ],
        coverShow: false,
        surePayTip: false,
        exchangeSuccess: false,
        inputAddress: false,
        method: '',
        disabled: false,
        isWrong: false,
        address: '',
        detailAddress: '',
        province: '',
        city: '',
        county: '',
        receiver: '',
        mobile: '',
        isMobile: true,
        reChangeMobile: '',
        isError: false,
        errorText: '网络拥挤，过会再来哦',
        exchangeRecordTitle: window.encodeURI('兑换记录'),
        finalGoodsDes: '',
        imageList: '',
        loginTimeOut: false,
        statusFlag: '',
        provs_data: '',
        citys_data: '',
        dists_data: ''
    },
    created: function () {
        var self = this
        self.token = self.getUrlParam('encryptionId').replace(/%2B/g, '+')
        self.getData()
        self.getAdress()
        if (os == 1) {
            Android.UpdateTitle('商品详情')
        }
        console.log(self.token)
        window.tokenCallback = function (data) {
            console.log(JSON.parse(data))
            self.token = JSON.parse(data).result
            setTimeout(function () {
                self.getData()
            }, 100)
            self.loginTimeOut = false
            self.coverShow = false
            console.log('token:', self.token)
            // return appData
        }
        window.hookCallback = function (data, method) {
            // this.$root.initData = data
            console.log('hookback:', data)
            console.log('success:', data.success)
            if (self.method == 3) {
                if (data.success) {
                    self.exchangeSuccess = true
                    self.surePayTip = false
                    //计算剩余的人气值数目
                    // initData.result.popularity = (initData.result.popularity - 0) - (self.productData.discountPrice - 0)
                } else {
                    //兑换失败时的提醒语
                    self.isError = true

                    if (data.resultCodes[0].code == 10001) {

                    } else if (data.resultCodes[0].code == 90058) {
                        self.errorText = '活动即将开始';
                    } else if (data.resultCodes[0].code == 90702) {
                        self.errorText = '活动已结束';
                    } else {
                        self.errorText = data.resultCodes[0].msg

                    }
                    setTimeout(function () {
                        self.isError = false
                    }, 2000)
                }
            } else {

                // this.$router.go({path: '/exchangeRecord?title=' + window.encodeURI('兑换记录')})
            }

        }


    },
    ready: function () {
        var self = this
        setTimeout(function () {
            var mySwiper = new Swiper('.swiper-container', {
                direction: 'horizontal',
                loop: true,
                autoplay: 2000,
                // 如果需要分页器
                pagination: '.swiper-pagination'

            })
        }, 100)

    },
    methods: {
        getData: function () {
            var self = this
            $.ajax({
                url: environment.globalPath + '/yrwHtml/getProductDetail',
                data: {
                    htmlToken: self.token,
                    goodId: self.goodId
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
                        self.imageList = data.result.imageList
                        self.levelNeed = data.result.levelNeed
                        self.statusFlag = data.result.goodStatusFlag
                        if (self.statusFlag == 4) {
                            self.statusFlag = 3
                        }
                        console.log(self.goodStatusFlag)
                        self.splitMsg(data.result.goodsDes)
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
                Android.GetToken('teat1', 'test2')
            } else if (os == 2) {
                $(event.currentTarget).attr('href', 'yrw-skip://invokeMethod=getToken')
            }
        },
        requestData: function (invokeMethod, event, type) {
            var self = this,
                encodeAddress = window.encodeURI(self.address),
                encodeReceiver = window.encodeURI(self.receiver),
                encodeMobile = window.encodeURI(self.mobile),
                encodeDetailAddress = window.encodeURI(self.detailAddress),
                encodeProvince = window.encodeURI($('#province').val()),
                encodeCity = window.encodeURI($('#city').val()),
                encodeCounty = window.encodeURI($('#county').val())
            var args = {
                recordList: 'args_currentPage_1_integer_1',
                productOrder: [
                    'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice, /*兑换的是一般的虚拟卡券*/
                    'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice + '&args_rechargeCard_1_string_' + self.reChangeMobile, /*充值类型的虚拟卡券*/
                    'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice + '&args_address_1_string_' + encodeDetailAddress + '&args_receiver_1_string_' + encodeReceiver + '&args_mobile_1_string_' + encodeMobile + '&args_province_1_string_' + encodeProvince + '&args_city_1_string_' + encodeCity + '&args_district_1_string_' + encodeCounty, /*兑换的商品是实物*/
                    'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice /*兑换的是特惠商品*/
                ]
            }
            self.method = invokeMethod;
            if (type == 'recordList') {
                hook.getEvent(invokeMethod + '&isNeedRealName=0&' + args[type], $(event.currentTarget), 1)
            } else {
                hook.getEvent(invokeMethod + '&isNeedRealName=0&' + args[type][self.productData.goodsType - 1], $(event.currentTarget), 1)
            }

        },
        submitBtn: function (id, event) {
            var self = this;
            if ($(id).hasClass('z-disabled')) {
                return false
            } else {
                if (self.productData.goodsType == 2 && self.productData.rechargeType == 1) {
                    self.limitMobile('#j-rechargeMobile')
                }
                if (self.isMobile) {
                    if (self.productData.goodsType == 3) {
                        //选取省市区
                        self.getArea()
                        self.inputAddress = true

                    } else {
                        self.surePayTip = true
                    }
                } else {
                    return false
                }
                self.coverShow = true

            }

        },
        toSubmitOrder: function (invokeMethod, event, type) {
            var self = this
            if (self.address != '' && self.receiver != '' && self.mobile != '' && self.detailAddress != '') {
                this.surePayTip = true
                self.inputAddress = false
            } else {
                return false
            }

        },
        limitInput: function (address, receiver, mobile, detailAddress) {

            if (address != '' && receiver != '' && mobile != '' && detailAddress != '') {
                console.log('true')
                return true
            } else {
                console.log('false')
                return false
            }
        },
        submitOrder: function (invokeMethod, event, type) {
            var self = this
            self.requestData(invokeMethod, event, type)

        },
        cancleBtn: function (id) {
            var self = this
            self.surePayTip = false
            self.coverShow = false
            self.exchangeSuccess = false
            self.inputAddress = false
        },
        limitMobile: function (id) {
            var reg = /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/;
            var mobileReg = /^1(3[4-9]|5[012789]|8[23478]|4[7]|7[8])\d{8}$/
            var val = $(id).val(),
                self = this
            if (!reg.test(val)) {
                self.phoneErrorText = '请输入正确的手机号码'
                self.isWrong = true
                setTimeout(function () {
                    self.isWrong = false
                }, 2000)
                self.isMobile = false
            } else {
                if (!mobileReg.test(val)) {
                    self.phoneErrorText = '抱歉，当前仅支持兑换移动话费'
                    self.isWrong = true
                    setTimeout(function () {
                        self.isWrong = false
                    }, 2000)
                    self.isMobile = false
                } else {
                    self.isMobile = true
                }

            }
            $('#j-submitBtn').css('position', 'fixed')
        },
        hideChangeBtn: function (id) {
            $(id).css('position', 'static')
        },
        splitMsg: function (msg) {
            var self = this
            if (msg) {
                self.finalGoodsDes = msg.replace(/#/g, ' ').replace('^', '\'').replace('$', '\"').replace('：', ':').split('&')
            } else {

            }
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
        getUrlParam: function (name, url) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"),
                localUrl = typeof(window) !== 'undefined' ? window.location.search : url.substr(url.indexOf('?')),
                params = localUrl.substr(1).match(reg); // 匹配目标参数
            return params ? decodeURI(params[2]) : ''; // 返回参数值
        },
        getArea: function () {
            $.ajax({
                url: environment.globalPath + '/security/find/getPopularityArea',
                data: {
                    // xToken:'c6e0da7e-5b0a-4150-8071-6906a5ae3f61'
                },
                type: 'GET',
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': '1.0.0'
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        self.provs_data = data.result.data[0].provs_data
                        self.citys_data = data.result.data[1].citys_data
                        self.dists_data = data.result.data[2].dists_data
                        setTimeout(function () {
                            var area = new LArea();
                            area.init({
                                'trigger': '#j-address', //触发选择控件的文本框，同时选择完毕后name属性输出到该位置
                                'province': '#province', //选择完毕后id属性输出到该位置
                                'city': '#city', //选择完毕后id属性输出到该位置
                                'county': '#county', //选择完毕后id属性输出到该位置
                                'keys': {
                                    id: 'code',
                                    name: 'text'
                                }, //绑定数据源相关字段 id对应valueTo的value属性输出 name对应trigger的value属性输出
                                'type': 2, //数据源类型
                                'data': [provs_data, citys_data, dists_data] //数据源
                            });
                        }, 500)
                    }


                }

            })
        },
        getAdress: function () {
            var self = this
            console.log('token:', self.token)
            $.ajax({
                url: environment.globalPath + '/yrwHtml/queryOrderDelivery',
                data: {
                    htmlToken: self.token
                },
                type: 'POST',
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': '1.0.0'
                },
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        self.mobile = data.result.mobile
                        self.receiver = data.result.receiver
                        self.detailAddress = data.result.address
                        self.province = data.result.province
                        self.city = data.result.city
                        self.county = data.result.district
                        self.address = data.result.province + '、' + data.result.city + '、' + data.result.district

                        console.log(self.mobile, self.receiver, self.detailAddress, self.province, self.city, self.county)
                    }

                }

            })
        },
        bindAndroidScorll: function (id) {
            $(id).css('top', '5%')
        },
        getNomal: function (id) {
            $(id).css('top', '20%')
            $('#j-submitBtn').css('position', 'fixed')
        }
    }
})