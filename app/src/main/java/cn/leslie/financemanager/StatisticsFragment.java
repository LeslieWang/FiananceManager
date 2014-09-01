package cn.leslie.financemanager;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.ChartData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.leslie.financemanager.data.Category;
import cn.leslie.financemanager.data.DataManager;
import cn.leslie.financemanager.data.Record;
import cn.leslie.financemanager.Utility.StatisticsData;
import cn.leslie.financemanager.Utility.StatisticsResult;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {
    private static final String ARG_NAME_RES_ID = "name_res_id";
    private static final String ARG_CATE_ID= "cate_id";
    private List<Record> mRecords;
    private GestureDetector mDetector;
    private long mStart = DataManager.INVALID_TIMESTAMP;
    private long mEnd = DataManager.INVALID_TIMESTAMP;
    private long mCateId;
    private int mNameResId;
    private int mOffset = 0;

    /**
     * Returns a new instance of this fragment for the given section name resource id.
     */
    public static StatisticsFragment newInstance(int nameResId, long cateId) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NAME_RES_ID, nameResId);
        args.putLong(ARG_CATE_ID, cateId);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isSubCategoryStatistics() {
        return mCateId != DataManager.INVALID_CATE_ID;
    }

    public StatisticsFragment() {
        mDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityY) > 100) {
                    if (velocityY > 0) {
                        // go to previous
                        if (mOffset < 0) {
                            mOffset++;
                            updateViews(getView());
                        }
                    } else {
                        // go to next
                        mOffset--;
                        updateViews(getView());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCateId = getArguments().getLong(ARG_CATE_ID);
        mNameResId = getArguments().getInt(ARG_NAME_RES_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateViews(getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics_tab, container, false);
        rootView.findViewById(R.id.btn_view_records).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewRecordActivity.show(getActivity(), mStart, mEnd, mCateId, false);
            }
        });

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mDetector.onTouchEvent(event);
            }
        });
        return rootView;
    }

    private StatisticsResult getStatisticsResult() {
        if (isSubCategoryStatistics()) {
            return Utility.analyzeRecords(mRecords, new Utility.RecordAnalyzeHandler() {
                @Override
                public long getId(Record record) {
                    return record.getSubCategory();
                }

                @Override
                public String getName(Record record) {
                    return Utility.getSubCategoryName(record);
                }
            });
        } else {
            return Utility.analyzeRecords(mRecords, new Utility.RecordAnalyzeHandler() {
                @Override
                public long getId(Record record) {
                    return record.getCategory();
                }

                @Override
                public String getName(Record record) {
                    return Utility.getCategoryName(record);
                }
            });
        }
    }

    private void updateViews(View rootView) {
        loadData();
        StatisticsResult result = getStatisticsResult();
        List<StatisticsData> datas = result.mDatas;

        updateTitle(rootView);
        updatePieChart(rootView, datas);
        updateSummary(rootView, result);
        updateCateList(rootView, datas);
    }

    private void updateTitle(View rootView) {
        TextView title = (TextView) rootView.findViewById(R.id.text_title);
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(mStart);
        String res = isSubCategoryStatistics()
                ? DataManager.getInstance().getCategoryById(mCateId).getName() + " " : "";
        switch (mNameResId) {
            case R.string.by_week:
                res += date.get(Calendar.YEAR) + "年 第" + date.get(Calendar.WEEK_OF_YEAR) + "周";
                break;
            case R.string.by_month:
                res += date.get(Calendar.YEAR) + "年" + (date.get(Calendar.MONTH) + 1) + "月";
                break;
            case R.string.by_year:
                res += date.get(Calendar.YEAR) + "年";
                break;
            case R.string.statistics_all:
                break;
            default:
                // TODO:
        }
        title.setText(res);
    }

    private void updatePieChart(View rootView, List<StatisticsData> datas) {
        ChartData data = Utility.toChartData(datas);
        PieChart pieChart = (PieChart) rootView.findViewById(R.id.pie_chart);
        if (data.isValid()) {
            pieChart.setDrawHoleEnabled(false);
            pieChart.setDrawXValues(true);
            pieChart.setDrawYValues(true);
            pieChart.setDrawLegend(false);
            pieChart.setDescription("");
            pieChart.setData(data);
            pieChart.setValuePaintColor(Color.GRAY);
            pieChart.setCenterText(getString(R.string.statistics_in_total,
                    (int) pieChart.getYValueSum()));
            pieChart.prepare();
        }
        pieChart.setVisibility(data.isValid() ? View.VISIBLE : View.INVISIBLE);
        pieChart.invalidate();
    }

    private void updateSummary(View rootView, StatisticsResult result) {
        int count = 0;
        for (StatisticsData data : result.mDatas) {
            count += data.mRecordCount;
        }
        TextView summary = (TextView) rootView.findViewById(R.id.text_summary);
        String res = "收入: " + (int) result.mIncome + " 支出: " + (int) result.mOutcome
                + "\n" + Utility.toPersonStr(getActivity(), Record.PERSON_MALE) + " 收入: "
                + (int) result.mMaleIncome + " 支出: " + (int) result.mMaleOutcome
                + "\n" + Utility.toPersonStr(getActivity(), Record.PERSON_FEMALE) + " 收入: "
                + (int) result.mFemaleIncome + " 支出: " + (int) result.mFemaleOutcome
                + "\n" + Utility.toPersonStr(getActivity(), Record.PERSON_ALL) + " 收入: "
                + (int) result.mAllIncome + " 支出: " + (int) result.mAllOutcome
                + "\n总共 " + count + " 条记录";
        summary.setText(res);
    }

    private void updateCateList(View rootView, List<StatisticsData> datas) {
        ListView cateList = (ListView) rootView.findViewById(R.id.list_cate);
        cateList.setAdapter(new CategoryAdapter(datas, getActivity()));
        if (!isSubCategoryStatistics()) {
            cateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SubCategoryStatisticsActivity.show(getActivity(), mNameResId, id);
                }
            });
        }
        cateList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ViewRecordActivity.show(getActivity(), mStart, mEnd, id, isSubCategoryStatistics());
                return true;
            }
        });
    }

    private void loadData() {
        switch (mNameResId) {
            case R.string.by_week:
                mStart = TimeUtility.getStartTimeOfWeek(mOffset);
                mEnd = TimeUtility.getStartTimeOfWeek(mOffset + 1);
                break;
            case R.string.by_month:
                mStart = TimeUtility.getStartTimeOfMonth(mOffset);
                mEnd = TimeUtility.getStartTimeOfMonth(mOffset + 1);
                break;
            case R.string.by_year:
                mStart = TimeUtility.getStartTimeOfYear(mOffset);
                mEnd = TimeUtility.getStartTimeOfYear(mOffset + 1);
                break;
            case R.string.statistics_all:
                mStart = DataManager.INVALID_TIMESTAMP;
                mEnd = DataManager.INVALID_TIMESTAMP;
                break;
            default:
                // TODO:
        }
        mRecords = DataManager.getInstance().getRecords(mStart, mEnd, mCateId, false);
    }

    private static class CategoryAdapter extends BaseAdapter {
        private List<StatisticsData> mData;
        private float mAmount;
        private Context mContext;

        public void updateData(List<StatisticsData> data) {
            if (data == null) {
                mData = new ArrayList<StatisticsData>();
            } else {
                mData = data;
            }
            mAmount = 0;
            for (StatisticsData d : mData) {
                if (d.mId != Category.FIXED_OUTCOME_INCOME) {
                    mAmount += d.mAmount;
                }
            }
            notifyDataSetChanged();
        }

        CategoryAdapter(List<StatisticsData> data, Context context) {
            mContext = context;
            updateData(data);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ((StatisticsData) getItem(position)).mId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView != null) {
                view = convertView;
            } else {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.statistics_item_cate, parent, false);
            }

            StatisticsData data = mData.get(position);
            ((TextView) view.findViewById(R.id.text_name)).setText(data.mName);
            ((TextView) view.findViewById(R.id.text_amount)).setText(
                    String.format("%.2f", data.mAmount));

            if (data.mId != Category.FIXED_OUTCOME_INCOME) {
                float percent = (data.mAmount * 100) / mAmount;
                ((TextView) view.findViewById(R.id.text_percent)).setText(
                        String.format("%.2f%%", percent));
            } else {
                ((TextView) view.findViewById(R.id.text_percent)).setText("");
            }

            ((TextView) view.findViewById(R.id.text_record_count)).setText(
                    data.mRecordCount + "条记录");
            return view;
        }
    }
}
