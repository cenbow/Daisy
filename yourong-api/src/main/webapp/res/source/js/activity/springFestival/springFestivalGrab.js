
/* 这个页面渲染领取红包的结果 */
define(['base', 'wxShare'],function (require, exports, module) {
    var base=require('base');
    var urls = {
        init: environment.globalPath + '/activity/redBag/init',
        receiveRedbag: environment.globalPath + '/activity/redBag/receive'
    };
    var $shareBtn = $('#j-share-btn'),
        phoneReg = /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/;


    // 获取URL参数
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg); // 匹配目标参数
        if (r != null)
            return unescape(r[2]);
        return null; // 返回参数值
    }

    // init 去后端查询红包是否被领完
    var redBagCode = getUrlParam('redBagCode');
    base.getAPI({
        url: urls.init + '?redBagCode=' + redBagCode,
        callback: function(data){
            // 已经抢完啦
            if(data.result.hasEmpty){
                $('#j-main').hide(); // 隐藏输入手机号的页面
                var resultList = data.result.receiveList;
                $('#j-grapOne').hide();
                $('#j-grapTwo').show();
                var listStr = '';
                for(var i = 0; i < resultList.length; i++){
                    listStr += '<li><span class="u-list-phone">'+ resultList[i].mobileStr +'</span>' + '<span class="u-list-num">'+ resultList[i].rewardValue +' 点</span></li>';
                }

                $('#j-list').html('').append(listStr);
                $('#j-result').show(); // 显示领取结果
            }
        }
    });

    //a.提示输入正确手机号码
    var $loading = $('#j-loading');
    $shareBtn.on('click', function(){
        $loading.show();
        var mobile = $('#j-phoneNum').val();
        if(phoneReg.test(mobile)){
            base.getAPI({
                url: urls.receiveRedbag+'?mobile='+mobile+'&redBagCode='+redBagCode,
                callback: function(data){
                    if(data.result && data.success && !data.result.hasException){
                        $('#j-main').hide(); // 隐藏输入手机号的页面
                        renderResult(data.result); // 渲染领取结果
                        $('#j-result').show(); // 显示领取结果
                    } else {
                        alert('领取红包失败');
                    }
                    $loading.hide();
                }
            });
        } else {
            $loading.hide();
            alert('请输入正确的手机号');
        }
    });

    // 取完数据之后渲染列表和圆环里的文字
    function renderResult(result){
        var rewardValue = result.rewardValue, // 红包值
            resultList = result.receiveList, // 本组红包的领取记录
            hasEmpty = result.hasEmpty, // 是否已领完
            hasException = result.hasException, // 是否异常
            hasReceive = result.hasReceive; // 是否已领取

        if(hasException) {
            alert('领取红包失败');
        } else {
            // 如果已经领完了
            if(hasEmpty && !hasReceive){
                $('#j-grapOne').hide();
                $('#j-grapTwo').show();
            }
            var listStr = '';
            for(var i = 0; i < resultList.length; i++){
                listStr += '<li><span class="u-list-phone">'+ resultList[i].mobileStr +'</span>' + '<span class="u-list-num">'+ resultList[i].rewardValue +' 点</span></li>';
            }

            $('#j-list').html('').append(listStr);
            $('.j-rewardValue').text(rewardValue);

            // 已经抢过
            if(hasReceive){ $('#j-circle-content').text('已抢过') }
        }
    }

    // 输手机号抢红包的页面也要限制只能分享到微信朋友和朋友圈
    var wxShare = require('wxShare');
    // 分享到微信
    //var toShareUrl = 'http://cchotaru.6655.la:14621/yourong-api/activity/redBag/grab?redBagCode=ZoX8laQZqtxnDjec5DTGFhNnfmQOOR9AFcxSyEmGqyg%3D'
    var toShareUrl = window.location.href.replace('share', 'grab');
    var appid = $('#j-appid').attr('data-appid');

    wxShare.init({
        debug:false,
        appId:appid,
        //reqUrl:'http://jnnhjn.vicp.cc/yourong-api/wechat/js/share?callback=?',
        //reqUrl:'http://m.yrw.com/wechat/js/share?callback=?',
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
