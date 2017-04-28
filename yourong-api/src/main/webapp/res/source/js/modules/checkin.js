/*
* 每日签到
* @author adeweb
*/
define(['zepto','base'],function (require, exports, module) {
    //签到api回调处理
    var base=require('base'),
        isBirthday=null,
        isBirthdayActivity=$('#j-checkin-plane').data('birthdayactivity');
    //签到状态请求
    base.getAPI({
        url:environment.globalPath +'/security/member/queryMemberSignInInfo',
        callback: function (data) {
            if(data.success){
                var isCheckin=data.result.signIn,
                    $checkPlane=$('#j-check-circle'),
                    $dice = $('#j-checkin-dice'),
                    $uncheck=$('#j-uncheck-box'),
                    result=data.result;
                isBirthday=result.birthday;
                if(isBirthday&&isBirthdayActivity){
                    if(isCheckin){
                        $checkPlane.find('.z-checked').show()
                            .find('em').text(result.todayGainPopularity);
                        $dice.data('checked',true);
                        $('.j-birthday-gift').show();
                    }else{
                        $uncheck.show()
                            .find('em').text(result.totalGainPopularity);
                        $uncheck.find('p').html("摇一摇签到<br/>祝你生日快乐！");
                        $('#j-birthday-x10').show();
                    }
                }
                else{
                    if(isCheckin){
                        $checkPlane.find('.z-checked').show()
                            .find('em').text(result.todayGainPopularity);
                        $dice.data('checked',true);
                    }else{
                        $uncheck.show()
                            .find('em').text(result.totalGainPopularity);
                        $uncheck.find('p').html("摇一摇签到");
                    }
                }

            }
        }
    });

    //圆环高度修正
    $(window).on('load resize', function () {

        var $circle = $('#j-check-circle'),
            $dice = $('#j-checkin-dice');

        $circle.show().height($circle.width());
        $circle.find('.z-uncheck span').css('line-height', $circle.width() * 0.15 + 'px');
        $dice.height($dice.width());
    });
    //触发签到
    function checkinAction() {
        
        var $dice = $('#j-checkin-dice');

        if($dice.data('checked')){
            return false;
        }
        $('#j-checkin-plane').addClass('z-disabled');
        $dice.removeClass('z-disabled').height($dice.width());

        base.getAPI({
            url:environment.globalPath +'/security/member/goSignIn',
            callback: function (data) {
                if(data.success){
                    var point=data.result.gainPopularity,
                        double=data.result.popularityDouble,
                        textPoint=point;
                    if(isBirthday&&isBirthdayActivity){
                        $('.j-birthday-gift').show();
                        textPoint=point+'x10';
                    }else{
                        $('.j-birthday-gift').hide();
                        if(double>1){
                            textPoint=point+'x'+double;
                        }
                    }
                    setTimeout(function () {
                        $('#j-checkin-point').text(textPoint);
                        $dice.find('img')
                            .prop('src',environment.globalPath+'/res/img/checkin/dice_'+point+'.png')
                            .siblings().show();
                    },1200);

                    $('#j-birthday-x10').hide();
                    $dice.data('checked',true);
                }else{
                    alert('您今天已经签到过。');

                }
            }
        });
    }

    //摇一摇
    if (window.DeviceMotionEvent) {
        window.addEventListener('devicemotion', deviceMotionHandler, false);
    }

    var flag = !0,
        value = 0,
        lastUpdate = 0,
        $spot = $('.spot'),
        SHAKE_THRESHOLD = 500,
        x, y, z, last_x, last_y, last_z;

    function deviceMotionHandler(eventData) {
        // Grab the acceleration including gravity from the results
        var acceleration = eventData.accelerationIncludingGravity;

        var curTime = new Date().getTime();

        if ((curTime - lastUpdate) > 100) {

            var diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            x = acceleration.x;
            y = acceleration.y;
            z = acceleration.z;

            var speed = Math.round((x + y + z - last_x - last_y - last_z) / diffTime * 10000);

            // 只计算第一次摇一摇的值
            if (speed > SHAKE_THRESHOLD && flag) {
                flag = !!0;

                if (speed > 300) {
                    checkinAction();
                }

            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }
});