package logger;

import common.enums.eKVLogLevel;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

public class KVOut {

    private Logger logger = null;
    private int outputLevel;
    private int loglevel = 0;
    public KVOut(String hint){
        logger = Logger.getLogger(hint);
    }
    public void enableLog(String logdir, Level loglevel){
        try {
            PatternLayout layout = new PatternLayout( "%d{ISO8601} %-5p [%t] %c: %m%n" );
		    FileAppender fileAppender = new FileAppender( layout, logdir, true );
            System.out.println("Logger will write to " + layout + ".");	
	    
	        ConsoleAppender consoleAppender = new ConsoleAppender(layout);
		    logger.addAppender(consoleAppender);
		    logger.addAppender(fileAppender);
		    logger.setLevel(loglevel);
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
        if(logger!=null && shouldLog(eKVLogLevel.DEBUG)){
            logger.debug("[DEBUG]:"+str);
        }
        if(shouldPrint(eKVLogLevel.DEBUG)){
            // System.out.println("[DEBUG]: "+str);
        }
    }
    public void println_info(String str){
        if(logger!=null&& shouldLog(eKVLogLevel.INFO)){
            logger.info("[INFO]: "+str);
        }
        if(shouldPrint(eKVLogLevel.INFO)){
            // System.out.println("[INFO]: "+str);
        }
    }
    public void println_warn(String str){
        if(logger!=null && shouldLog(eKVLogLevel.WARN)){
            logger.warn("[WARN]"+ str);
        }
        if(shouldPrint(eKVLogLevel.WARN)){
            // System.out.println("[WARN]: "+str);
        }
    }
    public void println_error(String str){
        if(logger!=null && shouldLog(eKVLogLevel.ERROR)){
            logger.error("[ERROR]: "+str);
        }
        if(shouldPrint(eKVLogLevel.ERROR)){
            // System.out.println("[ERROR]: "+str);
        }
    }
    public void println_fatal(String str){
        if(logger!=null && shouldLog(eKVLogLevel.FATAL)){
            logger.fatal("[FATAL]: "+str);
        }
        if(shouldPrint(eKVLogLevel.FATAL)){
            // System.out.println("[FATAL]: "+str);
        }
    }
}
