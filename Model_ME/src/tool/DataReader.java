package tool;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataReader {

	private int mLimit;
	private int mServerNumber;
	private int[] mUserNumbers;
	
	public void read(String filePath) {
		File file = new File(filePath);

		Scanner sc;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				String string = sc.nextLine().replaceAll(" ", "");
				if (string.startsWith("limit")) {
					mLimit = Integer.valueOf(string.replaceAll("limit=", ""));
				} else if (string.startsWith("serverNumber")) {
					mServerNumber = Integer.valueOf(string.replaceAll("serverNumber=", ""));
					mUserNumbers = new int[mServerNumber];
				} else if (string.startsWith("userNumbers")) {
					String[] values = string.replaceAll("userNumbers=", "").split(",");
					for (int i = 0; i < mServerNumber; i++) {
						mUserNumbers[i] = Integer.valueOf(values[i]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getLimit() {
		return mLimit;
	}

	public int getServerNumber() {
		return mServerNumber;
	}

	public int[] getUserNumbers() {
		return mUserNumbers;
	}

}
