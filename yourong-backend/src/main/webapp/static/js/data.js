jQuery(function($) {
    $("#exportLiCai").on("click",function(){
        var data =  $("#exportLiCai_form").serialize();
        window.location.href=config.urlMap.liCai+""+data+"";
    });

    $("#exportAlreadyInvest").on("click",function(){
        var data =  $("#exportAlreadyInvest_form").serialize();
        window.location.href=config.urlMap.alreadyInvest+""+data+"";
    });

    $("#exportNoInvest").on("click",function(){
        var data =  $("#exportNoInvest_form").serialize();
        window.location.href=config.urlMap.noInvest+""+data+"";
    });
    
    $("#exportDouWanInvest").on("click",function(){
        var data =  $("#exportDouWanInvest_form").serialize();
        window.location.href=config.urlMap.douWanInvest+""+data+"";
    });
});

