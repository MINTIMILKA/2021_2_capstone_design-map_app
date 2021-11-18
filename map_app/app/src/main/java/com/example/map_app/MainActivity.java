package com.example.map_app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SymbolTable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.naver.maps.map.MapFragment;

import java.util.Random;

public class MainActivity extends AppCompatActivity  {

    //맵 레이아웃 초기화
    FrameLayout main_map_layout;

    //메인 버튼 초기화
    Button_Event button_event;

    //맵 MapFragment, FragmentManager 초기화
    MapFragment main_map_fragment;
    FragmentManager fragment_manager;

    //맵 초기화
    Map_Draw map_draw;

    //버튼 객체 초기화
    Button change_next_button;

    //그래프 구조체(최대 4개)
    class graph_structure
    {
        //그래프, 그래프 데이터 초기화
        Statistical_Data main_graph;
        Statistical_Data_Structure main_graph_data;

        //그래프 번호
        int view_num;

        //메인 메뉴에서 선택한 지역
        int[] main_local_num;

        //리스트에서 선택한 제목
        int main_title;

        //리스트에서 선택한 부제목
        int main_subtitle;

        //그래프 테이터 색
        int[] data_color;

        graph_structure()
        {
            //각 그래프의 지역 숫자 할당
            this.main_local_num = new int[2];

            //데이터 색을 저장할 공간 할당
            this.data_color = new int[this.main_local_num.length];
        }
    }

    //그래프 구조체 배열 선언
    static graph_structure[] main_graph_group;

    //현재 선택한 지역 번호
    int current_view_num = 0;

    //생성할 수 있는 최대 지역 번호
    int MAX_VIEW_NUM = 4;

    //지역번호 상태 변수(true: 그래프 생성됨, false: 그래프 제거됨)
    boolean[] current_view_num_bool;

    //리스트 선택 확인 변수
    static boolean list_bool = false;

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //툴바 초기화
        Toolbar main_tool_bar = (Toolbar)findViewById(R.id.main_tool_bar);
        setSupportActionBar(main_tool_bar);
        main_tool_bar.setTitle(R.string.app_name);

        //버튼 초기화
        button_event_init(this);

        //맵 초기화
        map_init();

        //맵 프레임 레이아웃 초기화
        main_map_layout = (FrameLayout)findViewById(R.id.main_map);
        //버튼 초기화
        change_next_button = (Button)findViewById(R.id.change_next_button);
        //지역번호 상태 변수 초기화
        current_view_num_bool = new boolean[MAX_VIEW_NUM];
        for(int i=0;i<MAX_VIEW_NUM;i++){current_view_num_bool[i] = false;}

