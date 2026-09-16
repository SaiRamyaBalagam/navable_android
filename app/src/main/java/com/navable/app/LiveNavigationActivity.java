package com.navable.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.navable.app.model.GuidanceMode;
import com.navable.app.model.RouteResponse;
import com.navable.app.model.RouteStep;
import com.navable.app.util.NavigationSimulator;
import java.util.List;
import java.util.Locale;

/**
 * Simulates walking the route and re-announces guidance as the (simulated)
 * position crosses each waypoint. Stands in for real indoor positioning,
 * which Phase 4 explicitly defers.
 */
public class LiveNavigationActivity extends AppCompatActivity implements NavigationSimulator.Listener {

    private static final double BASE_WALKING_SPEED_MPS = 1.2;
    private static final long TICK_MILLIS = 200;

    private TextToSpeech tts;
    private NavigationSimulator simulator;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean playing = false;
    private double speedMultiplier = 1.0;

    private TextView tvInstruction;
    private TextView tvDistanceRemaining;
    private ProgressBar progressRoute;
    private Button btnPlayPause;

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!playing || simulator.isArrived()) return;
            double metersThisTick = BASE_WALKING_SPEED_MPS * speedMultiplier * (TICK_MILLIS / 1000.0);
            simulator.advance(metersThisTick);
            if (!simulator.isArrived()) {
                handler.postDelayed(this, TICK_MILLIS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_navigation);

        RouteResponse route = (RouteResponse) getIntent().getSerializableExtra("route");
        GuidanceMode mode = (GuidanceMode) getIntent().getSerializableExtra("guidanceMode");
        if (mode == null) mode = GuidanceMode.STANDARD;

        if (route == null || !Boolean.TRUE.equals(route.getFound())) {
            Toast.makeText(this, "No route to navigate!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<RouteStep> steps = route.getSteps();

        tvInstruction = findViewById(R.id.tvInstruction);
        tvDistanceRemaining = findViewById(R.id.tvDistanceRemaining);
        progressRoute = findViewById(R.id.progressRoute);
        btnPlayPause = findViewById(R.id.btnPlayPause);

        TextView tvModeLabel = findViewById(R.id.tvModeLabel);
        tvModeLabel.setText("Guidance mode: " + mode.toString());

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        simulator = new NavigationSimulator(steps, mode, this);
        simulator.start();

        RadioGroup radioGroupSpeed = findViewById(R.id.radioGroupSpeed);
        radioGroupSpeed.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioSpeed2x) {
                speedMultiplier = 2.0;
            } else if (checkedId == R.id.radioSpeed4x) {
                speedMultiplier = 4.0;
            } else {
                speedMultiplier = 1.0;
            }
        });

        btnPlayPause.setOnClickListener(v -> togglePlaying());

        Button btnEndNavigation = findViewById(R.id.btnEndNavigation);
        btnEndNavigation.setOnClickListener(v -> finish());
    }

    private void togglePlaying() {
        playing = !playing;
        btnPlayPause.setText(playing ? "Pause" : "Resume Walking");
        if (playing) {
            handler.postDelayed(tickRunnable, TICK_MILLIS);
        } else {
            handler.removeCallbacks(tickRunnable);
        }
    }

    @Override
    public void onProgress(int currentStepIndex, double distanceIntoSegment, double distanceRemainingInSegment, double totalDistanceRemaining) {
        tvDistanceRemaining.setText(String.format(
                "%.1f m to next waypoint  •  %.1f m remaining total",
                distanceRemainingInSegment, totalDistanceRemaining));

        double totalDistance = simulator.getTotalDistance();
        int progress = totalDistance > 0
                ? (int) (1000 * (1 - totalDistanceRemaining / totalDistance))
                : 1000;
        progressRoute.setProgress(Math.max(0, Math.min(1000, progress)));
    }

    @Override
    public void onAnnouncement(String text) {
        tvInstruction.setText(text);
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    @Override
    public void onArrived() {
        playing = false;
        handler.removeCallbacks(tickRunnable);
        btnPlayPause.setText("Arrived");
        btnPlayPause.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(tickRunnable);
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
