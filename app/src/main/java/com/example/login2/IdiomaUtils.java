package com.example.login2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IdiomaUtils {

    private static final String PREFS      = "optichelp_prefs";
    private static final String KEY_IDIOMA = "idioma";

    private static final List<String> PAISES_ESPANOL = Arrays.asList(
            "ES", "MX", "CO", "AR", "PE", "VE", "CL", "EC", "GT", "CU",
            "BO", "DO", "HN", "PY", "SV", "NI", "CR", "PA", "UY", "GQ"
    );

    public static void aplicarIdioma(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String idiomaGuardado   = prefs.getString(KEY_IDIOMA, null);

        String idioma;
        if (idiomaGuardado != null) {
            idioma = idiomaGuardado;
        } else {
            String pais = Locale.getDefault().getCountry().toUpperCase();
            idioma = PAISES_ESPANOL.contains(pais) ? "es" : "en";
        }

        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static void guardarIdioma(Context context, String codigo) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_IDIOMA, codigo)
                .apply();
    }

    public static String getIdiomaActual(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_IDIOMA, Locale.getDefault().getLanguage());
    }
}