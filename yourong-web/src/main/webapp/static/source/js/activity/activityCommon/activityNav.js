/** 活动页面公共导航*/
(function () {
    //获取要定位元素距离浏览器顶部的距离
    var floatingWin = $("#j-floating-window"),
        navH = floatingWin.offset().top;
    //滚动条事件
    sidebar();
    $(window).scroll(function () {
        sidebar();
    });
    function sidebar() {
        //获取滚动条的滑动距离
        var scroH = $(this).scrollTop();

        if (scroH < 689) {
            floatingWin.css({"position": "absolute", "top": 790});
        } else if (scroH > 689) {
            floatingWin.css({"position": "fixed", "top": "10%"});
        }
    }

    //置顶
    $('#j-icon-gotop').on('click',function(){
        $("html,body").animate({
            'scrollTop': '0px'
        }, 500)
    });


    //鼠标悬浮显示二维码
    floatingWin.on('mouseenter', 'a', function () {
        $(this).find('span').removeClass('f-dn');
    }).on("mouseleave", 'a', function () {
        $(this).find('span').addClass('f-dn');
    });
})();
