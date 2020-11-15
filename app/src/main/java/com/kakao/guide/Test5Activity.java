package com.kakao.guide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Test5Activity extends AppCompatActivity {
    public final static String PHARM_URL1 = "http://apis.data.go.kr/1262000/CountryBasicService/getCountryBasicList";
    public final static String PHARM_URL2 = "http://apis.data.go.kr/1262000/AccidentService/getAccidentInfo";
    public final static String KEY = "HR8TyxL0w4ktjhNK3sGgYDehPyfUNFiQmInLBxO4Oacj0WiY4aDSIGvjVLgMdt0SnrgXg6YGKMTlryaLcEFL0w%3D%3D";
    TextView text_country_list, text_country_continent, text_country_name, text_country_engName;
    String news;
    ImageView Image_country_image;
    Bitmap bitmap;

    // 지오코드 관련 테스트위치
    LatLng myGPS = new LatLng(35.647570, 138.079449);  // 일본 임.
    String CnameforGPS = "";                                // 이 위치의 국가이름.
    Geocoder geocoder = new Geocoder(this);

    List<Address> list = null;

    String continent = "";          // 대륙
    String countryEnName = "";      // 국가 영문이름.
    String countryCode = "";        // 국가코드.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test5_layout);
        text_country_list = (TextView)findViewById(R.id.text_country_list);
        text_country_continent = (TextView)findViewById(R.id.text_country_continent);
        text_country_name = (TextView)findViewById(R.id.text_country_name);
        text_country_engName = (TextView)findViewById(R.id.text_country_engName);
        Image_country_image = (ImageView) findViewById(R.id.Image_country_flag);

        // layout에 내 위치에 따른 국가이름 지정..
        text_country_name.setText(countryNameForGPS(myGPS));
        CnameforGPS = countryNameForGPS(myGPS);

        // 국가이름에 따른 조회. 비동기로 실행됨.
        String url = PHARM_URL1 +"?ServiceKey="+KEY+"&countryName="+CnameforGPS;
        OpenAPIforTest findCode = new OpenAPIforTest(url);
        findCode.execute();


    }

    // 좌표에 따른 해당위치 국가이름 반환.
    public String countryNameForGPS(LatLng gps) {
        String country = "none";
        LatLng location = gps;

        try {
            list = geocoder.getFromLocation(location.latitude, location.longitude,10);
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류");
        }
        if (list != null) {
            if (list.size()==0) {
                // 정보조회된 거 없음.
            } else {
                // 조회된 국가이름 반환.
                country = list.get(0).getCountryName();
            }
        }
        return country;
    }

    // 국가정보 of 공공데이터
    public class OpenAPIforTest extends AsyncTask<Void, Void, String> {
        private String url;

        public OpenAPIforTest(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactoty.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document doc = null;


            // 1. 국가이름 조회 2. 국가사건사고정보 조회 구분.
            if(url.contains("getCountryBasicList")) {
                try {
                    doc = dBuilder.parse(url);
                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                    Log.e("errooooor", "hi!!!");
                }
                // root tag
                doc.getDocumentElement().normalize();
                System.out.println("Root element: " + doc.getDocumentElement().getNodeName()); // Root element: result
                // 파싱할 tag
                NodeList nList = doc.getElementsByTagName("item");
                for(int temp = 0; temp < nList.getLength(); temp++){
                    Node nNode = nList.item(temp);
                    if(nNode.getNodeType() == Node.ELEMENT_NODE){
                        // 해당 태그별로 정보 분리 가능.
                        Element eElement = (Element) nNode;
                        Log.d("OPEN_API","국가코드  : " + getTagValue("id", eElement));
                        //imgUrl
                        countryCode = getTagValue("id", eElement);
                        continent = getTagValue("continent", eElement);
                        countryEnName = getTagValue("countryEnName", eElement);
                        bitmap = getImagefromURL(getTagValue("imgUrl", eElement));
                    }	// for end
                }	// if end
            }else if(url.contains("getAccidentInfo")){
                try {
                    doc = dBuilder.parse(url);
                } catch (IOException | SAXException e) {
                    e.printStackTrace();
                    Log.e("errooooor", "hi!!!");
                }
                // root tag
                doc.getDocumentElement().normalize();
                System.out.println("Root element: " + doc.getDocumentElement().getNodeName()); // Root element: result
                // 파싱할 tag
                NodeList nList = doc.getElementsByTagName("item");
                for(int temp = 0; temp < nList.getLength(); temp++){
                    Node nNode = nList.item(temp);
                    if(nNode.getNodeType() == Node.ELEMENT_NODE){
                        // 해당 태그별로 정보 분리 가능.
                        Element eElement = (Element) nNode;
                        Log.d("OPEN_API","국가이름  : " + getTagValue("news", eElement));
                        //imgUrl
                        news = getTagValue("news", eElement);
//                    Log.d("OPEN_API","미세먼지  : " + getTagValue("pm10Value", eElement));
//                    Log.d("OPEN_API","초미세먼지 : " + getTagValue("pm25Value", eElement));
                    }	// for end
                }	// if end
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // 결과.
            if(url.contains("getCountryBasicList")) {
                String url2 = PHARM_URL2 +"?ServiceKey="+KEY+"&id="+countryCode;
                OpenAPIforTest findAccident = new OpenAPIforTest(url2);
                findAccident.execute();
                Log.d("OPEN_API", "안녕"+countryCode);
                Image_country_image.setImageBitmap(bitmap);
                text_country_continent.setText(continent);
                text_country_engName.setText(countryEnName);
            }else if(url.contains("getAccidentInfo")) {
                news = Html.fromHtml(news).toString();
                text_country_list.setText(news);
            }
        }
    }

    private String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }


    public Bitmap getImagefromURL(final String photoURL){
        if ( photoURL == null) return null;
        try {
            URL url = new URL(photoURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();
            int responseStatusCode = httpURLConnection.getResponseCode();
            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            }
            else
                return null;
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            httpURLConnection.disconnect();
            return  bitmap;
        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }
        return null;
    }

}
// 	HR8TyxL0w4ktjhNK3sGgYDehPyfUNFiQmInLBxO4Oacj0WiY4aDSIGvjVLgMdt0SnrgXg6YGKMTlryaLcEFL0w%3D%3D
// DB리스너는 onCrete에서만 돌아가나?
//  http://apis.data.go.kr/1262000/AccidentService/getAccidentInfo?ServiceKey=HR8TyxL0w4ktjhNK3sGgYDehPyfUNFiQmInLBxO4Oacj0WiY4aDSIGvjVLgMdt0SnrgXg6YGKMTlryaLcEFL0w%3D%3D&id=18
// http://apis.data.go.kr/1262000/CountryBasicService/getCountryBasicList?ServiceKey=HR8TyxL0w4ktjhNK3sGgYDehPyfUNFiQmInLBxO4Oacj0WiY4aDSIGvjVLgMdt0SnrgXg6YGKMTlryaLcEFL0w%3D%3D]&isoCode1=CHN
