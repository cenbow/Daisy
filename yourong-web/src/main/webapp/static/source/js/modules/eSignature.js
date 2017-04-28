/**
 * Created by Administrator on 2016/7/13.
 */
//我的资料-电子签名
var $eSignatueChoice=$("#j-eSignatue-choice")
signWay=$("#j-signWay").data("signway")
//默认关闭自动签署，如果值为0表示开启自动签署

if(signWay===1){
    $("#j-eSignatue-autoOpen").removeClass('f-dn').siblings().addClass('f-dn')
    $("#j-eSignatue-handClose").removeClass('f-dn').siblings().addClass('f-dn')
}


$eSignatueChoice.on('click','.j-choice',function(){
    //查找点击的当前的div里面存不存在z-current，如果存在证明状态是选中状态，不运行切换代码，反之则能正常点击切换
    var i=$(this).find(".f-dn").siblings().find(".z-current").length
    if(i<1){
        $(this).find(".f-dn").removeClass('f-dn').siblings().addClass('f-dn')
        $(this).siblings().find(".f-dn").removeClass('f-dn').siblings().addClass('f-dn')
    }
})

var $eSignatueSsub=$("#j-eSignatue-sub");

$eSignatueSsub.on("click",function(){
    var way
    if($("#j-eSignatue-autoClose").hasClass('f-dn')){
        way=1
    }else{
        way=0
    }

    $.xPost({
        url:environment.globalPath+"/member/saveSignWay",
        data:{"signWay":way},
        callback:function(data){
            if(data.success){
                $.xDialog({
                    type: "success",
                    content:'设置成功！',
                    height:140,
                    width:360,
                    okValue: '确定'
                });

            }else {
                $.xDialog({
                    type: "error",
                    content:'设置失败，请稍后再试。',
                    height:140,
                    width:360,
                    okValue: '确定'
                });

            }
        }
    })
})
