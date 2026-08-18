package com.navable.app;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.navable.app.adapter.RouteStepAdapter;
import com.navable.app.model.RouteResponse;
import com.navable.app.model.RouteStep;
import java.util.List;
import java.util.Locale;

public class RouteActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private List<RouteStep> steps;

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

        Button btnSpeak = findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(v -> speakRoute());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void speakRoute() {
        if (tts == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Route found. ");
        for (int i = 0; i < steps.size(); i++) {
            RouteStep step = steps.get(i);
            if (i == 0) {
                sb.append("Start at ").append(step.getLocationName()).append(". ");
            } else {
                sb.append("Walk ").append(String.format("%.0f", step.getDistanceFromPrevious()))
                  .append(" meters to ").append(step.getLocationName()).append(". ");
            }
        }
        sb.append("You have arrived at your destination.");
        tts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
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
