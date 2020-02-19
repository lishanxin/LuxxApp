package net.senink.piservice.http;

/**
 * Created by wensttu on 2016/7/8.
 */
public class NameValuePair {
    private String pairName;
    private String pairValue;

    public NameValuePair(String name, String value){
        super();

        pairName = name;
        pairValue = value;
    }
    public String getName(){
        return pairName;
    }

    public String getValue(){
        return pairValue;
    }

    public String toString(){
        return pairName + "=" + pairValue;
    }

    public boolean equals(Object object) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public Object clone() throws CloneNotSupportedException {
        throw new RuntimeException("Stub!");
    }
}
