<style>
    body {
        background: #f3f3f3;
    }
</style>
<template>
    <div>
        <!--人气值清零提示条-->
        <div class="u-topTips" v-if="overPopularity.show&&showTips">
            <div>
                <div>
                    <div v-if="overPopularity.overduePopularity===0&&showTips">
                        <span>您没有人气值将于{{overPopularity.outlayTime | formatTime 'yyyy年MM月dd日'}}失效，习惯很好，继续保持噢！</span>
                        <em @click="showOnce" class="u-closeBtn"><img
                                :src=" getImg('/img/post/popularity/closeBtn.png')" alt="" width="100%"></em>
                    </div>
                    <div v-if="overPopularity.overduePopularity!=0">
                        您有{{overPopularity.overduePopularity}}点{{overPopularity.incomeTime | formatTime
                        'yyyy年MM月dd日'}}前获得的人气值，将于{{overPopularity.outlayTime | formatTime 'yyyy年MM月dd日'}}过期，建议您尽快兑换使用噢！
                        <em v-if="overPopularity.showRule" @click="ShowRules('true')" class="u-popularityRules">查看过期规则>&nbsp;&nbsp;&nbsp;</em>
                    </div>
                </div>
            </div>
            <div class="u-rulesBlock" v-if="showRules&&overPopularity.showRule">
                <h2 class="f-color3">人气值说明</h2>
                <p class="f-color6">
                    1，人气值获取：人气值可通过每日签到、抢标五重礼、投资分享红包等活动获取。<br>
                    2，人气值使用：人气值可用于兑换现金券、收益券及更多虚拟商品和超值实物。<br>
                    3，人气值有效期：每年12月31日，用户于一年前获取且至今未使用的人气值将过期失效。
                </p>
                <div @click="closeTip" class="u-rulesSureBtn">确定</div>
            </div>
        </div>

        <div class="u-memberInfo" :class="{'z-unLogined':!isLogined}">
            <div v-if="isLogined">
                <a @click="memberBehavior('G1',1)">
                    <!--:href="'siteurl/yrwHtml/memberCenter?isAuthorization=Y&title='+memberCenterTitle"-->
                    <!--v-link="'/memberCenter?title='+memberCenterTitle"-->
                    <img v-if="homeData.avatars" class="u-memberAvatar" :src="homeData.avatars" alt="">
                    <img v-else class="u-memberAvatar" :src="getImg('/img/member/login_avatar.png')" alt="">
                    <div class="u-memberName">
                        <p class="f-fs16">{{homeData.username}} <img
                                :src="getImg('/img/post/popularity/home_parks_v'+user.level+'.png')" alt="" width="15px"
                                height="15px"></p>
                        <span>
                            <p v-if="user.level==5" class="f-fs12">厉害的你已经在会员链顶端啦</p>
                            <p v-else
                               class="f-fs12">再积累{{homeData.needSncreaseScore}}分，升级到V{{user.level+1}}(每月1号24点前更新)</p>
                        </span>
                    </div>
                </a>
            </div>
            <div class="u-unLoginTip" v-else>
                <div>
            <span>
            <p class="f-fs20 f-color3">人气值大用途</p>
            <p class="f-fs14 f-color9">全新乐园玩转嗨不停~</p>
            </span>
            <span class="f-fs14 u-loginBtn">
            <a href="javascript:void(0)" @click="memberBehavior('G9'),gotoLogin($event)">登录/注册</a>
            </span>
                </div>
            </div>
        </div>
        <div class="f-fs14 u-memberPoint" :class="{'f-dn':!isLogined}">
            <span>
                <a href="javascript:void(0)" @click="memberBehavior('G2'),linkToEputationRecord($event)"
                   class="f-color6 f-fs12">
                    <img :src=" getImg('/img/post/popularity/ico_integral.png')" alt="" width="18%">
                <em class="f-coloreDC f-fs14">{{homeData.popularity}}</em>点人气值
               </a>
            </span>

            <span>
                <a href="javascript:void(0)" @click="memberBehavior('G3',0)">
                    <img :src=" getImg('/img/post/popularity/ico_earn.png')" alt="" width="18%">
                   <em class="f-fs14 f-color6">赚人气值</em>
               </a>
                <!--@click="memberBehavior('G3')"-->
            </span>

            <span>
            <a href="javascript:void(0)" @click="memberBehavior('G4',2)">
                <!--,requestData(4,$event,'exchangeRecord')-->
                 <img :src=" getImg('/img/post/popularity/ico_exchange.png')" alt="" width="18%">
                <em class="f-fs14 f-color6">兑换记录</em>
            </a>
                </span>
        </div>
        <div style="background:#F3F3F3;width: 100%;height: 10px; "></div>

        <div class="u-activity">
            <a href="javascript:void(0)" @click="memberBehavior('G5'),requestData(1,$event,'record')">
                <img :src=" getImg('/img/post/popularity/activityBanner.png')" alt="九宫格" width="100%">
            </a>
        </div>
        <div style="background:#F3F3F3;width: 100%;height: 10px; "></div>
        <div class="m-productList">
            <!--双节特惠商品-->
            <div v-if="homeData.doubleList.length" class="j-investmentList">
                <div style="margin-left:2%">
                    <span class="f-fs16 f-color3">新品特惠</span>
                    <a :href="siteurl+'/yrwHtml/investProductList?isAuthorization=Y&title='+investProductListTitle">
                        <!--#forInvestment-->
                        <span class="f-fs12 f-color9 f-fr u-getMore">更多></span>
                    </a>
                </div>
                <ul class="u-productList">
                    <li v-for="item in homeData.doubleList">
                        <div style="margin-left: 4%">
                            <a :href="siteurl+'/yrwHtml/productDetail?isAuthorization=Y&title='+productDetailTitle+'&goodId='+item.id"
                               data-id="item.id">
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
                                    <img v-if="item.tags==7" class="u-redTips u-labelRush"
                                         :src="getImg('/img/post/popularity/label_rush3.png')" alt="">
                                    <div class="u-imgBlock">
                                        <img :src="item.image" alt=""
                                             width="100%">
                                    </div>
                                </div>
                                <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div>
                                <div class="f-fs12 f-color9"><span
                                        class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span>
            <span class="u-originalPrice"
                  v-if="item.price>item.discountPrice">{{item.price}}点</span></div>

                            </a>
                        </div>
                    </li>
                </ul>
            </div>
            <!--读取商品的图片时有点问题-->
            <div v-if="homeData.investmentList.length" class="j-investmentList">
                <div style="margin-left:2%">
                    <span class="f-fs16 f-color3">投资专享</span><span class="f-fs12 f-color9">&nbsp;&nbsp;&nbsp;V2及以上等级兑换享折扣特权</span>
                    <a @click="memberBehavior('G6',3)">
                        <!--:href="siteurl+'/yrwHtml/investProductList?isAuthorization=Y&title='+investProductListTitle"-->
                        <!--#forInvestment-->
                        <span class="f-fs12 f-color9 f-fr u-getMore">更多></span>
                    </a>

                </div>
                <ul class="u-productList">
                    <li v-for="item in homeData.investmentList">
                        <div style="margin-left: 4%">
                            <a :href="siteurl+'/yrwHtml/productDetail?isAuthorization=Y&title='+productDetailTitle+'&goodId='+item.id"
                               data-id="item.id">
                                <!--@click="requestData(2,$event,'productDetail',item.id)"-->
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
                                        <img :src="item.image" alt=""
                                             width="100%">
                                    </div>
                                </div>
                                <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div>
                                <div class="f-fs12 f-color9"><span
                                        class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span>
                                    <span class="u-originalPrice"
                                          v-if="item.price>item.discountPrice">{{item.price}}点</span></div>

                            </a>
                        </div>
                    </li>
                </ul>
            </div>
            <div v-if="homeData.virtualCardList.length" class="j-virtualCardList">
                <div style="margin-left: 2%">
                    <span class="f-fs16 f-color3">虚拟卡券</span>
                    <a @click="memberBehavior('G7',4)">
                        <span class="f-fs12 f-color9 f-fr u-getMore">更多></span>
                    </a>
                    <!--#virtualCard-->
                </div>
                <ul class="u-productList">
                    <li v-for="item in homeData.virtualCardList">
                        <!--<router-link to="/productDetail">-->
                        <a :href="siteurl+'/yrwHtml/productDetail?isAuthorization=Y&title='+productDetailTitle+'&goodId='+item.id"
                           data-id="item.id">
                            <div style="margin-left: 4%">
                                <div class="u-couponImg f-fs14 " :class="{'z-productOver':item.availInventor<1}">
                                    <img v-if="item.tags==1" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_new.png')" alt="">
                                    <img v-if="item.tags==2" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_tejia.png')" alt="">
                                    <img v-if="item.tags==3" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_recommend.png')" alt="">
                                    <img v-if="item.tags==4" class="u-redTips u-labelRush"
                                         :src="getImg('/img/post/popularity/label_rush.png')" alt="">
                                    <div class="u-imgBlock">
                                        <!--<img v-if="item.image" :src="{{image}}" alt="" width="100%">-->
                                        <img :src="item.image" alt="" width="100%">
                                    </div>
                                </div>
                                <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div>
                                <div class="f-fs12 f-color9"><span
                                        class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span>
                                    <span class="u-originalPrice"
                                          v-if="item.price>item.discountPrice">{{item.price}}点</span></div>
                            </div>
                        </a>

                    </li>
                </ul>
            </div>
            <div v-if="homeData.physicalList.length" class="j-physicalList">
                <div style="margin-left: 2%">
                    <span class="f-fs16 f-color3">超值实物</span>
                    <a @click="memberBehavior('G8',5)">
                        <span class="f-fs12 f-color9 f-fr u-getMore">更多></span>
                    </a>
                    <!--#valueInKind-->
                </div>
                <ul class="u-productList">
                    <li v-for="item in homeData.physicalList">
                        <div style="margin-left: 4%">
                            <a :href="siteurl+'/yrwHtml/productDetail?isAuthorization=Y&title='+productDetailTitle+'&goodId='+item.id"
                               data-id="item.id">
                                <div class="u-couponImg f-fs14" :class="{'z-productOver':item.availInventor<1}">
                                    <img v-if="item.tags==1" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_new.png')" alt="">
                                    <img v-if="item.tags==2" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_tejia.png')" alt="">
                                    <img v-if="item.tags==3" class="u-redTips"
                                         :src="getImg('/img/post/popularity/label_recommend.png')" alt="">
                                    <img v-if="item.tags==4" class="u-redTips u-labelRush"
                                         :src="getImg('/img/post/popularity/label_rush.png')" alt="">
                                    <div class="u-imgBlock"><img :src="item.image" alt=""
                                                                 width="100%"></div>
                                </div>
                                <div class="f-fs14 f-color6 u-goodsName"> {{item.goodsName}}</div>
                                <div class="f-fs12 f-color9"><span
                                        class="f-fs16 f-coloreDC">{{item.discountPrice}}点</span>
                                    <span class="u-originalPrice"
                                          v-if="item.price>item.discountPrice">{{item.price}}点</span></div>
                            </a>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <!--v-if="show&&levelUpFlag"-->
        <div class="u-tipBlock" v-if="show&&levelUpFlag">
            <div>
                <div class="u-tipBackground ">
                    V{{homeData.vipLevel}}
                </div>
                <div style="text-align: left">
                    <p class="f-fs16 f-color3">恭喜您，升级啦！</p>
                    <p class="f-fs12 f-color9">
                        赶快去看看您的新特权吧~
                    </p>
                </div>
            </div>
            <div class="f-fs16 u-sureBtn" @click="closeTip">
                我知道了
            </div>
        </div>
        <div class="u-cover j-cover" style="display: block" v-if="(show&&levelUpFlag)||showRules"></div>
    </div>
