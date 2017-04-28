/*global define,$,alert,console,environment,env,log*/
define(['template'],function (require, exports, module) {
    'use strict';
    //API request handler
    var base={};
    var path=environment.globalPath;
    var investType='';
    base.getAPI = function (options) {
        if (typeof(options) === 'object') {
            var xToken=$('#xToken').val(),
                data={};
            if(options.data){
                if($.isArray(options.data)){
                    $.each(options.data, function (index, item) {
                        data[item.name]=item.value;
                    });
                }else{
                    data=options.data;
                }
            }
            data.xToken=xToken;

            $.ajax({
                url: options.url,
                data:data,
                type: options.type||'POST',
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': options.version ||  '1.0.0'
                },
                dataType: options.dataType|| 'json',
                success: function(data){
                    if(typeof(options.ids)==='object'){
                        options.callback(data,options.ids);
                    }else{
                        options.callback(data);
                    }
                }
            });
        } else {
            throw new Error('options must be an object');
        }
    };


  //设置屏幕的高度适配
    $('#j-screenHeight').css('height',$(window).height())
    //提交订单
    var $orderTitle=$('#j-order-title');
    var isTrueName=$('#j-isTrueName').attr('isTrueName');
    if($orderTitle){
        var orderData= {
            'pid':$orderTitle.attr('pid'),
            'investAmount':$orderTitle.attr('investamount')
        };

        if(orderData.pid&&orderData.investAmount&&isTrueName==='true'){
            base.getAPI({
                url:environment.globalPath +'/security/order/investment',
                version:'1.0.0',
                data:orderData,
                callback: function (data) {
                    if(data.success){
                        //判断项目类型
                        investType=data.result.investType;
                        //项目期数以及名称处理
                      var  nameArray=data.result.name.split('期');
                        data.result.preName=nameArray[0]+'期';
                        data.result.lastName=nameArray[1];
                        renderOrderInfo(data.result,template);
                        if(data.result.coupons.length!=0){
                            $('.j-choice').show();
                        }
                    }else{
                        alert(data.resultCodes[0].msg);
                        location.href=path+'/products/m/list-all-all-1.html';
                    }

                    //服务协议阻止提交
                    $("#j-agree").change(function () {
                        var checked = $(this).is(":checked"),
                            btn = $("#j-orderSubmit");
                        if (!checked) {
                            btn.addClass("z-disabled").attr("disabled", "disabled");
                        } else {
                            btn.removeClass("z-disabled").removeAttr("disabled");
                        }
                    });
                }


            });
        }
        var template=require('template');
        //创建订单
        var $orderSubmit=$('#j-orderSubmit');
        $orderSubmit.on('click', function () {
            var $orderSubmit=$(this),
                couponId=$orderSubmit.attr('couponid'),
                reqData={
                    //profitCouponNo:'', 需要选择后读取
                    projectId:orderData.pid,
                    investAmount:orderData.investAmount
                };

            if(couponId){
                reqData.profitCouponNo=couponId;
            }

            if(orderData.pid&&orderData.investAmount){
                base.getAPI({
                    url:path +'/security/order/createOrder',
                    data:reqData,
                    callback: function (data) {

                        if(data.success){
                            //需要做重复提交阻止2
                            location.href=$orderSubmit.attr('url')+data.result.id;
                        }else{

                            alert(data.resultCodes[0].msg);
                            location.href=path+'/products/m/list-all-all-1.html';

                        }
                    }
                });
            }else{
                return false;
            }
        });
    }
    function renderOrderInfo(data,template){

        var infoHtml=template('j-orderInfo-tpl',{orderInfo:data}),
            couponHtml=template('j-profitCoupon-tpl',data);
        $('#j-rate').val(data.annualizedRate);
        $('#j-orderInfo').html(infoHtml);
        $('#j-profitCoupon-list').html(couponHtml).css('max-height','360px');

        //替代原生toFixed
        _.mixin({
            toFixed: function (number,size) {
                var n=number-0||0,
                    s=size-0||2;
                return _.round(n,s).toFixed(s);
            }
        },{'chain':false});
        //加载请求协议
        investPreview()
        //关闭按钮
        $('.j-btn-close ').click(function () {
            $('.j-cover').hide();
            $('.u-cashCoupon-popup').hide();
        });
        //收益券弹窗
        $('.j-choice').on('click', function () {
            $('body').css('overflow','hidden');
            $('.u-cashCoupon-popup').show();
            $('.j-cover').show();
        });

        var annualizedRateNum=data.annualizedRate,
            expectAmountNum=data.expectAmount,
            profitType=data.profitType,
            investAmount=data.investAmount,
            earningsDays=data.earningsDays,
            investType=data.investType,
            firstDebtEarningDays = data.firstDebtEarningDays,//第一期债权本息收益天数
            firstRealEarningDays = data.firstRealEarningDays,//第一期本息实际收益天数
            earningPeriod = data.earningPeriod;//目前投资收益期数

        $('.u-cashCoupon>li').on('click',function(){
            $(this).addClass('j-certificate')
                .siblings().removeClass('j-certificate');
            var index=$(this).index();
            //判断现金券是否可以使用
            if($(this).hasClass('z-disabled')){
                return false;
            }

            if(!index){
                $('.j-cover').hide();
                $('.u-cashCoupon-popup').hide();
                $('.j-choice>input').attr('value','不使用收益券');
                $('#j-orderInfo').find(".j-profit").text( '¥ '+expectAmountNum);
                $orderSubmit.attr('couponid','');
            }
            else{
                var currentRate=Number(annualizedRateNum)+Number($(this).attr('couponamount'));
                var  couponValidityNum=data.coupons[index-1].couponValidity,
                    amount=data.coupons[index-1].amount;

                $orderSubmit.attr('couponid',$(this).attr('couponcode'));

                $('.j-choice>input').attr('value','+'+amount+'%'+'     '+couponValidityNum);

                var earnings=getEarnings(investAmount, currentRate,investType,earningsDays,profitType,firstDebtEarningDays,firstRealEarningDays,earningPeriod);
                var xEarnings=commafy(earnings);
                $('#j-orderInfo').find(".j-profit").text( '¥ '+xEarnings);
                $('.j-cover').hide();
                $('.u-cashCoupon-popup').hide();
            }
        });
    }

    /**
     * 获得收益
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     * @param profitType 还款方式
     */
    function getEarnings(amount, annualizedRate,investType, earningsDays, profitType,firstRealEarningDays,firstDebtEarningDays,earningPeriod) {
        var earnings = 0.00;
        switch (profitType) {
            case "monthly_paid": //按日计息
                earnings = getMonthlyPaidEarnings(amount, annualizedRate,investType, earningsDays,earningPeriod);
                break;
            case "once_paid": //一次性还本付息
                earnings = getMonthlyPaidEarnings(amount, annualizedRate,investType, earningsDays,earningPeriod);
                break;
            case "principal_average": //等额本金
                break;
            case "all_average": //等额本息
                break;
            case "avg_principal": //等本等息
                earnings =  getAvgPrincipalInterestEarnings(amount, annualizedRate,investType,firstRealEarningDays,firstDebtEarningDays,earningPeriod);
                break;
        }
        return earnings;
    }

    /**
     * 按日计息
     * @param amount 投资金额
     * @param annualizedRate 年化利率
     * @param earningsDays 收益天数
     */
    function getMonthlyPaidEarnings(amount, annualizedRate,investType, earningsDays,earningPeriod) {
        var interest = _(annualizedRate / 36000).toFixed(10);
        if(investType==2){
            var p2pearnings = _(amount * interest).toFixed(10);
            return _(p2pearnings * earningPeriod).toFixed(2);
        }else{
            var earnings = _(amount * interest).toFixed(2);
            return _(earnings * earningsDays).toFixed(2);
        }

    }

    /**
     * 等本等息
     * @param amount 投资额
     * @param annualizedRate  年化收益
     * @param firstRealEarningDays    第一期当前实际收益天数
     * @param firstDebtEarningDays    第一期债权收益天数
     * @param earningPeriod    收益期数
     * @returns
     */
    function getAvgPrincipalInterestEarnings(amount, annualizedRate,investType,firstRealEarningDays,firstDebtEarningDays,earningPeriod){
        var monthlyRate = _(annualizedRate /1200).toFixed(10);
        var interest = 0;
        if(investType==2){
            var p2pmonthlyInterest =( monthlyRate * amount).toFixed(10);
            interest=(p2pmonthlyInterest*earningPeriod).toFixed(2);
            return interest;
        }else{

            //第一期按照当月实际收益天数计算收益
            if(earningPeriod >0){
                var monthlyInterest = monthlyRate * amount ;
                interest = _(Number(_(monthlyInterest * (firstRealEarningDays / firstDebtEarningDays)).toFixed(2)) + Number(_(monthlyInterest).toFixed(2) * (earningPeriod-1))).toFixed(2);
                //console.info("total:"+interest)
                return interest;
            }
        }
    }


    /**
     *金额格式化
     * @param amount{Number} 需要格式化的金额
     * @param format{String} 格式化类型，split=逗号分割的整数
     */
    template.helper('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return commafy(amount);
                break;
        }

        // 用于给数字添加千分号
        function commafy(num) {
            num = num + "";
            var re = /(-?\d+)(\d{3})/;
            while (re.test(num)) {
                num = num.replace(re, "$1,$2");
            }
            return num;
        }
    });
    // 用于给数字添加千分号
    function commafy(num) {
        num = num + "";
        var re = /(-?\d+)(\d{3})/;
        while (re.test(num)) {
            num = num.replace(re, "$1,$2");
        }
        return num;
    }

function investPreview(){
    //债权协议
    var $preview=$('#j-previewForm'),
        $previewBtn=$('#j-contract-btn'),
        $contract=$('#j-contract');

    $previewBtn.on('click', function () {
        if($('#j-goback').length){
            $contract.show();
            return false;
        }
        var formArray=$preview.serializeArray(),
            url=$preview.attr('url'),
            xToken=$('#xToken').val();
        if(investType==2){
            url=path+'/security/transaction/p2pContract/preview';
        }
        formArray.push({name:'xToken',value:xToken});
        $.ajax({
            url:url,
            type:'POST',
            data:formArray,
            success: function (data) {
                $contract.html(data).append('<div class="u-contract-back" id="j-goback">返回</div>');
                $('#j-goback').on('click', function () {
                    $contract.hide();
                });
            }
        });
    });
}
 
});

