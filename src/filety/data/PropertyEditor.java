/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filety.data;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 *
 * @author palmerovich
 */
public class PropertyEditor {

    public File rootFile = new File("");
    public String fileNameFilter = "";
    public boolean fullname = false;
    public List<Property> propertyList;
    private List<FileFound> fileFoundList;

    public PropertyEditor() {
        propertyList = new ArrayList<>();
    }

    public void setFileNameFilter(String fileNameFilter) {
        this.fileNameFilter = fileNameFilter.toLowerCase();
    }

    public void setRootFile(File rootFile) {
        this.rootFile = rootFile;
    }

    public void setFullname(boolean fullname) {
        this.fullname = fullname;
    }

    public void saveValues() {
        try {
            FileWriter fileWriter = new FileWriter(new File("conf.txt"));
            fileWriter.write(rootFile.getAbsolutePath() + "\n");
            fileWriter.write(fileNameFilter + "\n");
            fileWriter.write((fullname ? 1 : 0) + "\n");
            fileWriter.close();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File("props.fty")));
            objectOutputStream.writeObject(propertyList);
            objectOutputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadValues() {
        try {
            FileReader fileReader = new FileReader(new File("conf.txt"));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            rootFile = new File(bufferedReader.readLine());
            fileNameFilter = bufferedReader.readLine();
            fullname = "1".equals(bufferedReader.readLine());
            bufferedReader.close();

            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("props.fty")));
            propertyList = (ArrayList<Property>) objectInputStream.readObject();
            for (Property property : propertyList) {
                property.values = new ArrayList<>();
            }
            objectInputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PropertyEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void replace(ProcessCallback callback) {
        callback.onUpdateProgress("Iniciando...");
        int i = 1;
        for (Property property : propertyList) {
            if (!callback.onUpdateProgress("Replacing property " + i)) {
                callback.onUpdateProgress("Canceled");
                callback.onCancel();
            }
            int j = 1;
            for (Value value : property.values) {
                if (!callback.onUpdateProgress("Replacing property " + i + ", value " + j)) {
                    callback.onUpdateProgress("Canceled");
                    callback.onCancel();
                    return;
                }
                j++;
                value.replace(property);
            }
            i++;
        }
        callback.onUpdateProgress("Saving...");
        for (FileFound fileFound : fileFoundList) {
            fileFound.write();
        }
        callback.onUpdateProgress("Success!!");
        callback.onFinish();
    }

    public void analyze(ProcessCallback callback) {
        for (Property property : propertyList) {
            property.values.clear();
        }
        fileFoundList = new ArrayList<>();
        if (!callback.onUpdateProgress("Searching for files")) {
            callback.onUpdateProgress("Canceled");
            callback.onCancel();
            return;
        }
        foundFiles(rootFile, fileFoundList);
        if (!callback.onUpdateProgress(fileFoundList.size() + " found files.")) {
            callback.onUpdateProgress("Canceled");
            callback.onCancel();
            return;
        }
        int i = 1;
        for (Property property : propertyList) {
            if (!callback.onUpdateProgress("Analizing property " + i + "...")) {
                callback.onUpdateProgress("Canceled");
                callback.onCancel();
                return;
            }
            analyzePropertyInFilesFound(fileFoundList, property);
            i++;
        }
        callback.onUpdateProgress("Finished!!");
        callback.onFinish();
    }

    void foundFiles(File root, List<FileFound> fileFoundList) {
        String[] filenameFilters = this.fileNameFilter.split("\\|");
        File[] fileChildren = root.listFiles();
        if (fileChildren != null) {
            for (File file : fileChildren) {
                if (file.isDirectory()) {
                    foundFiles(file, fileFoundList);
                } else {
                    if (fullname) {
                        for (String filter : filenameFilters) {
                            if (file.getName().equals(filter)) {
                                fileFoundList.add(new FileFound(file));
                            }
                        }
                    } else {
                        for (String filter : filenameFilters) {
                            if (file.getName().toLowerCase().contains(filter)) {
                                fileFoundList.add(new FileFound(file));
                            }
                        }
                    }
                }
            }
        }
    }

    void analyzePropertyInFilesFound(List<FileFound> fileFoundList, Property property) {
        HashMap<String, Value> valMap = new HashMap<>();
        for (FileFound fileFound : fileFoundList) {
            matchesFileWithProperty(fileFound, property, valMap);
        }
        property.sortValues();
    }

    void matchesFileWithProperty(FileFound fileFound, Property property, HashMap<String, Value> valuesMap) {
        String fileText = fileFound.getText();
        int index = 0;
        while (index >= 0) {
            index = fileText.indexOf(property.startChain, index);
            if (index >= 0) {
                int startValuePosition = index + property.startChain.length();
                int endValuePosition = property.endChain.isEmpty() ? fileText.length() : fileText.indexOf(property.endChain, startValuePosition);
                if (endValuePosition >= 0) {
                    String valueText = fileText.substring(startValuePosition, endValuePosition);
                    Value value;
                    if ((value = valuesMap.getOrDefault(valueText, null)) != null) {
                        boolean contains = value.contains(fileFound);
                        if (contains) {
                            //fileFoundValuePositioned.positions.add(startValuePosition);
                        } else {
                            value.fileFoundList.add(fileFound);
                        }
                    } else {
                        value = new Value(valueText, valueText, new ArrayList<>());
                        value.fileFoundList.add(fileFound);
                        valuesMap.put(valueText, value);
                        property.values.add(value);
                    }
                    index = endValuePosition + property.endChain.length();
                } else {
                    break;
                }
            }
        }
    }

    public static interface ProcessCallback {

        boolean onUpdateProgress(String text);

        void onCancel();

        void onFinish();
    }
}
