

package com.example.map_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/*
 AsyncTask에 대한 내용: https://aroundck.tistory.com/30
*/
public class NationComp_List_Data extends AsyncTask<NationComp_List_Data_Structure, Void, String[]>
{
    //출력할 리스트 뷰
    @SuppressLint("StaticFieldLeak")
    ListView main_statistical_list_list_view;

    /*
    statistical_list_item_adapter: 리스트 어댑터
    리스트 구현 설명: https://baessi.tistory.com/52
    */
    NationComp_List_Item_Adapter main_statistical_list_item_adapter;

    //api 주소
    String api_txt = "http://34.64.176.60/test_html_1/statistical_data_api.xml";
    //api 주소 내용
    String[] api_page;

    /*
    DSD(자료구조정의) 처리 변수

    예시:
    code_list_id[0]: CL_HJG, code_list_name_text[0]: 행정구역별
    code_id[0][0]: 11, code_name_text[0][0]: 서울특별시
    code_id[0][1]: 21, code_name_text[0][1]: 부산광역시
    code_id[0][2]: 22, code_name_text[0][2]: 대구광역시
    code_id[0][3]: 23, code_name_text[0][3]: 인천광역시

    code_list_num: Codelist 태그 개수
    code_num: Code 태그 개수
    */
    String[] code_list_id;
    String[] code_list_name_text;
    int code_list_num;

    //각각의 code_num을 저장할 배열
    int[] code_num_index;

    String[][] code_id;
    String[][] code_name_text;
    int code_num;

    /*
    StructureSpecific(구조화된 수치자료) 처리 변수

    title_num: 통계 제목 번호
    title_string: 통계 제목
    subtitle_num: 통계 부제목 번호
    subtitle_string: 통계 부제목 문자열
    */
    int title_num;
    String title_string = "";
    int subtitle_num;
    String subtitle_string = "";

    //버전 번호 (1: 제목 리스트, 2: 부제목 리스트)
    int version;

    //리스트에 출력될 통계 제목 리스트
    String[] title_list;

    //백그라운드 실행 (변수 초기화)
    @Override
    protected String[] doInBackground(NationComp_List_Data_Structure... NationComp_List_Data_Structures)
    {
        try
        {
            //버전 설정하기 (1: 제목 리스트, 0: 부제목 리스트)
            version = NationComp_List_Data_Structures[0].version;

            title_num = NationComp_List_Data_Structures[0].title_num;
            //subtitle_num = MainStatisticalList_Data_Structures[0].subtitle_num;

            api_page = new String[3];
            api_page[0] = (String)download_url((String)api_txt);

            main_statistical_list_list_view = NationComp_List_Data_Structures[0].main_statistical_list_list_view;

            main_statistical_list_item_adapter = NationComp_List_Data_Structures[0].main_statistical_list_item_adapter;

            if(NationComp_List_Data_Structures[0].version == 2)
            {
                get_statistical_data_api(title_num);
                api_page[1] = (String)download_url((String)api_page[1]);
                //api_page[2] = (String)download_url((String)api_page[2]);
            }

            return api_page;
        }
        catch(IOException e)
        { return null; }
    }

    //전달받은 API에 해당하는 문서 다운로드
    private String download_url(String api) throws IOException
    {
        HttpURLConnection conn = null;

        try
        {
            URL url = new URL(api);
            conn = (HttpURLConnection)url.openConnection();

            BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, StandardCharsets.UTF_8));

            String line = null;
            String page = "";

            while((line = bufreader.readLine()) != null)
            {
                page += line;
            }

