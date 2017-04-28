(function () {
    var $queryRanking = $('#j-queryRanking'),
        isLogined = $queryRanking.data("logined");
    $queryRanking.on('click', function () {
        if (isLogined) {
            var rankingData = $queryRanking.data('ranking');
            if (typeof(rankingData) !== 'undefined') {
                showRankDialog(rankingData);
            } else {
                $.xPost({
                    url: '/activity/investHouse/myRank',
                    callback: function (data) {
                        showRankDialog(data);
                        $queryRanking.data('ranking', data);
                    }
                });
            }

            function showRankDialog(data) {
                var dialogData = {};
                if (data.success) {
                    if (data.result.activityStatus > 4) {
                        if (data.result.activityStatus === 5) {
                            dialogData = {type: 'info', content: '活动已暂停'};
                        } else {
                            dialogData = {type: 'info', content: '活动已结束'};
                        }
                    } else if (data.result.activityStatus === 4) {
                        if (data.result.rankIndex) {
                            dialogData = {
                                type: 'success',
                                content: '您当前房有融累计投资额' + data.result.investAmountInteger + '元,<br/>排名第' + data.result.rankIndex + '位',
                                width:401
                            };
                        } else {
                            dialogData = {
                                type: 'info',
                                content: '您还没有投资房有融项目，赶紧<a class="u-investHouse-productsList" href="/products/list-house-all-1.html" target="_blank">去投资吧！</a>'
                            };
                        }
                    } else {
                        dialogData = {type: 'info', content: '活动还未开始'};
                    }
                } else {
                    dialogData = {type: 'info', content: '亲爱的用户，当前查询人数较多，请稍后再试~'};
                }
                $.xDialog(dialogData);
            }
        }
        else {
            window.location.href = '/security/login/';
        }
    });
    //优惠券请求
    $.xPost({
        url: '/activity/investHouse/coupon',
        callback: function (data) {
            if (data.success) {
                var html = template('j-investHouse-tplcoupon', data);
                $('#j-investHouse-coupon').html(html);
            }
        }
    });
    //列表请求
    $.xPost({
        url: '/activity/investHouse/list',
        callback: function (data) {
            if (data.success) {
                var html = template('j-investHouse-tpllist', data);
                $('#j-investHouse-list').html(html);
                $('.j-investHouse-list').scrollList({
                    size: 4,
                    length: 4,
                    height: 75,
                    time: 3000
                });
            }
        }
    });
}());