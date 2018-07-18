package org.gil.activemq.server.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.gil.web.common.model.BaseCode;
import org.gil.web.common.model.DataHolder;

public class CategoryFilter implements Filter{

	private static final String LASTACCESSPAGE="lastPage";
	
	private String STOCKPATH = "stock";
	
	private String INDEXPATH = "index";

	private String OTCPATH = "otc";
	
	private String FUNDPATH = "fund";

	private String HKPATH = "hk";
	
	@Override
	public void destroy() {
	}

	private Map<String, Consumer<Map<String, String>>> paramsProcessMap = new HashMap<String, Consumer<Map<String,String>>>();
	
	private Map<String, String> secuCodeMap = new HashMap<String, String>();
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest)arg0;
		String queryString = request.getQueryString();
		if (null==queryString) {
			arg2.doFilter(request, (HttpServletResponse)arg1);
			return;
		}
		String s = URLDecoder.decode(queryString, "UTF-8");
//		Map<String, String[]> params = request.getParameterMap();
		Map<String, String> params = getParameterMap(s);
		String sid = params.get("sid");//区分调用者
		if(StringUtils.isNotEmpty(sid)){
			Consumer<Map<String, String>> paramProcess = paramsProcessMap.get(sid);
			if(null != paramProcess){
				paramProcess.accept(params);
			}
		}
		Map<String, String> copyParams = new HashMap<String, String>();
		copyParams.putAll(params);
		String gilCode = copyParams.get("gilcode");
		String uri = request.getRequestURI();
		HttpSession session = request.getSession(true);
		String lastPage = (String)session.getAttribute(LASTACCESSPAGE);
		if(null != gilCode && (copyParams.get("type")==null || copyParams.get("name")==null)){//首次进入
			BaseCode baseCode = DataHolder.instance.getBaseCodeByGilCode(gilCode);
			if(null != baseCode){
				if(baseCode.getSecuCategoryCodeI() ==1){//股票
					if (baseCode.getSecuName().contains("退市")) {
						((HttpServletResponse)arg1).sendRedirect(request.getServletContext().getContextPath()+"/error.html");
						return;
					}
					if((baseCode.getSecuCategoryCodeII()==101 || baseCode.getSecuCategoryCodeII()==102) && !uri.startsWith(STOCKPATH)){//股票A,B股
						String type = copyParams.get("type");
						if (null ==type) {
							copyParams.put("type", String.valueOf(baseCode.getCompanyType()));
						}
						copyParams.put("name", baseCode.getSecuName());
						String page = STOCKPATH+"index.html?";
						if(null != lastPage && lastPage.startsWith(STOCKPATH)){
							page = lastPage;
						}
						((HttpServletResponse)arg1).sendRedirect(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+page+buildParams(copyParams));
						return;
					}else if (baseCode.getSecuCategoryCodeII()==105 && !uri.startsWith(HKPATH)) {//港股
						if (baseCode.getListedState()!=1 && baseCode.getListedState()!=3) {
							((HttpServletResponse)arg1).sendRedirect(request.getServletContext().getContextPath()+"/error.html");
							return;
						}
						String type = copyParams.get("type");
						if (null ==type) {
							copyParams.put("type", String.valueOf(baseCode.getCompanyType()));
						}
						copyParams.put("name", baseCode.getSecuName());
						String page = HKPATH+"index.html?";
						if(null != lastPage && lastPage.startsWith(HKPATH)){
							page = lastPage;
						}
						((HttpServletResponse)arg1).sendRedirect(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+page+buildParams(copyParams));
						return;
					}else if (baseCode.getSecuCategoryCodeII()==103 && !uri.startsWith(OTCPATH)) {//新三板
						if (baseCode.getListedState()!=1 && baseCode.getListedState()!=3) {
							((HttpServletResponse)arg1).sendRedirect(request.getServletContext().getContextPath()+"/error.html");
							return;
						}
						String type = copyParams.get("type");
						if (null ==type) {
							copyParams.put("type", String.valueOf(baseCode.getCompanyType()));
						}
						copyParams.put("name", baseCode.getSecuName());
						String page = OTCPATH+"index.html?";
						if(null != lastPage && lastPage.startsWith(OTCPATH)){
							page = lastPage;
						}
						((HttpServletResponse)arg1).sendRedirect(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+page+buildParams(copyParams));
						return;
					}else if(baseCode.getSecuCategoryCodeII()==104){//优先股
						((HttpServletResponse)arg1).sendRedirect(request.getServletContext().getContextPath()+"/error.html");
						return;
					}
				}else if (baseCode.getSecuCategoryCodeI()==2) {//基金
					if (baseCode.getSecuCategoryCodeII()==208) {
						((HttpServletResponse)arg1).sendRedirect(request.getServletContext().getContextPath()+"/error.html");
						return;
					}
					if(!uri.startsWith(FUNDPATH)){
						String type = copyParams.get("type");
						if (null ==type) {
							copyParams.put("type", String.valueOf(baseCode.getCompanyType()));
						}
						copyParams.put("name", baseCode.getSecuName());
						String page = FUNDPATH+"index.html?";
						if(null != lastPage && lastPage.startsWith(FUNDPATH)){
							page = lastPage;
						}
						((HttpServletResponse)arg1).sendRedirect(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+page+buildParams(copyParams));
						return;
					}
				}else if(baseCode.getSecuCategoryCodeI()==3){//指数
//					if(!uri.startsWith(INDEXPATH)){
//						String type = copyParams.get("type");
//						if(null == type){
							if(baseCode.getSecuCategoryCodeII()==301){
								if(baseCode.getSecucode().equals("000001") || baseCode.getSecucode().equals("399001")){//上综深成指数
									copyParams.put("type", "0");
								}else{//其余股票指数：1
									copyParams.put("type", "1");
								}
							}else{//非股票指数：2
								copyParams.put("type", "2");
							}
							copyParams.put("name", baseCode.getSecuName());
//						}
						String page = INDEXPATH+"index.html?";
						if(null != lastPage && lastPage.startsWith(INDEXPATH)){
							page = lastPage;
						}
						((HttpServletResponse)arg1).sendRedirect(request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+page+buildParams(copyParams));
						return;
//					}
				}
			}else {
				((HttpServletResponse)arg1).sendRedirect(request.getServletContext().getContextPath()+"/error.html");
				return;
			}
		}
		arg2.doFilter(request, (HttpServletResponse)arg1);
		if(StringUtils.isNotEmpty(gilCode)){//记录上一次浏览的页面
			if(uri.indexOf("index.html") ==-1){
				session.setAttribute(LASTACCESSPAGE, uri+"?");	
			}
		}
	}
	
	private Map<String, String> getParameterMap(String str){
		Map<String, String> map=new HashMap<>();
		if (str!=null) {
			String[] split = str.split("&");
			for (int i = 0; i < split.length; i++) {
				String[] eq = split[i].split("=");
				if (eq.length==2) {
					map.put(eq[0], eq[1]);
				}
			}
		}
		return map;
	}
	
	public String buildParams(Map<String, String> params) throws UnsupportedEncodingException{
		StringBuilder sBuilder = new StringBuilder();
		boolean prefix = false;
		for(Entry<String, String> maps:params.entrySet()){
			if(prefix){
				sBuilder.append("&");
			}
			sBuilder.append(maps.getKey()).append("=").append(maps.getValue());
			prefix = true;
		}
		return URLEncoder.encode(sBuilder.toString(), "UTF-8");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		STOCKPATH = arg0.getServletContext().getContextPath()+"/stock/";
		INDEXPATH = arg0.getServletContext().getContextPath()+"/index/";
		OTCPATH = arg0.getServletContext().getContextPath()+"/otc/";
		FUNDPATH = arg0.getServletContext().getContextPath()+"/fund/";
		HKPATH = arg0.getServletContext().getContextPath()+"/hk/";
		paramsProcessMap.put("hs",hsParamsProcess);
		secuCodeMap.put("2A01", "399001");
		secuCodeMap.put("2A02", "399002");
		secuCodeMap.put("2A03", "399003");
		secuCodeMap.put("2C02", "399107");
		secuCodeMap.put("1A0001", "000001");
		secuCodeMap.put("1A0002", "000002");
		secuCodeMap.put("1A0003", "000003");
	}
	
	/**
	 * 对接恒生投资赢家的参数
	 */
	private Consumer<Map<String, String>> hsParamsProcess = new Consumer<Map<String,String>>() {
		public void accept(java.util.Map<String,String> params) {
			String secuCode = params.get("secucode");
			if(StringUtils.isNotEmpty(secuCode)){
				try{
					int market = Integer.valueOf(params.get("market"));
					if(market == 0x1100){
						if(secuCode.startsWith("1B0")){
							secuCode = "000"+secuCode.substring(3);
						}else{
							if(secuCodeMap.containsKey(secuCode)){
								secuCode = secuCodeMap.get(secuCode);
							}
						}
					}else if(market == 0x1200){
						if(secuCodeMap.containsKey(secuCode)){
							secuCode = secuCodeMap.get(secuCode);
						}
					}
					int oneCategory = (market>>12) & 0xF;
					int twoCategory = (market>>8) & 0x0F;
					int threeCategory = market & 0x0FF;
					BaseCode bc = null;
					switch (oneCategory) {
					case 1://股票市场
						switch (threeCategory) {
						case 0:
						case 0xe:
						case 0x10://指数
							bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 3);
							break;
						case 4:
						case 8:
						case 9:
						case 0xb://基金
							bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 2);
							break;
						default:
							bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 1);
							break;
						}
						break;
					case 2://港股市场
						switch (twoCategory) {
						case 1:
							switch (threeCategory) {
							case 2:
							case 5://香港权证
								bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 9,905);
								break;
							case 4://香港基金
								bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 2,208);
							case 3://港股
								bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 1,105);
								break;
							}
							break;
						default:
							bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode, 1,105);
							break;
						}
						break;
					case 4://期货市场
						bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode,6);
						break;
					case 7://期权市场
						bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode,7);
						break;
					}
					if(null ==bc){
						bc = DataHolder.instance.getBaseCodeBySecuCode(secuCode,1);
					}
					if(null != bc){
						params.remove("market");
						params.remove("secucode");
						params.put("gilcode", bc.getGilCode());
					}
				}catch(Exception ex){
				}
			}
		};
	};
}
