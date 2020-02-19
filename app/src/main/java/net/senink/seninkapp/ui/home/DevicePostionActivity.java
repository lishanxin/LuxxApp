package net.senink.seninkapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import net.senink.seninkapp.BaseActivity;
import net.senink.seninkapp.R;

public class DevicePostionActivity extends BaseActivity implements
		OnClickListener {
	private View backbt;
	private TextView livingRoom, bedRoom, masterBedroom, guestRoom001,
			guestRoom002, diningHall, toyRoom, washingRoom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_select_position);
		initView();

	}

	private void initView() {
		backbt = findViewById(R.id.back);
		livingRoom = (TextView) findViewById(R.id.living_room);
		bedRoom = (TextView) findViewById(R.id.bed_room);
		masterBedroom = (TextView) findViewById(R.id.master_bedroom);
		guestRoom001 = (TextView) findViewById(R.id.guest_room_001);
		guestRoom002 = (TextView) findViewById(R.id.guest_room_002);
		diningHall = (TextView) findViewById(R.id.dinning_hall);
		toyRoom = (TextView) findViewById(R.id.toy_room);
		washingRoom = (TextView) findViewById(R.id.washing_room);
		washingRoom.setOnClickListener(this);
		toyRoom.setOnClickListener(this);
		diningHall.setOnClickListener(this);
		guestRoom002.setOnClickListener(this);
		guestRoom001.setOnClickListener(this);
		masterBedroom.setOnClickListener(this);
		backbt.setOnClickListener(this);
		livingRoom.setOnClickListener(this);
		bedRoom.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {

		Intent data = new Intent();
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.living_room:
			data.putExtra("device_position", livingRoom.getText());
			setResult(20, data);
			break;
		case R.id.bed_room:
			data.putExtra("device_position", bedRoom.getText());
			setResult(20, data);
			break;
		case R.id.master_bedroom:
			data.putExtra("device_position", masterBedroom.getText());
			setResult(20, data);
			break;
		case R.id.guest_room_001:
			data.putExtra("device_position", guestRoom001.getText());
			setResult(20, data);
			break;
		case R.id.guest_room_002:
			data.putExtra("device_position", guestRoom002.getText());
			setResult(20, data);
			break;
		case R.id.dinning_hall:
			data.putExtra("device_position", diningHall.getText());
			setResult(20, data);
			break;
		case R.id.toy_room:
			data.putExtra("device_position", toyRoom.getText());
			setResult(20, data);
			break;
		case R.id.washing_room:
			data.putExtra("device_position", washingRoom.getText());
			setResult(20, data);
			break;
		default:
			break;
		}
		finish();

	}

}