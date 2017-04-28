/**
 * JavaScript Base Library
 * Created by adeweb on 15/6/3.
 */
/*global define*/
define(function (require, exports, module) {

    //banner slider
    exports.bannerSlider = function (time) {
        var slider = new fz.Scroll('.ui-slider', {
            role: 'slider',
            indicator: true,
            autoplay: true,
            interval: time
        })
    };

    //项目环形图
    exports.projectCircle = function () {

        /*zepto animate module*/
        (function(d,f){var n="",s,m={Webkit:"webkit",Moz:"",O:"o"},a=document.createElement("div"),l=/^((translate|rotate|scale)(X|Y|Z|3d)?|matrix(3d)?|perspective|skew(X|Y)?)$/i,j,o,i,k,e,h,r,p,b,q={};function c(t){return t.replace(/([a-z])([A-Z])/,"$1-$2").toLowerCase()}function g(t){return s?s+t:t.toLowerCase()}d.each(m,function(u,t){if(a.style[u+"TransitionProperty"]!==f){n="-"+u.toLowerCase()+"-";s=t;return false}});j=n+"transform";q[o=n+"transition-property"]=q[i=n+"transition-duration"]=q[e=n+"transition-delay"]=q[k=n+"transition-timing-function"]=q[h=n+"animation-name"]=q[r=n+"animation-duration"]=q[b=n+"animation-delay"]=q[p=n+"animation-timing-function"]="";d.fx={off:(s===f&&a.style.transitionProperty===f),speeds:{_default:400,fast:200,slow:600},cssPrefix:n,transitionEnd:g("TransitionEnd"),animationEnd:g("AnimationEnd")};d.fn.animate=function(u,v,w,x,t){if(d.isFunction(v)){x=v,w=f,v=f}if(d.isFunction(w)){x=w,w=f}if(d.isPlainObject(v)){w=v.easing,x=v.complete,t=v.delay,v=v.duration}if(v){v=(typeof v=="number"?v:(d.fx.speeds[v]||d.fx.speeds._default))/1000}if(t){t=parseFloat(t)/1000}return this.anim(u,v,w,x,t)};d.fn.anim=function(B,w,v,D,x){var C,z={},F,A="",y=this,u,E=d.fx.transitionEnd,t=false;if(w===f){w=d.fx.speeds._default/1000}if(x===f){x=0}if(d.fx.off){w=0}if(typeof B=="string"){z[h]=B;z[r]=w+"s";z[b]=x+"s";z[p]=(v||"linear");E=d.fx.animationEnd}else{F=[];for(C in B){if(l.test(C)){A+=C+"("+B[C]+") "}else{z[C]=B[C],F.push(c(C))}}if(A){z[j]=A,F.push(j)}if(w>0&&typeof B==="object"){z[o]=F.join(", ");z[i]=w+"s";z[e]=x+"s";z[k]=(v||"linear")}}u=function(G){if(typeof G!=="undefined"){if(G.target!==G.currentTarget){return}d(G.target).unbind(E,u)}else{d(this).unbind(E,u)}t=true;d(this).css(q);D&&D.call(this)};if(w>0){this.bind(E,u);setTimeout(function(){if(t){return}u.call(y)},((w+x)*1000)+25)}this.size()&&this.get(0).clientLeft;this.css(z);if(w<=0){setTimeout(function(){y.each(function(){u.call(this)})},0)}return this};a=null})(Zepto);
        /*circle*/
        $('.u-project-circle').each(function (index, el) {

            var _this = $(this),
                num = _this.find('span').text() * 3.6,
                rightCircle = _this.find('.z-right'),
                leftCircle = _this.find('.z-left');

            //环形动画
            num=parseInt(num);
            if (num <= 180) {
                rightCircle.animate({
                    rotate: num + 'deg'
                }, 1000, 'ease-out');
            } else {
                rightCircle.css({'transform':'rotate(180deg)','-webkit-transform':'rotate(180deg)'});
                leftCircle.animate({
                    rotate: (num - 180) + 'deg'
                }, 1000, 'ease-out');
            }

        })
    };

    //格式化的倒计时
    exports.projectCountdown = function () {

        $('.j-project-noticeTime').each(function () {
            var _this = $(this),
                time = $(this).data('time');
            if(time>0){
                _this.text(getCountTime(time));
                var timer = setInterval(function () {
                    _this.text(getCountTime(time-1));
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

    //获取用于显示的倒计时时间
    exports.getCountTime = function(endTime) {
        var currentTime = new Date().getTime(),
            seconds = Math.round((endTime - currentTime) / 1000);
        if (endTime - currentTime < 0) {
            return false;
        }
        var timeArray = [Math.floor(seconds / 3600) || '00', Math.floor((seconds / 3600) % 1 * 60) || '00', seconds % 60];
        for (var i = 0; i < timeArray.length; i++) {
            if (timeArray[i] < 10 && timeArray[i] !== '00') {
                timeArray[i] = '0' + timeArray[i];
            }
        }
        var timeString = timeArray.join(":");
        if (timeString === '00:00:00') {
            return false;
        }
        return timeString;
    };

    //浏览器信息
    exports.borwser = {
        platform: (function () {
            var tags = ['iPhone', 'iPad', 'iPod', 'Android', 'Windows Phone'],
                ua = navigator.userAgent.toLowerCase(),
                name = 'Unknown';
            for (var i = 0; i < tags.length; i++) {
                if (ua.indexOf(tags[i].toLowerCase()) !== -1) {
                    name = tags[i];
                    break;
                }
            }
            return name;
        }()),
        screen: [screen.width, screen.height]
    };

    //common method script
    $.extend({
        // Ajax Post Method
        xPost: function (config) {
            var type = "POST",
                datatype = "json",
                async = true;
            if (typeof config === "object") {
                type = config.type ? config.type : type;
                datatype = config.datatype ? config.datatype : datatype;
                async = config.async ? config.async : async;
                var xToken = $("#xToken").val();
                if (typeof xToken !== "undefined" && xToken === "") {
                    if (config.hasOwnProperty("data")) {
                        config.data.xToken = xToken;
                    } else {
                        config.data = {
                            xToken: xToken
                        };
                    }
                }
            } else {
                throw new Error("xPost: config参数为空或不是对象");
            }
            $.ajax({
                type: type,
                url: config.url,
                data: config.data,
                datatype: datatype,
                async: async,
                success: config.callback,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // console.log(XMLHttpRequest);
                    // console.log(textStatus);
                    // console.log(errorThrown);
                },
                statusCode: {
                    403: function () {
                        alert('请刷新页面');
                    },
                    500: function () {
                        window.location.href = environment.globalPath + "/500";
                    }
                }

            });
        }
    });
    $.fn.greetingTime= function () {
        var rDate = new Date().getHours(),
            rText = "";
        if (rDate >= 12 && rDate < 18) {
            rText = "下午好，";
        } else if (rDate >= 18) {
            rText = "晚上好，";
        } else {
            rText = "上午好，";
        }
        $(this).text(rText);
    }
});