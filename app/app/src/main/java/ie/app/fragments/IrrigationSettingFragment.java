package ie.app.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ie.app.R;
import ie.app.api.FirebaseAPI;
import ie.app.databinding.FragmentIrrigationSettingBinding;
import ie.app.models.CustomizedParameter;
import ie.app.models.TreeData;

enum Mode {
    MANUAL,
    AUTO
}
public class IrrigationSettingFragment extends BaseFragment {

    private FragmentIrrigationSettingBinding binding;
    private Mode mode = Mode.MANUAL;
    private String selectedStartDate, selectedStartTime, selectedAmount;
    private boolean isComputed = false;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        Log.v("Irrigation Setting", field.irrigationInformation.toString());
        binding = FragmentIrrigationSettingBinding.inflate(inflater, container, false);
        update();
        mode = Mode.MANUAL;
        Log.v("Irrigation Setting", mode.toString());

        if(mode == Mode.AUTO) {
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.disable));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.amountEditText.setEnabled(false);
            binding.dateEditText.setEnabled(false);
            binding.timeEditText.setEnabled(false);
        } else {
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.disable));
            binding.amountEditText.setEnabled(true);
            binding.dateEditText.setEnabled(true);
            binding.timeEditText.setEnabled(true);
        }

        try {
            selectedStartDate = field.irrigationInformation.getStartDate();
            selectedStartTime = field.irrigationInformation.getStartTime();
            binding.dateEditText.setText(selectedStartDate);
            binding.timeEditText.setText(selectedStartTime);
            binding.endDate.setText(binding.dateEditText.getText().toString());
            binding.endTime.setText(binding.timeEditText.getText().toString());
            binding.amountEditText.setText("00:00:00");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();

        binding.manualButton.setOnClickListener(view1 -> {
            mode = Mode.MANUAL;
            // set background
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.disable));

            binding.amountEditText.setEnabled(true);
            binding.dateEditText.setEnabled(true);
            binding.timeEditText.setEnabled(true);
        });

        binding.autoButton.setOnClickListener(view12 -> {
            mode = Mode.AUTO;
            if (!isComputed) {
                field.irrigationInformation.setDuration(field.simulation());
                isComputed = true;
            }
            // set background
            binding.manualButton.setBackgroundColor(binding.manualButton.getContext().
                    getResources().getColor(R.color.disable));
            binding.autoButton.setBackgroundColor(binding.autoButton.getContext().
                    getResources().getColor(R.color.colorPrimary));
            binding.amountEditText.setEnabled(false);
            binding.dateEditText.setEnabled(false);
            binding.timeEditText.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (LocalTime.now().getHour() > 8) {
                    binding.dateEditText.setText(LocalDate.now().plusDays(1).toString());
                } else {
                    binding.dateEditText.setText(LocalDate.now().toString());
                }
                binding.timeEditText.setText("08:00:00");
                binding.amountEditText.setText(field.irrigationInformation.getDuration());
                binding.endDate.setText(binding.dateEditText.getText().toString());

                LocalTime x = LocalTime.parse(binding.amountEditText.getText());
                LocalTime y = LocalTime.of(8, 0, 0)
                        .plusHours(x.getHour())
                                .plusMinutes(x.getMinute())
                                        .plusSeconds(x.getSecond());
                String end = y.toString() + ((y.toString().length() < 8) ? ":00" : "");

                binding.endTime.setText(end);
            }
        });

        binding.dateEditText.setOnClickListener(v -> {
            // Lấy giá trị ngày giờ từ database để hiển thị trên DatePicker
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            // Hiển thị DatePickerDialog để cho phép người dùng chọn giá trị ngày giờ
            DatePickerDialog datePickerDialog = new DatePickerDialog(IrrigationSettingFragment.this.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view13, int year, int monthOfYear, int dayOfMonth) {
                    // Cập nhật giá trị ngày giờ của EditText khi người dùng chọn giá trị trên DatePicker
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, monthOfYear);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Date selectedDate = selectedCalendar.getTime(); // Lấy ra đối tượng Date tương ứng với giá trị được chọn


                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = formatter.format(selectedDate);
                    selectedStartDate = strDate;

                    binding.dateEditText.setText(strDate);

                    binding.endDate.setText(binding.dateEditText.getText().toString());
                }
            }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        binding.timeEditText.setOnClickListener(v -> {
            // Lấy giá trị giờ hiện tại để hiển thị trên DatePicker
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int min = calendar.get(Calendar.MINUTE);

            // Hiển thị DatePickerDialog để cho phép người dùng chọn giá trị giờ phút
            TimePickerDialog timePickerDialog = new TimePickerDialog(IrrigationSettingFragment.this.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view14, int hourOfDay, int minute) {
                    // Cập nhật giá trị giờ của EditText khi người dùng chọn giá trị trên TimePicker

                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(Calendar.HOUR, hourOfDay);
                    selectedCalendar.set(Calendar.MINUTE, minute);
                    selectedCalendar.set(Calendar.SECOND, 0);
                    Date selectedDate = selectedCalendar.getTime(); // Lấy ra đối tượng Date tương ứng với giá trị được chọn


                    @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String strTime = formatter.format(selectedDate);
                    selectedStartTime = strTime;

                    binding.timeEditText.setText(strTime);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocalTime x = LocalTime.parse(binding.amountEditText.getText());
                        LocalTime y = LocalTime.parse(strTime)
                                .plusHours(x.getHour())
                                .plusMinutes(x.getMinute())
                                .plusSeconds(x.getSecond());
                        String end = y.toString() + ((y.toString().length() == 5) ? ":00" : "");
                        binding.endTime.setText(end);
                    }
                }
            }, hour, min, true);
            timePickerDialog.show();
        });

        binding.updateButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.amountEditText.getText().toString();
                field.irrigationInformation.setDuration(input);
                if (!input.equals("00:00:00")) {
                    FirebaseAPI.changeIrrigationCheck("users", field.getName(), true);
                } else {
                    FirebaseAPI.changeIrrigationCheck("users", field.getName(), false);
                }

                FirebaseAPI.deleteMeasuredData("users", field.getName());
                FirebaseAPI.changeDuration("users", field.getName(), field.irrigationInformation.getDuration());

                input = binding.dateEditText.getText().toString();
                field.irrigationInformation.setNewStartDate(input, field.getName());
                input = binding.timeEditText.getText().toString();
                field.irrigationInformation.setNewStartTime(input, field.getName());
                input = binding.endDate.getText().toString();
                field.irrigationInformation.setNewEndDate(input, field.getName());
                input = binding.endTime.getText().toString();
                field.irrigationInformation.setNewEndTime(input, field.getName());

                field.irrigationInformation.setAutoIrrigation((mode == Mode.AUTO), field.name);
                Log.v("API", "after change mode");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class GetTreeData extends AsyncTask<String, Void, TreeData> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected TreeData doInBackground(String... params) {
            try {
                Task<TreeData> task = FirebaseAPI.getTreeData1(params[0], params[1]);
                Task<ArrayList<Double>> rootLengthTask = FirebaseAPI.getRootLength(params[0], params[1]);
                Task<ArrayList<Double>> rootTipsTask = FirebaseAPI.getRootTips(params[0], params[1]);
                Task<ArrayList<Double>> soilWaterCapacityTask = FirebaseAPI.getSoilWaterCapacity(params[0], params[1]);
                Task<ArrayList<Double>> contLTask = FirebaseAPI.getContL(params[0], params[1]);
                Task<ArrayList<Double>> nuptLTask = FirebaseAPI.getNuptL(params[0], params[1]);

                field.treeData = Tasks.await(task);
                field.treeData.rootLength = Tasks.await(rootLengthTask);
                field.treeData.rootTips = Tasks.await(rootTipsTask);
                field.treeData.soilWaterCapacity = Tasks.await(soilWaterCapacityTask);
                field.treeData.contL = Tasks.await(contLTask);
                field.treeData.nuptL = Tasks.await(nuptLTask);

                Log.v("TreeData", "Got update");
                return field.treeData;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(TreeData result) {
            super.onPostExecute(result);
            field.treeData = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetTreeData1 extends AsyncTask<String, Void, ArrayList<Double>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData1(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Double> doInBackground(String... params) {
            try {
                Task<ArrayList<Double>> rootLengthTask = FirebaseAPI.getRootLength((String) params[0], (String) params[1]);

                field.treeData.rootLength = Tasks.await(rootLengthTask);

                Log.v("TreeData", "Got update");
                return field.treeData.rootLength;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            super.onPostExecute(result);
            field.treeData.rootLength = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetTreeData2 extends AsyncTask<String, Void, ArrayList<Double>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData2(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Double> doInBackground(String... params) {
            try {
                Task<ArrayList<Double>> rootTipsTask = FirebaseAPI.getRootTips((String) params[0], (String) params[1]);
                field.treeData.rootTips = Tasks.await(rootTipsTask);
                Log.v("TreeData", "Got update");
                return field.treeData.rootTips;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            super.onPostExecute(result);
            field.treeData.rootTips = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetTreeData3 extends AsyncTask<String, Void, ArrayList<Double>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData3(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Double> doInBackground(String... params) {
            try {
                Task<ArrayList<Double>> soilWaterCapacityTask = FirebaseAPI.getSoilWaterCapacity((String) params[0], (String) params[1]);
                field.treeData.soilWaterCapacity = Tasks.await(soilWaterCapacityTask);
                Log.v("TreeData", "Got update");
                return field.treeData.soilWaterCapacity;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            super.onPostExecute(result);
            field.treeData.soilWaterCapacity = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetTreeData4 extends AsyncTask<String, Void, ArrayList<Double>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData4(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Double> doInBackground(String... params) {
            try {
                Task<ArrayList<Double>> contLTask = FirebaseAPI.getContL((String) params[0], (String) params[1]);
                field.treeData.contL = Tasks.await(contLTask);
                Log.v("TreeData", "Got update");
                return field.treeData.contL;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            super.onPostExecute(result);
            field.treeData.contL = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetTreeData5 extends AsyncTask<String, Void, ArrayList<Double>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTreeData5(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected ArrayList<Double> doInBackground(String... params) {
            try {
                Task<ArrayList<Double>> nuptLTask = FirebaseAPI.getNuptL((String) params[0], (String) params[1]);
                field.treeData.nuptL = Tasks.await(nuptLTask);
                Log.v("TreeData", "Got update");
                return field.treeData.nuptL;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> result) {
            super.onPostExecute(result);
            field.treeData.nuptL = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetAllMeasureData extends AsyncTask<String, Void, List<List<Double>>> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetAllMeasureData(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected List<List<Double>> doInBackground(String... params) {
            try {
                Task<List<List<Double>>> task = FirebaseAPI.getAllMeasuredData((String) params[0], (String) params[1]);
                if (field.allMeasuredData.size() == 0) {
                    field.allMeasuredData = Tasks.await(task);
                }
                Log.v("TreeData", "Got update");
                return field.allMeasuredData;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<List<Double>> result) {
            super.onPostExecute(result);
            if (field.allMeasuredData.size() == 0) {
                field.allMeasuredData = result;
            }
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private class GetTask extends AsyncTask<String, Void, CustomizedParameter> {
        protected ProgressDialog dialog;
        protected Context context;

        public GetTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog = new ProgressDialog(context, 1);
            this.dialog.setMessage("Đang lấy dữ liệu");
            this.dialog.show();
        }

        @Override
        protected CustomizedParameter doInBackground(String... params) {
            try {
                Task<CustomizedParameter> task = FirebaseAPI.getCustomizedParameter((String) params[0], (String) params[1]);
                field.customizedParameter = Tasks.await(task);
                Log.v("CustomizedFragment", "Got data: " + field.customizedParameter.toString());
                return field.customizedParameter;
            } catch (Exception e) {
                Log.v("ASYNC", "ERROR : " + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CustomizedParameter result) {
            super.onPostExecute(result);
            // Đăng ký OnFieldSelectedListener cho adapter
            field.customizedParameter = result;
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void update() {
        GetTreeData taskTreeData = new GetTreeData(getContext());
        taskTreeData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, TreeData>() {
            @Override
            protected TreeData doInBackground(Void... voids) {
                try {
                    return taskTreeData.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(TreeData treeData) {
                field.treeData = treeData;
            }
        }.execute();

        GetTreeData1 taskRootLength = new GetTreeData1(getContext());
        taskRootLength.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, ArrayList<Double>>() {
            @Override
            protected ArrayList<Double> doInBackground(Void... voids) {
                try {
                    return taskRootLength.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Double> treeData) {
                field.treeData.rootLength = treeData;
            }
        }.execute();

        GetTreeData2 taskRootTips = new GetTreeData2(getContext());
        taskRootTips.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, ArrayList<Double>>() {
            @Override
            protected ArrayList<Double> doInBackground(Void... voids) {
                try {
                    return taskRootTips.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Double> treeData) {
                field.treeData.rootTips = treeData;
            }
        }.execute();

        GetTreeData3 taskSoilWaterCapacity = new GetTreeData3(getContext());
        taskSoilWaterCapacity.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, ArrayList<Double>>() {
            @Override
            protected ArrayList<Double> doInBackground(Void... voids) {
                try {
                    return taskSoilWaterCapacity.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Double> treeData) {
                field.treeData.soilWaterCapacity = treeData;
            }
        }.execute();

        GetTreeData4 taskContL = new GetTreeData4(getContext());
        taskContL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, ArrayList<Double>>() {
            @Override
            protected ArrayList<Double> doInBackground(Void... voids) {
                try {
                    return taskContL.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Double> treeData) {
                field.treeData.contL = treeData;
            }
        }.execute();

        GetTreeData5 taskNuptL = new GetTreeData5(getContext());
        taskNuptL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, ArrayList<Double>>() {
            @Override
            protected ArrayList<Double> doInBackground(Void... voids) {
                try {
                    return taskNuptL.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Double> treeData) {
                field.treeData.nuptL = treeData;
            }
        }.execute();

        GetAllMeasureData taskAll = new GetAllMeasureData(getContext());
        taskAll.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, List<List<Double>>>() {
            @Override
            protected List<List<Double>> doInBackground(Void... voids) {
                try {
                    return taskAll.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<List<Double>> data) {
                if (field.allMeasuredData.size() == 0) {
                    field.allMeasuredData = data;
                }
            }
        }.execute();

        GetTask task = new GetTask(getContext());
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "/users", "/" + field.getName());
        new AsyncTask<Void, Void, CustomizedParameter>() {
            @Override
            protected CustomizedParameter doInBackground(Void... voids) {
                try {
                    return task.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CustomizedParameter customizedParameter) {
                Log.v("CustomizedFragment", "FieldByName " + field.getName() + " " + customizedParameter.toString());
                field.customizedParameter = customizedParameter;
            }
        }.execute();
    }
}
