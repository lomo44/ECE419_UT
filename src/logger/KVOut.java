package logger;

import common.enums.eKVLogLevel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;

public class KVOut {

    private LogSetup logSetupInstance = null;
    private int outputLevel;
    private int loglevel = 0;
    public KVOut(){
    }
    public void enableLog(String logdir, Level loglevel){
        try {
            logSetupInstance = new LogSetup(logdir,loglevel);
        } catch (IOException e) {
            System.out.println("Failed to create logger.");
            e.printStackTrace();
        }
    }

    public eKVLogLevel getOutputLevel(){
        return eKVLogLevel.fromInt(outputLevel);
    }

    public eKVLogLevel getLogLevel(){
        return eKVLogLevel.fromInt(loglevel);
    }

    public void changeOutputLevel(eKVLogLevel level){
        this.outputLevel = level.toInt();
    }

    public void changeLogLevel(eKVLogLevel level){
        this.loglevel = level.toInt();
    }

    private boolean shouldLog(eKVLogLevel level){
        if(level.toInt() >= loglevel){
            return true;
        }
        return false;
    }

    private boolean shouldPrint(eKVLogLevel level){
        if(level.toInt() >= outputLevel){
            return true;
        }
        return false;
    }

    public void println_debug(String str){
        if(logSetupInstance!=null && shouldLog(eKVLogLevel.DEBUG)){
            Logger.getRootLogger().debug("[DEBUG]:"+str);
        }
        if(shouldPrint(eKVLogLevel.DEBUG)){
            System.out.println("[DEBUG]: "+str);
        }
    }
    public void println_info(String str){
        if(logSetupInstance!=null&& shouldLog(eKVLogLevel.INFO)){
            Logger.getRootLogger().info("[INFO]: "+str);
        }
        if(shouldPrint(eKVLogLevel.INFO)){
            System.out.println("[INFO]: "+str);
        }
    }
    public void println_warn(String str){
        if(logSetupInstance!=null && shouldLog(eKVLogLevel.WARN)){
            Logger.getRootLogger().warn("[WARN]"+ str);
        }
        if(shouldPrint(eKVLogLevel.WARN)){
            System.out.println("[WARN]: "+str);
        }
    }
    public void println_error(String str){
        if(logSetupInstance!=null && shouldLog(eKVLogLevel.ERROR)){
            Logger.getRootLogger().error("[ERROR]: "+str);
        }
        if(shouldPrint(eKVLogLevel.ERROR)){
            System.out.println("[ERROR]: "+str);
        }
    }
    public void println_fatal(String str){
        if(logSetupInstance!=null && shouldLog(eKVLogLevel.FATAL)){
            Logger.getRootLogger().fatal("[FATAL]: "+str);
        }
        if(shouldPrint(eKVLogLevel.FATAL)){
            System.out.println("[FATAL]: "+str);
        }
    }
}
