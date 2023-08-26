/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filety.data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 *
 * @author palmerovich
 */
public class Property implements Serializable {

    private static final long serialVersionUID = 1L;
    public final String startChain, endChain;
    public transient List<Value> values;

    public Property(String startChain, String endChain) {
        this.startChain = startChain;
        this.endChain = endChain;
        this.values = new ArrayList<>();
    }

    @Override
    public String toString() {
        return StringUtils.toRead(startChain) + "{val}" + StringUtils.toRead(endChain);
    }

    void sortValues() {
        Object[] array = values.toArray();
        Arrays.sort(array, 0, values.size());
        values.clear();
        for (Object object : array) {
            values.add((Value) object);
        }
    }

}
