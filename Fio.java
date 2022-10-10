import java.io.File;
import java.util.Date;
import static java.util.concurrent.TimeUnit.*;

public class Fio {
    public static void main(String[] args) throws InterruptedException {
        Date previous = new Date();

        //Pause for 4 seconds
        Thread.sleep(2000);

        Date now = new Date();

        long MAX_DURATION = MILLISECONDS.convert(3, SECONDS);

        long duration = now.getTime() - previous.getTime();

        if (duration >= MAX_DURATION) {
            System.out.println("Mais do que 3 segundos");
        }
    }
}
