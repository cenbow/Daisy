(function () {
    var page = 1,
        $coupon = $('#j-activity-coupon'),
        oTop = $coupon.offset().top;

    //init list
    getActivityList();

    //scroll list
    $(window).on('scroll', function () {
        var sTop = $(this).scrollTop();
        if (sTop > oTop + page * 835) {
            page += 1;
            return getActivityList(page);
        }
    });

    //get list
    function getActivityList(page) {

        page = page || 1;

        var totalPage = $coupon.data('totalPageCount');
        if (totalPage && page > totalPage) {
            return false;
        }

        $coupon.xLoading(function(target){
            $.xPost({
                url: '/banner/activityListData',
                data: {
                    currentPage: page,
                    type: 0
                },
                callback: function(data){
                    if (data != null) {

                        var html = template('j-activity-tplcoupon', data);

                        if (page === 1) {

                            $coupon.html(html);
                            $coupon.data('totalPageCount', data.totalPageCount);

                        } else if (page > 1 && page <= data.totalPageCount) {

                            $coupon.append(html);

                        } else {
                            return false;
                        }
                    }
                    target.find('.u-xloading').remove();
                }
            });
        },'fixed');

    }

}());