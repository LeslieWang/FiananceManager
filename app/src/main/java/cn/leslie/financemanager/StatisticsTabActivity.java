package cn.leslie.financemanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class StatisticsTabActivity extends Activity implements ActionBar.TabListener {
    private static final int[] TabNames = new int[] {R.string.current_week,
            R.string.current_month, R.string.current_year, R.string.statistics_all};

    public static void show(Activity activity) {
        activity.startActivity(new Intent(activity, StatisticsTabActivity.class));
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_tab);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        actionBar.setDisplayHomeAsUpEnabled(true);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return StatisticsFragment.newInstance(TabNames[position]);
        }

        @Override
        public int getCount() {
            return TabNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(TabNames[position]);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StatisticsFragment extends Fragment {
        private static final String ARG_NAME_RES_ID = "name_res_id";
        private List<Record> mRecords;
        private GestureDetector mDetector;
        private long mStart = DataManager.INVALID_TIMESTAMP;
        private long mEnd = DataManager.INVALID_TIMESTAMP;
        private long mCateId = DataManager.INVALID_CATE_ID;
        private int mOffset = 0;

        /**
         * Returns a new instance of this fragment for the given section name resource id.
         */
        public static StatisticsFragment newInstance(int nameResId) {
            StatisticsFragment fragment = new StatisticsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_NAME_RES_ID, nameResId);
            fragment.setArguments(args);
            return fragment;
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
        public void onResume() {
            super.onResume();
            updateViews(getView());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_statistics_tab, container, false);
            rootView.findViewById(R.id.btn_view_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewRecordActivity.show(getActivity(), mStart, mEnd, mCateId);
                }
            });
            updateViews(rootView);

            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mDetector.onTouchEvent(event);
                }
            });
            return rootView;
        }

        private void updateViews(View rootView) {
            loadData();
            Utility.StatisticsResult result = Utility.analyzeRecordsByCategory(mRecords);
            List<Utility.StatisticsData> datas = result.mDatas;

            updateTitle(rootView);
            updatePieChart(rootView, datas);
            updateSummary(rootView, result);
            updateCateList(rootView, datas);
        }

        private void updateTitle(View rootView) {
            TextView title = (TextView) rootView.findViewById(R.id.text_title);
            int id = getArguments().getInt(ARG_NAME_RES_ID);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(mStart);
            String res = "";
            switch (id) {
                case R.string.current_week:
                    res = date.get(Calendar.YEAR) + "年 第" + date.get(Calendar.WEEK_OF_YEAR) + "周";
                    break;
                case R.string.current_month:
                    res = date.get(Calendar.YEAR) + "年" + (date.get(Calendar.MONTH) + 1) + "月";
                    break;
                case R.string.current_year:
                    res = date.get(Calendar.YEAR) + "年";
                    break;
                case R.string.statistics_all:
                    break;
                default:
                    // TODO:
            }
            title.setText(res);
        }

        private void updatePieChart(View rootView, List<Utility.StatisticsData> datas) {
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

        private void updateSummary(View rootView, Utility.StatisticsResult result) {
            int count = 0;
            for (Utility.StatisticsData data : result.mDatas) {
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

        private void updateCateList(View rootView, List<Utility.StatisticsData> datas) {
            ListView cateList = (ListView) rootView.findViewById(R.id.list_cate);
            cateList.setAdapter(new CategoryAdapter(datas, getActivity()));
        }

        private void loadData() {
            int id = getArguments().getInt(ARG_NAME_RES_ID);
            switch (id) {
                case R.string.current_week:
                    mStart = TimeUtility.getStartTimeOfWeek(mOffset);
                    mEnd = TimeUtility.getStartTimeOfWeek(mOffset + 1);
                    mCateId = DataManager.INVALID_CATE_ID;
                    break;
                case R.string.current_month:
                    mStart = TimeUtility.getStartTimeOfMonth(mOffset);
                    mEnd = TimeUtility.getStartTimeOfMonth(mOffset + 1);
                    mCateId = DataManager.INVALID_CATE_ID;
                    break;
                case R.string.current_year:
                    mStart = TimeUtility.getStartTimeOfYear(mOffset);
                    mEnd = TimeUtility.getStartTimeOfYear(mOffset + 1);
                    mCateId = DataManager.INVALID_CATE_ID;
                    break;
                case R.string.statistics_all:
                    mStart = DataManager.INVALID_TIMESTAMP;
                    mEnd = DataManager.INVALID_TIMESTAMP;
                    mCateId = DataManager.INVALID_CATE_ID;
                    break;
                default:
                    // TODO:
            }
            mRecords = DataManager.getInstance().getRecords(mStart, mEnd, mCateId);
        }
    }

    private static class CategoryAdapter extends BaseAdapter {
        private List<Utility.StatisticsData> mData;
        private float mAmount;
        private Context mContext;

        public void updateData(List<Utility.StatisticsData> data) {
            if (data == null) {
                mData = new ArrayList<Utility.StatisticsData>();
            } else {
                mData = data;
            }
            mAmount = 0;
            for (Utility.StatisticsData d : mData) {
                if (d.mId != Category.FIXED_OUTCOME_INCOME) {
                    mAmount += d.mAmount;
                }
            }
            notifyDataSetChanged();
        }

        CategoryAdapter(List<Utility.StatisticsData> data, Context context) {
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
            return ((Utility.StatisticsData) getItem(position)).mId;
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

            Utility.StatisticsData data = mData.get(position);
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
