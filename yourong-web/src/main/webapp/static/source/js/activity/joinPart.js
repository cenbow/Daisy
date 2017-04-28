
var $againPicture=$("#j-join-picture"),
    $firstPage=$("#j-first-page"),
    liLength=$againPicture.find('ul').find('li').length,
    ulWidth=$againPicture.find('ul').css("width",liLength*918),
    $jTitle=$("#j-title"),
    $jText=$("#j-text");
    //初始的文字
    $jTitle.text("秦皇岛市彩虹贸易有限公司总经理初辉铭致辞")

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
            i=4
        }
//根据索引值给下一个索引值加z-active
        $againPicture.find('ul').find('li').removeClass("z-active").eq(i).addClass("z-active")
       var n=$againPicture.find('ul').find('li.z-active').index(),
        ml=918*(n)
        $firstPage.animate({marginLeft:-ml},300)
        //显示第几页
        $("#j-mun").text(n+1)

        //每页不同的标题文字
        var pn=n+1;
        switch (pn){
            case 1:
                $jTitle.text("秦皇岛市彩虹贸易有限公司总经理初辉铭致辞")
                break;
            case 2:
                $jTitle.text("有融网方与国资入股方领导双方进行现场签约仪式")
                break;
            case 3:
                $jTitle.text("有融网方与国资入股方领导握手")
                break;
            case 4:
                $jTitle.text("杭州信有诚互联网金融服务有限公司执行总裁进行平台战略发布")
                break;
            case 5:
                $jTitle.text("有融网方与国资入股方领导合影")
                break;
            default:
                $jTitle.text("秦皇岛市彩虹贸易有限公司总经理初辉铭致辞")
        }


    })



