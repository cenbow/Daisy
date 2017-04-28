/*global projectArrowLocator,productConfig*/
//项目 
(function () {
    /**
     *
     * exAnnualizedRate--选择收益券所得的额外收益
     * limitAmount--所选收益券的限制起投金额
     * couponSize--加载出的收益券的长度
     * extraInterestType--收益券类型  1为高额收益券,0为普通收益券
     * extraInterestDay--高额收益券加息天数
     * @type {number}
     */
    var exAnnualizedRate=0,
        inputAmount=$("#investAmount").val(),
        limitAmount=0,
        extraInterestType = 0,
        extraInterestDay = 0;

    //转让项目基本信息input
    var $transfer = $('#j-transfer-info'),
        transferData=$transfer.data();

    var lteIE8 = $.support.leadingWhitespace; //return bool
    //项目TAB联动
    $("#j-plist-tab>li, #j-plist-status>li").on("click", function () {
        var i = $(this).index();
        $(this).addClass("z-current")
            .siblings().removeClass("z-current");
        $(this).has("em").parent().next().find("#j-profit-arrow").css("left", 80 + i * 166);
    });

    //抽奖次数的数据
    var ruleList = [];
    var $rule = $('.j-lottery-rule');
    if ($rule && $rule.length>0) {
        $rule.each(function(){
            var $this = $(this);
            var obj = {};
            obj.startAmount = $this.data('startamount');
            obj.endAmount = $this.data('endamount');
            obj.number = $this.data('number');
            ruleList.push(obj)
        })
    }

    //选项卡
    $(".j-infotab").on("click", "span", function () {
        var i = $(this).index(),
            left = $(this).position().left,
            width = $(this).outerWidth();
        $(this).parent().prev(".u-arrow-down").css("left", left - 3 + width / 2);
        $(this).addClass("z-current").siblings().removeClass("z-current");
        $(this).parent().nextAll(".u-pinfo-contract").eq(i)
            .addClass("z-current").siblings().removeClass("z-current");
    });
    //拖动条控件
    $.fn.xSlider = function (width, offset, tips) {
        var _this = $(this),
            tObj = $(tips),
            tWidth = tObj.outerWidth(),
            xType = $("#j-detail-process").data("type"),
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
            earningsDays = productConfig.earningsDays,
	        firstDebtEarningDays = productConfig.firstDebtEarningDays,//第一期债权本息收益天数
	    	firstRealEarningDays = productConfig.firstRealEarningDays,//第一期本息实际收益天数
	    	earningPeriod = productConfig.earningPeriod,//目前投资收益期数
            isDirectProject = productConfig.isDirectProject;//是否为直投项目
        _this.on("mousedown", function (e) {
            var sLeft = e.pageX,
                currentRate = "0.00",
                bLeft = _this.position().left;
            $("body").on("mousemove", function (e) {
                var eLeft = e.pageX,
                    dragLeft = eLeft - sLeft + bLeft,
                    productBalance = parseInt(balanceObj.data("balance"));

                //收益计算
                dragLeft = dragLeft > 685 ? 685 : dragLeft;
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
                } else {
                    balanceObj.removeClass("f-fc-red2");
                }
                profitPercentObj.text(currentRate + "%");
                var investmentObj = document.getElementById("j-investment");
                if(productConfig.category===2){
                    if(currentAmount<minAmount){
                        currentAmount=minAmount
                    }
                    var pValue = _.toFixed(transferData.value * currentAmount/transferData.balance,2),
                        needPay = _.toFixed(transferData.transferamount * currentAmount/transferData.balance,2)
                    $('#j-project-value').text(pValue)
                    $('#j-needPay').text(needPay)
                    profitType = 'monthly_paid'
                }
                if (currentStep >= 0) {
                    $("#investAmount").val(currentAmount);
                    if (lteIE8) {
                        $("#j-investment").val(currentAmount).text(currentAmount);
                    } else {
                        //ie8 jquery bug
                        investmentObj.innerText = currentAmount;
                        investmentObj.value = currentAmount;
                    }
                    getEarnings(currentAmount, currentRate, earningsDays, profitType,firstRealEarningDays,firstDebtEarningDays,earningPeriod,isDirectProject,needPay);
                } else {
                    $("#investAmount").val(minAmount);
                    if (lteIE8) {
                        $("#j-investment").val(minAmount).text(minAmount);
                    } else {
                        //ie8 jquery bug
                        investmentObj.innerText = minAmount;
                        investmentObj.value = minAmount;
                    }
                    getEarnings(minAmount, currentRate, earningsDays, profitType,firstRealEarningDays,firstDebtEarningDays,earningPeriod,isDirectProject,needPay);
                }
                //圆点和提示运动轨迹    
                var arrow = tObj.find("#j-profit-arrow"),
                    crt = $("#j-process-crt");

                var dragObjLeft, crtLeft, tObjLeft, arrowLeft;
                if (dragLeft < width - offset && dragLeft > -offset) {
                    dragObjLeft = dragLeft;
                    crtLeft = dragLeft;

                    if (dragLeft < (tWidth / 2)) {
                        tObjLeft = -offset;
                        arrowLeft = dragLeft + offset * 2 - 6;
                    } else if (dragLeft > (width - offset - tWidth / 2)) {
                        tObjLeft = width - tWidth + offset;
                        arrowLeft = Math.abs(width - offset - dragLeft - tWidth) - offset - 3;
                    } else {
                        tObjLeft = dragLeft - tWidth / 2 + offset;
                        arrowLeft = tWidth / 2 - 6;
                    }
                } else if (dragLeft <= -offset) {
                    dragObjLeft = -offset;
                    crtLeft = -offset;
                    tObjLeft = -offset;
                    arrowLeft = 17;
                } else if (dragLeft >= width - offset) {
                    dragObjLeft = width - offset;
                    crtLeft = width - offset;
                    tObjLeft = width - tWidth + offset;
                    arrowLeft = tWidth - 6 - offset;
                }

                tObj.css("left", tObjLeft);
                _this.css("left", dragObjLeft).data({
                    "currentAmount": currentAmount,
                    "dLeft": dragLeft
                });
                arrow.css("left", arrowLeft);
                crt.css("width", crtLeft);

            }).on("mouseup", function () {
                var currentAmount = $("#j-process-drag").data("currentAmount"),
                    productBalance = parseInt(balanceObj.data("balance"));
                if (currentAmount >= productBalance) {
                    $("#j-investment").blur();
                } else {
                    balanceObj.removeClass("f-fc-red2");
                }
                $("body").off("mousemove mouseup");
            });
        });
    };
    var investmentInput = $("#j-investment");
    //投资输入金额并定位滑动
    investmentInput.on("keyup blur change", function (e) {
        var currentAmount = Number($(this).val()),
            balanceObj = $("#productBalance"),
            productBalance = parseInt(balanceObj.data("balance")),
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
        var initPosition = $("#j-investment").data("initPosition");
        if (typeof (initPosition) !== "undefined" && initPosition) {
            //是否新手投资
            if (!productConfig.noviceProject) {
                currentAmount = productBalance / 2;
            } else {
                currentAmount = 100;
            }

            $("input#j-investment").removeData("initPosition");
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
                width = 700,
                aw1 = width / 3 - 15,
                aw2 = width * 2 / 3 - 15,
                reg = /^[1-9]\d*$/;
            var balance = balanceObj.data("balance");
            if (currentAmount >= balance) {
                $(this).val(balance);
                balanceObj.addClass("f-fc-red2");
            } else {
                balanceObj.removeClass("f-fc-red2");
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
            var aLeft = 0,
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
                            aLeft = (correctAmount / amount1) * aw2;
                        } else {
                            aLeft = (correctAmount / amount1) * aw1;
                        }
                    } else {
                        aLeft = -15;
                    }
                    aRate = $(".u-rate-s1").data("rate");
                } else if (correctAmount >= amount1 && correctAmount < amount2) {
                    aLeft = awTotal + 15 + ((correctAmount - amount1) / (amount2 - amount1)) * aw1;

                    if (correctAmount == amount1) {
                        aLeft = aLeft - 15;
                    }
                    aRate = $(".u-rate-s2").data("rate");
                } else if (correctAmount >= amount2) {
                    if (!amount3) {
                        aLeft = 685;
                        aRate = $(".u-rate-s2").data("rate");
                    } else {
                        aLeft = aw2 + 15 + ((correctAmount - amount2) / (amount3 - amount2)) * aw1;
                        aRate = $(".u-rate-s3").data("rate");
                    }
                    if (correctAmount == amount2) {
                        aLeft = aLeft - 15;
                    }
                }
            } else {
                aLeft = (correctAmount / amount1) * (width - 15);
            }

            $("#j-process-drag").animate({
                "left": aLeft
            }).data("aLeft", aLeft);
            var dynamicProfitObj = $("#j-dynamic-profit");
            proWidth = dynamicProfitObj.outerWidth(),
                bLeft = 0,
                cLeft = 0,
                offset = 15;
            if (aLeft < width - offset && aLeft > -offset) {
                if (aLeft < (proWidth / 2)) {
                    bLeft = -offset;
                    cLeft = aLeft + offset * 2 - 6;

                } else if (aLeft > (width - offset - proWidth / 2)) {
                    bLeft = width - proWidth + offset;
                    cLeft = Math.abs(width - offset - aLeft - proWidth) - offset - 6;
                } else {
                    bLeft = aLeft - proWidth / 2 + offset;
                    cLeft = proWidth / 2 - 6;
                }
            } else if (aLeft <= -offset) {
                bLeft = -offset;
                cLeft = 17;
            } else if (aLeft >= width - offset) {
                bLeft = width - proWidth + offset + 60;
                cLeft = proWidth - 7 - offset - 60;
            }
            $("#j-process-crt").animate({
                "width": aLeft
            });
            dynamicProfitObj.animate({
                "left": bLeft
            });
            $("#j-profit-arrow").animate({
                "left": cLeft
            });
            var _currentRate = "0.00";
            if ($(".u-rate-s3").length > 0) {
                _currentRate = aRate;
            } else {
                _currentRate = getProductRate(correctAmount);
            }
            if(productConfig.category===2){
                var pValue = _.toFixed(correctAmount * transferData.value/transferData.balance,2),
                    needPay = _.toFixed(correctAmount * transferData.transferamount/transferData.balance,2)
                $('#j-project-value').text(pValue)
                $('#j-needPay').text(needPay)
            }else{
                $("#j-profit-percent").text(_currentRate + "%").val(_currentRate);
            }
            getEarnings(correctAmount, _currentRate, productConfig.earningsDays, productConfig.profitType,
                productConfig.firstRealEarningDays,productConfig.firstDebtEarningDays,productConfig.earningPeriod,productConfig.isDirectProject,needPay);
            //$("#j-profit-percent").text(_currentRate + "%").val(_currentRate);
            $("#investAmount").val(correctAmount);
        }
    });
    //投资额输入框提示
    investmentInput.on("focus", function () {
        var tips = $(this).siblings("p");
        tips.show();
        setTimeout(function () {
            tips.fadeOut();
        }, 1500);
        $(this).data("focusVal", $(this).val());
    });

    // $('#j-process-drag').on('mousemove mouseup',function(){
    //     var amount = parseInt($('#j-investment').val());
    //     countLotteryNum(amount);
    // })

    $('#j-investment').on('keyup blur change',function(){
        var amount = parseInt($('#j-investment').val());
        countLotteryNum(amount);
    })

    /**
     * 可获得的抽奖次数
     * 抽奖次数等于  正常的次数加上额外次数
     * 正常次数 等于 投资金额除以起头金额
     * 额外次数 根据区间判断
     */
    function countLotteryNum(currentAmount) {
        var exeCount = 0;  //额外的抽奖次数
        var amount = productConfig.minInvestAmount;  //起投金额
        // startAmount 开始金额， endAmount 结束金额, number 为奖励次数
        for(var i = 0; i<ruleList.length; i++) {
            if(i + 1 == ruleList.length) {  //最后一条数据,只判断起投金额
                if(currentAmount >= ruleList[i].startAmount) {
                    exeCount = ruleList[i].number;
                }
            }else {
                if(currentAmount >= ruleList[i].startAmount && currentAmount <= ruleList[i].endAmount) {
                    exeCount = ruleList[i].number;
                }
            }
        }
        $("#j-lottery-count").text(parseInt(currentAmount/amount)+exeCount);
    }

    //进度定位
    initProcess();
    function initProcess() {
        var processDarg = $("#j-process-drag");
        if (processDarg.length > 0 && !processDarg.hasClass("z-final")) {
            processDarg.xSlider(700, 15, "#j-dynamic-profit");
        }
        if (typeof productConfig !== "undefined") {
            var itemSate = $(".j-investment-btn").data("state");
            if (itemSate === 1) {
                //投资额输入框初始值
                var scrollWidth = 700, //滑动条宽度
                //步进
                    initStep = 1,
                //项目收益类型1.阶梯收益 2.动态收益
                    xType = Number($("#j-detail-process").data("type")),
                    pointAmount = $(".u-point-amount"),
                    amount1 = Number(pointAmount.eq(1).data("amount")),
                    amount2 = Number(pointAmount.eq(2).data("amount")),
                    pBalance = Number($("#productBalance").data("balance")),
                    pAmount = Number($("#j-product-amount").data("amount")),
                    minAmount = productConfig.minInvestAmount,
                    _productRate = "0.00";
                //阶梯收益
                if (xType === 1) {
                    //三个阶梯
                    if ($(".j-rate").length !== 1) {
                        initStep = parseInt((amount1 / minAmount) +
                            ((amount2 - amount1) / minAmount) * 0.5);
                        pBalance = pBalance === pAmount ? initStep * minAmount : pBalance;
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
                        pBalance = pBalance === pAmount ? initStep * minAmount : pBalance;
                        _productRate = $(".u-rate-s1").data("rate");
                    }
                }
                //动态收益
                else {
                    initStep = parseInt((amount1 / minAmount) *
                        (scrollWidth / 2) / (scrollWidth * 2 / 3));
                    pBalance = pBalance === pAmount ? initStep * minAmount : pBalance;
                    _productRate = getProductRate(pBalance);
                }
                if(productConfig.category===2){
                    var pValue = _.toFixed(transferData.value * pBalance/transferData.balance,2),
                        needPay = _.toFixed(transferData.transferamount * pBalance/transferData.balance,2)
                    $('#j-project-value').text(pValue)
                    $('#j-needPay').text(needPay)
                    productConfig.profitType = 'monthly_paid'
                }else{
                    $("#j-profit-percent").text(_productRate).val(_productRate);
                }
                getEarnings(pBalance, _productRate, productConfig.earningsDays, productConfig.profitType,
                    productConfig.firstRealEarningDays,productConfig.firstDebtEarningDays,productConfig.earningPeriod,productConfig.isDirectProject,needPay);
                $("#investAmount").val(pBalance);
                $("#j-investment").data("initPosition", true).blur();
            }

        }
    }

    //图片滚动
    $.fn.photoSlider = function () {
        this.each(function () {
            var _this = $(this),
                list = _this.find("ul"),
                len = list.children().length,
                btnL = _this.find(".j-btn-l"),
                btnR = _this.find(".j-btn-r"),
                btnU = _this.find(".j-btn-u"),
                btnD = _this.find(".j-btn-d");
            //向右 
            btnR.on("click", function () {
                var clicked = $(this).data("clicked");
                if (!clicked) {
                    $(this).data("clicked", true);
                    var left = list.css("margin-left");
                    left = left.replace(/[px]/g, "");
                    if (len > 4 && (len + left / 105) > 4) {
                        list.animate({
                            "margin-left": left - 105
                        }, function () {
                            btnR.data("clicked", false);
                        });
                    } else {
                        btnR.data("clicked", false);
                    }
                }
            });
            //向左
            btnL.on("click", function () {
                var clicked = $(this).data("clicked");
                if (!clicked) {
                    $(this).data("clicked", true);
                    var left = list.css("margin-left");
                    left = Number(left.replace(/[px]/g, ""));
                    if (len > 4 && left < 0) {
                        list.animate({
                            "margin-left": left + 105
                        }, function () {
                            btnL.data("clicked", false);
                        });
                    } else {
                        btnL.data("clicked", false);
                    }
                }
            });
            //向下
            btnD.on("click", function () {
                var clicked = $(this).data("clicked");
                if (!clicked) {
                    $(this).data("clicked", true);
                    var top = list.css("margin-top");
                    top = top.replace(/[px]/g, "");
                    if (len > 8 && (len + top * 8 / 160) > 8) {
                        list.animate({
                            "margin-top": top - 160
                        }, function () {
                            btnD.data("clicked", false);
                        });
                    } else {
                        btnD.data("clicked", false);
                    }
                }
            });
            //向上
            btnU.on("click", function () {
                var clicked = $(this).data("clicked");
                if (!clicked) {
                    $(this).data("clicked", true);
                    var top = list.css("margin-top");
                    top = Number(top.replace(/[px]/g, ""));
                    if (len > 8 && top < 0) {
                        list.animate({
                            "margin-top": top + 160
                        }, function () {
                            btnU.data("clicked", false);
                        });
                    } else {
                        btnU.data("clicked", false);
                    }
                }
            });
            _this.find("img").on("mouseenter", function () {
                var imgUrl = $(this).attr("url"),
                    title = $(this).attr("title");
                if (typeof imgUrl != "undefined" && imgUrl !== "") {

                    var offset = $(this).offset(),
                        left = _this.offset().left,
                        top = Number(offset.top) + 58,
                        marginLeft = _this.find("ul").css("margin-left").replace("px", ""),
                        n = $(this).parent().index();
                    if (_this.data("type") !== "undefined" && _this.data("type") === "8x") {
                        n = n % 4;
                        top = top + 10;
                    }

                    var limgObj=$(".u-pinfo-limg"),
                        limg = limgObj.eq(0);

                    if (limg.data("parent") === _this.attr("id")) {
                        limg.remove();
                    }
                    var aLeft = 55 + (n + parseInt(marginLeft / 105)) * 105;
                    var largeImg = "<div class='u-pinfo-limg' style='left:" + left + "px;top:" +
                        top + "px;'><div><i style='left:" + aLeft + "px'></i><img src='" +
                        imgUrl + "' alt='' /><span>" + title + "</span></div></div>";
                    $("body").append(largeImg)
                        .find(".u-pinfo-limg").data("parent", _this.attr("id"));
                }

            }).on("mouseleave", function () {
                var timer = setTimeout(function () {
                    var limg = $(".u-pinfo-limg").eq(0);
                    if (typeof (limg.data("parent")) !== "undefined") {
                        limg.remove();
                    }
                }, 300);
                $(".u-pinfo-limg").on("mouseenter", function () {
                    clearTimeout(timer);
                }).on("mouseleave", function () {
                    $(this).remove();
                });
                _this.find("img").on("mouseover", function () {
                    clearTimeout(timer);
                });

            });
            $(".u-pinfo-limg").on("mouseenter", function () {
                $(".u-pinfo-limg").remove();
            });
        });
    };
    $(".j-btn-l2, .j-btn-r2").on("click", function () {
        if ($(this).hasClass("j-btn-l2")) {
            $(this).parents("td").find(".j-btn-l").click();
        } else {
            $(this).parents("td").find(".j-btn-r").click();
        }
    });
    $(".j-btn-u2, .j-btn-d2").on("click", function () {
        if ($(this).hasClass("j-btn-d2")) {
            $(this).parents("td").find(".j-btn-d").click();
        } else {
            $(this).parents("td").find(".j-btn-u").click();
        }
    });

    //侧栏导航
    initAreaNav();

    function initAreaNav() {
        var areaNav = $("#j-area-nav");
        if (areaNav.length > 0) {
            var areaA = $("#j-area-a").position().top + 130,
                areaB = $("#j-area-b").position().top + 130;
            $(window).on("scroll", function () {
                var aTop = areaNav.offset().top,
                    sTop = $(this).scrollTop(),
                    areaC = $("#transactionsDetail").offset().top;
                if (sTop > 86) {
                    areaNav.fadeIn();
                    if (aTop >= areaA && aTop < areaB - 82) {
                        areaNav.find("li").eq(0).addClass("z-current")
                            .siblings().removeClass("z-current");
                    } else if (aTop >= areaB - 82 && aTop < areaC - 164) {
                        areaNav.find("li").eq(1).addClass("z-current")
                            .siblings().removeClass("z-current");
                    } else if (aTop > areaC - 164) {
                        areaNav.find("li").eq(2).addClass("z-current")
                            .siblings().removeClass("z-current");
                    } else {
                        areaNav.find("li").removeClass("z-current");
                    }
                } else {
                    areaNav.fadeOut();
                }
                if (sTop >= 1960) {
                    areaNav.addClass("z-bottom");
                } else {
                    areaNav.removeClass("z-bottom");
                }
            });
        }

        function setNavPosition() {
            var sWidth = $("body").outerWidth();
            if ((sWidth - 990) / 2 - 50 > 0) {
                $("#j-area-nav").css("left", (sWidth - 990) / 2 - 50);
            } else {
                $("#j-area-nav").css("left", 0);
            }
        }

        if ($("body").outerWidth() > 990) {
            setNavPosition();
        }
        $(window).on("resize", function () {
            setNavPosition();
        });
        $("#j-gotop").click(function () {
            $(window).scrollTop(0);
        });
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
        var minRate = productConfig.minRate; //最小收益率
        var maxRate = productConfig.maxRate; //最大收益率
        if (currentAmount == startAmount) {
            return minRate;
        }
        if (currentAmount == totalAmount) {
            return maxRate;
        }
        if (currentAmount >= maxInvestAmount) {
            return maxRate;
        }
        var result = _(parseFloat(currentAmount / minAmount)).toFixed(2);
        if (result < 2) {
            currentRate = minRate;
        } else {
            var _rate = _(parseFloat(minRate + (result - 1) * parseFloat(incrementAnnualizedRate))).toFixed(2);
            if (_rate >= maxRate) {
                currentRate = maxRate;
            } else {
                currentRate = _rate;
            }
        }
        return currentRate;
    }


    /**
     * 选择收益券下拉框事件
     * exAnnualizedRate--选择收益券所得的额外收益
     * limitAmount--所选收益券的限制起投金额
     * amount--用户投资的金额
     * annualizedRate--用户所在的区域的年化收益
     */
    $(".j-couponSelector[type='new'] li").on("click", function() {
        if(!$(this).hasClass("z-disabled")){
            var inputAmount=$("#j-investment").val();
            var annualizedRate=Number($('#j-profit-percent').text().replace("%", ""));
            exAnnualizedRate=$(this).attr('amount');
            limitAmount=$(this).attr('limitAmount');
            extraInterestType=$(this).attr('type');
            extraInterestDay=$(this).attr('interestDays');
            getEarnings(inputAmount, annualizedRate, productConfig.earningsDays, productConfig.profitType,
                productConfig.firstRealEarningDays,productConfig.firstDebtEarningDays,productConfig.earningPeriod,productConfig.isDirectProject,null);
            if(exAnnualizedRate>0){
                $('.j-exAmount').show()
            }else{
                $('.j-exAmount').hide()
            }
        }else {
            return false
        }

    })
    /**
     * 获得收益
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     * allAnnualizedRate--年化收益率加上收益券收益率所得的总收益
     * @param profitType 还款方式
     */
    function getEarnings(amount, annualizedRate, earningsDays, profitType,firstRealEarningDays,firstDebtEarningDays,earningPeriod,isDirectProject,needPay) {
        //通过投资金额和收益券的变化来判断收益金额和收益券的选择
        var availabledCouponSize = 0
        $('.j-couponList>li').each(function(){
            var limitAmount = $(this).attr('limitAmount'),
                limited=$(this).attr('limited')
            if((amount-0)<(limitAmount-0)){
                $(this).addClass('z-disabled')
            }else{
                if(limited==1){
                    availabledCouponSize++
                }else{
                    $(this).removeClass('z-disabled')
                    availabledCouponSize++
                }

            }
        })
        if(availabledCouponSize<=1){
            exAnnualizedRate=0
            $('.j-esAnnualizedRate').text('+收益券')
            $('.j-selected-ipt').val(null)
            $('.j-exAmount').hide()
        }

        var earnings = 0.00,
            exEarnings=0.00;

        if (extraInterestType==1) {
            var allAnnualizedRate=(annualizedRate-0);
        }else{
            var allAnnualizedRate=(annualizedRate-0)+(exAnnualizedRate-0);
        }

        switch (profitType) {
            case "monthly_paid": //按日计息
                earnings = getMonthlyPaidEarnings(amount, allAnnualizedRate, earningsDays, isDirectProject, exAnnualizedRate, extraInterestDay);
                exEarnings=getMonthlyPaidEarnings(amount, exAnnualizedRate, earningsDays, isDirectProject, null, extraInterestDay);
                break;
            case "once_paid": //一次性还本付息
                earnings = getMonthlyPaidEarnings(amount, allAnnualizedRate, earningsDays,isDirectProject, exAnnualizedRate, extraInterestDay);
                exEarnings=getMonthlyPaidEarnings(amount, exAnnualizedRate, earningsDays,isDirectProject, null, extraInterestDay);
                break;
            case "principal_average": //等额本金
                break;
            case "all_average": //等额本息
                break;
            case "avg_principal": //等本等息按月还款
                earnings =  getAvgPrincipalInterestEarnings(amount, allAnnualizedRate,firstRealEarningDays,firstDebtEarningDays,earningPeriod,isDirectProject,exAnnualizedRate,extraInterestDay);
                exEarnings=getAvgPrincipalInterestEarnings(amount, exAnnualizedRate,firstRealEarningDays,firstDebtEarningDays,earningPeriod,isDirectProject,null,extraInterestDay);
                break;
            case "avg_principal_week": //等本等息按周还款
                earnings = getWeekPaidEarnings(amount, allAnnualizedRate, earningPeriod, exAnnualizedRate, extraInterestDay);
                exEarnings = getWeekPaidEarnings(amount, exAnnualizedRate, earningPeriod, null, extraInterestDay);
                break;
            case "season_paid": //按季付息
                earnings = getSeasonPaidEarnings(amount, allAnnualizedRate, earningsDays, exAnnualizedRate, extraInterestDay);
                exEarnings = getSeasonPaidEarnings(amount, exAnnualizedRate, earningsDays, null, extraInterestDay);
                break;
            default :
                break;
        }

        if($transfer.length){
            var transferAnnualizedRate = $("#transferAnnualizedRate").attr("value");
            var transferProfit = ((transferAnnualizedRate / 36000) * needPay * earningsDays).toFixed(20),
                transferDiscount = _.toFixed((amount - needPay), 2);
            transferProfit = (+transferProfit).toFixed(2)
            $("#j-transfer-profit").text(transferProfit);
            $("#j-transfer-discount").text(transferDiscount);
        }else{
            $("#j-profit").text(earnings);
        }
        $("#j-exAmount").text((+exEarnings).toFixed(2))
        // 计算抽奖机会
        var amount = parseInt($('#j-investment').val());
        countLotteryNum(amount);
    }

    /**
     * 按周计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数--本来应该是周，后端将其转化为天数了
     * @param highCouponRate 高额加息券利率
     * @param highCouponDays 高额收益券加息天数
     */
    function getWeekPaidEarnings(amount, annualizedRate, earningPeriod, highCouponRate, highCouponDays) {
        var interest = (annualizedRate / 36000).toFixed(10);
        var exInterest = (highCouponRate / 36000).toFixed(10);
        var earnings = (amount * interest).toFixed(10);
        var exEarnings = (amount * exInterest).toFixed(10);
        var totalEarnings = 0;

        if(highCouponDays && highCouponDays>0){
            if(highCouponRate && highCouponRate>0){
                totalEarnings = (+(earnings * 7 * earningPeriod).toFixed(2) + (+(exEarnings * highCouponDays).toFixed(2))).toFixed(2);
            }else {
                totalEarnings = (earnings * highCouponDays).toFixed(2);
            }
        }else{
            totalEarnings = (earnings * 7 * earningPeriod).toFixed(2);
        }

        return totalEarnings;
    }

    /**
     * 按季计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     * @param highCouponRate 高额加息券利率
     * @param highCouponDays 高额收益券加息天数
     */
    function getSeasonPaidEarnings(amount, annualizedRate, earningsDays, highCouponRate, highCouponDays) {
        console.log('data2', earningsDays)
        var interest = (annualizedRate / 36000).toFixed(10);
        var exInterest = (highCouponRate / 36000).toFixed(10);
        var earnings = (amount * interest).toFixed(10);
        var exEarnings = (amount * exInterest).toFixed(10);
        var totalEarnings = 0;

        if(highCouponDays && highCouponDays>0){
            if(highCouponRate && highCouponRate>0) {
                totalEarnings = (+(earnings * earningsDays).toFixed(2) + (+(exEarnings * highCouponDays).toFixed(2))).toFixed(2);
            }else {
                totalEarnings = (earnings * highCouponDays).toFixed(2);
            }
        }else {
            totalEarnings = (earnings * earningsDays).toFixed(2);
        }

        return totalEarnings;
    }
    /**
     * 按日计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     * @param highCouponRate 高额加息券利率
     * @param highCouponDays 高额收益券加息天数
     */
    function getMonthlyPaidEarnings(amount, annualizedRate, earningsDays, isDirectProject, highCouponRate, highCouponDays) {
        var interest = (annualizedRate / 36000).toFixed(10);
        var exInterest = (highCouponRate / 36000).toFixed(10);
    	var earnings = 0;
    	var exEarnings = 0;
        var totalEarnings = 0;

    	if(isDirectProject){
            earnings= (amount * interest).toFixed(10);
    	    if(highCouponDays && highCouponDays>0) {
                exEarnings= (amount * exInterest).toFixed(10);
            }
    	}else{
    		earnings= (amount * interest).toFixed(2);
    	}

    	if(highCouponDays && highCouponDays>0){
    	    if(highCouponRate && highCouponRate>0) {
                totalEarnings = (+(earnings * earningsDays).toFixed(2) + (+(exEarnings * highCouponDays).toFixed(2))).toFixed(2);
            }else {
                totalEarnings =  (earnings * highCouponDays).toFixed(2);
            }
        }else {
            totalEarnings = (earnings * earningsDays).toFixed(2);
        }

        return totalEarnings
    }
    
    /**
     * 等本等息
     * @param amount 投资额
     * @param annualizedRate  年化收益
     * @param firstRealEarningDays    第一期当前实际收益天数
     * @param firstDebtEarningDays    第一期债权收益天数
     * @param earningPeriod    收益期数
     * @param highCouponRate 高额加息券利率
     * @param highCouponDays 高额收益券加息天数
     * @returns
     */
    function getAvgPrincipalInterestEarnings(amount, annualizedRate,firstRealEarningDays,firstDebtEarningDays,earningPeriod,isDirectProject,highCouponRate,highCouponDays){
    	var monthlyRate = _(annualizedRate /1200).toFixed(10);
    	var exmonthlyRate = _(highCouponRate /36000).toFixed(10);
    	var interest = 0;

    	if(earningPeriod >0){
    		var monthlyInterest = monthlyRate * amount ;
    		if(isDirectProject){
    		    if(highCouponDays && highCouponDays>0){
                    if(highCouponRate && highCouponRate>0) {
                        interest = (+(monthlyInterest * earningPeriod).toFixed(2) + (+(exmonthlyRate * amount * highCouponDays).toFixed(2))).toFixed(2);
                    }else {
                        interest = ((annualizedRate /36000).toFixed(10) * amount * highCouponDays).toFixed(2);
                    }
                }else{
                    interest = (monthlyInterest * earningPeriod).toFixed(2);
    		    }
    		}else{
    			//第一期按照当月实际收益天数计算收益
    			interest = (Number((monthlyInterest * (firstRealEarningDays / firstDebtEarningDays)).toFixed(2)) + Number(monthlyInterest.toFixed(2) * (earningPeriod-1))).toFixed(2);
    		}

    		return interest;
    	}
    }


    if ("undefined" !== typeof productConfig) {//项目详情页才处理以下业务
        //质押&抵押详细信息
        if (typeof productConfig.pledgeBaseInfo === "object") {
            renderPledgeBaseInfo(productConfig.pledgeBaseInfo);
        }

        //初次加载计算收益
        // getEarnings(productConfig.minInvestAmount, $(".u-rate-s2").data("rate"), productConfig.earningsDays, productConfig.profitType);

        //加载项目交易记录
        //var transactionsList = $("#j-transactions-list"),
        //    transactionsDetail = $("#transactionsDetail");
        loadTransactionsByProjectId(productConfig.productId);

        //$(window).on("scroll", function () {
        //
        //    if (transactionsDetail.hasClass("z-empty") || transactionsList.hasClass("z-finished")) {
        //        return false;
        //    }
        //
        //    var height = $(window).height(),
        //        top = $(window).scrollTop(),
        //        pageNo = transactionsDetail.data("pageNo"),
        //        uHeight = pageNo ? 1200 * (pageNo - 1) - 1000 : 200,
        //        uscrollHeight = transactionsList.offset().top + uHeight,
        //        productId = productConfig.productId;
        //
        //    if (typeof (pageNo) === "undefined") {
        //        pageNo = 2;
        //    }
        //
        //    var isLoaded = transactionsDetail.data("isLoaded");
        //
        //    if ((height + top) >= uscrollHeight && isLoaded) {
        //        loadTransactionsByProjectId(productId, pageNo);
        //        transactionsDetail.data("pageNo", pageNo + 1);
        //    }
        //});

        //立即投资，保存订单到订单支付页面
        $("#j-now-order").click(function () {
            var hasSetPayPassword=$('#hasSetPayPassword').val()
            //如果非立即投资状态，不提交请求
            if ($(this).hasClass("z-disabled")||$(this).hasClass("z-ceased")) {
                return;
            }
            if(hasSetPayPassword==0){
                $('#j-paymentCipherBlock').show()
                $('.j-cover').show()
                $('.j-closeBtn').on('click',function () {
                    $('#j-paymentCipherBlock').hide()
                    $('.j-cover').hide()
                })
                return false
            }
            if (checkInvestData()) {
                // $("#productForm").submit();
                $(this).addClass("z-disabled").attr('disabled', "disabled").val("提交中");
                var profitCouponNo = $("#profitCouponNo-h").attr("value")||null;
                var investAmount = $("#investAmount").attr("value");
                var projectId = $("#projectId").attr("value");
                var projectCategory = $("#projectCategory").attr("value");
                var transferId = $("#transferId").attr("value");
                var xToken = $("#xToken").attr("value");
                var onceToken = $("#onceToken").attr("value");
                var data = {
                    'profitCouponNo': profitCouponNo,
                    'projectId': projectId,
                    'onceToken': onceToken,
                    'xToken': xToken,
                    'projectCategory': projectCategory,
                    'transferId': transferId
                };

                //转让项目传认购本金,普通项目传投资金额
                if(projectCategory==2){
                    data.transferPrincipal = investAmount;
                }else if(projectCategory==1){
                    data.investAmount = investAmount;
                }

                $.xPost({
                    url: environment.globalPath + "/order/save",
                    data: data,
                    callback: function(data) {
                        var result = data.result;
                        if (data.success) {
                            var orderNo = result.orderNo;
                              window.location.href = environment.globalPath + "/order/to/pay?orderNo=" + orderNo;
                        } else {
                            var error=data.resultCodeEum
                            if (error&&typeof error[0].msg !== 'undefined') {
                                var msg = error[0].msg,
                                    code=error[0].code

                                switch(code){
                                    case '4':
                                        msg='请勿重复下单，刷新当前页面可重新下单'
                                        break
                                    case '90406':
                                        msg='亲，该项目不可投，换个项目试试吧'
                                        break
                                }

                                $.xDialog({
                                    content: msg,
                                    callback:function(){
                                        if(code==='90406'){
                                            window.location.href='/products/list-all-all-investing-1-createTimeAsc-1.html'
                                        }
                                    }
                                })
                            } else {
                                $.xDialog({
                                    content: data.result, callback: function () {
                                        location.reload();
                                    }
                                });
                            }
                            $("#j-now-order").removeClass("z-disabled").removeAttr('disabled').val("提交订单");
                        }

                    }
                });
            }
        });
        //显示还款详情
        $("#j-proceeds-detail").on("mouseenter", function () {
            $("#j-refund-detail").slideDown();
        }).on("mouseleave", function () {
            var proceedsTimer = setTimeout(function () {
                $("#j-refund-detail").slideUp();
            }, 300);
            $("#j-refund-detail").on("mouseenter", function () {
                clearTimeout(proceedsTimer);
            }).on("mouseleave", function () {
                $(this).slideUp();
            });
        });
        //租赁协议和保证函、查看租赁合同
        $("#j-leaseBonus-mode").on("click",
            ".j-leaseBonus-agreement, .j-lease-contractBtn",
            function () {
                $("#j-other-info").click();
            }
        );
        //图片预览Init
        $("#j-photo-scroller1,#j-photo-scroller2,#j-photo-scroller3,#j-photo-scroller4").photoSlider();
    } else {//项目列表页
        //排序切换
        var sortDate = formatDate(environment.serverDate, 'yyyy-mm-dd').replace(/-/g, ""),
            $plistSortBtn=$('#j-plist-sort').find('a'),
            $sortList=$('.j-sort-list');
            $plistSortBtn.on('click', function () {
            var i = $(this).index();
                $(this).addClass('z-current').siblings().removeClass('z-current');
            $sortList.eq(i).removeClass('z-disabled').
                siblings('.j-sort-list').addClass('z-disabled');
            i === 1 ? $.xStorage('projectListSort', sortDate) : $.xStorage( 'projectListSort',null);
        });
        if (sortDate === $.xStorage('projectListSort')) {
            $sortList.eq(1).removeClass('z-disabled').
                siblings('.j-sort-list').addClass('z-disabled');
            $plistSortBtn.eq(1).addClass('z-current').siblings().removeClass('z-current');
        }else{
            $sortList.eq(0).removeClass('z-disabled');
        }

        //banner切换
        $("#j-products-banner").fadeShow({
            time: 5000,
            type: "adv"
        });
        //项目预告切换
        var $sidewrap = $('#j-side-wrap');
        if(($sidewrap.data("size"))>1 ){
            $sidewrap.fadeShow({
                time: 5000,
                type: "adv"
            });
        }
        //项目列表箭头定位
        projectArrowLocator();
    }

    // 箭头的定位（项目列表)
    function projectArrowLocator() {
        var currentMenu = $(".u-plist-tab").find(".z-current"),
            index = currentMenu.index(),
            $status=$(".u-plist-status"),
            currentSpan = $status.find("span"),
            currentI = $status.find("i"),
            //position = currentMenu.position(),
            //iWidth = currentI.outerWidth(),
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
     * 检查提交数据
     * @returns {Boolean}
     */
    function checkInvestData() {
        var balance = $("#productBalance").attr("value");
        var investAmount = $("#investAmount").val();
        if (balance === "") {
            $.xDialog({
                content: "项目剩余无可投金额，请查看其它可投资项目。"
            });
            return false;
        }
        if (parseFloat(investAmount) > parseFloat(balance)) {
            $.xDialog({
                content: "项目剩余可投金额" + balance + ",请重新选择 "
            });
            return false;
        }
        return true;
    }

    /**
     * 质押&抵押详细信息
     * @param dataJson
     */
    function renderPledgeBaseInfo(dataJson) {
        //var data = $.parseJSON(dataJson);
        $.each(dataJson, function (n, v) {
        	if(n=="houseRecord_info"){
        		$("#car_ms").text(v);
        	}else if(n=="db_company"){
        		if(typeof v !="undefined" && v!=null && v!=""){
        			$(".j-db-company").show();
        		}
        		$("#" + n).text(v);	
        	}else{
        		$("#" + n).text(v);
        		$("#" + n + "_title").attr("title", v);
        	}
        });
    }

    //加载项目交易记录
    function loadTransactionsByProjectId(projectId, pageNo) {
        $("#transactionsDetail").loading().load(environment.globalPath +
            "/projectTransaction/detail/transactions?projectId=" + projectId);
        //var list = $("#transactionsDetail"),
        //    tableList = list.find("tbody"),
        //    direction = pageNo === 1 ? "none" : "fixed";
        //list.data("isLoaded", false);
        //list.xLoading(function (loading) {
        //    $.getJSON('/projectTransaction/queryTransactionDetail', {
        //            'projectId': projectId,
        //            'pageNo': pageNo
        //        },
        //        function (data) {
        //            if (typeof (data) !== "undefined" && data.length) {
        //                var itemHtml = getDataHtml(data);
        //
        //                if (pageNo === 1) {
        //                    tableList.prepend(itemHtml);
        //                } else {
        //                    tableList.append(itemHtml);
        //                }
        //
        //                list.data("isLoaded", true).loadingDone();
        //
        //                if (data.length < 50) {
        //                    tableList.addClass("z-finished");
        //                }
        //            } else if (pageNo === 1) {
        //                list.hide().addClass("z-empty");
        //                //移除投资记录的左侧导航
        //                $("#j-transactions-detail").remove();
        //            }else {
        //                list.loadingDone();
        //            }
        //        }
        //    );
        //}, direction);
        ////记录数据处理
        //function getDataHtml(data) {
        //    var dataHtml = [];
        //    $.each(data, function (n, item) {
        //        var avatars = "",
        //            transactionTime = new Date(item.transactionTime),
        //            date = formatDate(transactionTime, "yyyy-mm-dd"),
        //            time = formatDate(transactionTime, "HH:mm:ss");
        //        avatars = item.avatars ? ("https://oss-cn-hangzhou.aliyuncs.com" +
        //        item.avatars) : "/static/img/member/avatar_35x35.png";
        //
        //        //一羊领头活动ICON
        //        var activityIconsArray = [item.lastInvest, item.luckInvest,
        //                item.mostInvest, item.firstInvest,
        //                "ycdy", "xyns", "ymjr", "yylt"],
        //            activityIconsHtml = "";
        //        for (var i = 0; i < activityIconsArray.length - 4; i++) {
        //            if (activityIconsArray[i]) {
        //                activityIconsHtml += '<span class="u-icon-act u-icon-' +
        //                    activityIconsArray[i + 4] + '"></span>';
        //            }
        //        }
        //
        //        //额外年化收益
        //        var extraRate = item.extraAnnualizedRate,
        //            totalRate = item.annualizedRate,
        //            extraRateHtml = "";
        //
        //        if (extraRate && extraRate > 0) {
        //            totalRate = (extraRate + item.annualizedRate).toFixed(2);
        //            extraRateHtml = ' (' + item.annualizedRate + '%+<em class="f-fc-red">' +
        //                extraRate + '%</em>)';
        //        }
        //
        //        var bonusList = "",
        //            totalBonusHtml = "",
        //            totalBonus = item.leaseBonusAmounts;
        //
        //        if (totalBonus) {
        //            bonusList = "data-bonus='" + JSON.stringify(item.leaseBonusDetailList) + "'";
        //            totalBonusHtml = "&nbsp;&nbsp;+￥<em>" + totalBonus + "</em>";
        //        }
        //
        //        var bonusRate = item.bonusAnnualizedRate ? ' data-bonusrate="' +
        //        item.bonusAnnualizedRate : "";
        //        dataHtml.push('<tr><td class="u-invest-user">' +
        //            '<div class="u-user-wrap"><span class="u-user-head">' +
        //            '<img src="' + avatars + '" alt="用户头像">' +
        //            '<i> </i></span>' + item.username + '</div>' +
        //            '<div class="u-icon-wrap">' + activityIconsHtml + '</div></td>' +
        //            '<td class="u-invest-amount"><em>￥' + item.investAmount + '</em></td>' +
        //            '<td class="u-invest-profit"><span>' + totalRate + '%</span>' + extraRateHtml + '</td>' +
        //            '<td class="u-invest-bonus j-invest-bonus f-fs12"' + bonusList +
        //            bonusRate + '"><span class="u-bonus-amount">' +
        //            '￥' + item.totalInterest + '</span>' + totalBonusHtml + '</td>' +
        //            '<td class="u-invest-time" width="130">' + date + '<span>' +
        //            time + '</span></td></tr>');
        //    });
        //    return dataHtml.join("");
        //}
    }


//项目列表页导航
var plistProducts=$('#j-plist-products'),
    litteArrows=$('#j-litte-arrows'),
    plistType=$('#j-plist-type');

    //箭头定位到选中项目
    var i=plistProducts.find('.z-current').index(),
    slidetime=200;
    switch (i){
        case 0:
            litteArrows.animate({
                left:43
            },slidetime);
            break;
        case 1:litteArrows.animate({
            left:140
        },slidetime);
            break;
        case 2:litteArrows.animate({
            left:235
        },slidetime);
            break;
    }
    //选择项目类型

    plistProducts.on('click','li',function(){
        $(this).addClass('z-current').siblings().removeClass('z-current');
        //鼠标进入标签时箭头跟随至标签下方
    }).on('mouseenter','li',function(){
       var i = $(this).index(),
           slidetime=200;
        switch (i){
            case 0:
                litteArrows.animate({
                   left:43
                },slidetime);
                break;
            case 1:litteArrows.animate({
                left:140
            },slidetime);
                break;
            case 2:litteArrows.animate({
                left:235
            },slidetime);
                break;
        }
        //鼠标离开时箭头回到被点击过的标签下方
    }).on('mouseleave',function(){
        var i=plistProducts.find('.z-current').index(),
        slidetime=200;
        switch (i){
            case 0:
                litteArrows.animate({
                    left:43
                },slidetime);
                break;
            case 1:litteArrows.animate({
                left:140
            },slidetime);
                break;
            case 2:litteArrows.animate({
                left:235
            },slidetime);
                break;
        }
    });
    var v=plistProducts.find("li.z-current").index(),
        z=v-1

    //选择项目类型
    plistType.find("ul").eq(z).show()
    plistType.on('click','li',function(){
        $(this).addClass('z-current').siblings().removeClass('z-current');
    })

    // 获取路径
    function getRootPath_web() {
        //获取当前网址，如： http://localhost:8081/products/list-all-all-investing-2-rateDesc-1.html
        var curWwwPath = window.document.location.href;
        //获取主机地址之后的目录，如： products/list-all-all-investing-2-rateDesc-1.html
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        //获取主机地址，如： http://localhost:8081
        var localhostPaht = curWwwPath.substring(0, pos);
        //获取带"/"的项目名，如：/products
        var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
        return (localhostPaht + projectName);
    }

    // 从路径获取排序参数
    function getSortNameByPath() {
        var projectUrl = location.href;
        var addressArr = projectUrl.split("/");
        var urlArr = addressArr[4].split("-");
        var sortStr = urlArr[5];
        return sortStr
    }

    // 排序选中
    $('#j-plist-type').find('.f-zrSort-btn').each(function(){
        var $this = $(this);
        var sort = "";
        var sortName = $this.data('sort');
        var ascSortName = $this.data('ascsort');
        var sortPath = getSortNameByPath();
        if(sortName == sortPath || ascSortName == sortPath) {
            $this.parent().addClass('z-current');
            if(sortPath=="createTimeAsc"||sortPath=="daysAsc"||sortPath=="rateAsc"){
                $this.find('.u-triangle-down').removeClass().addClass('u-triangle-up')
            }
        }
    })

    //转让项目排序列表
    $('#j-plist-type').on('click','.f-zrSort-btn',function(){
        var $this = $(this);
        var projectUrl = location.href;
        var sortData = $this.data('sort');
        var addressArr = projectUrl.split("/");
        var urlArr = addressArr[4].split("-");
        var sortStr = urlArr[5];
        var sort = "";
        var projectPath = getRootPath_web();
        var targetUrl = "";

        if(sortStr == "all"){
            targetUrl = projectPath+"/list-all-all-investing-2-"+sortData+"-"+urlArr[6];
        }else {
            if($this.parent().hasClass('z-current')){
                switch (sortStr) {
                    case "createTimeDesc":
                        sort = "createTimeAsc";
                        break;
                    case "createTimeAsc":
                        sort = "createTimeDesc";
                        break;
                    case "daysDesc":
                        sort = "daysAsc";
                        break;
                    case "daysAsc":
                        sort = "daysDesc";
                        break;
                    case "rateDesc":
                        sort = "rateAsc";
                        break;
                    case "rateAsc":
                        sort = "rateDesc";
                        break;
                    default:
                        sort = "all";
                        break;
                }
            }else {
                sort = sortData;
            }

            targetUrl = projectPath+"/list-all-all-investing-2-"+sort+"-"+urlArr[6];
        }
        $this.attr("href",targetUrl);
    });

})();
/*租赁分红*/
(function () {
    //加载租赁记录列表
    var projectId = $("#projectId").val(),
        bonusUsersObj = $("#j-bonus-users"),
        transactionsObj = $("#transactionsDetail");
    if (typeof (projectId) !== "undefined") {
        getLeaseDetail(projectId);
    }
    //加载分红详情
    $("#j-lease-list").on("click", ".j-lease-detailBtn", function (e) {
        var _this = $(this),
            bonusList = $("#j-bonus-list"),
            bonusListDom = bonusList.parents("tr"),
            itemId = $(this).data("id"),
            bonusItemId = bonusUsersObj.data("itemId");
        e.preventDefault();
        //是否重复点击
        if (bonusItemId === itemId) {
            //展开折叠
            if (_this.hasClass("z-opened")) {
                bonusList.slideUp(500, function () {
                    _this.removeClass("z-opened");
                    bonusListDom.hide();
                });
            } else {
                bonusListDom.show();
                bonusList.slideDown(500, function () {

                    _this.addClass("z-opened");
                });
            }
        } else {
            //是否初次打开
            if (typeof (bonusItemId) === "undefined") {
                getBonusDetail(itemId, 1);
                bonusAnimate();
            } else {
                bonusList.slideUp(500, function () {
                    getBonusDetail(itemId, 1);
                    bonusAnimate();
                });
            }
            //重置列表状态
            bonusUsersObj.removeData().data("itemId", itemId);
        }
        //打开折叠动画
        function bonusAnimate() {
            bonusUsersObj.css("visibility", "visible");
            _this.parents("tr").after(bonusListDom);
            bonusListDom.show();
            bonusList.slideDown(500, function () {
                _this.addClass("z-opened");
            });
        }
    });
    //显示投资者的租赁收益详情
    transactionsObj.on("mouseenter", ".j-invest-bonus", function () {
        var position = $(this).position(),
            bonusList = $(this).data("bonus"),
            totalBonus = $(this).find("em").text(),
            bonusRate = $(this).data("bonusrate"),
            bonusHtml = "";
        if (typeof (bonusList) !== "undefined") {
            for (var i = 0; i < bonusList.length; i++) {
                bonusHtml += "<span>" + formatDate(bonusList[i].createTime, "yyyy-mm-dd") +
                    "&nbsp;分红<em>+￥" + bonusList[i].bonusAmount + "</em></span>";
            }
            var bonusAmount = $("#j-bonus-amount");
            bonusAmount.html(bonusHtml)
                .prev("p").find("em").text(" +￥" + totalBonus);
            bonusAmount.next("span").find("em").text(bonusRate);
            $("#j-bonus-detail").css({
                "left": position.left - 29,
                "top": position.top - 5
            }).show();
        }
    });
    transactionsObj.on("mouseleave", "#j-bonus-detail", function () {
        $(this).hide();
    });
    //获取租赁详情
    function getLeaseDetail(projectId) {
        $.getJSON(environment.globalPath + "/leaseBonus/leaseDetail?projectId=" + projectId,
            function (data) {
                if (data.length !== 0) {
                    $(".u-lease-table").show();
                    var items = [];
                    $.each(data, function (i, item) {
                        var bonusStatus = "";
                        if (item.bonusStatus === 2) {
                            bonusStatus = "已分红";
                        }
                        items.push("<tr><td>" + (i + 1) + "</td>" +
                            "<td>" + item.startDateStr + "~" + item.endDateStr + "</td>" +
                            "<td>" + item.leaseDays + "</td>" +
                            "<td>￥" + item.rental + "</td>" +
                            "<td>￥" + item.totalRental + "</td>" +
                            "<td>" + item.userBonus + "%</td>" +
                            "<td>" + bonusStatus + "</td>" +
                            "<td>" + item.bonusDateStr + "</td>" +
                            "<td><a href='#' class='j-lease-detailBtn' data-id='" + item.id + "'>查看明细</a>" +
                            "<a href='#j-area-b' class='j-lease-contractBtn'>查看合同</a></td></tr>"
                        );
                    });
                    $("#j-lease-list").prepend(items.join(""));
                } else {
                    $("#j-lease-list").parent().remove();
                }

            }
        );
    }

    //获取分红详情
    function getBonusDetail(leaseDetailId, currentPage) {
        var data = {
                "currentPage": currentPage,
                "leaseDetailId": leaseDetailId
            },
            totalPages = bonusUsersObj.data("totalPages");
        //翻页判断
        if (typeof (totalPages) !== "undefined" && currentPage > totalPages) {
            return false;
        }
        $.getJSON(environment.globalPath + "/leaseBonus/bonusDetail", data, function (data) {
            var items = [],
                bonusRoles = $("#j-bonus-list").find("p");
            //分红比例
            $.each(data, function (n, v) {
                bonusRoles.find("span[name='" + n + "']").text(v);
            });
            //设置总页数
            if (typeof (totalPages) === "undefined") {
                bonusUsersObj.data("totalPages", data.bonusDetail.totalPageCount);
            }
            //分红用户列表
            $.each(data.bonusDetail.data, function (i, item) {
                var avatars = "";
                if (item.avatars) {
                    avatars = "https://oss-cn-hangzhou.aliyuncs.com" + item.avatars;
                } else {
                    avatars = "/static/img/member/avatar_35x35.png";
                }
                items.push("<li><div class='u-user-wrap'><span class='u-user-head'>" +
                    "<img src='" + avatars + "' alt='用户头像'/>" +
                    "<span>&nbsp;</span></span>" + item.username + "</div>" +
                    "<span>￥" + item.bonusAmountStr + "</span></li>"
                );
            });
            if (typeof (currentPage) !== "undefined" && currentPage === 1) {
                bonusUsersObj.html(items.join("")).data("leaseDetailId", leaseDetailId);
            } else {
                bonusUsersObj.append(items.join(""));
            }

        });
    }

    //加载更多分红详情(分页)
    bonusUsersObj.on("scroll", function () {
        var uHeight = $(this).outerHeight(),
            uScrollTop = $(this).scrollTop(),
            uscrollHeight = $(this).get(0).scrollHeight,
            itemId = $(this).data("itemId"),
            pageNum = $(this).data("pageNum");
        if (typeof (pageNum) === "undefined") {
            pageNum = 2;
        }
        if ((uHeight + uScrollTop) >= uscrollHeight) {
            getBonusDetail(itemId, pageNum);
            $(this).data("pageNum", pageNum + 1);
        }
    });
    //圆环
    var $pstat=$('.j-pl-stat');
    if ($pstat.length > 0) {
        $("body").append("<canvas id='j-canvas-demo'></canvas>");
        try {
            $("canvas")[0].getContext("2d");
            $pstat.circliful();
                } catch (err) {

        }
        $("#j-canvas-demo").remove();
    }

})();
(function(){
    //保障措施第二个td一行显示

   $(".u-pinfo-table td:contains('风控师审核')").next().addClass("j-title-text")
   $(".u-pinfo-table td:contains('质押管理')").next().addClass("j-title-text")
   $(".u-pinfo-table td:contains('风控审核')").next().addClass("j-title-text")
    //    str=riskCheck.text(),
    //    str1=riskCheck1.text(),
    //    str2=riskCheck2.text();
    //
    //insertions(riskCheck,str);
    //insertions(riskCheck1,str1);
    //insertions(riskCheck2,str2);



var td=$("td.j-title-text")
    td.each(function(){
            var tdText=$(this).text()
            insertions($(this),tdText)
            function insertions(insertTitle,insertText){
                insertTitle.html("<div class='u-text-overflow j-text-overflow'>"+"<i class='f-dn'>"+insertText+"</i>"+"<span class='f-text-overflow'>"+insertText.substring(0)+"</span></div>")
                    .find("div").css('width','755px');
            }
            var strLength=getStringLength(tdText)
        //字符长度大于120时执行
            if(strLength>120){
                var  textoverflow=$(".j-text-overflow");
                textoverflow.on('mouseenter',function(){
                    $(this).find('i').removeClass('f-dn');
                }).on('mouseleave',function(){
                    $(this).find('i').addClass('f-dn');
                })
            }



            function getStringLength(string){
                return string.replace(/[^\x00-\xff]/g,'**').length
            }


    })

})();

