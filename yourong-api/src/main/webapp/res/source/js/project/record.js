/**
 * Created by lyl on 2016/11/23.
 */
define(['base', 'vue'], function (require, exports, module) {
    'use strict'
    var base = require('base');
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

    var vm = new Vue({
        el: '#j-investRecord',
        data: {
            recordList: [],
            isLoad: false,
            totalPageCount: '',
            pageNo: 1
        },
        created: function () {
            this.getList(1)
            console.log('size:', 1)
        },
        methods: {
            getList: function (pageNo) {
                var self = this;
                base.getAPI({
                    url: environment.globalPath + '/project/getProjectRecord',
                    data: {
                        pageNo: pageNo,
                        projectId: projectId
                    },
                    version: '1.9.3',
                    callback: function (data) {
                        console.log('time:', 1)
                        if (data.success) {
                            self.totalPageCount = data.result.totalPageCount
                            self.pageNo = data.result.pageNo
                            if (self.pageNo > 1) {
                                self.recordList = self.recordList.concat(data.result.data)
                            } else {
                                self.recordList = data.result.data
                            }
                            self.isLoad = false
                        } else {
                            base.xTips({content: data.resultCodes[0].msg})
                        }
                    }
                })
            },
            reloadMore: function () {
                var self = this,
                    pageNo = self.pageNo + 1
                self.isLoad = true
                this.getList(pageNo)
            }
        }
    })
})