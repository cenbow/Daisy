/*global define,require,$,environment,alert,env,globalPath,Vue,log,formValid*/
/* jshint undef: true, asi: true, boss: true */
define(['base','vue','regionSelector'], function (require, exports, module) {
    'use strict'
    var base = require('base'),
        RegionSelector=require('regionSelector')

    var HashRoute= function (pageList) {
        var self=this
        this.tag='#!'
        this.pageList=pageList
    }

    HashRoute.prototype.getName= function (url) {
        return url ? url.split(this.tag)[1] : location.hash.replace(this.tag, '')
    }

    HashRoute.prototype.goto= function (target,page) {
        target.$set('currentView',page)
        location.hash=this.tag+page

        var title=this.pageList[page]
        if(title){
            target.$set('pageTitle',title)
        }
    }

    var route=new HashRoute({
        'security':'安全认证',
        'password':'修改密码',
        'personal':'个人资料',
        'detail':'详细信息'
    })

    window.onhashchange = function () {
        route.goto(secList,route.getName())
    }


    //开启chrome调试工具
    Vue.config.devtools = true

    //过滤标点符号
    Vue.filter('removePunctuation', function (value) {
        return value?value.replace(/[,.'":\/-=_+`~#@!$%^&*\|\\]/g,''):value
    })

    var secList = new Vue({
        el: '#j-security',
        data: {
            backUrl: document.referrer,
            originUrl:env.path + '/mCenter/home',
            currentView:route.getName() || 'security',
            bindEmailUrl:env.path+'/mCenter/bindEmail?from='+location.href
        },
        components:{
            security:{
                template:'#t-security',
                data:{
                    bindEmailUrl:env.path+'/mCenter/bindEmail?from='+location.href
                },
                activate: function (done) {
                    var self=this
                    base.getAPI({
                        url: environment.globalPath + '/security/member/queryMemberAuthorize',
                        version: '1.0.2',
                        callback: function (data) {
                            setVueData(self,data,done)
                            self.$set('bindEmailUrl',env.path+'/mCenter/bindEmail?from='+location.href)
                        }
                    })
                },
                methods:{
                    gotoPersonal: function () {
                        route.goto(this.$root,'personal')
                        $(window).scrollTop(0);
                        this.from=true
                    },
                    openEmailLayer: function ( id,event) {
                        //$(event.currentTarget).parent().next().show()
                        $(id).show()
                        base.cover.show(null)
                    },
                    cancelEmail: function (id) {
                        $(id).hide()
                        base.cover.hide()
                    },
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
                                        $(layerId).hide()
                                        base.cover.hide()
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
                    bindSecurityCard: function (id) {
                        var _this = this

                        if(!isNaN(_this.$data.safecardSize)){
                            base.cover.show(id,true)
                            return false
                        }

                        base.getAPI({
                            url: environment.globalPath + '/security/bankCard/queryNonSecurityBankCard',
                            version: '1.0.2',
                            callback: function (data) {
                                if (data.success) {
                                    var safecard;
                                    for(var i=0;i<data.result.bankCardList.length;i++){
                                        if(data.result.bankCardList[i].cardType===2){
                                            safecard=true
                                        }
                                    }
                                    _this.$set('safecard',safecard)

                                    base.cover.show(id,true)
                                } else {
                                    alert(data.resultCodes[0].msg)
                                }
                            }
                        })


                    },
                    auditMemberInfos:function(id){
                        base.getAPI({
                            url:environment.globalPath+'/security/member/showMemberInfosSina',
                            version:'1.7.0',
                            callback:function (data) {
                                if(data.success){
                                    window.location.href=data.result
                                }else{
                                    base.xTips({content:data.resultCodes[0].msg})
                                }
                            }
                        })
                    },
                    gotoPassword: function () {
                        route.goto(this.$root,'password')
                    }
                }
            },
            password:{
                template:'#t-password',
                data: function () {
                    return {
                        oldPassword:'',
                        password:'',
                        checkNewPassword:'',
                        checked:{
                            oldPassword:false,
                            password:false,
                            checkNewPassword:false
                        }
                    }
                },
                methods:{

                    checkPwd: function (event) {

                        var $this=$(event.currentTarget)
                        var passwordReg=/^(?![^a-zA-Z]+$)(?!\D+$).{6,16}$/,
                            name=$this.prop('name'),
                            checked=this.$get('checked.'+name)

                        if(passwordReg.test($this.val())){
                            $this.parent().next().hide()
                            checked=true
                        }
                        else{
                            $this.parent().next().show()
                            checked=false
                        }
                        this.$set('checked.'+name,checked)
                    },
                    submitPwd: function () {
                        var $recheckTips=$('.j-pwd-reck').parent().next('p')
                        if(this.checkNewPassword!==this.password){
                            $recheckTips.show()
                            return false
                        }else{
                            $recheckTips.hide()
                        }
                        var checked=true,
                            self=this
                        Object.keys(this.checked).map(function (item) {
                            if(!self.checked[item]){
                                checked=false
                            }
                        })
                        if(!checked){
                           return false
                        }
                        base.getAPI({
                            url:environment.globalPath+'/security/member/updatePassword',
                            version: '1.0.2',
                            data:$("#j-regPassword-form").serializeArray(),
                            callback:function(data){
                                if(data.success){
                                        $('#j-setPwdSuccess').show();
                                            base.cover.show(null)
                                }else{
                                    alert(data.resultCodes[0].msg)
                                }
                            }
                        })
                    }
                    //quit:function(id){
                    //    setTimeout(
                    //        $.get(location.href,function(){
                    //        $(id).attr('href',environment.globalPath +'/mstation/login')
                    //    }),500)
                    //
                    //}
                }
            },
            personal:{
                template:'#t-personal',
                activate: function (done) {
                    var self=this
                    base.getAPI({
                        url: environment.globalPath + '/security/banlance/queryMemberBalance',
                        callback: function (data) {
                            $('#j-title').text('个人资料')
                            setVueData(self,data,done)
                            if(self.from){
                                self.$root.$set('backUrl','#!security')
                            }else{
                                self.backUrl=document.referrer
                            }
                        }
                    })

                },
                methods:{
                    gotoDetail: function () {
                        route.goto(this.$root,'detail')
                         window.location.reload()
                    },
                    openNicknameLayer: function (id) {
                        $(id).show()
                        base.cover.show(null)
                    },
                    cancleNickname: function (id) {
                        $(id).hide()
                        base.cover.hide()
                    },
                    saveNickname: function (inputId, layerId) {
                        var $input=inputId?$(inputId):$(event.currentTarget),
                            userName=$input.val(),
                            nicknameReg=/^([A-Za-z]|[\u4E00-\u9FA5])+$/
                        if(nicknameReg.test(userName)){
                            base.getAPI({
                                url:environment.globalPath+'/security/member/saveUserName',
                                data:{userName:userName},
                                version: '1.0.2',
                                callback:function(data){
                                    if(data.success){
                                        $(layerId).hide()
                                        base.cover.hide()
                                        $('.j-setNickname').text(userName).addClass('z-nicknameActived')
                                    }
                                    else{
                                        alert(data.resultCodes[0].msg)
                                        $input.val('')
                                    }
                                }
                            })
                        }else{
                            // alert('昵称只能是汉字或字母')
                            base.xTips({content:'昵称只能是汉字或字母'})
                            $input.val('')
                        }


                    }
                }
            },
            detail:{
                template:'#t-detail',
                activate: function (done) {
                    var self=this
                    base.getAPI({
                        url: environment.globalPath + '/security/member/queryMemberInfo',
                        version: '1.0.2',
                        callback: function (data) {
                            $('#j-title').text('详细信息')
                            setVueData(self,data,done)
                            if(!data.result.income){
                                self.$data.income=0
                            }
                            if(!data.result.highEdu){
                                self.$data.highEdu=0
                            }
                            if(!data.result.occupation){
                                self.$data.occupation=0
                            }
                            //现居地选择
                            var presentSelector=new RegionSelector($('#j-residence'),1)
                            presentSelector.init(function (id,text) {
                                secList.$children[0].city=id
                                secList.$children[0].formatAreaFullName=text
                                $('#j-residence').hide()
                                base.cover.hide()
                                $('.j-backBtn').hide()
                                route.goto(secList,'detail')
                            })

                            //户籍地选择
                            var registeredSelector=new RegionSelector($('#j-domicile'),1)
                            registeredSelector.init(function (id,text) {
                                secList.$children[0].censusRegisterId=id
                                secList.$children[0].formatCensusRegisterName=text
                                $('#j-domicile').hide()
                                $('#detail').show()
                                base.cover.hide()
                                $('.j-backBtn').hide()
                                route.goto(secList,'detail')
                            })
                            self.$root.$set('backUrl','#!personal')
                        }
                    })
                },
                methods: {
                    openSelectLayer:function(id){
                        var $backBtn=$('.j-backBtn');
                        $(id).show()
                        $backBtn.show()
                        base.cover.show(null);
                        $backBtn.on('click',function(){
                            $(id).hide()
                            base.cover.hide()
                            $(this).hide()
                        })
                    },
                    openDetailAddressLayer: function (id) {
                        $(id).show()
                        base.cover.show(null)
                    },
                    cancleDetailAddress: function (id) {
                        $(id).hide()
                        base.cover.hide()
                    },
                    sureDetailAddress:function(id){
                        $(id).hide()
                        base.cover.hide()
                        $('.j-sureDetailAddress').text($('#j-inputAddress').val()).removeClass('f-detailAddress')
                    },
                    submitDetailInfo:function(){

                        //做并发处理
                        var $this=$(this);
                        var repeatedTime=$this.data('repeatedTime'),
                            now=new Date().getTime();

                        if(repeatedTime&&now-repeatedTime<=(1000||500)){
                            return false;
                        }else{
                            $this.data('repeatedTime',now);
                        }
                        //做并发处理
                        var data = $("#j-previewFor").serializeArray()
                        data[2].value=$('#j-inputAddress').val();
                        base.getAPI({
                            url: environment.globalPath + "/security/member/saveMemberInfo",
                            type:'POST',
                            version: '1.0.2',
                            data: data,
                            callback: function(data){
                                if (data.success) {
                                    base.xTips({content:'保存成功'})
                                }
                                else {
                                    base.xTips({content:'信息保存失败'})
                                }
                            }
                        })
                    }

                }
            }
        },
        watch:{
            currentView: function (nData,oData) {
                var hashUrl=''
                switch(nData){
                    case 'security':
                        hashUrl=env.path + '/mCenter/home'
                        break
                    case 'password':
                    case 'personal':
                        hashUrl='#!security'
                        break
                    case 'detail':
                        hashUrl='#!personal'
                        break
                }
                this.$set('backUrl',hashUrl)

            }
        }
    })




    /**
     *
     * @param target
     * @param data
     * @param done
     */
    function setVueData(target,data,done){
        if (data.success) {
            target.$data = data.result
            if(done){
                done()
            }
        }
        else {
            alert(data.resultCodes[0].msg)
        }
    }
})