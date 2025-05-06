package co.edu.unibague.ws.models.db;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * POJO to the query bills stored procedure
 *
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class Bill {

    /**
     * Bill's owner
     */
    private String client;

    /**
     * Bill amount
     */
    private double value;

    /**
     * Bill's payment limit date
     */
    private Timestamp dueDate;

    /**
     * Bill id in Oracle DB
     */
    private int id;

    /**
     * If is a normal bill, a credit quota bill, or other.
     */
    private String type;

    /**
     * Bill id in Informix DB
     */
    private String reference;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    /**
     * Takes only the number of the Informix bill id, and removes the prefix
     * letters and padding zeros added on Iceberg.
     *
     * @return Informix formatted bill id
     */
    public String getReferenceNumber(){
        if(reference != null){
            Pattern pt = Pattern.compile("^[A-Z]{3}0*([1-9]\\d*)$");
            Matcher mt = pt.matcher(reference);
            if(mt.find())
                return mt.group(1);
        }
        return null;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
