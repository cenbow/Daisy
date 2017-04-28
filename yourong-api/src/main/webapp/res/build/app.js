/******/ (function(modules) { // webpackBootstrap
/******/ 	// install a JSONP callback for chunk loading
/******/ 	var parentJsonpFunction = window["webpackJsonp"];
/******/ 	window["webpackJsonp"] = function webpackJsonpCallback(chunkIds, moreModules) {
/******/ 		// add "moreModules" to the modules object,
/******/ 		// then flag all "chunkIds" as loaded and fire callback
/******/ 		var moduleId, chunkId, i = 0, callbacks = [];
/******/ 		for(;i < chunkIds.length; i++) {
/******/ 			chunkId = chunkIds[i];
/******/ 			if(installedChunks[chunkId])
/******/ 				callbacks.push.apply(callbacks, installedChunks[chunkId]);
/******/ 			installedChunks[chunkId] = 0;
/******/ 		}
/******/ 		for(moduleId in moreModules) {
/******/ 			modules[moduleId] = moreModules[moduleId];
/******/ 		}
/******/ 		if(parentJsonpFunction) parentJsonpFunction(chunkIds, moreModules);
/******/ 		while(callbacks.length)
/******/ 			callbacks.shift().call(null, __webpack_require__);
/******/
/******/ 	};
/******/
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// object to store loaded and loading chunks
/******/ 	// "0" means "already loaded"
/******/ 	// Array means "loading", array contains callbacks
/******/ 	var installedChunks = {
/******/ 		0:0
/******/ 	};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;
/******/
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/ 	// This file contains only the entry chunk.
/******/ 	// The chunk loading function for additional chunks
/******/ 	__webpack_require__.e = function requireEnsure(chunkId, callback) {
/******/ 		// "0" is the signal for "already loaded"
/******/ 		if(installedChunks[chunkId] === 0)
/******/ 			return callback.call(null, __webpack_require__);
/******/
/******/ 		// an array means "currently loading".
/******/ 		if(installedChunks[chunkId] !== undefined) {
/******/ 			installedChunks[chunkId].push(callback);
/******/ 		} else {
/******/ 			// start chunk loading
/******/ 			installedChunks[chunkId] = [callback];
/******/ 			var head = document.getElementsByTagName('head')[0];
/******/ 			var script = document.createElement('script');
/******/ 			script.type = 'text/javascript';
/******/ 			script.charset = 'utf-8';
/******/ 			script.async = true;
/******/
/******/ 			script.src = __webpack_require__.p + "" + chunkId + ".app.js";
/******/ 			head.appendChild(script);
/******/ 		}
/******/ 	};
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "./res/build/";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';
	
	var _header = __webpack_require__(1);
	
	var _header2 = _interopRequireDefault(_header);
	
	var _footer = __webpack_require__(4);
	
	var _footer2 = _interopRequireDefault(_footer);
	
	var _navi = __webpack_require__(7);
	
	var _navi2 = _interopRequireDefault(_navi);
	
	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }
	
	Vue.config.devtools = true;
	
	var log = console.log.bind(console);
	// 路由器需要一个根组件
	var App = Vue.extend({
	    components: {
	        'app-header': _header2.default,
	        'app-footer': _footer2.default,
	        'app-navi': _navi2.default
	    },
	    props: ['logined', 'siteurl']
	});
	
	var Projects = Vue.extend({
	    template: '<router-view></router-view>'
	});
	var Member = Vue.extend({
	    template: '<router-view></router-view>'
	});
	
	// 创建一个路由器实例
	var router = new VueRouter();
	
	// 定义路由规则
	router.map({
	    '/': {
	        name: 'index',
	        component: function component(resolve) {
	            __webpack_require__.e/* require */(1, function(__webpack_require__) { var __WEBPACK_AMD_REQUIRE_ARRAY__ = [__webpack_require__(10)]; (resolve.apply(null, __WEBPACK_AMD_REQUIRE_ARRAY__));}.bind(this));
	        }
	    },
	    '/projects': {
	        name: 'projects',
	        component: Projects,
	        subRoutes: {
	            '/list': {
	                component: function component(resolve) {
	                    __webpack_require__.e/* require */(2, function(__webpack_require__) { var __WEBPACK_AMD_REQUIRE_ARRAY__ = [__webpack_require__(16)]; (resolve.apply(null, __WEBPACK_AMD_REQUIRE_ARRAY__));}.bind(this));
	                }
	            }
	        }
	    },
	    '/member': {
	        name: 'member',
	        component: Member,
	        subRoutes: {
	            '/home': {
	                component: function component(resolve) {
	                    __webpack_require__.e/* require */(3, function(__webpack_require__) { var __WEBPACK_AMD_REQUIRE_ARRAY__ = [__webpack_require__(22)]; (resolve.apply(null, __WEBPACK_AMD_REQUIRE_ARRAY__));}.bind(this));
	                }
	            }
	        }
	    }
	});
	
	router.beforeEach(function (transition) {
	    var name = transition.to.name;
	
	    if (name) {
	        router.app.$broadcast('currentNavi', name);
	    }
	    transition.next();
	});
	
	router.start(App, '#app');

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	var __vue_script__, __vue_template__
	__vue_script__ = __webpack_require__(2)
	if (__vue_script__ &&
	    __vue_script__.__esModule &&
	    Object.keys(__vue_script__).length > 1) {
	  console.warn("[vue-loader] components/header.vue: named exports in *.vue files are ignored.")}
	__vue_template__ = __webpack_require__(3)
	module.exports = __vue_script__ || {}
	if (module.exports.__esModule) module.exports = module.exports.default
	if (__vue_template__) {
	(typeof module.exports === "function" ? (module.exports.options || (module.exports.options = {})) : module.exports).template = __vue_template__
	}
	if (false) {(function () {  module.hot.accept()
	  var hotAPI = require("vue-hot-reload-api")
	  hotAPI.install(require("vue"), true)
	  if (!hotAPI.compatible) return
	  var id = "/Users/adeweb/ideaWorkspace/trunk/yourong-api/src/main/webapp/res/components/header.vue"
	  if (!module.hot.data) {
	    hotAPI.createRecord(id, module.exports)
	  } else {
	    hotAPI.update(id, module.exports, __vue_template__)
	  }
	})()}

/***/ },
/* 2 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	// <template>
	//     <h2 class="u-pageTitle f-pos-r">
	//         <a v-link="{path:'/'}">{{title}}</a>
	//         <span class="u-login-link f-pos-a" v-if="logined"><a @click="logout">退出</a></span>
	//         <span class="u-login-link f-pos-a" v-else><a href="{{siteurl}}">登录</a><a href="#">注册</a></span>
	//     </h2>
	// </template>
	// <script>
	exports.default = {
	    data: function data() {
	        var self = this;
	        return {
	            siteurl: this.$root.siteurl + '/?spa',
	            title: '有融网',
	            logined: this.$root.logined
	        };
	    },
	
	    methods: {
	        logout: function logout() {
	            var self = this;
	            $.get(self.$root.siteurl + '/security/logout', function (data) {
	                console.log(data);
	            });
	        }
	    }
	};
	// </script>
	/* generated by vue-loader */

