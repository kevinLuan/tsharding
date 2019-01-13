package com.look.cache.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ConfigurationPropertiesPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		ConfigurationProperties annotation = AnnotationUtils.findAnnotation(bean.getClass(),
				ConfigurationProperties.class);
		if (annotation != null) {
			String prefix = annotation.prefix();
			Properties properties = null;
			try {
				properties = loadProperties();
				if (prefix != null) {
					Field[] fields = bean.getClass().getDeclaredFields();
					for (int i = 0; i < fields.length; i++) {
						String fieldName = fields[i].getName();
						String key = prefix + "." + fieldName;
						if (properties.containsKey(key)) {
							String value = properties.getProperty(key);
							if (value != null) {
								setValue(fields[i], value, bean);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return bean;
	}

	private void setValue(Field field, String value, Object bean) {
		setAccessible(field);
		if (isIntType(field)) {
			int val = Integer.parseInt(value.trim());
			try {
				field.set(bean, val);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else if (field.getGenericType() == String.class) {
			try {
				field.set(bean, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("不支持field:" + field.getName() + "->" + field.getGenericType());
		}
	}

	private void setAccessible(Field field) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	private boolean isIntType(Field field) {
		return field.getGenericType() == int.class || field.getGenericType() == Integer.class;
	}

	static Properties loadProperties() throws IOException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		InputStream in = resolver.getResource("classpath:application.properties").getInputStream();
		if (in != null) {
			Properties properties = new Properties();
			properties.load(in);
			return properties;
		}
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
