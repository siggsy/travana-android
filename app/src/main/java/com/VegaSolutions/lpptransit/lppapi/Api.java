package com.VegaSolutions.lpptransit.lppapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Lpp Api wrapper class
 */
public class Api {

    // Map<String, Object> type used for Gson.
    private static Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
    private static Type mapTypeList = new TypeToken<Map<String, Object>>(){}.getType();



    /**
     * Returns information about single route identified by station's ID or int_id.
     * @param route_int_id int ID of route
     * @param callback callback to be triggered when finished
     */
    public static void getRouteDetails(int route_int_id, ApiCallback<Map<String, Object>> callback) {
        new LppQuery()
                .setParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapType, new CustomDeserializer())
                                        .create();
                        Map<String, Object> data = gson.fromJson(response, mapType);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_DETAILS);
    }

    /**
     * @apiNote getRouteDetails API DOC
     */
    public static class RouteDetails {
        /**
         * @apiNote ID of route's parent route. (String)
         */
        public static final String route_parent_id = "route_parent_id";
        /**
         * @apiNote Route integer identificator. (Integer)
         */
        public static final String int_id = "int_id";
        /**
         * @apiNote Integer ID of opposite route. (Integer)
         */
        public static final String opposite_route_int_id = "opposite_route_int_id";
        /**
         * @apiNote Route name. (String)
         */
        public static final String name = "name";
        /**
         * @apiNote Length of route in meters. (Double)
         */
        public static final String length = "length";
    }



    /**
     * Returns list of LPP Route Groups.
     * @param callback callback to be triggered when finished
     */
    public static void getRouteGroups(ApiCallback<Map<String, Object>> callback) {
        new LppQuery()
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapType, new CustomDeserializer())
                                        .create();
                        Map<String, Object> data = gson.fromJson(response, mapType);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_GROUPS);
    }

    /**
     * @apiNote getRouteGroups API DOC
     */
    public static class RouteGroups {
        /**
         * @apiNote Name of the route group (LPP route number on bus, ex. 6B, 1, 19I...). (String)
         */
        public static final String name = "name";
        /**
         * @apiNote Unique ID of route group. (String)
         */
        public static final String id = "id";
    }


    /**
     * Returns a list of LPP bus routes in specified route group.
     * @param route_id Route group ID. If not present, server will check for presence of route_name parameter.
     * @param callback callback to be triggered when finished
     */
    public static void getRoutes_route_id(String route_id, ApiCallback<Map<String, Object>> callback) {
        new LppQuery()
                .setParams("route_id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapType, new CustomDeserializer())
                                        .create();
                        Map<String, Object> data = gson.fromJson(response, mapType);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES);
    }

    /**
     * Returns a list of LPP bus routes in specified route group.
     * @param route_name Route group name, ex. 6, 19, 1 ...
     * @param callback callback to be triggered when finished
     */
    public static void getRoutes_route_name(String route_name, ApiCallback<Map<String, Object>> callback) {
        new LppQuery()
                .setParams("route_name", route_name)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapType, new CustomDeserializer())
                                        .create();
                        Map<String, Object> data = gson.fromJson(response, mapType);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTES);
    }




    /**
     * getRoutes API DOC
     */
    public static class Routes {
        /**
         * @apiNote Unique ID of this route. (String)
         */
        public static final String id = "id";
        /**
         * @apiNote ID of parent route within this route group. (String)
         */
        public static final String route_parent_id = "route_parent_id";
        /**
         * @apiNote Integer ID of this route. (Integer)
         */
        public static final String int_id = "int_id";
        /**
         * @apiNote Integer ID of opposite route, can be null. (Integer)
         */
        public static final String opposite_route_int_id = "opposite_route_int_id";
        /**
         * @apiNote Name of bus route. (String)
         */
        public static final String name = "name";
        /**
         * @apiNote Floating point length of route in meters. (Double)
         */
        public static final String length = "length";
        /**
         * @apiNote Name of parent route within this route group. (String)
         */
        public static final String route_parent_name = "route_parent_name";
    }



    /**
     * Returns data for stations on particular route.
     * @param route_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getStationsOnRoute_route_id(String route_id, ApiCallback<List<Map<String, Object>>> callback) {
        new LppQuery()
                .setParams("route_id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapTypeList, new CustomDeserializerList())
                                        .create();
                        List<Map<String, Object>> data = gson.fromJson(response, mapTypeList);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATIONS_ON_ROUTE);
    }

    /**
     * Returns data for stations on particular route.
     * @param route_int_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getStationsOnRoute_route__int_id(int route_int_id, ApiCallback<List<Map<String, Object>>> callback) {
        new LppQuery()
                .setParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapTypeList, new CustomDeserializerList())
                                        .create();
                        List<Map<String, Object>> data = gson.fromJson(response, mapTypeList);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_STATIONS_ON_ROUTE);
    }

    /**
     * getStationsOnRoute API DOC
     */
    public static class StationsOnRoute {
        /**
         * @apiNote Station identification. (String)
         */
        public static final String id = "id";
        /**
         * @apiNote Super station ID. (String)
         */
        public static final String super_station_id = "super_station_id";
        /**
         * @apiNote Numerical station ID. (Integer)
         */
        public static final String int_id = "int_id";
        /**
         * @apiNote Ref ID ??. (String)
         */
        public static final String ref_id = "ref_id";
        /**
         * @apiNote Station name in Slovenian. (String)
         */
        public static final String name = "name";
        /**
         * @apiNote Station's longitude in decimal numerical form. (Double)
         */
        public static final String longitude = "longitude";
        /**
         * @apiNote Station's latitude in decimal numerical form. (Double)
         */
        public static final String latitude = "latitude";
        /**
         * @apiNote Identification of route for this station. (String)
         */
        public static final String route_id = "route_id";
        /**
         * @apiNote Numerical ID of route for this station. (String)
         */
        public static final String route_int_id = "route_int_id";
        /**
         * @apiNote Presumably numerical order of station, currently not implemented in DB. (String)
         */
        public static final String order_no = "order_no";
    }


    /**
     * Returns data for parent stations.
     * @param route_int_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getRouteParents_route_int_id(int route_int_id, ApiCallback<List<Map<String, Object>>> callback) {
        new LppQuery()
                .setParams("route_int_id", String.valueOf(route_int_id))
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapTypeList, new CustomDeserializerList())
                                        .create();
                        List<Map<String, Object>> data = gson.fromJson(response, mapTypeList);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_PARENTS);
    }

    /**
     * Returns data for parent stations.
     * @param route_id ID of route (can be acquired with /api/getRoutes)
     * @param callback callback to be triggered when finished
     */
    public static void getRouteParents_route_id(String route_id, ApiCallback<List<Map<String, Object>>> callback) {
        new LppQuery()
                .setParams("route_id", route_id)
                .setOnCompleteListener((response, statusCode, success) -> {
                    if (success) {
                        Gson gson =
                                new GsonBuilder()
                                        .registerTypeAdapter(mapTypeList, new CustomDeserializerList())
                                        .create();
                        List<Map<String, Object>> data = gson.fromJson(response, mapTypeList);
                        callback.onComplete(data, statusCode, true);
                    } else
                        callback.onComplete(null, statusCode, false);
                })
                .execute(LppQuery.GET_ROUTE_PARENTS);
    }

    /**
     * getRouteParents API DOC
     */
    public static class RouteParents {
        /**
         * @apiNote ID of the route parent. (String)
         */
        public static final String id = "id";
        /**
         * @apiNote ID of the route group. (String)
         */
        public static final String route_group_id = "route_group_id";
        /**
         * @apiNote name of the route parent. (String)
         */
        public static final String name = "name";
    }



    // JSON -> Map<String, Object>.
    public static class CustomDeserializer implements JsonDeserializer<Map<String, Object>> {
        @Override
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement data = json.getAsJsonObject().get("data");
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>(){}.getType());
        }
    }

    // JSON -> List<Map<String, Object>>.
    public static class CustomDeserializerList implements JsonDeserializer<List<Map<String, Object>>> {
        @Override
        public List<Map<String, Object>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement data = json.getAsJsonObject().get("data");
            return new Gson().fromJson(data, new TypeToken<List<Map<String, Object>>>(){}.getType());
        }
    }

}
