/**
 * Created by lyl on 2016/9/9.
 */
define(['base'], function (require, exports, module) {
    'use strict'
    var base=require('base');
    var path = environment.globalPath,
        serverTime = +environment.serverDate,
        serverDate = new Date(serverTime),
        today = serverDate.getDate(),
        hook = new AppHook(os),
        isLogined=false

    if(isNeedYRWtoken){
        isLogined=!!encryptionId
    }else{
        isLogined=!!logined
    }

    Vue.config.devtools = true

    Vue.filter('raceHours', function (string) {
        return string ? string.replace(/:00$/g, '') : string
    })

    window.vm = new Vue({
        el:'#newSixGift',
        data:{
            loginUrl:isLogined?'javascript:;':(os>2?env.path + '/mstation/login?from=' + location.href:'javascript:;'),
            initData: window.receiveData.result||{},
            os:window.os,
            bindEmailUrl:os>2?env.path+'/mCenter/bindEmail?from='+location.href:'javascript:;',
            sinapayUrl: env.path + '/mCenter/sinapay?isNeedYRWtoken=Y&&from=' + location.href,
            weixinLeadUrl: env.path + '/activity/post/weixinService?isNeedYRWtoken=Y' + location.href
            // completedInfoUrl:env.path+'/mCenter/security#!personal?from='+location.href
        },
        created:function () {
            console.log('数据:', this.initData)
            console.log('首次投资：', this.initData.isInvestment)
            console.log('绑定邮箱：', this.initData.isBindEmail)                                                                                                                                                                 
            console.log('完善信息：', this.initData.isCompletedInfo)
        },
        methods:{
            focusingWeixin:function () {
                alert('请打开微信，搜索"yourong_weixin"，关注并绑定账号')
            },
            MemberBehavior: function (anchor, token) {
                if (token) {
                    base.memberBehavior(anchor, token)
                } else {

                }

                //需要用另外一种数据请求方式，APPHOOK
            },
            gotoLogin: function (event) {
                if (hook.logined) {
                    // alert('logined:'+true)
                } else {
                    hook.login($(event.currentTarget))
                }

            }
        }
    })
})