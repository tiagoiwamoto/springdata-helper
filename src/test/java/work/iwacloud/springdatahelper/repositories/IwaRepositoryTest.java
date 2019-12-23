package work.iwacloud.springdatahelper.repositories;


/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 22/12/2019 - 13:01
 */

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import work.iwacloud.springdatahelper.objects.DataTransfer;
import work.iwacloud.springdatahelper.objects.IwaTable;
import work.iwacloud.springdatahelper.enums.StatusOperation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IwaRepositoryTest {

    @Autowired
    private IwaRepository iwaRepository;

    @Test
    public void select() {
        List<Map> result = (List<Map>) iwaRepository.select("select u.* from TB_USERS u", true);
        Assert.assertEquals(10, result.size());
        result = (List<Map>) iwaRepository.select("call select *, UPDATED_AT from TB_USERS", true);
        Assert.assertEquals(0, result.size());
        result = (List<Map>) iwaRepository.select("call select * from TB_USERS", true);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void save() {
        LinkedList<IwaTable> columns = new LinkedList<>();
        columns.add(new IwaTable("NAME", "Name saved with repository"));
        columns.add(new IwaTable("BIRTHDAY", new SimpleDateFormat("yyyy-MM-dd").format(new Date(1984, 12, 30))));
        columns.add(new IwaTable("SEX", "M"));
        columns.add(new IwaTable("IS_ACTIVE", true));
        columns.add(new IwaTable("SALARY", 38689.59));
        columns.add(new IwaTable("CREATED_AT", new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
        DataTransfer<StatusOperation, Object> resp = iwaRepository.save(columns, "TB_USERS");
        Assert.assertEquals(StatusOperation.EXECUTED, resp.getResult());
    }

    @Test
    public void update() {
        //NAME, BIRTHDAY, SEX, IS_ACTIVE, SALARY, CREATED_AT
        LinkedList<IwaTable> columns = new LinkedList<>();
        columns.add(new IwaTable("IS_ACTIVE", false));
        columns.add(new IwaTable("SALARY", 12587.59));
        DataTransfer<StatusOperation, Object> resp = iwaRepository.update(columns, "TB_USERS", new IwaTable("ID", 1));
        Assert.assertEquals(StatusOperation.EXECUTED, resp.getResult());
    }

    @Test
    public void rawQuery() {
        String query = "delete from TB_USERS where ID = 1";
        DataTransfer<StatusOperation, Object> resp = iwaRepository.rawQuery(query);
        Assert.assertEquals(StatusOperation.EXECUTED, resp.getResult());
    }
}