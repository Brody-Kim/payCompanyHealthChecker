package com.finance.healthchecker;

import com.finance.healthchecker.comm.util.OKHttpClient;
import com.finance.healthchecker.controller.HealthCheckController;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.SynthesizedAnnotation;
import org.springframework.ui.context.Theme;

@SpringBootApplication
public class PayCompanyHealthCheckerApplication {

    public static void main(String[] args) {

        SpringApplication.run(PayCompanyHealthCheckerApplication.class, args);
        try{
            while(true) {
                Request request = new Request.Builder()
                        .url("http://localhost:8090/v1/doAllHealthCheck")
                        .build();

                OKHttpClient okClient = new OKHttpClient(request);
                int code = okClient.doUsingHttp();
                System.out.println("code:"+ code);
                Thread.sleep(30*1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
