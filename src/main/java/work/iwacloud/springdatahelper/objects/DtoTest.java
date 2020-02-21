package work.iwacloud.springdatahelper.objects;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoTest {
    private int id;
    private String name;
    private String sex;
    private Boolean isActive;
    private Float salary;
}
