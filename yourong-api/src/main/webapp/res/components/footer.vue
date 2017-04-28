<template>
    <div class="g-ft">
        <div class="u-safety-logo">
            <img :src="imgurl.sinapay" width="35%">
            <img :src="imgurl.guanghua" width="40%">
        </div>
        <div class="u-download-btn">
            <a href="{{siteurl}}/static/page/twoCodeRedict.html" target="_blank">下载APP</a>
            <a href="http://www.yrw.com/?fromMobile">电脑版</a>
        </div>
        <div class="u-guide-link">
            <a href="{{siteurl}}">首页</a>|<a href="{{tipsurl}}">关于有融</a>|<a href="{{tipsurl}}">安全保障</a>|<a href="{{tipsurl}}">帮助中心</a>
        </div>
        <span class="m-copyright">©2014-2016 YRW.COM 版权所有</span>
    </div>
</template>
<script>
    export default {
        data(){
            return {
                siteurl:this.$root.siteurl,
                tipsurl:this.$root.siteurl+'/mstation/tips',
                imgurl:{
                    sinapay:this.$root.siteurl+'/res/img/sinaPay_logo20151118.png',
                    guanghua:this.$root.siteurl+'/res/img/logo_ghkj20160411.png'
                }
            }
        }
    }
</script>