/***/ },
/* 3 */
/***/ function(module, exports) {

	module.exports = "\n<h2 class=\"u-pageTitle f-pos-r\">\n    <a v-link=\"{path:'/'}\">{{title}}</a>\n    <span class=\"u-login-link f-pos-a\" v-if=\"logined\"><a @click=\"logout\">退出</a></span>\n    <span class=\"u-login-link f-pos-a\" v-else><a href=\"{{siteurl}}\">登录</a><a href=\"#\">注册</a></span>\n</h2>\n";

/***/ },
/* 4 */
/***/ function(module, exports, __webpack_require__) {

	var __vue_script__, __vue_template__
	__vue_script__ = __webpack_require__(5)
	if (__vue_script__ &&
	    __vue_script__.__esModule &&
	    Object.keys(__vue_script__).length > 1) {
	  console.warn("[vue-loader] components/footer.vue: named exports in *.vue files are ignored.")}
	__vue_template__ = __webpack_require__(6)
	module.exports = __vue_script__ || {}
	if (module.exports.__esModule) module.exports = module.exports.default
	if (__vue_template__) {
	(typeof module.exports === "function" ? (module.exports.options || (module.exports.options = {})) : module.exports).template = __vue_template__
	}
	if (false) {(function () {  module.hot.accept()
	  var hotAPI = require("vue-hot-reload-api")
	  hotAPI.install(require("vue"), true)
	  if (!hotAPI.compatible) return
	  var id = "/Users/adeweb/ideaWorkspace/trunk/yourong-api/src/main/webapp/res/components/footer.vue"
	  if (!module.hot.data) {
	    hotAPI.createRecord(id, module.exports)
	  } else {
	    hotAPI.update(id, module.exports, __vue_template__)
	  }
	})()}

/***/ },
/* 5 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	// <template>
	//     <div class="g-ft">
	//         <div class="u-safety-logo">
	//             <img :src="imgurl.sinapay" width="35%">
	//             <img :src="imgurl.guanghua" width="40%">
	//         </div>
	//         <div class="u-download-btn">
	//             <a href="{{siteurl}}/static/page/twoCodeRedict.html" target="_blank">下载APP</a>
	//             <a href="http://www.yrw.com/?fromMobile">电脑版</a>
	//         </div>
	//         <div class="u-guide-link">
	//             <a href="{{siteurl}}">首页</a>|<a href="{{tipsurl}}">关于有融</a>|<a href="{{tipsurl}}">安全保障</a>|<a href="{{tipsurl}}">帮助中心</a>
	//         </div>
	//         <span class="m-copyright">©2014-2016 YRW.COM 版权所有</span>
	//     </div>
	// </template>
	// <script>
	exports.default = {
	    data: function data() {
	        return {
	            siteurl: this.$root.siteurl,
	            tipsurl: this.$root.siteurl + '/mstation/tips',
	            imgurl: {
	                sinapay: this.$root.siteurl + '/res/img/sinaPay_logo20151118.png',
	                guanghua: this.$root.siteurl + '/res/img/logo_ghkj20160411.png'
	            }
	        };
	    }
	};
	// </script>
	/* generated by vue-loader */

