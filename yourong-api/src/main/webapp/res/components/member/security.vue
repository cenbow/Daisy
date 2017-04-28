<style scoped>
    .m-safety-grap {
        background: #eeeeee;
    }

    .m-safety-grap ul {
        width: 90%;
        padding: 0 5%;
    }

    .m-safety-grap .f-securityList li {
        width: 100%;
        vertical-align: middle;
        padding: 36px 0 36px 0;
        border-bottom: 1px solid lightgray;
        float: left;
    }

    .m-safety-grap .f-checked {

        float: left;
        width: 20px;
        height: 20px;
        border-radius: 10px;
    }

    .u-titleLine {
        float: left;
        margin: 0 0 0 2%;
    }


    .f-safety-name i {
        font-size: 12px;

    }

    .m-safety-grap ul li .f-sinaVerification {

        float: right;
        margin: 8px 0 0 0;
        color: #fff;
        background: #999;
        width: 100px;
        text-align: center;

    }

    .m-safety-grap ul li em {
        color: #fff;
    }

    .m-safety-grap ul li em a {

        color: #fff;
    }

    .f-round {
        border-radius: 13px;
    }

    .f-safety-open, .f-safety-number {
        float: right;

        color: #999;
    }

    .f-safety-key {

        color: #fff;
        background: #999;
        width: 100px;
        display: block;
        float: right;

        text-align: center;
    }

    .f-safety-key a {
        color: #fff;
        display: block;
    }

    .f-immediately-reputation {
        width: 182px;
        float: right;
        background: #d74248;
        text-align: center;

    }

    .f-immediately-reputation a {
        color: #fff;
    }

    .f-immediately-open {
        width: 100px;
        float: right;
        background: #d74147;
        text-align: center;
        color: #fff;
    }

    .f-immediately-open a {
        color: #fff;
    }

    .f-immediately-reputation10 {
        width: 198px;
        float: right;
        background: #d74147;
        text-align: center;
    }
    .z-clicked{
        background: #999;
    }
    .f-immediately-reputation10 a {
        color: #fff;
    }
    .f-name{
        width:60%;
    }
    .f-name .f-safety-name{
        text-align: right;
    }
    .f-safety-card {
        width: 90%;
        position: fixed;
        top: 20%;
        left: 5%;
        z-index: 300;
    }

    .f-toolTip {
        background: #fffab0;
        display: block;
        text-align: center;
        line-height: 20px;
        font-size: 14px;
        font-weight: bold;
        padding: 20px 0 20px 0;
        border-top-left-radius: 8px;
        border-top-right-radius: 8px;
    }

    .f-safety-step {
        padding: 20px 0 0 0;
        background: #fff;
        border-bottom-left-radius: 5px;
        border-bottom-right-radius: 5px;
    }

    .f-safety-step > ul > li {
        margin: 0 0 0 10px;
        padding: 0 !important;
        line-height: 35px;
        border: none !important;
        color: #333;
    }

    .u-openQuickPayBtn {
        background: #d74147;
        color: #fff;
        line-height: 40px;
        width: 80%;
        border-radius: 6px;
        margin: 20px 10%;
        font-weight: bold;
    }

    .u-bankMsg {
        width: 90%;
        background: #eeeeee;
        border-radius: 8px;
        position: fixed;
        top: 20%;
        left: 5%;
        z-index: 200;
    }

    .u-bankMsg .f-choiceTip {
        line-height: 34px;
        color: #333;
    }

    .u-bankMsg .f-bankCard-list li {
        width: 100%;
        background: #fff;
        padding: 20px 0;
    }

    .u-bankMsg .f-bankCard-list .z-clicked {
        background: #eeeeee;
    }

    .u-bankMsg .f-backCardMsg {
        display: inline-block;
        margin-left: 10%;
    }

    /*快捷卡标志*/
    .u-bankMsg .u-shortcut {
        background: #999;
        border-top-left-radius: 15px;
        border-bottom-left-radius: 15px;
        width: 84px;
        height: 23px;
        margin-top: 15px;
        color: #fff;
        font-size: 14px;
        text-align: center;
        line-height: 24px;
        font-style: italic;
        font-weight: bold;
    }

    .u-bankMsg .f-bankLogo {
        width: 45px;
        height: 45px;
    }

    .f-marginLeft20 {
        margin-left: 20px;
    }

    .f-marginRight10 {
        margin-right: 10px;
    }
    .z-colorRed {
        color: #d74147;
    }
    /*绑定邮箱*/
    .u-inputEmail {
        width: 90%;
        background: #fff;
        border-radius: 5px;
        text-align: center;
        position: fixed;
        top: 40%;
        left: 5%;
        padding: 10px 0 0;
        z-index: 200;
    }

    .u-inputEmail .f-inputEmail {
        padding: 0 8px;
        color: #9c9c9c;
        width: 80%;
        margin: 10px 0 20px;
        line-height: 30px;
        background: rgba(0, 0, 0, 0.2);
        border-radius: 5px;
    }

    .u-inputEmail .f-clickBtn {
        border-top: 1px solid #ccc;
        line-height: 40px;
    }

    .u-inputEmail .f-clickBtn span {
        display: inline-block;
        width: 48%;
        height: 40px;
        border-right: 1px solid #ccc;
    }

    .z-noBorder{
        border: none!important;
    }
    /*个人资料*/
    .f-round a {
        color: #fff;
    }

    .f-color9 {
        color: #999;
    }

    .u-contract-back{
        width: 100%;
        display: block;
        background: #d74147;
        line-height: 60px;
        position: fixed;
        bottom: 0;
        text-align: center;
        color: #fff;
    }
    iframe{
        width: 100%;
        height: 100%;
    }
    /*地区选择框*/
    .f-selectArea{
        position: fixed;
        bottom:0;
        left: 5%;
        overflow: scroll;
        z-index: 2000;
        width: 90%;
        height:78%;
        background: #fff;
        color: #666;
        border-radius: 10px;
        margin-bottom: 40px;

    }
    .u-region-city {
        text-indent: 2em;

    }

    .u-region-area {
        text-indent: 4em;
    }
    .u-region-province{
        margin:0 5%;
        border-bottom: 1px solid #ccc;
        line-height: 40px;
    }
    .u-region-city:before,
    .u-region-province:before{
        content:'>';
        font-weight: bold;
        margin-right: 5px;
        font-size: 16px;
    }

    .z-expanded:before{
        content:'v';
    }
    .f-backBtn{
        text-align: center;
        color: #fff;
        line-height: 30px;
        background: #d74147;
        position: fixed;
        bottom:2px;
        left: 5%;
        z-index: 2000;
        width: 90%;
        border-radius:5px;
    }
    .f-detailAddress {
        color: #fff;
        background: #999;
        width: 100px;
        text-align: center;
    }
