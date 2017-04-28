/*global $,window,formValid,environment,Highcharts,dialog,highchartsJson,xShade*/

/**
 * @entry 我的消息
 * @url   /member/messages
 */
import 'common/member'
import 'module/jquery.extend'
import {Util} from 'common/util'

//消息订阅-修改模式
$("#j-feed-change[type!='submit']").on("click", function () {
    if (!$(this).hasClass("z-disabled")) {
        $(this).addClass("z-disabled")
        $(this).html("请稍候...")
        saveNotifySetting()
    }
})

//我的消息
var filterCheckObj = $("#j-filter-check"),
    umsgListObj = $("#j-umsg-list"),
    msgUrl = environment.globalPath + "/message/queryMessage"

if (filterCheckObj.length) {
    /*
     * 获取消息列表
     * @param url {String} API地址
     * @param page {Number} 分页页码
     * @param params {String} ajax params
     */
    function getMessagesList(url, page, params) {
        var argvData = params ? params : getFilterValues();
        $(".m-umsg-list").xLoading(function (target) {

            $.getJSON(url, argvData, function (json) {
                if (json.data.length) {
                    renderMessages(json, page);
                    umsgListObj.data("pageNo", page);
                } else {
                    renderNullMessage();
                }
                target.find('.u-xloading').remove();
            });

        }, "fixed");

    }

    /*
     * 列表渲染
     * @param data {Object} data json
     * @param page {Number}  page number
     * */
    function renderMessages(data, page) {
        var coverHtml = '<i class="u-umsg-icon u-msgWrap-tl"></i>' +
                '<i class="u-umsg-icon u-msgWrap-tr"></i>' +
                '<i class="u-umsg-icon u-msgWrap-bl"></i>' +
                '<i class="u-umsg-icon u-msgWrap-br"></i>',
            msgHtml = [],
            msgData = data.data,
            msgListObj = $("#j-umsg-list"),
            moreObj = msgListObj.find(".u-msglist-more"),
            size = msgData.length;

        moreObj.prev().removeClass("z-last");
        //数据处理
        $.each(msgData, function (i, item) {
            var date = Util.format(item.time, "yyyy-MM-dd"),
                time = Util.format(item.time, "hh:mm:ss"), hasRead = "";
            if (!item.hasRead) {
                hasRead = " z-unread";
            }
            if (i + 1 === size) {
                hasRead = hasRead + " z-last";
            }
            msgHtml.push('<li class="f-cf' + hasRead + '" data-id="' + item.msgId + '">' +
                '<div class="u-msglist-date f-fl">' + date + '<span>' + time +
                '</span><i>●</i></div><div class="u-msglist-content f-fl">' +
                '<div>' + item.content + '</div>' + coverHtml + '</div></li>');
        });
        //查看更多
        if (size === 10 && page !== data.maxPager) {
            if (moreObj.length) {
                moreObj.remove();
            }
            msgHtml.push('<li class="f-cf u-msglist-more" id="j-msglist-more">' +
                '<div class="u-msglist-date f-fl"></div><div class="u-msglist-content f-fl">' +
                '<div><a href="javascript:void(0)">查看更多</a></div>' + coverHtml + '</div></li>');
        } else {
            moreObj.remove();
        }
        //分页插入
        if (page === 1) {
            msgListObj.html(msgHtml.join(""));
        } else {
            msgListObj.append(msgHtml.join(""));
        }

    }

    function renderNullMessage() {
        $("#j-umsg-list").empty().html('<li class="u-umsg-empty f-dn">暂无消息</li>');
        $(".u-umsg-empty").fadeIn(750);
    }

    //获取过滤参数
    function getFilterValues() {
        return $("#j-umsg-filter").serialize();
    }

    //Init
    getMessagesList(msgUrl, 1);
    //类型过滤
    filterCheckObj.on("click", "input", function () {
        if (!filterCheckObj.find("input:checked").length) {
            return false;
        }

        $(this).parents("label").toggleClass("z-checked");
        getMessagesList(msgUrl, 1);
    });
    //时间过滤
    $("#j-umsg-dataSelect").on("click", "li", function () {
        if (!$(this).hasClass("z-first")) {
            $(this).addClass("z-hidden").siblings().removeClass("z-hidden");

        }

        getMessagesList(msgUrl, 1);
    });
    //标记已读
    $("#j-umsg-markRead").on("click", function () {
        if (!umsgListObj.find("li.f-cf").length) {
            return false;
        }
        $.getJSON(environment.globalPath + "/message/batchUpdateMessageStatus", getFilterValues(), function (data) {
            if (data.success) {
                $(".m-umsg-list").find("li").removeClass("z-unread");
                $("#j-umsg-notice").addClass("z-hidden");
            }
        });
    });
    umsgListObj.on("click", ".u-msglist-content", function () {
        var _this = $(this),
            parent = $(this).parent(),
            msgId = parent.data("id"),
            data = {"msgId": msgId};
        if (parent.hasClass("z-unread")) {
            $.getJSON(environment.globalPath + "/message/updateMessageStatus", data, function (data) {
                if (data.success) {
                    _this.parent().removeClass("z-unread");
                    var umsgCountObj = $("#j-umsg-number");
                    if (umsgCountObj.text() !== "1") {
                        umsgCountObj.text(umsgCountObj.text() - 1);
                    } else {
                        umsgCountObj.parent().addClass("z-hidden");
                    }

                }
            });
        }
    });

    //获取更多
    umsgListObj.on("click", ".u-msglist-more", function () {
        var msgLength = $(this).prevAll().length;
        if (msgLength % 10 > 0) {
            return false;
        }
        var parent = $(this).parent(),
            pageNo = parent.data("pageNo");
        if (typeof(pageNo) !== "undefined") {
            pageNo = pageNo + 1;
        } else {
            pageNo = 2;
        }

        var argv = getFilterValues() + "&pageNo=" + pageNo;
        getMessagesList(msgUrl, pageNo, argv);

    });


}


