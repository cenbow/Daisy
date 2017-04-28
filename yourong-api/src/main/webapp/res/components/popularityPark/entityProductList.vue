<template>
    <div>
        <div class="m-productList">
            <div id="forInvestment" v-if="productData.length">
                <!--<div style="margin-left: 4%">-->
                <!--<span class="f-fs16 f-color3">超值实物</span><span class="f-fs12 f-color9">V2及以上等级兑换享折扣特权</span>-->
                <!--</div>-->
                <ul class="u-productList">
                    <li v-for="item in productData">
                        <div style="margin-left: 4%">
                            <a href="javascript:void(0)" data-id="item.id" @click="reqData(2,$event,item.id)">
                                <div class="u-couponImg f-fs14" :class="{'z-productOver':item.availInventor<1}">
                                    <img v-if="item.tags==1" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_new.png')" alt="">
                                    <img v-if="item.tags==2" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_tejia.png')" alt="">
                                    <img v-if="item.tags==3" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_recommend.png')" alt="">
                                    <img v-if="item.tags==4" class="u-redTips u-labelRush"
                                         :src="getImg('/img/post/popularity/label_rush.png')" alt="">
                                    <div class="u-imgBlock">
                                        <img :src="item.image" alt="" width="100%">
                                    </div>
                                </div>
                                <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div>
                                <div class="f-fs12 f-color9">
                                    <span class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span>
                                <span class="u-originalPrice"
                                      v-if="item.price>item.discountPrice">{{item.price}}点</span>
                                </div>
                            </a>
                        </div>
                    </li>
                </ul>
            </div>
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
                productData: this.$root.physicalData.data.result.data
            }
        },
        created: function () {
            window.scrollTo(0, 0)
            //传递安卓title
            if (env.Os == 1) {
                Android.UpdateTitle('超值实物')
            }
            this.$on('methodCallback', function (data, method) {

                this.$root.DetailtMessage = data
                this.$router.go({path: '/productDetail?title=' + window.encodeURI('商品详情')})

            })
        },
        computed: {},
        methods: {
            getImg: function (url) {
                return RES + url
            },
            reqData: function (invokeMethod, event, id) {
                var self = this
                console.log('requestMethod222:', invokeMethod)
                console.log(hook, hook.getEvent)
                hook.getEvent(invokeMethod + '&isNeedRealName=0&args_goodId_1_long_' + id + '&isUserID=0', $(event.currentTarget), 1)
            }
        }
    }
</script>
