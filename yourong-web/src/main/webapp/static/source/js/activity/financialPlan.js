/**
 * 等本等息专题页
 */
(function(){
    var financialPlanBtn=$("#j-financialPlan-btn");
    $.xPost({
        url: '/products/averageCapitalMethod',
        callback: function (data) {
            if(data.success){
                var projectId=data.result.id;
                if(projectId){
                    financialPlanBtn.attr("href","/products/detail-"+projectId+".html")
                }else{
                    financialPlanBtn.attr("href","/products/list-all-all-investing-1-createTimeAsc-1.html")
                }
            }else{
                financialPlanBtn.attr("href","/products/list-all-all-investing-1-createTimeAsc-1.html")
            }
        }
    });
})();