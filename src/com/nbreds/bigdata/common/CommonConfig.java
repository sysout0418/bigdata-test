/******************************************************
 * <pre>
 * @author branz
 * @since 2018.04
 * 공통코드 - 설정값
 * </pre>
 ******************************************************/
package com.nbreds.bigdata.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonConfig {
	
	private static CommonConfig instance = null;	
	private static Properties props = new Properties();
	
	/***********************************************************************************
	 * 인스턴스화
	 * @return CommonConfig
	 ***********************************************************************************/
	public static CommonConfig getInstance() 
	{
		if (instance == null) 
		{
			instance = new CommonConfig();
		}
		
		return instance;
	}
	/*---------------------------------------------------------------------------------*/
	
	
	
	
	
	/***********************************************************************************
	 * 프로퍼티 파일 로드 
	 ***********************************************************************************/
	private CommonConfig() 
	{
		String SERVER_TYPE = System.getProperty("SERVER_TYPE");
		
		InputStream is = getClass().getResourceAsStream("/com/nbreds/bigdata/common/config/"+SERVER_TYPE+".config.properties");
		
		try 
		{
			props.load(is);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (is != null) is.close();
			} 
			catch (Exception e) 
			{	
				e.printStackTrace();
			}
		}
	}
	/*---------------------------------------------------------------------------------*/
	
	
	
	
	/***********************************************************************************
	 * DB에서 해당 Key 값을 호출해서 쓰고 없을 시에 
	 * 기본적으로 프로퍼티 파일에 선언된 값 사용
	 ***********************************************************************************/
	public String getString(String key) 
	{	
		//TODO DB구조 설계 및 해당 값 구하는 부분 쿼리(xml) 부분 개발 필요
//		try {
//			if(key != null ){
//				String value =  (String) SQLManager.getSqlMap().selectOne("Common.getProgerty", key);
//				if(value != null && !"".equals(value)){
//					System.out.println("Progerty For DB : "+value);
//					return value;
//				}else{
//					System.out.println("Progerty Not In DB : "+key);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return props.getProperty(key);
	}
	/*---------------------------------------------------------------------------------*/
	
	
	
	
	
	/***********************************************************************************
	 * 
	 * 
	 * 
	 ***********************************************************************************/
	public int getInt(String key) 
	{
		return Integer.parseInt(props.getProperty(key));
	}
	/*---------------------------------------------------------------------------------*/




	/**
	 * 디비에서 전번 안받고 config 에서 전번 받아서 처리하도록 수정
	 * @param key
	 * @return
	 */
	public String getStringFile(String key) {
		System.out.println("MDN For DB : "+props.getProperty(key));
		return props.getProperty(key);
	}
}
