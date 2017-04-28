/**
 * Created by lyl on 2016/6/30.
 */
/*global define,require,$,environment,console,env,globalPath,log,fz*/
define(['base', 'vue','xjs/modules/effects','frozen'], function (require, exports, module) {

    var bannerSlider = new fz.Scroll('.ui-slider', {
        role: 'slider',
        indicator: true,
        autoplay: true,
        interval: 3000
    })

    var effects = require('xjs/modules/effects')


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
    new Vue({
        el:'#projectsList',
        data:{
            projectsListData:'',
            noticeProjectsListData:''
        },
        created:function(){
            var self=this;
            base.getAPI({
                url:environment.globalPath+'/project/queryProjectList',
                data:{statusCode:'all'},
                version:'1.3.0',
                callback:function(data){

                    if(data.success){
                        var result = data.result,
                            novicePid = self.novicePid?+self.novicePid:0
                        result.data = self.filterList(result.data,novicePid,0)
                        self.projectsListData=result

                        setTimeout(function () {
                            var noticeData = self.noticeProjectsListData

                            if(noticeData){
                                result.data = self.filterList(result.data,novicePid,
                                    noticeData.pid)
                                self.projectsListData=result
                            }




                        },300)
                    }
                }
            })
            base.getAPI({
                url:environment.globalPath+'/project/queryProjectList',
                data:{statusCode:'notice'},
                version:'1.3.0',
                callback:function(data){
                    if(data.success){
                        if(data.result.data.length){
                            setTimeout(function () {
                                effects.processCircle('.u-project-circle')
                                effects.projectCountdown()
                            },100)
                            self.noticeProjectsListData=data.result.data[0]
                        }

                    }
                }
            })

        },
        methods:{
            filterList: function (list,noviceId,noticeId) {
                var newList = []
                list.forEach(function (item) {

                    if(item.pid!==noviceId&&item.pid!==noticeId){
                        newList.push(item)
                    }
                })
                return newList
            }
        }
    })

})
