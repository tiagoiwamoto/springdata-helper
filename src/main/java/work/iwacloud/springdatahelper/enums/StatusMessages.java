package work.iwacloud.springdatahelper.enums;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 22/12/2019 - 11:26
 */

public enum StatusMessages {

    PENDING(""),
    EXECUTING(""),
    EXECUTED("Operation complete"),
    CANCELED(""),
    ABORTED("Operation aborted"),
    ERROR("Can't complete operation");

    private String message;

    StatusMessages(String message) {
        this.message = message;
    }

    public String value() {
        return message;
    }
}
