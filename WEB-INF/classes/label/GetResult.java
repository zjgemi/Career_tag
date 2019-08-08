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
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;

import net.sf.json.*;

/**
 * Servlet implementation class GetResult
 */
@WebServlet("/GetResult")
public class GetResult extends HttpServlet {
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
    public GetResult() {
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
        HttpSession session = request.getSession();
        Object name = session.getAttribute("name");

        Connection conn = null;
        Statement stmt = null;

        try{

            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);
        
            // 打开链接
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();

            String qrysql = "SELECT * FROM data_" + name + " ORDER BY self desc, others desc;";
            ResultSet rs = stmt.executeQuery(qrysql);

            ArrayList<JSONObject> tt = new ArrayList<JSONObject>();
            ArrayList<JSONObject> tf = new ArrayList<JSONObject>();
            ArrayList<JSONObject> ft = new ArrayList<JSONObject>();
            ArrayList<JSONObject> ff = new ArrayList<JSONObject>();
            int sumothers = 0;
            while(rs.next()) {
                String attr = rs.getString("attr");
                int self = rs.getInt("self");
                int others = rs.getInt("others");
                sumothers += others;

                JSONObject obj = new JSONObject();
                obj.put("attr",attr);
                obj.put("self",self);
                obj.put("others",others);
                if(self > 0) {
                    if(others > 0) {
                        tt.add(obj);
                    }
                    else {
                        tf.add(obj);
                    }
                }
                else {
                    if(others > 0) {
                        ft.add(obj);
                    }
                    else {
                        ff.add(obj);
                    }
                }
            }
            JSONObject obj = new JSONObject();
            obj.put("tt",tt);
            obj.put("tf",tf);
            obj.put("ft",ft);
            obj.put("ff",ff);
            obj.put("sumothers",sumothers/5);
            obj.put("name",name);
            out.write(JSONObject.fromObject(obj).toString());
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
