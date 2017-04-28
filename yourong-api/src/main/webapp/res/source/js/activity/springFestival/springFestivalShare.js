
define(['base', 'wxShare'],function (require, exports, module) {
    var base=require('base');
    var wxShare = require('wxShare');

    var appid = $('#j-appid').attr('data-appid');

    // 分享到微信
    //var toShareUrl = 'http://cchotaru.6655.la:14621/yourong-api/activity/redBag/grab?redBagCode=ZoX8laQZqtxnDjec5DTGFhNnfmQOOR9AFcxSyEmGqyg%3D'
    var toShareUrl = window.location.href.replace('share', 'grab');
    wxShare.init({
        debug:false,
        appId:appid, // 正式 appId
        //appId: 'wxded01b64a081b72b', // 测试appId
        //reqUrl:'http://jnnhjn.vicp.cc/yourong-api/wechat/js/share?callback=?',
        reqUrl: environment.globalPath + '/wechat/js/share?callback=?',
        shareUrl:location.href,
        title:'有融网发红包啦，喊好友一起来拼手气吧！',
        desc: '手气红包，一起来抢吧！',
        link: toShareUrl,
        imgUrl: 'http://yrimg.oss-cn-hangzhou.aliyuncs.com/img/weixin_share.png',
        hideMenuItemsList: [
            'menuItem:share:qq',
            'menuItem:share:weiboApp',
            'menuItem:favorite',
            'menuItem:share:facebook',
            'menuItem:share:QZone',
            'menuItem:editTag',
            'menuItem:delete',
            'menuItem:copyUrl',
            'menuItem:openWithQQBrowser',
            'menuItem:openWithSafari',
            'menuItem:originPage',
            'menuItem:share:email'
        ],
        success:function(res){
            console.log('分享成功',res);
        },
        cancel:function(res){
            console.log('失败',res);
        }
    });
});
