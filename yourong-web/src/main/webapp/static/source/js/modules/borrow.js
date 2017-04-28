/**
 * //个人中心-我的借款
 * @author xxx
 */
(function () {
    var $popForm = $("#j-popularity-form");
    $popForm.on("click", ".j-selector", function (e) {
        $(this).toggleClass("z-actived");
        e.preventDefault();
        return false;
    }).on("mouseleave", function () {
        $(this).removeClass("z-actived");
    });
    if (location.href.indexOf('friendList#inviteFriends') === -1) {
        $popForm.find("input[name='status']").val(52);
        $popForm.find("input[name='type']").val(0);
        fnOnGoToPageGain();// 获取记录
    }

    // 菜单下的箭头定位
    arrowLocator(".u-uc-menu");


    /* 按钮的选中样式转化 */
    $(".j-type-select").on("click", function () {
        $(".m-activity-list").find(".z-current").removeClass("z-current");
        $(this).addClass("z-current");
			var index = $(this).index();
			console.log(index);
        // 箭头定位
        arrowLocator(".m-activity-list");
			$("#u-jiantou-up").attr("class", "u-arrow-up u-arrow-i" + index);
    });

    /* 还款中的借款click */
    $("#j-popularity-gain").on("click", function () {
        var status = $(this).attr("value");
        if ($popForm.find("input[name='status']").length > 0) {
            $popForm.find("input[name='status']").val(status);
        } else {
            $("#j-popularity-form").append(
                $("<input type='hidden' name='status' value='" + status
                    + "'>"))
        }
        if ($popForm.find("input[name='currentPage']").length > 0) {
            $popForm.find("input[name='currentPage']").val(1);
        }
        $popForm.find("input[name='status']").val(status);
        $popForm.find("input[name='type']").val(0);
        fnOnGoToPageGain();
    });
    /* 已还清的借款click */
    $("#j-popularity-end").on("click", function () {
        var status = $(this).attr("value");
        if ($popForm.find("input[name='status']").length > 0) {
            $popForm.find("input[name='status']").val(status);
        } else {
            $("#j-popularity-form").append(
                $("<input type='hidden' name='status' value='" + status
                    + "'>"))
        }
        if ($popForm.find("input[name='currentPage']").length > 0) {
            $popForm.find("input[name='currentPage']").val(1);
        }
        $popForm.find("input[name='type']").val(2);
        fnOnGoToPageGain();
    });

    /* 募集中的借款click */
    $("#j-popularity-raising").on("click", function () {
        var status = $(this).attr("value");
        if ($popForm.find("input[name='status']").length > 0) {
            $popForm.find("input[name='status']").val("");
        } else {
            $("#j-popularity-form").append(
                $("<input type='hidden' name='status' value='" + status
                    + "'>"))
        }
        if ($popForm.find("input[name='currentPage']").length > 0) {
            $popForm.find("input[name='currentPage']").val(1);
        }
        $popForm.find("input[name='status']").val("");
        $popForm.find("input[name='type']").val(1);
        fnOnGoToPageGain();
    });
    /* 逾期的借款click */
    $("#j-popularity-overdue").on(
        "click",
        function () {
            var status = $(this).attr("value");
            if ($popForm.find("input[name='status']").length > 0) {
                $popForm.find("input[name='status']").val(status);
            } else {
                $("#j-popularity-form").append(
                    $("<input type='hidden' name='status' value='" + status
                        + "'>"))
            }
            if ($popForm.find("input[name='currentPage']").length > 0) {
                $popForm.find("input[name='currentPage']").val(1);
            }
            $popForm.find("input[name='status']").val(status);
            fnOnGoToPageOverdue();
        });
    /*  流标 click */
    $("#j-popularity-label").on("click", function () {
        var status = $(this).attr("value");
        if ($popForm.find("input[name='status']").length > 0) {
            $popForm.find("input[name='status']").val(status);
        } else {
            $("#j-popularity-form").append(
                $("<input type='hidden' name='status' value='" + status
                    + "'>"))
        }
        if ($popForm.find("input[name='currentPage']").length > 0) {
            $popForm.find("input[name='currentPage']").val(1);
        }
        $popForm.find("input[name='status']").val(status);
        fnOnGoToPageLabel();
    });
    $("#j-popularity-borrowlist").on("click", ".j-selector li", function () {
        var status = $(this).attr("value");
        $popForm.find("input[name='status']").val(status);
        $("#j-popularity-borrowlist").find("input[name='currentPage']").val(1);
        fnOnGoToPageGain();
    });
    
    //本息记录
    $("#j-popularity-borrowlist").on("click", ".piRecord-link", function() {
    	var projectId = $(this).parent().parent().find("input").val();
        $.projectId=projectId;
    		$.xPost({
    			url: environment.globalPath + "/myBorrow/interest/list",
    			data: {
    				"projectId": projectId
    			},
    			type: "GET",
    			callback: function(data) {
    				var res = data.resultList,
                        interests=res;

    				$("#j-pi-record tbody tr").remove();
    				if (!!interests && interests.length > 0) {
    					for (var i = 0; i < interests.length; i++) {
    						var index = i + 1;
    						var trObj = $("<tr>" +
    								"<td >" + index + "</td>" +
    								"<td name='refundTimeStr[" + index + "]'>" + interests[i].refundTimeStr+"</td>" +
    								"<td name='payTypes[" + index + "]'>" + interests[i].payTypesStr + "</td>" +
    								"<td name='payableAmount[" + index + "]'><span>" + interests[i].payableAmountStr + "</span></td>" +
    								//"<td class='u-record-deep' name='status[" + index + "]'><span>" + interests[i].statusStr + "</span></td>" +
    								"<td  name='overdueStatus[" + index + "]'>" + interests[i].overdueStatusStr + "</td>" +
    								"<td name='underwrite[" + index + "]'>" + interests[i].underwriteStr + "</td>" +
    								"<td name='overdue[" + index + "]'>" + interests[i].overdueStr + "</td>" +
    						"</tr>");
    						$("#j-pi-record tbody").append(trObj);
    					}
    				}

    			}
    		});
        //显示本息记录弹框
        xShade("show");
        $('.j-piRecord').removeClass('f-dn');
    });
  //逾期还款记录
    $("#j-popularity-borrowlist").on("click", ".overdue-record-link", function() {
    	var projectId = $(this).parent().parent().find("input").val();
        $.projectId=projectId;
    		$.xPost({
    			url: environment.globalPath + "/myBorrow/overdueComm/list",
    			data: {
    				"projectId": projectId
    			},
    			type: "GET",
    			callback: function(data) {
    				var res = data.resultList;
    				var interests;
    				interests=res;
    				$("#j-overdue-comm-record tbody tr").remove();
    				if (!!interests && interests.length > 0) {
    					for (var i = 0; i < interests.length; i++) {
    						var index = i + 1;
    						var trObj = $("<tr>" +
    								"<td >" + index + "</td>" +
    								"<td name='overdueStartDate[" + index + "]'>" + interests[i].overdueStartDateStr + "</td>" +
    								"<td name='repayTime[" + index + "]'>" + interests[i].repayTimeStr + "</td>" +
    								"<td name='overdueDay[" + index + "]'><span>" + interests[i].overdueDay + "天</span></td>" +
    								"<td name='overduePrincipal[" + index + "]'><span>" + interests[i].overduePrincipalStr + "</span></td>" +
    								"<td name='overdueInterest[" + index + "]'><span>" + interests[i].overdueInterestStr + "</span></td>" +
    								"<td name='getOverdueFine[" + index + "]'><span>" + interests[i].overdueFineStr + "</span></td>" +
    								"<td name='realpayAmount[" + index + "]'><span>" + interests[i].realpayAmountStr + "</span></td>" +
    								"<td  name='repayStatus[" + index + "]'>" + interests[i].repayStatusStr + "</td>" +
    						"</tr>");
    						$("#j-overdue-comm-record tbody").append(trObj);
    					}
    				}
    			}
    		});
        //显示逾期还款记录弹框
        xShade("show");
        $('.j-overdue-comm').removeClass('f-dn');
    });
    //垫资还款记录
    $("#j-popularity-borrowlist").on("click", ".underwrite-record-link", function() {
    	var projectId = $(this).parent().parent().find("input").val();
        $.projectId=projectId;
    		$.xPost({
    			url: environment.globalPath + "/myBorrow/overdue/list",
    			data: {
    				"projectId": projectId
    			},
    			type: "GET",
    			callback: function(data) {
    				var res = data.resultList;
    				var interests;
    				interests=res;
    				$("#j-overdue-record tbody tr").remove();
    				if (!!interests && interests.length > 0) {
    					for (var i = 0; i < interests.length; i++) {
    						var index = i + 1;
    						var trObj = $("<tr>" +
    								"<td >" + index + "</td>" +
    								"<td name='repayDate[" + index + "]'>" + interests[i].repayDateStr + "</td>" +
    								"<td name='overduePrincipal[" + index + "]'>" + interests[i].overduePrincipalStr + "</td>" +
    								"<td name='overdueInterest[" + index + "]'><span>" + interests[i].overdueInterestStr + "</span></td>" +
    								"<td name='overdueFine[" + index + "]'><span>" + interests[i].overdueFineStr + "</span></td>" +
    								"<td name='breachAmount[" + index + "]'><span>" + interests[i].breachAmountStr + "</span></td>" +
    								"<td name='payableAmount[" + index + "]'><span>" + interests[i].payableAmountStr + "</span></td>" +
    								"<td name='realpayAmount[" + index + "]'><span>" + interests[i].realpayAmountStr + "</span></td>" +
    								"<td class='u-record-deep' name='repayType[" + index + "]'><span>" + interests[i].repayTypeStr + "</span></td>" +
    								"<td  name='repayStatus[" + index + "]'>" + interests[i].repayStatusStr + "</td>" +
    						"</tr>");
    						$("#j-overdue-record tbody").append(trObj);
    					}
    				}
    			}
    		});
        //显示逾期还款记录弹框
        xShade("show");
        $('.j-overdue').removeClass('f-dn');
    });
    //垫资还款
    $("#j-popularity-borrowlist").on("click", ".underwrite-link", function() {
    	var projectId = $(this).parent().parent().find("input").val();
        $.projectId=projectId;
    		$.xPost({
    			url: environment.globalPath + "/myBorrow/underwrite/detail",
    			data: {
    				"projectId": projectId
    			},
    			type: "GET",
    			callback: function(data) {
    				var res = data.result;
    				var interests;
    				if (!!res) {
    					//列表
    					interests = res.overdueList;
    					$("#j-popping-record tbody tr").remove();
        				if (!!interests && interests.length > 0) {
        					for (var i = 0; i < interests.length; i++) {
        						var index = i + 1;
        						var trObj = $("<tr>" +
        								"<td name='periods[" + index + "]'>" + interests[i].periods+ "</td>" +
        								"<td name='repayDate[" + index + "]'>" + interests[i].repayDateStr + "</td>" +
        								"<td name='interestTypes[" + index + "]'>" + interests[i].interestTypesStr + "</td>" +
        								"<td name='totalPayAmount[" + index + "]'><span>" + interests[i].totalPayAmountStr + "</span></td>" +
        								"<td name='overdueStatus[" + index + "]'>" + interests[i].overdueStatusStr + "</td>" +
        								"<td name='underwrite[" + index + "]'>" + interests[i].underwriteStr + "</td>" +
        								"<td name='overdue[" + index + "]'>" + interests[i].overdueStr + "</td>" +
        						"</tr>");
        						$("#j-popping-record tbody").append(trObj);
        					}
        				}
    					//详情
    					if(!!res){
    						//逾期本金
    						$("#borrow_overduePrincipal").html(res.overduePrincipalStr);
    						//逾期利息
    						$("#borrow_overdueInterest").html(res.overdueInterestStr);
    						//滞纳金
    						$("#borrow_overdueFine").html(res.overdueFineStr);
    						//未还本金总额
    						$("#borrow_returnPrincipal").html(res.returnPrincipalStr);
    						//逾期天数
    						$("#borrow_overdueDay").html(res.overdueDay+"天");
    						//逾期罚息率
    						$("#borrow_overdueFeeRate").html(res.overdueFeeRate+"‰");
    						//合计应还款
    						$("#borrow_payableAmount").html(res.payableAmountStr);
    						
    						$("#borrow_projectId").val(res.projectId);
    					}
    				}
                    var tdsum=$("#j-popping-record").find("tr").length;
                if(tdsum>6){
                    $("#j-popping-record").parent().addClass("u-record-underwrite")
                }

    			}
    		});
        //显示垫资还款弹框
        xShade("show");
        $('.j-underwrite').removeClass('f-dn');
    });
    //逾期还款
    $("#j-popularity-borrowlist").on("click", ".overdue-link", function() {
    	var projectId = $(this).parent().parent().find("input").val();
        $.projectId=projectId;
    		$.xPost({
    			url: environment.globalPath + "/myBorrow/overdue/detail",
    			data: {
    				"projectId": projectId
    			},
    			type: "GET",
    			callback: function(data) {
    				var res = data.result;
    				var interests;
    				if (!!res) {
    					//列表
    					interests = res.overdueList;
    					$("#j-popping-common-record tbody tr").remove();
        				if (!!interests && interests.length > 0) {
        					for (var i = 0; i < interests.length; i++) {
        						var index = i + 1;
        						var trObj = $("<tr>" +
        								"<td name='periods[" + index + "]'>" + interests[i].periods+ "</td>" +
        								"<td name='repayDate[" + index + "]'>" + interests[i].repayDateStr + "</td>" +
        								"<td name='interestTypes[" + index + "]'>" + interests[i].interestTypesStr + "</td>" +
        								"<td name='totalPayAmount[" + index + "]'><span>" + interests[i].totalPayAmountStr + "</span></td>" +
        								"<td name='overdueStatus[" + index + "]'>" + interests[i].overdueStatusStr + "</td>" +
        								"<td name='underwrite[" + index + "]'>" + interests[i].underwriteStr + "</td>" +
        								"<td name='overdue[" + index + "]'>" + interests[i].overdueStr + "</td>" +
        						"</tr>");
        						$("#j-popping-common-record tbody").append(trObj);
        					}
        				}
    					//详情
    					if(!!res){
    						//逾期本金
    						$("#overdue_overduePrincipal").html(res.overduePrincipalStr);
    						//逾期利息
    						$("#overdue_overdueInterest").html(res.overdueInterestStr);
    						//滞纳金
    						$("#overdue_overdueFine").html(res.formatOverdueFineStr);
    						//未还本金总额
    						$("#overdue_returnPrincipal").html(res.returnPrincipalStr);
    						//逾期天数
    						$("#overdue_overdueDay").html(res.overdueDay+"天");
    						//逾期罚息率
    						$("#overdue_overdueFeeRate").html(res.lateFeeRate +"‰" || 0+"‰");
    						//合计应还款
    						$("#overdue_payableAmount").html(res.commonPayableAmountStr);
    						
    						$("#borrow_projectId").val(res.projectId);
    					}
    				}
                    var tdsum=$("#j-popping-common-record").find("tr").length;
                if(tdsum>6){
                    $("#j-popping-common-record").parent().addClass("u-record-underwrite")
                }

    			}
    		});
        //显示垫资还款弹框
        xShade("show");
        $('.j-underwrite-common').removeClass('f-dn');
    });
    //还款
    $(".j-underwrite").on("click", ".u-borrow-button", function() {
		var projectId = $("#borrow_projectId").val(),
			$this = $(this),
			clickInterval = 1000, // 默认一秒钟的
			thisClickTime = (new Date).getTime();
		$(".u-borrow-button").addClass("z-forbid");
		if(thisClickTime - ($this.attr('data-clickbegin') - 0 || 0) > clickInterval) {
			$this.attr('data-clickbegin', thisClickTime);

			$.xPost({
				url: environment.globalPath + "/myBorrow/toUnderwriteRepay",
				data: {
					"projectId": projectId
				},
				callback: function(data) {
					if (data.success) {
						$.xDialog({
							type: "success",
							content: "还款操作成功。温馨提示：稍后请查看【垫资还款记录】确认本次已经还款成功。",
						});
						$(".j-underwrite").off("click");
						$(".j-dialog-ok,.ui-dialog-close,.j-close-popping").on('click',function(){
							location.reload();
						})
					}else {
						$.xDialog({
							type: "error",
							content: data.resultCodeEum[0].msg
						});
					}
				}
			});
		} else {
			return false;
		}
    });
    //关闭弹框
    $('.j-close-popping').on('click',function(){
        xShade("hide");
        $('.j-close-popping').parent().addClass('f-dn');
		$(".u-borrow-button").removeClass("z-forbid")
    });
    ////弹框定位
    //var $postop=$('.j-position-top'),winHeight;
    ////窗口高度
    //if (window.innerHeight){
    //       winHeight = window.innerHeight;
    //}else if ((document.body) && (document.body.clientHeight)){
    //       winHeight = document.body.clientHeight;
    //}
    ////定位
    //var boxheight=$postop.height()+150,
    //    top=(winHeight-boxheight)/2;
    //    $postop.css('top',top)
})();
/*流标 */
function fnOnGoToPageLabel() {
    var data = $("#j-popularity-form").serialize();
    $("#j-popularity-borrowlist").loading().load(
        environment.globalPath + "/myBorrow/borrow/labelList", data);
}
/* 借款记录load */
function fnOnGoToPageGain() {
    var data = $("#j-popularity-form").serialize();
    $("#j-popularity-borrowlist").loading().load(
        environment.globalPath + "/myBorrow/borrow/list", data);
}
/*逾期 */
function fnOnGoToPageOverdue() {
    var data = $("#j-popularity-form").serialize();
    $("#j-popularity-borrowlist").loading().load(
        environment.globalPath + "/myBorrow/borrow/overdueList", data);
}

