package com.example.login2;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class ScanActivity extends AppCompatActivity {

    private static final int PAGE_INFO   = 0;
    private static final int PAGE_SCAN   = 1;
    private static final int TOTAL_PAGES = 2;

    private ViewPager2          viewPager;
    private View                dotInfo, dotScan;
    private ScanFragment        scanFragment;
    private ProductInfoFragment productInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IdiomaUtils.aplicarIdioma(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);

        inicializarFragments();
        inicializarVistas();
        configurarViewPager();
        configurarInsets();
    }

    private void inicializarFragments() {
        scanFragment        = new ScanFragment();
        productInfoFragment = new ProductInfoFragment();

        scanFragment.setOnQrValidadoListener(qrId -> {
            productInfoFragment.actualizarUI();
            viewPager.setCurrentItem(PAGE_INFO, true);
        });
    }

    private void inicializarVistas() {
        viewPager = findViewById(R.id.view_pager);
        dotInfo   = findViewById(R.id.dot_info);
        dotScan   = findViewById(R.id.dot_scan);
    }

    private void configurarViewPager() {
        viewPager.setAdapter(new ScanPagerAdapter(this));
        viewPager.setCurrentItem(PAGE_SCAN, false);
        actualizarIndicadores(PAGE_SCAN);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                actualizarIndicadores(position);
                if (position == PAGE_SCAN) {
                    scanFragment.reactivarScanner();
                    viewPager.announceForAccessibility(
                            getString(R.string.scan_page_indicator_scanner));
                } else {
                    productInfoFragment.actualizarUI();
                    viewPager.announceForAccessibility(
                            getString(R.string.scan_page_indicator_info));
                }
            }
        });
    }

    private void configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void actualizarIndicadores(int pagina) {
        dotInfo.setBackground(ContextCompat.getDrawable(this,
                pagina == PAGE_INFO ? R.drawable.dot_active : R.drawable.dot_inactive));
        dotScan.setBackground(ContextCompat.getDrawable(this,
                pagina == PAGE_SCAN ? R.drawable.dot_active : R.drawable.dot_inactive));
    }

    private class ScanPagerAdapter extends FragmentStateAdapter {

        ScanPagerAdapter(FragmentActivity fa) { super(fa); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return position == PAGE_INFO ? productInfoFragment : scanFragment;
        }

        @Override
        public int getItemCount() { return TOTAL_PAGES; }
    }
}