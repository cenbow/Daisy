
(function(){
    var ReviewImg=$("#j-review-img"),
        lilength=ReviewImg.find('li').length,
        ulwidth=lilength*890;
    //计算ul宽度
    ReviewImg.css("width",ulwidth);


//鼠标悬浮停止动画效果
    var timer = scrollTimer();
    ReviewImg.on('mouseenter', function () {
        clearInterval(timer);
    }).on('mouseleave', function () {
        timer = scrollTimer();
    });


    //定时动画效果
    function scrollTimer() {
        return setInterval(function () {
            var firstli = ReviewImg.find('li').eq(0);
            firstli.animate({marginLeft: -890}, 1000, function () {
                firstli.slice(0, 890).appendTo(ReviewImg);
                firstli.css("margin-left", 0);
            });
        }, 4000)
    }
})();



