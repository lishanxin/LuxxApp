package net.senink.seninkapp.adapter;

        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.text.TextUtils;
        import android.util.SparseArray;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;
        import net.senink.seninkapp.R;
//import com.senink.seninkapp.core.PISXinInsole;
//import com.senink.seninkapp.core.PISXinInsole.TimeAction;
        import net.senink.seninkapp.ui.activity.AddTimerActivity;
        import net.senink.seninkapp.ui.constant.Constant;
        import net.senink.seninkapp.ui.constant.MessageModel;
        import net.senink.seninkapp.ui.util.StringUtils;
        import net.senink.seninkapp.ui.util.Utils;
        import net.senink.seninkapp.ui.view.listview.SwipeMenu;
        import net.senink.seninkapp.ui.view.listview.SwipeMenuCreator;
        import net.senink.seninkapp.ui.view.listview.SwipeMenuItem;
        import net.senink.seninkapp.ui.view.listview.SwipeMenuListView;

/**
 * 用于在智能鞋垫定时器列表界面中的组列表
 *
 * @author zhaojunfeng
 * @date 2016-02-01
 */
//public class InsoleTimerListAdapter extends BaseAdapter {
//
//    private LayoutInflater inflater;
//    private SparseArray<TimeAction> list = null;
//    private Activity mContext;
//    private PISXinInsole mPISXinInsole;
//
//    public InsoleTimerListAdapter(Activity context, PISXinInsole infor,
//                                  SparseArray<TimeAction> infors) {
//        this.inflater = LayoutInflater.from(context);
//        this.mContext = context;
//        this.mPISXinInsole = infor;
//        setList(infors);
//    }
//
//    /**
//     * 更新信息
//     *
//     * @param infors
//     */
//    public void setList(SparseArray<TimeAction> infors) {
//        if (null == infors) {
//            this.list = new SparseArray<TimeAction>();
//        } else {
//            this.list = infors;
//        }
//    }
//
//    @Override
//    public int getCount() {
//        return list.size();
//    }
//
//    @Override
//    public TimeAction getItem(int position) {
//        return list.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @SuppressLint("InflateParams")
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        if (null == convertView) {
//            holder = new ViewHolder();
//            convertView = inflater.inflate(R.layout.insoletimerlist_item, null);
//            holder.lvOperate = (SwipeMenuListView) convertView
//                    .findViewById(R.id.insoletimer_item_listview);
//            holder.tvTime = (TextView) convertView
//                    .findViewById(R.id.insoletimer_item_time);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        TimeAction infor = list.get(position);
//        setOperateView(holder.lvOperate, infor);
//        String time = null;
//        if (infor.repeat) {
//            time = mContext.getString(R.string.everyday);
//        } else {
//            time = mContext.getString(R.string.never);
//        }
//        time = mContext.getString(R.string.timer_time, time);
//        holder.tvTime.setText(time);
//        return convertView;
//    }
//
//    private void setOperateView(final SwipeMenuListView listView,
//                                final TimeAction infor) {
//        MyAdapter adapter = new MyAdapter(infor);
//        listView.setAdapter(adapter);
//        SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//            @Override
//            public void create(SwipeMenu menu) {
//                SwipeMenuItem editItem = new SwipeMenuItem(mContext);
//                editItem.setBackground(new ColorDrawable(0xFFC7C7C7));
//                editItem.setWidth(Utils.dpToPx(55, mContext.getResources()));
//                editItem.setTitle(R.string.Edit);
//                editItem.setTitleSize(18);
//                editItem.setTitleColor(Color.WHITE);
//                menu.addMenuItem(editItem);
//                editItem = new SwipeMenuItem(mContext);
//                editItem.setBackground(new ColorDrawable(0xFFFF004B));
//                editItem.setWidth(Utils.dpToPx(55, mContext.getResources()));
//                editItem.setTitle(R.string.delete);
//                editItem.setTitleSize(18);
//                editItem.setTitleColor(Color.WHITE);
//                menu.addMenuItem(editItem);
//            }
//        };
//        listView.setMenuCreator(creator);
//        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(int position, SwipeMenu menu,
//                                           int index) {
//                if (mPISXinInsole != null) {
//                    if (infor != null) {
//                        if (index == 0) {
//                            Intent intent = new Intent(mContext,
//                                    AddTimerActivity.class);
//                            intent.putExtra(MessageModel.ACTIVITY_VALUE,
//                                    mPISXinInsole.getPISKeyString());
//                            intent.putExtra("taskid", infor.taskId);
//                            mContext.startActivityForResult(intent,
//                                    Constant.REQUEST_CODE_ADDTIMER);
//                            mContext.overridePendingTransition(
//                                    R.anim.anim_in_from_right,
//                                    R.anim.anim_out_to_left);
//                        } else if (index == 1) {
//                            mPISXinInsole.cancelTimer(infor.taskId, true);
//                        }
//                    }
//                }
//                return false;
//            }
//        });
//    }
//
//    private class ViewHolder {
//        public SwipeMenuListView lvOperate;
//        public TextView tvTime;
//    }
//
//    private class MyAdapter extends BaseAdapter {
//
//        private TimeAction infor;
//
//        public MyAdapter(TimeAction infor) {
//            this.infor = infor;
//        }
//
//        @Override
//        public int getCount() {
//            return 1;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return infor;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @SuppressLint({ "ViewHolder", "InflateParams" })
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            convertView = inflater.inflate(R.layout.timer_item_time, null);
//            TextView tv = (TextView) convertView
//                    .findViewById(R.id.timer_item_time);
//            TextView tvTip = (TextView) convertView
//                    .findViewById(R.id.timer_item_tip);
//            String time = null;
//            if (infor != null) {
//                time = StringUtils.getTime(mContext, infor.time);
//            }
//            if (TextUtils.isEmpty(time)) {
//                time = "";
//            }
//            if (infor.isRun) {
//                tvTip.setText(R.string.open);
//            } else {
//                tvTip.setText(R.string.close);
//            }
//            tv.setText(time);
//            return convertView;
//        }
//    }
//}
