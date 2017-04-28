/**
 * @name Cube
 * @description 网站全局环境使用的组件集合
 */
import {Util} from 'common/util'

/**
 * @module 每日签到
 */
const Checkin = class {

    //签到初始化
    static init() {
        var dailyCheckIn = {
            config: {
                target: "#j-checkin-box",
                closeBtn: "#j-checkin-close",
                diceImg: "#j-dice-action",
                signInUrl: "/member/check/"
            },
            show: function () {
                var _this = this
                //显示阴影和签到浮层
                $.shade("show")
                $(_this.config.target).show()
                //显示动画
                this.diceAnimate()
                //定时器
                setTimeout(function () {
                    //clearInterval(_this.timer)
                    _this.getDiceNumber()
                }, 1750)
                //关闭窗口
                $(this.config.closeBtn + ",.u-shade").on("click", function () {
                    _this.close()
                    window.location.reload()
                })
            },
            close: function () {
                $(this.config.target).hide()
                $.shade("hide")
                if ($("#j-reputation-checkin").length === 1) {
                    window.location.reload()
                }
            },
            //骰子动画
            diceAnimate: function () {
                var diceImg = this.config.diceImg,
                    count = 0
                this.timer = setInterval(function () {
                    count += 1

                    if (count > 4) {
                        count = 1
                    }
                    $(diceImg).attr("class", "u-dice-action u-dice-b" + count)
                }, 120)
            },
            //获取骰子(人气值)点数
            getDiceNumber: function () {
                var _this = this
                $.getJSON(this.config.signInUrl, function (data) {
                    if (data.success) {
                        clearInterval(_this.timer)
                        //显示签到数据
                        var basePoint = data.result.gainPopularity,
                            doublePoint = data.result.popularityDouble,
                            point = basePoint,
                            pointText = point,
                            checkInBtn = $("#j-reputation-checkin")
                        if (doublePoint > 1) {
                            pointText = basePoint + "x" + doublePoint
                        }
                        //是否生日处理
                        var isBirthday = $(_this.config.target).data('isBirthday'),
                            $diceImg = $(_this.config.diceImg)
                        $diceImg
                            .prop("class", "u-dice-action u-dice-a" + point)
                        $("#j-checkin-btn").remove()
                        if (isBirthday) {
                            $(_this.config.target).addClass('z-birthday')
                                .find(".j-checkin-point").text(pointText)
                                .parent().fadeIn()
                        } else {
                            $(_this.config.target)
                                .find(".j-checkin-point").text(pointText)
                                .parent().fadeIn()
                            if (doublePoint > 1) {
                                $diceImg.after('<em>x&nbsp<span>' + doublePoint + '</span></em>')
                            }
                        }

                        //人气值页面签到
                        if (checkInBtn.length === 1) {
                            checkInBtn.data("isChecked", true)
                                .addClass("u-myinvite-signInLater")
                                .removeClass("u-myinvite-signIn")
                                .find("span").html("<i>+</i><em class='f-ff-amount'>" + basePoint + "</em><div class='f-ff-dian'>点</div>")
                                .next("i").addClass("f-fs14").removeClass("f-fs16")
                                .text("今日签到人气值")
                            var pv = $("#j-popularity-value")
                            pv.text(Number(pv.text()) + basePoint)
                        }
                    } else {
                        //错误处理
                        var errorCode = Number(data.resultCodeEum[0].code)
                        switch (errorCode) {
                            case 90050:
                                $.xDialog({
                                    content: "您已签到，请勿重复操作。",
                                    callback: function () {
                                        $.shade("hide")
                                        window.location.reload()
                                    }
                                })
                                break
                            default:
                                break
                        }
                    }
                })
            }
        }
        if ($("#j-checkin-box").length === 1) {
            $("#j-checkin-btn,#j-reputation-checkin").on("click", function (e) {
                e.stopPropagation()
                if (typeof($(this).data("isChecked")) !== "undefined") {
                    return false
                }
                dailyCheckIn.show()
                var shadeLayer = $("#j-banner-shade")
                if (shadeLayer.length) {
                    shadeLayer.remove()
                    $("#j-checkin-guide").remove()
                }
                return false
            })
        }
    }

    /**
     * 签到引导
     * @param target {String} css选择符
     */
    static guide(target) {

        var checkinState = Util.storage('checkinDaily'),
            today = Util.format(environment.serverDate, "yyyy-MM-dd").replace(/-/g, ''),
            $checkinGuide = $("#j-checkin-guide")

        if (checkinState && checkinState === today) {

            if ($checkinGuide.length === 1) {
                $checkinGuide.remove()
            }
            return false
        }
        //加载阴影层
        var layerParent = $(target)
        if (layerParent.find(".u-shade").length === 0 && $checkinGuide.length === 1) {

            layerParent.append("<div class='u-shade u-signin-shade' id='j-banner-shade'></div>")

            //关闭按钮&试试手气按钮
            var shadeLayer = $("#j-banner-shade")

            $checkinGuide.show().on("click", "span", function () {
                Util.storage('checkinDaily', today)
                $checkinGuide.remove()
                shadeLayer.remove()
            }).on("click", "button", function () {

                if (!$(this).data("logined")) {
                    window.location.href = "/security/login/"
                } else {
                    $checkinGuide.remove()
                    shadeLayer.remove()
                    $("#j-checkin-btn").click()
                }
            })
        }
    }
}

