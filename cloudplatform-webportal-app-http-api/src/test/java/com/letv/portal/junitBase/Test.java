package com.letv.portal.junitBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.letv.common.util.JsonUtil;

class Tag{
	private String tagName;
	private String code;
	private List<Link> links;
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(List<Link> links) {
		this.links = links;
	}
}
class Link{
	private String url;
	private String style;
	private String name;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
public class Test {
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		List<Tag> listtag = new ArrayList<Tag>();
		Tag tag1 = new Tag();
			Link link1 = new Link();
			link1.setUrl("http://www.leshiren.cn");
			link1.setStyle("btn-warning");
			link1.setName("www.leshiren.cn");
			Link link2 = new Link();
			link2.setUrl("http://lingshu.letv.cn/");
			link2.setStyle("btn-warning");
			link2.setName("灵枢管控平台");
			List<Link> list = new ArrayList<Link>();
			list.add(link1);
			list.add(link2);
		tag1.setCode("work");
		tag1.setTagName("工作");
		tag1.setLinks(list);
		Tag tag2 = new Tag();
			Link link3 = new Link();
			link3.setUrl("http://www.leshiren.cn");
			link3.setStyle("btn-warning");
			link3.setName("www.leshiren.cn");
			Link link4 = new Link();
			link4.setUrl("http://lingshu.letv.cn/");
			link4.setStyle("btn-warning");
			link4.setName("灵枢管控平台");
			List<Link> list2 = new ArrayList<Link>();
			list2.add(link3);
			list2.add(link4);
		tag2.setCode("work");
		tag2.setTagName("工作");
		tag2.setLinks(list2);
		listtag.add(tag1);
		listtag.add(tag2);
		System.out.println(JsonUtil.toJson(listtag));
	}
}
