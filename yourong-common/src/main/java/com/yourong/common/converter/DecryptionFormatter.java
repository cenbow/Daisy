package com.yourong.common.converter;

import com.yourong.common.util.AES;
import com.yourong.common.util.PropertiesUtil;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

/**
 * 解密
 * Created by py on 2015/3/24.
 */
public class DecryptionFormatter implements Formatter<String> {
    @Override
    public String parse(String text, Locale locale) throws ParseException {
       return PropertiesUtil.isAppNoEncrption()  ? text : AES.getInstance().decrypt(text);

    }

    @Override
    public String print(String object, Locale locale) {
        return PropertiesUtil.isAppNoEncrption() ? object : AES.getInstance().decrypt(object);
    }
}
