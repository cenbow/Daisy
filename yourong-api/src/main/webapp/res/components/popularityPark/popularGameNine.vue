<template>
    <div>
        <div class="u-gameBg">
            <div>
                <p class="f-fs14 u-myGrade">
                    您的人气值：<em class="f-color-gold">{{popularity}}点</em>
                    <span class="u-playAgainBtn" v-if="isGuessed" @click="playAgain">再玩一次</span>
                </p>
                <!--<p class="f-fs14 u-myGrade" v-else>-->
                <!--登录后查看人气值-->
                <!--</p>-->
            </div>
            <div class="u-popularGameNine" id="j-popularGameNine">
                <a href="javascript:void(0)" @click="inputPoint(0,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(1,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(2,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(3,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(4,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(5,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(6,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(7,$event)"><span></span></a>
                <a href="javascript:void(0)" @click="inputPoint(8,$event)"><span></span></a>

                <!--<span @click="inputPoint(1)"></span>-->
                <!--<span @click="inputPoint(2)"></span>-->
                <!--<span @click="inputPoint(3)"></span>-->
                <!--<span @click="inputPoint(4)"></span>-->
                <!--<span @click="inputPoint(5)"></span>-->
                <!--<span @click="inputPoint(6)"></span>-->
                <!--<span @click="inputPoint(7)"></span>-->
                <!--<span @click="inputPoint(8)"></span>-->
            </div>

            <p class="u-activityRules f-fs14" @click="showRules=true">
                活动规则>>
            </p>
        </div>
        <div class="u-winnerBlock">
            <p>
                9宫格赢家
            </p>
            <div class="u-winnerListWrap">
                <ul v-if="winnerListData.list.length" class="u-winnerList" id="j-winnerList">
                    <li v-for="item in winnerListData.list">
                        <span>
                        <img v-if="item.avatar" :src="alipath+item.avatar" alt="" width="100%">
                        <img v-else :src="getImg('/img/member/login_avatar.png')" alt="" width="100%">
                        </span>
                        <div class="f-fs14 f-dis-inlineBlock u-memberPhone">
                            <p>{{item.memberName}}</p>
                            <p class="f-fs12">{{item.createTimeStr2}}</p>
                        </div>
                        <div class="f-fs14 f-dis-inlineBlock u-gainGift">
                            <p>押宝：<em>{{item.chip}}</em>点</p>
                            <p class="f-color-gold">赢得：<em>{{item.rewardResult}}</em>点</p>
                        </div>
                    </li>
                </ul>
                <div v-else class="f-fs16 u-noWinnerList">
                    赶快来玩，幸运女神在召唤
                </div>
            </div>
        </div>

        <!--##    活动规则弹窗-->
        <div class="u-activityRulesWrap" v-if="showRules">
            <p class="f-fs14 f-color3">活动规则
                <span style="position: absolute;right: 5px;top:20px;" class=" j-clockBtn" @click="cancleBtn"><img
                        :src="getImg('/img/post/popularity/ico_inputClose.png')" alt="关闭" width="50%"></span></p>
            <ul class="f-fs14 f-color6">
                <li>用户输入任意点数人气值，即可获得一次猜“幸运格”的机会；</li>
                <li>9宫格中有3块“幸运格”，分别对应10倍、5倍以及2倍人气值奖励， 选中“幸运格”的用户所输入的人气值将翻相应倍数，未选中幸运格的用户人气值不予返还；</li>
                <li>猜幸运格获得的人气值奖励，将于10分钟内发放；</li>
                <li>如有其它疑问，请拨打客服热线 <br>400-025-6688，本活动最终解释权归有融网所有。</li>
            </ul>
        </div>
        <!--##    输入人气值弹窗-->
        <div class="u-inputBlock" v-if="pointInput">
            <p class="f-fs18 f-color3">试试手气
                <span style="position: absolute;right: 5px;top: 8px;" class="j-clockBtn" @click="cancleBtn"><img
                        :src="getImg('/img/post/popularity/ico_inputClose.png')" alt="关闭" width="50%"></span></p>
            <input v-model="point" type="number" class="f-fs14 f-color9" placeholder="请输入人气值">
            <a href="javascript:void(0)" @click="getLottery(0,$event)">
                <div class="u-sureBtn f-fs16 " :class="{'f-color-blue':limitInput(point)}">确定</div>
            </a>
        </div>
        <!--##    人气值不足提示-->
        <div class="u-gameTipBlock f-fs18" v-if="unEnoughPopularity">
            <p>
                人气值不足
            </p>
            <div class="u-sureBtn f-fs18  f-color-blue" @click="cancleBtn">确定</div>
        </div>
        <div class="u-gameTipBlock f-fs18" v-if="rewardTips">
            <p class="f-color6">
                {{rewardText}}
            </p>
            <div class="u-sureBtn f-fs18 f-color-blue" @click="cancleBtn">确定</div>
        </div>
        <div class="u-cover" style="display: block" v-if="coverShow"></div>
    </div>
