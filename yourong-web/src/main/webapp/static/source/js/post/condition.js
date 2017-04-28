(function () {

    var path = environment.globalPath;

    var vm = new Vue({
        el: '#j-officalNotice',
        data: {
            urls: {
                init: path + '/article/newsajax',
            },
            categoryId: 1,
            genre: null,
            currentPage: 1,
            init: {},
            show: false,
            typeText: '全部类型',
            aferPage: [],
            isLoading: false,
            noticeKind: ['', '银行公告', '活动公告', '其他公告'],
            locUrl: ''
        },
        watch: {
            init: function () {
                var self = this
                if (self.locUrl != '-1') {
                    var height = $('#j-notice-list').find('#newsId_' + self.locUrl).children("div").find('span').height(),
                        largeHeight = height + 160
                    $('#j-notice-list').find('#newsId_' + self.locUrl).addClass("z-expand").children("div").height(height)
                    $('#j-notice-list').find('#newsId_' + self.locUrl).height(largeHeight)
                }
            }
        },
        created: function () {
            var self = this
            var aferPage = self.aferPage
            var locationHash = location.hash,
                locationUrl = locationHash.split('&')
            if (locationUrl[0] == '#notice') {
                //跳转展开
                if (locationUrl[1]) {
                    self.condition(1, null, 1, true)
                    self.locUrl = locationUrl[1].split('_')[1]
                } else {
                    self.condition(1, null, 1)
                }
            } else if (locationUrl[0] == '#report') {
                self.condition(2, null, 1)
            }
            $(window).scroll(function () {
                var scrollTop = $(this).scrollTop();
                var scrollHeight = $(document).height();
                var windowHeight = $(this).height();
                if (scrollTop + windowHeight == scrollHeight) {
                    self.page++
                    self.isLoading = true
                    $.xPost({
                        url: self.urls.init,
                        type: 'GET',
                        data: {
                            'categoryId': self.categoryId,
                            'genre': self.genre,
                            'currentPage': self.page
                        },
                        callback: function (data) {
                            if (data.success && data.result.data) {
                                self.isLoading = false
                                self.aferPage = data.result.data
                                self.aferPage.forEach(function (obj) {
                                    self.init.push(obj)
                                })
                            } else {
                                console.log('页面初始化错误', data)
                            }
                        }
                    })
                }
            });
        },
        methods: {
            condition: function (categoryId, genre, page, sign) {
                var self = this
                self.categoryId = categoryId,
                    self.genre = genre,
                    self.page = page
                $.xPost({
                    url: self.urls.init,
                    type: 'GET',
                    data: {
                        'categoryId': self.categoryId,
                        'genre': self.genre,
                        'currentPage': self.page
                    },
                    callback: function (data) {
                        if (data.success) {
                            self.init = data.result.data
                        } else {
                            console.log('页面初始化错误', data)
                        }
                    }
                })
                if (!sign) {
                    if (self.categoryId === 1) {
                        window.location.hash = '#notice'
                    } else {
                        window.location.hash = '#report'
                    }
                }
            }
        }
    })

    $("#j-notice-list").on("click", 'li strong', function () {
        var $this = $(this),
            noticeLi = $this.parent(),
            n = noticeLi.data("clickTimes"),
            post = noticeLi.find("div"),
            height = $this.siblings('div').children('span').height(),
            heightSmall = height + 20,
            heightLarge = height + 160
        console.log('height', height)
        if (typeof (n) !== "undefined") {
            n = n === 0 ? 1 : 0
        } else {
            n = 1;
        }

        if (noticeLi.hasClass("z-expand")) {
            n = 0;
        }

        if (n === 1) {
            post.animate({
                height: heightSmall
            }, 750)
            noticeLi.animate({
                height: heightLarge
            }, 750).addClass("z-expand")
        } else {
            post.animate({
                height: 0
            }, 750)
            noticeLi.animate({
                height: 75
            }, 750).removeClass("z-expand")
            post.scrollTop(0)
        }

        noticeLi.data("clickTimes", n)

        var otherNoticeLi = noticeLi.siblings(), contentDiv

        $.each(otherNoticeLi, function (index, val) {
            contentDiv = $(this).find("div")

            n = 0

            contentDiv.animate({
                height: 0
            }, 750)

            $(this).animate({
                height: 75
            }, 750).removeClass("z-expand")

            contentDiv.scrollTop(0);
            $(this).data("clickTimes", n)
        })
    })
})()

