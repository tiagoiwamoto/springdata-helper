package work.iwacloud.springdatahelper.repositories;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 22/12/2019 - 00:31
 */

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import work.iwacloud.springdatahelper.exceptions.IwaException;
import work.iwacloud.springdatahelper.helpers.TableHelper;
import work.iwacloud.springdatahelper.objects.DataTransfer;
import work.iwacloud.springdatahelper.enums.IwaMessages;
import work.iwacloud.springdatahelper.objects.IwaTable;
import work.iwacloud.springdatahelper.enums.StatusOperation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

@SuppressWarnings("all")
@Component
public class IwaRepository {

    private static final Long TIME_TO_EXECUTE = 1000L;
    private EntityManagerFactory entityManagerFactory;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public IwaRepository(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Method configured to return a json array,
     * in this context i have to set a Object.
     * @param select to execute and convert to a linkedlist
     * @return a linkedlist of map
     */
    public Object select(String select, Boolean asList){

        if(select.startsWith("select") || select.startsWith("SELECT")) {
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
                if (asList) {
                    return new ArrayList<>();
                } else {
                    return new JSONArray();
                }
            }
        }else{
            if (asList) {
                return new ArrayList<>();
            } else {
                return new JSONArray();
            }
        }

    }

    /**
     * Persist on table of database
     * using spring-data entitymanager
     * @param customQueries to execute a save command
     * @param table name to persist
     * @return a dto with result
     */
    public DataTransfer<StatusOperation, Object> save(LinkedList<IwaTable> customQueries, String table){
        String columns = "";
        int position = 1;
        int timesExecuted = 0;

        for(IwaTable value : customQueries){
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
            for (IwaTable value : customQueries) {
                query.setParameter(position++, value.getValue());
            }
            em.getTransaction().begin();
            /* Try to execute 3 times */
            while(timesExecuted < 3){
                Integer queryResult = query.executeUpdate();
                if(queryResult > 0){
                    em.getTransaction().commit();
                    return new DataTransfer<>(StatusOperation.EXECUTED, IwaMessages.EXECUTED.value());
                }else{
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }
            return new DataTransfer(StatusOperation.ABORTED, IwaMessages.ABORTED.value());

        }catch (Exception e){
            return new DataTransfer(StatusOperation.ERROR, IwaMessages.ERROR.value());
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
     * @return
     * @param customQueries to execute a save command
     * @param table name to persist
     * @param where condition to update a row
     * @return a dto with result
     */
    public DataTransfer<StatusOperation, Object> update(LinkedList<IwaTable> tableColumns, String table, IwaTable where){
        String columns = "";
        int position = 1;
        int timesExecuted = 0;

        for(IwaTable value : tableColumns){
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
            for (IwaTable value : tableColumns) {
                query.setParameter(position++, value.getValue());
            }
            em.getTransaction().begin();
            while (timesExecuted < 3){
                Integer queryResult = query.executeUpdate();
                if(queryResult > 0){
                    em.getTransaction().commit();
                    return new DataTransfer<>(StatusOperation.EXECUTED, IwaMessages.EXECUTED.value());
                }else{
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }

            return new DataTransfer(StatusOperation.ABORTED, IwaMessages.ABORTED.value());
        }catch (Exception e){
            return new DataTransfer(StatusOperation.ERROR, IwaMessages.ERROR.value());
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
                    return new DataTransfer(StatusOperation.EXECUTED, IwaMessages.EXECUTED.value());
                } else {
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }
            return new DataTransfer(StatusOperation.ABORTED, IwaMessages.ABORTED.value());
        }catch (Exception e){
            return new DataTransfer(StatusOperation.ERROR, IwaMessages.ERROR.value());
        }finally {
            if(em.isOpen()){
                em.clear();
                em.close();
            }
        }
    }
}
