package develop.elbarberoapptest.db;

import android.provider.BaseColumns;

/**
 * Created by adma9717 on 28/05/18.
 */

public final class DatabaseModel {

    private DatabaseModel(){}

    /* Inner class that defines the service table */
    public static class ServiceTable implements BaseColumns {
        public static final String TABLE_NAME = "Services";
        public static final String COLUMN_1 = "Title";
        public static final String COLUMN_2 = "Description";
        public static final String COLUMN_3 = "Price";
        public static final String COLUMN_4 = "ImageUrl";
    }

    //method for maintaining the Service Table
    public static final String SQL_CREATE_SERVICE =
            "CREATE TABLE " + ServiceTable.TABLE_NAME + " (" +
                    ServiceTable._ID + " INTEGER PRIMARY KEY," +
                    ServiceTable.COLUMN_1 + " TEXT," +
                    ServiceTable.COLUMN_2 + " TEXT," +
                    ServiceTable.COLUMN_3 + " INTEGER," +
                    ServiceTable.COLUMN_4 + " TEXT)";

    public static final String SQL_DELETE_SERVICE =
            "DROP TABLE IF EXISTS " + ServiceTable.TABLE_NAME;

}
