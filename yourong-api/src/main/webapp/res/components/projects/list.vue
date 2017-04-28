<style scoped>
    .m-plist-navi{
        height: 50px;
    }
    .m-plist-navi li{
        display: inline-block;
        text-align: center;
        width: 24%;
        color: #666;
        padding: 10px 0;
    }
    .m-plist-navi .z-current{
        border-bottom:1px solid #d74148;
        color: #d74148;
    }
    .m-projectsList{
        margin-bottom: 56px;
    }
    .f-empty{
        width: 60%;
        text-align: center;
        color: #666;
        position: absolute;
        left: 20%;
        top:40%;
    }
    .f-loadNextPage{
        height: 50px;
        text-align: center;
        width: 80%;
        margin-left: 10%;
        line-height: 50px;
        background: lightskyblue;
        display: block;
    }
</style>
<template>
    <ul class="m-plist-navi">
        <li class="m-noticeList" :class="{'z-current':isCurrent==0}" @click="changeTab(0)" >预告</li>
        <li class="m-investList" :class="{'z-current':isCurrent==1}" @click="changeTab(1)" >投资中</li>
        <li class="m-performancetList" :class="{'z-current':isCurrent==2}" @click="changeTab(2)">履约中</li>
        <li class="m-repaymentList" :class="{'z-current':isCurrent==3}" @click="changeTab(3)">已还款</li>
    </ul>

    <div id="m-noticeProject" class="m-projectsList" v-if="isCurrent==0">
        <div class="f-empty" v-if="!investData.notice.data.length">
            <img :src="imgurl.logoGray" alt=""/>
            <p>新项目即将上线，敬请期待</p>
        </div>
        <ul v-else class="m-project-list" id="j-project-list">
            <li v-for="item in investData.notice.data" class="u-project-item f-pos-r" data-ruyi="${projectUtils.getProjectActivitySign($project.id)}">
                <a href="{{siteurl}}/products/detail-{{item.pid}}.html" class="u-project-link">
                    <h3 class="f-fs14">
                        <em>{{item.name | formatName}}期</em>
                        <span class="u-badge-red" v-if="item.isNoviceProject===true">新客专享</span>
                        <span class="u-badge-red" v-if="item.isJoinLease===true">租赁分红</span>
                        <!--#if(${projectUtils.getProjectActivitySign($_project.id)}==1)<i class="u-badge-ruyi">如意</i>#end-->

                    </h3>
                    <div class="u-pl-box f-pos-r">
                        <div class="u-pl-wrap f-pos-a">
                            <div class="u-pl-photo f-pos-r">
                                <div class="ui-avatar-one u-project-avatar">
                                    <span style="background-image:url({{item.thumbnail}})"></span>
                                </div>
                                <div class="u-project-circle">
                                    <div class="f_pie_left"><div class="z-left"></div></div>
                                    <div class="f_pie_right"><div class="z-right"></div></div>
                                    <div class="u-project-mask"><span>0</span></div>
                                </div>
                            </div>

                        </div>
                        <div class="u-pl-info f-fl">
                            <div>
                                <span>项目总额 &yen;{{item.totalAmount}}元</span>
                        <span>
                            年化收益
                            <i v-if="item.maxAnnualizedRate==item.minAnnualizedRate">{{item.minAnnualizedRate}}%</i>
                            <i v-else>{{item.minAnnualizedRate}}%-{{item.maxAnnualizedRate}}%</i>
                        </span>
                                <span>收益天数 {{item.earningsDays}}天</span>
                            </div>
                        </div>
                    </div>
                    <div class="u-pl-process">
                        <i style="width:{{item.investmentProgress}}%;" #if($_process==100)class="z-finished"#end></i>
                        <div>
                            当前进度 {{item.investmentProgress}}%

                            <span>剩余可投金额 {{item.availableBalance}}元</span>
                        </div>
                    </div>
                </a>
            </li>

        </ul>
    </div>
    <div id="m-investProjects" class="m-projectsList" v-if="isCurrent==1">
        <ul class="m-project-list">
            <li v-for="item in investData.investing.data" class="u-project-item f-pos-r" data-ruyi="${projectUtils.getProjectActivitySign($project.id)}">
                <a href="{{siteurl}}/products/detail-{{item.pid}}.html" class="u-project-link">
                    <h3 class="f-fs14">
                        <em>{{item.name | formatName}}期</em>
                        <span class="u-badge-red" v-if="item.isNoviceProject===true">新客专享</span>
                        <span class="u-badge-red" v-if="item.isJoinLease===true">租赁分红</span>
                        <!--#if(${projectUtils.getProjectActivitySign($_project.id)}==1)<i class="u-badge-ruyi">如意</i>#end-->

                    </h3>
                    <div class="u-pl-box f-pos-r">
                        <div class="u-pl-wrap f-pos-a">
                            <div class="u-pl-photo f-pos-r">
                                <div class="ui-avatar-one u-project-avatar">
                                    <span style="background-image:url({{item.thumbnail}})"></span>
                                </div>
                                <div class="u-project-circle">
                                    <div class="f_pie_left"><div class="z-left"></div></div>
                                    <div class="f_pie_right"><div class="z-right"></div></div>
                                    <div class="u-project-mask"><span>0</span></div>
                                </div>
                            </div>

                        </div>
                        <div class="u-pl-info f-fl">
                            <div>
                                <span>项目总额 &yen;{{item.totalAmount}}元</span>
                        <span>
                            年化收益
                            <i v-if="item.maxAnnualizedRate==item.minAnnualizedRate">{{item.minAnnualizedRate}}%</i>
                            <i v-else>{{item.minAnnualizedRate}}%-{{item.maxAnnualizedRate}}%</i>
                        </span>
                                <span>收益天数 {{item.earningsDays}}天</span>
                            </div>
                        </div>
                    </div>
                    <div class="u-pl-process">
                        <i style="width:{{item.investmentProgress}}%;" #if($_process==100)class="z-finished"#end></i>
                        <div>
                            当前进度 {{item.investmentProgress}}%

                            <span>剩余可投金额 {{item.availableBalance}}元</span>
                        </div>
                    </div>
                </a>
            </li>
        </ul>
        <!--<botton v-if="investData.investing.totalPageCount" class="f-loadNextPage" :pageCount='nextPageCount' @click="getNextInvestData">点击加载下一页</botton>-->
    </div>
    <div id="m-performancetProjects" class="m-projectsList" v-if="isCurrent==2">
        <ul class="m-project-list">
            <li v-for="item in investData.performance.data" class="u-project-item f-pos-r" data-ruyi="${projectUtils.getProjectActivitySign($project.id)}">
                <a href="{{siteurl}}/products/detail-{{item.pid}}.html" class="u-project-link">
                    <h3 class="f-fs14">
                        <em>{{item.name | formatName}}期</em>
                        <span class="u-badge-red" v-if="item.isNoviceProject===true">新客专享</span>
                        <span class="u-badge-red" v-if="item.isJoinLease===true">租赁分红</span>
                        <!--#if(${projectUtils.getProjectActivitySign($_project.id)}==1)<i class="u-badge-ruyi">如意</i>#end-->

                    </h3>
                    <div class="u-pl-box f-pos-r">
                        <div class="u-pl-wrap f-pos-a">
                            <div class="u-pl-photo f-pos-r">
                                <div class="ui-avatar-one u-project-avatar">
                                    <span style="background-image:url({{item.thumbnail}})"></span>
                                </div>
                                <div class="u-project-circle">
                                    <div class="f_pie_left"><div class="z-left"></div></div>
                                    <div class="f_pie_right"><div class="z-right"></div></div>
                                    <div class="u-project-mask"><span>0</span></div>
                                </div>
                            </div>

                        </div>
                        <div class="u-pl-info f-fl">
                            <div>
                                <span>项目总额 &yen;{{item.totalAmount}}元</span>
                        <span>
                            年化收益
                            <i v-if="item.maxAnnualizedRate==item.minAnnualizedRate">{{item.minAnnualizedRate}}%</i>
                            <i v-else>{{item.minAnnualizedRate}}%-{{item.maxAnnualizedRate}}%</i>
                        </span>
                                <span>收益天数 {{item.earningsDays}}天</span>
                            </div>
                        </div>
                    </div>
                    <div class="u-pl-process">
                        <i style="width:{{item.investmentProgress}}%;" #if($_process==100)class="z-finished"#end></i>
                        <div>
                            当前进度 {{item.investmentProgress}}%

                            <span>剩余可投金额 {{item.availableBalance}}元</span>
                        </div>
                    </div>
                </a>
            </li>
        </ul>
    </div>
    <div id="m-repaymentProjects" class="m-projectsList" v-if="isCurrent==3">
        <ul class="m-project-list">
            <li v-for="item in investData.repayment.data" class="u-project-item f-pos-r" data-ruyi="${projectUtils.getProjectActivitySign($project.id)}">
                <a href="{{siteurl}}/products/detail-{{item.pid}}.html" class="u-project-link">
                    <h3 class="f-fs14">
                        <em>{{item.name | formatName}}期</em>
                        <span class="u-badge-red" v-if="item.isNoviceProject===true">新客专享</span>
                        <span class="u-badge-red" v-if="item.isJoinLease===true">租赁分红</span>
                        <!--#if(${projectUtils.getProjectActivitySign($_project.id)}==1)<i class="u-badge-ruyi">如意</i>#end-->

                    </h3>
                    <div class="u-pl-box f-pos-r">
                        <div class="u-pl-wrap f-pos-a">
                            <div class="u-pl-photo f-pos-r">
                                <div class="ui-avatar-one u-project-avatar">
                                    <span style="background-image:url({{item.thumbnail}})"></span>
                                </div>
                                <div class="u-project-circle">
                                    <div class="f_pie_left"><div class="z-left"></div></div>
                                    <div class="f_pie_right"><div class="z-right"></div></div>
                                    <div class="u-project-mask"><span>0</span></div>
                                </div>
                            </div>

                        </div>
                        <div class="u-pl-info f-fl">
                            <div>
                                <span>项目总额 &yen;{{item.totalAmount}}元</span>
                        <span>
                            年化收益
                            <i v-if="item.maxAnnualizedRate==item.minAnnualizedRate">{{item.minAnnualizedRate}}%</i>
                            <i v-else>{{item.minAnnualizedRate}}%-{{item.maxAnnualizedRate}}%</i>
                        </span>
                                <span>收益天数 {{item.earningsDays}}天</span>
                            </div>
                        </div>
                    </div>
                    <div class="u-pl-process">
                        <i style="width:{{item.investmentProgress}}%;" #if($_process==100)class="z-finished"#end></i>
                        <div>
                            当前进度 {{item.investmentProgress}}%

                            <span>剩余可投金额 {{item.availableBalance}}元</span>
                        </div>
                    </div>
                </a>
            </li>
        </ul>
    </div>
