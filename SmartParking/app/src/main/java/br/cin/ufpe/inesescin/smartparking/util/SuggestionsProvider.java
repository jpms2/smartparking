package br.cin.ufpe.inesescin.smartparking.util;

import android.app.SearchManager;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by João Pedro on 20/07/2016.
 */
public class SuggestionsProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "br.cin.ufpe.inesescin.smartparking.util.SuggestionsProvider";

    public static final int MODE = DATABASE_MODE_QUERIES;

    String[] columns = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
    };

    public SuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String[] columns = {
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA
        };
        MatrixCursor cursor = new MatrixCursor(columns);
        cursor = deliverCursor(cursor,selectionArgs[0]);
        return cursor;
    }

    public MatrixCursor deliverCursor(MatrixCursor mc, String search){

         String[] lojas = {"O de sempre", "Perini","Seaway","Tok & Stock", "Swarovski"};
        MatrixCursor matrixCursor = mc;
        matrixCursor.addRow(new String[] {"" + 0, lojas[0],lojas[0]});
        for(int i = 1;i < lojas.length;i++){
            if(lojas[i].contains(search)){
                matrixCursor.addRow(new String[] {"" + i, lojas[i],lojas[i]});
            }
        }
        return matrixCursor;
    }

}
