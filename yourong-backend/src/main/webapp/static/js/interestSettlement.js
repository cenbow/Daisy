jQuery(function($) {

    function getAllOperation(row) {
        var detail = "<a class='j-interest-detail-btn' data-rel='popover' data-placement='top' href='#' style='margin-left: 30px;' data-id='" + row.projectId + "' data-periodsdetail='" + row.interestPeriodsFormula + "' data-periods='" + row.interestPeriods + "' data-title='" + row.projectName + "' data-content=''>详情</a>"
        return detail;
    }
    /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = { 
            "sExtends": "xls", 
            "sButtonText": "导出Excel" ,
            "sFileName": "线上线下营收结算.xls"
    };
    if(config.permission.excelExport){
    	exportButton.push(excelButton);
    }
    	
    var interestTable = $('#interestSettlement-table').dataTable({
		"tableTools": {//excel导出
            "aButtons": exportButton,
            "sSwfPath": config.swfUrl
        },
        'order': [
            [0, "desc"]
        ],
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [
          {'mDataProp': 'projectId',
              'bSortable': false,
              "bVisible" : false
              },
      	{
            'mDataProp': 'projectName',
            'bSortable': false
        }, {
            'mDataProp': 'lenderName',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if (row.lenderMember != null && row.lenderMember.trueName != null) {
                    return row.lenderMember.trueName;
                } else {
                    return "";
                }
            }
        }, {
            'mDataProp': 'endDate',
            'bSortable': true,
            'mRender':function(data){
            	return formatDate(data);
            }
        }, {
            'mDataProp': 'totalAmount',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'offlineAnnualizedRate',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return data == null ? "" : data + "%";
            }
        }, {
            'mDataProp': 'totalPeriods',
            'bSortable': false
        }, {
            'mDataProp': 'period',
            'bSortable': false,
            'mRender': function(data) {
                return data == null ? "" : data + "天";
            }
        }, {
            'mDataProp': 'interestSettlementStr',
            'bSortable': false,
            'mRender': function(data, type, row) {
                var settleData = "<font color='blue'>" + data + "</font>";
                if (row.interestSettlementFormula != null && row.interestSettlementFormula.length > 0) {
                    settleData = row.interestSettlementFormula + "=" + settleData;
                    if (row.totalPeriods > 1) {
                        return settleData + getAllOperation(row);
                    }
                }
                return settleData;
            }
        },{
            'mDataProp': 'interestSettlementStr',
            'bSortable': false
        }, {
            'mDataProp': 'userInterestStr',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'usedCouponAmountStr',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'extraInterestStr',
            'bSortable': false,
            'mRender': function(data) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'grossProfitStr',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return "￥" + data;
            }
        }, {
            'mDataProp': 'transactionTime',
            'bSortable': true,
            'mRender': function(data, type, row) {
            	if(row.status >= 50) {
            		if(data == null) {
            			return '';
            		}
            		return formatDate(data,"yyyy-mm-dd");            		
            	} else return '未完成';
            }
        },{
            'mDataProp': 'plaInterestStr',
            'bSortable': false,
            'mRender': function(data, type, row) {
                //return "￥" +(Number(row.interestSettlementStr)-Number(row.userInterestStr)).toFixed(2);
            	return "￥" +data;
            }
        } ]
    });

    /**
     * 查询事件
     */
    $("#query_interest").on("click", function() {
        interestTable.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
        return false;
    });

    /**
     * 详情事件
     */
    $("#interestSettlement-table").on("click", ".j-interest-detail-btn", function() {
        var projectId = $(this).data("id"),
            periodsdetail = $(this).data("periodsdetail"),
            periods = $(this).data("periods"),
            title = $(this).data("title"),
            perioddetailArray = periodsdetail.split(","),
            periodsArray = periods.split(",");
        console.info(periods);
        $(".j-interest-periods-detail").find("tr").remove();
        $(".j-interest-title").find("span").remove();
	    for(var i=0;i<perioddetailArray.length;i++){
	        if(perioddetailArray[i].length>0){
	            $(".j-interest-periods-detail").append("<tr><td style='width: 60px;'>第"+(i+1)+"期</td><td>"+perioddetailArray[i]+"="+ changeTwoDecimal_f(periodsArray[i])+"</td></tr>");
	        }else{
	            $(".j-interest-periods-detail").append("<tr><td style='width: 60px;'>第"+(i+1)+"期</td><td>"+periodsArray[i]+"</td></tr>");
	        }
	    }
	      
	    $(".j-interest-title").append("<span>"+title+"</span>");
	    $("#settlement-detail-table").modal('show');
//        var periodsContent = "<table class='table table-striped table-bordered table-hover j-interest-periods-detail' cellspacing='0' width='100%'>";
//        for (var i = 0; i < perioddetailArray.length; i++) {
//            if (perioddetailArray[i].length > 0) {
//                periodsContent = periodsContent + "<tr><td style='width: 60px;'>第" + i + "期</td><td>" + perioddetailArray[i] + "=" + periodsArray[i] + "</td></tr>";
//            } else {
//                periodsContent = periodsContent + "<tr><td style='width: 60px;'>第" + i + "期</td><td>" + periodsArray[i] + "</td></tr>";
//            }
//            if(i=perioddetailArray.length-1){
//                periodsContent = periodsContent + "</table>";
//            }
//        }
//        $(this).popover({
//        	content:periodsContent,
//        	trigger:"click",
//            html: true
//        });
    });


});

changeTwoDecimal_f= function (floatvar)
{
var f_x = parseFloat(floatvar);
if (isNaN(f_x))
{
alert('function:changeTwoDecimal->parameter error');
return false;
}
var f_x = Math.round(f_x*100)/100;
var s_x = f_x.toString();
var pos_decimal = s_x.indexOf('.');
if (pos_decimal < 0)
{
pos_decimal = s_x.length;
s_x += '.';
}
while (s_x.length <= pos_decimal + 2)
{
s_x += '0';
}
return s_x;
}