</template>

<script>
    import Base from 'js/base'
    export default {
          data(){
            return {
                isCurrent:+this.$route.query.type||1,
                nextPageCount:2,
                siteurl:this.$root.siteurl,
                investData:{},
                imgurl:{
                    logoGray:this.$root.siteurl+'/res/img/logoGray.png'
                },
                statusCode:['notice','investing','performance','repayment']
            }
        },
        watch:{
            'isCurrent':function(n,o){
                    if(!this.$get('investData.'+this.statusCode[n])){
                        this.getInvestData(n)
                    }
            }
        },
    created:function(){
        this.getInvestData()
        Vue.filter('formatName',function(value){
            return value.split('期')[0]
        })
        console.log(this.$data.isCurrent)
    },
        methods:{
            changeTab:function(i){
              this.isCurrent = i
            },
            getInvestData:function(){
                var _this=this,
                        type=_this.$data.isCurrent;
                Base.getAPI({
                    url:Base.path+'/project/queryProjectList',
                    data:{statusCode:_this.statusCode[type]},
                    version: '1.0.2',
                    callback:function(data){
                        _this.$set('investData.'+_this.statusCode[type],data.result)
                    }
                })
            }
//            getNextInvestData:function(type){
//                var _this=this
//                Base.getAPI({
//                    url:Base.path+'/project/queryProjectList',
//                    data:{statusCode:'investing',pageNo:_this.nextPageCount},
//                    version: '1.0.2',
//                    callback:function(data){
//                        _this.$set('investData.investing',data.result)
//                        _this.nextPageCount+=1
//                        _this.$log( _this.nextPageCount)
//                        if(_this.nextPageCount=('investData.investing.totalPageCount'-1)){
//                            $('.f-loadNextPage').hide()
//                        }
//                    }
//                })
//            }
           }


    }
</script>