package com.kakao.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ScheduleListAdapter extends BaseAdapter {
    private String key = "";
    private String value = "";
    public String line = ""; //일정 순서.
    public String state = "";
    private String mapL = "";
    private String mapR = "";
    private Context context;
    private int layout;
    public ArrayList<String> scheduleData;
    private LayoutInflater inflater;

    // 파이어베이스 연결.
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    public ScheduleListAdapter(Context applicationContext, int talklist, ArrayList<String> list) {
        this.context = applicationContext;
        this.layout = talklist;
        this.scheduleData = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() { // 전체 데이터 개수
        return scheduleData.size();
    }
    @Override
    public Object getItem(int position) { // position번째 아이템
        return scheduleData.get(position);
    }
    @Override
    public long getItemId(int position) { // position번째 항목의 id인데 보통 position
        return position;
    }

    public void updateReceiptsList(ArrayList<String> newlist) {
        scheduleData.clear();
        scheduleData.addAll(newlist);
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //항목의 index, 전에 inflate 되어있는 view, listView
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("route");

        //첫항목을 그릴때만 inflate 함 다음거부터는 매개변수로 넘겨줌 (느리기때문) : recycle이라고 함
        final ViewHolder holder;
        if(convertView == null){                                                    //어떤 레이아웃을 만들어 줄 것인지, 속할 컨테이너, 자식뷰가 될 것인지
            convertView = inflater.inflate(layout, parent, false);      //아이디를 가지고 view를 만든다
            holder = new ViewHolder();
            holder.text_schedulelist_name = (TextView)convertView.findViewById(R.id.text_schedulelist_name);            // TextView
            holder.text_schedulelist_state = (TextView)convertView.findViewById(R.id.text_schedulelist_state);
            holder.layout_schedulelist_item = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_item);   // LinearLayout
            holder.layout_schedulelist_new = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_new);
            holder.layout_schedulelist_web = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_web);
            holder.layout_schedulelist_mapName = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_mapName);
            holder.layout_schedulelist_update = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_update);
            holder.layout_schedulelist_change = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_change);
            holder.layout_schedulelist_upAndDown = (LinearLayout) convertView.findViewById(R.id.layout_schedulelist_upAndDown);
            holder.web_schedulelist = (MapView) convertView.findViewById(R.id.fragment_main_mv);                        // WebView
            holder.image_schedulelist_state = (ImageView) convertView.findViewById(R.id.image_schedulelist_state);      // ImageView
            holder.image_schedulelist_new = (ImageView) convertView.findViewById(R.id.image_schedulelist_new);
            holder.image_schedulelist_up = (ImageView) convertView.findViewById(R.id.image_schedulelist_up);
            holder.image_schedulelist_down = (ImageView) convertView.findViewById(R.id.image_schedulelist_down);
            holder.image_schedulelist_update = (ImageView) convertView.findViewById(R.id.image_schedulelist_update);
            holder.image_schedulelist_delete = (ImageView) convertView.findViewById(R.id.image_schedulelist_delete);
            holder.image_schedulelist_add = (ImageView) convertView.findViewById(R.id.image_schedulelist_add);
            holder.edt_schedulelist_new = (EditText) convertView.findViewById(R.id.edt_schedulelist_new);               // EditText
            convertView.setTag(holder);
            // 초기 화면 세팅.
            holder.layout_schedulelist_item.setVisibility(View.VISIBLE);
            holder.layout_schedulelist_web.setVisibility(View.GONE);
            holder.layout_schedulelist_new.setVisibility(View.GONE);

            // line을 리스트로 바꾸기.
            ArrayList < String > lineLIst = new ArrayList<>();
            String[] lineSplit = line.split(",");
            for(int y=0; y<lineSplit.length; y++) {
                lineLIst.add(lineSplit[y]);
                Log.d("ccc", "우선순위를 문자열 ==> 리스트에 저장."+lineLIst);
            }

            // 수신된 루트 리스트를 우선순위대로 재배열해서 저장하기.
            ArrayList<String> newLIst = new ArrayList<String>();
            for(int x=0; x<scheduleData.size(); x++) {
                for(int y=0; y<scheduleData.size(); y++) {
                    // 루트리스트 중에 우선순위와 일차하냐?
                    if(scheduleData.get(y).contains(lineLIst.get(x))) {
                        newLIst.add(scheduleData.get(y));
                    }
                }
            }
            scheduleData = newLIst;

            // 일정 이름 지정.
            String[] split = scheduleData.get(position).split("=");
            key = split[0];
            value = split[1];
            holder.text_schedulelist_name.setText(key);

            // 지도 정의.
            Bundle bundle = null;
            holder.web_schedulelist.onCreate(bundle);
            holder.web_schedulelist.onResume();

            holder.web_schedulelist.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    // 리스트에서 key와 value를 구분.
                    String[] split = scheduleData.get(position).split("=");
                    key = split[0];
                    value = split[1];
                    String[] split2 = value.split(",");
                    mapL = split2[0];
                    mapR = split2[1];

                    // 구글 지도 설정.
                    final GoogleMap map = googleMap;
                    MapsInitializer.initialize(context);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    map.setMyLocationEnabled(false);
                    LatLng focus;

                    // 일정추가 관련 조건문. 넘어가도 무방.
                    if(key.equals("빈 일정")) {
                        focus = new LatLng(27.903035, 168.407800);
                    } else {
                        focus = new LatLng(Float.parseFloat(mapL), Float.parseFloat(mapR));
                    }
                    String markerTitle = "내 위치";
                    String snippetTitle = "";

                    // 서브타이틀은 .snippet(snippetTitle)추가만 해주면 된다.
                    if (markerTitle != null && snippetTitle != null) {
                        map.addMarker(new MarkerOptions()
                                .title(markerTitle)
                                .position(focus));
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(focus, 15));
                }
            });
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        // 일정 자세히 보기.
        holder.image_schedulelist_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.layout_schedulelist_web.getVisibility()==View.GONE) {
                    holder.image_schedulelist_state.setImageResource(R.drawable.list_on);
                    holder.layout_schedulelist_web.setVisibility(View.VISIBLE);
                    //일단 보류.
                    //holder.layout_schedulelist_upAndDown.setVisibility(View.VISIBLE);
                } else {
                    holder.image_schedulelist_state.setImageResource(R.drawable.list_off);
                    holder.layout_schedulelist_web.setVisibility(View.GONE);
                    //일단 보류.
                    //holder.layout_schedulelist_upAndDown.setVisibility(View.INVISIBLE);
                }
            }
        });

        // 지도 수정 모드.
        holder.layout_schedulelist_mapName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout_schedulelist_mapName.setVisibility(View.GONE);
                holder.layout_schedulelist_update.setVisibility(View.VISIBLE);
            }
        });
        // 지도 수정 되돌리기.
        holder.layout_schedulelist_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.layout_schedulelist_mapName.setVisibility(View.VISIBLE);
                holder.layout_schedulelist_update.setVisibility(View.GONE);
            }
        });

        // 지도 눌렀을 때 메인씬으로 가서 해당 지도 위치 수신.
        holder.layout_schedulelist_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //postion값으로 위치넘겨주면 된다.
                String[] split = scheduleData.get(position).split("=");
                key = split[0];
                value = split[1];
                Intent intent = new Intent(context, MainActivity.class);

                intent.putExtra("thisLocation",value);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.getApplicationContext().startActivity(intent);
            }
        });


        // 지도 추가.
