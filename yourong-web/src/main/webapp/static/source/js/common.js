/*global escape,unescape,dialog*/
//no cache for all ajax request
$.ajaxSetup({
    //不缓存请求
    cache: false,
    //异常状态处理
    statusCode: {
        403: function () {
            alert('请刷新页面');
        },
        500: function () {
            window.location.href = environment.globalPath + "/500";
        },
        //session timeout
        901: function () {
            window.location.href = environment.globalPath + "/security/login/";
        }
    }
});

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
            var xToken = $("#xToken").val(),
                xData=config.data;

            if (typeof xToken !== "undefined" && xToken !== "") {
                if (config.hasOwnProperty("data")) {
                    //拼接的JSON(Object)
                    if($.isPlainObject(xData)){
                        xData.xToken = xToken;
                    }
                    //序列化的表单数据(Array)
                    else if($.isArray(xData)){
                        //add xToken
                        var hasToken=false;
                        $.each(xData, function (index, item) {
                            if(item.name==='xToken'){
                                hasToken=true;
                            }
                        });
                        !hasToken?xData.push({name:'xToken',value:xToken}):null;
                    }

                }
                else {
                    xData = {
                        xToken: xToken
                    };
                }
            }
        }
        else {
            throw new Error("xPost: config参数为空或不是对象");
        }
        $.ajax({
            type: type,
            url: config.url,
            data: xData,
            dataType: datatype,
            async: async,
            success: config.callback,
            error: function (XMLHttpRequest, textStatus, errorThrown) {}
        });
    },
    // Message Dialog
    xDialog: function (config) {
        $(".ui-popup").remove();
        if (typeof (config) !== "undefined" && $.isPlainObject(config)) {
            //类型初始化
            var type = "";
            if (typeof (config.type) === "undefined") {
                type = "info";
            } else {
                switch (config.type) {
                    case "error":
                        type = "error2";
                        break;
                    case "warn":
                        type = "warn";
                        break;
                    case "success":
                        type = "success2";
                        break;
                    default:
                        type = "info";
                }
            }
            //参数初始化
            var title = config.title ? config.title:"提示",
                content =config.content ? config.content:"" ,
                okValue = config.okValue?config.okValue:'确定',
                cancelValue= config.cancelValue?config.cancelValue:'取消',
                width = config.width||360,
                height = config.height||140,
                callback = typeof (config.callback) !== "function" ? function () {
                } : config.callback,
                cancel = function () {
                },
                cancelBtn = "";
            if (typeof (config.cancel) === "function") {
                cancel = config.cancel;
                cancelBtn = "<a href='javascript:void(0)' class='f-round u-btn-white j-dialog-cancel'>" +
                    cancelValue + "</a></div>";
            } else {
                cancel = function () {
                };
            }
            var html = "<div class='u-dialog-wrap'><i class='u-icon-" + type +
                    " u-icon f-icon-37'></i><span>" + content +
                    "</span><div class='u-dialog-btn'><a href='javascript:void(0)' "+
                    "class='f-round u-btn-white j-dialog-ok'>"+okValue+"</a>" +
                    cancelBtn + "</div>",
                d = dialog({
                    title: title,
                    content: html,
                    skin: "u-dialog-box",
                    width: width,
                    height: height
                });
            d.show();
            //关闭窗口
            $(".j-dialog-ok").click(function () {
                d.close().remove();
                callback();
            });
            //取消
            $(".j-dialog-cancel").click(function () {
                d.close().remove();
                cancel();
            });
        } else {
            throw new TypeError("xDialog参数类型错误或不存在");
        }
    },
    /*
     * 本地存储封装,所有键值对存在localStorage.xStorage下
     * @param name {string} key name
     * @param value {string｜number} key value
     * @return get => {string|number}, set|del => {object},没有参数 => undefined
     */
    xStorage: function (name,value) {
        var sessionData= {},
            xStorageValue=localStorage.getItem('xStorage');
        if(xStorageValue){
            sessionData=$.parseJSON(xStorageValue);
        }else{
            localStorage.setItem('xStorage','{}');
        }

        var action=typeof(arguments[1])==='undefined'?'get':(arguments[1]!==null?'set':'del');
        switch(action){
            case 'get':
                return $.type(name)==='string'?sessionData[name]:undefined;
                break;

            case 'set':
                sessionData[name]=value;
                localStorage.setItem('xStorage', JSON.stringify(sessionData));
                break;

            case 'del':
                delete sessionData[name];
                name?localStorage.setItem('xStorage',JSON.stringify(sessionData)) : throwError('name');
                break;

            default:
                return sessionData;
        }

        function throwError(name){
            throw new ReferenceError('xStorage: '+name+' is not defined');
        }

        return sessionData;
    }
});

