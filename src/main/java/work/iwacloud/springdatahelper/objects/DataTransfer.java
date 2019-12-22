package work.iwacloud.springdatahelper.objects;


/**
 * Created by: Tiago Henrique Iwamoto
 * Email: tiago.iwamoto@gmail.com
 * System Analyst
 * 22/10/2019 - 21:15
 *
 * This class is used to transfer objects in application or
 * return
 */
public class DataTransfer<T, M> {

    /* Can receive a boolean, integer, enum, HttStatus */
    private T result;

    /* A message to identify the result */
    private String message;

    /* Any Object, List, Map */
    private M any;

    public DataTransfer(T result, String message) {
        this.result = result;
        this.message = message;
    }

    public DataTransfer(T result, String message, M any) {
        this.result = result;
        this.message = message;
        this.any = any;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public M getAny() {
        return any;
    }

    public void setAny(M any) {
        this.any = any;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataObject{");
        sb.append("result=").append(result);
        sb.append(", message='").append(message).append('\'');
        sb.append(", any=").append(any);
        sb.append('}');
        return sb.toString();
    }
}

