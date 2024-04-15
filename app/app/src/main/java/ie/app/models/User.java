package ie.app.models;

import java.util.ArrayList;

public class User {
    ArrayList<Field> fields;
    public User() {
        fields = new ArrayList<>();
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public void addField(Field field) {
        fields.add(field);
    }

    @Override
    public String toString() {
        String ret = "size: " + fields.size() + "\n";
        for(Field field: fields) ret += field.name + "  ";
        return ret;
    }
}