</style>
<template>
    <div class="m-safety-grap">
        <div id="t-security" >
            <ul id="j-securityInfo" class="f-securityList">
                <!--##    手机号码-->
                <li>
                    <img class="f-checked" :src="imgurl.redCheck"/>
                    <span class="f-fs16 u-titleLine">手机号码</span>
                    <div class="f-safety-number">{{securityData.mobile}}</div>
                </li>
                <!--##    登录密码-->
                <li>
                    <img class="f-checked" :src="imgurl.redCheck"/>
                    <span class="u-titleLine u-titleLine">登录密码</span>
                    <div class="f-safety-key f-round f-fs14 j-changePassword"  v-link="{path:'/member/password'}">
                      修改密码
                    </div>
                </li>
                <!--##        新浪存钱罐-->
                <li>
                    <img class="f-checked " v-if="!securityData.isVerifyTrueName" :src="imgurl.grayCheck" alt=""/>
                    <img class="f-checked " v-else  :src="imgurl.redCheck" alt=""/>
                    <span class="u-titleLine">新浪存钱罐</span>
                    <div class="f-immediately-reputation f-round f-fr" v-if="!securityData.isVerifyTrueName">
                        <a href="{{siteurl}}/mCenter/sinapay" class="j-openSinaMember f-fs14">立即开通，获30元现金券</a>
                    </div>
                    <div v-else class="f-fr f-name" >
                        <div class="f-safety-name f-fr f-color9">
                            <span >{{securityData.name}}</span>
                            <i>{{securityData.memberSinaId}}</i>
                        </div>
                        <!--新浪验证-->
                        <em class="f-round f-sinaVerification ">
                            <a class="f-fs14">新浪验证</a>
                        </em>
                    </div>
                </li>
                <!--##    安全银行卡-->
                <li>

                    <img class="f-checked " v-if="!securityData.isSecurity" :src="imgurl.grayCheck" alt=""/>
                    <img class="f-checked " v-else  :src="imgurl.redCheck" alt=""/>
                    <span class="u-titleLine">安全银行卡</span>

                    <div v-if="!securityData.isSecurity" class="f-immediately-open f-round f-fs14 j-openSecurityCard"
                         @click="bindSecurityCard('#j-cardLayer')">立即开通
                    </div>
                    <div v-else class="f-safety-open ">已开通</div>
                    <!--##安全卡开通快捷支付-->
                    <div class="f-safety-card f-dn u-safetyCard" id="j-cardLayer">
                        <span class="f-toolTip">安全卡是：快捷支付状态下充值、提现<br/>同卡进出的银行卡安全设置</span>

                        <div class="f-safety-step">
                            <ul>
                                <li>开通步骤：</li>
                                <li>1、银行卡开通快捷支付</li>
                                <li>2、用该卡成功交易(充值)</li>
                                <li>3、该卡即升级为安全卡</li>
                            </ul>
                            <a v-if="safecard===true" href="{{siteurl}}/mCenter/recharge">
                                <button class="u-openQuickPayBtn">去充值</button>
                            </a>
                            <a v-else href="{{siteurl}}/mCenter/bankAdd">
                                <button class="u-openQuickPayBtn">立即开通快捷卡</button>
                            </a>
                        </div>
                    </div>
                </li>

                <!--##    电子邮箱-->
                <li>
                    <img class="f-checked " v-if="!securityData.isBindEmail" :src="imgurl.grayCheck" alt=""/>
                    <img class="f-checked " v-else= :src="imgurl.redCheck" alt=""/>
                    <span class="u-titleLine">电子邮箱</span>
                    <div  v-if="!securityData.isBindEmail">
                        <!--<div class="f-immediately-reputation10 z-clicked f-round" v-if="isSend">-->
                            <!--<em class="f-fs14 ">请前往邮箱验证</em>-->
                        <!--</div>-->
                        <div class="f-immediately-reputation10 f-round j-bindEmail" >
                            <em class="f-fs14" @click="openEmailLayer('#j-changeEmail')">立即绑定，送10点人气值</em>
                        </div>
                    </div>
                    <div v-else class="" >
                        <div class="f-immediately-reputation10 z-clicked f-round" v-if="isSend">
                            <em class="f-fs14 ">请前往邮箱验证</em>
                        </div>
                        <div >
                            <div class="f-safety-name f-fr f-color9">{{securityData.email}}</div>
                            <br/>
                            <em class="f-round f-fs14 f-sinaVerification j-changeEmail" @click="openEmailLayer('#j-changeEmail')">更换邮箱</em>
                        </div>
                    </div>
                    <!--##绑定邮箱-->
                    <div class="u-inputEmail j-inputEmail-show f-dn" id="j-changeEmail">
                        <p class="f-fs22" v-if="!securityData.isBindEmail">绑定邮箱</p>
                        <p class="f-fs22" v-else>修改邮箱</p>
                        <input v-if="!securityData.isBindEmail" class="f-inputEmail f-fs16" type="email" id="j-inputEmail" placeholder="请输入您的邮箱地址"
                               @keyup.enter="saveEmail('#j-changeEmail')"/>

                        <input v-else class="f-inputEmail f-fs16" type="email" id="j-inputEmail" placeholder="请输入您的新邮箱地址"
                               @keyup.enter="saveEmail('#j-changeEmail')"/>
                        <p class="f-clickBtn">
                            <span @click="cancelEmail('#j-changeEmail')">取消</span>
                            <span class="z-colorRed z-noBorder"  @click="saveEmail('#j-changeEmail','#j-inputEmail')">保存</span>
                        </p>
                    </div>
                </li>
                <!--##    个人资料-->
                <li>
                    <img class="f-checked " v-if="!securityData.isCompletedInfo" :src="imgurl.grayCheck" alt=""/>
                    <img class="f-checked " v-else :src="imgurl.redCheck" alt=""/>
                    <span class="u-titleLine">个人资料</span>

                    <div class="f-immediately-reputation10 f-round " v-if="!securityData.isCompletedInfo"
                         v-link="{path:'/member/personal'}">
                        <em class="f-fs14"> <a>立即完善，送10点人气值</a> </em>
                    </div>
                    <div v-else class="" v-link="{path:'/member/personal'}">
                        <div class="f-round f-fs14 f-sinaVerification" >修改信息</div>
                    </div>
                </li>
            </ul>
        </div>
        <div id="j-sinaVerificationInfo" class="f-dn">
            <iframe name="sinaVerificationInfo" id="sinaVerificationInfo" frameborder="0"></iframe>
            <span id="j-goback" class="u-contract-back f-fs24">返回</span>
        </div>


    </div>
