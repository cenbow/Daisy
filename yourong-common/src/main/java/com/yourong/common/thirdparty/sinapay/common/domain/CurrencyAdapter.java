package com.yourong.common.thirdparty.sinapay.common.domain;

import java.util.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author daisy
 * make currency performed correctly
 *
 */
public class CurrencyAdapter extends XmlAdapter<String, Currency> {
    public Currency unmarshal(String val) throws Exception {
        return Currency.getInstance(val);
    }

    public String marshal(Currency val) throws Exception {
        return val.toString();
    }
}
