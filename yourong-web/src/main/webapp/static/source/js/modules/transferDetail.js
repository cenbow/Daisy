(function () {
  loadTransferByProjectId(productConfig.transferId);
  function loadTransferByProjectId(transferId){
    $("#J_TranferRecord").loading().load(environment.globalPath +
    "/projectTransaction/detail/transferTransactions?projectId="+transferId);
  }
})();
