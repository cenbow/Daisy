/**
 * Created by Administrator on 2016/8/26.
 */
$(function () {
    var isCodeControl = false;
    var menuTitles =  $("#slideRightNavLsit .u-nav-item");
    var frNav = $("#frNav");

    /*右侧导航选中高亮*/
    var rightSlideActive = function rightSlideActive(index){
        menuTitles.removeClass("z-current")
            .eq(index)
            .addClass("z-current");
    }

    /*流程面板各模块选中高亮*/
    var flowPanelActive = function flowPanelActive(obj,index){
        //流程面板底部导航选中
        obj.find('.u-carousel-foot li')
            .removeClass('z-current')
            .eq(index)
            .addClass('z-current');
        //流程面板图片内容显示
        obj.find('.u-carousel-main li')
            .addClass('z-dn')
            .eq(index)
            .removeClass('z-dn');
        //流程面板头部步骤选中
        obj.find('.m-step-contc li')
            .removeClass('z-ds')
            .eq(index)
            .addClass('z-ds');
    }

    /*左侧导航顶级选项高亮,并显示相应面板*/
    $('li.u-menu-btn a').on('click',function (e) {
        var dropDown = $(this).parent().next();
        var $id = $(this).parent().data('target');
        $('.u-menu-hd').removeClass('z-current');
        $(this).addClass('z-current');
        $('.u-menu-dropdown').not(dropDown).slideUp('slow');
        dropDown.slideToggle('slow');
        if ($id){
            $('.m-subc-wrap').removeClass('z-ds').filter('#'+$id).addClass('z-ds');
            resetPanel($id);
        }else {
            dropDown.find('li[data-target]').each(function(){
                var $itemId = $(this).data('target');
                if ($(this).hasClass('z-current')) {
                    $('.m-subc-wrap').removeClass('z-ds').filter('#'+$itemId).addClass('z-ds');
                    resetPanel($itemId);
                }
            });
        }
        e.preventDefault();
    })

    /*流程体验面板重置*/
    var resetPanel = function resetPanel(id){
        var $parentDiv = $('#'+id);
        flowPanelActive($parentDiv,0);
    }

    /*左侧导航二级选项高亮,并显示相应面板*/
    $('li.u-menu-dropdown li').on('click',function(){
        var $target = $(this).data('target');
        resetPanel($target);
        $('li.u-menu-dropdown li').removeClass('z-current');
        $(this).addClass('z-current');
        $('.m-subc-wrap').removeClass('z-ds').filter('#'+$target).addClass('z-ds');
    })

    /*流程体验模块--图片底部导航*/
    $('#carouselFoot li').on('click',function(){
        var $index = $(this).index();
        var $parent = $(this).parent().parent();
        flowPanelActive($parent,$index);
    })

    /*流程体验模块--点击图片，进入下一步*/
    $('.u-itme-bg').on('click',function(){
        var $this = $(this);
        var $imgIndex = $this.index();
        var $len = $this.parent().find('.u-itme-bg').length;
        var $pointIndex = $imgIndex+1;
        if ($imgIndex === $len-1) {
            return false;
        }else {
            var $wrapDiv = $this.closest('.m-subc-wrap');
            flowPanelActive($wrapDiv,$pointIndex);
        }
    })

    /*流程体验模块--图片顶部进度条*/
    $('.u-step-btn').on('click',function(){
        var $index = $(this).index();
        var $parentDiv = $(this).closest('.m-subc-wrap');
        flowPanelActive($parentDiv,$index);
    })

    /*右侧导航*/
    $('#slideRightNavLsit li').on('click',function(){
        var $index = $(this).index();
        var $len = $('#slideRightNavLsit li').length-1;

        rightSlideActive($index);

        if ($index === $len) {
            isCodeControl = true;
            $("html,body").animate({
                'scrollTop': '0px'
            }, 500,function(){
                isCodeControl = false;
                rightSlideActive(0);
                updateNavItem();
            })
        }else {
            isCodeControl = true;
            $("html,body").animate({
                scrollTop:$('#conTentNavList .u-by-cx').eq($index).offset().top
            },500,function(){
                isCodeControl = false;
                updateNavItem();
            });
        }
    })

    var updateNavItem = function updateNavItem(){
        //获取滚动条的滑动距离
        var top = $(document).scrollTop();          //定义变量，获取滚动条的高度
        var menu = $("#conTentNavList");            //定义变量，抓取#menu
        var items = menu.find(".u-by-cx");          //定义变量，查找.item
        var curId = "";                              //定义变量，当前所在的楼层item #id

        items.each(function(index){
            var item  =  $(this);
            var itemTop = item.offset().top-150;        //定义变量，获取当前的top偏移量
            $(this).attr("data-id",index);
            if (top >= itemTop) {
                curId =  item.attr("data-id");
            }else{
                return false;
            }
        });

        //根据取得的id设置相应属性
        if(curId && !menuTitles.eq(curId).hasClass("z-current")){
            rightSlideActive(curId);
        }
    }

    /*滚动到相应模块，右侧导航响应*/
    $(window).scroll(function(){
        var navTop = $(this).scrollTop();

        navTop < 542 ? frNav.css({
            position: "absolute",
            top: 542
        }) : navTop > 542 && (frNav.css({
            position: "fixed",
            top: "10%"
        }), navTop > 1973 && frNav.css({
            position: "absolute",
            top: 2018
        }))

        if(isCodeControl) return;
        updateNavItem();
    });
});