</template>
<script>
    import Base from 'js/base'
    export default {
        data(){
            return{
                isSend:false,
                siteurl: this.$root.siteurl,
                imgurl:{
                    redCheck:this.$root.siteurl+'/res/img/red_get.png',
                    grayCheck:this.$root.siteurl+'/res/img/gray_get.png',
                },
                securityData: {}
            }
        },
    created:function(){
        var _this=this;
        Base.getAPI({
           url:Base.path + '/security/member/queryMemberAuthorize',
            version: '1.0.2',
            callback: function (data) {
                _this.$set('securityData', data.result)
                console.log(data.result)
            }
        })
    },
    methods:{

        openEmailLayer: function ( id,event) {
            //$(event.currentTarget).parent().next().show()
            $(id).show()
            Base.cover.show(null)
        },
        cancelEmail: function (id) {
            $(id).hide()
            Base.cover.hide()
        },
        saveEmail: function (layerId, inputId) {
            var $input = inputId ? $(inputId) : $(event.currentTarget),
                    email = $input.val(),
                    emailReg = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/,
                    _this = this,
                    isSend=false;

            if (emailReg.test(email)) {
                Base.getAPI({
                    url: environment.globalPath + '/security/member/bindEmail',
                    data: {email: email},
                    version: '1.0.4',
                    callback: function (data) {
                        if(data.success){
                            _this.$set('isSend',true)
                            $(layerId).hide()
                            Base.cover.hide()
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
                Base.cover.show(id,true)
                return false
            }

            Base.getAPI({
                url:Base.path + '/security/bankCard/queryNonSecurityBankCard',
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

                        Base.cover.show(id,true)
                    } else {
                        alert(data.resultCodes[0].msg)
                    }
                }
            })


        },
////        新浪验证
//        auditMemberInfos:function(id){
//            $(id).show()
//            $('#j-security,.g-ft').hide()
//            $('#j-goback').click(function(){
//                $(id).hide()
//                $('#j-security,.g-ft').show()
//            })
//            $('#sinaVerificationInfo').height(window.screen.height).attr('src','./auditMemberInfos')
//        }
    }
    }
</script>