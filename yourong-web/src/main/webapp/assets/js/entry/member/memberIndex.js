/**
 * @entry 账户概览
 * @url   /member/home
 */
import 'common/member'
import 'module/jquery.extend'
import {Util} from 'common/util'
import {Dialog,Checkin} from 'module/cube'
import {template} from 'module/template'

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

/*global $,window,formValid,environment,Highcharts,dialog,highchartsJson,xShade*/
//加载交易收益日历
if ($("#j-transaction-tab").length) {
    renderProfitCal('#j-transaction-tab', 36);
    loadTransactionInterestData();
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

    Util.post(config);
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
    $('#j-profit-cal').scrollLeft(((curYear-2015)*12 - 2 + curMonth) * 104);
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
        interval=(curDate.getFullYear()-2015)*12;
    table.find("tr:eq(" + (curDate.getDate() + 1) + ") td:eq(" + (curDate.getMonth() + 2+interval) + ")").addClass("z-today");
    curDate = new Date(curDate.getFullYear(), curDate.getMonth(), curDate.getDate());

    //log(data.resultList)

    $.each(data.resultList, function (i, obj) {
        var month = obj.month,
            day = obj.day,
            num = obj.num, // 正常项目
            noNum = obj.noNum || 0, // 正常未还
            overNum = obj.overNum || 0, // 逾期已还
            overNoPayNum = obj.overNoPayNum || 0, // 逾期未还
            preNum = obj.preNum || 0, // 提前还款

            year = obj.year,
            totalPayablePrincipal = obj.totalPayablePrincipal,
            totalPayableInterest = obj.totalPayableInterest;
        var mm = month;
        if (num > 0 || noNum > 0 || overNum > 0 || overNoPayNum > 0 || preNum > 0) {
            //月份在界面设置不是有序的，所以这里需要特别处理
            var curYear = new Date().getFullYear();

            if(year===curYear){
                mm=month+(curYear-2015)*12+2;
            }else{
                mm = month + (year - 2015) * 12 + 2;
            }
            //是否已还款  mark=1空心
            // 如果全是已经还款的，那么就显示一个实心圆，如果出现一个未还款还款的，就显示空心圆
            var calDate = new Date(obj.payDate).getTime(),
                mark = 1;

            // if (calDate < curDate) {
            //     mark = 0;
            // }
            if( (num > 0 || preNum > 0 || overNum > 0) && (noNum == 0 && overNoPayNum == 0) ){
                mark = 0
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
            Util.post({
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

        function renderCalList(data) {
            var tplData = data.result,
                dateArray = date.split('-');

            tplData.date = date;
            tplData.month = dateArray[1];
            tplData.day = dateArray[2];

            if(typeof(template)!=='function'){
                return false;
            }

            var transactionInterestList = tplData.calendarTransactionInterestDetailDto.transactionInterestList || [], // 期数信息
                topay = 0,  // 待还款
                paied = 0  // 已还款

            $.each(transactionInterestList, function (index, val) {
                if(val.status != 1){
                    topay += 1
                }

                if(val.status == 1){
                    paied += 1
                }
            })

            tplData.calendarTransactionInterestDetailDto.topay = topay
            tplData.calendarTransactionInterestDetailDto.paied = paied

            $('#j-topay').text(topay)
            $('#j-paied').text(paied)

            // 防止后端 detailList为null的时候 js报错,以及 修正arttemplate报错
            var detailList = tplData.calendarTransactionInterestDetailDto.transactionInterestList || []
            tplData.calendarTransactionInterestDetailDto.transactionInterestList = detailList
            var size = detailList.length;

            var html = template('j-cal-tpl', tplData),
                $calDetail = $('#j-cal-detail');
            $calDetail.html(html);


            //多了滚动条之后高度会有偏差
            var scrollHeight=0
            if(size > 6){
                size = 6
                scrollHeight = 22
            }

            var calHeight = 146 + size * 33 + scrollHeight;
            //log('calHeight: aa',calHeight)
            _this.popupPosition({
                target: $calDetail,
                width: 620,
                height: calHeight
            })
            $calTable.on('mouseenter','td', function () {
                if(!$(this).hasClass('j-cal-tips')){
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

        var url = ''
        switch(reqType){
            case 'interest':
                url = '/memberBalance/getTransactionInterestDetailForMember'
                break
            case 'principal':

                if(principalType === "transPrincipal"){
                    url = '/memberBalance/getTransactionDetailForMember'
                }else{
                    if(type==='hadTransferValue'){
                        url = '/memberBalance/getTransactionInterestDetailForMemberTransfer'
                    }else{
                        url = '/memberBalance/getTransactionPrincipalDetailForMember'
                    }
                }
                break
            case 'trPrincipal':
                    url = url='/memberBalance/getTransferDetailForMember'

        }

        if (!incomeData) {
            if (reqType == 'trPrincipal') {
                Util.post({
                    url: url,
                    data: {"type": type},
                    type: 'GET',
                    callback: function (data) {
                        if (data.success && data.result) {
                            renderIncomeList(data);
                            _this.data('incomeData', data);
                        }
                    }
                });
            } else {
                Util.post({
                    url: url,
                    data: {"type": type},
                    callback: function (data) {
                        if (data.success && data.result) {
                            renderIncomeList(data);
                            _this.data('incomeData', data);
                        }
                    }
                });
            }
        } else {
            renderIncomeList(incomeData);
        }

        function renderIncomeList(data) {
            var _incomeData = data;
            _incomeData.title = title;
            _incomeData.url = targetUrl;
            _incomeData.reqType = reqType;

            if(typeof(template)!=='function'){
                return false;
            }

            var html = template('j-transaction-interest-detail-tpl', _incomeData),
                $interestDetail = $('#j-transaction-interest-detail');
            var profitHeight = 145 + (data.result.length - 1) * 28;

            $interestDetail.html(html);
            _this.popupPosition({
                target: $interestDetail,
                height: profitHeight,
                width: 340,
                offset: 15
            });
            _this.on('mouseleave', function () {
                var timer = setTimeout(function(){
                    $interestDetail.hide();
                },300);
                $interestDetail.on('mouseenter',function(){
                    clearTimeout(timer);
                });
            })

        }

    }).on("mouseleave", ".j-popup-profit", function (e) {
        $('.ui-popup').remove();
    });
}


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

    point = circle;

    if (point !== "") {
        return "<i>" + point + "</i>";
    }
    return "";
}

function exchangeCouponByTemplateId(templateId) {
    var xToken = $("#xToken").attr("value");
    Util.post({
        url: environment.globalPath + "/coupon/exchange",
        data: {
            'couponTemplateId': templateId,
            'num': 1,
            'xToken': xToken
        },
        callback: function(data) {
            if (data.success) {
                var couponName = "查看我的现金券!",
                    url = "/coupon/couponList";
                if (data.result != null && data.result.couponType != null) {
                    if (data.result.couponType == 2) {
                        couponName = "查看我的收益券";
                        url = "/coupon/profitCouponList";
                    }
                }
                $('body').find('.u-shade').click();
                $('.j-cashCouponSlideBtn-show').hide();
                Dialog.show({
                    content: "优惠券兑换成功，" + "<a class='u-dialog-link' href='" + environment.globalPath + url + "' >" + couponName + "</a>",
                    type: "success", //success,warn,error,info
                    callback: function() {
                        window.location.href = environment.globalPath + "/coupon/reputation";
                    } //确认按钮回调
                });
            } else {
                var $repButton = $('#j-cash-button');
                if (data.result.couponType == 2) {
                    $repButton = $('#j-profit-button');
                }
                $repButton.removeClass('z-disabled').removeProp('disabled');
                Dialog.show({
                    content: "领用失败！",
                    type: "error"
                });
            }
        }
    });
}

//跳转到投资记录页面查询当天的记录
$("#j-cal-detail").on("click",".j-jump-investment",function(){
    var date = $(this).data("day"),//查询的日期
        reqdata = {"searchDate":date},//请求的参数
        url = $(this).data("url");//请求地址
    window.open(url+"?searchDate="+date,"_blank");
});

//风险评测提示
const evaluated = $('#j-evaluated').val()
const hasSkipEvaluation = Util.storage('skipEvaluation')
const skipEvaluation = ()=> {
    Util.storage('skipEvaluation', true)
}
// log({evaluated: +evaluated, hasSkipEvaluation: !!hasSkipEvaluation})

const showEvaluateTips = ()=> {
    // console.log(!+evaluated && !hasSkipEvaluation)
    if (!+evaluated && !hasSkipEvaluation) {

        Dialog.show({
            content: '测测你的投资风险承受能力吧',
            okValue: '去测评',
            cancelValue: '跳过',
            callback: ()=> {
                location.href = '/member/questionnaire'
            },
            cancel: skipEvaluation,
            close: skipEvaluation
        })
    }
}

//签到引导
Checkin.guide('body')

//签到弹出风险评测
let checkinTag = ''
const $checkinGuide = $('#j-checkin-guide')

if ($checkinGuide.length) {
    //关闭签到后弹出
    $checkinGuide.on('click', 'span,button', function () {
        checkinTag = $(this)[0].tagName

        if (checkinTag === 'SPAN') {
            showEvaluateTips()
        }
    })

    //签到成功后弹出
    $('body').on('click', '.u-shade', function () {

        if (checkinTag === 'BUTTON') {
            showEvaluateTips()
        }
    })

} else {
    showEvaluateTips()
}


//账户概况查看人气值（清空提示）链接显示情况
let $checkBtn = $('#j-valueCount-check')
let userId = $checkBtn.data('userid') + ''
let userIdLatter = userId.substr(7, 11)
let checkCount = +Util.storage(`checkCount_${userIdLatter}`)
let currentDate = Util.format(+environment.serverDate, 'yyyy-MM')
let checkDate = ''

if ($checkBtn.length) {
    if (checkCount) {
        checkDate = Util.format(checkCount, 'yyyy-MM')
        if (checkDate !== currentDate) {
            $checkBtn.show()
        }
    } else {
        $checkBtn.show()
    }
    $checkBtn.on("click", "a", function () {
        Util.storage(`checkCount_${userIdLatter}`, Date.now())
        $(this).parent().remove()
    })
}
