/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filety.data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author palmerovich
 */
public class Value implements Comparable<Object>, Serializable {

    String value;
    String replacement;
    List<FileFound> fileFoundList;

    public Value(String value, String replacement, List<FileFound> fileFoundList) {
        this.value = value;
        this.replacement = replacement;
        this.fileFoundList = fileFoundList;
    }

    boolean contains(FileFound fileFound) {
        for (FileFound fileFoundList : fileFoundList) {
            if (fileFoundList.file.getAbsolutePath().equals(fileFound.file.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getReplacement() {
        return replacement;
    }

    public void replace(Property property) {
        for (FileFound fileFound : fileFoundList) {
            fileFound.replaceOnText(property, this);
        }
    }

    @Override
    public String toString() {
        String label = value.equals(replacement) ? "=" : "!";
        return label +" "+ value + " -> " + replacement;
    }

    @Override
    public int compareTo(Object t) {
        if (t instanceof Value) {
            return this.value.compareTo(((Value) t).value);
        }
        return 0;
    }

}
