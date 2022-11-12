package com.finance.healthchecker.comm.util;

import java.security.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TranFormat {
    public static String tranNvl(String param, String newParam) {

        if (param == null ||
                param.equals("") ||
                param.length() == 0 ||
                param.equals("null")) {

            String reParam = newParam;
            return reParam;
        }
        else {
            return param.trim();
        }
    }

    public static String tranTwoByte(String str) {

        String sLarge = "" ;
        try {
            for ( int i = 0; i < str.length(); i++ ) {
                String testOne = str.substring( i, i + 1 ) ;
                String resultString = convSmallToLarge( testOne ) ;
                sLarge += resultString;
            }
        }
        catch ( Exception e ) {
            System.out.println( "err:" + e.getMessage() ) ;
        }
        return sLarge;
    }

    private static String convSmallToLarge( String chkString ) {
        try {
            if ( chkString.length() != 1 ) {
                return chkString ;
            }

            byte[] stringByte = chkString.getBytes() ;
            if ( stringByte.length == 2 ) {
                return chkString ;
            }

            byte[] tempByte = chkString.getBytes() ;
            Byte smallByte = new Byte( tempByte[0] ) ;
            int smallInt = smallByte.intValue() ;

            byte[] largeByte = new byte[2] ;
            largeByte[0] = -93 ;
            Integer largeInt = new Integer( smallInt - 128 ) ;
            largeByte[1] = largeInt.byteValue();

            String largeString = new String( largeByte ) ;
            return largeString ;
        }
        catch ( Exception e ) {
            System.out.println( "err:" + e.getMessage() ) ;
            return "" ;
        }
    }


    public static String tranSosi(String str) throws Exception {
        int ByteSize = str.getBytes().length;
        byte convertstr[] = new byte[ByteSize + 2];
        byte currentstr[] = new byte[ByteSize];
        convertstr[0] = 14;
        currentstr = str.getBytes();
        for (int i = 0; i < currentstr.length; i++)
            convertstr[i + 1] = currentstr[i];

        convertstr[ByteSize + 1] = 15;
        return new String(convertstr);
    }


    public static String tranFillZero(long num, int len) {
        StringBuffer sb = new StringBuffer(len);
        int zeroCount = len - ("" + num).length();
        for (int i = 0; i < zeroCount; i++) {
            sb.append(0);
        }
        sb.append(num);
        return sb.toString();
    }

    public static String tranFillZero(String num, int len) {
        StringBuffer sb = new StringBuffer(len);
        int zeroCount = len -  num.length();
        for (int i = 0; i < zeroCount; i++) {
            sb.append(0);
        }
        sb.append(num);
        return sb.toString();
    }

    public static String tranNumberFormat(int num){
        DecimalFormat formatter = new DecimalFormat("###,###");
        return formatter.format(num);
    }

    public static String tranDateFormat(String dateStr) throws Exception{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date formatDate = dtFormat.parse(dateStr);

        String strNewDtFormat = newDtFormat.format(formatDate);

        return strNewDtFormat;
    }

    public static String tranDateFormat(String dateStr, String pattern) throws Exception{
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat newDtFormat = new SimpleDateFormat(pattern);
        Date formatDate = dtFormat.parse(dateStr);

        String strNewDtFormat = newDtFormat.format(formatDate);

        return strNewDtFormat;
    }

    public static Hashtable checkUrlHash = new Hashtable();
    public static void setCheckUrl(){
        checkUrlHash.put("inicis", "https://stdpay.inicis.com/jsApi/payCheck");
        checkUrlHash.put("naverpay", "https://pay.naver.com/payments/recurrent/20221112ZUhUVjQ5SXB4blB4MjdPREp5Yis5SklSeHNZPQ==");
        checkUrlHash.put("kakaopay", "https://online-pay.kakao.com");
        checkUrlHash.put("kcp", "https://npay.kcp.co.kr");
        checkUrlHash.put("uplus", "https://xpay.uplus.co.kr");
        checkUrlHash.put("tosspayments", "https://js.tosspayments.com");
        checkUrlHash.put("nice", "https://web.nicepay.co.kr");
        checkUrlHash.put("smilepay", "https://pg.cnspay.co.kr");
        checkUrlHash.put("payco", "https://bill.payco.com/");
        checkUrlHash.put("danal_checkout", "https://checkout.teledit.com");
        checkUrlHash.put("danal", "https://dams.danalpay.com");
        checkUrlHash.put("paymentwall", "https://widgets.paymentwall.com");
        checkUrlHash.put("tosspay", "https://pay.toss.im/payfront/web/login");
        checkUrlHash.put("paypal", "https://www.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-9BG89631P81638708");
        checkUrlHash.put("settle", "https://npg.settlebank.co.kr/card/NewCardAction.do?PHash=&PData=&PStateCd=&POrderId=&PNoteUrl=https%3A%2F%2Fservice.iamport.kr%2Fsettle_payments%2Fresult&PNextPUrl=https%3A%2F%2Fservice.iamport.kr%2Fsettle_payments%2Frelay%2Fnew&PCancPUrl=https%3A%2F%2Fservice.iamport.kr%2Fsettle_payments%2Fclose%2Fnew&PEmail=gildong%40gmail.com&PPhone=010-4242-4242&POid=imp_922572453567&t_PGoods=%EA%B0%80%EB%82%98%EB%8B%A4%EB%9D%BC&t_PNoti=imp_uid%3Dimp_922572453567%7Crequest_id%3Dreq_1668224575203%7Cuser_code%3Dimp60208978&t_PMname=&t_PUname=%EB%A7%88%EB%B0%94%EC%82%AC&t_PBname=%EB%A7%88%EB%B0%94%EC%82%AC&PEname=&PVtransDt=&PUserid=&PCardType=&PChainUserId=&PGoods=%25EA%25B0%2580%25EB%2582%2598%25EB%258B%25A4%25EB%259D%25BC&PNoti=imp_uid%3Dimp_922572453567%257Crequest_id%3Dreq_1668224575203%257Cuser_code%3Dimp60208978&PMname=&PUname=%25EB%25A7%2588%25EB%25B0%2594%25EC%2582%25AC&PBname=%25EB%25A7%2588%25EB%25B0%2594%25EC%2582%25AC&PMid=mid_test&PAmt=1000");
        checkUrlHash.put("kiwoompay", "https://ssltest.kiwoompay.co.kr/card2/DaouDirectCardSelect.jsp");
    };
    public static String getCheckUrl(String pg_provider) throws Exception{
        return (String) checkUrlHash.get(pg_provider);
    }


    public static String getMD5(String pwd) {
        String MD5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pwd.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }

    public static String alertedList = "";
    public static boolean isAlerted(String date, String checkKey){

        if(alertedList.indexOf(date)<0) {
            alertedList = "";
        }

        if(alertedList.indexOf(checkKey)>=0){
            return true;
        }else{
            alertedList += checkKey+",";
            return false;
        }
    }

    public static String getToday() {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");

        String today = SDF.format(calendar.getTime());
        return today;
    }

}
