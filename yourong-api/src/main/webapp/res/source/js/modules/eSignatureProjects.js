/**
 * Created by lyl on 2016/7/15.
 */
define(['base', 'vue','zepto'], function (require, exports, module) {
    'use strict'
    var base = require('base');
    Vue.filter('amountFormat', function (amount, format) {

        switch (format) {
            case 'split':
                return splitAmount(amount);
                break;
        }
        // 用于给数字添加千分号
        function splitAmount(num) {
            num = num + "";
            var re = /(-?\d+)(\d{3})/;
            while (re.test(num)) {
                num = num.replace(re, "$1,$2");
            }
            return num;
        }
    });
   new Vue({
        el:'#j-eSignatureProjectList',
        data:{
            projectListData:{},
            signWay:'',
            pageNavi:{}
        },
        created:function(){
           this.getList(1)
        },
       methods:{
           gotoPage: function (pageNo) {
               this.getList( pageNo)
               window.scrollTo(0,0)
           },
           getList:function (pageNo) {
               var _this=this
               base.getAPI({
                   url:environment.globalPath+'/security/transaction/queryUnsignList',
                   version:'1.6.0',
                   data:{pageNo:pageNo},
                   callback:function(data){
                       if(data.success){
                           _this.signWay=data.result.signWay
                           _this.projectListData=data.result.unSingList;
                           _this.setPageNavi(data.result.unSingList.totalPageCount, data.result.unSingList.pageNo)
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
           },
           signAll:function(){
               base.getAPI({
                   url:environment.globalPath+'/security/transaction/signAllContract',
                   version:'1.6.0',
                   callback:function (data) {
                       if(data.success){
                           location.reload()
                       }else{
                            base.xTips({
                                content:data.resultCodes[0].msg,
                                type:data.resultCodes[0].type
                            })
                       }
                   }
               })
           },
           signOne:function (transactionId) {
               base.getAPI({
                   url:environment.globalPath+'/security/transaction/signContract',
                   version:'1.6.0',
                   data:{transactionId:transactionId},
                   callback:function (data) {
                       if(data.success){
                           window.location.herf=data.result
                           
                       }else{
                           base.xTips({
                               content:data.resultCodes[0].msg,
                               type:data.resultCodes[0].type
                           })
                       }
                   }
               })
           },
           showTips:function(id){
               $(id).show()
               base.cover.show(null)
           },
           closeTips:function (id) {
               $(id).hide()
               base.cover.hide()
           }

       }
    })

})
