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
    logined = $('#j-newyear').data('logined')

  Vue.config.devtools = true

  window.vm = new Vue({
    el: '#j-newyear',
    data: {
      // 后端接口地址
      urls: {
        init: path + '/activity/newyear/init',
        receiveCoupon: path + '/activity/dynamicInvoke',
        receiveReward: path + '/activity/dynamicInvoke'
      },
      logined: os > 2 ? logined : hook.logined,
      loginUrl: path + '/mstation/login?from=' + location.href,
      initData: {},
      newMan: false,
      backMan: false,
      finishMan: false,
      defeatMan: false,
      show: true,
      dialog: false,
      dialog1: false,
      dialog2: false,
      dialog3: false,
      gray: false,
      gray2: false,
      happen: false,
      friend: false,
      activityOver: false,
      selectTab: 1,                      //切换

      //提示框
      dialogTips: '',                    //提示信息
      dialogTips2: '',
      targetUrls: '',                    //跳转路径
      dialogOther: '',                   //链接文字
      door: false,                       //蒙层

    },
    created: function () {
      var self = this;
      var activityForNewYear = receiveData.activityForNewYear;
      self.initData = {
        status: receiveData.status,
        newDate: +environment.serverDate,
        startTime: receiveData.startTime,
        endTime: receiveData.endTime,
        registerTime: receiveData.registerTime,  //注册时间
        transactionCount: receiveData.transactionCount,  //十月后投资数
        referralCount: receiveData.referralCount,  //邀请用户数
        referralTransactionCount: receiveData.referralTransactionCount,  //邀请用户并投资数
        luckyMoney28: receiveData.luckyMoneyTemplateIds.split(',')[0],
        luckyMoney88: receiveData.luckyMoneyTemplateIds.split(',')[1],
        luckyMoney888: receiveData.luckyMoneyTemplateIds.split(',')[2],
        luckyMoney2017: receiveData.luckyMoneyTemplateIds.split(',')[3]
      };
      if (self.initData.status == 6) {
        self.gray = true;
        self.gray2 = true;
        self.activityOver = true;
      }
      window.hookCallback = function (data,eventName) {
        switch (eventName) {
          case 'newYearExchange':
            self.Callback1(data);
            break;
          case 'newYearLuckyMoney':
            // var method = window.eventName.split('&')[1];
            // if ('change' === method) {
            //   self.Callback1(data);
            // }else if ('money' === method) {
            //   self.Callback2(data);
            // }
            self.Callback2(data);
            break
        }
      }
    },
    methods:{
      // tab切换
      goSelectTab: function (tabNum) {
        var body = $('body');
        var icon = $('.m-nav-icon1');
        var self = this;
        self.selectTab = tabNum;
      },
      //查看图标
      look: function ($event) {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true;
          self.dialogTips = '活动未开始';
          self.door = true
        } else {
          if (os > 2) {
            self.goLoginPage();
          } else {
            hook.login($($event.currentTarget));
          }
        }
      },
      //我要特权
      getpower: function ($event) {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true;
          self.dialogTips = '活动未开始';
          self.door = true
        }else if (self.initData.status == 6) {
          return false;
        } else {
          if (!self.logined) {
            if (os > 2) {
              self.goLoginPage();
            } else {
              hook.login($($event.currentTarget));
            }
          } else {
            self.show = false;
            if (self.initData.registerTime > self.initData.startTime) {
              self.newMan = true
            } else if (self.initData.registerTime < self.initData.startTime && self.initData.transactionCount == 0) {
              self.backMan = true
            } else if (self.initData.transactionCount > 0 && self.initData.referralTransactionCount < 5) {
              self.defeatMan = true
            } else if (self.initData.transactionCount > 0 && self.initData.referralTransactionCount >= 5) {
              self.finishMan = true
            }
          }
        }
      },
      //邀请好友
      invite: function () {
        var self = this;
        self.friend = true
        self.door = true
      },
      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl
        return false
      },
      //规则
      rule: function () {
        var self = this;
        self.dialog3 = true;
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
      },
      //兑换方法
      gochange: function (val,$event) {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true;
          self.dialogTips = '活动未开始';
          self.door = true
        } else if (self.initData.status == 6) {
          return false;
        } else {
          if (!self.logined) {
            if (os > 2) {
              self.goLoginPage();
            } else {
              hook.login($($event.currentTarget));
            }
          } else {
            if (os !== 3) {
              hook.getEvent('newYearExchange'
                + '&isNeedRealName=0'
                + '&args_templateid_1_long_' + val, $($event.currentTarget))
              // window.eventName = 'newYearExchange&change'
            }else {
              base.getAPI({
                url: self.urls.receiveCoupon,
                data: {
                  invokeMethod: 'newYearExchange',
                  invokeParameters:'args_templateid_1_long_'+val
                },
                callback: function (data) {
                  self.Callback1(data)
                }
              })
            }
          }
        }
      },
      //领券
      getmoney: function (val,$event) {
        var self = this;
        if (self.initData.newDate < self.initData.startTime) {
          self.dialog = true
          self.door = true
          self.dialogTips = '活动未开始'
        } else if (self.initData.newDate > self.initData.endTime) {
          return false;
        } else {
          if (!self.logined) {
            if (os > 2) {
              self.goLoginPage();
            } else {
              hook.login($($event.currentTarget));
            }
          } else {
            if (os !== 3) {
              hook.getEvent('newYearLuckyMoney'
                + '&isNeedRealName=0'
                + '&args_templateid_1_long_' + val, $($event.currentTarget))
              // window.eventName = 'newYearLuckyMoney&money'
            }else {
              base.getAPI({
                url: self.urls.receiveCoupon,
                data: {
                  invokeMethod: 'newYearLuckyMoney',
                  invokeParameters: 'args_templateid_1_long_' + val
                },
                callback: function (data) {
                  self.Callback2(data)
                }
              })
            }
          }
        }
      },
      Callback1: function (data) {
        var self = this;
        // alert("datasuccess"+data.success);
        if (data.success) {
          self.dialog1 = true
          self.dialogTips = data.result.couponAmount;
          self.door = true
          self.initData.activityLotteryCount = self.initData.activityLotteryCount - data.result.consume
        } else {
          self.dialog = true
          // console.log(data.resultCodeEump.msg)
          self.dialogTips = '图标数量不足，快去投资获得更多图标吧！'
          self.door = true
        }
      },
      Callback2: function (data) {
        var self = this;
        if (data.success) {
          self.dialog2 = true;
          self.dialogTips = data.result.luckyMoneyAmount;
          self.door = true
        } else {
          self.dialog = true;
          self.door = true
          if (data.resultCodes[0].code == 90709){
            self.dialogTips = '您需要使用掉该面额红包才能再次领取哦'
          }else {
            self.dialogTips = data.resultCodes[0].msg;
          }
        }
      }
    }
  })
})