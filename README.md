RadiatingCircleProgressBar
==========================

Custom View Showing Radiating Circular Animation for Displaying Progress Events

Usage :
  
    <com.example.radiatingcircle.RadiatingCircleProgressBar 
        android:id="@+id/radiating_circle_progress_bar"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent" />

In Code: 
 
    RadiatingCircleProgressBar rcpb = (RadiatingCircleProgressBar)
    rootView.findViewById(R.id.radiating_circle_progress_bar);

    rcpb.showProgress();
