<style>
    body {
        background: #f3f3f3;
    }
</style>
<template>
<div class="m-recordList">
    <div>
        <ul class="u-recordList">
            <li>
                <div class="u-recordImg">
                    <img :src="alipath+orderData.imageList[0].fileUrl" alt="" width="100%">
                </div>
                <div class="u-recordInfo u-orderInfo">
                    <div class="u-listType">
                        <span class="f-fs16 f-color6">{{orderData.goodsName}}</span>
                        <span v-if="orderData.status==1||orderData.status==4" class="f-fs14 f-coloreDC f-fr u-rightIcon">待发货</span>
                        <span v-if="orderData.status==5" class="f-fs14 f-color9 f-fr u-rightIcon">充值失败</span>
                        <span v-if="orderData.status==2" class="f-fs14 f-color9 f-fr u-rightIcon">已发货</span>
                        <span v-if="orderData.status==3" class="f-fs14 f-color9 f-fr u-rightIcon">已取消</span>
                    </div>
                    <div class="f-fs12 f-color9"><em class="f-coloreDC">{{orderData.amount}}</em>点人气值</div>
                    <div class="f-fs12 f-color9 u-timeLine">{{orderData.createTimeStr}}</div>
                </div>
            </li>
            <li class="u-bgLine" style="" v-if="orderData.orderType==3 || (orderData.orderType==2&&orderData.rechargeType==1) || orderData.sendRemark">
                <img :src="getImg('/img/post/popularity/express.png')" alt="" width="100%">
            </li>
            <li class="u-remarksBlock" v-if="orderData.orderType==3 || (orderData.orderType==2&&orderData.rechargeType==1) || orderData.sendRemark">
                <div class="u-remarksBlock f-fs14 f-color9">
                       <div v-if="orderData.orderType==3">
                    <div class="u-memberMsg u-infoList">
                        <div class="u-tipTitle f-disInline">收货人：</div>
                        <div class="u-mainInfo f-disInline f-color6"><em>{{orderData.receiver}}</em><em class="f-fr">{{orderData.mobile}}</em></div>
                    </div>

                    <div class=" u-address u-infoList">
                        <div class="u-tipTitle f-disInline">收货地址：</div>
                        <div class="u-mainInfo f-disInline f-color6">{{Address}}</div>
                    </div>
                    </div>
                    <div v-if="orderData.orderType==2&&orderData.rechargeType==1" class="u-memberMsg u-infoList">
                        <div class="u-tipTitle f-disInline">收货人：</div>
                        <div class="u-mainInfo f-disInline f-color6"><em>{{orderData.rechargeCard}}</em></div>
                    </div>
                    <div class="u-remark u-infoList" v-if="orderData.sendRemark">
                        <div class="u-tipTitle f-disInline">备注：</div>
                        <div class="u-mainInfo f-disInline f-color6"><p v-for="item in SendRemark">{{item}}</p></div>
                    </div>
                </div>
            </li>
        </ul>
    </div>

</div>
</template>
<script>

    const RES=env.resPath
    export default{
        data:function () {
            return {
                siteurl: env.siteurl,
                alipath:env.alipath,
                res: env.resPath,
                orderData: {},
                SendRemark: '',
                Address: ''
            }
        },
        created:function () {
            //传递安卓title
            if(env.Os==1){
                Android.UpdateTitle('订单详情')
            }
            this.orderData=this.$root.orderMessage.data.result
            if (this.orderData.sendRemark) {
                this.SendRemark = this.orderData.sendRemark.replace(/#/g, ' ').replace('^', '\'').replace('$', '\"').replace('：', ':').split('&')
            }
            if (this.orderData.address) {
                this.Address = this.orderData.address.replace(/#/g, ' ').replace(/&/g, ' ').replace('^', '\'').replace('$', '\"').replace('：', ':')
            }


        },
        computed:{},
        methods:{
            getImg:function (url) {
                return RES+url
            },
            splitMsg: function (msg) {
                var self = this
                self.finalGoodsDes = msg.replace(/#/g, ' ').split('&')
            }
        }
    }
</script>