/***/ },
/* 6 */
/***/ function(module, exports) {

	module.exports = "\n<div class=\"g-ft\">\n    <div class=\"u-safety-logo\">\n        <img :src=\"imgurl.sinapay\" width=\"35%\">\n        <img :src=\"imgurl.guanghua\" width=\"40%\">\n    </div>\n    <div class=\"u-download-btn\">\n        <a href=\"{{siteurl}}/static/page/twoCodeRedict.html\" target=\"_blank\">下载APP</a>\n        <a href=\"http://www.yrw.com/?fromMobile\">电脑版</a>\n    </div>\n    <div class=\"u-guide-link\">\n        <a href=\"{{siteurl}}\">首页</a>|<a href=\"{{tipsurl}}\">关于有融</a>|<a href=\"{{tipsurl}}\">安全保障</a>|<a href=\"{{tipsurl}}\">帮助中心</a>\n    </div>\n    <span class=\"m-copyright\">©2014-2016 YRW.COM 版权所有</span>\n</div>\n";

/***/ },
/* 7 */
/***/ function(module, exports, __webpack_require__) {

	var __vue_script__, __vue_template__
	__vue_script__ = __webpack_require__(8)
	if (__vue_script__ &&
	    __vue_script__.__esModule &&
	    Object.keys(__vue_script__).length > 1) {
	  console.warn("[vue-loader] components/navi.vue: named exports in *.vue files are ignored.")}
	__vue_template__ = __webpack_require__(9)
	module.exports = __vue_script__ || {}
	if (module.exports.__esModule) module.exports = module.exports.default
	if (__vue_template__) {
	(typeof module.exports === "function" ? (module.exports.options || (module.exports.options = {})) : module.exports).template = __vue_template__
	}
	if (false) {(function () {  module.hot.accept()
	  var hotAPI = require("vue-hot-reload-api")
	  hotAPI.install(require("vue"), true)
	  if (!hotAPI.compatible) return
	  var id = "/Users/adeweb/ideaWorkspace/trunk/yourong-api/src/main/webapp/res/components/navi.vue"
	  if (!module.hot.data) {
	    hotAPI.createRecord(id, module.exports)
	  } else {
	    hotAPI.update(id, module.exports, __vue_template__)
	  }
	})()}

/***/ },
/* 8 */
/***/ function(module, exports) {

	'use strict';
	
	Object.defineProperty(exports, "__esModule", {
	    value: true
	});
	// <template>
	//     <ul class="ui-grid-trisect u-navi">
	//         <li :class="{'z-current ui-border-b':current==='index'}">
	//             <a v-link="{path:'/'}">首页</a>
	//         </li>
	//         <li :class="{'z-current ui-border-b':current==='projects'}">
	//             <a v-link="{path:'/projects/list'}">理财项目</a>
	//         </li>
	//         <li :class="{'z-current ui-border-b':current==='member'}" >
	//             <a v-link="{path:'/member/home'}">我的账户</a>
	//         </li>
	//     </ul>
	// </template>
	// <script>
	exports.default = {
	    data: function data() {
	        var currentNavi = this.$root.currentNavi || 'index';
	        return {
	            current: currentNavi
	        };
	    },
	    events: {
	        'currentNavi': function currentNavi(name) {
	            this.$set('current', name);
	        }
	    }
	};
	// </script>
	/* generated by vue-loader */

/***/ },
/* 9 */
/***/ function(module, exports) {

	module.exports = "\n<ul class=\"ui-grid-trisect u-navi\">\n    <li :class=\"{'z-current ui-border-b':current==='index'}\">\n        <a v-link=\"{path:'/'}\">首页</a>\n    </li>\n    <li :class=\"{'z-current ui-border-b':current==='projects'}\">\n        <a v-link=\"{path:'/projects/list'}\">理财项目</a>\n    </li>\n    <li :class=\"{'z-current ui-border-b':current==='member'}\" >\n        <a v-link=\"{path:'/member/home'}\">我的账户</a>\n    </li>\n</ul>\n";

/***/ }
/******/ ]);
//# sourceMappingURL=app.js.map