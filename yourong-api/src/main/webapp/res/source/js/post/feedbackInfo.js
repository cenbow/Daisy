/**
 * Created by lyl on 2016/9/23.
 */
define(['base'], function (require, exports, module) {
    'use strict'
    var base = require('base'),
        path = environment.globalPath
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
    new Vue({
        el:'#feedbackInfo',
        data:{
            isLoad: false,
            initData:{},
            memberId: $('#j-selfInfo').attr('memberId'),
            totalPageCount: '',
            pageNo: 1
        },
        created:function () {
            this.getList(1)
        },
        methods:{
            getList:function(pageNo){
                var self=this;
                base.getAPI({
                    url: path + '/article/queryFeedbackList',   
                    version:'1.8.0',
                    data:{
                        pageNo:pageNo,
                        memberId:self.memberId
                    },
                    callback:function (data) {
                        if(data.success){
                            self.totalPageCount = data.result.totalPageCount
                            self.pageNo = data.result.pageNo
                            if (self.pageNo > 1) {
                                self.initData = self.initData.concat(data.result.data)
                            } else {
                                self.initData = data.result.data
                            }
                        }else{
                            base.xTips({content:data.resultCodes[0].msg})
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