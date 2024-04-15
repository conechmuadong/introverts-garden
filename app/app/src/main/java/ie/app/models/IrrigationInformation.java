package ie.app.models;

import android.annotation.SuppressLint;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ie.app.api.FirebaseAPI;
import ie.app.fragments.BaseFragment;

public class IrrigationInformation extends BaseFragment {
    private String startTime, endTime, duration;
    private String _startDate, _startTime, _endDate, _endTime;
    boolean checked;
    private Date dateTime;
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public boolean autoIrrigation;

    @Override
    public String toString() {
        return "IrrigationInformation: " + startTime + " - " + endTime
                + ", auto is " + autoIrrigation + "\n";
    }

    public String getStartDate() throws ParseException {
        dateTime = dateFormat.parse(startTime);
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatDate.format(dateTime);
        _startDate = date;
        return date;
    }

    public String getStartTime() throws ParseException {
        dateTime = dateFormat.parse(startTime);
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormatTime.format(dateTime); // định dạng đối tượng Date thành chuỗi định dạng giờ phút giây
        _startTime = time;
        return time;
    }

    public String getEndDate() throws ParseException {
        dateTime = dateFormat.parse(endTime);
        SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormatDate.format(dateTime);
        return date;
    }

    public String getEndTime() throws ParseException {
        dateTime = dateFormat.parse(endTime);
        SimpleDateFormat dateFormatTime = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormatTime.format(dateTime); // định dạng đối tượng Date thành chuỗi định dạng giờ phút giây
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isAutoIrrigation() {
        return autoIrrigation;
    }

    public void setStartTime(String startTime) throws ParseException {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setAutoIrrigation(boolean autoIrrigation, String field) {
        this.autoIrrigation = autoIrrigation;
        FirebaseAPI.changeAutoIrrigation("users", field, autoIrrigation);
    }

    public void setNewStartDate(String newStartDate, String field) {
        this._startDate = newStartDate;
        this.startTime = newStartDate + " " + _startTime;
        FirebaseAPI.changeIrrigationTime("users", field, startTime);
    }

    public void setNewStartTime(String newStartTime, String field) {
        this._startTime = newStartTime;
        this.startTime = _startDate + " " + newStartTime;
        FirebaseAPI.changeIrrigationTime("users", field, startTime);
    }

    public void setNewEndDate(String newEndDate, String field) {
        this._endDate = newEndDate;
        this.endTime = newEndDate + " " + _endTime;
        FirebaseAPI.changeEndTime("users", field, endTime);
    }

    public void setNewEndTime(String newEndTime, String field) {
        this._endTime = newEndTime;
        this.endTime = _endDate + " " + newEndTime;
        FirebaseAPI.changeEndTime("users", field, endTime);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

/* TODO
- Lấy lượng nước tưới được nhập
- Tính toán lại thời gian kết thúc tưới
- Cài đặt chế độ Auto không cho phép chỉnh sửa
- Cập nhật chế độ manual - auto lên database
*/