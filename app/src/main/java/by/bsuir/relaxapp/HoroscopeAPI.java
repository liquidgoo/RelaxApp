package by.bsuir.relaxapp;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.locks.ReentrantLock;

public class HoroscopeAPI {
    private static String[] signsNames=new String[]{"aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius" , "pisces"};
    private Sign[] signs;

    private ReentrantLock mutex;

    public Sign[] getSigns() {
        mutex.lock();
        mutex.unlock();
        return signs;
    }

    public HoroscopeAPI(){
        signs=new Sign[signsNames.length];
        mutex = new ReentrantLock();
    }

    public void load(){
        mutex.lock();
        for (int i = 0; i < signsNames.length; i++) {
            try {
                URL url = new URL("https://aztro.sameerkumar.website/?sign="+signsNames[i]+"&day=today");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                signs[i]=new Sign(signsNames[i], con.getInputStream());

                con.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        mutex.unlock();
    }

    public void load(int i){
        mutex.lock();
        Sign sign=null;
        try {
            URL url = new URL("https://aztro.sameerkumar.website/?sign="+signsNames[i]+"&day=today");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            sign = new Sign(signsNames[i], con.getInputStream());
            con.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
        signs[i]=sign;
        mutex.unlock();
    }

    static public boolean canConnect(){
        try {
            URL url = new URL("https://aztro.sameerkumar.website/?sign=aries&day=today");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            int status = con.getResponseCode();
            con.disconnect();
            return status!=-1 && status<=500;
        }catch (Exception e){
            return false;
        }
    }

    static public int size(){
        return signsNames.length;
    }
}
