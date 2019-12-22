package work.iwacloud.springdatahelper.repositories;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 22/12/2019 - 00:31
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import work.iwacloud.springdatahelper.helper.TableHelper;
import work.iwacloud.springdatahelper.objects.DataTransfer;
import work.iwacloud.springdatahelper.objects.IwaTable;
import work.iwacloud.springdatahelper.objects.StatusOperation;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.LinkedList;

@SuppressWarnings("all")
public class IwaRepository {

    private static final Long TIME_TO_EXECUTE = 2000L;
    private EntityManagerFactory entityManagerFactory;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public IwaRepository(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Persist on table of database
     * using spring-data entitymanager
     * @param customQueries
     * @param table
     * @return
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
            while(timesExecuted < 3){
                Integer queryResult = query.executeUpdate();
                if(queryResult > 0){
                    em.getTransaction().commit();
                    return new DataTransfer<>(StatusOperation.EXECUTED, "");
                }else{
                    Thread.sleep(TIME_TO_EXECUTE);
                    timesExecuted++;
                }
            }
            return new DataTransfer(StatusOperation.ABORTED, "");

        }catch (Exception e){
            //new ProcessingException().tratarException(e, getClass().getSimpleName());
            return new DataTransfer(StatusOperation.ERROR, "");
        }finally {
            if(em.isOpen()){
                em.clear();
                em.close();
            }
        }
    }
}
