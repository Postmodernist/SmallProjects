import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Interval> intervals = Arrays.asList(new Interval(0, 10), new Interval(340, 884));
        Gson gson = new Gson();
        String json = gson.toJson(intervals);
        System.out.println(json);

        Type listOfIntervalsType = new TypeToken<ArrayList<Interval>>() {}.getType();
        List<Interval> intervalsOut = gson.fromJson(json, listOfIntervalsType);
        System.out.println(intervalsOut);
    }
}
