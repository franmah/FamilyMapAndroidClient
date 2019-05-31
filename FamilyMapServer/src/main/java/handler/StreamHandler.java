package handler;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/** Take care of writing in an OutputStream, Reading from an InputStream.
 *
 */
public class StreamHandler {

    public StreamHandler(){}

    public String readInputStream(InputStream is) throws IOException {
        StringBuilder final_string = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while((len = reader.read(buf)) > 0){
            final_string.append(buf, 0, len);
        }
        return final_string.toString();
    }

    public void writeToOutputStream(String str, OutputStream os) throws IOException{
        OutputStreamWriter writer = new OutputStreamWriter(os);
        writer.write(str);
        writer.flush();
    }

}
