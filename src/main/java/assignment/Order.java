package assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by leo on 2018/4/11.
 */
public class Order {

    int id;

    List<Item> items;

    AtomicInteger finishedCount = new AtomicInteger(0);

    public boolean finished() {
        return items.size() == finishedCount.intValue();
    }

    LocalDateTime startTime;
    LocalDateTime endTime;

}
