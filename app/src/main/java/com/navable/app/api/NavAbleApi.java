package com.navable.app.api;

import com.navable.app.model.Building;
import com.navable.app.model.Floor;
import com.navable.app.model.Location;
import com.navable.app.model.RouteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NavAbleApi {

    @GET("api/buildings")
    Call<List<Building>> getBuildings();

    @GET("api/floors/building/{buildingId}")
    Call<List<Floor>> getFloors(@Path("buildingId") Long buildingId);

    @GET("api/locations/floor/{floorId}")
    Call<List<Location>> getLocations(@Path("floorId") Long floorId);

    @GET("api/route")
    Call<RouteResponse> getRoute(@Query("from") Long fromId, @Query("to") Long toId, @Query("accessibleOnly") boolean accessibleOnly);
}
