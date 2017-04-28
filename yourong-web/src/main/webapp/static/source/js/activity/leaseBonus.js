$("#leasebonus_form").on("mouseenter", ".j-lease-detail,.j-bonus-detail", function () {
    var item = $(this).parents(".j-item-detail"),
        left = $(this).position().left,
        top = item.position().top,
        index = item.index(),
        isLease = $(this).hasClass("j-lease-detail"),
        right = "auto",
        floatbox = "";
    if ((index + 1) % 4 != 0) {
        left = 0;
        $(".u-floatbox").children("i").css("left", (index % 4) * 250 + 178);
    } else {
        $(".u-floatbox").children("i").css({
            "left": "auto",
            "right": "46px"
        });
        left = "auto";
        right = 0;
    }
    if (isLease) {
        top = top + 300;
        floatbox = "#j-lease-box";
        var leaseBonusId = item.data("id");
        getLeaseDetail(leaseBonusId);
    } else {
        top = top + 338;
        floatbox = "#j-bonus-box";
        var projectId = item.data("pid");
        getBonusDetail(projectId, 1);
        getBonusPercentDetail(projectId);
    }
    $(floatbox).css({
        "left": left,
        "right": right,
        "top": top
    }).show();
}).on("mouseleave", ".j-lease-detail,.j-bonus-detail", function () {
    var isLease = $(this).hasClass("j-lease-detail"),
        targetBox = null;
    if (isLease) {
        targetBox = "#j-lease-box";
    } else {
        targetBox = "#j-bonus-box";
    }

    function resetBonusDetail() {
        $(targetBox).hide();
        if (!isLease) {
            $("#j-bonus-users").removeData(["projectId","pageNum"]).empty();
        }
    }
    var timer = setTimeout(function () {
        resetBonusDetail();
    }, 200);
    $(".j-lease-detail,.j-bonus-detail").on("mouseenter", function () {
        if ($(this) == $(targetBox)) {
            clearTimeout(timer);
        }
    });
    $(".u-floatbox").on("mouseenter", function () {
        clearTimeout(timer);
    }).on("mouseleave", function () {
        resetBonusDetail();
    });
});

//获取租赁详情
function getLeaseDetail(leaseBonusId) {
    $.getJSON(environment.globalPath + "/leaseBonus/leaseDetail?leaseBonusId=" + leaseBonusId, function (data) {
        var items = [];
        $.each(data, function (i, item) {
            items.push("<tr><td width='70'>" + (i + 1) + "</td>" +
                "<td width='208'>" + formatDate(item.startDate) + "~" + formatDate(item.endDate) + "</td>" +
                "<td width='88'>" + item.dayNum + "</td>" +
                "<td width='153'>￥" + item.rental + "</td>" +
                "<td>￥" + item.totalRental + "</td></tr>"
            )
        });
        $("#j-lease-body").html(items.join(""));
    });
}

//获取分红详情
function getBonusDetail(projectId, currentPage) {
    var data = {
        "currentPage": currentPage,
        "projectId": projectId
    },
    	totalPages=$("#j-bonus-users").data("totalPages");
    if(typeof(totalPages)!=="undefined"&&currentPage > totalPages){
    	return false;
    }
    
    $.getJSON(environment.globalPath + "/leaseBonus/bonusDetail", data, function (data) {
        var items = [];
        if(typeof(totalPages)==="undefined"){
        	$("#j-bonus-users").data("totalPages",data.totalPageCount);
        }
        
        $.each(data.data, function (i, item) {
            var avatars = "";
            if (item.avatars) {
                avatars = "https://oss-cn-hangzhou.aliyuncs.com" + item.avatars;
            } else {
                avatars = "$root_url/static/img/member/avatar_35x35.png";
            }
            items.push("<li><div class='u-user-wrap'><span class='u-user-head'>" +
                "<img src='"+avatars+"' alt='用户头像'/>" +
                "<span>&nbsp;</span></span>" + item.username + "</div>" +
                "<span>￥" + item.bonusAmountStr + "</span></li>"
            );
        });
        if (typeof (currentPage) !== "undefined" && currentPage === 1) {
            $("#j-bonus-users").html(items.join("")).data("projectId", projectId);
        } else {
            $("#j-bonus-users").append(items.join(""));
        }

    });
};

//分红百分比详情
function getBonusPercentDetail(projectId){
    var data = {"projectId":projectId};
    $.getJSON(environment.globalPath + "/leaseBonus/bonusPercentDetail", data, function (data) {
        $.each(data,function(n,v){
            $("em[name='"+n+"'],span[name='"+n+"']").html(v);
        });
    });

}

/*分页获取租赁分红列表*/
fnOnGoToPage();

//加载更多分红详情(分页)
$("#j-bonus-users").on("scroll", function () {
    var uHeight = $(this).outerHeight(),
        uScrollTop = $(this).scrollTop(),
        uscrollHeight = $(this).get(0).scrollHeight,
        pid = $(this).data("projectId"),
        pageNum = $(this).data("pageNum");
    if (typeof (pageNum) === "undefined") {
        pageNum = 2;
    }
    if ((uHeight + uScrollTop) >= uscrollHeight) {
        getBonusDetail(pid, pageNum);
        $(this).data("pageNum", pageNum + 1);
    }
});

function fnOnGoToPage() {
    loadLeaseBonusList();
}
function loadLeaseBonusList() {
        var data = $("#leasebonus_form").serialize();
        $("#j-leaseBonus-table").loading().load(environment.globalPath + "/leaseBonus/leaseBonusList", data, function () {
            $(".j-item-detail:nth-child(4n)").addClass("z-nth4n");
            //设置当前页为1
            $("input[name='currentPage']").val(1);
        });
}