$.fn.extend({
    // loading
    loading: function () {
        return this.each(function () {
            $(this).html('<div class="u-loading-box"><div class="u-progress-bar"><img src="' + environment.globalPath + '/static/img/common/loading.gif"/>请稍候...</div></div>');
        });
    },
    //xLoading 用法：$(target).xLoading(function(){ajaxCallback:target.find('.u-xloading').remove()})
    xLoading: function (callback, direction) {
        var position = "",
            dir = "",
            element;
        if (typeof(direction) !== "undefined") {
            dir = "z-dir-" + direction;
        }
        element = '<span class="u-xloading u-circle-15 ' + dir + '"></span>';

        position = this.css("positon");
        if (position !== "absolute" || position !== "relative") {
            this.addClass("f-pos-r");
        }
        this.append(element);
        this.loadingDone = function () {
            $(this).find(".u-xloading").remove();
        };

        if (typeof(callback) === "function") {
            callback(this);
        }

    },
    // 协议弹出层
    xArgreement: function () {
        var _this = $(this),
            url = _this.data("url"),
            tid = _this
                .data("target"),
            box = $("#" + tid);
        _this.on("click", function () {
            box.find("iframe").attr("src", url);
            xShade("show");
            $(".u-shade").css("z-index", 1001);
            box.fadeIn();
            return false;
        });
        box.find("i").on("click", function () {
            box.hide();
            xShade("hide");
            $(".u-shade").css("z-index", 999);
        });
    },
    //浮动层定位
    floatLayer: function () {
        this.each(function () {
            $(this).css({
                "margin-left": -($(this).outerWidth() / 2),
                "margin-top": -($(this).outerHeight() / 2)
            });
        });
    },
    // 问候时间
    greetingTime: function () {
        var rDate = new Date().getHours(),
            rText = "";
        if (rDate > 12 && rDate < 18) {
            rText = "下午好，";
        } else if (rDate >= 18) {
            rText = "晚上好，";
        } else {
            rText = "上午好，";
        }
        $(this).text(rText);
    },
    //粘贴事件
    pasteEvents: function (delay) {
        if (typeof (delay) === "undefined") {
            delay = 20;
        }
        return $(this).each(function () {
            var $el = $(this);
            $el.on("paste", function () {
                $el.trigger("prepaste");
                setTimeout(function () {
                    $el.trigger("postpaste");
                }, delay);
            });
        });
    },
    //焦点图
    fadeShow: function (config) {
        var _this = $(this),
            time = 3000,
            type = "",
            size = _this.data("size");
        if (typeof (config) !== "undefined") {
            time = typeof (config.time) !== "undefined" ? config.time : 3000;
            size = typeof (size) !== "undefined" ? size : 3;
            type = typeof (config.type) !== "undefined" ? config.type : "banner";
        } else {
            throw new Error("fadeShow():config is not defined");
        }

        function fadeSlider() {
            var news = _this.find("ul li.z-current").eq(0),
                allNews = _this.find("ul li"),
                dot = _this.find("ol li"),
                i = news.index(),
                n = 0;
            if (i >= 0 && i < (size - 1)) {
                news.next().css("z-index", "50");
                news.css("z-index", "51").fadeOut(500, function () {
                    news.show().css("z-index", "-1").removeClass("z-current");
                    news.next().css("z-index", "50").addClass("z-current");

                });
                n = i + 1;
            } else if (i === (size - 1)) {
                allNews.eq(0).css("z-index", "50");
                news.css("z-index", "51").fadeOut(500, function () {
                    news.show().css("z-index", "-1").removeClass("z-current");
                    allNews.eq(0).css("z-index", "50").addClass("z-current");

                });
                n = 0;
            }
            dot.eq(n).addClass("z-current").siblings().removeClass("z-current");
        }

        //定时
        var timer = setInterval(function () {
            fadeSlider();
        }, time);

        //鼠标经过停止计时
        _this.on("mouseenter", "ul li", function () {
            clearInterval(timer);
        });

        //鼠标离开继续计时
        _this.on("mouseleave", "ul li", function () {
            timer = setInterval(function () {
                fadeSlider();
            }, time);
        });

        _this.on("click", "ol li:not(.z-current)", function () {
            var i = $(this).index(),
                thisBanner = _this.find("ul li").eq(i),
                thisEffect = thisBanner.data("effect");
            $(this).addClass("z-current").siblings().removeClass("z-current");
            _this.find("ul li").eq(i).css("z-index", "50").addClass("z-current")
                .siblings().css("z-index", "-1").removeClass("z-current");

            clearInterval(timer);
        });
    },
    bannerSlider: function (time) {
        var _this = $(this);
        if (_this.find("a").length < 2) {
            return false;
        }
        setInterval(function () {
            var vImg = _this.find("a:visible"),
                hImg = _this.find("a:hidden");
            vImg.fadeOut(500, function () {
                hImg.show();
            });
        }, time);
    },
    //限制只能输入数字
    numOnly: function () {
        $(this).on("keyup", function () {
            var val = $(this).val();
            if (!Number(val)) {
                $(this).val(val.replace(/[^\d]/g, ""));
            }
        });
    },
    //大图预览
    bigImgPreview: function () {
        var body = $("body"), leave = false, timer,stamp;

        $(this).each(function () {

            $(this).on("mouseenter", function () {
                var imgUrl = $(this).data("imgurl"),
                    bLeft = $(this).offset().left,
                    bTop = $(this).offset().top,
                    direction = $(this).data("direction");
                stamp=new Date().getTime();
                $(".j-qrcode-img").remove();
                switch (direction) {
                    case "leftTop":
                        bTop = bTop - 200 + $(this).height();
                        break;
                    case "centerbottom":
                        bLeft = bLeft - 100 + 10;
                        bTop = bTop + 22;
                        leave = true;
                        break;
                    default:
                        break;
                }

                var bigImgHtml = '<img src="' + imgUrl + '" class="u-qrcode-img j-qrcode-img" id="j-img-'+stamp+'" style="left:' +
                    bLeft + 'px;top:' + bTop + 'px;"/>';
                body.append(bigImgHtml);
            }).on("mouseleave", function () {
                if (leave) {
                    timer = setTimeout(function () {
                        stamp=stamp||0;
                        body.find("#j-img-"+stamp).remove();
                    }, 300);
                }
            });
        });
        body.on("mouseenter", ".j-qrcode-img", function () {
            if (timer) {
                clearTimeout(timer);

            }
        }).on("mouseleave", ".j-qrcode-img", function () {

            $(this).remove();
        });
    },
    //浮层Banner显示关闭保持
    toggleLayerDisplay: function () {
        $(this).each(function () {
            var label = $(this).data("label"),
                targetSelctor = $(this).data("target"),
                target = targetSelctor ? $(targetSelctor) : $(this).parent(),
                closedLayerStatus = $.xStorage("closedLayerStatus"),
                bannerStatusJSON = closedLayerStatus ? JSON.parse(closedLayerStatus) : {};
            if (label) {
                var curDate = formatDate(environment.serverDate, "yyyy-mm-dd").replace(/-/g, "");

                if (!bannerStatusJSON[label] ||
                    (bannerStatusJSON[label]&&bannerStatusJSON[label] !== curDate)) {
                    target.css("display", "block");
                }
                $(this).click(function (evt) {
                    evt.stopPropagation();

                    //删除banner
                    target.remove();

                    //记录状态日期
                    bannerStatusJSON[label] = curDate;
                    $.xStorage("closedLayerStatus", JSON.stringify(bannerStatusJSON));

                    return false;
                });
            }
        });
    },
    /*
     * 弹出层定位(在artDialog定位异常的情况下使用)
     * @param config {Object} 配置文件
     * config.target => 要定位的目标
     * config.height,config.width 定位层的宽高
     * @param offset 显示位置距离事件触发对象的距离
     */
    popupPosition: function (config) {
        //弹出层
        var $popupLayer=config.target,
            popupTimer=null,
            popupWidth=config.width|| 0,
            offset=$(this).offset(),
            width=$(this).outerWidth(),
            popupOffset=config.offset||0,
            direction='left',
            layerLeft= 0,
            layerTop=0;
        var screenWidth= window.innerWidth ||
            document.documentElement.clientWidth ||
            document.body.clientWidth;
        if(offset.left>screenWidth/2&&screenWidth>=990){
            direction='right';
        }

        switch(direction){
            case 'left':
                layerLeft=offset.left + width + popupOffset;
                layerTop=offset.top;
                $popupLayer.removeClass('z-right');
                break;
            case 'right':
                layerLeft=offset.left-popupWidth - popupOffset;
                layerTop=offset.top;
                $popupLayer.addClass('z-right');
                break;
            default :
                break;
        }

        $popupLayer.css({
            'left':layerLeft,
            'top':layerTop - config.height/2,
            'position':'absolute',
            'height':config.height||'auto',
            'width':config.width||'auto'
        }).show();

        $(this).on('mouseenter', function () {
            $popupLayer.data('isDisplay',true);
        }).on('mouseleave', function () {

            popupTimer=setTimeout(function () {

                if (!$popupLayer.data('isDisplay')) {
                    $popupLayer.hide();
                    $popupLayer.data('isDisplay',false);
                }

            },50);
        });

        $popupLayer.on('mouseenter', function () {
            popupTimer?clearTimeout(popupTimer):null;
        }).on('mouseleave', function () {
            $(this).hide();
            clearTimeout(popupTimer);
        });
    },
    scrollList: function (config) {
        /*
         * scrollList
         * @config {size:[Number]最小滚动行数,length:[Number]每行个数,height:[Number]最小滚动高度,time:[Number]滚动间隔时间,stoppabled:[bool]是否可以经过停止}
         */
        this.each(function () {
            var _this = $(this),
                scrollSize = _this.find("li").length / config.length;

            if (scrollSize > config.size) {
                var timer = scrollTimer();

                if(config.stoppabled){
                    _this.on('mouseenter', function () {
                        clearInterval(timer);
                    }).on('mouseleave', function () {
                        timer = scrollTimer();
                    });
                }
            }
            //定时动画效果
            function scrollTimer(){
                return setInterval(function () {
                    var scrollItems = _this.find("li:visible");
                    _this.animate({marginTop: -config.height}, 700, function () {
                        scrollItems.slice(0,config.length).appendTo(_this);
                        _this.css("margin-top", 0);
                    });
                }, config.time);
            }
        })
    }
});
//placeholder
(function ($) {
    $.fn.extend({
        "placeholder": function (options) {
            options = $.extend({
                placeholderColor: '#ACA899',
                isUseSpan: false, //是否使用插入span标签模拟placeholder的方式,默认false,默认使用value模拟
                onInput: true //使用标签模拟(isUseSpan为true)时，是否绑定onInput事件取代focus/blur事件
            }, options);

            $(this).each(function () {
                var _this = this;
                var supportPlaceholder = 'placeholder' in document.createElement('input');
                if (!supportPlaceholder) {
                    var defaultValue = $(_this).attr('placeholder');
                    var defaultColor = $(_this).css('color');
                    if (options.isUseSpan == false) {
                        $(_this).focus(function () {
                            var pattern = new RegExp("^" + defaultValue + "$|^$");
                            pattern.test($(_this).val()) && $(_this).val('').css('color', defaultColor);
                        }).blur(function () {
                            if ($(_this).val() == defaultValue) {
                                $(_this).css('color', defaultColor);
                            } else if ($(_this).val().length === 0) {
                                $(_this).val(defaultValue).css('color', options.placeholderColor);
                            }
                        }).trigger('blur');
                    } else {
                        var $imitate = $('<span class="wrap-placeholder" style="position:absolute; display:inline-block; overflow:hidden; color:' + options.placeholderColor + '; width:' + $(_this).outerWidth() + 'px; height:' + $(_this).outerHeight() + 'px;">' + defaultValue + '</span>');
                        $imitate.css({
                            'margin-left': $(_this).css('margin-left'),
                            'margin-top': $(_this).css('margin-top'),
                            'font-size': $(_this).css('font-size'),
                            'font-family': $(_this).css('font-family'),
                            'font-weight': $(_this).css('font-weight'),
                            'padding-left': parseInt($(_this).css('padding-left')) + 2 + 'px',
                            'line-height': _this.nodeName.toLowerCase() == 'textarea' ? $(_this).css('line-weight') : $(_this).outerHeight() + 'px',
                            'padding-top': _this.nodeName.toLowerCase() == 'textarea' ? parseInt($(_this).css('padding-top')) + 2 : 0
                        });
                        $(_this).before($imitate.click(function () {
                            $(_this).trigger('focus');
                        }));

                        $(_this).val().length !== 0 && $imitate.hide();

                        if (options.onInput) {
                            //绑定oninput/onpropertychange事件
                            var inputChangeEvent = typeof (_this.oninput) == 'object' ? 'input' : 'propertychange';
                            $(_this).bind(inputChangeEvent, function () {
                                $imitate[0].style.display = $(_this).val().length !== 0 ? 'none' : 'inline-block';
                            });
                        } else {
                            $(_this).focus(function () {
                                $imitate.hide();
                            }).blur(function () {
                                /^$/.test($(_this).val()) && $imitate.show();
                            });
                        }
                    }
                }
            });
            return this;
        }
    });
})(jQuery);
$('input[type="text"]:visible,input[type="password"]').placeholder({
    isUseSpan: true
});
// 时间格式化 useage:formatDate("1408609262000","yyyy-mm-dd");
function formatDate(timestamp, format) {
    if (typeof timestamp !== "undefined") {
        var date = new Date(Number(timestamp)),
            oDate = "",
            year = date.getFullYear(),
            month = date.getMonth() + 1,
            day = date.getDate(),
            hours = date.getHours(),
            minutes = date.getMinutes(),
            seconds = date.getSeconds();
        var time = [month, day, hours, minutes, seconds];
        for (var i in time) {
            if (time[i] < 10) {
                time[i] = "0" + time[i];
            }
        }
        if (typeof format === "undefined") {
            oDate = year + "-" + time[0] + "-" + time[1];
            return oDate;
        } else {
            switch (format) {
                case "yyyy-mm-dd HH:mm:ss":
                    oDate = year + "-" + time[0] + "-" + time[1] + " " + time[2] + ":" + time[3] + ":" + time[4];
                    break;
                case "yyyy-mm-dd HH:mm":
                    oDate = year + "-" + time[0] + "-" + time[1] + " " + time[2] + ":" + time[3];
                    break;
                case "yyyy-mm-dd":
                    oDate = year + "-" + time[0] + "-" + time[1];
                    break;
                case "HH:mm:ss":
                    oDate = time[2] + ":" + time[3] + ":" + time[4];
                    break;
                case "HH:mm":
                    oDate = time[2] + ":" + time[3];
                    break;
                default:
                    break;
            }
            return oDate;
        }

    } else {
        throw new Error("formatDate():timestamp is not defined");
    }
}

