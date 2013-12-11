package cis573.carecoor.bean;

import java.io.Serializable;

public class Friend implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String url;
	private int iconId;
	
	public Friend() {
	}
	
	public Friend(String name, String url, int iconId) {
		this.name = name;
		this.url = url;
		this.iconId = iconId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
}
