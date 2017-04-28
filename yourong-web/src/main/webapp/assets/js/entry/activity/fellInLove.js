//520活动
import {template} from'module/template'
import {Util} from 'common/util'

let domain=environment.globalPath,
    urls={
          init:domain+"/activity/venus/init ",
          profile: domain + '/member/profile' //实名认证接口，用来实现登录跳转
        }
//加载数据
Util.post({
    url:urls.init,
    callback:function(data){
        if(data.success){
            init(data.result)
        }
    }
})
//数字转换成千分号方法
let amountSplit=(num)=>{
    if (!num) {
        return ''
    }
    var n = num + "",
        reg = /(-?\d+)(\d{3})/
    return n.replace(reg, "$1,$2")
}

let init=(result)=>{
    let actvityStatus=result.activityStatus||'';
    switch (actvityStatus){
        case 2:
            beforeActive();
            //duringActive(result);
            break;
        case 4:
            duringActive(result);
            //beforeActive();
            break;
        case 6:
            afterActive(result);
            break;
        default:
            break;
    }
}

//活动开始之前
let beforeActive=()=>{
    //进度条
    $("#j-progress-plan").css("left",0)
    $("#j-progress-content").css("left","99%")
    $("#j-coupon-val").text("5200")
    $(".u-fellInLove-progress").css("opacity",1)
    //巅峰壕礼
    $("#j-countdown-content").removeClass("f-dn")
    //按钮
    $("#j-fellInLove-btn").text("活动未开始").addClass("z-current")
    //列表
    $("#j-list-beforeActive").removeClass("f-dn")
}
//活动进行中
let duringActive=(result)=>{
    //进度条
    let count=result.couponNumber,
        pre=((count/5200).toFixed(2))*100-100
    $("#j-progress-plan").css("left",pre+"%")
    $("#j-progress-content").css("left",(99+pre)+"%")
    $("#j-coupon-val").text(count)
    $(".u-fellInLove-progress").css("opacity",1)
    //巅峰豪礼-按钮
    let login=$("#j-fellInLove-btn").data("logined")
    if(login){
        let memberTotalAmount=amountSplit(result.memberTotalAmount||'');
        if(memberTotalAmount){
            $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元")
        }else{
            $("#j-fellInLove-btn").text("我的投资额:0元")
        }
    }else{
        $("#j-fellInLove-btn").text("登录查看我的投资额")
            .attr("href","javascript:void(0)")
            .on("click",function(){
            Util.post({ url: urls.profile })
        })
    }
    //巅峰榜表单
    $("#j-fellInLove-list").removeClass("f-dn")
    let rankList = result.rankList ||[],
        listLength=rankList.length,
        html;
    if(listLength<10){
        for (var i=0;i<10-listLength;i++) {
            rankList.push({
                avatar: "/static/img/member/avatar_35x35.png",
                lastUsername: "虚位以待",
                lastTotalInvest: ""
            })
        }
    }
    result.rankList = rankList
    html = template('j-fellInLove-list-tpl', result);
    $('#j-fellInLove-list').html(html);


    //倒计时部分
    $("#j-countdown-countdown").removeClass("f-dn");
    var $actCountdown=$("#j-activity-countdown"),
        startTime=result.startTime,
        endTime=result.endTime,
        tNow=environment.serverDate,
        //startTime=new Date('2016/05/12 18:00:00').getTime(),
        //endTime=new Date('2016/05/12 17:00:00').getTime(),
        actCountTime=Math.floor((endTime - tNow) / 1000),
        countClock = $actCountdown.FlipClock({
        countdown: true,
        autoStart: false,
        clockFace: 'HourlyCounter',
        callbacks: {
            stop: function () {
                var html = template('j-countdown-ranking-tpl', result);
                $('#j-countdown-ranking').html(html);
            }
        }
    });
    //当前时间大于等于活动开始时间活动倒计时开始
    if(tNow>=startTime){
        countClock.setTime(actCountTime);
        countClock.start();
    }

}

//活动结束后
let afterActive=(result)=>{
    //进度条
    $("#j-progress-plan").css("left","-100%")
    $("#j-progress-content").css("left",0)
    $("#j-coupon-val").text("0")
    $(".u-fellInLove-progress").css("opacity",1)
    //巅峰豪礼-按钮
    let login=$("#j-fellInLove-btn").data("logined")
    if(login){
        let memberTotalAmount=amountSplit(result.memberTotalAmount||'');
        if(memberTotalAmount){
            $("#j-fellInLove-btn").text("我的投资额:"+memberTotalAmount+"元").addClass("z-current")
        }else{
            $("#j-fellInLove-btn").text("我的投资额:0元").addClass("z-current")
        }

    }else{
        $("#j-fellInLove-btn").text("登录查看我的投资额")
            .attr("href","javascript:void(0)")
            .on("click",function(){
                Util.post({ url: urls.profile })
            })
    }
    //巅峰壕礼
    $("#j-countdown-ranking").removeClass("f-dn");
    var html1 = template('j-countdown-ranking-tpl', result);
    $('#j-countdown-ranking').html(html1);
    //排名
    //巅峰榜表单
    $("#j-fellInLove-list").removeClass("f-dn")
    let rankList = result.rankList ||[],
        listLength=rankList.length,
    html;
    if(listLength<10){
        for (var i=0;i<10-listLength;i++) {
            rankList.push({
                avatar: "/static/img/member/avatar_35x35.png",
                lastUsername: "虚位以待",
                lastTotalInvest: ""
            })
        }
    }
    result.rankList = rankList
    html = template('j-fellInLove-list-tpl', result);
    $('#j-fellInLove-list').html(html);

}


