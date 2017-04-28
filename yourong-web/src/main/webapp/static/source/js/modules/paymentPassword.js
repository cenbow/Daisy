(function () {
   
    Vue.config.devtools = true;
    
    window.vm=new Vue({
        el:'#j-paymentPassword',
        data:{
            tipsText:{
                tipContent:['请在打开的新浪页面完成支付密码设置','请在打开的新浪页面完成支付密码修改','请在打开的新浪页面完成支付密码找回'],
                btnLeft:['已完成支付密码设置','已完成支付密码修改','已完成支付密码找回'],
                btnRight:['设置遇到问题，重新设置','修改遇到问题，重新修改','找回遇到问题，重新找回'],
            },
            tipContentText:'',
            tipBtnLeftText:'',
            tipBtnRightText:''
        },
        created:function () {
        },
        methods:{
            gotoSetPayPwd: function (e,type) {
                var $link = $(e.currentTarget),_this=this
                $link.attr('href',$link.attr('href')+'&type='+type)
                _this.changeTipText($link,type)
                _this.openBlock('#j-goToSinaBlock')
            },
            changeTipText:function ($link,type) {
                var _this=this
                // 弹窗的文案变化修改
                _this.tipContentText=_this.tipsText.tipContent[type-1]
                _this.tipBtnLeftText=_this.tipsText.btnLeft[type-1]
                _this.tipBtnRightText=_this.tipsText.btnRight[type-1]
            },
            openBlock:function (id) {
                $(id).show();
                $('.j-cover').show()
            },
            closeBlock:function (id) {
                $(id).hide();
                $('.j-cover').hide()
                location.reload()
            }
        }
    })
})()