<style>
    body{
        background: #f3f3f3;
    }
</style>
<template>
<div class="m-recordList">
    <div v-if="growthData.length">
        <ul class="u-recordList">
            <li v-for="item in growthData">
                <div class="u-listType"><span class="f-fs16">成长值</span><span class="f-fs14 f-coloreDC f-fr u-rightIcon">+<em>{{item.increasedScore}}</em>分</span>
                </div>
                <div class="f-fs12 f-color9 u-timeLine">{{item.updateTimeStr}}</div>
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
<script>

    const RES = env.resPath
    export default {
        data: function () {
            return {
                siteurl: env.siteurl,
                res: env.resPath,
                growthData:this.$root.initMessage.data.result.data,
                pageNo:this.$root.initMessage.data.result.pageNo,
                totalPageCount:this.$root.initMessage.data.result.totalPageCount,
                isLoad:false
            }
        },
        created: function () {
            var self=this
            //传递安卓title
            if(env.Os==1){
                Android.UpdateTitle('成长记录')
            }
            console.log('growthData:',self.growthData)
            this.$on('methodCallback',function (msg) {
                    self.growthData=self.growthData.concat(msg.data.result.data)
                    self.pageNo=msg.data.result.pageNo
                    self.isLoad=false

            })
        },
        computed: {},
        methods: {
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