// 遮罩层
function xShade(action) {
    var shade = $(".u-shade");
    if (typeof action === "undefined" || action === "show") {
        if (shade.length === 0) {
            var body=$("body"),
                height = body.height(),
                html = "<div class='u-shade' style='height:" + height + "px;'></div>";
            body.append(html);
        } else {
            shade.show();
        }
    } else if (action === "hide") {
        shade.remove();
    }
}

//window.log
(function () {
    var _this=this;
    this.log=function(){};
    if(this.chrome||this.mozInnerScreenX){
        this.log=console.log.bind(console)
    }else if(this.console){
        console.log=this.log
    }else{
        this.console={
            log:_this.log
        }
    }
})(window);

(function () {
    //基础方法浏览器兼容性修正
    /**
     * Namespace
     * @param {String} namespace 'YR.util.formatDate'
     * @return {Object}
     * */
    window.ns=function(namespace){
        "use strict";
        var _ns = namespace.split('.'),
            cur=window[_ns[0]];
    if(typeof(cur)==='undefined'){
            cur=window[_ns[0]]={};
        }

        var len=_ns.length;

        for(var i=1;i<len;i++){
            cur=cur[_ns[i]]=cur[_ns[i]]||{};
        }
        return cur;
    };

    //替代原生toFixed
    _.mixin({
        toFixed: function (number,size) {
            var n=number-0||0,
                s=size-0||2;
            return _.round(n,s).toFixed(s);
        }
    },{'chain':false});     
    //Date.now
    if (!Date.now) {
        Date.now = function() { return new Date().getTime(); };
    }

    // 模拟多选框
    $(".j-ckbox").on("click", function () {
        if (!$(this).attr("disabled")) {
            if ($(this).hasClass("z-checked")) {
                $(this).removeClass("z-checked").children("input")
                    .removeAttr("checked");
            } else {
                $(this).addClass("z-checked").children("input").attr(
                    "checked", "checked");
            }
        }
    });

    // 模拟选择框
    var $selector = $(".j-selector");
    $selector.on("click", function (e) {
        $(this).toggleClass("z-actived");
        e.preventDefault();
        return false;
    }).on("mouseleave", function () {
        $(this).removeClass("z-actived");
    });
    $(".j-selector ul").on("mouseenter", function () {
        $(this).parent().addClass("z-actived");
    });
    $(".u-scroll-selector ul").scroll(function (e) {
        e.stopPropagation();
    });
    $selector.on("click", "li", function () {
        var text = $(this).html(),
            val = $(this).attr("value"),
            parent = $(this).parents();
        //只限可用状态下选择
        if(!$(this).hasClass("z-disabled")){
            parent.siblings("button").html(text);
            parent.siblings(".j-selected-ipt").val(val);
        }else{
           return false;
       }

    });
var $couponSelect=$('.j-couponSelector')

    $couponSelect.on("click", function (e) {
        $(this).toggleClass("z-actived");
        e.preventDefault();
        return false;
    }).on("mouseleave", function () {
        $(this).removeClass("z-actived");
    });
    $(".j-couponSelector ul").on("mouseenter", function () {
        $(this).parent().addClass("z-actived");
    });
    $(".u-scroll-selector ul").scroll(function (e) {
        e.stopPropagation();
    });
    $couponSelect.on("click", "li", function () {
        var text = $(this).children('em').html(),
            val = $(this).attr("value"),
            parent = $(this).parent().parent();
        //只限可用状态下选择
        if(!$(this).hasClass("z-disabled")){
            parent.prevAll("button").html(text);
            parent.siblings(".j-selected-ipt").val(val);
        }else{
            return false;
        }

    });
    // 用户提示
    $(".j-user-tips").on("mouseenter", function () {
        var content = $(this).data("tips"),
            skin = $(this).data("skin"),
            align = $(this).data("align");
        align = typeof align === "undefined" ? "bottom" : align;
        content = typeof content === "undefined" ? "" : content;
        skin = typeof skin === "undefined" ? "u-user-tips" : skin;
        var d = dialog({
            align: align,
            skin: skin,
            content: content
        });
        d.show(this);
        $(this).data("hoverObj", d);
    }).on("mouseleave", function () {
        var d = $(this).data("hoverObj");
        d.close().remove();
    });

    //首页新闻
    $(".j-scroll-news").fadeShow({
        time: 5000,
        type: "news"
    });
    //首页焦点图
    var focusShowObj = $(".j-focus-show"),
        focusCount = focusShowObj.data("size");
    if (focusCount > 1) {
        focusShowObj.fadeShow({
            time: 10000,
            type: "banner"
        });
    }
    //浮动层定位
    $(".j-floatlayer").floatLayer();
    //每日签到
    var dailyCheckIn = {
        config: {
            target: "#j-checkin-box",
            closeBtn: "#j-checkin-close",
            diceImg: "#j-dice-action",
            signInUrl: "/member/check/"
        },
        show: function () {
            var _this = this;
            //显示阴影和签到浮层
            xShade("show");
            $(_this.config.target).show();
            //显示动画
            this.diceAnimate();
            //定时器
            setTimeout(function () {
                //clearInterval(_this.timer);
                _this.getDiceNumber();
            }, 1750);
            //关闭窗口
            $(this.config.closeBtn + ",.u-shade").on("click", function () {
                _this.close();
                window.location.reload();
            });
        },
        close: function () {
            $(this.config.target).hide();
            xShade("hide");
            if ($("#j-reputation-checkin").length === 1) {
                window.location.reload();
            }
        },
        //骰子动画
        diceAnimate: function () {
            var diceImg = this.config.diceImg,
                count = 0;
            this.timer = setInterval(function () {
                count += 1;

                if (count > 4) {
                    count = 1;
                }
                $(diceImg).attr("class", "u-dice-action u-dice-b" + count);
            }, 120);
        },
        //获取骰子(人气值)点数
        getDiceNumber: function () {
            var _this = this;
            $.getJSON(this.config.signInUrl, function (data) {
                if (data.success) {
                    clearInterval(_this.timer);
                    //显示签到数据
                    var basePoint = data.result.gainPopularity,
                        doublePoint= data.result.popularityDouble,
                        point = basePoint,
                        pointText = point,
                        checkInBtn = $("#j-reputation-checkin");
                    if (doublePoint>1) {
                        pointText = basePoint + "x"+doublePoint;
                    }
                    //是否生日处理
                    var isBirthday = $(_this.config.target).data('isBirthday'),
                        $diceImg=$(_this.config.diceImg);
                    $diceImg
                        .prop("class","u-dice-action u-dice-a" + point);
                    $("#j-checkin-btn").remove();
                    if(isBirthday){
                        $(_this.config.target).addClass('z-birthday')
                            .find(".j-checkin-point").text(pointText)
                            .parent().fadeIn();
                    }else{
                        $(_this.config.target)
                            .find(".j-checkin-point").text(pointText)
                            .parent().fadeIn();
                        if(doublePoint>1){
                            $diceImg.after('<em>x&nbsp;<span>'+doublePoint+'</span></em>');
                        }
                    }

                    //人气值页面签到
                    if (checkInBtn.length === 1) {
                        checkInBtn.data("isChecked", true)
                            .addClass("u-myinvite-signInLater")
                            .removeClass("u-myinvite-signIn")
                            .find("span").html("<i>+</i><em class='f-ff-amount'>" + basePoint + "</em><div class='f-ff-dian'>点</div>")
                            .next("i").addClass("f-fs14").removeClass("f-fs16")
                            .text("今日签到人气值");
                        var pv = $("#j-popularity-value");
                        pv.text(Number(pv.text()) + basePoint);
                    }
                } else {
                    //错误处理
                    var errorCode = Number(data.resultCodeEum[0].code);
                    switch (errorCode) {
                        case 90050:
                            $.xDialog({
                                content: "您已签到，请勿重复操作。",
                                callback: function () {
                                    xShade("hide");
                                    window.location.reload();
                                }
                            });
                            break;
                        default:
                            break;
                    }

                }
            });
        }
    };
    if ($("#j-checkin-box").length === 1) {
        $("#j-checkin-btn,#j-reputation-checkin").on("click", function (e) {
            e.stopPropagation();
            if (typeof($(this).data("isChecked")) !== "undefined") {
                return false;
            }
            dailyCheckIn.show();
            var shadeLayer = $("#j-banner-shade");
            if (shadeLayer.length) {
                shadeLayer.remove();
                $("#j-checkin-guide").remove();
            }
            return false;
        });
    }

    //validform(首页不加载)
    var pagetag=$('#j-pagetag').val();
    if(!pagetag||pagetag!=='index'){

        //开启验证提示
        window.formValid = $(".j-validform").Validform({
            tiptype: 3,
            postonce: true,
            datatype: {
                //中文
                "zh2-4": /^[\u4E00-\u9FA5]{2,4}$/,
                //少数民族姓名
                "zhs": /^[\u4E00-\u9FA5]{2,10}(?:·[\u4E00-\u9FA5]{2,10})*$/,
                //密码
                "pwd": /^(?![^a-zA-Z]+$)(?!\D+$).{6,16}$/,
                "mobile": /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/,
                //货币
                "money": /^[1-9]*[1-9][0-9]*(.[0-9]{1,2})?$|^(0.[0-9]{1,2})$/,
                //身份证
                "idcard": function (gets, obj, curform, datatype) {
                    var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1]; // 加权因子;
                    var ValideCode = [1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2]; // 身份证验证位值，10代表X;

                    if (gets.length == 15) {
                        return isValidityBrithBy15IdCard(gets);
                    } else if (gets.length == 18) {
                        var a_idCard = gets.split(""); // 得到身份证数组
                        if (isValidityBrithBy18IdCard(gets) && isTrueValidateCodeBy18IdCard(a_idCard)) {
                            return true;
                        }
                        return false;
                    }
                    return false;

                    function isTrueValidateCodeBy18IdCard(a_idCard) {
                        var sum = 0; // 声明加权求和变量
                        if (a_idCard[17].toLowerCase() == 'x') {
                            a_idCard[17] = 10; // 将最后位为x的验证码替换为10方便后续操作
                        }
                        for (var i = 0; i < 17; i++) {
                            sum += Wi[i] * a_idCard[i]; // 加权求和
                        }
                        var valCodePosition = sum % 11; // 得到验证码所位置
                        if (a_idCard[17] == ValideCode[valCodePosition]) {
                            return true;
                        }
                        return false;
                    }

                    function isValidityBrithBy18IdCard(idCard18) {
                        var year = idCard18.substring(6, 10);
                        var month = idCard18.substring(10, 12);
                        var day = idCard18.substring(12, 14);
                        var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
                        // 这里用getFullYear()获取年份，避免千年虫问题
                        if (temp_date.getFullYear() != parseFloat(year) || temp_date.getMonth() != parseFloat(month) - 1 || temp_date.getDate() != parseFloat(day)) {
                            return false;
                        }
                        return true;
                    }

                    function isValidityBrithBy15IdCard(idCard15) {
                        var year = idCard15.substring(6, 8);
                        var month = idCard15.substring(8, 10);
                        var day = idCard15.substring(10, 12);
                        var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
                        // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法
                        if (temp_date.getYear() != parseFloat(year) || temp_date.getMonth() != parseFloat(month) - 1 || temp_date.getDate() != parseFloat(day)) {
                            return false;
                        }
                        return true;
                    }

                },
                //4-14位中英文字符
                "z3-14": function (gets, obj, curform, datatype) {
                    var reg = /^[\u4E00-\u9FA5\uf900-\ufa2dA-Za-z]+$/;
                    if (reg.test(gets)) {
                        var strLength = getStrLeng(gets);
                        if (strLength < 3 || strLength > 14) {
                            return false;
                        }
                        return true;
                    }
                    return false;

                    function getStrLeng(str) {
                        var realLength = 0;
                        var len = str.length;
                        var charCode = -1;
                        for (var i = 0; i < len; i++) {
                            charCode = str.charCodeAt(i);
                            if (charCode >= 0 && charCode <= 128) {
                                realLength += 1;
                            } else {
                                // 如果是中文则长度加2
                                realLength += 2;
                            }
                        }
                        return realLength;
                    }
                },
                //日期
                "date": /^(?:(?:1[6-9]|[2-9][0-9])[0-9]{2}([-/.]?)(?:(?:0?[1-9]|1[0-2])\1(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])\1(?:29|30)|(?:0?[13578]|1[02])\1(?:31))|(?:(?:1[6-9]|[2-9][0-9])(?:0[48]|[2468][048]|[13579][26])|(?:16|[2468][048]|[3579][26])00)([-/.]?)0?2\2(?:29))(\s+([01][0-9]:|2[0-3]:)?[0-5][0-9]:[0-5][0-9])?$/
            }
        });
        //验证规则
        formValid.addRule([{
            ele: ".v-sex", //性别
            datatype: "*",
            nullmsg: "请选择性别",
            errormsg: "请选择性别"
        }, {
            ele: ".v-edu", //学历
            datatype: "*",
            nullmsg: "请选择学历",
            errormsg: "请选择学历"
        }, {
            ele: ".v-mob", //手机
            datatype: "mobile",
            nullmsg: "手机号码不能为空",
            errormsg: "请输入正确的手机号码"
        }, {
            ele: ".v-pwd", //密码
            datatype: "pwd",
            nullmsg: "请输入密码",
            errormsg: "长度6-16位至少包含数字和字母"
        }, {
            ele: ".v-pwd-reck", //密码确认(注意添加这个class的时候，需要添加recheck属性,例如：recheck="password"，password为密码input的name值)
            datatype: "pwd",
            nullmsg: "请再输入一次密码",
            errormsg: "您两次输入的密码不一致"
        }, {
            ele: ".v-oldPwd", //密码
            datatype: "pwd",
            nullmsg: "请输入旧密码",
            errormsg: "长度6-16位至少包含数字和字母"
        }, {
            ele: ".v-newPwd", //密码
            datatype: "pwd",
            nullmsg: "请输入新密码",
            errormsg: "长度6-16位至少包含数字和字母"
        }, {
            ele: ".v-rname", //真实姓名 2-4中文
            datatype: "zhs,*2-25",
            nullmsg: "请填写姓名",
            errormsg: "请填写中文姓名"
        }, {
            ele: ".v-select-req",
            datatype: "*",
            nullmsg: "此项为必选",
            errormsg: "此项为必选"
        }, {
            ele: ".v-input-req",
            datatype: "*",
            nullmsg: "此项为必填",
            errormsg: "此项为必填"
        }, {
            ele: ".v-loginid", //昵称 nickname
            datatype: "mobile|z3-14",
            nullmsg: "请填写手机或昵称",
            errormsg: "请填写正确的手机号码或昵称"
        }, {
            ele: ".v-job", //职业
            datatype: "*",
            nullmsg: "请选择职业",
            errormsg: "请选择职业"
        }, {
            ele: ".v-n1-10", //金额 没有小数
            datatype: "n1-10",
            nullmsg: "请填写信息",
            errormsg: "请填写1到10位数字"
        }, {
            ele: ".v-money", //金额
            datatype: "money",
            nullmsg: "请填写金额",
            errormsg: "金额不能小于0.01元"
        }, {
            ele: ".v-bankcard", //银行卡
            datatype: "n16-19",
            nullmsg: "请填写银行卡",
            errormsg: "请填写正确的银行卡号"
        }, {
            ele: ".v-bank-reck", //银行卡确认 (注意添加这个class的时候，需要添加recheck属性)
            datatype: "n16-19",
            nullmsg: "请再输入一次银行卡",
            errormsg: "您两次输入的银行卡号不一致"
        }, {
            ele: ".v-idcard", //身份证
            datatype: "idcard",
            nullmsg: "请填写身份证",
            errormsg: "请填写正确的身份证"
        }]);
        //验证表单填写正确统一不提示信息
        formValid.tipmsg.r = " ";
    }
})();

