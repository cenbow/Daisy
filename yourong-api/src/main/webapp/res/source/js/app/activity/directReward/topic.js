new Vue({
    el:'#app',
    data:{
        page: {},
        initData: {},
        isShow: true
    },
    created:function () {
        var bIsAndroid = navigator.userAgent.toLowerCase().match(/android/i) == "android";
        if (bIsAndroid) {
            Android.UpdateTitle('快投有奖规则')
        }
        
        if (this.isShow) {
            var speed = 50;

            function Marquee() {
                if (tipBlock.scrollLeft >= marqueText.scrollWidth) {
                    tipBlock.scrollLeft = 0;
                } else {
                    tipBlock.scrollLeft++;
                }
            }

            setTimeout(
                function () {
                    setInterval(Marquee, speed)
                }, 2000)
        }

        var data = $('#j-data').attr('data') || '{}'
        this.initData = JSON.parse(data)
    },
    methods:{
        closeTip: function () {
            this.isShow = false
        }
    },
    filters:{
        rewardName:{
            read:function (val) {
                var zhNum = ['一','二','三','四','五','六','七','八','九','十']
                return zhNum[val-1]+'等奖'
            }
        }
    }
})