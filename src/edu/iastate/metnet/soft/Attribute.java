package edu.iastate.metnet.soft;

public class Attribute {
    private String key;
    private String value;

    public Attribute(String key, String value) {
        setKey(key);
        setValue(value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        if ((obj instanceof String)) {
            return key.equals(obj);
        }
        if ((obj instanceof Attribute)) {
            Attribute o = (Attribute) obj;
            return (getKey().equals(o.getKey())) && (getValue().equals(o.getValue()));
        }
        return false;
    }
}
