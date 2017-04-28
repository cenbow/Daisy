(function () {
    var path = environment.globalPath
    var vm = new Vue({
        el: '#j-join',
        data: {
            urls: {
                jobTypes: path + '/about/job/category',
                jobsContent: path + '/about/joinajax'
            },
            init: {},
            jobs: {},
            jobkinds: 1,
            currentPage: 1,
            categoryId: '',
            unfold: false,
            aferPage: []
        },
        created: function () {
            var self = this
            $.xPost({
                url: self.urls.jobTypes,
                type: 'GET',
                callback: function (data) {
                    if (data.success) {
                        self.init = data.result
                        self.jobTypes(self.init[0].id)
                    } else {
                        console.log('页面初始化错误', data)
                    }
                }
            })
            $(window).scroll(function () {
                var scrollTop = $(this).scrollTop();
                var scrollHeight = $(document).height();
                var windowHeight = $(this).height();
                if (scrollTop + windowHeight == scrollHeight) {
                    self.currentPage++
                    $.xPost({
                        url: self.urls.jobsContent,
                        type: 'GET',
                        data: {
                            'categoryId': self.categoryId,
                            'currentPage': self.currentPage
                        },
                        callback: function (data) {
                            if (data.success) {
                                self.aferPage = data.page.data
                                self.aferPage.forEach(function (obj) {
                                    self.init.push(obj)
                                })
                            } else {
                                console.log('页面初始化错误', data)
                            }
                        }
                    })
                }
            })



        },
        methods: {
            jobTypes: function (id) {
                var self = this
                self.categoryId = id
                $.xPost({
                    url: self.urls.jobsContent,
                    type: 'GET',
                    data: {
                        'categoryId': self.categoryId,
                        'currentPage': self.currentPage
                    },

                    callback: function (data) {
                        if (data.success) {
                            self.jobs = data.page.data
                        } else {
                            console.log('页面初始化错误', data)
                        }
                    }
                })
            },
        }
    })
    $('#j-job-centent').on("click", 'h2', function () {
        if ($(this).parent().find('.u-demand').length) {
            if ($(this).parent().find('.u-demand').hasClass('f-dn')) {
                $(this).parent().find('i').text('-')
                $(this).parent().siblings().find('i').text('+')
                $(this).parent().find('.u-demand').removeClass('f-dn')
                $(this).parent().siblings().find('.u-demand').addClass('f-dn')
                $(this).parent().find('i').addClass('z-disabled')
                $(this).parent().siblings().find('i').removeClass('z-disabled')
            } else {
                $(this).parent().find('i').text('+')
                $(this).parent().find('.u-demand').addClass('f-dn')
                $(this).parent().find('i').removeClass('z-disabled')
            }
        }
    })
})()
