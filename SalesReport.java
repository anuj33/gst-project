import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;
import java.io.IOException;
import java.util.*;
import java.util.Iterator;
import org.bson.Document;
import java.lang.Object;
import org.bson.types.ObjectId;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
public class SalesReport
{
    int firmId;
    /**
     * values of type
     * 0      weekly
     * 1      monthly
     * 2      yearly
     */
    int type;
    Date startDate;
    Date endDate;
    Vector<Product> product;
    Vector<Double> transactionAmount;
    Vector<String> xAxis = new Vector<String>();
    Vector<String> yAxis = new Vector<String>();
    Vector<Date> startBucket = new Vector<Date>();
    Vector<Date> endBucket = new Vector<Date>();
    SalesReport(int firmId,int type,Date startDate,Date endDate,Vector<Product> product)
    {
        this.firmId = firmId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.product = product;
    }
    SalesReport(int firmId,int type,Date startDate,Date endDate)
    {
        this.firmId = firmId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.product = product;
    }
    Date getStartOfDay(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Integer.parseInt(dateExtractor.getAttribute("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, 0, 0, 0);
        return calendar.getTime();
    }
    Date getEndOfDay(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Integer.parseInt(dateExtractor.getAttribute("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, 23, 59, 59);
        return calendar.getTime();
    }
    Date getStartOfMonth(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, 0, 0, 0);
        return calendar.getTime();
    }
    Date getEndOfMonth(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, Utility.getNumberOfDays(month,year), 23, 59, 59);
        return calendar.getTime();
    }
    Date getStartOfYear(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = 0;
        int currDate = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, 0, 0, 0);
        return calendar.getTime();
    }
    Date getEndOfYear(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = 11;
        int currDate = 31;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, 23, 59, 59);
        return calendar.getTime();
    }
    Date getStartOfWeek(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int day = Utility.getMonth(dateExtractor.getAttribute("day"));
        date = decrementDate(date,--day);
        dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        year = Integer.parseInt(dateExtractor.getAttribute("year"));
        month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Utility.getMonth(dateExtractor.getAttribute("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, 0, 0, 0);
        return calendar.getTime();
    }
    Date getEndOfWeek(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int day = Utility.getMonth(dateExtractor.getAttribute("day"));
        date = incrementDate(date,7 - day);
        dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        year = Integer.parseInt(dateExtractor.getAttribute("year"));
        month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Utility.getMonth(dateExtractor.getAttribute("date"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, Utility.getNumberOfDays(month,year), 23, 59, 59);
        return calendar.getTime();
    }
    Date decrementMonth(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Integer.parseInt(dateExtractor.getAttribute("date"));
        int hour = Integer.parseInt(dateExtractor.getAttribute("hour"));
        int minute = Integer.parseInt(dateExtractor.getAttribute("minute"));
        int second = Integer.parseInt(dateExtractor.getAttribute("second"));
        if(month == 0)
        {
            year--;
            month = 11;
        }
        else
            month--;
        if(currDate > Utility.getNumberOfDays(month, year))
            currDate = Utility.getNumberOfDays(month, year);
        System.out.println("IN month "+month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, hour, minute, second);
        System.out.println("IN Calendar"+calendar.getTime());
        return calendar.getTime();
    }
    Date decrementYear(Date date)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Integer.parseInt(dateExtractor.getAttribute("date"));
        int hour = Integer.parseInt(dateExtractor.getAttribute("hour"));
        int minute = Integer.parseInt(dateExtractor.getAttribute("minute"));
        int second = Integer.parseInt(dateExtractor.getAttribute("second"));
        year--;
        System.out.println("IN DECREMENT YEAR month "+month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, hour, minute, second);
        System.out.println("IN DECREMENT YEAR Calendar"+calendar.getTime());
        return calendar.getTime();
    }
    Date decrementDate(Date date,int numOfDays)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Integer.parseInt(dateExtractor.getAttribute("date"));
        int hour = Integer.parseInt(dateExtractor.getAttribute("hour"));
        int minute = Integer.parseInt(dateExtractor.getAttribute("minute"));
        int second = Integer.parseInt(dateExtractor.getAttribute("second"));
        while(numOfDays > 0)
        {
            if(currDate == 1)
            {
                if(month == 0)
                {
                    year--;
                    month = 11;
                }
                else
                    month--;
                currDate = Utility.getNumberOfDays(month, year);
            }
            else
                currDate--;
            numOfDays--;
        }
        System.out.println("In decrement Date "+month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, hour, minute, second);
        System.out.println("IN decrement date Calendar"+calendar.getTime());
        return calendar.getTime();
    }
    Date incrementDate(Date date,int numOfDays)
    {
        Utility dateExtractor = new Utility();
        dateExtractor.dateHandeler(date.toString());
        int year = Integer.parseInt(dateExtractor.getAttribute("year"));
        int month = Utility.getMonth(dateExtractor.getAttribute("month"));
        int currDate = Integer.parseInt(dateExtractor.getAttribute("date"));
        int hour = Integer.parseInt(dateExtractor.getAttribute("hour"));
        int minute = Integer.parseInt(dateExtractor.getAttribute("minute"));
        int second = Integer.parseInt(dateExtractor.getAttribute("second"));
        while(numOfDays > 0)
        {
            if(currDate == Utility.getNumberOfDays(month, year))
            {
                if(month == 11)
                {
                    year++;
                    month = 0;
                }
                else
                    month++;
                currDate = 1;
            }
            else
                currDate++;
            numOfDays--;
        }
        System.out.println("In Increment Date "+month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, currDate, hour, minute, second);
        System.out.println("IN Increment date Calendar"+calendar.getTime());
        return calendar.getTime();
    }
    void createReport()
    {
        System.out.println("start date end date"+startDate + " " + endDate);
        BusinessFirm firm = new BusinessFirm();
        firm.fetchTransaction(firmId);
        Vector<Transaction> record = firm.getTransactionList();
        int numOfRecords = firm.getTransactionNumber();
        Vector<Transaction> recordWithinRange = new Vector<Transaction>();
        System.out.println("record size "+ record.size() + "  "+numOfRecords);
        for (int i = 0;i < record.size() ;i++ )
        {
            System.out.println(record.get(i).getBillingDate());
            if(record.get(i).getBillingDate().after(startDate) && record.get(i).getBillingDate().before(endDate))
                recordWithinRange.add(record.get(i));
        }
        Vector<Double> bucket = new Vector<Double>();
        Date date = new Date();
        date.setTime(endDate.getTime());
        Date a = new Date();
        Date b = new Date();
        while(date.after(startDate))
        {
            switch(type)
            {
                case 0:
                    a = getStartOfWeek(date);
                    b = getEndOfWeek(date);
                    date = decrementDate(date,7);
                break;
                case 1:
                    a = getStartOfMonth(date);
                    b = getEndOfMonth(date);
                    date = decrementMonth(date);
                break;
                case 2:
                    a = getStartOfYear(date);
                    b = getEndOfYear(date);
                    date = decrementYear(date);
                break;
            }
            startBucket.add(a);
            endBucket.add(b);
            System.out.println("In while LOOP" + date);
        }
        switch(type)
        {
            case 0:
                a = getStartOfWeek(date);
                b = getEndOfWeek(date);
                date = decrementDate(date,7);
            break;
            case 1:
                a = getStartOfMonth(date);
                b = getEndOfMonth(date);
                date = decrementMonth(date);
            break;
            case 2:
                a = getStartOfYear(date);
                b = getEndOfYear(date);
                date = decrementYear(date);
            break;
        }
        if(startDate.after(a) && startDate.before(b))
        {
            startBucket.add(a);
            endBucket.add(b);
        }
        if(endBucket.get(0).after(endDate))
            endBucket.set(0,endDate);
        if(startBucket.get(startBucket.size() - 1).before(startDate))
            startBucket.set(startBucket.size() - 1,startDate);
        Collections.reverse(startBucket);
        Collections.reverse(endBucket);
        for (int i = 0;i < startBucket.size() ; i++ )
            bucket.add(0.0);
        System.out.println("Size of "+ recordWithinRange.size());
        System.out.println("Size of "+ startBucket.size());
        System.out.println("start date end date"+startDate + " " + endDate);
        for (int i = 0;i < recordWithinRange.size() ; i++ )
        {
            for(int j = 0;j < startBucket.size();j++)
            {
                System.out.println(startBucket.get(j)+ " " + endBucket.get(j));
                if(recordWithinRange.get(i).getBillingDate().after(startBucket.get(j)) &&
                    recordWithinRange.get(i).getBillingDate().before(endBucket.get(j)))
                {
                System.out.println(recordWithinRange.get(i)+ " " + startBucket.get(j));
                    bucket.set(j,bucket.get(j) + recordWithinRange.get(i).getTotalAmt());
                }
            }
        }
        transactionAmount = bucket;
    }
    Vector<Double> getTransactionAmount()
    {
        return transactionAmount;
    }
    void createXAxis()
    {
        String xAxisStr = null;
        for(int i = 0;i < transactionAmount.size();i++)
        {
            Date mean = new Date((startBucket.get(i).getTime() + endBucket.get(i).getTime())/2);
            Utility dateExtractor = new Utility();
            dateExtractor.dateHandeler(mean.toString());
            int year = Integer.parseInt(dateExtractor.getAttribute("year"));
            int month = Utility.getMonth(dateExtractor.getAttribute("month"));
            switch(type)
            {
                case 0:
                    xAxisStr = "week" + ":" + (i+1);
                    xAxis.add(xAxisStr);
                break;
                case 1:
                    xAxisStr = Utility.getMonthInString(month) + (year%100);
                    xAxis.add(xAxisStr);
                break;
                case 2:
                    xAxisStr = "year " + year;
                    xAxis.add(xAxisStr);
                break;
            }
        }
    }
    void createYAxis()
    {
        double high = Utility.getVectorMax(transactionAmount);
        double low = 0;
        if(high == low)
        {
            yAxis.add(Double.toString(0.0));
            return;
        }
        for (int i = 0;i < 10 ; i++)
        {
            yAxis.add(Double.toString(low));
            low = low + high/10.0;
        }
    }
    Vector<String> getXAxis()
    {
        return xAxis;
    }
    Vector<String> getYAxis()
    {
        return yAxis;
    }
}
