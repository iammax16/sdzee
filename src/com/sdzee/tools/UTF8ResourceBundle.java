package com.sdzee.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

/* Outil permettant d'utiliser des caractères exotiques (lettres accentués, alphabets non ISO, caractères asiatiques, etc.) 
 * directement dans les fichiers Properties utilisés pour l'i18n. 
 * 
 * Sans cette classe, il fraudrait remplacer manuellement chaque caractère par son équivalent en unicode...
 */
public class UTF8ResourceBundle extends ResourceBundle {

    protected static final String  BUNDLE_NAME      = "com.sdzee.i18n.messages";
    protected static final String  BUNDLE_EXTENSION = "properties";
    protected static final String  CHARSET          = "UTF-8";
    protected static final Control UTF8_CONTROL     = new UTF8Control();

    public UTF8ResourceBundle() {
        setParent( ResourceBundle.getBundle( BUNDLE_NAME,
                FacesContext.getCurrentInstance().getViewRoot().getLocale(), UTF8_CONTROL ) );
    }

    @Override
    protected Object handleGetObject( String key ) {
        return parent.getObject( key );
    }

    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }

    protected static class UTF8Control extends Control {
        public ResourceBundle newBundle
                ( String baseName, Locale locale, String format, ClassLoader loader, boolean reload )
                        throws IllegalAccessException, InstantiationException, IOException
        {
            // The below code is copied from default Control#newBundle() implementation.
            // Only the PropertyResourceBundle line is changed to read the file as UTF-8.
            String bundleName = toBundleName( baseName, locale );
            String resourceName = toResourceName( bundleName, BUNDLE_EXTENSION );
            ResourceBundle bundle = null;
            InputStream stream = null;
            if ( reload ) {
                URL url = loader.getResource( resourceName );
                if ( url != null ) {
                    URLConnection connection = url.openConnection();
                    if ( connection != null ) {
                        connection.setUseCaches( false );
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream( resourceName );
            }
            if ( stream != null ) {
                try {
                    bundle = new PropertyResourceBundle( new InputStreamReader( stream, CHARSET ) );
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }
}