// 如意图标点击跳转到春节活动页
(function() {
    var isRuyi = false;
    $('.j-icon-ruyi').on('click', function() {
        isRuyi = true;
        window.location.href = '/activity/springFestival/index#act4';
        return false;
    });

    $('.j-icon-ruyi').parent().on('click', function() {
        if (isRuyi) {
            return false;
        }
    });
})();

//P2P项目详情
(function(){
    //详情页分栏
    var tabInfo=$("#j-tab-info");
    tabInfo.on('click','a',function(){
        var i = $(this).index();
        $(this).addClass('z-current').siblings().removeClass('z-current');
        $(this).parent().nextAll(".m-detail-wrap").eq(i)
            .addClass("z-current").siblings().removeClass("z-current");
    });
    //切换显示
    var directionBtn = $('.j-direction-btn'),
        liSum=directionBtn.parent().find('li').length,
        isClickBtn = true;
        directionBtn.parent().find('ul').css("width",liSum*236);

    if(liSum>2){
        directionBtn.on('click', function () {
            var direction = $(this).data('direction'),
                list = $(this).parent().find('ul'),
                listSize = list.find('li').length, step = 0,
                offset = Number(list.css('margin-left').replace('px', ''));

            // 防止频繁点击
            if (!isClickBtn) {
                return false
            }
            isClickBtn = false

            //滑动方向
            if (direction === 'left') {
                step = -1;
                if (Math.abs(offset) / 236 >= listSize - 2) {
                    isClickBtn = true
                    return false;
                }
            } else {
                step = 1;
                if (offset === 0) {
                    isClickBtn = true
                    return false;
                }
            }

            list.animate({
                marginLeft: offset + step * 236
            }, 500, function () {
                isClickBtn = true
            });
        });
    }else{
        directionBtn.addClass('f-dn');
    }

    /**直投项目风控信息显示*/
    if("undefined" != typeof directProjectInfo && "undefined" != typeof directProjectInfo.riskInfo){
    	$.each(directProjectInfo.riskInfo,function(n,v){
    		if("undefined" == typeof v || v==null || v==""){
    			$("#"+n).parent("tr").hide();
    		}
    		$("#"+n).html(v);
    	});
    }
    /**直投项目担保物详细信息显示*/
    if("undefined" != typeof directProjectInfo && "undefined" != typeof directProjectInfo.collateral){
    	var	colTrObj = $("<div>"),
    		i=0;//value不为空的担保物信息的个数
    	$.each(directProjectInfo.collateral,function(n,v){
            if (v != '') {
                if (!!v) {
                    colTrObj.append(v);
                    //colTrObj.append("<td width='115' class='f-fwb'>"+n+"</td><td width='335'>"+v+"</td>");
                    i = i + 1;
                }
                if (i % 2 == 0) {
                    $("#j-collateral-detail").append(colTrObj);
                    colTrObj = $("<div>");
                }
            } else {
                $('#j-collateral-detailWrap').hide()
                if ($('#j-collateralImg').data('size') == 0) {
                    $('#j-selfInfoMsg').hide()
                }
            }

        });
    	
    	if(i%2!=0){
    		$("#j-collateral-detail").append(colTrObj);
			colTrObj= $("<div>");
    	}
    }



    //倒计时扩展
    $.fn.extend({
        timeCountdown: function (timestamp, callback) {
            this.each(function () {
                var _this = $(this),
                    time = ((_this.data('time') || timestamp) - env.serverDate) / 1000; // cc change a little

                if (time > 0) {
                    _this.text(getCountTime(time));
                    var timer = setInterval(function () {
                        _this.text(getCountTime(time - 1));
                        time = time - 1;

                        if (time <= 0) {
                            clearInterval(timer);
                            callback();
                        }
                    }, 1000);
                }else {
                    callback();
                }
            });
            function getCountTime(seconds) {
                if (seconds < 0) {
                    return 0;
                }
                var timeArray = [
                    Math.floor(seconds / 86400) + '' || '00',
                    seconds >= 86400 ? Math.floor(seconds % 86400 / 3600) :
                    Math.floor(seconds / 3600) || '00',
                    Math.floor((seconds / 3600) % 1 * 60) || '00',
                    Math.floor(seconds % 60) || '00'
                ];
                var timeString;
                if(timeArray[0] > 0){
                    timeString = '剩余时间'+timeArray[0] + '天';
                } else {
                    for (var i = 0; i < timeArray.length; i++) {
                        if (timeArray[i] < 10 && timeArray[i] !== '00') {
                            timeArray[i] = '0' + timeArray[i];
                        }
                    }
                    timeArray.shift();
                    timeString = '剩余时间'+timeArray.join(":");
                }

                return timeString;
            }
        }
    });

         //项目详情-项目预告时间
        var  countDown=$('#j-choise-countDown'),
        //js转化之后会比后台返回的时间多14个小时
        countDownHours = new Date(countDown.attr('data-saleendtime')).getHours() - 14,
        originalTime = new Date(countDown.attr('data-saleendtime')).setHours(countDownHours),
            projectCategory=countDown.attr('data-projectCategory'),
        cdTimestamp=new Date(originalTime).getTime();
        countDown.timeCountdown(cdTimestamp,function(){
            if(projectCategory==2){
                countDown.text('剩余时间：00:00:00')
            }else{
                countDown.hide();
            }


        });


//直投项目车辆信息点击放大图片
    var personalInfo=$(".j-personalInfo-img"),
        lilen=personalInfo.find("li").length;
    personalInfo.on("click","li img",function(){
        var bigImg = $(this).attr("url"),
            i=$(this).parents("li").index()+ 1,
            prevDisabled="",nextDisabled="";
        $(".ui-popup").remove();
        xShade("hide");
        if(i===lilen){
            nextDisabled="z-disabled";
        }else if(i===1){
            prevDisabled="z-disabled";
        }
        imgDialog= dialog({
            content:"<img src='"+bigImg+"' class='u-personalInfo-bigImg' id='j-bigImg' style='display:block;margin:0 auto;' />"
            +"<button id='j-imgbtn-prev' data-direction='prev' class='u-imgbtn u-imgbtn-prev "+prevDisabled+"'></button>"+
            "<button id='j-imgbtn-next' data-direction='next' class='u-imgbtn u-imgbtn-next "+nextDisabled+"'></button>"+
            "<input type='hidden' id='j-curIndex' data-index='"+i+"'>"
            +"<i class='u-imgbtn-close f-fs14' id='j-imgbtn-close'></i>",
            width:425,
            fixed:true
        });
        if(lilen<2){
            $('#j-imgbtn-prev').hide();
            $('#j-imgbtn-next').hide()
        }
        xShade("show");
        imgDialog.show();
        //图片关闭
        $("#j-imgbtn-close").on("click",function(){
            imgDialog.close().remove();
            xShade("hide");
        });

    });
    //点击背景隐藏
    $("body").on("click", ".u-shade", function(){
        $(".ui-popup").remove();
        xShade("hide");
    });
    //图片翻页
    $("body").on("click",".u-imgbtn",function(){

        var direction=$(this).data("direction"),
            curIndex=$("#j-curIndex").data("index"),
            bigImg = $("#j-bigImg").attr("src"),
            lisum=personalInfo.find("li").length,
            newIndex = 0;
        switch(direction){
                case "next":
                    newIndex=curIndex+1;
                    break;
                case "prev":
                    newIndex=curIndex-1;
                    break;
                default:
            }

        if(newIndex>lisum){
            newIndex=lisum;
        }else if(newIndex<1){
            newIndex=1;
        }
        if(newIndex!==curIndex){
            $("#j-curIndex").data("index",newIndex);
            bigImg=$(".j-personalInfo-img").find("li").eq(newIndex-1).find("img").attr("url");
            //bigImg = bigImg.replace("0"+curIndex,"0"+newIndex);
            $("#j-bigImg").attr("src",bigImg);
        }

    });

//p2p项目详情页弹窗优化
var userMenu=$('#j-user-menu'),
    shadeGray=$('#j-shade-gray');
    userMenu.on('mouseenter',function(){
        $('#j-investment-schedule').css("z-index","10");
        shadeGray.animate({opacity:0.6},300);
    }).on('mouseleave',function(){
        $('#j-investment-schedule').css("z-index","11");
        shadeGray.animate({opacity:0.0},300);
    })
})();
//企业信息图片放大
//由于时间紧急先实现放大功能，待有时间重新完善这里的代码。
(function(){
    var carInfo=$(".j-carInfo-img"),
        carlilen=carInfo.find("li").length;
    carInfo.on("click","li img",function(){
        var bigImg = $(this).attr("url"),
            i=$(this).parents("li").index()+ 1,
            prevDisabled="",nextDisabled="";
        $(".ui-popup").remove();
        xShade("hide");
        if(i===carlilen){
            nextDisabled="z-disabled";
        }else if(i===1){
            prevDisabled="z-disabled";
        }
        imgDialog= dialog({
            content:"<img src='"+bigImg+"' class='u-carInfo-bigImg j-bigImg' style='display:block;margin:0 auto;' />"
            +"<button id='j-imgbtn-prev' data-direction='prev' class='u-imgbtn u-imgbtn-prev "+prevDisabled+"'></button>"+
            "<button id='j-imgbtn-next' data-direction='next' class='u-imgbtn u-imgbtn-next "+nextDisabled+"'></button>"+
            "<input type='hidden' class='j-curIndex' data-index='"+i+"'>"
            +"<i class='u-imgbtn-close f-fs14' id='j-imgbtn-close'></i>",
            width:425,
            fixed:true
        });
        if(carlilen<2){
            $('#j-imgbtn-prev').hide();
            $('#j-imgbtn-next').hide()
        }
        xShade("show");
        imgDialog.show();
        //图片关闭
        $("#j-imgbtn-close").on("click",function(){
            imgDialog.close().remove();
            xShade("hide");
        });

    });

    //关闭按钮
    $('.j-closeBtn').on('click',function () {
        $(this).parent().hide()
        $('#j-paymentCipherBlock-cover').hide()
    })
    //点击背景隐藏
    $("body").on("click", ".u-shade", function(){
        $(".ui-popup").remove();
        xShade("hide");
    });
    //图片翻页
    $("body").on("click",".u-imgbtn",function(){

        var direction=$(this).data("direction"),
            curIndex=$(".j-curIndex").data("index"),
            bigImg = $(".j-bigImg").attr("src"),
            lisum=carInfo.find("li").length,
            newIndex = 0;
        switch(direction){
            case "next":
                newIndex=curIndex+1;
                break;
            case "prev":
                newIndex=curIndex-1;
                break;
            default:
        }

        if(newIndex>lisum){
            newIndex=lisum;
        }else if(newIndex<1){
            newIndex=1;
        }
        if(newIndex!==curIndex){
            $(".j-curIndex").data("index",newIndex);
            bigImg=$(".j-carInfo-img").find("li").eq(newIndex-1).find("img").attr("url");
            //bigImg = bigImg.replace("0"+curIndex,"0"+newIndex);
            $(".j-bigImg").attr("src",bigImg);
        }

    });
})();

