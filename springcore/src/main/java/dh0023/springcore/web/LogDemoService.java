package dh0023.springcore.web;

import dh0023.springcore.common.MyLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogDemoService {
    private final MyLogger myLogger;

    public void logic(String id){
        System.out.println("myLogger = " + myLogger.getClass());
        myLogger.log("logdemoservice: " + id);
    }
}
