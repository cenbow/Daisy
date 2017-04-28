<style>
    body{
        background: #f3f3f3;
    }
</style>
<template>
<div class="m-recordList">
    <div v-if="recordData.length">
        <ul class="u-recordList">
            <li v-for="item in recordData">
                <div class="u-listType">
                    <span class="f-fs16 f-color6">{{typeText[item.type-1]}}</span>
                    <span class="f-fs14 f-coloreDC f-fr u-rightIcon" v-if="item.income">+<em>{{item.income}}</em>点</span>
                    <span class="f-fs14 f-color6 f-fr u-rightIcon" v-if="item.outlay">-<em>{{item.outlay}}</em>点</span>
                </div>
                <div class="f-fs12 f-color9 u-timeLine">{{item.happenTimeStr}}</div>
            </li>
        </ul>
        <a href="javascript:void(0)" @click="loadMoreData(6,$event)" v-if="pageNo<totalPageCount">
            <div class="u-reloadMore " :class="{'z-loading':isLoad}">
                <span v-if="isLoad">加载中...</span>
                <span v-else >加载更多</span>
            </div>
        </a>
    </div>
    <div v-else style="margin: 50% 30%;width: 40%; color: #999999; text-align: center">

        <img :src="getImg('/img/logoGray.png')" alt=""/>

        <p> 您还没有相关记录</p>
    </div>
</div>
</template>
<script>

    const RES=env.resPath
    export default{
        data:function () {
            return {
                siteurl: env.siteurl,
                res: env.resPath,
                recordData:this.$root.initMessage.data.result.data,
                pageNo:this.$root.initMessage.data.result.pageNo,
                totalPageCount:this.$root.initMessage.data.result.totalPageCount,
                isLoad:false,
                typeText:['推荐好友','平台活动','平台派送','兑换现金券','补发人气值','签到','提现手续费','兑换收益券','商品兑换']
            }
        },
        created:function () {
            var self=this
            window.scrollTo(0, 0)
            //传递安卓title
            if(env.Os==1){
                Android.UpdateTitle('人气值流水')
            }
            this.$on('methodCallback',function (msg) {
                self.recordData=self.recordData.concat(msg.data.result.data)
                self.pageNo=msg.data.result.pageNo
                self.isLoad=false
            })
        },
        computed:{},
        methods:{
            getImg:function (url) {
                return RES+url
            },
            loadMoreData:function (invokeMethod,event) {
                var self=this,
                        pageNo=self.pageNo+1

                hook.getEvent(invokeMethod + '&isNeedRealName=0&args_pageNo_1_integer_'+pageNo,$(event.currentTarget),1)
                self.isLoad=true
            }
        }
    }
</script>