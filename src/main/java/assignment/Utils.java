package assignment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leo on 2018/4/11.
 */
public class Utils {

    public static String currentTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SS");
        return dateFormat.format(new Date());
    }
}
