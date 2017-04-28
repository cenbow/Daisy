(function () {
    $("#memberIdentity_form_button").on("click", function () {
    	//alert("444");
        //var Province = $("#reportregion-area-province").val();
       // $("#province").val(Province);
        var data = $("#memberReport_form").serializeArray();
        var href = environment.globalPath + "/memberReport/save";
        /*if ($("#j-checkname").val() === "") {
            $.xDialog({
                content: "请输入姓名"
            });
            return false;
        }
        if ($("#j-checkmobile").val() === "") {
            $.xDialog({
                content: "请输入手机号"
            });
            return false;
        }
        if (Province === ""||Province=="1") {
            $.xDialog({
                content: "请选择所在省份"
            });
            return false;
        }
        if ($("#travelMode").val() === "") {
            $.xDialog({
                content: "请选择出行方式"
            });
            return false;
        }*/
        $.xPost({
            url: href,
            data: data,
            callback: function (data) {
            	var dataCodeEum0 = data.resultCodeEum[0],
            	reCode=Number(dataCodeEum0.code);
                if (data.success) {
                	 alert(dataCodeEum0.msg);
                	// dialogData = {type: 'info', content: '报名成功：感谢您报名参加“共融未来  行以致远——有融网一周年庆典暨投资人答谢会”'};
                    // $.xDialog(dialogData);
                } else {
                         alert(dataCodeEum0.msg);
                 }
                	//dialogData = {type: 'info', content: dataCodeEum0.msg};
                      //   $.xDialog(dialogData);

            }
        });
    });
}());