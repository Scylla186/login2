package com.example.login2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class IdiomaUtils {

    private static final String PREFS      = "optichelp_prefs";
    private static final String KEY_IDIOMA = "idioma";

    public static void aplicarIdioma(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String idiomaGuardado   = prefs.getString(KEY_IDIOMA, null);

        String idioma;
        if (idiomaGuardado != null) {
            idioma = idiomaGuardado;
        } else {
            // Obtener idioma completo incluyendo variantes regionales
            String idiomaDispositivo = Locale.getDefault().getLanguage();
            String paisDispositivo   = Locale.getDefault().getCountry().toUpperCase();

            // Es español si el idioma empieza con "es" O si el país es hispanohablante
            if (idiomaDispositivo.startsWith("es") || paisDispositivo.equals("CO")
                    || paisDispositivo.equals("MX") || paisDispositivo.equals("ES")
                    || paisDispositivo.equals("AR") || paisDispositivo.equals("PE")
                    || paisDispositivo.equals("VE") || paisDispositivo.equals("CL")) {
                idioma = "es";
            } else {
                idioma = "en";
            }
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