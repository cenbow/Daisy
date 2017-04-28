/**
 * @entry 公告帮助中心
 */

// 新手指导页的左右方向键点击切换图片
$('.j-help-ileft, .j-help-iright').on('click', function(){
    let $this = $(this),
        $parent = $this.parent(),
        $parentsLi = $this.parents('li'),

        step = Number($this.data('step')),
        maxstep = Number($this.data('maxstep')),
        direction = $this.data('direction')

    if(step <= maxstep){
        if(direction !== 'left'){
            if(step !== maxstep){
                step++
            } else {
                return false
            }
        } else {
            step--
        }
    } else {
        step = 1
    }

    if(step === 0 && direction === 'left'){
        return false
    }

    $this.data('step', step).siblings('i').data('step', step)
    $parent.find('.j-help-step').text(step)
    $parentsLi.find('.j-screenshot-list li').eq(step - 1).show().siblings().hide()
    $parentsLi.find('.u-help-fbig').eq(step - 1).show().siblings('p').hide()
    $parent.next().find('ol li').eq(step - 1).show().siblings().hide()
})

//跳转展开
let locUrl=location.href
if(locUrl.indexOf("newsId_")!==-1){
    let newsId = locUrl.substr(locUrl.indexOf("#newsId_"),locUrl.length)

    $(newsId).addClass("z-expand").height(625)
        .children("div").height(544)
}

//新闻公告, 网站公告，点击公告名称，下拉显示详细内容
$(".j-notice-list li>strong").on("click", function() {
    let $this = $(this),
        noticeLi = $this.parent(),
        n = noticeLi.data("clickTimes"),
        post = noticeLi.find("div")

    if (typeof (n) !== "undefined") {
        n = n === 0 ? 1 : 0
    } else {
        n = 1;
    }

    if(noticeLi.hasClass("z-expand")){
        n=0;
    }

    if (n === 1) {
        post.animate({
            height : 544
        }, 750)
        noticeLi.animate({
            height : 680
        }, 750).addClass("z-expand")
    } else {
        post.animate({
            height : 0
        }, 750)
        noticeLi.animate({
            height : 75
        }, 750).removeClass("z-expand")
        post.scrollTop(0)
    }

    noticeLi.data("clickTimes", n)

    let otherNoticeLi = noticeLi.siblings(), contentDiv

    $.each(otherNoticeLi, function(index, val) {
        contentDiv = $(this).find("div")

        n = 0

        contentDiv.animate({
            height : 0
        }, 750)

        $(this).animate({
            height : 75
        }, 750).removeClass("z-expand")

        contentDiv.scrollTop(0);
        $(this).data("clickTimes", n)
    })
})
