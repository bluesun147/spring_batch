package com.example.demo.custom;

import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// BeanWrapperFieldExtractor과 똑같은 내용 - 커스텀 할 수 있다는것 보여주기 위해
// but 커스텀은 processor에서 하는 것 권장!

public class CustomBeanWrapperFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
    private String[] names;

    public void setNames(String[] names) {
        Assert.notNull(names, "Names must be non-null");
        this.names = (String[]) Arrays.asList(names).toArray(new String[names.length]);
    }

    public Object[] extract(T item) {
        List<Object> values = new ArrayList();
        BeanWrapper bw = new BeanWrapperImpl(item);
        String[] var4 = this.names;
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String propertyName = var4[var6];
            values.add(bw.getPropertyValue(propertyName));
        }

        return values.toArray();
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.names, "The 'names' property must be set.");
    }
}