//        holder.image_schedulelist_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 새로 추가된 상황이 아니라면.
//                if(!line.contains("빈 일정")) {
//                    reference.child(state).child("빈 일정").setValue("0,0");
//                    reference.child(state).child("line").setValue(line+",빈 일정");
//                    scheduleData.add("빈 일정=0,0");
//                }
//                //updateReceiptsList(scheduleData);
////                ScheduleListActivity.scheduleList_list.clear();
////                ScheduleListActivity.scheduleList_list = scheduleData;
////                Log.d("hhh", scheduleData.toString());
//
//            }
//        });


        return convertView;
    }

    //뷰홀더패턴
    public class ViewHolder{
        // 기존 리스트 출력.
        TextView text_schedulelist_name;
        TextView text_schedulelist_state;

        // 새로운 아이템 출력.
        LinearLayout layout_schedulelist_item, layout_schedulelist_new, layout_schedulelist_web,
                layout_schedulelist_mapName, layout_schedulelist_update, layout_schedulelist_change, layout_schedulelist_upAndDown;
        MapView web_schedulelist;
        ImageView image_schedulelist_state, image_schedulelist_new, image_schedulelist_up, image_schedulelist_down,
                image_schedulelist_update, image_schedulelist_delete, image_schedulelist_add;
        EditText edt_schedulelist_new;
    }
}