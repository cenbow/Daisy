/**
 * @module Yourong jQuery Extends
 */
import {Util} from 'common/util'

//@Module Image
$.fn.extend({
    /**
     * 焦点图
     * @param config {object} {size:图片数量,time:切换的间隔毫秒数,type:焦点图类型banner||news}
     */
    fadeShow: function (config) {
        var $this = $(this),
            time = 3000,
            type = "",
            size = $this.data("size")
        if (typeof (config) !== "undefined") {
            time = typeof (config.time) !== "undefined" ? config.time : 3000
            size = typeof (size) !== "undefined" ? size : 3
            type = typeof (config.type) !== "undefined" ? config.type : "banner"
        } else {
            throw new Error("fadeShow():config is not defined")
        }

        function fadeSlider() {
            var news = $this.find("ul li.z-current").eq(0),
                allNews = $this.find("ul li"),
                dot = $this.find("ol li"),
                i = news.index(),
                n = 0
            if (i >= 0 && i < (size - 1)) {
                news.next().css("z-index", "50")
                news.css("z-index", "51").fadeOut(500, function () {
                    news.show().css("z-index", "-1").removeClass("z-current")
                    news.next().css("z-index", "50").addClass("z-current")

                })
                n = i + 1
            } else if (i === (size - 1)) {
                allNews.eq(0).css("z-index", "50")
                news.css("z-index", "51").fadeOut(500, function () {
                    news.show().css("z-index", "-1").removeClass("z-current")
                    allNews.eq(0).css("z-index", "50").addClass("z-current")

                })
                n = 0
            }
            dot.eq(n).addClass("z-current").siblings().removeClass("z-current")
        }

        //定时
        var timer = setInterval(function () {
            fadeSlider()
        }, time)

        //鼠标经过停止计时
        $this.on("mouseenter", "ul li", function () {
            clearInterval(timer)
        })

        //鼠标离开继续计时
        $this.on("mouseleave", "ul li", function () {
            timer = setInterval(function () {
                fadeSlider()
            }, time)
        })

        $this.on("click", "ol li:not(.z-current)", function () {
            var i = $(this).index(),
                thisBanner = $this.find("ul li").eq(i),
                thisEffect = thisBanner.data("effect")
            $(this).addClass("z-current").siblings().removeClass("z-current")
            $this.find("ul li").eq(i).css("z-index", "50").addClass("z-current")
                .siblings().css("z-index", "-1").removeClass("z-current")

            clearInterval(timer)
        })
    },

    /**
     * banner倒计时
     * @param {String} endDate 结束时间，格式：'2015/07/31 23:59:59'
     * @param {String} effectId banner的唯一标示(在backend指定)
     */
    bannerCountdown: function (endDate, effectId) {
        var curDate = new Date(environment.serverDate),
            endTime = new Date(endDate),
            days = endTime.getDate() - curDate.getDate() + 1
        if (curDate > endTime) {
            days = 0
        }
        if (days < 10) {
            if (days < 0) {
                days = 0
            }
            days = "0" + days
        }
        $(this).find('[data-effect="' + effectId + '"]')
            .append('<div class="u-countdown-wrap"><p class="u-' + effectId + '-countdown">' + days + '</p></div>')
    },

    //二维码大图预览
    qrcodePreview: function () {
        var body = $("body"), leave = false, timer, stamp

        this.each(function () {

            $(this).on("mouseenter", function () {
                var imgUrl = $(this).data("imgurl"),
                    bLeft = $(this).offset().left,
                    bTop = $(this).offset().top,
                    direction = $(this).data("direction")
                stamp = new Date().getTime()
                $(".j-qrcode-img").remove()
                switch (direction) {
                    case "leftTop":
                        bTop = bTop - 200 + $(this).height()
                        break
                    case "centerbottom":
                        bLeft = bLeft - 100 + 10
                        bTop = bTop + 22
                        leave = true
                        break
                    default:
                        break
                }

                var bigImgHtml = '<img src="' + imgUrl + '" class="u-qrcode-img j-qrcode-img" id="j-img-' + stamp + '" style="left:' +
                    bLeft + 'px;top:' + bTop + 'px"/>'
                body.append(bigImgHtml)
            }).on("mouseleave", function () {
                if (leave) {
                    timer = setTimeout(function () {
                        stamp = stamp || 0
                        body.find("#j-img-" + stamp).remove()
                    }, 300)
                }
            })
        })
        body.on("mouseenter", ".j-qrcode-img", function () {
            if (timer) {
                clearTimeout(timer)

            }
        }).on("mouseleave", ".j-qrcode-img", function () {

            $(this).remove()
        })
    }
})

