define(['base'], function (require, exports, module) {
    'use strict'
    var path = environment.globalPath,
        base = require('base')
    var vm = new Vue({
        el: '#j-about',
        data: {
            urls: {
                leader: path + '/about/manage/ajax',  //管理层接口
                history: path + '/about/event/ajax'     //有融发展历程接口
            },
            historyLine: {},
            leaders: [],
            leaderNo: 0,
            aboutYRTitle: window.encodeURI('关于有融'),
            safeTitle: window.encodeURI('安全保障'),
            cooperatorTitle: window.encodeURI('合作伙伴'),
            honorTitle: window.encodeURI('资质荣誉'),
        },
        created: function () {
            var self = this

            var swiperH = new Swiper('.j-swiper-events', {
                pagination: '.swiper-pagination-h1',
                observer: true,
                observeParents: true
            });
            base.getAPI({
                url: self.urls.history,
                callback: function (data) {
                    if (data.success) {
                        var d = data.result, tag = null, arr = [], yearList = {}
                        d.forEach(function (obj) {
                            if (obj.eventYear !== tag) {
                                tag = obj.eventYear
                                arr = []
                            }
                            arr.push(obj)
                            yearList[tag] = arr
                        })
                        self.historyLine = yearList
                    } else {
                        console.log('取数据失败')
                    }
                }
            })
            base.getAPI({
                url: self.urls.leader,
                callback: function (data) {
                    if (data.success) {
                        self.leaders = data.result
                    } else {
                        console.log('取数据失败')
                    }
                }
            })
        }
    })
})
