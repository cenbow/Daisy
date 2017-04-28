/**
 * 债权管理
 */
/*global $,log,setTimeout,environment,template,formatDate*/
(function () {
    'use strict';

    var $debtForm = $("#debt_form"),
        $projects = $('#j-projects-list'),
        $detailLayer = $('#j-detail'),
        cacheData={};



    var Debt = {
        init: function () {
            var self = this;
            self.loadDebtPage(1);
            self.loadDebtThreeDay();

            //查询数据
            var $startTime = $('#jStartTime'),
                $endTime = $('#jEndTime'),
                $borrowerName = $('#jBorrowerName'),
                $notice = $('#j-notice');

            $(".j-debt-search").on("click", function () {
                $notice.attr('byComplexSearch', true);
                var startTimeVal = $startTime.val(),
                    endTimeVal = $endTime.val(),
                    borrowerNameVal = $borrowerName.val();

                if(borrowerNameVal){
                    self.loadDebtPage();
                } else {
                        if(!startTimeVal && !endTimeVal && !borrowerNameVal){
                            return false;
                        }
                        self.loadDebtPage();
                }
            });

            //筛选赋值
            $('#j-filterBox').on('blur', 'input', function () {
                self.setFilterValue($(this));
            });

            //显示详情
            var timer;
            $projects.on('mouseenter', '.j-debtdetail-link', function () {
                self.showDetail($(this));
            }).on("mouseleave", function () {
                timer= setTimeout(function(){
                     self.hideDetail($(this));
                    $(".j-debtdetail-link").find('s').hide();
                },200);
            });
            $detailLayer.on("mouseenter",function(){
                clearTimeout(timer);
            }).on("mouseleave",function(){
                $(".j-debtdetail-link").find('s').hide();
                self.hideDetail($(this));
            });
            //模拟选择框
            var $selector = $('body');
            $selector.on("click", ".j-selector", function (e) {
                $(this).toggleClass("z-actived");
                e.preventDefault();
                return false;

            }).on("mouseleave", ".j-selector", function () {
                $(this).removeClass("z-actived");
            });

            $selector.on("mouseenter", '.j-selector ul', function () {
                $(this).parent().addClass("z-actived");
            });

            $selector.on("click", ".j-selector li", function () {
                self.periodSelect($(this));
            });
        },
        //过滤条件赋值
        setFilterValue: function (target) {
            target.val(target.val().replace(/ /g, ''));

            var i = target.data('index'),
                val = target.val();

            //验证姓名
            if (i === 2) {
                var nameReg = /^[\u4E00-\u9FA5]{2,10}(?:·[\u4E00-\u9FA5]{2,10})*$/;
                if (val && !nameReg.test(val)) {
                    $.xDialog({content: '请输入中文姓名'});
                    target.val('');
                    return false;
                }
            }
            $debtForm.find('input').eq(i).val(val);
        },
        //时间段选择
        periodSelect: function (target) {
            var self=this,
                text = target.html(),
                val = target.attr("value"),
                parent = target.parent(),
                thisText=target.text();

            //只限可用状态下选择
            if (!target.hasClass("z-disabled")) {
                parent.siblings("button").html(text);
                parent.siblings(".j-selected-ipt").val(text === '全部' ? '' : text);

                //下拉时间查询
                $debtForm.data('selectedPeriod',val);
                self.getFilterDate(val);

                //清空回款日期选择
                $('#j-filter-datePicker').find('input').val('');
                $('#jBorrowerName').val('');

            } else {
                return false;
            }
        },
        //加载债权管理页数据
        loadDebtPage: function (first) {
            var self=this;
            $projects.hide();
            var data = $debtForm.serializeArray(),
                dataStr=$debtForm.serialize();

            if(!cacheData[dataStr]){
                $.xPost({
                    url: environment.globalPath + "/transaction/debtList",
                    data: data,
                    callback: function (data) {
                        if (data.success) {
                            //cache
                            cacheData[dataStr]=data;
                            self.renderPage(data,first);
                        }
                    }
                });
            }else{
                self.renderPage(cacheData[dataStr],first);
            }
        },

        //加载最近三天数据
        loadDebtThreeDay:function(){
            var self=this;
            $.xPost({
                url: environment.globalPath + "/transaction/debtListThreeDay",
                callback: function (data) {
                    if (data.success) {
                        self.renderThreeDay(data);
                    }
                }
            });
        },


        //渲染三天数据
        renderThreeDay:function(data){
            var noticeDataThreeDay = data.result;
            $('#j-notice-threeDay').html(template('j-notice-threeDay-tpl', noticeDataThreeDay));
        },

        // 黄条提示停留
        noticeRender: '',

        //渲染数据
        renderPage: function (data,isFirst) {
            //统计
            var $stats=$('#j-stats');
            $stats.html(template('j-stats-tpl', data.result));
            var selectedPeriod=$debtForm.data('selectedPeriod');
            if(!isNaN(selectedPeriod)){
                var $selectedText=$stats.find('button');
                $selectedText.text($selectedText.siblings('ul')
                    .find('li').eq(selectedPeriod).text());
            }

            //通知
            if(!isFirst){
                var noticeData = data.result,
                    $notice = $('#j-notice');

                $('#j-notice-threeDay').hide();

                if($notice.attr('byComplexSearch')){
                    $('#j-debtTime').val('')

                    noticeData.debtTime = false;
                    noticeData.startDate = $("input[name='startDate']").val();
                    noticeData.endDate = $("input[name='endDate']").val();
                    noticeData.borrowerName = $("input[name='borrowerName']").val();

                    $('.j-selector').find('button').text('全部')

                } else {
                    noticeData.debtTime = $('.j-selector').find('button').text();
                }

                $notice.html(template('j-notice-tpl', noticeData)).removeAttr('byComplexSearch')
            }

            //显示五秒隐藏通知
            clearTimeout(this.noticeRender);
            this.noticeRender = setTimeout(function(){
                $('.u-uc-notice').slideUp()
            }, 5000);

            var dataPage = data.page || {};
            var pagenum=dataPage.maxPager;
            //列表
            $projects.html(template('j-projects-tpl', dataPage)).show();
            if(pagenum>1){
                //分页
                this.showPagenavi('j-pagenav', 'j-pagenav-tpl', dataPage);
            }


        },
        //显示详情框

        showDetail: function (target) {
            var self = this,
                $this = target,
                data = $this.data(),
                acount = data.count,
                debtProjectId = data.projectid;
                //detailStatus=$this.data("status");


            //框距离顶部的高度
            var layerTop = 0;
            switch (true) {
                case acount > -1 && acount < 4:
                    layerTop = 179;
                    break;
                case acount > 3 && acount < 8:
                    layerTop = 389;
                    break;
                case acount > 7 && acount < 12:
                    layerTop = 599;
                    break;
                case acount > 11 && acount < 16:
                    layerTop = 809;
                    break;
            }

            $detailLayer.css("top", layerTop);
            var info = {
                markProjectName: data.markprojectname,
                restMarkProjectName: data.restmarkprojectname,
                totalAmount: data.totalamount,
                borrowerName: data.borrowername,
                rate: data.rate,
                total: data.total,
                width: self.getProgressWidth(data.total, data.rate,$this),
                status:$this.data("status")
            };

            if(!cacheData[debtProjectId]){
                //表单中进度
                $.xPost({
                    url: environment.globalPath + "/transaction/debtTransactionInterest",
                    data: {'projectId': debtProjectId},
                    callback: function (data) {
                        if (data.success) {
                            cacheData[debtProjectId]=data;
                            renderDetail(data);
                        }
                    }
                });
            }else{
                renderDetail(cacheData[debtProjectId]);
            }
            function renderDetail(data){
                var detailData = data.result;
                detailData.info = info;
                $('#j-detail').html(template('j-detail-tpl', detailData));

                $projects.find('s').hide();
                $this.find('s').show();

                //显示箭头和进度框
                $detailLayer.show();
            }
        },
        //隐藏详情框
        hideDetail: function () {
            $detailLayer.hide();
        },
        /**
         * 获取"项目待还本息"进度条宽度
         * @param size 期数
         * @param length 已还期数
         */
        getProgressWidth: function (size, length,element) {
            var sideWidth = 10,
                BOX_WIDTH = 115,
                gapWidth = 0,//每期之间的距离
                barWidth = 880,//灰色进度条长度
                progressWidth = 0,//红色进度条长度
                overallWidth,
                status=element.data("status");//是否已还款状态


            switch (size) {
                case 1:
                    sideWidth = barWidth / 2;
                    break;
                case 2:
                    sideWidth = 170;
                    break;
                case 3:
                    sideWidth = 110;
                    break;
                case 4:
                    sideWidth = 60;
                    break;
                case 5:
                case 6:
                    sideWidth = 10;
                    break;
                default:
            }

            if (size <= 6) {
                gapWidth = size === 1 ? 385 : (880 - size * BOX_WIDTH - sideWidth * 2) / (size - 1);
            } else {
                gapWidth = (880 - 6 * BOX_WIDTH - sideWidth * 2) / (6 - 1);
                barWidth = sideWidth * 2 + BOX_WIDTH * size + gapWidth * (size - 1);
            }
            //判断是否全部已还款
            if(status===1){
                    progressWidth =size === 1 ? 880 : sideWidth*2+(size-1)*gapWidth+size*BOX_WIDTH;
            }else{
                if (size === 1) {
                    progressWidth = 880/2;
                } else {
                    progressWidth = sideWidth + BOX_WIDTH * 0.5 + BOX_WIDTH * (length-1) + gapWidth * (length-1);
                }
            }

        //总宽度
            overallWidth= size <6 ? 880:sideWidth*2+BOX_WIDTH*size+gapWidth*(size-1);
            //log(overallWidth)
            return {
                side: sideWidth,
                gap: Math.ceil(gapWidth),
                progress: Math.ceil(progressWidth),
                bar: barWidth,
                overall:overallWidth
            };
        },
        /**
         * 选择查询时间的取值
         * @param type
         */
        getFilterDate: function (type) {
            var endTime = formatDate(environment.serverDate),
                endDate = new Date(endTime),
                day = endDate.getDate(),
                month = endDate.getMonth();

            var startTime='-1';

            switch (type) {
                case "1"://1days
                    endDate.setDate(day);

                    break;
                case "2"://3days
                    endDate.setDate(day + 2);

                    break;
                case "3"://7days
                    endDate.setDate(day + 6);

                    break;
                case "4"://15days
                    endDate.setDate(day + 14);

                    break;
                case "5"://1 month
                    endDate.setMonth(month + 1);

                    break;
                case "6"://3 month
                    endDate.setMonth(month + 3);

                    break;
                case "0"://all
                    startTime = '';
                    endTime = '';
                    break;
            }

            if(startTime){
                startTime = formatDate(endDate.getTime());
            }

            var borrowerName = $('#borrowerName').val();
            $("input[name='startDate']").val(endTime);
            $("input[name='endDate']").val(startTime);
            $("input[name='borrowerName']").val(borrowerName);

            this.loadDebtPage();
        },
        /**
         * 通用分页条
         * @param targetId 要渲染的id
         * @param templateId 模板id
         * @param data 分页数据
         */
        showPagenavi:function (targetId, templateId, data) {
        var pageData = data||{},
            self=this,
            $page = $('#' + targetId);

        $page.off('click', 'a');
        pageData.list = new Array(pageData.totalPageCount);
        $page.html(template(templateId, pageData));

        var $curPage = $('#j-currentPage');
        if ($curPage.length) {
            $page.on('click', 'a', function () {
                var pn = $(this).data('pn');
                $curPage.val(pn);
                self.loadDebtPage();
            });
        }
    }
    };

    Debt.init();

    /**
     * 分割金额小数点
     * @param amount 传入的金额
     * @param type 输出类型 0=>整数部分,1=>小数部分
     */
    template.helper('splitAmount', function (amount, type) {
        var amountArray = [];

        if (amount) {
            amountArray = (amount + '').split('.');
            if (amountArray[1]=="00"||!amountArray[1]) {
                amountArray[1] = '.00';
            } else {
                amountArray[1] = (amountArray[1] / 100 + '').substr(1);
                if(amountArray[1].length == 2){
                    amountArray[1] = amountArray[1] + '0';
                }
            }
        } else {
            amountArray = ['0', '.00'];
        }
        return amountArray[type];

    });
})();
