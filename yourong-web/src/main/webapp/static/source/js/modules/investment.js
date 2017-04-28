//我的投资
window.Investment = ns('You.Member.Investment')

;
(function () {
    Investment.$raiseList = $("#j-raise-list")
    Investment.$investList = $("#j-investment-table")
    Investment.$orderList = $('#j-order-list')
    Investment.$transferList = $('#j-transfer-list')
    Investment.$transferProjectList = $('#j-transferProject-list')
    $.fn.extend({
        listSelector: function (callback) {
            var $selector = $(this)

            $selector.on("click", function (e) {

                $(this).toggleClass("z-actived")
                e.preventDefault()
                return false

            }).on("mouseleave", function () {

                $(this).removeClass("z-actived")
            })

            $selector.find('ul').on("mouseenter", function () {

                $(this).parent().addClass("z-actived")

                if ($(this).hasClass('u-scroll-selector')) {
                    e.stopPropagation()
                }
            })

            $selector.on("click", "li", function () {
                var text = $(this).html(),
                    val = $(this).attr("value"),
                    parent = $(this).parent()

                //只限可用状态下选择
                if (!$(this).hasClass("z-disabled")) {

                    parent.siblings("button").html(text)
                    parent.siblings("input").val(val)

                    if (typeof(callback) === 'function') {
                        callback(val)
                    }

                } else {
                    return false
                }
            })

        }
    })

    //状态选择初始化
    Investment.initStatusSelector = function (selector, callback) {
        $('body').find(selector).listSelector(callback)
    }

})()

