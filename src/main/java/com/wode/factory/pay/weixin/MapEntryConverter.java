package com.wode.factory.pay.weixin;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Created by zoln on 2016/4/12.
 *
 * Map转换器 for Xstream, 用于将Map转换成微信API需要的xml格式
 */
public class MapEntryConverter implements Converter {

	public boolean canConvert(Class clazz) {
		return AbstractMap.class.isAssignableFrom(clazz);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		AbstractMap map = (AbstractMap) value;
		for (Object obj : map.entrySet()) {
			Map.Entry entry = (Map.Entry) obj;
			writer.startNode(entry.getKey().toString());
			Object val = entry.getValue();
			if (null != val) {
				writer.setValue(val.toString());
			}
			writer.endNode();
		}

	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<String, String> map = null;
		Class clazz = context.getRequiredType();
		try {
			try {
				map = (Map)clazz.getConstructor().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String key = reader.getNodeName(); // nodeName aka element's name
			String value = reader.getValue();
			map.put(key, value);
			reader.moveUp();
		}
		return map;
	}

}
