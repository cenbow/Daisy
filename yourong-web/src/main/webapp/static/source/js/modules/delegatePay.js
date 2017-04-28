/**
 * Created by lyl on 2016/7/25.
 */
(function () {
    Vue.config.devtools = true

    window.vm=new Vue({
        el:'#j-delegatePay',
        data:{
            urls:{

            },
            initData:{},
            memberId:$('#j-memberId').attr('memberId'),
            tipsText:{
                tipContent:['请在打开的新浪页面完成委托授权开通','请在打开的新浪页面完成委托授权关闭'],
                btnLeft:['已开通委托授权','已关闭委托授权'],
                btnRight:['开通遇到问题，重新开通','关闭遇到问题，重新关闭']
            },
            tipContentText:'',
            tipBtnLeftText:'',
            tipBtnRightText:''
        },
        created:function () {
        },
        methods:{
            openDelegatePay:function (e,type) {
                var $link = $(e.currentTarget),_this=this
                $link.attr('href',$link.attr('href')+'&type='+type)
                _this.changeTipText(type)
                _this.openBlock('#j-goToSinaBlock')
            },
          
            changeTipText:function (type) {
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