package com.yourong.api.controller;

import com.yourong.api.service.BalanceService;
import com.yourong.api.service.CouponService;
import com.yourong.api.service.OrderService;
import com.yourong.api.service.TransactionService;
import com.yourong.api.utils.ServletUtil;
import com.yourong.common.constant.Constant;
import com.yourong.common.enums.TypeEnum;
import com.yourong.core.fin.model.Balance;
import com.yourong.core.mc.model.Coupon;
import com.yourong.core.tc.model.biz.MemberTransactionCapital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Enumeration;

/**
 * m站 会员登陆后的操作， 实名认证 *
 * @author pengyong
 */
@Controller
@RequestMapping("mCenter")
public class MstationMemberController extends BaseController{
    @Autowired
    private CouponService couponService;

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private OrderService orderService;
    /**
     * 移动端我的账户
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("home")
    public ModelAndView homeMobile(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        Long memberId = getMember().getId();
        // 取会员余额放进model
        Balance balance = this.balanceService.queryBalance(memberId, TypeEnum.BALANCE_TYPE_PIGGY);
        model.addObject("balance", balance);
        // 投资数据
        MemberTransactionCapital capital = transactionService.getMemberTransactionCapital(memberId);
        model.addObject("capital", capital);
        // 资产总计＝存钱罐余额＋待收本金＋待收收益
        // 待收本金
        BigDecimal receivablePrincipal = capital == null ? BigDecimal.ZERO : capital.getReceivablePrincipal();
        // 待收收益
        BigDecimal receivableInterest = capital == null ? BigDecimal.ZERO : capital.getReceivableInterest();
        // 资产总计
        BigDecimal totalAssets = balance.getBalance().add(receivablePrincipal).add(receivableInterest);
        // 未完成订单数
        //int noPayOrdeCount = orderService.getNoPayOrdeCount(memberId);
        //过滤p2p
        int noPayOrdeCount = orderService.getNoPayOrdeCountFilterP2p(memberId);
        model.addObject("noPayOrdeCount", noPayOrdeCount);
        // 累计收益 = 待收收益 + 存钱罐收益 + 已收收益
        // 已收收益
        BigDecimal receivedInterest = capital == null ? BigDecimal.ZERO : capital.getReceivedInterest();
        // 存钱罐收益
        BigDecimal savingPotEarnig = this.balanceService.queryBalance(memberId,
                TypeEnum.BALANCE_TYPE_MEMBER_TOTAL_PIGGY).getBalance();
        model.addObject("savingPotEarnig", savingPotEarnig.setScale(2, BigDecimal.ROUND_HALF_UP));
        // 累计投资收益
        BigDecimal totalInvestEarnings = receivableInterest.add(receivedInterest);
        model.addObject("totalEarnings", totalInvestEarnings.setScale(2, BigDecimal.ROUND_HALF_UP));
        model.addObject("totalAssets", totalAssets);
       
        model.addObject("receivableInterest", receivableInterest);
        model.addObject("receivablePrincipal", receivablePrincipal);
        
        model.addObject("isDirectProject", transactionService.isMemberDirectInvest(memberId));
        model.setViewName("/mobile/member/home");
        return model;
    }

    /**
     * 第三方支付开通 打开页面
     *
     * @return
     */
    @RequestMapping("sinapay")
    public ModelAndView registerSinapayMobile(HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/login/register-sinapay");
        return model;
    }
    
    /**
     * P2PM站公告
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("directProjectNotice")
    public ModelAndView directProjectNotice(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/activity/nop2p");
        return model;
    }

    /**
     * 注册成功显示页面
     *
     * @return
     */
    @RequestMapping("registerSuccess")
    public ModelAndView registerRewardMobile() {
        ModelAndView modelAndView = new ModelAndView();
        if (ServletUtil.isVerifyTrueName()) {
            modelAndView.setViewName("/mobile/login/register-reward");
            Coupon coupon = couponService.getCouponByMemberIdAndActivity(getMember().getId(), 1L);
            modelAndView.addObject("coupon", coupon);
        } else {
            modelAndView.setViewName("forward:/mstation/tips");
        }
        return modelAndView;
    }


