/**
 * Please use lower case mac:
 * <p>
 * cc:25:ef:6d:84:d0 is hashed to 96bb6975eb022cbc246d373eba14332e2c9ca476
 * https://www.geeksforgeeks.org/sha-1-hash-in-java/
 * http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
 */
package example.com.bus2.service;


//Java program to calculate SHA-1 hash value 

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GFG {

    private static final String solt1 = "scope";

    public static String encryptThisString(String input) {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 40(?) bit
            while (hashtext.length() < 40) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getJstTime(){

        ZonedDateTime jstTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        jstTime = jstTime.minusDays(jstTime.getDayOfWeek().getValue()-1);

        String solt2 = jstTime.format(formatter);

        return solt2;

    }

    public static String getHash(String mac){

        /*
        the way to get a right hash is so:
        1) lowercase the mac
        2) encripti it with "scope" solt
        3) encript the result with current JST date
         */

        String data = mac.toLowerCase();

        String step1 = encryptThisString(solt1+data);

        String step2 = encryptThisString(getJstTime()+step1);

        return step2;



    }
} 
 