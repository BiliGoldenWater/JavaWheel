package com.company.module;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBOperator{
    public Connection conn;
    public DBOperator(String fileName){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:"+fileName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void finalize(){
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void executeCommand(String sqlCommand) throws SQLException{
        PreparedStatement ptmt = conn.prepareStatement(sqlCommand);
        ptmt.execute();
        ptmt.close();
    }

    public Map<ResultSet, PreparedStatement> executeCommand(String sqlCommand,boolean hasReturn) throws SQLException{
        PreparedStatement preparedStatement = conn.prepareStatement(sqlCommand);
        ResultSet rs = null;
        Map<ResultSet, PreparedStatement> resultSetPreparedStatementMap = new HashMap<>();
        if (hasReturn){
            rs = preparedStatement.executeQuery();
//            System.out.println(rs.getString("name"));
        } else {
            preparedStatement.executeQuery();
        }
        resultSetPreparedStatementMap.put(rs, preparedStatement);
        return resultSetPreparedStatementMap;
    }

    public int createTable(String tableName, Map<String,String> nameAndType){
        StringBuilder command = new StringBuilder();
        command.append("create table ").append(tableName).append(" (");
        nameAndType.forEach((k, v)->{
            command.append(k).append(" ").append(v);
            if(k!=nameAndType.keySet().toArray()[nameAndType.keySet().size()-1]){
                command.append(", ");
            }
        });
        command.append(")");
        try {
            executeCommand(command.toString());
            return 0;
        } catch (SQLException e) {
            return e.getMessage().indexOf(tableName);
        }
    }

    public Map<Integer, List<Object>> select(String tableName, String column, String argName, String argEqualValue, int columnNum){
        StringBuilder command = new StringBuilder();
        Map<Integer, List<Object>> result = new HashMap<>();
        command.append("select ")
                .append(column)
                .append(" from ")
                .append(tableName)
                .append(" where ")
                .append(argName)
                .append("=")
                .append(argEqualValue)
                .append(";");
        try {
            executeCommand(command.toString(), true).forEach((k,v)->{
                try {
                    for(int i=1; i<=columnNum; i++){
                        result.put(i, new ArrayList<>());
                    }

                    while (k.next()){
                        for(int i=1; i<=columnNum; i++){
                            List<Object> columnArray = result.get(i);
                            columnArray.add(k.getObject(i));
                            result.put(i, columnArray);
                        }
                    }
                    v.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean insert(String tableName, List<String> columns, List<String> values){
        if (columns == null || values == null || columns.size() != values.size() || columns.size() == 0) return false;

        StringBuilder command = new StringBuilder();
        command.append("insert into ").append(tableName).append(" (");

        for (int i = 0;i<columns.size();i++){
            command.append(columns.get(i))
                    .append(i < columns.size() - 1 ? ", " : "");
        }
        command.append(") values (");

        for (int i = 0;i<values.size();i++){
            try {
                Double.valueOf(values.get(i));
                command.append(values.get(i))
                        .append(i < values.size() - 1 ? ", " : "");
            } catch (Exception ignored){
                command.append("\"")
                        .append(values.get(i))
                        .append("\"")
                        .append(i < values.size() - 1 ? ", " : "");
            }

        }
        command.append(");");

        try {
            executeCommand(command.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public Connection getConnection(){
        return conn;
    }
}
