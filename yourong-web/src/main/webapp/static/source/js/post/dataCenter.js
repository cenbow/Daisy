(function () {
    var path = environment.globalPath

    Vue.config.devtools = true

    var dataCenter = new Vue({
        el: '#dataCenter',
        data: {
            // 后端接口地址
            urls: {
                init: path + '/activity/dataChannel/init',
                investTotal: path + '/activity/dataChannel/realTimeData'
            },
            initData: {},  // 初始化接口返回数据
            investType: 0,  //投资人数占比：0， 投资额度占比：1
            regionDistribute: 0,  //注册人数： 0， 投资金额： 1

            allInvestData: {},

            regionList: [],
            provinceList: [],
            monthList: [{value: 0, textStyle: {color: '#fff'}}],  //投资月份列表
            investMonthAmountList: [],  //每个月的投资金额列表

            userSexChart: {},  //用户性别分布图表
            topRegionChart: {},  //地域分布图表


            regionInvesTop10tList: [],     // 根据投资金额的地域分布前10排名
            regionRegisterTop10List: [],   // 根据注册人数的地域分布前10排名
            //用于TOP3排行的列表
            regionInvestList: [],         //  regionInvesTop10tList投资的省份名称数组
            regionRegisterList: [],       //  regionRegisterTop10List注册的省份名称数组
            //用于地图
            provinceInvestList: [],       //  regionInvesTop10tList投资的地域名称数组
            provinceRegisterList: [],     //  regionInvesTop10tList投资的地域名称数组

            registerMember: [],           //regionInvesTop10tList投资的省份名称数组
            investAmount: [],

            pageRegisterList: [],
            pageRankList: [],
            regionChart: [],   //地域分布图表

            //地域分布颜色列表
            regionColor: [
                {blueColor: 'rgba(80,109,225,1)'},
                {blueColor: 'rgba(80,109,225,.96)'},
                {blueColor: 'rgba(80,109,225,.93)'},
                {blueColor: 'rgba(80,109,225,.9)'},
                {blueColor: 'rgba(80,109,225,.87)'},
                {blueColor: 'rgba(80,109,225,.84)'},
                {blueColor: 'rgba(80,109,225,0.81)'},
                {blueColor: 'rgba(80,109,225,0.78)'},
                {blueColor: 'rgba(80,109,225,0.75)'},
                {blueColor: 'rgba(80,109,225,0.72)'},
                {blueColor: 'rgba(80,109,225,0.69)'},
                {blueColor: 'rgba(80,109,225,0.66)'},
                {blueColor: 'rgba(80,109,225,0.63)'},
                {blueColor: 'rgba(80,109,225,0.6)'},
                {blueColor: 'rgba(80,109,225,0.57)'},
                {blueColor: 'rgba(80,109,225,0.54)'},
                {blueColor: 'rgba(80,109,225,0.51)'},
                {blueColor: 'rgba(80,109,225,0.48)'},
                {blueColor: 'rgba(80,109,225,0.45)'},
                {blueColor: 'rgba(80,109,225,0.42)'},
                {blueColor: 'rgba(80,109,225,0.39)'},
                {blueColor: 'rgba(80,109,225,0.36)'},
                {blueColor: 'rgba(80,109,225,0.33)'},
                {blueColor: 'rgba(80,109,225,0.3)'},
                {blueColor: 'rgba(80,109,225,0.27)'},
                {blueColor: 'rgba(80,109,225,0.24)'},
                {blueColor: 'rgba(80,109,225,0.2)'},
                {blueColor: 'rgba(80,109,225,0.2)'},
                {blueColor: 'rgba(80,109,225,0.2)'},
                {blueColor: 'rgba(80,109,225,0.2)'},
                {blueColor: 'rgba(80,109,225,0.2)'},
                {blueColor: 'rgba(80,109,225,0.2)'},
                {blueColor: 'rgba(80,109,225,0.2)'}
            ]
        },
        computed: {
            //以下数据截止时间
            d: function dataDeadline() {
                var nowdays = new Date();
                var year = nowdays.getFullYear();
                var month = nowdays.getMonth();
                if (month == 0) {
                    month = 12;
                    year = year - 1;
                }
                if (month < 10) {
                    month = "0" + month;
                }

                var myDate = new Date(year, month, 0);
                var deadline = year + "年" + month + "月" + myDate.getDate() + "日";//上个月的最后一天
                return deadline;
            }
        },
        created: function () {
            var self = this
            showInvestTotal()
            $.xPost({
                url: self.urls.init,
                callback: function (data) {
                    if (data.success) {
                        var listRegionMonthList = data.result.listRegionMonth;

                        self.regionInvesTop10tList = self.regionDataRank(listRegionMonthList, "investAmountRate", 10);  //投资前10数组
                        self.regionRegisterTop10List = self.regionDataRank(listRegionMonthList, "registerMemberRate", 10);  //注册前10数组
                        self.regionRegisterAllList = self.regionDataRank(listRegionMonthList, "registerMemberRate", 33);
                        self.regionInvesAlltList = self.regionDataRank(listRegionMonthList, "investAmountRate", 33);  //投资前10数组
                        self.$set('provinceInvestList', self.fillProvinceList(self.regionInvesAlltList, self.provinceInvestList, 1));
                        self.$set('provinceRegisterList', self.fillProvinceList(self.regionRegisterAllList, self.provinceRegisterList, 0));
                        self.$set('regionInvestList', self.fillRegionList(self.regionInvesTop10tList, self.regionInvestList));
                        self.$set('regionRegisterList', self.fillRegionList(self.regionRegisterTop10List, self.regionRegisterList));
                        self.$set('investAmount', self.fillInvestAmountList(self.regionInvesTop10tList));
                        self.$set('registerMember', self.fillRegisterList(self.regionDataRank(listRegionMonthList, "registerMemberRate", 10)));

                        self.pageRegisterList = self.regionRegisterList;
                        self.pageRankList = [
                            ((+self.registerMember[1]) / (+self.registerMember[0])).toFixed(4) * 100,
                            ((+self.registerMember[2]) / (+self.registerMember[0])).toFixed(4) * 100
                        ];
                        self.$set('monthList', self.fillInvestMonthList(data.result.listTotalAmount || []))
                        self.$set('investMonthAmountList', self.fillInvestMonthAmountList(data.result.listTotalAmount || []))
                        init(data.result)
                    } else {
                        console.log('页面初始化错误', data)
                    }
                }
            })

            function init(result) {
                self.initData = {
                    projectInfo: result.projectData || {}         // 项目数据
                }
                self.initInvestTotalCharts()
                self.initProjectTypeCharts();
                self.initProjectTimeCharts();
                self.initInvestTerminalCharts();
                self.initProjectPaymentsCharts();
                self.initInvestPeopleCharts();
                self.initUserAgeCharts();
                self.initRegionCharts();
                self.initTopRegionCharts();
            }

            function showInvestTotal() {
                $.xPost({
                    url: self.urls.investTotal,
                    callback: function (data) {
                        if (data.success) {
                            self.allInvestData = {
                                registerCount: data.result.memberCount || 0,
                                investAmount: data.result.totalInvest || 0,
                                profitTotal: data.result.totalInvestInterest || 0,
                                tradeCount: data.result.transactionCount || 0
                            }
                            self.getTotalData();
                        } else {
                            console.log('页面初始化错误', data)
                        }
                    }
                })
            }
        },
        methods: {
            //数据总览
            getTotalData: function () {
                var self = this;
                var options = {
                    useEasing: true,
                    useGrouping: true,
                    separator: ',',
                    decimal: '.',
                    prefix: '',
                    suffix: ''
                };
                var investTotal = new CountUp("j-invest-total", 0, self.allInvestData.investAmount, 2, 2.5, options);
                var registerTotal = new CountUp("j-register-total", 0, self.allInvestData.registerCount, 0, 2.5, options);
                var profitTotal = new CountUp("j-profit-total", 0, self.allInvestData.profitTotal, 2, 2.5, options);
                var tradeTotal = new CountUp("j-trade-total", 0, self.allInvestData.tradeCount, 0, 2.5, options);
                investTotal.start();
                registerTotal.start();
                profitTotal.start();
                tradeTotal.start();
            },
            // 投资人数占比与投资额度占比切换
            getSexRate: function (num) {
                var dataOption, self = this

                self.investType = num

                if (num == 0) {
                    dataOption = {
                        series: [{
                            data: ['', {
                                value: self.formatUserSexData(self.initData.projectInfo.manNumRate),
                                itemStyle: {
                                    normal: {
                                        color: '#FF6B6B',
                                        barBorderRadius: 2
                                    }
                                }
                            }, {
                                value: self.formatUserSexData(self.initData.projectInfo.womanNumRate),
                                itemStyle: {
                                    normal: {
                                        color: '#8CEBC7',
                                        barBorderRadius: 2
                                    }
                                }
                            }, '']
                        }]
                    }
                } else {
                    dataOption = {
                        series: [{
                            data: ['', {
                                value: self.formatUserSexData(self.initData.projectInfo.womanInvestAmountRate),
                                itemStyle: {
                                    normal: {
                                        color: '#FF6B6B',
                                        barBorderRadius: 2
                                    }
                                }
                            }, {
                                value: self.formatUserSexData(self.initData.projectInfo.manInvestAmountRate),
                                itemStyle: {
                                    normal: {
                                        color: '#8CEBC7',
                                        barBorderRadius: 2
                                    }
                                }
                            }, '']
                        }]
                    }
                }
                self.userSexChart.setOption(dataOption)
            },
            // 注册人数与投资金额切换
            getTopRegionRate: function (num) {
                var dataOption, self = this
                self.regionDistribute = num
                if (num == 0) {
                    dataOption = {
                        yAxis: [{data: self.regionRegisterList}],
                        series: [{

                            data: self.registerMember
                        }]
                    }
                    self.regionChart.setOption({
                        tooltip: {
                            formatter: function (params) {
                                if (isNaN(params.value)) {
                                    return false
                                } else {
                                    return '注册人数占比' + (params.value * 100).toFixed(2) + '%'
                                }
                            }
                        },
                        series: [{
                          data:self.provinceRegisterList
                        }]
                    })
                } else {
                    dataOption = {
                        yAxis: [{data: self.regionInvestList}],
                        series: [{
                            data: self.investAmount
                        }]
                    }
                    self.regionChart.setOption({
                        tooltip: {
                            formatter: function (params) {
                                if (isNaN(params.value)) {
                                    return false
                                } else {
                                    return '投资金额占比' + (params.value * 100).toFixed(2) + '%'
                                }
                            }
                        },
                        series: [{
                            data: self.provinceInvestList
                        }]
                    })
                }
                self.topRegionChart.setOption(dataOption)
            },
            // 区域分布数据排序
            regionDataRank: function (arr, item, num) {
                var self = this;
                var len = arr.length

                for (var i = 0; i < len; i++) {
                    arr[i][item] = (arr[i][item] === undefined ? 0 : arr[i][item]);
                }
                for (var i = 0; i < len; i++) {
                    for (var j = 0; j < len - i - 1; j++) {
                        if (arr[j][item] < arr[j + 1][item]) {
                            var swap = arr[j];
                            arr[j] = arr[j + 1];
                            arr[j + 1] = swap;
                        }
                    }
                }
                return arr.slice(0, num)
            },
            // 省份格式化
            formatProvinceName: function (str) {
                if(str=="黑龙江省"){
                    return  "黑龙江"
                }else {
                    return str ? str.substr(0, 2) : ''
                }
            },
            // 用户性别分布数据格式化
            formatUserSexData: function (str) {
                return parseInt((+(str)).toFixed(4) * 10000) / 100
            },
            // 累计投资金额格式化为亿
            formatInvestAmount: function (str) {
                var n = 100000000;
                return Math.round(((+str) / n) * 100) / 100
            },
            //数字转换成千分号方法
            formatAmountSplit: function (num, units, needZero) {
                var unit = units || ''
                if (!num) {
                    if (needZero) {
                        return unit + 0
                    } else {
                        return ''
                    }
                }
                var parts = num.toString().split(".")
                parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",")
                return unit + parts.join(".")
            },
            // 地域分布--区域列表
            fillProvinceList: function (arr, vmArr, num) {
                var i,
                    self = this,
                    dataList = vmArr,
                    len = arr.length,
                    other = {
                        name: '南海诸岛',
                        itemStyle: {
                            normal: {
                                areaColor: '#F4F4F2',
                                borderColor: '#CAD2D3',
                                borderWidth: '2px'
                            }
                        }
                    }

                for (i = 0; i < len; i++) {
                    dataList.push(
                        {
                            name: self.formatProvinceName(arr[i].province),
                            value: num == 0 ? arr[i].registerMemberRate : arr[i].investAmountRate,
                            itemStyle: {
                                normal: {
                                    color: dataCenter.regionColor[i].blueColor
                                }
                            }
                        }
                    )
                }
                dataList.push(other)
                return dataList
            },
            // 地域分布--TOP10省份列表
            fillRegionList: function (arr, vmArr) {
                var i,
                    self = this,
                    dataList = vmArr,
                    len = arr.length
                for (i = 0; i < len; i++) {
                    dataList.push(
                        self.formatProvinceName(arr[i].province)
                    )
                }
                return dataList.reverse()
            },
            // 地域分布--TOP10注册人数列表
            fillRegisterList: function (arr, vmArr) {
                var i,
                    self = this,
                    registerMember = self.registerMember,
                    len = arr.length
                for (i = 0; i < len; i++) {
                    registerMember.unshift(
                        arr[i].registerMemberRate
                    )
                }
                return registerMember
            },
            // 地域分布--TOP10投资金额列表
            fillInvestAmountList: function (arr) {
                var i,
                    self = this,
                    investAmount = self.investAmount,
                    len = arr.length
                for (i = 0; i < 10; i++) {
                    investAmount.unshift(
                        arr[i].investAmountRate
                    )
                }
                return investAmount
            },
            // 累计投资金额--月份列表
            fillInvestMonthList: function (arr) {
                var i,
                    self = this,
                    monthList = self.monthList,
                    len = arr.length
                for (i = 0; i < len; i++) {
                    monthList.unshift(
                        arr[i].month
                    )
                }
                return monthList
            },
            // 累计投资金额--金额列表
            fillInvestMonthAmountList: function (arr) {
                var i,
                    self = this,
                    investMonthAmountList = self.investMonthAmountList,
                    len = arr.length
                for (i = 0; i < len; i++) {
                    investMonthAmountList.unshift(
                        arr[i].totalInvestAmount
                    )
                }
                return investMonthAmountList
            },
            // 累计投资金额--折线图
            initInvestTotalCharts: function () {
                var self = this
                // 基于准备好的dom，初始化echarts实例
                var investmentChart = echarts.init(document.getElementById('investment'));

                // 指定图表的配置项和数据
                var investmentOption = {
                    animationDuration: 1500,
                    tooltip: {
                        trigger: 'axis',
                        backgroundColor: '#FFFFFF',
                        textStyle: {
                            color: '#FF6B6B'
                        },
                        extraCssText: 'box-shadow: 0px 0px 8px 0px rgba(255,107,107,0.45);border-radius: 3px;',
                        formatter: function (params) {
                            return '￥' + self.formatAmountSplit(params[0].value)
                        }
                    },
                    legend: {
                        show: false,
                        containLabel: true
                    },
                    toolbox: {
                        show: false
                    },
                    grid: {
                        left: '93px'
                    },
                    textStyle: {
                        color: '#666666',
                        fontSize: 16
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        splitLine: {
                            show: true,
                            lineStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0, color: '#F9F9F9' // 0% 处的颜色
                                }, {
                                    offset: 1, color: '#D6D6D6' // 100% 处的颜色
                                }], false),
                                type: 'dashed'
                            }
                        },
                        axisLine: {
                            lineStyle: {
                                color: '#E9E9E9'
                            }
                        },
                        data: self.monthList
                    },
                    yAxis: {
                        type: 'value',
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            formatter: function (val) {
                                return val == 0 ? '' : (self.formatInvestAmount(val) + '亿');
                            }
                        },
                        splitLine: {
                            show: false
                        }
                    },
                    series: [{
                        name: '投资金额',
                        type: 'line',
                        symbolSize: 12,
                        areaStyle: {
                            normal: {
                                color: 'rgba(255,107,107,0.10)'
                            }
                        },
                        data: self.investMonthAmountList
                    }],
                    color: [
                        '#FF6B6B'
                    ]
                };

                // 使用刚指定的配置项和数据显示图表。

                investmentChart.setOption(investmentOption);
            },
            // 项目类型分布--饼图
            initProjectTypeCharts: function () {
                var self = this
                // 基于准备好的dom，初始化echarts实例
                var projectTypeChart = echarts.init(document.getElementById('projectType'));

                // 指定图表的配置项和数据
                var projectTypeOption = {
                    title: {
                        text: '项目类型分布',
                        textStyle: {
                            color: '#999999',
                            fontSize: 18,
                            fontWeight: 400
                        },
                        x: 'center',
                        padding: [0, 0]
                    },
                    // tooltip: {
                    //     trigger: 'item',
                    //     backgroundColor: '#FFFFFF',
                    //     textStyle: {
                    //         color: '#FF6B6B'
                    //     },
                    //     extraCssText: 'box-shadow: 0px 0px 8px 0px rgba(255,107,107,0.45);border-radius: 3px;',
                    //     formatter: "{a} <br/>{b}: {c} ({d}%)"
                    // },
                    calculable: true,
                    series: [
                        {
                            name: '项目类型',
                            type: 'pie',
                            radius: [35, 65],
                            center: ['50%', '50%'],
                            grid: {
                                bottom: 0
                            },
                            hoverAnimation: false,
                            label: {
                                normal: {
                                    formatter: "{b} \n{d}%",

                                    textStyle: {
                                        color: '#666666'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: true
                                }
                            },
                            data: [
                                {value: self.initData.projectInfo.debtRate, name: '债权项目'},
                                {value: self.initData.projectInfo.directRate, name: '直投项目'}
                            ]
                        }
                    ],
                    color: [
                        '#FF6B6B', '#506DE1'
                    ]
                }

                // 使用刚指定的配置项和数据显示图表。
                projectTypeChart.setOption(projectTypeOption);
            },
            // 项目期限分布--饼图
            initProjectTimeCharts: function () {
                var self = this
                // 基于准备好的dom，初始化echarts实例
                var projectTimeChart = echarts.init(document.getElementById('projectTime'));

                // 指定图表的配置项和数据
                var projectTimeOption = {
                    title: {
                        text: '项目期限分布',
                        textStyle: {
                            color: '#999999',
                            fontSize: 18,
                            fontWeight: 400
                        },
                        x: 'center',
                        padding: [0, 0]
                    },
                    calculable: true,
                    series: [
                        {
                            name: '项目期限',
                            type: 'pie',
                            radius: [35, 65],
                            center: ['50%', '50%'],
                            hoverAnimation: false,
                            startAngle: 85,
                            label: {
                                normal: {
                                    formatter: function (params) {
                                        return params.name + "\n" + (params.value * 100).toFixed(2) + '%'
                                    },
                                    textStyle: {
                                        color: '#666666'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: true
                                }
                            },
                            data: [
                                {value: self.initData.projectInfo.cycle01Rate, name: '0~1个月'},
                                {value: self.initData.projectInfo.cycle12Rate, name: '1~2个月'},
                                {value: self.initData.projectInfo.cycle23Rate, name: '2~3个月'},
                                {value: self.initData.projectInfo.cycle36Rate, name: '3~6个月'},
                                {value: self.initData.projectInfo.cycle612Rate, name: '6~12个月'},
                                {value: self.initData.projectInfo.cycleElseRate, name: '12个月以上'}
                            ]
                        }
                    ],
                    color: [
                        '#B8D4EF', '#454C5C', '#B5B4B2', '#7B8FBD', '#D1D1D1','#E3E3E3'
                    ]
                }

                // 使用刚指定的配置项和数据显示图表。
                projectTimeChart.setOption(projectTimeOption);
            },
            // 投资终端分布--饼图
            initInvestTerminalCharts: function () {
                // 基于准备好的dom，初始化echarts实例
                var investTerminalChart = echarts.init(document.getElementById('investTerminal'));

                // 指定图表的配置项和数据
                var investTerminalOption = {
                    title: {
                        text: '投资终端分布',
                        textStyle: {
                            color: '#999999',
                            fontSize: 18,
                            fontWeight: 400
                        },
                        x: 'center',
                        padding: [0, 0]
                    },
                    calculable: true,
                    series: [
                        {
                            name: '投资终端',
                            type: 'pie',
                            radius: [35, 65],
                            center: ['50%', '50%'],
                            hoverAnimation: false,
                            label: {
                                normal: {
                                    formatter: "{b} \n{d}%",

                                    textStyle: {
                                        color: '#666666'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: true
                                }
                            },
                            data: [
                                {value: this.initData.projectInfo.mobileRate, name: '移动端'},
                                {value: this.initData.projectInfo.webRate, name: 'PC端'}
                            ]
                        }
                    ],
                    color: [
                        '#BD7EFF', '#FFDB54'
                    ]
                }

                // 使用刚指定的配置项和数据显示图表。
                investTerminalChart.setOption(investTerminalOption);
            },
            // 项目还款情况--饼图
            initProjectPaymentsCharts: function () {
                var self = this
                // 基于准备好的dom，初始化echarts实例
                var projectPaymentsChart = echarts.init(document.getElementById('projectPayments'));

                // 指定图表的配置项和数据
                var projectPaymentsOption = {
                    title: {
                        text: '项目还款情况',
                        textStyle: {
                            color: '#999999',
                            fontSize: 18,
                            fontWeight: 400
                        },
                        x: 'center',
                        padding: [0, 0]
                    },
                    calculable: true,
                    series: [
                        {
                            name: '项目还款情况',
                            type: 'pie',
                            radius: [35, 65],
                            startAngle: 50,
                            center: ['50%', '50%'],
                            hoverAnimation: false,
                            label: {
                                normal: {
                                    formatter: function (params) {
                                        return params.name + "\n" + "￥" + self.formatAmountSplit(params.value)
                                    },

                                    textStyle: {
                                        color: '#666666'
                                    }
                                }
                            },
                            labelLine: {
                                normal: {
                                    show: true
                                }
                            },
                            data: [
                                {value: this.initData.projectInfo.paidAmount, name: '已还金额'},
                                {value: this.initData.projectInfo.unpaidAmount, name: '待还金额'}
                            ]
                        }
                    ],
                    color: [
                        '#8CEBC7', '#6AA3FF'
                    ]
                }

                // 使用刚指定的配置项和数据显示图表。
                projectPaymentsChart.setOption(projectPaymentsOption);
            },
            // 用户性别分布--柱状图
            initInvestPeopleCharts: function () {
                var self = this
                // 基于准备好的dom，初始化echarts实例
                self.userSexChart = echarts.init(document.getElementById('userSex'));

                // 指定图表的配置项和数据
                var userSexOption = {
                    grid: {
                        left: '3%',
                        right: '4%',
                        top: '20px',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'category',
                        axisTick: {
                            show: false
                        },
                        axisLine: {
                            lineStyle: {
                                color: '#6A7785'
                            }
                        },
                        data: ["", "男性", "女性", ""]
                    },
                    yAxis: {
                        type: 'value',
                        min: 0,
                        max: 100,
                        splitNumber: 5,
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        },
                        axisLabel: {
                            formatter: function (value) {
                                return value == 0 ? '' : value + '%';
                            }
                        },
                        splitLine: {
                            show: false
                        }
                    },
                    series: [{
                        name: '性别',
                        type: 'bar',
                        itemStyle: {
                            normal: {
                                shadowBlur: 13,
                                shadowColor: 'rgba(0, 0, 0, 0.3)'
                            }
                        },
                        hoverAnimation: false,
                        label: {
                            normal: {
                                show: true,
                                position: 'top',
                                textStyle: {
                                    color: '#fff',
                                    fontSize: 20
                                },
                                formatter: '{c}%'
                            }
                        },
                        barWidth: '50',
                        barCategoryGap: '36',
                        data: ['', {
                            value: self.formatUserSexData(self.initData.projectInfo.manNumRate),
                            itemStyle: {
                                normal: {
                                    color: '#FF6B6B',
                                    barBorderRadius: 2
                                }
                            }
                        }, {
                            value: self.formatUserSexData(self.initData.projectInfo.womanNumRate),
                            itemStyle: {
                                normal: {
                                    color: '#8CEBC7',
                                    barBorderRadius: 2
                                }
                            }
                        }, '']
                    }],
                    textStyle: {
                        color: '#AFBBC7',
                        fontSize: 16
                    }
                };

                // 使用刚指定的配置项和数据显示图表。
                self.userSexChart.setOption(userSexOption);
            },
            // 用户年龄分布--饼图
            initUserAgeCharts: function () {
                // 基于准备好的dom，初始化echarts实例
                var userAgeChart = echarts.init(document.getElementById('userAge'));

                // 指定图表的配置项和数据
                var userAgeOption = {
                    calculable: true,
                    series: [
                        {
                            name: '年龄分布',
                            type: 'pie',
                            adius: '55%',
                            center: ['50%', '50%'],
                            hoverAnimation: false,
                            label: {
                                normal: {
                                    formatter: function (params) {
                                        return params.name + "\n" + (params.value * 100).toFixed(2) + '%'
                                    },
                                    textStyle: {
                                        color: '#fff',
                                        fontSize: 14
                                    }
                                }
                            },
                            data: [
                                {value: this.initData.projectInfo.year50Rate, name: '50后'},
                                {value: this.initData.projectInfo.year60Rate, name: '60后'},
                                {value: this.initData.projectInfo.year70Rate, name: '70后'},
                                {value: this.initData.projectInfo.year80Rate, name: '80后'},
                                {value: this.initData.projectInfo.year90Rate, name: '90后'},
                                {value: this.initData.projectInfo.yearElseRate, name: '其他'}
                            ]
                        }
                    ],
                    color: [
                        '#E79750', '#21DCDC', '#6AA3FF', '#FF6B6B', '#506DE1','#B0B0B0'
                    ]
                };

                // 使用刚指定的配置项和数据显示图表。
                userAgeChart.setOption(userAgeOption);
            },
            // 地域分布--地图
            initRegionCharts: function () {
                var self = this
                var dom = document.getElementById("region");
                self.regionChart = echarts.init(dom);
                var app = {};
                var regionOption = null;

                regionOption = {
                    tooltip: {
                        show: true,
                        trigger: 'item',
                        formatter: function (params) {
                            if (isNaN(params.value)) {
                                return false
                            } else {
                                return params.seriesName + (params.value * 100).toFixed(2) + '%'
                            }
                        }
                    },
                    series: [
                        {
                            name: '注册人数占比',
                            type: 'map',
                            mapType: 'china',
                            layoutCenter: ['30%', '50%'],
                            layoutSize: 450,
                            zoom: 1.5,
                            label: {
                                normal: {
                                    show: false
                                },
                                emphasis: {
                                    show: true
                                }
                            },
                            itemStyle: {
                                normal: {
                                    areaColor: '#F4F4F2',
                                    borderColor: '#CAD2D3',
                                    borderWidth: 1
                                }
                            },
                            data: self.provinceRegisterList
                        }
                    ],
                };

                if (regionOption && typeof regionOption === "object") {
                    self.regionChart.setOption(regionOption, true);
                }
            },
            // 地域分布TOP10
            initTopRegionCharts: function () {
                var self = this

                // 基于准备好的dom，初始化echarts实例
                self.topRegionChart = echarts.init(document.getElementById('topRegion'));

                // 指定图表的配置项和数据
                var topRegionOption = {
                    title: {
                        text: 'TOP10',
                        top: 25,
                        left: 0,
                        textStyle: {
                            color: '#999',
                            fontSize: 14,
                            fontWeight: 'normal'
                        }
                    },
                    grid: {
                        left: '40px'
                    },
                    xAxis: [
                        {
                            type: 'value',
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                show: false
                            },
                            axisLabel: {
                                show: false
                            }
                        }
                    ],
                    yAxis: [
                        {
                            type: 'category',
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            splitLine: {
                                show: false
                            },
                            data: self.regionRegisterList
                        }
                    ],
                    series: [{
                        name: '地区',
                        type: 'bar',
                        itemStyle: {
                            normal: {
                                color: '#506DE1',
                                barBorderRadius: 2
                            }
                        },
                        barWidth: '17',
                        barCategoryGap: '36',
                        data: self.registerMember
                    }],
                    textStyle: {
                        color: '#454C5C',
                        fontSize: 16
                    }
                };

                // 使用刚指定的配置项和数据显示图表。
                self.topRegionChart.setOption(topRegionOption);
            }
        }
    })

    window.dataCenter = dataCenter

})()
