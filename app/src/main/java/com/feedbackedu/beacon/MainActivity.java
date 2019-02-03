package com.feedbackedu.beacon;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    private HeartBeatView mHeartBeatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHeartBeatView = findViewById(R.id.heartview);
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("beacon-demo-4zt",
                        "3075352a64b88e47f5d1f46d2212a0f3");

        this.proximityObserver = new ProximityObserverBuilder(getApplicationContext(),
                cloudCredentials).withBalancedPowerMode().build();

        final ProximityZone zone = new ProximityZoneBuilder()
                .forTag("Shop")
                .inNearRange()
                .onEnter(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        String deskOwner = context.getAttachments().get("desk-owner");
                        Log.d("app", "Welcome to " + deskOwner + "'s desk");
                        mHeartBeatView.start();
                        return null;
                    }
                })
                .onExit(new Function1<ProximityZoneContext, Unit>() {
                    @Override
                    public Unit invoke(ProximityZoneContext context) {
                        Log.d("app", "Bye bye, come again!");
                        mHeartBeatView.stop();
                        return null;
                    }
                })
                .build();
        this.proximityObserver.startObserving(zone);

        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        new Function0<Unit>() {
                            @Override
                            public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.startObserving(zone);
                                return null;
                            }
                        },
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override
                            public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }
}
