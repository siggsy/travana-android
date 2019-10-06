package com.VegaSolutions.lpptransit.ui.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.VegaSolutions.lpptransit.lppapideprecated.Api;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.LiveBusArrivalV2;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationById;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsInRange;
import com.VegaSolutions.lpptransit.lppapideprecated.responseclasses.StationsOnRoute;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

@Deprecated
public class LppSharedViewModel extends ViewModel {

    private MutableLiveData<List<StationsInRange>> stationsInRange = new MutableLiveData<>();
    private MutableLiveData<List<StationsOnRoute>> stationsOnRoute = new MutableLiveData<>();
    private MutableLiveData<StationById> stationById = new MutableLiveData<>();
    private MutableLiveData<ArrivalWrapper> arrivalById = new MutableLiveData<>();

    public LiveData<List<StationsInRange>> getStationsInRangeData() {
        return stationsInRange;
    }
    public LiveData<List<StationsOnRoute>> getStationsOnRouteData() {
        return stationsOnRoute;
    }
    public LiveData<StationById> getStationsByIdData() {
        return stationById;
    }
    public LiveData<ArrivalWrapper> getArrivalsById() {
        return arrivalById;
    }

    public void stationsInRange(int radius, LatLng location) {
        Api.stationsInRange(radius, location.latitude, location.longitude, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    stationsInRange.postValue(apiResponse.getData());
                }
            }
        });
    }

    public void stationsOnRoute(int id) {

        Api.getStationsOnRoute(id, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    stationsOnRoute.postValue(apiResponse.getData());
                }
            }
        });

    }

    public void stationsOnRoute(String id) {

        Api.getStationsOnRoute(id, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    stationsOnRoute.postValue(apiResponse.getData());
                }
            }
        });

    }

    public void stationById(int id) {
        Api.getStationById(id, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    stationById.postValue(apiResponse.getData());
                }
            }
        });
    }

    public void liveBusArrival(int id) {
        Api.liveBusArrivalV2(id, (apiResponse, statusCode, success) -> {
            if (success) {
                if (apiResponse.isSuccess()) {
                    arrivalById.postValue(new ArrivalWrapper(id, apiResponse.getData()));
                }
            }
        });
    }

    public static class ArrivalWrapper {
        public int id;
        public LiveBusArrivalV2 arrivalV2;

        private ArrivalWrapper(int id, LiveBusArrivalV2 arrivalV2) {
            this.id = id;
            this.arrivalV2 = arrivalV2;
        }

    }

    //public void liveBusArrival

    public static class LppViewModelFactory implements ViewModelProvider.Factory {

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LppSharedViewModel();
        }
    }

}
