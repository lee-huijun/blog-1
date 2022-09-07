package com.minzheng.blog.config;

import cn.hutool.core.util.StrUtil;
import com.minzheng.blog.entity.TbReceviver;
import com.minzheng.blog.entity.WeatherInfo;
import com.minzheng.blog.util.GaodeUtil;
import com.minzheng.blog.util.RandomAncientPoetry;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;

import java.lang.reflect.Field;
import java.util.List;

import static com.minzheng.blog.util.GaodeUtil.getAdcCode;

/**
 * 醉后不知天在水，满船清梦压星河。
 *
 * @author lhj
 * @date 2022/09/06 23:59
 **/
//工具类
public class TemplateDataBuilder {
    //以下是微信模板工具方法
    private String name;
    private String value;
    private String color;

    public static TemplateDataBuilder builder() {
        return new TemplateDataBuilder();
    }
    public TemplateDataBuilder name(String name) {
        this.name = name;
        return this;
    }
    public TemplateDataBuilder value(String value) {
        this.value = value;
        return this;
    }
    public TemplateDataBuilder color(String color) {
        this.color = color;
        return this;
    }
    public WxMpTemplateData build() {
        if (StrUtil.hasEmpty(name, value)) {
            throw new IllegalArgumentException("参数不正确");
        }
        WxMpTemplateData data = new WxMpTemplateData();
        data.setName(name);
        data.setValue(value);
        data.setColor(color);
        return data;
    }
    //微信模板工具方法
    public static List<WxMpTemplateData> buildData(TbReceviver friend) {
        WeatherInfo weather = GaodeUtil.getNowWeatherInfo(getAdcCode(friend.getProvince(), friend.getCity()));
        RandomAncientPoetry.AncientPoetry ancientPoetry = RandomAncientPoetry.getNext();
        return List.of(
                TemplateDataBuilder.builder().name("name").value(friend.getName()).color("#D91AD9").build(),
                TemplateDataBuilder.builder().name("age").value(friend.getAge().toString()).color("#F77234").build(),
                TemplateDataBuilder.builder().name("howLongLived").value(friend.getHowLongLived()).color("#437004").build(),
                TemplateDataBuilder.builder().name("nextBirthday").value(friend.getNextBirthdayDays()).color("#771F06").build(),
                TemplateDataBuilder.builder().name("nextMemorialDay").value(friend.getNextMemorialDay()).color("#551DB0").build(),
                TemplateDataBuilder.builder().name("province").value(friend.getProvince()).color("#F53F3F").build(),
                TemplateDataBuilder.builder().name("city").value(friend.getCity()).color("#FADC19").build(),
                TemplateDataBuilder.builder().name("weather").value(weather.getWeather()).color("#00B42A").build(),
                TemplateDataBuilder.builder().name("temperature").value(weather.getTemperature()).color("#722ED1").build(),
                TemplateDataBuilder.builder().name("winddirection").value(weather.getWinddirection()).color("#F5319D").build(),
                TemplateDataBuilder.builder().name("windpower").value(weather.getWindpower()).color("#3491FA").build(),
                TemplateDataBuilder.builder().name("humidity").value(weather.getHumidity()).color("#F77234").build(),
                TemplateDataBuilder.builder().name("author").value(ancientPoetry.getAuthor()).color("#F53F3F").build(),
                TemplateDataBuilder.builder().name("origin").value(ancientPoetry.getOrigin()).color("#F53F3F").build(),
                TemplateDataBuilder.builder().name("content").value(ancientPoetry.getContent()).color("#F53F3F").build()
        );
    }

    //定时任务工具方法
    public static Object getProperty(Object obj, String name) throws NoSuchFieldException {
        Object value = null;
        Field field = findField(obj.getClass(), name);
        if (field == null) {
            throw new NoSuchFieldException("no such field [" + name + "]");
        }
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            value = field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(accessible);
        return value;
    }

    public static Field findField(Class<?> clazz, String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException ex) {
            return findDeclaredField(clazz, name);
        }
    }
    public static Field findDeclaredField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredField(clazz.getSuperclass(), name);
            }
            return null;
        }
    }
}
