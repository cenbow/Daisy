/***
 * 充值提现列表
 */
$(function() {
	var ITERMS_PER_PAGE_INT =10;
		
		 $("#rePagination").pagination( $("#rePagination").attr("data"), {
		    	prev_show_always:true,
				next_show_always:true,
				prev_text: "上一页",
				next_text: "下一页",
				//num_edge_entries: 4, //边缘页数
				//num_display_entries: 4, //主体页数
				callback: pageselectCallback,
				items_per_page: ITERMS_PER_PAGE_INT //每页显示1项				
			});
			function pageselectCallback(page_index, jq) {				
				var hef = environment.globalPath+"/memberBalance/findByPageRechargeLog?currentPage="+page_index+"&&iDisplayLength="+ITERMS_PER_PAGE_INT;
				var data ={},
				bankCodeList={ 
						"ICBC":"中国工商银行",
						"ABC":"中国农业银行",
						"BOC":"中国银行",
						"CMB":"招商银行",
						"CITIC":"中兴银行",
						"GDB":"广发银行",
						"HXB":"华夏银行",
						"PSBC":"中国邮储银行",
						"CMBC":"民生银行",
						"CCB":"建设银行",
						"COMM":"交通银行",
						"SPDB":"浦发银行",
						"CZB":"浙商银行",
						"SZPAB":"平安银行",
						"CEB":"光大银行",
						"BCCB":"北京银行",
						"BOS":"上海银行",
						"CIB":"兴业银行",
						"HCCB":"杭州银行",
						"HKBCHINA":"汉口银行",
						"NJCB":"南京银行",
						"NBCB":"宁波银行"
					}
				$.xPost({
		            url: hef,
		            data: data,
		            type:"GET",
		            callback: function(data) { 
		            	   if(data.resultList !== "undefined" ){
		            		   $("#rechargeTable tbody").empty();		            		
		            		  for(var index=0;index<data.resultList.length;index++){
		            			  var payMethod=Number(data.resultList[index].payMethod) || 1,
		            			  		bankImgUrl="",
		            			  		bankCode=data.resultList[index].bankCode;
		            			  if(payMethod===1){
									  bankImgUrl="w_"+bankCode;
		            			  }else{
									  bankImgUrl="q_"+bankCode;
		            			  };
								  var bankImg=	"<img src='" +
									  environment.globalPath +
									  "/static/img/member/bank/bank_" + bankImgUrl +
									  ".png' class='f-vam' alt='bank icon' title='"+bankCodeList[bankCode]+"'/>";
								  if(!bankCode){
									  bankImg="<em>--</em>"
								  }
		            				var trHTML = "<tr><td>"+data.resultList[index].rechargeTimeToS+"</td><td>"
		            						+data.resultList[index].rechargeNo
		            				      +"</td><td><em class='f-ff-rmb u-amount'>"+
		            				      data.resultList[index].amountToS+"</em></td><td>" +
										 bankImg
		            				      +"</td><td>"+data.resultList[index].statusToSt
		            				      +"</td><td>"+data.resultList[index].typeToS
		            				      +"</td><td width=140>" +data.resultList[index].remarks+
		            				      "</td></tr>";		            				
		                    		$("#rechargeTable").append(trHTML)
		            		   }
		            	   }         
		            	
		            }
		        });		
				return false;
			}
			
			$("#wiPagination").pagination($("#wiPagination").attr("data"), {
				prev_show_always:true,
				next_show_always:true,
				prev_text: "上一页",
				next_text: "下一页",		
				//num_edge_entries: 4, //边缘页数
				//num_display_entries: 4, //主体页数
				callback: pageselectwiCallback,
				items_per_page: ITERMS_PER_PAGE_INT //每页显示1项
				
			});			
			function pageselectwiCallback(page_index, jq) {				
				var hef = environment.globalPath+"/memberBalance/findByPageWithdrawLog?currentPage="+page_index+"&&iDisplayLength="+ITERMS_PER_PAGE_INT;
				var data ={};			
				$.xPost({
		            url: hef,
		            data: data,
		            type:"GET",
		            callback: function(data) { 
		            	   if(data.resultList !== "undefined" ){
		            		   $("#withrdTable tbody").empty();			            		  		            		 
		            		  for(var index=0;index<data.resultList.length;index++){
		            				var trHTML = "<tr><td>"+data.resultList[index].withdrawTimeToS+"</td><td>"
		            						+data.resultList[index].withdrawNo
		            				      +"</td><td> <em class='f-ff-rmb u-amount'>"+
		            				      data.resultList[index].withdrawAmountToS+"</em></td><td>"+
                                        (data.resultList[index].bankCardNo||'--')
		            				      +"</td><td>"+data.resultList[index].statusToS
		            				      				            			
		            				//if(data.resultList[index].status===1){
		            				//	cancelButtonHtml = "&nbsp;&nbsp;&nbsp;<a href ='javascript:void(0)' class='j-cancel' data-value='"+data.resultList[index].id+"'>取消</a>"
		            				//	trHTML = trHTML+cancelButtonHtml;
		            				//}
		            				trHTML = trHTML+
		            					"</td><td widt=150>" +data.resultList[index].notice+"</td></tr>";
		                    		$("#withrdTable").append(trHTML);
		            		   }
		            	   }
		            	
		            }
		        });		
				return false;
			}		
		

	
	
	//充值提现列表tab
    var $rechargeTab=$(".j-recharge-tab").find('a');

    $rechargeTab.bind("switchTab", function() {
        var index = $(this).index()-1;
        $(this).addClass("z-current").siblings().removeClass("z-current").addClass('u-tit-l');
        $(".u-rtable").eq(index).removeClass("f-dn").siblings(".u-rtable").addClass("f-dn");
    });

    var currentUrl=location.href,
        tabIndex=currentUrl.indexOf('?type='),
        tabName=currentUrl.slice(tabIndex+6);
    if(tabIndex!==-1&&tabName==='withdraw'){
        $rechargeTab.eq(2).trigger('switchTab');
    }



    //充值提现－历史收益列表
    $(".j-stat-more").mouseenter(function() {
        $(".j-stat-more ul").slideDown();
    }).mouseleave(function() {
    	$(".j-stat-more ul").slideUp();
    });
    
    //菜单下的箭头定位
	//arrowLocator(".u-uc-menu");
	//问候时间
    $("#j-regards").greetingTime();
    
    $("#withrdTable").on("click",'.j-cancel' ,function () {
        var id = $(this).data("value");
        var href = environment.globalPath + "/memberBalance/cancelWithdraw";
        var data = {'id':id};

        function delAction() {
            $.xPost({
                url: href,
                data: data,
                callback: function (data) {
                    if (data.success) {
                        window.location.href = environment.globalPath + "/memberBalance/rechargeIndex?type=withdraw";
                    } else {
                        showErrorMessage(data);
                    }
                }
            });
        }
        $.xDialog({
            content: '是否取消该笔提现申请？',
            okValue:'是',
            cancelValue:'否',
            callback: function () {
                delAction();
            },
            cancel: function () {
            }
        });
    });
    
});