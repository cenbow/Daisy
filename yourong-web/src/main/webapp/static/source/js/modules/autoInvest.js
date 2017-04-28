(function () {
    window.pickFilterDate = function (value, flag) {
        if (flag) {
            vm.startTime = value
        }
        else {
            vm.endTime = value
        }
    }

    Vue.config.devtools = true
    window.vm = new Vue({
        el: '#j-setbox',
        data: {
            autoInvestSwitch: false,
            tipsSwitch: false,
            contractSwitch: true,
            agreement: false,
            entrust: true,
            agreeText:false,
            closeButton:false,
            couponRemark:0,
            init: {},
            open:'',
            periodMax:'',
            periodMin:'',
            initialData: {
                investFrequency: 1,
                couponType:0,
                projectType: '1,2',
                periodMin: 1,
                periodMax: 3,
                amount: '1000'
            },
            periodList: [
                null,
                '5天', '15天', '30天',
                '2个月', '3个月', '6个月', '12个月', '24个月'
            ],
            frequencyList: [
                '请选择投资频率',
                '每天投标一次',
                '每个项目投标一次'
            ],
            couponList:[
                '不使用优惠券',
                '优先使用收益最高优惠券',
                '优先使用有效期最短收益券'
            ],
            editable: false
        },
        computed: {
            'initStartTime': function () {
                return this.init.startTime || environment.serverDate
            },
            'initEndTime': function () {
                var today = new Date(environment.serverDate)
                today.setMonth(today.getMonth() + 1)

                return this.init.endTime || today.getTime()
            },
            investFrequencyText: function () {
                var frequency = this.init.investFrequency||this.initialData.investFrequency
                return this.frequencyList[frequency]
            },
            couponTypeText:function(){
                var coupon= this.init.couponType||this.initialData.couponType
                return this.couponList[coupon]
            },
            projectType: function () {
                var initType = this.init.projectType||this.initialData.projectType,
                    list = []

                if ($.isArray(initType)) {
                    return initType
                } else {
                    list = initType.split(',')
                }
                switch (list.length) {
                    case 0:
                        typeList = [0, 0]
                        break
                    case 1:
                        if (list[0] === '1') {
                            typeList = [1, 0]
                        } else {
                            typeList = [0, 1]
                        }
                        break
                    case 2:
                        typeList = [1, 1]
                        break
                }
                return typeList
            }
        },
        created: function () {
            selector()
            this.queryAutoInvest()

        },
        watch: {
            'init': function (n) {
                var size = Object.keys(n).length,
                    flag = this.init.investFlag
                if (size && flag) {
                    if(!this.open){
                        this.autoInvestSwitch=false
                    }else{
                        this.autoInvestSwitch = this.init.investFlag === 1
                    }

                }
                if (size) {
                    this.startTime = this.initStartTime
                    this.endTime = this.initEndTime
                }
            },
            'amount': function (n) {
                if(n%1E3!==0||n>1E5||n<1E3){
                    this.amount = this.filterAmount(n)
                }
            }
        },
        methods: {
            switchAutoInvest: function () {
                this.contractSwitch = true
                if (this.hasWithholdAuthority > 0) {
                    if (this.autoInvestSwitch) {
                        this.tipsSwitch = 2
                        this.init.investFlag=2
                    } else {
                        this.tipsSwitch = !this.tipsSwitch ? 1 : 0
                        this.init.investFlag=1
                    }
                } else {
                    this.tipsSwitch = 3
                }
            },
            setAutoInvest: function (flag,callback) {
                $.xPost({
                    url: environment.globalPath + '/member/autoInvestSet',
                    data: {investFlag: flag},
                    callback:callback
                })
            },
            getPeriodMinText: function (id) {
                var period = id || this.init.periodMin||this.initialData.periodMin
                return this.periodList[+period]
            },
            getPeriodMaxText: function (id) {
                var period = id || this.init.periodMax||this.initialData.periodMax
                return this.periodList[+period]
            },
            setPeriod: function (value, type) {
                //var min = +this.periodMin,
                //    max = +this.periodMax,
                //    self = this,
                //    val=''
                var self = this
                if(type){
                    this.periodMax = value
                    self.$set('periodMaxText', self.getPeriodMaxText())
                }else{
                    this.periodMin = value
                    self.$set('periodMinText', self.getPeriodMinText())
                }
                //if (type) {
                //    val = value < min ? min : value
                //    this.periodMax = val
                //    setTimeout(function () {
                //        self.$set('periodMaxText', self.getPeriodMaxText(val))
                //    },500)
                //} else {
                //    val = value > max ? max : value
                //    this.periodMin = val
                //    setTimeout(function () {
                //        self.$set('periodMinText', self.getPeriodMinText(val))
                //    },500)
                //}
            },
            remark:function(){

            },
            queryAutoInvest: function () {
                var self = this
                $.xPost({
                    url: '/member/queryAutoInvest',
                    callback: function (data) {
                        if (data.success) {
                            if (data.result) {
                                self.init = data.result
                                self.open=data.result.open
                            } else {
                                self.init = self.initialData
                            }
                            self.$set('periodMinText', self.getPeriodMinText())
                            self.$set('periodMaxText', self.getPeriodMaxText())
                        }
                    }
                })
            },
            selectProjectType: function (type) {
                var initType = this.projectType,
                    type2 = initType[type] ? 0 : 1
                this.init.projectType = !type ? [type2, initType[1]] : [initType[0], type2]
            },
            switchContract: function () {
                this.contractSwitch = !this.contractSwitch
            },
            switchOpenTips: function (type) {
                var self = this
                if (this.contractSwitch&&type) {
                    this.setAutoInvest(1, function (data) {
                        self.autoInvestSwitch = true
                        self.sort=data.result
                        if(!self.init.open){
                            self.editable = true
                        }
                    })
                    this.contractSwitch = false
                    this.tipsSwitch = 0
                } else {
                    if (!type) {
                        this.tipsSwitch = 0
                    }
                }
            },
            switchEditable: function () {
                this.editable = true
            },
            switchCloseTips: function (type) {
                var self = this
                if (type && this.autoInvestSwitch) {
                    this.setAutoInvest(2, function (data) {
                        self.autoInvestSwitch = false
                        self.sort=data.result
                    })
                }
                this.tipsSwitch = 0
            },
            closeSelector: function (e) {
                $(e.currentTarget).removeClass('z-actived')
            },
            closeTips: function () {
                this.tipsSwitch = 0
            },
            filterAmount: function (n) {
                var amount = n
                if (Number(n)) {
                    if (n > 1E5) {
                        amount = 1E5
                    } else if (n < 1E3) {
                        amount = 1E3
                    } else {
                        if (n % 1000) {
                            amount = Math.ceil(n / 1000) * 1000
                        }
                    }
                } else {
                    amount = 1E3
                }
                return amount
            },
            updateAmount: function (target) {
                $(target).blur()
            },
            saveAutoInvest: function () {
                var self = this,
                    type = self.projectType,
                    projectType = []

                type.forEach(function (item, index) {
                    if (item) {
                        projectType.push(index + 1)
                    }
                })
                if(this.periodMax<this.periodMin){
                    $.xDialog({content: '项目周期开始时间必须小于结束时间'})
                    return false
                }
                if (!projectType.length) {
                    $.xDialog({content: '请选择项目类型'})
                    return false
                }

                if(this.amount<1E3){
                    $.xDialog({content: '请选择投标金额'})
                    return false
                }

                $.xPost({
                    url: '/member/saveAutoInvestSet',
                    data: {
                        projectType: projectType.join(','),
                        investFrequency: self.initialData.investFrequency || self.init.investFrequency,
                        couponType:self.initialData.couponType||self.init.couponType,
                        amount: self.amount||self.init.amount,
                        startTime: self.startTime,
                        endTime: self.endTime,
                        investFlag: self.init.investFlag,
                        periodMin: self.periodMin||self.initialData.periodMin,
                        periodMax: self.periodMax||self.initialData.periodMax
                    },
                    callback: function (data) {
                        if (data.success) {
                            self.editable = false
                            self.queryAutoInvest()
                        } else {
                            $.xDialog({content: '保存失败'})
                        }
                    }
                })
            },
            setInvestFrequency: function (flag) {
                this.initialData.investFrequency = +flag
            },
            setCouponType: function (flag) {
                this.initialData.couponType = flag+''
                this.init.couponType = flag
                this.couponRemark= flag+''
            },
            textAgree:function(){
                this.closeButton=false
                 this.agreeText=true
            },
            buttonClose:function(){
                this.closeButton=true
            }
        }

    })

    function selector() {
        var $selector = $("body");
        $selector.on("click", ".j-selector2", function (e) {
            $(this).toggleClass("z-actived");
            e.preventDefault();
            return false;
        }).on("mouseleave", ".j-selector2", function () {
            $(this).removeClass("z-actived");
        });
        $selector.on("mouseenter", '.j-selector2 ul', function () {
            $(this).parent().addClass("z-actived");
        });
        $selector.on("click", ".j-selector2 li", function () {
            var text = $(this).html(),
                val = $(this).attr("value"),
                parent = $(this).parents();
            //只限可用状态下选择
            if (!$(this).hasClass("z-disabled")) {
                if (!$(this).hasClass('z-period')) {
                    parent.siblings("button").html(text);
                }
                parent.siblings(".j-selected-ipt").val(val);
            } else {
                return false;
            }

        });
    }
})()