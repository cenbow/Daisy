/*global define,$,alert,console,environment,env,log*/
define(['template', 'xjs/modules/effects'], function (require, exports, module) {
    'use strict';
    //API request handler
    var base = {};
    var effects = require('xjs/modules/effects');
    /**
     *
     * exAnnualizedRate--选择收益券所得的额外收益
     * limitAmount--所选收益券的限制起投金额
     * extraInterestDay--所选收益券的收益天数
     * extraInterestType--所选收益券的类型
     * couponSize--加载出的收益券的长度
     * @type {number}
     */
    var exAnnualizedRate = 0,
        inputAmount = $("#investAmount").val(),
        extraInterestType = 0,
        extraInterestDay = 0,
        limitAmount = 0;
    base.getAPI = function (options) {
        if (typeof(options) === 'object') {
            var xToken = $('#xToken').val(),
                data = {};
            if (options.data) {
                if ($.isArray(options.data)) {
                    $.each(options.data, function (index, item) {
                        data[item.name] = item.value;
                    });
                } else {
                    data = options.data;
                }
            }
            data.xToken = xToken;

            $.ajax({
                url: options.url,
                data: data,
                type: options.type || 'POST',
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': options.version || '1.0.0'
                },
                dataType: options.dataType || 'json',
                success: function (data) {
                    if (typeof(options.ids) === 'object') {
                        options.callback(data, options.ids);
                    } else {
                        options.callback(data);
                    }
                }
            });
        } else {
            throw new Error('options must be an object');
        }
    };

    $('#j-coupon-popup').on('scroll', function (e) {
        e.stopPropagation()
    })

    //立即投资
    var $investAmount = $('#j-investment'),
        $detailProcess = $('#j-detail-process'),
        $processHeight = $detailProcess.find('i'),
        windowHeight = window.screen.availHeight;

    $processHeight.height(windowHeight - 205);

    if (windowHeight > 640 && windowHeight < 670) {
        $processHeight.height(windowHeight - 235);
    }
    else if (windowHeight > 470 && windowHeight < 500) {
        $processHeight.height(windowHeight - 105);
    }

    var $getableEarnings = $("#j-profit-amount")
    //标题值传递
    var $investTitle = $('#j-invest-title');
    if ($investTitle.length) {
        var pid = $investTitle.data('pid'),
        //pageTitle = localStorage.getItem('pid_' + pid),
        //pageTitle =$investTitle.attr('projectName'),
            memberId = $investTitle.attr('memberId'),
            $investSubmit = $('#j-submit-select'),
            comeUrl = document.referrer;

        var productConfig = {};

        $investAmount.trigger('focus');
        setTimeout(function () {
            $investAmount.trigger('blur');

        }, 500);
        //init
        var template = require('template');

        base.getAPI({
            url: environment.globalPath + '/project/queryProjectInterestById',
            data: {
                pid: pid,
                memberId: memberId
            },
            version: '1.0.0',
            callback: function (data) {
                if (data.success) {

                    var result = data.result;
                    var pageTitle = result.projectName.split('期')[0] + '期';
                    var reg = /products[/]detail/g;

                    if (reg.test(comeUrl)) {
                        $investTitle.text(pageTitle);
                    } else {
                        alert('非法提交!');
                    }

                    var projectInfo = result.projectInterestInfoList;
                    productConfig = {
                        minInvestAmount: projectInfo[0].minInvest,
                        incrementAmount: projectInfo[0].minInvest,
                        areaSize: projectInfo.length,
                        minRate: projectInfo[0].annualizedRate,
                        maxRate: projectInfo[projectInfo.length - 1].annualizedRate,
                        annualizedRateType: result.annualizedRateType,
                        maxInvest: projectInfo[projectInfo.length - 1].maxInvest,
                        profitType: result.profitType,
                        maxInvestAmount: result.availableBalance,
                        availableBalance: result.availableBalance - 0,
                        earningPeriod: result.earningPeriod,
                        earningsDays: result.earningsDays,
                        investType: result.investType,
                        firstRealEarningDays: result.firstRealEarningDays,
                        firstDebtEarningDays: result.firstDebtEarningDays,
                        mostInvestAmount: result.mostInvestAmount,
                        incrementAnnualizedRate: result.incrementAnnualizedRate,
                        coupons: result.coupons
                    };
                    //  log(productConfig);
                    //$investAmount.val(parseInt(result.availableBalance/productConfig.minInvestAmount/2)*1000);

                    var processData = {
                        processList: result.projectInterestInfoList,
                        processInfo: {
                            minRate: productConfig.minRate,
                            maxRate: productConfig.maxRate,
                            maxInvest: productConfig.maxInvest,
                            annualizedRateType: productConfig.annualizedRateType,
                            minInvestAmount: productConfig.minInvestAmount
                        }
                    };
                    //渲染出优惠券
                    var $coupons = $('#j-coupons'),
                        coupons = data.result.coupons;
                    if (coupons.length > 0) {
                        coupons.forEach(function (item, index) {
                            coupons[index].isAvailable = item.isAvailable ? 1 : 0
                        })
                        var tpl = template('j-profitCoupon-tpl', {coupons: coupons}),
                            $couponsList = $('#j-profitCoupon-list')
                        //couponsList = coupons
                        $couponsList.html(tpl)
                        $coupons.on('click', function () {
                            $('#j-coupon-popup').removeClass('f-dn')
                            $('.j-cover').show()
                        })
                        selectCoupon($couponsList)
                    } else {
                        $coupons.find('span').eq(1).show().siblings('span').hide()

                    }
                    // 预告项目倒计时
                    var severTime = $('#j-severTime').attr('severTime')
                    var noticeTime = (result.onlineTime - severTime) / 1000;
                    if (noticeTime > 0) {
                        $('#j-submit-select').hide();
                        $('#j-noticeProject').show().css('line-height', '50px');
                        setTimeout(function () {
                            effects.projectCountdown(noticeTime)
                        }, 100)
                    }


                    var rewardPoint = {
                        ymjr: result.mostInvestPopularity,
                        ycdy: result.lastInvestPopularity,
                        yzqj: result.mostAndLastInvestPopularity
                    };
                    //获取五重礼的奖励
                    var html = template('j-gainRank-tpl', {reward: rewardPoint});
                    $('#j-gainRank').html(html);
                    //滑动条渲染
                    //log('processData:\n',processData);
                    var processHtml = template('j-process-tpl', processData);
                    $('#j-process-amount').html(processHtml);

                    $('#j-gainRank').data('restAmount', productConfig.availableBalance);
                    //进度定位
                    initProcess(productConfig, processData);
                } else {
                    if (data.resultCodes[0].code == '90414') {
                        $('.j-tipblock ').show();
                        $('.j-tipText').text(data.resultCodes[0].msg)
                        $('.j-reload').show().on('click', function () {
                            location.reload()
                        })
                        $('.j-cover').show()
                    } else {
                        $('.j-tipblock ').show();
                        $('.j-tipText').text(data.resultCodes[0].msg)
                        $('.j-reload').hide()
                        $('.j-cover').show()
                    }

                }
            }
        });
        //金额输入
        var amount = 0;
        $investAmount.on('blur', function () {
            var $this = $(this);

            setTimeout(function () {
                var val = $this.val();
                if (val > 0) {
                    amount = val;
                    $investSubmit.attr('href', $investSubmit.attr('url') + amount);
                }
                showRewardInfo(+amount);
            }, 200);
        });

        $investAmount.on('keydown', function (e) {
            if (e.keyCode === 190 || e.keyCode === 110) {
                return false;
            }
        });

        //投资提交
        $investSubmit.on('click', function (e) {
            var amount = $investAmount.val(),
                url = $(this).attr('url'),
                profitCouponNo = $('#j-couponNo').val() || null,
                backUrl = $('#j-goback').attr('href');
            $(this).attr('href', 'javascript:void(0)');
            if (!amount || !pid) {
                alert('请输入投资金额');
                return false;
            }

            base.getAPI({
                url: environment.globalPath + '/security/order/createOrder',
                version: '1.7.0',
                data: {
                    projectId: pid,
                    investAmount: amount,
                    profitCouponNo: profitCouponNo
                },
                callback: function (data) {
                    if (data.success) {
                        var result = data.result,
                            reVal = true;
                        if (result.status !== 0) {
                            alert('项目不是投资中状态');
                            reVal = false;
                        } else {
                            if (result.availableBalance - 0 < amount - 0) {
                                alert('项目余额不足，赶紧去修改投资额度吧！');
                                reVal = false;
                            }
                        }
                        if (reVal) {
                            location.href = url + result.id;
                        } else {
                            location.href = backUrl;
                            return false;
                        }
                    } else {
                        $('.j-closeBtn').show().on('click', function () {
                            $(this).parents().find('.j-tipsBlock').hide();
                            $('.j-cover').hide()
                        })
                        if (data.resultCodes[0].code == "10029") {
                            $('#j-SetPayPwdTips ').show();
                            $('.j-cover').show()


                        } else if (data.resultCodes[0].code == "90042") {
                            $('#j-sinapayDialog').show();
                            $('.j-cover').show()

                        } else {
                            alert(data.resultCodes[0].msg)
                            location.href = backUrl;
                        }
                        // alert('项目不是投资中状态');
                        // location.href=backUrl;
                        // return false;
                    }
                }
            });

        });
        var processHeight = 615;

        //投资输入金额并定位滑动
        $investAmount.on("keyup blur change", function (e) {

            var currentAmount = $(this).val(),
                productBalance = productConfig.availableBalance,
                minAmount = productConfig.minInvestAmount;

            if (e.type === "keyup") {

                if (isNaN(currentAmount)) {
                    //输入非数字
                    $(this).val(minAmount);
                } else {
                    //超出最大可投资额度
                    if (currentAmount >= productBalance) {
                        $(this).val(productBalance);
                    }
                }
                if (e.keyCode === 13) {
                    $(this).data("isEnter", true);
                } else {
                    $(this).data("isEnter", false);
                }
            }
            var initPosition = $investAmount.data("initPosition");
            if (typeof (initPosition) !== "undefined" && initPosition) {

                currentAmount = Math.ceil(productBalance / minAmount) * minAmount / 2;

                $investAmount.removeData("initPosition");
            }
            if (e.type === "blur" || $(this).data("isEnter") === true) {
                var correctAmount = 0,
                    step = currentAmount / productConfig.minInvestAmount,
                    pointAmount = $(".u-point-amount"),
                    maxAmount = Number(pointAmount.eq(-1).data("amount")),
                    amount0 = Number(pointAmount.eq(0).data("amount")),
                    amount1 = Number(pointAmount.eq(1).data("amount")),
                    amount2 = Number(pointAmount.eq(2).data("amount")),
                    amount3 = Number(pointAmount.eq(3).data("amount")),
                    width = processHeight,
                    aw1 = width / 3 - 15,
                    aw2 = width * 2 / 3 - 15,
                    reg = /^[1-9]\d*$/;
                if (currentAmount >= productBalance) {
                    $(this).val(productBalance);
                }
                //整数倍判断
                if (reg.test(step)) {
                    //最大值限制
                    if (currentAmount >= maxAmount) {
                        correctAmount = maxAmount;
                    } else {
                        correctAmount = step * productConfig.minInvestAmount;
                    }
                } else {
                    //值小于最小额度
                    if (step >= 1) {
                        correctAmount = Math.ceil(step) * productConfig.minInvestAmount;
                    } else {
                        correctAmount = productConfig.minInvestAmount;
                    }
                }
                $(this).val(correctAmount);
                //滑动
                var aTop = 0,
                    aRate = "0.00",
                    awTotal = aw1,
                    soleRate = ($(".j-rate").length === 1);
                if (!amount3) {
                    awTotal = aw2;
                }
                if (!soleRate) {

                    if (correctAmount < amount1) {
                        if (correctAmount !== amount0) {
                            if (!amount3) {
                                aTop = (correctAmount / amount1) * aw2;
                            } else {
                                aTop = (correctAmount / amount1) * aw1;
                            }
                        } else {
                            aTop = -15;
                        }
                        aRate = $(".u-rate-s1").data("rate");
                    } else if (correctAmount >= amount1 && correctAmount < amount2) {
                        aTop = awTotal + 15 + ((correctAmount - amount1) / (amount2 - amount1)) * aw1;

                        if (correctAmount == amount1) {
                            aTop = aTop - 15;
                        }
                        aRate = $(".u-rate-s2").data("rate");
                    } else if (correctAmount >= amount2) {
                        if (!amount3) {
                            aTop = 615;
                            aRate = $(".u-rate-s2").data("rate");
                        } else {
                            aTop = aw2 + 15 + ((correctAmount - amount2) / (amount3 - amount2)) * aw1;
                            aRate = $(".u-rate-s3").data("rate");
                        }
                        if (correctAmount == amount2) {
                            aTop = aTop - 15;
                        }
                    }
                } else {
                    aTop = (correctAmount / amount1) * (width - 15);
                    aRate = $(".u-rate-s1").data("rate");
                }

                if (aTop < 10) {
                    aTop = 10;
                }

                $("#j-process-drag").animate({
                    "top": aTop
                }).data("aTop", aTop);

                $("#j-process-crt").animate({
                    "height": aTop
                });
                var _currentRate = "0.00";
                if (productConfig.annualizedRateType === 1 ||
                    correctAmount >= $('#j-process-s1').data('amount')) {
                    _currentRate = aRate;
                } else {
                    _currentRate = getProductRate(correctAmount);
                }

                getEarnings(correctAmount, _currentRate, productConfig.earningsDays, productConfig.profitType, productConfig.firstRealEarningDays, productConfig.firstDebtEarningDays, productConfig.earningPeriod, productConfig.investType);
                $("#j-profit-percent").text(_currentRate);
                $("#investAmount").val(correctAmount);
            }
        });

    }
    function selectCoupon($id) {
        $id.off('click', 'li').on('click', 'li', function () {
            if (!$(this).hasClass("z-disabled")) {
                exAnnualizedRate = $(this).attr('exAnnualizedRate')
                extraInterestType = $(this).attr('extraInterestType')
                extraInterestDay = $(this).attr('extraInterestDay') || 0
                limitAmount = $(this).attr('amountScope')
                var couponNo = $(this).attr('code'),
                    text = '+' + $(this).children('em').html() + '收益券',
                    exText = $(this).children('em').html(),
                    $jCouponNo = $('#j-couponNo'),
                    $jCouponAmount = $('.j-couponAmount'),
                    $jExProfitPercent = $('.j-exProfit-percent'),
                    $jExPercent = $('#j-exProfit-percent'),
                    $jExProfit = $('.j-exProfit');
                if (extraInterestType == 1) {
                    exText = $(this).children('em').html() + '(' + extraInterestDay + '天)'
                }

                if (couponNo) {
                    $jCouponNo.val(couponNo).attr('exAnnualizedRate', exAnnualizedRate)
                    $jCouponAmount.html(text).addClass('f-fc-red')
                    $jExPercent.text(exText)
                    $jExProfit.show()
                    $jExProfitPercent.show()
                } else {
                    $jCouponNo.val('').attr('exAnnualizedRate', 0)
                    $jCouponAmount.html($(this).children('em').html()).addClass('f-fc-red')
                    $jExProfit.hide()
                    $jExProfitPercent.hide()
                }

                $id.parent().addClass('f-dn')
                $('.j-cover').hide()

                inputAmount = $("#j-investment").val()
                var annualizedRate = $('#j-profit-percent').text();
                getEarnings(inputAmount, annualizedRate, productConfig.earningsDays, productConfig.profitType,
                    productConfig.firstRealEarningDays, productConfig.firstDebtEarningDays, productConfig.earningPeriod, productConfig.investType);
            }
            else {
                return false
            }
        })
    }

    //随着金额的改变过滤收益券改变其排列顺序
    function filterCoupons(coupons, investAmount) {
        var usabledCoupons = [],
            disabledCoupons = [],
            amount = +investAmount
        coupons.forEach(function (item) {
            // 判断初始条件是否可用
            if (item.isAvailable == 0) {
                item.isAvailable = 0
            } else {
                item.isAvailable = (amount >= item.amountScope) ? 1 : 0
            }

            if (item.isAvailable) {
                usabledCoupons.push(item)
            } else {
                disabledCoupons.push(item)
            }
        })
        return [usabledCoupons.concat(disabledCoupons), usabledCoupons.length]
    }

    //初始化滑动条
    function initProcess(productConfig, processData) {
        var processDarg = $("#j-process-drag");
        if (processDarg.length > 0 && !processDarg.hasClass("z-final")) {
            processDarg.xSlider(processHeight, -10, "#j-dynamic-profit");
        }
        if (typeof productConfig !== "undefined") {
            //投资额输入框初始值
            var initStep = 1,//步进
            //项目收益类型1.阶梯收益 2.动态收益
                xType = productConfig.annualizedRateType,
                pointAmount = $(".u-point-amount"),
                amount1 = Number(pointAmount.eq(1).data("amount")),
                amount2 = Number(pointAmount.eq(2).data("amount")),
                pBalance = productConfig.availableBalance,
                pAmount = processData.processInfo.maxInvest,
                investType = productConfig.investType,
                minAmount = productConfig.minInvestAmount,
                _productRate = "0.00";
            //阶梯收益
            if (xType === 1) {
                //三个阶梯
                if ($(".j-rate").length !== 1) {
                    initStep = parseInt((amount1 / minAmount) +
                        ((amount2 - amount1) / minAmount) * 0.5);
                    pBalance = pBalance === pAmount ? initStep * minAmount : pBalance / 2;
                    if (pBalance < amount1) {
                        _productRate = $(".u-rate-s1").data("rate");
                    } else if (pBalance >= amount1 && pBalance < amount2) {
                        _productRate = $(".u-rate-s2").data("rate");
                    } else if (pBalance >= amount2) {
                        _productRate = $(".u-rate-s3").data("rate");
                    }
                }
                //只有一个阶梯
                else {
                    initStep = parseInt(amount1 * 0.5 / minAmount);
                    pBalance = pBalance === pAmount ? initStep * minAmount : pBalance / 2;
                    _productRate = $(".u-rate-s1").data("rate");
                }
            }
            //动态收益
            else {
                initStep = parseInt((amount1 / minAmount) *
                    (processHeight / 2) / (processHeight * 2 / 3));
                pBalance = pBalance === pAmount ? initStep * minAmount : pBalance / 2;
                _productRate = getProductRate(pBalance);
            }

            var earings = getEarnings(pBalance, _productRate, productConfig.earningsDays, productConfig.profitType, productConfig.firstRealEarningDays, productConfig.firstDebtEarningDays, productConfig.earningPeriod, investType);

            $("#j-profit-percent").text(_productRate + "%");
            //$("#j-profit-amount").text(earings);
            $investAmount.data("initPosition", true).blur();

        }
    }

    //动态显示五重礼奖励
    function showRewardInfo(amount) {

        var totalAmount = productConfig.maxInvest - 0,
            availableAmount = productConfig.maxInvestAmount - 0,
            mostInvestAmount = productConfig.mostInvestAmount;

        var $rank = $('#j-gainRank'),
            $rewardText = $rank.find('p'),
            state = {
                ymjr: false,
                ycdy: false,
                yzqj: false
            },
            $rewards = {
                ymjr: $rewardText.eq(1),
                ycdy: $rewardText.eq(2),
                yzqj: $rewardText.eq(3)
            };

        if (mostInvestAmount && mostInvestAmount > 0) {
            state.ymjr = (amount >= mostInvestAmount);
        }

        state.ycdy = (amount === availableAmount);

        state.yzqj = (state.ymjr && state.ycdy);

        $rank.find('.j-restAmount').text(splitAmount($rank.data('restAmount')));
        $rewards.yzqj.css('display', state.yzqj ? 'block' : 'none');
        $rewards.ycdy.css('display', state.ycdy ? 'block' : 'none');
        $rewards.ymjr.css('display', state.ymjr ? 'block' : 'none');

    }

    //金额格式化
    function splitAmount(amount) {

        if (amount >= 1000) {

            var amountStr = amount.toString(),
                size = parseInt(amountStr.length / 3),
                amountArray = amountStr.split('').reverse();

            for (var i = 1; i <= size; i++) {
                var j = i * 3 - 1;
                if (j !== amountArray.length - 1) {
                    amountArray[j] = ',' + amountArray[j];
                }
            }

            return amountArray.reverse().join('');

        } else {
            return amount;
        }
    }

    //拖动条控件
    $.fn.xSlider = function (width, offset, tips) {
        var _this = $(this),
            tObj = $(tips),
            tWidth = tObj.outerWidth(),
            xType = productConfig.annualizedRateType,
            rate1 = $(".u-rate-s1"),
            rate2 = $(".u-rate-s2"),
            rate3 = $(".u-rate-s3"),
            pointAmount = $(".u-point-amount"),
            balanceObj = $("#productBalance"),
            amount0 = Number(pointAmount.eq(0).data("amount")),
            amount1 = Number(pointAmount.eq(1).data("amount")),
            amount2 = Number(pointAmount.eq(2).data("amount")),
            amount3 = Number(pointAmount.eq(3).data("amount")),
            step1 = (amount1 - amount0) / amount0,
            step2 = (amount2 - amount1) / amount0,
            step3 = (amount3 - amount2) / amount0,
            aw1 = width / 3 - 15,
            aw2 = width * 2 / 3 - 15,
            aw3 = width - 15,
            currentStep = 0,
            startAmount = productConfig.minInvestAmount,
            minAmount = productConfig.incrementAmount,
            profitType = productConfig.profitType,
            earningsDays = productConfig.earningsDays;
        var firstDebtEarningDays = productConfig.firstDebtEarningDays;//第一期债权本息收益天数
        var firstRealEarningDays = productConfig.firstRealEarningDays;//第一期本息实际收益天数
        var earningPeriod = productConfig.earningPeriod;//目前投资收益期数
        _this.on("mousedown", function (e) {
            var sLeft = e.pageX,
                currentRate = "0.00",
                bTop = _this.position().top;
            $("body").on("mousemove", function (e) {
                var eLeft = e.pageX,
                    dragLeft = eLeft - sLeft + bTop,
                    productBalance = productConfig.availableBalance;

                //收益计算
                dragLeft = dragLeft > processHeight - 15 ? processHeight - 15 : dragLeft;
                if (xType === 1) {//阶梯收益
                    if ($(".j-rate").length !== 1) {

                        if (dragLeft > aw2) {

                            //第三阶段
                            currentStep = Math.floor((dragLeft - aw2) / ((aw3 - aw2) / step3));
                            startAmount = amount2;
                            currentRate = rate3.data("rate");
                        } else if (dragLeft > aw1 && dragLeft <= aw2) {

                            //第二阶段
                            currentStep = Math.floor((dragLeft - aw1) / ((aw2 - aw1) / step2));
                            startAmount = amount1;
                            currentRate = rate2.data("rate");
                        } else {

                            //第一阶段
                            currentStep = Math.floor(dragLeft / (aw1 / step1));
                            startAmount = amount0;
                            currentRate = rate1.data("rate");
                        }
                    } else {
                        aw1 = width - 15;
                        currentStep = Math.floor(dragLeft / (aw1 / step1));
                        startAmount = amount0;
                        currentRate = rate1.data("rate");
                    }
                } else {
                    //递增收益
                    if (dragLeft > aw2) {
                        currentStep = Math.floor((dragLeft - aw2) / ((aw3 - aw2) / step2));
                        startAmount = amount1;
                        currentRate = rate2.data("rate");
                    } else {
                        currentStep = Math.floor(dragLeft / (aw2 / step1));
                        startAmount = minAmount;
                        //动态收益率计算
                        //currentRate = rate1.data("rate");
                        var _amount = currentStep * minAmount + startAmount;
                        currentRate = getProductRate(_amount);
                    }
                }

                var currentAmount = currentStep * minAmount + startAmount,
                    profitPercentObj = $("#j-profit-percent");

                if (currentAmount >= productBalance) {
                    currentAmount = productBalance;
                    currentRate = profitPercentObj.text().replace("%", "");
                }

                profitPercentObj.text(currentRate + "%");
                if (currentStep >= 0) {
                    $("#investAmount").val(currentAmount);

                    $investAmount.val(currentAmount).text(currentAmount);
                    getEarnings(currentAmount, currentRate, earningsDays, profitType, firstRealEarningDays, firstDebtEarningDays, earningPeriod);
                } else {
                    $("#investAmount").val(minAmount);
                    $investAmount.val(minAmount).text(minAmount);

                    getEarnings(minAmount, currentRate, earningsDays, profitType, firstRealEarningDays, firstDebtEarningDays, earningPeriod);

                }
                //圆点和提示运动轨迹
                var arrow = tObj.find(".u-arrow-up"),
                    crt = $("#j-process-crt");

                var dragObjLeft, crtLeft, tObjLeft, arrowLeft;
                if (dragLeft < width - offset && dragLeft > -offset) {
                    dragObjLeft = dragLeft;
                    crtLeft = dragLeft;
                } else if (dragLeft <= -offset) {
                    dragObjLeft = -offset;
                    crtLeft = -offset;
                } else if (dragLeft >= width - offset) {
                    dragObjLeft = width - offset;
                    crtLeft = width - offset;
                }

                //tObj.css("left", tObjLeft);
                //if(dragObjLeft<10){
                //    dragObjLeft=10;
                //}
                _this.css("top", dragObjLeft).data({
                    "currentAmount": currentAmount,
                    "dLeft": dragLeft
                });
                //arrow.css("left", arrowLeft);
                crt.css("height", crtLeft);

            }).on("mouseup", function () {
                var currentAmount = $("#j-process-drag").data("currentAmount"),
                    productBalance = parseInt(balanceObj.data("balance"));
                if (currentAmount >= productBalance) {
                    $investAmount.blur();
                } else {
                    balanceObj.removeClass("f-fc-red2");
                }
                $("body").off("mousemove mouseup");
            });
        });
    };

    /**
     * 获得收益
     * @param amount {number} 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     * @param profitType 还款方式
     * @param firstRealEarningDays
     * @param firstDebtEarningDays
     * @param investType 项目类型 2=>直头 1=>债权
     * @param earningPeriod
     */

    function getEarnings(amount, annualizedRate, earningsDays, profitType, firstRealEarningDays, firstDebtEarningDays, earningPeriod, investType) {
//通过投资金额和收益券的变化来判断收益金额和收益券的选择
//         var $profitCoupon=$('#j-profitCoupon-list'),
//             filteredCoupons = filterCoupons(couponsList,amount),
//             availabledCouponSize = filteredCoupons[1],

        // //修改投资金额时再次渲染收益券列表
        // tpl=template('j-profitCoupon-tpl',{coupons:filteredCoupons[0]})
        // $profitCoupon.html(tpl)


        var availabledCouponSize = 0
        $('#j-profitCoupon-list>li').each(function () {
            var limitAmount = $(this).attr('amountScope'),
                isAvailable = $(this).attr('isAvailable');
            if ((amount - 0) < (limitAmount - 0)) {
                $(this).addClass('z-disabled')
            } else {
                if (isAvailable == 0) {
                    availabledCouponSize++
                } else {
                    $(this).removeClass('z-disabled')
                    availabledCouponSize++
                }
            }
        })
        if (availabledCouponSize <= 1) {
            exAnnualizedRate = 0
            $('.j-couponAmount').text('选择收益券').removeClass('f-fc-red')
            $('#j-couponNo').val(null)
            $('.j-exProfit').hide()
            $('.j-exProfit-percent').hide()
        }
        var allAnnualizedRate = (annualizedRate - 0) + (exAnnualizedRate - 0);
        var earnings = 0.00,
            exEarnings = 0.00;
        switch (profitType) {
            case "monthly_paid": //按日计息

                if (extraInterestType == 1) {
                    earnings = ((getMonthlyPaidEarnings(amount, annualizedRate, earningsDays, earningPeriod, investType) - 0) + (exProfitAmount(amount, exAnnualizedRate, extraInterestDay) - 0)).toFixed(2)
                    exEarnings = exProfitAmount(amount, exAnnualizedRate, extraInterestDay)
                } else {
                    earnings = getMonthlyPaidEarnings(amount, allAnnualizedRate, earningsDays, earningPeriod, investType)
                    exEarnings = getMonthlyPaidEarnings(amount, exAnnualizedRate, earningsDays, earningPeriod, investType)
                }

                break;
            case "once_paid": //一次性还本付息
                if (extraInterestType == 1) {
                    earnings = ((getMonthlyPaidEarnings(amount, annualizedRate, earningsDays, earningPeriod, investType) - 0) + (exProfitAmount(amount, exAnnualizedRate, extraInterestDay) - 0)).toFixed(2)
                    exEarnings = exProfitAmount(amount, exAnnualizedRate, extraInterestDay)
                } else {
                    earnings = getMonthlyPaidEarnings(amount, allAnnualizedRate, earningsDays, earningPeriod, investType)
                    exEarnings = getMonthlyPaidEarnings(amount, exAnnualizedRate, earningsDays, earningPeriod, investType)
                }

                break;
            case "principal_average": //等额本金
                break;
            case "all_average": //等额本息
                break;
            case "avg_principal": //等本等息
                if (extraInterestType == 1) {
                    earnings = ((getAvgPrincipalInterestEarnings(amount, annualizedRate, firstRealEarningDays, firstDebtEarningDays, earningPeriod, investType) - 0) + (exProfitAmount(amount, exAnnualizedRate, extraInterestDay) - 0)).toFixed(2)
                    exEarnings = exProfitAmount(amount, exAnnualizedRate, extraInterestDay)
                } else {
                    earnings = getAvgPrincipalInterestEarnings(amount, allAnnualizedRate, firstRealEarningDays, firstDebtEarningDays, earningPeriod, investType)
                    exEarnings = getAvgPrincipalInterestEarnings(amount, exAnnualizedRate, firstRealEarningDays, firstDebtEarningDays, earningPeriod, investType)
                }
                break;
            case "avg_principal_week"://直投项目周期为周
                if (extraInterestType == 1) {
                    earnings = ((getWeekPaidEarnings(amount, annualizedRate, earningPeriod) - 0) + (exProfitAmount(amount, exAnnualizedRate, extraInterestDay) - 0)).toFixed(2)
                    exEarnings = exProfitAmount(amount, exAnnualizedRate, extraInterestDay)
                } else {
                    earnings = getWeekPaidEarnings(amount, allAnnualizedRate, earningPeriod);
                    exEarnings = getWeekPaidEarnings(amount, exAnnualizedRate, earningPeriod);
                }
                break;
            case "season_paid"://直投项目按季付息
                if (extraInterestType == 1) {
                    earnings = ((getSeasonPaidEarnings(amount, annualizedRate, earningPeriod) - 0) + (exProfitAmount(amount, exAnnualizedRate, extraInterestDay) - 0)).toFixed(2)
                    exEarnings = exProfitAmount(amount, exAnnualizedRate, extraInterestDay)
                } else {
                    earnings = getSeasonPaidEarnings(amount, allAnnualizedRate, earningPeriod)
                    exEarnings = getSeasonPaidEarnings(amount, exAnnualizedRate, earningPeriod);
                }

                break;
        }
        $getableEarnings.text(earnings)
        $("#j-exAmount").text((exEarnings - 0).toFixed(2))
        return earnings
    }

    /**
     * 高额加息计算
     * @param amount 投资金额
     * @param annualizedRate 高额加息率
     * @param extraInterestDay 加息天数
     *
     */
    function exProfitAmount(amount, annualizedRate, extraInterestDay) {
        return (((annualizedRate / 36000) * amount) * extraInterestDay).toFixed(2)
    }
    /**
     * 按周计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数--单位是周
     *
     */
    function getWeekPaidEarnings(amount, annualizedRate, earningPeriod) {
        var interest = (annualizedRate / 36000).toFixed(10);
        var earnings = (amount * interest).toFixed(10);
        return (earnings * 7 * earningPeriod).toFixed(2);
    }

    /**
     * 按季计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningPeriod 收益天数--是季度
     *
     */
    function getSeasonPaidEarnings(amount, annualizedRate, earningPeriod) {
        var interest = (annualizedRate / 36000).toFixed(10);
        var earnings = (amount * interest).toFixed(10);
        return (earnings * earningPeriod).toFixed(2);
    }

    /**
     * 按日计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     * @param investType 项目类型 2=>直头 1=>债权
     * @param earningPeriod 收益期数
     */
    function getMonthlyPaidEarnings(amount, annualizedRate, earningsDays, earningPeriod, investType) {
        var interest = (annualizedRate / 36000).toFixed(10);
        if (investType == 2) {
            var p2pearnings = (amount * interest).toFixed(10);
            return (p2pearnings * earningPeriod).toFixed(2);
        } else {
            var earnings = (amount * interest).toFixed(2);
            return (earnings * earningsDays).toFixed(2);
        }
    }

    /**
     * 等本等息
     * @param amount 投资额
     * @param annualizedRate  年化收益
     * @param firstRealEarningDays    第一期当前实际收益天数
     * @param firstDebtEarningDays    第一期债权收益天数
     * @param earningPeriod    收益期数
     * @param investType 项目类型 2=>直头 1=>债权
     * @returns
     */
    function getAvgPrincipalInterestEarnings(amount, annualizedRate, firstRealEarningDays, firstDebtEarningDays, earningPeriod, investType) {
        var monthlyRate = (annualizedRate / 1200).toFixed(10);
        var interest = 0;

        if (investType == 2) {

            var P2PMonthlyInterest = ( monthlyRate * amount).toFixed(10);
            interest = (P2PMonthlyInterest * earningPeriod).toFixed(2);
            return interest;

        } else {
            //第一期按照当月实际收益天数计算收益
            if (earningPeriod > 0) {

                var monthlyInterest = monthlyRate * amount;
                interest = (Number((monthlyInterest * (firstRealEarningDays / firstDebtEarningDays)).toFixed(2)) + Number(monthlyInterest.toFixed(2) * (earningPeriod - 1))).toFixed(2);
                return interest;
            }
        }

    }

    /**
     * 获得产品金额
     * @param currentAmount
     * @returns
     */
    function getProductRate(currentAmount) {
        var currentRate = productConfig.minRate, //默认最小收益
            startAmount = productConfig.minInvestAmount, //起投金额
            minAmount = productConfig.incrementAmount, //递增单位金额
        //profitType = productConfig.profitType, //类型
        //earningsDays = productConfig.earningsDays, //天
            maxInvestAmount = productConfig.maxInvestAmount, //收益封顶
            totalAmount = productConfig.totalAmount, //总金额
            incrementAnnualizedRate = productConfig.incrementAnnualizedRate; //递增收益
        var minRate = productConfig.minRate - 0; //最小收益率
        var maxRate = productConfig.maxRate - 0; //最大收益率
        if (currentAmount == startAmount) {
            return minRate;
        }
        if (currentAmount == totalAmount) {
            return maxRate;
        }
        if (currentAmount >= maxInvestAmount) {
            return maxRate;
        }
        var result = (parseFloat(currentAmount / minAmount)).toFixed(2);
        if (result < 2) {
            currentRate = minRate;
        } else {
            var _rate = (minRate + (result - 1) * incrementAnnualizedRate).toFixed(2) - 0;
            if (_rate >= maxRate) {
                currentRate = maxRate;
            } else {
                currentRate = _rate;
            }
        }
        return currentRate;
    }

    /*
     * */

    // 获取URL参数
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg); // 匹配目标参数
        if (r != null) {
            return unescape(r[2]);
        }
        return null; // 返回参数值
    }
});