    /**
     * 会员退出
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value = "logout")
    public String logoutMobile(HttpServletRequest req, HttpServletResponse resp) {

        String path = cleanSession(req, resp);
        // 拼接跳转页面路径
        String basePath = req.getScheme() + "://" + req.getServerName() + ":"
                + req.getServerPort() + path + "/mIndex";
        return "redirect:" + basePath;

    }

    /**
     * 清空session里所有的值
     *
     * @param req
     * @param resp
     * @return author: pengyong
     * 上午9:19:49
     */
    private String cleanSession(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession httpSession = getHttpSession(req);
        // 清除session
        Enumeration<String> em = httpSession.getAttributeNames();
        while (em.hasMoreElements()) {
            req.getSession().removeAttribute(em.nextElement().toString());
        }
        httpSession.removeAttribute(Constant.CURRENT_USER);
        httpSession.invalidate();
        String path = req.getContextPath();
        setResponseHeaders(resp);
        return path;
    }
    /**
     * 人气值页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping(value="reputationPage")
    public ModelAndView memberReputation(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/reputationPage");
        return model;
    }
    /**
     * 兑换现金券
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("reputationExchange")
    public ModelAndView memberChoiseReputation(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/reputationExchange");
        return model;
    }
    /**
     * 人气值获得记录
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("reputationRecord")
    public ModelAndView memberReputationRecord(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/reputationRecord");
        return model;
    }
    /**
     * 我的优惠
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("coupon")
    public ModelAndView memberCoupon(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/coupon");
        return model;
    }

    /**
     * 签到页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("checkin")
    public ModelAndView memberCheckin(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/checkin");
        return model;
    }
    /**
     * 邀请好友页面
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("inviteFriend")
    public ModelAndView memberInviteFriend(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/inviteFriend");
        return model;
    }

    /**
     * 添加银行卡
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("bankAdd")
    public ModelAndView bankAdd(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/bankAdd");
        return model;
    }
    /**
     * 银行卡管理
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("bankManage")
    public ModelAndView bankManage(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/bankManage");
        return model;
    }
    /**
     * 我的交易
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("myTransaction")
    public ModelAndView myTransaction(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/myTransaction");
        return model;
    }

    /**
     * 充值
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("recharge")
    public ModelAndView recharge(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/recharge");
        return model;
    }

    /**
     * 提交订单
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("submitOrder")
    public ModelAndView submitOrder(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/submitOrder");
        return model;
    }
    /**
     * 提现
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("withdraw")
    public ModelAndView mWithdrawals(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/withdraw");
        return model;
    }
    /**
     * 订单支付
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("orderPayment")
    public ModelAndView orderPayment(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/orderPayment");
        return model;
    }
    /**
     * 安全认证
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("security")
    public ModelAndView security(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/security");
        return model;
    }

    /**
     * 投资项目
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("invest")
    public ModelAndView invest(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/invest");
        return model;
    }
    /**
     * 个人资料
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("auditMemberInfos")
    public ModelAndView auditMemberInfos(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/auditMemberInfos");
        return model;
    }

    /**
     * 投资完成详情页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("finishInvest")
    public ModelAndView finishInvest(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/finishInvest");
        return model;
    }
    /**
     * 投资完成详情页
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("investmentDetail")
    public ModelAndView investmentDetail(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/investmentDetail");
        return model;
    }
    /**
     * 电子签名
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("eSignature")
    public ModelAndView eSignature(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/eSignature");
        return model;
    }
    /**
     * 未签署项目
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("unSignedProjects")
    public ModelAndView unSignedProjects(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/unSignedProjects");
        return model;
    }
    /**
     * 资金记录
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("capitalInOutLog")
    public ModelAndView capitalInOutLog(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/capitalInOutLog");
        return model;
    }
    /**
     * 支付密码
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("setPayPassword")
    public ModelAndView setPayPassword(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/setPayPassword");
        return model;
    }
    /**
     * 委托支付
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("paymentCipher")
    public ModelAndView paymentCipher(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/paymentCipher");
        return model;
    }
    /**
     * 完成充值
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("afterRechange")
    public ModelAndView afterRechange(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/afterRechange");
        return model;
    }
    /**
     * 完成提现
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("afterWithdraw")
    public ModelAndView afterWithdraw(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/afterWithdraw");
        return model;
    }
    /**
     * 绑定邮箱
     *
     * @param req
     * @param resp
     * @return
     */
    @RequestMapping("bindEmail")
    public ModelAndView bindEmail(HttpServletRequest req, HttpServletResponse resp) {
        ModelAndView model = new ModelAndView();
        model.setViewName("/mobile/member/bindEmail");
        return model;
    }
}
