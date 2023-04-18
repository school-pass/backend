package gbsw.plutter.project.PMS.config;


import javax.smartcardio.*;

import java.nio.ByteBuffer;
import java.util.*;
public class NFCMain {
    private static final String UNKNOWN_CMD_SW = "0000";
    private static final String SELECT_OK_SW = "9000";

    public static void main(String[] args) {
        NFCMain nfcMain = new NFCMain();
        nfcMain.run();
    }
    public void run() {
        CardTerminal terminal;
        CardChannel channel;

        while (true) {
            // 터미널 초기화
            try {
                terminal = InitializeTerminal();

                if(IsCardPresent(terminal)) {                                   // 리더기 위에 카드(핸드폰)가 있을 경우
                    channel = GetCardAndOpenChannel(terminal);

                    String response = selectCardAID(channel);

                    System.out.println(response);
                }

                Thread.sleep(2000);

            } catch (CardException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public CardTerminal InitializeTerminal() throws CardException {
        // Get terminal
        System.out.println("Searching for terminals...");
        CardTerminal terminal = null;
        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();

        //Print list of terminals
        for(CardTerminal ter:terminals) {
            System.out.println("Found: "  +ter.getName().toString());
            terminal = terminals.get(0);// We assume just one is connected
        }

        return terminal;
    }

    public boolean IsCardPresent(CardTerminal terminal) throws CardException {
        System.out.println("Waiting for card...");

        boolean isCard = false;

        while (!isCard) {
            isCard = terminal.waitForCardPresent(0);
            if(isCard)
                System.out.println("Card was found! :-)");
        }

        return true;
    }

    public CardChannel GetCardAndOpenChannel(CardTerminal terminal) throws CardException {
        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();

        byte[] baReadUID = new byte[5];
        baReadUID = new byte[]{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        // tag의 uid (unique ID)를 얻은 후 출력
        System.out.println("UID : " + SendCommand(baReadUID, channel));

        return channel;
    }

    public String selectCardAID(CardChannel channel) {

        byte[] baSelectCardAID = new byte[11];
        baSelectCardAID = new byte[]{(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte)0x05,(byte) 0xF2, (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22};

        return SendCommand(baSelectCardAID, channel);
    }

    public static String SendCommand(byte[] cmd, CardChannel channel) {
        String response = "";
        byte[] baResp = new byte[258];

        ByteBuffer bufCmd = ByteBuffer.wrap(cmd);
        ByteBuffer bufResp = ByteBuffer.wrap(baResp);

        int output = 0;

        try {
            output = channel.transmit(bufCmd, bufResp);
        } catch(CardException ex){
            ex.printStackTrace();
        }

        for (int i = 0; i < output; i++) {
            response += String.format("%02X", baResp[i]);
        }

        return response;
    }
}
