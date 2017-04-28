<style scoped>
.m-password-grap{
    width: 100%;
    background: #eeeeee;
    height: 100%;
}
    /*修改密码页面*/
    .u-password{
        width: 90%;
        margin: auto;
        background: #eeeeee;
        padding: 20px 0 0;
        text-align: center;
    }
    .u-password .f-inputPassword{
        width: 100%;
        height: 60px;
        line-height: 80px;
        border-bottom: 1px solid #ccc;
        margin-bottom: 20px;
    }
    .u-password .f-inputPassword span{
        width: 80px;
        text-align: left;
    }
    .u-password span{
        color: #666;

    }
    .u-password .f-reminder{
        color: #666;
    }
    .u-password .u-saveNewPasswordBtn{
        background: #d74147;
        color: #fff;
        line-height: 40px;
        width: 100%;
        border-radius: 4px;
        margin: 20px 0;
        font-weight: bold;
    }
    .z-colorRed{
        color: #d74147;
    }
</style>
<template>
    <div class="m-password-grap">
    <div class="u-password" id="j-password">
        <form action="" id="j-regPassword-form" method="post" class="j-validform">
            <div class="f-inputPassword j-inputPassword">
                <span class="f-fl">原密码</span>
                <input class="j-oldPwd " @blur="checkPwd" type="password" name="oldPassword" placeholder="输入当前密码" v-model="oldPassword" lazy/>
            </div>
            <p class="f-dn f-fs14 f-errorTip z-colorRed">长度6-16位至少包含数字和字母</p>

            <div class="f-inputPassword j-inputPassword">
                <span class="f-fl">新密码</span>
                <input class="j-newPwd " @blur="checkPwd" type="password" name="password" placeholder="输入新密码" v-model="password" lazy/>
            </div>
            <p class="f-dn f-fs14 f-errorTip z-colorRed">长度6-16位至少包含数字和字母</p>

            <div class="f-inputPassword j-inputPassword">
                <span class="f-fl">确认新密码</span>
                <input class="j-pwd-reck" @blur="checkPwd" type="password" name="checkNewPassword" placeholder="确认新密码" v-model="checkNewPassword" lazy/>

            </div>
            <p class="f-dn f-fs14 f-errorTip z-colorRed">您两次输入的密码不一致</p>
            <span class="f-reminder f-fs12 ">密码长度6-16位，至少包含数字和字母</span>

            <div>
                <input class="u-saveNewPasswordBtn" type="button" value="确认" @click="submitPwd"/>
            </div>
        </form>
    </div>
    </div>
    <!--##设置密码成功后弹窗-->
    <div class="u-popup f-popup f-dn " id="j-setPwdSuccess">
        <ul>
            <li class=" f-fs18">您的密码已修改，请重新登录</li>
            <li class="f-secondLi">
                <a  href="{{siteurl}}/mCenter/logout" id="j-quit">退出</a>
                <a href="{{siteurl}}/mCenter/logout" class="f-secondA " >
                    <span >重新登录</span>
                </a>
            </li>
        </ul>
    </div>
</template>
<script>
    import Base from 'js/base'
    export default{
        data(){
        return{
            oldPassword:'',
            password:'',
            checkNewPassword:'',
            checked:{
                oldPassword:false,
                password:false,
                checkNewPassword:false
            },
            siteurl: this.$root.siteurl,
            passwordData: {}
        }
    },
    created:function(){

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
            Base.getAPI({
                url:Base.path+'/security/member/updatePassword',
                version: '1.0.2',
                data:$("#j-regPassword-form").serializeArray(),
                callback:function(data){
                    if(data.success){
                        $('#j-setPwdSuccess').show();
                        Base.cover.show(null)
                    }else{
                        alert(data.resultCodes[0].msg)
                    }
                }
            })
        }

    }
    }
</script>