//阻止Enter ['实名认证','个人资料','添加银行卡','验证码输入框']
$("#memberIdentity_form input[type='text'], #memberInfoForm input[type='text'], #bankAdd_form,#j-bank-checkcode").on("keydown", function (e) {
    var ev = e || event;
    if (ev.keyCode == 13) {
        ev.keyCode = 0;
        return false;
    }
});

$(".area-select").on("click", "li", function () {
    // var val = $(this).html();
    var areaSelect = $(this).closest(".area-select");
    // var btnText = areaSelect.find("button").html();
    // console.log(val+" "+btnText);
    // if(val == btnText){
    // return;
    // }
    var areaId = areaSelect.data("id");
    selectRegion(areaSelect.attr("value"), areaId, eval(areaId + "LoadAreaByParentIdCallback"));
});
// 弹出提示 一般ajax调用之后显示错误信息
function showErrorMessage(data) {
    if (data.resultCodeEum != null && data.resultCodeEum != "") {
        var message = "";
        for (var x in data.resultCodeEum) {
            message = data.resultCodeEum[x].msg + message;
        }
        $.xDialog({
            content: message,
            type: 'error'
        });
    } else {
        $.xDialog({
            content: data.result,
            type: 'error'
        });
    }
}
//充值，支付，调用
function showErrorResultMessage(data) {
    var errorMsg = "支付失败";
    if (data.result != null || data.result == "") {
        errorMsg = data.result;
    } else {
        if (data.resultCodeEum != null && data.resultCodeEum != "") {
            var message = "";
            for (var x in data.resultCodeEum) {
                message = data.resultCodeEum[x].msg + message;
            }
            errorMsg = message;
        }
    }
    $.xDialog({
        content: errorMsg
    });
}

