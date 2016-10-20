package Helper;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableComparable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kedarvdm
 */
public class KeyValueTuple implements WritableComparable<KeyValueTuple> {
    private String key;
    private long value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public void write(DataOutput d) throws IOException {
        d.writeUTF(key);
        d.writeLong(value);
    }

    @Override
    public void readFields(DataInput di) throws IOException {
        key=di.readUTF();
        value= di.readLong();
    }

    @Override
    public int compareTo(KeyValueTuple that) {
        //return (int)(that.getValue()-this.getValue());
        if (this.getKey().equals(that.getKey())) {
            return 0;
        }
        if (that.getValue() > this.getValue()) {
            return 1;
        }else
        {
            return -1;
        }
    }
    
    @Override
    public String toString()
    {
        return key+"\t"+value;
    }
}
