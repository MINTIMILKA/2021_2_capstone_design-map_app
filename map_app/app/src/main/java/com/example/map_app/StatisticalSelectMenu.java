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

import java.util.Random;

public class StatisticalSelectMenu extends AppCompatActivity
{
    //그래프 뷰
    FrameLayout statistical_select_menu_layout;

    /*
    main_statistical_list_item_adapter: 리스트 어댑터
    리스트 구현 설명: https://baessi.tistory.com/52
    */

    StatisticalSelect_Data statistical_select_data;
    StatisticalSelect_Data_Structure statistical_select_data_structure;

    //제목 번호 초기화
    static int[] statistical_select_title_num;
    //부제목 번호 초기화
    static int[] statistical_select_subtitle_num;

    //지역 번호
    static int[] statistical_select_local_num;

    //마지막 그래프 번호(0 ~ 11);
    static int last_view_num;

    //리스트 선택 확인 변수
    static boolean statistical_select_list_bool = false;

    //리스트 지역번호 선택 확인 변수
    static boolean statistical_select_list_local_bool = false;

    @Override
    protected  void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistical_select_menu);

        //툴바 초기화
        Toolbar statistical_select_tool_bar = (Toolbar)findViewById(R.id.statistical_select_tool_bar);
        setSupportActionBar(statistical_select_tool_bar);

        //뒤로가기 버튼
        ActionBar statistical_select_action_bar = getSupportActionBar();
        statistical_select_action_bar.setDisplayHomeAsUpEnabled(true);

        //그래프 뷰
        statistical_select_menu_layout = (FrameLayout)findViewById(R.id.statistical_select_menu_layout);

        Random rand = new Random();

        //지역 번호 초기화
        statistical_select_local_num = new int[2];
        for(int i=0;i<statistical_select_local_num.length;i++)
        {
            statistical_select_local_num[i] = rand.nextInt(8);
            for(int j=0;j<i;j++)
            {
                if(statistical_select_local_num[i] == statistical_select_local_num[j])
                {
                    i--;
                    break;
                }
            }
        }

        //제목, 부제목 초기화(랜덤으로 초기화 >> 부제목 선택 리스트에 제목 번호를 전송하기 위해 양수로 초기화(랜덤: -1))
        statistical_select_title_num = new int[12];
        statistical_select_subtitle_num = new int[12];

        for(int i=0;i<statistical_select_title_num.length;i++)
        {
            statistical_select_title_num[i] = rand.nextInt(100) + 1;
            statistical_select_subtitle_num[i] = 0;
        }

        last_view_num = 5;

        //그래프 출력 데이터 초기화
        statistical_select_data = new StatisticalSelect_Data();
        statistical_select_data_structure = new StatisticalSelect_Data_Structure(1, this, statistical_select_menu_layout, last_view_num, statistical_select_title_num, statistical_select_subtitle_num, statistical_select_local_num);

        statistical_select_data.execute(statistical_select_data_structure);
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
    public void local_1_onclick(View view)
    {
        //통계 선택하기
        Intent local_select_intent_1;
        local_select_intent_1 = new Intent(this, StatisticalSelect_List.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목, 3: 왼쪽 지역 번호, 4: 오른쪽 지역 번호)
        local_select_intent_1.putExtra("version", 3);

        startActivity(local_select_intent_1);
    }
    public void local_2_onclick(View view)
    {
        //통계 선택하기
        Intent local_select_intent_2;
        local_select_intent_2 = new Intent(this, StatisticalSelect_List.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목, 3: 왼쪽 지역 번호, 4: 오른쪽 지역 번호)
        local_select_intent_2.putExtra("version", 4);

        startActivity(local_select_intent_2);
    }
    public void title_select_onclick(View view)
    {
        //통계 선택하기
        Intent title_select_intent;
        title_select_intent = new Intent(this, StatisticalSelect_List.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목)
        title_select_intent.putExtra("version", 1);

        startActivity(title_select_intent);
    }
    public void subtitle_select_onclick(View view)
    {
        //항목 선택하기
        Intent subtitle_select_intent;
        subtitle_select_intent = new Intent(this, StatisticalSelect_List.class);

        //version: 리스트에 출력할 내용을 처리하기 위한 버전 번호 (1: 통계 제목, 2: 통계 부제목)
        subtitle_select_intent.putExtra("version", 2);
        subtitle_select_intent.putExtra("title_num", statistical_select_title_num[last_view_num]);

        startActivity(subtitle_select_intent);
    }
    public void graph_add_onclick(View view)
    {
        if((last_view_num >= -1) && (last_view_num < 11))
        {
            last_view_num++;
            statistical_select_graph_create(2, this, last_view_num, statistical_select_menu_layout, statistical_select_title_num, statistical_select_subtitle_num, statistical_select_local_num);
        }
        else if(last_view_num >= 11)
        {
            for(int i=0;i<last_view_num+1; i++)
            {
                statistical_select_graph_base_remove(i, statistical_select_menu_layout);
            }

            for(int i=1;i<statistical_select_title_num.length;i++)
            {
                statistical_select_title_num[i-1] = statistical_select_title_num[i];
                statistical_select_subtitle_num[i-1] = statistical_select_subtitle_num[i];
            }
            Random rand = new Random();
            statistical_select_title_num[statistical_select_title_num.length-1] = rand.nextInt(5) + 1;
            statistical_select_subtitle_num[statistical_select_subtitle_num.length-1] = 0;

            statistical_select_graph_create(1, this, last_view_num, statistical_select_menu_layout, statistical_select_title_num, statistical_select_subtitle_num, statistical_select_local_num);
        }
    }
    public void graph_remove_onclick(View view)
    {
        if(last_view_num >= 0)
        {
            statistical_select_graph_base_remove(last_view_num, statistical_select_menu_layout);
            last_view_num--;
        }
    }
    public void graph_clear_onclick(View view)
    {
        if(last_view_num >= 0)
        {
            for(int i=0;i<last_view_num+1; i++)
            {
                statistical_select_graph_base_remove(i, statistical_select_menu_layout);
            }
            last_view_num = -1;
        }
    }
    public void statistic_color_onclick(View view)
    {
        if(last_view_num >= 0)
        {
            statistical_select_graph_base_remove(last_view_num, statistical_select_menu_layout);
            statistical_select_graph_create(2, this, last_view_num, statistical_select_menu_layout, statistical_select_title_num, statistical_select_subtitle_num, statistical_select_local_num);
        }

    }
    //다른 화면에서 메인 메뉴로 다시 돌아올 때
    @Override
    public void onResume()
    {
        super.onResume();

        //리스트 확인 변수가 true일 때
        if(statistical_select_list_bool == true)
        {
            //지도 다시 만들기
            if(last_view_num >= 0)
            {
                statistical_select_graph_base_remove(last_view_num, statistical_select_menu_layout);
                statistical_select_graph_create(2, this, last_view_num, statistical_select_menu_layout, statistical_select_title_num, statistical_select_subtitle_num, statistical_select_local_num);
            }
            statistical_select_list_bool = false;
        }
        else if(statistical_select_list_local_bool == true)
        {
            if(last_view_num >= 0)
            {
                for(int i=0;i<last_view_num+1; i++)
                {
                    statistical_select_graph_base_remove(i, statistical_select_menu_layout);
                }
                statistical_select_graph_create(1, this, last_view_num, statistical_select_menu_layout, statistical_select_title_num, statistical_select_subtitle_num, statistical_select_local_num);
            }
            //
            statistical_select_list_local_bool = false;
        }
    }

    //맵을 참고하지 않는 그래프 생성 함수
    public void statistical_select_graph_create(int version, Context context, int last_view_num, FrameLayout statistical_select_menu_layout, int[] title_num, int[] subtitle_num, int[] local_num)
    {
        StatisticalSelect_Data statistical_select_graph_data = new StatisticalSelect_Data();
        StatisticalSelect_Data_Structure  statistical_select_graph_data_structure = new StatisticalSelect_Data_Structure(version, context, statistical_select_menu_layout, last_view_num, title_num, subtitle_num, local_num);

        //statistical_select_graph_data_structure.set_graph_status(last_view_num, true, true);

        statistical_select_graph_data.execute(statistical_select_graph_data_structure);

        statistical_select_graph_data = null;
        statistical_select_graph_data_structure = null;
    }

    //그래프 제거 기초 함수
    public void statistical_select_graph_base_remove(int last_view_num, FrameLayout statistical_select_menu_layout)
    {
        int VIEW_BASE_ID = 333000;
        statistical_select_menu_layout.removeView(statistical_select_menu_layout.findViewById(VIEW_BASE_ID + (last_view_num*2)));
        statistical_select_menu_layout.removeView(statistical_select_menu_layout.findViewById(VIEW_BASE_ID + (last_view_num*2) +1));
    }
}