        //그래프 그리기 초기화
        graph_init();
    }

    //툴바 메뉴 구성
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    //툴바 메뉴 선택
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int item_id = item.getItemId();

        if(item_id == R.id.statistic_select_item)
        {
            //통계 모음
            Intent statistic_select_intent;
            statistic_select_intent = new Intent(this, StatisticalSelectMenu.class);
            startActivity(statistic_select_intent);
            return true;
        }
        else if(item_id == R.id.nation_comp_item)
        {
            //전국 비교
            Intent nation_comp_intent;
            nation_comp_intent = new Intent(this, NationCompMenu.class);
            startActivity(nation_comp_intent);
            return true;
        }
        else if(item_id == R.id.dev_info)
        {
            //개발자 정보
            AlertDialog.Builder dev_info = new AlertDialog.Builder(this);

            String dev_info_message = "";
            dev_info_message += "한남대학교\n";
            dev_info_message += "컴퓨터통신무인기술학과 유태호\n";
            dev_info_message += "컴퓨터통신무인기술학과 김평석";

            dev_info.setTitle("개발자 정보");
            dev_info.setMessage(dev_info_message);

            dev_info.setPositiveButton("확인", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.cancel();
                }
            });

            AlertDialog dev_info_alert = dev_info.create();

            dev_info_alert.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //맵 초기화
    private void map_init()
    {
        //맵 fragment 구하기
        fragment_manager = getSupportFragmentManager();
        main_map_fragment = (MapFragment)fragment_manager.findFragmentById(R.id.main_map);

        //맵 초기화 (객체 생성)
        map_draw = new Map_Draw(main_map_fragment, fragment_manager);
        map_draw.map_init();
    }


    //그래프 그리기 초기화
    private void graph_init()
    {
        //그래프 할당
        main_graph_group = new graph_structure[MAX_VIEW_NUM];

        for(int i=0;i<MAX_VIEW_NUM;i++)
        {
            main_graph_group[i] = new graph_structure();

            //랜덤으로 데이터 초기화
            Random rand = new Random();
            for(int j=0;j<main_graph_group[i].main_local_num.length;j++)
            {
                main_graph_group[i].main_local_num[j] = rand.nextInt(8);
                for(int k=0;k<j;k++)
                {
                    if(main_graph_group[i].main_local_num[j] == main_graph_group[i].main_local_num[k])
                    {
                        j--;
                        break;
                    }
                }
            }

            //그래프 번호 초기화
            main_graph_group[i].view_num = i;

            //제목 초기화(랜덤으로 초기화 >> 부제목 선택 리스트에 제목 번호를 전송하기 위해 양수로 초기화(랜덤: -1))
            main_graph_group[i].main_title = rand.nextInt(100) + 1;

            //부제목 초기화(처음항목으로 초기화)
            main_graph_group[i].main_subtitle = 0;
        }

        //그래프 초기화
        main_graph_group[0].main_graph = new Statistical_Data();
        main_graph_group[0].main_graph_data = new Statistical_Data_Structure(this, main_map_layout, map_draw, main_graph_group[0].view_num, main_graph_group[0].main_title, main_graph_group[0].main_subtitle, main_graph_group[0].main_local_num);

        //그래프 실행
        main_graph_group[0].main_graph.execute(main_graph_group[0].main_graph_data);

        //그래프 상태 변수 변경
        current_view_num_bool[0] = true;

        //그래프 할당 해제
        main_graph_group[0].main_graph = null;
        main_graph_group[0].main_graph_data = null;
    }

    //버튼 이벤트 초기화 함수
    private void button_event_init(Context context)
    {
        button_event = new Button_Event(context);
    }
    //버튼 클릭 함수
    public void seoul_button_onclick(View view)
    {
        //서울
        button_event.seoul_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void busan_button_onclick(View view)
    {
        //부산
        button_event.busan_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void deagu_button_onclick(View view)
    {
        //대구
        button_event.deagu_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void incheon_button_onclick(View view)
    {
        //인천
        button_event.incheon_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void gwangju_button_onclick(View view)
    {
        //광주
        button_event.gwangju_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void daejeon_button_onclick(View view)
    {
        //대전
        button_event.daejeon_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void ulsan_button_onclick(View view)
    {
        //울산
        button_event.ulsan_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    public void sejong_button_onclick(View view)
    {
        //세종
        button_event.sejong_button_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    //표 생성하기 버튼
    public void graph_add_onclick(View view)
    {
        button_event.graph_add_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }
    //표 지우기 버튼
    public void graph_remove_onclick(View view)
    {
        button_event.graph_remove_onclick(current_view_num_bool, map_draw, main_map_layout, current_view_num);
    }
    public void title_select_onclick(View view)
    {
        //통계 선택하기
        //button_event.title_select_onclick();

        Intent title_select_intent;
        title_select_intent = new Intent(this, MainStatisticalList.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목)
        title_select_intent.putExtra("version", 1);
        title_select_intent.putExtra("current_view_num", current_view_num);

        startActivity(title_select_intent);
    }
    public void subtitle_select_onclick(View view)
    {
        //항목 선택하기
        //button_event.subtitle_select_onclick();

        Intent subtitle_select_intent;
        subtitle_select_intent = new Intent(this, MainStatisticalList.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목)
        subtitle_select_intent.putExtra("version", 2);
        subtitle_select_intent.putExtra("title_num", main_graph_group[current_view_num].main_title);
        subtitle_select_intent.putExtra("current_view_num", current_view_num);

        startActivity(subtitle_select_intent);
    }
    public void change_next_onclick(View view)
    {
        //현재 선택한 지역 번호 증가
        current_view_num = button_event.change_next_onclick(current_view_num_bool, main_graph_group, map_draw, current_view_num, MAX_VIEW_NUM);
        //버튼 텍스트 변경
        change_next_button.setText(String.valueOf(current_view_num + 1));
        //버튼 색깔 변경
        switch(current_view_num)
        {
            case 0:
                //빨강색
                change_next_button.setBackgroundColor(Color.parseColor("#FFA7A7"));
                break;
            case 1:
                //노랑색
                change_next_button.setBackgroundColor(Color.parseColor("#FAED7D"));
                break;
            case 2:
                //초록색
                change_next_button.setBackgroundColor(Color.parseColor("#B7F0B1"));
                break;
            case 3:
                //파랑색
                change_next_button.setBackgroundColor(Color.parseColor("#6799FF"));
                break;
            default:
                //회색
                change_next_button.setBackgroundColor(Color.parseColor("#BDBDBD"));
                break;
        }
    }
    public void statistic_color_onclick(View view)
    {
        //메인 화면 통계 색 초기화
        button_event.statistic_color_onclick(this, current_view_num_bool, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
    }

    //다른 화면에서 메인 메뉴로 다시 돌아올 때
    @Override
    public void onResume()
    {
        super.onResume();

        //리스트 확인 변수가 true일 때
        if(list_bool == true)
        {
            //지도 다시 만들기
            button_event.graph_remove_onclick(current_view_num_bool, map_draw, main_map_layout, current_view_num);
            button_event.graph_add_onclick(current_view_num_bool, this, map_draw, main_map_layout, current_view_num, main_graph_group[current_view_num].main_title, main_graph_group[current_view_num].main_subtitle, main_graph_group[current_view_num].main_local_num);
            list_bool = false;
        }
    }
}