/**
 * @module 对话框
 */
const Dialog = class {
    /**
     * 基础通用弹出对话框
     * @param {Object} config
     *     config:{
     *            content: '我是一个对话框啊我!',      //提示内容，必须，字符串
     *            title: '提示标题',  //目前组件没有显示标题，非必须，初始值 '提示'
     *            type: 'info',   //非必须 信息类型：success => 成功, info => 提醒, warn => 警告, error => 错误 初始值 'info'
     *           callback:function(){},  //点击确定后的回调函数 非必须
     *           cancel: function(){},   //增加取消按钮并回调 非必须
     *           okValue: '确定啊', //确定按钮的文字显示 非必须
     *           cancelValue: '取消啊', //取消按钮的文字显示 非必须
     *           width:240,  //对话框宽和高
     *           height:180
     *     }
     */
    static show(config){
        $(".ui-popup").remove()

        if (typeof (config) !== "undefined" && $.isPlainObject(config)) {
            //类型初始化
            var type = ""
            if (typeof (config.type) === "undefined") {
                type = "info"
            } else {
                switch (config.type) {
                    case "error":
                        type = "error2"
                        break
                    case "warn":
                        type = "warn"
                        break
                    case "success":
                        type = "success2"
                        break
                    default:
                        type = "info"
                }
            }
            //参数初始化
            var title = config.title ? config.title : "提示",
                content = config.content ? config.content : "",
                okValue = config.okValue ? config.okValue : '确定',
                cancelValue = config.cancelValue ? config.cancelValue : '取消',
                width = config.width || 360,
                height = config.height || 140,
                callback = typeof (config.callback) !== "function" ? function () {
                } : config.callback,
                cancel = function () {
                },
                cancelBtn = ""
            if (typeof (config.cancel) === "function") {
                cancel = config.cancel
                cancelBtn = "<a href='javascript:void(0)' class='f-round u-btn-white j-dialog-cancel'>" +
                    cancelValue + "</a></div>"
            } else {
                cancel = function () {}
            }
            var close = function(){}
            if (typeof (config.close) === "function") {
                close = config.close
            }

            var html = "<div class='u-dialog-wrap'><i class='u-icon-" + type +
                    " u-icon f-icon-37'></i><span>" + content +
                    "</span><div class='u-dialog-btn'><a href='javascript:void(0)' " +
                    "class='f-round u-btn-white j-dialog-ok'>" + okValue + "</a>" +
                    cancelBtn + "</div>",
                d = window.dialog({
                    title: title,
                    content: html,
                    skin: "u-dialog-box",
                    width: width,
                    height: height
                })
            d.show()
            //关闭窗口
            $(".j-dialog-ok").click(function () {
                d.close().remove()
                callback()
            })
            //取消
            $(".j-dialog-cancel").click(function () {
                d.close().remove()
                cancel()
            })
            //X关闭
            $('.ui-dialog-close').click(function () {
                close()
            })
        } else {
            throw new TypeError("dialog参数类型错误或不存在")
        }
    }

    // 弹出提示 一般ajax调用之后显示错误信息
    showErrorMessage(data){
        if (data.resultCodeEum) {
            var message = "";
            for (var x in data.resultCodeEum) {
                message = data.resultCodeEum[x].msg + message;
            }
            this.show({
                content: message,
                type: 'error'
            });
        } else {
            this.show({
                content: data.result,
                type: 'error'
            });
        }
    }

    //充值，支付，调用 require dialog
    static showErrorResultMessage(data) {
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
        this.show({
            content: errorMsg
        });
    }
}

