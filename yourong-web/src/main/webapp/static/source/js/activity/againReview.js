
var $againPicture=$("#j-Again-picture"),
    $firstPage=$("#j-first-page"),
    liLength=$againPicture.find('ul').find('li').length,
    ulWidth=$againPicture.find('ul').css("width",liLength*890),
    $jTitle=$("#j-title"),
    $jText=$("#j-text");
    //初始的文字
    $jTitle.text("有融网获A+轮5000万融资 双方共启融资签约仪式")
    $jText.text("2016年6月28日，有融网A+轮融资发布会在浙江杭州隆重举办。")

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
        ml=890*(n)
        $firstPage.animate({marginLeft:-ml},300)
        //显示第几页
        $("#j-mun").text(n+1)

        //每页不同的标题文字
        var pn=n+1;
        switch (pn){
            case 1:
                $jTitle.text("有融网获A+轮5000万融资 双方共启融资签约仪式")
                $jText.text("2016年6月28日，有融网A+轮融资发布会在浙江杭州隆重举办。")
                break;
            case 2:
                $jTitle.text("杭州信有诚互联网金融服务有限公司董事长程国洪致辞")
                $jText.text("有融网此次获A+轮融资是平台迎来新一轮发展的契机。")
                break;
            case 3:
                $jTitle.text("投资方鼎立控股集团许继石致辞")
                $jText.text("鼎立控股集团将与有融网携手在资产端开拓等方面开展全方位多角度深层次的战略合作。")
                break;
            case 4:
                $jTitle.text("中国光华科技基金会王凤林致辞")
                $jText.text("有融网为打造互联网金融健康生态圈的梦想而拼博的状态，是在促进整个互金行业向好发展。")
                break;
            case 5:
                $jTitle.text("有融网总经理吴建发布平台运营报告")
                $jText.text("平台上线一年多以来，在团队的努力以及广大用户的支持下，实现了稳步发展。")
                break;
            case 6:
                $jTitle.text("浙江大学互联网金融研究院副院长李有星教授精彩演讲")
                $jText.text("有融网通过与浙大的产学研合作，共探行业信息披露及风险控制研究，为推动行业的合规发展提供借鉴性研究成果。")
                break;
            case 7:
                $jTitle.text("《每日经济新闻》专访有融网")
                $jText.text(" ")
                break;
            case 8:
                $jTitle.text("融资发布会现场精彩问答")
                $jText.text(" ")
                break;
            case 9:
                $jTitle.text("现场嘉宾积极互动")
                $jText.text(" ")
                break;
            case 10:
                $jTitle.text("嘉宾入场的全息通道 科幻大片即视感十足")
                $jText.text(" ")
                break;
            case 11:
                $jTitle.text("有融网用户代表合影")
                $jText.text(" ")
                break;
            case 12:
                $jTitle.text("气势恢宏的发布会现场")
                $jText.text(" ")
                break;
            case 13:
                $jTitle.text("发布会现场表演")
                $jText.text(" ")
                break;
            default:
                $jTitle.text("有融网获A+轮5000万融资 双方共启融资签约仪式")
                $jText.text("2016年6月28日，有融网A+轮融资发布会在浙江杭州隆重举办。")
        }


    })



