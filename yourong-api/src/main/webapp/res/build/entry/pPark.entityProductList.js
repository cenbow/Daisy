webpackJsonp([7], {
    41: function (t, e) {
        "use strict";
        Object.defineProperty(e, "__esModule", {value: !0});
        var i = env.resPath;
        e.default = {
            data: function () {
                return {siteurl: env.siteurl, res: env.resPath, productData: this.$root.physicalData.data.result.data}
            }, created: function () {
                window.scrollTo(0, 0), 1 == env.Os && Android.UpdateTitle("超值实物"), this.$on("methodCallback", function (t, e) {
                    this.$root.DetailtMessage = t, this.$router.go({path: "/productDetail?title=" + window.encodeURI("商品详情")})
                })
            }, computed: {}, methods: {
                getImg: function (t) {
                    return i + t
                }, reqData: function (t, e, i) {
                    console.log("requestMethod222:", t), console.log(hook, hook.getEvent), hook.getEvent(t + "&isNeedRealName=0&args_goodId_1_long_" + i + "&isUserID=0", $(e.currentTarget), 1)
                }
            }
        }
    }, 86: function (t, e) {
        t.exports = ' <div> <div class=m-productList> <div id=forInvestment v-if=productData.length> <ul class=u-productList> <li v-for="item in productData"> <div style="margin-left: 4%"> <a href=javascript:void(0) data-id=item.id @click=reqData(2,$event,item.id)> <div class="u-couponImg f-fs14" :class="{\'z-productOver\':item.availInventor<1}"> <img v-if="item.tags==1" class=u-redTips :src="getImg(\'/img/post/popularity/label_new.png\')" alt=""> <img v-if="item.tags==2" class=u-redTips :src="getImg(\'/img/post/popularity/label_tejia.png\')" alt=""> <img v-if="item.tags==3" class=u-redTips :src="getImg(\'/img/post/popularity/label_recommend.png\')" alt=""> <img v-if="item.tags==4" class="u-redTips u-labelRush" :src="getImg(\'/img/post/popularity/label_rush.png\')" alt=""> <div class=u-imgBlock> <img :src=item.image alt="" width=100%> </div> </div> <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div> <div class="f-fs12 f-color9"> <span class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span> <span class=u-originalPrice v-if="item.price>item.discountPrice">{{item.price}}点</span> </div> </a> </div> </li> </ul> </div> </div> </div> '
    }, 94: function (t, e, i) {
        var s, o, a = {};
        s = i(41), o = i(86), t.exports = s || {}, t.exports.__esModule && (t.exports = t.exports.default);
        var r = "function" == typeof t.exports ? t.exports.options || (t.exports.options = {}) : t.exports;
        o && (r.template = o), r.computed || (r.computed = {}), Object.keys(a).forEach(function (t) {
            var e = a[t];
            r.computed[t] = function () {
                return e
            }
        })
    }
});