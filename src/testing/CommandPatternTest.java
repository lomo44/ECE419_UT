package testing;

import app_kvClient.CommandPatterns.*;
import app_kvClient.Commands.KVCommand;
import junit.framework.TestCase;
import org.junit.Test;

public class CommandPatternTest extends TestCase {
    @Test
    public void testCommandPattern_Connect_Match(){
        KVCommandPatternConnect connect = new KVCommandPatternConnect();
        assertTrue(connect.isMatched("connect localhost 123"));
    }
    @Test
    public void testCommandPattern_Connect_Valid(){
        String inputString = "connect localhost 123";
        KVCommandPatternConnect connect = new KVCommandPatternConnect();
        assertTrue(connect.isMatched(inputString));
        KVCommand newCommand = connect.generateCommand(inputString);
        assertEquals(newCommand.get("HostName"),"localhost");
        assertEquals(newCommand.get("PortNumber"),"123");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.CONNECT);
    }
    @Test
    public void testCommandPattern_Connect_invalid_port(){
        String inputString = "connect localhost bbb";
        KVCommandPatternConnect connect = new KVCommandPatternConnect();
        assertFalse(connect.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_Connect_IP_Valid(){
        String inputString = "connect 123.123.123.123 123";
        KVCommandPatternConnect connect = new KVCommandPatternConnect();
        assertTrue(connect.isMatched(inputString));
        KVCommand newCommand = connect.generateCommand(inputString);
        assertEquals(newCommand.get("HostName"),"123.123.123.123");
        assertEquals(newCommand.get("PortNumber"),"123");
    }

    @Test
    public void testCommandPattern_Disconnect_Match(){
        KVCommandPatternDisconnect command = new KVCommandPatternDisconnect();
        assertTrue(command.isMatched("disconnect"));
    }
    @Test
    public void testCommandPattern_Disconnect_Valid(){
        String inputString = "disconnect";
        KVCommandPatternDisconnect command = new KVCommandPatternDisconnect();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.DISCONNECT);
    }
    @Test
    public void testCommandPattern_Disconnect_Invalid(){
        String inputString = "disconnect123";
        KVCommandPatternDisconnect connect = new KVCommandPatternDisconnect();
        assertFalse(connect.isMatched(inputString));
    }

    @Test
    public void testCommandPattern_Quit_Match(){
        KVCommandPatternQuit command = new KVCommandPatternQuit();
        assertTrue(command.isMatched("quit"));
    }
    @Test
    public void testCommandPattern_Quit_Valid(){
        String inputString = "quit";
        KVCommandPatternQuit command = new KVCommandPatternQuit();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.QUIT);
    }
    @Test
    public void testCommandPattern_Quit_Invalid(){
        String inputString = "12quit12";
        KVCommandPatternQuit connect = new KVCommandPatternQuit();
        assertFalse(connect.isMatched(inputString));
    }


    @Test
    public void testCommandPattern_Help_Match(){
        KVCommandPatternHelp command = new KVCommandPatternHelp();
        assertTrue(command.isMatched("help"));
    }
    @Test
    public void testCommandPattern_Help_Valid(){
        String inputString = "help";
        KVCommandPatternHelp command = new KVCommandPatternHelp();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.HELP);
    }
    @Test
    public void testCommandPattern_Help_Invalid(){
        String inputString = "helpaa";
        KVCommandPatternHelp connect = new KVCommandPatternHelp();
        assertFalse(connect.isMatched(inputString));
    }

    @Test
    public void testCommandPattern_Put_Match(){
        KVCommandPatternPut command = new KVCommandPatternPut();
        assertTrue(command.isMatched("put asd asd"));
    }
    @Test
    public void testCommandPattern_Put_Valid(){
        String inputString = "put asd 123";
        KVCommandPatternPut command = new KVCommandPatternPut();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Key"),"asd");
        assertEquals(newCommand.get("Value"),"123");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.PUT);
    }
    @Test
    public void testCommandPattern_Put_invalid(){
        String inputString = "put1 localhost bbb";
        KVCommandPatternPut command = new KVCommandPatternPut();
        assertFalse(command.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_Put_Valid_ValueWithSpace(){
        String inputString = "put localhost b bb";
        KVCommandPatternPut command = new KVCommandPatternPut();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Key"),"localhost");
        assertEquals(newCommand.get("Value"),"b bb");
    }

    @Test
    public void testCommandPattern_Get_Match(){
        KVCommandPatternGet command = new KVCommandPatternGet();
        assertTrue(command.isMatched("get asd"));
    }
    @Test
    public void testCommandPattern_Get_Valid(){
        String inputString = "get asd";
        KVCommandPatternGet command = new KVCommandPatternGet();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Key"),"asd");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.GET);
    }
    @Test
    public void testCommandPattern_Get_invalid(){
        String inputString = "get localhost bbb";
        KVCommandPatternPut command = new KVCommandPatternPut();
        assertFalse(command.isMatched(inputString));
    }
    @Test
    public void testCommandPattern_Get_invalid_ValueWithSpace(){
        String inputString = "get local host";
        KVCommandPatternPut command = new KVCommandPatternPut();
        assertFalse(command.isMatched(inputString));
    }

    @Test
    public void testCommandPattern_LogLevel_Match(){
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched("loglevel ALL"));
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_ALL(){
        String inputString = "loglevel ALL";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"ALL");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_DEBUG(){
        String inputString = "loglevel DEBUG";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"DEBUG");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_INFO(){
        String inputString = "loglevel INFO";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"INFO");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_WARN(){
        String inputString = "loglevel WARN";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"WARN");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_Valid_ERROR(){
        String inputString = "loglevel ERROR";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"ERROR");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_Valid_FATAL(){
        String inputString = "loglevel FATAL";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"FATAL");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_Valid_OFF(){
        String inputString = "loglevel OFF";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.get("Level"),"OFF");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_invalid(){
        String inputString = "loglevel test";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertFalse(command.isMatched(inputString));
    }

}
