import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MybatisTest {
	public static void main(String[] args) {
		SqlSession sc = null;
		String SERVER_TYPE = System.getProperty("SERVER_TYPE");
		if(SERVER_TYPE == null || "".equals(SERVER_TYPE)) {
			SERVER_TYPE = "dev";
		}
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
		} finally {
			try {
				if (reader != null) reader.close();	
			} catch (Exception e) {e.printStackTrace();}
		}
	}
}
