package com.example.sonjunhyeok.myhome.Light;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sonjunhyeok.myhome.MQTT_Main;
import com.example.sonjunhyeok.myhome.MQTT_Sub;
import com.example.sonjunhyeok.myhome.Network;
import com.example.sonjunhyeok.myhome.R;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LightReserveAdapter extends RecyclerView.Adapter<LightReserveAdapter.ItemViewHolder> {

    private ArrayList<LightReserveListitem> listData = new ArrayList<>();
    private Context context;
    // Item의 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    // 직전에 클릭됐던 Item의 position
    private int prePosition = -1;


    public LightReserveAdapter(List<LightReserveListitem> data) {
        this.listData = (ArrayList<LightReserveListitem>) data;
    }
    public LightReserveAdapter(){

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lightreserve_listitem, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.onBind(listData.get(position), position);
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(LightReserveListitem data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public void removeItem(int position){
        listData.remove(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private TextView textView_name, textView_room;
        private ImageView imageView_toggle;
        private ImageButton imageButton_state;
        private Button button_cancel, button_fix, button_delete;
        private ToggleButton toggle_sun, toggle_mon, toggle_tus, toggle_wen,
                toggle_thu, toggle_fri, toggle_set;
        private Switch switch_repeat;
        private RelativeLayout relativeLayout;
        private LinearLayout linearLayout,toggle_linearLayout;
        private TimePicker timePicker;
        private LightReserveListitem data;

        private String resultStr_power="ON", resultStr_powerchange = "ON",resultStr_repeat="False", resultStr_repeatchange="False" ,resultStr_day="", resultStr_name;
        private String[] resultStrDays = {"","","","","","",""}, resultStrArrayDays={"","","","","","",""};
        private int resultInt_hour=0, resultInt_min=0;

        private String roomName_eng, roomName_kor;
        private int position;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_name = itemView.findViewById(R.id.light_reserveList_header_title);
            textView_room = itemView.findViewById(R.id.light_reserveList_room_textview);
            imageButton_state = itemView.findViewById(R.id.light_reserveList_state_Imagebutton);
            imageView_toggle = itemView.findViewById(R.id.light_reserveList_btn_expand_toggle);
            toggle_mon = itemView.findViewById(R.id.light_reserveList_toggle_mon);
            toggle_tus = itemView.findViewById(R.id.light_reserveList_toggle_tus);
            toggle_wen = itemView.findViewById(R.id.light_reserveList_toggle_wen);
            toggle_thu = itemView.findViewById(R.id.light_reserveList_toggle_thu);
            toggle_fri = itemView.findViewById(R.id.light_reserveList_toggle_fri);
            toggle_set = itemView.findViewById(R.id.light_reserveList_toggle_set);
            toggle_sun = itemView.findViewById(R.id.light_reserveList_toggle_sun);
            relativeLayout = itemView.findViewById(R.id.light_reserveList_RelativeLayout);
            timePicker = itemView.findViewById(R.id.light_reserveList_timepicker);
            button_cancel = itemView.findViewById(R.id.light_reserveList_cancel_button);
            button_delete = itemView.findViewById(R.id.light_reserveList_delete_button);
            button_fix = itemView.findViewById(R.id.light_reserveList_fix_button);
            linearLayout = itemView.findViewById(R.id.Light_ReserveList_linearLayout);
            toggle_linearLayout = itemView.findViewById(R.id.light_reserveList_dayLayout);
            switch_repeat = itemView.findViewById(R.id.light_reserveList_repeatswitch);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Light_ReserveList_linearLayout:
                    if (selectedItems.get(position)) {
                        // 펼쳐진 Item을 클릭 시
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    // 해당 포지션의 변화를 알림
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    // 클릭된 position 저장
                    prePosition = position;
                    break;
                case R.id.light_reserveList_fix_button:
                    if(onCheckOverlap()){
                            if(switch_repeat.isChecked()) {
                                for (String resultStrArrayDay : resultStrArrayDays) {
                                    resultStr_day += resultStrArrayDay;
                                }
                                if(resultStr_day.equals("")){
                                    Toast.makeText(context, "반복할 요일을 선택하세요.", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                System.out.println("Days data : " + resultStr_day);
                            }
                            else{
                                resultStr_day = "No data";
                            }
                            String mintmp ="";
                            if(timePicker.getMinute() < 10){
                                mintmp = "0"+timePicker.getMinute();
                            }
                            else{
                                mintmp = String.valueOf(timePicker.getMinute());
                            }
                            String resultStr_time = timePicker.getHour()+":"+mintmp;
                            Network network = new Network(); //String time, String day, String name, String room, String roomkor, String action, String repeat String num
                            try {
                                String netwrok_result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"Light_ReserveUpdate", resultStr_time, resultStr_day,
                                        resultStr_name, roomName_eng, roomName_kor, resultStr_powerchange, resultStr_repeatchange, Integer.toString(data.getPrimary_key())).get();
                                System.out.println("reverse fix : " + resultStr_time+", "+resultStr_day+", "+resultStr_powerchange+", "+resultStr_repeatchange);
                                if("Success".equals(netwrok_result)){
                                    onSetData(resultStr_powerchange, resultStr_repeatchange, resultStrArrayDays, timePicker.getHour(), timePicker.getMinute());
                                    Toast.makeText(context,"변경되었습니다.",Toast.LENGTH_SHORT).show();
                                    if(resultStr_day.contains(DayOfWeek()) || resultStr_repeatchange.equals("False")){
                                        MQTT_Sub mqtt = new MQTT_Sub();
                                        mqtt.on_publish("reserve");
                                    }
                                }
                                else{
                                    Toast.makeText(context,"에러가 발생되었습니다. 잠시 후 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                                }
                            } catch (ExecutionException | InterruptedException | MqttException e) {
                                e.printStackTrace();
                            }
                        resultStr_day = "";

                        }
                    else{
                        Toast.makeText(context,"내용을 변경 후 재시도 하세요.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case  R.id.light_reserveList_delete_button:
                    Network network = new Network();
                    try {
                        String result = network.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "Light_ReserveDelete", Integer.toString(data.getPrimary_key())).get();
                        if(result.equals("Success")) {
                            Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            removeItem(position);
                            notifyDataSetChanged();
                            MQTT_Sub mqtt = new MQTT_Sub();
                            mqtt.on_publish("reserve");
                        }
                        else{
                            Toast.makeText(context, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.light_reserveList_cancel_button:
                    selectedItems.delete(position);
                    if (prePosition != -1) notifyItemChanged(prePosition);
                    notifyItemChanged(position);
                    prePosition = position;
                    break;
                case R.id.light_reserveList_state_Imagebutton:
                    if(resultStr_powerchange.equals("OFF")){
                        Log.i("ReserveAdapter","image button true");
                        imageButton_state.setImageResource(R.drawable.light_on);
                        resultStr_powerchange = "ON";
                    }
                    else{
                        Log.i("ReserveAdapter","image button false");
                        imageButton_state.setImageResource(R.drawable.light);
                        resultStr_powerchange = "OFF";
                    }
                    break;
            }
        }

        public void onBind(LightReserveListitem data, int position) {
            this.data = data;
            this.position = position;

            resultStr_name = data.getName();
            resultStr_repeat = data.getRepeat();
            resultStr_power = data.getAction();
            resultInt_hour = data.getHour();
            resultInt_min = data.getMin();
            roomName_eng = data.getRoom();
            roomName_kor = data.getRoomkor();

            textView_room.setText(data.getRoomkor());
            textView_name.setText(data.getName());
            timePicker.setHour(data.getHour());
            timePicker.setMinute(data.getMin());
            if(data.getAction().equals("OFF")){
                imageButton_state.setImageResource(R.drawable.light);
            }
            else if(data.getAction().equals("ON")){
                imageButton_state.setImageResource(R.drawable.light_on);
            }
            if(data.getRepeat().equals("True")){
                switch_repeat.setChecked(true);
                toggle_linearLayout.setVisibility(View.VISIBLE);
            }
            else{
                switch_repeat.setChecked(false);
                toggle_linearLayout.setVisibility(View.INVISIBLE);
            }
            if(data.getDays()[0].equals("월")){
                toggle_mon.setChecked(true);
                resultStrDays[0] = data.getDays()[0];
            }
            else{
                toggle_mon.setChecked(false);
                resultStrDays[0] = "";
            }
            if(data.getDays()[1].equals("화")){
                toggle_tus.setChecked(true);
                resultStrDays[1] =  data.getDays()[1];
            }
            else{
                toggle_tus.setChecked(false);
                resultStrDays[1] = "";
            }
            if(data.getDays()[2].equals("수")){
                toggle_wen.setChecked(true);
                resultStrDays[2] =  data.getDays()[2];
            }
            else{
                toggle_wen.setChecked(false);
                resultStrDays[2] = "";
            }
            if(data.getDays()[3].equals("목")){
                toggle_thu.setChecked(true);
                resultStrDays[3] =  data.getDays()[3];
            }
            else{
                toggle_thu.setChecked(false);
                resultStrDays[3] = "";
            }
            if(data.getDays()[4].equals("금")){
                toggle_fri.setChecked(true);
                resultStrDays[4] =  data.getDays()[4];
            }
            else{
                toggle_fri.setChecked(false);
                resultStrDays[4] = "";
            }
            if(data.getDays()[5].equals("토")){
                toggle_set.setChecked(true);
                resultStrDays[5] =  data.getDays()[5];
            }
            else{
                toggle_set.setChecked(false);
                resultStrDays[5] = "";
            }
            if(data.getDays()[6].equals("일")){
                toggle_sun.setChecked(true);
                resultStrDays[6] =  data.getDays()[6];
            }
            else{
                toggle_sun.setChecked(false);
                resultStrDays[6] = "";
            }
            changeVisibility(selectedItems.get(position));

            linearLayout.setOnClickListener(this);
            button_fix.setOnClickListener(this);
            button_delete.setOnClickListener(this);
            button_cancel.setOnClickListener(this);
            imageButton_state.setOnClickListener(this);
            switch_repeat.setOnCheckedChangeListener(this);

            toggle_mon.setOnCheckedChangeListener(this);
            toggle_tus.setOnCheckedChangeListener(this);
            toggle_wen.setOnCheckedChangeListener(this);
            toggle_thu.setOnCheckedChangeListener(this);
            toggle_fri.setOnCheckedChangeListener(this);
            toggle_set.setOnCheckedChangeListener(this);
            toggle_sun.setOnCheckedChangeListener(this);
        }
        private String DayOfWeek(){
            Calendar calendar = Calendar.getInstance();
            int code = calendar.get(Calendar.DAY_OF_WEEK);
            switch (code){
                case 1:
                    return "월";
                case 2:
                    return "화";
                case 3:
                    return "수";
                case 4:
                    return "목";
                case 5:
                    return "금";
                case 6:
                    return "토";
                case 7:
                    return "일";
                default:
                    return "";
            }
        }
        private void changeVisibility(final boolean isExpanded) {
            int dpValue = 530;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    // value는 height 값
                    int value = (int) animation.getAnimatedValue();

                    relativeLayout.getLayoutParams().height = value;
                    relativeLayout.requestLayout();

                    relativeLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    imageView_toggle.setImageResource(isExpanded ? R.drawable.circle_minus : R.drawable.circle_plus);
                }
            });
            // Animation start
            va.start();
        }

        private void onSetData(String result_power, String result_repeat, String[] resultDays, int result_hour, int result_min){
            this.resultStr_power = result_power;
            this.resultStr_repeat = result_repeat;
            this.resultInt_hour = result_hour;
            this.resultInt_min = result_min;
            for(int i=0;i<this.resultStrDays.length;i++){
                this.resultStrDays[i] = resultDays[i];
            }
        }
        private boolean onCheckOverlap(){
            if(!resultStr_name.equals(textView_name.getText().toString())){
                System.out.println("Name overlap check");
                return true;
            }
            else if(!resultStr_power.equals(resultStr_powerchange)){
                System.out.println("Power overlap check : " + resultStr_power + " : " + resultStr_powerchange);
                return true;
            }
            else if(onCheckOverlapRepeat()){
                System.out.println("Repat overlap check True");
                return true;
            }
            else if(!(resultInt_hour == timePicker.getHour())){
                System.out.println("Hour overlap check : " + resultInt_hour + " : " + timePicker.getHour());
                return true;
            }
            else if(!(resultInt_min == timePicker.getMinute())){
                System.out.println("Min overlap check : " + resultInt_min + " : " + timePicker.getMinute());
                return true;
            }
            else{
                System.out.println("Days overlap check True");
                for(int i=0;i<7;i++){
                    if(!resultStrDays[i].equals(resultStrArrayDays[i])){
                        System.out.println("Days overlap check : " + resultStrDays[i] + " : " + resultStrArrayDays[i]);
                        return true;
                    }
                }
            }
            return false;
        }
        private boolean onCheckOverlapRepeat(){
            if(resultStr_repeat.equals("True")){
                if(!switch_repeat.isChecked()){
                    System.out.println("Repat overlap check True");
                    return true;
                }
            }
            else if(resultStr_repeat.equals("False")){
                if(switch_repeat.isChecked()){
                    System.out.println("Repat overlap check False");
                    return true;
                }
            }
            return false;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.light_reserveList_toggle_mon:
                    if(isChecked){
                        resultStrArrayDays[0] ="월,";
                    }
                    else {
                        resultStrArrayDays[0] = "";
                    }
                    break;
                case R.id.light_reserveList_toggle_tus:
                    if(isChecked){
                        resultStrArrayDays[1] ="화,";
                    }
                    else{
                        resultStrArrayDays[1] = "";
                    }
                    break;
                case R.id.light_reserveList_toggle_wen:
                    if(isChecked){
                        resultStrArrayDays[2] ="수,";
                    }
                    else{
                        resultStrArrayDays[2] = "";
                    }
                    break;
                case R.id.light_reserveList_toggle_thu:
                    if(isChecked){
                        resultStrArrayDays[3] ="목,";
                    }
                    else{
                        resultStrArrayDays[3] = "";
                    }
                    break;
                case R.id.light_reserveList_toggle_fri:
                    if(isChecked){
                        resultStrArrayDays[4] ="금,";
                    }
                    else{
                        resultStrArrayDays[4] = "";
                    }
                    break;
                case R.id.light_reserveList_toggle_set:
                    if(isChecked){
                        resultStrArrayDays[5] ="토,";
                    }
                    else{
                        resultStrArrayDays[5] = "";
                    }
                    break;
                case R.id.light_reserveList_toggle_sun:
                    if(isChecked){
                        resultStrArrayDays[6] ="일,";
                    }
                    else{
                        resultStrArrayDays[6] = "";
                    }
                    break;
                case R.id.light_reserveList_repeatswitch:
                    if(isChecked){
                        resultStr_repeatchange="True";
                        toggle_linearLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        Log.i("ReserveAdapter","repeat false");
                        toggle_linearLayout.setVisibility(View.GONE);
                        resultStr_repeatchange="False";
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
