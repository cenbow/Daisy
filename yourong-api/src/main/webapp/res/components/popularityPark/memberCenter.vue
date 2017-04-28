<template xmlns="http://www.w3.org/1999/html">
    <div>
        <div class="u-myGradeInfo">
            <div class="swiper-container02 u-swiperWrap" style="width: 100%;margin-top: 20px;">
                <div class="swiper-wrapper" style="height: 60px;line-height: 60px;">
                    <div class="swiper-slide u-myGradeProcess" :class="{'z-myGrade':level==0 , 'z-unactived':level<1}">
                        <span style="position: absolute;left: -180px;width: 200px;background: #D74148"></span>
                        <div @click="changeCurrent(0)" class="u-imgBlock">
                            <a href="javascript:void(0)" @click="memberBehavior('H6')">
                                <img v-if="level>0" :src="getImg('/img/post/popularity/parks_V0.png')" alt=""
                                     width="60%">
                                <div v-if="level==0">
                                    <i>
                                        <img class="u-avatars" v-if="initData.avatars" :src="alipath+initData.avatars"
                                             alt="" width="100%">
                                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                                    </i>
                                    <img class="u-memberGradeImg" :src="getImg('/img/post/popularity/member_vo.png')"
                                         alt="">
                                </div>
                            </a>
                        </div>
                        <span></span>
                    </div>
                    <div class="swiper-slide u-myGradeProcess" :class="{'z-myGrade':level==1 , 'z-unactived':level<2}">

                        <div @click="changeCurrent(1)" class="u-imgBlock ">
                            <a href="javascript:void(0)" @click="memberBehavior('H6')">
                                <!--##                        当前的等级-->
                                <img v-if="level>1" :src="getImg('/img/post/popularity/parks_V1.png')" alt=""
                                     width="60%">
                                <div v-if="level==1">
                                    <i>
                                        <img class="u-avatars" v-if="initData.avatars" :src="alipath+initData.avatars"
                                             alt="" width="100%">
                                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                                    </i>
                                    <img class="u-memberGradeImg" :src="getImg('/img/post/popularity/member_v1.png')"
                                         alt="">
                                </div>
                                <img v-if="level<1&&current!=1" :src="getImg('/img/post/popularity/unparks_V1.png')"
                                     alt=""
                                     width="60%">
                                <!--##                        没到的等级，没有选择-->
                                <img v-if="current==1&&level<1" :src="getImg('/img/post/popularity/clickparks_V1.png')"
                                     alt="" width="60%">
                            </a>
                        </div>
                        <span></span>

                    </div>
                    <div class="swiper-slide u-myGradeProcess" :class="{'z-myGrade':level==2 , 'z-unactived':level<3}">

                        <div @click="changeCurrent(2)" class="u-imgBlock">
                            <a href="javascript:void(0)" @click="memberBehavior('H6')">
                                <img v-if="level>2" :src="getImg('/img/post/popularity/parks_V2.png')" alt=""
                                     width="60%">
                                <div v-if="level==2">
                                    <i>
                                        <img class="u-avatars" v-if="initData.avatars" :src="alipath+initData.avatars"
                                             alt="" width="100%">
                                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                                    </i>
                                    <img class="u-memberGradeImg" :src="getImg('/img/post/popularity/member_v2.png')"
                                         alt="">
                                </div>
                                <img v-if="level<2&&current!=2" :src="getImg('/img/post/popularity/unparks_V2.png')"
                                     alt=""
                                     width="60%">
                                <!--##                        没到的等级，没有选择-->
                                <img v-if="current==2&&level<2" :src="getImg('/img/post/popularity/clickparks_V2.png')"
                                     alt="" width="60%">
                            </a>
                        </div>
                        <span></span>
                    </div>
                    <div class="swiper-slide u-myGradeProcess" :class="{'z-myGrade':level==3 , 'z-unactived':level<4}">

                        <div @click="changeCurrent(3)" class="u-imgBlock">
                            <a href="javascript:void(0)" @click="memberBehavior('H6')">
                                <img v-if="level>3" :src="getImg('/img/post/popularity/parks_V3.png')" alt=""
                                     width="60%">
                                <div v-if="level==3">
                                    <i>
                                        <img class="u-avatars" v-if="initData.avatars" :src="alipath+initData.avatars"
                                             alt="" width="100%">
                                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                                    </i>
                                    <img class="u-memberGradeImg" :src="getImg('/img/post/popularity/member_v3.png')"
                                         alt="">
                                </div>
                                <img v-if="level<3&&current!=3" :src="getImg('/img/post/popularity/unparks_V3.png')"
                                     alt=""
                                     width="60%">
                                <!--##                        没到的等级，没有选择-->
                                <img v-if="current==3&&level<3" :src="getImg('/img/post/popularity/clickparks_V3.png')"
                                     alt="" width="60%">
                            </a>
                        </div>
                        <span></span>
                    </div>
                    <div class="swiper-slide u-myGradeProcess" :class="{'z-myGrade':level==4 , 'z-unactived':level<5}">

                        <div @click="changeCurrent(4)" class="u-imgBlock">
                            <a href="javascript:void(0)" @click="memberBehavior('H6')">
                                <img v-if="level>4" :src="getImg('/img/post/popularity/parks_V4.png')" alt=""
                                     width="60%">
                                <div v-if="level==4">
                                    <i>
                                        <img class="u-avatars" v-if="initData.avatars" :src="alipath+initData.avatars"
                                             alt="" width="100%">
                                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                                    </i>
                                    <img class="u-memberGradeImg" :src="getImg('/img/post/popularity/member_v4.png')"
                                         alt="">
                                </div>
                                <img v-if="level<4&&current!=4" :src="getImg('/img/post/popularity/unparks_V4.png')"
                                     alt=""
                                     width="60%">
                                <!--##                        没到的等级，没有选择-->
                                <img v-if="current==4&&level<4" :src="getImg('/img/post/popularity/clickparks_V4.png')"
                                     alt="" width="60%">
                            </a>
                        </div>
                        <span></span>
                    </div>
                    <div class="swiper-slide u-myGradeProcess" :class="{'z-myGrade':level==5 , 'z-unactived':level<5}">
                        <div @click="changeCurrent(5)" class="u-imgBlock">
                            <a href="javascript:void(0)" @click="memberBehavior('H6')">
                                <img v-if="level>5" :src="getImg('/img/post/popularity/parks_V5.png')" alt=""
                                     width="60%">
                                <div v-if="level==5">
                                    <i>
                                        <img class="u-avatars" v-if="initData.avatars" :src="alipath+initData.avatars"
                                             alt="" width="100%">
                                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                                    </i>
                                    <img class="u-memberGradeImg" :src="getImg('/img/post/popularity/member_v5.png')"
                                         alt="">
                                </div>
                                <img v-if="level<5&&current!=5" :src="getImg('/img/post/popularity/unparks_V5.png')"
                                     alt=""
                                     width="60%">
                                <!--##                        没到的等级，没有选择-->
                                <img v-if="current==5&&level<5" :src="getImg('/img/post/popularity/clickparks_V5.png')"
                                     alt="" width="60%">
                            </a>
                        </div>
                        <span class="u-lastSpan"></span>
                    </div>
                </div>
            </div>
            <div class="u-myGradeDetail" style="display: block">
                <a href="javascript:void(0)" @click="memberBehavior('H1'),requestData(7,$event,'成长记录')">
                    <div class="u-myGrade">{{popularity}}</div>
                    <p class="f-fs12">我的成长值</p>
                </a>
                <div>
                    <p v-if="level==5" class="f-fs12">厉害的你已经在会员链顶端啦 &nbsp;&nbsp;<a class="u-promotionTip"
                                                                                   v-link="'/growthRecode?title='+growthRecodeTitle"
                                                                                   @click="memberBehavior('H2')">如何升级?</a>
                    </p>
                    <p v-else class="f-fs12">再积累<em>{{needSncreaseScore}}</em>分，升级到<em>V{{level+1}}</em> <a
                            class="u-promotionTip" v-link="'/growthRecode?title='+growthRecodeTitle"
                            @click="memberBehavior('H2')">如何升级?</a></p>
                </div>
            </div>
        </div>
        <div class="m-contentInfo">
            <div v-if="current==0" class="u-memberGrade00 u-gradeGift">
                <ul>
                    <li>
                        <a v-link="'/birthdayGift?title='+birthdayGiftTitle" @click="memberBehavior('H5')">
                            <img :src="getImg('/img/post/popularity/ico_birthdaygift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">生日礼包</p>
                                <p class="f-fs12 f-color9">当天签到人气值翻10倍，送50元现金券、0.5%收益券</p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
            <div v-if="current==1" class="u-memberGrade01 u-gradeGift">
                <ul>
                    <li>
                        <a v-link="'/upgradePacks?title='+upgradePacksTitle" @click="memberBehavior('H4')">
                            <img :src="getImg('/img/post/popularity/ico_updategift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">升级礼包</p>
                                <p class="f-fs12 f-color9">50元现金券</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/birthdayGift?title='+birthdayGiftTitle" @click="memberBehavior('H5')">
                            <img :src="getImg('/img/post/popularity/ico_birthdaygift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">生日礼包</p>
                                <p class="f-fs12 f-color9">当天签到人气值翻10倍，送50元现金券、0.5%收益券</p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
            <div v-if="current==2" class="u-memberGrade02 u-gradeGift">
                <ul>
                    <li>
                        <a v-link="'/membershipPrivileges?title='+membershipPrivilegesTitle"
                           @click="memberBehavior('H3')">
                            <img :src="getImg('/img/post/popularity/ico_perexchange.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">优惠兑换现金券</p>
                                <p class="f-fs12 f-color9">人气值兑换现金券9.6折</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/upgradePacks?title='+upgradePacksTitle" @click="memberBehavior('H4')">
                            <img :src="getImg('/img/post/popularity/ico_updategift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">升级礼包</p>
                                <p class="f-fs12 f-color9">50元现金券</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/birthdayGift?title='+birthdayGiftTitle" @click="memberBehavior('H5')">
                            <img :src="getImg('/img/post/popularity/ico_birthdaygift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">生日礼包</p>
                                <p class="f-fs12 f-color9">当天签到人气值翻10倍，送50元现金券、1%收益券</p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
            <div v-if="current==3" class="u-memberGrade03 u-gradeGift ">
                <ul>
                    <li>
                        <a v-link="'/membershipPrivileges?title='+membershipPrivilegesTitle"
                           @click="memberBehavior('H3')">
                            <img :src="getImg('/img/post/popularity/ico_perexchange.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">优惠兑换现金券</p>
                                <p class="f-fs12 f-color9">人气值兑换现金券9.4折</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/upgradePacks?title='+upgradePacksTitle" @click="memberBehavior('H4')">
                            <img :src="getImg('/img/post/popularity/ico_updategift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">升级礼包</p>
                                <p class="f-fs12 f-color9">50元现金券</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/birthdayGift?title='+birthdayGiftTitle" @click="memberBehavior('H5')">
                            <img :src="getImg('/img/post/popularity/ico_birthdaygift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">生日礼包</p>
                                <p class="f-fs12 f-color9">当天签到人气值翻10倍，送50元现金券、1%收益券</p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
            <div v-if="current==4" class="u-memberGrade04 u-gradeGift ">
                <ul>
                    <li>
                        <a v-link="'/membershipPrivileges?title='+membershipPrivilegesTitle"
                           @click="memberBehavior('H3')">
                            <img :src="getImg('/img/post/popularity/ico_perexchange.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">优惠兑换现金券</p>
                                <p class="f-fs12 f-color9">人气值兑换现金券9.2折</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/upgradePacks?title='+upgradePacksTitle" @click="memberBehavior('H4')">
                            <img :src="getImg('/img/post/popularity/ico_updategift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">升级礼包</p>
                                <p class="f-fs12 f-color9">50元现金券、0.5%收益券</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/birthdayGift?title='+birthdayGiftTitle" @click="memberBehavior('H5')">
                            <img :src="getImg('/img/post/popularity/ico_birthdaygift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">生日礼包</p>
                                <p class="f-fs12 f-color9">当天签到人气值翻10倍，送50元现金券、1.5%收益券</p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
            <div v-if="current==5" class="u-memberGrade05 u-gradeGift ">
                <ul>
                    <li>
                        <a v-link="'/membershipPrivileges?title='+membershipPrivilegesTitle"
                           @click="memberBehavior('H3')">
                            <img :src="getImg('/img/post/popularity/ico_perexchange.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">优惠兑换现金券</p>
                                <p class="f-fs12 f-color9">人气值兑换现金券9.0折</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/upgradePacks?title='+upgradePacksTitle" @click="memberBehavior('H4')">
                            <img :src="getImg('/img/post/popularity/ico_updategift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">升级礼包</p>
                                <p class="f-fs12 f-color9">50元现金券、1.5%收益券</p>
                            </div>
                        </a>
                    </li>
                    <li>
                        <a v-link="'/birthdayGift?title='+birthdayGiftTitle" @click="memberBehavior('H5')">
                            <img :src="getImg('/img/post/popularity/ico_birthdaygift.png')" alt="">
                            <div>
                                <p class="f-fs16 f-color3">生日礼包</p>
                                <p class="f-fs12 f-color9">当天签到人气值翻10倍，送50元现金券、1.5%收益券</p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>