</template>
<script>
    const RES = env.resPath
    const Data = initData
    export default {
        data: function () {
            return {
                url: 'popularityPark?',
                siteurl: env.path,
                res: env.resPath,
                alipath: env.alipath,
                os: env.Os,
                user: {
                    level: ''
                },
                homeData: Data.result,
                levelUpFlag: '',
                method: '',
                overPopularity: '',
                show: false,
                showTips: true,
                showRules: false,
                showSpecial: false,
                memberCenterTitle: window.encodeURI('会员中心'),
                investProductListTitle: window.encodeURI('投资专享'),
                entityProductListTitle: window.encodeURI('超值实物'),
                fictitiousProductListTitle: window.encodeURI('虚拟卡券'),
                productDetailTitle: window.encodeURI('商品详情'),
                isLogined: false,
                goodsType: '',
                isLink: ''
            }
        },
        beforeCompile: function () {
            var self = this
//            switch (env.parameter) {
//                case 'memberCenter':
//                    this.$router.replace({path: '/memberCenter?title=' + self.memberCenterTitle});
//                    break;
//                case 'mall':
//                    this.$router.replace({path: '/allProductList?title=' + self.investProductListTitle});
//                    break;
//                case 'purchaseHistory':
//                    this.$router.replace({path: '/exchangeRecord?title=' + self.encoudeTitle('兑换记录')});
//                    break;
//                default:
//                    //this.$router.go({ path: '/'});
//                    break
//            }
        },
        created: function () {

            var self = this
//            window.scrollTo(0, 0)
            console.log(self.homeData)
            if (hook.logined) {
                self.isLogined = true
                self.user.level = Data.result.vipLevel
                self.levelUpFlag = Data.result.levelUpFlag
                self.overPopularity = Data.result.overduePopularity
                console.log('cookie:', this.getCookie('isShowed_memberId_00_' + self.homeData.memberId))
                if (this.getCookie('isShowed_memberId_00_' + self.homeData.memberId)) {
                    this.show = false
                } else {
                    this.setCookie("isShowed_memberId_00_" + self.homeData.memberId, self.homeData.memberId, "d27");
                    this.show = true
                }
                console.log('show:', this.show)
            } else {
                self.isLogined = false
            }

//            判断是否出现清零提示条
            if (this.getCookie('isShowed_memberId_popularity_' + self.homeData.memberId)) {
                this.showTips = false
            }

            //传递安卓title
            if (env.Os == 1) {
                Android.UpdateTitle('人气值乐园')
            }

            this.$on('methodCallback', function (data, method) {
                switch (self.method) {
                    case 1:
                        self.$router.go({path: '/popularGameNine?title=' + self.encoudeTitle('幸运九宫格')})
                        this.$root.initMessage = data
                        break;
//                    case 2:
//                        self.$router.go({path: '/productDetail?title=' + self.encoudeTitle('商品详情')})
//                        this.$root.DetailtMessage = data
//                        break;
//                    case 4:
//                        self.$router.go({path: '/exchangeRecord?title=' + self.encoudeTitle('兑换记录')})
//                        this.$root.initData = data
//                        break;
//                    case 6:
//                        self.$router.go({path: '/eputationRecord?title=' + self.encoudeTitle('人气值流水')})
//                        this.$root.initMessage = data
//                        break;
//                    case 14:
//                        if (self.isLink == 0) {
//                            this.location.href=self.siteurl+'/mstation/post/gainPopularity?isAuthorization=Y'
//                        } else {
//                        }
//                        break;
                    default:
                        break;
                }

            })
            console.log(self.overPopularity)

        },
        beforeDestroy: function () {
            this.$off('methodCallback')
        },
        computed: {},
        methods: {
            getImg: function (url) {
                return RES + url
            },
            closeTip: function () {
                var self = this;
                self.levelUpFlag = false
                this.show = false
                this.showRules = false
            },

            getUrl: function (url) {
                var self = this
                return self.siteurl + url + '&title=' + window.encodeURI('赚人气值')
            },
            gotoLogin: function (event) {
                hook.login($(event.currentTarget))
            },
//            closeTip: function () {
//                var self = this;
//                self.levelUpFlag = false
//                this.show = false
//            },
            requestData: function (invokeMethod, event, type, id) {
                console.log('id:', id)
                var self = this
                self.goodsType = type
                var args = {
                    eputationRecord: 'args_pageNo_1_integer_1',
                    record: ''
//                    productDetail: 'args_goodId_1_long_' + id + '&isUserID=0',
//                    exchangeRecord: 'args_currentPage_1_integer_1',
//                    virtualCardList: 'args_goodsType_1_integer_2&isUserID=0',
//                    physicalList: 'args_goodsType_1_integer_3&isUserID=0'

                }
                this.method = invokeMethod;
                hook.getEvent(invokeMethod + '&isNeedRealName=0&' + args[type], $(event.currentTarget), 1)
            },
            getCookie: function (name) {
                var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
                if (arr = document.cookie.match(reg)) return unescape(arr[2]);
                else return null;
            },
            setCookie: function (name, value, time) {
                var self = this;
                var strsec = self.getsec(time);
                var exp = new Date();
                exp.setTime(exp.getTime() + strsec * 1);
                document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
            },
            getsec: function (str) {
                var str1 = str.substring(1, str.length) * 1;
                var str2 = str.substring(0, 1);
                if (str2 == "s") {
                    return str1 * 1000;
                } else if (str2 == "h") {
                    return str1 * 60 * 60 * 1000;
                } else if (str2 == "d") {
                    return str1 * 24 * 60 * 60 * 1000;
                }
            },
            encoudeTitle: function (title) {
                return window.encodeURI(title)
            },
            ShowRules: function (value) {
                this.showRules = value
            },
            showOnce: function () {
                var self = this
                self.showTips = false
                this.setCookie("isShowed_memberId_popularity_" + self.homeData.memberId, self.homeData.memberId, "d31");
            },
            memberBehavior: function (anchor, isLink) {
                var self = this
                hook.getEvent(14 + '&isNeedRealName=0&args_anchor_1_string_' + anchor + '&isUserID=0', $(event.currentTarget), 1)
                /*
                 * 0--赚人气值页面
                 * 1--会员中心页面
                 * 2--兑换记录页面
                 * 3--投资专享页面
                 * 4--虚拟卡券页面
                 * 5--超值实物页面
                 * */
                switch (isLink) {
                    case 0:
                        $(event.currentTarget).attr('href', self.siteurl + '/mstation/post/gainPopularity?isAuthorization=Y&title=' + window.encodeURI('赚人气值'))
                        break;
                    case 1:
                        $(event.currentTarget).attr('href', self.siteurl + '/yrwHtml/memberCenter?isAuthorization=Y&title=' + window.encodeURI('会员中心'))
                        break;
                    case 2:
                        $(event.currentTarget).attr('href', self.siteurl + '/yrwHtml/exchangeRecord?isAuthorization=Y&title=' + window.encodeURI('兑换记录'))
                        break;
                    case 3:
                        $(event.currentTarget).attr('href', self.siteurl + '/yrwHtml/investProductList?isAuthorization=Y&title=' + window.encodeURI('投资专享'))
                        break;
                    case 4:
                        $(event.currentTarget).attr('href', self.siteurl + '/yrwHtml/fictitiousProductList?isAuthorization=Y&title=' + window.encodeURI('虚拟卡券'))
                        break;
                    case 5:
                        $(event.currentTarget).attr('href', self.siteurl + '/yrwHtml/entityProductList?isAuthorization=Y&title=' + window.encodeURI('超值实物'))
                        break;
                    default:
                        break

                }
//                if (isLink == 0) {
//                    $(event.currentTarget).attr('href', self.siteurl + '/mstation/post/gainPopularity?isAuthorization=Y&title=' + window.encodeURI('赚人气值'))
//                }
            },
            linkToEputationRecord: function () {
                var self = this
                if (self.os == 1) {
                    Android.ToActivity('popularityRecord', null);
                } else {
                    $(event.currentTarget).attr('href', 'yrw-skip://invokeMethod=popularityRecord')
                }
            }
        }
    }
</script>