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
                    SlackUtil.postSlackMessage(":alert: Please, Check "+pg_provider+ "check-out page!!! :alert: ");
                    //}
                }
            }


        }catch(Exception e){
            e.printStackTrace();

            return new ResponseEntity(new CustomErrorType("???????????? ???????????????"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("check success", responseHeaders, HttpStatus.OK);

    }

    @RequestMapping(value = "/doOpenMonitoring/{user_id}/{user_nm}/{pg_id}/{openDttm}", method = RequestMethod.GET)
    public ResponseEntity<String> doOpenMonitoring(@PathVariable("user_id") String user_id,
                                                   @PathVariable("user_nm") String user_nm,
                                                   @PathVariable("pg_id") String pg_id,
                                                   @PathVariable("openDttm") String openDttm) {


        OKHttpClient okClient = null;
        HttpHeaders responseHeaders = new HttpHeaders();
        int code = 0;

        try {

            String nowKST = TranFormat.getToday()+TranFormat.getTime();
            //19????????? ????????????
            if(Long.parseLong(nowKST) > Long.parseLong(TranFormat.getToday()+"190500")){
                return new ResponseEntity<String>("check time over", responseHeaders, HttpStatus.OK);
            }

            if(Long.parseLong(nowKST) < Long.parseLong(openDttm)){
                return new ResponseEntity<String>("pleae wait open time~", responseHeaders, HttpStatus.OK);
            }

            Long nowTime = System.currentTimeMillis();
            Long lastCheckTime = (Long) queue.pull(user_id);

            System.out.println("lastCheckTime: "+lastCheckTime);

            //3???????????? ??????
            if (lastCheckTime == null || lastCheckTime == 0L || (nowTime - lastCheckTime)/1000 >= 60*60*3){

                DataControl control = new DataControl();
                String searchResult = "[" + user_nm + "]\n";
                ArrayList paymentsList = control.getPaymentsData(user_id, pg_id, openDttm);
                if (paymentsList != null && paymentsList.size() > 0) {
                   System.out.println("paymentsList: "+paymentsList.size());
                   for (int i = 0; i < paymentsList.size(); i++) {
                        PaymentsEntity entity = (PaymentsEntity) paymentsList.get(i);
                        searchResult += entity.getPg_provider() + " : " + entity.getPg_id() + " : " + entity.getPay_method() + " : " + entity.getStatus() + " : "
                                     + TranFormat.tranNumberFormat(entity.getPay_cnt()) + "???" + " : " + TranFormat.tranNumberFormat(entity.getAmount()) + " ("+ entity.getCurrency()+")\n";
                   }


                   ArrayList paymentsFirstPaidList = control.getFirstPaidData(user_id, pg_id, openDttm);
                   if (paymentsFirstPaidList != null && paymentsFirstPaidList.size() > 0) {
                       searchResult += "[??? ????????????]";
                       PaymentsEntity entity = (PaymentsEntity) paymentsFirstPaidList.get(0);
                       searchResult += entity.getImp_uid() + " : " + entity.getBuyer_email() + " : "
                                    +  entity.getPaid_at() + " : "
                                    + TranFormat.tranNumberFormat(entity.getAmount()) + " ("+ entity.getCurrency()+")\n";
                   }

                   ArrayList paymentsFailList = control.getFailData(user_id, pg_id, openDttm);
                   if (paymentsFailList != null && paymentsFailList.size() > 0) {
                       searchResult += "[???????????????]";
                       for (int i = 0; i < paymentsFailList.size(); i++) {
                           System.out.println("paymentsFailList: "+paymentsFailList.size());
                           PaymentsEntity entity = (PaymentsEntity) paymentsFailList.get(i);
                           searchResult += entity.getFail_reason() + " : "
                                   + TranFormat.tranNumberFormat(entity.getPay_cnt()) + "??? \n";
                       }
                   }
                }else {
                    searchResult += "?????? ????????? ?????????~ :blobnervouspleading:";
                }

                queue.put(user_id, nowTime);
                SlackUtil.postSlackMessage(searchResult);
            }else{
                return new ResponseEntity<String>("not check time", responseHeaders, HttpStatus.OK);
            }

        }catch(Exception e){
            e.printStackTrace();

            return new ResponseEntity(new CustomErrorType("???????????? ???????????????"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("check success", responseHeaders, HttpStatus.OK);

    }

}
