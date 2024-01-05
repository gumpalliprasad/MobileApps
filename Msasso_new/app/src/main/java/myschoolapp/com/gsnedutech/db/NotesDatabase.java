package myschoolapp.com.gsnedutech.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Notes.class, exportSchema = false, version = 1)
public abstract class NotesDatabase extends RoomDatabase {

    private static final String DB_NAME = "notes_db";
    private static NotesDatabase instance;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static synchronized NotesDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), NotesDatabase.class,DB_NAME)
                    .fallbackToDestructiveMigration().build();

            //migrations
//            instance = Room.databaseBuilder(context.getApplicationContext(), NotesDatabase.class,DB_NAME)
//                    .addMigrations(MIGRATION_1_2).build();
        }
        return instance;
    }

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE notes "
//                    + " ADD COLUMN student_id TEXT DEFAULT ''");
//        }
//    };

    public abstract NotesDao notesDao();

}
