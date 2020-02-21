/******************************************************
 * <pre>
 * @author branz
 * @since 2018.04
 * DB접속 관리자 
 * </pre>
 ******************************************************/
package com.nbreds.bigdata.common.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class SQLManager {
	protected static SqlSession  sc = null;
	private static Logger log = Logger.getLogger("process.all.batch");
	/***********************************************************************************
	 * 프로퍼티 파일 로드 
	 * @return SqlMapClient
	 ***********************************************************************************/	
	public static SqlSession getSqlMap() 
	{
		String SERVER_TYPE = System.getProperty("SERVER_TYPE");
		if(SERVER_TYPE == null || "".equals(SERVER_TYPE)) {
			SERVER_TYPE = "dev";
		}
		if (sc == null) 
		{
			Reader reader = null;
			try 
			{
				System.out.println("com/nbreds/bigdata/common/db/"+SERVER_TYPE+".sqlMapConfig.xml");
				reader = Resources.getResourceAsReader("com/nbreds/bigdata/common/db/"+SERVER_TYPE+".sqlMapConfig.xml");
				sc = new SqlSessionFactoryBuilder().build(reader).openSession();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				log.error(e.getMessage(), e);
			} finally {
				try {
					if (reader != null) reader.close();
				} catch (Exception e) {e.printStackTrace();}
			}
		}
        return sc;
    }
	/*---------------------------------------------------------------------------------*/
	
	
}
