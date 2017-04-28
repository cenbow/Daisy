package com.yourong.common.converter;

import com.google.common.collect.Sets;
import com.yourong.common.annotation.EncryptionAnnotation;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Set;

/**
 * 解密
 * Created by py on 2015/3/24.
 */
public class EncrptionAnnotationFormatterFactory  implements AnnotationFormatterFactory<EncryptionAnnotation> {
    private final Set<Class<?>> fieldTypes;
    private final DecryptionFormatter formatter;
    public EncrptionAnnotationFormatterFactory() {
        Set<Class<?>> set = Sets.newHashSet();
        set.add(String.class);
        this.fieldTypes = set;
        this.formatter = new DecryptionFormatter();//此处使用之前定义的Formatter实现
    }

    @Override
    public Set<Class<?>> getFieldTypes() {
        return fieldTypes;
    }

    @Override
    public Printer<?> getPrinter(EncryptionAnnotation annotation, Class<?> fieldType) {
        return formatter;
    }
    @Override
    public Parser<?> getParser(EncryptionAnnotation annotation, Class<?> fieldType) {
        return formatter;
    }
}