<style>
    .swiper-slide {
        width: 180px;
    }

    body {
        background: #F3F3F3;
    }
</style>

<script>

    const RES = env.resPath
    export default {
        data: function () {
            return {
                siteurl: env.siteurl,
                res: env.resPath,
                alipath: env.alipath,
                current: initData.result.vipLevel,
                initData: initData.result,
                level: initData.result.vipLevel,
                needSncreaseScore: initData.result.needSncreaseScore,
                popularity: initData.result.score,
                membershipPrivilegesTitle: window.encodeURI('优惠兑换现金券'),
                upgradePacksTitle: window.encodeURI('升级礼包'),
                birthdayGiftTitle: window.encodeURI('生日礼包'),
                growthRecodeTitle: window.encodeURI('会员成长体系'),
                method: '',
                isGetData: false
            }
        },
        created: function () {
            //安卓传递title
            if (env.Os == 1) {
                Android.UpdateTitle('会员中心')
            }

            this.$on('methodCallback', function (data) {
                if (this.method == 14 && !this.isGetData) {

                } else if (this.isGetData) {
                    this.$root.initMessage = data
                    this.$router.go({path: '/growthSystem?title=' + window.encodeURI('成长记录')})
                }

            })
        },
        ready: function () {
            var self = this
            var mySwiper = new Swiper('.swiper-container02', {
                direction: 'horizontal',
                freeMode: true,
                slidesPerView: 'auto',
                initialSlide: self.level,
                centeredSlides: true,
                slidesOffsetBefore: 40

            })
        },
        computed: {},
        watched: {},
        methods: {
            getImg: function (url) {
                return RES + url
            },
            changeCurrent: function (num) {
                this.current = num
            },
            requestData: function (invokeMethod, event) {
                var self = this
                self.isGetData = true
                hook.getEvent(invokeMethod + '&isNeedRealName=0&args_pageNo_1_integer_1', $(event.currentTarget), 1)
            },
            memberBehavior: function (anchor) {
                var self = this
                self.method = 14
                hook.getEvent(14 + '&isNeedRealName=0&args_anchor_1_string_' + anchor, $(event.currentTarget), 1)
            }
        }
    }
</script>