(function(){
    var pnt = $("#j-project-notice-time");
    if (pnt.length > 0) {
        noticeProjectTimer(pnt.attr("sencond"));
    }
    $(".j-index-notice").on("click", function () {
        var notice = $("#j-project-notice");
        if (!notice.hasClass("detail-notice") && notice.data("url") != "") {
            window.location.href = notice.data("url");
        }
    });

    //推送用户未读消息
    pushUnreadMessage(300);

    //移动端显示APP下载提示
    var mobileReg=/iPhone|Android/i;
    if(!mobileReg.test(navigator.userAgent)){
        $("#j-app-download").remove();
    }

    //banner浮动层显示
    $(".j-layer-close").toggleLayerDisplay();

    //二维码预览
    $(".j-app-qrcode").bigImgPreview();
})();
// 箭头的定位（用户中心tab也切换时）
function arrowLocator(menuId) {
    if (!!menuId) {
        var currentMenu = $(menuId).find(".z-current"),
            position = currentMenu.position(),
            uWidth = currentMenu.width(),
            iWidth,
            left;
        if (typeof position == "undefined") {
            return;
        }
        var currentI = $(menuId).children("i");
        if (currentI.length === 1) {
            iWidth = currentI.width();
            left = position.left + uWidth / 2 - iWidth / 2;
            currentI.css({
                "left": left
            });

        } else {
            var arrow = $(menuId).find("#j-profit-arrow");
            if (arrow.length > 0) {
                iWidth = arrow.width();
                left = position.left + uWidth / 2 - iWidth / 2;
                arrow.css({
                    "left": left
                });
            }

        }

    }
}
// 获取URL参数 设置cookie
$(function () {
    var registerTraceNo = getUrlParam('registerTraceNo');
    var registerTraceSource = getUrlParam('registerTraceSource');
    //    var cookie_path = window.location.pathname;  
    //    cookie_path = cookie_path.substring(0, cookie_path.lastIndexOf('/') + 1);
    var date = new Date();
    date.setTime(date.getTime() + (30 * 60 * 1000));

    if (registerTraceNo != null) {

        SetCookie("registerTraceNo", registerTraceNo, date);
    }
    if (registerTraceSource) {
        SetCookie("registerTraceSource", registerTraceSource, date);

    }


});