            return page;
        }
        finally
        {
            conn.disconnect();
        }
    }

    //최종 실행
    @SuppressLint("ResourceType")
    protected void onPostExecute(String[] result)
    {
        //버전 설정하기 (1: 제목 리스트, 2: 부제목 리스트)
        main_statistical_list_item_adapter.set_version(version);

        if(version == 1)
        {
            get_statistical_title_list(api_page[0]);

            for(int i=0;i<title_list.length;i++)
            {
                //리스트 아이템 추가
                main_statistical_list_item_adapter.add_item(title_list[i]);
            }
        }
        else if(version == 2)
        {
            get_dsd_data(result[1]);

            //부제목이 저장된 id의 번호 구하기
            int subtitle_id_num = get_statistical_title_list();

            for(int i=0;i<code_id[subtitle_id_num].length;i++)
            {
                //리스트 아이템 추가
                main_statistical_list_item_adapter.add_item(code_name_text[subtitle_id_num][i]);
            }
        }

        //리스트 출력
        main_statistical_list_list_view.setAdapter(main_statistical_list_item_adapter);
    }

    //제목 가져오기
    public void get_statistical_title_list(String api_page_txt)
    {
        //XML 문서 이벤트 변수
        int event_type;

        /*
        XML 문서 태그 내용

        예시: <Series:NAME ATTR_NAME_1="A" ATTR_NAME_2="B" ATTR_NAME_3="C">
        xml_prefix = Series
        xml_name = NAME
        xml_attr_name = ATTR_NAME_1, ATTR_NAME_2, ATTR_NAME_3
        xml_attr_value = A, B, C
        attribute_num = 3 (A = 0, B = 1, C = 2)
        */
        String xml_prefix = "";
        String xml_name = "";
        String xml_attr_name = "";
        String xml_attr_value = "";
        String xml_text = "";
        int attribute_num = -1;

        //전체 주소 수
        int api_count = 0;
        //가져올 주소 번호
        int api_num = 0;

        //제목 또는 부제목이 없을 경우 주소의 개수를 센 후 랜덤으로 값을 부여
        try
        {
            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            //파싱할 문서 설정
            xpp.setInput(new StringReader(api_page_txt));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            //XML 문서 검사(code_list_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {
                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();
                    attribute_num = xpp.getAttributeCount();
                    if(xml_name.equals("api"))
                    {
                        //api_count 갯수 세기
                        api_count++;
                    }
                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }

        //통계 제목 리스트 할당
        title_list = new String[api_count];
        api_count = 0;

        //제목을 리스트에 저장
        try
        {
            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            //파싱할 문서 설정
            xpp.setInput(new StringReader(api_page_txt));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            //XML 문서 검사(code_list_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {
                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();
                    attribute_num = xpp.getAttributeCount();

                    if(xml_name.equals("api"))
                    {
                        //api_count 갯수 세기
                        api_count++;

                        for(int i=0;i<attribute_num;i++)
                        {
                            xml_attr_name = xpp.getAttributeName(i);
                            xml_attr_value = xpp.getAttributeValue(i);
                            if(xml_attr_name.equals("name"))
                            {
                                title_list[api_count-1] = xml_attr_value;
                            }
                        }
                    }
                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }
    }

    //부제목 가져오기
    public int get_statistical_title_list()
    {
        for(int i=0;i<code_list_id.length;i++)
        {
            //부제목 비교
            if(code_list_id[i].equals("CL_ITEM"))
            {
                return i;
            }
        }
        return -1;
    }

    //statistical_data_api.xml에서 통계 자료의 주소를 추출하는 함수
    public void get_statistical_data_api(int statistic_title)
    {
        //XML 문서 이벤트 변수
        int event_type;

        /*
        XML 문서 태그 내용

        예시: <Series:NAME ATTR_NAME_1="A" ATTR_NAME_2="B" ATTR_NAME_3="C">
        xml_prefix = Series
        xml_name = NAME
        xml_attr_name = ATTR_NAME_1, ATTR_NAME_2, ATTR_NAME_3
        xml_attr_value = A, B, C
        attribute_num = 3 (A = 0, B = 1, C = 2)
        */
        String xml_prefix = "";
        String xml_name = "";
        String xml_attr_name = "";
        String xml_attr_value = "";
        String xml_text = "";
        int attribute_num = -1;

        //전체 주소 수
        int api_count = 0;
        //가져올 주소 번호
        int api_num = 0;

        //제목 또는 부제목이 없을 경우 주소의 개수를 센 후 랜덤으로 값을 부여
        try
        {
            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            //파싱할 문서 설정
            xpp.setInput(new StringReader(api_page[0]));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            //XML 문서 검사(code_list_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {
                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();
                    attribute_num = xpp.getAttributeCount();
                    if(xml_name.equals("api"))
                    {
                        //api_count 갯수 세기
                        api_count++;
                    }
                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }

        //api 주소번호를 랜덤으로 지정
        if((statistic_title < 0) || (statistic_title > api_count))
        {
            Random rand = new Random();
            api_num = rand.nextInt(api_count);
            api_num++;
        }
        else
        {
            api_num = statistic_title;
        }


        //api_count를 0으로 초기화하여 다시 세기
        api_count = 0;

        //선택한 api 주소 확인 변수
        boolean api_bool = false;

        try
        {
            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            //파싱할 문서 설정
            xpp.setInput(new StringReader(api_page[0]));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            //XML 문서 검사(code_list_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {
                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();
                    attribute_num = xpp.getAttributeCount();

                    if(xml_name.equals("api"))
                    {
                        api_count++;
                    }
                    if(api_count == api_num)
                    {
                        api_bool = true;
                    }
                }
                else if(event_type == XmlPullParser.TEXT)
                {
                    xml_text = xpp.getText();

                    if(api_bool == true)
                    {
                        if(xml_name.equals("dsd"))
                        {
                            if(xml_text.length()>30)
                            {
                                api_page[1] = xml_text;
                            }
                        }
                        else if(xml_name.equals("structure_specific"))
                        {
                            if(xml_text.length()>30)
                            {
                                api_page[2] = xml_text;
                            }
                        }
                        else
                        {
                            api_bool = false;
                        }
                    }

                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }
    }

    //DSD(자료구조정의)에서 데이터 추출하는 함수
    public void get_dsd_data(String result)
    {
        //XML 문서 이벤트 변수
        int event_type;

        /*
        XML 문서 태그 내용

        예시: <Series:NAME ATTR_NAME_1="A" ATTR_NAME_2="B" ATTR_NAME_3="C">
        xml_prefix = Series
        xml_name = NAME
        xml_attr_name = ATTR_NAME_1, ATTR_NAME_2, ATTR_NAME_3
        xml_attr_value = A, B, C
        attribute_num = 3 (A = 0, B = 1, C = 2)
        */
        String xml_prefix = "";
        String xml_name = "";
        String xml_attr_name = "";
        String xml_attr_value = "";
        String xml_text = "";
        int attribute_num = -1;

        try
        {
            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();


            //파싱할 문서 설정
            xpp.setInput(new StringReader(result));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            code_list_num = 0;
            code_num = 0;

            //제목 상태 확인 변수
            boolean header_bool = false;

            //XML 태그 차원 수, 임시 저장 수
            int xml_dimension = 0, temp_dimension = 0;

            //XML 문서 검사(code_list_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {
                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();

                    //System.out.println("=================xml_prefix: " + xml_prefix + ", xml_name: "+ xml_name + "=================\n");

                    if(header_bool == true)
                    {
                        //헤더 안에 있을 때 차원 증가
                        xml_dimension++;
                    }
                    if(xml_prefix.equals("structure") && xml_name.equals("Codelist"))
                    {
                        //code_list_num 갯수 세기
                        code_list_num++;
                    }
                    else if(xml_prefix.equals("message") && xml_name.equals("Header"))
                    {
                        //차원 증가
                        xml_dimension++;
                        //헤더 검사중 확인 설정
                        header_bool = true;
                        //header의 차원수 저장
                        temp_dimension = xml_dimension;
                    }
                }
                else if(event_type == XmlPullParser.TEXT)
                {
                    xml_text = xpp.getText();

                    if(xml_prefix.equals("common") && xml_name.equals("Name"))
                    {
                        if(temp_dimension == xml_dimension-1)
                        {
                            //제목 저장
                            title_string = xml_text;
                        }
                    }

                }
                else if(event_type == XmlPullParser.END_TAG)
                {
                    if(header_bool == true)
                    {
                        //헤더 안에 있는 END 태그일 때 차원 감소
                        xml_dimension--;
                    }
                    if(temp_dimension > xml_dimension)
                    {
                        //헤더가 끝날때 헤더 확인 변수 비활성화
                        header_bool = false;
                    }
                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }

        try
        {
            //text가 Codelist 또는 Code에 있는지 확인하기 위한 변수
            boolean code_list_boolean = false, code_boolean = false;

            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();


            //파싱할 문서 설정
            xpp.setInput(new StringReader(result));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            //code_list 변수 할당
            code_list_id = new String[code_list_num];
            code_list_name_text = new String[code_list_num];
            code_id = new String[code_list_num][];
            code_name_text = new String[code_list_num][];

            //code_list_num 초기화
            code_list_num = 0;

            //XML 문서 검사(code_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {

                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();

                    //Codelist, code 태그 갯수 세기
                    if(xml_prefix.equals("structure") && xml_name.equals("Codelist"))
                    {
                        attribute_num = xpp.getAttributeCount();

                        for(int i=0;i<attribute_num;i++)
                        {
                            if(xpp.getAttributeName(i).equals("id"))
                            {
                                code_list_id[code_list_num] = xpp.getAttributeValue(i);
                            }
                        }

                        //code_num 초기화
                        code_num = 0;

                        code_list_boolean = true;
                        code_boolean = false;

                        code_list_num++;
                    }
                    else if(xml_prefix.equals("structure") && xml_name.equals("Code"))
                    {
                        code_list_boolean = false;
                        code_boolean = true;

                        //code_list_num 갯수 세기
                        code_num++;
                    }
                }
                else if(event_type == XmlPullParser.TEXT)
                {
                    xml_text = xpp.getText();

                    if((xml_prefix.equals("common")) && xml_name.equals("Name"))
                    {
                        if((code_list_boolean == true) && (code_boolean == false))
                        {
                            code_list_name_text[code_list_num-1] = xml_text;
                        }
                    }
                }
                else if(event_type == XmlPullParser.END_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();

                    if(xml_prefix.equals("structure") && xml_name.equals("Codelist"))
                    {
                        if(code_list_num > 0)
                        {
                            //code 변수 할당
                            code_id[code_list_num-1] = new String[code_num];
                            code_name_text[code_list_num-1] = new String[code_num];
                        }
                        code_list_boolean = false;
                    }
                    if(xml_prefix.equals("structure") && xml_name.equals("Code"))
                    {
                        code_boolean = false;
                    }
                }
                event_type = xpp.next();
            }

        }
        catch(Exception e)
        { }

        try
        {
            //text가 Codelist 또는 Code에 있는지 확인하기 위한 변수
            boolean code_list_boolean = false, code_boolean = false;

            //XML Pull Parser 객체 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();


            //파싱할 문서 설정
            xpp.setInput(new StringReader(result));

            //현재 이벤트 유형 반환
            event_type = xpp.getEventType();

            //code_list_num 초기화
            code_list_num = 0;

            //XML 문서 검사(code_num)
            while(event_type != XmlPullParser.END_DOCUMENT)
            {

                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();

                    //Codelist, code 태그 갯수 세기
                    if(xml_prefix.equals("structure") && xml_name.equals("Codelist"))
                    {
                        //code_num 초기화
                        code_num = 0;

                        code_list_boolean = true;
                        code_boolean = false;

                        code_list_num++;
                    }
                    else if(xml_prefix.equals("structure") && xml_name.equals("Code"))
                    {
                        attribute_num = xpp.getAttributeCount();

                        for(int i=0;i<attribute_num;i++)
                        {
                            if(xpp.getAttributeName(i).equals("id"))
                            {
                                code_id[code_list_num-1][code_num] = xpp.getAttributeValue(i);
                            }
                        }

                        code_list_boolean = false;
                        code_boolean = true;

                        //code_list_num 갯수 세기
                        code_num++;
                    }
                }
                else if(event_type == XmlPullParser.TEXT)
                {
                    xml_text = xpp.getText();

                    if((xml_prefix.equals("common")) && xml_name.equals("Name"))
                    {
                        if((code_list_boolean == false) && (code_boolean == true))
                        {
                            code_name_text[code_list_num-1][code_num-1] = xml_text;

                            //main_text_2.append("code_name_text: " + code_name_text[code_list_num-1][code_num-1] + "\n");
                            //main_text_2.append("code_list_num: "+ code_list_num + "code_num: " + code_num);
                        }
                    }
                }
                else if(event_type == XmlPullParser.END_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();

                    if(xml_prefix.equals("structure") && xml_name.equals("Codelist"))
                    {
                        code_list_boolean = false;

                    }
                    if(xml_prefix.equals("structure") && xml_name.equals("Code"))
                    {
                        code_boolean = false;
                    }
                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }
    }
}
