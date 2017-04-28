<style scoped>
    /*个人资料*/
    .f-round a {
        color: #fff;
    }

    .f-personalDataList li {
        width: 90%;
        margin: 0 5%;
        line-height: 20px;
        padding: 30px 0;
        border-bottom: 1px solid #ccc;
    }

    .f-personalDataList .f-avatar {
        line-height: 70px;
    }

    .f-personalDataList .f-avatar img {
        width: 70px;
        height: 70px;
        border-radius: 35px;
    }

    .f-nickname {
        color: #666;
    }
    .f-safety-key {
        color: #fff;
        background: #999;
        width: 100px;
        display: block;
        float: right;
        text-align: center;
    }
    .f-round {
        border-radius: 13px;
    }
    /*昵称*/
    .u-popup {
        width: 90%;
        -webkit-border-radius: 8px;
        -moz-border-radius: 8px;
        border-radius: 8px;
        position: absolute;
        top: 40%;
        left: 5%;
        text-align: center;
        background: #fff;
        line-height: 40px;
        z-index: 200;
        color: #999;
        padding-top: 20px;
    }

    .f-setNickname input {
        width: 80%;
        background: rgba(0, 0, 0, 0.2);
        border-radius: 5px;
        padding: 6px 8px;
        color: #999;
    }
    @media screen and (max-width:320px) {
        .f-firstLi{
            font-size: 14px;
        }
        .f-selectName{
            font-size: 16px;
        }
    }
    .u-popup .f-secondLi {
        margin-top: 30px;
        border-top: 1px solid #ccc;
    }


    .u-popup .f-secondLi a {
        width: 48%;
        color: #000;
        display: inline-block;
        border-right: 1px solid #ccc;
        padding: 5px 0;
    }

    .u-popup .f-secondLi .f-secondA {
        border: none;
        color: #da404a;
    }

    .z-nicknameActived {
        color: #666;
        background: none;
        font-size: 18px;
    }
    .f-popup{
        color: #000;
    }
    .f-popup .f-secondLi{
        margin: 0;
    }
</style>
<template>
    <div>
        <ul class="f-personalDataList" id="j-personalDataList">
            <li class="f-avatar">
                <span class="f-fs18">头像</span>
                <img class="f-fr" v-if="personalData.avatar" :src="avatar" alt="头像" class="j-avatar"/>
                <img class="f-fr" v-else :src="imgUrl.avatar" alt="头像" class="j-avatar"/>
            </li>
            <li>
                <span class="f-fs18">昵称</span>
                    <span class="f-safety-key f-round f-fs14 f-fr j-setNickname " v-if="!personalData.isSetNickname"
                          @click="openNicknameLayer('#j-setNickname')">设置昵称</span>
                <span class="f-fs18 f-nickname j-nickname f-fr" v-else>{{personalData.userName}}</span>
            </li>

            <li>
                <span class="f-fs18">详细信息</span>
                <span class="f-safety-key f-round f-fs14 f-fr"  v-link="{path:'/member/detail'}">修改信息</span>
            </li>
        </ul>
        <!--##设置昵称弹窗-->
        <div class="u-popup f-setNickname f-dn" id="j-setNickname">
            <ul>
                <li class="f-firstLi f-fs14 ">昵称只有一次机会哦&nbsp;确定后不能修改</li>
                <input type="text" placeholder="输入高大上的昵称" id="j-inputNickname" datatype="*,z3-14"
                       errormsg="昵称需为中文、字母或中文字母的组合，长度为3-14个字符（一个汉字为2个字符）。"/>
                <li class="f-secondLi">
                    <a id="j-cancleNickname" href="javascript:void(0)"
                       @click="cancleNickname('#j-setNickname')">取消</a>
                    <a href="javascript:void(0)" class="f-secondA j-transactionLink" id=" j-sureNickname"
                       @click="saveNickname('#j-inputNickname','#j-setNickname')">
                        <span >确定</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</template>
<script>
    import Base from 'js/base'
    export default {
        data(){
        return{
            personalData:{},
            siteurl:this.$root.siteurl,
            imgUrl:{
                avatar:this.$root.siteurl+'/res/img/member/avatar.png'
            }
        }
    },
    created:function (done) {
        var self=this
        Base.getAPI({
            url: Base.path + '/security/banlance/queryMemberBalance',
            callback: function (data) {
                $('#j-title').text('个人资料')
                self.$set('personalData',data.result)
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
            Base.cover.show(null)
        },
        cancleNickname: function (id) {
            $(id).hide()
            Base.cover.hide()
        },
        saveNickname: function (inputId, layerId) {
            var $input=inputId?$(inputId):$(event.currentTarget),
                    userName=$input.val(),
                    nicknameReg=/^([A-Za-z]|[\u4E00-\u9FA5])+$/
            if(nicknameReg.test(userName)){
                Base.getAPI({
                    url:Base.path+'/security/member/saveUserName',
                    data:{userName:userName},
                    version: '1.0.2',
                    callback:function(data){
                        if(data.success){
                            $(layerId).hide()
                            Base.cover.hide()
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
                Base.xTips({content:'昵称只能是汉字或字母'})
                $input.val('')
            }


        }
    }
    }
</script>