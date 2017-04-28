package com.yourong.core;

import com.google.common.eventbus.EventBus;
import com.yourong.core.uc.dao.MemberMapper;
import com.yourong.core.uc.model.Member;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2015/7/7.
 */
public class GuavaEventTest extends  BaseTest {
//    @Autowired
//    private AsyncEventBusAdapter asyncEventBusAdapter;
//    @Autowired
//    private EventBusFactoryBean eventBusFactoryBean;
    @Autowired
	private MemberMapper memberMapper;

//    @Autowired
//    private  EventBusAdapter eventBus;

     @Autowired
   private  EventBus eventBus;

    @Test
    public void testPostEvent() {
        Member member = memberMapper.selectByPrimaryKey(110800000200L);
     //   RegisterEventObject object = new RegisterEventObject(member);
        //MemberRegisterListener listener = new MemberRegisterListener();
      //  eventBus.register(listener);
        eventBus.post(member);
        System.out.println("事件发布完成");
    }

}
