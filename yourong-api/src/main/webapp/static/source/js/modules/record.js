/**
 * Created by adeweb on 15/5/14.
 * @module appRecord
 * @description app端投资记录
 */

(function () {
    var listObj = $("#transactionsDetail"),
        tableList = listObj.find("tbody");
    loadTransactionsByProjectId(projectId, 1);

    //加载项目交易记录
    function loadTransactionsByProjectId(projectId, pageNo) {

        $.getJSON(environment.globalPath+'/project/queryTransactionDetail', {
                'projectId': projectId,
                'pageNo': pageNo
            },
            function (data) {
                if (typeof (data) !== "undefined" && data.totalElements) {
                    var itemHtml = getDataHtml(data.data),
                        moreObj = listObj.find('.j-btn-more');
                    moreObj.remove();
                    if (pageNo === 1) {
                        tableList.html(itemHtml);
                        listObj.data('pageNo',1);
                        listObj.data('totalPageCount',data.totalPageCount);
                    } else {
                        tableList.append(itemHtml);
                        listObj.data('pageNo',pageNo);
                    }
                    if (pageNo>=data.totalPageCount) {
                        listObj.find('.j-btn-more').remove();
                    }
                }
            }
        );
        //记录数据处理
        function getDataHtml(data) {
            var dataHtml = [];
            $.each(data, function (n, item) {
                var avatars = "",
                    transactionTime = new Date(item.transactionTime),
                    date = formatDate(transactionTime, "yyyy-mm-dd"),
                    time = formatDate(transactionTime, "HH:mm:ss");
                dataHtml.push('<tr><td>' + item.username + '</td><td>￥' + item.investAmount +
                '</td><td>' + date + ' ' + time + '</td></tr>')
            });
            dataHtml.push('<tr class="u-load-more j-btn-more"><td colspan="3">点击加载更多...</td></tr>');
            return dataHtml.join("");
        }
    }

    $('.u-record-table').on('click', '.j-btn-more', function () {
        $(this).html('<td colspan="3"><img src="/static/img/common/loading.gif"' +
        ' class="u-load-img" alt="loading..."/>投资记录加载中... </td>');
        var pageNo= 1,prevPageNo=listObj.data('pageNo'),
            totalPageCount=listObj.data('totalPageCount');
        if(prevPageNo){
            pageNo=prevPageNo+1;
        }else{
            pageNo=pageNo+1;
        }
        if(pageNo<=totalPageCount){
            loadTransactionsByProjectId(projectId, pageNo);
        }

    });
    // 时间格式化 useage:formatDate("1408609262000","yyyy-mm-dd");
    function formatDate(timestamp, format) {
        if (typeof timestamp !== "undefined") {
            var date = new Date(Number(timestamp)),
                oDate = "",
                year = date.getFullYear(),
                month = date.getMonth() + 1,
                day = date.getDate(),
                hours = date.getHours(),
                minutes = date.getMinutes(),
                seconds = date.getSeconds();
            var time = [month, day, hours, minutes, seconds];
            for (var i in time) {
                if (time[i] < 10) {
                    time[i] = "0" + time[i];
                }
            }
            if (typeof format === "undefined") {
                oDate = year + "-" + time[0] + "-" + time[1];
                return oDate;
            } else {
                switch (format) {
                    case "yyyy-mm-dd HH:mm:ss":
                        oDate = year + "-" + time[0] + "-" + time[1] + " " + time[2] + ":" + time[3] + ":" + time[4];
                        break;
                    case "yyyy-mm-dd HH:mm":
                        oDate = year + "-" + time[0] + "-" + time[1] + " " + time[2] + ":" + time[3];
                        break;
                    case "yyyy-mm-dd":
                        oDate = year + "-" + time[0] + "-" + time[1];
                        break;
                    case "HH:mm:ss":
                        oDate = time[2] + ":" + time[3] + ":" + time[4];
                        break;
                    case "HH:mm":
                        oDate = time[2] + ":" + time[3];
                        break;
                    default:
                        break;
                }
                return oDate;
            }
            return;
        } else {
            throw new Error("formatDate():timestamp is not defined");
        }
    }
})();