package quizApp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


public class Steganography {

    private static final int DATA_SIZE = 8;
    private static final int MAX_INT_LEN = 4;

   public boolean hide(String textFnm, String imFnm) throws IOException {
       {

           String inputText = Files.readString(Path.of("C:\\Users\\HP\\Desktop\\Kripto\\pitanja\\" + textFnm), StandardCharsets.UTF_8);
           if ((inputText == null) || (inputText.length() == 0))
               return false;
           byte[] stego = buildStego(inputText);

           BufferedImage im= ImageIO.read(new File("C:\\Users\\HP\\Desktop\\Kripto\\slike\\" + imFnm));
           if (im == null)
               return false;
           byte imBytes[] = accessBytes(im);
           if (!singleHide(imBytes, stego)) // ako je true slika sadrzi pitanje
               return false;
           // store the modified image in <fnm>Msg.png
           String fnm = "coded" + imFnm;
           return ImageIO.write(im,"png",new File("C:\\Users\\HP\\Desktop\\Kripto\\pictures\\"+ fnm));
       }

   }

    private static byte[] buildStego(String inputText)
    {

        byte[] msgBytes = inputText.getBytes();
        byte[] lenBs = intToBytes(msgBytes.length);
        int totalLen = lenBs.length + msgBytes.length;
        byte[] stego = new byte[totalLen]; // konacni niz bajtova sa duzinom pitanja i pitanjem

        System.arraycopy(lenBs, 0, stego, 0, lenBs.length);

        System.arraycopy(msgBytes, 0, stego, lenBs.length,msgBytes.length);

        return stego;
    }

    private static byte[] intToBytes(int i)
    {

        byte[] integerBs = new byte[MAX_INT_LEN];
        integerBs[0] = (byte) ((i >>> 24) & 0xFF);
        integerBs[1] = (byte) ((i >>> 16) & 0xFF);
        integerBs[2] = (byte) ((i >>> 8) & 0xFF);
        integerBs[3] = (byte) (i & 0xFF);
        return integerBs;
    }

    private static byte[] accessBytes(BufferedImage image)
    {
        WritableRaster raster = image.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    private static boolean singleHide(byte[] imBytes, byte[] stego)
    {
        int imLen = imBytes.length; // velicina slike
       System.out.println(imLen);
        int totalLen = stego.length; // velicina pitanja

        if ((totalLen*DATA_SIZE) > imLen) {
            System.out.println("Pitanje je predugačko za ovu sliku!");
            return false;
        }
        hideStego(imBytes, stego, 0);
        return true;
    }

    private static void hideStego(byte[] imBytes, byte[] stego,
                                  int offset)
    {
        for (int i = 0; i < stego.length; i++) {
            int byteVal = stego[i];
            for(int j=7; j >= 0; j--) {
                int bitVal = (byteVal >>> j) & 1;

                imBytes[offset] = (byte)((imBytes[offset] & 0xFE) | bitVal);
                offset++;
            }
        }
    }





        public String reveal(String imFnm) throws IOException {

            BufferedImage im= ImageIO.read(new File("C:\\Users\\HP\\Desktop\\Kripto\\pictures\\" + imFnm));
            if (im == null)
                return "";
            byte[] imBytes = accessBytes(im);


            int msgLen = getMsgLength(imBytes, 0);
            if (msgLen == -1)
                return "";


            String msg = getMessage(imBytes, msgLen, MAX_INT_LEN*DATA_SIZE);
            if (msg != null) {

//                String fnm = imFnm;
//                BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\HP\\Desktop\\Kripto\\izlaz\\"+ fnm +".txt"));
//                writer.write(msg);
//                writer.close();
                return msg;
            }
            else {
                System.out.println("Poruka nije pronadjena");
                return "";
            }
        }



    private static int getMsgLength(byte[] imBytes, int offset)
    {
        byte[] lenBytes = extractHiddenBytes(imBytes, MAX_INT_LEN, offset);

        if (lenBytes == null)
            return -1;

        int msgLen = ((lenBytes[0] & 0xff) << 24) |
                ((lenBytes[1] & 0xff) << 16) |
                ((lenBytes[2] & 0xff) << 8) |
                (lenBytes[3] & 0xff);
        if ((msgLen <= 0) || (msgLen > imBytes.length)) {
            System.out.println("Pogresna velicina poruke!");
            return -1;
        }
        return msgLen;
    }

    private static byte[] extractHiddenBytes(byte[] imBytes,
                                             int size, int offset)
    {
        int finalPosn = offset + (size*DATA_SIZE);
        if (finalPosn > imBytes.length) {
            System.out.println("Greska!Predjen je kraj slike!");
            return null;
        }
        byte[] hiddenBytes = new byte[size];
        for (int j = 0; j < size; j++) {
            for (int i=0; i < DATA_SIZE; i++) {

                hiddenBytes[j] = (byte) ((hiddenBytes[j] << 1) |
                        (imBytes[offset] & 1));

                offset++;
            }
        }
        return hiddenBytes;
    }

    private static String getMessage(byte[] imBytes, int msgLen, int
            offset)
    {
        byte[] msgBytes = extractHiddenBytes(imBytes, msgLen, offset);

        if (msgBytes == null)
            return null;

        String msg = new String(msgBytes);

        return msg;




        }



}
