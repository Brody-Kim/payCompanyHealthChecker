package com.finance.healthchecker.controller;

import com.finance.healthchecker.comm.util.OKHttpClient;
import com.finance.healthchecker.comm.util.SlackUtil;
import com.finance.healthchecker.comm.util.TranFormat;
import com.finance.healthchecker.entity.CustomErrorType;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.Hashtable;

@RestController
@RequestMapping("/v1")
public class HealthCheckController {

    @Autowired
    SlackUtil slackUtil;

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
                    //slackUtil.postSlackMessage(pg_provider + " is ok");
                }else{
                    String today = TranFormat.getToday();
                    //if(TranFormat.isAlerted(today, today+pg_provider) == false){
                        //System.out.println(":alert: Please, Check "+pg_provider+ "checout page!!! :alert: " );
                        this.slackUtil.postSlackMessage(":alert: Please, Check "+pg_provider+ "checout page!!! :alert: ");
                    //}
                }
            }


        }catch(Exception e){
            e.printStackTrace();

            return new ResponseEntity(new CustomErrorType("관리자에 문의하세요"), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<String>("check success", responseHeaders, HttpStatus.OK);

    }

}
