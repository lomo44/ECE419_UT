package testing.ClientTests;

import app_kvClient.CommandPatterns.*;
import common.command.KVCommand;
import common.command.KVCommandPattern;
import junit.framework.TestCase;
import org.junit.Test;

public class KVClientCommandPatternTest extends TestCase {
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
        assertEquals(newCommand.getValue("HostName"),"localhost");
        assertEquals(newCommand.getValue("PortNumber"),"123");
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
        assertEquals(newCommand.getValue("HostName"),"123.123.123.123");
        assertEquals(newCommand.getValue("PortNumber"),"123");
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
        assertEquals(newCommand.getValue("Key"),"asd");
        assertEquals(newCommand.getValue("Value"),"123");
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
        assertEquals(newCommand.getValue("Key"),"localhost");
        assertEquals(newCommand.getValue("Value"),"b bb");
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
        assertEquals(newCommand.getValue("Key"),"asd");
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
        assertTrue(command.isMatched("logLevel ALL"));
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_ALL(){
        String inputString = "logLevel ALL";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"ALL");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_DEBUG(){
        String inputString = "logLevel DEBUG";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"DEBUG");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_INFO(){
        String inputString = "logLevel INFO";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"INFO");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }
    @Test
    public void testCommandPattern_LogLevel_Valid_WARN(){
        String inputString = "logLevel WARN";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"WARN");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_Valid_ERROR(){
        String inputString = "logLevel ERROR";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"ERROR");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_Valid_FATAL(){
        String inputString = "logLevel FATAL";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"FATAL");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_Valid_OFF(){
        String inputString = "logLevel OFF";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertTrue(command.isMatched(inputString));
        KVCommand newCommand = command.generateCommand(inputString);
        assertEquals(newCommand.getValue("Level"),"OFF");
        assertEquals(newCommand.getCommandType(), KVCommandPattern.KVCommandType.LOG_LEVEL);
    }

    @Test
    public void testCommandPattern_LogLevel_invalid(){
        String inputString = "logLevel test";
        KVCommandPatternLogLevel command = new KVCommandPatternLogLevel();
        assertFalse(command.isMatched(inputString));
    }

}
