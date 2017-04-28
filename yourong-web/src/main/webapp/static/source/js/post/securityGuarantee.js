// 安全保障

(function () {
    //导航栏点击样式切换
    $('.j-navBar').on('click','li',function(){
        $(this).addClass('z-clicked').siblings().removeClass('z-clicked')
    })

    // 债券项目风控流程图点击切换事件
    $('#j-processWrap').on('click', '.j-creditorProcess', function () {
        var $this = $(this)
        // 图片样式切换
        $('.j-creditorProcess').removeClass('z-clicked')
        $this.addClass('z-clicked')

        $('#j-processWrap').find('img').each(function () {
            var imgUrl = $(this).attr('src')
            imgUrl = imgUrl.replace('-active.png', '.png')
            $(this).attr('src', imgUrl)
        })

        var tempImgUrl = $this.find('img').attr('src')
        tempImgUrl = tempImgUrl.replace('.png', '-active.png')
        $this.find('img').attr('src', tempImgUrl)


        // 详细列表切换
        $('#j-seeDetialWrap>div').addClass('f-dn').eq($this.index()).removeClass('f-dn')
    })


    // 详细列表鼠标移上去样式添加
    $('#j-seeDetialWrap').on('mouseover', 'li', function () {
        $('#j-seeDetialWrap li').removeClass('z-clickedBg')
        $(this).addClass('z-clickedBg')
    }).on('mouseout', function () {
        $(this).removeClass('z-clickedBg')
    })




    // 一大波的hover  tooltip事件
    $('.j-hoverBlock').hover(function () {
        $(this).find('.j-toolTip').toggleClass('z-show')
    })




    // 法律保障 tab切换
    $('#j-navBar-tab').on('click', 'li', function () {
        $(this).siblings().removeClass('z-clicked').end().addClass('z-clicked')
        $('#j-textBlock-wrap>div').addClass('f-dn')
            .eq($(this).index()).removeClass('f-dn')
    })

})()