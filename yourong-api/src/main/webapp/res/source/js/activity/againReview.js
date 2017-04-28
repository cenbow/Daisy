
var $againPicture=$("#j-Again-picture"),
    $firstPage=$("#j-first-page"),
    $jTitle=$("#j-title"),
    $jText=$("#j-text"),
    liLength=$againPicture.find('ul').find('li').length,
    liWidth=document.body.clientWidth*0.94,
    ulWidth=liWidth*liLength,
    boxHeight=liWidth*5/6.7;
//alert(screen.width)
    $againPicture.find('ul').find('li').css("width",liWidth),
    $againPicture.find('ul').find('li').css("height",boxHeight),
    $againPicture.find('ul').css("width",ulWidth);
    $againPicture.css("height",boxHeight)
//alert(screen.width)
//alert(liWidth)
//alert(boxHeight)
//alert(liWidth,ulWidth,liWidth*67/50)
//liheight=(screen.height*67)/50
//alert(screen.height)
//初始的文字
$jTitle.text("有融网获A+轮5000万融资 双方共启融资签约仪式")

//setTimeout(function(){
//    var n= index+1
//    $againPicture.find('ul').find('li').removeClass("z-active").eq(n).addClass("z-active")
//    //var timer=next()
//    //$againPicture.on('mouseenter', function () {
//    //    clearInterval(timer);
//    //}).on('mouseleave', function () {
//    //    timer = next();
//    //});
//},500)
//点击左右翻页
$againPicture.on('click','span',function(){
    var index=$againPicture.find('ul').find('li.z-active').index(),
        i;
    direction=$(this).data('click')
    //判断左右翻页
    switch(direction){
        case "left":
            i=index-1;
            break;
        case "right":
            i=index+1;
            break;
        default:
    }
//判断是否为第一页或者最后一页
    if(i>liLength-1){
        i=0
    }else if(i<0){
        i=12
    }
//根据索引值给下一个索引值加z-active
    $againPicture.find('ul').find('li').removeClass("z-active").eq(i).addClass("z-active")
    var n=$againPicture.find('ul').find('li.z-active').index(),
        ml=liWidth*(n)
    $firstPage.animate({marginLeft:-ml},300)
    //显示第几页
    $("#j-mun").text(n+1)

    //每页不同的标题文字
    var pn=n+1;
    switch (pn){
        case 1:
            $jTitle.text("有融网获A+轮5000万融资 双方共启融资签约仪式")
            break;
        case 2:
            $jTitle.text("杭州信有诚互联网金融服务有限公司董事长程国洪致辞")
            break;
        case 3:
            $jTitle.text("投资方鼎立控股集团许继石致辞")
            break;
        case 4:
            $jTitle.text("中国光华科技基金会王凤林致辞")
            break;
        case 5:
            $jTitle.text("有融网总经理吴建发布平台运营报告")
            break;
        case 6:
            $jTitle.text("浙江大学互联网金融研究院副院长李有星教授精彩演讲")
            break;
        case 7:
            $jTitle.text("《每日经济新闻》专访有融网")
            break;
        case 8:
            $jTitle.text("融资发布会现场精彩问答")
            break;
        case 9:
            $jTitle.text("现场嘉宾积极互动")
            break;
        case 10:
            $jTitle.text("嘉宾入场的全息通道 科幻大片即视感十足")
            break;
        case 11:
            $jTitle.text("有融网用户代表合影")
            break;
        case 12:
            $jTitle.text("气势恢宏的发布会现场")
            break;
        case 13:
            $jTitle.text("发布会现场表演")
            break;
        default:
            $jTitle.text("有融网获A+轮5000万融资 双方共启融资签约仪式")

    }


})
$againPicture.find('ul').find('li').find('img').css("width",'100%')



