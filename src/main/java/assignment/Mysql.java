package assignment;

import com.sun.tools.corba.se.idl.constExpr.Times;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by leo on 2018/4/11.
 */
public class Mysql {

    Connection connection;

    public Mysql() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://rm-uf65k56lfc9dfv13rko.mysql.rds.aliyuncs.com:3306/Test", "root", "Yucong1118");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insert(Order order) {
        // insert order
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

        // insert item
        Map<String, Long> result = order.items.stream()
                .collect(groupingBy(Item::getName, counting()));
        result.entrySet().stream().forEach(e -> {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO `item` (`order_id`, `name`, `number`) values(?,?,?)");
                statement.setInt(1, order.id);
                statement.setString(2, e.getKey());
                statement.setLong(3, e.getValue());
                statement.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

    }

    public void showStats() {
        String started = "";
        String took = "";
        String ended = "";
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT min(start_time) as start, max(end_time) as end, TIMEDIFF(max(end_time),  min(start_time)) as span\n" +
                    "from `order`");
            ResultSet res = statement.executeQuery();

            while(res.next()) {
                started = res.getString("start");
                took = res.getString("span");
                ended = res.getString("end");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Execution started at: " + started);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT order_id , TIMEDIFF(end_time, start_time) as time\n" +
                    "from `order`\n" +
                    "group by order_id");
            ResultSet res = statement.executeQuery();

            while(res.next()) {
                String orderId = res.getString("order_id");
                String time = res.getString("time");
                System.out.println(String.format("Order %s took %s seconds to complete.", orderId, time));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Execution took: " + took + " seconds to complete");
        System.out.println("Execution ended at: " + ended);

    }
}
