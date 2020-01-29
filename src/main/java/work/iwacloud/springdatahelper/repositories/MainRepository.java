package work.iwacloud.springdatahelper.repositories;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 22/12/2019 - 00:31
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import work.iwacloud.springdatahelper.exceptions.IwaException;
import work.iwacloud.springdatahelper.helpers.TableHelper;
import work.iwacloud.springdatahelper.objects.DataTransfer;
import work.iwacloud.springdatahelper.enums.StatusMessages;
import work.iwacloud.springdatahelper.objects.DbColumn;
import work.iwacloud.springdatahelper.enums.StatusOperation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@Component
public class MainRepository<T> {

    private static final Long TIME_TO_EXECUTE = 1000L;
    private EntityManagerFactory entityManagerFactory;
    private JdbcTemplate jdbcTemplate;
    Logger logger = LoggerFactory.getLogger(MainRepository.class);

    @Autowired
    public MainRepository(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Method configured to return a object array,
     * in this context i have to set a Object.
     * @param select to execute and convert to a linkedlist
     * @exception when a invalid select query
     * @return a linkedlist of map
     */
    public List<T> select(String select) throws Exception {
        try {
            Gson gson = new Gson();
            LinkedList<Map> maps = (LinkedList<Map>) this.select(select, false);
            String json = gson.toJson(maps);
            List<T> dados = gson.fromJson(json, new TypeToken<List<T>>() {
            }.getType());
            return dados;
        }catch (Exception e){
            logger.error(String.format("Error to process query: %s", e.getMessage()), e);
            throw new IwaException(String.format("Error to process query: %s", e.getMessage()), e);
        }
    }

    /**
     * Method configured to return a json array,
     * in this context i have to set a Object.
     * @param select to execute and convert to a linkedlist
     * @param asList a boolean to choose if you want a list or json array
     * @throws Exception when a invalid select query
     * @return a linkedlist of map
     */
    public Object select(String select, Boolean asList) throws Exception {

        if(select.toLowerCase().startsWith("select ")) {
            try {
                return jdbcTemplate.query(select, new ResultSetExtractor<Object>() {

                    @Override
                    public Object extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        try {
                            return new TableHelper().serializeResultSet(resultSet);
                        } catch (IwaException e) {
                            if (asList) {
                                return new ArrayList<>();
                            } else {
                                return new JSONArray();
                            }
                        }
                    }

                });
            }catch (Exception e){
                logger.error(String.format("Error to process query: %s", e.getMessage()), e);
                if(e instanceof BadSqlGrammarException)
                    throw new IwaException(String.format("Error to process query: %s", e.getMessage()), e);

                if (asList) {
                    return new ArrayList<>();
                } else {
                    return new JSONArray();
                }
            }
        }else{
            logger.debug(String.format("It is not a valid select query: %s", select));
            throw new IwaException(String.format("It is not a valid select query: %s", select));
        }
    }

    /**
     * Persist on table of database
     * using spring-data entitymanager
     * @param customQueries to execute a save command
     * @param table name to persist
     * @return a dto with result
     */
    public DataTransfer<StatusOperation, Object> save(LinkedList<DbColumn> customQueries, String table){
        String columns = "";
        int position = 1;
        int timesExecuted = 0;

        for(DbColumn value : customQueries){
            if(position == customQueries.size()){
                columns += value.getColumn();
            }else{
                columns += value.getColumn() + ", ";
            }
            ++position;
        }

        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            String sqlCommand = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, new TableHelper().getNumberOfColumns(columns));
            Query query = em.createNativeQuery(sqlCommand);
            position = 1;
            for (DbColumn value : customQueries) {
                query.setParameter(position++, value.getValue());
            }
            em.getTransaction().begin();
            /* Try to execute 3 times */
            while(timesExecuted < 3){
                Integer queryResult = query.executeUpdate();
                if(queryResult > 0){
                    em.getTransaction().commit();
                    return new DataTransfer<>(StatusOperation.EXECUTED, StatusMessages.EXECUTED.value());
                }else{
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }
            logger.warn("Timeout reached, aborting query");
            return new DataTransfer(StatusOperation.ABORTED, StatusMessages.ABORTED.value());

        }catch (Exception e){
            logger.error(String.format("Error to process query: %s", e.getMessage()), e);
            return new DataTransfer(StatusOperation.ERROR, StatusMessages.ERROR.value());
        }finally {
            if(em.isOpen()){
                em.clear();
                em.close();
            }
        }
    }

    /**
     * Update a line on table of database
     * using spring-data entitymanager
     * @param tableColumns to execute a save command
     * @param table name to persist
     * @param where condition to update a row
     * @return a dto with result
     */
    public DataTransfer<StatusOperation, Object> update(LinkedList<DbColumn> tableColumns, String table, DbColumn where){
        String columns = "";
        int position = 1;
        int timesExecuted = 0;

        for(DbColumn value : tableColumns){
            if(position == tableColumns.size()){
                columns += value.getColumn() + " = ?";
            }else{
                columns += value.getColumn() + " = ? , ";
            }
            ++position;
        }

        EntityManager em = entityManagerFactory.createEntityManager();

        try{
            String whereToUpdate = String.format("%s = %s", where.getColumn(), where.getValue());

            String sqlCommand = String.format("UPDATE %s SET %s WHERE %s", table, columns, whereToUpdate);
            Query query = em.createNativeQuery(sqlCommand);
            position = 1;
            for (DbColumn value : tableColumns) {
                query.setParameter(position++, value.getValue());
            }
            em.getTransaction().begin();
            while (timesExecuted < 3){
                Integer queryResult = query.executeUpdate();
                if(queryResult > 0){
                    em.getTransaction().commit();
                    return new DataTransfer<>(StatusOperation.EXECUTED, StatusMessages.EXECUTED.value());
                }else{
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }
            logger.warn("Timeout reached, aborting query");
            return new DataTransfer(StatusOperation.ABORTED, StatusMessages.ABORTED.value());
        }catch (Exception e){
            logger.error(String.format("Error to process query: %s", e.getMessage()), e);
            return new DataTransfer(StatusOperation.ERROR, StatusMessages.ERROR.value());
        }finally {
            if(em.isOpen()){
                em.clear();
                em.close();
            }
        }
    }

    /**
     * execute a pure sql command
     * using spring-data entitymanager
     * @param command to exeute on database
     * @return a dto with result
     */
    public DataTransfer<StatusOperation, Object> rawQuery(String command){
        EntityManager em = entityManagerFactory.createEntityManager();

        try{
            int timesExecuted = 0;
            String sqlCommand = String.format(command);
            Query query = em.createNativeQuery(sqlCommand);
            em.getTransaction().begin();
            /* Try to execute 3 times */
            while(timesExecuted < 3) {
                Integer queryResult = query.executeUpdate();
                if (queryResult > 0) {
                    em.getTransaction().commit();
                    return new DataTransfer(StatusOperation.EXECUTED, StatusMessages.EXECUTED.value());
                } else {
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }
            logger.warn("Timeout reached, aborting query");
            return new DataTransfer(StatusOperation.ABORTED, StatusMessages.ABORTED.value());
        }catch (Exception e){
            logger.error(String.format("Error to process query: %s", e.getMessage()), e);
            return new DataTransfer(StatusOperation.ERROR, StatusMessages.ERROR.value());
        }finally {
            if(em.isOpen()){
                em.clear();
                em.close();
            }
        }
    }
}
