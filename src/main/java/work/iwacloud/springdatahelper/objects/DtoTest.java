package work.iwacloud.springdatahelper.objects;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTest {
    private int Id;
    private String Name;
    private Date Birthday;
    private String Sex;
    private Boolean IsActive;
    private Float Salary;
    private Date CreatedAt;
}