</template>
<script>

    const RES = env.resPath
    export default {
        data: function () {
            return {
                siteurl: env.siteurl,
                res: env.resPath,
                alipath: env.alipath,
                winnerListData: {
                    list: []
                },
                popularity: initData.result.popularity,
                rewardText: '',
                point: '',
                index: '',
                pointInput: false,
                unEnoughPopularity: false,
                coverShow: false,
                rewardTips: false,
                isGuessed: false,
                rewardPoint: 0,
                showRules: false,
                pointTrue: false,
                isLogined: false
            }
        },
        ready: function () {
            this.winnerListData.list = this.$root.initMessage.data.result.list
        },
        created: function () {
            var self = this
            window.scrollTo(0, 0)
            //安卓传递title
            if (env.Os == 1) {
                Android.UpdateTitle('幸运九宫格')
            }
            this.$on('methodCallback', function (data, method) {
                var reward = {ratio: 0};
                switch (data.data.result.rewardCode) {
                    case 'noReward':
                        reward.ratio = 0;
                        break;
                    case 'PopularityFor10times':
                        reward.ratio = 10;
                        break;
                    case 'PopularityFor5times':
                        reward.ratio = 5;
                        break;
                    case 'PopularityFor2times':
                        reward.ratio = 2;
                        break;
                    default :
                        break;
                }
                if (reward.ratio) {
                    self.rewardText = '恭喜获得' + reward.ratio * self.point + '点人气值,您就是今天的幸运儿~';
                } else {
                    self.rewardText = '再来一次，幸运女神会眷顾你哦';
                }
                self.rewardTips = true
                self.pointInput = false
                self.rewardPoint = reward.ratio
                //计算剩余的人气值数
                self.popularity = initData.result.popularity = self.popularity - self.point + (self.point * reward.ratio)
                setTimeout(function () {
                    //翻转格子
                    self.gridAnimate(reward.ratio)
                    self.isGuessed = true
                    self.point = ''
                }, 1000);
            })
            if (hook.logined) {
                self.isLogined = true
            } else {
                self.isLogined = false
            }

        },
        computed: {},
        watch: {
            "winnerListData.list": function () {
                var self = this
                self.scrollUserList({
                    size: 5,
                    height: -50,
                    length: 1
                });
            }
        },
        methods: {
            getImg: function (url) {
                return RES + url
            },
            Multiplication: function (a, b) {
                return a * b

            },
            inputPoint: function (Index, event) {
                var self = this
                self.index = Index
//                if (hook.logined) {
//                    alert('已登录')
                    if (!self.isGuessed) {
                        self.pointInput = true
                        self.coverShow = true
                    } else {
                        return false
                    }
//                } else {
//                    alert('去登录')
//                    hook.login($(event.currentTarget))
//                }


            },
            getLottery: function (invokeMethod, event) {

                var self = this
                if (self.popularity < self.point) {
                    self.unEnoughPopularity = true
                    self.pointInput = false
                    self.point = ''
                } else {
                    if (!self.isGuessed && self.pointTrue) {
                        hook.getEvent(invokeMethod + '&isNeedRealName=0&args_chip_1_integer_' + self.point, $(event.currentTarget), 1)
                    } else {
                        return false
                    }

                }


            },

            gridAnimate: function (reward) {
                var ratio = [2, 5, 10],
                        gridArray = [],
                        randomGridArray = [],
                        $gridList = $('#j-popularGameNine').find('span'),
                        self = this;
                for (var n = 0; n < 9; n++) {
                    gridArray.push(n);
                }
                gridArray.splice(self.index, 1);
                if (reward) {
                    var ratioIndex = $.inArray(reward, ratio);
                    if (ratioIndex !== -1) {
                        ratio.splice(ratioIndex, 1);
                    }
                    randomGridArray = self.getArrayItems(gridArray, 2);
                    $gridList.addClass('z-opened')
                    $gridList.eq(self.index).removeClass('z-opened').addClass('z-actived' + reward);
                    $gridList.eq(randomGridArray[0]).removeClass('z-opened').addClass('z-opened' + ratio[0]);
                    $gridList.eq(randomGridArray[1]).removeClass('z-opened').addClass('z-opened' + ratio[1]);
                } else {
                    randomGridArray = self.getArrayItems(gridArray, 3);
                    $gridList.addClass('z-opened')
                    $gridList.eq(randomGridArray[0]).removeClass('z-opened').addClass('z-opened' + ratio[0]);
                    $gridList.eq(randomGridArray[1]).removeClass('z-opened').addClass('z-opened' + ratio[1]);
                    $gridList.eq(randomGridArray[2]).removeClass('z-opened').addClass('z-opened' + ratio[2]);
                    $gridList.eq(self.index).removeClass('z-opened').addClass('z-actived' + reward);

                }
//                self.lotteryAnimate(1);
            },
            //从一个给定的数组arr中,随机返回num个不重复项
            getArrayItems: function (arr, num) {
                //新建一个数组,将传入的数组复制过来,用于运算,而不要直接操作传入的数组;
                var temp_array = [];
                for (var index in arr) {
                    temp_array.push(arr[index]);
                }
                //取出的数值项,保存在此数组
                var return_array = [];
                for (var i = 0; i < num; i++) {
                    //判断如果数组还有可以取出的元素,以防下标越界
                    if (temp_array.length > 0) {
                        //在数组中产生一个随机索引
                        var arrIndex = Math.floor(Math.random() * temp_array.length);
                        //将此随机索引的对应的数组元素值复制出来
                        return_array[i] = temp_array[arrIndex];
                        //然后删掉此索引的数组元素,这时候temp_array变为新的数组
                        temp_array.splice(arrIndex, 1);
                    } else {
                        //数组中数据项取完后,退出循环,比如数组本来只有10项,但要求取出20项.
                        break;
                    }
                }
                return return_array;

            },
            playAgain: function () {
                var $gridList = $('#j-popularGameNine').find('span'),
                        self = this;
                self.isGuessed = false
                $gridList.removeClass('z-opened').removeClass('z-opened2').removeClass('z-opened5').removeClass('z-opened10').removeClass('z-actived0').removeClass('z-actived2').removeClass('z-actived5').removeClass('z-actived10')
                $gridList.eq(self.index).removeClass('z-actived' + self.rewardPoint)
            },
            scrollUserList: function (config) {
                $("#j-winnerList").each(function () {
                    var _this = $(this),
                            scrollSize = _this.find("li").length;
                    if (scrollSize > config.size) {
                        setInterval(function () {
                            var scrollItems = _this.find("li:visible");
                            _this.animate({marginTop: config.height}, 700, function () {
                                scrollItems.eq(0).appendTo(_this);
                                _this.css("margin-top", 0);
                            });
                        }, 3000);
                    }
                })
            },
            cancleBtn: function () {
                var self = this
                self.pointInput = false
                self.unEnoughPopularity = false
                self.coverShow = false
                self.rewardTips = false
                self.showRules = false
            },
            limitInput: function (num) {
                var reg = /^\+?[1-9]\d*$/,
                        self = this;
                if (reg.test(num)) {
                    self.pointTrue = true
                    return true;

                } else {
                    self.pointTrue = false
                    return false;


                }
            }

        }

    }


</script>