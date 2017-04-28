define(['base'],function (require, exports, module){
    'use strict'

    log('最新代码b')

    /* 除掉移动端默认的点击图片出现全图浏览 */
    $('img').on('click', function (e) {e.preventDefault()})

    var base = require('base'),
        logined = $('#j-newComer').data('logined'),
        path = environment.globalPath,
        invoke= path + '/activity/dynamicInvoke',
        hook = new AppHook(os),
        encryptionId = base.getUrlParam('encryptionId') || ''

    Vue.config.devtools = true

    var newComer = new Vue({
        el: '#j-newComer',
        data: {
            //后端接口地址
            urls: {

            },
            loginUrl: path + '/mstation/login?from=' + location.href,
            logined: logined,
            encryptionId: encryptionId,
            initData: {}, // 初始化接口返回数据
        },

        props: ['os'],

        created: function(){
            var self = this
            if (receiveData.success) {
                nit(receiveData.result)
            }else {
                log(receiveData)
            }

            function init(result) {
                self.initData = {

                }
            }
        },

        computed: {
            //新手任务按钮文案
        }
    })
})
