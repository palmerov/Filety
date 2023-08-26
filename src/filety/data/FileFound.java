/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filety.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 *
 * @author palmerovich
 */
public class FileFound {

    public FileFound(File file) {
        this.file = file;
        read();
    }

    public final File file;
    private String text;
    private boolean edited = false;

    public void read() {
        Charset encoding = Charset.forName("UTF-8"); // Codificación del archivo
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), encoding))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            reader.close();
            text = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write() {
        if (edited) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write(text);
                bufferedWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(FileFound.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getText() {
        return text;
    }

    public File getFile() {
        return file;
    }

    public void replaceOnText(Property property, Value value) {
        if (!value.value.equals(value.replacement)) {
            edited = true;
            int start, end;
            String chain = property.startChain + value.value + property.endChain;
            while ((start = text.indexOf(property.startChain + value.value + property.endChain)) >= 0) {
                end = start + chain.length();
                if (end <= text.length()) {
                    text = text.substring(0, start) + property.startChain + value.replacement + property.endChain + text.substring(end);
                }
            }
        }
    }

}
