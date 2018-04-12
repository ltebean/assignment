package assignment;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by leo on 2018/4/11.
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("Enter file name:");
        Scanner reader = new Scanner(System.in);
        String fileName = reader.nextLine();
        reader.close();

        Gson gson = new Gson();
        FileReader fileReader;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return;
        }
        JsonReader jsonReader = new JsonReader(fileReader);
        Data data = gson.fromJson(jsonReader, Data.class);

        BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Order> finishQueue = new LinkedBlockingQueue<>();

        Kitchen kitchen = new Kitchen(orderQueue, finishQueue);
        FinishQueueListener listener = new FinishQueueListener(data.customers.size(), finishQueue);
        new Thread(kitchen).start();
        new Thread(listener).start();
        Register register = new Register(orderQueue);

        int counter = 1;
        for (Map<String, List<String>> customer: data.customers) {
            Order order = new Order();
            order.items = customer.get("order").stream().map(name -> {
                Item item = new Item();
                item.name = name;
                item.category = data.getCategory(name);
                return item;
            }).collect(Collectors.toList());
            order.id = counter;
            register.takeOrder(order);
            counter++;
        }
    }



    static class Register {

        BlockingQueue<Order> queue;

        public Register(BlockingQueue<Order> queue) {
            this.queue = queue;
        }

        public synchronized void takeOrder(Order order) {
            try {
                Thread.sleep(1500);
                order.startTime = LocalDateTime.now();
                queue.put(order);
                System.out.println(String.format("%s %s %d", Utils.currentTime(), "Starting order", order.id));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Appliance {

        Semaphore semaphore;

        int seconds;

        BlockingQueue<Order> finishingQueue;

        public Appliance(int items, int seconds, BlockingQueue<Order> finishQueue) {
            this.finishingQueue = finishQueue;
            this.semaphore = new Semaphore(items);
            this.seconds = seconds;
        }

        public void handle(Order order, Item item)  {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        Thread.sleep(seconds * 1000);
                        semaphore.release();
                        finishingQueue.put(order);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    public static class Kitchen implements Runnable {

        BlockingQueue<Order> orderQueue;

        Appliance deepFryer;
        Appliance grill;
        Appliance milkshakeMaker;
        Appliance drinkMachine;

        public Kitchen(BlockingQueue<Order> orderQueue, BlockingQueue<Order> finishQueue) {
            this.orderQueue = orderQueue;

            deepFryer = new Appliance(4, 2, finishQueue);
            grill = new Appliance(5, 5, finishQueue);
            milkshakeMaker = new Appliance(2, 3, finishQueue);
            drinkMachine = new Appliance(2, 2, finishQueue);


        }

        @Override
        public void run() {
            while (true) {
                try {
                    Order order = orderQueue.take();
                    for (Item item: order.items) {
                        handleItem(order, item);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void handleItem(Order order, Item item) throws InterruptedException {
            if (item.category.equals("deep fryer")) {
                deepFryer.handle(order, item);
            } else if (item.category.equals("grill")) {
                grill.handle(order, item);
            } else if (item.category.equals("milkshake maker")) {
                milkshakeMaker.handle(order, item);
            } else if (item.category.equals("drink machine")) {
                drinkMachine.handle(order, item);
            } else {
                System.out.println("category not found");
            }
        }


    }

    static class FinishQueueListener implements Runnable {
        BlockingQueue<Order> queue;
        int counter = 0;
        int totalCount = 0;
        public FinishQueueListener(int totalCount, BlockingQueue<Order> queue) {
            this.totalCount = totalCount;
            this.queue = queue;
        }

        Mysql mysql = new Mysql();

        @Override
        public void run() {
            while(true) {
                try {
                    Order order = queue.take();
                    order.finishedCount.incrementAndGet();
//                    System.out.println(order.id + " " + order.finishedCount.intValue());
                    if (order.finished()) {
                        order.endTime = LocalDateTime.now();
                        mysql.insert(order);
                        System.out.println(String.format("%s %s %d", Utils.currentTime(), "Completed order", order.id));
                        counter++;
                        if (counter == totalCount) {
                            System.out.println("All orders complete");
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
