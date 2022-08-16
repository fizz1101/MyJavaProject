import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeZoneTest {

    public static void main(String[] args) {
        TimeZoneTest test = new TimeZoneTest();

        final long ONE_DAY_MILLIS = 1000 * 3600 * 24L;
        Date date0 = new Date(ONE_DAY_MILLIS * 0);
        System.out.println("start date=" + test.format(date0) + "millis=" +  date0.getTime());
        System.out.println("********************");
        int count = 0;

        for (int i = 0; i < 300000; ++i) {
            long millis = ONE_DAY_MILLIS * i;
            Date date = new Date(millis);

            if (date.getHours() != 8 || date.getMinutes() != 0 || date.getSeconds() != 0) {
                System.out.println("i = " + i + " | " + test.format(date) + "| " + date + ", millis=" + millis);

                GregorianCalendar newDate = new GregorianCalendar();
                newDate.setTimeInMillis(millis);
                System.out.println("i = " + i + " | newDate " + newDate.getTimeInMillis() + "| " + newDate);
                count++;
            }
        }
        System.out.println("count=" + count);
    }

    public String format(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        return dateStr;
    }

}
