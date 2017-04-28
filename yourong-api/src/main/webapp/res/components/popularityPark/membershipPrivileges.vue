<template>
<div class="g-birthdayGiftWrap">
    <img :src="getImg('/img/post/popularity/Group 9.png')" alt="优惠兑换现金券" width="100%">
    <div class="m-mainContent">
        <div>
            <div class="u-contentTitle f-color3 f-fs16">

                <img :src="getImg('/img/post/popularity/ico_explain.png')" alt="" >

                <span>特权说明</span>
            </div>
            <ul class="f-color9 f-fs16 u-memberList">
                <li>
                    <p class="f-color3">等级折扣</p>
                    <p><img :src="getImg('/img/post/popularity/vipdiscount.png')" alt=""></p>
                </li>
            </ul>
        </div>
        <div style="margin: 20px 0 30px 0;">
            <div class="u-contentTitle f-color3 f-fs16">

                <img :src="getImg('/img/post/popularity/ico_rule.png')" alt="">

                <span>规则说明</span>
            </div>
            <ul class=" f-color9 f-fs14">
                <li>不同等级的用户享受不同折扣特权，若折扣后人气值出现小数点，则四舍五入取整。等级越高，享受的折扣力度越大。</li>
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
            }
        },
        created:function () {
            //安卓传递title
            if(env.Os==1){
                Android.UpdateTitle('优惠兑换现金券')
            }
        },
        methods: {
            getImg:function (url) {
                return RES+url
            }
        }
    }
</script>