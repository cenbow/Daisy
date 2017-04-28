$('.wp-inner').fullpage({
    page: '.page',
    start: 0,
    duration: 500,

    // 为了兼容app中的头部所以drag这里设为false
    drag: true,
    loop: false,
    dir: 'v',
    change: function (e) {
        $('.u-section-content').eq(e.cur).addClass('z-enter');
        if(e.cur===14){
            $('.start').hide();
        }
        else{
            $('.start').show();
        }
    },
    beforeChange: function (e) {
    },
    afterChange: function (e) {
    },
    orientationchange: function (e) {
        /* 如果出现自动横屏就提示竖屏的体验更好 */
        if (e === 'landscape') {
            $('.landscape').show();
        } else {
            $('.landscape').hide();
        }
    }
});

$('#j-backToTop').click(function () {
    $.fn.fullpage.moveTo(0, true);
});

var section3Height = $('#j-section3').height(),
// ipad 下的 window高度和 page 高度一样，安卓不一样
    bodyHeight = window.innerHeight,
// 这是容器的高度，不包括浏览器自带的head和 foot
    pageHeight = $('#page').height(),
    marginBottomNum;

/* 这里微调第三块的图片，在ipod touch下，$('#page')的高度会高于bodyHeight，
 所以在这里特别做一下兼容，其他手机都正常
 因为在上下都有导航情况下，图片会顶头
 */
if (bodyHeight == pageHeight) {
    marginBottomNum = (pageHeight - section3Height) / 2 + (pageHeight - bodyHeight);
} else {
    marginBottomNum = (pageHeight - section3Height) / 2 + 25;
}
$('.u-section').css({
    'bottom': marginBottomNum
});