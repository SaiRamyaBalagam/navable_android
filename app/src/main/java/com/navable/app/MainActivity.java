package com.navable.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.navable.app.api.RetrofitClient;
import com.navable.app.model.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerBuilding, spinnerFloor, spinnerFrom, spinnerTo;
    private CheckBox checkAccessible;

    private List<Building> buildings = new ArrayList<>();
    private List<Floor> floors = new ArrayList<>();
    private List<Location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerFloor = findViewById(R.id.spinnerFloor);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        checkAccessible = findViewById(R.id.checkAccessible);

        loadBuildings();

        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                loadFloors(buildings.get(position).getId());
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                loadLocations(floors.get(position).getId());
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        findViewById(R.id.btnFindRoute).setOnClickListener(v -> findRoute());
    }

    private void loadBuildings() {
        RetrofitClient.getInstance().getApi().getBuildings().enqueue(new Callback<List<Building>>() {
            public void onResponse(Call<List<Building>> call, Response<List<Building>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    buildings = response.body();
                    ArrayAdapter<Building> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, buildings);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBuilding.setAdapter(adapter);
                }
            }
            public void onFailure(Call<List<Building>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load buildings: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadFloors(Long buildingId) {
        RetrofitClient.getInstance().getApi().getFloors(buildingId).enqueue(new Callback<List<Floor>>() {
            public void onResponse(Call<List<Floor>> call, Response<List<Floor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    floors = response.body();
                    ArrayAdapter<Floor> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, floors);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFloor.setAdapter(adapter);
                }
            }
            public void onFailure(Call<List<Floor>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load floors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLocations(Long floorId) {
        RetrofitClient.getInstance().getApi().getLocations(floorId).enqueue(new Callback<List<Location>>() {
            public void onResponse(Call<List<Location>> call, Response<List<Location>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    locations = response.body();
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, locations);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFrom.setAdapter(adapter);
                    spinnerTo.setAdapter(adapter);
                }
            }
            public void onFailure(Call<List<Location>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load locations", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void findRoute() {
        if (locations.size() < 2) {
            Toast.makeText(this, "Please select at least 2 locations", Toast.LENGTH_SHORT).show();
            return;
        }
        Location from = locations.get(spinnerFrom.getSelectedItemPosition());
        Location to = locations.get(spinnerTo.getSelectedItemPosition());
        if (from.getId().equals(to.getId())) {
            Toast.makeText(this, "From and To cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean accessibleOnly = checkAccessible.isChecked();
        RetrofitClient.getInstance().getApi().getRoute(from.getId(), to.getId(), accessibleOnly)
                .enqueue(new Callback<RouteResponse>() {
                    public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                            intent.putExtra("route", response.body());
                            startActivity(intent);
                        }
                    }
                    public void onFailure(Call<RouteResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Failed to find route: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
