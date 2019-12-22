package work.iwacloud.springdatahelper.helper;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 21/12/2019 - 23:16
 */

import work.iwacloud.springdatahelper.exception.IwaException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SuppressWarnings("all")
public class TableHelper {


    /**
     * Convert a column from database to a java pattern
     * example: 'TB_USERS_COLUMN' to 'tbUsersColumn'
     * @param column
     * @return
     * @throws Exception
     */
    public String convertColumnToJava(String column) throws IwaException {

        if(column == null){
            return "column";
        }

        if(column.isEmpty()){
            throw new IwaException("Column name is empty");
        }

        /*Starting the variable*/
        String columnToJava = "";

        /* Replace whitespace for '_' */
        column = column.replaceAll("\\s","_");

        /*Split by '_' to create a array of words*/
        String[] columns = column.split("_");

        for(int currentWord = 0; currentWord < columns.length; currentWord++){
            if(currentWord == 0){
                /*Convert the first word to lowercase*/
                columnToJava += columns[currentWord].toLowerCase();
            }else{
                /*Convert the first char to uppercase*/
                columnToJava += columns[currentWord].substring(0, 1).toUpperCase() +
                        columns[currentWord].substring(1).toLowerCase();
            }
        }

        return columnToJava;
    }

    /**
     * This method receive the fields of table
     * ex: select (a, b, c) from table;
     * you only pass the a,b,c as string
     * and this return ?, ?, ?
     * to use with prepared statement.
     * @param sql
     * @return
     */
    public String getNumberOfColumns(String sql) throws IwaException {
        if(sql.isEmpty()){
            throw new IwaException("Value null or empty");
        }
        String[] columns = sql.split(",");
        String value = "";
        for(int columnPosition = 0; columnPosition < columns.length; columnPosition++){
            if(columnPosition + 1 == columns.length){
                value += "?";
            }else{
                value += "?, ";
            }
        }
        return value;
    }

    /**
     * This method receive a resultset and convert to a list of map.
     * you can serialize to a pojo or just return the list.
     * @param rs
     * @return
     * @throws IwaException
     * @throws SQLException
     */
    public LinkedList<Map> serializeResultSet(ResultSet rs) throws IwaException, SQLException {

        if(rs == null){
            throw new IwaException("Your ResultSet is null");
        }

        LinkedList<Map> lines = new LinkedList<>();

        try {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();

                for (int column = 1; column <= rs.getMetaData().getColumnCount(); column++) {

                    //region TYPE OF COLUMNS
                    if (rs.getObject(column).getClass() == String.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getString(column)
                        );
                    } else if (rs.getObject(column).getClass() == Integer.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getInt(column)
                        );
                    } else if (rs.getObject(column).getClass() == Long.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getLong(column)
                        );
                    } else if (rs.getObject(column).getClass() == Double.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getDouble(column)
                        );
                    } else if (rs.getObject(column).getClass() == Float.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getFloat(column)
                        );
                    } else if (rs.getObject(column).getClass() == Boolean.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getBoolean(column)
                        );
                    } else if (rs.getObject(column).getClass() == BigDecimal.class) {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getBigDecimal(column)
                        );
                    } else if (rs.getObject(column).getClass() == Timestamp.class) {
                        try {
                            map.put(this.convertColumnToJava(
                                    rs.getMetaData().getColumnLabel(column)),
                                    rs.getTimestamp(column)
                            );
                        } catch (Exception e) {
                            map.put(this.convertColumnToJava(
                                    rs.getMetaData().getColumnLabel(column)),
                                    rs.getObject(column)
                            );
                        }
                    } else if (rs.getObject(column).getClass() == Date.class) {
                        try {
                            map.put(this.convertColumnToJava(
                                    rs.getMetaData().getColumnLabel(column)),
                                    rs.getDate(column)
                            );
                        } catch (Exception e) {
                            map.put(this.convertColumnToJava(
                                    rs.getMetaData().getColumnLabel(column)),
                                    rs.getObject(column)
                            );
                        }
                    } else if (rs.getObject(column).getClass() == Time.class) {
                        try {
                            map.put(this.convertColumnToJava(
                                    rs.getMetaData().getColumnLabel(column)),
                                    rs.getTime(column)
                            );
                        } catch (Exception e) {
                            map.put(this.convertColumnToJava(
                                    rs.getMetaData().getColumnLabel(column)),
                                    rs.getObject(column)
                            );
                        }
                    } else {
                        map.put(this.convertColumnToJava(
                                rs.getMetaData().getColumnLabel(column)),
                                rs.getObject(column)
                        );
                    }
                    //endregion

                }
                lines.add(map);
            }

            return lines;
        }catch (Exception e){
            return new LinkedList<>();
        }

    }

}
