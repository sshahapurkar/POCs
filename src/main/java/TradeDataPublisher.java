import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;

public class TradeDataPublisher {
    private static final Executor SERVER_EXECUTOR = Executors.newFixedThreadPool(10);
    private static final int PORT = 9999;
    private static final String DELIMITER = ":";
    private static final long EVENT_PERIOD_SECONDS = 1;
    private static final Random random = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        BlockingQueue<TradePOJO> eventQueue = new ArrayBlockingQueue<>(100);
        SteamingServer ss = new SteamingServer(eventQueue);
        SERVER_EXECUTOR.execute(ss);
        while (true) {
            eventQueue.put(generateEvent());
            Thread.sleep(TimeUnit.SECONDS.toMillis(EVENT_PERIOD_SECONDS));
        }
    }

    private static TradePOJO generateEvent() {
        int tradeId = random.nextInt(10);
        TradePOJO trade = null;
        if(tradeId <3)
            trade = new TradePOJO(tradeId, "EQUITY", "INDIA", "INR");
        else if (tradeId < 5)
            trade = new TradePOJO(tradeId, "FX", "UK", "GBP");
        else if (tradeId <10)
            trade = new TradePOJO(tradeId, "FI", "US", "USD");
        else
            trade = new TradePOJO(tradeId, "OTC", "AUS", "AUD");

        System.out.println("The event is --- "+trade);
        return trade;
    }

    private static class SteamingServer implements Runnable {
        private final BlockingQueue<TradePOJO> eventQueue;

        public SteamingServer(BlockingQueue<TradePOJO> eventQueue) {
            this.eventQueue = eventQueue;
        }

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(PORT);
                 Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                while (true) {
                    TradePOJO event = eventQueue.take();
                    System.out.println(String.format("Writing \"%s\" to the socket.", event));
                    Gson gson = new Gson();
                    String json = gson.toJson(event);
                    out.println(json);
                }
            } catch (IOException|InterruptedException e) {
                throw new RuntimeException("Server error", e);
            }
        }
    }
}