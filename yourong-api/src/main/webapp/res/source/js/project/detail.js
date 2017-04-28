//还款计划
define(['zepto','base','template'],function (require, exports, module) {
    var template = require('template'),
        base = require('base');
    var $checkResult=$("#j-check-result"),
        $overdue=$("#j-repaymentPlan-overdue"),
        $repaymentPlanResult=$("#j-repaymentPlan-result");
    var pid = $('#j-pid').val();

    //渲染数据
    base.getAPI({
        url:environment.globalPath +'/project/getRepaymentPlan',
        version:'1.5.0',
        data: {
            pid: pid
        },
        callback: function (data) {
            if (data.success) {

                var result = data.result || {};

                var
                //逾期还款 overFlag=0 正常 overFlag=1 逾期
                    overFlag=result.overFlag,
                 //提前还款  preFlag=0 正常  preFlag=1 提前
                    preFlag=result.preFlag,
                //项目状态
                    status=result.status,
                //还款计划
                    overdue=result.overdue ||{},
                //催收中
                    overdueRepayBiz=result.overdueRepayBiz ||[],
                //催收成果
                    interestList=result.interestList,
                //逾期催收
                    collectList=overdue.collectList ||{},
                    prepaymentRemark=result.prepaymentRemark||'';

                result.overdueRepayBiz = overdueRepayBiz
                result.overdue=overdue

                if (status == 51 || status == 52 || status == 70) {
                    //逾期/提前还款
                    var html = template('j-repaymentPlan-overdue-tpl', result);
                    $overdue.html(html);
                    //催收成果
                    var html = template('j-repaymentPlan-result-tpl', result);
                    $repaymentPlanResult.html(html);
                    setTimeout(function () {
                        if (overdueRepayBiz.length > 0) {

                            $checkResult.removeClass('f-dn')
                        }
                    }, 500)
                } else {
                    $('.u-repayment-tiptext').show()
                }


            }
        }


    })

//点击查看催收成果
    var v=0;
    $checkResult.on('click',function(){
        if(v===0){
            $repaymentPlanResult.removeClass('f-dn');
            $checkResult.find('i').addClass('z-close')
            v=1;
        }else if(v===1){
            $repaymentPlanResult.addClass('f-dn');
            $checkResult.find('i').removeClass('z-close')
            v=0;
        }

    })
  })

