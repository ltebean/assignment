package assignment;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by leo on 2018/4/11.
 */
public class Data {

    @SerializedName("Menu")
    public Map<String, List<String>> menu;

    @SerializedName("Customers")
    public List<Map<String, List<String>>> customers;

    public String getCategory(String name) {
        for (Map.Entry<String, List<String>> entry : menu.entrySet()) {
            if (entry.getValue().contains(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