//@Module Form
$.fn.extend({
    //模拟多选框 //TODO need to test
    xCheckBox: function () {
        this.each(function () {
            $(this).on("click", function () {

                if (!$(this).attr("disabled")) {

                    if ($(this).hasClass("z-checked")) {

                        $(this).removeClass("z-checked").children("input")
                            .removeAttr("checked")
                    } else {
                        $(this).addClass("z-checked").children("input").attr(
                            "checked", "checked")
                    }
                }
            })
        })
    },

    //模拟下拉选择框 //TODO need to test
    xSelector: function () {
        this.each(function () {
            var $selector = $(this)

            $selector.on("click", function (e) {
                $(this).toggleClass("z-actived")
                e.preventDefault()
                return false
            }).on("mouseleave", function () {
                $(this).removeClass("z-actived")
            })

            $selector.on("mouseenter",'ul', function () {
                $(this).parent().addClass("z-actived")
            })

            if($selector.hasClass('u-scroll-selector')){
                $selector.on('scroll', function (e) {
                    e.stopPropagation()
                })
            }

            $selector.on("click", "li", function () {
                var text = $(this).html(),
                    val = $(this).attr("value"),
                    parent = $(this).parent()

                //只限可用状态下选择
                if(!$(this).hasClass("z-disabled")){
                    parent.siblings("button").html(text)
                    parent.siblings(".j-selected-ipt").val(val)
                }else{
                    return false
                }
            })
        })
    }
})

//@Module List
$.fn.extend({
    /**
     * scrollList 滚动显示列表
     * @config {size:[Number]最小滚动行数,length:[Number]每行个数,height:[Number]最小滚动高度,
     * time:[Number]滚动间隔时间,stoppabled:[bool]是否可以经过停止}
     */
    scrollList: function (config) {
        this.each(function () {
            var $this = $(this),
                scrollSize = $this.find("li").length / config.length;

            if (scrollSize > config.size) {
                var timer = scrollTimer();

                if (config.stoppabled) {
                    $this.on('mouseenter', function () {
                        clearInterval(timer);
                    }).on('mouseleave', function () {
                        timer = scrollTimer();
                    });
                }
            }
            //定时动画效果
            function scrollTimer() {
                return setInterval(function () {
                    var scrollItems = $this.find("li:visible");
                    $this.animate({marginTop: -config.height}, 700, function () {
                        scrollItems.slice(0, config.length).appendTo($this);
                        $this.css("margin-top", 0);
                    });
                }, config.time);
            }
        })
    },

    /**
     * switchList 切换显示列表
     * @target {string} $(target) => 切换显示的目标
     */
    switchList:function(target){
        $(this).on('click','h2,h3,h4,span', function () {
            let i = $(this).index()

            $(this).addClass('z-current')
                .siblings().removeClass('z-current')

            $(this).parent().siblings().eq(i).removeClass('f-dn')
                .siblings('ul').addClass('f-dn')
        })
        return this
    }
})

//@Module Loading
$.fn.extend({
    //loading 不建议使用
    loading: function () {
        return this.each(function () {
            $(this).html('<div class="u-loading-box"><div class="u-progress-bar">'+
                '<img src="' + environment.globalPath +
                '/static/img/common/loading.gif"/>请稍候...</div></div>')
        })
    },

    /**
     * xLoading
     * @param callback {function}
     * @param direction {string} loading显示的位置
     * @usage $(target).xLoading(function(){ajaxCallback:target.find('.u-xloading').remove()})
     */
    xLoading: function (callback, direction) {
        var position = "",
            dir = "",
            element

        if (typeof(direction) !== "undefined") {
            dir = "z-dir-" + direction
        }

        element = '<span class="u-xloading u-circle-15 ' + dir + '"></span>'

        position = this.css("positon")

        if (position !== "absolute" || position !== "relative") {
            this.addClass("f-pos-r")
        }

        this.append(element)
        this.loadingDone = function () {
            $(this).find(".u-xloading").remove()
        }

        if (typeof(callback) === "function") {
            callback(this)
        }

    }
})

//@Module Tab
$.fn.extend({
    //TAB箭头的定位，在我的借款，我的优惠等页面用到了
    arrowLocator:function() {
        var $menu = $(this),
            currentMenu = $menu.find(".z-current"),
            position = currentMenu.position(),
            uWidth = currentMenu.width(),
            iWidth,
            left;

        if (typeof position == "undefined") {
            return;
        }
        var currentI = $menu.children("i");

        if (currentI.length === 1) {
            iWidth = currentI.width();
            left = position.left + uWidth / 2 - iWidth / 2;
            currentI.css({
                "left": left
            });

        } else {
            var arrow = $menu.find(".u-arrow-up");

            if (arrow.length > 0) {
                iWidth = arrow.width();
                left = position.left + uWidth / 2 - iWidth / 2;
                arrow.css({
                    "left": left
                });
            }
        }
    }
})