function SetCookie(name, value, exp) //两个参数，一个是cookie的名子，一个是值
{
    var Days = 30; //此 cookie 将被保存 30 天
    // var exp  = new Date();    //new Date("December 31, 9998");
    exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
    document.cookie = name + "=" + escape(value) + ";path=/" + ";expires=" + exp.toGMTString();
}

// 获取URL参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); // 匹配目标参数
    if (r != null)
        return unescape(r[2]);
    return null; // 返回参数值
}
// 箭头的定位（项目列表）
function projectArrowLocator() {
    var currentMenu = $(".u-plist-tab").find(".z-current"),
        index = currentMenu.index(),
        currentSpan = $(".u-plist-status").find("span"),
        currentI = $(".u-plist-status").find("i"),
        position = currentMenu.position(),
        iWidth = currentI.outerWidth(),
        uWidth = currentMenu.outerWidth(),
        left = uWidth * index;
    currentI.css({
        "left": left
    });
    currentSpan.css({
        "left": left
    });
}

/**
 * 预告倒计时
 *
 * @param seconds
 *            秒
 */
function noticeProjectTimer(seconds) {
    var hh = 0,
        mm = 0,
        ss = 0;

    function setNoticeTime() {
        hh = numberAppendZero(Math.floor((seconds / 3600)));
        mm = numberAppendZero(Math.floor((seconds / 60) % 60));
        ss = numberAppendZero(Math.floor(seconds % 60));
        $("#j-project-notice-time").text(hh + ":" + mm + ":" + ss);
    }

    if (seconds > 0) {
        setNoticeTime();
    }
    var noticeProjectInterval = setInterval(function () {
        if (seconds > 1) {
            seconds--;
            setNoticeTime();
        } else {
            clearInterval(noticeProjectInterval);
            var projectNotice = $("#j-project-notice");
            if (!projectNotice.hasClass("detail-notice")) {
                projectNotice.text("立即投资");
                projectNotice.removeClass("z-disabled");
                projectNotice.addClass("z-actived");
                projectNotice.attr("href", projectNotice.data("url"));
            } else {
                projectNotice.removeClass("detail-notice");
                $("#j-now-order").show();
                projectNotice.hide();
            }
        }
        // seconds--;
    }, 1000);
}

