(function () {
    var $jmidAutumnbtn = $('#j-midAutumn-btn'),
        isLogined = $jmidAutumnbtn.data("logined"),
        isStart = $jmidAutumnbtn.data("start"),
        isEnd = $jmidAutumnbtn.data("end");

    //按钮点击事件
    $jmidAutumnbtn.on('click', function () {
        var dialogData = {};
        if (environment.serverDate > isStart && environment.serverDate < isEnd) {
            if (isLogined) {
                $.xPost({
                    url: '/activity/midAutumn/coupon',
                    callback: function (data) {
                        showGetDialog(data);
                    }
                });
                //弹框内容判断
                function showGetDialog(data) {
                    var hasSharebox=false;
                    if (data.success) {
                        dialogData = {
                            type: 'success',
                            content: '亲爱的用户，50元现金券已发放至您的账户，请查收，祝您中秋快乐！<div id="j-dialog-share" class="u-dialog-share"> </div>'
                        };
                        hasSharebox=true;
                    } else {
                        var dataCodeEum0 = data.resultCodeEum[0],
                            code=Number(dataCodeEum0.code);
                        if (code === 90727) {
                            dialogData = {
                                type: 'info',
                                content: '亲爱的用户，祝您中秋快乐！您已领取过现金券，把机会分享给好友吧~' + '<div id="j-dialog-share" class="u-dialog-share"> </div>'
                            };
                            hasSharebox=true;
                        } else {
                            if (code === 90726) {
                                dialogData = {type: 'info', content: dataCodeEum0.msg};
                            } else {
                                dialogData = {
                                    type: 'info',
                                    content: dataCodeEum0.msg + '<div id="j-dialog-share" class="u-dialog-share"> </div>'
                                };
                            }
                        }
                    }

                    //对话框实例
                    var dialog = $.xDialog(dialogData);
                    //删除取消按钮
                    $('.j-dialog-cancel').remove();
                    if(hasSharebox){
                        //分享组件剪切到对话框中
                        $('#j-dialog-share').append($('#j-share-box').show());
                    }
                    //监听对话框的remove事件，刷新页面
                    dialog.addEventListener('remove', function () {
                        location.reload();
                    });
                }
            } else {
                dialogData = {
                    type: 'info', content: '亲爱的用户，您尚未登录', okValue: '请先前往登录'
                    , callback: function () {
                        window.location.href = '/security/login/';
                    }
                };
                $.xDialog(dialogData);

            }
        } else {
            if (environment.serverDate >= isEnd) {
                dialogData = {type: 'info', content: '亲爱的用户，祝您中秋节快乐！现金券已经送完啦，十一还有惊喜，记得赶早哦~'};
                $('#j-share-box').hide();
            } else if(environment.serverDate<isStart) {
                dialogData = {type: 'info', content: '亲爱的用户，活动未开始，把机会分享给好友，9月26日10点，约起来吧！'+ '<div id="j-dialog-share" class="u-dialog-share"> </div>'};

            }

            //对话框实例
            var dialog = $.xDialog(dialogData);
            //删除取消按钮
            $('.j-dialog-cancel').remove();
            $('#j-dialog-share').append($('#j-share-box').show());
            //监听对话框的remove事件，刷新页面
            dialog.addEventListener('remove', function () {
                location.reload();
            });
        }
        //移动端不执行云的js效果
        if (!isMobile()) {
            $('#j-cloud-lfet').animate({left: '-940px', opacity: '0'}, 11000);
            $('#j-cloud-right').animate({left: '1900px', opacity: '0'}, 11000);
        }


        function isMobile() {
            var tags = ['iPhone', 'iPod', 'Android', 'Windows Phone'],
                ua = navigator.userAgent.toLowerCase(),
                value = false;
            for (var i = 0; i < tags.length; i++) {
                if (ua.indexOf(tags[i].toLowerCase()) !== -1) {
                    value = true;
                    break;
                }
            }
            return value;
        }
    });

}());