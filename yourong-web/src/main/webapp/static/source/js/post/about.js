/* 关于有融 */
(function(){
    /* 设置视频阴影层的高度 */
    var $video = $('#j-video'),
        $hdCover = $('#j-hd-cover'),
        videoHeight
    var path = environment.globalPath;

    function setCoverHeight(){
        videoHeight = $('#j-video').height()
        $hdCover.css('height', videoHeight)
    }

    // // video开始加载数据了就计算一下 蒙层的高度
    // $video.on('progress', setCoverHeight)
    window.onload = setCoverHeight
    // setCoverHeight()
    /* 检测一下 IE9, 10, 11的话就用图片代替视频 */
    var $bg = $('#j-bg')
    function isIE() { //ie?
        if (!!window.ActiveXObject || "ActiveXObject" in window)
            return true;
        else
            return false;
    }
    if(isIE()){
        $bg.removeClass('z-hide')
        $video.remove()
        $hdCover.remove()

        setCoverHeight()
    } else {
        $bg.addClass('z-hide')
    }

    $(window).on('resize', setCoverHeight)
    var vm = new Vue({
        el: '#j-about',
        data: {
            urls: {
                init: path + '/about/event/ajax',     //我们的成长接口
                leaders: path + '/about/manageajax'  //领导层接口
            },
            initData: [],
            sorts: false,
            leaderList: [],
            showNO: 1,
            leaderListLen: 0,
            addAnimation: false,
            addAnimation2: false,
            isFixed: false,
            isFirefox: false
        },
        created: function () {
            var self = this
            $.xPost({
                url: self.urls.init,
                type: 'GET',
                callback: function (data) {
                    if (data.success) {
                        self.initData = data.result
                        for (var i = 0; i < data.result.length; i++) {
                            self.initData[i].link = self.initData[i].link || 'javascript:'
                        }
                        var d = self.initData, tag = null
                        d = d.map(function (item) {
                            if (item.eventYear !== tag) {
                                item.isFirst = true
                                tag = item.eventYear
                            } else {
                                item.isFirst = false
                            }
                            return item
                        })
                    } else {
                        console.log('页面初始化错误', data)
                    }
                }
            })
            $.xPost({
                url: self.urls.leaders,
                type: 'GET',
                callback: function (data) {
                    if (data.success) {
                        self.leaderList = data.result
                        self.leaderListLen = self.leaderList.length
                    } else {
                        console.log('页面初始化错误', data)
                    }
                }
            })

                $(window).scroll(function () {
                    var scrollTop = $(this).scrollTop();
                    var scrollLine = $('#j-why-us ').position().top - 200;
                    if (scrollTop >= scrollLine) {
                        self.addAnimation = true
                    }
                })
            $(window).scroll(function () {
                var scrollTop = $(this).scrollTop();
                var scrollLine = $('#contactUs ').position().top - 150;
                if (scrollTop >= scrollLine) {
                    self.addAnimation2 = true
                }
            })
            if (isFirefox = navigator.userAgent.indexOf("Firefox") > 0) {
                self.isFirefox = true
            }
        },
        methods: {
            pageOver: function (index) {
                var self = this
                if (index === 1) {
                    self.showNO++
                } else {
                    self.showNO--
                }
                if (self.showNO < 1) {
                    self.showNO = self.leaderListLen
                } else if (self.showNO > self.leaderListLen) {
                    self.showNO = 1
                }
            }
        }
    })
})()
