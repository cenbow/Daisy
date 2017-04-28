"use strict";function goLoginPage(){_util.Util.post({url:urls.receive})}function formatTime(t,e){t=new Date(t);var a={M:t.getMonth()+1,d:t.getDate(),h:t.getHours(),m:t.getMinutes(),s:t.getSeconds(),q:Math.floor((t.getMonth()+3)/3),S:t.getMilliseconds()};return e=e.replace(/([yMdhmsqS])+/g,function(e,i){var r=a[i];return void 0!==r?(e.length>1&&(r="0"+r,r=r.substr(r.length-2)),r):"y"===i?(t.getFullYear()+"").substr(4-e.length):e})}var _template=require("module/template"),_util=require("common/util"),_cube=require("module/cube"),$progressInner=$("#j-progress-inner"),$progressTip=$("#j-progress-tip"),$progressTotal=$("#j-progress-total"),$progressRemain=$("#j-progress-remain");$("#j-tab-menu").on("click","li.u-tab",function(){var t=$(this),e=t.data("skin");$("#j-main").removeClass().addClass("g-main").addClass("z-"+e)});var domain=env.globalPath,urls={init:environment.globalPath+"/activity/break2Billion/init",receive:environment.globalPath+"/activity/break2Billion/receive"},logined=$("#j-main").data("logined");$("#j-checkNum").on("click",function(){return!logined&&void _util.Util.post({url:urls.receive,callback:function(t){}})}),_util.Util.post({url:urls.init,callback:function(t){t.success?init(t.result):log("后台加载数据出错")}});var init=function(t){var e=t.activityStatus||"";if(t.hasBreak&&$("#j-total").text(_util.Util.format(t.break2BillionTime,"yyyy年的MM月dd日hh时mm分")+",交易额突破20亿"),1===logined){if(6==t.activityStatus)var a="我的累计投资额：¥"+(_util.Util.format(t.memberTotalAmount||0)||0);else var a="我的今日投资额：¥"+(_util.Util.format(t.memberDayAmount||0)||0)+"，我的累计投资额：¥"+(_util.Util.format(t.memberTotalAmount||0)||0);$("#j-checkNum").text(a).css("cursor","default")}switch($("#j-num").removeClass("z-fadeOut"),e){case 2:beforeActive(t);break;case 4:duringActive(t);break;case 6:afterActive(t)}$("#j-progress-wrap").addClass("z-show");var i=function(t){var a=t.lotteryList||[];if(2==e)$("#j-luckly-list").addClass("z-noData2").text("活动未开始");else if(4==e||6==e)if(0==a.length)$("#j-luckly-list").addClass("z-noData").text("—— 快来试试手气吧 ——");else{var i=(0,_template.template)("j-luckly-list-tpl",t);$("#j-luckly-list").html(i).scrollList({size:5,height:50,length:1,time:5e3,stoppable:!0})}};if(i(t),2==e)$("#j-list-beforeActive").removeClass("f-dn");else{var r=t.rankList||[],s=r.length,n=void 0;if($("#j-ranking-list").removeClass("f-dn"),s<10)for(var l=0;l<10-s;l++)r.push({avatar:"/static/img/member/avatar_35x35.png",lastUsername:"虚位以待",lastTotalInvest:""});t.rankList=r,n=(0,_template.template)("j-ranking-list-tpl",t),$("#j-ranking-list").html(n)}},beforeActive=function(t){$("#j-lottery-arrow").on("click",function(){_cube.Dialog.show({content:"来早啦~活动未开始"})}),$("#j-section2-btn").text("即将开启").addClass("z-disabled")},duringActive=function(t){var e=t.fund;e>1e6&&(e=1e6),e<0&&(e=0);var a=100*(e/1e6).toFixed(2)-100;$progressRemain.text(_util.Util.format(e)||0),e>0&&(e<1e4||1e4==e)?$progressInner.css("left","-99%"):$progressInner.css("left",a+"%"),$progressTip.css("left",a+93+"%");var i=20,r={popularityFor8:135-i,popularityFor18:90-i,popularityFor58:45-i,popularityFor118:-i,"annualizedFor0.005":-45-i,"annualizedFor0.01":-90-i,couponFor100:-135-i,couponFor200:180-i},s=Math.random(),n=Math.round(s)?1:-1,l=s*n*12;$("#j-lottery-arrow").on("click",function(){if(logined){var e=(new Date).getTime(),a=$(this);if(!(e-(a.attr("data-clickbegin")-0||0)>1e3))return a.attr("data-clickbegin",e),!1;a.attr("data-clickbegin",e),t.hasLottery||1==a.data("status")?_cube.Dialog.show({content:"今日已抽奖，明天再来吧！"}):_util.Util.post({url:urls.receive,data:{activityPart:1},callback:function(t){var e=t.result.lotteryRewardCode||"",a=t.result.lotteryRewardName||"";t.success=!0,t.success&&e?$("#j-lottery-rotate").rotate({angle:0,animateTo:1800+r[e]+l,duration:4e3,callback:function(){$("#j-lottery-arrow").data("status",1),_cube.Dialog.show({type:"success",content:"恭喜你！获得"+a+"，稍后可在账户中查看"})}}):"90724"===t.resultCodeEum[0].code&&_cube.Dialog.show({content:"还差一点，快去投资获得机会~"})}})}else goLoginPage()}),$("#j-section2-btn").text("即将开启")},afterActive=function(t){var e=1e4*$(void 0).val();e>1e6&&(e=1e6),e<0&&(e=0),$progressRemain.text(0),$progressInner.css("left","-100%"),$progressTip.css("left","-6%"),$("#j-lottery-arrow").on("click",function(){_cube.Dialog.show({content:"活动已结束"})});var a=t.break2BillionTime+60*t.giftOutTime*60*1e3;if(a<environment.serverDate?$("#j-section2-btn").text("活动已结束").addClass("z-disabled"):t.hasReceiveGift||1===$("#j-section2-btn").attr("data-hasReceiveGift")?$("#j-section2-btn").text("已领取").addClass("z-disabled"):logined?(t.memberTotalAmount||0)<t.giftLevel[0]?$("#j-section2-btn").text("立刻领取").addClass("z-disabled"):$("#j-section2-btn").removeClass("z-disabled").on("click",function(){return"1"!==$(this).attr("data-hasReceiveGift")&&void _util.Util.post({url:urls.receive,data:{activityPart:2},callback:function(t){if(t.success&&t.result.giftIndex){var e="";switch(t.result.giftIndex){case 0:e="恭喜您！获得100元现金券，0.5%收益券";break;case 1:e="恭喜您！获得200元现金券，1%收益券";break;case 2:e="恭喜您！获得200元现金券，1.5%收益券";break;case 3:e="恭喜您！获得500元现金券，2%收益券"}_cube.Dialog.show({type:"success",content:e}),$("#j-section2-btn").text("已领取").addClass("z-disabled").attr("data-hasReceiveGift",1)}else log("领取第二部分红包后端返回数据有错误: ",t)}})}):$("#j-section2-btn").text("立刻领取").removeClass("z-disabled").on("click",function(){goLoginPage()}),2==t.activityStatus)$("#j-list-beforeActive").removeClass("f-dn");else{var i=t.rankList||[],r=i.length,s=$("#j-card-list");if(r>0){i=t.rankList||[];for(var n=0;n<3;n++)if(i[n]){var l=i[n].avatar||"",o=i[n].lastUsername||"";l?s.find(".m-card").eq(n).find(".u-user-header img").attr("src","https://oss-cn-hangzhou.aliyuncs.com"+l).end().find(".u-user-name").text(o):s.find(".m-card").eq(n).find(".u-user-header img").attr("src","/static/img/member/avatar_35x35.png").end().find(".u-user-name").text(o)}}}};
//# sourceMappingURL=break2Billion.js.map