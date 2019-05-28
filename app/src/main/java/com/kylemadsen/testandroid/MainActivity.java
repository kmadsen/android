package com.kylemadsen.testandroid;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.kylemadsen.core.view.ViewGroupController;
import com.kylemadsen.core.view.ViewRouter;

public class MainActivity extends AppCompatActivity {

    private final ViewRouter viewRouter = new ViewRouter();
    private ViewGroupController viewGroupController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout linearLayout = findViewById(R.id.container);
        viewGroupController = ViewGroupController.onCreate(linearLayout);
        viewGroupController.attach(new MainViewController(viewRouter));
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewRouter.attach(this);
    }

    @Override
    protected void onStop() {
        viewRouter.detach();
        super.onStop();
    }
}
