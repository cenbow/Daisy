/*global define,require,$,environment,console,env,globalPath,log,fz*/
define(function (require, exports, module) {
    "use strict";

    //项目环形图
    exports.processCircle = function (target) {
        /*circle*/
        var $target = $(target);
        $target.each(function (index, el) {

            var _this = $(this),
                num = _this.find('span').text() * 3.6,
                rightCircle = _this.find('.z-right'),
                leftCircle = _this.find('.z-left');

            //环形动画
            num = parseInt(num);
            if (num <= 180) {
                rightCircle.animate({
                    rotate: num + 'deg'
                }, 1000, 'ease-out');
            } else {
                rightCircle.css({'transform': 'rotate(180deg)', '-webkit-transform': 'rotate(180deg)'});
                leftCircle.animate({
                    rotate: (num - 180) + 'deg'
                }, 1000, 'ease-out');
            }

        });
    };

    //项目倒计时
    exports.projectCountdown = function (noticeTime) {

        $('.j-project-noticeTime').each(function () {
            var _this = $(this),
                time={};
            if(!$(this).attr('data-time')){
                time=Math.ceil(noticeTime)
            }else{
                time=Math.ceil($(this).attr('data-time'))
            }
            if (time > 0) {
                _this.text(getCountTime(time));
                var timer = setInterval(function () {
                    _this.text(getCountTime(time - 1));
                    time = time - 1;
                    if (time <= 0) {
                        clearInterval(timer);
                        _this.parent().siblings('.u-badge-red').removeClass('f-dn');
                        _this.parent().remove();

                    }
                }, 1000);
            }

        });
        function getCountTime(seconds) {
            if (seconds < 0) {
                return 0;
            }
            var timeArray = [Math.floor(seconds / 3600) || '00', Math.floor((seconds / 3600) % 1 * 60) || '00', seconds % 60];
            for (var i = 0; i < timeArray.length; i++) {
                if (timeArray[i] < 10 && timeArray[i] !== '00') {
                    timeArray[i] = '0' + timeArray[i];
                }
            }
            var timeString = timeArray.join(":");
            if (timeString === '00:00:00') {
                return 0;
            }
            return timeString;
            
        }
    };

});