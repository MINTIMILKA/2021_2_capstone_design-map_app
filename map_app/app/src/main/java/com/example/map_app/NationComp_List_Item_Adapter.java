package com.example.map_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NationComp_List_Item_Adapter extends BaseAdapter
{
    private ArrayList<Statistical_List_Item> statistical_list_item_list = new ArrayList<Statistical_List_Item>();

    //버전 번호 (1: 제목 리스트, 2: 부제목 리스트)
    int version;

    LinearLayout statistical_list_item_layout;
    TextView item_text_view;

    @Override
    public int getCount() {
        return statistical_list_item_list.size();
    }

    @Override
    public Object getItem(int i) {
        return statistical_list_item_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Context context = viewGroup.getContext();

        if(view == null)
        {
            LayoutInflater statistical_list_item_Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = statistical_list_item_Inflater.inflate(R.layout.statistical_list_item, viewGroup, false);
        }

        Statistical_List_Item statistical_list_item = statistical_list_item_list.get(i);

        item_text_view = (TextView) view.findViewById(R.id.statistical_list_item_text);
        item_text_view.setText(statistical_list_item.get_item());

        final int final_i = i;


        //리스트 클릭 리스너
        statistical_list_item_layout = (LinearLayout)view.findViewById(R.id.statistical_list_item_layout);
        statistical_list_item_layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View onclick_view)
            {
                if(version == 1)
                {
                    //제목 리스트일 때
                    NationCompMenu.nation_comp_title_num = final_i + 1;
                    //부제목 초기화
                    NationCompMenu.nation_comp_subtitle_num = 0;
                }
                else if(version == 2)
                {
                    //부제목 리스트일 때
                    NationCompMenu.nation_comp_subtitle_num = final_i;
                }

                //리스트 확인 변수 설정
                NationCompMenu.nation_comp_list_bool = true;

                //메인 화면으로 나가기
                ((NationComp_List)context).finish();
            }
        });

        return view;
    }

    public void add_item(String item)
    {
        Statistical_List_Item statistical_list_item = new Statistical_List_Item();

        statistical_list_item.set_item(item);

        statistical_list_item_list.add(statistical_list_item);
    }

    public void set_version(int version)
    {
        this.version = version;
    }
}
