(function(){
    "use strict";
    //未登录跳转到登录页面
    var $birthdayBtn = $("#j-birthdayGift-btn");
    var $btn = $("#j-birthday-btn");
    var $btn1 = $("#j-btn-1");
    var $btn2 = $("#j-btn-2");
    var $checkinBox = $("#j-checkin-box2");
    var $guide = $(".f-login-guide");
    var username=$('#j-username').text();
    var isLogined = $btn.data('logined');

    function activityStartTips(){
        $.xDialog({
            content: "活动还未开始，请提前摆好姿势，9月1日约~",
            callback: function () {
                xShade("hide");
                window.location.reload();
            }
        });
    }

    $birthdayBtn.on('click', function () {
        var _this=$(this),
            cacheData=$(this).data('cacheData');

        var isStart=_this.data('start');
        if(!isStart){
            activityStartTips();
            return false;
        }

        if(!cacheData){
            if (isLogined) {
                $.xPost({
                    url: '/birthday/goSignIn',
                    callback: function (data) {
                        if(data.success){
                            $birthdayBtn.data("isChecked", true);
                            checkinSuccess(data.result.gainPopularity);
                        $('#j-checkin-btn').remove();
                        }else{
                            birthdayCallback(data,_this,'');
                        }
                    }


                });
            }
            else {
                window.location.href = '/security/login/';
            }
        }
        else{
            if(cacheData.success){
                $birthdayBtn.data("isChecked", true);
                checkinSuccess(cacheData.result.gainPopularity);
            }else{
                birthdayCallback(cacheData,_this,'');
            }
        }
        function checkinSuccess(point){
            setTimeout(function(){
                $checkinBox
                    .addClass('f-login-guide1')
                    .find('img').replaceWith('<div class="u-dice-action u-dice-a'+point+'"></div>');
                $birthdayBtn.hide().after("<span>恭喜获得"+point+"x10点人气值<br />生日快乐！</span>");
            },750);
        }
    });

    //红包特权——专享50元现金券
    $btn1.on('click',function(data){
        var _this=$(this),
            cacheData=$(this).data('cacheData'),
            content='生日快乐！亲爱的{{username}}，一张50元现金券已放入您的账户，请注意查收哦！'+
                '<a class="u-birthdayGift-productsList" href ="/coupon/couponList" target="_blank" >（查看我的现金券）</a>';

        var isStart=_this.data('start');
        if(!isStart){
            activityStartTips();
            return false;
        }

        if(!cacheData){
            if(isLogined) {
                $.xPost({
                    url: '/birthday/receiveBirthday50Coupon',
                    callback: function (data) {
                        content=replaceContent(content,username);
                        birthdayCallback(data,_this,content);
                    }
                });
            }
            else{
                window.location.href = '/security/login/';
            }
        }else{
            birthdayCallback(cacheData,_this,'亲爱的会员，您已经领取过生日礼包啦~');
        }
    });

    //收益特权——专享1%收益券
    $btn2.on('click',function(data){
        var _this=$(this),
            cacheData=$(this).data('cacheData'),
            content='生日快乐！亲爱的{{username}}，一张1%收益券已放入您的账户，请注意查收哦！'+
                '<a class="u-birthdayGift-productsList" href="/coupon/profitCouponList" target="_blank">'+
                '（查看我的收益券）</a>';

        var isStart=_this.data('start');
        if(!isStart){
            activityStartTips();
            return false;
        }

        if(!cacheData) {
            if(isLogined){
                $.xPost({
                    url: '/birthday/receiveBirthday001Coupon',
                    callback: function (data) {
                        content=replaceContent(content,username);
                        birthdayCallback(data,_this,content,function(){
                            /*   window.location.reload();*/
                        });
                    }
                });
            }else{
                window.location.href = '/security/login/';
            }
        }
        else{
            birthdayCallback(cacheData,_this,'亲爱的会员，您已经领取过生日礼包啦~');
        }
    });

    /**
     * 生日请求回调
     * @param data {Object} json
     * @param target {Object} event target
     * @param content {String} dialog content
     * @param callback {String}
     */
    function birthdayCallback(data,target,content,callback){
        //cache data
        target.data('cacheData',data);

        if(data.success){
            showDialogResult(data,content,callback);
        } else {//error handler
            var errorCode = Number(data.resultCodeEum[0].code);
            switch (errorCode) {
                case 90042:
                    var eventWords='领取生日礼包';
                    if(target[0].id==='j-birthdayGift-btn'){
                        eventWords='享受签到特权'
                    }
                    $.xDialog({

                        content: "亲爱的会员，实名认证后才能"+eventWords+
                        "，先去<a class='u-birthdayGift-productsList' "+
                        "href='/member/sinapay' target='_blank'>"+
                        "实名认证</a>吧~"
                    });
                    break;
                default:
                    showDialogResult(data,data.resultCodeEum[0].msg,callback);
                    break;
            }
        }
    }

    /**
     *  消息结果对话框
     *  @param data {Object} json
     *  @param content {String} dialog content
     *  @param callback {Function}
     */
    function showDialogResult(data,content,callback){
        if(data.success){
            $.xDialog({content:content,type:'success',callback:callback});
        }else if(data.error){
            $.xDialog({content:data.resultCodeEum[0].msg,type:'info',callback:callback});
        }
    }

    /**
     *  数据替换
     *  @param content {String} dialog content
     *  @param name {String} username
     *  @return {String}
     */
    function replaceContent(content,name){
        return content.replace(/\{{.*?\}}/g,name);
    }
})();
