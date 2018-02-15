package utility;


import common.command.KVCommand;

/**
 * Traffic generator class for evaluate performance
 * Use this class to generate series of command for evaluating performance
 */
public interface IKVTrafficGenerator {

    public KVCommand getNextCommand();
}
