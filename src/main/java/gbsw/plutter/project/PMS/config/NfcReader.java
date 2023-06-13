package gbsw.plutter.project.PMS.config;

import javax.smartcardio.*;

public class NfcReader {
    public static void main(String[] args) {
        try {
            TerminalFactory terminalFactory = TerminalFactory.getDefault();
            CardTerminals cardTerminals = terminalFactory.terminals();

            // NFC 태그를 대기하고 있는지 확인
            if (cardTerminals.list().isEmpty()) {
                System.out.println("No NFC tag found.");
                return;
            }

            // 첫 번째 NFC 리더기를 선택
            CardTerminal cardTerminal = cardTerminals.list().get(0);

            // NFC 태그와 연결
            Card card = cardTerminal.connect("*");

            // ATR(Appearance Time Response) 값을 출력
            ATR atr = card.getATR();
            System.out.println("ATR: " + bytesToHex(atr.getBytes()));

            // NFC 태그에 대한 카드 리더 생성
            CardChannel cardChannel = card.getBasicChannel();

            // SELECT 명령어 전송
            byte[] selectApdu = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, (byte) 0xD2, (byte) 0x76, (byte) 0x00, (byte) 0x00, (byte) 0x85, (byte) 0x01, (byte) 0x01};
            ResponseAPDU response = cardChannel.transmit(new CommandAPDU(selectApdu));

            if (response.getSW() != 0x9000) {
                System.out.println("SELECT command failed.");
                return;
            }

            // READ_BINARY 명령어 전송
            byte[] readBinaryApdu = {(byte) 0x00, (byte) 0xB0, (byte) 0x00, (byte) 0x00, (byte) 0x0F};
            response = cardChannel.transmit(new CommandAPDU(readBinaryApdu));

            if (response.getSW() != 0x9000) {
                System.out.println("READ_BINARY command failed.");
                return;
            }

            // IMEI 값 추출
            byte[] responseData = response.getData();
            byte[] imeiBytes = new byte[15];
            System.arraycopy(responseData, 6, imeiBytes, 0, 15);
            String imei = new String(imeiBytes);
            System.out.println("IMEI: " + imei);

            // NFC 태그와의 연결 종료
            card.disconnect(false);
        } catch (CardException e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
