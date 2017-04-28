package com.yourong.core.mc.manager;
public interface DouwanManager {
    /**
     * 都玩实名认证后回调
     * @param id
     */
    public void douwanRegisteredCallBack(Long id);
    /**
       * 都玩Email绑定回调
     * @param id
     */
    public void douwanEmailBingCallBack(Long id);
    /**
     * 都玩首次投资回调
     * @param id
     */
    public void douwanFirstTransaction(Long id,int totalDays);

}