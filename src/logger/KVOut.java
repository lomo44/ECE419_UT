package logger;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import logger.LogSetup;

import java.io.IOException;

public class KVOut {

    private LogSetup logSetupInstance = null;
    public KVOut(){

    }
    public void enableLog(String logdir, Level loglevel){
        try {
            logSetupInstance = new LogSetup(logdir,loglevel);
        } catch (IOException e) {
            System.out.println("Failed to create logger");
            e.printStackTrace();
        }
    }
    public void println_debug(String str){
        if(logSetupInstance!=null){
            Logger.getRootLogger().debug(str);
        }
        System.out.println("[DEBUG]: "+str);
    }
    public void println_info(String str){
        if(logSetupInstance!=null){
            Logger.getRootLogger().info("[INFO]: "+str);
        }
        System.out.println(str);
    }
    public void println_warn(String str){
        if(logSetupInstance!=null){
            Logger.getRootLogger().warn("[WARN]"+ str);
        }
        System.out.println(str);
    }
    public void println_error(String str){
        if(logSetupInstance!=null){
            Logger.getRootLogger().error("[ERROR]: "+str);
        }
        System.out.println(str);
    }
    public void println_fatal(String str){
        if(logSetupInstance!=null){
            Logger.getRootLogger().fatal("[FATAL]: "+str);
        }
        System.out.println(str);
    }
}
