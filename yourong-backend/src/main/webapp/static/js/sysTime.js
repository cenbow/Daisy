/**
 * Created by Administrator on 2015/1/19.
 */
jQuery(function ($) {
    $('.excuteTask').on('click', function () {
    	var  taskkey = $(this).attr("data-value");
    	
    	bootbox.confirm("你确定要执行吗?", function(result) {
			if (result) {
				
				
				
		        $(this).addClass("disabled");
		        var that = $(this);
		        $.post(
		            config.urlMap.excuteTask, {taskkey:taskkey}, function (data) {
		                that.removeClass("disabled");
		                if (data.success) {
		                    alert("执行成功");
		                } else {
		                    alert("执行异常");
		                }
		            }
		        );
		        
			}
		});
    	
    	
    	
        
    });











})