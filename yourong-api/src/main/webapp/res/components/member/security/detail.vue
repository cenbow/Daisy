<style scoped>
    /*详细信息页样式*/
    .u-detailInformation {
        background: #eeeeee;
    }

    .u-detailInformation li {
        width: 90%;
        margin: 0 5%;
        border-bottom: 1px solid #ccc;
        padding: 20px 0;
        float: right;
    }
    .u-detailInformation li .u-select{
        width: 80%;
        text-align: right;
    }
    .u-detailInformation li .u-select .f-choiceBtn{
        background: #9c9c9c;
        color: #fff;
        padding: 2px 30px;
        border-radius: 12px;
    }

    .u-detailInformation li select {
        -webkit-appearance: none;
        /*border: 0;*/
        padding-right: 14px;
        color: #999;
        width: 100%;
        padding-left: 20px;
        /*direction: rtl;*/
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

    .u-preservationBtn {
        background: #d74147;
        color: #fff;
        line-height: 40px;
        width: 80%;
        border-radius: 6px;
        margin: 20px 10%;
        font-weight: bold;
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
        <div>
            <form id="j-previewFor" action="" method="post" class="j-validform">
                <!--##现居地-->
                <input type="hidden" name="city" value="{{city}}">
                <input type="hidden" name="areaFullName" value="{{formatAreaFullName}}">
                <input type="hidden" name="address" value="{{address}}">
                <!--##户籍地-->
                <input type="hidden" name="censusRegisterId" value="{{censusRegisterId}}">
                <input type="hidden" name="censusRegisterName" value="{{formatCensusRegisterName}}">

                <input type="hidden"  name="highEdu" value="{{highEdu}}"/>
                <input type="hidden"  name="occupation" value="{{occupation}}"/>
                <input type="hidden"  name="income" value="{{income}}"/>
                <ul class="u-detailInformation">
                    <!--##                性别-->
                    <li v-if="sex!==null">
                        <span class="f-fs18 f-selectName">性别</span>
                        <span v-if="sex===0" class="f-fr f-color9">女</span>
                        <span v-if="sex===1" class="f-fr f-color9">男</span>
                    </li>
                    <!--##                生日-->
                    <li v-if="birthday">
                        <span class="f-fs18 f-selectName ">生日</span>
                <span class="f-fr f-color9">
                    {{birthday}}
                </span>
                    </li>

                    <!--##学历-->
                    <li>
                        <span class="f-fs18 f-selectName">学历</span>
                <span class="f-fr u-select">
                    <select id="j-education" dir="rtl" v-model="highEdu">
                        <option dir="ltr" value="0">选择</option>
                        <option dir="ltr" value="1">小学</option>
                        <option dir="ltr" value="2">初中</option>
                        <option dir="ltr" value="3">中专</option>
                        <option dir="ltr" value="4">高中</option>
                        <option dir="ltr" value="5">大专</option>
                        <option dir="ltr" value="6">本科</option>
                        <option dir="ltr" value="7">硕士</option>
                        <option dir="ltr" value="8">博士</option>
                    </select>


                </span>
                    </li>
                    <!--##    职业-->
                    <li>
                        <span class="f-fs18 f-selectName">职业</span>
                <span class="f-fr u-select">
                    <select id="j-occupation" dir="rtl" v-model="occupation">
                        <option dir="ltr" value="0">选择</option>
                        <option dir="ltr" value="1">企业主</option>
                        <option dir="ltr" value="2">个体户</option>
                        <option dir="ltr" value="3">上班族</option>
                        <option dir="ltr" value="4">无固定职业</option>
                    </select>

                </span>
                    </li>
                    <!--##    月收入-->

                    <li>
                        <span class="f-fs18 f-selectName">月收入</span>
                <span class="f-fr u-select">
                    <select id="j-income" class="f-income" dir="rtl" v-model="income">
                        <option dir="ltr" value="0">选择</option>
                        <option dir="ltr" value="1">1000元以下</option>
                        <option dir="ltr" value="2">1000~2000元</option>
                        <option dir="ltr" value="3">2001~4000元</option>
                        <option dir="ltr" value="4">4001~6000元</option>
                        <option dir="ltr" value="5">6001~8000元</option>
                        <option dir="ltr" value="6">8001~10000元</option>
                        <option dir="ltr" value="7">10001~20000元</option>
                        <option dir="ltr" value="8">20001~40000元</option>
                        <option dir="ltr" value="9">40001~60000元</option>
                        <option dir="ltr" value="10">60000元以上</option>
                    </select>

                </span>
                    </li>
                    <!--##    户籍地-->
                    <li>
                        <span class="f-fs18 f-selectName">户籍地</span>
                <span class="f-fr  f-color9  u-select">
              <i class="f-fs14 " @click="openSelectLayer('#j-domicile')"> {{formatCensusRegisterName | removePunctuation}}</i>
        <i v-if="!formatCensusRegisterName" class="j-domicile  f-choiceBtn" @click="openSelectLayer('#j-domicile')">选择</i>
                </span>

                    </li>
                    <!--##    现居地-->
                    <li>
                        <span class="f-fs18 f-selectName">现居地</span>
            <span class="f-fr f-color9  u-select">
                <template v-if="formatAreaFullName">
                    <i class="f-fs14 " @click="openSelectLayer('#j-residence')">
                        {{formatAreaFullName | removePunctuation}}
                    </i>
                    <br/>
                    <span v-if="!address" class="f-detailAddress f-fs12 f-round f-fr j-sureDetailAddress" @click="openDetailAddressLayer('#j-detailAddress')"> 详细地址</span>
                    <span v-else class="j-sureDetailAddress "  @click="openDetailAddressLayer('#j-detailAddress')">{{address}}</span>
                </template>
                <i v-else class="j-residence f-choiceBtn" @click="openSelectLayer('#j-residence')">选择</i>
            </span>

                    </li>
                </ul>
                <!--##详细地址弹窗-->
                <div class="u-popup f-setNickname f-dn" id="j-detailAddress">
                    <ul>
                        <li class="f-firstLi f-fs16 ">输入详细地址</li>
                        <input type="text" placeholder="请输入地址" id="j-inputAddress" onkeyup="value=value.replace(/[^\w\u4E00-\u9FA5]/g, '')"/>
                        <li class="f-secondLi">
                            <a id="j-cancleDetailAddress" href="javascript:void(0)"
                               @click="cancleDetailAddress('#j-detailAddress')">取消</a>
                            <a href="javascript:void(0)" class="f-secondA j-transactionLink" id=" j-sureDetailAddress"
                               @click="sureDetailAddress('#j-detailAddress')">
                                <span>确定</span>
                            </a>
                        </li>
                    </ul>
                </div>
            </form>
            <button id="j-preservationBtn" class="u-preservationBtn j-reg-btn"  @click="submitDetailInfo">保存</button>
        </div>
    <!--##户籍地选择-->
    <div class="f-backBtn j-backBtn f-dn">取消</div>
    <div class="u-domicile f-selectArea f-dn" id="j-domicile"></div>
    <!--##现居地选择-->
    <div class="u-residence f-selectArea f-dn" id="j-residence"></div>
</template>
<script>
    import Base from 'js/base'
    export default {
        data(){
        return{
            siteurl:this.$root.siteurl,
            detailData:{}
        }
    },
    created:function (done) {
        var self=this
        Base.getAPI({
            url: Base.path + '/security/member/queryMemberInfo',
            version: '1.0.2',
            callback: function (data) {
                $('#j-title').text('详细信息')
                self.$data=data.result
                console.log(self.$data)
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
                    self.$children[0].city=id
                    self.$children[0].formatAreaFullName=text
                    $('#j-residence').hide()
                    Base.cover.hide()
                    $('.j-backBtn').hide()
                })

                //户籍地选择
                var registeredSelector=new RegionSelector($('#j-domicile'),1)
                registeredSelector.init(function (id,text) {
                    self.$children[0].censusRegisterId=id
                    self.$children[0].formatCensusRegisterName=text
                    $('#j-domicile').hide()
                    $('#detail').show()
                    Base.cover.hide()
                    $('.j-backBtn').hide()
                })

            }
        })},
    methods:{

    }
    }
</script>