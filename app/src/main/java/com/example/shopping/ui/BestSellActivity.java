package com.example.shopping.ui;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.AnyChartView;
import com.example.shopping.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BestSellActivity extends AppCompatActivity {
    private static final String TAG = "BestSellActivity";
    private FirebaseFirestore db;
    private Pie pie;
    private AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_sell);
        pie = AnyChart.pie();
        anyChartView = findViewById(R.id.any_chart_view);
        db = FirebaseFirestore.getInstance();
        fetchDataAndUpdateChart();

    }

    private void fetchDataAndUpdateChart() {
        CollectionReference bestProductRef = db.collection("best_product");
        bestProductRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DataEntry> data = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Number productSoldCount = document.getLong("soldCount");
                    if (productSoldCount != null) {
                        data.add(new ValueDataEntry(document.getId(),productSoldCount));
                    }
                }
                pie.data(data);
                anyChartView.setChart(pie);
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e(TAG, "Error fetching data:", exception);
                }
            }
        });
    }
}
