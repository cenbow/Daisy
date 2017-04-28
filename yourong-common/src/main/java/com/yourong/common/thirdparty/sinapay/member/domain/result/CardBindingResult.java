package com.yourong.common.thirdparty.sinapay.member.domain.result;

/**
 * <p>银行卡绑定结果信息</p>
 * @author Wallis Wang
 * @version $Id: CardBindingResult.java, v 0.1 2014年6月25日 上午10:54:07 wangqiang Exp $
 */
public class CardBindingResult {

    /**
     * 卡ID
     */
    private String  cardId;

    /**
     * 是否已认证
     */
    private boolean verified;

    /**
     * 后续推进需要的参数
     */
    private String ticket;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
