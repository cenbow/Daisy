/*global $,window,formValid,environment,Highcharts,dialog,highchartsJson,xShade*/
(function () {
    //初始化分页
    var ITERMS_PER_PAGE_INT = 10,
        pageIndex = 0;
    //签到引导
    signinGuide("body");

    //问候时间
    $("#j-regards").greetingTime();
    //消息订阅.修改模式
    $("#j-feed-change[type!='submit']").on("click", function () {
        if (!$(this).hasClass("z-disabled")) {
            $(this).addClass("z-disabled");
            $(this).html("请稍候...");
            saveNotifySetting();
        }
    });

    //冻结余额
    var $blockAmount = $('#j-blocking-amount');
    if ($blockAmount.length) {
        var $blockTips = $blockAmount.next('em'),
            blockingTimer = null;
        $blockAmount.on('mouseenter', function () {
            $blockTips.show();
        }).on('mouseleave', function () {
            blockingTimer = setTimeout(function () {
                $blockTips.hide();
            }, 300);
        });
        $blockTips.on('mouseenter', function () {
            if (blockingTimer) {
                clearTimeout(blockingTimer);
            }
        }).on('mouseleave', function (e) {
            var eventBottom = e.pageX + 30,
                targetBottom = $(this).offset().top + 30;
            if (eventBottom > targetBottom && blockingTimer) {
                $blockTips.hide();
            }
        });
    }

    var withdrawSelect = $("#j-withdraw-select");
    //提现银行选择
    withdrawSelect.on("click", ".u-withdraw-bank", function () {
        $(this).addClass("z-selected").siblings().removeClass("z-selected");
        var img = $(this).find("img"),
            src = img.attr("src"),
            val = $(this).attr("value");
        if (src.lastIndexOf("hBank") !== -1) {
            src = src.replace("hBank", "bank");
            img.attr("src", src);
        }
        $("#j-bankCardId").val(val);
        var oImg = $(this).siblings().find("img");
        oImg.each(function () {
            var oSrc = $(this).attr("src");
            oSrc = oSrc.replace("bank_", "hBank_");
            $(this).attr("src", oSrc);
        });
    });

    //初始选中第一个提现银行
    withdrawSelect.find('.u-withdraw-bank:eq(0)').click();

    //个人资料.修改模式
    $("#j-profile-update").on("click", function () {
        if ($(this).attr("data") === "edit") {
            $(".u-profile-table table").addClass("z-editabled");
            $(this).attr("data", "save").text("保存");
        } else {
            if (!$(this).hasClass("z-disabled")) {
                var area = $("#region-area-area").val();
                var censusRegister = $("#censusRegister-area-area").val();
                //IE li空值获得的是1???
                area = (area == 1 ? "" : area);
                censusRegister = (censusRegister == 1 ? "" : censusRegister);
                if (!formValid.check(false) || area == "" || censusRegister == "") {
                    if (area == "") {
                        $("#j-area-error").show();
                    }
                    if (censusRegister == "") {
                        $("#j-census-register-error").show();
                    }

                } else {
                    $(this).addClass("z-disabled");
                    $(this).html("请稍候...");
                    saveMemberInfo();
                }
            }
        }
    });
    //地市区下拉选项错误提示处理
    $(".itemData").on("click", "li", function () {
        //这里监控area-area无效，因为该值还没有被写入
        if ($(this).parent().attr("id").indexOf("area_select_list_4") >= 0) {
            if ($(this).text() != "选择区") {
                $(this).parents("td").find(".region-error").hide();
            }
        }
    });
    //银行选择
    $("#j-bank-selector").on("click", "li", function () {
        var siblings = $(this).siblings();
        $(this).addClass("z-selected").find(".u-bankicon").removeClass("z-disabled");
        siblings.removeClass("z-selected")
            .find("span").css("border-color", "#dddbd6");
        siblings.find(".u-bankicon").addClass("z-disabled");
        var color = $(this).find(".u-bankicon").css("color");
        $(this).find("span").css("border-color", color);

        var bankCode = $(this).attr("value");
        $("#j-bankid").val(bankCode);
        $(".u-bankid-check").remove();
        $(".j-select-other").removeClass("z-selected").find("button").text("选择其他银行");

        //显示银行额度限制
        bankCode ? showBankLimit(bankCode, 1) : null;
    });

    //其他银行选择时设置隐藏表单的值
    $(".j-select-other ul").on("click", "li", function () {
        var bankSelect = $("#j-bank-selector"),
            bankId = $(this).attr("value");
        $("#j-bankid").val(bankId);
        bankSelect.find(".u-bankicon").removeClass("z-selected").addClass("z-disabled");
        bankSelect.find("span").css("border-color", "#dddbd6");
    });

    //百分比报表
    function columnChart(object) {
        var obj = $(object),
            chartData = [],
            bar = obj.find(".u-col-bar");
        obj.find(".u-col-data").each(function () {
            chartData.push(Number($(this).data("rate")));
        });
        var chartData2 = $.map(chartData, function (n) {
            return n;
        });
        var chartSortData = chartData.sort(),
            minData = chartSortData[0],
            maxData = chartSortData[chartSortData.length - 1],
            dataArea = maxData - minData,
            percentData = [];
        $.each(chartData2, function (i) {
            $(bar).eq(i).height((chartData2[i] / maxData) * 220);
        });
    }

    var $profitWeekly = $('#j-profit-weekly'),
        $profitMonthly = $('#j-profit-monthly');
    if ($profitWeekly.length) {
        columnChart(".j-column-chart");
        var $popupWeekly = $('#j-show-weekly'),
            $popupMonthly = $('#j-show-monthly');
        var weeklySize = $popupWeekly.find('li').length,
            weeklyHeight = 50 + (weeklySize < 7 ? weeklySize - 1 : 6) * 40,
            monthlySize = $popupMonthly.find('li').length,
            monthlyHeight = 50 + (monthlySize < 7 ? monthlySize - 1 : 6) * 40;

        if (weeklySize !== 0) {
            $profitWeekly.on('mouseover', function () {
                $(this).popupPosition({
                    target: $popupWeekly,
                    height: weeklyHeight,
                    width: 477,
                    offset: 15
                });
            });
            $profitWeekly.on('mouseleave', function () {
                var timer = setTimeout(function () {
                    $popupWeekly.hide();
                }, 300);
                $popupWeekly.on('mouseenter', function () {
                    clearTimeout(timer);
                });
            })
        } else {
            $profitWeekly.find('i').hide();
        }
        if (monthlySize !== 0) {
            $profitMonthly.on('mouseover', function () {
                $(this).popupPosition({
                    target: $popupMonthly,
                    height: monthlyHeight,
                    width: 477,
                    offset: 15
                });
            });
            $profitMonthly.on('mouseleave', function () {
                var timer = setTimeout(function () {
                    $popupMonthly.hide();
                }, 300);
                $popupMonthly.on('mouseenter', function () {
                    clearTimeout(timer);
                });
            })
        } else {
            $profitMonthly.find('i').hide();
        }

    }
    //提现提交
    $("#j-withdraw-submit").on("click", function () {
        var isCorrect = $("#withdrawAmount").data("isCorrect"),
            withdrawAmount = $("#withdrawAmount").val(),
            hasSetPayPassword = $('#j-hasSetPayPassword').val();
        if (hasSetPayPassword != 1) {
            xShade();
            $("#j-paymentCipherBlock").show();
            return false;
        } else {

            if (typeof (isCorrect) === "undefined" || isCorrect === 0) {
                $("#withdrawAmount").blur();
                return false;
            } else {
                $(this).attr('href', environment.globalPath + '/member/pageRelay?from=withdrawpage&&withdrawAmount=' + withdrawAmount)
            }
            xShade();
            $("#j-goToSinaBlock").show();
        }
    });
//充值提交
    $("#member_rechargePage_submit").on("click", function () {
        var amountInput = $("#j-recharge-amount"),
            amount = $("#j-recharge-amount").val(),
            hasSetPayPassword = $('#j-hasSetPayPassword').val();
        if (hasSetPayPassword != 1) {
            xShade();
            $("#j-paymentCipherBlock").show();
            return false
        } else {

            if (!formValid.check() || $(".u-recharge-amount .Validform_wrong").length > 0 || amountInput.val().length < 1) {
                amountInput.blur();
                return false;
            } else {
                $(this).attr('href', environment.globalPath + '/member/pageRelay?from=rechargePage&&amount=' + amount)
            }
            xShade();
            $("#j-goToSinaBlock").show();
        }

    });
    /**
     * 关闭设置支付密码提示弹窗
     */
    $('.j-close').on('click', function () {
        $(this).parent().hide()
        xShade('hide');

    })
    /**
     * 完成充值提现的刷新页面
     */
    $('.j-reload').on('click', function () {
        location.reload()
    })
    /**
     * 删除银行卡
     */
    $(".isDelete").on("click", function () {
        var id = $(this).data("value"),
            type = $(this).data("type");
        var href = environment.globalPath + "/memberBankCard/deleteMemberBankCard";
        $("#memberCardID").val(id);
        var data = $("#memberCardForm").serializeArray();

        function delAction() {
            $.xPost({
                url: href,
                data: data,
                callback: function (data) {
                    if (data.success) {
                        window.location.href = environment.globalPath + "/memberBankCard/bankManage";
                    } else {
                        showErrorMessage(data);
                    }
                }
            });
        }

        if (typeof (type) !== "undefined" && type === "safecard") {
            $.xPost({
                url: "/memberBankCard/balanceIsZero",
                data: data,
                callback: function (data) {
                    var btnHtml = "",
                        tipsText = "";
                    if (data.result) {
                        btnHtml = "<a href='javascript:void(0)' class='f-round u-btn-white u-btn-ok j-dialog-ok'>确定删除</a>" +
                            "<a href='javascript:void(0)' class='f-round u-btn-white u-btn-cancel j-dialog-cancel'>暂不删除</a>";
                        tipsText = "删除安全卡后，需要重新开通快捷并支付才能生成新的安全卡，是否确定删除？";
                    } else {
                        btnHtml = "<a href='javascript:void(0)' class='f-round u-btn-white u-btn-cancel j-dialog-cancel'>知道了</a>";
                        tipsText = "为了您的资金安全，当资产总计为0时，方可删除安全卡。<br>资产总计=存钱罐余额+待收本金+待收收益";

                    }
                    var html = "<div class='u-dialog-wrap'><i class='u-icon-info u-icon f-icon-37'></i><span>" +
                            tipsText +
                            "</span><div class='u-dialog-btn'>" + btnHtml + "</div>" + "</div>",
                        d = dialog({
                            title: "",
                            content: html,
                            skin: "u-dialog-box u-delcard-box",
                            width: 560,
                            height: 160
                        });
                    d.show();
                    $(".j-dialog-ok").click(function () {
                        d.close().remove();
                        if (data.result) {
                            delAction();
                        } else {
                            window.location.href = "/memberBalance/withdrawPage?isClean=true";
                        }
                    });
                    //取消
                    $(".j-dialog-cancel").click(function () {
                        d.close().remove();
                    });
                }
            });
        } else {
            $.xDialog({
                content: '是否删除银行卡',
                callback: function () {
                    delAction();
                },
                cancel: function () {
                }
            });

        }
    });
    //提现卡-变成快捷卡
    $(".j-quick-pay").on("click", function () {
        var id = $(this).data("value");
        //   var href = environment.globalPath + "/memberBankCard/deleteMemberBankCard";
        window.location.href = environment.globalPath + "/memberBankCard/bankAdd?id=" + id;
        //        $("#memberCardID").val(id);
        //        var data = $("#memberCardForm").serializeArray();
        //        $.xDialog({
        //            content: '开通快捷支付功能，新浪支付将重新验证您这张银行卡的信息，以免冲突，我们会删除您的原卡信息',
        //            callback: function() {
        //                $.xPost({
        //                    url: href,
        //                    data: data,
        //                    callback: function(data) {
        //                        if (data.success) {
        //                            window.location.href = environment.globalPath + "/memberBankCard/bankAdd?id="+id;
        //                        } else {
        //                            showErrorMessage(data);
        //                        }
        //                    }
        //                });
        //                return true;
        //            },
        //            cancel: function() {}
        //        });
    });

    // 添加银行卡 选择
    $("#j-bankAdd-select").find("ul").on("click", "li", function () {
        var value = $(this).data("value"),
            bankCodeObj = $("#bankCode");

        //换卡清空已填信息
        if (bankCodeObj.val() !== "" && value !== bankCodeObj.val()) {

            $(".j-bankcard-id").val("").next(".Validform_checktip").empty();

            formValid.resetForm();

            var provinceObj = $("#regionarea_data_2"),
                cityObj = $("#regionarea_data_3");

            provinceObj.find("button").text("选择省");
            $("#region-area-province").val("");

            cityObj.find("button").text("选择市");
            $("#region-area-city").val("");
        }

        bankCodeObj.val(value);

        var cardType = $("#cardType");
        if ($(this).data("type") === 1) {
            $("#j-bank-mobile").appendTo("#j-qmobile-wrap");
            //非快捷卡直接提交不用下一步选择
            $("#j-submit-select").val("立即添加").addClass("j-not-Quickcard");
        } else {
            var mobileInputObj = $("#j-qmobile-wrap").find("input");
            if (mobileInputObj.length > 0) {
                $("#j-qbank-mobile").find("label").after(mobileInputObj.removeAttr("disabled"));
            }
            $("#j-submit-select").val("下一步").removeClass("j-not-Quickcard");
        }
    });

    /**
     * 添加银行卡
     */
    //提交非快捷卡
    function submitNormalCard(target) {
        var data = $("#bankAdd_form").serializeArray(),
            cardType = $("#cardType").val(),
            href = "";
        data.cardType = 1;
        href = environment.globalPath + "/memberBankCard/addMemberBankCard";
        target.attr('disabled', "true").addClass("z-disabled").val("添加中");
        $.xPost({
            url: href,
            data: data,
            callback: function (data) {
                if (data.success) {
                    window.location.href = environment.globalPath + "/memberBankCard/bankManage";
                } else {
                    showErrorMessage(data);
                    target.removeAttr("disabled").removeClass("z-disabled").val("立即添加");
                }
            }
        });
    }

    //选择绑定快捷支付卡
    $("#j-select-qcard").on("click", function () {
        //显示协议
        $("#j-submit-argreement").css("display", "block");
        //显示手机号码输入
        $("#j-qmobile-input").prepend($("#j-qbank-mobile").children()).parents("tr").show();
        //切换按钮
        $("#j-type-select").remove();
        $("#j-submit-quickNext").show();
        //改变 cardType
        $("#cardType").val(2);

    });
    $("#j-submit-quickNext").on("click", function () {
        var mobile = $("#j-bank-mobile"),
            _this = $(this);

        //阻止重复提交
        if (_this.hasClass("z-disabled")) {
            return false;
        }

        //检验手机号码
        if (mobile.val().length !== 11 || mobile.next(".Validform_wrong").length === 1) {
            $(mobile).blur();
            return false;
        }

        var data = $("#bankAdd_form").serializeArray(),
            href = "";
        href = environment.globalPath + "/memberBankCard/sendThirdPayBingBankCard";
        _this.addClass("z-disabled").val("提交中");
        $.xPost({
            url: href,
            data: data,
            callback: function (data) {
                if (data.success) {
                    _this.remove();
                    //$("#outerSourceId").val(data.result.outerSourceId);
                    //手机号只读
                    $("#j-bank-mobile").attr("readonly", "readonly").addClass("z-disabled");
                    //显示提交按钮
                    $("#j-submit-qcard").show();
                    //ticket赋值
                    $("#ticket").val(data.result.ticket);
                    //移除协议
                    $("#j-submit-argreement").remove();
                    //显示校验码输入
                    $(".j-qmobile-wrap").show();
                    //手机号码显示和隐藏
                    //$("#j-qmobile-view").text($("#j-bank-mobile").val());
                    //$(".j-qbank-mobile").hide();

                    //启用验证码输入
                    $("#j-bank-checkcode").removeAttr("disabled");
                    //显示取消按钮
                    $("#j-btn-cancel").removeClass("z-hidden");
                    //短信倒计时
                    $("#j-recode-tips2").css("display", "inline-block");
                    smsCountdown("#j-recode-tips2");
                } else {
                    _this.removeClass("z-disabled").val("下一步");
                    showErrorMessage(data);
                }
            }
        });
    });
    //选择绑定普通卡
    $("#j-select-ncard").on("click", function () {
        submitNormalCard($(this));
    });
    //添加卡的新流程
    $("#j-submit-select").on("click", function () {
        var bankProvince = $("#regionarea_data_2").find("button").text(),
            bankCity = $("#regionarea_data_3").find("button").text(),
            bankCode = $("#bankCode").val();
        //银行卡校验
        if (!formValid.check()) {
            return;
        }
        //校验银行选择
        if (bankCode == "" || bankCode == null) {
            $.xDialog({
                content: "请选择银行"
            });
            return;
        }
        //校验省市
        if (bankCity == "选择市" || bankProvince == "选择省") {
            $.xDialog({
                content: "开户省份或者市不能为空"
            });
            return;
        }
        //省市赋值
        $("#bankProvince").val(bankProvince);
        $("#bankCity").val(bankCity);
        //表单数据
        var data = $("#bankAdd_form").serializeArray(),
            cardType = $("#cardType").val(),
            that = $(this),
            href = "";

        if ($(".j-not-Quickcard").length != 1) {
            //选择是否开通快捷卡
            var qImg = $("#j-qcard-img"),
                bankName = $("#j-bankAdd-select").find("button").text();
            qImg.attr("src", qImg.data("src") + bankCode + ".png").parents("tr").show();
            $("#j-qcard-num").html("<em class='f-fs14'>" + bankName + "</em>" + $("input[name='cardNumber']").val());
            //移除银行和地区显示
            $(".u-bankAdd-select,.u-qbank-area,.u-qbank-card").hide();
            //按钮切换显示
            $(this).hide();
            $("#j-type-select").show();
            //显示 Banner
            $(".u-bankadd-banner").show();
        } else {
            //不支持快捷支付的卡直接提交
            submitNormalCard($(this));
        }
    });

    //重复发送添加快捷银行卡验证码
    $("#j-recode-tips2").on("click", function () {
        if ($(this).hasClass("u-recode-btn")) {
            $("#ticket").val("");
            var time = $(this).data("time"),
                formData = $("#bankAdd_form").serializeArray();
            $(this).html("校验码已发送<em>" + time + "</em>秒后可重获").removeClass("u-recode-btn");
            $("#j-bank-checkcode").val("");
            smsCountdown("#j-recode-tips2");
            formData.cardType = 2;
            $.xPost({
                url: environment.globalPath + "/memberBankCard/sendThirdPayBingBankCard",
                data: formData,
                callback: function (data) {
                    if (data.success) {
                        $("#outerSourceId").val(data.result.outerSourceId);
                        $("#ticket").val(data.result.ticket);
                    } else {
                        showErrorMessage(data);
                    }
                }
            });
        }
    });

    //省市赋值
    $("#j-qbank-city").find("ul").on("click", "li", function () {
        var val = $(this).attr("value");
        if ($(this).parent().is("#area_select_list_2")) {
            $("#bankProvince").val(val);
        } else {
            $("#bankCity").val(val);
        }

    });
    //快捷支付推进
    $("#j-submit-qcard").on("click", function () {
        var data = $("#bankAdd_form").serializeArray();
        var that = $(this);
        var href = environment.globalPath + "/memberBankCard/checkCodeFromThirdPay";
        if ($("#j-bank-checkcode").val() === "") {
            $.xDialog({
                content: "请输入验证码"
            });
            return false;
        }
        $(this).attr('disabled', "true").addClass("z-disabled").val("添加中");
        data.cardType = 2;
        $.xPost({
            url: href,
            data: data,
            callback: function (data) {
                that.removeAttr("disabled").removeClass("z-disabled").val("立即添加");
                if (data.success) {
                    window.location.href = environment.globalPath + "/memberBankCard/bankManage";
                } else {
                    var errorMsg = "验证码错误", errType = "normal";
                    if (data.result != null || data.result == "") {
                        if ("短信验证失败" == data.result || "ticket不存在或已失效" == data.result || "推进失败" == data.result) {
                            errorMsg = "您输入的验证码有误，新的验证码已发送，请注意查收";
                            errType = "smsError";
                        } else {
                            errorMsg = data.result;
                        }
                    }
                    //重新触发获取验证码
                    if (errType === "smsError") {
                        $("#j-recode-tips2").addClass("u-recode-btn").click();
                    }
                    $.xDialog({
                        content: errorMsg
                    });
                }

            }
        });
    });


    //单选按钮勾选时，设置.u-pro-text文本值
    $(".u-pro-input :input[type='radio']").on("click", function () {
        $(this).parents("td").find(".u-pro-text").html($(this).parent().text());
    });

    //充值页面--绑卡支付
    $("#member_rechargeOnBank_submit").on("click", function () {
        var _this = $(this),
            formData = $("#rechargePage_form_on_card").serializeArray();
        if ($(".Validform_wrong").length > 0) {
            return false;
        }
        if ($("#j-recharge-amount2").val() === "") {
            $("#j-recharge-amount2").blur();
            return false;
        }
        $(this).attr('disabled', "true").addClass("z-disabled").val("支付中");
        $.xPost({
            url: environment.globalPath + "/memberBalance/rechargeOnBankCard",
            data: formData,
            callback: function (data) {
                if (data.success) {
                    $("#outAdvanceNo").val(data.result.rechargeNo);
                    $("#ticket").val(data.result.ticket);
                    //禁用 提交充值 并 显示 确认充值
                    _this.hide();
                    $("#j-qbank-amount").removeAttr("disabled").val($("#j-recharge-amount2").val());
                    $("#j-recharge-amount2").parents(".u-recharge-amount2").hide();
                    $("#j-recharge-code").show().children().removeAttr("disabled");
                    $("#member_rechargeOnBank_advance").show().removeAttr("disabled").parent().addClass("z-last");
                    //$("#j-qbank-tips").remove();

                    smsCountdown("#j-recode-tips");
                    $("#j-submit-argreement").remove();
                } else {
                    showErrorResultMessage(data);
                    setTimeout(function () {
                        _this.removeAttr("disabled").removeClass("z-disabled").val("提交充值");
                    }, 700);
                }
            }
        });
    });

    //短信倒计时
    function smsCountdown(obj) {
        if (typeof obj !== "string") {
            return false;
        }
        var countObj = $(obj);
        if (countObj.length > 0) {
            var cid = "count_" + (new Date().getTime());
            countObj.find("em").attr("id", cid);
            var countNum = $("#" + cid),
                timer = setInterval(function () {
                    var count = Number(countNum.text());
                    if (count !== 0) {
                        count -= 1;
                        countNum.text(count);
                    } else {
                        clearInterval(timer);
                        countObj.text("点击重获手机验证码").addClass("u-recode-btn");
                    }
                }, 1000);
        }
    }

    //限制只能输入数字
    $("#j-bank-checkcode,#j-bank-mobile").on("keyup", function () {
        var val = $(this).val();
        $(this).val(val.replace(/[^\d]/g, ""));
    });
    $("#j-bank-checkcode").on("blur", function () {
        var val = $(this).val();
        if (val === "") {
            if ($(this).siblings(".Validform_wrong").length === 0) {
                $("#j-recharge-code").append("<span class='Validform_checktip Validform_wrong'>请输入验证码</span>");
            }
        } else {
            $("#j-recharge-code .Validform_wrong").remove();
        }
    });
    //重复发送充值验证码
    $("#j-recode-tips").on("click", function () {
        if ($(this).hasClass("u-recode-btn")) {
            $("#ticket").val("");
            var time = $(this).data("time"),
                formData = $("#rechargePage_form_on_card").serializeArray();
            $(this).html("校验码已发送<em>" + time + "</em>秒后可重获").removeClass("u-recode-btn");
            $("#j-bank-checkcode").val("");
            smsCountdown("#j-recode-tips");
            $.xPost({
                url: environment.globalPath + "/memberBalance/rechargeOnBankCard",
                data: formData,
                callback: function (data) {
                    if (data.success) {
                        $("#outAdvanceNo").val(data.result.rechargeNo);
                        $("#ticket").val(data.result.ticket);
                    } else {
                        showErrorResultMessage(data);
                    }
                }
            });
        }
    });
    //银行卡 id 赋值
    $(".j-recharge-qbank").on("click", function () {
        var $rechargeCode = $("#j-recharge-code"),
            $ticket = $("#ticket");
        //切换快捷卡时初始化充值提交
        if ($ticket.val() !== "" && !$(this).hasClass("z-selected")) {
            $ticket.val("");
            $("#member_rechargeOnBank_submit").show()
                .removeClass("z-disabled").removeAttr("disabled").val("提交充值");
            $("#j-qbank-amount").attr("disabled", true);
            $("#j-recharge-amount2").parents(".u-recharge-amount2").show();
            $rechargeCode.hide().children().attr("disabled", true);
            $("#member_rechargeOnBank_advance").hide().attr("disabled", true).parent().removeClass("z-last");
        }

        //点击切换银行卡
        var cardId = $(this).data("cardid");
        $(this).addClass("z-selected").siblings().removeClass("z-selected");
        $("#j-cardId").val(cardId);
        $rechargeCode.find('strong').text($(this).data("bankmobile"));
        var bankCode = $(this).data("code");
        showBankLimit(bankCode, 2);
    });

    //充值页面--输入验证码
    $("#member_rechargeOnBank_advance").on("click", function () {
        if ($("#j-bank-checkcode").val() === "") {
            $("#j-bank-checkcode").blur();
            return;
        }

        var data = $("#rechargePage_form_on_card").serializeArray();
        $(this).attr('disabled', "true").addClass("z-disabled").val("支付中");
        var that = $(this);
        $.xPost({
            url: environment.globalPath + "/memberBalance/rechargeOnBankCardCheck",
            data: data,
            callback: function (data) {
                if (data.success) {
                    //弹出层
                    //that.removeAttr("disabled").removeClass("z-disabled").val("");
                    xShade();
                    $("#j-recharge-on-bankcard").show();
                    //location.href = environment.globalPath + "/memberBalance/rechargeIndex";
                } else {
                    that.removeAttr("disabled").removeClass("z-disabled").val("确认充值");
                    var errorMsg = "验证码错误", errType = "normal";
                    if (data.result != null || data.result == "") {
                        if ("短信验证失败" == data.result || "ticket不存在或已失效" == data.result) {
                            errorMsg = "您输入的验证码有误，新的验证码已发送，请注意查收";
                            errType = "smsError";
                        } else {
                            errorMsg = data.result;
                        }
                    }
                    //重新触发获取验证码
                    if (errType === "smsError") {
                        $("#j-recode-tips").addClass("u-recode-btn").click();
                    }
                    $.xDialog({
                        content: errorMsg
                    });
                }
            }
        });
    });

    //下接框选中时，设置.u-pro-text文本值
    $(".u-selector ul li").on("click", function () {
        var parent = $(this).parent(),
            vform = parent.siblings(".Validform_wrong"),
            text = $(this).text(),
            iptText = $(this).parents("td").find(".j-selected-ipt");
        //验证样式
        if (text != "选择" && vform.length > 0) {
            vform.removeClass("Validform_wrong").hide();
        } else if (text == "选择") {
            vform = parent.siblings(".Validform_checktip");
            if (iptText.length > 0 && vform.length > 0) {
                vform.removeClass("Validform_right")
                    .addClass("Validform_wrong")
                    .text(iptText.attr("errormsg")).show()
                    .removeAttr("style");
            }
            text = "";
        }
        $(this).parents("td").find(".u-pro-text").html(text);
    });

    //文本框离开时，设置.u-pro-text文本值
    $(".u-pro-input :input[type='text']").on("blur", function () {
        var val = $(this).val();
        $(this).parents("td").find(".u-pro-text").html(val);
    });

    //用户名验证
    var userNameValid = $(".u-profile-name").Validform({
        tiptype: function (msg, o, cssctl) {
            var objtip = $("#u-validform-tips");
            cssctl(objtip, o.type);
            if (o.type == 2) { //通过
                objtip.text("");
                objtip.hide();
            } else {
                objtip.show();
                objtip.text(msg);
            }
        },
        postonce: true,

        datatype: { //只允许中文和字母且字符长度在4~10之间
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
            }
        }
    });


    //修改昵称
    $(".u-btn-user-name").click(function () {
        if ($(this).hasClass("z-disabled")) {
            return;
        }
        var userName = $("#userName").val();
        var userNameToken = $("#userNameToken").val();
        if (!userNameValid.check(false)) {
            return;
        }
        $(this).val("请稍候...");
        $(this).addClass("z-disabled");
        var config = {
            url: environment.globalPath + "/member/saveUserName",
            data: {
                userName: userName,
                xToken: userNameToken
            },
            type: 'post',
            callback: saveUserNameCallback
        };
        $.xPost(config);
    });
    //邮箱开通绑定
    $("#j-email-verify").on("click", function () {
        //var sendEmailIsShow = $(".u-email-send").css("display");
        if (!$(".u-email-send:visible").length) {
            $(".u-security-email").fadeIn();
        }
        return false;
    });
    $("#j-email-btn").on("click", function () {
        var val = $("#j-email-input").val(),
            reg = /^\w+([-+.']\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
        var bindedEmail = $("#j-member-email").html();
        if (typeof val !== "undefined" && val.length !== 0) {
            if (reg.test(val)) {
                if (bindedEmail == val) {
                    $.xDialog({
                        content: "该邮箱已绑定！"
                    });
                    return;
                }
                $("#j-email-btn").addClass("z-disabled").attr("disabled", true).html("发送中");
                $("#email").html(val);
                var aIndex = val.indexOf("@");
                var mailUrl = "http://mail." + val.substring(aIndex + 1);
                $("#j-goto-email").attr("href", mailUrl);
                var xToken = $("#xToken").attr("value"),
                    data = {
                        'email': val,
                        'xToken': xToken
                    };
                $.xPost({
                    url: environment.globalPath + "/member/to/bind/email",
                    data: data,
                    callback: function (data) {
                        $("#j-email-btn").removeClass("z-disabled").removeAttr("disabled", true).html("确定");
                        var result = data.success;
                        if (!result) {
                            $.xDialog({
                                content: data.resultCodeEum[0].msg
                            });

                        } else {
                            $(".u-security-email").hide();
                            $(".u-email-send").fadeIn();
                        }
                    }
                });
            } else {
                $.xDialog({
                    content: "请输入正确的邮箱地址"
                });
            }
        } else {
            $.xDialog({
                content: "请输入邮箱"
            });
        }
    });
    $("#j-email-cancel").on("click", function () {
        $(".u-security-email").hide();
    });

    //邮件再次发送
    $("#j-resend-email").on("click", function () {
        var resendStatus = $(this).html();
        if (resendStatus == "发送中") {
            return false;
        }
        $(this).html("发送中");
        var val = $("#j-email-input").val();
        var xToken = $("#xToken").attr("value"),
            data = {
                'email': val,
                'xToken': xToken
            };
        $.xPost({
            url: environment.globalPath + "/member/to/bind/email",
            data: data,
            callback: function (data) {
                //邮箱已绑定，再次发送邮件时提示邮箱已绑定
                var result = data.success;
                if (!result) {
                    $.xDialog({
                        content: data.resultCodeEum[0].msg
                    });

                } else {
                    $.xDialog({
                        title: '提示',
                        content: '发送成功！',
                        callback: function () {
                            $("#j-resend-email").html("没收到 再次发送");
                        }
                    });
                }
            }
        });
        return false;
    });

    //菜单下的箭头定位
    arrowLocator(".u-uc-menu");

    //邀请链接复制
    var copyInvite = $('#j-copy-invite')
    if (copyInvite.length === 1) {
        var clipboard = new Clipboard('#j-copy-invite');
        clipboard.on('success', function (e) {
            console.info('Action:', e.action);
            console.info('Text:', e.text);
            console.info('Trigger:', e.trigger);
            $.xDialog({
                type: "success",
                content: "复制成功！"
            });
            e.clearSelection();
        });
    }

    // if (copyInvite.length === 1) {
    //     copyInvite.zclip({
    //         path: environment.globalPath + "/static/lib/js/zclip/ZeroClipboard.swf",
    //         copy: function () {
    //             return $('#j-invite-url').data("url");
    //         },
    //         afterCopy: function () {
    //             $.xDialog({
    //                 type: "success",
    //                 content: "复制成功！"
    //             });
    //         }
    //     });
    // }
    //人气值进度位置计算
    function showRepPoint() {
        var repPoint = $("#j-rep-point");
        if (repPoint.length === 1) {
            var pointVal = Number(repPoint.data("point")),
                point = repPoint.find("em"),
                pointWidth = 890 * 0.25;
            if (pointVal === 0) {
                point.hide();
            } else if (pointVal >= 950) {
                $(".u-rep-process em i").height("53px");
                if (pointVal > 1000) {
                    repPoint.css("width", "100%");
                    repPoint.removeClass("z-normal");
                } else {
                    repPoint.removeClass("z-normal");

                    repPoint.css("width", pointWidth * 3 + pointWidth * (pointVal / 1000));
                }

            } else {
                var pointLeft = 0;
                if (pointVal <= 50) {
                    if (pointVal > 4) {
                        pointLeft = pointWidth * (pointVal / 50);
                    } else {
                        pointLeft = 20;
                    }
                } else if (pointVal <= 100 && pointVal > 50) {
                    pointLeft = pointWidth + pointWidth * ((pointVal - 50) / 50);
                } else if (pointVal <= 500 && pointVal > 100) {
                    pointLeft = pointWidth * 2 + pointWidth * ((pointVal - 100) / 400);
                } else if (pointVal <= 1000 && pointVal > 500) {
                    pointLeft = pointWidth * 3 + pointWidth * ((pointVal - 500) / 500);
                }
                repPoint.css("width", pointLeft);
            }
        }
    }

    showRepPoint();
    //充值金额校验
    $("#j-recharge-amount,#j-recharge-amount2").on("blur", function () {
        var amount = Number($(this).val()),
            amountVal = $(this).val(),
            isError = false,
            errorTipsArea = $(this).next(".Validform_checktip"),
            errorTips = "金额必须为整数或小数，小数点不超过2位。",
            point = -1;
        if (amountVal.indexOf(".") !== -1) {
            point = amountVal.length - amountVal.indexOf(".") - 1;
        }
        if (!isNaN(amount)) {
            if (amount === 0) {
                isError = true;
            } else if (amount < 0.01) {
                isError = true;
            } else if (point > 2 || point === 0) {
                isError = true;
            } else if (amount > 100000000) {
                isError = true;
                errorTips = "您输入的金额超限";
            } else {
                errorTipsArea.remove();
            }
        } else {
            isError = true;
            errorTips = "请输入有效金额";
        }
        if (isError) {

            if (errorTipsArea.length !== 1) {
                $(this).after("<span class='Validform_checktip Validform_wrong'>" + errorTips + "</span>");
            } else {
                errorTipsArea.text(errorTips);
            }
        }
    });

    $("#j-recharge-amount, #j-recharge-amount2, #withdrawAmount").on("keydown", function (e) {
        var amount = $(this).val();
        return checkKeyForFloat(amount, e);
    });
    //提现金额校验
    $("#withdrawAmount").on("blur", function () {
        var amount = Number($("#j-available-balance").data("amount")),
            withdrawAmount = $(this).val(),
            amountMsg = "",
            msgType = 0;

        function amountTips(iType, msg) {
            var type = typeof (iType) !== "undefined" ? iType : 0,
                valWrongObj = $(".u-amount-wrap .Validform_wrong");
            switch (type) {
                case 0:
                    if (valWrongObj.length > 0) {
                        valWrongObj.html(msg);
                    } else {
                        $(".u-amount-wrap").append("<span class='Validform_checktip Validform_wrong'>" + msg + "</span>");
                    }
                    break;
                case 1:
                    if (valWrongObj.length > 0) {
                        valWrongObj.remove();
                    } else {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }

        if (Number(withdrawAmount) && Number(withdrawAmount) > 0) {
            var l = withdrawAmount.length,
                n = withdrawAmount.indexOf(".");
            if (n === -1) {
                if (withdrawAmount > amount) {
                    amountMsg = "您的可提现金额只有" + amount + "元";
                } else {
                    msgType = 1;
                }
            } else {

                if (n !== 0 && l - 1 > n) {
                    if (l - (n + 1) > 2) {
                        amountMsg = "金额必须为整数或小数，小数点后不超过2位";
                    } else if (withdrawAmount > amount) {
                        amountMsg = "您的可提现金额只有" + amount + "元";
                    } else {
                        msgType = 1;
                    }
                } else {
                    amountMsg = "请输入有效金额";
                }
            }
        } else {
            amountMsg = "请输入有效金额";
        }
        amountTips(msgType, amountMsg);
        $(this).data("isCorrect", msgType);
    });

    //银行卡限制只能输入数字
    $(".j-bankcard-id").numOnly();

    // 输入金额禁止回车
    $(".j-paytype, #withdrawAmount").on("keydown", function (e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
            return false;
        }
    });

    //加载消息订阅配置
    if (typeof memberNotifySettingsConfig !== "undefined") {
        renderNotifySettingItem();
    }

    //加载交易收益日历
    if ($("#j-transaction-tab").length) {
        renderProfitCal('#j-transaction-tab', 24);
        loadTransactionInterestData();
    }

    //我的消息
    var filterCheckObj = $("#j-filter-check"),
        umsgListObj = $("#j-umsg-list"),
        msgUrl = environment.globalPath + "/message/queryMessage";
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
                var date = formatDate(item.time, "yyyy-mm-dd"),
                    time = formatDate(item.time, "HH:mm:ss"), hasRead = "";
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
    //人气值兑换

    var exchangeList = $(".j-reputation-exchange"),
        exchangeCoupon = $(".j-exchage-coupon");
    if (exchangeList && exchangeList.length > 0) {
        var repList = $(".j-rep-list");

        // 修正未签到，我的优惠页面提示框出现时，点击蒙层，蒙层隐藏但是 签到引导框没有隐藏的 bug，同时把绑定事件从循环中提出来
        $('body').on('click', '.u-shade', function () {
            if ($('#j-checkin-guide').length == 0) {
                xShade('hide');
                repList.parent().hide();
                $('.j-cashCouponSlideBtn-show').hide();// cc add，在点击阴影层时左右按钮没有隐藏
            }
        });

        //兑换选择
        repList.each(function () {
            var _this = $(this),
                repButton = _this.next('div').find('.j-exc-button'),
                curPoint = _this.next('div').find('.j-exc-curPoint');
            _this.parent().appendTo('body');

            _this.on('click', 'li.z-usabled', function (e) {
                e.stopPropagation();
                $(this).addClass('z-selected').siblings().removeClass('z-selected');
                var index = $(this).index();
                repButton.css('margin-left', 244 * index).show();
                curPoint.val($(this).data('templateid'));
            });

            //人气值卡片显示
            var repPoint = $('#j-rep-point').data('point');

            $('.u-rep-card').each(function () {
                var point = $(this).parent().data('point');
                if (point <= repPoint) {
                    $(this).addClass('z-card-usabled').parent().addClass('z-usabled');
                }
            });
            //当人气值大于500点样式
            //         log($('#j-cash-list .u-rep-card').length);
            //         var redCashLength=$('#j-cash-list .u-rep-card').length;
            //         switch (redCashLength){
            //             case 5:
            //                 $('#j-cash-list').css('marginLeft','-244px');
            //                 repButton.css('marginLeft','244px').show();
            //                 break;
            //             case 6:
            //                 $('#j-cash-list').css('marginLeft','-488px');
            //                 repButton.css('marginLeft', '488px').show();
            //                 break;
            //             default :
            //                 break;
            //
            //         }
            $('#j-profit-list').find('.z-usabled').eq(-1).click();
            $('#j-cash-list').find('.z-usabled').eq(-1).click();
            //人气值兑换
            repButton.on('click', function () {

                if ($(this).hasClass('z-disabled')) {
                    return false;
                }

                $(this).addClass('z-disabled').prop('disabled', 'disabled');

                exchangeCouponByTemplateId(curPoint.val());

                //兑换回调
                function callback() {
                    $('body').find('.u-shade').click();
                    $('.j-cashCouponSlideBtn-show').hide();
                }
            });

            //隐藏全都不存在的条件
            var $disabledScopes = _this.find('.j-couponScope-disabled');
            if ($disabledScopes.length === 4) {
                $disabledScopes.remove();
                //var repBox = _this.parent();
                //repBox.height(repBox.height() - 48);
            }
            //现金券的左右滑动

            $('.j-slideBtn').on('click', function () {
                var direction = $(this).data('direction'),
                    step = 0,
                    list = $('#j-cash-list'),
                    now = new Date().getTime(),
                    listSize = list.find('li').length,
                    offset = Number(list.css('margin-left').replace('px', ''));
                // 防止频繁点击
                var difftime = $(this).data('difftime') || 0;
                if (difftime !== 0 && now - difftime < 500) {
                    return false;
                }
                $(this).data('difftime', now);

                //滑动方向
                if (direction === 'left') {
                    step = -1;
                    if (Math.abs(offset) / 244 >= listSize - 4) {
                        return false;
                    }
                } else {
                    step = 1;
                    if (offset === 0) {
                        return false;
                    }
                }
                list.animate({
                    marginLeft: offset + step * 244
                }, 500);
                repButton.parent().animate({
                    marginLeft: offset + step * 244
                }, 500);

                _this.on('click', 'li.z-usabled', function (e) {
                    e.stopPropagation();
                    $(this).addClass('z-selected').siblings().removeClass('z-selected');
                    var index = $(this).index();
                    repButton.css('margin-left', index * 244).show();
                    curPoint.val($(this).data('templateid'));
                });
            });
        });
        exchangeCoupon.on('click', function () {

            var i = $(this).index();
            repList.eq(i).parent().show();
            xShade();
        });
        $('#j-exchange-cashCoupon').on('click', function () {
            $('.j-cashCouponSlideBtn-show').show();
        });

        //关闭按钮
        $('.j-exc-close').on('click', function () {
            $('.u-shade').click();
            $('.j-cashCouponSlideBtn-show').hide();
        });
        //新手任务
        var $repTaskList = $('#j-reptesk-list');

        $repTaskList.on('mouseenter', 'li', function () {
            showQrcode($(this));
        }).on('mouseleave', 'li', function () {
            showQrcode($(this));
        });

        function showQrcode(target) {
            var times = target.data('times'),
                title = target.find('em').text(),
                tag = [],
                returnValue = true;

            !times ? target.data('times', 1) : target.data('times', times + 1);

            switch (title) {
                case '绑定微信':
                    tag = ['weixin', '/static/img/common/weixin_qrcode1.jpg'];
                    returnValue = false;
                    break;
                case '体验APP':
                    tag = ['app', '/static/img/common/QR_code.png'];
                    returnValue = false;
                    break;

                default:
                    break;
            }

            if (!returnValue) {
                (times % 2) ? target.find('strong').remove() :
                    target.append('<strong class="u-qrcode-' + tag[0] +
                        '"><img src="' + tag[1] + '" /></strong>');
                target.find('a').prop('href', 'javascript:void(0)')
                    .removeAttr("target")
                    .removeAttr("href");
            }
            return returnValue;

        }

    }
//现金券左右移动按钮定位
    var windowWidth = $(window).width();
    resizeCouponLayer(windowWidth);

    $(window).on('resize', function () {
        var width = $(window).width();
        resizeCouponLayer(width);
    });
    function resizeCouponLayer(windowWidth) {
        var offset = 0;
        if (windowWidth > 992) {
            offset = (windowWidth - 992) / 2 + 992;
        } else {
            offset = 992;
        }
        $('.u-prevBtn').css('right', offset);
        $('.u-nextBtn').css('left', offset);
    }

    /***
     * 资金流水分页处理
     */
    //initPagination 初始化分页
    if ($('#capitalInOutLogTable').length) {
        getCapitalInOutLogList(1);
    }

    //用户中心Banner
    var ucBanner = $('#j-uc-banner');
    if (ucBanner.length) {
        var $profit = $('#j-income-statistics');
        if ($profit.length) {
            bannerTimer = setTimeout(bannerTimeoutAction, 3000);
            ucBanner.removeClass('f-dn').data('expand', true);
        } else {
            ucBanner.width(10).removeClass('f-dn');
            ucBanner.data('expand', false);
        }

        var intervalTime = 0;
        ucBanner.on('mouseenter', function () {
            if (ucBanner.data('expand')) {
                clearTimeout(bannerTimer);
            } else {
                bannerTimeoutAction('expand');
            }
            intervalTime = new Date().getTime();
        }).on('mouseleave', function () {

            intervalTime = (new Date().getTime()) - intervalTime;
            if (intervalTime < 350) {
                setTimeout(function () {
                    bannerTimeoutAction();
                }, 3000 - intervalTime);
            } else if (ucBanner.data('expand')) {
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
        }, 350, function () {
            ucBanner.data('expand', isExpand);
        });
    }


    //
    function pageselectCallback(page_index, jq) {
        getCapitalInOutLogList(page_index + 1);
    }

//加载资金流水表分页数据
    function getCapitalInOutLogList(page_index, reload) {
        pageIndex = page_index;
        $("input[name='currentPage']").val(pageIndex);
        var data = $("#j-capitalForm").serializeArray();
        var url = environment.globalPath + "/member/capitalInOutLogList";
        $.xPost({
            url: url,
            data: data,
            callback: function (msg) {
                var total = 0,
                    tbody = $("#capitalInOutLogTable").find('tbody');
                tbody.empty();
                if (msg != null && msg.capitalPage != null) {
                    var total = msg.capitalPage.totalElements;
                    if (total === 0) {
                        $('#capitalPagination').empty();
                        var typeDesc = "资金流水";
                        if (msg.capitalQuery.typeDesc) {
                            typeDesc = msg.capitalQuery.typeDesc;
                        }
                        var trHtml = tbody.append("<tr><td colspan='7' class='u-table-empty'>暂无" + typeDesc + "记录</td></tr>");
                        tbody.append(trHtml);
                    }
                    if (msg.capitalPage.data !== "undefined" && msg.capitalPage.data.length > 0) {

                        $.each(msg.capitalPage.data, function (v, n) {
                            var income = n.formatIncome,
                                incomeHtml = '<td class="f-fwb f-tar u-cOutLog-earning"><em class="f-fwb">' + income + '</em></td>',
                                outlay = n.formatOutlay,
                                outlayHtml = '<td class="f-fwb f-tar"><em class="f-fwb">' + outlay + '</em></td>';
                            if (income === '-') {
                                incomeHtml = '<td class="f-tac u-cOutLog-earning"><em class="f-fwb">' + income + '</em></td>';
                            }
                            if (outlay === '-') {
                                outlayHtml = '<td class="f-fwb f-tac"><em class="f-fwb">' + outlay + '</em></td>';
                            }
                            var trHtml = "<tr><td class='f-tac'>" + formatDate(n.happenTime, 'yyyy-mm-dd') + "<span>" +
                                formatDate(n.happenTime, 'HH:mm:ss') + "</span></td><td>" +
                                n.typeDesc + "</td>" + incomeHtml + outlayHtml + "<td class='f-tar'><em>" + n.formatBalance + "</em></td><td>" + n.remark + "</td></tr>";
                            tbody.append(trHtml);
                        });

                        if ($("#capitalPagination").html().length == '' || (typeof reload !== "undefined" && reload !== "" )) {
                            initPagination(total);
                        }
                    }
                }
            }
        });
    }

//initPagination 初始化分页
    function initPagination(total) {
        $("#capitalPagination").pagination(total, {
            prev_show_always: true,
            next_show_always: true,
            prev_text: "上一页",
            next_text: "下一页",
            link_to: 'javascript:void(0)',
            callback: pageselectCallback,
            items_per_page: ITERMS_PER_PAGE_INT //每页显示1项
        });
    }

    $(".j-capital-selector ul li").on("click", function () {
        var type = $(this).val();
        $("input[name='type']").val(type);
        getCapitalInOutLogList(1, "reload");
    });
    var $capitalSearch = $(".j-capital-search");
    $capitalSearch.on("click", function () {
        var startTime = $("#jStartTime").val(),
            endTime = $("#jEndTime").val();

        if (startTime || endTime) {
            $("input[name='startTime']").val(startTime);
            $("input[name='endTime']").val(endTime);
        }

        getCapitalInOutLogList(1, "reload");
    });

    //时间选择器
    var timeRang = $('#j-filter-timeRange'),
        capitalForm = $('#j-capitalForm'),
        dateInput = $('#j-filter-datePicker'),
        endTime = formatDate(environment.serverDate),
        startInput = capitalForm.find('[name="startTime"]'),
        endInput = capitalForm.find('[name="endTime"]'),
        startDateInput = $('#jStartTime'),
        endDateInput = $('#jEndTime');

    //时间选择器赋值
    window.pickFilterDate = function () {
        var start = $('#jStartTime'),
            startTime = start.val(),
            end = $('#jEndTime'),
            endTime = end.val();
        startInput.val(startTime);
        endInput.val(endTime);

        endDateInput.removeClass('z-hidden');

        //清除时间段过滤
        timeRang.find('em').removeClass('z-selected');
        if (startDateInput.hasClass('z-hidden')) {
            startInput.val(' ');
        }
//        getCapitalInOutLogList(1, 'reload');
    };
    window.startTimePicked = function () {
        $('#jStartTime').removeClass('z-hidden');
    };
    //时间范围赋值
    timeRang.on('click', 'em', function () {
        var startTime,
            endTime = formatDate(environment.serverDate),
            endDate = new Date(endTime),
            type = $(this).data('type'),
            day = endDate.getDate(),
            month = endDate.getMonth();
        startDateInput.addClass('z-hidden');
        endDateInput.addClass('z-hidden');
        switch (type) {
            case 1://7days
                endDate.setDate(day - 6);
                startTime = formatDate(endDate.getTime());
                break;
            case 2://1 month
                endDate.setMonth(month - 1);
                startTime = formatDate(endDate.getTime());
                break;
            case 3://1 year
                endDate.setMonth(month - 3);
                startTime = formatDate(endDate.getTime());
                break;
            case 0://all
                dateInput.find('input').addClass('z-hidden');
                startTime = '';
                endTime = '';
                break;
        }
        //清除时间选择框
        startDateInput.val('');
        endDateInput.val('');
        //表单时间赋值
        startInput.val(startTime);
        endInput.val(endTime);

        $(this).parent().data({
            'start': startTime,
            'end': endTime
        });
        $capitalSearch.click();
        //选中
        $(this).addClass('z-selected').siblings().removeClass('z-selected');
    });

    /*
     * 获取收益日历每日点数
     * @param num {Number} 当日点数
     * @param mark {Boolean} 是否已还款标记
     * @return {String} 返回html字符串
     */
    function getDataPoint(num, mark) {
        var circle = "●",
            point = "";
        if (mark) {
            circle = "○";
        }
        if (num < 3) {
            for (var i = 1; i <= num; i++) {
                point += circle;
            }
        } else {
            point = circle + circle + circle;
        }
        if (point !== "") {
            return "<i>" + point + "</i>";
        }
        return "";
    }

    function exchangeCouponByTemplateId(templateId) {
        var xToken = $("#xToken").attr("value");
        $.xPost({
            url: environment.globalPath + "/coupon/exchange",
            data: {
                'couponTemplateId': templateId,
                'num': 1,
                'xToken': xToken
            },
            callback: function (data) {
                if (data.success) {
                    var couponName = "查看我的现金券!", url = "/coupon/couponList";
                    if (data.result != null && data.result.couponType != null) {
                        if (data.result.couponType == 2) {
                            couponName = "查看我的收益券";
                            url = "/coupon/profitCouponList";
                        }
                    }
                    $('body').find('.u-shade').click();
                    $('.j-cashCouponSlideBtn-show').hide();
                    $.xDialog({
                        content: "优惠券兑换成功，" + "<a class='u-dialog-link' href='" + environment.globalPath + url + "' >" + couponName + "</a>",
                        type: "success", //success,warn,error,info
                        callback: function () {
                            window.location.href = environment.globalPath + "/coupon/reputation";
                        } //确认按钮回调
                    });
                } else {
                    var $repButton = $('#j-cash-button');
                    if (data.result.couponType == 2) {
                        $repButton = $('#j-profit-button');
                    }
                    $repButton.removeClass('z-disabled').removeProp('disabled');
                    $.xDialog({
                        content: "领用失败！",
                        type: "error"
                    });
                }
            }
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

    /**
     * 加载交易收益日历
     */
    function loadTransactionInterestData() {

        var config = {
            url: environment.globalPath + "/transaction/interest/calendar",
            type: 'get',
            callback: loadTransactionInterestDataCallback
        };

        $.xPost(config);
    }

    //渲染收益日历
    function renderProfitCal(tableId, month) {
        var calList = [],
            cal = '',
            table = $(tableId),
            date = new Date(),
            curMonth = date.getMonth(),
            curYear = date.getFullYear();
        if (typeof (month) !== "number") {
            return false;
        }
        for (var j = 1; j <= month; j++) {
            cal += '<td></td>';
        }
        for (var i = 1; i <= 31; i++) {
            calList.push('<tr>' + cal + '</tr>');
        }
        calList = calList.join('');
        //插入表格
        table.find('tbody').append(calList);
        $('.m-profit-cal table').width(104 * month);
        //当前月份显示在中间
        $('#j-profit-cal').scrollLeft(((curYear - 2015) * 12 - 2 + curMonth) * 104);
    }

    /**
     * 加载交易收益日历回调函数
     */
    function loadTransactionInterestDataCallback(data) {

        //renderProfitCal('#j-transaction-tab', 24);

        if (typeof data === "undefined" || data.resultList === null) {
            return;
        }
        //设置当日还款时间
        var curDate = new Date(),
            table = $("#j-transaction-tab"),
            interval = (curDate.getFullYear() - 2015) * 12;
        table.find("tr:eq(" + (curDate.getDate() + 1) + ") td:eq(" + (curDate.getMonth() + 2 + interval) + ")").addClass("z-today");
        curDate = new Date(curDate.getFullYear(), curDate.getMonth(), curDate.getDate());

        $.each(data.resultList, function (i, obj) {
            var month = obj.month,
                day = obj.day,
                num = obj.num,
                year = obj.year,
                totalPayablePrincipal = obj.totalPayablePrincipal,
                totalPayableInterest = obj.totalPayableInterest;
            var mm = month;
            if (num > 0) {
                //月份在界面设置不是有序的，所以这里需要特别处理
                var curYear = new Date().getFullYear();

                if (year === curYear) {
                    mm = month + (curYear - 2015) * 12 + 2;
                } else {
                    mm = month + (year - 2015) * 12 + 2;
                }
                //是否已还款
                var calDate = new Date(obj.payDate).getTime(),
                    mark = 1;
                if (calDate < curDate) {
                    mark = 0;
                }
                var targetRow = table.find("tr:eq(" + (day + 1) + ") td:eq(" + (mm - 1) + ")");
                targetRow
                    .addClass("j-cal-tips")
                    .attr({
                        "data-date": year + "-" + month + "-" + day
                    })
                    .html(getDataPoint(num, mark));
            }

        });
        //收益日历提示
        calTips();
        //收益统计
        incomeStatisticsDetail();
    }

    //收益日历提示
    function calTips() {
        var $calTable = $('#j-transaction-tab');
        $calTable.on("mouseenter", ".j-cal-tips", function () {
            var _this = $(this),
                date = $(this).data("date"),
                calData = $(this).data('calData');

            if (!calData) {
                $.xPost({
                    url: "/memberBalance/getMemberInterestDetail",
                    data: {"date": date},
                    callback: function (data) {
                        if (data.success && data.result) {
                            renderCalList(data);
                            _this.data('calData', data);
                            // circle();
                        }

                    }
                });
            } else {
                renderCalList(calData);
            }
            //function circle(){
            //    $(".j-cal-tips").on('mouseenter',function(){
            //
            //        var hoverTime=$(this).data('hoverTime')||null,
            //            time=(new Date().getTime())-hoverTime;
            //        if(hoverTime===null||(hoverTime!==null&&time>350)){
            //            clearTimeout(time);
            //        }
            //
            //        alert(time);
            //
            //    }).on('mouseleave',function(){
            //        $(this).data('hoverTime',new Date().getTime());
            //    });
            //}
            function renderCalList(data) {
                var tplData = data.result,
                    dateArray = date.split('-');
                tplData.date = date;
                tplData.month = dateArray[1];
                tplData.day = dateArray[2];

                if (typeof(template) !== 'function') {
                    return false;
                }

                var html = template('j-cal-tpl', tplData),
                    $calDetail = $('#j-cal-detail');
                $calDetail.html(html);
                var size = tplData.detailList.length;
                var calHeight = 185 + (size < 4 ? size - 1 : 3) * 34;
                _this.popupPosition({
                    target: $calDetail,
                    width: 556,
                    height: calHeight
                });
                $calTable.on('mouseenter', 'td', function () {
                    if (!$(this).hasClass('j-cal-tips')) {
                        $('#j-cal-detail').hide();
                    }
                })
            }

        }).on("mouseleave", ".j-cal-tips", function (e) {
            //$('.ui-popup').remove();
        });

    }

    //收益统计
    function incomeStatisticsDetail() {
        var $incomeTable = $('#j-income-statistics');
        $incomeTable.on("mouseover", ".j-popup-profit", function () {
            var _this = $(this),
                title = $(this).data("title"),
                targetUrl = $(this).data('url'),
                type = $(this).data('type'),
                reqType = _this.parents(".u-statBox").data("type"),
                principalType = _this.parents(".j-principal-type").data("type"),
                incomeData = $(this).data('incomeData');
            var url = "/memberBalance/getTransactionPrincipalDetailForMember";
            if (reqType == "interest") {
                url = "/memberBalance/getTransactionInterestDetailForMember";
            } else {
                if (principalType == "transPrincipal") {
                    url = "/memberBalance/getTransactionDetailForMember";
                }
            }

            if (!incomeData) {
                $.xPost({
                    url: url,
                    data: {"type": type},
                    callback: function (data) {
                        if (data.success && data.result) {
                            renderIncomeList(data);
                            _this.data('incomeData', data);
                        }
                    }
                });
            } else {
                renderIncomeList(incomeData);
            }

            function renderIncomeList(data) {
                var _incomeData = data;
                _incomeData.title = title;
                _incomeData.url = targetUrl;
                _incomeData.reqType = reqType;
                if (typeof(template) !== 'function') {
                    return false;
                }

                var html = template('j-transaction-interest-detail-tpl', _incomeData),
                    $interestDetail = $('#j-transaction-interest-detail');
                var profitHeight = 145 + (data.result.length - 1) * 28;

                $interestDetail.html(html);
                _this.popupPosition({
                    target: $interestDetail,
                    height: profitHeight,
                    width: 350,
                    offset: 15
                });
                _this.on('mouseleave', function () {
                    var timer = setTimeout(function () {
                        $interestDetail.hide();
                    }, 300);
                    $interestDetail.on('mouseenter', function () {
                        clearTimeout(timer);
                    });
                })

            }

        }).on("mouseleave", ".j-popup-profit", function (e) {
            $('.ui-popup').remove();
        });
    }


    /**
     * 对日期进行格式化，
     * @param date 要格式化的日期
     * @param format 进行格式化的模式字符串
     *     支持的模式字母有：
     *     y:年,
     *     M:年中的月份(1-12),
     *     d:月份中的天(1-31),
     *     h:小时(0-23),
     *     m:分(0-59),
     *     s:秒(0-59),
     *     S:毫秒(0-999),
     *     q:季度(1-4)
     * @return String
     * @author yanis.wang
     * @see    http://yaniswang.com/frontend/2013/02/16/dateformat-performance/
     */
    if (typeof(template) === 'function') {
        template.helper('dateFormat', function (date, format) {
            date = new Date(date);
            var map = {
                "M": date.getMonth() + 1, //月份
                "d": date.getDate(), //日
                "h": date.getHours(), //小时
                "m": date.getMinutes(), //分
                "s": date.getSeconds(), //秒
                "q": Math.floor((date.getMonth() + 3) / 3), //季度
                "S": date.getMilliseconds() //毫秒
            };
            format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
                var v = map[t];
                if (v !== undefined) {
                    if (all.length > 1) {
                        v = '0' + v;
                        v = v.substr(v.length - 2);
                    }
                    return v;
                }
                else if (t === 'y') {
                    return (date.getFullYear() + '').substr(4 - all.length);
                }
                return all;
            });
            return format;
        });
    }

    /**
     * 保存用户信息
     */
    function saveMemberInfo() {
        //    var checkFlag = checkDateSelect();
        //    if (!checkFlag) {
        //        $("#j-profile-update").html("保存");
        //        $("#j-profile-update").removeClass("z-disabled");
        //        return false;
        //    }
        getChooseAreaData("censusRegister");
        getChooseAreaData("region");
        $("#city").val($("#region-area-area").val());
        var jad = $("#j-area-address");
        var add = $("#address");
        var area = $("#region-address").text();
        jad.text(area + add.val());
        $("#areaFullName").val(area);
        $("#j-census-register-name").val($("#censusRegister-address").text());
        $("#j-census-register-id").val($("#censusRegister-area-area").val());
        var data = $("#memberInfoForm").serializeArray();
        var config = {
            url: environment.globalPath + "/member/saveMemberInfo",
            data: data,
            callback: saveMemberInfoCallback
        };
        $.xPost(config);
    }


    /**
     * 保存用户信息回调函数
     * @param data
     */
    function saveMemberInfoCallback(data) {
        if (data != "") {
            if (data.success) {
                $("#j-profile-update").html("修改");
                $("#j-profile-update").attr("data", "edit");
                $(".u-profile-table table").removeClass("z-editabled");
                $("#j-profile-update").removeClass("z-disabled");
            } else {
                $.xDialog({
                    type: "error",
                    content: "详细信息保存失败"
                });
            }
        }
    }

    /**
     * 保存用户昵称回调函数
     * @param data
     */
    function saveUserNameCallback(data) {
        $(".u-btn-user-name").removeClass("z-disabled").val("确定");
        if (data != "") {
            if (data.success) {
                $("#user-name").text($("#userName").val());
                $("#userName").remove();
                $(".u-btn-user-name").remove();
            } else {
                $.xDialog({
                    type: "error",
                    content: data.resultCodeEum[0].msg
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
//个人中心头部余额展开效果
    var $couponHeadbalance = $('#j-couponHead-balance');
    $couponHeadbalance.on('mouseenter', function () {
        var hoverTime = $(this).data('hoverTime') || null,
            time = (new Date().getTime()) - hoverTime;
        if (hoverTime === null || (hoverTime !== null && time > 500)) {
            $(this).animate({width: '375px'}, 500).find('.j-couponHead-btn').animate({opacity: 1}, 500);
        }
    });
    $couponHeadbalance.on('mouseleave', function () {
        $(this).data('hoverTime', new Date().getTime());
        $(this).animate({width: '215px'}, 500).find('.j-couponHead-btn').animate({opacity: 0}, 500);
    });

    //跳转到投资记录页面查询当天的记录
    $("#j-cal-detail").on("click", ".j-jump-investment", function () {
        var date = $(this).data("day"),//查询的日期
            reqdata = {"searchDate": date},//请求的参数
            url = $(this).data("url");//请求地址
        window.open(url + "?searchDate=" + date, "_blank");
    });
})();


//显示银行限额
function showBankLimit(bankCode, typeLimit) {
    $.xPost({
        url: environment.globalPath + "/payment/platformLimit",
        data: {
            'code': bankCode,
            'typeLimit': typeLimit,
            'platformType': 1
        },
        callback: function (data) {
            var quickRemark = $("#td-quick-remark");
            if (data.success) {
                //查询出所有的限额以及对应的bankId
                if (typeLimit == 2) {
                    $("#t-quick").show();
                    $("#h6-bankName-quick").text(data.result.simpleName + "快捷支付限额如下");
                    $("#td-quick-singleLimit").text(showLimitStr(data.result.singleLimit));
                    $("#td-quick-dailyLimit").text(showLimitStr(data.result.dailyLimit));
                    $("#td-quick-minLimit").text(data.result.minLimit);

                    /* if(data.result.remark==null){
                     $("quickRemark").text("");
                     }
                     else{
                     $("quickRemark").text(data.result.remark);
                     }*/
                    data.result.remark === null ? quickRemark.text("") : quickRemark.text(data.result.remark);
                }

                if (data.result.maintenanceContent != null && data.result.maintenanceContent.length > 0) {
                    if (typeLimit == 2) {
                        $(".u-recharge-amount2").addClass("f-dn");
                        $(".d-quick").addClass("f-dn");
                        $(".d-quick-maint").removeClass("f-dn");
                        $(".d-quick-maint").html(data.result.maintenanceContent);
                    }
                    if (typeLimit == 1) {
                        $(".d-eBank").addClass("f-dn");
                        $(".d-ebank-maint").removeClass("f-dn");
                        $(".d-ebank-maint").html(data.result.maintenanceContent);
                    }
                } else {
                    if (typeLimit == 2) {
                        $(".u-recharge-amount2").removeClass("f-dn");
                        $(".d-quick").removeClass("f-dn");
                        $(".d-quick-maint").addClass("f-dn");
                    }
                    if (typeLimit == 1) {
                        $(".d-eBank").removeClass("f-dn");
                        $(".d-ebank-maint").addClass("f-dn");
                        $(".d-ebank-maint").html(data.result.maintenanceContent);
                    }
                }
            } else {
                $("#t-quick").hide();
                $("#h6-bankName-quick").text("");
                $("#td-quick-singleLimit").text('');
                $("#td-quick-dailyLimit").text('');
                $("#td-quick-minLimit").text('');
                quickRemark.text('');
                /*data.result.remark===null?$("quickRemark").text(""):$("quickRemark").text(data.result.remark);*/
                if (typeLimit == 2) {
                    $(".u-recharge-amount2").removeClass("f-dn");
                    $(".d-quick").removeClass("f-dn");
                    $(".d-quick-maint").addClass("f-dn");
                }
                if (typeLimit == 1) {
                    $(".d-eBank").removeClass("f-dn");
                    $(".d-ebank-maint").addClass("f-dn").html(data.result.maintenanceContent);
                }
            }
        }
    });
}

//显示银行维护公告
function showBankMaint(bankId, typeLimit, platformType) {
    $.xPost({
        url: environment.globalPath + "/payment/platformMaint",
        data: {bankId: bankId, typeLimit: typeLimit, platformType: platformType},
        callback: function (data) {

        }
    });
}

function showLimitStr(num) {
    var intNum = parseInt(num);
    if (intNum > 10000) {
        intNum = intNum / 10000;
        return intNum + "万";
    }
    return intNum;
}

//我的优惠券用户提醒
var repuWithdTips = $(".j-reputation-tips");
//鼠标悬浮显示提示框
repuWithdTips.on('mouseenter', function () {
    repuWithdTips.find("p").show();
}).on('mouseleave', function () {
    repuWithdTips.find("p").hide();
});
//
//    var $trigger=$(".f-trigger"),
//        content1 = $trigger.data("tips"),
//        skin = $trigger.data("skin"),
//        align = $trigger.data("align");
//    align = typeof align === "undefined" ? "bottom" : align;
//    content1 = typeof content1 === "undefined" ? "" : content1;
//    skin = typeof skin === "undefined" ? "u-user-tips" : skin;
//    var d = dialog({
//        align: 'top left',
//        skin: skin,
//        content1: content1
//    });
//    d.show(this);
//    $(this).data("hoverObj", d);
//}).on("mouseleave", function () {
//    var d = $(this).data("hoverObj");
//    d.close().remove();
//});
//
//repuWithdTips.on('mouseenter',function(){
//    $(".j-trigger").trigger("mouseenter");
//}).on('mouseleave',function(){
//    $(".j-trigger").trigger("mouseleave");
//});


//IE8 IE9 IE10下我的优惠券红色圆圈图片显示
$(function () {
    var isIE = !!window.ActiveXObject;
    var isIE6 = isIE && !window.XMLHttpRequest;
    var isIE8 = isIE && !!document.documentMode;
    var isIE7 = isIE && !isIE6 && !isIE8;

    if (navigator.appName == "Microsoft Internet Explorer" && navigator.appVersion.match(/8./i) == "8.") {
        $(".f-bar-red").css('display', 'block');
        /*alert("IE 8");*/
    }

});

// //阳光保险+新浪支付的小banner
// var sinapaybanner=$('.j-sinapay-banner');
// setTimeout("sinapaybanner.slideUp();",5000);

