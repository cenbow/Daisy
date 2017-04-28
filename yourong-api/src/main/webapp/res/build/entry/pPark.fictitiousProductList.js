webpackJsonp([6], {
    44: function (t, e) {
        "use strict";
        Object.defineProperty(e, "__esModule", {value: !0});
        var i = env.resPath;
        e.default = {
            data: function () {
                return {
                    siteurl: env.siteurl,
                    res: env.resPath,
                    productData: this.$root.virtualCardData.data.result.data
                }
            }, created: function () {
                window.scrollTo(0, 0), 1 == env.Os && Android.UpdateTitle("虚拟卡券"), this.$on("methodCallback", function (t, e) {
                    this.$root.DetailtMessage = t, this.$router.go({path: "/productDetail?title=" + window.encodeURI("商品详情")})
                })
            }, computed: {}, methods: {
                getImg: function (t) {
                    return i + t
                }, reqData: function (t, e, i) {
                    hook.getEvent(t + "&isNeedRealName=0&args_goodId_1_long_" + i + "&isUserID=0", $(e.currentTarget), 1)
                }
            }
        }
    }, 89: function (t, e) {
        t.exports = ' <div> <div class=m-productList> <div id=forInvestment v-if=productData.length> <ul class=u-productList> <li v-for="item in productData"> <div style="margin-left: 4%"> <a href=javascript:void(0) data-id=item.id @click=reqData(2,$event,item.id)> <div class="u-couponImg f-fs14" :class="{\'z-productOver\':item.availInventor<1}"> <img v-if="item.tags==1" class=u-redTips :src="getImg(\'/img/post/popularity/label_new.png\')" alt=""> <img v-if="item.tags==2" class=u-redTips :src="getImg(\'/img/post/popularity/label_tejia.png\')" alt=""> <img v-if="item.tags==3" class=u-redTips :src="getImg(\'/img/post/popularity/label_recommend.png\')" alt=""> <img v-if="item.tags==4" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush.png\')" alt=""> <div class=u-imgBlock> <img :src=item.image alt="" width=100%> </div> </div> <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div> <div class="f-fs12 f-color9"> <span class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span> <span class=u-originalPrice v-if="item.price>item.discountPrice">{{item.price}}点</span> </div> </a> </div> </li> </ul> </div> </div> </div> '
    }, 97: function (t, e, i) {
        var s, a, o = {};
        s = i(44), a = i(89), t.exports = s || {}, t.exports.__esModule && (t.exports = t.exports.default);
        var r = "function" == typeof t.exports ? t.exports.options || (t.exports.options = {}) : t.exports;
        a && (r.template = a), r.computed || (r.computed = {}), Object.keys(o).forEach(function (t) {
            var e = o[t];
            r.computed[t] = function () {
                return e
            }
        })
    }
});