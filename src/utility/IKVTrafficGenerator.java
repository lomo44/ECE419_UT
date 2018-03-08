package utility;


import common.command.KVCommand;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Traffic generator class for evaluate performance
 * Use this class to generate series of command for evaluating performance
 */
public abstract class IKVTrafficGenerator {

    public abstract KVCommand getNextCommand();
    protected String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789abcdefghijklmnopqrstuvwxyz";
    protected String getRandomeString(int length){
        // Clean previous build string
        StringBuilder stringBuilder = new StringBuilder();
        while(stringBuilder.length() < length){
            stringBuilder.append(alphabet.charAt(stringBuilder.length()%alphabet.length()));
        }
        return  stringBuilder.toString();
    }

    protected int getRandomPercentage(){
        return ThreadLocalRandom.current().nextInt(0,100);
    }
}
