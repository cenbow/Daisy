/**
 * Created by XR on 2017/3/22.
 */
(function () {

  var path = environment.globalPath

  var vm = new Vue({
    el: '#j-goodSpring',
    data: {
      urls: {
        init: path + '/activity/springComing/init',                  //页面初始化接口
        receive: path + '/activity/springComing/receive',                  //页面初始化接口
      },

      init: {},
      logined: logined,
      whiteCoverShow: false,
      isShowTips: false,
      tipsList:['','活动即将开始','活动已结束','您已经领取过了哦'],
      tipsIndex: 0,
      rewardIndex: 0,

      coverShow: false,          //黑色蒙层
      showDialog: false,         //是否显示弹框
      nocheck: false
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
      });
    },
    methods: {
      // 非模态提示框
      openTipsFrame: function(num) {
        var self = this
        self.tipsIndex = num
        self.isShowTips = true
        setTimeout(function(){
          self.isShowTips = false
        },2000)
      },
      // 初始化数据
      initData: function (result) {
        var self = this
        self.init = {
          newDate: env.serverDate,
          startDate: result.startDate || '',   //活动开始时间
          endDate: result.endDate || '',       //活动结束时间
          status: result.status || 2,          //活动状态
          couponList: result.couponList || [],     //券
          template: result.template || 0,          //领过的券ID
          totalAmount: result.totalAmount || 0,    //投资额
          pacNum: result.pacNum || 0         //如意包
        };
      },
      // 去登陆
      goLoginPage: function () {
        $.xPost({url: this.urls.receive})
      },
      checkin: function () {
        var self = this;
        if (!self.logined) {
          self.goLoginPage()
        } else {
          setTimeout(function(){
            self.nocheck = false
          },2000)
        }
      },
      close: function () {
        var self = this;
        self.coverShow = false;
        self.showDialog = false;
        self.nocheck = false;
      },
      getmoney: function (num) {
        var self = this;
        if (self.init.status == 2){
          self.openTipsFrame(1)
        }else if (self.init.status == 6){
          self.openTipsFrame(2)
        }else {
          if (!self.logined) {
            self.goLoginPage()
          } else {
            if(self.init.template){
              return false;
            }else {
              self.whiteCoverShow = true;
              $.xPost({
                url: self.urls.receive,
                data: {
                  templateId: self.init.couponList[num].id
                },
                callback: function (data) {
                  if (data.success){
                    self.coverShow = true;
                    self.showDialog = true;
                    self.rewardIndex = num;
                    self.init.template = self.init.couponList[num].id;
                  }else{
                    switch (data.resultCodeEum[0].code) {
                      case "90703":
                        self.openTipsFrame(3);
                        break;
                      case "94016":
                        self.coverShow = true;
                        self.nocheck = true;
                        break;
                      default:
                        break;
                    }
                  }
                  self.whiteCoverShow = false;
                }
              })
            }
          }
        }
      }
    }
  })

})();