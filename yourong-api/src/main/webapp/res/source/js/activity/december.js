/**
 * Created by Administrator on 2016/12/9.
 */
define(['base'], function (require, exports, module) {
  'use strict'
  var path = environment.globalPath,
    logined = $('#j-total').data('logined'),
    base = require('base'),
    hook = new AppHook(os),
    encryptionId = base.getUrlParam('encryptionId') || ''
  Vue.config.devtools = true

  /* 除掉移动端默认的点击图片出现全图浏览 */
  // $('img').on('click', function (e) {
  //   e.preventDefault()
  // })

  window.vm = new Vue({
    el: '#j-total',
    data: {
      // 后端接口地址
      urls: {
        receiveReward: path + '/activity/dynamicInvoke/'
      },
      loginUrl: path + '/mstation/login?from=' + location.href,
      encryptionId: encryptionId,
      logined: os > 2 ? logined : hook.logined,
      list: [],
      list1: [],
      list2: [],
      startTime: 0,
      endTime: 0,
      success: 0,
      ten: 0,
      fifTeen: 0,
      myInvestAmount: 0,
      activityStatus: 0,
      show: 0,
      current: 1,
      activeTab: 1,
      more: 0,
      more1: 0,
      couponAmountnum: '',
      date: new Date().getTime(),
      activetip: '',
      hours: '',
      minutes: '',
      seconds: '',
      tenMinuteStatus: false,
      startGrabCash: false,
      startCountDown: false,
      nextfifteen: false,
      newdate: '',
      startDateA: '',
      startDateM: '',
      curTime: '',
      startTimeA: '',
      startTimeM: '',
      can: 0,
      click: true,
      startactive: false,
      endactive: false,
      redRemind: 0,
      look: true
    },
    ready: function () {
      var self = this
      console.log(logined);
      self.success = receiveData.success
      if (receiveData.success) {
        var result = receiveData.result
        self.list = result.firstInvestAmount
        self.list1 = result.everyDayFirstInvestAmount
        self.list2 = result.countFirstAmountNumber
        self.endTime = result.endTime
        self.startTime = result.startTime
        self.activityStatus = result.status
        self.myInvestAmount = result.myInvestAmount || 0
        self.ten = result.ten
        self.fifTeen = result.fifTeen
        self.totalRed = result.totalRed
        self.redRemind = result.redRemind
        if (result.everyDayFirstInvestAmount.length > 0) {
          self.nextday = result.everyDayFirstInvestAmount[result.everyDayFirstInvestAmount.length - 1].dayTime + 24 * 60 * 60 * 1000
        }
        self.newdate = +environment.serverDate;
        self.startDateM = self.formatTime(self.newdate, 'yyyy/M/d') + " " + self.ten + ":00:00";
        self.startDateA = self.formatTime(self.newdate, 'yyyy/M/d') + " " + self.fifTeen + ":00:00";
        self.curTime = self.getSeconds(new Date(self.newdate).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        self.startTimeM = self.getSeconds(new Date(self.startDateM).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        console.log(self.startTimeM)
        self.startTimeA = self.getSeconds(new Date(self.startDateA).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
      }
      if (self.list2.length > 8) {
        self.more = 1;
      }
      if (self.list1.length > 8) {
        self.more1 = 1
      }
      if ((self.curTime < self.startTimeM) || (self.activityStatus == 2)) {
        self.can = 1
      }
      // if (self.curTime > self.startTimeM && self.curTime < self.endTime) {
      //   self.click = false;
      // }
    },
    created: function () {
      var self = this;
      window.hookCallback = function (data) {
        //console.log('data:', data)
        self.Callback(data)
      }
    },
    computed: {
      // 抢红包倒计时
      TimeCount: function () {
        var activetip = "";
        var self = this;
        var seconds = 0;
        var flag = false;
        // var newdate = +environment.serverDate;
        // var startDateM = self.formatTime(newdate, 'yyyy/M/d') + " 10:00:00";
        // var startDateA = self.formatTime(newdate, 'yyyy/M/d') + " 15:00:00";
        // var curTime = self.getSeconds(new Date(newdate).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        // var startTimeM = self.getSeconds(new Date(startDateM).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        // var startTimeA = self.getSeconds(new Date(startDateA).toJSON().split(/[TZ]/)[1]) + 3600 * 8;

        //活动未开始
        if (self.activityStatus == 2) {
          //activetip = "活动即将开始"
          self.startactive = true;
        } else if (self.activityStatus == 6) {
          //activetip = "活动已结束"
          self.endactive = true;
          self.click = true;
        } else {
          self.startactive = false;
          self.endactive = false;
          //早上活动开始
          if (self.formatTime(self.newdate, 'h') >= 0 && self.formatTime(self.newdate, 'h') < 8){
            self.startCountDown = true;
          }else {
            if (self.curTime < self.startTimeM && self.startTimeM - self.curTime > 600) {
              //activetip = "每日10点、15点整点开抢"
              self.startCountDown = true;
              var extraTime = 10 * 60;
              var extraSeconds = self.startTimeM - self.curTime - extraTime;
              flag = false

              var exTimer = setInterval(function () {
                if (extraSeconds > 0) {
                  extraSeconds--
                } else {
                  clearInterval(exTimer);
                  seconds = 600;
                  self.startCountDown = false;
                  self.tenMinuteStatus = true;
                  self.countdown(parseInt(seconds))
                }
              }, 1000)

            } else if (self.curTime < self.startTimeM && self.startTimeM - self.curTime < 600) {
              seconds = self.startTimeM - self.curTime;
              self.countdown(parseInt(seconds));
              self.tenMinuteStatus = true;
            }
            if (self.curTime > self.startTimeM && self.startTimeA - self.curTime > 600 && self.redRemind > 0) {
              self.startGrabCash = true;
              self.click = false;
              //activetip = "点我赢红包大礼"
              var extraTime = 10 * 60;
              var extraSeconds = self.startTimeA - self.curTime - extraTime;
              flag = false;

              var exTimer = setInterval(function () {
                if (extraSeconds > 0) {
                  extraSeconds--
                } else {
                  clearInterval(exTimer);
                  seconds = 600;
                  self.startCountDown = false;
                  self.tenMinuteStatus = true;
                  self.startGrabCash = false;
                  self.countdown(parseInt(seconds))
                  self.click = true;
                }
              }, 1000)
            } else if (self.curTime > self.startTimeM && self.startTimeA - self.curTime > 600 && self.redRemind == 0) {
              self.nextfifteen = true;
              //activetip = "下一场：15点开抢"
              var extraTime = 10 * 60;
              var extraSeconds = self.startTimeA - self.curTime - extraTime;
              flag = false;

              var exTimer = setInterval(function () {
                if (extraSeconds > 0) {
                  extraSeconds--
                } else {
                  clearInterval(exTimer);
                  seconds = 600;
                  self.startCountDown = false;
                  self.tenMinuteStatus = true;
                  self.nextfifteen = false;
                  self.countdown(parseInt(seconds));
                  self.click = true;
                }
              }, 1000)
            } else if (self.curTime < self.startTimeA && self.startTimeA - self.curTime < 600) {
              seconds = self.startTimeA - self.curTime;
              self.countdown(parseInt(seconds));
              self.tenMinuteStatus = true;
              self.click = true;
              //activetip = "开抢倒计时：09:08:25"
            } else if (self.curTime > self.startTimeA && self.redRemind > 0) {
              self.startGrabCash = true;
              self.click = false;
              //activetip = "点我赢红包大礼"
            } else if (self.curTime > self.startTimeA && self.redRemind == 0) {
              self.startCountDown = true;
              //activetip = "每日10点、15点整点开抢"
            }
          }
        }

        return true
      }
    },
    methods: {
      start: function (event) {
        console.log(new Date().getTime())
        var self = this;
        window.scrollTo(0, 0)
        if (!self.logined) {
          if (os < 3) {
            hook.login($(event.currentTarget))
          } else {
            self.goLoginPage()
          }
        }
      },
      //倒计时
      fixZero: function (num) {
        return +num < 10 ? '0' + num : num
      },
      countdown: function (seconds) {
        var sec = seconds - 1,
          self = this

        count()

        var timer = setInterval(function () {

          if (sec > 0) {
            count()
          } else {
            clearInterval(timer);
            self.startGrabCash = true;
            self.tenMinuteStatus = false;
            self.click = false;
          }
        }, 1000)

        function count() {
          var hours = parseInt(sec / 3600),
            minutes = parseInt((sec - hours * 3600) / 60)
          self.$set('hours', self.fixZero(hours))
          self.minutes = self.fixZero(minutes)
          self.seconds = self.fixZero(sec - hours * 3600 - minutes * 60)
          sec--
        }
      },
      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl
        return false
      },
      traggleBtn: function (type) {
        var self = this;
        self.current = type;
        self.activeTab = type;
      },
      readmore: function () {
        var self = this;
        self.more = 0
      },
      readmore1: function () {
        var self = this;
        self.more1 = 0
      },
      rule: function () {
        var self = this;
        self.look = false;
        window.scrollTo(0, 0)
        // $(".u-rule-content").show();
        // base.cover.show();
      },
      know: function () {
        // $(".u-rule-content").hide();
        // base.cover.hide();
        var self = this;
        self.look = true;
      },
      hide: function () {
        $(".m-december-tip").hide();
      },
      formatTime: function (date, format) {
        date = new Date(date);

        var map = {
          "M": date.getMonth() + 1, //月份
          "d": date.getDate(), //日
          "h": date.getHours(), //小时
          "m": date.getMinutes(), //分
          "s": date.getSeconds(), //秒
          "q": Math.floor((date.getMonth() + 3) / 3), //季度
          "S": date.getMilliseconds() //毫秒
        };
        format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
          var v = map[t];
          if (v !== undefined) {
            if (all.length > 1) {
              v = '0' + v;
              v = v.substr(v.length - 2);
            }
            return v;
          }
          else if (t === 'y') {
            return (date.getFullYear() + '').substr(4 - all.length);
          }
          return all;
        });

        return format;
      },
      getSeconds: function (timeString) {
        var list = timeString.split(':')
        return list[0] * 3600 + list[1] * 60 + Math.ceil(list[2] * 1)
      },
      grabred: function (event) {
        var self = this;
        if (!self.logined && self.click == false) {
          console.log('app');
          if (os < 3) {
            hook.login($(event.currentTarget))
          } else {
            self.goLoginPage()
          }
        } else {
          if (self.click || (self.activityStatus == 2) || (self.activityStatus == 6)) {
            return false;
          } else {
            if (os != 3) {
              hook.getEvent('doubleReceiveCoupon' + '&isNeedRealName=0', $(event.currentTarget))
            } else {
              base.getAPI({
                url: self.urls.receiveReward,
                data: {
                  invokeMethod: 'doubleReceiveCoupon'
                },
                callback: function (data) {
                  self.Callback(data)
                }
              })
            }
          }
        }
      },
      Callback: function (data) {
        var self = this;
        if (data.success) {
          if (data.result.redRemind > 0) {
            self.couponAmountnum = data.result.couponAmount
            $("#u-success").fadeIn();
          } else if (data.result.redRemind == 0 && (self.curTime < self.startTimeA)) {
            self.couponAmountnum = data.result.couponAmount
            $("#u-success").fadeIn();
            self.nextfifteen = true;
            self.startGrabCash = false;
            self.click = true;
          } else if (data.result.redRemind == 0 && (self.curTime > self.startTimeA)) {
            self.couponAmountnum = data.result.couponAmount
            $("#u-success").fadeIn();
            self.startCountDown = true;
            self.startGrabCash = false;
            self.click = true;
          }
        } else {
          if (data.resultCodes[0].code == 94005) {
            base.xTips({
              time: 2000,
              content: "每日限抢一次哦"
            })
          } else if (data.resultCodes[0].code == 94003 && (self.curTime < self.startTimeA)) {
            self.nextfifteen = true;
            self.startGrabCash = false;
            self.click = true;
            base.xTips({
              time: 2000,
              content: "已抢完"
            })
          } else if (data.resultCodes[0].code == 94003 && (self.curTime > self.startTimeA)) {
            self.startCountDown = true;
            self.startGrabCash = false;
            self.click = true;
            base.xTips({
              time: 2000,
              content: "已抢完"
            })
          } else {
            base.xTips({
              time: 2000,
              content: "网络拥挤，过会再来哦"
            })
          }
        }
      }
    }
  })
})