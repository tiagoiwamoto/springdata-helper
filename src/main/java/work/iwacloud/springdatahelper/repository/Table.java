package work.iwacloud.springdatahelper.repository;

/*
 * Tiago Henrique Iwamoto
 * tiago.iwamoto@gmail.com
 * https://www.linkedin.com/in/tiago-iwamoto
 * System Analyst | MBA Business Intelligence
 * 21/12/2015 - 22:55
 */

import java.util.Objects;

/**
 * This is a model with a column name and a value to persist in database
 */
@SuppressWarnings("all")
public class Table {

    //region ATRIBUTES
    private String column;

    private Object value;
    //endregion

    //region CONSTRUCTORS

    public Table(String column, Object value) {
        this.column = column;
        this.value = value;
    }

    //endregion

    //region SUPPORT
    public Table withColumn(String column){
        this.column = column;
        return this;
    }

    public Table withValue(Object value){
        this.value = value;
        return this;
    }
    //endregion

    //region GETTERS

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    //endregion

    //region OVERRIDE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(column, table.column) &&
                Objects.equals(value, table.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("column='").append(column).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }

    //endregion


}
