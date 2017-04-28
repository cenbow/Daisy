/**
 * Created by Administrator on 2016/8/9.
 */

//var result=$("#j-test").data("map"),
    var $jTest=$("#j-test");
var result=$jTest.data("result"),
    os=$jTest.data("os"),
     url=$jTest.data('url'),
    type=$jTest.data('type'),
    tradeNo=$jTest.attr('tradeNo'),
    withdrawNo=$jTest.attr('withdrawNo'),
    orderId=$jTest.attr('orderId')

if(os==1){
	//alert(os+"和"+type);
    switch(type){
        //设置支付密码
        case 1:
            Android.CallBack("2",null);
            break
        //开关委托扣款
        case 2:
            Android.CallBack("3",null);
            break
        //充值
        case 3:
            Android.CallBack("4",null);
            break
        //交易充值
        case 4:
            Android.CallBack("6",null);
            break
        //提现
        case 5:
            Android.CallBack("5",null);
            break
        //投资
        case 6:
            Android.CallBack("6",null);
            break
          //未开通委托扣款，交易页面充值
        case 7:
            Android.CallBack("6",null);
            break
            //开关委托扣款
        case 8:
            Android.CallBack("3",null);
            break
            
    }
}else if(os==2){
	//alert(os+"和"+type);
    switch(type){
        //设置支付密码
        case 1:
            window.location.hash='yrw-callback://type=1'
            break
        //开关委托扣款
        case 2:
            window.location.hash='objc:yrw-callback://type=2'
            break
        //充值
        case 3:
            window.location.hash='yrw-callback://type=3'
            break
        //交易充值
        case 4:
            window.location.hash='yrw-callback://type=4'
            break
        //提现
        case 5:
            window.location.hash='yrw-callback://type=5'
            break
        //投资
        case 6:
            window.location.hash='yrw-callback://type=6'
            break
        //未开通委托扣款，交易页面充值
        case 7:
            window.location.hash='yrw-callback://type=4'
            break
        case 8:
            window.location.hash='yrw-callback://type=2'
            break
    }
}else if(os==3){
	//alert(os+"和"+type);
    switch(type){
        case 1:
            //M站设置支付密码
            window.location.href=url+'/mCenter/setPayPassword'
            break
        case 2:
            //M站开关委托扣款
             window.location.href=url+'/mCenter/paymentCipher'
            break
        case 3:
            //M站充值
            window.location.href=url+'/mCenter/afterRechange?tradeNo='+tradeNo
            break
        case 4:
            //M站交易充值
            window.location.href=url+'/mCenter/orderPayment?orderId='+orderId+'&type='+type
            break
        case 5:
            //M站提现
            window.location.href=url+'/mCenter/afterWithdraw?withdrawNo='+withdrawNo
            break
        case 6:
            //M站投资
            window.location.href=url+'/mCenter/orderPayment?orderId='+orderId+'&type='+type
            break
        case 7:
            //M站充值接口，交易充值类型
            window.location.href=url+'/mCenter/afterRechange?tradeNo='+tradeNo
            break
        case 8:
            //投资页面M站开关委托扣款
            window.location.href=url+'/mCenter/orderPayment?orderId='+orderId+'&type='+type
            break
        case 9:
            //新浪个人中心返回
            window.location.href=url+'/mCenter/security'
        case 11:
            window.close()
            break
    }
}else if(os==4){
	//alert(os+"和"+type);

}

