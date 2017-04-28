package com.yourong.web.service;


/**
 * Created by Administrator on 2015/2/12.
 */
public interface DouwanService {
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
    public void douwanFirstTransaction(Long id,int totalDyas);

}
