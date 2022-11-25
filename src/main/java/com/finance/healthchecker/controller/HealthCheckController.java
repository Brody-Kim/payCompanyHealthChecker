package com.finance.healthchecker.controller;

import com.finance.healthchecker.comm.entity.PaymentsEntity;
import com.finance.healthchecker.comm.util.*;
import com.finance.healthchecker.entity.CustomErrorType;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

@RestController
@RequestMapping("/v1")
public class HealthCheckController {

    @Autowired
    cardAppQueue queue;

    @RequestMapping(value = "/doAllHealthCheck", method = RequestMethod.GET)
    public ResponseEntity<String> doAllHealthCheck() {


        OKHttpClient okClient = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        int code = 0;

        try {
            TranFormat.setCheckUrl();
            String pg_provider = "";
            String url = "";
            for (Enumeration el = TranFormat.checkUrlHash.keys(); el.hasMoreElements(); ) {
                pg_provider = (String)el.nextElement();
                url = TranFormat.getCheckUrl(pg_provider);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                okClient = new OKHttpClient(request);
                code = okClient.doUsingHttp();


                // 200OK, 403Forbidden
                if (code == 200 || code == 403 ) {
                    //System.out.println(pg_provider + " is ok");
                    //SlackUtil.postSlackMessage(pg_provider + " is ok");
                }else{
                    String today = TranFormat.getToday();
                    //if(TranFormat.isAlerted(today, today+pg_provider) == false){
                        //System.out.println(":alert: Please, Check "+pg_provider+ "checout page!!! :alert: " );
                        SlackUtil.postSlackMessage(":alert: Please, Check "+pg_provider+ "checout page!!! :alert: ");
                    //}
                }
            }


        }catch(Exception e){
            e.printStackTrace();

            return new ResponseEntity(new CustomErrorType("관리자에 문의하세요"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("check success", responseHeaders, HttpStatus.OK);

    }

    @RequestMapping(value = "/doOpenMonitoring/{user_id}/{user_nm}/{openDttm}", method = RequestMethod.GET)
    public ResponseEntity<String> doOpenMonitoring(@PathVariable("user_id") String user_id,
                                                   @PathVariable("user_nm") String user_nm,
                                                   @PathVariable("openDttm") String openDttm) {


        OKHttpClient okClient = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        int code = 0;

        try {

            String nowKST = TranFormat.getToday()+TranFormat.getTime();
            //19시까지 체크해줌
            if(Long.parseLong(nowKST) > Long.parseLong(TranFormat.getToday()+"190500")){
                return new ResponseEntity<String>("check time over", responseHeaders, HttpStatus.OK);
            }

            if(Long.parseLong(nowKST) < Long.parseLong(openDttm)){
                return new ResponseEntity<String>("pleae wait open time~", responseHeaders, HttpStatus.OK);
            }

            Long nowTime = System.currentTimeMillis();
            Long lastCheckTime = (Long) queue.pull(user_id);

            System.out.println("lastCheckTime: "+lastCheckTime);

            //3시간마다 체크
            if (lastCheckTime == null || lastCheckTime == 0L || (nowTime - lastCheckTime)/1000 >= 60*3){

                DataControl control = new DataControl();
                String searchResult = "[" + user_nm + "]\n";
                ArrayList paymentsList = control.getPaymentsData(user_id, openDttm);
                if (paymentsList != null && paymentsList.size() > 0) {
                    for (int i = 0; i < paymentsList.size(); i++) {
                        PaymentsEntity entity = (PaymentsEntity) paymentsList.get(i);
                        searchResult += entity.getPg_provider() + " : " + entity.getPg_id() + " : " + entity.getPay_method() + " : "
                                + TranFormat.tranNumberFormat(entity.getPay_cnt()) + "건" + " : " + TranFormat.tranNumberFormat(entity.getAmount()) + " ("+ entity.getCurrency()+")\n";
                    }
                }
                queue.put(user_id, nowTime);
                SlackUtil.postSlackMessage(searchResult);
            }else{
                return new ResponseEntity<String>("not check time", responseHeaders, HttpStatus.OK);
            }

        }catch(Exception e){
            e.printStackTrace();

            return new ResponseEntity(new CustomErrorType("관리자에 문의하세요"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("check success", responseHeaders, HttpStatus.OK);

    }

}
