define(['base'], function (require, exports, module) {
    'use strict'
    var  path = environment.globalPath,
         logined = $('#j-total').data('logined'),
         base = require('base'),
         hook = new AppHook(os),
         encryptionId = base.getUrlParam('encryptionId') || ''
    Vue.config.devtools = true

    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function (e) {e.preventDefault()})

        window.vm = new Vue({
            el: '#j-october',
            data: {
                logined: os > 2 ? logined : hook.logined,
                list: {},
                startTime: 0,
                endTime: 0,
                success:0,
                myInvestAmount:0,
                activityStatus:0
            },
            ready: function () {
                var self = this
                console.log(logined);
                      self.success = receiveData.success
                        if (receiveData.success) {
                            var result = receiveData.result
                            self.list = result.firstTenInvestAmount
                            self.endTime = result.endTime
                            self.startTime = result.startTime
                            self.activityStatus = result.activityStatus
                            self.myInvestAmount = result.myInvestAmount || 0
                        }
            },
            created: function () {
            },
            watch: {
                "list": function () {
                    var self = this
                    self.scrollUserList({
                        size: 5,
                        height: -47,
                        length: 1
                    });
                }
            },
            methods:{
                scrollUserList: function (config) {
                    $("#j-usersRankList").each(function () {
                        var _this = $(this),
                          scrollSize = _this.find("li").length;
                        if (scrollSize > config.size) {
                            setInterval(function () {
                                var scrollItems = _this.find("li:visible");
                                _this.animate({marginTop: config.height}, 700, function () {
                                    scrollItems.eq(0).appendTo(_this);
                                    _this.css("margin-top", 0);
                                });
                            }, 3000);
                        }
                    })
                }
            }
        })
})