package com.pacman.utils;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseUtils {

    private static String DB_URL = "jdbc:mysql://localhost:3306/nhom10";
    private static String USER_NAME = "root";
    private static String PASSWORD = "";

    public static void savePlayerResult(Date playDate, int Score, int level, boolean winState) {
        Connection conn = null;
        PreparedStatement pre = null;
        try {
            // Ket noi database
            conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            // query - insert
            String sql = "insert into playerscore(date, score, level, status) values(?, ?, ?, ?)";
            pre = conn.prepareStatement(sql);
            pre.setString(1, playDate.toString());
            pre.setInt(2, Score);
            pre.setInt(3, level);
            pre.setString(4, winState ? "Win" : "Lose");
            pre.execute();
            // close connection
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                pre.close();
                conn.close();

            } catch (SQLException e2) {
                // TODO: handle exception
            }
        }
    }


    public static String[][] getPlayerResult(int page) { // FIXME
        Connection conn = null;
        Statement statement = null;
        List<String> resList = new ArrayList<>();

        String[][] res = new String[10][];
        StringBuilder bd = new StringBuilder();
        try {
            // Ket noi den database
            conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            // khoi tao doi
            statement = conn.createStatement();
            // Tạo câu truy vấn
            String sql = "select * from playerscore order by score DESC ";
            // Thuc thi
            ResultSet rs = statement.executeQuery(sql);
            int c = 0;
            while (c < page*10) {
                c++;
            }
            while (rs.next() && c < page*10 + 10) {
                bd.setLength(0);
                String date = rs.getString("date");
                int score = rs.getInt("score");
                int level = rs.getInt("level");
                String winState = rs.getString("status");
                String[] day = date.split(" ");
                bd.append(day[0]);
                bd.append(",");
                bd.append(score);
                bd.append(",");
                bd.append(level);
                bd.append(",");
                bd.append(winState);
                bd.append(",");
                res[c] = bd.toString().split(",");
                c++;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }


    public static int getHighestScore() {
        Connection conn = null;
        Statement statement = null;
        try {
            // Ket noi den database
            conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            // khoi tao doi
            statement = conn.createStatement();
            // Tạo câu truy vấn

            String sql = "select distinct score from playerscore where playerscore.score = (select max(score) from playerscore) ";

            // Thuc thi
            ResultSet rs = statement.executeQuery(sql);


            while (rs.next()) {
                Integer score = rs.getInt("score");
                return score;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }
        return 0;
    }
}