/**
 * 小于10在数字前追加0
 *
 * @param num
 * @returns {String}
 */
function numberAppendZero(num) {
    if (num < 10) {
        num = "0" + num;
    }
    return num;
}

// 限制金额输入,只允许输入数字与小数点    
function checkKeyForFloat(value, e) {
    var isOK = false;
    var key = window.event ? e.keyCode : e.which;
    if ((key > 95 && key < 106) || //小键盘上的0到9  
        (key > 47 && key < 60) || //大键盘上的0到9  
        (key == 110 && value.indexOf(".") < 0) || //小键盘上的.而且以前没有输入.  
        (key == 190 && value.indexOf(".") < 0) || //大键盘上的.而且以前没有输入.  
        key == 8 || key == 9 || key == 46 || key == 37 || key == 39 //不影响正常编辑键的使用(8:BackSpace;9:Tab;46:Delete;37:Left;39:Right)  
    ) {
        isOK = true;
    } else {
        if (window.event) //IE    
        {
            e.returnValue = false; //event.returnValue=false 效果相同.    
        } else //Firefox    
        {
            e.preventDefault();
        }
    }
    if (e.shiftKey) { //或者event.keyCode==16 也是可行的。
        e.cancelBubble = true;
        e.keyCode = 0;
        isOK = false;
    }
    return isOK;
}
/*
 * 签到引导
 * @method sigininGuide
 * @param target {String} css选择符
 * @author Adeweb
 */
