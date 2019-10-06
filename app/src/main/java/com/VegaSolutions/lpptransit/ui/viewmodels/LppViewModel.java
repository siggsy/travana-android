package com.VegaSolutions.lpptransit.ui.viewmodels;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.VegaSolutions.lpptransit.lppapi.Api;
import com.VegaSolutions.lpptransit.lppapi.ApiCallback;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ApiResponse;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.ArrivalWrapper;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Bus;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Route;
import com.VegaSolutions.lpptransit.lppapi.responseobjects.Station;

import java.util.List;

public class LppViewModel extends ViewModel {

    private MutableLiveData<List<Bus>> buses = new MutableLiveData<>();
    private MutableLiveData<List<Station>> stations = new MutableLiveData<>();
    private MutableLiveData<List<Route>> routes = new MutableLiveData<>();

    public LiveData<List<Bus>> getBuses() {
        return buses;
    }
    public LiveData<List<Station>> getStations() {
        return stations;
    }
    public void getStationsInRage() {

        Api.stationDetails(true, (apiResponse, statusCode, success) -> {
            if (success)
                if (apiResponse.isSuccess())
                    stations.postValue(apiResponse.getData());
        });

    }
}