//@Module Tips
$.fn.extend({
    // 问候提示 proto:greetingTime
    greetingTips: function () {
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

    //用户提示 proto:xUserTips
    hoverTips: function () {
        this.each(function () {
            $(this).on("mouseenter", function () {
                var content = $(this).data("tips"),
                    skin = $(this).data("skin"),
                    align = $(this).data("align");
                align = typeof align === "undefined" ? "bottom" : align;
                content = typeof content === "undefined" ? "" : content;
                skin = typeof skin === "undefined" ? "u-user-tips" : skin;
                var d = window.dialog({
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
        })
    }
})

//@Module Limit
$.fn.extend({
    //粘贴事件
    pasteEvents: function (delay) {
        if (typeof (delay) === "undefined") {
            delay = 20
        }
        return $(this).each(function () {
            var $el = $(this)
            $el.on("paste", function () {
                $el.trigger("prepaste")
                setTimeout(function () {
                    $el.trigger("postpaste")
                }, delay)
            })
        })
    },

    //限制只能输入数字
    numOnly: function () {
        $(this).on("keyup", function () {
            var val = $(this).val()
            if (!Number(val)) {
                $(this).val(val.replace(/[^\d]/g, ""))
            }
        })
    },

    //阻止按回车
    stopEnterKey: function () {
        this.each(function () {
            $(this).on("keydown", function (e) {
                var ev = e || event
                if (ev.keyCode === 13) {
                    ev.keyCode = 0
                    return false
                }
            })
        })
    }
})

//@Module Layer
$.fn.extend({
    // 协议弹出层
    xArgreement: function () {
        var $this = $(this),
            url = $this.data("url"),
            tid = $this
                .data("target"),
            $box = $("#" + tid)
        $this.on("click", function () {
            $box.find("iframe").attr("src", url)
            $.shade("show")
            $(".u-shade").css("z-index", 1001)
            $box.fadeIn()
            return false
        })
        $box.find("i").on("click", function () {
            $box.hide()
            $.shade("hide")
            $(".u-shade").css("z-index", 999)
        })
    },

    //浮动层定位
    floatLayer: function () {
        this.each(function () {
            $(this).css({
                "margin-left": -($(this).outerWidth() / 2),
                "margin-top": -($(this).outerHeight() / 2)
            })
        })
    },

    //浮层Banner显示关闭保持
    toggleLayerDisplay: function () {
        this.each(function () {
            var label = $(this).data("label"),
                targetSelctor = $(this).data("target"),
                target = targetSelctor ? $(targetSelctor) : $(this).parent(),
                closedLayerStatus = Util.storage("closedLayerStatus"),
                bannerStatusJSON = closedLayerStatus ? JSON.parse(closedLayerStatus) : {}
            if (label) {
                var curDate = Util.format(environment.serverDate, "yyyyMMdd")

                if (!bannerStatusJSON[label] ||
                    (bannerStatusJSON[label] && bannerStatusJSON[label] !== curDate)) {
                    target.css("display", "block")
                }
                $(this).click(function (evt) {
                    evt.stopPropagation()

                    //删除banner
                    target.remove()

                    //记录状态日期
                    bannerStatusJSON[label] = curDate
                    Util.storage("closedLayerStatus", JSON.stringify(bannerStatusJSON))

                    return false
                })
            }
        })
    },

    /**
     * 弹出层定位(在artDialog定位异常的情况下使用)
     * @param config {Object} 配置文件
     * config.target => 要定位的目标
     * config.height,config.width 定位层的宽高
     */
    popupPosition: function (config) {
        //弹出层
        var $popupLayer = config.target,
            popupTimer = null,
            popupWidth = config.width || 0,
            offset = $(this).offset(),
            width = $(this).outerWidth(),
            popupOffset = config.offset || 0,
            direction = 'left',
            layerLeft = 0,
            layerTop = 0
        var screenWidth = window.innerWidth ||
            document.documentElement.clientWidth ||
            document.body.clientWidth
        if (offset.left > screenWidth / 2 && screenWidth >= 990) {
            direction = 'right'
        }

        switch (direction) {
            case 'left':
                layerLeft = offset.left + width + popupOffset
                layerTop = offset.top
                $popupLayer.removeClass('z-right')
                break
            case 'right':
                layerLeft = offset.left - popupWidth - popupOffset
                layerTop = offset.top
                $popupLayer.addClass('z-right')
                break
            default :
                break
        }

        $popupLayer.css({
            'left': layerLeft,
            'top': layerTop - config.height / 2,
            'position': 'absolute',
            'height': config.height || 'auto',
            'width': config.width || 'auto'
        }).show()

        $(this).on('mouseenter', function () {
            $popupLayer.data('isDisplay', true)
        }).on('mouseleave', function () {

            popupTimer = setTimeout(function () {

                if (!$popupLayer.data('isDisplay')) {
                    $popupLayer.hide()
                    $popupLayer.data('isDisplay', false)
                }

            }, 50)
        })

        $popupLayer.on('mouseenter', function () {
            popupTimer ? clearTimeout(popupTimer) : null
        }).on('mouseleave', function () {
            $(this).hide()
            clearTimeout(popupTimer)
        })
    }
})
$.extend({
    /**
     * 遮罩层
     * @param {String} action => hide or show
     */
    shade: function (action) {
        var shade = $(".u-shade")
        if (typeof action === "undefined" || action === "show") {
            if (!shade.length) {
                var body = $("body"),
                    height = body.height(),
                    html = "<div class='u-shade' style='height:" + height + "px'></div>"
                body.append(html)
            } else {
                shade.show()
            }
        } else if (action === "hide") {
            shade.remove()
        }
    }
})

//@Module Shim
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
})