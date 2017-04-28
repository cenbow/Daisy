<style>
    body{
        background: #f3f3f3;
    }
</style>
<template>
<div class="m-recordList">
    <div v-if="exchangeRecord.length">
        <ul class="u-recordList">
            <li v-for="item in exchangeRecord">
                <a href="javascript:void(0)" @click="requestData(5,$event,item.orderId)">
                    <div class="u-recordImg">
                        <img :src="alipath+item.image" alt="">
                    </div>
                    <div class="u-recordInfo">
                        <div class="u-listType">
                            <span class="f-fs16 f-color6">{{item.goodsName}}</span>
                            <span v-if="item.status==1||item.status==4" class="f-fs14 f-coloreDC f-fr u-rightIcon">待发货</span>
                            <span v-if="item.status==5" class="f-fs14 f-color9 f-fr u-rightIcon">充值失败</span>
                            <span v-if="item.status==2" class="f-fs14 f-color9 f-fr u-rightIcon">已发货</span>
                            <span v-if="item.status==3" class="f-fs14 f-color9 f-fr u-rightIcon">已取消</span>
                        </div>
                        <div class="f-fs12 f-color9 u-timeLine">{{item.createTimeStr}}</div>
                    </div>
                </a>
            </li>
        </ul>
        <a href="javascript:void(0)" @click="loadMoreData(4,$event)" v-if="pageNo<totalPageCount">
        <div class="u-reloadMore " :class="{'z-loading':isLoad}">
            <span v-if="isLoad">加载中...</span>
            <span v-else >加载更多</span>
        </div>
        </a>
    </div>
    <div v-else style="margin: 50% 30%;width: 40%; color: #999999; text-align: center">

        <img :src="getImg('/img/norecord.png')" alt="" width="80%"/>

        <p> 您还没有相关记录</p>
    </div>
</div>
</template>
<style>
    body{
        background: #F3F3F3;
    }
</style>
<script>

    const RES=env.resPath
    export default{
        data:function () {
            return {
                siteurl: env.siteurl,
                alipath:env.alipath,
                res: env.resPath,
                exchangeRecord:'',
                pageNo:this.$root.initData.data.result.pageNo,
                totalPageCount:this.$root.initData.data.result.totalPageCount,
                isLoad:false
            }
        },
        created:function () {
            var self=this
            window.scrollTo(0, 0)
            //传递安卓title
            if(env.Os==1){
                Android.UpdateTitle('兑换记录')
            }
            self.exchangeRecord=this.$root.initData.data.result.data
            this.$on('methodCallback',function (msg) {
                if(msg.method==5){
                    this.$root.orderMessage=msg
                    this.$router.go({path: '/exchangeOrderInfo?title='+window.encodeURI('订单详情') })
                }else{
                    self.exchangeRecord=self.exchangeRecord.concat(msg.data.result.data)
                    self.pageNo=msg.data.result.pageNo
                    self.isLoad=false
                }

            })
        },
        computed:{},
        methods:{
            getImg:function (url) {
                return RES+url
            },
            requestData:function (invokeMethod,event,id) {
                var self=this
                hook.getEvent(invokeMethod + '&isNeedRealName=0&args_orderMainId_1_long_'+id,$(event.currentTarget),1)
            },
            loadMoreData:function (invokeMethod,event) {
                var self=this,
                        pageNo=self.pageNo+1

                hook.getEvent(invokeMethod + '&isNeedRealName=0&args_currentPage_1_integer_'+pageNo,$(event.currentTarget),1)
                self.isLoad=true
            }
        }
    }
</script>