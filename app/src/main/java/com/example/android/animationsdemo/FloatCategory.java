package com.example.android.animationsdemo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by derekchang on 2015/9/9.
 */
public class FloatCategory implements DialogInterface.OnDismissListener{
    public static final String TAG = "FloatCategory";
    private Context context = null;
    private View mContentView = null;
    private GridView mCategoryGridView = null;
    private Dialog mDialog = null;
    private View mNaviView;
    private DisplayMetrics metrics;
    private OnCloseListener mOnCloseListener = null;
    private static volatile FloatCategory instance;
    private boolean isInited = false;
    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is dismissed.
     */
    interface OnCloseListener {
        void onClose();
    }

    public static FloatCategory getInstance() {
        if(instance == null) {
            synchronized(FloatCategory.class) {
                if(instance == null) {
                    instance = new FloatCategory();
                }
            }
        }

        return instance;
    }

    public FloatCategory(){

    }

    public synchronized void init(Context context, View naviBarView){
        if(context == null || naviBarView == null) {
            throw new IllegalArgumentException("Context and View can not be initialized with null");
        }else {
            this.context = context;
            this.mNaviView = naviBarView;
            metrics = context.getResources().getDisplayMetrics();

            //+ TODO init contentView
            mContentView = LayoutInflater.from(context).inflate(R.layout.activity_category_popview, null);
            mCategoryGridView = (GridView) mContentView.findViewById(R.id.category_gridview);
            for (int i = 0; i < 26; i++) {
                int id = CATEGORY_ICON_ARRAY[(int) (Math.random() * CATEGORY_ICON_ARRAY.length)];
                mCategoryIcon.add(id);
            }
            CategoryAdapter mAdapter = new CategoryAdapter();
            mCategoryGridView.setAdapter(mAdapter);
            //- TODO init contentView


            // set as inited
            isInited = true;
        }
    }

    /**
     * listen to the dismiss event
     * @param listener
     */
    public void setOnCloseListener(OnCloseListener listener){
        mOnCloseListener = listener;
    }

    public boolean isShow(){
        if(mDialog == null) return false;
        return mDialog.isShowing();
    }

    /**
     * set the view wanna display
     * @param contentView
     */
    public void setContentView(View contentView){
        mContentView = contentView;
    }

    public void show(){
        if(!isInited){
            throw new IllegalArgumentException("Init() must be call first!");
        }else {
            if (mDialog == null) {
                mDialog = new Dialog(context, R.style.dialog);
                //mDialog = new PopupWindow(context);
                mDialog.setContentView(mContentView);
                // set layout params
                WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();

                lp.windowAnimations = R.style.CategoryDialogAnimation;
                lp.width = metrics.widthPixels;
                lp.height = metrics.heightPixels - mNaviView.getHeight();
                lp.x = 0;
                lp.y = mNaviView.getHeight();
                //lp.gravity = Gravity.LEFT | Gravity.BOTTOM;

                mDialog.getWindow().setAttributes(lp);
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.setOnDismissListener(this);
            }
            mDialog.show();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        DKLog.d(TAG, Trace.getCurrentMethod());
        if(mOnCloseListener != null){
            mOnCloseListener.onClose();
        }
    }


    List<Integer> mCategoryIcon = new ArrayList<>();
    int[] CATEGORY_ICON_ARRAY = {
            R.drawable.search_category_baby_btn,
            R.drawable.search_category_furniture_btn,
            R.drawable.search_category_game_btn,
            R.drawable.search_category_handcrafts_btn,
            R.drawable.search_category_health_and_fitness_btn,
    };
    class CategoryAdapter extends BaseAdapter{
        private static final String TAG = "CategoryAdapter";
        public CategoryAdapter(){

        }

        @Override
        public int getCount() {
            return mCategoryIcon.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategoryIcon.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_category_popview_item, parent, false);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.category_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.category_name);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.icon.setImageResource(mCategoryIcon.get(position));
            return convertView;
        }

        class ViewHolder {
            TextView name;
            ImageView icon;
        }
    }

    public void destroy(){
        if(mDialog != null){
            mDialog.dismiss();
            mDialog = null;
        }

        if(instance != null){
            instance = null;
        }

    }


}
