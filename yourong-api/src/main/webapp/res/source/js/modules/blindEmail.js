/**
 * Created by lyl on 2016/9/10.
 */
define(['base'],function (require) {
    'use strict'
    var base=require('base')

    new Vue({
        el:'#j-blindEmail',
        data:{
            pageTitle:'绑定邮箱',
            isSend:false,
            isBindEmail:false
        },
        created:function () {
            var self=this
            base.getAPI({
                url: environment.globalPath + '/security/member/queryMemberAuthorize',
                version: '1.0.2',
                callback: function (data) {
                    self.isBindEmail=data.result.isBindEmail
                    if(data.result.isBindEmail){
                        self.pageTitle='修改邮箱'
                    }
                }
            })
        },
        methods:{
            saveEmail: function (layerId, inputId) {
                var $input = inputId ? $(inputId) : $(event.currentTarget),
                    email = $input.val(),
                    emailReg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/,
                    _this = this,
                    isSend=false;

                if (emailReg.test(email)) {
                    base.getAPI({
                        url: environment.globalPath + '/security/member/bindEmail',
                        data: {email: email},
                        version: '1.0.4',
                        callback: function (data) {
                            if(data.success){
                                _this.$set('isSend',true)
                                _this.isSend=true
                                alert('一封验证邮件已发送至' + email +
                                    '请登录您的邮箱查收并通过邮件验证'
                                )
                            }
                            else{
                                alert(data.resultCodes[0].msg)
                            }

                        }
                    })
                } else {
                    var tips = '请输入正确的邮箱地址'
                    if (!email.length) {
                        tips = '请输入邮箱地址'
                    }
                    alert(tips)
                    $input.val('')
                }
            },
            checkEmail:function (inputId) {
                var $input = inputId ? $(inputId) : $(event.currentTarget),
                    email = $input.val()
                base.getAPI({
                    url:environment.globalPath+'/security/member/checkBindEmailStatus',
                    data:{email: email},
                    version:'1.0.4',
                    callback:function (data) {
                        if(data.result){
                            alert('邮箱绑定成功！')
                            window.location.href=environment.globalPath+'/mCenter/security?form=1'
                        }else{
                            alert(data.resultCodes[0].msg)
                        }
                    }
                })
            }
        }

    })
})
