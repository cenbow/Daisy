/*global define,$*/
/*global define,require,$,environment,console,env,globalPath,log,fz*/
define(['base', 'xjs/modules/effects','vue'], function (require, exports, module) {
    'use strict'
    var effects = require('xjs/modules/effects')
    var base = require('base');
    Vue.filter('formatName', function (value) {
        return value.split('期')[0]
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
    Vue.filter('amountFloor',function(amount){
        return parseInt(amount)
    })
    Vue.filter('amountFormat', function (amount, format) {

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
    var pid = $('#j-pid').val();
    $('#j-detail-tab').on('click', 'span', function () {
        var _this = $(this);
        _this.addClass('z-current').siblings().removeClass('z-current');
        $('#j-detail-frame').prop('src', _this.data('url'));
    });
      
    new Vue({
        el: '#projectDetail',
        data: {
            projectDetailData: {},
            projectData: {},
            isCurrent: 0
        },
        created: function () {
            var self = this
            base.getAPI({
                url: environment.globalPath + '/project/queryProjectById',
                data: {pid: pid},
                version: '1.0.2',
                callback: function (data) {
                    if (data.success) {
                        self.projectData = data.result
                        self.projectDetailData = data.result.project
                        var customerData = data.result.project;
                        var encryptionId=$('#j-pid').attr('encryptionId')||'';
                        self.showNewCustomerButton(customerData.investType, customerData.isNoviceProject, data.result.isNewCustomer, customerData.status, customerData.availableBalance, customerData.notice)
                        if (data.result.project.investType == 2) {
                            $('#j-detail-frame').prop('src', environment.globalPath + '/project/p2p-detail-' + pid + '.html?isMsite=Y'+'&&encryptionId='+encryptionId);
                            if(data.result.project.status === 30||data.result.project.status === 40){
                                setTimeout(function () {
                                    var timer=Math.floor($('.j-project-noticeTime').attr('data-time')/86400);
                                    if(timer>0){
                                        $('.j-project-noticeTime').text(timer+'天')
                                    }else {
                                        effects.projectCountdown()
                                    }
                                },100)
                            }



                        } else {
                            $('#j-detail-frame').prop('src', environment.globalPath + '/project/detail-' + pid + '.html');
                        }
                    }

                }
            })

            //返回按钮链接修改
            if (document.referrer.indexOf('list-all-all') > -1) {
                $('#j-detail-back').prop('href', document.referrer);
            }
        },
        methods: {
            /*
            判断项目状态，来判断投资按钮的文案的是否可以点击
            * investType==项目类型--2=>直投；1=>债权
            * */
            showNewCustomerButton: function (investType, isNoviceProject, isNewCustomer, status, availableBalance, notice) {
                var $jInvestmentbtn = $('#j-investment-btn'),
                    $jInvestmentStatus=$('#j-investmentStatus');
                if (notice == true) {
                    $jInvestmentStatus.hide()
                    $('#j-noticeProject').show().css('line-height','50px');
                    setTimeout(function () {
                        effects.projectCountdown()
                    },100)
                      return false
                }
                if (investType == 2) {
                    if(status === 80){
                        $jInvestmentbtn.css('line-height','50px');
                        if(+availableBalance === 0){
                            $jInvestmentbtn.addClass('z-disable').prop('href', 'javascript:void(0)');
                            $jInvestmentStatus.text('已投满')
                        }else{
                            $jInvestmentbtn.addClass('z-disable').prop('href', 'javascript:void(0)');
                            $jInvestmentStatus.text('已截止')
                        }
                        return false
                    }
                    if (status === 30) {
                        if (+availableBalance === 0) {
                            $jInvestmentbtn.addClass('z-disable').prop('href', 'javascript:void(0)').css('line-height','50px');
                            $jInvestmentStatus.text('支付确认中')
                            $('#j-residualTime').hide()
                        }
                        if (isNoviceProject && !isNewCustomer) {
                            $jInvestmentbtn.addClass('z-disable').prop('href', 'javascript:void(0)').css('line-height','50px');
                            $jInvestmentStatus.text('新客项目')
                            $('#j-residualTime').hide()
                        }
                        return false
                    }
                    else if(status === 40){
                            $jInvestmentbtn.addClass('z-disable').prop('href', 'javascript:void(0)');
                            $jInvestmentStatus.text('已暂停')
                        // $('.j-relactTime').show()
                        return false
                        }

                    else {
                        var statusText = {'50': '已投满','51':'履约中','52':'履约中', '60': '已截止', '70': '已还款','81':'已投满','90':'流标'};
                        $jInvestmentbtn.addClass('z-disable').attr('href', 'javascript:void(0)').css('line-height','50px');
                        $jInvestmentStatus.text(statusText[status] || '')
                        return false
                    }
                    
                }
                else if (investType == 1) {
                    if (status === 30) {
                        $jInvestmentbtn.css('line-height','50px')
                        if (+availableBalance === 0) {
                            $jInvestmentbtn.addClass('z-disable').text('支付确认中').prop('href', 'javascript:void(0)');
                        }
                        if (isNoviceProject && !isNewCustomer) {
                            $jInvestmentbtn.addClass('z-disable').text('新客项目').prop('href', 'javascript:void(0)');
                        }
                    } else {
                        var statusText = {'40': '已暂停', '50': '履约中', '60': '履约中', '70': '已还款'};
                        $jInvestmentbtn.addClass('z-disable').text(statusText[status] || '').prop('href', 'javascript:void(0)').css('line-height','50px');
                    }
                }

            },
            //投资按钮赋值,给下一个页面传送title  
            setName: function () {
                var title = $('#j-detail-title').data('title');
                localStorage.setItem('pid_' + pid, title);
            }
        }
    })
})


// define(['zepto','base','template'], function (require, exports, module) {
//     'use strict';
//     var template = require('template'),
//         base = require('base');
//     //项目详情页数据渲染
//     var $productsDetail = $('#j-productsDetail');
//     var pid = $('#j-pid').val();
//     base.getAPI({
//         url: environment.globalPath +'/project/queryProjectById?pid=' + pid,
//         version:'1.0.2',
//         callback: function (data) {
//
//             if (data.success) {
//                 var projectData=data.result.project,
//                 //项目期数以及名称处理
//                     nameArray=projectData.name.split('期');
//                 data.result.project.preName=nameArray[0]+'期';
//                 data.result.project.lastName=nameArray[1];
//                 //项目余额以及百分比计算
//                 var Balance=projectData.availableBalance,
//                     Amount=projectData.totalAmount,
//                     Investment=projectData.totalInvestment;
//                     data.result.project.percentBalance=((1-Balance/Amount)*100).toFixed(2)-0;
//                     data.result.project.availableBalance=parseInt(Balance);
//
//                 // var html = template('j-productsDetail-tpl', projectData);
//                 // $productsDetail.html(html);
//                 showNewCustomerButton(projectData.isNoviceProject,data.result.isNewCustomer,projectData.status,
//                     projectData.availableBalance,projectData.notice);
//                 //投资进度100%时进度条置灰
//                 if(Balance==0){
//                     $('.u-detail-progress').css('background','none');
//                 }
//                 //投资列表笔数显示
//                 var $detailCount=$('#j-detail-count');
//                 if(Investment>99){
//                     $detailCount.text('99+');
//                 }else if(Investment>0){
//                     $detailCount.text(Investment);
//                 }else{
//                     $detailCount.hide();
//                 }
//
//                 //返回按钮链接修改
//                 if(document.referrer.indexOf('list-all-all')>-1){
//                     $('#j-detail-back').prop('href',document.referrer);
//                 }
//             }
//         }
//     });
//
//     function showNewCustomerButton(isNoviceProject,isNewCustomer,status,availableBalance,notice){
//         var $jInvestmentbtn= $('#j-investment-btn');
//         if(status===30){
//             if(availableBalance===0){
//                 $jInvestmentbtn.addClass('z-disable').text('支付确认中').prop('href','javascript:void(0)');
//             }
//             if(isNoviceProject&&!isNewCustomer){
//                 $jInvestmentbtn.addClass('z-disable').text('新客项目').prop('href','javascript:void(0)');
//             }
//         }else{
//             var statusText={'40':'已暂停','50':'履约中','60':'履约中','70':'已还款'};
//             $jInvestmentbtn.addClass('z-disable').text(statusText[status]||'').prop('href','javascript:void(0)');
//         }
//         if(notice>0){
//             $jInvestmentbtn.text('项目预告中').prop('href','javascript:void(0)');
//         }
//     }
//
//     //TAB切换
//     $('#j-detail-tab').on('click', 'span', function () {
//         var _this = $(this);
//         _this.addClass('z-current').siblings().removeClass('z-current');
//         $('#j-detail-frame').prop('src', _this.data('url'));
//     });
//
//     //投资按钮赋值
//     $('#j-investment-btn').on('click', function () {
//        var title=$('#j-detail-title').data('title');
//         var pid=$('#j-pid').val();
//         localStorage.setItem('pid_'+pid,title);
//     });
// });
