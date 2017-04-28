##后台JS功能模块用法说明

xform -- 表单处理，$("#demo").xform(action, url, async):
	action值为post、load、get、reset，分别为表单更新(或新增)、加载、获取json、重置，action参数必需，action为post、load和get时，url为必需。
	get为获取json，直接返回数据，而load做了表单加载处理,例如$(".object").xform("get"),返回请求过来的原始json数据。不绑定dom节点
	url值为json的提交链接
	async值为bool，true或false，默认是异步true

setCategory -- 分类目录弹框选择组件，点击输入框后弹出分类选择树，选择某个分类后id(cid)和name(value)返回给输入框。
	用法：$("#inputCategory").setCategory(obj,data); obj为分类树的id或class，data为json数据，格式如下，
		{ "id":"1", "pId":"0", "name":"新闻公告", "open":"true"}，pId为父级id，用于树形显示，open为是否展开
		必须先加载/common/modules/categorySelector.vm
