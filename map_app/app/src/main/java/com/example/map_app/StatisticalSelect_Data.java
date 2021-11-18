package com.example.map_app;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
public class StatisticalSelect_Data extends AsyncTask<StatisticalSelect_Data_Structure, Void, String[][]>
{
    //api 주소
    String api_txt = "http://34.64.176.60/test_html_1/statistical_data_api.xml";
    //api 주소 내용
    String[][] api_page;

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
    local_num: 통계 자료에서 찾을 지역
    */
    int[] title_num;
    String title_string = "";
    int[] subtitle_num;
    String subtitle_string = "";
    int[] local_num;

    /*
    그래프를 그리기 위한 변수

    nation_comp_menu_list_view: 리스트 뷰
    nation_comp_menu_list_item_adapter: 리스트 어댑터
    graph_background_view: 그래프 배경 그림
    graph_data_view: 그래프 데이터 그림
    list_graph_view: 리스트에 출력할 그래프 뷰
    nation_comp_list_item_layout: 리스트에 출력할 그래프 프레임 뷰

    @SuppressLint 설명: @SuppressLint("NewApi")는 해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때  warning을 없애고 개발자가 해당 APi를 사용할 수 있게 합니다.
    출처: https://nsstbg.tistory.com/13
    */
    @SuppressLint("StaticFieldLeak")
    FrameLayout statistical_select_menu_layout;

    @SuppressLint("StaticFieldLeak")
    Graph_Draw[] graph_background_view, graph_data_view;

    int last_view_num;

    boolean[][] main_graph_bool;

    int VIEW_BASE_ID = 333000;

    int version;
    int version_view_num = 0;

