package com.letv.common.util.jacksonext.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import com.letv.common.util.jacksonext.FilterPropertyHandler;
import com.letv.common.util.jacksonext.annotation.ExcludeProperty;
import com.letv.common.util.jacksonext.annotation.IncludeProperty;
import com.letv.common.util.jacksonext.annotation.JsonFilterProperties;
import com.letv.common.util.jacksonext.helper.ThreadJacksonMixInHolder;

/**
 * 使用代理来创建jackson的MixInAnnotation注解接口
 * 
 * @author linzhanbo .
 * @since 2016年7月7日, 下午5:41:34 .
 * @version 1.0 .
 */
public class JavassistFilterPropertyHandler implements FilterPropertyHandler {

	public static final Logger LOGGER = Logger
			.getLogger(JavassistFilterPropertyHandler.class);

	/**
	 * 注解的方法对应生成的代理类映射表
	 */
	private static Map<Method, Map<Class<?>, Class<?>>> proxyMethodMap = new HashMap<Method, Map<Class<?>, Class<?>>>();

	/**
	 * String数组的hashCode与生成的对应的代理类的映射表
	 */
	private static Map<Integer, Class<?>> proxyMixInAnnotationMap = new HashMap<Integer, Class<?>>();

	private static String[] globalIgnoreProperties = new String[] {
			"hibernateLazyInitializer", "handler" };

	/**
	 * 如果是标注的SpringMVC中的Controller方法，则应判断是否注解了@ResponseBody
	 */
	private boolean isResponseBodyAnnotation;
	/**
	 * 创建代理接口的唯一值索引
	 */
	private static int proxyIndex;

	public JavassistFilterPropertyHandler() {
	}

	public JavassistFilterPropertyHandler(String[] globalIgnoreProperties) {
		JavassistFilterPropertyHandler.globalIgnoreProperties = globalIgnoreProperties;
	}

	/**
	 * 为了减少包的依赖大小，新版本不再支持对@ResponseBody的支持，请在Aop中使用以下方法自行判断：
	 * 
	 * <pre>
	 * Method.isAnnotationPresent(ResponseBody.class)
	 * </pre>
	 *
	 * @param isResponseBodyAnnotation
	 *            如果是标注的SpringMVC中的Controller方法，则应判断是否注解了@ResponseBody
	 */
	@Deprecated
	public JavassistFilterPropertyHandler(boolean isResponseBodyAnnotation) {
		this.isResponseBodyAnnotation = isResponseBodyAnnotation;
	}

	/**
	 * @param collection
	 * @param names
	 * @return
	 */
	private Collection<String> checkAndPutToCollection(
			Collection<String> collection, String[] names) {
		if (collection == null) {
			collection = new HashSet<String>();
		}
		Collections.addAll(collection, names);
		return collection;
	}

	private Collection<String> putGlobalIgnoreProperties(
			Collection<String> collection) {
		if (globalIgnoreProperties != null) {
			if (collection == null) {
				collection = new HashSet<String>();
			}
			for (int i = 0; i < globalIgnoreProperties.length; i++) {
				String name = globalIgnoreProperties[i];
				collection.add(name);
			}
		}
		return collection;
	}

	/**
	 * 处理IgnoreProperties注解 <br>
	 *
	 * @param properties
	 * @param pojoAndNamesMap
	 */
	private void processIgnorePropertiesAnnotation(JsonFilterProperties properties,
			Map<Class<?>, Collection<String>> pojoAndNamesMap) {
		ExcludeProperty[] values = properties.excluses();

		IncludeProperty[] allowProperties = properties.includes();

		if (allowProperties != null) {
			for (IncludeProperty allowProperty : allowProperties) {
				processAllowPropertyAnnotation(allowProperty, pojoAndNamesMap);
			}
		}

		if (values != null) {
			for (ExcludeProperty property : values) {
				processIgnorePropertyAnnotation(property, pojoAndNamesMap);
			}
		}

	}

	/**
	 * 处理IgnoreProperty注解 <br>
	 * @param property
	 * @param pojoAndNamesMap
	 */
	private void processIgnorePropertyAnnotation(ExcludeProperty property,
			Map<Class<?>, Collection<String>> pojoAndNamesMap) {
		String[] names = property.names();
		Class<?> pojoClass = property.pojo();
		// Class<?> proxyAnnotationInterface = createMixInAnnotation(names);//
		// 根据注解创建代理接口

		Collection<String> nameCollection = pojoAndNamesMap.get(pojoClass);
		nameCollection = checkAndPutToCollection(nameCollection, names);
		pojoAndNamesMap.put(pojoClass, nameCollection);
	}

