/**
 * 用户中心核心JS
 * 把公共方法放在这里，不要在这里写实例
 * Created by adeweb on 15/8/12
 */
var XRW = window.XRW || {};
(function () {
    "use strict";
    XRW.UC = function(){};
    /**
     * 用户中心Banner
     * @param id {String} banner id
     * @param time {number} slider's microsecond
     * @param page {String} which page the banner show in
     */
    XRW.UC.prototype.slideBanner=function(id,time,page) {
        var ucBanner = $(id),bannerTimer=0;
        if (ucBanner.length) {
            if(page==='uc:home'){
                bannerTimer = setTimeout(bannerTimeoutAction, time);
                ucBanner.removeClass('f-dn').data('expand', true);
            }else{
                ucBanner.width(10).removeClass('f-dn');
                ucBanner.data('expand', false);
            }

            var intervalTime=0;
            ucBanner.on('mouseenter', function () {
                if (ucBanner.data('expand')) {
                    clearTimeout(bannerTimer);
                } else {
                    bannerTimeoutAction('expand');
                }
                intervalTime=new Date().getTime();
            }).on('mouseleave', function () {

                intervalTime=(new Date().getTime())-intervalTime;
                if(intervalTime<350){
                    setTimeout(function () {
                        bannerTimeoutAction();
                    },3000-intervalTime);
                }else if(ucBanner.data('expand')){
                    bannerTimeoutAction();
                }
            });
        }

        function bannerTimeoutAction(action) {
            var width = '10px',
                isExpand = false;
            if (action === 'expand') {
                width = '650px';
                isExpand = true;
            }
            ucBanner.animate({
                width: width
            },350, function () {
                ucBanner.data('expand', isExpand);
            });
        }
    }
})();

