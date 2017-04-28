/**
 * Created by Administrator on 2015/10/10.
 */
var severtime=environment.serverDate,
    jschedule=$('#j-meeting-schedule'),
    fspan=jschedule.find('span'),
    fi=jschedule.find('i'),
    fdl= jschedule.find('dl'),
    jbtnText=$('#j-btn-text'),
    jlivetext=$('#j-live-text'),
    jlivevideo=$('#j-meetingPreHeat-youku');
//判断服务器时间是否大于11月18号
if(severtime>(new Date('2015/11/18 ').getTime())){
    fi.eq(0).css("color","#c93940");
    fdl.eq(0).css("color","#c93940");
    //判断服务器时间是否大于11月19号
    if(severtime>(new Date('2015/11/19').getTime())){
        fspan.css("width","25%");
        fi.eq(1).css("color","#c93940");
        fdl.eq(1).css("color","#c93940");

    }
    //判断服务器时间是否大于11月19号13点30
    if(severtime>(new Date('2015/11/19 13:30:00').getTime())){
        fspan.css("width","50%");
        fi.eq(2).css("color","#c93940");
        fdl.eq(2).css("color","#c93940");
        jlivetext.text("现场直播");
        jlivevideo.remove();
        $('#j-live-video').append('<iframe class="video_iframe" style="z-index:1;" src="http://v.qq.com/iframe/player.html?vid=q0173sc1cu6" allowfullscreen="" frameborder="0" height="512" width="100%"></iframe>');
    }
    //判断服务器时间是否大于11月19号16点30
    if(severtime>(new Date('2015/11/19 15:30:00').getTime())){
        fspan.css("width","75%");
        fi.eq(3).css("color","#c93940");
        fdl.eq(3).css("color","#c93940");
        jlivetext.text("庆典回顾");
    }
    //判断服务器时间是否大于11月19号20点
    if(severtime>(new Date('2015/11/20').getTime())){
        fspan.css("width","100%");
        fi.eq(4).css("color","#c93940");
        fdl.eq(4).css("color","#c93940");
    }
}
//判断服务器时间是否大于11月17号
if(severtime>(new Date('2015/11/17').getTime())){
    jbtnText.prop("href","/activity/anniversary/index");
    jbtnText.prop("target","_blank");
    jbtnText.text("周年庆理财分会场，海量好礼送不停！");
}
//页面跳转https替换成http
if(location.href.indexOf('https')!==-1){
    window.location=location.href.replace(/https/g,'http');
}