/**
 * 渲染消息订阅配置项
 */
function renderNotifySettingItem() {
    var item = memberNotifySettingsConfig.notifySettingItem;
    if (item != "") {
        var data = eval("(" + item + ")");
        for (var i = 0; i < data.length; i++) {
            var notifyType = data[i][0];
            var notifyWay = data[i][1];
            var id = "notify_" + notifyType + "_" + notifyWay;
            if ($("#" + id).length) {
                $("#" + id).addClass("z-checked");
                document.getElementById(id + "_box").checked = true;
            }
        }
    }
}

/**
 * 保存订阅配置项
 */
function saveNotifySetting() {
    var data = $("#memberNotifySettingsForm").serializeArray();
    var config = {
        url: environment.globalPath + "/member/updateMemberNotifySettings",
        data: data,
        callback: saveNotifySettingCallback
    };
    $.xPost(config);
}

/**
 * 保存订阅配置项回调事件处理
 * @param data
 */
function saveNotifySettingCallback(data) {
    if (data != "") {
        if (data.success) {
            $("#j-feed-change").html("保存").removeClass("z-disabled");
            $.xDialog({
                type: "success",
                content: "订阅配置保存成功"
            });
        } else {
            $.xDialog({
                type: "error",
                content: "订阅配置保存失败"
            });
        }
    }
}

//改用原生js实现，使用jquery操作checkbox有问题
$(".notify-ckbox").on("click", function () {
    if (!$(this).attr("disabled")) {
        var id;
        if ($(this).hasClass("z-checked")) {
            $(this).removeClass("z-checked");
            id = $(this).children("input").attr("id");
            document.getElementById(id).checked = false;
        } else {
            $(this).addClass("z-checked");
            id = $(this).children("input").attr("id");
            document.getElementById(id).checked = true;
        }
    }
});
