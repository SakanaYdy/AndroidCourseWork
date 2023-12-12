package xyz.doikki.dkplayer.dataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBOpenHelper {
    private static String driver = "com.mysql.jdbc.Driver";
   //加入utf-8是为了后面往表中输入中文，表中不会出现乱码的情况
//   private static String url = "jdbc:mysql://sh-cdb-bhl1jqmg.sql.tencentcdb.com:63437/video?characterEncoding=utf-8";
//   private static String user = "Ydy";//用户名
//   private static String password = "123Asd**";//密码

    private static String url = "jdbc:mysql://192.168.200.130:3306/test?characterEncoding=utf-8";
    private static String user = "root";//用户名
    private static String password = "1234"; //密码

    /*
    * 连接数据库
    * */
   public static Connection getConn(){
       Connection conn = null;
       try {
           Class.forName(driver);
           conn = DriverManager.getConnection(url, user, password);
       } catch (ClassNotFoundException | SQLException e) {
           e.printStackTrace();
       }
       return conn;
   }

}