/**
 * @module 消息处理
 */
const Message = class {
    /**
     * 推送用户未读消息
     * @param seconds 获取消息的频率
     */
    static pushUnreadMessage(seconds) {
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
                var msgPageUrl = Util.storage('msgNotice.url'),
                    msgCount = Number(Util.storage("msgNotice.count"));

                //独占机制
                switch (event) {
                    case 'onload':
                        Util.storage('msgNotice.url', curDate + ":" + curPageUrl);
                        //console.log("onload:消息被此页 "+curPageUrl+" 独占");
                        break;
                    case 'onTimer':
                        if (!msgPageUrl) {
                            Util.storage('msgNotice.url', curDate + ":" + curPageUrl);

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
                            Util.storage("msgNotice.count", data.result);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        clearInterval(msgTimer);
                        Util.storage('msgNotice.url', null);
                    }
                });
            } else {
                return false;
            }
        }

        //对session影响的处理
        setTimeout(function () {
            if (msgTimer) {
                clearInterval(msgTimer);
            }
        }, 30 * 60 * 1000 + 1000);

        //关闭页面处理
        $(window).unload(function () {
            var msgPageUrl = Util.storage('msgNotice.url'),
                curPageUrl = location.href.replace(environment.globalPath, "");
            if (msgPageUrl === curPageUrl) {
                Util.storage('msgNotice.url', null);
            }
        });
    }
}

/**
 * @module 统计跟踪
 */
const Stats = class {
    //注册跟踪配置
    static registerTrace() {
        var registerTraceNo = Util.getUrlParam('registerTraceNo');
        var registerTraceSource = Util.getUrlParam('registerTraceSource');

        var date = new Date();
        date.setTime(date.getTime() + (30 * 60 * 1000));

        if (registerTraceNo) {

            Util.setCookie("registerTraceNo", registerTraceNo, date);
        }
        if (registerTraceSource) {
            Util.setCookie("registerTraceSource", registerTraceSource, date);
        }
    }
}

/**
 * @module 用户界面
 */
const UI = class {
    /**
     * 遮罩层
     * @param {string} action => hide or show
     */
    static shade(action){
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

    //TAB箭头的定位
    //static arrowLocator(menu) {
    //    var $menu = $(menu),
    //        currentMenu = $menu.find(".z-current"),
    //        position = currentMenu.position(),
    //        uWidth = currentMenu.width(),
    //        iWidth,
    //        left;
    //
    //    if (typeof position == "undefined") {
    //        return;
    //    }
    //    var currentI = $menu.children("i");
    //
    //    if (currentI.length === 1) {
    //        iWidth = currentI.width();
    //        left = position.left + uWidth / 2 - iWidth / 2;
    //        currentI.css({
    //            "left": left
    //        });
    //
    //    } else {
    //        var arrow = $menu.find(".u-arrow-up");
    //
    //        if (arrow.length > 0) {
    //            iWidth = arrow.width();
    //            left = position.left + uWidth / 2 - iWidth / 2;
    //            arrow.css({
    //                "left": left
    //            });
    //        }
    //    }
    //}
}

export {
    Checkin,
    Dialog,
    Message,
    Stats,
    UI
}