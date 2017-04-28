define(['base'],function (require, exports, module) {

var base = require('base');
var domain = environment.globalPath;
var urls = {
     dynamic: domain + '/activity/dynamicInvoke'
}

$('#show').text($('#loginSource').val() + "!");
getDynamic($('#loginSource').val());

function getDynamic(loginSource) {
	var method = $('#invoke_method').val();
	var args = $("input[name='args']");
	var invokeStr = '';
	if(args.length > 0) {
		$.each(args,function(n, obj) {
			if(n == 0)
				invokeStr += $(this).attr("data-invoke") + "=" + $(this).val();
			else 
				invokeStr += "&" + $(this).attr("data-invoke") + "=" + $(this).val();
		});
	}
	if(loginSource == 1) {
		// android post
		Android.GetEvent(method, invokeStr);
	} else if (loginSource == 2) {
		// ios post
		var url = 'yrw://method=' + method;
		if(invokeStr != '') {
			url += "&" + invokeStr;
		}
		window.location.href=url;
	} else if(loginSource == 3) {
		// mstation post
		var parameters={};
		parameters['xToken'] = $('#xToken').val();
		parameters['invokeMethod'] = method;
		parameters['invokeParameters'] = invokeStr;
		base.getAPI({
			url: urls.dynamic,
			data: parameters,
			callback: function (data) {
				if (data.success) {
					console.log(data);
				}
			}
		});
	} 
}
    
});
