import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.io.*;
import java.util.ArrayList;

public class WriteToFile {
    static int count = 0;
    static void writeToFile(ArrayList<Byte> bytesList) throws IOException {


        OutputStream os = null;


        try {
            os = new FileOutputStream(Main.filePath, true);
            byte[] array = new byte[bytesList.size()];

            for (int j=0;j<bytesList.size();j++) {
               array[j] = bytesList.get(j);
               count+=1;
            }
            os.write(array);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                assert os != null;
                os.close();
                System.out.println("total Bytes written" + count);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