function signinGuide(target) {
    var checkinState = $.xStorage('checkinDaily'),
        today = formatDate(environment.serverDate, "yyyy-mm-dd").replace(/-/g, ""),
        checkinGuide = $("#j-checkin-guide");
    if (checkinState && checkinState === today) {

        if (checkinGuide.length === 1) {
            checkinGuide.remove();
        }
        return false;
    }
    //加载阴影层
    var layerParent = $(target);
    if (layerParent.find(".u-shade").length === 0 && checkinGuide.length === 1) {

        layerParent.append("<div class='u-shade u-signin-shade' id='j-banner-shade'></div>");

        //关闭按钮&试试手气按钮
        var shadeLayer = $("#j-banner-shade");

        checkinGuide.show().on("click", "span", function () {
            $.xStorage('checkinDaily',today);

            checkinGuide.remove();
            shadeLayer.remove();
        }).on("click", "button", function () {

            if (!$(this).data("logined")) {
                window.location.href = "/security/login/";
            } else {
                checkinGuide.remove();
                shadeLayer.remove();
                $("#j-checkin-btn").click();
            }
        });
    }

}

/**
 * 推送用户未读消息
 * @param seconds 获取消息的频率
 */
function pushUnreadMessage(seconds) {
    var isLogined = !!$(".m-user-menu").length,
        isMessagesPage = !!$("#j-umsg-filter").length,
        curDate = new Date().getTime(),
        curPageUrl = location.href.replace(environment.globalPath, ""),
        noticeObj = $("#j-umsg-notice");

    //初始加载
    getMesssagesCount("onload");

    var msgTimer = setInterval(function () {
        getMesssagesCount("onTimer");
    }, seconds * 1000);

    /*
     * 获取消息数
     * @param event {Sting} 'onload'=>页面加载时,'onTimer'=>定时器触发时
     */
    function getMesssagesCount(event) {
        //登录且非消息页才加载
        if (isLogined && !isMessagesPage) {
            var msgPageUrl = $.xStorage('msgNotice.url'),
                msgCount = Number($.xStorage("msgNotice.count"));

            //独占机制
            switch (event) {
                case 'onload':
                    $.xStorage('msgNotice.url', curDate + ":" + curPageUrl);
                    //console.log("onload:消息被此页 "+curPageUrl+" 独占");
                    break;
                case 'onTimer':
                    if (!msgPageUrl) {
                        $.xStorage('msgNotice.url', curDate + ":" + curPageUrl);

                        //console.log("onTimer:消息被此页 "+curPageUrl+" 独占");
                    } else if (msgPageUrl !== curDate + ":" + curPageUrl) {
                        if (msgCount) {
                            $("#j-umsg-number").text(msgCount);
                            noticeObj.removeClass("z-hidden");
                        } else {
                            noticeObj.addClass("z-hidden");
                        }
                        //console.log(curPageUrl+":get messages from localStorage");
                        return false;
                    }
                    break;
                default:
                    break;
            }


            //request
            $.ajax({
                url: environment.globalPath + "/message/unreadMessage",
                type: "GET",
                dataType: "json",
                success: function (data) {
                    if (data.success) {

                        if (data.result > 0) {
                            $("#j-umsg-number").text(data.result);
                            noticeObj.removeClass("z-hidden");

                        } else {
                            noticeObj.addClass("z-hidden");
                        }
                        $.xStorage("msgNotice.count", data.result);
                    }
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    clearInterval(msgTimer);
                    $.xStorage('msgNotice.url',null);
                }
            });
        } else {
            return false;
        }
    }

    //对session影响的处理
    setTimeout(function(){
        if(msgTimer){
            clearInterval(msgTimer);
        }
    },30*60*1000+1000);

    //关闭页面处理
    $(window).unload(function () {
        var msgPageUrl = $.xStorage('msgNotice.url'),
            curPageUrl = location.href.replace(environment.globalPath, "");
        if (msgPageUrl === curPageUrl) {
            $.xStorage('msgNotice.url',null);
        }
    });
}

//获取缩略图
function getProjectThumbnail(thumbnail) {
    if(thumbnail == undefined || thumbnail === '') {
        return environment.globalPath + '/static/img/common/default-product.jpg';
    } else {
        return environment.aliyunPath + '/' + thumbnail;
    }
}

//日常维护区块
(function () {
    //页面底部工作时间
    var $timezone=$('#j-server-timezone'),
        $timezone1=$('#j-server-timezone1'),
        month=new Date(environment.serverDate).getMonth()+1;
    if(month<4||month>9){
        $timezone.text('9:00-17:00');
        $timezone1.text('9:00-17:00');
    }

})();
