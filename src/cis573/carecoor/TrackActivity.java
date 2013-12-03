package cis573.carecoor;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalActivity;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class TrackActivity extends BannerActivity {
	private GraphicalView graph;
	private LinearLayout trackLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_track);
		setBannerTitle(R.string.track);
		trackLayout=(LinearLayout)findViewById(R.id.track_layout);
		
		graph=ChartFactory.getLineChartView(this, getDemoDataset(), getDemoRenderer());
		graph.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		trackLayout.addView(graph);
		
	}
	public void f(View v) {
		// TODO Auto-generated method stub
		
	}

	 private XYMultipleSeriesDataset getDemoDataset() {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    final int nr = 10;
		    Random r = new Random();
		    for (int i = 0; i < 2; i++) {
		      XYSeries series = new XYSeries("Demo series " + (i + 1));
		      for (int k = 0; k < nr; k++) {
		        series.add(k, 20 + r.nextInt() % 100);
		      }
		      dataset.addSeries(series);
		    }
		    return dataset;
		  }
	
	 private XYMultipleSeriesRenderer getDemoRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);
		    renderer.setPointSize(5f);
		    renderer.setMargins(new int[] {20, 30, 15, 0});
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.BLUE);
		    r.setPointStyle(PointStyle.SQUARE);
		    //r.setFillBelowLine(true);
		    //r.setFillBelowLineColor(Color.WHITE);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    r = new XYSeriesRenderer();
		    r.setPointStyle(PointStyle.CIRCLE);
		    r.setColor(Color.GREEN);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    renderer.setAxesColor(Color.DKGRAY);
		    renderer.setLabelsColor(Color.LTGRAY);
		    return renderer;
		  }
}
