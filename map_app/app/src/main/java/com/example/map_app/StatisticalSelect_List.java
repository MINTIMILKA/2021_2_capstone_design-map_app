package com.example.map_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

public class StatisticalSelect_List extends AppCompatActivity
{
    int version;

    int title;

    //출력할 리스트 뷰
    ListView statistical_select_menu_list_layout;

    /*
    main_statistical_list_item_adapter: 리스트 어댑터
    리스트 구현 설명: https://baessi.tistory.com/52
    */
    StatisticalSelect_List_Adapter statistical_select_list_item_adapter;

    //메뉴 리스트 항목
    String[] menu_list;
    //메뉴 리스트 항목 갯수
    int menu_list_num;

    //list_data 변수
    StatisticalSelect_List_Data statistical_select_list_data;
    StatisticalSelect_List_Data_Structure statistical_select_list_data_structure;

    @Override
    protected void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.main_statistical_list);

        //인텐트 데이터 가져오기
        Intent main_statistical_list_intent = getIntent();

        //버전 번호 초기화
        version = main_statistical_list_intent.getIntExtra("version", 0);

        //제목 리스트일 경우 제목 가져오기
        if(version == 2)
        {
            title = main_statistical_list_intent.getIntExtra("title_num", 0);
        }

        //툴바 초기화
        Toolbar main_statistical_list_tool_bar = (Toolbar)findViewById(R.id.main_statistical_list_tool_bar);
        setSupportActionBar(main_statistical_list_tool_bar);

        //뒤로가기 버튼
        ActionBar main_statistical_list_action_bar = getSupportActionBar();
        main_statistical_list_action_bar.setDisplayHomeAsUpEnabled(true);

        //출력할 리스트 뷰 초기화
        statistical_select_menu_list_layout = (ListView)findViewById(R.id.main_statistical_list);
        //리스트 어댑터 초기화
        statistical_select_list_item_adapter = new StatisticalSelect_List_Adapter();
        statistical_select_list_item_adapter.set_version(version);

        //리스트 데이터 초기화
        statistical_select_list_data = new StatisticalSelect_List_Data();
        statistical_select_list_data_structure = new StatisticalSelect_List_Data_Structure(version, statistical_select_menu_list_layout, statistical_select_list_item_adapter);

        if(version == 2)
        {
            statistical_select_list_data_structure.set_title_num(title);
        }

        statistical_select_list_data.execute(statistical_select_list_data_structure);
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
}
