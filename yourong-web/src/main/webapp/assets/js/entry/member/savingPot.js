/**
 * @entry 我的存钱罐
 * @url /memberBalance/savingPot
 * @description 只在我的存钱罐本页用到，在中间部分，近一周收益，近一月收益，
 * 鼠标移上去之后展示列表提示框
 */

import 'common/member'
import 'module/jquery.extend'
import { Dialog } from 'module/cube'

/**
 * /memberBalance/savingPot
 * 只在我的存钱罐本页用到，在中间部分，近一周收益，近一月收益，
 * 鼠标移上去之后展示列表提示框
 */

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
var $profitWeekly=$('#j-profit-weekly'),
    $profitMonthly=$('#j-profit-monthly');
if($profitWeekly.length){
    columnChart(".j-column-chart");
    var $popupWeekly=$('#j-show-weekly'),
        $popupMonthly=$('#j-show-monthly');
    var weeklySize= $popupWeekly.find('li').length,
        weeklyHeight = 50 + (weeklySize < 7 ? weeklySize - 1 : 6) * 40,
        monthlySize=$popupMonthly.find('li').length,
        monthlyHeight=50 + (monthlySize < 7 ? monthlySize - 1 : 6) * 40;

    if(weeklySize!==0){
        $profitWeekly.on('mouseover', function () {
            $(this).popupPosition({
                target:$popupWeekly,
                height:weeklyHeight,
                width:477,
                offset:15
            });
        });
        $profitWeekly.on('mouseleave', function () {
            var timer = setTimeout(function(){
                $popupWeekly.hide();
            },300);
            $popupWeekly.on('mouseenter',function(){
                clearTimeout(timer);
            });
        })
    }else{
        $profitWeekly.find('i').hide();
    }
    if(monthlySize!==0){
        $profitMonthly.on('mouseover', function () {
            $(this).popupPosition({
                target:$popupMonthly,
                height:monthlyHeight,
                width:477,
                offset:15
            });
        });
        $profitMonthly.on('mouseleave', function () {
            var timer = setTimeout(function(){
                $popupMonthly.hide();
            },300);
            $popupMonthly.on('mouseenter',function(){
                clearTimeout(timer);
            });
        })
    }else{
        $profitMonthly.find('i').hide();
    }

}

// 新浪支付的小banner
//var sinapaybanner=$('.j-sinapay-banner');
//setTimeout("sinapaybanner.slideUp();",5000);

/**
 * 充值  /memberBalance/rechargePage
 */









/**
 * 提现  /memberBalance/rechargePage
 */
