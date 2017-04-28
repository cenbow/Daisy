<style scoped>
    @import url(../../source/css/modules/login.css);
</style>
<template>
    <!--##/header-->
    <div class="u-mobile-guide">
        <span></span>
        <ul>
            <li>1</li>
            <li>2</li>
            <li>3</li>
        </ul>
    </div>
    <section class="g-bd">
        <form data-action="{{siteurl}}/mstation/register/saveMember" method="POST" class="m-reg-form j-form-xpost j-validform" data-page="register" id="j-reg-form">
            #CSRFToken("xToken")
            <input type="hidden" id="j-reg-mobile"/>
            #onceToken()
            <div class="u-ipt-group f-pos-r">
                <input type="tel" name="mobile" oncopy="return false;" onpaste="return false;" id="j-cpn" autocomplete="off" nullmsg="手机号码不能为空" errormsg="请输入正确的手机号码" class="f-round f-ipt-w430 u-ipt-text u-ipt-str" placeholder="请输入手机号码" @keypress="cpnCheck" @keyup="cpnCheckKeyup" @blur="cpnCheckBlur"/>
                <span class='m-val-tips'></span>
                <div class="f-mob-sha f-dn" id="j-mobile"></div>
            </div>
            <div class="m-input-vercode f-cf f-pos-r"  @click="vercodeWrapChoice" @click="vercodeWrap" @click="vercodeWrapChange">
			<span class="u-xtips-wrap">
				<input type="text" id="j-vercode" autocomplete="off" nullmsg="请输入图形验证码" class="u-ipt-text u-ipt-vercode j-vercode" placeholder="输入图形码" @focus="vercodeCheck" @keyup="vercodeSendMessage" />
			</span>
                <img src="{{siteurl}}/mstation/validateCode" alt="验证码" title="点击重新获取验证码" id="j-img-vercode" class="u-img-vercode" />
			<span id="j-phonecode-wrap" class="u-xtips-wrap u-phonecode-wrap z-hide">
				<input type="text" name="phonecode" autocomplete="off" id="j-phonecode" placeholder="输入手机验证码" class="f-clear f-round u-ipt-text f-ipt-w248 u-ipt-phonecode" />
			</span>
			<span class="f-round u-tips-vercode f-tips-gray f-dn">
			获得手机验证码
			</span>
                <span class="m-val-tips"></span>
            </div>
            <div class="u-ipt-group">
                <input type="password" name="password" oncopy="return false;" onpaste="return false;" class="f-round u-ipt-text f-ipt-w430 v-pwd" placeholder="密码6-16位，至少包含数字和字母" />
            </div>
            <div class="u-ipt-group">
                <input type="password" name="repassword" oncopy="return false;" onpaste="return false;" recheck="password" class="f-round f-ipt-w430 u-ipt-text v-pwd-reck" placeholder="再次输入密码" />
            </div>
            <div class="u-ipt-group f-pos-r">
                <input type="tel" id="j-refercode" autocomplete="off" value="$!{mobile}" $!{disabled} ignore="ignore" class="f-round f-ipt-w430 u-ipt-text" placeholder="推荐人手机号码，选填   $!{tureName}" @blur="referCodeBlur" @keyup="referCodeKryup" @postpaste="referCodeKryup"/>
			<span class="m-val-tips">
                #if($!{tureName})
                    您的推荐人：$!{tureName}
                #end
            </span>
            </div>
            ##推荐人ID
            <input type="hidden" name="referCode" id="j-refercode-val" value="$!{shortUrl}" >
            <input type="hidden" name="tid"   value="$!{request.getParameter('tid')}" >
            <input type="hidden" name="registerTraceSource" value='$!{session.getAttribute("register_TraceSource")}'/>
            <input type="hidden" name="registerTraceNo"  value='$!{session.getAttribute("register_TraceNo")}'/>
            <input type="hidden" name="referSource"  value="$!{referSource}"/>
            <input type="hidden" name="checkType" id="j-checkType" value="1"/>
            <input type="hidden" name="pngCode" id="j-pngcode"/>
            <input type="hidden" name="loginSource" value="0"/>
            <label class="f-clear f-mgl-8">
                <input type="checkbox" checked="checked" name="agree" id="j-agree" />&nbsp;我已阅读并同意
                <a class="f-fc-red" target="_blank" href="$siteurl/res/page/register-agreement.html">《有融网服务协议》</a>
            </label>
            <button type="submit" class="u-reg-btn j-reg-btn" >立即注册</button>
        </form>
    </section>
</template>
<script>
    import Base from 'js/base'
    require(['js/login'])
    export default {
        data(){
        return{
            siteurl:this.$root.siteurl
        }
    },
    created:function(){

    },
    methods:{

    }

    }
</script>