package com.nobody.nobodyplace.trash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

public class Solution {
    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

//    public static void writeDataToDB() {
//        String url = "jdbc:mysql://localhost:3306/nobodyplace";
//        String userName = " ";
//        String password = " ";
//        String tableName = "csgo_item";
//        Connection connection = null;
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            connection = DriverManager.getConnection(url, userName, password);
//        } catch (Exception e) {
//            System.out.println("err" + e.getMessage());
//        }
//
//        try {
//            String sql = "insert into " + tableName + " (item_id, name_cn, name_eng, pic_url, exterior, main_type, sub_type) values (?, ?, ?, ?, ?, ?, ?)";
//            PreparedStatement statement = connection,preparedStatement
//        } catch (Exception e2) {
//            System.out.println("err2" + e2.getMessage());
//        }
//    }

    public static void main(String[] args) {
        int[] a = new int[54];

    }

}
