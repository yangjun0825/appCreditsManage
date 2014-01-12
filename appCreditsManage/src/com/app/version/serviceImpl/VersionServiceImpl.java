package com.app.version.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.util.MyBatisDao;
import com.app.util.Util;
import com.app.version.service.VersionService;
import com.app.vo.Head;

@Service
public class VersionServiceImpl implements VersionService {

	@Autowired
	private MyBatisDao<Map<String, Object>> versionDao;
	
	private Log logger = LogFactory.getLog(VersionServiceImpl.class);	
	
	/* (非 Javadoc) 
	* <p>Title: versionUpdate</p> 
	* <p>Description:版本升级处理 </p> 
	* @param xmlStr
	* @param head
	* @return
	* @throws Exception 
	* @see com.app.version.service.VersionService#versionUpdate(java.lang.String, com.app.vo.Head) 
	*/
	@Override
	public String versionUpdate(String xmlStr, Head head) throws Exception {
		logger.debug("enter VersionServiceImpl.versionUpdate(String xmlStr, Head head) " 
				   + "[xmlStr] = " + xmlStr);
		
		String response = "";
		
		List<Element> list = Util.getRequestDataByXmlStr(xmlStr, "svccont/pra/item");
		
		String version = "";
		
		if(!CollectionUtils.isEmpty(list)) {
			version = list.get(0).elementTextTrim("version");
		}
		
		if (StringUtils.isBlank(version)) {
			response = Util.getResponseForFalse(xmlStr, head, "102", "无效请求");
			return response;
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("[version] = " + version);
		}
		
		//查询版本信息
		Map<String, Object> params = new HashMap<String, Object>();
		String queryStr = "version.retrieveVersionInfo";
		List<Map<String, Object>> versionInfoList = versionDao.getSearchList(queryStr, params);
		
		StringBuffer versionSb = new StringBuffer();
		
		if(CollectionUtils.isNotEmpty(versionInfoList)) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("[versionInfoList] = " + versionInfoList);
			}
			
	        String versionDb = (String)versionInfoList.get(0).get("version");
	        
	        if(logger.isDebugEnabled()) {
	        	logger.debug("[versionDb] = " + versionDb);
	        }
	        
	        if(StringUtils.isNotBlank(versionDb)) {
	        	versionDb = versionDb.replace(".", "");
	        	version = version.replace(".", "");
	        	
	        	int versionDb_i = Integer.parseInt(versionDb);
	        	
	        	int version_i = Integer.parseInt(version);
	        	
	        	if(versionDb_i > version_i) {
	        		
	        		String isenforce = (String)versionInfoList.get(0).get("isenforce");
	    	        String vsdesc = (String)versionInfoList.get(0).get("vsdesc");
	    	        String vspath = (String)versionInfoList.get(0).get("vspath");
	        		
	    	        if(logger.isDebugEnabled()) {
	    	        	logger.debug("[isenforce] = " + isenforce + " [vsdesc] = " + vsdesc + " [vspath] = " + vspath);
	    	        }
	    	        
	        		versionSb.append("<c>1</c>");
	        		versionSb.append("<v>" + versionDb + "</v>");
	        		versionSb.append("<b>" + isenforce + "</b>");
	        		versionSb.append("<r>" + vsdesc + "</r>");
	        		versionSb.append("<l>" + vspath + "</l>");
	        		
	        		String encodeStr = versionSb.toString();
	        		if(logger.isDebugEnabled()) {
	        			logger.debug("[encodeStr] = " + encodeStr);
	        		}
	        		response = Util.getResponseForTrue(head, encodeStr); 
	        	}
	        }
		}
		
		logger.debug("exit VersionServiceImpl.versionUpdate(String xmlStr, Head head)");
		return response;
	}

}
