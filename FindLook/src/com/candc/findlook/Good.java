package com.candc.findlook;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 作者: Stonekity(596017443@qq.com)
 * 
 * 时间: 2014年10月10日 上午02:05
 * 
 * 描述: 商品实体类
 */
@SuppressWarnings("serial")
public class Good extends BmobObject {

	private String type; // 商品类型
	private String name; // 商品名称
	private String description; // 商品描述
	private String value; // 商品性价比
	private String url; // 商品对应的描述－网页地址
	private BmobFile picture; // 商品图片

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public BmobFile getPicture() {
		return picture;
	}

	public void setPicture(BmobFile picture) {
		this.picture = picture;
	}

}
