/*global define,require,$,environment,alert,confirm,console*/

define(['base', 'vue'], function (require, exports, module) {
    'use strict'

    var base = require('base')

    Vue.filter('formatName', function (value) {
        return value.split('期')[0]
    })
    Vue.filter('amountFormat', function (amount, format) {
    
        switch (format) {
            case 'split':
                return splitAmount(amount);
                break;
        }
        // 用于给数字添加千分号
        function splitAmount(num) {
            num = num + "";
            var re = /(-?\d+)(\d{3})/;
            while (re.test(num)) {
                num = num.replace(re, "$1,$2");
            }
            return num;
        }
    });

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
    })

    var transactionList = new Vue({
        el: '#j-myInvestList',
        data: {
            isCurrent: base.getUrlParam("isCurrent")-0||0,
            investList: {},
            cProjectList: {},
            orderList: {},
            cache: {
                investList: {},
                cProjectList: {},
                orderList: {}
            },
            url: {
                orderList: '/security/order/queryOrderForMember?pageNo=',
                cProjectList: '/security/transaction/queryMyRasingList?pageNo=',
                investList: '/security/transaction/queryMyTransactionList?pageNo='
            },
            version:{
                orderList:'1.3.0',
                cProjectList:'1.0.0',
                investList:'1.0.0'
            },
            pageNavi: {}
        },
        created: function () {
            // this.getList('orderList')
            // this.getList('investList')
                var tabIndex = this.isCurrent
                switch (tabIndex) {
                    case 0:
                        this.getList('investList')
                        break
                    case 1:
                        this.getList('cProjectList')
                        break
                    case 2:
                        this.getList('orderList')
                        break
                }
        },
        methods: {

            changeTab: function (i) {
                this.isCurrent = i
                if (i === 0) {
                    this.setPageNavi(this.investList.totalPageCount, this.investList.pageNo)
                }
            },
            gotoPage: function (pageNo) {
                var tabIndex = this.isCurrent
                switch (tabIndex) {
                    case 0:
                        this.getList('investList', pageNo)
                        break
                    case 1:
                        this.getList('cProjectList', pageNo)
                        break
                    case 2:
                        this.getList('orderList', pageNo)
                        break
                }
            },
            getList: function (listName, pageNo) {

                var self = this,
                    list = self.$get(listName),
                    url = self.$get('url.' + listName),
                    version=self.$get('version.'+listName),
                    page = pageNo || 1,
                    cacheTag = 'cache.' + listName + '.p' + page,
                    cacheData = self.$get(cacheTag)

                if (cacheData) {
                    self.$set(listName, cacheData)
                    list = cacheData
                }

                if (list.data && list.pageNo === page) {

                    self.setPageNavi(list.totalPageCount, list.pageNo)
                }
                else {
                    base.getAPI({
                        url: environment.globalPath + url + page,
                        version:version,
                        callback: function (data) {

                            if (data.success) {
                                var result = data.result
                                self.$set(listName, result)


                                if (!self.$get(cacheTag)) {
                                    self.$set(cacheTag, result)
                                }
                                window.scrollTo(0,0)
                                self.setPageNavi(result.totalPageCount, result.pageNo)
                            }else{
                                alert(data.resultCodes[0].msg)
                            }
                        }
                    })
                }
            },
            cancleOrder: function (id) {
                var orderId = $(id).attr('orderId');
                if (confirm('确定要取消订单吗?')) {
                    //取消订单接口
                    base.getAPI({
                        url: environment.globalPath + '/security/order/cancelOrder',
                        data: {orderId: orderId},
                        callback: function (data) {
                            if (data.success) {
                                location.reload();
                            } else {
                                alert(data.resultCodes[0].msg);
                            }
                        }
                    });
                }
            },
            isTransfer:function () {
                alert('转让项目订单支付请前往APP或电脑端')
            },
            //分页
            setPageNavi: function (totalPageCount, pageNo) {

                var countArray = []

                if (totalPageCount > 7) {

                    if (pageNo <= totalPageCount - 3) {

                        if (pageNo <= 4) {

                            for (var i = 1; i <= 7; i++) {
                                //利用push方法将页码一个个放入countArray数组
                                countArray.push({'index': i})
                            }
                        } else {

                            for (var l = pageNo - 3; l <= pageNo + 3; l++) {
                                //利用push方法将页码一个个放入countArray数组
                                countArray.push({'index': l})
                            }
                        }
                    } else {

                        for (var k = pageNo - 3; k <= totalPageCount; k++) {
                            countArray.push({'index': k})
                        }
                    }
                }
                else {
                    for (var j = 1; j <= totalPageCount; j++) {
                        countArray.push({'index': j})
                    }
                }

                this.pageNavi = {
                    list: countArray,
                    pageNo: pageNo,
                    totalPageCount: totalPageCount
                }
            }
        }
    })
})

