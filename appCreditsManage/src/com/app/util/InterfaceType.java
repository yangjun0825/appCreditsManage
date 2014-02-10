package com.app.util;


/**
 * 所有接口bizCode枚举类
 * @author yangjun
 *
 */
public enum InterfaceType {
	tjt001, tjt002, tjt003, tjt004, tjt005, tjt006, tjt007, tjt008, tjt009;

	public static InterfaceType getInterfaceType(String type) {
		return valueOf(type.toLowerCase());
	}
}