//还款计划图片放大
(function(){
    var collectInfo=$(".j-collectInfo-img"),
        collectlilen=collectInfo.find("li").length;
    collectInfo.on("click","li img",function(){
        var bigImg = $(this).attr("url"),
            i=$(this).parents("li").index()+ 1,
            prevDisabled="",nextDisabled="";
        $(".ui-popup").remove();
        xShade("hide");
        if(i===collectlilen){
            nextDisabled="z-disabled";
        }else if(i===1){
            prevDisabled="z-disabled";
        }
        imgDialog= dialog({
            content:"<img src='"+bigImg+"' class='u-carInfo-bigImg ' id='j-bigImg' style='display:block;margin:0 auto;' />"
            +"<button id='j-imgbtn-prev' data-direction='prev' class='u-imgbtn u-imgbtn-prev "+prevDisabled+"'></button>"+
            "<button id='j-imgbtn-next' data-direction='next' class='u-imgbtn u-imgbtn-next "+nextDisabled+"'></button>"+
            "<input type='hidden' id='j-collectIndex' data-index='"+i+"'>"
            +"<i class='u-imgbtn-close f-fs14' id='j-imgbtn-close'></i>",
            width:425,
            fixed:true
        });
        if(collectlilen<2){
            $('#j-imgbtn-prev').hide();
            $('#j-imgbtn-next').hide()
        }
        xShade("show");
        imgDialog.show();
        //图片关闭
        $("#j-imgbtn-close").on("click",function(){
            imgDialog.close().remove();
            xShade("hide");
        });

    });
    //点击背景隐藏
    $("body").on("click", ".u-shade", function(){
        $(".ui-popup").remove();
        xShade("hide");
    });
    //图片翻页
    $("body").on("click",".u-imgbtn",function(){

        var direction=$(this).data("direction"),
            curIndex=$("#j-collectIndex").data("index"),
            bigImg = $("#j-bigImg").attr("src"),
            lisum=collectInfo.find("li").length,
            newIndex = 0;
        switch(direction){
            case "next":
                newIndex=curIndex+1;
                break;
            case "prev":
                newIndex=curIndex-1;
                break;
            default:
        }

        if(newIndex>lisum){
            newIndex=lisum;
        }else if(newIndex<1){
            newIndex=1;
        }
        if(newIndex!==curIndex){
            $("#j-collectIndex").data("index",newIndex);
            bigImg=$(".j-collectInfo-img").find("li").eq(newIndex-1).find("img").attr("url");
            //bigImg = bigImg.replace("0"+curIndex,"0"+newIndex);
            $("#j-bigImg").attr("src",bigImg);
        }

    });
})();

