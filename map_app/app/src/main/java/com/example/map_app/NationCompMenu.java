package com.example.map_app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Random;

public class NationCompMenu extends AppCompatActivity
{
    //출력할 리스트 뷰
    FrameLayout nation_comp_menu_list_layout;

    /*
    main_statistical_list_item_adapter: 리스트 어댑터
    리스트 구현 설명: https://baessi.tistory.com/52
    */
    //리스트 데이터
    NationComp_Statistical_Data nation_comp_statistical_data;
    NationComp_Statistical_Data_Structure nation_comp_statistical_data_structure;

    //제목 번호 초기화
    static int nation_comp_title_num = 0;
    //부제목 번호 초기화
    static int nation_comp_subtitle_num = 0;

    //지역 번호
    int[] local_num;

    //지역번호 상태 변수(true: 그래프 생성됨, false: 그래프 제거됨)
    boolean current_view_bool;

    //리스트 선택 확인 변수
    static boolean nation_comp_list_bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nation_comp_menu);

        //툴바 초기화
        Toolbar nation_comp_tool_bar = (Toolbar)findViewById(R.id.nation_comp_tool_bar);
        setSupportActionBar(nation_comp_tool_bar);

        //뒤로가기 버튼
        ActionBar nation_comp_action_bar = getSupportActionBar();
        nation_comp_action_bar.setDisplayHomeAsUpEnabled(true);

        //그래프 뷰
        nation_comp_menu_list_layout = (FrameLayout)findViewById(R.id.nation_comp_statistical_layout);

        //지역 번호 초기화
        local_num = new int[8];
        for(int i=0;i<local_num.length;i++)
        {
            local_num[i] = i;
        }

        //제목 초기화(랜덤으로 초기화 >> 부제목 선택 리스트에 제목 번호를 전송하기 위해 양수로 초기화(랜덤: -1))
        Random rand = new Random();
        nation_comp_title_num = rand.nextInt(100) + 1;

        //리스트 데이터 초기화
        nation_comp_statistical_data = new NationComp_Statistical_Data();
        nation_comp_statistical_data_structure = new NationComp_Statistical_Data_Structure(this, nation_comp_menu_list_layout, nation_comp_title_num, 0, local_num);

        nation_comp_statistical_data.execute(nation_comp_statistical_data_structure);

        current_view_bool = true;

        nation_comp_statistical_data = null;
        nation_comp_statistical_data_structure = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu_item)
    {
        int menu_item_id = menu_item.getItemId();

        if(menu_item_id == android.R.id.home)
        {
            //툴바의 뒤로가기 버튼을 눌렀을 때
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menu_item);
    }
    public void nation_comp_title_select_onclick(View view)
    {
        //통계 선택하기
        Intent title_select_intent;
        title_select_intent = new Intent(this, NationComp_List.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목)
        title_select_intent.putExtra("version", 1);

        startActivity(title_select_intent);
    }
    public void nation_comp_subtitle_select_onclick(View view)
    {
        //항목 선택하기
        Intent subtitle_select_intent;
        subtitle_select_intent = new Intent(this, NationComp_List.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목)
        subtitle_select_intent.putExtra("version", 2);
        subtitle_select_intent.putExtra("title_num", nation_comp_title_num);

        startActivity(subtitle_select_intent);
    }
    public void statistic_color_onclick(View view)
    {
        graph_list_recreate(this, current_view_bool, nation_comp_menu_list_layout, nation_comp_title_num, nation_comp_subtitle_num, local_num);
    }
    //다른 화면에서 메인 메뉴로 다시 돌아올 때
    @Override
    public void onResume()
    {
        super.onResume();

        //리스트 확인 변수가 true일 때
        if(nation_comp_list_bool == true)
        {
            //지도 다시 만들기
            if(current_view_bool == true)
            {
                graph_base_remove(nation_comp_menu_list_layout);
                current_view_bool = false;
            }
            if(current_view_bool == false)
            {
                graph_list_recreate(this, current_view_bool, nation_comp_menu_list_layout, nation_comp_title_num, nation_comp_subtitle_num, local_num);
                current_view_bool = true;
            }
            nation_comp_list_bool = false;
        }
    }

    //맵을 참고하지 않는 그래프 생성 함수
    public void graph_list_recreate(Context context, boolean current_view_bool, FrameLayout main_map_layout, int title, int subtitle, int[] main_local_num)
    {
        NationComp_Statistical_Data nation_comp_graph = new NationComp_Statistical_Data();
        NationComp_Statistical_Data_Structure  nation_comp_graph_data = new NationComp_Statistical_Data_Structure(context, main_map_layout, title, subtitle, main_local_num);

        if(current_view_bool == true)
        {
            nation_comp_graph_data.set_graph_status(true, true);
        }

        nation_comp_graph.execute(nation_comp_graph_data);

        nation_comp_graph = null;
        nation_comp_graph_data = null;
    }

    //그래프 제거 기초 함수
    public void graph_base_remove(FrameLayout main_map_layout)
    {
        int VIEW_BASE_ID = 222000;
        main_map_layout.removeView(main_map_layout.findViewById(VIEW_BASE_ID));
        main_map_layout.removeView(main_map_layout.findViewById(VIEW_BASE_ID + 1));
    }
}
