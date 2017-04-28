/**
 * Created by lyl on 2016/8/9.
 */
define(['base', 'vue'], function (require, exports, module) {
    'use strict'

    var base = require('base')
 var setPayPassword=new Vue({
        el:"#setPayPassword",
        data:{
            isSet:false,
            isPaymentCipher:false
        },
        created:function () {
            var _this=this
            base.getAPI({
                url:environment.globalPath +'/security/member/synPayPasswordFlag',
                version:'1.7.0',
                callback:function (data) {
                    _this.isSet=data.result
                    if (_this.isSet){
                        base.getAPI({
                            url:environment.globalPath +'/security/member/queryWithholdAuthority',
                            version:'1.7.0',
                            callback:function (data) {
                                _this.isPaymentCipher=data.result
                            }
                        })
                    }
                }
            })


        },
        methods:{
            gotoSetPayPwd:function (type) {
                base.getAPI({
                    url:environment.globalPath +'/security/member/handlePayPassword',
                    data:{
                        handleType:type
                    },
                    version:'1.7.0',
                    callback:function (data) {
                        window.location.href=data.result
                    }
                })

            },
            gotoSetPaymentCipher:function (type,mType) {
                if(!this.isSet){
                    $('#j-SetPayPwdTips').show()
                    base.cover.show(null)
                    return false
                }else{
                    base.getAPI({
                        url:environment.globalPath+'/security/member/handWithholdAuthority',
                        version:'1.7.0',
                        data:{
                            type:type,
                            mType:mType
                        },
                        callback:function (data) {
                             window.location.href=data.result
                        }
                    })
                }
            },
            closeBlock:function (id) {
                $(id).hide()
                base.cover.hide()
            }
            
        }
    })
})