/*逾期还款*/
$(".j-underwrite-common").on("click", ".u-borrow-button", function() {
	var projectId = $("#borrow_projectId").val(),
		$this = $(this),
		clickInterval = 1000, // 默认一秒钟的
		thisClickTime = (new Date).getTime();
	$(".u-borrow-button").addClass("z-forbid");
	if(thisClickTime - ($this.attr('data-clickbegin') - 0 || 0) > clickInterval) {
		$this.attr('data-clickbegin', thisClickTime);
		$.xPost({
			url: environment.globalPath + "/myBorrow/toOverdueRepay",
			data: {
				"projectId": projectId
			},
			callback: function(data) {
				if (data.success) {
					$.xDialog({
						type: "success",
						content: "还款操作成功。温馨提示：稍后请查看【逾期还款记录】确认本次已经还款成功。",
					});
					$(".j-underwrite-common").off("click");

					$(".j-dialog-ok,.ui-dialog-close,.j-close-popping").on('click',function(){
						location.reload();
					})
				}else {
					$.xDialog({
						type: "error",
						content: data.resultCodeEum[0].msg
					});
				}
			}
		});
	} else {
		return false;
	}
});

//借款管理-明细
var $projectcostDetail=$('.j-projectcost-detail')

$('#j-popularity-borrowlist').on('mouseenter','.j-projectcost-detail',function(){
	$(this).find('.j-projectcost-box').removeClass('f-dn')
}).on('mouseleave','.j-projectcost-detail', function () {
	$(this).find('.j-projectcost-box').addClass('f-dn')
})