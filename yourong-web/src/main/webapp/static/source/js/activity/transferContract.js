
    //var result=$("#j-test").data("map"),
        var result=$("#j-test").data("result"),
            os=$("#j-test").data("os")

    //alert(os)
    var jresult=JSON.stringify(result)
    if(os==1){
        Android.CallBack(1,jresult);
    }else if(os==2){
    	var res = escape(jresult).replace(/%u/g,'\\u');//IOS结果转义

        window.location.hash='yrw-callback://type=1&message='+res;

    } else if(os==3){
        this.window.opener = null;
        window.close();
    }else if(os==4) {
        this.window.opener = null;
        window.close();
    }

