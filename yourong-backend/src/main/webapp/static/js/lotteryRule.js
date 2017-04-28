jQuery(function($) {

	var lotteryRuleForm = $("#lottery_rule_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	var hz = ["一","二","三","四","五","六"];
	
	var flag =false;
	
	formInit();
	
	$.ajax({
        url: config.urlMap.ajax,
        data: {
            
        },
        type: "post",
        success:function(data){
        	if(data.success){
				var result=data.result;
				//var prizePool = result.prizePool;//奖项系数
				var prizeInPool = result.prizeInPool;//奖项设置
				var lottery = result.lottery;//抽奖规则抽奖规则
				var rewardHour = result.rewardHour;//抽奖有效时间
				var popularity = result.popularity;//抽奖返人气值
				
				/*for (var i = 0; i < prizePool.length; i++) {
					
					var trObj = $('#sample-table-1 tbody tr')[i];
					$(trObj).find("input[name='prizePool[" + i + "].ratio']").val(prizePool[i].ratio); //奖金系数
				}*/
				
				$("#rewardHour").val(rewardHour);
				$("#popularity").val(popularity);
				
				for (var i = 0; i < prizeInPool.length; i++) {
					if(i>2){
						addPrizeInPoolRow();
					}
					var trObj = $('#sample-table-2 tbody tr')[i];
					$(trObj).find("input[name='prizeInPool[" + i + "].proportion']").val(prizeInPool[i].proportion); //占奖金池比例大小 
					$(trObj).find("input[name='prizeInPool[" + i + "].num']").val(prizeInPool[i].num); //奖项个数
				}
				
				for (var i = 0; i < lottery.length; i++) {
					if(i>0){
						addLotteryRow();
					}
					var trObj = $('#sample-table-3 tbody tr')[i];
					$(trObj).find("input[name='lottery[" + i + "].startAmount']").val(lottery[i].startAmount); //区间金额 
					$(trObj).find("input[name='lottery[" + i + "].endAmount']").val(lottery[i].endAmount); //区间金额
					$(trObj).find("input[name='lottery[" + i + "].number']").val(lottery[i].number); //抽奖次数
				}
				
				
			}
			
		}
    });
	

	
	/**
	 * 添加奖项
	 */
	$('#add_prizeInPool').click(function() {
		if(!flag){
			bootbox.alert('请先进行编辑！');
			return false;
		}
		if($('#sample-table-2 tbody tr').length==6){
			bootbox.alert('最多六等奖！');
			return false;
		}
		if ($('#sample-table-2 tBody tr').length >= 1) {
			var read ="";
			if(!flag){
				read = "readonly";
			}
			
			$("#sample-table-2 tbody").append("<tr><td>" + (hz[$('#sample-table-2 tbody tr').length ]+"等奖") 
					+ " </td><td><input type='text' name='prizeInPool[" + ($('#sample-table-2 tbody tr').length) + "].proportion' " +
							"id=’prizeIn_pool_proportion_"+ ($('#sample-table-2 tbody tr').length) +"‘ " +
							" "+read+"  /></td>" +
							"<td><input type='text' name='prizeInPool[" + ($('#sample-table-2 tbody tr').length) + "].num' "+read+"   /></td></tr>")
							formInit();
		}
		return false;
	});

	/**
	 * 减少奖项
	 */
	$('#del_prizeInPool').click(function() {
		if(!flag){
			bootbox.alert('请先进行编辑！');
			return false;
		}
		if ($('#sample-table-2 tBody tr').length > 3) {
			$('#sample-table-2 tBody tr:last').remove();
		} else {
			bootbox.alert('最少减到3等奖，不允许删除！');
		}
		return false;
	});

	/**
	 * 添加抽奖规则
	 */
	$('#add_lottery').click(function() {
		if(!flag){
			bootbox.alert('请先进行编辑！');
			return false;
		}
		if($('#sample-table-3 tbody tr').length==5){
			bootbox.alert('最多5条规则！');
			return false;
		}
		if ($('#sample-table-3 tBody tr').length >= 1) {
			var read ="";
			if(!flag){
				read = "readonly";
			}
			var preEndAmount = "";
			if($('#sample-table-3 tBody tr:last .i_proportion_e:eq(0)').val()>0){
				preEndAmount = + $('#sample-table-3 tBody tr:last .i_proportion_e:eq(0)').val()+1;
			}
			
			$("#sample-table-3 tbody").append("<tr><td>" + ($('#sample-table-3 tbody tr').length + 1) + "</td><td><input type='text' name='lottery[" 
					+ ($('#sample-table-3 tbody tr').length) + "].startAmount' value='" + preEndAmount + "' class='i_proportion_s' "+read+"  /> " +
							"至  <input type='text' name='lottery[" + ($('#sample-table-3 tbody tr').length) 
							+ "].endAmount' class='i_proportion_e' "+read+"  /></td>" +
									"<td>"+"<span class='input-text'>X+</span>" +"<input type='text' name='lottery[" + ($('#sample-table-3 tbody tr').length) + "].number'"+read+"   /></td></tr>")
									formInit();
		}
		return false;
	});

	/**
	 * 减少规则
	 */
	$('#del_lottery').click(function() {
		if(!flag){
			bootbox.alert('请先进行编辑！');
			return false;
		}
		if ($('#sample-table-3 tBody tr').length > 1) {
			$('#sample-table-3 tBody tr:last').remove();
		} else {
			bootbox.alert('默认一条，不允许删除！');
		}
		return false;
	});

	
	$('#save_lottery_rule').click(function() {
		
		if(!flag){
			bootbox.alert('尚未修改！');
			return false;
		}
		if (lotteryRuleForm.check(false)) {
			$('#lottery_rule_form').xform('post', config.urlMap.save, function(data) {
				if (!data.success) {
					var flag = "规则保存失败!";
					if(!!data.resultCodeEum){
						flag = data.resultCodeEum[0].msg;
					}
					bootbox.alert(flag,function(){
						$('#edit_lottery_rule').removeAttr("disabled");
					});
				} else {
					bootbox.alert("规则保存成功!", function() {
						$('#edit_lottery_rule').removeAttr("disabled");
					});
					$("form :input[type='text']").attr("readonly","readonly");
				}
			});
		}
		
	});

	
	
	$('#edit_lottery_rule').click(function() {
		flag = true;
		$("form :input[type='text']").removeAttr("readonly"); 
	});
	
	$('#cancle_lottery_rule').click(function() {
		flag = false;
		window.location.reload();
		$("form :input[type='text']").attr("readonly","readonly");
	});
	
	
	function addPrizeInPoolRow() {
		$("#sample-table-2 tbody").append("<tr><td>" + (hz[$('#sample-table-2 tbody tr').length ]+"等奖") 
				+ " </td><td><input type='text' name='prizeInPool[" + ($('#sample-table-2 tbody tr').length) + "].proportion'" +
				"id=’prizeIn_pool_proportion_"+ ($('#sample-table-2 tbody tr').length) +"‘ " +
						"  readonly /></td>" +
						"<td><input type='text' name='prizeInPool[" + ($('#sample-table-2 tbody tr').length) + "].num' readonly  /></td></tr>")
						
						formInit();
	}
	
	function addLotteryRow() {
		$("#sample-table-3 tbody").append("<tr><td>" + ($('#sample-table-3 tbody tr').length + 1) + "</td><td><input type='text' name='lottery[" 
				+ ($('#sample-table-3 tbody tr').length) + "].startAmount' class='i_s_date' readonly /> " +
						"至  <input type='text' name='lottery[" + ($('#sample-table-3 tbody tr').length) 
						+ "].endAmount' class='i_e_date' readonly /></td>" +
								"<td>"+"<span class='input-text'>X+</span>" +"<input type='text' name='lottery[" + ($('#sample-table-3 tbody tr').length) + "].number' readonly /></td></tr>")
			
								formInit();
	}
	 
	function formInit (){
		lotteryRuleForm = $("#lottery_rule_form").Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});
	}
	
	
	
});

