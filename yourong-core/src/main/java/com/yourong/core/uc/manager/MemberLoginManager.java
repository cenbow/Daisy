package com.yourong.core.uc.manager;

import com.yourong.common.exception.ManagerException;
import com.yourong.core.uc.model.MemberLogin;

public interface MemberLoginManager {
    int insert(MemberLogin record) throws ManagerException;
}
