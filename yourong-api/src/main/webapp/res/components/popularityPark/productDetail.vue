<style>
    body {
        background: #ffffff;
    }

    .swiper-pagination-bullet-active {
        background: #D74148;
    }
</style>
<template>
    <div>
        <!--##banner-->
        <!--##aliyun-->
        <div>
            <div v-if="productData.imageList.length>1" class="swiper-container u-swiperWrap"
                 style=" width: 90%;height: 200px;text-align: center;padding: 0 5%;overflow: hidden;">
                <div v-if="productData.imageList.length" class="swiper-wrapper"
                     style="width: 100%;height: 200px;line-height: 200px;">
                    <div v-for="item  in productData.imageList" class="swiper-slide">
                        <img :src="alipath+item.fileUrl" alt="" heigth="200px" width="200px">
                    </div>
                </div>
                <!-- 如果需要分页器 -->
                <div class="swiper-pagination"></div>
            </div>

            <div v-else class="u-swiperWrap"
                 style=" width: 90%;height: 200px;text-align: center;padding: 0 5%;overflow: hidden;">
                <img :src="alipath+productData.imageList[0].fileUrl" alt="" heigth="200px" width="200px">
            </div>
        </div>
        <!--##/banner-->
        <div style="background:#F3F3F3;width: 100%;height: 10px; "></div>
        <div class="u-productInfo">
            <div class="u-exchangeValue">
                <p class="f-color3 f-fs16">
                    {{productData.goodsName}}
                </p>
                <p class="f-fs16 f-color6">
                    <em>{{productData.discountPrice}}点</em>人气值 <span class="f-fs12 f-color9"
                                                                     v-if="productData.price>productData.discountPrice">{{productData.price}}点人气值</span>
                </p>
                <input v-if="productData.goodsType==2&&productData.rechargeType==1"
                       class="u-phoneNumber f-color9 f-fs14 " id="j-rechargeMobile" type="number" placeholder="请输入手机号码"
                       @focus="hideChangeBtn('#j-submitBtn')"
                       @blur="limitMobile('#j-rechargeMobile')" v-model="reChangeMobile">
            </div>
            <div>
                <p class="f-fs16 f-color6 u-infoTitle">详情说明</p>
                <p class="f-fs14 f-color9" v-for="item in finalGoodsDes">{{item}}</p>
                <p class="f-fs16 f-color6 u-infoTitle">兑换规则</p>
                <p class="f-fs14 f-color9">1、卡券类商品3个工作日内发放，实物类商品10个工作日内包邮寄出；<br>
                    2、除商品质量问题外，概不退货；<br>
                    3、最终解释权归有融网所有。
                </p>
            </div>
            <a href="javascript:void(0)" @click="submitBtn('#j-submitBtn',$event)">
                <div id="j-submitBtn" class="u-changeBtn " :class="{'z-disabled':productData.goodStatusFlag>0}">
                <!--z-disabled-->
                {{btnText[productData.goodStatusFlag]}}
            </div>
            </a>
        </div>


        <!--##    提示框-->
        <div class="u-checkBox animated zoomIn" v-show="exchangeSuccess">
            <p class="f-color3 f-fs16">
                兑换成功
            </p>
            <div class="f-fs16">
                <span @click="cancleBtn">取消</span> <a href="javascript:void(0)"
                                                      @click="requestData(4,$event,'recordList')"><span>查看兑换记录</span></a>
            </div>
        </div>

        <div class="u-checkBox animated zoomIn" v-show="surePayTip">
            <p class="f-color3 f-fs16">
                确认花费{{productData.discountPrice}}点人气值兑换?
            </p>
            <div class="f-fs16">
                <span @click="cancleBtn">取消</span> <a href="javascript:void(0)"
                                                      @click="submitOrder(3,$event,'productOrder')"><span>确定</span></a>
            </div>
        </div>
        <!--##    收货地址弹框-->
        <div class="u-addressBox animated zoomIn" v-show="inputAddress">
            <p class="f-fs16 f-color3">
                请填写收货信息
            </p>
            <input class="j-watchChange" type="text" name="userName" placeholder="收货人姓名" v-model="receiver">
            <input class="j-watchChange" type="number" name="userPhone" placeholder="手机号码" min="0" max="11"
                   pattern="[0-9]" v-model="mobile">
            <textarea class="j-watchChange" type="text" name="userAddress" placeholder="收货地址"
                      v-model="address"></textarea>
            <div class="f-fs16 u-sureBtn">
                <span @click="cancleBtn">取消</span> <span :class="{'z-filled':limitInput(address,receiver,mobile)}"
                                                         @click="toSubmitOrder">确定</span>
            </div>
        </div>
        <!--兑换错误提示-->
        <div class="u-wrongTips" v-if="isError">
            {{errorText}}
        </div>
        <!--手机号错误提示框-->
        <div class="u-wrongTips j-wrongTips " v-if="isWrong">
            {{phoneErrorText}}
        </div>
        <div class="u-cover" style="display: block" v-if="coverShow"></div>
    </div>
