<template>
 <div class="g-ft">
 <h1>Homepage2333</h1>
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
<style scoped>
    h1 {
        background: #ccc222;
    }
    .g-ft{
        color:#999;
        height: 306px;
        background-color: #fff;
    }
    .g-ft img{
        margin-top: 16px;
    }
    .u-safety-logo{
        text-align: center;
        margin: 16px 0 0;
    }
    .u-safety-logo img:first-child{
        margin: 0 10% 0 0;
    }
    .u-download-btn{
        padding: 0 4%;
        margin-top: 20px;
    }
    .u-download-btn a{
        width: 46%;
        height: 40px;
        line-height: 40px;
        text-align: center;
        color: #999;
        text-decoration: none;
        display: inline-block;
        -moz-border-radius: 5px;
        -webkit-border-radius: 5px;
        border-radius: 5px;
        border: 1px solid #999;
    }
    .u-download-btn a:link,
    .u-download-btn a:visited,
    .u-download-btn a:hover{
        color: #999;
        text-decoration: none;
    }
    .u-download-btn a:first-child {
        margin-right: 4%;
    }
    .u-guide-link{
        color: #999;
        text-align: center;
        margin: 17px 0;
    }
    .u-guide-link a{
        color: #999;
        text-decoration: none;
        margin: 0 3%;
    }
    .u-guide-link a:link,
    .u-guide-link a:visited,
    .u-guide-link a:hover{
        color: #999;
        text-decoration: none;
    }
    .m-copyright{
        text-align: center;
        display: block;
    }
    /**APP下载提示**/
    .u-header{
        z-index: 2;
        text-align: center;
        color: #fff;
        height: 50px;
        line-height: 50px;
        padding: 0 1rem;
        background-color: #d74148;
        background-size: auto 50%;
    }
    .u-header a{
        position: absolute;
        left: 1rem;
        color: #fff;
    }
    .u-header span{
        font-weight: bold;
    }
    .m-Tips-content{
        padding: 0 16%;
        color: #999;
        text-align: center;
    }
    .m-Tips-content p {
        margin: 24px 0 14px;
    }
    .m-Tips-content a{
        margin-top: 15px;
        text-align: center;
        line-height: 28px;
        width: 99%;
        height: 28px;
        display: block;
        color: #999;
        -moz-border-radius: 5px;
        -webkit-border-radius: 5px;
        border-radius: 5px;
        border: 1px solid #999;
    }
    .u-download-android{
        margin-bottom: 40px;
    }
</style>