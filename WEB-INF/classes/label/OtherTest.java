package label;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;

/**
 * Servlet implementation class OtherTest
 */
@WebServlet("/OtherTest")
public class OtherTest extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/label";
 
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "ZRHlxzj0606";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OtherTest() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应内容类型
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        // 处理中文
        String name = new String(request.getParameter("name").getBytes("ISO8859-1"),"UTF-8");
        String attr[] = request.getParameterValues("attr");

        Connection conn = null;
        Statement stmt = null;

        try{

            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
        
            // 打开链接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
            
            int match = 0;
            for(int i = 0; i < 5; i++) {
            	String qrysql = "SELECT self, others FROM data_" + name + " WHERE attr=" + attr[i] + ";";
            	ResultSet rs = stmt.executeQuery(qrysql);
            	rs.next();
            	int self = rs.getInt("self");
            	if(self==1) match += 100/5;
            	int others = rs.getInt("others");
            	others++;
            	String updsql = "UPDATE data_" + name + " SET others=" + others + " WHERE attr=" + attr[i] + ";";
            	stmt.execute(updsql);
            }
            
            out.write("<script>location.href='match.html?match=" + match + "'</script>");

            // 完成后关闭
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
    
    // 处理 POST 方法请求的方法
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
