import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Test {

	public static void main(String[] args) {
		File file = new File("/home/user/git/Cache-Simulator/Unicore/FFT1.prg");
		FileInputStream fis;
		ArrayList<String> str = new ArrayList<String>();
		try {
			fis = new FileInputStream(file);

			BufferedInputStream bis = new BufferedInputStream(fis);
			BufferedReader trace = new BufferedReader(new InputStreamReader(bis));
			String s;
			while(trace.ready()){
				s=trace.readLine();
				s=s.split(" ")[1];
				if(!str.contains(s)){
					str.add(s);
					System.out.println(s);
				}
			}
//			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
