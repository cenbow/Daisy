jQuery(function($) {

	//认证
	$('#query_vefify').on('click', function() {
		
		var  memberID= $("#search_memberID").val();
		
		var verifytype =$("#verify_type  option:selected").val();		
		$.post(
				config.urlMap.verify,{memberID:memberID,verifytype:verifytype},function(data){
					if(data.success) {
						bootbox.alert("用户认证通过");	
					} else {
						bootbox.alert(data.errorMsg);
					}
				}
			);
	});
	
	//新浪认证
	$('#query_sinavefify').on('click', function() {
		var  memberID= $("#search_sinamemberID").val();
		$.post(
				config.urlMap.sinaverify,{memberID:memberID},function(data){
					$('#sinaverify').html(data);
					$("#formAuthSavingPot").attr("target","_blank");//添加target属性，在新窗口打开
					document.getElementById("formAuthSavingPot").submit();
				}
			);
	});
	
});