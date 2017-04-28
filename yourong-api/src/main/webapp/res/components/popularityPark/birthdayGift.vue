<template>
<div class="g-birthdayGiftWrap">
    <img :src="getImg('/img/post/popularity/head_birthdaygift.png')" alt="生日礼包" width="100%">
    <div class="m-mainContent">
        <div>
            <div class="u-contentTitle f-color3 f-fs16">

                <img :src="getImg('/img/post/popularity/ico_explain.png')" alt="" >

                <span>特权说明</span>
            </div>
            <ul class="f-color9 f-fs14 u-memberList">
                <li>
                    <p class="f-color3">V0、V1会员</p>
                    <p>生日当天签到人气值翻10倍，送50元现金券、0.5%收益券</p>
                </li>
                <li>
                    <p class="f-color3">V2、V3会员</p>
                    <p>生日当天签到人气值翻10倍，送50元现金券、1%收益券</p>
                </li>
                <li>
                    <p class="f-color3">V4、V5会员</p>
                    <p>生日当天签到人气值翻10倍，送50元现金券、1.5%收益券</p>
                </li>
            </ul>
        </div>
        <div style="margin-top: 20px;">
            <div class="u-contentTitle f-color3 f-fs16">

                <img :src="getImg('/img/post/popularity/ico_rule.png')" alt="">

                <span>规则说明</span>
            </div>
            <ul class="u-rulesList f-color9 f-fs14">
                <li>该礼包发放给有融网所有用户，等级越高，奖励越丰富；</li>
                <li>活动仅限实名认证（即开通新浪存钱罐）的用户参与，生日以身份证上的出生日期为准；新注册用户在生日当天完成实名认证后可签到领取生日礼包；若生日当天先签到后实名认证，将不补发奖励</li>
                <li>生日当天签到人气值翻十倍，如遇其他签到人气值翻倍活动，以最高倍数为准，奖励不累加；</li>
                <li>签到后现金券、收益券将自动发放，30天内有效、可叠加使用；</li>
                <li>生日礼包自2016年11月起升级，升级前的礼包按原规则发放；</li>
                <li>
                    若有其它疑问请拨打客服热线：<br>400-025-6688。
                </li>
            </ul>
        </div>
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
                show:false
            }
        },
        created:function () {
            //传递安卓title
            if(env.Os==1){
                Android.UpdateTitle('生日礼包')
            }

        },
        methods: {
            getImg:function (url) {
                return RES+url
            }
        }
    }
</script>