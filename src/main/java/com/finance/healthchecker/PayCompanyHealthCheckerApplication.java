package com.finance.healthchecker;

import com.finance.healthchecker.comm.util.OKHttpClient;
import com.finance.healthchecker.comm.util.TranFormat;
import okhttp3.Request;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayCompanyHealthCheckerApplication {

    public static void main(String[] args) {

        SpringApplication.run(PayCompanyHealthCheckerApplication.class, args);
        try{

            String[] monitorList = {"38815,그로비교육,ALL,20221128130000","13372,애기야가자,ALL,20221128130000",
                                    "41313,하얀마인드,ALL,20221201090000","39617,설로인(b2b),ALL,20221130140000"};

            while(true) {
                Request request = new Request.Builder()
                        .url("http://localhost:8080/v1/doAllHealthCheck")
                        .build();

                OKHttpClient okClient = new OKHttpClient(request);
                int code = okClient.doUsingHttp();
                System.out.println("code:"+ code);

                if(monitorList != null && monitorList.length >0) {
                    for(int i=0; i< monitorList.length; i++) {
                        String[] monitorInfo = monitorList[i].split(",");
                        if(Integer.parseInt(TranFormat.getToday()) < Integer.parseInt(monitorInfo[3].substring(0,8))){
                            continue;
                        }
                        request = new Request.Builder()
                                .url("http://localhost:8080/v1/doOpenMonitoring/"+monitorInfo[0]+"/"+monitorInfo[1]+"/"+monitorInfo[2]+"/"+monitorInfo[3])
                                .build();

                        okClient = new OKHttpClient(request);
                        code = okClient.doUsingHttp();
                        System.out.println("body:" + okClient.getResponseBody());
                    }
                }
                Thread.sleep(30*1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
