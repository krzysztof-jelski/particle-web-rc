package pl.krzysztofjelski.particlewebrc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static pl.krzysztofjelski.particlewebrc.BuildConfig.ACCESS_TOKEN;
import static pl.krzysztofjelski.particlewebrc.BuildConfig.DEVICE_ID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                EventBus.getDefault().post(new SpeedChangeEvent(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public static final MediaType FORM
            = MediaType.parse("application/x-www-form-urlencoded");

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void setSpeed(SpeedChangeEvent speedChangeEvent) throws IOException {
        int seek = speedChangeEvent.speed;
        int speed = (int) (-255.0 + (seek / 100.0) * 510.0);
        if (Math.abs(speed) < 110)
            speed = 0;
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(FORM, "arg=" + speed +
                "&access_token=" + ACCESS_TOKEN);
        Request request = new Request.Builder()
                .url("https://api.particle.io/v1/devices/" + DEVICE_ID + "/speed")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
    }
}
