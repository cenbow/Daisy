/*global define,require,$,environment,console,env,globalPath,log,fz*/
define(['base', 'vue'], function (require, exports, module) {
    'use strict'
    var base = require('base');
    Vue.filter('formatName', function (value) {
        return value.split('期')[0]
    });
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
    Vue.filter('amountFloor',function(amount){
        return parseInt(amount)
    })
    // var pageNo=$('.u-pnav-crt').text()
    new Vue({
        el:'#projectsList',
        data:{
            projectsListData:'',
            pageNavi:{}

        },
        created:function (){
           this.getList(1)
        },
        methods:{
            gotoPage: function (pageNo) {
                this.getList( pageNo)
                window.scrollTo(0,0)
            },
            getList:function(pageNo){
                var self=this;
                base.getAPI({
                    url:environment.globalPath+'/project/queryProjectList',
                    data:{
                        statusCode:'all',
                        pageNo:pageNo,
                        querySource:'mStation'
                    },
                    version:'1.3.0',
                    callback:function(data){
                        if(data.success){
                            self.projectsListData=data.result;
                            // self.projectsListData.data.availableBalance=(self.projectsListData.data.availableBalance).toFixed(0)
                            console.log(self.projectsListData.data.availableBalance)
                            self.setPageNavi(data.result.totalPageCount, data.result.pageNo)
                        }
                    }
                })

            },
            //分页
            setPageNavi: function (totalPageCount, pageNo) {

                var countArray = []

                if (totalPageCount > 7) {

                    if (pageNo <= totalPageCount - 3) {

                        if (pageNo <= 4) {

                            for (var i = 1; i <= 7; i++) {
                                //利用push方法将页码一个个放入countArray数组
                                countArray.push({'index': i})
                            }
                        } else {

                            for (var l = pageNo - 3; l <= pageNo + 3; l++) {
                                //利用push方法将页码一个个放入countArray数组
                                countArray.push({'index': l})
                            }
                        }
                    } else {

                        for (var k = pageNo - 3; k <= totalPageCount; k++) {
                            countArray.push({'index': k})
                        }
                    }
                }
                else {
                    for (var j = 1; j <= totalPageCount; j++) {
                        countArray.push({'index': j})
                    }
                }

                this.pageNavi = {
                    list: countArray,
                    pageNo: pageNo,
                    totalPageCount: totalPageCount
                }
            }
        }
    })
})