package electeng209.energymonitor;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        final TextView dataDisplay = (TextView)findViewById(R.id.textView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("data");

        /*// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, String> myMap = (Map)dataSnapshot.getValue();
                if (!(myMap == null)) {
                    String[] dataArray = myMap.values().toArray(new String[0]);
                    ArrayList<MyData> powerArrayList = new ArrayList<>();
                    int number;
                    float value;
                    String valueString;
                    for (int i=0;i<dataArray.length;i++) {
                        try {
                            JSONObject obj = new JSONObject(dataArray[i]);
                            number = obj.getInt("number");
                            valueString = obj.getString("value");
                            value = Float.parseFloat(valueString);
                        }  catch(JSONException e) {
                            number = 0;
                            value = 0;
                            dataDisplay.setText(e.getMessage());
                        }
                        MyData myData = new MyData(number, value);
                        powerArrayList.add(myData);
                    }
                    powerArrayList = sortArray(powerArrayList);
                    // ||=========================================================================||
                    // ||At this stage, the last MyData in the array is the most recent data value||
                    // ||So display code goes here.                                               ||
                    // ||Right now it just displays the most recent MyData in a textView          ||
                    // ||Feel free to add helper functions and other classes                      ||
                    // ||=========================================================================||
                    //dataDisplay.setText("Number is:" + powerArrayList.get(powerArrayList.size()-1).number + " Value is: " + powerArrayList.get(powerArrayList.size()-1).value);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                dataDisplay.setText("Error: " + error.toException());
            }
        });*/

        Button graphsButton = (Button) findViewById(R.id.graphButton);
        graphsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToGraphs();
            }
        });

        Button settingsButton = (Button) findViewById(R.id.realTimeButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRealTime();
            }
        });
    }

    /*public ArrayList<MyData> sortArray(ArrayList<MyData> inputList) {
        for(int i=1;i<inputList.size();i++) {
            MyData temp;
            if (inputList.get(i-1).number > inputList.get(i).number) {
                temp = inputList.get(i-1);
                inputList.set(i-1,inputList.get(i));
                inputList.set(i, temp);
            }
        }
        return inputList;
    }*/

    private void goToRealTime() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void goToGraphs() {
        Intent intent = new Intent(this, Graphs.class);
        startActivity(intent);
    }

}