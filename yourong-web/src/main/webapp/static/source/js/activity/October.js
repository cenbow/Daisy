(function () {
    $('#j-total').snowfall({
        image: '/static/img/activity/october/red.png',
        flakeCount :1,
        minSize:30,
        maxSize:60,
    });
    $('#j-total').snowfall({
        image: '/static/img/activity/october/num5.png',
        flakeCount :3,
        minSize:30,
        maxSize:100,
    });
    $('#j-total').snowfall({
        image: '/static/img/activity/october/num3.png',
        flakeCount :2,
        minSize:30,
        maxSize:80,
    });

    // var base = require('base'),
    var logined = $('#j-total').data('logined')
      // path = environment.globalPath,
      // invoke = path + '/activity/dynamicInvoke',
      // hook = new AppHook(os),
      // encryptionId = base.getUrlParam('encryptionId') || ''

    window.vm = new Vue({
        el:'#j-october',
        data:{
            list:{},
            startTime:0,
            endTime:0,
            success:0,
            myInvestAmount:0,
            number:[4,5,6,7,8,9,10],
            activityStatus:0,
            logined:  logined
        },

        ready:function(){
            var self = this
            console.log(self.logined);
            $.xPost({
                url: '/activity/october/init',
                callback: function(data){
                    self.success=data.success
                    console.log(data);
                    if(data.success){
                        var result = data.result
                        self.list = result.firstTenInvestAmount
                        self.endTime = result.endTime
                        self.startTime = result.startTime
                        self.activityStatus = result.activityStatus
                        self.myInvestAmount = result.myInvestAmount || 0
                    }
                }
            })
        },
        created:function(){
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
})()