/* 直投二期 催收历程 cc*/
(function () {

    // 处理催收的箭头和显隐
    var $switcherArrow = $('#j-switcher-arrow'),
        $allResult = $('#j-all-result')

    $('#j-prev-collectList-switcher').on('click', function () {
        $switcherArrow.toggleClass('z-rotate')
        $allResult.toggleClass('z-active')
    })

    $('.u-prev-list-item').on('click', '.u-collectList-info', function () {
        $('.u-collectList-info').not($(this)).next('.j-collectList-main-wrap').removeClass('z-active')
        $(this).next('.j-collectList-main-wrap').toggleClass('z-active')


        // $(this).next('.j-collectList-main-wrap').toggleClass('z-active')
        // $('.j-collectList-main-wrap').not($(this)).removeClass('z-active')

    })
})();

/* 块头二期 */
(function(){
    //列表滚动
    $.fn.scrollUserList = function (config) {
        this.each(function () {
            var _this = $(this),
                scrollSize = _this.find("li").length;
            if (scrollSize > config.size) {
                setInterval(function () {
                    var scrollItems = _this.find("li:visible");
                    _this.animate({marginTop: config.height}, 700, function () {
                        scrollItems.eq(0).appendTo(_this);
                        _this.css("margin-top", 0);
                    });
                }, 3000);
            }
        })
    };

    $("#j-historyLotteryList").scrollUserList({
        size: 3,
        height: -75,
        length: 1
    })
})();
