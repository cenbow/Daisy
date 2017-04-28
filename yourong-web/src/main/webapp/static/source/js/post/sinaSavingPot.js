//新浪存钱罐，调用fullpage.js
(function(){
/* 头部放到第一页内 */
$('.f-cb').appendTo($('#j-hd')).addClass('g-hd-fix');

/* 尾部放到第六页 */
$('.m-guide, .m-certification').remove();
$('.g-ft').appendTo($('#j-ft')).addClass('g-ft-fix');
//$('#section1,#section2,#section3,#section4,#section5,#section6').hide();

var $section1Items = $('#section1 *'),
    $section2Items = $('#section2 *'),
    $section3Items = $('#section3 *'),
    $section4Items = $('#section4 *'),
    $section5Items = $('#section5 *'),
    $section6 = $('#section6'),
    $fpNav = $('#fp-nav');
$('#fullpage').fullpage({
    //Navigation
    menu: '#menu',
    lockAnchors: false,
    anchors:['firstPage', 'secondPage', 'thridPage', 'forthPage', 'fifthPage', 'footer'],
    navigation: true,
    navigationPosition: 'right',
    slidesNavPosition: 'bottom',

    //Scrolling
    autoScrolling: true,
    fitToSection: true,
    fitToSectionDelay: 3000, // 如果页面超过了section，页面回滚到 section的延迟时间
    scrollBar: false, // 浏览器的滚动条, 这个打开就可以连着头部滚动了，不打开就不行啊
    loopBottom: false,
    loopTop: false,
    //normalScrollElements: '#element1, .element2',
    scrollOverflow: false,
    touchSensitivity: 15,
    normalScrollElementTouchThreshold: 5,

    //Design
    verticalCentered: false,
    resize : false,
    paddingTop: '0',
    paddingBottom: '0px',
    //fixedElements: '#header, .footer',

    //Custom selectors
    sectionSelector: '.section',
    slideSelector: '.slide',

    //events
    onLeave: function(index, nextIndex, direction){
        //var loadedSection = $(this);
        //
        if(nextIndex == 6) {
            $('#fp-nav').addClass('z-enter');
        }
        if(index == 6 && direction == 'up'){
            $('#fp-nav').removeClass('z-enter');
            $('.g-bd').css('background', '#d74148');
        }

        switch(nextIndex) {
            case 1:
                $section1Items.addClass('z-enter');
                break;
            case 2:
                $section2Items.addClass('z-enter');
                break;
            case 3:
                $section3Items.addClass('z-enter');
                break;
            case 4:
                $section4Items.addClass('z-enter');
                break;
            case 5:
                $section5Items.addClass('z-enter');
                $section6.css('marginTop', 0)

                // 这里做一下火狐兼容, 火狐的鼠标滚轮事件时 DOMMouseScroll
                // 第五屏滚轮往下就显示footer，往上滚动就把footer隐藏，同时需要改变第五屏的图片文字的位置
                //$('.section5').one('mousewheel DOMMouseScroll', function(e){
                //    var delta = (e.originalEvent.wheelDelta
                //        && (e.originalEvent.wheelDelta > 0 ? 1 : -1)) ||  // chrome & ie
                //        (e.originalEvent.detail && (e.originalEvent.detail > 0 ? -1 : 1));              // firefox
                //   if (delta < 0) {
                //        $('.section5 .g-wrap:eq(0)').animate({'margin-top': '-330px'}, function(){
                //            $('.g-ft').css({
                //                'marginTop': '57px'
                //            }).fadeIn();
                //        });
                //    }
                //});
                break;
            case 6:
                $('.g-bd').css('background', '#eee')
                $section6.css('marginTop', '-17px')
            default:
                break;
        }
    },

    afterLoad: function(anchorLink, index){
        if(index == 1){
            $('#fullpage').show();
            //$('#section1,#section2,#section3,#section4,#section5,#section6').show();
            $section1Items.addClass('z-enter');
        }
    }
});


})();
