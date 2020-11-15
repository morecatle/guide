package com.kakao.guide;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
onCreateViewHolder을 통해서 생성되면, onBindViewHolder에서 해당 holder의 View에 데이터를 노출을 정의하면 된다.
RecyclerView는 ViewHolder을 재사용할 수 있도록 설계되어 있으므로, ViewType이 한번 생성된 이후로는 onBindViewHolder만 호출되게 된다.
* */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    static ArrayList<String> itemList = null;
    private Context context;
    private View.OnClickListener onClickItem;
    public static String line = "";
    String key = "";
    String value = "";
    String mapL = "";
    String mapR = "";

    private OnItemClickListener mListener = null ;
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public MainAdapter(Context context, ArrayList<String> itemList, View.OnClickListener onClickItem) {
        this.context = context;
        this.itemList = itemList;
        this.onClickItem = onClickItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // context 와 parent.getContext() 는 같다.
//        View view = LayoutInflater.from(context)
//                .inflate(R.layout.schedule_main, parent, false);
        View view = inflater.inflate(R.layout.schedule_main, parent, false) ;
        MainAdapter.ViewHolder vh = new MainAdapter.ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //String item = itemList.get(position);
        // 문자 구분.
        // 일정 이름 지정.
//        String[] split = itemList.get(position).split("=");
//        key = split[0];
//        value = split[1];
        // line을 리스트로 바꾸기.
        ArrayList < String > lineLIst = new ArrayList<>();
        String[] lineSplit = line.split(",");
        for(int y=0; y<lineSplit.length; y++) {
            lineLIst.add(lineSplit[y]);
            Log.d("ccc", "우선순위를 문자열 ==> 리스트에 저장."+lineLIst);
        }

        // 수신된 루트 리스트를 우선순위대로 재배열해서 저장하기.
        ArrayList<String> newLIst = new ArrayList<String>();
        for(int x=0; x<itemList.size(); x++) {
            for(int y=0; y<itemList.size(); y++) {
                // 루트리스트 중에 우선순위와 일차하냐?
                if(itemList.get(y).contains(lineLIst.get(x))) {
                    newLIst.add(itemList.get(y));
                }
            }
        }
        itemList = newLIst;
        Log.d("this", itemList.toString());

        // 일정 이름 지정.
        String[] split = itemList.get(position).split("=");
        key = split[0];
        value = split[1];
        String[] split2 = value.split(",");
        mapL = split2[0];
        mapR = split2[1];

        final Geocoder geocoder = new Geocoder(context);
        List<Address> list = null;
        try {
            //미리 구해놓은 위도값 mLatitude;
            //미리 구해놓은 경도값 mLongitude;

            list = geocoder.getFromLocation(
                    Float.parseFloat(mapL), // 위도
                    Float.parseFloat(mapR), // 경도
                    10); // 얻어올 값의 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류");
        }
        if (list != null) {
            if (list.size() == 0) {
                holder.text_main_scheduleAdd.setText("해당되는 주소 정보는 없습니다");
            } else {
                holder.text_main_scheduleAdd.setText(list.get(0).getAddressLine(0));
            }
        }
        holder.text_main_scheduleName.setText(key);
        holder.text_main_scheduleGPS.setText("10m");

        //holder.textview.setTag(item);
        //holder.textview.setOnClickListener(onClickItem);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_main_scheduleAdd;
        public TextView text_main_scheduleName;
        public TextView text_main_scheduleGPS;

        public ViewHolder(View itemView) {
            super(itemView);
            text_main_scheduleAdd = itemView.findViewById(R.id.text_main_scheduleAdd);
            text_main_scheduleName = itemView.findViewById(R.id.text_main_scheduleName);
            text_main_scheduleGPS = itemView.findViewById(R.id.text_main_scheduleGPS);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION){
                        if(mListener !=null){
                            mListener.onItemClick(v,position);
                        }
                    }
                }
            });
        }
    }
}
