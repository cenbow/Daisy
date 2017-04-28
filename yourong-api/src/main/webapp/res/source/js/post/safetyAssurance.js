var skyCarousel1 = $('#character-slider').carousel({
    itemWidth: 100,
    itemHeight: 100,
    enableMouseWheel: true,
    gradientOverlayVisible: false,
    gradientOverlayColor: '#fff',
    // gradientOverlaySize: 300,
    distance: 0,
    selectedItemDistance: 80,
    selectByClick: true,
    selectedItemZoomFactor: 0.8,
    unselectedItemZoomFactor: 0.4,
    navigationButtonsVisible: false,
    showPreloader: false,
    // autoSlideshow: true
    startIndex: 0
})

var data1 = [{
    titleTip: '债权甄选',
    detailMsg: '出借人准入标准、借款人信息尽调、<br>足额物权担保、专业法律监控'
},{
    titleTip: '债权复审',
    detailMsg: '债权推荐、债权准入复审、<br>法律监控'
},{
    titleTip: '债权披露',
    detailMsg: '优质债权披露、<br>第三方数据保全'
},{
    titleTip: '贷后管理',
    detailMsg: '资产监管、资金跟踪、<br>风险预警'
},{
    titleTip: '逾期保障',
    detailMsg: '项目逾期赎回、<br>项目逾期代付'
}]

var $titleTip1 = $('#j-titleTip1'),
    $detailMsg1 = $('#j-detailMsg1');

skyCarousel1.on('itemSelected.sc', function(evt) {
    $titleTip1.text(data1[evt.item.index()].titleTip)
    $detailMsg1.html(data1[evt.item.index()].detailMsg)
    $('#character-slider').find('img').each(function () {
        var tempImgUrl = $(this).attr('src')
        tempImgUrl = tempImgUrl.replace('-active.png', '.png')
        $(this).attr('src', tempImgUrl)
    })
    var imgUrl = $('#character-slider').find('li.u-transIcon').eq(evt.item.index()).find('img').attr('src')
    imgUrl = imgUrl.replace('.png', '-active.png')
    $('#character-slider').find('li.u-transIcon').eq(evt.item.index()).find('img').attr('src', imgUrl)
});

skyCarousel1.on('selectionAnimationStart.sc', function(evt) {
    $('#j-touch-left1').fadeOut()
});


// 第二个 carousel
var skyCarousel2 = $('#character-slider2').carousel({
    itemWidth: 100,
    itemHeight: 100,
    enableMouseWheel: true,
    gradientOverlayVisible: false,
    gradientOverlayColor: '#fff',
    // gradientOverlaySize: 300,
    distance: 0,
    selectedItemDistance: 80,
    selectByClick: true,
    selectedItemZoomFactor: 0.8,
    unselectedItemZoomFactor: 0.4,
    navigationButtonsVisible: false,
    showPreloader: false,
    // autoSlideshow: true
    startIndex: 0
})

var data2 = [{
    titleTip: '资料初审',
    detailMsg: '借款方基本信息核实'
},{
    titleTip: '资质认证',
    detailMsg: '第三方身份认证、第三方信用认证、<br>人行征信记录分析'
},{
    titleTip: '实地考察',
    detailMsg: '实地考察借款方家庭住所、<br>工作场所'
},{
    titleTip: '财务分析',
    detailMsg: '借款方财务状况分析'
},{
    titleTip: '资产审查',
    detailMsg: '借款方动产审查、<br>借款方不动产审查'
},{
    titleTip: '还款能力评估',
    detailMsg: '借款方综合收入/支出核实'
},{
    titleTip: '担保评估',
    detailMsg: '担保物价值评估、<br>担保人能力评估'
},{
    titleTip: '贷后管理',
    detailMsg: '资金流向跟踪、风险评估预警、<br>督促到期还款'
},{
    titleTip: '逾期保障',
    detailMsg: '逾期信息披露、专业催收管理、<br>担保物处理'
}]

var $titleTip2 = $('#j-titleTip2'),
    $detailMsg2 = $('#j-detailMsg2');

skyCarousel2.on('itemSelected.sc', function(evt) {
    $titleTip2.text(data2[evt.item.index()].titleTip)
    $detailMsg2.html(data2[evt.item.index()].detailMsg)

    $('#character-slider2').find('img').each(function () {
        var tempImgUrl = $(this).attr('src')
        tempImgUrl = tempImgUrl.replace('-active.png', '.png')
        $(this).attr('src', tempImgUrl)
    })

    var imgUrl = $('#character-slider2').find('li.u-transIcon').eq(evt.item.index()).find('img').attr('src')
    imgUrl = imgUrl.replace('.png', '-active.png')
    $('#character-slider2').find('li.u-transIcon').eq(evt.item.index()).find('img').attr('src', imgUrl)
});

skyCarousel2.on('selectionAnimationStart.sc', function(evt) {
    $('#j-touch-left2').fadeOut()
});