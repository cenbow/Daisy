<style scoped>
    /*没有实名认证提示框*/
    .u-dialog {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 100000;
        background: rgba(0, 0, 0, .4);
    }

    .ui-dialog-cnt {
        border-radius: 6px;
        width: 270px;
        height: 150px;
        -webkit-background-clip: padding-box;
        background-clip: padding-box;
        pointer-events: auto;
        background-color: rgba(253, 253, 253, .95);
        position: absolute;
        top: 50%;
        left: 50%;
        margin-left: -135px;
        margin-top: -75px;
        font-size: 16px
    }

    .ui-dialog-bd {
        min-height: 71px;
        border-top-left-radius: 6px;
        border-top-right-radius: 6px;
        padding: 18px;
        display: -webkit-box;
        display: box;
        -webkit-box-pack: center;
        -webkit-box-align: center;
        -webkit-box-orient: vertical
    }

    .ui-dialog-ft {
        border-bottom-left-radius: 6px;
        border-bottom-right-radius: 6px;
        display: -webkit-box;
        width: 100%;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
        -webkit-box-align: center;
        border-top: 1px solid #e0e0e0;
        height: 42px;
        line-height: 42px
    }

    .ui-dialog-ft button {
        color: #00a5e0;
        text-align: center;
        border-right: 1px #e0e0e0 solid;
        width: 100%;
        line-height: 42px;
        background: transparent;
        display: block;
        margin: 0 !important;
        -webkit-box-flex: 1
    }

    .ui-dialog-ft button:active {
        background-color: rgba(0, 0, 0, .1) !important
    }

    .ui-dialog-ft button:first-child {
        border-bottom-left-radius: 6px
    }

    .ui-dialog-ft button:last-child {
        border-right: 0;
        border-bottom-right-radius: 6px
    }

</style>
<template>
    <!--##home-->
    <div class="m-home-content">
        <div class="u-home-info">
        <span><i id="j-greetingTime">你好，</i>
            <em>{{memberData.userName}}</em>
        <a href="{{siteurl}}/mCenter/recharge" class="j-show-dialog">充值</a><a href="{{siteurl}}/mCenter/withdraw"
                                                                              class="j-show-dialog">提现</a></span>

            <div class="u-home-balance">
                <p>存钱罐余额(元)<span>{{memberData.balance}}</span></p>
                <em>存钱罐可用余额(元)&nbsp;&nbsp;<span>{{memberData.availableBalance}}</span></em>
            </div>
        </div>
    </div>
    <div class="u-home-money">
        <p>
            <span>累计投资(元)<br/><em>{{memberData.investTotal}}</em></span>
            <span>累计投资收益(元)<br/><em>{{memberData.receivedInterest}}</em></span>
        </p>

        <p>
            <span>待收本金(元)<br/><em>{{memberData.receivablePrincipal}}</em></span>
            <span>待收投资收益(元)<br/><em>{{memberData.receivableInterest}}</em></span>
        </p>
    </div>

    <div class="u-home-signIn">
        人气值
        <em>
            <i>{{memberData.popularity}}</i>点
        </em>
        <a href="{{siteurl}}/mCenter/checkin">签到</a>
        <a class="u-home-nviteFriend" href="{{siteurl}}/mCenter/inviteFriend">邀请好友</a>
    </div>

    <div class="u-home-list">
        <ul>
            <li><a href="{{siteurl}}/mCenter/bankManage" class="j-show-dialog"><span>我的银行卡</span><i></i></a></li>
            <li><a href="{{siteurl}}/mCenter/myTransaction"><span>我的交易</span><i></i></a></li>
            <li><a v-link="{path:'/member/security'}"><span>安全认证</span><i></i></a></li>
            <li><a href="{{siteurl}}/mCenter/reputationPage"><span>我的优惠</span><i></i></a></li>
        </ul>
    </div>

    <div class="u-dialog f-dn" :class="{'f-dn':memberData.isDirectProject}" id="j-sinapay-dialog" >
        <div class="ui-dialog-cnt">
            <div class="ui-dialog-bd">
                <div>为了确保您的正常投资和资金安全，请开通新浪支付存钱罐。</div>
            </div>
            <div class="ui-dialog-ft">
                <button type="button" data-role="button" @click="cancelDislog('#j-sinapay-dialog')">取消</button>
                <button type="button" data-role="button" ><a href="{{siteurl}}/mCenter/sinapay">立即开通</a></button>
            </div>
        </div>
    </div>
</template>
<script>
    import Base from 'js/base'
    export default {
        data()
    {
        return {
            siteurl: this.$root.siteurl,
            memberData: {}
        }
    },
    created:function () {
        var _this = this
        Base.getAPI({
            url: Base.path + '/security/banlance/queryMemberBalance',
            version: '1.0.2',
            callback: function (data) {
                _this.$set('memberData', data.result)
            }
        })
    },
    methods:{
        cancelDislog:function(id){
            $(id).hide()
        }
    }
    }
</script>