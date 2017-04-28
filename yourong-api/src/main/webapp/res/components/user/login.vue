<style>
    @import url(../../source/css/modules/login.css);
</style>
<template>
    <section class="g-bd">
        <div class="m-reg-form">
            <!--openIdIsBind是什么鬼? 可能与微信openid有关-->
            <div class="u-tips-login" v-if='showErrorTips||openIdIsBind'></div>
            <div class="u-ipt-group">
                <input type="text" name="username" value="ade" class="f-round f-ipt-w430 u-ipt-text u-ipt-str v-loginid" placeholder="请输入手机号或昵称" v-model="username"/>
            </div>
            <div class="u-ipt-group">
                <input type="password" name="password" datatype="s6-16" @copy="return false;" @paste="return false;"
                       class="f-round u-ipt-text f-ipt-w430 v-pwd" value="ttt555" v-model="password" placeholder="密码长度6-16位，至少包含数字和字母"/>
            </div>

            <input type="hidden" name="pngCode" id="j-pngcode"/>
            <input type="hidden" name="openId" id="j-openId" value=""/><br/>
            <button type="submit" class="u-reg-btn" @click="submitLogin">登录
            </button>
            <div class="u-reg-tips">
                没有账号？<a v-link="{path:'/user/register'}" class="f-fc-gray">注册</a>
            </div>
        </div>
        <div class="m-input-vercode f-cf z-hide" @click="vercodeWrapChoice" @click="vercodeWrap"
             @click="vercodeWrapChange">
            <input type="text" nullmsg="请输入验证码" class="f-round u-ipt-text f-ipt-w430 z-hide"
                   placeholder="验证码" v-model="pngCode"/>
            <img :src="" id="j-img-vercode2" alt="验证码" class="u-img-vercode u-img-vercode2"/>
            <span class="m-val-tips"> &nbsp; </span>
        </div>
    </section>
</template>
<script>
    import Base from 'js/base'

    export default {
        data(){
            return {
                tips: {
                    iderror: '请填写正确的手机号码或昵称1',
                    idNull: '请填写手机或昵称',
                    vercodeNull:'请输入验证码'
                },
                mobileReg:/^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/,
                pwdReg:/^(?![^a-zA-Z]+$)(?!\D+$).{6,16}$/
            }
        },
        created: function () {
            if(this.$root.logined){
                this.$route.router.go('/member/home')
            }
        },
        methods: {
            isUsername () {
                var reg = /^[\u4E00-\u9FA5\uf900-\ufa2dA-Za-z]+$/,
                        username=this.username||''
                if (reg.test(username)) {
                    var strLength = getStrLeng(username)
                    return strLength >= 3 && strLength <= 14
                }
                return false

                function getStrLeng(str) {
                    var realLength = 0
                    var len = str.length
                    var charCode = -1
                    for (var i = 0; i < len; i++) {
                        charCode = str.charCodeAt(i)
                        if (charCode >= 0 && charCode <= 128) {
                            realLength += 1
                        } else {
                            // 如果是中文则长度加2
                            realLength += 2
                        }
                    }
                    return realLength
                }
            },
            isMobile(){
                return this.mobileReg.test(this.username)
            },
            isPassword(){
              return this.pwdReg.test(this.password)
            },
            submitLogin (){
                let self = this
                if((this.isUsername()||this.isMobile())&&this.isPassword()){
                    self.xPost({
                        url:this.$root.siteurl+'/mstation/logined',
                        data:{
                            username:self.username,
                            password:self.password,
                            loginSource:3,
                            openId:'',
                            pngCode:self.pngCode
                        },
                        callback: function (data) {
                            if(data.success){
                                location.reload()
                            }
                        }
                    })
                }else{
                    //错误处理
                }
            },
            xPost: function (config) {
                var type = "POST",
                        datatype = "json",
                        async = true
                if (typeof config === "object") {
                    type = config.type ? config.type : type
                    datatype = config.datatype ? config.datatype : datatype
                    async = config.async ? config.async : async
                    var xToken = $("#xToken").val()
                    if (typeof xToken !== "undefined" && xToken !== "") {
                        if (config.hasOwnProperty("data")) {
                            config.data.xToken = xToken
                        } else {
                            config.data = {
                                xToken: xToken
                            }
                        }
                    }
                } else {
                    throw new Error("xPost: config参数为空或不是对象")
                }
                $.ajax({
                    type: type,
                    url: config.url,
                    data: config.data,
                    datatype: datatype,
                    async: async,
                    success: config.callback,
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        // console.log(XMLHttpRequest)
                        // console.log(textStatus)
                        // console.log(errorThrown)
                    },
                    statusCode: {
                        403: function () {
                            alert('请刷新页面')
                        },
                        500: function () {
                            window.location.href = environment.globalPath + "/500"
                        }
                    }

                });
            }
        }
    }
</script>