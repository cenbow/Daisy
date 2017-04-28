(function () {
    //var $queryRanking = $('#j-queryRanking'),
    //    isLogined = $queryRanking.data("logined");
    //$queryRanking.on('click', function () {
    //    if (isLogined) {
    //        var rankingData = $queryRanking.data('ranking');
    //        if (typeof(rankingData) !== 'undefined') {
    //            showRankDialog(rankingData);
    //        } else {
    //            $.xPost({
    //                url: '/activity/investHouse/myRank',
    //                callback: function (data) {
    //                    showRankDialog(data);
    //                    $queryRanking.data('ranking', data);
    //                }
    //            });
    //        }
    //
    //        function showRankDialog(data) {
    //            var dialogData = {};
    //            if (data.success) {
    //                if (data.result.activityStatus > 4) {
    //                    if (data.result.activityStatus === 5) {
    //                        dialogData = {type: 'info', content: '�����ͣ'};
    //                    } else {
    //                        dialogData = {type: 'info', content: '��ѽ���'};
    //                    }
    //                } else if (data.result.activityStatus === 4) {
    //                    if (data.result.rankIndex) {
    //                        dialogData = {
    //                            type: 'success',
    //                            content: '��ǰ�������ۼ�Ͷ�ʶ�' + data.result.investAmountInteger + 'Ԫ,<br/>�����' + data.result.rankIndex + 'λ',
    //                            width:401
    //                        };
    //                    } else {
    //                        dialogData = {
    //                            type: 'info',
    //                            content: '��û��Ͷ�ʷ�������Ŀ���Ͻ�<a class="u-investHouse-productsList" href="/products/list-house-all-1.html" target="_blank">ȥͶ�ʰɣ�</a>'
    //                        };
    //                    }
    //                } else {
    //                    dialogData = {type: 'info', content: '���δ��ʼ'};
    //                }
    //            } else {
    //                dialogData = {type: 'info', content: '�װ����û�����ǰ��ѯ����϶࣬���Ժ�����~'};
    //            }
    //            $.xDialog(dialogData);
    //        }
    //    }
    //    else {
    //        window.location.href = '/security/login/';
    //    }
    //});
    //�Ż�ȯ����
    $.xPost({
        url: '/activity/investHouse/coupon',
        callback: function (data) {
            if (data.success) {
                var html = template('j-investHouse-tplcoupon', data);
                $('#j-investHouse-coupon').html(html);
            }
        }
    });
    
    $.xPost({
        url: '/products/prop/getHouseRecommendProject',
        data: {
            "queryNum": 2,
            "xToken": xToken
        },
        callback: function (data) {
            if (data.success) {
            	var hasInvesting = false;
            	for(var i=0; i<data.resultList.length; i++) {
            		var idIndex = i + 1;
            		$("#rec_projectName" + idIndex).html('<strong>' + data.resultList[i].prefixProjectName + '</strong>&nbsp;' + data.resultList[i].suffixProjectName);
                    $("#rec_aA" + idIndex).attr('href', environment.globalPath + "/products/detail-" + data.resultList[i].id + ".html");
                    $("#rec_aB" + idIndex).attr('href', environment.globalPath + "/products/detail-" + data.resultList[i].id + ".html");
                    $("#rec_img" + idIndex).attr('src', getProjectThumbnail(data.resultList[i].thumbnail));
                    $("#rec_img" + idIndex).attr('alt', data.resultList[i].suffixProjectName);
                    $("#rec_totalAmount" + idIndex).text(data.resultList[i].formatTotalAmount);
                    if (data.resultList[i].minAnnualizedRate != data.resultList[i].maxAnnualizedRate) {
                        $("#rec_annualizedRate" + idIndex).html('<span>' + data.resultList[i].formatMinAnnualizedRate + '~' + data.resultList[i].formatMaxAnnualizedRate + '</span>');
                    } else {
                        $("#rec_annualizedRate" + idIndex).html('<span>' + data.resultList[i].formatMinAnnualizedRate + '</span>');
                    }
                    if (data.resultList[i].status == 30 || data.resultList[i].status == 40) {
                        $("#rec_earningsDays" + idIndex).text(data.resultList[i].earningsDays);
                    } else {
                        $("#rec_earningsDays" + idIndex).text(data.resultList[i].earningsTotalDays);
                    }
                    $("#rec_process" + idIndex).text(data.resultList[i].process);
                    $("#rec_processBar" + idIndex).attr("style", "width:" + data.resultList[i].process);
                    $("#rec_balance" + idIndex).text(data.resultList[i].formatAvailableBalance);
                    $("#rec_aC" + idIndex).attr('href', environment.globalPath + "/products/detail-" + data.resultList[i].id + ".html");
                    $("#rec_aC" + idIndex).text(data.resultList[i].buttonText);
                    if (data.resultList[i].active && !data.resultList[i].stop) {
                        $("#rec_aC" + idIndex).addClass('f-bgc-red2');
                    } else {
                        $("#rec_aC" + idIndex).addClass('u-pl-btn z-closed');
                    }
                    if(data.resultList[i].joinLease) {
                    	$("#rec_joinLease" + idIndex).addClass('u-dividend-releaseIcon');
                    }
                    if(data.resultList[i].status == 30) {
                    	hasInvesting = true;
                    }
            	}
            }
            if(hasInvesting) {
            	$(".u-investHouse-btn").attr('href','/products/list-house-investing-1.html');
            }
        }
    });
}());
