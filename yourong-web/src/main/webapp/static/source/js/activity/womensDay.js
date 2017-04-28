/**
 * Created by XR on 2017/2/22.
 */
(function () {
  $("#j-womensDay").snowfall({
    image: '/static/img/activity/womensDay/star.png',
    flakeCount: 20,
    minSize: 20,
    maxSize: 30
  });
  if (isDev) {
    Vue.config.devtools = true
  }

  var path = environment.globalPath;

  var vm = new Vue({
    el: '#j-womensDay',
    data: {
      urls: {
        init: path + '/activity/womensDay/init',                  //页面初始化接口
        receive: path + '/activity/womensDay/bag'            //领取福袋接口
      },

      init: {},
      logined: logined,
      tipsShow: false,      //是否显示非模态提示框
      tipsType: 0,          //提示类型
      one: false,
      two: false,
      three: false,
      four: false,
      five: false,
      six: false,
      seven: false,
      eight: false,
      nine: false,
      tipsTextList: [
        '女神，记得3月8号来领取哦',
        '快带女神来领吧~',
        '网络拥挤，过会再来~',
        '女神，你已错过领取哦',
        '活动即将开始'
      ],
      timestamp: new Date().getTime(),
      nosextip: false,
      womentip: false,
      door: false,
      whitedoor: false,
      happend: false,
      timeLate: false
    },
    created: function () {
      var self = this
      $.xPost({
        url: self.urls.init,
        callback: function(data){
          if(data.success){
            self.initData(data.result)
          }else {
            console.log('页面初始化错误',data)
          }
        }
      })
    },
    methods: {
      // 初始化数据
      initData: function (result) {
        var self = this
        self.init = {
          startTime: result.startDate || '',   //活动开始时间
          endTime: result.endDate || '',       //活动结束时间
          signDate: result.signDate || [],      //签到日期
          investment: result.investment || 0,     //我的投资额
          bag: result.bag,
          sex: result.sex,
          womensDate: result.womensDate
        };
        if (!self.init.bag && self.init.sex === 0){
          self.happend = true;
        }
        if (self.timestamp > self.init.endTime){
          self.timeLate = true;
        }
        for (var i=0;i<self.init.signDate.length;i++){
          var arr = self.init.signDate[i];
          var n = 24*60*60*1000;
          switch (arr){
            case self.init.startTime:
                self.one = true;
              break;
            case self.init.startTime + n:
                self.two = true;
              break;
            case self.init.startTime + n*2:
              self.three = true;
              break;
            case self.init.startTime + n*3:
              self.four = true;
              break;
            case self.init.startTime + n*4:
              self.five = true;
              break;
            case self.init.startTime + n*5:
              self.six = true;
              break;
            case self.init.startTime + n*6:
              self.seven = true;
              break;
            case self.init.startTime + n*7:
              self.eight = true;
              break;
            case self.init.startTime + n*8:
              self.nine = true;
              break;
          }
        }
      },
      // 去登陆
      goLoginPage: function () {
        $.xPost({url: this.urls.receive})
      },
      // 非模态提示框
      openTipsFrame: function(num) {
        var self = this
        self.tipsType = num
        self.tipsShow = true
        setTimeout(function(){
          self.tipsShow = false
        },2000)
      },

      //日期格式化
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

      //匹配签到日期
      check: function (day) {
        var self = this;
        var num = $.inArray(day, self.init.signDate);
        return num;
      },

      //关闭弹框
      close: function () {
        var self = this;
        self.door = false;
        self.womentip = false;
        self.nosextip = false;
      },

      //领取礼包
      getbag: function () {
        var self = this;
        if (self.timestamp < self.init.startTime){
          self.openTipsFrame(4)
        }else {
          if (!self.logined) {
            self.goLoginPage();
          } else {
            if (self.timestamp < new Date(self.init.womensDate+' 00:00:00').getTime()) {
              if (self.init.sex == 0){
                self.openTipsFrame(0)
              }else if (self.init.sex == 1){
                self.openTipsFrame(1)
              }else {
                self.door = true;
                self.nosextip = true;
              }
            }else if (self.timestamp > new Date(self.init.womensDate+' 23:59:59').getTime()) {
              self.openTipsFrame(3)
            }else {
              self.whitedoor = true;
              $.xPost({
                url: self.urls.receive,
                callback: function (data) {
                  self.whitedoor = false;
                  if (data.success) {
                    self.door = true;
                    self.womentip = true;
                    self.happend = true;
                  } else {
                    if (data.resultCodeEum[0].code == 94012){
                      self.openTipsFrame(1)
                    }else if (data.resultCodeEum[0].code == 94013){
                      self.door = true;
                      self.nosextip = true;
                    }else {
                      self.openTipsFrame(2)
                    }
                  }
                }
              })
            }
          }
        }
      }
    }
  })

})()
