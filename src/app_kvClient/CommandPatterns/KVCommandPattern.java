package app_kvClient.CommandPatterns;

import app_kvClient.Commands.KVCommand;

import java.util.regex.Pattern;

// This class is used for handle individual command.
public abstract class KVCommandPattern {
    public enum KVCommandType{
        CONNECT,
        DISCONNECT,
        PUT,
        GET,
        LOG_LEVEL,
        HELP,
        QUIT,
        ECHO,
        UNKNOWN
    }
    protected KVCommandType commandType;
    protected Pattern commandRegex;

    public KVCommandPattern(){
        commandRegex = generateRegex();
    }

    /**
     * Check if given string is matched with this command pattern
     * @param inputString Input Command String
     * @return
     */
    public boolean isMatched(String inputString){
        return this.commandRegex.matcher(inputString).matches();
    }

    /**
     * Get the type of this command pattern
     * @return
     */
    public KVCommandType getCommandType() {
        return commandType;
    }

    /**
     * Generate rate a command based on input string
     * @param input input string
     * @return KVCommand instance, should be consumed by the command handler
     */
    public abstract KVCommand generateCommand(String input);

    /**
     * Generate the regex for parsing the command
     * @return regex pattern
     */
    public abstract Pattern generateRegex();

    /**
     * @return return the help message string for this command
     */
    public abstract String getHelpMessageString();
}
