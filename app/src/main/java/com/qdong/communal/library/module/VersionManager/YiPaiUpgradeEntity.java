package com.qdong.communal.library.module.VersionManager;

import java.io.Serializable;

/**
 * 升级实体
 *
 * @author LHD
 *
 */
public class YiPaiUpgradeEntity implements Serializable {
	private static final long serialVersionUID = 0x001;


	/**
	 * id : 1
	 * idf : A-yipai20
	 * version : 1.0
	 * needupdate : 2019-01-17 17:54:10
	 * updatamessage : 更新提示message信息
	 * downloadtype : 1
	 * downloadurl : http://www.baidu.com
	 */

	private int id;
	private String idf;
	private String version;
	private String needupdate;
	private String updatamessage;
	private String downloadtype;
	private String downloadurl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdf() {
		return idf;
	}

	public void setIdf(String idf) {
		this.idf = idf;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNeedupdate() {
		return needupdate;
	}

	public void setNeedupdate(String needupdate) {
		this.needupdate = needupdate;
	}

	public String getUpdatamessage() {
		return updatamessage;
	}

	public void setUpdatamessage(String updatamessage) {
		this.updatamessage = updatamessage;
	}

	public String getDownloadtype() {
		return downloadtype;
	}

	public void setDownloadtype(String downloadtype) {
		this.downloadtype = downloadtype;
	}

	public String getDownloadurl() {
		return downloadurl;
	}

	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
}
