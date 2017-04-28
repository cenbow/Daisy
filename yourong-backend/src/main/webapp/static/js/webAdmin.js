var web_admin_form= $("#web-admin-form").Validform({
	tiptype: 4
});


$("#j-save-webInfo").click(function(){
	

	var startDate = $("#startDate").val();  
	var endDate = $("#endDate").val();  
	
	ediff = compareTwoDate(startDate,endDate); 
	//结束时间必须大于开始时间
	if (ediff < 0) {
		 bootbox.alert("结束时间必须大于开始时间");
		 return;
	 }
		if(web_admin_form.check(false)){
			var url = config.urlMap.saveWebAdmin;
			$('#web-admin-form').xform("post", url,function(data){
			 	   if(!data.success){
			 		  bootbox.alert("维护网站公告失败/(ㄒoㄒ)/~~");
			  	  	}else{
			  		  bootbox.alert("维护网站公告成功\(^o^)/",function() {window.location.reload();});
			  		  	
			  	  	}
			 	   
			});
		}
		
});

/*比较两个时间的大小
 * 1:end>start
 * 0:end==start
 * -1:start>end
 * */
function compareTwoDate(start,end){
	if(start==""){
		return 1;
	}else if(end==""){
		return -1;
	}else if(start=="" && end ==""){
		return 0;
	}else{
		var dayNum = new Date(end) - new Date(start);
		if(dayNum>0){
			return 1;
		}else if(dayNum<0){
			return -1;
		}else{
			return -1;
		}
	}
}

//重置清空
$("#j-reset-webInfo").click(function(){
	
	$("#startDate").attr("value","");  
	$("#endDate").attr("value","");   
	$("#href").attr("value","");  
	$("#content").val("");  
	$("#webClient").removeAttr("checked"); 
	$("#mobClient").removeAttr("checked"); 
	
});
