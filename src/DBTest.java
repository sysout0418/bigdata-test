import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTest {
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 드라이버 이름 대소문자 주의
			Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.66:3306/test", "root", "root1234");
			System.out.println("PASS STEP 1");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from test");
			while(rs.next()) {
				System.out.println(rs.getString("name")+" : "+rs.getInt("cnt"));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
