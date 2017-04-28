

$("#j-eSignature").on('click',function(){

    var t=$(".j-contract-box").data("transationid")
    $.xPost({
        url: environment.globalPath + "/transaction/signContract",
        data:{transactionId:t},
        callback: function (data) {
            if (data.success) {
                window.location.href=data.result
            }else{
                $("#j-shade-contract").removeClass("f-dn")
                $("#j-contract-tips").removeClass("f-dn").find("p").text(data.resultCodeEum[0].msg)
            }
        }
    })

})
$("#j-contract-download").on('click',function(){
    $("#j-download-box").removeClass("f-dn")
    $("#j-shade-contract").removeClass("f-dn")
})
$("#j-download-box").on('click','span',function(){
    var t=$(".j-contract-box").data("transationid")
    $.xPost({
        url: environment.globalPath + "/transaction/getContractDownUrl",
        data:{transactionId:t},
        callback: function (data) {
            if (data.success) {
                window.location.href=data.result
            }else{
                $("#j-download-box").addClass('f-dn')
                $("#j-contract-tips").removeClass('f-dn').find("p").text(data.resultCodeEum[0].msg)
            }
        }
    })
})
$("#j-download-box").on('click','i',function(){
    $("#j-download-box").addClass("f-dn")
    $("#j-shade-contract").addClass("f-dn")
})
$("#j-contract-tips").on('click','i,span',function(){
    location.reload();
    window.location.href="#bottom"

})
