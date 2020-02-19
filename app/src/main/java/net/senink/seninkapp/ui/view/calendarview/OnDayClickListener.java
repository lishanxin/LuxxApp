package net.senink.seninkapp.ui.view.calendarview;

import android.view.View;
import android.widget.AdapterView;

/**
 * 日历自定义组件的监听器
 * @author zhaojunfeng
 * @date 2016-01-05
 */

public interface OnDayClickListener {
	public void onDayClicked(AdapterView<?> adapter, View view, int position,
			long id, Day day);
}
