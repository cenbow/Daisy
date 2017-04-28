webpackJsonp([8], {
    40: function (t, i) {
        "use strict";
        Object.defineProperty(i, "__esModule", {value: !0});
        var e = env.resPath;
        i.default = {
            data: function () {
                return {siteurl: env.siteurl, res: env.resPath, method: "", productData: initData.result}
            }, created: function () {
                var t = this;
                window.scrollTo(0, 0), 1 == env.Os && Android.UpdateTitle("投资专享"), this.$on("methodCallback", function (i, e) {
                    2 == t.method && (this.$root.DetailtMessage = i, this.$router.go({path: "/productDetail?title=" + window.encodeURI("商品详情")}))
                })
            }, computed: {}, methods: {
                getImg: function (t) {
                    return e + t
                }, reqData: function (t, i, e) {
                    var s = this;
                    s.method = t, hook.getEvent(t + "&isNeedRealName=0&args_goodId_1_long_" + e + "&isUserID=0", $(i.currentTarget), 1)
                }
            }
        }
    }, 85: function (t, i) {
        t.exports = ' <div> <div class=m-productList> <div id=doubleProject v-if=productData.doubleListAll.length> <ul class=u-productList> <li v-for="item in productData.doubleListAll"> <div style="margin-left: 4%"> <a href=javascript:void(0) data-id=item.id @click=reqData(2,$event,item.id)> <div class="u-couponImg f-fs14" :class="{\'z-productOver\':item.availInventor<1}"> <img v-if="item.tags==1" class=u-redTips :src="getImg(\'/img/post/popularity/label_new.png\')" alt=""> <img v-if="item.tags==2" class=u-redTips :src="getImg(\'/img/post/popularity/label_tejia.png\')" alt=""> <img v-if="item.tags==3" class=u-redTips :src="getImg(\'/img/post/popularity/label_recommend.png\')" alt=""> <img v-if="item.tags==4" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush.png\')" alt=""> <img v-if="item.tags==5" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush4.png\')" alt=""> <img v-if="item.tags==6" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush5.png\')" alt=""> <img v-if="item.tags==7" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush3.png\')" alt=""> <div class=u-imgBlock> <img :src=item.image alt="" width=100%> </div> </div> <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div> <div class="f-fs12 f-color9"> <span class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span> <span class=u-originalPrice v-if="item.price>item.discountPrice">{{item.price}}点</span> </div> </a> </div> </li> </ul> </div> <div id=forInvestment v-if=productData.investmentListAll.length> <ul class=u-productList> <li v-for="item in productData.investmentListAll"> <div style="margin-left: 4%"> <a href=javascript:void(0) data-id=item.id @click=reqData(2,$event,item.id)> <div class="u-couponImg f-fs14" :class="{\'z-productOver\':item.availInventor<1}"> <img v-if="item.tags==1" class=u-redTips :src="getImg(\'/img/post/popularity/label_new.png\')" alt=""> <img v-if="item.tags==2" class=u-redTips :src="getImg(\'/img/post/popularity/label_tejia.png\')" alt=""> <img v-if="item.tags==3" class=u-redTips :src="getImg(\'/img/post/popularity/label_recommend.png\')" alt=""> <img v-if="item.tags==4" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush.png\')" alt=""> <div class=u-imgBlock> <img :src=item.image alt="" width=100%> </div> </div> <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div> <div class="f-fs12 f-color9"> <span class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span> <span class=u-originalPrice v-if="item.price>item.discountPrice">{{item.price}}点</span> </div> </a> </div> </li> </ul> </div> </div> </div> '
    }, 93: function (t, i, e) {
        var s, a, l = {};
        s = e(40), a = e(85), t.exports = s || {}, t.exports.__esModule && (t.exports = t.exports.default);
        var o = "function" == typeof t.exports ? t.exports.options || (t.exports.options = {}) : t.exports;
        a && (o.template = a), o.computed || (o.computed = {}), Object.keys(l).forEach(function (t) {
            var i = l[t];
            o.computed[t] = function () {
                return i
            }
        })
    }
});