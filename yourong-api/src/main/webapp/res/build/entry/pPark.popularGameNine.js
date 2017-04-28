webpackJsonp([2], {
    39: function (e, i) {
        "use strict";
        Object.defineProperty(i, "__esModule", {value: !0});
        var t = env.resPath;
        i.default = {
            data: function () {
                return {
                    siteurl: env.siteurl,
                    res: env.resPath,
                    alipath: env.alipath,
                    winnerListData: {list: []},
                    popularity: initData.result.popularity,
                    rewardText: "",
                    point: "",
                    index: "",
                    pointInput: !1,
                    unEnoughPopularity: !1,
                    coverShow: !1,
                    rewardTips: !1,
                    isGuessed: !1,
                    rewardPoint: 0,
                    showRules: !1,
                    pointTrue: !1,
                    isLogined: !1
                }
            }, ready: function () {
                this.winnerListData.list = this.$root.initMessage.data.result.list
            }, created: function () {
                var e = this;
                window.scrollTo(0, 0), 1 == env.Os && Android.UpdateTitle("幸运九宫格"), this.$on("methodCallback", function (i, t) {
                    var a = {ratio: 0};
                    switch (i.data.result.rewardCode) {
                        case"noReward":
                            a.ratio = 0;
                            break;
                        case"PopularityFor10times":
                            a.ratio = 10;
                            break;
                        case"PopularityFor5times":
                            a.ratio = 5;
                            break;
                        case"PopularityFor2times":
                            a.ratio = 2
                    }
                    a.ratio ? e.rewardText = "恭喜获得" + a.ratio * e.point + "点人气值,您就是今天的幸运儿~" : e.rewardText = "再来一次，幸运女神会眷顾你哦", e.rewardTips = !0, e.pointInput = !1, e.rewardPoint = a.ratio, e.popularity = initData.result.popularity = e.popularity - e.point + e.point * a.ratio, setTimeout(function () {
                        e.gridAnimate(a.ratio), e.isGuessed = !0, e.point = ""
                    }, 1e3)
                }), hook.logined ? e.isLogined = !0 : e.isLogined = !1
            }, computed: {}, watch: {
                "winnerListData.list": function () {
                    var e = this;
                    e.scrollUserList({size: 5, height: -50, length: 1})
                }
            }, methods: {
                getImg: function (e) {
                    return t + e
                }, Multiplication: function (e, i) {
                    return e * i
                }, inputPoint: function (e, i) {
                    var t = this;
                    return t.index = e, !t.isGuessed && (t.pointInput = !0, void(t.coverShow = !0))
                }, getLottery: function (e, i) {
                    var t = this;
                    if (t.popularity < t.point)t.unEnoughPopularity = !0, t.pointInput = !1, t.point = ""; else {
                        if (t.isGuessed || !t.pointTrue)return !1;
                        hook.getEvent(e + "&isNeedRealName=0&args_chip_1_integer_" + t.point, $(i.currentTarget), 1)
                    }
                }, gridAnimate: function (e) {
                    for (var i = [2, 5, 10], t = [], a = [], s = $("#j-popularGameNine").find("span"), n = this, o = 0; o < 9; o++)t.push(o);
                    if (t.splice(n.index, 1), e) {
                        var r = $.inArray(e, i);
                        r !== -1 && i.splice(r, 1), a = n.getArrayItems(t, 2), s.addClass("z-opened"), s.eq(n.index).removeClass("z-opened").addClass("z-actived" + e), s.eq(a[0]).removeClass("z-opened").addClass("z-opened" + i[0]), s.eq(a[1]).removeClass("z-opened").addClass("z-opened" + i[1])
                    } else a = n.getArrayItems(t, 3), s.addClass("z-opened"), s.eq(a[0]).removeClass("z-opened").addClass("z-opened" + i[0]), s.eq(a[1]).removeClass("z-opened").addClass("z-opened" + i[1]), s.eq(a[2]).removeClass("z-opened").addClass("z-opened" + i[2]), s.eq(n.index).removeClass("z-opened").addClass("z-actived" + e)
                }, getArrayItems: function (e, i) {
                    var t = [];
                    for (var a in e)t.push(e[a]);
                    for (var s = [], n = 0; n < i && t.length > 0; n++) {
                        var o = Math.floor(Math.random() * t.length);
                        s[n] = t[o], t.splice(o, 1)
                    }
                    return s
                }, playAgain: function () {
                    var e = $("#j-popularGameNine").find("span"), i = this;
                    i.isGuessed = !1, e.removeClass("z-opened").removeClass("z-opened2").removeClass("z-opened5").removeClass("z-opened10").removeClass("z-actived0").removeClass("z-actived2").removeClass("z-actived5").removeClass("z-actived10"), e.eq(i.index).removeClass("z-actived" + i.rewardPoint)
                }, scrollUserList: function (e) {
                    $("#j-winnerList").each(function () {
                        var i = $(this), t = i.find("li").length;
                        t > e.size && setInterval(function () {
                            var t = i.find("li:visible");
                            i.animate({marginTop: e.height}, 700, function () {
                                t.eq(0).appendTo(i), i.css("margin-top", 0)
                            })
                        }, 3e3)
                    })
                }, cancleBtn: function () {
                    var e = this;
                    e.pointInput = !1, e.unEnoughPopularity = !1, e.coverShow = !1, e.rewardTips = !1, e.showRules = !1
                }, limitInput: function (e) {
                    var i = /^\+?[1-9]\d*$/, t = this;
                    return i.test(e) ? (t.pointTrue = !0, !0) : (t.pointTrue = !1, !1)
                }
            }
        }
    }, 75: function (e, i) {
        e.exports = ' <div> <div class=u-gameBg> <div> <p class="f-fs14 u-myGrade"> 您的人气值：<em class=f-color-gold>{{popularity}}点</em> <span class=u-playAgainBtn v-if=isGuessed @click=playAgain>再玩一次</span> </p> </div> <div class=u-popularGameNine id=j-popularGameNine> <a href=javascript:void(0) @click=inputPoint(0,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(1,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(2,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(3,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(4,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(5,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(6,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(7,$event)><span></span></a> <a href=javascript:void(0) @click=inputPoint(8,$event)><span></span></a> </div> <p class="u-activityRules f-fs14" @click="showRules=true"> 活动规则>> </p> </div> <div class=u-winnerBlock> <p> 9宫格赢家 </p> <div class=u-winnerListWrap> <ul v-if=winnerListData.list.length class=u-winnerList id=j-winnerList> <li v-for="item in winnerListData.list"> <span> <img v-if=item.avatar :src=alipath+item.avatar alt="" width=100%> <img v-else :src="getImg(\'/img/member/login_avatar.png\')" alt="" width=100%> </span> <div class="f-fs14 f-dis-inlineBlock u-memberPhone"> <p>{{item.memberName}}</p> <p class=f-fs12>{{item.createTimeStr2}}</p> </div> <div class="f-fs14 f-dis-inlineBlock u-gainGift"> <p>押宝：<em>{{item.chip}}</em>点</p> <p class=f-color-gold>赢得：<em>{{item.rewardResult}}</em>点</p> </div> </li> </ul> <div v-else class="f-fs16 u-noWinnerList"> 赶快来玩，幸运女神在召唤 </div> </div> </div> <div class=u-activityRulesWrap v-if=showRules> <p class="f-fs14 f-color3">活动规则 <span style="position: absolute;right: 5px;top:20px" class=j-clockBtn @click=cancleBtn><img :src="getImg(\'/img/post/popularity/ico_inputClose.png\')" alt=关闭 width=50%></span></p> <ul class="f-fs14 f-color6"> <li>用户输入任意点数人气值，即可获得一次猜“幸运格”的机会；</li> <li>9宫格中有3块“幸运格”，分别对应10倍、5倍以及2倍人气值奖励， 选中“幸运格”的用户所输入的人气值将翻相应倍数，未选中幸运格的用户人气值不予返还；</li> <li>猜幸运格获得的人气值奖励，将于10分钟内发放；</li> <li>如有其它疑问，请拨打客服热线 <br>400-025-6688，本活动最终解释权归有融网所有。</li> </ul> </div> <div class=u-inputBlock v-if=pointInput> <p class="f-fs18 f-color3">试试手气 <span style="position: absolute;right: 5px;top: 8px" class=j-clockBtn @click=cancleBtn><img :src="getImg(\'/img/post/popularity/ico_inputClose.png\')" alt=关闭 width=50%></span></p> <input v-model=point type=number class="f-fs14 f-color9" placeholder=请输入人气值> <a href=javascript:void(0) @click=getLottery(0,$event)> <div class="u-sureBtn f-fs16" :class="{\'f-color-blue\':limitInput(point)}">确定</div> </a> </div> <div class="u-gameTipBlock f-fs18" v-if=unEnoughPopularity> <p> 人气值不足 </p> <div class="u-sureBtn f-fs18 f-color-blue" @click=cancleBtn>确定</div> </div> <div class="u-gameTipBlock f-fs18" v-if=rewardTips> <p class=f-color6> {{rewardText}} </p> <div class="u-sureBtn f-fs18 f-color-blue" @click=cancleBtn>确定</div> </div> <div class=u-cover style="display: block" v-if=coverShow></div> </div> '
    }, 77: function (e, i, t) {
        var a, s, n = {};
        a = t(39), s = t(75), e.exports = a || {}, e.exports.__esModule && (e.exports = e.exports.default);
        var o = "function" == typeof e.exports ? e.exports.options || (e.exports.options = {}) : e.exports;
        s && (o.template = s), o.computed || (o.computed = {}), Object.keys(n).forEach(function (e) {
            var i = n[e];
            o.computed[e] = function () {
                return i
            }
        })
    }
});