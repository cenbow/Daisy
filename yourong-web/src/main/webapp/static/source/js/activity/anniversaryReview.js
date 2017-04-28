(function(){
    //页面跳转https替换成http
    if(location.href.indexOf('https')!==-1){
        window.location=location.href.replace(/https/g,'http');
    }

    var $guandianpic=$('#j-guandian-pic');
        $guandianpic.scrollList({
            size: 1,
            length: 3,
            height: 540,
            time: 5000,
            stoppabled:true
        });

    $('#j-shunjian-pic').scrollList({
        size: 1,
        length: 1,
        height: 859,
        time: 5000
    });
})();