    //백그라운드 실행 (변수 초기화)
    @Override
    protected String[][] doInBackground(StatisticalSelect_Data_Structure... statistical_select_data_structures)
    {
        try
        {
            version = statistical_select_data_structures[0].version;

            statistical_select_menu_layout = statistical_select_data_structures[0].statistical_select_menu_layout;

            title_num = statistical_select_data_structures[0].title_num;
            subtitle_num = statistical_select_data_structures[0].subtitle_num;

            last_view_num = statistical_select_data_structures[0].last_view_num;

            local_num = statistical_select_data_structures[0].local_num;

            if(version == 1)
            {
                version_view_num = 0;
            }
            else if(version == 2)
            {
                version_view_num = last_view_num;
            }

            api_page = new String[title_num.length][3];
            for(int i=version_view_num;i<last_view_num+1;i++)
            {
                api_page[i][0] = (String)download_url((String)api_txt);
                get_statistical_data_api(i, title_num[i]);

                api_page[i][1] = (String)download_url((String)api_page[i][1]);
                api_page[i][2] = (String)download_url((String)api_page[i][2]);

                graph_background_view = statistical_select_data_structures[0].graph_background_view;
                graph_data_view = statistical_select_data_structures[0].graph_data_view;

                main_graph_bool = statistical_select_data_structures[0].main_graph_bool;
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
    protected void onPostExecute(String[][] result)
    {
        for(int h=version_view_num;h<last_view_num+1;h++)
        {
            //DSD(자료구조정의)에서 데이터 자료 추출
            get_dsd_data(result[h][1]);

            //그래프 데이터 배열 할당
            graph_data_view[h].alloc_data_array(local_num.length);

            //그래프 데이터 수에 따른 배열 할당, 초기화
            graph_data_view[h].graph_data_array_init();

            for(int i=0;i<graph_data_view[h].graph_data_num;i++)
            {
                //StructureSpecific(구조화된 수치자료)에서 원하는 자료 추출 후 전송
                graph_data_view[h].data_value[i] =  Float.parseFloat(get_structure_specific_data(result[h][2], subtitle_num[h], local_num[i]));
            }

            //그래프 부제목, 지역 정보 입력
            graph_data_view[h].graph_element.set_local_data(title_string, subtitle_string, city_string_array(local_num));

            //그래프 배경 크기 설정
            graph_background_view[h].set_statistical_select_graph_list_size(statistical_select_menu_layout);

            //그래프 위치 설정
            graph_background_view[h].set_statisticalselect_graph_background_pos(statistical_select_menu_layout, h);

            //그래프 데이터 크기정보 설정
            graph_data_view[h].set_graph_data_pos_and_size(graph_background_view[h].graph_element);

            //그래프 배경이 없을 경우
            if(main_graph_bool[h][0] == false)
            {
                //그래프 배경 그리기
                statistical_select_menu_layout.addView(graph_background_view[h]);
            }
            main_graph_bool[h][0] = true;

            //그래프 데이터가 있을 경우
            if(main_graph_bool[h][1] == true)
            {
                //그래프 데이터, 지우고 다시 그리기
                statistical_select_menu_layout.removeView(statistical_select_menu_layout.findViewById(VIEW_BASE_ID + (last_view_num*2) + 1));
            }
            statistical_select_menu_layout.addView(graph_data_view[h]);

            main_graph_bool[h][1] = true;
        }
    }

    //각각의 변수 구하기
        /*
        String[] code_list_id;
        String[] code_list_name_text;
        int code_list_num;
        String[][] code_id;
        String[][] code_name_text;
        int code_num;
        */
    //메인 메뉴의 도시 번호를 도시 이름으로 변환
    public String city_string(int num)
    {
        switch(num)
        {
            case 0:
                return "서울특별시";
            case 1:
                return "부산광역시";
            case 2:
                return "대구광역시";
            case 3:
                return "인천광역시";
            case 4:
                return "광주광역시";
            case 5:
                return "대전광역시";
            case 6:
                return "울산광역시";
            case 7:
                return "세종특별자치시";
            default:
                return "";
        }
    }

    //메인 메뉴의 도시 번호 배열을 도시 이름 배열로 변환(파싱에 쓰이지 않음)
    public String[] city_string_array(int[] num)
    {
        String[] temp_string = new String[num.length];

        for(int i=0;i<num.length;i++)
        {
            switch(num[i])
            {
                case 0:
                    temp_string[i] = "서울";
                    break;
                case 1:
                    temp_string[i] = "부산";
                    break;
                case 2:
                    temp_string[i] = "대구";
                    break;
                case 3:
                    temp_string[i] = "인천";
                    break;
                case 4:
                    temp_string[i] = "광주";
                    break;
                case 5:
                    temp_string[i] = "대전";
                    break;
                case 6:
                    temp_string[i] = "울산";
                    break;
                case 7:
                    temp_string[i] = "세종";
                    break;
                default:
                    temp_string[i] = "";
                    break;
            }
        }
        return temp_string;
    }

    //메인 메뉴의 도시 선택
    public void num_to_city(int num, int[] code_index, String[][] code_name_text)
    {
        String temp_local;
        temp_local = city_string(num);

        for(int i=0;i<code_name_text.length;i++)
        {
            for(int j=0;j<code_name_text[i].length;j++)
            {
                if(code_name_text[i][j].equals(temp_local))
                {
                    code_index[0] = i;
                    code_index[1] = j;
                }
            }
        }
    }
    //statistical_data_api.xml에서 통계 자료의 주소를 추출하는 함수
    public void get_statistical_data_api(int current_view_num, int statistic_title)
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
            xpp.setInput(new StringReader(api_page[current_view_num][0]));

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
            xpp.setInput(new StringReader(api_page[current_view_num][0]));

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
                                api_page[current_view_num][1] = xml_text;
                            }
                        }
                        else if(xml_name.equals("structure_specific"))
                        {
                            if(xml_text.length()>30)
                            {
                                api_page[current_view_num][2] = xml_text;
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

    /*
    StructureSpecific(구조화된 수치자료)에서 데이터를 추출하는 함수

    statistic_subtitle: 통계 부제목(항목)
    statistic_local: 통계 자료에서 찾을 지역

    DSD(자료구조정의)에서 get_dsd_data()함수로 추출한 데이터
    String[] code_list_id
    String[][] code_name_text
    */
    public String get_structure_specific_data(String result, int statistic_subtitle, int statistic_local)
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
        String[] xml_attr_name;
        String[] xml_attr_value;
        String xml_text = "";
        int attribute_num = -1;

        //찾고싶은 지역의 code 위치
        int[] code_index = new int[2];

        //속성값이 일치하는지 확인하기 위한 변수
        boolean attr_boolean = false;

        //찾고싶은 데이터의 위치 확인
        num_to_city(statistic_local, code_index, code_name_text);

        //찾은 데이터
        String data = "";

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

            int test_num = 0;

            //XML 문서 검사
            while(event_type != XmlPullParser.END_DOCUMENT)
            {
                if(event_type == XmlPullParser.START_TAG)
                {
                    xml_prefix = xpp.getPrefix();
                    xml_name = xpp.getName();

                    if((xml_prefix == null) && xml_name.equals("Series"))
                    {
                        attr_boolean = false;

                        //매개변수 index = attribute_num - 1
                        attribute_num = xpp.getAttributeCount();

                        if(attribute_num > 0)
                        {
                            xml_attr_name = new String[attribute_num];
                            xml_attr_value = new String[attribute_num];

                            for(int i=0;i<attribute_num;i++)
                            {
                                xml_attr_name[i] = xpp.getAttributeName(i);
                                xml_attr_value[i] = xpp.getAttributeValue(i);
                                //main_text.append(" " + xml_attr_name[i]);
                            }
                            //main_text.append("\n");

                            //속성들을 검사하여 일치하지 않으면 false로 전환
                            attr_boolean = true;

                            for(int i=0;i<attribute_num;i++)
                            {
                                for(int j=0;j<code_list_id.length;j++)
                                {
                                    //id가 C_로 시작할 경우
                                    if(xml_attr_name[i].substring(0, 2).equals("C_"))
                                    {
                                        //xml_attr_name[i].substring(2) >> C_XXX에서 C를 잘라 XXX를 추출하기
                                        //code_list_id[j].substring(3) >> CL_XXX에서 CL_을 잘라 XXX를 추출하기
                                        if(xml_attr_name[i].substring(2).equals(code_list_id[j].substring(3)))
                                        {
                                            //도시 비교(code_index[0]: 도시 항목을 가리키는 수, code_index[1]: 특정 도시를 가리키는 수)
                                            if(j == code_index[0])
                                            {
                                                //main_text.append("xml_attr_value "+xml_attr_value[i]+"\n");
                                                //main_text.append("code_name_text "+ code_id[code_index[0]][code_index[1]]+"\n");
                                                if(xml_attr_value[i].equals(code_id[code_index[0]][code_index[1]]) == false)
                                                {
                                                    attr_boolean = false;
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        //code_list_id[j].substring(3) >> CL_XXX에서 CL_을 잘라 XXX를 추출하기
                                        if(xml_attr_name[i].equals(code_list_id[j].substring(3)))
                                        {
                                            //부제목 비교 (부제목이 없을 경우: -1)
                                            if(xml_attr_name[i].equals("ITEM") && (statistic_subtitle >= 0))
                                            {
                                                if(xml_attr_value[i].equals(code_id[j][statistic_subtitle]) == false)
                                                {
                                                    attr_boolean = false;
                                                }
                                                else
                                                {
                                                    subtitle_string = code_name_text[j][statistic_subtitle];
                                                }
                                            }

                                            //도시 비교(code_index[0]: 도시 항목을 가리키는 수, code_index[1]: 특정 도시를 가리키는 수)
                                            if(j == code_index[0])
                                            {
                                                if(xml_attr_value[i].equals(code_id[code_index[0]][code_index[1]]) == false)
                                                {
                                                    attr_boolean = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if((xml_prefix == null) && xml_name.equals("Obs"))
                    {
                        if(attr_boolean == true)
                        {
                            //매개변수 index = attribute_num - 1
                            attribute_num = xpp.getAttributeCount();

                            if(attribute_num > 0)
                            {
                                xml_attr_name = new String[attribute_num];
                                xml_attr_value = new String[attribute_num];

                                for(int i=0;i<attribute_num;i++)
                                {
                                    xml_attr_name[i] = xpp.getAttributeName(i);
                                    xml_attr_value[i] = xpp.getAttributeValue(i);
                                }

                                for(int i=0;i<attribute_num;i++)
                                {
                                    if(xml_attr_name[i].equals("OBS_VALUE"))
                                    {
                                        data = xml_attr_value[i];
                                    }
                                }
                            }
                        }
                    }
                }
                event_type = xpp.next();
            }
        }
        catch(Exception e)
        { }

        //데이터가 없을 경우
        if(data.isEmpty())
        {
            data = "0";
        }

        return data;
    }
}
