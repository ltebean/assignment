package assignment;

import com.sun.tools.corba.se.idl.constExpr.Times;

import java.sql.*;
import java.util.Date;
import java.util.Map;

/**
 * Created by leo on 2018/4/11.
 */
public class Mysql {

    Connection connection;

    public Mysql() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://rm-uf67v0qkgn673b70pfo.mysql.rds.aliyuncs.com:3306/test", "root", "1qaz!QAZ");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insert(Order order) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `order` (`order_id`, `items`, `start_time`, `end_time`) values(?,?,?,?)");
            statement.setInt(1, order.id);
            statement.setInt(2, order.items.size());
            statement.setTimestamp(3, Timestamp.valueOf(order.startTime));
            statement.setTimestamp(4, Timestamp.valueOf(order.endTime));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void showStats() {
        System.out.println("Execution started at: " + Utils.currentTime());
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT order_id , avg(TIMEDIFF(end_time, start_time)) as time\n" +
                    "from `order`\n" +
                    "group by order_id");
            long start = System.currentTimeMillis();
            ResultSet res = statement.executeQuery();
            while(res.next()) {
                String orderId = res.getString("order_id");
                String time = res.getString("time");
                System.out.println(String.format("Order %s took %s seconds to complete.", orderId, time));
            }
            long end = System.currentTimeMillis();
            System.out.println("Execution took: " + (end - start) / 1000.f + " seconds to complete");
            System.out.println("Execution ended at: " + Utils.currentTime());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