;
(function () {
    //问候时间
    $("#j-regards").greetingTime();
    // 订单 order
    fnOnGoToPageOrder();
    //募集中项目
    fnOnGoToPageRaise();
    //转让记录
    fnOnGoToPageTransfer(1);
    $(".j-entrust-argreement-link").xArgreement();
    //转让项目
    fnOnGoToPageTransferProject();
    //我的投资详情的委托协议初始化
    $(".j-investentrust-argreement-link").xArgreement();
    //募集中
    $("#j-collecting-argreement-link").xArgreement();

    $("#j-collecting-zrArgreement-link").xArgreement();
    //菜单下的箭头定位
    arrowLocator(".u-uc-menu");

    var timeRang = $('#j-filter-timeRange'),
        investmentForm = $('#investment_form'),
        dateInput = $('#j-filter-datePicker'),
        endTime = formatDate(environment.serverDate),
        startInput = investmentForm.find('[name="startDate"]'),
        endInput = investmentForm.find('[name="endDate"]'),
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

        //清楚时间段过滤
        timeRang.find('em').removeClass('z-selected');
        if (startDateInput.hasClass('z-hidden')) {
            startInput.val(' ');
        }
//        getCapitalInOutLogList(1,'reload');
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
        startInput.val(startTime);
        endInput.val(endTime);

        loadInvestments();
        //选中
        $(this).addClass('z-selected').siblings().removeClass('z-selected');
    });

    $(".j-invest-search").on("click", function () {
        $("input[name='startDate']").val($("#jStartTime").val());
        $("input[name='endDate']").val($("#jEndTime").val());
        //$("input[name='investType']").val($("#investType").val());
        loadInvestments();
    });

    fnOnGoToPage();

    /*查看交易详情*/
    Investment.$investList.on("click", ".details-link", function () {
        var transactionId = $(this).parent().parent().find("input").val();
        var $orderDetail = $('#j-investment-order-detail');
        var opened = $orderDetail.data("opened");
        var tranIdDetail = $orderDetail.data("tranid");
        var tranIdLink = $(this).data("tranid");
        var $this = $(this);
        var category = $(this).data("category");
        var html = '';
        var tShaft = $('#j-raise-ztShaft');
        var zrShaft = $('#j-raise-zrShaft');
        var projectStatus = $(this).data('projectstatus');
        var p2pstatus = $this.data("status");
        var prepayment = $this.data('prepayment'); // 提前还款
        var $transferProtocol = $('#j-transfer-protocol'),
            $ztTransferProtocol = $('#j-ztTransfer-protocol');
        if (prepayment === 1) {
            $('#j-investDetail-prepay-icon').removeClass('z-fadeOut')
        }

        //直投与转投的信息表格
        if (category == 2) {
            html = '认购本金：¥<em class="f-fs24 f-ff-amount" name="formatTransferPrincipal"></em>'
            $('#j-investInfo,#j-investUserInfo').addClass('f-dn');
            $('#j-zrInvestInfo,#j-zrInvestUserInfo').removeClass('f-dn');
            $('#j-investment-order-detail').find('.f-ff-ver').html(html);
        } else {
            html = '投资金额：¥<em class="f-fs24 f-ff-amount" name="formatInvestAmount"></em>'
            $('#j-investInfo,#j-investUserInfo').removeClass('f-dn');
            $('#j-zrInvestInfo,#j-zrInvestUserInfo').addClass('f-dn');
            $('#j-investment-order-detail').find('.f-ff-ver').html(html);
        }

        //隐藏其他箭头并显示当前表单箭头
        $('.j-box-pos').find('s').hide();
        $(this).find('s').fadeIn();
        if (typeof opened == "undefined" || tranIdLink != tranIdDetail) {
            $orderDetail.data("opened", false);
            $('#j-investment-profit-table').data("opened", false);
            $orderDetail.data("tranid", transactionId);
            opened = false;
        }
        if (!opened) {
            $(".u-profit-table,.u-order-detail").hide();
            $.xPost({
                url: environment.globalPath + "/transaction/detail",
                data: {
                    'transactionId': transactionId
                },
                type: "GET",
                callback: function (data) {
                    var transaction = data.result;

                    emptyDetail("#j-investment-order-detail");
                    transactionDetail(transaction);
                    $orderDetail.fadeIn();
                    $orderDetail.data("opened", true);

                    // 如果提前还款，需要显示一个提前还款标记

                    // console.log(status)

                    //合同预览链接
                    $("#j-debt-protocol").attr("href", "" + environment.globalPath + "/transaction/contract/view?orderId=" + transaction.orderId);
                    //直投合同链接
                    $("#j-zt-debtProtocol").attr("href", "" + environment.globalPath + "/transaction/p2pContract/view?orderId=" + transaction.orderId);
                    //转投合同链接
                    // 债权项目转让合同
                    $transferProtocol.attr('href', "" + environment.globalPath + "/transaction/transferContract/view?orderId=" + transaction.orderId)
                    // 直投项目转让合同
                    $ztTransferProtocol.attr('href', "" + environment.globalPath + '/transaction/ztTransferContract/view?orderId=' + transaction.orderId)
                    if (transaction.projectCategory == 2) {
                        // 转投项目添加进度条
                        zrShaft.show();
                        tShaft.hide();

                        //0-募集中 1-回款中 2-已完结 3-流标 4-转让中，5-已转让
                        //订单支付、转让成功、还款完成对应0、1、2
                        if (transaction.status == 0 || transaction.status == 3) {
                            $('.u-tShaft-dl01').find('dt').addClass('z-current');
                            $('.u-info-tShaft').find('span').removeClass().addClass('z-current1');
                        } else if (transaction.status == 1) {
                            $('.u-tShaft-dl01').find('dt').addClass('z-current');
                            $('.u-tShaft-dl02').find('dt').addClass('z-current');
                            $('.u-info-tShaft').find('span').removeClass().addClass('z-current2');
                        } else if (transaction.status == 2) {
                            $('.u-tShaft-dl01').find('dt').addClass('z-current');
                            $('.u-tShaft-dl02').find('dt').addClass('z-current');
                            $('.u-tShaft-dl03').find('dt').addClass('z-current');
                            $('.u-info-tShaft').find('span').removeClass().addClass('z-current3');
                        }

                        if (transaction.isDirectProject) {
                            $('.j-agreement-ztTrProject').removeClass('f-dn');
                            $('.j-agreement-trProject').addClass('f-dn');

                        } else {
                            $('.j-agreement-ztTrProject').addClass('f-dn');
                            $('.j-agreement-trProject').removeClass('f-dn');
                        }
                    } else {
                        //直投项目添加进度轴
                        if (transaction.isDirectProject) {
                            tShaft.show();
                            zrShaft.hide();
                            //协议调整
                            $('.j-agreement-ztProject').removeClass('f-dn');
                            $('.j-agreement-cgProject').addClass('f-dn');
                            $('#j-generalProduct').hide();
                            $('#j-p2pProject').show();
                            if (transaction.status == 3) {
                                $('#j-remarks').show();
                                $("#j-productExpire").hide();
                                $('.u-tShaft-dl01').find('dt').addClass('z-current');
                                $('.u-info-tShaft').find('span').remove('z-current1').addClass('z-current1');
                            } else if (transaction.status == 2) {
                                $("#j-productExpire").show();
                                $("#j-remarks").hide();
                                $('.u-tShaft-dl01').find('dt').addClass('z-current');
                                $('.u-tShaft-dl02').find('dt').addClass('z-current');
                                $('.u-tShaft-dl03').find('dt').addClass('z-current');
                                $('.u-tShaft-dl04').find('dt').addClass('z-current');
                                $('.u-info-tShaft').find('span').remove('z-current1').addClass('z-current4');
                            } else {
                                $("#j-productExpire").show();
                                $("#j-remarks").hide();
                                $('.u-tShaft-dl01').find('dt').addClass('z-current');
                                $('.u-tShaft-dl02').find('dt').addClass('z-current');
                                $('.u-tShaft-dl03').find('dt').addClass('z-current');
                                $('.u-info-tShaft').find('span').remove('z-current1').addClass('z-current3');
                            }
                        } else {
                            tShaft.hide();
                            zrShaft.hide();
                            $('#j-productExpire').hide();
                            $('.j-agreement-ztProject').addClass('f-dn');
                            $('.j-agreement-cgProject').removeClass('f-dn');
                            $('#j-generalProduct').show();
                            $('#j-p2pProject').hide();
                        }
                    }
                }
            });
        } else {
            $orderDetail.hide();
            //箭头显示
            $(this).find('s').hide();
            $orderDetail.data("opened", false);
        }
    });

    /*收益表详情*/
    Investment.$investList.on("click", ".interests-link", function () {
        var transactionId = $(this).parent().parent().find("input").val();
        var $profitTable = $('#j-investment-profit-table')
        var opened = $profitTable.data("opened"),
            tranIdProfit = $profitTable.data("tranid"),
            tranIdLink = $(this).data("tranid");
        var prepayment = $(this).data('prepayment'); // 提前还款
        var category = $(this).data("category");
        var statusTransfer = $(this).data("statustransfer");
        var norHead = $('.j-normal-plan-head');
        var zrHead = $(".j-zr-plan-head");

        if (prepayment == 1) {
            $('#j-investDetail-prepay-icon2').removeClass('z-fadeOut')
        }

        //隐藏其他箭头并显示当前表单箭头
        $('.j-box-pos').find('s').hide();
        $(this).find('s').fadeIn();
        if (typeof opened == "undefined" || tranIdLink != tranIdProfit) {
            $profitTable.data("opened", false);
            $('#j-investment-order-detail').data("opened", false);
            $profitTable.data("tranid", transactionId);
            opened = false;
        }

        $.transactionId = transactionId;

        if (!opened) {
            $('#j-profit-prev').addClass('z-hidden') // 提前还款标记每次都需要重置
            $(".u-profit-table,.u-order-detail").hide();
            $.xPost({
                url: environment.globalPath + "/transaction/interests",
                data: {
                    "transactionId": transactionId
                },
                type: "GET",
                callback: function (data) {
                    var res = data.result;
                    var transactionInterests;
                    var flag;
                    if (!!res) {
                        $.each(res, function (n, v) {
                            $("#j-investment-profit-table").find("td[name='" + n + "'],em[name='" + n + "'],span[name='" + n + "']").html(v);
                        });

                        if (category == 2) {
                            norHead.hide();
                            zrHead.show();
                            zrHead.find("span[name='formatTotalPayment']").html(res.formatTotalPayment);
                            zrHead.find("span[name='formatTransferPrincipal']").html(res.formatTransferPrincipal);
                            zrHead.find("span[name='formatTotalInterest']").html(res.formatTotalInterest);
                        } else {
                            zrHead.hide();
                            norHead.show();
                        }

                        transactionInterests = res.transactionInterests;
                        flag = res.flag;
                        var timeName;

                        if (res.isDirectProject) {
                            timeName = "收益周期:" + res.profitPeriod;
                        } else {
                            timeName = "收益天数:" + res.totalDays + "天";
                        }
                        $("#j-investment-profit-table").find("span[name='timeName']").html(timeName);

                        $(".j-bonus-amount").remove();
                        if (res.leaseBonus) {
                            var leaseBonueValObj = "<div class='j-bonus-amount'><span class='u-dividend-detailLease u-circle-15 f-fs14'><em class='u-dividend-leaseicon'></em>￥" + res.leaseTotalRentalStr + "</span></div>"
                            $("#j-investment-profit-table table").after(leaseBonueValObj);
                            var detailObj = "";
                            $.each(res.leaseBonusDetails, function (n, v) {
                                detailObj = detailObj + "<p><em>" + v.createTimeStr + "</em>￥" + v.bonusAmountStr + "</p>"
                            });
                            $(".j-bonus-amount").append(detailObj);
                        }
                    }
                    $("#j-investment-profit-table tbody tr").remove();
                    if (!!transactionInterests && transactionInterests.length > 0) {

                        var titleNomal = "<tr>" +
                            "<th width='50'>期数</th>" +
                            "<th>支付天数</th>" +
                            "<th>回款时间</th>" +
                            "<th>利息</th>" +
                            "<th>本金</th>" +
                            "<th>状态</th>" +
                            "</tr>";


                        var titleOver = "<tr>" +
                            "<th width='50'>期数</th>" +
                            "<th>支付天数</th>" +
                            "<th>回款时间（预期/实际）</th>" +
                            "<th>利息</th>" +
                            "<th>本金</th>" +
                            "<th>滞纳金</th>" +
                            "<th>状态</th>" +
                            "</tr>";


                        var titlePre = "<tr>" +
                            "<th width='50'>期数</th>" +
                            "<th>支付天数（预期/实际）</th>" +
                            "<th>回款时间（预期/实际）</th>" +
                            "<th>利息（预期/实际）</th>" +
                            "<th>本金</th>" +
                            "<th>状态</th>" +
                            "</tr>";

                        var titlePreOver = "<tr>" +
                            "<th width='50'>期数</th>" +
                            "<th>支付天数（预期/实际）</th>" +
                            "<th>回款时间（预期/实际）</th>" +
                            "<th>利息（预期/实际）</th>" +
                            "<th>本金</th>" +
                            "<th>滞纳金</th>" +
                            "<th>状态</th>" +
                            "</tr>";

                        if ((category == 1 && statusTransfer == 2) || (category == 1 && statusTransfer == 3)) {
                            var titleNomal = "<tr>" +
                                "<th width='50'>期数</th>" +
                                "<th>支付天数</th>" +
                                "<th>回款时间</th>" +
                                "<th>利息（预期/实际）</th>" +
                                "<th>本金（预计/实际）</th>" +
                                "<th>状态</th>" +
                                "</tr>";


                            var titleOver = "<tr>" +
                                "<th width='50'>期数</th>" +
                                "<th>支付天数</th>" +
                                "<th>回款时间（预期/实际）</th>" +
                                "<th>利息（预期/实际）</th>" +
                                "<th>本金（预计/实际）</th>" +
                                "<th>滞纳金</th>" +
                                "<th>状态</th>" +
                                "</tr>";


                            var titlePre = "<tr>" +
                                "<th width='50'>期数</th>" +
                                "<th>支付天数（预期/实际）</th>" +
                                "<th>回款时间（预期/实际）</th>" +
                                "<th>利息（预期/实际）</th>" +
                                "<th>本金（预计/实际）</th>" +
                                "<th>状态</th>" +
                                "</tr>";

                            var titlePreOver = "<tr>" +
                                "<th width='50'>期数</th>" +
                                "<th>支付天数（预期/实际）</th>" +
                                "<th>回款时间（预期/实际）</th>" +
                                "<th>利息（预期/实际）</th>" +
                                "<th>本金（预计/实际）</th>" +
                                "<th>滞纳金</th>" +
                                "<th>状态</th>" +
                                "</tr>";

                        }
                        if (flag == 0) {//正常
                            $("#j-investment-profit-table tbody").append(titleNomal);
                            for (var i = 0; i < transactionInterests.length; i++) {
                                var index = i + 1;
                                var trObj = $("<tr>" +
                                    "<td >" + index + "</td>" +
                                    "<td name='days[" + index + "]'>" + transactionInterests[i].days + "天</td>" +
                                    "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "</td>" +
                                    "<td name='payableInterest[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].formatPayableInterest + "</em></td>" +
                                    "<td name='payablePrincipal[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].formatPayablePrincipal + "</em></td>" +
                                    "<td name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                    "</tr>");
                                if ((category == 1 && statusTransfer == 2) || (category == 1 && statusTransfer == 3)) {
                                    var trObj = $("<tr>" +
                                        "<td >" + index + "</td>" +
                                        "<td name='days[" + index + "]'>" + transactionInterests[i].days + "天</td>" +
                                        "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "</td>" +
                                        "<td name='payableInterest[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].payableInterest + "/￥" + transactionInterests[i].realPayInterest + "</em></td>" +
                                        "<td name='payablePrincipal[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].payablePrincipal + "/￥" + transactionInterests[i].realPayPrincipal + "</em></td>" +
                                        "<td name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                        "</tr>");
                                }
                                $("#j-investment-profit-table tbody").append(trObj);
                            }
                        } else if (flag == 1) {//逾期
                            $("#j-investment-profit-table tbody").append(titleOver);
                            for (var i = 0; i < transactionInterests.length; i++) {
                                var index = i + 1;
                                var trObj = $("<tr>" +
                                    "<td >" + index + "</td>" +
                                    "<td name='days[" + index + "]'>" + transactionInterests[i].days + "天</td>" +
                                    "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "/<em style='color: #333'>" + transactionInterests[i].payTimeStr + "</em></td>" +
                                    "<td name='payableInterest[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].formatPayableInterest + "</em></td>" +
                                    "<td name='payablePrincipal[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].formatRealPayPrincipal + "</em></td>" +
                                    "<td name='payablePrincipal[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].formatOverdueFine + "</em></td>" +
                                    "<td style='color: #333' name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                    "</tr>");


                                if ((category == 1 && statusTransfer == 2) || (category == 1 && statusTransfer == 3)) {

                                    var trObj = $("<tr>" +
                                        "<td >" + index + "</td>" +
                                        "<td name='days[" + index + "]'>" + transactionInterests[i].days + "天</td>" +
                                        "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "/<em style='color: #333'>" + transactionInterests[i].payTimeStr + "</em></td>" +
                                        "<td name='payableInterest[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].payableInterest + "/￥" + transactionInterests[i].realPayInterest + "</em></td>" +
                                        "<td name='payablePrincipal[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].payablePrincipal + "/￥" + transactionInterests[i].realPayPrincipal + "</em></td>" +
                                        "<td name='payablePrincipal[" + index + "]'><em style='color: #333'>￥" + transactionInterests[i].formatOverdueFine + "</em></td>" +
                                        "<td style='color: #333' name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                        "</tr>");

                                }
                                $("#j-investment-profit-table tbody").append(trObj);
                            }
                        } else if (flag == 2) {//提前
                            $("#j-investment-profit-table tbody").append(titlePre);
                            for (var i = 0; i < transactionInterests.length; i++) {
                                var index = i + 1;
                                var trObj = $("<tr>" +
                                    "<td >" + index + "</td>" +
                                    "<td name='days[" + index + "]'>" + transactionInterests[i].days + "/<em style='color: #333'>" + transactionInterests[i].realDays + "</em></td>" +
                                    "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "/<em style='color: #333'>" + transactionInterests[i].payTimeStr + "</em></td>" +
                                    "<td name='payableInterest[" + index + "]'>￥" + transactionInterests[i].formatPayableInterest + "/<em style='color: #333'>￥" + transactionInterests[i].formatRealPayInterest + "</em></td>" +
                                    "<td style='color: #333' name='payablePrincipal[" + index + "]'>￥" + transactionInterests[i].formatRealPayPrincipal + "</td>" +
                                    "<td style='color: #333' name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                    "</tr>");
                                if ((category == 1 && statusTransfer == 2) || (category == 1 && statusTransfer == 3)) {
                                    var trObj = $("<tr>" +
                                        "<td >" + index + "</td>" +
                                        "<td name='days[" + index + "]'>" + transactionInterests[i].days + "/<em style='color: #333'>" + transactionInterests[i].realDays + "</em></td>" +
                                        "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "/<em style='color: #333'>" + transactionInterests[i].payTimeStr + "</em></td>" +
                                        "<td name='payableInterest[" + index + "]'>￥" + transactionInterests[i].formatPayableInterest + "/<em style='color: #333'>￥" + transactionInterests[i].formatRealPayInterest + "</em></td>" +
                                        "<td style='color: #333' name='payablePrincipal[" + index + "]'>￥" + transactionInterests[i].formatPayablePrincipal + "/<em style='color: #333'>￥" + transactionInterests[i].realPayPrincipal + "</em></td>" +
                                        "<td style='color: #333' name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                        "</tr>");
                                }


                                $("#j-investment-profit-table tbody").append(trObj);
                            }

                            // 提前还款标记展示
                            $('#j-profit-prev').removeClass('z-hidden')
                        } else if (flag == 3) {//既逾期又提前
                            $("#j-investment-profit-table tbody").append(titlePreOver);
                            for (var i = 0; i < transactionInterests.length; i++) {
                                var index = i + 1;
                                var trObj = $("<tr>" +
                                    "<td >" + index + "</td>" +
                                    "<td name='days[" + index + "]'>" + transactionInterests[i].days + "/<em style='color: #333'>" + transactionInterests[i].realDays + "</em></td>" +
                                    "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "/<em style='color: #333'>" + transactionInterests[i].payTimeStr + "</em></td>" +
                                    "<td name='payableInterest[" + index + "]'>￥" + transactionInterests[i].formatPayableInterest + "/<em style='color: #333'>￥" + transactionInterests[i].formatRealPayInterest + "</em></td>" +
                                    "<td style='color: #333'  name='payablePrincipal[" + index + "]'>￥" + transactionInterests[i].formatRealPayPrincipal + "</td>" +
                                    "<td style='color: #333'  name='payablePrincipal[" + index + "]'>￥" + transactionInterests[i].formatOverdueFine + "</td>" +
                                    "<td style='color: #333'  name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                    "</tr>");

                                if ((category == 1 && statusTransfer == 2) || (category == 1 && statusTransfer == 3)) {
                                    var trObj = $("<tr>" +
                                        "<td >" + index + "</td>" +
                                        "<td name='days[" + index + "]'>" + transactionInterests[i].days + "/<em style='color: #333'>" + transactionInterests[i].realDays + "</em></td>" +
                                        "<td name='endDate[" + index + "]'>" + transactionInterests[i].endDateStr + "/<em style='color: #333'>" + transactionInterests[i].payTimeStr + "</em></td>" +
                                        "<td name='payableInterest[" + index + "]'>￥" + transactionInterests[i].formatPayableInterest + "/<em style='color: #333'>￥" + transactionInterests[i].formatRealPayInterest + "</em></td>" +
                                        "<td style='color: #333'  name='payablePrincipal[" + index + "]'>￥" + transactionInterests[i].formatPayablePrincipal + "/<em style='color: #333'>￥" + transactionInterests[i].realPayPrincipal + "</em></td>" +
                                        "<td style='color: #333'  name='payablePrincipal[" + index + "]'>￥" + transactionInterests[i].formatOverdueFine + "</td>" +
                                        "<td style='color: #333'  name='status[" + index + "]'>" + transactionInterests[i].interestStatusName + "</td>" +
                                        "</tr>");
                                }

                                $("#j-investment-profit-table tbody").append(trObj);
                            }

                            // 提前还款标记展示
                            $('#j-profit-prev').removeClass('z-hidden')
                        }
                    }
                    $profitTable.fadeIn().data("opened", true);
                }
            });
        } else {
            $profitTable.hide().data("opened", false)
            //箭头隐藏
            $(this).find('s').hide();
        }
    });

    /*设置交易详情值*/
    function transactionDetail(transaction) {
        var pId = "";
        $("#j-cashCouponNo").remove();
        $("#j-profitCouponNo").remove();
        $("#j-interestFrom").remove();

        //// TODO 起息方式显示位置
        //objectInterestFrom ="<tr id='j-interestFrom'><td>起息方式 </td><td colspan='3'>T(募集完成日)+<em name='interestFrom'></em></td></tr>";
        //	if(transaction.isDirectProject){
        //        if(transaction.status!==3){
        //            $("#j-investInfo tbody").append(objectInterestFrom);
        //        }
        $("#j-investment-order-detail").find("em[name='interestFrom']").html(transaction.interestFrom);
        //
        //}

        $.each(transaction, function (n, v) {
            var objectProfit = "<tr id='j-profitCouponNo'><td>使用收益券</td><td colspan='3'><em name='extraAnnualizedRate'></em>%(<em id='j-coupon-extraDay'>起息日起，加息<em name='extraInterestDay'></em>天，</em><em name='profitCouponNo'></em>)</td></tr>",
                objectCash = "<tr id='j-cashCouponNo'><td>使用现金券</td><td colspan='3'><em name='formatUsedCouponAmount'></em>元(<em name='cashCouponNo'></em>)</td></tr>";
            if (n == "extraAnnualizedRate") {
                if (typeof(v) != "undefined" && v != "" && v != null) {
                    $("#j-investInfo tbody").append(objectProfit);
                }
            }

            if (n == "cashCouponNo") {
                if (typeof(v) != "undefined" && v != "" && v != null) {
                    $("#j-investInfo tbody").append(objectCash);
                }
            }

            $("#j-investment-order-detail").find("td[name='" + n + "'],em[name='" + n + "'],span[name='" + n + "'],a[name='" + n + "']").html(v);
            $("#j-investment-order-detail").find("input[name='" + n + "']").val(v);
        });
        var timeName, moneyName, investProfitPeriod;
        if (transaction.isDirectProject) {
            timeName = "收益周期";
            moneyName = "投资金额";
            investProfitPeriod = transaction.profitPeriod;
        } else {
            timeName = "收益天数";
            moneyName = "购买金额";
            investProfitPeriod = transaction.totalDays + "天";
        }
        $("#j-investment-order-detail").find("td[name='time-name']").html(timeName);
        $("#j-investment-order-detail").find("td[name='money']").html(moneyName);
        $("#j-investment-order-detail").find("td[name='invest-profitPeriod']").html(investProfitPeriod);

        if (transaction.projectCategory == 2) {
            pId = transaction.transferId;
        } else {
            pId = transaction.projectId;
        }
        $("#j-investment-order-detail").find("a[name='projectName']").attr("href", environment.globalPath + "/products/detail-" + pId + ".html");

        if(!transaction.extraInterestDay || transaction.extraInterestDay==0){
            $("#j-investment-order-detail").find("#j-coupon-extraDay").remove();
        }
    }

    /*清空详情内容*/
    function emptyDetail(tableId) {
        $(tableId).find("td[name],em[name],span[name],a[name]").html("");
        $(tableId).find("input[name]").val("");
    }

    /*取消订单详情  cancel order*/
    Investment.$orderList.on("click", ".order-cancel-link", function () {
        var orderId = $(this).parent().parent().find("input").val();
        var opened = $('#j-cancel-order').data("opened"),
            orderLink = $(this).data("orderid"),
            orderDetail = $("#j-cancel-order").data("orderid");
        if (typeof opened == "undefined" || orderLink != orderDetail) {
            $('#j-cancel-order').data("opened", false);
            $("#j-order-detail").data("opened", false);
            $('#j-cancel-order').data("orderid", orderId);
            opened = false;
        }
        if (!opened) {
            $(".u-order-detail").hide();
            $.xPost({
                url: environment.globalPath + "/order/detail",
                data: {
                    'orderId': orderId
                },
                type: 'GET',
                callback: function (data) {
                    var orderDetail = data.result;
                    emptyDetail("#j-cancel-order");
                    /*设置取消订单详情*/
                    cancelOrderDetail(orderDetail);
                    $('#j-cancel-order').fadeIn();
                    $("#j-cancel-order").data("opened", true);
                }
            });
        } else {
            $('#j-cancel-order').hide();
            $("#j-cancel-order").data("opened", false);
        }
    });

    /*订单详情  order detail*/
    Investment.$orderList.on("click", ".order-detail-link", function () {
        var orderId = $(this).parent().parent().find("input").val();
        var opened = $('#j-order-detail').data("opened"),
            orderLink = $(this).data("orderid"),
            orderDetail = $("#j-order-detail").data("orderid");
        var $this = $(this);
        var payFinish = $this.data("status");
        var html = "";
        var category = $this.data('category');

        //隐藏其他箭头并显示当前表单箭头
        $('.j-box-pos').find('s').hide();
        $(this).find('s').fadeIn();

        if (typeof opened == "undefined" || orderLink != orderDetail) {
            $('#j-order-detail').data("opened", false);
            $("#j-cancel-order").data("opened", false);
            $('#j-order-detail').data("orderid", orderId);
            opened = false;
        }

        //直投与转投的信息表格
        if (category == 2) {
            html = '认购本金：¥<em class="f-fs24 f-ff-amount" name="transferPrincipal"></em>'
            $('#j-orderInfo,#j-orderUserInfo').addClass('f-dn');
            $('#j-zrOrderInfo,#j-zrOrderUserInfo').removeClass('f-dn');
            $('#j-order-detail').find('.f-ff-ver').html(html);
        } else {
            html = '投资金额：¥<em class="f-fs24 f-ff-amount" name="formatInvestAmount"></em>'
            $('#j-orderInfo,#j-orderUserInfo').removeClass('f-dn');
            $('#j-zrOrderInfo,#j-zrOrderUserInfo').addClass('f-dn');
            $('#j-order-detail').find('.f-ff-ver').html(html);
        }

        if (!opened) {
            $(".u-order-detail").hide();
            $.xPost({
                url: environment.globalPath + "/order/detail",
                data: {
                    'orderId': orderId
                },
                type: 'GET',
                callback: function (data) {
                    var orderDetail = data.result;
                    emptyDetail("#j-order-detail");
                    if (payFinish == 3) {
                        $("#j-order-debtProtocol-link").attr("href", "" + environment.globalPath + "/transaction/p2pContract/view?orderId=" + orderDetail.orderId);
                    } else {
                        $("#j-order-debtProtocol-link").attr("href", "/products/p2pContract/preview?projectId=" + orderDetail.projectId);
                    }
                    /*设置订单详情*/
                    setOrderDetail(orderDetail);
                    $('#j-order-detail').fadeIn();
                    $('#j-order-detail').data("opened", true);
                    var isZTproject = $this.data('projecttype');

                    if (isZTproject) {
                        $('.j-agreement-ztProject').removeClass('f-dn');
                        $('.j-agreement-cgProject').addClass('f-dn');
                    } else {
                        $('.j-agreement-ztProject').addClass('f-dn');
                        $('.j-agreement-cgProject').removeClass('f-dn');
                    }

                }
            });
        } else {
            $('#j-order-detail').hide();
            //隐藏箭头
            $(this).find('s').hide();
            $('#j-order-detail').data("opened", false);
        }
    });

    /*募集中订单详情  raise detail*/
    Investment.$raiseList.on("click", ".order-detail-link", function () {
        var transactionId = $(this).parent().parent().find("input").val();
        var opened = $('#j-raise-detail').data("opened"),
            orderLink = $(this).data("orderid"),
            orderDetail = $("#j-raise-detail").data("orderid");
        var ztStatus = $(this).data("projectstatus");
        var html = "";
        var category = $(this).data("category");

        if (typeof opened == "undefined" || orderLink != orderDetail) {
            $('#j-raise-detail').data("opened", false);
            $('#j-raise-detail').data("transactionid", transactionId);
            opened = false;
        }

        //直投与转投的信息表格
        if (category == 2) {
            html = '支付金额：¥<em class="f-fs24 f-ff-amount" name="formatInvestAmount"></em>'
            $('#j-projectInfo,#j-projectUserInfo').addClass('f-dn');
            $('#j-zrProjectInfo,#j-zrProjectUserInfo').removeClass('f-dn');
            $('#j-raise-detail').find('.f-ff-ver').html(html);
        } else {
            html = '投资金额：¥<em class="f-fs24 f-ff-amount" name="formatInvestAmount"></em>'
            $('#j-projectInfo,#j-projectUserInfo').removeClass('f-dn');
            $('#j-zrProjectInfo,#j-zrProjectUserInfo').addClass('f-dn');
            $('#j-raise-detail').find('.f-ff-ver').html(html);
        }

        if (!opened) {
            $(".u-order-detail").hide();
            $.xPost({
                url: environment.globalPath + "/transaction/collectProjectDetail",
                data: {
                    'transactionId': transactionId
                },
                type: 'GET',
                callback: function (data) {
                    var projectDetail = data.result,
                        $trDebtProtocol = $("#j-collecting-trDebtProtocol"),
                        $trProtocol = $("#j-collecting-trProtocol"),
                        $ztShaft = $("#j-collect-ztShaft"),
                        $zrShaft = $("#j-collect-zrShaft");
                    emptyDetail("#j-raise-detail");
                    /*设置募集中项目详情*/
                    setProjectDetail(projectDetail);
                    $('#j-raise-detail').fadeIn();
                    $('#j-raise-detail').data("opened", true);

                    //直投项目合同
                    $("#j-collecting-debtProtocol").attr("href", "" + environment.globalPath + "/transaction/p2pContract/view?orderId=" + projectDetail.orderId);
                    //直投转让合同
                    $trDebtProtocol.attr('href', '' + environment.globalPath + '/transaction/ztTransferContract/view?orderId=' + projectDetail.orderId);
                    //债权转让合同
                    $trProtocol.attr('href', '' + environment.globalPath + '/transaction/transferContract/view?orderId=' + projectDetail.orderId);
                    if (category == 2) {
                        $ztShaft.hide();
                        $zrShaft.show();
                        $('.u-tShaft-dl01').find('dt').addClass('z-current');
                        $('.u-info-tShaft').find('span').removeClass().addClass('z-current1');

                        if (projectDetail.isDirectProject) {
                            $trDebtProtocol.show()
                            $trProtocol.hide()
                        } else {
                            $trDebtProtocol.hide()
                            $trProtocol.show()
                        }
                    } else {
                        $zrShaft.hide();
                        $ztShaft.show();
                        if (ztStatus == 50) {
                            $('.u-tShaft-dl01').find('dt').addClass('z-current');
                            $('.u-tShaft-dl02').find('dt').addClass('z-current');
                            $('.u-info-tShaft').find('span').removeClass().addClass('z-current2');
                        } else if (ztStatus == 81) {
                            $('.u-tShaft-dl01').find('dt').addClass('z-current');
                            $('.u-tShaft-dl02').find('dt').addClass('z-current');
                            $('.u-tShaft-dl03').find('dt').addClass('z-current');
                            $('.u-info-tShaft').find('span').removeClass().addClass('z-current3');
                        } else {
                            $('.u-tShaft-dl01').find('dt').addClass('z-current');
                            $('.u-info-tShaft').find('span').removeClass().addClass('z-current1');
                        }
                    }
                }
            });
        } else {
            $('#j-raise-detail').hide();
            $('#j-raise-detail').data("opened", false);
        }
    });

    function setProjectDetail(projectDetail) {
        var pId = "";
        $("#j-cashCouponNo").remove();
        $("#j-profitCouponNo").remove();

        $.each(projectDetail, function (n, v) {
            var objectProfit = "<tr id='j-profitCouponNo'><td>使用收益券</td><td colspan='3'><em name='extraAnnualizedRate'></em>%(<em id='j-coupon-extraDay'>起息日起，加息<em name='extraInterestDay'></em>天，</em><em name='profitCouponNo'></em>)</td></tr>",
                objectCash = "<tr id='j-cashCouponNo'><td>使用现金券</td><td colspan='3'><em name='formatUsedCouponAmount'></em>元(<em name='cashCouponNo'></em>)</td></tr>";
            if (n == "extraAnnualizedRate") {
                if (typeof(v) != "undefined" && v != "" && v != null) {
                    $("#j-projectInfo tbody").append(objectProfit);
                }
            }
            ;
            if (n == "cashCouponNo") {
                if (typeof(v) != "undefined" && v != "" && v != null) {
                    $("#j-projectInfo tbody").append(objectCash);
                }
            }
            ;
            $("#j-raise-detail").find("td[name='" + n + "'],em[name='" + n + "'],span[name='" + n + "'],a[name='" + n + "']").html(v);
        });
        //详情页项目链接 $('#j-order-argreement-link').attr("href","" +environment.globalPath+"/transaction/contract/view?orderId="+order.orderId);
        if (projectDetail.projectCategory == 2) {
            pId = projectDetail.transferId;
        } else {
            pId = projectDetail.projectId;
        }
        $("#j-raise-detail").find("a[name='projectName']").attr("href", environment.globalPath + "/products/detail-" + pId + ".html");
        var timeName, interestFrom, investProfitPeriod, endDateStr, projectValue;
        if (projectDetail.isDirectProject && projectDetail.profitPeriod != null) {
            timeName = "收益周期";
            interestFrom = "起息方式";
            investProfitPeriod = projectDetail.profitPeriod;
            projectValue = projectDetail.projectValue;
            endDateStr = "T(募集完成日)+" + projectDetail.interestFrom;

        } else {
            timeName = "收益天数";
            interestFrom = "项目到期日";
            investProfitPeriod = projectDetail.profitDays + "天";
            projectValue = projectDetail.projectValue;
            endDateStr = projectDetail.endDateStr;
        }
        $("#j-raise-detail").find("td[name='transactionTime']").html(projectDetail.transactionTime);

        $("#j-raise-detail").find("td[name='endDateStr']").html(endDateStr);
        $("#j-raise-detail").find("td[name='interestFrom']").html(interestFrom);
        $("#j-raise-detail").find("td[name='invest-profitPeriod']").html(investProfitPeriod);
        $("#j-raise-detail").find("td[name='timeName']").html(timeName);
        $("#j-raise-detail").find("td[name='projectValue']").html(projectValue);

        if(!projectDetail.extraInterestDay || projectDetail.extraInterestDay==0){
            $("#j-raise-detail").find("#j-coupon-extraDay").remove();
        }
    }

    //订单窗口定位
    $('.m-recharge-list').on("click", ".j-box-pos", function () {
        var position = $(this).position(),
            action = $(this).data("action"),
            left = position.left - 680,
            top = position.top,
            offsetTop = 35,
            offsetLeft = 0,
            $this = $(this);
        if ($(".j-recharge-tab").length > 0) {
            offsetTop = 0;
            offsetLeft = 30;
        }
        var $boxdetail = $(".j-box-detail");
        var detailHeight = $boxdetail.eq(0).outerHeight();
        //     status=$('#j_investmentTransfer').attr('status');
        // console.log($('#j_investmentTransfer').attr('status'))
        // console.log(status)
        switch (action) {
            case "detail":
                var projectType = $this.data('projecttype');
                //判断是否为直投项目
                if (projectType) {
                    top = top + 42 - 14 - offsetTop - 480 / 2 - 90;
                } else {
                    top = top + 42 - 14 - offsetTop - 480 / 2 - 18;
                }
                left = left - offsetLeft - 29;
                $boxdetail.css({
                    "left": left,
                    "top": top
                });
                break;
            case "cancel":
                var $boxcancel = $(".j-box-cancel");
                var cancelHeight = $boxcancel.eq(0).outerHeight();
                top = top + 105 - 16 - offsetTop - cancelHeight / 2;
                $boxcancel.css({
                    "left": left,
                    "top": top
                });
                break;
            case "profit":
                $boxprofit = $(".j-box-profit");
                var profitlHeight = $boxprofit.eq(0).outerHeight();
                top = top + 28 - offsetTop - profitlHeight / 2;
                left = left - offsetLeft * 2 - 78;
                $boxprofit.css({
                    "left": left,
                    "top": top
                });
                break;
            case "oprdetail":
                top = top + 60 - 14 - offsetTop - detailHeight / 2;
                left = left - offsetLeft + 1;
                $boxdetail.css({
                    "left": left,
                    "top": top
                });
                break;
            case "cpdetail":
                top = top + 60 - 14 - offsetTop - detailHeight / 2 + 20;
                left = left - offsetLeft;
                $boxdetail.css({
                    "left": left,
                    "top": top
                });
                break;
            // case "transfer":
            //     if (status==1){
            //         top = top + 60 + 57 - 14 - offsetTop - detailHeight / 2 + 17;
            //         left = left - offsetLeft - 28;
            //         $boxdetail.css({
            //             "left": left,
            //             "top": top
            //         });
            //     }else if(status==3){
            //         top = top + 60 + 96 - 14 - offsetTop - detailHeight / 2 + 17;
            //         left = left - offsetLeft - 28;
            //         $boxdetail.css({
            //             "left": left,
            //             "top": top
            //         });
            //     }else{
            //         top = top + 60 + 57 - 14 - offsetTop - detailHeight / 2 + 17;
            //         left = left - offsetLeft - 28;
            //         $boxdetail.css({
            //             "left": left,
            //             "top": top
            //         });
            //     }
            //     break;
            default:
                break;
        }
        return false;
    });

    /*取消订单*/
    $("#j-cancel-order-button").on('click', function () {
        var orderId = $("#j-orderId").val();
        $.xPost({
            url: environment.globalPath + "/order/member/cancel/order",
            data: {
                'orderId': orderId
            },
            type: 'GET',
            callback: function (data) {
                if (data.success) {
                    $('#j-cancel-order').fadeOut();
                    $('#j-cancel-order').data("opened", false);
                    $.xDialog({content: "取消成功！", type: 'success'});
                    fnOnGoToPageOrder();
                } else {
                    $.xDialog({content: "取消失败！"});
                }
            }
        });
    });

    /*暂不取消*/
    $("#j-not-cancel-button").on('click', function () {
        $('#j-cancel-order').fadeOut();
        $('#j-cancel-order').data("opened", false);
    });

    /*设置取消订单详情*/
    function cancelOrderDetail(order) {
        $.each(order, function (n, v) {
            $("#j-cancel-order").find("td[name='" + n + "'],em[name='" + n + "'],span[name='" + n + "'],a[name='" + n + "']").html(v);
            $("#j-cancel-order").find("input[name='" + n + "']").val(v);
        });
        $("#j-cancel-order").find("a[name='projectName']").attr("href", environment.globalPath + "/products/detail-" + order.projectId + ".html");

        var timeName, interestFrom, investProfitPeriod, endDateStr;
        if (order.isDirectProject && order.profitPeriod != null) {
            timeName = "收益周期";
            investProfitPeriod = order.profitPeriod;
        } else {
            timeName = "收益天数";
            investProfitPeriod = order.profitDays + "天";
        }
        $("#j-cancel-order").find("td[name='invest-profitPeriod']").html(investProfitPeriod);
        $("#j-cancel-order").find("td[name='time-name']").html(timeName);

    }

    /*设置订单详情*/
    function setOrderDetail(order) {
        var pId = "";
        var $jOrderDetail = $("#j-order-detail");
        $.each(order, function (n, v) {
            $jOrderDetail.find("td[name='" + n + "'],em[name='" + n + "'],span[name='" + n + "'],a[name='" + n + "']").html(v);
        });
        $('#j-order-argreement-link').attr("href", "" + environment.globalPath + "/transaction/contract/view?orderId=" + order.orderId);

        if (order.projectCategory == 2) {
            pId = order.transferId;
        } else {
            pId = order.projectId;
        }
        $jOrderDetail.find("a[name='projectName']").attr("href", environment.globalPath + "/products/detail-" + pId + ".html");
        var timeName, interestFrom, investProfitPeriod, endDateStr, projectValue, remarks;
        if (order.isDirectProject && order.profitPeriod != null) {
            timeName = "收益周期";
            interestFrom = "起息方式";
            moneyName = "投资金额";
            investProfitPeriod = order.profitPeriod;
            endDateStr = "T(募集完成日)+" + order.interestFrom;
            projectValue = order.projectValue;
            remarks = order.remarks;

        } else {
            timeName = "收益天数";
            interestFrom = "项目到期日";
            moneyName = "购买金额";
            investProfitPeriod = order.profitDays + "天";
            endDateStr = order.endDateStr;
            projectValue = order.projectValue;
            remarks = order.remarks;
        }
        $jOrderDetail.find("td[name='money']").html(moneyName);
        $jOrderDetail.find("td[name='endDateStr']").html(endDateStr);
        $jOrderDetail.find("td[name='interestFrom']").html(interestFrom);
        $jOrderDetail.find("td[name='invest-profitPeriod']").html(investProfitPeriod);
        $jOrderDetail.find("td[name='time-name']").html(timeName);
        $jOrderDetail.find("td[name='profitDays']").html(order.profitDays);
        $jOrderDetail.find("td[name='projectValue']").html(projectValue);
        $jOrderDetail.find("td[name='remarks']").html(remarks);
        //转让协议查看
//	$("#j-order-argreement-link").xArgreement();
    }

    $("#j-debt-protocol").on("click", function () {
        var transactionId = $("#j-investment-order-detail input[name='transactionId']").val();
        $.xPost({
            url: environment.globalPath + "/transaction/contract/view",
            data: {
                'transactionId': transactionId
            },
            type: 'GET',
            callback: function (data) {
            }
        });
    });

//关闭按钮
    $(".j-dialog-close").on("click", function () {
        var flag = false;
        var dialogWrap = $(this).parent().parent().parent();
        dialogWrap.hide();
        $('.j-box-pos').find('s').hide();
        //设置页面状态为关闭
        dialogWrap.data("opened", false);
        //进度条状态重置
        flag = $(this).parent().find('.u-raise-tShaft');
        if (flag) {
            $('.u-info-tShaft').find('dt').removeClass('z-current');
            $('.u-info-tShaft').find('span').removeClass();
        }
    });

//根据投资状态查询数据
    $(".j-status-select").on("click", function () {
        $("strong.z-current").removeClass("z-current");
        $(this).addClass("z-current");
        //箭头定位
        arrowLocator(".m-recharge-list");
        $(".m-recharge-list").find("z-current");
        var val = $(this).attr("value");
        $("#investment_form input[name='status']").val(val);
        loadInvestments();
    });

    var uc = new XRW.UC;
    //头部Banner
    uc.slideBanner('#j-uc-banner', 3000);
})()

//投资记录-上上签部分
;
(function () {

    var i,
        //t=1 正在募集  t=2 投资列表
        t = $("#j-investment-val").data("page")
    //正在募集中-一键签署
    Investment.$raiseList.on('click', '#j-eSignatue-allbtn', function () {
        $('#j-eSignatue-all').removeClass('f-dn')
    })
    //上上签签署功能-一键签署
    Investment.$investList.on('click', '#j-eSignatue-allbtn', function () {
        $('#j-eSignatue-all').removeClass('f-dn')
    })
    $("#j-eSignatue-all-ok").on('click', function () {
        $.xPost({
            url: environment.globalPath + "/transaction/signAllContract",
            data: {type: t},
            callback: function (data) {
                if (data.success) {
                    i = data.result
                    $("#j-eSignatue-all").addClass('f-dn')
                    $("#j-eSignatue-all").addClass('f-dn')
                    $.xDialog({
                        type: "success",
                        content: '恭喜您，成功签署' + i + '份协议。',
                        height: 140,
                        width: 360,
                        callback: function () {
                            window.location.reload()
                        },
                        //成功后点击确定刷新页面
                        okValue: '确定'
                    });
                } else {
                    $("#j-eSignatue-all").addClass('f-dn')
                    $.xDialog({
                        type: "info",
                        content: data.resultCodeEum[0].msg,
                        height: 140,
                        width: 360,
                        //成功后点击确定刷新页面
                        okValue: '确定'
                    });
                }
            }
        })
    })

    $("#j-eSignatue-all").on('click', 'i', function () {
        $("#j-eSignatue-all").addClass('f-dn')
    })

//上上签签署功能-单独签署-投资列表
    Investment.$investList.on('click', '.j-eSignatue-result', function () {
        //是否设置过手动签署  0手动  1自动
        var transactionId = $(".j-eSignatue-result").data("transactionid"),
            orderUrl = $(this).data("contracturl");
        //未设置自动签署
        $("#j-inv-coantract").attr("href", orderUrl)
        $("#j-eSignatue-state").removeClass("f-dn").next().removeClass("f-dn")


    })


    //上上签签署功能-单独签署-正在募集
    Investment.$raiseList.on('click', '.j-eSignatue-result', function () {
        //是否设置过手动签署  0手动  1自动
        var transactionId = $(".j-eSignatue-result").data("transactionid"),
            orderUrl = $(this).data("contracturl");
        $("#j-coll-coantract").attr("href", orderUrl)

        $("#j-eSignatue-state").removeClass("f-dn").next().removeClass("f-dn")

    })

    $("#j-eSignatue-state").on('click', 'i', function () {
        $("#j-eSignatue-state").addClass('f-dn').next().addClass("f-dn")
        location.reload()
    })
    $("#j-eSignatue-result").on('click', 'i', function () {
        $("#j-eSignatue-result").addClass('f-dn')
        location.reload()
    })
    //签署成功确定按钮
    $("#j-eSignatue-result").on("click", "a", function () {
        $("#j-eSignatue-result").addClass('f-dn')
        location.reload()
    })


    Investment.$investList.on('mouseenter ', '.j-eSignatue-finish', function () {
        $(this).text("点击查看")
    }).on('mouseleave  ', '.j-eSignatue-finish', function () {
        $(this).text("已签署")
    })
    Investment.$investList.on('mouseenter ', '.j-eSignatue-past', function () {
        $(this).text("点击查看")
    }).on('mouseleave  ', '.j-eSignatue-past', function () {
        $(this).text("已过期")
    })

//签署失败系统异常弹框
    Investment.$investList.on('click', '.j-eSignatue-fail', function () {
        $("#j-eSignatue-resultfail").removeClass('f-dn')
        //$.xDialog({
        //    type: "error",
        //    content:'合同正在生成中，请您耐心等待，稍后签署',
        //    height:140,
        //    width:360
        //});
    })
    $("#j-eSignatue-resultfail").on('click', 'i', function () {
        location.reload()
    })
    $("#j-eSignatue-resultfail").on('click', 'a', function () {
        location.reload()
    })

    //募集中项目部分
    Investment.$raiseList.on('mouseenter ', '.j-eSignatue-finish', function () {
        $(this).text("点击查看")
    }).on('mouseleave  ', '.j-eSignatue-finish', function () {
        $(this).text("已签署")
    })
    Investment.$raiseList.on('mouseenter ', '.j-eSignatue-past', function () {
        $(this).text("点击查看")
    }).on('mouseleave  ', '.j-eSignatue-past', function () {
        $(this).text("已过期")
    })
    Investment.$raiseList.on('click', '.j-eSignatue-result', function () {
        //是否设置过手动签署  0手动  1自动
        var signWay = $(this).data("signway")
        if (signWay === 0) {
            $("#j-eSignatue-state").removeClass("f-dn").next().removeClass("f-dn")
        } else if (signWay === 1) {
            $("#j-eSignatue-result").removeClass('f-dn')

        }

    })

    //签署失败系统异常弹框
    Investment.$raiseList.on('click', '.j-eSignatue-fail', function () {
        $("#j-eSignatue-resultfail").removeClass('f-dn')
    })
})()


//加载我的投资
function loadInvestments() {
    $(".u-profit-table,.u-order-detail").hide();
    var data = $("#investment_form").serialize();
    Investment.$investList.loading().load(environment.globalPath + "/transaction/investmentListData",
        data, function () {
            $("input[name='currentPage']").val(1);//设置当前页为1
            //筛选状态
            Investment.initStatusSelector('.j-status-selector', loadInvestments)
            //筛选签署
            Investment.initStatusSelector('.j-signStatus-selector', loadInvestments)

            // 获取发红包规则
            $.xPost({
                url: environment.globalPath + "/activity/redBag/getRule",
                callback: function (data) {
                    if (data.success && data.result) {
                        var startTime = data.result.startTime || 0,
                            endTime = data.result.endTime || 0,
                            nowTime = env.serverDate;
                        if ((nowTime > startTime || nowTime == startTime) && nowTime < endTime) {
                            initRedbag(data.result);
                        }
                    }
                }
            });

            function initRedbag(result) {
                var minInvestAmount = result.minInvestAmount,
                    exceptActivitySign = result.exceptActivitySign[0],
                    startTime = result.startTime,
                    endTime = result.endTime,
                    // 先筛选是否发红包具有资格的项目
                    selectedTr = $('#j-invest-table').find('tr[data-status="1"]'),
                    tempHtml = '<span class="u-redbag-icon"><span class="u-redbag-img"></span><div class="u-popup"><h3>微信扫一扫发红包</h3><div class="u-popup-qrcode"></div></div></span>';

                selectedTr.each(function () {
                    var _this = $(this),
                        thisInvestAmount = _this.find('.u-withAmount').attr('data-investAmount') - 0, // 项目金额
                        transactionId = _this.attr('data-transactionId'), // 项目编号
                        activitySign = _this.attr('data-activitySign') - 0, // 1的话就被排除

                        // 交易时间要在活动开始之后，结束之前，这里js转化之后会比后台返回的时间多14个小时
                        hours = new Date(_this.attr('data-transactionTime')).getHours() - 14,
                        newTransactionTime = new Date(_this.attr('data-transactionTime')).setHours(hours),
                        transactionTime = new Date(newTransactionTime).getTime();

                    if ((thisInvestAmount > minInvestAmount || thisInvestAmount === minInvestAmount)
                        && activitySign != exceptActivitySign
                        && (transactionTime > startTime && transactionTime < endTime)) {
                        _this.find('.j-receiveQrcode').append(tempHtml);
                        _this.find('.u-redbag-icon').attr('data-transactionId', transactionId);
                    }
                });

                $('.u-redbag-icon').on('mouseenter', function () {
                    $(this).attr('data-enter', 1);
                }).on('mouseout', function () {
                    $(this).attr('data-enter', 0);
                }).on('click', function () {
                    $('#qrcode').html('');
                    $('.u-popup').hide();
                    var _this = $(this);
                    _this.attr('data-qrshow', 1);

                    var qrcodeConfig = {
                        width: 150,
                        height: 150,
                        colorDark: "#000000",
                        colorLight: "#ffffff",
                        correctLevel: QRCode.CorrectLevel.L
                    };

                    if (_this.attr('data-redBagCode') && _this.attr('data-redBagCode') !== '') {
                        $('#qrcode').html('');
                        var temp = _this.attr('data-redBagCode');
                        _this.find('.u-popup-qrcode').append($('#qrcode')); // 这里要清除一下原有的二维码
                        qrcodeConfig.text = temp;
                        new QRCode(document.getElementById('qrcode'), qrcodeConfig);
                        _this.parent().find('.u-popup').show();
                    } else {
                        var transactionId = _this.attr('data-transactionId');
                        $.xPost({
                            url: environment.globalPath + '/activity/redBag/shareUrl',
                            data: {transactionId: transactionId},
                            callback: function (data) {
                                if (data.success) {
                                    //console.log('第一次取到二维码url',data);
                                    var temp2 = data.result.redBagCode;
                                    // cc test
                                    //var temp2 = temp2.replace('dev.yourong.cn:8081', 'm.test.yourong.cn:8081');
                                    //var temp2 = temp2.replace('dev.yourong.cn:8081', 'cchotaru.6655.la:14621');
                                    // cc test end
                                    _this.attr('data-redBagCode', temp2);
                                    _this.find('.u-popup-qrcode').append($('#qrcode'));
                                    qrcodeConfig.text = temp2;
                                    new QRCode(document.getElementById('qrcode'), qrcodeConfig);
                                    _this.find('.u-popup').show();
                                }
                                //else {
                                //    console.log('没有取到二维码url',data);
                                //}
                            }
                        })
                    }

                    $('body').on('click', function (event) {
                        if ($('.u-redbag-icon[data-qrshow="1"]').attr('data-enter') == 0) {
                            $('.u-redbag-icon[data-qrshow="1"]').attr('data-qrshow', 0);
                            $('.u-popup').hide();
                            $('#qrcode').html('');
                        }
                        event.stopPropagation();
                    })
                })
            }

            // 如意图标点击跳转到春节活动页
            var isRuyi = false,
                $ruyiBtn = $('.j-icon-ruyi');

            $ruyiBtn.on('click', function () {
                isRuyi = true;
                window.location.href = '/activity/springFestival/index#act4';
                return false;
            });

            $ruyiBtn.parent().on('click', function () {
                if (isRuyi) {
                    return false;
                }
            });

        });

}

//加载我的订单 order
function loadOrders() {
    $(".u-order-detail").hide();
    var data = $("#j-order-form").serialize();
    $("#j-order-list").loading().load(environment.globalPath + "/order/orderList", data,
        function () {
            $("input[name='currentPage']").val(1);
            Investment.initStatusSelector('.j-orderType-selector', loadOrders);
        }
    );
}

//加载募集中项目 raise
function loadRaise() {
    $(".u-order-detail").hide();
    var data = $("#j-raise-form").serialize()
    Investment.$raiseList.loading().load(environment.globalPath + "/transaction/collectProjectList", data, function () {
        $("input[name='currentPage']").val(1);//设置当前页为1
        Investment.initStatusSelector('.j-collectSignStatus-selector', loadRaise)

    })
}

//加载转让记录列表
function loadTransfer(pageNo) {
    if (Investment.$transferList.length) {
        Investment.$transferList.loading().load('/transaction/transferProjectList?currentPage=' + pageNo, function () {
            $("input[name='currentPage']").val(pageNo);
        })
    }
}

//加载转让项目列表
function loadTransferProject() {
    var data = $('#j-transferStatus').serialize();
    if (Investment.$transferProjectList.length) {
        Investment.$transferProjectList.loading().load(environment.globalPath + "/transaction/transferprojectDataList", data,
            function () {
                $("input[name='currentPage']").val(1);//设置当前页为1
                //筛选状态
                Investment.initStatusSelector('.j-transferStatus-selector', loadTransferProject)
            })
    }
}

/*分页获取转让记录列表*/
function fnOnGoToPageTransfer(pageNo) {
    loadTransfer(pageNo);
}

/*分页获取转让项目列表*/
function fnOnGoToPageTransferProject(pageNo) {
    loadTransferProject(pageNo);
}
/*分页获取我的交易的我的投资列表数据*/
function fnOnGoToPage() {
    var searchDate = $("#jStartTime").val();
    if (searchDate) {
        $(".j-invest-search").trigger('click');
    } else {
        loadInvestments();
    }
}

//order
/*分页获取我的交易的我的订单列表数据*/
function fnOnGoToPageOrder() {
    loadOrders();
}
//分页获取募集中项目列表
function fnOnGoToPageRaise() {
    loadRaise();
}
//红包二维码&&用户提示
(function () {
    var $shareredpacket = $('#j-share-redpacket');
    if ($shareredpacket.length) {
        //生成红包二维码
        var cacheUrl = null;
        var qrcode = new QRCode(document.getElementById("qrcode"), {
            width: 150,
            height: 150,
            colorDark: "#000000",
            colorLight: "#ffffff",
            correctLevel: QRCode.CorrectLevel.L
        });
        $('#j-investment-table').on('click', '.interests-link', function () {

            $shareredpacket.hide();
            $('#qrcode').hide();

            $.xPost({
                url: env.globalPath + '/activity/anniversary/shareUrl',
                data: {transactionId: $.transactionId},
                callback: function (data) {
                    if (data.success) {
                        qrcode.makeCode(env.mGlobalDomain + '/activity/anniversary/loadRed?code=' + data.result.encryptUrl);
                        cacheUrl = data.result.encryptUrl;
                        $shareredpacket.show();
                    }
                }
            });
            $shareredpacket.on("click", function () {
                $('#qrcode').show();
            });
        });

    }
    // 待确认时用户提示
    $('body').on("mouseenter", ".j-userTips", function () {
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
    }).on("mouseleave", ".j-userTips", function () {
        var d = $(this).data("hoverObj");
        d.close().remove();
    });
})()

//项目转让
if (typeof Vue !== 'undefined') {
    Vue.config.devtools = true;
    //数字转换成千分号方法
    Vue.filter('amountSplit', function (num) {
        var parts = _.toFixed(num, 2).toString().split("."),
            dec = parts[1],
            int2 = parts[0];

        parts[0] = int2.replace(/\B(?=(\d{3})+(?!\d))/g, ",")

        if (typeof(dec) === 'undefined') {
            parts[1] = '00'
        } else if (dec.length === 1) {
            parts[1] = dec + '0'
        }

        return parts.join(".")
    });
    Vue.filter('dateFormat', function (date, format) {

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
    Vue.filter('formatName', function (value) {
        return value.split('期')[0]
    });
    window.Transfer = new Vue({
        el: '#j-transfer',
        data: {
            info: {
                projectValue: '',
                residualPrincipal: '',
                currentInterest: ''
            },
            tips: {
                incorrect: '请输入正确的金额!',
                cancelTransferError: '',
                cannotTransfer: ''
            },
            transferAmount: '',
            inputTips: '',
            inputCorrect: false,
            confirmOpened: false,
            cancelTipsOpened: false,
            confirmCancelOpened: false,
            transferSuccess: false,
            cannotTransfer: false,
            date: '',
            cache: {},
            detail: [],  //转让详情框里的历史转让数据
            pageDetail: {},  //转让详情框里的页数
            transactionId: '',  //债权转让页面取的转让交易号
            currentPage: 1,
            totalPages: 0,
            status: '',          //债权转让页面取的状态
            currentDetail: {},    //转让详情框里的转让中的数据
            Rateregulary: {},
            cancelSuccess: 0,       //终止转让文案显示判断
            transferLinkTop: 0,     //债权转让转让记录文案的top和left
            transferLinkLeft: 0
        },
        computed: {
            holdDays: function () {
                return this.info.holdDays ? this.info.holdDays : 0
            },
            transferDiscount: function () {
                return this.detail.discount ? this.detail.discount : 0
            }
        },
        created: function () {
            var self = this

            Investment.$transferProjectList.on("click", ".transfer-link", function () {
                //清空输入
                self.clearData()
                $('.j-transfer-cover').show()
                var $this = $(this),
                    transactionId = $this.data('tid')

                $.xPost({
                    url: '/transaction/ableToTransfer',
                    data: {transactionId: transactionId},
                    callback: function (data) {
                        if (data.success) {
                            //显示转让窗口
                            self.cannotTransfer = false
                            self.startTransfer(transactionId)
                        } else {
                            //显示不能转让的提示
                            self.cannotTransfer = true
                            self.tips.cannotTransfer = data.resultCodeEum[0].msg
                            self.tips.code = data.resultCodeEum[0].code
                            self.data = data.result
                        }
                    }
                })
            });

            Investment.$transferProjectList.on("click", ".transfer-ending", function () {
                self.confirmCancelOpened = true
                self.cache.tid = $(this).data('tid')
            });

            Investment.$transferProjectList.on("click", ".transfer-detail", function () {
                var $this = $(this),
                    position = $this.offset(),
                    left = position.left,
                    top = position.top
                self.transferLinkTop = top
                self.transferLinkLeft = left
                self.status = $this.data('status')
                var currentPage = 1,
                    transactionId = $this.data('tid')
                self.transferDetail(transactionId, currentPage, true)

            });
        },
        methods: {
            /**
             * 四舍五入保留两位小数
             * @param num {number}
             * @param size {number}
             * @returns {number}
             */
            fixed: function (num, size) {
                return _.toFixed(num, size || 2)
            },
            /**
             * 检查转让价格输入
             */
            checkInput: function () {
                var amount = this.transferAmount
                if (!amount || !Number(amount) || Number(amount) < 0) {
                    this.inputCorrect = false
                    this.inputTips = this.tips.incorrect
                }
                else if (amount > this.info.projectValue) {
                    this.inputCorrect = false
                } else {
                    this.inputCorrect = true
                    this.inputTips = ''
                }
            },
            /**
             * 确定转让(提交转让)
             */
            submitTransfer: function () {
                $('.j-transfer-cover').show()
                if (this.inputCorrect && this.agree) {
                    this.confirmOpened = true
                } else {
                    this.checkInput()
                }
            },
            /**
             * 弹窗关闭并刷新页面
             */
            closeAndReload: function () {
                this.transferSuccess = false
                location.reload()
            },

            /**
             * 确认转让(是否转让)
             * @param confirm
             */
            confirmTransfer: function (confirm) {
                if (!confirm) {
                    this.confirmOpened = false
                    return false
                }

                var info = this.info,
                    self = this,
                    transferAmount = this.transferAmount,
                    transactionId = info.transactionId,
                    discount = this.fixed(info.residualPrincipal - this.transferAmount)

                $.xPost({
                    url: '/transaction/transferToProject',
                    data: {
                        transactionId: transactionId,
                        discount: discount,
                        transferAmount: transferAmount
                    },
                    callback: function (data) {
                        self.confirmOpened = false

                        if (data.success) {
                            self.transferSuccess = true
                        } else {
                            $.xDialog({content: data.resultCodeEum[0].msg})
                        }
                    }
                })
            },
            /**
             * 结束转让确认
             * @param confirm
             */
            confirmCancelTransfer: function (confirm) {
                if (confirm) {
                    var self = this
                    self.cancelTipsOpened = true
                    $.xPost({
                        url: '/transaction/cancelTransferProject',
                        data: {transactionId: self.cache.tid},
                        callback: function (data) {
                            delete self.cache.tid
                            if (data.success) {
                                self.cancelSuccess = 2
                            } else {
                                self.tips.cancelTransferError = data.resultCodeEum[0].msg
                                self.cancelSuccess = 1
                            }
                        }
                    })
                } else {
                    this.confirmCancelOpened = false
                }
                this.confirmCancelOpened = false
            },
            /**
             * 清理转让弹层数据
             */
            clearData: function () {
                this.transferAmount = ''
                this.inputCorrect = false
                $('.j-box-transfer').hide()
                $('.j-transfer-cover').hide()
            },
            startTransfer: function (transactionId) {
                var self = this, arrLength
                $.xPost({
                    url: environment.globalPath + "/transaction/transferInformation",
                    data: {
                        'transactionId': transactionId
                    },
                    type: "GET",
                    callback: function (data) {
                        var transfer = data.result
                        $('.j-box-transfer').show()
                        self.info = transfer
                        self.cannotTransfer = false
                        self.inputTips = ""
                        self.Rateregulary = transfer.transferRateList
                    }
                });
            },
            /**
             * 转让记录请求数据
             * @param confirm
             */
            transferDetail: function (transactionId, currentPage, index) {
                var self = this
                $.xPost({
                    url: environment.globalPath + "/transaction/transfedetaillist",
                    data: {
                        'transactionId': transactionId,
                        'currentPage': currentPage
                    },
                    type: "GET",
                    callback: function (data) {
                        if (data.page.data) {
                            var transferDetail = data.page.data[0],
                                transferDetailPage = data.page
                            if (transferDetail && transferDetail.failFlag === 2) {
                                transferDetail.transferEndDate = transferDetail.failTime
                            }
                        }

                        self.pageDetail = transferDetailPage
                        self.transactionId = transactionId
                        self.currentDetail = data.result
                        self.currentPage = self.pageDetail.pageNo
                        self.totalPages = self.pageDetail.totalPageCount
                        self.detail = transferDetail

                        if (index) {
                            self.frameLocation()
                        }

                    }
                });

            },
            /**
             * 转让记录框定位与显示
             * @param confirm
             */
            frameLocation: function () {

                var self = this,
                    top = self.transferLinkTop,
                    left = self.transferLinkLeft - 708

                if (self.status == "转让中") {

                    if (self.detail) {
                        if (self.detail.length < 1) {
                            top = top - 150
                        } else {
                            top = top - 270
                        }
                    } else {
                        top = top - 150
                    }

                } else {
                    top = top - 215
                }

                $boxdetail = $(".j-box-transferDetail");
                $boxdetail.css({
                    "left": left,
                    "top": top
                });
                $('.j-box-transferDetail').fadeIn()


            },
            /**
             * 转让记录翻页请求
             * @param confirm
             */
            pageTurning: function (index) {
                var self = this,
                    transactionId = self.transactionId,
                    currentPage = self.currentPage

                if (index === 1) {
                    if (currentPage < self.totalPages) {
                        currentPage++;
                        self.transferDetail(transactionId, currentPage, false)
                    }
                } else {
                    if (currentPage > 1) {
                        currentPage--;
                        self.transferDetail(transactionId, currentPage, false)
                    }
                }
            }
        }
    })
}