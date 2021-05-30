package exam1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

// 네이버 기계번역 (Papago SMT) API 예제
public class ApiExamTranslateNmt {

	public static void main(String[] args) {
		String clientId = "vfPkwI08_EQhr8LFX75J";// 애플리케이션 클라이언트 아이디값";
		String clientSecret = "tkUXFrsxJ8";// 애플리케이션 클라이언트 시크릿값";

		String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
		Scanner sc = new Scanner(System.in);
		System.out.print("번역해드리겠습니다. >> ");
		String text = sc.nextLine();
		HttpURLConnection con = null;
		BufferedReader br = null;
		BufferedWriter bw = null;

		try {
			text = URLEncoder.encode(text, "UTF-8");
			Map<String, String> requestHeaders = new HashMap<>();
			requestHeaders.put("X-Naver-Client-Id", clientId);
			requestHeaders.put("X-Naver-Client-Secret", clientSecret);
			
			con = connect(apiURL);
			String postParams = "source=ko&target=en&text=" + text;
			con.setRequestMethod("POST");
			for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
				con.setRequestProperty(header.getKey(), header.getValue());
			}
			con.setDoOutput(true);
			try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder responseBody = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				responseBody.append(line);
			}
			String result = responseBody.toString();
			bw = new BufferedWriter(new FileWriter("translate_result.txt"));
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);
			JSONObject obj2 = (JSONObject) obj.get("message");
			JSONObject obj3 = (JSONObject) obj2.get("result");
			String translatedText = (String) obj3.get("translatedText");
			bw.write(translatedText);
			bw.newLine();
		
			System.out.println("translate_result.txt 파일에 결과가 저장되었습니다.");

		} catch (Exception e) {
			try {
				bw = new BufferedWriter(new FileWriter("translate_error.txt"));
				bw.write(e.getMessage() + "\t" + new SimpleDateFormat("yyyy-MM-dd a h:mm:ss").format(new Date()));
				System.out.println("translate_error.txt 파일에 에러 로그가 저장되었습니다.");
			} catch (Exception e2) {
			}
		} finally {
			try {
				if (con != null) {
					con.disconnect();
				}
				if (br != null) {
					br.close();
				}
				if (bw != null) {
					bw.close();
				}
			} catch (Exception e) {
			}
		}
		sc.close();
	}
	
	private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }
	
}
