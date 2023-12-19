package com.nobody.nobodyplace.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {

    /**
     * CSV文件生成方法  字符流追加：FileWriter writer = new FileWriter(file，true)
     *
     * @param headLabel 头部标签
     * @param dataList  数据列表
     * @param filePath  文件路径
     * @param addFlag   是否追加
     */
    public static void writeToCsv(String[] headLabel, List<String[]> dataList, String filePath, boolean addFlag) {
        BufferedWriter buffWriter = null;
        try {
            buffWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, addFlag),"GBK"));
//            if (headLabel != null && headLabel.length > 0) {
//                buffWriter.write(String.join(",", headLabel));
//                buffWriter.newLine();
//            }

            for (String[] rowStr : dataList) {
                if (Objects.nonNull(rowStr)) {
                    String tmp = FileUtil.convertToCSVFormat(rowStr);
                    buffWriter.write(tmp);
                    buffWriter.newLine();
                }
            }
            //刷新流，也就是把缓存中剩余的内容输出到文件
            buffWriter.flush();
        } catch (Exception e) {
            System.out.println("writeToCsv... 写入csv出现异常");
        } finally {
            try {
                //关闭流
                if (buffWriter != null) {
                    buffWriter.close();
                }
            } catch (IOException e) {
                System.out.println("writeToCsv... 写入csv出现异常");
            }
        }
    }


    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    public static String convertToCSVFormat(String[] data) {
        return Stream.of(data)
                .map(FileUtil::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public static void main(String[] args) {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"123", "大苏打"});
        data.add(new String[]{"123", "大s 苏打"});
        FileUtil.writeToCsv(new String[]{"col1", "col2"}, data, "./test.csv", true);

    }
}
