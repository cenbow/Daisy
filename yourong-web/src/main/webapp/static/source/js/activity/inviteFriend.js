/**
 * Created by XR on 2017/3/7.
 */
(function () {

  var path = environment.globalPath

  var vm = new Vue({
    el: '#j-inviteFriend',
    data: {
      urls: {
        init: path + '/activity/inviteFriend/init',                  //页面初始化接口
        receive: path + '/activity/inviteFriend/detail',                  //页面初始化接口
      },

      init: {},
      logined: logined,
      friend: false,
      myLotteryList: [],
      whiteCoverShow: false,
      isShowTips: false,
      tipsList:['','活动即将开始','活动已结束'],
      tipsIndex: 0,

      coverShow: false,          //黑色蒙层
      showDialog: false,         //是否显示弹框
      pages: {                  //我的中奖记录分页
        max: 1,
        min: 1,
        pageNo: 1,
        totalPageCount: 0
      }
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
      // 用虚位以待填充
      fillLuckyMemberList: function (arr) {
        if(0 < arr.length && arr.length < 8){
          var arrLength = arr.length
          for (var i = 0; i < 8 - arrLength; i++) {
            arr.push({
              username : "邀友风云榜，等你来上榜",
              avatars : ''
            })
          }
        }
        return arr
      },
      // 初始化数据
      initData: function (result) {
        var self = this
        self.init = {
          newDate: env.serverDate,
          startDate: result.startDate || '',   //活动开始时间
          endDate: result.endDate || '',       //活动结束时间
          status: result.status || 2,          //活动状态
          inviteFriendLists: result.inviteFriendLists || [],       //风云榜
          inviteCount: result.inviteCount || 0,       //邀请人数
          invest: result.invest || 0,       //累计投资额
          reward: result.reward || 0       //奖金
        };

        //列表小于8条时用虚位以待填充
        self.init.inviteFriendLists = self.fillLuckyMemberList(self.init.inviteFriendLists)
      },
      // 去登陆
      goLoginPage: function () {
        $.xPost({url: this.urls.receive})
      },
      // 邀请好友
      invite: function () {
        var self = this;
        if (self.init.newDate < self.init.startDate){
          self.openTipsFrame(1)
        }else if (self.init.newDate > self.init.endDate){
          self.openTipsFrame(2)
        }else {
          if (!self.logined) {
            self.goLoginPage();
          }else {
            self.friend = true;
            self.coverShow = true
          }
        }
      },
      close: function () {
        var self = this;
        self.friend = false;
        self.coverShow = false;
      },
      // 打开中奖纪录
      openDialog: function () {
        var self = this
        self.showDialog = true
        self.coverShow = true
        self.awardRecord();
      },
      // 关闭中奖纪录
      closeDialog: function (callback) {
        this.showDialog = false
        this.coverShow = false
        this.pages.pageNo = 1
        // this.sign = ''
        if(callback) {
          window.location.reload()
        }
      },
      //分页
      goPage: function (pageNo) {
        var pages = this.pages,
          self = this
        if (pageNo > 0 && pageNo <= pages.totalPageCount) {
          self.pages.pageNo = pageNo
          self.awardRecord()
        }
      },
      // 中奖纪录
      awardRecord: function () {
        var self = this
        if (!self.logined) {
          self.goLoginPage();
        } else {
          $.xPost({
            url: self.urls.receive,
            data: {
              pageNo: self.pages.pageNo
            },
            callback: function (data) {
              if (data.success) {
                var awardRecord = data.result;
                    self.myLotteryList = awardRecord.data
                    self.pages.pageNo = awardRecord.pageNo
                    self.pages.max = awardRecord.maxPager - awardRecord.minPager + 1
                    self.pages.min = awardRecord.minPager
                    self.pages.totalPageCount = awardRecord.totalPageCount
              }
            }
          })
        }
      }
    }
  })

  // 邀请链接复制
  var clipboard = new Clipboard('#j-copy-invite')
  clipboard.on('success', function () {
    // $.xDialog({
    //   type: 'success',
    //   content: '复制成功'
    // })
    $('.u-vanish-get').removeClass('z-hidden',setTimeout(function(){
      $('.u-vanish-get').addClass('z-hidden')
    },2000));
  })
})();