	/**
	 * 获取Class类的所有非静态字段名 <br>
	 * @param clazz
	 * @return
	 */
	private Collection<String> getUnstaticClassFieldNameCollection(
			Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException("传入的clazz为空对象！");
		}
		Field[] fields = clazz.getDeclaredFields();
		int length = fields.length;
		Collection<String> fieldNames = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			Field field = fields[i];
			if (!Modifier.isStatic(field.getModifiers())) {
				fieldNames.add(field.getName());
			}
		}
		return fieldNames;
	}

	/**
	 * 处理AllowProperty注解 <br>
	 * @param property
	 * @param pojoAndNamesMap
	 */
	private void processAllowPropertyAnnotation(IncludeProperty property,
			Map<Class<?>, Collection<String>> pojoAndNamesMap) {
		String[] allowNames = property.names();
		Class<?> pojoClass = property.pojo();

		Collection<String> ignoreProperties = getUnstaticClassFieldNameCollection(pojoClass);

		Collection<String> allowNameCollection = new ArrayList<String>();
		Collections.addAll(allowNameCollection, allowNames);

		Collection<String> nameCollection = pojoAndNamesMap.get(pojoClass);
		if (nameCollection != null) {
			nameCollection.removeAll(allowNameCollection);
		} else {
			ignoreProperties.removeAll(allowNameCollection);
			nameCollection = ignoreProperties;
		}
		pojoAndNamesMap.put(pojoClass, nameCollection);
	}

	/**
	 * 根据方法获取过滤映射表 <br>
	 * @param method
	 *            注解了 @IgnoreProperties 或 @IgnoreProperty 的方法（所在的类）
	 * @return Map pojo与其属性的映射表
	 */
	public Map<Class<?>, Class<?>> getProxyMixInAnnotation(Method method) {
		Map<Class<?>, Class<?>> map = proxyMethodMap.get(method);// 从缓存中查找是否存在

		if (map != null && map.entrySet().size() > 0) {// 如果已经读取该方法的注解信息，则从缓存中读取
			return map;
		} else {
			map = new HashMap<Class<?>, Class<?>>();
		}

		Class<?> clazzOfMethodIn = method.getDeclaringClass();// 方法所在的class

		Map<Class<?>, Collection<String>> pojoAndNamesMap = new HashMap<Class<?>, Collection<String>>();

		JsonFilterProperties classIgnoreProperties = clazzOfMethodIn
				.getAnnotation(JsonFilterProperties.class);
		ExcludeProperty classIgnoreProperty = clazzOfMethodIn
				.getAnnotation(ExcludeProperty.class);
		IncludeProperty classAllowProperty = clazzOfMethodIn
				.getAnnotation(IncludeProperty.class);

		JsonFilterProperties ignoreProperties = method
				.getAnnotation(JsonFilterProperties.class);
		ExcludeProperty ignoreProperty = method
				.getAnnotation(ExcludeProperty.class);
		IncludeProperty allowProperty = method.getAnnotation(IncludeProperty.class);

		if (allowProperty != null) {// 方法上的AllowProperty注解
			processAllowPropertyAnnotation(allowProperty, pojoAndNamesMap);
		}
		if (classAllowProperty != null) {
			processAllowPropertyAnnotation(classAllowProperty, pojoAndNamesMap);
		}

		if (classIgnoreProperties != null) {// 类上的IgnoreProperties注解
			processIgnorePropertiesAnnotation(classIgnoreProperties,
					pojoAndNamesMap);
		}
		if (classIgnoreProperty != null) {// 类上的IgnoreProperty注解
			processIgnorePropertyAnnotation(classIgnoreProperty,
					pojoAndNamesMap);
		}

		if (ignoreProperties != null) {// 方法上的IgnoreProperties注解
			processIgnorePropertiesAnnotation(ignoreProperties, pojoAndNamesMap);
		}
		if (ignoreProperty != null) {// 方法上的IgnoreProperties注解
			processIgnorePropertyAnnotation(ignoreProperty, pojoAndNamesMap);
		}

		Set<Entry<Class<?>, Collection<String>>> entries = pojoAndNamesMap
				.entrySet();
		for (Iterator<Entry<Class<?>, Collection<String>>> iterator = entries
				.iterator(); iterator.hasNext();) {
			Entry<Class<?>, Collection<String>> entry = (Entry<Class<?>, Collection<String>>) iterator
					.next();
			Collection<String> nameCollection = entry.getValue();
			nameCollection = putGlobalIgnoreProperties(nameCollection);// 将全局过滤字段放入集合内
			String[] names = nameCollection.toArray(new String[] {});
			Class<?> clazz = createMixInAnnotation(names);

			map.put(entry.getKey(), clazz);
		}

		proxyMethodMap.put(method, map);
		return map;
	}

	/**
	 * 计算数组的hashCode <br>
	 * 2013-10-25 上午11:06:57
	 *
	 * @param stringArray
	 * @return
	 */
	private int hashCodeOfStringArray(String[] stringArray) {
		if (stringArray == null) {
			return 0;
		}
		int hashCode = 17;
		for (int i = 0; i < stringArray.length; i++) {
			String value = stringArray[i];
			hashCode = hashCode * 31 + (value == null ? 0 : value.hashCode());
		}
		return hashCode;
	}

	/**
	 * 创建jackson的代理注解接口类 <br>
	 * 2013-10-25 上午11:59:50
	 *
	 * @param names
	 *            要生成的字段
	 * @return 代理接口类
	 */
	private Class<?> createMixInAnnotation(String[] names) {
		Class<?> clazz = null;
		clazz = proxyMixInAnnotationMap.get(hashCodeOfStringArray(names));
		if (clazz != null) {
			return clazz;
		}

		ClassPool pool = ClassPool.getDefault();

		// 创建代理接口
		CtClass cc = pool.makeInterface("ProxyMixInAnnotation"
				+ System.currentTimeMillis() + proxyIndex++);

		ClassFile ccFile = cc.getClassFile();
		ConstPool constpool = ccFile.getConstPool();

		// create the annotation
		AnnotationsAttribute attr = new AnnotationsAttribute(constpool,
				AnnotationsAttribute.visibleTag);
		// 创建JsonIgnoreProperties注解
		Annotation jsonIgnorePropertiesAnnotation = new Annotation(
				JsonIgnoreProperties.class.getName(), constpool);

		BooleanMemberValue ignoreUnknownMemberValue = new BooleanMemberValue(
				false, constpool);

		ArrayMemberValue arrayMemberValue = new ArrayMemberValue(constpool);// value的数组成员

		Collection<MemberValue> memberValues = new HashSet<MemberValue>();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			StringMemberValue memberValue = new StringMemberValue(constpool);// 将name值设入注解内
			memberValue.setValue(name);
			memberValues.add(memberValue);
		}
		arrayMemberValue.setValue(memberValues.toArray(new MemberValue[] {}));

		jsonIgnorePropertiesAnnotation
				.addMemberValue("value", arrayMemberValue);
		jsonIgnorePropertiesAnnotation.addMemberValue("ignoreUnknown",
				ignoreUnknownMemberValue);

		attr.addAnnotation(jsonIgnorePropertiesAnnotation);
		ccFile.addAttribute(attr);

		// generate the class
		try {
			clazz = cc.toClass();
			proxyMixInAnnotationMap.put(hashCodeOfStringArray(names), clazz);
		} catch (CannotCompileException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return clazz;

	}

	@Override
	public Object filterProperties(Method method, Object object) {

		Map<Class<?>, Class<?>> map = getProxyMixInAnnotation(method);
		if (map == null || map.entrySet().size() == 0) {// 如果该方法上没有注解，则返回原始对象
			return object;
		}
		ThreadJacksonMixInHolder.addMixIns(getEntries(map));
		return object;
	}

	public Set<Entry<Class<?>, Class<?>>> getEntries(Map<Class<?>, Class<?>> map) {
		Set<Entry<Class<?>, Class<?>>> entries = map.entrySet();
		return entries;
	}

	/**
	 * 根据指定的过滤表创建jackson对象 <br>
	 * 2013-10-25 下午2:46:43
	 *
	 * @param map
	 *            过滤表
	 * @return ObjectMapper
	 */
	private ObjectMapper createObjectMapper(Map<Class<?>, Class<?>> map) {
		ObjectMapper mapper = new ObjectMapper();
		Set<Entry<Class<?>, Class<?>>> entries = map.entrySet();
		for (Iterator<Entry<Class<?>, Class<?>>> iterator = entries.iterator(); iterator
				.hasNext();) {
			Entry<Class<?>, Class<?>> entry = iterator.next();
			mapper.getSerializationConfig().addMixInAnnotations(entry.getKey(), entry.getValue());
		}
		return mapper;
	}

	/**
	 * 根据方法上的注解生成objectMapper
	 *
	 * @param method
	 * @return
	 */
	public ObjectMapper createObjectMapper(Method method) {
		return createObjectMapper(getProxyMixInAnnotation(method));
	}

	/**
	 * <br>
	 * 2013-10-25 下午12:29:58
	 *
	 * @param characterEncoding
	 * @return
	 */
	private JsonEncoding getJsonEncoding(String characterEncoding) {
		for (JsonEncoding encoding : JsonEncoding.values()) {
			if (characterEncoding.equals(encoding.getJavaName())) {
				return encoding;
			}
		}
		return JsonEncoding.UTF8;
	}

}
