<template>
<div class="g-birthdayGiftWrap">
    <img :src="getImg('/img/post/popularity/head_update.png')" alt="升级礼包" width="100%">
    <div class="m-mainContent">
        <div>
            <div class="u-contentTitle f-color3 f-fs16">

                <img :src="getImg('/img/post/popularity/ico_explain.png')" alt="" >

                <span>特权说明</span>
            </div>
            <ul class="f-color9 f-fs14 u-memberList">
                <li>
                    <p class="f-color3">成功升至V1等级</p>
                    <p>可获得50元现金券一张</p>
                </li>
                <li>
                    <p class="f-color3">成功升至V2等级</p>
                    <p>可获得50元现金券一张</p>
                </li>
                <li>
                    <p class="f-color3">成功升至V3等级</p>
                    <p>可获得50元现金券一张</p>
                </li>
                <li>
                    <p class="f-color3">成功升至V4等级</p>
                    <p>可获得50元现金券、0.5%收益券</p>
                </li>
                <li>
                    <p class="f-color3">成功升至V5等级</p>
                    <p>可获得100元现金券、1.5%收益券</p>
                </li>
            </ul>
        </div>
        <div style="margin: 20px 0 30px 0;">
            <div class="u-contentTitle f-color3 f-fs16">

                <img :src="getImg('/img/post/popularity/ico_rule.png')" alt="">

                <span>规则说明</span>
            </div>
            <ul class=" f-color9 f-fs14">
                <li>用户每次升级会自动发放一份升级礼包，等级越高，礼包越丰厚。</li>
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
                url: 'popularityPark?',
                siteurl: env.siteurl,
                res: env.resPath,

            }
        },
        created:function () {
            //安卓传递title
            if(env.Os==1){
                Android.UpdateTitle('升级礼包')
            }
        },
        methods: {
            getImg:function (url) {
                return RES+url
            }
        }
    }
</script>