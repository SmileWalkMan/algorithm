package deeplearning;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream("deeplearning/0.csv");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			int n=0;
			String str = null;
			str = bufferedReader.readLine();
			Double preValue = Double.parseDouble(str);
			String arrStr = "";
			List<Double> dlist = new ArrayList<Double>();
			List<String> target = new ArrayList<String>();
			
			while((str = bufferedReader.readLine()) != null)
			{
				Double curValue=Double.parseDouble(str);
				dlist.add(curValue);
			}
			for(int i=50;i<dlist.size();i++) {
				System.out.print("[["+dlist.get(i-20)/4000+"],");
				for(int k=i-20+1;k<i-1;k++) {
					System.out.print("["+dlist.get(k)/4000+"],");
				}
				System.out.print("["+dlist.get(i-1)/4000+"]],");
			}
			System.out.println();
			for(int i=50;i<dlist.size();i++) {
				if(dlist.get(i)-dlist.get(i-1)>0)
					System.out.print("[["+1+"]],");
				else
					System.out.print("[["+0+"]],");
			}
				
			//close
			inputStream.close();
			bufferedReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