</template>
<script>
    const RES = env.resPath
    export default{
        data: function () {
            return {
                siteurl: env.siteurl,
                res: env.resPath,
                alipath: env.alipath,
                productData: this.$root.DetailtMessage.data.result,
                btnText: [
                    '立即兑换',
                    '人气值不足',
                    '投资后才可以兑换哦',
                    'V' + this.$root.DetailtMessage.data.result.levelNeed + '及以上才可以兑换哦',
                    '已兑完'
                ],
                coverShow: false,
                surePayTip: false,
                exchangeSuccess: false,
                inputAddress: false,
                method: '',
                disabled: false,
                isWrong: false,
                address: '',
                receiver: '',
                mobile: '',
                isMobile: true,
                reChangeMobile: '',
                isError: false,
                errorText: '网络拥挤，过会再来哦',
//                goodsDes: decodeURI(this.$root.DetailtMessage.data.result.goodsDes),
                finalGoodsDes: ''
            }
        },
        created: function () {
            var self = this
            console.log('goodsDes:', self.goodsDes)
            console.log(self.productData)
            console.log(this.$root.DetailtMessage.data.result.goodsDes)
            self.splitMsg(this.$root.DetailtMessage.data.result.goodsDes)
            window.scrollTo(0, 0)
            //传递安卓title
            if (env.Os == 1) {
                Android.UpdateTitle('商品详情')
            }
            this.$on('methodCallback', function (data, method) {
                this.$root.initData = data

                if (self.method == 3) {
                    if (data.data.success) {
                        self.exchangeSuccess = true
                        self.surePayTip = false
                        //计算剩余的人气值数目
                        initData.result.popularity = (initData.result.popularity - 0) - (self.productData.discountPrice - 0)
                    } else {
                        //兑换失败时的提醒语
                        self.isError = true

                        if (data.data.resultCodes[0].code == 10001) {

                        } else if (data.data.resultCodes[0].code == 90058) {
                            self.errorText = '活动即将开始';
                        } else if (data.data.resultCodes[0].code == 90702) {
                            self.errorText = '活动已结束';
                        } else {
                            self.errorText = data.data.resultCodes[0].msg

                        }
                        setTimeout(function () {
                            self.isError = false
                        }, 2000)
                    }
                } else {

                    this.$router.go({path: '/exchangeRecord?title=' + window.encodeURI('兑换记录')})
                }

            })
        },
        ready: function () {
            var self = this
            var mySwiper = new Swiper('.swiper-container', {
                direction: 'horizontal',
                loop: true,
                autoplay: 2000,
                // 如果需要分页器
                pagination: '.swiper-pagination'

            })
        },
        components: {},
        methods: {
            getImg: function (url) {
                return RES + url
            },
            requestData: function (invokeMethod, event, type) {
                var self = this,
                        encodeAddress = window.encodeURI(self.address),
                        encodeReceiver = window.encodeURI(self.receiver),
                        encodeMobile = window.encodeURI(self.mobile)
                var args = {
                    recordList: 'args_currentPage_1_integer_1',
                    productOrder: [
                        'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice, /*兑换的是一般的虚拟卡券*/
                        'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice + '&args_rechargeCard_1_string_' + self.reChangeMobile, /*充值类型的虚拟卡券*/
                        'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice + '&args_address_1_string_' + encodeAddress + '&args_receiver_1_string_' + encodeReceiver + '&args_mobile_1_string_' + encodeMobile, /*兑换的商品是实物*/
                        'args_goodId_1_long_' + self.productData.id + '&args_amount_1_integer_' + self.productData.discountPrice /*兑换的是特惠商品*/
                    ]
                }
                self.method = invokeMethod;
                if (type == 'recordList') {
                    hook.getEvent(invokeMethod + '&isNeedRealName=0&' + args[type], $(event.currentTarget), 1)
                } else {
                    hook.getEvent(invokeMethod + '&isNeedRealName=0&' + args[type][self.productData.goodsType - 1], $(event.currentTarget), 1)
                }

            },
            submitBtn: function (id, event) {
                var self = this;
                if ($(id).hasClass("z-disabled")) {
                    return false
                } else {
                        if (self.productData.goodsType == 2 && self.productData.rechargeType == 1) {
                            self.limitMobile('#j-rechargeMobile')
                        }
                        if (self.isMobile) {
                            if (self.productData.goodsType == 3) {
                                self.inputAddress = true

                            } else {
                                self.surePayTip = true
                            }
                        } else {
                            return false
                        }
                        self.coverShow = true

                }

            },
            toSubmitOrder: function (invokeMethod, event, type) {
                var self = this
                if (self.address != '' && self.receiver != '' && self.mobile != '') {
                    this.surePayTip = true
                    self.inputAddress = false
                } else {
                    return false
                }

            },
            limitInput: function (address, receiver, mobile) {

                if (address != '' && receiver != '' && mobile != '') {
                    console.log('true')
                    return true
                } else {
                    console.log('false')
                    return false
                }
            },
            submitOrder: function (invokeMethod, event, type) {
                var self = this
                self.requestData(invokeMethod, event, type)

            },
            cancleBtn: function (id) {
                var self = this
//                $(id).removeClass('zoomIn').addClass('zoomOut')
                self.surePayTip = false
                self.coverShow = false
                self.exchangeSuccess = false
                self.inputAddress = false
            },
            limitMobile: function (id) {
                var reg = /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/;
                var mobileReg = /^1(3[4-9]|5[012789]|8[23478]|4[7]|7[8])\d{8}$/
                var val = $(id).val(),
                        self = this
                if (!reg.test(val)) {
                    self.phoneErrorText = '请输入正确的手机号码'
                    self.isWrong = true
                    setTimeout(function () {
                        self.isWrong = false
                    }, 2000)
                    self.isMobile = false
                } else {
                    if (!mobileReg.test(val)) {
                        self.phoneErrorText = '抱歉，当前仅支持兑换移动话费'
                        self.isWrong = true
                        setTimeout(function () {
                            self.isWrong = false
                        }, 2000)
                        self.isMobile = false
                    } else {
                        self.isMobile = true
                    }

                }
                $('#j-submitBtn').css('position', 'fixed')
            },
            hideChangeBtn: function (id) {
                $(id).css('position', 'static')
            },
            splitMsg: function (msg) {
                var self = this
                if (msg) {
                    self.finalGoodsDes = msg.replace(/#/g, ' ').replace('^', '\'').replace('$', '\"').replace('：', ':').split('&')
                } else {

                }

            }
        }
    }
</script>