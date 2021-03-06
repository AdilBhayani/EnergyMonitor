package electeng209.energymonitor;

import android.content.Intent;//General android imports
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import java.util.ArrayList;

import com.google.firebase.database.ChildEventListener;//Firebase imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jjoe64.graphview.DefaultLabelFormatter;//Graph view imports
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.text.NumberFormat;

import android.graphics.PorterDuff;

public class Graphs extends AppCompatActivity {
    int drawPower = 0;
    LineGraphSeries<DataPoint> mSeries1;//Declarations to be used later
    ArrayList<MyData> powerArrayList = new ArrayList<>();
    ArrayList<MyData> voltageArrayList = new ArrayList<>();
    ArrayList<MyData> currentArrayList = new ArrayList<>();
    ArrayList<MyData> arrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);//Spinner initialization
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.graphsArray, R.layout.myspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();//get the database
        DatabaseReference myRef = database.getReference("data");//With reference to everything stored under data

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {//Functionality of the spinner to choose power current or voltage graphs
                Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
                if (arrayList.size() !=0) {
                    GraphView graph = (GraphView) findViewById(R.id.graph);
                    graph.removeAllSeries();
                    drawPower = i;
                    graphDrawer();
                }
                if (i == 0){
                    mySpinner.getBackground().setColorFilter(Color.argb(190,20,20,255), PorterDuff.Mode.SRC_ATOP);//Changing colour to match
                } else if(i == 1){
                    mySpinner.getBackground().setColorFilter(Color.argb(190,255,20,20), PorterDuff.Mode.SRC_ATOP);
                }else{
                    mySpinner.getBackground().setColorFilter(Color.argb(190,20,255,20), PorterDuff.Mode.SRC_ATOP);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        GraphView graph = (GraphView) findViewById(R.id.graph);//Graph theme customisation
        graph.getGridLabelRenderer().setGridColor(Color.argb(255,255,255,255));
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.argb(255,255,255,255));
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.argb(255,255,255,255));
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Data points");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.argb(255,255,255,255));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.argb(255,255,255,255));

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//Read data when child is added on database
                String dataString = dataSnapshot.getValue(String.class);//Data extraction
                String[] dataArray = dataString.split(",");
                int myNumber = Integer.parseInt(dataArray[0].substring(16));
                String myUnit = dataArray[1].substring(dataArray[0].lastIndexOf(":"),dataArray[0].lastIndexOf(":")+1);
                float myValue = Float.parseFloat(dataArray[2].substring(dataArray[2].lastIndexOf(":")+2,dataArray[2].lastIndexOf("}")));

                if (myUnit.equals("W")) {//Filtering of impossibly high values
                    if (myValue > 9){
                        myValue = 9;
                    }
                } else if (myUnit.equals("A")){
                    if (myValue > 1){
                        myValue = 1;
                    }
                } else if (myUnit.equals("V")){
                    if (myValue > 14.5){
                        myValue = (float)14.5;
                    }
                }

                MyData myData = new MyData(myNumber, myValue, myUnit);//Storing data with data class
                if (myUnit.equals("W")) {//Adding to correct arraylist
                    powerArrayList.add(myData);
                } else if (myUnit.equals("A")){
                    currentArrayList.add(myData);
                } else if (myUnit.equals("V")){
                    voltageArrayList.add(myData);
                }
                arrayList.add(myData);
                graphDrawer();//Redraw after receiving data
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {//When data is erased remotely also delete arraylists
                arrayList.clear();
                powerArrayList.clear();
                currentArrayList.clear();
                voltageArrayList.clear();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        Button graphsButton = (Button) findViewById(R.id.realTimeButton);//Changing between tabs functionality
        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRealTime();
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });
    }

    private void goToRealTime() {
        Intent intent = new Intent(this, MainActivity.class);//Switch to main activity
        startActivity(intent);
    }

    private void goToSettings() {
        Intent intent = new Intent(this, About.class);//Switch to about tab
        startActivity(intent);
    }

    private DataPoint[] generateData() {//Helper function to graph
        ArrayList<MyData> myArrayList;
        String myString = "";
        if(drawPower == 0) {
            myArrayList = powerArrayList;//Decide what needs to be graphed
            myString = "W";
        } else if (drawPower == 1){
            myArrayList = currentArrayList;
            myString = "A";
        } else {
            myArrayList = voltageArrayList;
            myString = "V";
        }
            int count = arrayList.size();
            DataPoint[] values = new DataPoint[count];
            values[0] = new DataPoint(0,0);
            int counter = 0;
            float oldY = 0;
            float x = 0;
            float y = 0;
            for (int i = 1; i < count; i++) {//Convert stored data into something that can be graphed
                if (arrayList.get(i-1).unit.equals(myString)) {
                    x = myArrayList.get(counter).number;
                    y = myArrayList.get(counter).value;
                    oldY = y;
                    counter++;
                } else{
                    x++;
                    y = oldY;
                }
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }
        
            return values;//Return values that need to be graphed
    }

    private void graphDrawer() {
            NumberFormat nf = NumberFormat.getInstance();//Set the labels to be 2dp
            NumberFormat nf1 = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            nf1.setMinimumFractionDigits(0);

            GraphView graph = (GraphView) findViewById(R.id.graph);
            graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf1,nf));

            graph.removeAllSeries();//Clear the data points from earlier
            mSeries1 = new LineGraphSeries<>(generateData());
            mSeries1.setThickness(8);//Change line thickness
            if (drawPower == 1) {
                mSeries1.setColor(Color.RED);
                graph.getGridLabelRenderer().setVerticalAxisTitle("Current (A)");//Change axis label to match graph
            } else if (drawPower == 2){
                mSeries1.setColor(Color.GREEN);
                graph.getGridLabelRenderer().setVerticalAxisTitle("Voltage (V)");
            } else {
                graph.getGridLabelRenderer().setVerticalAxisTitle("Power (W)");
            }

            graph.addSeries(mSeries1);
            graph.getViewport().setScalable(true);//Make the graph scalable and scrollable
            graph.getViewport().setScrollable(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setMaxX(arrayList.size() + 10);

    }

}
