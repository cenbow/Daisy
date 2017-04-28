
/* 这个页面渲染领取红包的结果 */
define(['zepto','base'],function (require, exports, module) {
    var base=require('base'),
        urls = {
            init: environment.globalPath + '/activity/popRedPackage/init',
            receiveRedbag: environment.globalPath + '/activity/popRedPackage/receive',
            getRedBagCode: environment.globalPath + '/security/activity/redBag/shareUrl'
        },
        $grabBtn = $('#j-grab-btn'),
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
            //console.log('init：', data)
            var resultCodes = data.resultCodes || [];
            $img = $('.u-img-responsive');
            $href = $('.u-img-href');

            $img.attr("src",'https://oss-cn-hangzhou.aliyuncs.com'+data.result.image);
            $href.attr("href",data.result.href);

            if((data.result && data.result.hasEmpty) || (resultCodes && resultCodes.length > 0) ){
                //console.log('红包被领完或者红包已经过期')
                $('#g-grab-input').addClass('z-hidden');
                $('#g-grab-over').removeClass('z-hidden');
                renderResult(data); // 渲染领取结果
                $('#j-show-result').removeClass('z-hidden'); // 显示领取结果
            } else {
                // 显示输入手机号的页面
                $('#j-redbag-grab').removeClass('z-hidden');
            }
        }
    });

    //a.提示输入正确手机号码
    $grabBtn.on('click', function(){
        var mobile = $('#j-phone-num').val();

        if(phoneReg.test(mobile)){
            base.getAPI({
                url: urls.receiveRedbag+'?mobile='+mobile+'&redBagCode='+redBagCode,
                callback: function(data){
                    console.log('输入手机号之后取到的数据 ', data)

                    if(data.result && data.success && !data.result.hasException && !data.result.hasReceive){
                        // $('#j-redbag-grab').addClass('z-hidden'); // 隐藏输入手机号的页面

                        renderResult(data); // 渲染领取结果

                    } else if (data.result.hasReceive){

                        renderResult(data); // 渲染领取结果

                        base.xTips({
                            time: 3000,
                            content: "你已抢过该红包"
                        })
                    } else {
                        alert('领取红包失败');
                    }
                }
            });
        } else {
            alert('请输入正确的手机号');
        }
    });

    // 取完数据之后渲染列表和圆环里的文字
    function renderResult(data){

        var result = data.result || {},
            resultCodes = data.resultCodes || [], // 用来判断是否过期
            rewardValue = result.rewardValue || '', // 红包值
            receive = result.receive || '',        // 用户号码
            resultList = result.receiveList || [], // 本组红包的领取记录
            hasEmpty = result.hasEmpty || '', // 是否已领完
            hasException = result.hasException || '', // 是否异常
            hasReceive = result.hasReceive || '', // 是否已领取
            myPackage = result.myPackage || '',
            receiveFlag = result.receiveFlag || '', // 是否是未注册的人第一次领取
            image = result.image,
            href = result.href,

            $circle = $('#j-circle'), // 圆环
            $registerBtn = $('#j-register-btn'), // 注册按钮
            $bestluck = $('#j-bestluck'); // 手气最佳 图标


        //console.log('是否已经领取：', hasReceive);

        $('#j-show-result').removeClass('z-hidden'); // 显示领取结果

        if(hasException) {
            alert('领取红包失败');
        } else {
            // 过期
            if( resultCodes && resultCodes.length > 0){
                if(resultCodes[0].code == '91104' ){
                    $('.m-quan-bg-over').text('已过期');
                    $('#j-timeout,#j-get-num').removeClass('z-hidden');
                    if(hasEmpty){
                        $bestluck.removeClass('z-hidden'); // 显示手气最佳的图标
                    }
                    // $registerBtn.removeClass('z-hidden'); // 显示底部的注册按钮

                    // 如果列表长度为0还需要把过期的圆圈放到正中间
                    $circle.addClass('z-text-middle');
                    //if(resultList.length == 0){
                        $circle.addClass('z-no-result-list');
                    //}
                }

            } else {
                $circle.removeClass('z-only-num');

                //console.log(2222)
                // 是我发的红包
                if(myPackage){
                    //console.log(3333)
                    var residualAmount = result.residualAmount || 0, // 剩余人气值点数
                        residualNumber = result.residualNumber || 0; // 剩余红包个数

                    $('#j-myPackage').text('还剩' + residualNumber + '个红包，' + residualAmount + '点人气值').removeClass('z-hidden');
                }

                // 如果已经领完了，因为可能领的时候那个人正好是最后一个
                if( !rewardValue && hasEmpty){
                    //console.log(4444)
                    $circle.addClass('z-text-middle');
                    $('#j-notNew2, #j-get-num').removeClass('z-hidden'); // 圆圈文案变为已抢完
                    $bestluck.removeClass('z-hidden'); // 显示手气最佳的图标
                    // $registerBtn.removeClass('z-hidden'); // 显示底部的注册按钮
                } else {
                    // 判断是否已抢过，这里主要用于圆圈显示
                    if(!hasException){
                        $('#g-grab-input').addClass('z-hidden');
                        $('#g-grab-quan').removeClass('z-hidden');

                        $('#j-user-mobile').text('已存入账户');
                        $('#j-user-mobile-num').text(data.result.receive);
                        $('#j-pop-num').text(data.result.rewardValue);
                        //console.log(5555)
                        // 判断是否是新手
                        if(receiveFlag != 1){
                            //console.log(6666)
                            //console.log('不是新手')
                            $('#j-grab-redbag').removeClass('z-hidden');
                            // $('#j-new-rewardValue').text(rewardValue).removeClass('z-hidden'); // 新手的话，30元大字，人气值小字
                            // $('#j-new, #j-get-num').removeClass('z-hidden');
                            // $('#j-already-get').text('新用户再得518').addClass('u-already-get').removeClass('z-hidden');
                            $registerBtn.removeClass('z-hidden'); // 显示底部的注册按钮

                        } else {
                            //console.log(7777)
                            // $('#j-rewardValue').text(rewardValue).removeClass('z-hidden'); // 圈圈里的大数字
                            // $('#j-notNew1, #j-get-num').removeClass('z-hidden'); // 圆环展示
                            // $('#j-get-text').removeClass('z-hidden'); // 提示已经存入账户
                        }

                        // 如果已经抢过了，就显示这个小的已抢过文字
                        // if(rewardValue && hasReceive){
                        //     //console.log(8888)
                        //     $('#j-already-get').text('已抢过').addClass('u-already-get').removeClass('z-hidden');
                        // } else {
                        //     $circle.addClass('z-only-num'); // 只有数字的话字需要居中
                        // }
                    }else {
                        //console.log(9999)
                        alert('领取红包失败');
                    }

                }
            }

            // 渲染结果列表 receiveFlag 和 myPackage，如果为0就不显示整个列表了
            // 如果过期了也不渲染整个列表了
            if(resultList.length > 0 && !(resultCodes[0] && resultCodes[0].code == '91104')){
                var listStr = '',
                    len = resultList.length,
                    listMyPackage,
                    listReceiveFlag,
                    tempStr = '',
                    hasTop = false;

                for(var i = 0; i < len; i++){
                    listMyPackage = resultList[i].myPackage;
                    listReceiveFlag = resultList[i].receiveFlag;
                    listActivityRole = resultList[i].activityRole;

                    // 如果投资结束需要把手气最佳的那个放到第一位
                    if(hasEmpty && listActivityRole == 'redPackageTop'){
                        tempStr = '';

                        // receiveFlag = 0 就显示 新用户再得30元
                        if(listReceiveFlag == 0){
                            tempStr = '<li><span class="u-list-phone">' + resultList[i].mobileStr + '</span>' + '<span class="u-list-num z-with30">' + ' <span>(新用户再得518)</span>' + resultList[i].rewardValue + '点</span><i>' + resultList[i].receiveTimeStr + '</i><div class="u-list-best">手气最佳</div></li>';
                        } else {
                            tempStr = '<li><span class="u-list-phone">'+ resultList[i].mobileStr +'</span>' + '<span class="u-list-num">'+ resultList[i].rewardValue +' 点</span><i>'+resultList[i].receiveTimeStr+'</i><div class="u-list-best">手气最佳</div></li>';
                        }

                        hasTop = true; // 存在手气最佳的人
                    } else {
                        // receiveFlag = 0 就显示 新用户再得30元
                        if(listReceiveFlag == 0){
                            listStr += '<li><span class="u-list-phone">' + resultList[i].mobileStr + '</span>' + '<span class="u-list-num z-with30">' + ' <span>(新用户再得518)</span>' + resultList[i].rewardValue + '点</span><i>' + resultList[i].receiveTimeStr + '</i></li>';
                        } else {
                            listStr += '<li><span class="u-list-phone">'+ resultList[i].mobileStr +'</span>' + '<span class="u-list-num">'+ resultList[i].rewardValue +' 点</span><i>'+resultList[i].receiveTimeStr+'</i></li>';
                        }
                    }
                }

                // 把手气最佳的添加到字符串头部
                if(hasTop){
                    listStr = tempStr + listStr;
                }

                $('#j-list').html(listStr);
                $('#j-result').removeClass('z-hidden');
            }

        }
    }

});
