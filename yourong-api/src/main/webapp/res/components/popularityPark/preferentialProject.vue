<template>
    <div>
        <div class="m-productList">
            <div id="doubleProject" v-if="productData.doubleListAll.length">
                <!--<div style="margin-left: 4%">-->
                <!--<span class="f-fs16 f-color3">双节特惠</span>-->
                <!--</div>-->
                <ul class="u-productList">
                    <li v-for="item in productData.doubleListAll">
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
                                    <img v-if="item.tags==5" class="u-redTips u-labelRush"
                                         :src="getImg('/img/post/popularity/label_rush4.png')" alt="">
                                    <img v-if="item.tags==6" class="u-redTips u-labelRush"
                                         :src="getImg('/img/post/popularity/label_rush5.png')" alt="">
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
                productData: initData.result
            }
        },
        created: function () {
            window.scrollTo(0, 0)
            //传递安卓title
            if (env.Os == 1) {
                Android.UpdateTitle('双节特惠')
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
                hook.getEvent(invokeMethod + '&isNeedRealName=0&args_goodId_1_long_' + id, $(event.currentTarget), 1)
            }
        }
    }
</script>
