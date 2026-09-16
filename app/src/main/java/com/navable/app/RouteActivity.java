package com.navable.app;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.navable.app.adapter.RouteStepAdapter;
import com.navable.app.model.GuidanceMode;
import com.navable.app.model.RouteResponse;
import com.navable.app.model.RouteStep;
import com.navable.app.util.NavigationGuide;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private List<RouteStep> steps;
    private GuidanceMode guidanceMode = GuidanceMode.STANDARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        RouteResponse route = (RouteResponse) getIntent().getSerializableExtra("route");

        if (route == null || !Boolean.TRUE.equals(route.getFound())) {
            Toast.makeText(this, "No route found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        steps = route.getSteps();

        TextView tvTotalDistance = findViewById(R.id.tvTotalDistance);
        tvTotalDistance.setText(String.format("Total distance: %.1f meters", route.getTotalDistance()));

        RecyclerView recyclerView = findViewById(R.id.recyclerRoute);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RouteStepAdapter(steps));

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        RadioGroup radioGroupGuidanceMode = findViewById(R.id.radioGroupGuidanceMode);
        radioGroupGuidanceMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMinimal) {
                guidanceMode = GuidanceMode.MINIMAL;
            } else if (checkedId == R.id.radioPrecision) {
                guidanceMode = GuidanceMode.PRECISION;
            } else {
                guidanceMode = GuidanceMode.STANDARD;
            }
        });

        Button btnSpeak = findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(v -> speakRoute());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void speakRoute() {
        if (tts == null) return;
        String text = NavigationGuide.buildSpokenRoute(steps, guidanceMode);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
