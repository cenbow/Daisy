/*银行卡管理*/
define(['zepto', 'base', 'template','area','touch'], function (require, exports, module) {

    var phoneReg = /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/,
        cardReg = /^(\d{16}|\d{19})$/,
        base = require('base'),
        template = require('template'),
        area = require('area'),
        timer ,
        touch = require('touch');

    //#银行卡管理#

    //A.银行卡列表
    if (base.isPage('bankManage')) {

        base.getAPI({
            url: env.path + '/security/bankCard/queryBankCardList',
            callback: showCardlist
        });

        function showCardlist(data) {
            var cardData = data.result, $cardlist = $('#j-cardlist');
            if (data.success && cardData.data.length) {
                var html = template('j-cardlist-tpl', cardData);
                $cardlist.html(html);

                //滑动删除卡
                touch.on('#j-cardlist li', 'swipeleft swiperight', function (ev) {
                    ev.stopPropagation();
                    var type = ev.type;
                    if (type === 'swipeleft') {

                        var parents = $(this).parents('li'),
                            cardid = parents.data('cardid');

                        if (parents.find('.j-card-del').length < 1) {
                            parents.animate({'margin-left': '-20%'})
                                .append('<em class="j-card-del u-card-del"' + ' data-cardid="' + cardid + '">删除</em>');
                        }
                    } else {
                        $(this).parents('li').animate({'margin-left': 0})
                            .find('.j-card-del').remove();
                    }
                });
                $cardlist.on('click', '.j-card-del', function () {
                    var cardid = $(this).data('cardid');
                    delBankcard(cardid);
                });
            }
        }

        function delBankcard(cardid) {
            base.getAPI({
                url: env.path + '/security/bankCard/deleteBankCardByID',
                data: [{name: 'bankCardID', 'value': cardid}],
                callback: function (data) {
                    if (data.success) {
                        base.xTips({
                            content: '删除成功！', callback: function () {
                                setTimeout(function () {
                                    location.reload();
                                }, 1000);
                            }
                        });
                    } else {
                        var errTips = data.resultCodes[0].msg;
                        base.xTips({content: errTips});
                    }
                }
            })
        }
    }

    //B.添加银行卡
    if (base.isPage('bankAdd')) {

        base.getAPI({
            url: env.path + '/security/bankCard/queryBanks',
            ids: {tpl: 'j-cardSelectlist-tpl', target: '#j-cardSelectlist'},
            callback: showUsabledCardlist
        });

        function showUsabledCardlist(data, ids) {
            var cardData = data.result;
            if (data.success && cardData.data.length) {
                var html = template(ids.tpl, cardData);
                $(ids.target).html(html);
            }
        }

        //输入校验
        base.validate('body', 'change');

        //验证码发送与校验
        var $bankaddNext = $('#j-bankCard-next');
        $bankaddNext.on('click', function () {
            if(timer){
                clearInterval(timer)
            }
            var notVerifiedList = [];
            $('.j-validate').each(function () {
                var isVerified = $(this).data('verified'),
                    name = $(this).attr('name');
                if (!isVerified) {
                    notVerifiedList.push({tips: $(this).data('errtips'), name: name});
                }
            });
            var tipsName = notVerifiedList.length ? notVerifiedList[0].name : '';
            if (tipsName !== 'smsCode') {
                tipsName ? base.xTips({content: notVerifiedList[0].tips}) : null;
            } else {
                var $rechargeFrom = $('#j-form-card');
                //$('#j-dialog-next').addClass('show');
                base.cache.bankcardData = $rechargeFrom.serializeArray();
                sendMessage(base.cache.bankcardData);
            }

        });
        //send sms
        function sendMessage(bankcardData, resent) {
            //阻止异常提交
            if (!bankcardData) {
                return false;

            }
            //发送验证码
            base.getAPI({
                url: env.path + '/security/bankCard/bingQuickBankCard',
                data: bankcardData,
                callback: function (data) {
                    if (data.success) {
                        var item = data.result;
                        base.cache.smsData = bankcardData;
                        base.cache.smsData.push({name: 'ticket', value: item.ticket});
                        showCodeDialog(resent);

                    } else {
                        var errTip = data.resultCodes[0].msg;
                        base.xTips({content: errTip});
                        //target.removeAttr('disabled').removeClass('z-disabled');
                    }
                }
            })
        }

        //显示短信对话框
        var showCodeDialog = function (resent) {
            var $dialog = $('#j-dialog-next'),
                $countBox = $dialog.find('.j-count-box'),
                $resentBox = $dialog.find('.j-resent-box'),
                $mobile = $dialog.find('.j-mobile'),
                $num = $dialog.find('.j-count');

            if (!resent) {
                //显示窗口
                $dialog.addClass('show');
                $mobile.text($('#j-mobile').val().substr(7, 4));
            } else {
                $resentBox.hide();
                $countBox.show();
                $num.text(60);
            }

            //倒计时
            $num.text(60 - 1);
             timer = setInterval(function () {
                var count = Number($num.text());
                if (count > 0) {
                    $num.text(count - 1);
                } else {
                    $countBox.hide();
                    $resentBox.show();
                    clearInterval(timer);
                }
                base.cache.smsCount = count - 1;
            }, 1000);
        };

        $('.j-resent-box').on('click', function () {
            var smsCount = base.cache.smsCount;

            if (smsCount && smsCount < 1) {
                sendMessage(base.cache.bankcardData, true);
            }
        });
        //点击取消窗口消失
        $('#j-cancel').on('click', function () {
            $('#j-dialog-next').removeClass('show');
            //location.reload()
            $('#j-smscode').data('verified',false).val('')
        });
        //按钮添加
        $('#j-btn-submit').on('click', function () {
            var smsData = base.cache.smsData,
                $smsCode = $('#j-smscode'),
                smsCode = $smsCode.val(),
                smsValidated = $smsCode.data('verified');

            if (smsData) {
                if (smsValidated) {
                    smsData.push({name: 'validCode', value: smsCode});
                    getBankcardAdd(smsData);
                } else {
                    base.xTips({content: '请输入正确的验证码'});
                }
            } else {
                base.xTips({content: '请勿非法提交'});
            }
        });
        //添加请求
        function getBankcardAdd(smsData) {
            base.getAPI({
                url: env.path + '/security/bankCard/checkCodeAndSaveBankCard',
                data: smsData,
                callback: function (data) {
                    if (data.success) {
                        base.xTips({
                            content: '银行卡添加成功！', delay: 1000, callback: function () {
                                location.href = env.path + '/mCenter/home';
                            }
                        })
                    } else {
                        var errTip = data.resultCodes[0].msg,
                            errCode = data.resultCodes[0].code;
                        switch (errCode) {
                            case "90044":
                                errTip = '输入错误，请重新输入';
                                break;
                            default :
                                break;
                        }
                        base.xTips({content: errTip});
                    }
                }
            });
        }
    }
    //新浪服务协议
    $('#j-qbank-agree').on('click', function () {
        var checked = $(this).data('checked');
        if (checked) {
            $bankaddNext.addClass('z-disabled').attr('disabled', 'disabled');
        } else {
            $bankaddNext.removeClass('z-disabled').removeAttr('disabled');
        }
        $(this).data('checked', checked ? false : true);
    });
    //阻止UC/QQ滑动翻页
    base.stopBrowserSlide();

    //什么是安全卡
    var securitycard = $('#j-security-card'),
        securityCardbox = $('#j-securityCard-box');
    securitycard.on('click', function () {
        securityCardbox.addClass('show');
    });
    securityCardbox.on('click', function () {
        securityCardbox.removeClass('show');
    })
});