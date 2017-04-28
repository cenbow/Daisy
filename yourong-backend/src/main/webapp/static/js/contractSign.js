jQuery(function($) {

	var contractTable = $('#contract-table').dataTable({
			'bFilter': false,
			'bProcessing': true,
			'bSort': true,
			'bServerSide': true,
			'fnServerParams': function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource': config.urlMap.ajax,
			'aoColumns' : [
				{
					'mDataProp' : 'id',
					'bSortable' : false,
					'mRender' : function(data, type, row) {
						return "<input type='checkbox' value=" + row.id + ">";
					}
				}, {
					'mDataProp' : 'projectName',
					'bSortable' : false
				}, {
					'mDataProp' : 'orderNo',
					'bSortable' : false
				}, {
					'mDataProp' : 'memberName',
					'bSortable' : true
				}, {
					'mDataProp' : 'memberMoible',
					'bSortable' : true
				}, {
					'mDataProp' : 'signStatus',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						return getDictLabel(config.sign_status, row.signStatus);
					}
				},{
					'mDataProp' : 'secondStatus',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						return getDictLabel(config.second_status, row.secondStatus);
					}
				}, {
					'mDataProp' : 'contractTime',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						return formatDate(data,'yyyy-mm-dd HH:mm:ss');
					}
				}, {
					'mDataProp' : 'signTime',
					'bSortable' : true,
					'mRender'   : function(data, type, row) {
						if(data){
							return formatDate(data,'yyyy-mm-dd HH:mm:ss');
						}
							return "";
					}
				}, {
					'mDataProp' : 'signWay',
					'bSortable' : true,
					'mRender'   : function(data, type, row) {
						if(data==0){
							return"手动";
						}
						if(data==1){
							return"自动";				
						}
					}
				}]
		});
	
	$('#query_contract').on('click', function() {
		contractTable.fnDraw();
	});
	

});

