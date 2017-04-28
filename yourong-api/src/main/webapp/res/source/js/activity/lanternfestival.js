/**
 * Created by XR on 2017/2/9.
 */
/**
 * Created by XR on 2017/1/6.
 */
/*global receiveData,environment,os*/
define(['base'], function (require, exports, module) {
  'use strict'
  /* 除掉移动端默认的点击图片出现全图浏览 */
  // $('img').on('click', function (e) {
  //   e.preventDefault()
  // })

  var path = environment.globalPath,
    base = require('base'),
    hook = new AppHook(os),
    logined = $('#j-lanternfestival').data('logined')

  Vue.config.devtools = true

  window.vm = new Vue({
    el: '#j-lanternfestival',
    data: {
      // 后端接口地址
      urls: {
      },
      logined: os > 2 ? logined : hook.logined,
      loginUrl: path + '/mstation/login?from=' + location.href,
      show: true,
      gray: false,
      gray2: false,
      happen: false,
      friend: false,
      activityOver: false,
      dialog: false,
      selectTab: 1,                      //切换
      door: false,                       //蒙层

    },
    created: function () {
    },
    methods:{
      // tab切换
      goSelectTab: function (tabNum) {
        var body = $('body');
        var icon = $('.m-nav-icon1');
        var self = this;
        self.selectTab = tabNum;
      },
      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl
        return false
      },
      //规则
      rule: function () {
        var self = this;
        self.dialog = true;
        self.door = true;
      },
      //关闭弹框
      close: function () {
        var self = this;
        self.dialog = false;
        self.dialog1 = false;
        self.dialog2 = false;
        self.dialog3 = false;
        self.door = false;
        self.friend = false;
      }
    }
  })
})