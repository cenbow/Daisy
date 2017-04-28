(function () {
    //查找指定name值的节点
    $.fn.fm = function (name) {
        if (typeof name === 'string') {
            return this.find('[name="' + name + '"]');
        }
    };

    //新手项目
    $.getJSON('../source/data/payRise.json',function(data){
        showNewCustomerProject(data[0])
    })
    function showNewCustomerProject(data) {
        // var xData={showNewCustomerProject:data}

        var circle = $('#j-pl-stat2');
                if (data.success) {
                    $("#newC_prefixProjectName").text(data.result.prefixProjectName);
                    $("#newC_suffixProjectName").text(data.result.suffixProjectName);
                    $("#newC_totalAmount").text(data.result.formatTotalAmount);
                    if (data.result.active) {
                        circle.data('percent', data.result.round);
                        var round=$('#newC_round'),
                            oldImg=round.css('background-image'),
                            newImg=oldImg.replace(/_finish/g, data.result.round);
                        round.css('background-image',newImg);
                    } else {
                        circle.data({
                            'percent': '100',
                            'fgcolor':'#f8ada8',
                            'bgcolor':'#f8ada8'
                        });
                    }
                    try {
                        circle.circliful();
                    } catch (err) {

                    }
                    if (data.result.minAnnualizedRate != data.result.maxAnnualizedRate) {

                        $("#newC_annualizedRate").html('<span>' +
                        data.result.formatMinAnnualizedRate + '~' +
                        data.result.formatMaxAnnualizedRate +
                        '<span class="f-ff-amount f-fs22 f-fc-red2">%</span></span>');
                    } else {
                        $("#newC_annualizedRate").html('<span>' + data.result.formatMinAnnualizedRate + '%</span>');
                    }
                    if (data.result.status == 30 || data.result.status == 40) {
                        $("#newC_earningsDays").text(data.result.earningsDays);
                    } else {
                        $("#newC_earningsDays").text(data.result.earningsTotalDays);
                    }
                    $("#newC_process").text(data.result.process);
                    $("#newC_processBar").attr("style", "width:" + data.result.process);
                    $("#newC_balance").text(data.result.formatAvailableBalance);
                    $("#newC_aA,#newC_aB,#newC_aC").attr('href', 'http://www.yrw.com' +
                    "/products/detail-" + data.result.id + ".html?trackid=360B_SY_360B");
                    $("#newC_aC").text(data.result.buttonText);
                    if (data.result.active && !data.result.stop) {
                        $("#newC_aC").addClass('f-bgc-red2');
                    } else {
                        $("#newC_aC").addClass('u-pl-btn z-closed');
                    }
                    $("#newC_img").attr({
                        'src': getProjectThumbnail(data.result.thumbnail),
                        'alt': data.result.suffixProjectName
                    });
                    if(data.result.joinLease)
                    	$("#newC_joinLease").addClass('u-dividend-leaseicon');
                  }
    }

    //获取着陆页推荐项目
    $.getJSON('../source/data/payRise.json',function(data){
        showLandingRecommendProjectProject(data[1])
    })
    function showLandingRecommendProjectProject(data) {
                if (data.success) {
                    $("#rec_projectName").html('<strong>' + data.result.prefixProjectName + '</strong>&nbsp;' + data.result.suffixProjectName);
                    $("#rec_aA").attr('href', 'http://www.yrw.com' + "/products/detail-" + data.result.id + ".html?trackid=360B_SY_360B");
                    $("#rec_aB").attr('href', 'http://www.yrw.com' + "/products/detail-" + data.result.id + ".html?trackid=360B_SY_360B");
                    $("#rec_img").attr('src', getProjectThumbnail(data.result.thumbnail));
                    $("#rec_img").attr('alt', data.result.suffixProjectName);
                    $("#rec_totalAmount").text(data.result.formatTotalAmount);
                    if (data.result.minAnnualizedRate != data.result.maxAnnualizedRate) {
                        $("#rec_annualizedRate").html('<span>' + data.result.formatMinAnnualizedRate + '~' + data.result.formatMaxAnnualizedRate + '</span>');
                    } else {
                        $("#rec_annualizedRate").html('<span>' + data.result.formatMinAnnualizedRate + '</span>');
                    }
                    if (data.result.status == 30 || data.result.status == 40) {
                        $("#rec_earningsDays").text(data.result.earningsDays);
                    } else {
                        $("#rec_earningsDays").text(data.result.earningsTotalDays);
                    }
                    $("#rec_process").text(data.result.process);
                    $("#rec_processBar").attr("style", "width:" + data.result.process)
                    $("#rec_balance").text(data.result.formatAvailableBalance);
                    $("#rec_aC").attr('href', 'http://www.yrw.com' + "/products/detail-" + data.result.id + ".html?trackid=360B_SY_360B");
                    $("#rec_aC").text(data.result.buttonText);
                    if (data.result.active && !data.result.stop) {
                        $("#rec_aC").addClass('f-bgc-red2');
                    } else {
                        $("#rec_aC").addClass('u-pl-btn z-closed');
                    }
                    if(data.result.joinLease)
                    	$("#rec_joinLease").addClass('u-dividend-releaseIcon');
                }
    };
    //跟踪代码
    var traceUrl=$(".j-trace-url");
    traceUrl.each(function(){
        var selfUrl=$(this).attr("href"),
            stUrl=selfUrl.substring(selfUrl.indexOf("?")+1,0),
            pageUrl=location.href,
            traceNo=pageUrl.substring(pageUrl.indexOf("?")+1,
                pageUrl.length);
        if(pageUrl.indexOf("?")!==-1){
            $(this).attr("href",stUrl+traceNo);
        }
    });

})();